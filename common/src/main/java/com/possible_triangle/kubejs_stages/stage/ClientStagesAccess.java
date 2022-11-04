package com.possible_triangle.kubejs_stages.stage;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

import com.possible_triangle.kubejs_stages.network.SyncMessage;

public class ClientStagesAccess extends StagesAccess {

    private Collection<String> disabledStages = Collections.emptyList();
    private Stage disabledContent = Stage.EMPTY;

    @Override
    public ThreeState getState(String id, StageContext context) {
        return disabledStages.contains(id) ? ThreeState.DISABLED : ThreeState.ENABLED;
    }

    public void receiveSync(SyncMessage message) {
        disabledContent = message.content;
        disabledStages = message.stages;
        Stages.getAccess().notifyListeners();
    }

    @Override
    public Stream<String> getDisabledStages(StageContext context) {
        return disabledStages.stream();
    }

    @Override
    public Stage getDisabledContent(StageContext context) {
        return disabledContent;
    }

}
