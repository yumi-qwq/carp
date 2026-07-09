package com.example.carp.features;

import com.example.carp.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

/**
 * 渲染增强：夜视、去南瓜、反恶心、反黑暗。
 * 修复：跨世界切换时自动重新应用夜视。
 */
public class RenderFeatures {

    private Player lastPlayer;
    private boolean nauseaCleared, blindnessCleared;

    public void onClientTick(Minecraft client) {
        Player p = client.player;
        if (p == null) return;

        // 检测世界切换（玩家对象变化）
        if (p != lastPlayer) {
            lastPlayer = p;
            // 新世界中重新应用夜视（如果配置开启了）
            if (Config.nightVision.getBooleanValue()) {
                p.forceAddEffect(new MobEffectInstance(MobEffects.NIGHT_VISION,
                        MobEffectInstance.INFINITE_DURATION, 0, true, false), null);
            }
        }

        // 夜视
        boolean wantNV = Config.nightVision.getBooleanValue();
        if (wantNV && !p.hasEffect(MobEffects.NIGHT_VISION)) {
            p.forceAddEffect(new MobEffectInstance(MobEffects.NIGHT_VISION,
                    MobEffectInstance.INFINITE_DURATION, 0, true, false), null);
        }
        if (!wantNV && p.hasEffect(MobEffects.NIGHT_VISION)) {
            p.removeEffect(MobEffects.NIGHT_VISION);
        }

        // 反恶心
        if (Config.antiNausea.getBooleanValue()) p.removeEffect(MobEffects.NAUSEA);
        // 反黑暗
        if (Config.antiBlindness.getBooleanValue()) {
            p.removeEffect(MobEffects.BLINDNESS);
            p.removeEffect(MobEffects.DARKNESS);
        }
    }
}
