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

    private final MinecraftKey a;
    private final long b;

    private LootItemFunctionSetTable(LootItemCondition[] alootitemcondition, MinecraftKey minecraftkey, long i) {
        super(alootitemcondition);
        this.a = minecraftkey;
        this.b = i;
    }

    @Override
    public LootItemFunctionType b() {
        return LootItemFunctions.q;
    }

    @Override
    public ItemStack a(ItemStack itemstack, LootTableInfo loottableinfo) {
        if (itemstack.isEmpty()) {
            return itemstack;
        } else {
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            nbttagcompound.setString("LootTable", this.a.toString());
            if (this.b != 0L) {
                nbttagcompound.setLong("LootTableSeed", this.b);
            }

            itemstack.getOrCreateTag().set("BlockEntityTag", nbttagcompound);
            return itemstack;
        }
    }

    @Override
    public void a(LootCollector lootcollector) {
        if (lootcollector.a(this.a)) {
            lootcollector.a("Table " + this.a + " is recursively called");
        } else {
            super.a(lootcollector);
            LootTable loottable = lootcollector.c(this.a);

            if (loottable == null) {
                lootcollector.a("Unknown loot table called " + this.a);
            } else {
                loottable.a(lootcollector.a("->{" + this.a + "}", this.a));
            }

        }
    }

    public static class a extends LootItemFunctionConditional.c<LootItemFunctionSetTable> {

        public a() {}

        public void a(JsonObject jsonobject, LootItemFunctionSetTable lootitemfunctionsettable, JsonSerializationContext jsonserializationcontext) {
            super.a(jsonobject, (LootItemFunctionConditional) lootitemfunctionsettable, jsonserializationcontext);
            jsonobject.addProperty("name", lootitemfunctionsettable.a.toString());
            if (lootitemfunctionsettable.b != 0L) {
                jsonobject.addProperty("seed", lootitemfunctionsettable.b);
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
