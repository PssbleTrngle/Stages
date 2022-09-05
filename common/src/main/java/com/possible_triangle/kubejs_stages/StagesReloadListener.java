package com.possible_triangle.kubejs_stages;

import com.possible_triangle.kubejs_stages.stage.Stages;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class StagesReloadListener implements ResourceManagerReloadListener {

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        Stages.finishLoad();
    }

}
