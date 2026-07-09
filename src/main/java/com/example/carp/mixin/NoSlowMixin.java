package com.example.carp.mixin;

import com.example.carp.config.Config;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * NoSlowMixin — 拦截 modifyInput 中的物品使用减速（吃/喝/拉弓/举盾）。
 * <p>
 * MC 26.2 在 LocalPlayer.modifyInput() 中调用 Vec2.scale() 对使用物品时的
 * 移动输入进行减速。此 Mixin 用 @Redirect 跳过减速调用，直接返回原始输入。
 */
@Mixin(LocalPlayer.class)
public abstract class NoSlowMixin {

    @Redirect(method = "modifyInput", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/phys/Vec2;scale(F)Lnet/minecraft/world/phys/Vec2;",
            ordinal = 1))
    private Vec2 redirectItemSlowdown(Vec2 instance, float multiplier) {
        if (!Config.noSlowEnabled.getBooleanValue())
            return instance.scale(multiplier);

        LocalPlayer self = (LocalPlayer) (Object) this;

        if (self.isUsingItem()) {
            var anim = self.getUseItem().getUseAnimation();
            if (Config.noSlowConsume.getBooleanValue()
                    && (anim == ItemUseAnimation.EAT || anim == ItemUseAnimation.DRINK))
                return instance;
            if (Config.noSlowBow.getBooleanValue()
                    && (anim == ItemUseAnimation.BOW || anim == ItemUseAnimation.CROSSBOW))
                return instance;
        }

        if (Config.noSlowBlocking.getBooleanValue() && self.isBlocking())
            return instance;

        return instance.scale(multiplier);
    }
}
