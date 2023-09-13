package net.minecraft.world.level.storage.loot.providers.nbt;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.level.storage.loot.LootSerializer;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;

public class StorageNbtProvider implements NbtProvider {

    final MinecraftKey id;

    StorageNbtProvider(MinecraftKey minecraftkey) {
        this.id = minecraftkey;
    }

    @Override
    public LootNbtProviderType getType() {
        return NbtProviders.STORAGE;
    }

    @Nullable
    @Override
    public NBTBase get(LootTableInfo loottableinfo) {
        return loottableinfo.getLevel().getServer().getCommandStorage().get(this.id);
    }

    @Override
    public Set<LootContextParameter<?>> getReferencedContextParams() {
        return ImmutableSet.of();
    }

    public static class a implements LootSerializer<StorageNbtProvider> {

        public a() {}

        public void serialize(JsonObject jsonobject, StorageNbtProvider storagenbtprovider, JsonSerializationContext jsonserializationcontext) {
            jsonobject.addProperty("source", storagenbtprovider.id.toString());
        }

        @Override
        public StorageNbtProvider deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            String s = ChatDeserializer.getAsString(jsonobject, "source");

            return new StorageNbtProvider(new MinecraftKey(s));
        }
    }
}
