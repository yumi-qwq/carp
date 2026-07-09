package baritone.utils;

import baritone.Baritone;
import baritone.api.Settings;
import baritone.api.event.events.RenderEvent;
import baritone.api.utils.BetterBlockPos;
import baritone.behavior.PathingBehavior;
import baritone.pathing.path.PathExecutor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.List;

public final class PathRenderer {

    private PathRenderer() {}

    private static final int FADE_START = 10;
    private static final int FADE_END = 20;

    private static final float[] color = new float[]{1.0F, 1.0F, 1.0F, 1.0F};

    public static void render(RenderEvent event, PathingBehavior behavior) {
        Settings s = Baritone.settings();
        if (!s.renderPath.value) return;

        PathExecutor cur = behavior.getCurrent();
        PathExecutor next = behavior.getNext();

        if (cur != null && cur.getPath() != null) {
            int start = Math.max(cur.getPosition() - 3, 0);
            drawPath(event.getModelViewStack(), cur.getPath().positions(), start, (Color) s.colorCurrentPath.value, s.fadePath.value, 10, 20);
        }
        if (next != null && next.getPath() != null) {
            drawPath(event.getModelViewStack(), next.getPath().positions(), 0, (Color) s.colorNextPath.value, s.fadePath.value, 10, 20);
        }
        behavior.getInProgress().ifPresent(running -> {
            running.bestPathSoFar().ifPresent(p ->
                drawPath(event.getModelViewStack(), p.positions(), 0, (Color) s.colorBestPathSoFar.value, s.fadePath.value, 10, 20));
            running.pathToMostRecentNodeConsidered().ifPresent(p ->
                drawPath(event.getModelViewStack(), p.positions(), 0, (Color) s.colorMostRecentConsidered.value, s.fadePath.value, 10, 20));
        });
    }

    private static void drawPath(PoseStack stack, List<BetterBlockPos> positions, int startIndex, Color color, boolean fadeOut, int fadeStart0, int fadeEnd0) {
        Settings s = Baritone.settings();
        glColor(color, 0.4F);
        RenderSystem.lineWidth(s.pathRenderLineWidthPixels.value);
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);

        int fadeStart = fadeStart0 + startIndex;
        int fadeEnd = fadeEnd0 + startIndex;

        for (int i = startIndex, next; i < positions.size() - 1; i = next) {
            BetterBlockPos start = positions.get(i);
            BetterBlockPos end = positions.get(next = i + 1);

            int dirX = end.x - start.x;
            int dirY = end.y - start.y;
            int dirZ = end.z - start.z;

            while (next + 1 < positions.size() && (!fadeOut || next + 1 < fadeStart) &&
                    (dirX == positions.get(next + 1).x - end.x &&
                            dirY == positions.get(next + 1).y - end.y &&
                            dirZ == positions.get(next + 1).z - end.z)) {
                end = positions.get(++next);
            }

            if (fadeOut) {
                float alpha;
                if (i <= fadeStart) {
                    alpha = 0.4F;
                } else {
                    if (i > fadeEnd) {
                        break;
                    }
                    alpha = 0.4F * (1.0F - (float) (i - fadeStart) / (float) (fadeEnd - fadeStart));
                }
                glColor(color, alpha);
            }

            emitPathLine(bufferBuilder, stack, start.x, start.y, start.z, end.x, end.y, end.z, 0.5D);
        }

        MeshData meshData = bufferBuilder.build();
        if (meshData != null) {
            RenderType.lines().draw(meshData);
        }
    }

    private static void emitPathLine(BufferBuilder bufferBuilder, PoseStack stack, double x1, double y1, double z1, double x2, double y2, double z2, double offset) {
        double vpX = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().x;
        double vpY = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().y;
        double vpZ = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().z;

        emitLine(bufferBuilder, stack,
                x1 + offset - vpX, y1 + offset - vpY, z1 + offset - vpZ,
                x2 + offset - vpX, y2 + offset - vpY, z2 + offset - vpZ
        );
    }

    private static void emitLine(BufferBuilder bufferBuilder, PoseStack stack,
                                 double x1, double y1, double z1,
                                 double x2, double y2, double z2) {
        final double dx = x2 - x1;
        final double dy = y2 - y1;
        final double dz = z2 - z1;
        final double invMag = 1.0 / Math.sqrt(dx * dx + dy * dy + dz * dz);
        final float nx = (float) (dx * invMag);
        final float ny = (float) (dy * invMag);
        final float nz = (float) (dz * invMag);

        PoseStack.Pose pose = stack.last();
        bufferBuilder.addVertex(pose, (float) x1, (float) y1, (float) z1).setColor(color[0], color[1], color[2], color[3]).setNormal(pose, nx, ny, nz);
        bufferBuilder.addVertex(pose, (float) x2, (float) y2, (float) z2).setColor(color[0], color[1], color[2], color[3]).setNormal(pose, nx, ny, nz);
    }

    private static void glColor(Color c, float alpha) {
        float[] colorComponents = c.getColorComponents(null);
        color[0] = colorComponents[0];
        color[1] = colorComponents[1];
        color[2] = colorComponents[2];
        color[3] = alpha;
    }
}
