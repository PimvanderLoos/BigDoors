package net.minecraft.world.level.storage.loot.entries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.Iterator;
import java.util.function.Consumer;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsInstance;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootSelectorTag extends LootSelectorEntry {

    final Tag<Item> tag;
    final boolean expand;

    LootSelectorTag(Tag<Item> tag, boolean flag, int i, int j, LootItemCondition[] alootitemcondition, LootItemFunction[] alootitemfunction) {
        super(i, j, alootitemcondition, alootitemfunction);
        this.tag = tag;
        this.expand = flag;
    }

    @Override
    public LootEntryType getType() {
        return LootEntries.TAG;
    }

    @Override
    public void createItemStack(Consumer<ItemStack> consumer, LootTableInfo loottableinfo) {
        this.tag.getValues().forEach((item) -> {
            consumer.accept(new ItemStack(item));
        });
    }

    private boolean expandTag(LootTableInfo loottableinfo, Consumer<LootEntry> consumer) {
        if (!this.canRun(loottableinfo)) {
            return false;
        } else {
            Iterator iterator = this.tag.getValues().iterator();

            while (iterator.hasNext()) {
                final Item item = (Item) iterator.next();

                consumer.accept(new LootSelectorEntry.c() {
                    @Override
                    public void createItemStack(Consumer<ItemStack> consumer1, LootTableInfo loottableinfo1) {
                        consumer1.accept(new ItemStack(item));
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

    public static LootSelectorEntry.a<?> tagContents(Tag<Item> tag) {
        return simpleBuilder((i, j, alootitemcondition, alootitemfunction) -> {
            return new LootSelectorTag(tag, false, i, j, alootitemcondition, alootitemfunction);
        });
    }

    public static LootSelectorEntry.a<?> expandTag(Tag<Item> tag) {
        return simpleBuilder((i, j, alootitemcondition, alootitemfunction) -> {
            return new LootSelectorTag(tag, true, i, j, alootitemcondition, alootitemfunction);
        });
    }

    public static class a extends LootSelectorEntry.e<LootSelectorTag> {

        public a() {}

        public void serializeCustom(JsonObject jsonobject, LootSelectorTag lootselectortag, JsonSerializationContext jsonserializationcontext) {
            super.serializeCustom(jsonobject, (LootSelectorEntry) lootselectortag, jsonserializationcontext);
            jsonobject.addProperty("name", TagsInstance.getInstance().getIdOrThrow(IRegistry.ITEM_REGISTRY, lootselectortag.tag, () -> {
                return new IllegalStateException("Unknown item tag");
            }).toString());
            jsonobject.addProperty("expand", lootselectortag.expand);
        }

        @Override
        protected LootSelectorTag deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, int i, int j, LootItemCondition[] alootitemcondition, LootItemFunction[] alootitemfunction) {
            MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "name"));
            Tag<Item> tag = TagsInstance.getInstance().getTagOrThrow(IRegistry.ITEM_REGISTRY, minecraftkey, (minecraftkey1) -> {
                return new JsonParseException("Can't find tag: " + minecraftkey1);
            });
            boolean flag = ChatDeserializer.getAsBoolean(jsonobject, "expand");

            return new LootSelectorTag(tag, flag, i, j, alootitemcondition, alootitemfunction);
        }
    }
}
