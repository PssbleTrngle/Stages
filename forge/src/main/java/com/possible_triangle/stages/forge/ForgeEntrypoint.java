package com.possible_triangle.stages.forge;

import com.possible_triangle.stages.CommonClass;
import com.possible_triangle.stages.Stages;
import com.possible_triangle.stages.command.StageArgument;
import com.possible_triangle.stages.command.StageCommand;
import com.possible_triangle.stages.forge.platform.ForgeNetwork;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;

@Mod(CommonClass.ID)
public class ForgeEntrypoint {
    private static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, CommonClass.ID);

    public ForgeEntrypoint() {
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();

        CraftingHelper.register(new StageEnabledCondition.Serializer());

        COMMAND_ARGUMENT_TYPES.register("stage", () ->
                ArgumentTypeInfos.registerByClass(StageArgument.class, SingletonArgumentInfo.contextFree(StageArgument::new)));

        COMMAND_ARGUMENT_TYPES.register(modBus);

        modBus.addListener((FMLCommonSetupEvent event) -> {
            ForgeNetwork.register(MinecraftForge.EVENT_BUS);
        });

        MinecraftForge.EVENT_BUS.addListener((ServerAboutToStartEvent event) -> {
            Stages.initServer(event.getServer());
        });

        MinecraftForge.EVENT_BUS.addListener((ServerStoppedEvent event) -> {
            Stages.clearServer();
        });

        MinecraftForge.EVENT_BUS.addListener((RegisterCommandsEvent event) -> {
            StageCommand.register(event.getDispatcher());
        });

        //MinecraftForge.EVENT_BUS.addListener((AddReloadListenerEvent event) -> {
        //    event.addListener(new StageReloadListener());
        //});

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> clientInit());
    }

    private static void clientInit() {
        MinecraftForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingIn event) -> {
            Stages.initClient();
        });

        MinecraftForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingOut event) -> {
            Stages.clearClient();
        });
    }

    public static FluidStack mapFluidStack(com.possible_triangle.stages.platform.FluidStack from) {
        return new FluidStack(from.fluid(), from.amount(), from.nbt());
    }
}
