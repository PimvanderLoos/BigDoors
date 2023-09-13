package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.storage.loot.LootCollector;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootItemFunctionSetTable extends LootItemFunctionConditional {

    final MinecraftKey name;
    final long seed;
    final TileEntityTypes<?> type;

    LootItemFunctionSetTable(LootItemCondition[] alootitemcondition, MinecraftKey minecraftkey, long i, TileEntityTypes<?> tileentitytypes) {
        super(alootitemcondition);
        this.name = minecraftkey;
        this.seed = i;
        this.type = tileentitytypes;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.SET_LOOT_TABLE;
    }

    @Override
    public ItemStack run(ItemStack itemstack, LootTableInfo loottableinfo) {
        if (itemstack.isEmpty()) {
            return itemstack;
        } else {
            NBTTagCompound nbttagcompound = ItemBlock.getBlockEntityData(itemstack);

            if (nbttagcompound == null) {
                nbttagcompound = new NBTTagCompound();
            }

            nbttagcompound.putString("LootTable", this.name.toString());
            if (this.seed != 0L) {
                nbttagcompound.putLong("LootTableSeed", this.seed);
            }

            ItemBlock.setBlockEntityData(itemstack, this.type, nbttagcompound);
            return itemstack;
        }
    }

    @Override
    public void validate(LootCollector lootcollector) {
        if (lootcollector.hasVisitedTable(this.name)) {
            lootcollector.reportProblem("Table " + this.name + " is recursively called");
        } else {
            super.validate(lootcollector);
            LootTable loottable = lootcollector.resolveLootTable(this.name);

            if (loottable == null) {
                lootcollector.reportProblem("Unknown loot table called " + this.name);
            } else {
                loottable.validate(lootcollector.enterTable("->{" + this.name + "}", this.name));
            }

        }
    }

    public static LootItemFunctionConditional.a<?> withLootTable(TileEntityTypes<?> tileentitytypes, MinecraftKey minecraftkey) {
        return simpleBuilder((alootitemcondition) -> {
            return new LootItemFunctionSetTable(alootitemcondition, minecraftkey, 0L, tileentitytypes);
        });
    }

    public static LootItemFunctionConditional.a<?> withLootTable(TileEntityTypes<?> tileentitytypes, MinecraftKey minecraftkey, long i) {
        return simpleBuilder((alootitemcondition) -> {
            return new LootItemFunctionSetTable(alootitemcondition, minecraftkey, i, tileentitytypes);
        });
    }

    public static class a extends LootItemFunctionConditional.c<LootItemFunctionSetTable> {

        public a() {}

        public void serialize(JsonObject jsonobject, LootItemFunctionSetTable lootitemfunctionsettable, JsonSerializationContext jsonserializationcontext) {
            super.serialize(jsonobject, (LootItemFunctionConditional) lootitemfunctionsettable, jsonserializationcontext);
            jsonobject.addProperty("name", lootitemfunctionsettable.name.toString());
            jsonobject.addProperty("type", IRegistry.BLOCK_ENTITY_TYPE.getKey(lootitemfunctionsettable.type).toString());
            if (lootitemfunctionsettable.seed != 0L) {
                jsonobject.addProperty("seed", lootitemfunctionsettable.seed);
            }

        }

        @Override
        public LootItemFunctionSetTable deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "name"));
            long i = ChatDeserializer.getAsLong(jsonobject, "seed", 0L);
            MinecraftKey minecraftkey1 = new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "type"));
            TileEntityTypes<?> tileentitytypes = (TileEntityTypes) IRegistry.BLOCK_ENTITY_TYPE.getOptional(minecraftkey1).orElseThrow(() -> {
                return new JsonSyntaxException("Unknown block entity type id '" + minecraftkey1 + "'");
            });

            return new LootItemFunctionSetTable(alootitemcondition, minecraftkey, i, tileentitytypes);
        }
    }
}
