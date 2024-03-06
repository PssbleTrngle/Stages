package com.possible_triangle.stages;

import com.possible_triangle.stages.network.SyncMessage;
import net.minecraft.resources.ResourceLocation;

import java.util.stream.Stream;

public class ClientStagesAccess extends StagesAccess {
    private Stage disabledContent = Stage.EMPTY;

    @Override
    public ThreeState getState(ResourceLocation id, StageContext context) {
        return disabledContent.parents().contains(id) ? ThreeState.DISABLED : ThreeState.ENABLED;
    }

    public void receiveSync(SyncMessage message) {
        disabledContent = message.content;
        Stages.requireAccess().notifyListeners();
    }

    @Override
    public Stream<ResourceLocation> getDisabledStages(StageContext context) {
        return disabledContent.parents().stream();
    }

    @Override
    public Stage getDisabledContent(StageContext context) {
        return disabledContent;
    }

}
