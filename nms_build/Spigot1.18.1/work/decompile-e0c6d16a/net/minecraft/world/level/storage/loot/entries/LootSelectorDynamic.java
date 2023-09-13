package net.minecraft.world.level.storage.loot.entries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootSelectorDynamic extends LootSelectorEntry {

    final MinecraftKey name;

    LootSelectorDynamic(MinecraftKey minecraftkey, int i, int j, LootItemCondition[] alootitemcondition, LootItemFunction[] alootitemfunction) {
        super(i, j, alootitemcondition, alootitemfunction);
        this.name = minecraftkey;
    }

    @Override
    public LootEntryType getType() {
        return LootEntries.DYNAMIC;
    }

    @Override
    public void createItemStack(Consumer<ItemStack> consumer, LootTableInfo loottableinfo) {
        loottableinfo.addDynamicDrops(this.name, consumer);
    }

    public static LootSelectorEntry.a<?> dynamicEntry(MinecraftKey minecraftkey) {
        return simpleBuilder((i, j, alootitemcondition, alootitemfunction) -> {
            return new LootSelectorDynamic(minecraftkey, i, j, alootitemcondition, alootitemfunction);
        });
    }

    public static class a extends LootSelectorEntry.e<LootSelectorDynamic> {

        public a() {}

        public void serializeCustom(JsonObject jsonobject, LootSelectorDynamic lootselectordynamic, JsonSerializationContext jsonserializationcontext) {
            super.serializeCustom(jsonobject, (LootSelectorEntry) lootselectordynamic, jsonserializationcontext);
            jsonobject.addProperty("name", lootselectordynamic.name.toString());
        }

        @Override
        protected LootSelectorDynamic deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, int i, int j, LootItemCondition[] alootitemcondition, LootItemFunction[] alootitemfunction) {
            MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "name"));

            return new LootSelectorDynamic(minecraftkey, i, j, alootitemcondition, alootitemfunction);
        }
    }
}
