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
    public LootEntryType getType() {
        return LootEntries.REFERENCE;
    }

    @Override
    public void createItemStack(Consumer<ItemStack> consumer, LootTableInfo loottableinfo) {
        LootTable loottable = loottableinfo.getLootTable(this.name);

        loottable.getRandomItemsRaw(loottableinfo, consumer);
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

    public static LootSelectorEntry.a<?> lootTableReference(MinecraftKey minecraftkey) {
        return simpleBuilder((i, j, alootitemcondition, alootitemfunction) -> {
            return new LootSelectorLootTable(minecraftkey, i, j, alootitemcondition, alootitemfunction);
        });
    }

    public static class a extends LootSelectorEntry.e<LootSelectorLootTable> {

        public a() {}

        public void serializeCustom(JsonObject jsonobject, LootSelectorLootTable lootselectorloottable, JsonSerializationContext jsonserializationcontext) {
            super.serializeCustom(jsonobject, (LootSelectorEntry) lootselectorloottable, jsonserializationcontext);
            jsonobject.addProperty("name", lootselectorloottable.name.toString());
        }

        @Override
        protected LootSelectorLootTable deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, int i, int j, LootItemCondition[] alootitemcondition, LootItemFunction[] alootitemfunction) {
            MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "name"));

            return new LootSelectorLootTable(minecraftkey, i, j, alootitemcondition, alootitemfunction);
        }
    }
}
