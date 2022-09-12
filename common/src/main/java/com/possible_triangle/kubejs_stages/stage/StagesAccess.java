package com.possible_triangle.kubejs_stages.stage;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.possible_triangle.kubejs_stages.KubeJSStages;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

public abstract class StagesAccess {

    public abstract boolean isDisabled(String id);

    public final boolean isEnabled(String id) {
        return !isDisabled(id);
    }

    public abstract Stream<Map.Entry<String, Stage>> getStages();

    private final HashMap<String, Consumer<Stage>> listeners = Maps.newHashMap();


    public abstract Stage getDisabledContent();

    public Stream<String> getDisabledStages() {
        return getStages().map(Map.Entry::getKey).filter(this::isDisabled);
    }

    public final void unsubscribe(String id) {
        listeners.remove(id);
    }

    public final Runnable onChange(String id, Consumer<Stage> listener) {
        listeners.put(id, listener);
        return () -> unsubscribe(id);
    }

    public final Runnable onChangeOnce(String id, Consumer<Stage> listener) {
        return onChange(id, stage -> {
            unsubscribe(id);
            listener.accept(stage);
        });
    }

    protected final void notifyListeners(Stage disabled) {
        var frozen = new ImmutableSet.Builder<Consumer<Stage>>().addAll(listeners.values()).build();
        frozen.forEach(listener -> {
            try {
                listener.accept(disabled);
            } catch (Throwable e) {
                KubeJSStages.LOGGER.error("Client Handler encountered exception: {}", e.getMessage());
            }
        });
    }

    protected void onUpdate() {
    }

}
