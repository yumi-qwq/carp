package com.example.carp.mixin;

import com.example.carp.config.Config;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemUseAnimation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * NoSlowMixin — 拦截 aiStep 中的物品使用减速（吃/喝/拉弓/举盾/潜行/蜘蛛网）。
 * <p>
 * MC 1.21.11 (1.21.x 系列) 的减速机制与 26.2 不同：
 * - 26.2 在 LocalPlayer.modifyInput() 中调用 Vec2.scale() 进行减速
 * - 1.21.x 在 Player.aiStep() 中使用 0.2F 常量对输入进行缩放
 * <p>
 * 此处使用 @ModifyConstant 拦截 aiStep 中的 0.2F 减速常量，
 * 根据配置条件决定是否跳过减速。
 */
@Mixin(LocalPlayer.class)
public abstract class NoSlowMixin {

    @ModifyConstant(method = "aiStep", constant = @Constant(floatValue = 0.2F))
    private float modifyItemSlowdown(float constant) {
        if (!Config.noSlowEnabled.getBooleanValue())
            return constant;

        LocalPlayer self = (LocalPlayer) (Object) this;

        if (self.isUsingItem()) {
            var anim = self.getUseItem().getUseAnimation();
            if (Config.noSlowConsume.getBooleanValue()
                    && (anim == ItemUseAnimation.EAT || anim == ItemUseAnimation.DRINK))
                return 1.0F;
            if (Config.noSlowBow.getBooleanValue()
                    && (anim == ItemUseAnimation.BOW || anim == ItemUseAnimation.CROSSBOW))
                return 1.0F;
        }

        if (Config.noSlowBlocking.getBooleanValue() && self.isBlocking())
            return 1.0F;

        if (Config.noSlowSneaking.getBooleanValue() && self.isCrouching() && !self.isSprinting())
            return (float) Config.noSlowMultiplier.getDoubleValue();

        return constant;
    }
}
