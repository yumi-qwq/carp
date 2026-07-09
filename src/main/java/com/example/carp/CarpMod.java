package com.example.carp;

import com.example.carp.config.Config;
import com.example.carp.config.ConfigGui;
import com.example.carp.features.*;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.hotkeys.IKeybindManager;
import fi.dy.masa.malilib.hotkeys.IKeybindProvider;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class CarpMod implements ClientModInitializer, IKeybindProvider {
    public static final String MOD_ID = "carp";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private final Scaffold scaffold = new Scaffold();
    private final BoatFly boatFly = new BoatFly();
    private final FreeElytra freeElytra = new FreeElytra();
    private final Speed speed = new Speed();
    private final NoFall noFall = new NoFall();
    private final NoSlow noSlow = new NoSlow();
    private final NoWeb noWeb = new NoWeb();
    private final KillAura killAura = new KillAura();
    private final RenderFeatures renderFeatures = new RenderFeatures();
    private final MovementFeatures movementFeatures = new MovementFeatures();
    private final AutoEat autoEat = new AutoEat();
    private final Weeding weeding = new Weeding();
    private final BaritoneProcess baritoneProcess = new BaritoneProcess();

    @Override
    public void onInitializeClient() {
        Config.init();
        ConfigManager.getInstance().registerConfigHandler(MOD_ID, new Config());
        InputEventHandler.getKeybindManager().registerKeybindProvider(this);

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (Minecraft.getInstance().player == null) return;
            scaffold.onClientTick(client);
            boatFly.onClientTick(client);
            freeElytra.onClientTick(client);
            speed.onClientTick(client);
            noFall.onClientTick(client);
            noSlow.onClientTick(client);
            noWeb.onClientTick(client);
            killAura.onClientTick(client);
            renderFeatures.onClientTick(client);
            movementFeatures.onClientTick(client);
            autoEat.onClientTick(client);
            weeding.onClientTick(client);
            baritoneProcess.onClientTick(client);
        });

    }

    @Override
    public void addKeysToMap(IKeybindManager m) {
        m.addKeybindToMap(Config.openConfigHotkey.getKeybind());
        m.addKeybindToMap(Config.scaffoldHotkey.getKeybind());
        m.addKeybindToMap(Config.killAuraHotkey.getKeybind());
        m.addKeybindToMap(Config.baritoneGotoHotkey.getKeybind());
        m.addKeybindToMap(Config.baritoneMineHotkey.getKeybind());
        m.addKeybindToMap(Config.baritoneFollowHotkey.getKeybind());
        m.addKeybindToMap(Config.baritoneExploreHotkey.getKeybind());
        m.addKeybindToMap(Config.baritoneTunnelHotkey.getKeybind());
    }
    @Override
    public void addHotkeys(IKeybindManager m) {
        m.addHotkeysForCategory(MOD_ID, "carp.hotkeys.category.general",
                List.of(Config.openConfigHotkey, Config.scaffoldHotkey, Config.killAuraHotkey,
                        Config.baritoneGotoHotkey, Config.baritoneMineHotkey,
                        Config.baritoneFollowHotkey, Config.baritoneExploreHotkey, Config.baritoneTunnelHotkey));
        Config.openConfigHotkey.getKeybind().setCallback((a, k) -> { ConfigGui.open(); return true; });
        Config.scaffoldHotkey.getKeybind().setCallback((a, k) -> {
            boolean on = !Config.scaffoldEnabled.getBooleanValue();
            Config.scaffoldEnabled.setBooleanValue(on);
            return true;
        });
        Config.killAuraHotkey.getKeybind().setCallback((a, k) -> {
            boolean on = !Config.killAuraEnabled.getBooleanValue();
            Config.killAuraEnabled.setBooleanValue(on);
            return true;
        });
        Config.baritoneGotoHotkey.getKeybind().setCallback((a, k) -> {
            boolean on = !Config.baritoneGotoEnabled.getBooleanValue();
            Config.baritoneGotoEnabled.setBooleanValue(on);
            return true;
        });
        Config.baritoneMineHotkey.getKeybind().setCallback((a, k) -> {
            boolean on = !Config.baritoneMineEnabled.getBooleanValue();
            Config.baritoneMineEnabled.setBooleanValue(on);
            return true;
        });
        Config.baritoneFollowHotkey.getKeybind().setCallback((a, k) -> {
            boolean on = !Config.baritoneFollowEnabled.getBooleanValue();
            Config.baritoneFollowEnabled.setBooleanValue(on);
            return true;
        });
        Config.baritoneExploreHotkey.getKeybind().setCallback((a, k) -> {
            boolean on = !Config.baritoneExploreEnabled.getBooleanValue();
            Config.baritoneExploreEnabled.setBooleanValue(on);
            return true;
        });
        Config.baritoneTunnelHotkey.getKeybind().setCallback((a, k) -> {
            boolean on = !Config.baritoneTunnelEnabled.getBooleanValue();
            Config.baritoneTunnelEnabled.setBooleanValue(on);
            return true;
        });
    }
}
