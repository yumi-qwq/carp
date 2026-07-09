package com.example.carp.features;

import com.example.carp.CarpMod;
import com.example.carp.config.Config;
import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.Settings;
import baritone.api.event.events.TickEvent;
import baritone.api.event.events.PlayerUpdateEvent;
import baritone.api.event.events.type.EventState;
import baritone.api.pathing.calc.IPath;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalXZ;
import baritone.api.pathing.path.IPathExecutor;
import baritone.api.process.IBaritoneProcess;
import baritone.api.process.PathingCommand;
import baritone.api.utils.BetterBlockPos;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class BaritoneProcess {

    private boolean wasGotoActive, wasFollowActive, wasExploreActive, wasMineActive, wasTunnelActive;
    private int tickCount, mineScanTick;
    private String lastError;
    private int diagTick;
    public void onClientTick(Minecraft client) {
        LocalPlayer p = client.player;
        if (p == null || client.level == null) return;

        IBaritone baritone;
        try {
            baritone = BaritoneAPI.getProvider().getPrimaryBaritone();
        } catch (Exception e) {
            error(p, "BaritoneProvider init: " + e.getMessage());
            return;
        }
        if (baritone == null) return;

        baritone.Baritone b = (baritone.Baritone) baritone;
        try { syncBaritoneSettings(); } catch (Exception ignored) {}

        try {
            tickCount++;
            baritone.getGameEventHandler().onTick(new TickEvent(EventState.PRE, TickEvent.Type.IN, tickCount));
        } catch (Exception e) { error(p, "IN tick: " + e.toString()); }

        boolean gotoActive = Config.baritoneGotoEnabled.getBooleanValue();
        if (gotoActive != wasGotoActive) {
            wasGotoActive = gotoActive;
            try {
                if (gotoActive) {
                    int x = Config.baritoneGotoX.getIntegerValue();
                    int z = Config.baritoneGotoZ.getIntegerValue();
                    baritone.getCustomGoalProcess().setGoalAndPath(new GoalXZ(x, z));
                    diagTick = 1;
                    p.displayClientMessage(Component.literal("[Carp] A* -> (" + x + ", " + z + ")"), false);
                } else {
                    baritone.getCustomGoalProcess().onLostControl();
                    baritone.getPathingBehavior().cancelEverything();
                    p.displayClientMessage(Component.literal("[Carp] Stopped"), false);
                }
            } catch (Exception e) { error(p, "Goto: " + e.getMessage()); }
        }

        boolean followActive = Config.baritoneFollowEnabled.getBooleanValue();
        if (followActive != wasFollowActive) {
            wasFollowActive = followActive;
            try {
                if (followActive) {
                    baritone.getFollowProcess().follow(e -> true);
                    p.displayClientMessage(Component.literal("[Carp] Follow ON"), false);
                } else {
                    baritone.getFollowProcess().onLostControl();
                    baritone.getPathingBehavior().cancelEverything();
                }
            } catch (Exception e) { error(p, "Follow: " + e.getMessage()); }
        }

        boolean exploreActive = Config.baritoneExploreEnabled.getBooleanValue();
        if (exploreActive != wasExploreActive) {
            wasExploreActive = exploreActive;
            try {
                if (exploreActive) {
                    int range = Config.baritoneExploreRange.getIntegerValue();
                    baritone.getExploreProcess().explore(0, range);
                    p.displayClientMessage(Component.literal("[Carp] Explore range=" + range), false);
                } else {
                    baritone.getExploreProcess().onLostControl();
                    baritone.getPathingBehavior().cancelEverything();
                }
            } catch (Exception e) { error(p, "Explore: " + e.getMessage()); }
        }

        // Mine
        boolean mineActive = Config.baritoneMineEnabled.getBooleanValue();
        if (mineActive != wasMineActive) {
            wasMineActive = mineActive;
            if (mineActive) p.displayClientMessage(Component.literal("[Carp] Mine ON"), false);
            else { baritone.getPathingBehavior().cancelEverything(); }
        }
        if (mineActive) tickMine(p, b);

        // Tunnel
        boolean tunnelActive = Config.baritoneTunnelEnabled.getBooleanValue();
        if (tunnelActive != wasTunnelActive) {
            wasTunnelActive = tunnelActive;
            if (tunnelActive) p.displayClientMessage(Component.literal("[Carp] Tunnel ON"), false);
            else { baritone.getPathingBehavior().cancelEverything(); }
        }
        if (tunnelActive) tickTunnel(p, b);

        if (b.bsi == null && diagTick > 0 && Config.baritoneDebug.getBooleanValue()) {
            p.displayClientMessage(Component.literal("[Carp] bsi is null!"), false);
        }
        dispatchProcess(b, baritone.getCustomGoalProcess(), p);
        dispatchProcess(b, baritone.getExploreProcess(), null);
        dispatchProcess(b, baritone.getFollowProcess(), null);

        try {
            baritone.getGameEventHandler().onPlayerUpdate(new PlayerUpdateEvent(EventState.PRE));
            baritone.getGameEventHandler().onPlayerUpdate(new PlayerUpdateEvent(EventState.POST));
            baritone.getGameEventHandler().onTick(new TickEvent(EventState.POST, TickEvent.Type.OUT, tickCount));
            lastError = null;
        } catch (Exception e) {
            String msg = e.toString();
            if (!msg.equals(lastError)) {
                lastError = msg;
                error(p, "Tick: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
            }
        }
    }

    private void dispatchProcess(baritone.Baritone baritone, IBaritoneProcess process, LocalPlayer p) {
        if (!process.isActive()) return;
        try {
            PathingCommand cmd = process.onTick(false, true);
            if (cmd != null) {
                baritone.getPathingBehavior().secretInternalSetGoalAndPath(cmd);
                if (p != null && Config.baritoneDebug.getBooleanValue() && diagTick > 0 && diagTick <= 5) {
                    p.displayClientMessage(Component.literal("[Carp] dispatch: " + cmd.commandType + " inProg=" + baritone.getPathingBehavior().getInProgress().isPresent()), false);
                    diagTick++;
                }
            }
        } catch (Exception e) { if (p != null) error(p, "dispatch: " + e.toString()); }
    }

    private void error(LocalPlayer p, String msg) {
        CarpMod.LOGGER.error(msg);
        if (p != null && Config.baritoneDebug.getBooleanValue()) {
            p.displayClientMessage(Component.literal("[Carp] " + msg), false);
        }
    }

    // Scan for nearest target block and goTo it
    private void tickMine(LocalPlayer p, baritone.Baritone b) {
        if (!b.getPathingBehavior().isPathing() && b.getPathingBehavior().getInProgress().isEmpty()) {
            mineScanTick++;
            if (mineScanTick % 20 != 0) return; // scan every 20 ticks
            BlockPos nearest = findNearestBlock(p, getMineTarget());
            if (nearest != null) {
                b.getCustomGoalProcess().setGoalAndPath(new GoalBlock(nearest));
            }
        }
    }

    // Tunnel straight ahead
    private void tickTunnel(LocalPlayer p, baritone.Baritone b) {
        if (!b.getPathingBehavior().isPathing() && b.getPathingBehavior().getInProgress().isEmpty()) {
            int dist = 100;
            float yaw = p.getYRot();
            double rad = Math.toRadians(yaw);
            int tx = (int)(p.getX() - Math.sin(rad) * dist);
            int tz = (int)(p.getZ() + Math.cos(rad) * dist);
            b.getCustomGoalProcess().setGoalAndPath(new GoalXZ(tx, tz));
        }
    }

    private BlockPos findNearestBlock(LocalPlayer p, Block target) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int r = 32;
        BlockPos best = null;
        double bestDist = Double.MAX_VALUE;
        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    pos.set(p.getX() + dx, p.getY() + dy, p.getZ() + dz);
                    if (p.level().getBlockState(pos).getBlock() == target) {
                        double d = p.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                        if (d < bestDist) { bestDist = d; best = pos.immutable(); }
                    }
                }
            }
        }
        return best;
    }

    private Block getMineTarget() {
        return switch (Config.baritoneMineBlock.getStringValue()) {
            case "diamond_ore" -> Blocks.DIAMOND_ORE;
            case "iron_ore" -> Blocks.IRON_ORE;
            case "gold_ore" -> Blocks.GOLD_ORE;
            case "emerald_ore" -> Blocks.EMERALD_ORE;
            case "coal_ore" -> Blocks.COAL_ORE;
            case "copper_ore" -> Blocks.COPPER_ORE;
            case "redstone_ore" -> Blocks.REDSTONE_ORE;
            case "lapis_ore" -> Blocks.LAPIS_ORE;
            case "nether_quartz_ore" -> Blocks.NETHER_QUARTZ_ORE;
            case "ancient_debris" -> Blocks.ANCIENT_DEBRIS;
            case "nether_gold_ore" -> Blocks.NETHER_GOLD_ORE;
            default -> Blocks.DIAMOND_ORE;
        };
    }

    private void syncBaritoneSettings() {
        Settings s = BaritoneAPI.getSettings();
        s.allowBreak.value = Config.baritoneAllowBreak.getBooleanValue();
        s.allowPlace.value = Config.baritoneAllowPlace.getBooleanValue();
        s.allowSprint.value = Config.baritoneAllowSprint.getBooleanValue();
        s.allowParkour.value = Config.baritoneAllowParkour.getBooleanValue();
        s.autoTool.value = Config.baritoneAutoTool.getBooleanValue();
        s.allowInventory.value = Config.baritoneAllowInventory.getBooleanValue();
        s.allowParkourPlace.value = Config.baritoneAllowParkourPlace.getBooleanValue();
        s.allowWaterBucketFall.value = Config.baritoneAllowWaterBucketFall.getBooleanValue();
        s.blockPlacementPenalty.value = Config.baritoneBlockPlacePenalty.getDoubleValue();
        s.blockBreakAdditionalPenalty.value = Config.baritoneBlockBreakPenalty.getDoubleValue();
        s.walkOnWaterOnePenalty.value = Config.baritoneWalkOnWaterPenalty.getDoubleValue();
        s.jumpPenalty.value = Config.baritoneJumpPenalty.getDoubleValue();
        s.costHeuristic.value = Config.baritoneCostHeuristic.getDoubleValue();
        s.maxFallHeightNoWater.value = Config.baritoneMaxFallHeight.getIntegerValue();
        s.freeLook.value = false; // 自由视角会干扰寻路，始终禁用
        s.assumeWalkOnWater.value = true;
        s.sprintInWater.value = Config.baritoneSprintInWater.getBooleanValue();
        s.sprintAscends.value = Config.baritoneSprintAscends.getBooleanValue();
        s.antiCheatCompatibility.value = Config.baritoneAntiCheatCompatibility.getBooleanValue();
        s.chatControl.value = Config.baritoneChatControl.getBooleanValue();
        s.renderPath.value = Config.baritonePathRender.getBooleanValue();
        s.renderPathIgnoreDepth.value = Config.baritonePathIgnoreDepth.getBooleanValue();
        s.renderPathAsLine.value = Config.baritonePathAsLine.getBooleanValue();
        s.pathRenderLineWidthPixels.value = (float) Config.baritonePathLineWidth.getIntegerValue();
        s.fadePath.value = Config.baritonePathFade.getBooleanValue();
    }
}
