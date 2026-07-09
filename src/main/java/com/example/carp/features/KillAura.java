package com.example.carp.features;

import com.example.carp.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

/**
 * KillAura — 持续平滑瞄准 + 不发包时不回弹。
 */
public class KillAura {

    private int attackCooldown, blocking;
    private Entity lockedTarget;
    // 平滑过渡的状态
    private float currentYaw, currentPitch;
    private boolean aimInited;

    public void onClientTick(Minecraft client) {
        if (!Config.killAuraEnabled.getBooleanValue()) { aimInited = false; return; }
        Player p = client.player;
        if (p == null || p.isDeadOrDying() || p.isSpectator()) { aimInited = false; return; }
        var conn = client.getConnection();
        if (conn == null) return;
        if (attackCooldown > 0) attackCooldown--;

        // 单一目标
        boolean singleTarget = Config.killAuraSingleTarget.getBooleanValue();
        if (singleTarget && lockedTarget instanceof LivingEntity le && le.isAlive() && !le.isDeadOrDying()
                && p.distanceToSqr(lockedTarget) < 64.0) { /* keep */ } else { lockedTarget = null; }
        double range = Config.killAuraRange.getDoubleValue();
        Entity target = lockedTarget != null ? lockedTarget : findTarget(client, p, range);
        if (target == null) { lockedTarget = null; aimInited = false; return; }
        if (singleTarget && lockedTarget == null) lockedTarget = target;

        // 计算目标旋转
        Vec3 tp = target.getEyePosition(), pp = p.getEyePosition();
        float ty = (float) Math.toDegrees(Math.atan2(-(tp.x - pp.x), tp.z - pp.z));
        float tpt = (float) -Math.toDegrees(Math.atan2(tp.y - pp.y, Math.sqrt(Math.pow(tp.x - pp.x, 2) + Math.pow(tp.z - pp.z, 2))));

        // 持续平滑过渡
        if (!aimInited) { currentYaw = p.getYRot(); currentPitch = p.getXRot(); aimInited = true; }
        float lf = 0.25f;
        currentYaw = Mth.rotLerp(lf, currentYaw, ty);
        currentPitch = Mth.lerp(lf, currentPitch, tpt);

        // 持续发包旋转 + 仅设头部渲染
        if (Config.killAuraRotations.getBooleanValue()) {
            p.yHeadRot = currentYaw;
            p.yBodyRot = currentYaw;
            conn.send(new ServerboundMovePlayerPacket.Rot(currentYaw, currentPitch, p.onGround(), p.horizontalCollision));
        }

        if (attackCooldown > 0) return;
        attackCooldown = 20 / Config.killAuraCps.getIntegerValue();

        if (Config.killAuraCriticals.getBooleanValue()) {
            conn.send(new ServerboundMovePlayerPacket.PosRot(p.getX(), p.getY()+0.0625, p.getZ(), currentYaw, currentPitch, false, p.horizontalCollision));
            conn.send(new ServerboundMovePlayerPacket.PosRot(p.getX(), p.getY(), p.getZ(), currentYaw, currentPitch, false, p.horizontalCollision));
        }

        if (isHoldingSword(p)) {
            if (blocking <= 0) { client.options.keyUse.setDown(true); blocking = 5; }
            else { blocking--; client.options.keyUse.setDown(false); }
        }

        String mode = Config.killAuraAttackMode.getOptionListValue().getStringValue();
        if ("packet".equals(mode))
            conn.send(ServerboundInteractPacket.createInteractionPacket(target, p.isShiftKeyDown(), InteractionHand.MAIN_HAND, Vec3.ZERO));
        else
            client.gameMode.attack(p, target);
        p.swing(InteractionHand.MAIN_HAND);
    }

    private boolean isHoldingSword(Player p) {
        if (!Config.killAuraAutoBlock.getBooleanValue()) return false;
        var item = p.getMainHandItem().getItem();
        return item == Items.WOODEN_SWORD || item == Items.STONE_SWORD || item == Items.IRON_SWORD
                || item == Items.GOLDEN_SWORD || item == Items.DIAMOND_SWORD || item == Items.NETHERITE_SWORD;
    }

    private Entity findTarget(Minecraft client, Player p, double range) {
        Config.TargetMode mode = (Config.TargetMode) Config.killAuraTargetMode.getOptionListValue();
        Config.SortMode sort = (Config.SortMode) Config.killAuraSortMode.getOptionListValue();
        Entity best = null; double bestScore = Double.MAX_VALUE;
        for (Entity e : client.level.entitiesForRendering()) {
            if (e == p || !(e instanceof LivingEntity le) || !le.isAlive() || le.isDeadOrDying()) continue;
            double dist = p.distanceToSqr(e);
            if (dist > range * range) continue;
            if (!matches(le, mode)) continue;
            double score = switch (sort) { case DISTANCE -> dist; case HEALTH -> -le.getHealth(); };
            if (score < bestScore) { bestScore = score; best = e; }
        }
        return best;
    }

    private boolean matches(LivingEntity e, Config.TargetMode m) {
        return switch (m) {
            case ALL -> true; case PLAYERS -> e instanceof Player;
            case HOSTILE -> e instanceof Enemy;
            case PASSIVE -> e instanceof Animal || (e instanceof Mob mb && !(mb instanceof Enemy));
            case NOT_PASSIVE -> e instanceof Enemy || e instanceof Player;
        };
    }
}
