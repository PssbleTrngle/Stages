package com.possible_triangle.kubejs_stages;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.possible_triangle.kubejs_stages.stage.Stages;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;

import java.util.function.Predicate;

public class StageCommand {

    private static Predicate<CommandSourceStack> permission() {
        return it -> it.hasPermission(3);
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection selection) {
        dispatcher.register(Commands.literal("stages").requires(permission()).then(
                        Commands.literal("enable").then(
                                Commands.literal("*").executes(StageCommand::enableAll)
                        )
                ).then(
                        Commands.literal("disable").then(
                                Commands.literal("*").executes(StageCommand::disableAll)
                        )
                )
        );
    }

    private static int enableAll(CommandContext<CommandSourceStack> ctx) {
        var enabled = Stages.enableAll(ctx.getSource().getServer());
        ctx.getSource().sendSuccess(new TextComponent("Enabled " + enabled + " stages"), true);
        return enabled;
    }

    private static int disableAll(CommandContext<CommandSourceStack> ctx) {
        var disabled = Stages.disableAll(ctx.getSource().getServer());
        ctx.getSource().sendSuccess(new TextComponent("Disabled " + disabled + " stages"), true);
        return disabled;
    }

}
