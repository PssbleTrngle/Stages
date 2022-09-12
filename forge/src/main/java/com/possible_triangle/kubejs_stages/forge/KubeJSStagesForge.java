package com.possible_triangle.kubejs_stages.forge;

import com.possible_triangle.kubejs_stages.KubeJSStages;
import net.minecraftforge.fml.common.Mod;

@Mod(KubeJSStages.ID)
public class KubeJSStagesForge {
    public KubeJSStagesForge() {
        KubeJSStages.init();
    }
}