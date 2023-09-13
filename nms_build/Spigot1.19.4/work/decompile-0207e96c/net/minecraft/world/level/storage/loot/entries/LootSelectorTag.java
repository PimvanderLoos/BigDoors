package net.minecraft.world.level.storage.loot.entries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Iterator;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootSelectorTag extends LootSelectorEntry {

    final TagKey<Item> tag;
    final boolean expand;

    LootSelectorTag(TagKey<Item> tagkey, boolean flag, int i, int j, LootItemCondition[] alootitemcondition, LootItemFunction[] alootitemfunction) {
        super(i, j, alootitemcondition, alootitemfunction);
        this.tag = tagkey;
        this.expand = flag;
    }

    @Override
    public LootEntryType getType() {
        return LootEntries.TAG;
    }

    @Override
    public void createItemStack(Consumer<ItemStack> consumer, LootTableInfo loottableinfo) {
        BuiltInRegistries.ITEM.getTagOrEmpty(this.tag).forEach((holder) -> {
            consumer.accept(new ItemStack(holder));
        });
    }

    private boolean expandTag(LootTableInfo loottableinfo, Consumer<LootEntry> consumer) {
        if (!this.canRun(loottableinfo)) {
            return false;
        } else {
            Iterator iterator = BuiltInRegistries.ITEM.getTagOrEmpty(this.tag).iterator();

            while (iterator.hasNext()) {
                final Holder<Item> holder = (Holder) iterator.next();

                consumer.accept(new LootSelectorEntry.c() {
                    @Override
                    public void createItemStack(Consumer<ItemStack> consumer1, LootTableInfo loottableinfo1) {
                        consumer1.accept(new ItemStack(holder));
                    }
                });
            }

            return true;
        }
    }

    @Override
    public boolean expand(LootTableInfo loottableinfo, Consumer<LootEntry> consumer) {
        return this.expand ? this.expandTag(loottableinfo, consumer) : super.expand(loottableinfo, consumer);
    }

    public static LootSelectorEntry.a<?> tagContents(TagKey<Item> tagkey) {
        return simpleBuilder((i, j, alootitemcondition, alootitemfunction) -> {
            return new LootSelectorTag(tagkey, false, i, j, alootitemcondition, alootitemfunction);
        });
    }

    public static LootSelectorEntry.a<?> expandTag(TagKey<Item> tagkey) {
        return simpleBuilder((i, j, alootitemcondition, alootitemfunction) -> {
            return new LootSelectorTag(tagkey, true, i, j, alootitemcondition, alootitemfunction);
        });
    }

    public static class a extends LootSelectorEntry.e<LootSelectorTag> {

        public a() {}

        public void serializeCustom(JsonObject jsonobject, LootSelectorTag lootselectortag, JsonSerializationContext jsonserializationcontext) {
            super.serializeCustom(jsonobject, (LootSelectorEntry) lootselectortag, jsonserializationcontext);
            jsonobject.addProperty("name", lootselectortag.tag.location().toString());
            jsonobject.addProperty("expand", lootselectortag.expand);
        }

        @Override
        protected LootSelectorTag deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, int i, int j, LootItemCondition[] alootitemcondition, LootItemFunction[] alootitemfunction) {
            MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "name"));
            TagKey<Item> tagkey = TagKey.create(Registries.ITEM, minecraftkey);
            boolean flag = ChatDeserializer.getAsBoolean(jsonobject, "expand");

            return new LootSelectorTag(tagkey, flag, i, j, alootitemcondition, alootitemfunction);
        }
    }
}
