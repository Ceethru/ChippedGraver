package com.chippedgraver.fabric;

import com.chippedgraver.common.ChippedGraver;
import net.fabricmc.api.ModInitializer;

public class ChippedGraverFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ChippedGraver.init();
    }
}
