package com.possible_triangle.kubejs_stages.features;

import com.possible_triangle.kubejs_stages.KubeJSStages;
import com.possible_triangle.kubejs_stages.stage.Stages;
import dev.latvian.mods.kubejs.recipe.RecipeEventJS;
import dev.latvian.mods.kubejs.recipe.filter.IDFilter;
import dev.latvian.mods.kubejs.recipe.filter.OutputFilter;

public class StagesRecipes {

    public static void removeRecipes(RecipeEventJS recipes) {
        Stages.onChangeOnce("recipes", disabled -> {
            KubeJSStages.LOGGER.info("Removing recipes of disabled stages for {} items", disabled.items().size());
            disabled.items().forEach(item -> {
                recipes.remove(new OutputFilter(item, false));
            });


            KubeJSStages.LOGGER.info("Removing {} recipes by ID", disabled.recipes().size());
            disabled.recipes().forEach(id -> {
                recipes.remove(new IDFilter(id));
            });

        });
    }

}
