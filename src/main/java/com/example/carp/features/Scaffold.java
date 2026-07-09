package com.example.carp.features;

import com.example.carp.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import java.util.Random;

/**
 * Scaffold — building bridge. Works with all directions and diagonal sprint.
 */
public class Scaffold {

    private final Random rng = new Random();
    private int cooldown;
    private int placementY;

    public void onClientTick(Minecraft client) {
        if (!Config.scaffoldEnabled.getBooleanValue()) return;
        LocalPlayer p = client.player;
        if (p == null || client.level == null || client.gameMode == null) return;
        if (cooldown > 0) { cooldown--; return; }

        // Also bridge when walking backward
        boolean moving = client.options.keyUp.isDown() || client.options.keyDown.isDown()
                || client.options.keyLeft.isDown() || client.options.keyRight.isDown();
        if (!moving) return;

        Level level = client.level;
        if (p.onGround()) placementY = p.blockPosition().getY() - 1;

        boolean sprinting = p.isSprinting();
        int predictTicks = sprinting ? 2 : 1;
        BlockPos base = p.blockPosition();

        Vec3 vel = p.getDeltaMovement();
        double forwardX = 0, forwardZ = 0;
        if (client.options.keyUp.isDown()) { forwardX -= Math.sin(Math.toRadians(p.getYRot())); forwardZ += Math.cos(Math.toRadians(p.getYRot())); }
        if (client.options.keyDown.isDown()) { forwardX += Math.sin(Math.toRadians(p.getYRot())); forwardZ -= Math.cos(Math.toRadians(p.getYRot())); }
        if (client.options.keyLeft.isDown()) { forwardX -= Math.cos(Math.toRadians(p.getYRot())); forwardZ -= Math.sin(Math.toRadians(p.getYRot())); }
        if (client.options.keyRight.isDown()) { forwardX += Math.cos(Math.toRadians(p.getYRot())); forwardZ += Math.sin(Math.toRadians(p.getYRot())); }

        if (forwardX != 0 || forwardZ != 0) {
            // 预测 1 tick 后的位置，放在脚下（placementY），不是正前方
            base = BlockPos.containing(p.getX() + forwardX * predictTicks * 0.25, p.getY(), p.getZ() + forwardZ * predictTicks * 0.25);
        }

        BlockPos target = new BlockPos(base.getX(), placementY, base.getZ());
        if (!level.getBlockState(target).isAir()) {
            target = target.above();
            if (!level.getBlockState(target).isAir()) return;
        }

        target = pickBestTarget(p, level, target, placementY);

        PlaceResult pr = findFace(p, target);
        if (pr == null) return;

        if (!switchToBlock(p)) return;
        if (Config.scaffoldSprint.getBooleanValue()) p.setSprinting(true);

        InteractionResult r = client.gameMode.useItemOn(p, InteractionHand.MAIN_HAND,
                new BlockHitResult(pr.hit, pr.face, pr.neighbor, false));

        if (r.consumesAction()) {
            int min = Config.scaffoldDelayMin.getIntegerValue(), max = Config.scaffoldDelayMax.getIntegerValue();
            cooldown = (min + rng.nextInt(Math.max(1, max - min + 1))) / 50;
            if (sprinting) cooldown = Math.max(1, cooldown / 2);
        }
    }

    private static class PlaceResult { final Vec3 hit; final Direction face; final BlockPos neighbor;
        PlaceResult(Vec3 h, Direction f, BlockPos n) { hit = h; face = f; neighbor = n; } }

    private BlockPos pickBestTarget(LocalPlayer p, Level level, BlockPos predicted, int placementY) {
        int px = p.blockPosition().getX(), pz = p.blockPosition().getZ();
        BlockPos feet = new BlockPos(px, placementY, pz);
        if (level.getBlockState(feet).isAir()) return feet;
        if (level.getBlockState(predicted).isAir()) return predicted;
        if (px != predicted.getX()) { BlockPos midX = new BlockPos(px > predicted.getX() ? px - 1 : px + 1, placementY, pz); if (level.getBlockState(midX).isAir()) return midX; }
        if (pz != predicted.getZ()) { BlockPos midZ = new BlockPos(px, placementY, pz > predicted.getZ() ? pz - 1 : pz + 1); if (level.getBlockState(midZ).isAir()) return midZ; }
        return predicted;
    }

    private PlaceResult findFace(LocalPlayer p, BlockPos target) {
        Level level = p.level();
        BlockPos below = target.below();
        if (!level.getBlockState(below).isAir()) return new PlaceResult(Vec3.atCenterOf(below).add(0, 0.5, 0), Direction.UP, below);
        for (Direction d : Direction.values()) {
            if (d == Direction.DOWN || d == Direction.UP) continue;
            BlockPos nb = target.relative(d);
            if (!level.getBlockState(nb).isAir()) return new PlaceResult(Vec3.atCenterOf(nb).add(d.getOpposite().getStepX()*0.5, 0, d.getOpposite().getStepZ()*0.5), d.getOpposite(), nb);
        }
        return null;
    }

    private boolean switchToBlock(LocalPlayer p) {
        int pref = Config.scaffoldSlot.getIntegerValue() - 1;
        ItemStack s = p.getInventory().getItem(pref);
        if (!s.isEmpty() && s.getItem() instanceof BlockItem) { p.getInventory().setSelectedSlot(pref); return true; }
        for (int i = 0; i < 9; i++) { ItemStack st = p.getInventory().getItem(i); if (!st.isEmpty() && st.getItem() instanceof BlockItem) { p.getInventory().setSelectedSlot(i); return true; } }
        return false;
    }
}
