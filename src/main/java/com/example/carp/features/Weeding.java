package com.example.carp.features;

import com.example.carp.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import java.util.ArrayList;
import java.util.List;

public class Weeding {
    private int tickCounter;
    private int weedIndex;
    private List<BlockPos> weedCache = new ArrayList<>();

    public void onClientTick(Minecraft client) {
        if (!Config.weedingEnabled.getBooleanValue()) return;
        LocalPlayer p = client.player;
        if (p == null || client.level == null) return;

        tickCounter++;
        int rate = Config.weedingTickRate.getIntegerValue();
        if (tickCounter % rate != 0) return;

        // Scan for weeds
        if (weedCache.isEmpty() || tickCounter % (rate * 20) == 0) {
            weedCache = scanWeeds(p, Config.weedingRadius.getIntegerValue());
            weedIndex = 0;
        }

        if (weedCache.isEmpty()) return;

        String mode = Config.weedingMode.getStringValue();
        if ("simultaneous".equals(mode)) {
            for (BlockPos pos : weedCache) {
                breakBlock(p, pos);
            }
            weedCache.clear();
        } else {
            // sequential
            if (weedIndex < weedCache.size()) {
                breakBlock(p, weedCache.get(weedIndex));
                weedIndex++;
            }
            if (weedIndex >= weedCache.size()) weedCache.clear();
        }
    }

    private List<BlockPos> scanWeeds(LocalPlayer p, int radius) {
        List<BlockPos> list = new ArrayList<>();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int px = (int) p.getX(), py = (int) p.getY(), pz = (int) p.getZ();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                for (int dy = p.getBlockY() - 1; dy <= p.getBlockY() + 2; dy++) {
                    pos.set(px + dx, dy, pz + dz);
                    BlockState state = p.level().getBlockState(pos);
                    Block block = state.getBlock();
                    if (isWeed(block)) {
                        list.add(pos.immutable());
                    }
                }
            }
        }
        return list;
    }

    private boolean isWeed(Block b) {
        // 草（不含竹子）
        boolean isGrass = b == Blocks.SHORT_GRASS || b == Blocks.TALL_GRASS
            || b == Blocks.FERN || b == Blocks.LARGE_FERN
            || b == Blocks.DEAD_BUSH || b == Blocks.VINE
            || b == Blocks.SUGAR_CANE || b == Blocks.KELP
            || b == Blocks.KELP_PLANT || b == Blocks.SEAGRASS
            || b == Blocks.TALL_SEAGRASS;
        // 花
        boolean isFlower = b == Blocks.DANDELION || b == Blocks.POPPY
            || b == Blocks.BLUE_ORCHID || b == Blocks.ALLIUM
            || b == Blocks.AZURE_BLUET || b == Blocks.RED_TULIP
            || b == Blocks.ORANGE_TULIP || b == Blocks.WHITE_TULIP
            || b == Blocks.PINK_TULIP || b == Blocks.OXEYE_DAISY
            || b == Blocks.CORNFLOWER || b == Blocks.LILY_OF_THE_VALLEY
            || b == Blocks.SUNFLOWER || b == Blocks.LILAC
            || b == Blocks.ROSE_BUSH || b == Blocks.PEONY
            || b == Blocks.WITHER_ROSE;
        // 落叶（野花 + 落叶层）
        boolean isLeaf = b == Blocks.WILDFLOWERS || b == Blocks.LEAF_LITTER;

        boolean match = false;
        if (Config.weedingGrass.getBooleanValue() && isGrass) match = true;
        if (Config.weedingFlowers.getBooleanValue() && isFlower) match = true;
        if (Config.weedingLeaves.getBooleanValue() && isLeaf) match = true;
        return match;
    }

    private void breakBlock(LocalPlayer p, BlockPos pos) {
        String mode = Config.weedingBreakMode.getStringValue();
        if ("packet".equals(mode)) {
            p.connection.send(new ServerboundPlayerActionPacket(
                ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK,
                pos, Direction.UP, 0));
            p.connection.send(new ServerboundPlayerActionPacket(
                ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK,
                pos, Direction.UP, 0));
        } else {
            // click mode — also uses packet (MC 26.1 compat)
            p.connection.send(new ServerboundPlayerActionPacket(
                ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK,
                pos, Direction.UP, 0));
            p.connection.send(new ServerboundPlayerActionPacket(
                ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK,
                pos, Direction.UP, 0));
        }
    }
}
