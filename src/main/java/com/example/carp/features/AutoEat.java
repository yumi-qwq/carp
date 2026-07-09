package com.example.carp.features;

import com.example.carp.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

/**
 * AutoEat — 饱食度/血量低于 70% 时自动进食。
 */
public class AutoEat {

    private int eatCooldown;

    public void onClientTick(Minecraft client) {
        if (!Config.autoEatEnabled.getBooleanValue()) return;
        Player p = client.player;
        if (p == null || p.isDeadOrDying() || p.isSpectator()) return;
        if (eatCooldown > 0) { eatCooldown--; return; }

        float healthPct = p.getHealth() / p.getMaxHealth();
        float foodPct = p.getFoodData().getFoodLevel() / 20f;

        if (healthPct >= 0.7f && foodPct >= 0.7f) return;

        int foodSlot = findFoodSlot(p);
        if (foodSlot < 0) return;

        p.getInventory().setSelectedSlot(foodSlot);
        client.options.keyUse.setDown(true);
        eatCooldown = 10;
    }

    private int findFoodSlot(Player p) {
        if (isEdible(p.getMainHandItem())) return p.getInventory().getSelectedSlot();
        for (int i = 0; i < 9; i++) {
            if (isEdible(p.getInventory().getItem(i))) return i;
        }
        return -1;
    }

    private boolean isEdible(ItemStack stack) {
        if (stack.isEmpty()) return false;
        FoodProperties food = stack.get(DataComponents.FOOD);
        return food != null && food.nutrition() > 0;
    }
}
