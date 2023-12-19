package com.chyzman.chyzyLogistics.client;

import net.fabricmc.api.ClientModInitializer;

public class ChyzyLogisticsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientEventListeners.init();
    }
}