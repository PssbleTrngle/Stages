package com.possible_triangle.kubejs_stages.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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
                        ).then(
                                Commands.argument("stage", new StageArgument()).executes(StageCommand::enable)
                        )
                ).then(
                        Commands.literal("disable").then(
                                Commands.literal("*").executes(StageCommand::disableAll)
                        ).then(
                                Commands.argument("stage", new StageArgument()).executes(StageCommand::disable)
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

    private static int enable(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        var stage = StageArgument.get("stage", ctx);
        var enabled = Stages.enable(ctx.getSource().getServer(), stage);
        return enabled ? 1 : 0;
    }

    private static int disable(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        var stage = StageArgument.get("stage", ctx);
        var disabled = Stages.disable(ctx.getSource().getServer(), stage);
        return disabled ? 1 : 0;
    }
}
