package com.xebialabs.deployit.community.verifier;

public enum RequiredInstancesPerEnvironment {
    EXACTLY_ZERO(0, 0),
    ZERO_OR_MORE(0, Integer.MAX_VALUE),
    EXACTLY_ONE(1, 1),
    ONE_OR_MORE(1, Integer.MAX_VALUE),
    TWO_OR_MORE(2, Integer.MAX_VALUE);

    private final int minimum;
    private final int maximum;

    private RequiredInstancesPerEnvironment(int minimum, int maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public boolean isCompliant(int value, RequiredInstancesEnforcement enforcementLevel) {
        switch (enforcementLevel) {
        case NONE:
            return true;
        case LENIENT:
            return value >= Math.min(minimum, 1) && value <= maximum;
        default:
            return value >= minimum && value <= maximum;
        }
    }
}
