package com.possible_triangle.kubejs_stages.command;

import java.util.function.Predicate;
import java.util.stream.Stream;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.possible_triangle.kubejs_stages.stage.StageContext;
import com.possible_triangle.kubejs_stages.stage.StageScope;
import com.possible_triangle.kubejs_stages.stage.Stages;
import com.possible_triangle.kubejs_stages.stage.StagesAccess;
import com.possible_triangle.kubejs_stages.stage.ThreeState;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;

public class StageCommand {

    private static final Dynamic2CommandExceptionType ALREADY_STATE = new Dynamic2CommandExceptionType((a, b) -> new TextComponent(String.format("%s is already %s", a, b)));

    private static Predicate<CommandSourceStack> permission() {
        return it -> it.hasPermission(3);
    }

    private static LiteralArgumentBuilder<CommandSourceStack> stateNode(ThreeState state) {
        var playerArg = Commands.argument("player", EntityArgument.players());
        return Commands.literal(state.name().toLowerCase()).then(
                Commands.literal("*")
                        .executes(setStates(state, StageScope.GLOBAL))
                        .then(playerArg.executes(setStates(state, StageScope.PLAYER)))
        ).then(
                Commands.argument("stage", new StageArgument())
                        .executes(setState(state, StageScope.GLOBAL))
                        .then(playerArg.executes(setState(state, StageScope.PLAYER)))
        );
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection selection) {
        dispatcher.register(Commands.literal("stages").requires(permission())
                .then(stateNode(ThreeState.ENABLED))
                .then(stateNode(ThreeState.DISABLED))
                .then(stateNode(ThreeState.UNSET))
                .then(
                        Commands.literal("list").executes(StageCommand::list).then(
                                Commands.literal("enabled").executes(ctx -> list(ctx, StagesAccess::isEnabled))
                        ).then(
                                Commands.literal("disabled").executes(ctx -> list(ctx, StagesAccess::isDisabled))
                        )
                )
        );
    }

    private static Stream<StageContext> createContexts(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        var server = commandContext.getSource().getServer();
        try {
            var players = EntityArgument.getPlayers(commandContext, "player");
            return players.stream().map(it -> new StageContext(server, it));
        } catch (IllegalArgumentException ex) {
            var source = commandContext.getSource();
            if (source.getEntity() instanceof Player player) {
                return Stream.of(new StageContext(server, player));
            } else {
                return Stream.of(new StageContext(server, null));
            }
        }
    }

    private static StageContext createContext(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        return createContexts(commandContext).findFirst().orElseThrow();
    }

    private static int list(CommandContext<CommandSourceStack> ctx, StagePredicate predicate) throws CommandSyntaxException {
        var access = Stages.getServerAccess().orElseThrow();
        var context = createContext(ctx);
        var stages = access.getStages()
                .filter(it -> predicate.test(access, it, context))
                .toList();

        var ids = String.join(", ", stages);
        var size = stages.size();
        ctx.getSource().sendSuccess(new TextComponent(String.format("%s disabled stages: %s", size, ids)), false);
        return size;
    }

    private static int list(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        var access = Stages.getServerAccess().orElseThrow();
        var context = createContext(ctx);
        var stages = access.getStages().toList();
        ctx.getSource().sendSuccess(new TextComponent(String.format("Found %s stages", stages.size())), false);

        stages.forEach(id -> {
            var isDisabled = access.isDisabled(id, context);
            var status = isDisabled ? "disabled" : "enabled";
            var statusColor = isDisabled ? ChatFormatting.RED : ChatFormatting.GREEN;
            ctx.getSource().sendSuccess(new TextComponent(String.format("   %s: ", id)).append(new TextComponent(status).withStyle(statusColor)), false);
        });
        return stages.size();
    }

    private static Command<CommandSourceStack> setStates(ThreeState state, StageScope scope) {
        return ctx -> {
            var ids = Stages.getServerAccess().orElseThrow().getStages().toList();
            var targets = createContexts(ctx).toList();
            var affected = 0;
            for (var target : targets) {
                var changed = scope.setStates(ids, state, target);
                if (changed > 0) affected++;
            }
            ctx.getSource().sendSuccess(new TextComponent("Updated stages for " + affected + " players"), true);
            return affected;
        };
    }

    private static Command<CommandSourceStack> setState(ThreeState state, StageScope scope) {
        return ctx -> {
            var stage = StageArgument.get("stage", ctx);
            var targets = createContexts(ctx).toList();
            var affected = 0;
            for (var target : targets) {
                var success = scope.setState(stage, state, target);
                if (success) affected++;
            }
            if (affected == 0) throw ALREADY_STATE.create(stage, state.name());
            ctx.getSource().sendSuccess(new TextComponent(String.format("Successfully set state of %s to %s for %s players", stage, state.name(), affected)), true);
            return 1;
        };
    }

    @FunctionalInterface
    private interface StagePredicate {
        boolean test(StagesAccess access, String id, StageContext context);
    }
}
