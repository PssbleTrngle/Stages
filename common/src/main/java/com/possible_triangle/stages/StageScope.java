package com.possible_triangle.stages;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.possible_triangle.stages.platform.Services;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.Collections;

public abstract class StageScope {

    private static final SimpleCommandExceptionType NO_PLAYER = new SimpleCommandExceptionType(Component.literal("No player provided"));

    private static final String STAGE_DATA_KEY = CommonClass.ID + "_stages";

    private static CompoundTag perPlayerData(Player player) {
        var data = Services.PLATFORM.getPersistentData(player);
        if (data.contains(STAGE_DATA_KEY, 10)) return data.getCompound(STAGE_DATA_KEY);
        var compound = new CompoundTag();
        data.put(STAGE_DATA_KEY, compound);
        return compound;
    }

    public static void restorePlayerData(ServerPlayer oldPlayer, ServerPlayer player) {
        var data = Services.PLATFORM.getPersistentData(player);
        data.put(STAGE_DATA_KEY, perPlayerData(oldPlayer));
    }

    public static final StageScope GLOBAL = new StageScope() {
        @Override
        public ThreeState getState(String id, StageContext context) {
            return StageConfig.instance(context.server()).getState(id);
        }

        @Override
        public int setStates(ServerStagesAccess access, Collection<String> ids, ThreeState state, StageContext context) {
            var affected = ids.stream().filter(id -> StageConfig.instance(context.server()).setState(id, state));
            return Math.toIntExact(affected.count());
        }
    };

    public static final StageScope PLAYER = new StageScope() {
        @Override
        public ThreeState getState(String id, StageContext context) throws CommandSyntaxException {
            if (context.player() == null) throw NO_PLAYER.create();
            var data = StageScope.perPlayerData(context.player());
            if (!data.contains(id, 8)) return ThreeState.UNSET;
            return ThreeState.valueOf(data.getString(id));
        }

        @Override
        public int setStates(ServerStagesAccess access, Collection<String> ids, ThreeState state, StageContext context) throws CommandSyntaxException {
            if (context.player() == null) throw NO_PLAYER.create();
            return Math.toIntExact(ids.stream()
                    .filter(id -> {
                        var data = StageScope.perPlayerData(context.player());
                        var oldName = data.getString(id);
                        data.putString(id, state.name());
                        return !oldName.equals(data.getString(id));
                    })
                    .count());
        }
    };

    public abstract ThreeState getState(String id, StageContext context) throws CommandSyntaxException;

    public final ThreeState getOptionalState(String id, StageContext context) {
        try {
            return getState(id, context);
        } catch (CommandSyntaxException e) {
            return ThreeState.UNSET;
        }
    }

    public abstract int setStates(ServerStagesAccess access, Collection<String> ids, ThreeState state, StageContext context) throws CommandSyntaxException;

    public final int setStates(Collection<String> ids, ThreeState state, StageContext context) throws CommandSyntaxException {
        return Stages.modify(access -> {
            for (String id : ids) access.assertExists(id);
            return setStates(access, ids, state, context);
        }).orElse(0);
    }

    public final boolean setState(String id, ThreeState state, StageContext context) throws CommandSyntaxException {
        return setStates(Collections.singleton(id), state, context) > 0;
    }

    /*
    public int disableAll(StageContext context) throws CommandSyntaxException {
        return Stages.modify(access -> {
            var definedStages = access.getStages();
            var enabled = definedStages.map(Map.Entry::getKey).filter(id -> getState(id, context) != ThreeState.DISABLED).toList();
            enabled.forEach(it -> StageConfig.instance(context.server()).setState(it, ThreeState.DISABLED));
            return enabled.size();
        }).orElse(0);
    }

    public int enableAll(StageContext context) throws CommandSyntaxException {
        return Stages.modify(access -> {
            var definedStages = access.getStages();
            var disabled = definedStages.map(Map.Entry::getKey).filter(id -> getState(id, context) != ThreeState.ENABLED).toList();
            disabled.forEach(it -> StageConfig.instance(context.server()).setState(it, ThreeState.ENABLED));
            return disabled.size();
        }).orElse(0);
    }

    public boolean enable(String id, StageContext context) throws CommandSyntaxException {
        return Stages.modify(access -> {
            access.assertExists(id);
            return StageConfig.instance(context.server()).enable(id);
        }).orElse(false);
    }

    public boolean disable(String id, StageContext context) throws CommandSyntaxException {
        return Stages.modify(access -> {
            access.assertExists(id);
            return StageConfig.instance(context.server()).disable(id);
        }).orElse(false);
    }
     */

}
