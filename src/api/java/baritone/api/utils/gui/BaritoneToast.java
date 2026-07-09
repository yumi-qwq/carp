package baritone.api.utils.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.components.toasts.SystemToast;

public class BaritoneToast {

    private static final SystemToast.SystemToastId BARITONE_TOAST_ID = new SystemToast.SystemToastId(5000L);

    public static void addOrUpdate(Component title, Component subtitle) {
        SystemToast.addOrUpdate(Minecraft.getInstance().getToastManager(), BARITONE_TOAST_ID, title, subtitle);
    }
}
