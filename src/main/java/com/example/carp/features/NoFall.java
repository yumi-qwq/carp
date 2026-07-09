package com.example.carp.features;

import com.example.carp.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * NoFall — 防摔落伤害。参考 LiquidBounce NoFall 模块。
 */
public class NoFall {

    private int mlgCooldown;

    public void onClientTick(Minecraft client) {
        if (!Config.noFallEnabled.getBooleanValue()) return;
        var p = client.player;
        if (p == null || p.isCreative() || p.isSpectator()) return;
        if (p.getAbilities().flying || p.getAbilities().invulnerable) return;
        if (p.isFallFlying()) return;

        String mode = Config.noFallMode.getOptionListValue().getStringValue();

        switch (mode) {
            case "ground" -> {
                if (p.fallDistance > 3.0f) {
                    p.connection.send(new net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.PosRot(
                            p.getX(), p.getY() - 0.1, p.getZ(), p.getYRot(), p.getXRot(), true, p.horizontalCollision
                    ));
                }
            }
            case "noground" -> {
                if (p.fallDistance > 2.5f) {
                    p.connection.send(new net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.PosRot(
                            p.getX(), p.getY() - 0.1, p.getZ(), p.getYRot(), p.getXRot(), false, p.horizontalCollision
                    ));
                }
            }
            case "packet" -> {
                if (p.fallDistance > 2.0f && p.getDeltaMovement().y < -0.5) {
                    p.connection.send(new net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.PosRot(
                            p.getX(), p.getY() + 0.42, p.getZ(), p.getYRot(), p.getXRot(), true, p.horizontalCollision
                    ));
                    p.fallDistance = 0;
                }
            }
            case "mlg" -> {
                if (p.fallDistance > 5.0f && mlgCooldown <= 0) {
                    var hit = p.pick(5, 0, false);
                    if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
                        var bhr = (BlockHitResult) hit;
                        for (int i = 0; i < 9; i++) {
                            var s = p.getInventory().getItem(i);
                            if (s.getItem() == Items.WATER_BUCKET) {
                                p.getInventory().setSelectedSlot(i);
                                client.gameMode.useItemOn(p, InteractionHand.MAIN_HAND, bhr);
                                mlgCooldown = 20;
                                break;
                            }
                        }
                    }
                }
                if (mlgCooldown > 0) mlgCooldown--;
            }
        }
    }
}
