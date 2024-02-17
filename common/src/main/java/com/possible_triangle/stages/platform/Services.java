package com.possible_triangle.stages.platform;


import com.possible_triangle.stages.CommonClass;
import com.possible_triangle.stages.platform.services.INetwork;
import com.possible_triangle.stages.platform.services.IPlatformHelper;

import java.util.ServiceLoader;

public class Services {
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);
    public static final INetwork NETWORK = load(INetwork.class);

    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        CommonClass.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}