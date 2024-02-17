package com.possible_triangle.stages;

public enum ThreeState {
    ENABLED, DISABLED, UNSET;

    public ThreeState inverse() {
        return switch (this) {
            case ENABLED -> DISABLED;
            case DISABLED -> ENABLED;
            case UNSET -> UNSET;
        };
    }

    public static ThreeState of(boolean bool) {
        return bool ? ENABLED : DISABLED;
    }
}
