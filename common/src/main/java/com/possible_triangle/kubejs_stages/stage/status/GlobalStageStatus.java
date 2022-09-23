package com.possible_triangle.kubejs_stages.stage.status;

import com.possible_triangle.kubejs_stages.StageConfig;
import com.possible_triangle.kubejs_stages.stage.StageContext;

public class GlobalStageStatus extends StageStatus {

    @Override
    boolean isEnabled(StageContext context, String id) {
        return StageConfig.instance(context.server()).isDisabled(id);
    }
}
