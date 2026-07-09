package com.example.carp.features;

import com.example.carp.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;

/**
 * Speed — 参考 LiquidBounce SpeedCustom。
 */
public class Speed {

    private int velTimeout;

    public void onClientTick(Minecraft client) {
        if (!Config.speedEffect.getBooleanValue()) return;
        LocalPlayer p = client.player;
        if (p == null || !p.onGround()) return;

        // 检查是否在移动
        boolean moving = client.options.keyUp.isDown() || client.options.keyDown.isDown()
                || client.options.keyLeft.isDown() || client.options.keyRight.isDown();
        if (!moving) return;

        if (velTimeout > 0) { velTimeout--; return; }

        int amp = Config.speedAmplifier.getIntegerValue();
        double baseSpeed = 0.2873 + amp * 0.02;

        float yaw = p.getYRot();
        Vec3 vel = p.getDeltaMovement();

        // 计算 strafe 方向
        float forward = client.options.keyUp.isDown() ? 1f : client.options.keyDown.isDown() ? -1f : 0f;
        float strafe = client.options.keyLeft.isDown() ? 1f : client.options.keyRight.isDown() ? -1f : 0f;

        if (forward != 0 || strafe != 0) {
            float direction = yaw;
            if (forward < 0) direction += 180;
            if (strafe < 0) direction += forward == 0 ? 90 : forward > 0 ? -45 : 45;
            else if (strafe > 0) direction += forward == 0 ? -90 : forward > 0 ? 45 : -45;

            double rad = Math.toRadians(direction);
            p.setDeltaMovement(-Math.sin(rad) * baseSpeed, vel.y, Math.cos(rad) * baseSpeed);
        }

        // 跳跃加速
        if (client.options.keyJump.isDown()) {
            p.setDeltaMovement(p.getDeltaMovement().multiply(1.1, 1, 1.1));
            p.jumpFromGround();
        }
    }
}
