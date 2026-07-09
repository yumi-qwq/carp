package com.example.carp.mixin;

import com.example.carp.config.Config;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * NoSlowMixin — 在 travel 中处理所有减速（物品使用 + 潜行 + 蜘蛛网）。
 * <p>
 * 合并原 NoSlowMixin (物品减速) 和 NoSlowTravelMixin (潜行/蜘蛛网减速)。
 * MC 26.1.2 在 Player.aiStep() 中对 xxa/zza 乘以 0.2 减速。
 * HEAD 预放大 xxa/zza 补偿减速，TAIL 恢复原始值。
 */
@Mixin(LivingEntity.class)
public abstract class NoSlowMixin {

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

        // 物品使用减速 (吃/喝/拉弓/举盾)
        if (player.isUsingItem()) {
            var anim = player.getUseItem().getUseAnimation();
            if (Config.noSlowConsume.getBooleanValue()
                    && (anim == ItemUseAnimation.EAT || anim == ItemUseAnimation.DRINK))
                boost *= 5.0f;
            if (Config.noSlowBow.getBooleanValue()
                    && (anim == ItemUseAnimation.BOW || anim == ItemUseAnimation.CROSSBOW))
                boost *= 5.0f;
            if (Config.noSlowBlocking.getBooleanValue() && player.isBlocking())
                boost *= 5.0f;
        }

        // 潜行减速
        if (Config.noSlowSneaking.getBooleanValue() && player.isCrouching() && !player.isSprinting())
            boost *= 3.33f;

        // 蜘蛛网减速
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
