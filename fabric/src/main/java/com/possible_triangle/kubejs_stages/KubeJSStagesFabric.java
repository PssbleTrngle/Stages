package com.possible_triangle.kubejs_stages;

import net.fabricmc.api.ModInitializer;

public class KubeJSStagesFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        KubeJSStages.init();

        /*
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
         */
    }

}
