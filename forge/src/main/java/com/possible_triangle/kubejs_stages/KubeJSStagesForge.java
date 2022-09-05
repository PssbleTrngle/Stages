package com.possible_triangle.kubejs_stages;

import com.possible_triangle.kubejs_stages.features.StagesTOP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(KubeJSStages.ID)
public class KubeJSStagesForge {
    public KubeJSStagesForge() {
        KubeJSStages.init();

        MinecraftForge.EVENT_BUS.addListener((AddReloadListenerEvent event) -> {
            event.addListener(new StagesReloadListener());
        });

        FMLJavaModLoadingContext.get().getModEventBus().addListener((InterModEnqueueEvent event) -> {
            InterModComms.sendTo("theoneprobe", "getTheOneProbe", StagesTOP::new);
        });
    }
}