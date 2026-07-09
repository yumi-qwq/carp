package com.example.carp.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * Mixin 插件 —— 在 Mixin 加载时检查 MaLiLib 是否已安装。
 * 如果 MaLiLib 不存在，则跳过对 MaLiLib 类的 Mixin 注入以避免崩溃。
 */
public class MixinPlugin implements IMixinConfigPlugin {

    private boolean malilibLoaded = false;

    @Override
    public void onLoad(String mixinPackage) {
        try {
            Class.forName("fi.dy.masa.malilib.MaLiLib");
            malilibLoaded = true;
        } catch (ClassNotFoundException e) {
            malilibLoaded = false;
            System.err.println("[Carp] MaLiLib 未检测到，跳过 MaLiLib Mixin 注入");
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        // 如果目标类是 MaLiLib 的类但 MaLiLib 未加载，则跳过
        if (targetClassName.startsWith("fi.dy.masa.malilib") && !malilibLoaded) {
            return false;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass,
                         String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass,
                          String mixinClassName, IMixinInfo mixinInfo) {
    }
}
