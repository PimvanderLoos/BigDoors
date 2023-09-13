package net.minecraft.commands.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent;

public class ArgumentTime implements ArgumentType<Integer> {

    private static final Collection<String> EXAMPLES = Arrays.asList("0d", "0s", "0t", "0");
    private static final SimpleCommandExceptionType ERROR_INVALID_UNIT = new SimpleCommandExceptionType(IChatBaseComponent.translatable("argument.time.invalid_unit"));
    private static final Dynamic2CommandExceptionType ERROR_TICK_COUNT_TOO_LOW = new Dynamic2CommandExceptionType((object, object1) -> {
        return IChatBaseComponent.translatable("argument.time.tick_count_too_low", object1, object);
    });
    private static final Object2IntMap<String> UNITS = new Object2IntOpenHashMap();
    final int minimum;

    private ArgumentTime(int i) {
        this.minimum = i;
    }

    public static ArgumentTime time() {
        return new ArgumentTime(0);
    }

    public static ArgumentTime time(int i) {
        return new ArgumentTime(i);
    }

    public Integer parse(StringReader stringreader) throws CommandSyntaxException {
        float f = stringreader.readFloat();
        String s = stringreader.readUnquotedString();
        int i = ArgumentTime.UNITS.getOrDefault(s, 0);

        if (i == 0) {
            throw ArgumentTime.ERROR_INVALID_UNIT.create();
        } else {
            int j = Math.round(f * (float) i);

            if (j < this.minimum) {
                throw ArgumentTime.ERROR_TICK_COUNT_TOO_LOW.create(j, this.minimum);
            } else {
                return j;
            }
        }
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandcontext, SuggestionsBuilder suggestionsbuilder) {
        StringReader stringreader = new StringReader(suggestionsbuilder.getRemaining());

        try {
            stringreader.readFloat();
        } catch (CommandSyntaxException commandsyntaxexception) {
            return suggestionsbuilder.buildFuture();
        }

        return ICompletionProvider.suggest((Iterable) ArgumentTime.UNITS.keySet(), suggestionsbuilder.createOffset(suggestionsbuilder.getStart() + stringreader.getCursor()));
    }

    public Collection<String> getExamples() {
        return ArgumentTime.EXAMPLES;
    }

    static {
        ArgumentTime.UNITS.put("d", 24000);
        ArgumentTime.UNITS.put("s", 20);
        ArgumentTime.UNITS.put("t", 1);
        ArgumentTime.UNITS.put("", 1);
    }

    public static class a implements ArgumentTypeInfo<ArgumentTime, ArgumentTime.a.a> {

        public a() {}

        public void serializeToNetwork(ArgumentTime.a.a argumenttime_a_a, PacketDataSerializer packetdataserializer) {
            packetdataserializer.writeInt(argumenttime_a_a.min);
        }

        @Override
        public ArgumentTime.a.a deserializeFromNetwork(PacketDataSerializer packetdataserializer) {
            int i = packetdataserializer.readInt();

            return new ArgumentTime.a.a(i);
        }

        public void serializeToJson(ArgumentTime.a.a argumenttime_a_a, JsonObject jsonobject) {
            jsonobject.addProperty("min", argumenttime_a_a.min);
        }

        public ArgumentTime.a.a unpack(ArgumentTime argumenttime) {
            return new ArgumentTime.a.a(argumenttime.minimum);
        }

        public final class a implements ArgumentTypeInfo.a<ArgumentTime> {

            final int min;

            a(int i) {
                this.min = i;
            }

            @Override
            public ArgumentTime instantiate(CommandBuildContext commandbuildcontext) {
                return ArgumentTime.time(this.min);
            }

            @Override
            public ArgumentTypeInfo<ArgumentTime, ?> type() {
                return a.this;
            }
        }
    }
}
