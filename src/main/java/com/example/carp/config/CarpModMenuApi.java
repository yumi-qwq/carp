package com.example.carp.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/**
 * Mod Menu 集成 — 为 Carp 在 Mod Menu 列表中提供配置按钮。
 */
public class CarpModMenuApi implements ModMenuApi {

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ConfigScreenFactory getModConfigScreenFactory() {
        return ConfigGui::new;
    }
}
