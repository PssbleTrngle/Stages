package com.possible_triangle.kubejs_stages.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.possible_triangle.kubejs_stages.stage.Stages;
import net.minecraft.commands.CommandSourceStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class StageArgument implements ArgumentType<String> {
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "bar");

    public static String get(String name, CommandContext<CommandSourceStack> ctx) {
        return ctx.getArgument(name, String.class);
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        StringReader reader = new StringReader(builder.getInput());
        reader.setCursor(builder.getStart());
        try {
            var start = reader.readString();
            Stages.getDefinedStages()
                    .map(Map.Entry::getKey)
                    .filter(it -> it.startsWith(start))
                    .forEach(builder::suggest);
            return builder.buildFuture();
        } catch (CommandSyntaxException var7) {
            return Suggestions.empty();
        }
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
