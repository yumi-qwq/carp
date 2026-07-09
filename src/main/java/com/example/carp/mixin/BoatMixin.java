package com.example.carp.mixin;

import com.example.carp.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class BoatMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onEntityTick(CallbackInfo ci) {
        if (!Config.boatFlyEnabled.getBooleanValue()) return;
        Entity self = (Entity) (Object) this;
        if (!(self instanceof Boat boat)) return;

        Minecraft client = Minecraft.getInstance();
        if (client.player == null || boat.getControllingPassenger() != client.player) return;

        double forwardSpeed = Config.boatFlyForwardSpeed.getDoubleValue();
        double backwardSpeed = Config.boatFlyBackwardSpeed.getDoubleValue();
        double upSpeed = Config.boatFlyUpSpeed.getDoubleValue();

        float yaw = boat.getYRot();
        double rad = Math.toRadians(yaw);
        Vec3 move = Vec3.ZERO;

        if (client.options.keyUp.isDown())
            move = move.add(-Math.sin(rad) * forwardSpeed, 0, Math.cos(rad) * forwardSpeed);
        if (client.options.keyDown.isDown())
            move = move.add(Math.sin(rad) * backwardSpeed, 0, -Math.cos(rad) * backwardSpeed);
        if (client.options.keyJump.isDown())
            move = move.add(0, upSpeed, 0);

        if (move.lengthSqr() > 0)
            boat.setDeltaMovement(move);
    }
}
