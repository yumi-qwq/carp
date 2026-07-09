package com.example.carp.config;

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.config.options.*;
import fi.dy.masa.malilib.util.StringUtils;
import java.util.ArrayList;
import java.util.List;

public class Config implements IConfigHandler {

    public static final ConfigHotkey openConfigHotkey = new ConfigHotkey(
            "carp.openconfig.hotkey", "RIGHT_SHIFT", "carp.openconfig.hotkey.comment");

    // ========== Scaffold ==========
    public static final ConfigBoolean scaffoldEnabled = new ConfigBoolean(
            "carp.scaffold.enabled", false, "carp.scaffold.enabled.comment");
    public static final ConfigHotkey scaffoldHotkey = new ConfigHotkey(
            "carp.scaffold.hotkey", "", "carp.scaffold.hotkey.comment");
    public static final ConfigInteger scaffoldDelayMin = new ConfigInteger(
            "carp.scaffold.delay_min", 100, 50, 300, "");
    public static final ConfigInteger scaffoldDelayMax = new ConfigInteger(
            "carp.scaffold.delay_max", 200, 80, 400, "");
    public static final ConfigBoolean scaffoldSprint = new ConfigBoolean(
            "carp.scaffold.sprint", true, "carp.scaffold.sprint.comment");
    public static final ConfigInteger scaffoldSlot = new ConfigInteger(
            "carp.scaffold.slot", 9, 1, 9, "carp.scaffold.slot.comment");

    // ========== 除草 ==========
    public static final ConfigBoolean weedingEnabled = new ConfigBoolean(
            "carp.weeding.enabled", false, "carp.weeding.enabled.comment");
    public static final ConfigInteger weedingRadius = new ConfigInteger(
            "carp.weeding.radius", 5, 1, 6, "carp.weeding.radius.comment");
    public static final ConfigInteger weedingTickRate = new ConfigInteger(
            "carp.weeding.tick_rate", 1, 1, 20, "carp.weeding.tick_rate.comment");
    public static final ConfigOptionList weedingMode = new ConfigOptionList(
            "carp.weeding.mode", WeedingMode.SEQUENTIAL, "carp.weeding.mode.comment");
    public static final ConfigOptionList weedingBreakMode = new ConfigOptionList(
            "carp.weeding.break_mode", BreakMode.PACKET, "carp.weeding.break_mode.comment");
    public static final ConfigBoolean weedingLeaves = new ConfigBoolean(
            "carp.weeding.leaves", true, "carp.weeding.leaves.comment");
    public static final ConfigBoolean weedingGrass = new ConfigBoolean(
            "carp.weeding.grass", true, "carp.weeding.grass.comment");
    public static final ConfigBoolean weedingFlowers = new ConfigBoolean(
            "carp.weeding.flowers", true, "carp.weeding.flowers.comment");

    public enum WeedingMode implements IConfigOptionListEntry {
        SEQUENTIAL, SIMULTANEOUS;
        @Override public String getStringValue() { return name().toLowerCase(); }
        @Override public String getDisplayName() { return t("carp.weeding.mode." + name().toLowerCase()); }
        @Override public IConfigOptionListEntry cycle(boolean f) { int i = ordinal() + (f ? 1 : -1); if (i >= values().length) i = 0; else if (i < 0) i = values().length - 1; return values()[i]; }
        @Override public IConfigOptionListEntry fromString(String s) { try { return valueOf(s.toUpperCase()); } catch (Exception e) { return SEQUENTIAL; } }
    }
    public enum BreakMode implements IConfigOptionListEntry {
        PACKET, CLICK;
        @Override public String getStringValue() { return name().toLowerCase(); }
        @Override public String getDisplayName() { return t("carp.weeding.break_mode." + name().toLowerCase()); }
        @Override public IConfigOptionListEntry cycle(boolean f) { int i = ordinal() + (f ? 1 : -1); if (i >= values().length) i = 0; else if (i < 0) i = values().length - 1; return values()[i]; }
        @Override public IConfigOptionListEntry fromString(String s) { try { return valueOf(s.toUpperCase()); } catch (Exception e) { return PACKET; } }
    }

    // ========== BoatFly ==========
    public static final ConfigBoolean boatFlyEnabled = new ConfigBoolean(
            "carp.boatfly.enabled", false, "carp.boatfly.enabled.comment");
    public static final ConfigDouble boatFlyForwardSpeed = new ConfigDouble(
            "carp.boatfly.forward_speed", 0.3, 0.0, 2.0, "carp.boatfly.forward_speed.comment");
    public static final ConfigDouble boatFlyBackwardSpeed = new ConfigDouble(
            "carp.boatfly.backward_speed", 0.2, 0.0, 2.0, "carp.boatfly.backward_speed.comment");
    public static final ConfigDouble boatFlyUpSpeed = new ConfigDouble(
            "carp.boatfly.up_speed", 0.2, 0.0, 2.0, "carp.boatfly.up_speed.comment");

    // ========== FreeElytra ==========
    public static final ConfigBoolean freeElytraEnabled = new ConfigBoolean(
            "carp.freeelytra.enabled", false, "carp.freeelytra.enabled.comment");
    public static final ConfigDouble freeElytraSpeed = new ConfigDouble(
            "carp.freeelytra.speed", 1.0, 0.1, 5.0, "");

    // ========== Speed ==========
    public static final ConfigBoolean speedEffect = new ConfigBoolean(
            "carp.move.speed", false, "carp.move.speed.comment");
    public static final ConfigInteger speedAmplifier = new ConfigInteger(
            "carp.move.speedamp", 1, 0, 255, "carp.move.speedamp.comment");
    public static final ConfigBoolean autoSprint = new ConfigBoolean(
            "carp.move.autosprint", false, "carp.move.autosprint.comment");
    public static final ConfigBoolean autoRespawn = new ConfigBoolean(
            "carp.move.autorespawn", false, "carp.move.autorespawn.comment");

    // ========== NoFall ==========
    public static final ConfigBoolean noFallEnabled = new ConfigBoolean(
            "carp.nofall.enabled", false, "carp.nofall.enabled.comment");
    public static final ConfigOptionList noFallMode = new ConfigOptionList(
            "carp.nofall.mode", NoFallMode.PACKET, "carp.nofall.mode.comment");

    public enum NoFallMode implements IConfigOptionListEntry {
        GROUND, NOGROUND, PACKET, MLG;
        @Override public String getStringValue() { return name().toLowerCase(); }
        @Override public String getDisplayName() { return t("carp.nofall.mode." + name().toLowerCase()); }
        @Override public IConfigOptionListEntry cycle(boolean f) { int i = ordinal() + (f ? 1 : -1); if (i >= values().length) i = 0; else if (i < 0) i = values().length - 1; return values()[i]; }
        @Override public IConfigOptionListEntry fromString(String s) { try { return valueOf(s.toUpperCase()); } catch (Exception e) { return GROUND; } }
    }

    // ========== NoSlow ==========
    public static final ConfigBoolean noSlowEnabled = new ConfigBoolean(
            "carp.noslow.enabled", false, "carp.noslow.enabled.comment");
    public static final ConfigBoolean noSlowBlocking = new ConfigBoolean(
            "carp.noslow.blocking", true, "carp.noslow.blocking.comment");
    public static final ConfigBoolean noSlowConsume = new ConfigBoolean(
            "carp.noslow.consume", true, "carp.noslow.consume.comment");
    public static final ConfigBoolean noSlowBow = new ConfigBoolean(
            "carp.noslow.bow", true, "carp.noslow.bow.comment");
    public static final ConfigBoolean noSlowSneaking = new ConfigBoolean(
            "carp.noslow.sneaking", true, "carp.noslow.sneaking.comment");
    public static final ConfigBoolean noSlowWeb = new ConfigBoolean(
            "carp.noslow.web", true, "carp.noslow.web.comment");
    public static final ConfigDouble noSlowMultiplier = new ConfigDouble(
            "carp.noslow.multiplier", 1.0, 0.2, 2.0, "carp.noslow.multiplier.comment");

    // ========== NoWeb ==========
    public static final ConfigBoolean noWebEnabled = new ConfigBoolean(
            "carp.noweb.enabled", false, "carp.noweb.enabled.comment");
    public static final ConfigOptionList noWebMode = new ConfigOptionList(
            "carp.noweb.mode", NoWebMode.AIR, "carp.noweb.mode.comment");

    public enum NoWebMode implements IConfigOptionListEntry {
        AIR, STRAFE, GRIM;
        @Override public String getStringValue() { return name().toLowerCase(); }
        @Override public String getDisplayName() { return t("carp.noweb.mode." + name().toLowerCase()); }
        @Override public IConfigOptionListEntry cycle(boolean f) { int i = ordinal() + (f ? 1 : -1); if (i >= values().length) i = 0; else if (i < 0) i = values().length - 1; return values()[i]; }
        @Override public IConfigOptionListEntry fromString(String s) { try { return valueOf(s.toUpperCase()); } catch (Exception e) { return AIR; } }
    }

    // ========== KillAura ==========
    public static final ConfigBoolean killAuraEnabled = new ConfigBoolean(
            "carp.killaura.enabled", false, "carp.killaura.enabled.comment");
    public static final ConfigDouble killAuraRange = new ConfigDouble(
            "carp.killaura.range", 3.5, 1.0, 6.0, "carp.killaura.range.comment");
    public static final ConfigInteger killAuraCps = new ConfigInteger(
            "carp.killaura.cps", 5, 1, 20, "carp.killaura.cps.comment");
    public static final ConfigBoolean killAuraAutoBlock = new ConfigBoolean(
            "carp.killaura.autoblock", false, "carp.killaura.autoblock.comment");
    public static final ConfigBoolean killAuraRotations = new ConfigBoolean(
            "carp.killaura.rotations", true, "carp.killaura.rotations.comment");
    public static final ConfigOptionList killAuraAttackMode = new ConfigOptionList(
            "carp.killaura.attack_mode", AttackMode.CLICK, "carp.killaura.attack_mode.comment");
    public static final ConfigOptionList killAuraTargetMode = new ConfigOptionList(
            "carp.killaura.target_mode", TargetMode.HOSTILE, "carp.killaura.target_mode.comment");
    public static final ConfigOptionList killAuraSortMode = new ConfigOptionList(
            "carp.killaura.sort_mode", SortMode.DISTANCE, "carp.killaura.sort_mode.comment");
    public static final ConfigBoolean killAuraSingleTarget = new ConfigBoolean(
            "carp.killaura.single_target", true, "carp.killaura.single_target.comment");
    public static final ConfigDouble killAuraRotationSpeed = new ConfigDouble(
            "carp.killaura.rotation_speed", 0.08, 0.02, 0.50, "carp.killaura.rotation_speed.comment");
    public static final ConfigBoolean killAuraCriticals = new ConfigBoolean(
            "carp.killaura.criticals", false, "carp.killaura.criticals.comment");
    public static final ConfigHotkey killAuraHotkey = new ConfigHotkey(
            "carp.killaura.hotkey", "", "carp.killaura.hotkey.comment");

    // ========== 自动进食 ==========
    public static final ConfigBoolean autoEatEnabled = new ConfigBoolean(
            "carp.autoeat.enabled", false, "carp.autoeat.enabled.comment");

    // 除草已在上方 weeding* 中定义，这里不再重复

    public enum AttackMode implements IConfigOptionListEntry {
        CLICK, PACKET;
        @Override public String getStringValue() { return name().toLowerCase(); }
        @Override public String getDisplayName() { return t("carp.killaura.attack_mode." + name().toLowerCase()); }
        @Override public IConfigOptionListEntry cycle(boolean f) { int i = ordinal() + (f ? 1 : -1); if (i >= values().length) i = 0; else if (i < 0) i = values().length - 1; return values()[i]; }
        @Override public IConfigOptionListEntry fromString(String s) { try { return valueOf(s.toUpperCase()); } catch (Exception e) { return CLICK; } }
    }

    public enum TargetMode implements IConfigOptionListEntry {
        ALL, PLAYERS, HOSTILE, PASSIVE, NOT_PASSIVE;
        @Override public String getStringValue() { return name().toLowerCase(); }
        @Override public String getDisplayName() { return t("carp.killaura.target_mode." + name().toLowerCase()); }
        @Override public IConfigOptionListEntry cycle(boolean f) { int i = ordinal() + (f ? 1 : -1); if (i >= values().length) i = 0; else if (i < 0) i = values().length - 1; return values()[i]; }
        @Override public IConfigOptionListEntry fromString(String s) { try { return valueOf(s.toUpperCase()); } catch (Exception e) { return HOSTILE; } }
    }

    public enum SortMode implements IConfigOptionListEntry {
        DISTANCE, HEALTH;
        @Override public String getStringValue() { return name().toLowerCase(); }
        @Override public String getDisplayName() { return t("carp.killaura.sort_mode." + name().toLowerCase()); }
        @Override public IConfigOptionListEntry cycle(boolean f) { int i = ordinal() + (f ? 1 : -1); if (i >= values().length) i = 0; else if (i < 0) i = values().length - 1; return values()[i]; }
        @Override public IConfigOptionListEntry fromString(String s) { try { return valueOf(s.toUpperCase()); } catch (Exception e) { return DISTANCE; } }
    }

    // ========== 渲染 ==========
    public static final ConfigBoolean noPumpkinOverlay = new ConfigBoolean(
            "carp.render.nopumpkin", false, "carp.render.nopumpkin.comment");
    public static final ConfigBoolean nightVision = new ConfigBoolean(
            "carp.render.nightvision", false, "carp.render.nightvision.comment");
    public static final ConfigBoolean antiNausea = new ConfigBoolean(
            "carp.render.antinausea", false, "carp.render.antinausea.comment");
    public static final ConfigBoolean antiBlindness = new ConfigBoolean(
            "carp.render.antiblindness", false, "carp.render.antiblindness.comment");
    public static final ConfigBoolean baritoneDebug = new ConfigBoolean(
            "carp.baritone.debug", false, "carp.baritone.debug.comment");
    public static final ConfigBoolean baritonePathRender = new ConfigBoolean(
            "carp.baritone.path_render", true, "carp.baritone.path_render.comment");
    public static final ConfigBoolean baritonePathIgnoreDepth = new ConfigBoolean(
            "carp.baritone.path_ignore_depth", false, "carp.baritone.path_ignore_depth.comment");
    public static final ConfigBoolean baritonePathAsLine = new ConfigBoolean(
            "carp.baritone.path_as_line", false, "carp.baritone.path_as_line.comment");
    public static final ConfigInteger baritonePathLineWidth = new ConfigInteger(
            "carp.baritone.path_line_width", 2, 1, 10, "carp.baritone.path_line_width.comment");
    public static final ConfigBoolean baritonePathFade = new ConfigBoolean(
            "carp.baritone.path_fade", true, "carp.baritone.path_fade.comment");
    public static final ConfigInteger baritonePathFadeStart = new ConfigInteger(
            "carp.baritone.path_fade_start", 20, 0, 50, "carp.baritone.path_fade_start.comment");
    public static final ConfigInteger baritonePathFadeEnd = new ConfigInteger(
            "carp.baritone.path_fade_end", 30, 0, 50, "carp.baritone.path_fade_end.comment");
    public static final ConfigString baritoneColorCurrent = new ConfigString(
            "carp.baritone.color_current", "0xFF0000", "carp.baritone.color_current.comment");
    public static final ConfigString baritoneColorNext = new ConfigString(
            "carp.baritone.color_next", "0xFF0000", "carp.baritone.color_next.comment");
    public static final ConfigString baritoneColorBest = new ConfigString(
            "carp.baritone.color_best", "0x0000FF", "carp.baritone.color_best.comment");
    public static final ConfigString baritoneColorConsidered = new ConfigString(
            "carp.baritone.color_considered", "0x00FFFF", "carp.baritone.color_considered.comment");

    // ========== 水中 & 爬升 ==========
    public static final ConfigBoolean baritoneSprintInWater = new ConfigBoolean(
            "carp.baritone.sprint_in_water", true, "carp.baritone.sprint_in_water.comment");
    public static final ConfigBoolean baritoneSprintAscends = new ConfigBoolean(
            "carp.baritone.sprint_ascends", true, "carp.baritone.sprint_ascends.comment");

    // ========== 寻路 (Baritone) ==========
    // ── 通用寻路参数 ──
    public static final ConfigBoolean baritoneAutoTool = new ConfigBoolean(
            "carp.baritone.auto_tool", true, "carp.baritone.auto_tool.comment");
    public static final ConfigBoolean baritoneAllowInventory = new ConfigBoolean(
            "carp.baritone.allow_inventory", false, "carp.baritone.allow_inventory.comment");
    public static final ConfigBoolean baritoneAllowParkourPlace = new ConfigBoolean(
            "carp.baritone.allow_parkour_place", false, "carp.baritone.allow_parkour_place.comment");
    public static final ConfigBoolean baritoneAllowWaterBucketFall = new ConfigBoolean(
            "carp.baritone.allow_water_bucket", true, "carp.baritone.allow_water_bucket.comment");
    public static final ConfigDouble baritoneBlockPlacePenalty = new ConfigDouble(
            "carp.baritone.block_place_penalty", 20.0, 0.0, 100.0, "carp.baritone.block_place_penalty.comment");
    public static final ConfigDouble baritoneBlockBreakPenalty = new ConfigDouble(
            "carp.baritone.block_break_penalty", 2.0, 0.0, 100.0, "carp.baritone.block_break_penalty.comment");
    public static final ConfigDouble baritoneWalkOnWaterPenalty = new ConfigDouble(
            "carp.baritone.walk_on_water_penalty", 3.0, 0.0, 100.0, "carp.baritone.walk_on_water_penalty.comment");
    public static final ConfigDouble baritoneJumpPenalty = new ConfigDouble(
            "carp.baritone.jump_penalty", 2.0, 0.0, 100.0, "carp.baritone.jump_penalty.comment");
    public static final ConfigDouble baritoneCostHeuristic = new ConfigDouble(
            "carp.baritone.cost_heuristic", 3.563, 0.1, 10.0, "carp.baritone.cost_heuristic.comment");
    public static final ConfigInteger baritoneMaxFallHeight = new ConfigInteger(
            "carp.baritone.max_fall_height", 3, 0, 256, "carp.baritone.max_fall_height.comment");
    public static final ConfigBoolean baritoneAvoidance = new ConfigBoolean(
            "carp.baritone.avoidance", false, "carp.baritone.avoidance.comment");
    public static final ConfigInteger baritonePathTimeout = new ConfigInteger(
            "carp.baritone.path_timeout", 300, 1, 10000, "carp.baritone.path_timeout.comment");
    public static final ConfigInteger baritonePlanAheadTime = new ConfigInteger(
            "carp.baritone.plan_ahead_time", 4000, 500, 20000, "carp.baritone.plan_ahead_time.comment");
    public static final ConfigInteger baritoneBuildTickLimit = new ConfigInteger(
            "carp.baritone.build_tick_limit", 20, 1, 200, "carp.baritone.build_tick_limit.comment");
    public static final ConfigBoolean baritoneAntiCheatCompatibility = new ConfigBoolean(
            "carp.baritone.anti_cheat", false, "carp.baritone.anti_cheat.comment");
    public static final ConfigBoolean baritoneChatControl = new ConfigBoolean(
            "carp.baritone.chat_control", true, "carp.baritone.chat_control.comment");
    public static final ConfigBoolean baritoneFreeLook = new ConfigBoolean(
            "carp.baritone.free_look", false, "carp.baritone.free_look.comment");
    // 自动寻路 (GoTo)
    public static final ConfigBoolean baritoneGotoEnabled = new ConfigBoolean(
            "carp.baritone.goto_enabled", false, "carp.baritone.goto_enabled.comment");
    public static final ConfigHotkey baritoneGotoHotkey = new ConfigHotkey(
            "carp.baritone.goto_hotkey", "", "carp.baritone.goto_hotkey.comment");
    public static final ConfigInteger baritoneGotoX = new ConfigInteger(
            "carp.baritone.goto_x", 0, Integer.MIN_VALUE, Integer.MAX_VALUE, "carp.baritone.goto_x.comment");
    public static final ConfigInteger baritoneGotoZ = new ConfigInteger(
            "carp.baritone.goto_z", 0, Integer.MIN_VALUE, Integer.MAX_VALUE, "carp.baritone.goto_z.comment");
    public static final ConfigBoolean baritoneAllowBreak = new ConfigBoolean(
            "carp.baritone.allow_break", true, "carp.baritone.allow_break.comment");
    public static final ConfigBoolean baritoneAllowPlace = new ConfigBoolean(
            "carp.baritone.allow_place", true, "carp.baritone.allow_place.comment");
    public static final ConfigBoolean baritoneAllowSprint = new ConfigBoolean(
            "carp.baritone.allow_sprint", true, "carp.baritone.allow_sprint.comment");
    public static final ConfigBoolean baritoneAllowParkour = new ConfigBoolean(
            "carp.baritone.allow_parkour", true, "carp.baritone.allow_parkour.comment");

    // 自动挖矿 (Mine)
    public static final ConfigBoolean baritoneMineEnabled = new ConfigBoolean(
            "carp.baritone.mine_enabled", false, "carp.baritone.mine_enabled.comment");
    public static final ConfigHotkey baritoneMineHotkey = new ConfigHotkey(
            "carp.baritone.mine_hotkey", "", "carp.baritone.mine_hotkey.comment");
    public static final ConfigOptionList baritoneMineBlock = new ConfigOptionList(
            "carp.baritone.mine_block", MineBlockTarget.DIAMOND_ORE, "carp.baritone.mine_block.comment");
    public static final ConfigBoolean baritoneMineLegit = new ConfigBoolean(
            "carp.baritone.mine_legit", false, "carp.baritone.mine_legit.comment");
    public static final ConfigBoolean baritoneMineScanDrops = new ConfigBoolean(
            "carp.baritone.mine_scan_drops", true, "carp.baritone.mine_scan_drops.comment");
    public static final ConfigInteger baritoneMineScanInterval = new ConfigInteger(
            "carp.baritone.mine_scan_interval", 20, 1, 200, "carp.baritone.mine_scan_interval.comment");
    public static final ConfigInteger baritoneMineMinY = new ConfigInteger(
            "carp.baritone.mine_min_y", 0, -64, 320, "carp.baritone.mine_min_y.comment");
    public static final ConfigInteger baritoneMineMaxY = new ConfigInteger(
            "carp.baritone.mine_max_y", 256, -64, 320, "carp.baritone.mine_max_y.comment");
    public static final ConfigBoolean baritoneMineExposedOnly = new ConfigBoolean(
            "carp.baritone.mine_exposed_only", false, "carp.baritone.mine_exposed_only.comment");
    public static final ConfigBoolean baritoneMineLegitDiag = new ConfigBoolean(
            "carp.baritone.mine_legit_diag", false, "carp.baritone.mine_legit_diag.comment");

    // 跟随 (Follow)
    public static final ConfigBoolean baritoneFollowEnabled = new ConfigBoolean(
            "carp.baritone.follow_enabled", false, "carp.baritone.follow_enabled.comment");
    public static final ConfigHotkey baritoneFollowHotkey = new ConfigHotkey(
            "carp.baritone.follow_hotkey", "", "carp.baritone.follow_hotkey.comment");
    public static final ConfigOptionList baritoneFollowTarget = new ConfigOptionList(
            "carp.baritone.follow_target", FollowTarget.PLAYER, "carp.baritone.follow_target.comment");
    public static final ConfigDouble baritoneFollowRadius = new ConfigDouble(
            "carp.baritone.follow_radius", 3.0, 1.0, 32.0, "carp.baritone.follow_radius.comment");

    // 探索 (Explore)
    public static final ConfigBoolean baritoneExploreEnabled = new ConfigBoolean(
            "carp.baritone.explore_enabled", false, "carp.baritone.explore_enabled.comment");
    public static final ConfigHotkey baritoneExploreHotkey = new ConfigHotkey(
            "carp.baritone.explore_hotkey", "", "carp.baritone.explore_hotkey.comment");
    public static final ConfigInteger baritoneExploreRange = new ConfigInteger(
            "carp.baritone.explore_range", 500, 50, 5000, "carp.baritone.explore_range.comment");

    // 隧道 (Tunnel)
    public static final ConfigBoolean baritoneTunnelEnabled = new ConfigBoolean(
            "carp.baritone.tunnel_enabled", false, "carp.baritone.tunnel_enabled.comment");
    public static final ConfigHotkey baritoneTunnelHotkey = new ConfigHotkey(
            "carp.baritone.tunnel_hotkey", "", "carp.baritone.tunnel_hotkey.comment");
    public static final ConfigInteger baritoneTunnelWidth = new ConfigInteger(
            "carp.baritone.tunnel_width", 1, 1, 5, "carp.baritone.tunnel_width.comment");
    public static final ConfigInteger baritoneTunnelHeight = new ConfigInteger(
            "carp.baritone.tunnel_height", 2, 1, 5, "carp.baritone.tunnel_height.comment");
    public static final ConfigBoolean baritoneTunnelBackfill = new ConfigBoolean(
            "carp.baritone.tunnel_backfill", false, "carp.baritone.tunnel_backfill.comment");

    // ========== 寻路枚举 ==========

    public enum MineBlockTarget implements IConfigOptionListEntry {
        DIAMOND_ORE, IRON_ORE, GOLD_ORE, EMERALD_ORE, COAL_ORE,
        COPPER_ORE, REDSTONE_ORE, LAPIS_ORE, NETHER_QUARTZ_ORE,
        ANCIENT_DEBRIS, NETHER_GOLD_ORE;
        @Override public String getStringValue() { return name().toLowerCase(); }
        @Override public String getDisplayName() { return t("carp.baritone.mine_block." + name().toLowerCase()); }
        @Override public IConfigOptionListEntry cycle(boolean f) { int i = ordinal() + (f ? 1 : -1); if (i >= values().length) i = 0; else if (i < 0) i = values().length - 1; return values()[i]; }
        @Override public IConfigOptionListEntry fromString(String s) { try { return valueOf(s.toUpperCase()); } catch (Exception e) { return DIAMOND_ORE; } }
    }

    public enum FollowTarget implements IConfigOptionListEntry {
        PLAYER, ENTITY, PLAYERS, ENTITIES;
        @Override public String getStringValue() { return name().toLowerCase(); }
        @Override public String getDisplayName() { return t("carp.baritone.follow_target." + name().toLowerCase()); }
        @Override public IConfigOptionListEntry cycle(boolean f) { int i = ordinal() + (f ? 1 : -1); if (i >= values().length) i = 0; else if (i < 0) i = values().length - 1; return values()[i]; }
        @Override public IConfigOptionListEntry fromString(String s) { try { return valueOf(s.toUpperCase()); } catch (Exception e) { return PLAYER; } }
    }

    public static void init() {
        // 翻译名称由 malilib 在 GUI 渲染时通过 StringUtils.translate(configName) 自动获取
        // 不在 init() 中提前翻译，避免 MC 翻译系统未就绪的时序问题
    }

    private static String t(String key) { return StringUtils.translate(key); }

    // ========== 配置持久化（Gson 手动读写 config/carp.json）==========

    private static final List<IConfigBase> ALL_CONFIGS = collectConfigs();

    private static List<IConfigBase> collectConfigs() {
        List<IConfigBase> list = new ArrayList<>();
        for (java.lang.reflect.Field f : Config.class.getDeclaredFields()) {
            if (IConfigBase.class.isAssignableFrom(f.getType())
                    && java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
                try { list.add((IConfigBase) f.get(null)); } catch (Exception ignored) {}
            }
        }
        return list;
    }

    @Override
    public void load() {
        java.io.File file = new java.io.File(net.minecraft.client.Minecraft.getInstance().gameDirectory, "config/carp.json");
        if (!file.exists()) return;
        try {
            String json = java.nio.file.Files.readString(file.toPath());
            java.util.Map<String, com.google.gson.JsonElement> map = new com.google.gson.Gson().fromJson(json,
                    new com.google.gson.reflect.TypeToken<java.util.Map<String, com.google.gson.JsonElement>>(){}.getType());
            for (IConfigBase cfg : ALL_CONFIGS) {
                com.google.gson.JsonElement val = map.get(cfg.getName());
                if (val != null) cfg.setValueFromJsonElement(val);
            }
        } catch (Exception ignored) {}
    }

    @Override
    public void save() {
        java.io.File file = new java.io.File(net.minecraft.client.Minecraft.getInstance().gameDirectory, "config/carp.json");
        java.util.Map<String, com.google.gson.JsonElement> map = new java.util.LinkedHashMap<>();
        for (IConfigBase cfg : ALL_CONFIGS) {
            map.put(cfg.getName(), cfg.getAsJsonElement());
        }
        try {
            file.getParentFile().mkdirs();
            java.nio.file.Files.writeString(file.toPath(),
                    new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(map));
        } catch (Exception ignored) {}
    }
}