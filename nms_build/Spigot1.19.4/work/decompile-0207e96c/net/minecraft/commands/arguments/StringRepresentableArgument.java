package net.minecraft.commands.arguments;

import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.util.INamable;

public class StringRepresentableArgument<T extends Enum<T> & INamable> implements ArgumentType<T> {

    private static final DynamicCommandExceptionType ERROR_INVALID_VALUE = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("argument.enum.invalid", object);
    });
    private final Codec<T> codec;
    private final Supplier<T[]> values;

    protected StringRepresentableArgument(Codec<T> codec, Supplier<T[]> supplier) {
        this.codec = codec;
        this.values = supplier;
    }

    public T parse(StringReader stringreader) throws CommandSyntaxException {
        String s = stringreader.readUnquotedString();

        return (Enum) this.codec.parse(JsonOps.INSTANCE, new JsonPrimitive(s)).result().orElseThrow(() -> {
            return StringRepresentableArgument.ERROR_INVALID_VALUE.create(s);
        });
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandcontext, SuggestionsBuilder suggestionsbuilder) {
        return ICompletionProvider.suggest((Iterable) Arrays.stream((Enum[]) this.values.get()).map((object) -> {
            return ((INamable) object).getSerializedName();
        }).map(this::convertId).collect(Collectors.toList()), suggestionsbuilder);
    }

    public Collection<String> getExamples() {
        return (Collection) Arrays.stream((Enum[]) this.values.get()).map((object) -> {
            return ((INamable) object).getSerializedName();
        }).map(this::convertId).limit(2L).collect(Collectors.toList());
    }

    protected String convertId(String s) {
        return s;
    }
}
