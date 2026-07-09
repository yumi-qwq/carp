package baritone.utils;

import baritone.Baritone;
import baritone.api.Settings;
import baritone.api.event.events.RenderEvent;
import baritone.api.utils.BetterBlockPos;
import baritone.behavior.PathingBehavior;
import baritone.pathing.path.PathExecutor;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.List;

public final class PathRenderer {

    private PathRenderer() {}

    private static final int FADE_START = 10;
    private static final int FADE_END = 20;

    public static void render(RenderEvent event, PathingBehavior behavior) {
        Settings s = Baritone.settings();
        if (!s.renderPath.value) return;

        PathExecutor cur = behavior.getCurrent();
        PathExecutor next = behavior.getNext();

        if (cur != null && cur.getPath() != null) {
            int start = Math.max(cur.getPosition() - 3, 0);
            drawPath(cur.getPath().positions(), start, (Color) s.colorCurrentPath.value, s);
        }
        if (next != null && next.getPath() != null) {
            drawPath(next.getPath().positions(), 0, (Color) s.colorNextPath.value, s);
        }
        behavior.getInProgress().ifPresent(running -> {
            running.bestPathSoFar().ifPresent(p ->
                drawPath(p.positions(), 0, (Color) s.colorBestPathSoFar.value, s));
            running.pathToMostRecentNodeConsidered().ifPresent(p ->
                drawPath(p.positions(), 0, (Color) s.colorMostRecentConsidered.value, s));
        });
    }

    private static void drawPath(List<BetterBlockPos> positions, int startIdx, Color color, Settings s) {
        if (positions.size() <= startIdx + 1) return;

        boolean fade = s.fadePath.value;
        boolean ignoreDepth = s.renderPathIgnoreDepth.value;
        float lineWidth = s.pathRenderLineWidthPixels.value;
        boolean asLine = s.renderPathAsLine.value;
        int fadeStartIdx = FADE_START + startIdx;
        int fadeEndIdx = FADE_END + startIdx;

        for (int i = startIdx, next; i < positions.size() - 1; i = next) {
            BetterBlockPos start = positions.get(i);
            BetterBlockPos end = positions.get(next = i + 1);
            int dirX = end.x - start.x;
            int dirY = end.y - start.y;
            int dirZ = end.z - start.z;
            while (next + 1 < positions.size() && (!fade || next + 1 < fadeStartIdx)
                    && dirX == positions.get(next + 1).x - end.x
                    && dirY == positions.get(next + 1).y - end.y
                    && dirZ == positions.get(next + 1).z - end.z) {
                end = positions.get(++next);
            }

            float alpha;
            if (fade) {
                if (i <= fadeStartIdx) alpha = 0.4F;
                else if (i > fadeEndIdx) break;
                else alpha = 0.4F * (1.0F - (float)(i - fadeStartIdx) / (float)(fadeEndIdx - fadeStartIdx));
            } else {
                alpha = 1.0F;
            }

            int segColor = packColor(color, alpha);
            double offset = 0.5;
            Vec3 from = new Vec3(start.x + offset, start.y + offset, start.z + offset);
            Vec3 to = new Vec3(end.x + offset, end.y + offset, end.z + offset);
            emitLine(from, to, segColor, lineWidth, ignoreDepth);

            if (!asLine) {
                double extra = offset + 0.03;
                Vec3 toHigh = new Vec3(end.x + offset, end.y + extra, end.z + offset);
                Vec3 fromHigh = new Vec3(start.x + offset, start.y + extra, start.z + offset);
                emitLine(to, toHigh, segColor, lineWidth, ignoreDepth);
                emitLine(toHigh, fromHigh, segColor, lineWidth, ignoreDepth);
                emitLine(fromHigh, from, segColor, lineWidth, ignoreDepth);
            }
        }
    }

    private static void emitLine(Vec3 from, Vec3 to, int color, float lineWidth, boolean ignoreDepth) {
        try {
            var props = Gizmos.line(from, to, color, lineWidth);
            if (ignoreDepth) props.setAlwaysOnTop();
        } catch (IllegalStateException ignored) {}
    }

    private static int packColor(Color c, float alpha) {
        int a = Math.clamp((int)(alpha * 255), 0, 255);
        return (a << 24) | (c.getRed() << 16) | (c.getGreen() << 8) | c.getBlue();
    }
}