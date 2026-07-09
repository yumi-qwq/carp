package com.example.carp.mixin;

import com.example.carp.config.Config;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * NoSlowTravelMixin — 在 travel 中处理潜行/蜘蛛网减速。
 * <p>
 * 潜行和蜘蛛网减速发生在 LivingEntity.travel() 内部（降低 speed 变量）。
 * HEAD 预放大 xxa/zza 补偿 speed 降低，TAIL 恢复原始值。
 */
@Mixin(LivingEntity.class)
public abstract class NoSlowTravelMixin {

    @Unique
    private float carp$origXxa;
    @Unique
    private float carp$origZza;

    @Inject(method = "travel", at = @At("HEAD"))
    private void onTravelHead(Vec3 movement, CallbackInfo ci) {
        if (!Config.noSlowEnabled.getBooleanValue()) return;
        if (!((Object) this instanceof Player player)) return;

        carp$origXxa = player.xxa;
        carp$origZza = player.zza;

        float boost = 1.0f;

        if (Config.noSlowSneaking.getBooleanValue() && player.isCrouching() && !player.isSprinting())
            boost *= 3.33f;

        if (Config.noSlowWeb.getBooleanValue() && isInWeb(player))
            boost *= 4.0f;

        if (boost > 1.0f) {
            float userMul = (float) Config.noSlowMultiplier.getDoubleValue();
            if (userMul > 0) boost *= userMul;
            player.xxa *= boost;
            player.zza *= boost;
        }
    }

    @Inject(method = "travel", at = @At("TAIL"))
    private void onTravelTail(Vec3 movement, CallbackInfo ci) {
        if (!Config.noSlowEnabled.getBooleanValue()) return;
        if (!((Object) this instanceof Player player)) return;
        player.xxa = carp$origXxa;
        player.zza = carp$origZza;
    }

    @Unique
    private static boolean isInWeb(Player p) {
        return p.level().getBlockState(p.blockPosition()).getBlock()
                instanceof net.minecraft.world.level.block.WebBlock;
    }
}
