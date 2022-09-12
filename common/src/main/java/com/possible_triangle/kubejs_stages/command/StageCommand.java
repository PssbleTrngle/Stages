package com.possible_triangle.kubejs_stages.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.possible_triangle.kubejs_stages.stage.Stages;
import com.possible_triangle.kubejs_stages.stage.StagesAccess;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;

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
                                Commands.literal("enabled").executes(ctx -> list(ctx, StagesAccess::isEnabled))
                        ).then(
                                Commands.literal("disabled").executes(ctx -> list(ctx, StagesAccess::isDisabled))
                        )
                )
        );
    }

    private static int list(CommandContext<CommandSourceStack> ctx, BiPredicate<StagesAccess, String> predicate) {
        var access = Stages.getServerAccess().orElseThrow();
        var stages = access.getStages()
                .map(Map.Entry::getKey)
                .filter(it -> predicate.test(access, it))
                .toList();

        var ids = String.join(", ", stages);
        var size = stages.size();
        ctx.getSource().sendSuccess(new TextComponent(String.format("%s disabled stages: %s", size, ids)), false);
        return size;
    }

    private static int list(CommandContext<CommandSourceStack> ctx) {
        var access = Stages.getServerAccess().orElseThrow();
        var stages = access.getStages().toList();
        ctx.getSource().sendSuccess(new TextComponent(String.format("Found %s stages", stages.size())), false);
        stages.forEach(stage -> {
            var id = stage.getKey();
            var isDisabled = access.isDisabled(id);
            var status = isDisabled ? "disabled" : "enabled";
            var statusColor = isDisabled ? ChatFormatting.RED : ChatFormatting.GREEN;
            ctx.getSource().sendSuccess(new TextComponent(String.format("   %s: ", id)).append(new TextComponent(status).withStyle(statusColor)), false);
        });
        return stages.size();
    }

    private static int enableAll(CommandContext<CommandSourceStack> ctx) {
        var access = Stages.getServerAccess().orElseThrow();
        var enabled = access.enableAll();
        ctx.getSource().sendSuccess(new TextComponent("Enabled " + enabled + " stages"), true);
        return enabled;
    }

    private static int disableAll(CommandContext<CommandSourceStack> ctx) {
        var access = Stages.getServerAccess().orElseThrow();
        var disabled = access.disableAll();
        ctx.getSource().sendSuccess(new TextComponent("Disabled " + disabled + " stages"), true);
        return disabled;
    }

    private static int enable(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        var access = Stages.getServerAccess().orElseThrow();
        var stage = StageArgument.get("stage", ctx);
        var success = access.enable(stage);
        if (!success) throw ALREADY_ENABLED.create(stage);
        ctx.getSource().sendSuccess(new TextComponent(String.format("Successfully enabled %s", stage)), true);
        return 1;
    }

    private static int disable(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        var access = Stages.getServerAccess().orElseThrow();
        var stage = StageArgument.get("stage", ctx);
        var success = access.disable(stage);
        if (!success) throw ALREADY_DISABLED.create(stage);
        ctx.getSource().sendSuccess(new TextComponent(String.format("Successfully disabled %s", stage)), true);
        return 1;
    }
}
