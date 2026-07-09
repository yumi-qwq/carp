package com.example.carp.features;

import com.example.carp.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;

/**
 * 自动疾跑 + 自动重生。
 * 速度效果已移至 Speed 类（strafe 模式）。
 */
public class MovementFeatures {

    private boolean respawnSent;

    public void onClientTick(Minecraft client) {
        if (client.player == null) return;

        // 自动疾跑
        if (Config.autoSprint.getBooleanValue()) {
            if (client.player.zza > 0 && !client.player.isInWater() && !client.player.isUsingItem()) {
                client.player.setSprinting(true);
            }
        }

        // 自动重生
        if (Config.autoRespawn.getBooleanValue()) {
            if (client.player.isDeadOrDying() && !respawnSent) {
                client.player.connection.send(
                    new ServerboundClientCommandPacket(
                        ServerboundClientCommandPacket.Action.PERFORM_RESPAWN));
                respawnSent = true;
            }
            if (!client.player.isDeadOrDying()) respawnSent = false;
        }
    }
}
