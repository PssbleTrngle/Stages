package com.possible_triangle.kubejs_stages.forge;

import com.possible_triangle.kubejs_stages.KubeJSStages;
import com.possible_triangle.kubejs_stages.StageReloadListener;
import com.possible_triangle.kubejs_stages.command.StageArgument;
import com.possible_triangle.kubejs_stages.command.StageCommand;
import com.possible_triangle.kubejs_stages.forge.platform.ForgeNetwork;
import com.possible_triangle.kubejs_stages.stage.Stages;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Registry;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;

@Mod(KubeJSStages.ID)
public class KubeJSStagesForge {
    private static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(Registry.COMMAND_ARGUMENT_TYPE_REGISTRY, KubeJSStages.ID);

    public KubeJSStagesForge() {
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        var forgeBus = MinecraftForge.EVENT_BUS;

        COMMAND_ARGUMENT_TYPES.register("stage", () ->
                ArgumentTypeInfos.registerByClass(StageArgument.class, SingletonArgumentInfo.contextFree(StageArgument::new)));

        COMMAND_ARGUMENT_TYPES.register(modBus);

        modBus.addListener((FMLCommonSetupEvent event) -> {
            ForgeNetwork.register(forgeBus);
        });

        forgeBus.addListener((ServerAboutToStartEvent event) -> {
            Stages.initServer(event.getServer());
        });

        forgeBus.addListener((ServerStoppedEvent event) -> {
            Stages.clearServer();
        });

        forgeBus.addListener((ClientPlayerNetworkEvent.LoggingIn event) -> {
            Stages.initClient();
        });

        forgeBus.addListener((ClientPlayerNetworkEvent.LoggingOut event) -> {
            Stages.clearClient();
        });

        forgeBus.addListener((RegisterCommandsEvent event) -> {
            StageCommand.register(event.getDispatcher());
        });

        forgeBus.addListener((AddReloadListenerEvent event) -> {
            event.addListener(new StageReloadListener());
        });
    }

    public static FluidStack mapFluidStack(com.possible_triangle.kubejs_stages.platform.FluidStack from) {
        return new FluidStack(from.fluid(), from.amount(), from.nbt());
    }
}
