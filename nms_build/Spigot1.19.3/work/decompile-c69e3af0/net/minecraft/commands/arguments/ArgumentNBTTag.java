package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTTagCompound;

public class ArgumentNBTTag implements ArgumentType<NBTTagCompound> {

    private static final Collection<String> EXAMPLES = Arrays.asList("{}", "{foo=bar}");

    private ArgumentNBTTag() {}

    public static ArgumentNBTTag compoundTag() {
        return new ArgumentNBTTag();
    }

    public static <S> NBTTagCompound getCompoundTag(CommandContext<S> commandcontext, String s) {
        return (NBTTagCompound) commandcontext.getArgument(s, NBTTagCompound.class);
    }

    public NBTTagCompound parse(StringReader stringreader) throws CommandSyntaxException {
        return (new MojangsonParser(stringreader)).readStruct();
    }

    public Collection<String> getExamples() {
        return ArgumentNBTTag.EXAMPLES;
    }
}
