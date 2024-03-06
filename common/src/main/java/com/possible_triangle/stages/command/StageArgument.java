package com.possible_triangle.stages.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.possible_triangle.stages.ServerStagesAccess;
import com.possible_triangle.stages.Stages;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class StageArgument implements ArgumentType<ResourceLocation> {
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "bar");

    public static ResourceLocation get(String name, CommandContext<CommandSourceStack> ctx) {
        return ctx.getArgument(name, ResourceLocation.class);
    }

    @Override
    public ResourceLocation parse(StringReader reader) throws CommandSyntaxException {
        return ResourceLocation.read(reader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        StringReader reader = new StringReader(builder.getInput());
        reader.setCursor(builder.getStart());
        try {
            var start = reader.readString();
            Stages.getServerAccess()
                    .map(ServerStagesAccess::getStages)
                    .ifPresent(stages -> stages
                            .map(ResourceLocation::toString)
                            .filter(it -> it.startsWith(start))
                            .forEach(builder::suggest)
                    );
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
