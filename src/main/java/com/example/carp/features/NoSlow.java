package com.example.carp.features;

import com.example.carp.config.Config;
import net.minecraft.client.Minecraft;

/**
 * NoSlow — 取消物品/方块减速。
 * <p>
 * 核心逻辑已移至 NoSlowMixin（拦截 modifyInput / isSlowDueToUsingItem / travel）。
 * onClientTick 仅保留轻量状态检查，不直接操作玩家输入。
 */
public class NoSlow {

    public void onClientTick(Minecraft client) {
        // NoSlow 核心逻辑在 NoSlowMixin 中处理。
        // onClientTick 保留以供未来扩展（如状态检测、通知等）。
        if (!Config.noSlowEnabled.getBooleanValue()) return;
        // 无额外每 tick 操作；所有减速拦截由 Mixin 完成。
    }
}
