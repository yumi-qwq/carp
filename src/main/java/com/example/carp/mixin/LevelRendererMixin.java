package com.example.carp.mixin;

import baritone.api.BaritoneAPI;
import baritone.api.event.events.RenderEvent;
import baritone.behavior.PathingBehavior;
import baritone.utils.PathRenderer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Inject(method = "render", at = @At("RETURN"))
    private void onRender(GraphicsResourceAllocator allocator, DeltaTracker tracker, boolean bl,
                          CameraRenderState camState, Matrix4fc projMatrix, GpuBufferSlice bufferSlice,
                          Vector4f fogColor, boolean hasFog, CallbackInfo ci) {
        try {
            baritone.Baritone b = (baritone.Baritone) BaritoneAPI.getProvider().getPrimaryBaritone();
            if (b == null) return;
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;
            PathingBehavior pb = b.getPathingBehavior();
            PoseStack stack = new PoseStack();
            // apply camera transform
            stack.mulPose(camState.orientation);
            stack.translate(-camState.pos.x, -camState.pos.y, -camState.pos.z);
            pb.onRenderPass(new RenderEvent(tracker.getGameTimeDeltaTicks(), stack, new Matrix4f(projMatrix)));
        } catch (Exception ignored) {}
    }
}
