package net.minecraft.world.level.storage.loot.entries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootItem extends LootSelectorEntry {

    private final Item g;

    private LootItem(Item item, int i, int j, LootItemCondition[] alootitemcondition, LootItemFunction[] alootitemfunction) {
        super(i, j, alootitemcondition, alootitemfunction);
        this.g = item;
    }

    @Override
    public LootEntryType a() {
        return LootEntries.b;
    }

    @Override
    public void a(Consumer<ItemStack> consumer, LootTableInfo loottableinfo) {
        consumer.accept(new ItemStack(this.g));
    }

    public static LootSelectorEntry.a<?> a(IMaterial imaterial) {
        return a((i, j, alootitemcondition, alootitemfunction) -> {
            return new LootItem(imaterial.getItem(), i, j, alootitemcondition, alootitemfunction);
        });
    }

    public static class a extends LootSelectorEntry.e<LootItem> {

        public a() {}

        public void a(JsonObject jsonobject, LootItem lootitem, JsonSerializationContext jsonserializationcontext) {
            super.a(jsonobject, (LootSelectorEntry) lootitem, jsonserializationcontext);
            MinecraftKey minecraftkey = IRegistry.ITEM.getKey(lootitem.g);

            if (minecraftkey == null) {
                throw new IllegalArgumentException("Can't serialize unknown item " + lootitem.g);
            } else {
                jsonobject.addProperty("name", minecraftkey.toString());
            }
        }

        @Override
        protected LootItem b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, int i, int j, LootItemCondition[] alootitemcondition, LootItemFunction[] alootitemfunction) {
            Item item = ChatDeserializer.i(jsonobject, "name");

            return new LootItem(item, i, j, alootitemcondition, alootitemfunction);
        }
    }
}
