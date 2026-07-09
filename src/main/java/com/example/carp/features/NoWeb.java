package com.example.carp.features;

import com.example.carp.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WebBlock;

/**
 * NoWeb — 蜘蛛网无减速。参考 LiquidBounce NoWeb。
 */
public class NoWeb {

    public void onClientTick(Minecraft client) {
        if (!Config.noWebEnabled.getBooleanValue()) return;
        var p = client.player;
        if (p == null || !isInWeb(p)) return;

        String mode = Config.noWebMode.getOptionListValue().getStringValue();

        switch (mode) {
            case "air" -> {
                // 把网当作空气，保持原速
                p.setDeltaMovement(p.getDeltaMovement().multiply(1.2, 1, 1.2));
            }
            case "strafe" -> {
                // strafe 模式：保持运动方向但维持水平速度
                float yaw = p.getYRot();
                if (p.zza != 0 || p.xxa != 0) {
                    float dir = yaw;
                    if (p.zza < 0) dir += 180;
                    if (p.xxa < 0) dir += 90;
                    else if (p.xxa > 0) dir -= 90;
                    double rad = Math.toRadians(dir);
                    p.setDeltaMovement(-Math.sin(rad) * 0.2873, p.getDeltaMovement().y, Math.cos(rad) * 0.2873);
                }
            }
            case "grim" -> {
                // GrimAC 绕过：直接破坏脚下的网
                var pos = p.blockPosition();
                for (int dx = -1; dx <= 1; dx++)
                    for (int dz = -1; dz <= 1; dz++) {
                        var bp = pos.offset(dx, -1, dz);
                        if (client.level.getBlockState(bp).getBlock() instanceof WebBlock) {
                            client.gameMode.startDestroyBlock(bp, net.minecraft.core.Direction.UP);
                        }
                    }
            }
        }
    }

    private boolean isInWeb(net.minecraft.world.entity.player.Player p) {
        return p.level().getBlockState(p.blockPosition()).getBlock() instanceof WebBlock;
    }
}
