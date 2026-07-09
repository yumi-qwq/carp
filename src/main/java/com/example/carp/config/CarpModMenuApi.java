package com.example.carp.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/**
 * Mod Menu 集成 —— 在 Mod Menu 模组列表中为 Carp 提供"配置"按钮，
 * 点击后打开 Carp 的双标签配置界面。
 */
public class CarpModMenuApi implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ConfigGui::new;
    }
}
