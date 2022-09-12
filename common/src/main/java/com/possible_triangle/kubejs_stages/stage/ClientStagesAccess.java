package com.possible_triangle.kubejs_stages.stage;

import com.possible_triangle.kubejs_stages.network.SyncMessage;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

public class ClientStagesAccess extends StagesAccess {

    private Collection<String> disabledStages = Collections.emptyList();
    private Stage disabledContent = Stage.EMPTY;

    @Override
    public boolean isDisabled(String id) {
        return disabledStages.contains(id);
    }

    @Override
    public Stream<Map.Entry<String, Stage>> getStages() {
        return Stream.empty();
    }

    public void receiveSync(SyncMessage message) {
        disabledContent = message.content;
        disabledStages = message.stages;
        Stages.getAccess().notifyListeners(message.content);
    }

    @Override
    public Stage getDisabledContent() {
        return disabledContent;
    }

    @Override
    public Stream<String> getDisabledStages() {
        return disabledStages.stream();
    }
}
