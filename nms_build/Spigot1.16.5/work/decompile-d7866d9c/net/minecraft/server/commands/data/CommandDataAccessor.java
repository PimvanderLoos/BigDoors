package net.minecraft.server.commands.data;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.ArgumentNBTKey;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.IChatBaseComponent;

public interface CommandDataAccessor {

    void a(NBTTagCompound nbttagcompound) throws CommandSyntaxException;

    NBTTagCompound a() throws CommandSyntaxException;

    IChatBaseComponent b();

    IChatBaseComponent a(NBTBase nbtbase);

    IChatBaseComponent a(ArgumentNBTKey.h argumentnbtkey_h, double d0, int i);
}
