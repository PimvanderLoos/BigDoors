package net.minecraft.world.level.storage.loot.providers.nbt;

import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;

public interface NbtProvider {

    @Nullable
    NBTBase a(LootTableInfo loottableinfo);

    Set<LootContextParameter<?>> b();

    LootNbtProviderType a();
}
