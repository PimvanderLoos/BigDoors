package net.minecraft.world.level.storage.loot.entries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootItem extends LootSelectorEntry {

    final Item item;

    LootItem(Item item, int i, int j, LootItemCondition[] alootitemcondition, LootItemFunction[] alootitemfunction) {
        super(i, j, alootitemcondition, alootitemfunction);
        this.item = item;
    }

    @Override
    public LootEntryType getType() {
        return LootEntries.ITEM;
    }

    @Override
    public void createItemStack(Consumer<ItemStack> consumer, LootTableInfo loottableinfo) {
        consumer.accept(new ItemStack(this.item));
    }

    public static LootSelectorEntry.a<?> lootTableItem(IMaterial imaterial) {
        return simpleBuilder((i, j, alootitemcondition, alootitemfunction) -> {
            return new LootItem(imaterial.asItem(), i, j, alootitemcondition, alootitemfunction);
        });
    }

    public static class a extends LootSelectorEntry.e<LootItem> {

        public a() {}

        public void serializeCustom(JsonObject jsonobject, LootItem lootitem, JsonSerializationContext jsonserializationcontext) {
            super.serializeCustom(jsonobject, (LootSelectorEntry) lootitem, jsonserializationcontext);
            MinecraftKey minecraftkey = BuiltInRegistries.ITEM.getKey(lootitem.item);

            if (minecraftkey == null) {
                throw new IllegalArgumentException("Can't serialize unknown item " + lootitem.item);
            } else {
                jsonobject.addProperty("name", minecraftkey.toString());
            }
        }

        @Override
        protected LootItem deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, int i, int j, LootItemCondition[] alootitemcondition, LootItemFunction[] alootitemfunction) {
            Item item = ChatDeserializer.getAsItem(jsonobject, "name");

            return new LootItem(item, i, j, alootitemcondition, alootitemfunction);
        }
    }
}
