package com.possible_triangle.kubejs_stages;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class KubeJSStagesFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        KubeJSStages.init();

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new IdentifiableResourceReloadListener() {
            private PreparableReloadListener inner = new StagesReloadListener();
            private ResourceLocation id = new ResourceLocation(KubeJSStages.ID, "listener");

            @Override
            public ResourceLocation getFabricId() {
                return id;
            }

            @Override
            public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller profilerFiller, ProfilerFiller profilerFiller2, Executor executor, Executor executor2) {
                return inner.reload(preparationBarrier, resourceManager  , profilerFiller, profilerFiller2, executor, executor2);
            }
        });
    }

}
