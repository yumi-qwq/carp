package com.example.carp.features;

import com.example.carp.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;

public class BoatFly {

    public void onClientTick(Minecraft client) {}

    public static boolean handleBoatTick(Boat boat) {
        if (boat.getControllingPassenger() == null) return false;
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || boat.getControllingPassenger() != client.player) return false;
        if (!Config.boatFlyEnabled.getBooleanValue()) return false;

        double forwardSpeed = Config.boatFlyForwardSpeed.getDoubleValue();
        double backwardSpeed = Config.boatFlyBackwardSpeed.getDoubleValue();
        double upSpeed = Config.boatFlyUpSpeed.getDoubleValue();

        float yaw = boat.getYRot();
        double rad = Math.toRadians(yaw);
        Vec3 delta = Vec3.ZERO;

        if (client.options.keyUp.isDown())
            delta = delta.add(-Math.sin(rad) * forwardSpeed, 0, Math.cos(rad) * forwardSpeed);
        if (client.options.keyDown.isDown())
            delta = delta.add(Math.sin(rad) * backwardSpeed, 0, -Math.cos(rad) * backwardSpeed);
        if (client.options.keyJump.isDown())
            delta = delta.add(0, upSpeed, 0);

        if (delta.lengthSqr() > 0) boat.setDeltaMovement(delta);
        return true;
    }
}
