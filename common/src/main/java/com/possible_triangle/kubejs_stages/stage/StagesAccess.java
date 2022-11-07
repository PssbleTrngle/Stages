package com.possible_triangle.kubejs_stages.stage;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.possible_triangle.kubejs_stages.KubeJSStages;

public abstract class StagesAccess {

    @FunctionalInterface
    public interface UpdateEvent {
        @Nullable
        Runnable onUpdate(StagesAccess access);
    }

    public abstract ThreeState getState(String id, StageContext context);

    public final boolean isEnabled(String id, StageContext context) {
        return getState(id, context) == ThreeState.ENABLED;
    }

    public final boolean isDisabled(String id, StageContext context) {
        return getState(id, context) == ThreeState.DISABLED;
    }

    private final HashMap<String, UpdateEvent> listeners = Maps.newHashMap();
    private List<Runnable> cleanup = Collections.emptyList();


    public abstract Stream<String> getDisabledStages(StageContext context);

    public abstract Stage getDisabledContent(StageContext context);

    public final void unsubscribe(String id) {
        listeners.remove(id);
    }

    public final Runnable onChange(String id, Consumer<StagesAccess> listener) {
        return onChange(id, it -> {
            listener.accept(it);
            return null;
        });
    }

    public final Runnable onChange(String id, UpdateEvent listener) {
        listeners.put(id, listener);
        return () -> unsubscribe(id);
    }

    public final Runnable onChangeOnce(String id, UpdateEvent listener) {
        return onChange(id, it -> {
            unsubscribe(id);
            listener.onUpdate(it);
        });
    }

    protected final void notifyListeners() {
        var frozen = new ImmutableSet.Builder<UpdateEvent>().addAll(listeners.values()).build();
        KubeJSStages.LOGGER.debug("Cleaning up {} handlers", cleanup.size());
        KubeJSStages.LOGGER.debug("Notifying {} handlers", frozen.size());
        cleanup.forEach(Runnable::run);
        cleanup = frozen.stream().map(listener -> {
            try {
                return listener.onUpdate(this);
            } catch (Exception e) {
                KubeJSStages.LOGGER.error("Update Handler encountered exception: {}", e.getMessage());
                return null;
            }
        }).filter(Objects::nonNull).toList();
    }

}
