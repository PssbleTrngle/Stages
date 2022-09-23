package com.possible_triangle.kubejs_stages.stage.status;

import com.possible_triangle.kubejs_stages.stage.StageContext;

public abstract class StageStatus  {

    abstract boolean isEnabled(StageContext context, String id);

    final boolean isDisabled(StageContext context, String id) {
        return !isEnabled(context, id);
    }

}
