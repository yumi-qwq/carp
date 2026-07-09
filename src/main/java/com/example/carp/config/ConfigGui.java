package com.example.carp.config;

import com.example.carp.CarpMod;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import java.util.ArrayList;
import java.util.List;

/**
 * 配置 GUI — 六标签：Mod / 建筑 / 移动 / 渲染 / 玩家 / 自动。
 */
public class ConfigGui extends GuiConfigsBase {

    private static final List<ConfigOptionWrapper> MOD, BUILD, MOVE, RENDER, PLAYER, PATHFIND;

    static {
        List<ConfigOptionWrapper> mod = new ArrayList<>();
        mod.add(new ConfigOptionWrapper(Config.openConfigHotkey));
        MOD = List.copyOf(mod);

        // 建筑: 搭路 + 除草
        List<ConfigOptionWrapper> build = new ArrayList<>();
        build.add(new ConfigOptionWrapper("──── " + StringUtils.translate("carp.tab.build") + " ────"));
        build.addAll(wr(Config.scaffoldEnabled, Config.scaffoldHotkey,
                Config.scaffoldDelayMin, Config.scaffoldDelayMax,
                Config.scaffoldSprint, Config.scaffoldSlot));
        build.add(new ConfigOptionWrapper("──── " + StringUtils.translate("carp.weeding.title") + " ────"));
        build.addAll(wr(Config.weedingEnabled, Config.weedingRadius, Config.weedingTickRate,
                Config.weedingMode, Config.weedingBreakMode,
                Config.weedingLeaves, Config.weedingGrass, Config.weedingFlowers));
        BUILD = List.copyOf(build);

        // 移动: 载具 / 跑图 / 防摔 / 减速免疫
        List<ConfigOptionWrapper> move = new ArrayList<>();
        move.add(new ConfigOptionWrapper("──── " + StringUtils.translate("carp.move.cat.vehicle") + " ────"));
        move.addAll(wr(Config.boatFlyEnabled, Config.boatFlyForwardSpeed, Config.boatFlyBackwardSpeed,
                Config.boatFlyUpSpeed, Config.freeElytraEnabled, Config.freeElytraSpeed));
        move.add(new ConfigOptionWrapper("──── " + StringUtils.translate("carp.move.cat.walk") + " ────"));
        move.addAll(wr(Config.speedEffect, Config.speedAmplifier, Config.autoSprint, Config.autoRespawn));
        move.add(new ConfigOptionWrapper("──── " + StringUtils.translate("carp.move.cat.antifall") + " ────"));
        move.addAll(wr(Config.noFallEnabled, Config.noFallMode));
        move.add(new ConfigOptionWrapper("──── " + StringUtils.translate("carp.move.cat.antislow") + " ────"));
        move.addAll(wr(Config.noSlowEnabled, Config.noSlowBlocking, Config.noSlowConsume,
                Config.noSlowBow, Config.noSlowSneaking, Config.noSlowWeb, Config.noSlowMultiplier));
        move.addAll(wr(Config.noWebEnabled, Config.noWebMode));
        MOVE = List.copyOf(move);

        // 渲染: 视觉
        List<ConfigOptionWrapper> render = new ArrayList<>();
        render.add(new ConfigOptionWrapper("──── " + StringUtils.translate("carp.tab.render") + " ────"));
        render.addAll(wr(Config.noPumpkinOverlay, Config.nightVision, Config.antiNausea, Config.antiBlindness));
        RENDER = List.copyOf(render);

        // 玩家: 杀戮 / 保护
        List<ConfigOptionWrapper> player = new ArrayList<>();
        player.add(new ConfigOptionWrapper("──── " + StringUtils.translate("carp.player.cat.combat") + " ────"));
        player.addAll(wr(Config.killAuraEnabled, Config.killAuraHotkey, Config.killAuraRange,
                Config.killAuraCps, Config.killAuraAutoBlock, Config.killAuraRotations,
                Config.killAuraRotationSpeed, Config.killAuraAttackMode, Config.killAuraTargetMode,
                Config.killAuraSortMode, Config.killAuraSingleTarget, Config.killAuraCriticals));
        player.add(new ConfigOptionWrapper("──── " + StringUtils.translate("carp.player.cat.protect") + " ────"));
        player.addAll(wr(Config.autoEatEnabled));
        PLAYER = List.copyOf(player);

        // 自动: 基本配置 / 寻路 / 挖矿 / 跟随 / 探索 / 隧道
        List<ConfigOptionWrapper> path = new ArrayList<>();
        path.add(new ConfigOptionWrapper("──── " + StringUtils.translate("carp.baritone.general") + " ────"));
        path.add(new ConfigOptionWrapper(Config.baritoneDebug));
        path.add(new ConfigOptionWrapper(Config.baritonePathRender));
        path.addAll(wr(Config.baritonePathAsLine, Config.baritonePathIgnoreDepth));
        path.add(new ConfigOptionWrapper(Config.baritonePathLineWidth));
        path.addAll(wr(Config.baritonePathFade, Config.baritonePathFadeStart, Config.baritonePathFadeEnd));
        path.addAll(wr(Config.baritoneColorCurrent, Config.baritoneColorNext,
                Config.baritoneColorBest, Config.baritoneColorConsidered));
        path.addAll(wr(Config.baritoneSprintInWater, Config.baritoneSprintAscends));
        path.addAll(wr(Config.baritoneAutoTool, Config.baritoneAllowInventory, Config.baritoneAllowParkourPlace,
                Config.baritoneAllowWaterBucketFall));
        path.addAll(wr(Config.baritoneBlockPlacePenalty, Config.baritoneBlockBreakPenalty,
                Config.baritoneWalkOnWaterPenalty, Config.baritoneJumpPenalty, Config.baritoneCostHeuristic));
        path.addAll(wr(Config.baritoneMaxFallHeight, Config.baritonePathTimeout, Config.baritonePlanAheadTime,
                Config.baritoneBuildTickLimit));
        path.addAll(wr(Config.baritoneAntiCheatCompatibility, Config.baritoneChatControl));
        path.add(new ConfigOptionWrapper(Config.baritoneFreeLook));
        // ──── 自动寻路 ────
        path.add(new ConfigOptionWrapper("──── " + StringUtils.translate("carp.baritone.goto") + " ────"));
        path.add(new ConfigOptionWrapper(Config.baritoneGotoEnabled));
        path.add(new ConfigOptionWrapper(Config.baritoneGotoHotkey));
        path.add(new ConfigOptionWrapper(Config.baritoneGotoX));
        path.add(new ConfigOptionWrapper(Config.baritoneGotoZ));
        path.add(new ConfigOptionWrapper(Config.baritoneAllowBreak));
        path.add(new ConfigOptionWrapper(Config.baritoneAllowPlace));
        path.add(new ConfigOptionWrapper(Config.baritoneAllowSprint));
        path.add(new ConfigOptionWrapper(Config.baritoneAllowParkour));
        // ──── 自动挖矿 ────
        path.add(new ConfigOptionWrapper("──── " + StringUtils.translate("carp.baritone.mine") + " ────"));
        path.add(new ConfigOptionWrapper(Config.baritoneMineEnabled));
        path.add(new ConfigOptionWrapper(Config.baritoneMineHotkey));
        path.add(new ConfigOptionWrapper(Config.baritoneMineBlock));
        path.add(new ConfigOptionWrapper(Config.baritoneMineLegit));
        path.add(new ConfigOptionWrapper(Config.baritoneMineScanDrops));
        path.addAll(wr(Config.baritoneMineScanInterval, Config.baritoneMineMinY,
                Config.baritoneMineMaxY, Config.baritoneMineExposedOnly, Config.baritoneMineLegitDiag));
        // ──── 跟随 ────
        path.add(new ConfigOptionWrapper("──── " + StringUtils.translate("carp.baritone.follow") + " ────"));
        path.add(new ConfigOptionWrapper(Config.baritoneFollowEnabled));
        path.add(new ConfigOptionWrapper(Config.baritoneFollowHotkey));
        path.add(new ConfigOptionWrapper(Config.baritoneFollowTarget));
        path.add(new ConfigOptionWrapper(Config.baritoneFollowRadius));
        // ──── 探索 ────
        path.add(new ConfigOptionWrapper("──── " + StringUtils.translate("carp.baritone.explore") + " ────"));
        path.add(new ConfigOptionWrapper(Config.baritoneExploreEnabled));
        path.add(new ConfigOptionWrapper(Config.baritoneExploreHotkey));
        path.add(new ConfigOptionWrapper(Config.baritoneExploreRange));
        // ──── 隧道 ────
        path.add(new ConfigOptionWrapper("──── " + StringUtils.translate("carp.baritone.tunnel") + " ────"));
        path.add(new ConfigOptionWrapper(Config.baritoneTunnelEnabled));
        path.add(new ConfigOptionWrapper(Config.baritoneTunnelHotkey));
        path.add(new ConfigOptionWrapper(Config.baritoneTunnelWidth));
        path.add(new ConfigOptionWrapper(Config.baritoneTunnelHeight));
        path.add(new ConfigOptionWrapper(Config.baritoneTunnelBackfill));
        PATHFIND = List.copyOf(path);
    }

    private static List<ConfigOptionWrapper> wr(IConfigBase... c) { return ConfigOptionWrapper.createFor(List.of(c)); }

    private static final String[] TABS = {"carp.tab.mod", "carp.tab.build", "carp.tab.move", "carp.tab.render", "carp.tab.player", "carp.tab.pathfind"};
    private final Screen prev;
    private final int tab;

    public ConfigGui() { this(null, 0); }
    public ConfigGui(Screen p) { this(p, 0); }
    private ConfigGui(Screen p, int t) {
        super(0, 35, StringUtils.translate("carp.gui.title"), p, CarpMod.MOD_ID);
        this.prev = p; this.tab = t;
    }

    @Override
    public void initGui() {
        super.initGui();
        int n = TABS.length, bw = 48, bh = 20, gap = 2;
        int sx = 10, y = 6;
        for (int i = 0; i < n; i++) {
            ButtonGeneric b = new ButtonGeneric(sx + i * (bw + gap), y, bw, bh, StringUtils.translate(TABS[i]));
            b.setEnabled(i != tab); b.setRenderDefaultBackground(true); b.setTextCentered(true);
            final int ti = i;
            this.addButton(b, (bb, mb) -> Minecraft.getInstance().setScreen(new ConfigGui(prev, ti)));
        }
    }

    @Override public List<ConfigOptionWrapper> getConfigs() {
        return switch (tab) { case 1 -> BUILD; case 2 -> MOVE; case 3 -> RENDER; case 4 -> PLAYER; case 5 -> PATHFIND; default -> MOD; };
    }

    public static void open() { Minecraft.getInstance().setScreen(new ConfigGui()); }
    public static void open(Screen p) { Minecraft.getInstance().setScreen(new ConfigGui(p)); }
}
