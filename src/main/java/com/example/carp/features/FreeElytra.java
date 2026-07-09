package com.example.carp.features;

import com.example.carp.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class FreeElytra {

    public void onClientTick(Minecraft client) {
        if (!Config.freeElytraEnabled.getBooleanValue()) return;

        LocalPlayer player = client.player;
        if (player == null) return;

        ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chestStack.isEmpty() || !chestStack.is(Items.ELYTRA)) return;

        handleFreeFlight(player);
    }

    private void handleFreeFlight(LocalPlayer player) {
        double speed = Config.freeElytraSpeed.getDoubleValue();
        Minecraft client = Minecraft.getInstance();

        float yaw = player.getYRot(), pitch = player.getXRot();
        double radYaw = Math.toRadians(yaw), radPitch = Math.toRadians(pitch);

        Vec3 lookVec = new Vec3(
            -Math.sin(radYaw) * Math.cos(radPitch),
            -Math.sin(radPitch),
            Math.cos(radYaw) * Math.cos(radPitch)
        ).normalize();
        Vec3 rightVec = new Vec3(Math.cos(radYaw), 0, Math.sin(radYaw)).normalize();

        Vec3 newVelocity = player.getDeltaMovement();

        if (client.options.keyUp.isDown()) newVelocity = newVelocity.add(lookVec.scale(speed * 0.05));
        if (client.options.keyDown.isDown()) newVelocity = newVelocity.add(lookVec.scale(-speed * 0.03));
        if (client.options.keyLeft.isDown()) newVelocity = newVelocity.add(rightVec.scale(-speed * 0.03));
        if (client.options.keyRight.isDown()) newVelocity = newVelocity.add(rightVec.scale(speed * 0.03));
        if (client.options.keyJump.isDown()) newVelocity = newVelocity.add(0, speed * 0.05, 0);
        if (client.options.keyShift.isDown()) newVelocity = newVelocity.add(0, -speed * 0.05, 0);

        double maxSpeed = speed * 2.0;
        if (newVelocity.length() > maxSpeed) newVelocity = newVelocity.normalize().scale(maxSpeed);
        if (newVelocity.y < -maxSpeed) newVelocity = new Vec3(newVelocity.x, -maxSpeed, newVelocity.z);

        player.setDeltaMovement(newVelocity);
    }
}
