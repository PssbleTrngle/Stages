package com.possible_triangle.kubejs_stages.features;

import com.possible_triangle.kubejs_stages.KubeJSStages;
import com.possible_triangle.kubejs_stages.stage.StageContext;
import com.possible_triangle.kubejs_stages.stage.StagesAccess;

import dev.latvian.mods.kubejs.recipe.RecipeEventJS;

public class StagesRecipes {

    public static void removeRecipes(StagesAccess access, RecipeEventJS recipes) {
        access.onChangeOnce("recipes", disabled -> {
            var context = new StageContext(null, null, true);
            var stage = disabled.getDisabledContent(context);

            KubeJSStages.LOGGER.info("Removing recipes of disabled stages for {} items", stage.items().size());
            KubeJSStages.LOGGER.info("Removing {} recipes by ID", stage.recipes().size());

            stage.recipeFilters().forEach(recipes::remove);
        });
    }

}
