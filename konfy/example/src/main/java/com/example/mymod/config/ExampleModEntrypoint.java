package com.example.mymod.config;

import main.konfy.lib.api.KonfyLibApi;
import main.konfy.lib.core.config.impl.ModConfig;

public class ExampleModEntrypoint implements KonfyLibApi {
    @Override
    public ModConfig getConfig() {
        return ExampleConfig.createConfig();
    }
}
