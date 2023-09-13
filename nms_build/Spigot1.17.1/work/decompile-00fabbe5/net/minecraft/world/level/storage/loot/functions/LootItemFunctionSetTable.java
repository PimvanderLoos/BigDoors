package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootCollector;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootItemFunctionSetTable extends LootItemFunctionConditional {

    final MinecraftKey name;
    final long seed;

    LootItemFunctionSetTable(LootItemCondition[] alootitemcondition, MinecraftKey minecraftkey, long i) {
        super(alootitemcondition);
        this.name = minecraftkey;
        this.seed = i;
    }

    @Override
    public LootItemFunctionType a() {
        return LootItemFunctions.SET_LOOT_TABLE;
    }

    @Override
    public ItemStack a(ItemStack itemstack, LootTableInfo loottableinfo) {
        if (itemstack.isEmpty()) {
            return itemstack;
        } else {
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            nbttagcompound.setString("LootTable", this.name.toString());
            if (this.seed != 0L) {
                nbttagcompound.setLong("LootTableSeed", this.seed);
            }

            itemstack.getOrCreateTag().set("BlockEntityTag", nbttagcompound);
            return itemstack;
        }
    }

    @Override
    public void a(LootCollector lootcollector) {
        if (lootcollector.a(this.name)) {
            lootcollector.a("Table " + this.name + " is recursively called");
        } else {
            super.a(lootcollector);
            LootTable loottable = lootcollector.c(this.name);

            if (loottable == null) {
                lootcollector.a("Unknown loot table called " + this.name);
            } else {
                loottable.a(lootcollector.a("->{" + this.name + "}", this.name));
            }

        }
    }

    public static LootItemFunctionConditional.a<?> a(MinecraftKey minecraftkey) {
        return a((alootitemcondition) -> {
            return new LootItemFunctionSetTable(alootitemcondition, minecraftkey, 0L);
        });
    }

    public static LootItemFunctionConditional.a<?> a(MinecraftKey minecraftkey, long i) {
        return a((alootitemcondition) -> {
            return new LootItemFunctionSetTable(alootitemcondition, minecraftkey, i);
        });
    }

    public static class a extends LootItemFunctionConditional.c<LootItemFunctionSetTable> {

        public a() {}

        public void a(JsonObject jsonobject, LootItemFunctionSetTable lootitemfunctionsettable, JsonSerializationContext jsonserializationcontext) {
            super.a(jsonobject, (LootItemFunctionConditional) lootitemfunctionsettable, jsonserializationcontext);
            jsonobject.addProperty("name", lootitemfunctionsettable.name.toString());
            if (lootitemfunctionsettable.seed != 0L) {
                jsonobject.addProperty("seed", lootitemfunctionsettable.seed);
            }

        }

        @Override
        public LootItemFunctionSetTable b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "name"));
            long i = ChatDeserializer.a(jsonobject, "seed", 0L);

            return new LootItemFunctionSetTable(alootitemcondition, minecraftkey, i);
        }
    }
}
