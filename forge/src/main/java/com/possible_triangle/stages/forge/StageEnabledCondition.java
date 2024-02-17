package com.possible_triangle.stages.forge;

import com.google.gson.JsonObject;
import com.possible_triangle.stages.CommonClass;
import com.possible_triangle.stages.StageContext;
import com.possible_triangle.stages.Stages;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class StageEnabledCondition implements ICondition {

    private static final ResourceLocation ID = new ResourceLocation(CommonClass.ID, "stage_enabled");

    private final String stage;

    public StageEnabledCondition(String stage) {
        this.stage = stage;
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public boolean test(IContext context) {
        return Stages.getServerAccess()
                .flatMap(access -> access.getServer().filter(server ->
                        !access.isDisabled(stage, new StageContext(server, null, false)))
                )
                .isPresent();
    }

    @Override
    public String toString() {
        return "stage_enabled(\"" + stage + "\")";
    }

    public static class Serializer implements IConditionSerializer<StageEnabledCondition> {

        @Override
        public void write(JsonObject json, StageEnabledCondition value) {
            json.addProperty("stage", value.stage);
        }

        @Override
        public StageEnabledCondition read(JsonObject json) {
            return new StageEnabledCondition(GsonHelper.getAsString(json, "stage"));
        }

        @Override
        public ResourceLocation getID() {
            return StageEnabledCondition.ID;
        }
    }

}
