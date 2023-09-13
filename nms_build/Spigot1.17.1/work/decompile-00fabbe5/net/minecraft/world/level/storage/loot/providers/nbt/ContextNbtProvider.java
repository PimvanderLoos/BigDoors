package net.minecraft.world.level.storage.loot.providers.nbt;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.CriterionConditionNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.storage.loot.JsonRegistry;
import net.minecraft.world.level.storage.loot.LootSerializer;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;

public class ContextNbtProvider implements NbtProvider {

    private static final String BLOCK_ENTITY_ID = "block_entity";
    private static final ContextNbtProvider.a BLOCK_ENTITY_PROVIDER = new ContextNbtProvider.a() {
        @Override
        public NBTBase a(LootTableInfo loottableinfo) {
            TileEntity tileentity = (TileEntity) loottableinfo.getContextParameter(LootContextParameters.BLOCK_ENTITY);

            return tileentity != null ? tileentity.save(new NBTTagCompound()) : null;
        }

        @Override
        public String a() {
            return "block_entity";
        }

        @Override
        public Set<LootContextParameter<?>> b() {
            return ImmutableSet.of(LootContextParameters.BLOCK_ENTITY);
        }
    };
    public static final ContextNbtProvider BLOCK_ENTITY = new ContextNbtProvider(ContextNbtProvider.BLOCK_ENTITY_PROVIDER);
    final ContextNbtProvider.a getter;

    private static ContextNbtProvider.a b(final LootTableInfo.EntityTarget loottableinfo_entitytarget) {
        return new ContextNbtProvider.a() {
            @Nullable
            @Override
            public NBTBase a(LootTableInfo loottableinfo) {
                Entity entity = (Entity) loottableinfo.getContextParameter(loottableinfo_entitytarget.a());

                return entity != null ? CriterionConditionNBT.b(entity) : null;
            }

            @Override
            public String a() {
                return loottableinfo_entitytarget.name();
            }

            @Override
            public Set<LootContextParameter<?>> b() {
                return ImmutableSet.of(loottableinfo_entitytarget.a());
            }
        };
    }

    private ContextNbtProvider(ContextNbtProvider.a contextnbtprovider_a) {
        this.getter = contextnbtprovider_a;
    }

    @Override
    public LootNbtProviderType a() {
        return NbtProviders.CONTEXT;
    }

    @Nullable
    @Override
    public NBTBase a(LootTableInfo loottableinfo) {
        return this.getter.a(loottableinfo);
    }

    @Override
    public Set<LootContextParameter<?>> b() {
        return this.getter.b();
    }

    public static NbtProvider a(LootTableInfo.EntityTarget loottableinfo_entitytarget) {
        return new ContextNbtProvider(b(loottableinfo_entitytarget));
    }

    static ContextNbtProvider a(String s) {
        if (s.equals("block_entity")) {
            return new ContextNbtProvider(ContextNbtProvider.BLOCK_ENTITY_PROVIDER);
        } else {
            LootTableInfo.EntityTarget loottableinfo_entitytarget = LootTableInfo.EntityTarget.a(s);

            return new ContextNbtProvider(b(loottableinfo_entitytarget));
        }
    }

    private interface a {

        @Nullable
        NBTBase a(LootTableInfo loottableinfo);

        String a();

        Set<LootContextParameter<?>> b();
    }

    public static class b implements JsonRegistry.b<ContextNbtProvider> {

        public b() {}

        public JsonElement a(ContextNbtProvider contextnbtprovider, JsonSerializationContext jsonserializationcontext) {
            return new JsonPrimitive(contextnbtprovider.getter.a());
        }

        @Override
        public ContextNbtProvider a(JsonElement jsonelement, JsonDeserializationContext jsondeserializationcontext) {
            String s = jsonelement.getAsString();

            return ContextNbtProvider.a(s);
        }
    }

    public static class c implements LootSerializer<ContextNbtProvider> {

        public c() {}

        public void a(JsonObject jsonobject, ContextNbtProvider contextnbtprovider, JsonSerializationContext jsonserializationcontext) {
            jsonobject.addProperty("target", contextnbtprovider.getter.a());
        }

        @Override
        public ContextNbtProvider a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            String s = ChatDeserializer.h(jsonobject, "target");

            return ContextNbtProvider.a(s);
        }
    }
}
