package com.shole.config;

import main.konfy.lib.api.KonfyLibApi;
import main.konfy.lib.core.config.impl.ModConfig;

public class KonfyIntegration implements KonfyLibApi {

    @Override
    public ModConfig getConfig() {
        return SholeConfig.createConfig();
    }
}
