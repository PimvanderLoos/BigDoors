package net.minecraft.network.chat.contents;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.stream.Stream;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.nbt.NBTTagCompound;

@FunctionalInterface
public interface DataSource {

    Stream<NBTTagCompound> getData(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException;
}
