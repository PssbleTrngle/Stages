package com.possible_triangle.kubejs_stages.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.possible_triangle.kubejs_stages.stage.Stages;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;

import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class StageCommand {

    private static final DynamicCommandExceptionType ALREADY_ENABLED = new DynamicCommandExceptionType(it -> new TextComponent(String.format("%s is already enabled", it)));
    private static final DynamicCommandExceptionType ALREADY_DISABLED = new DynamicCommandExceptionType(it -> new TextComponent(String.format("%s is already disabled", it)));

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
                ).then(
                        Commands.literal("list").executes(StageCommand::list).then(
                                Commands.literal("enabled").executes(ctx -> list(ctx, Stages::isEnabled))
                        ).then(
                                Commands.literal("disabled").executes(ctx -> list(ctx, Stages::isDisabled))
                        )
                )
        );
    }

    private static int list(CommandContext<CommandSourceStack> ctx, BiPredicate<MinecraftServer, String> predicate) {
        var stages = Stages.getDefinedStages()
                .map(Map.Entry::getKey)
                .filter(it -> predicate.test(ctx.getSource().getServer(), it))
                .toList();

        var ids = String.join(", ", stages);
        var size = stages.size();
        ctx.getSource().sendSuccess(new TextComponent(String.format("%s disabled stages: %s", size, ids)), false);
        return size;
    }

    private static int list(CommandContext<CommandSourceStack> ctx) {
        var stages = Stages.getDefinedStages().toList();
        ctx.getSource().sendSuccess(new TextComponent(String.format("Found %s stages", stages.size())), false);
        stages.forEach(stage -> {
            var id = stage.getKey();
            var isDisabled = Stages.isDisabled(ctx.getSource().getServer(), id);
            var status = isDisabled ? "disabled" : "enabled";
            var statusColor = isDisabled ? ChatFormatting.RED : ChatFormatting.GREEN;
            ctx.getSource().sendSuccess(new TextComponent(String.format("   %s: ", id)).append(new TextComponent(status).withStyle(statusColor)), false);
        });
        return stages.size();
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
        var success = Stages.enable(ctx.getSource().getServer(), stage);
        if (!success) throw ALREADY_ENABLED.create(stage);
        ctx.getSource().sendSuccess(new TextComponent(String.format("Successfully enabled %s", stage)), true);
        return 1;
    }

    private static int disable(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        var stage = StageArgument.get("stage", ctx);
        var success = Stages.disable(ctx.getSource().getServer(), stage);
        if (!success) throw ALREADY_DISABLED.create(stage);
        ctx.getSource().sendSuccess(new TextComponent(String.format("Successfully disabled %s", stage)), true);
        return 1;
    }
}
