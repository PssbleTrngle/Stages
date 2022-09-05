package com.possible_triangle.kubejs_stages;

import net.minecraftforge.fml.common.Mod;

@Mod(KubeJSStages.ID)
public class KubeJSStagesForge {
    public KubeJSStagesForge() {
        KubeJSStages.init();

        //MinecraftForge.EVENT_BUS.addListener((AddReloadListenerEvent event) -> {
        //    event.addListener(new StagesReloadListener());
        //});

        //FMLJavaModLoadingContext.get().getModEventBus().addListener((InterModEnqueueEvent event) -> {
        //    InterModComms.sendTo("theoneprobe", "getTheOneProbe", StagesTOP::new);
        //});
    }
}