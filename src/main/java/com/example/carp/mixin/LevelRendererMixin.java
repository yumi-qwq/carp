package com.example.carp.mixin;

import baritone.api.BaritoneAPI;
import baritone.api.event.events.RenderEvent;
import baritone.behavior.PathingBehavior;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Inject(method = "renderLevel", at = @At("RETURN"))
    private void onRenderLevel(GraphicsResourceAllocator allocator, DeltaTracker tracker, boolean bl,
                               Camera camera, Matrix4f orientation, Matrix4f projMatrix,
                               GpuBufferSlice bufferSlice, Vector4f fogColor, boolean hasFog, CallbackInfo ci) {
        try {
            baritone.Baritone b = (baritone.Baritone) BaritoneAPI.getProvider().getPrimaryBaritone();
            if (b == null) return;
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;
            PathingBehavior pb = b.getPathingBehavior();
            PoseStack stack = new PoseStack();
            stack.mulPose(orientation);
            stack.translate(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z);
            pb.onRenderPass(new RenderEvent(tracker.getGameTimeDeltaPartialTick(false), stack, projMatrix));
        } catch (Exception ignored) {}
    }
}
