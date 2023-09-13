package net.minecraft.world.ticks;

import java.util.function.Function;
import net.minecraft.nbt.NBTBase;

public interface SerializableTickContainer<T> {

    NBTBase save(long i, Function<T, String> function);
}
