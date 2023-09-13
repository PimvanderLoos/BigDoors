package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTBase;

public class ArgumentNBTBase implements ArgumentType<NBTBase> {

    private static final Collection<String> a = Arrays.asList("0", "0b", "0l", "0.0", "\"foo\"", "{foo=bar}", "[0]");

    private ArgumentNBTBase() {}

    public static ArgumentNBTBase a() {
        return new ArgumentNBTBase();
    }

    public static <S> NBTBase a(CommandContext<S> commandcontext, String s) {
        return (NBTBase) commandcontext.getArgument(s, NBTBase.class);
    }

    public NBTBase parse(StringReader stringreader) throws CommandSyntaxException {
        return (new MojangsonParser(stringreader)).d();
    }

    public Collection<String> getExamples() {
        return ArgumentNBTBase.a;
    }
}
