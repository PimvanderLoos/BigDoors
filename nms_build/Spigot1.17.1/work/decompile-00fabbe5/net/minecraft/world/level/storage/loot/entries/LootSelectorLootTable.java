package net.minecraft.world.level.storage.loot.entries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootCollector;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootSelectorLootTable extends LootSelectorEntry {

    final MinecraftKey name;

    LootSelectorLootTable(MinecraftKey minecraftkey, int i, int j, LootItemCondition[] alootitemcondition, LootItemFunction[] alootitemfunction) {
        super(i, j, alootitemcondition, alootitemfunction);
        this.name = minecraftkey;
    }

    @Override
    public LootEntryType a() {
        return LootEntries.REFERENCE;
    }

    @Override
    public void a(Consumer<ItemStack> consumer, LootTableInfo loottableinfo) {
        LootTable loottable = loottableinfo.a(this.name);

        loottable.populateLootDirect(loottableinfo, consumer);
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

    public static LootSelectorEntry.a<?> a(MinecraftKey minecraftkey) {
        return a((i, j, alootitemcondition, alootitemfunction) -> {
            return new LootSelectorLootTable(minecraftkey, i, j, alootitemcondition, alootitemfunction);
        });
    }

    public static class a extends LootSelectorEntry.e<LootSelectorLootTable> {

        public a() {}

        public void a(JsonObject jsonobject, LootSelectorLootTable lootselectorloottable, JsonSerializationContext jsonserializationcontext) {
            super.a(jsonobject, (LootSelectorEntry) lootselectorloottable, jsonserializationcontext);
            jsonobject.addProperty("name", lootselectorloottable.name.toString());
        }

        @Override
        protected LootSelectorLootTable b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, int i, int j, LootItemCondition[] alootitemcondition, LootItemFunction[] alootitemfunction) {
            MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "name"));

            return new LootSelectorLootTable(minecraftkey, i, j, alootitemcondition, alootitemfunction);
        }
    }
}
