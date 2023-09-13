package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;

public class LootSelectorDynamic extends LootSelectorEntry {

    private final MinecraftKey g;

    private LootSelectorDynamic(MinecraftKey minecraftkey, int i, int j, LootItemCondition[] alootitemcondition, LootItemFunction[] alootitemfunction) {
        super(i, j, alootitemcondition, alootitemfunction);
        this.g = minecraftkey;
    }

    @Override
    public LootEntryType a() {
        return LootEntries.d;
    }

    @Override
    public void a(Consumer<ItemStack> consumer, LootTableInfo loottableinfo) {
        loottableinfo.a(this.g, consumer);
    }

    public static LootSelectorEntry.a<?> a(MinecraftKey minecraftkey) {
        return a((i, j, alootitemcondition, alootitemfunction) -> {
            return new LootSelectorDynamic(minecraftkey, i, j, alootitemcondition, alootitemfunction);
        });
    }

    public static class a extends LootSelectorEntry.e<LootSelectorDynamic> {

        public a() {}

        public void a(JsonObject jsonobject, LootSelectorDynamic lootselectordynamic, JsonSerializationContext jsonserializationcontext) {
            super.a(jsonobject, (LootSelectorEntry) lootselectordynamic, jsonserializationcontext);
            jsonobject.addProperty("name", lootselectordynamic.g.toString());
        }

        @Override
        protected LootSelectorDynamic b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, int i, int j, LootItemCondition[] alootitemcondition, LootItemFunction[] alootitemfunction) {
            MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "name"));

            return new LootSelectorDynamic(minecraftkey, i, j, alootitemcondition, alootitemfunction);
        }
    }
}
