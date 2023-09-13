package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;

public class LootSelectorLootTable extends LootSelectorEntry {

    private final MinecraftKey c;

    private LootSelectorLootTable(MinecraftKey minecraftkey, int i, int j, LootItemCondition[] alootitemcondition, LootItemFunction[] alootitemfunction) {
        super(i, j, alootitemcondition, alootitemfunction);
        this.c = minecraftkey;
    }

    @Override
    public void a(Consumer<ItemStack> consumer, LootTableInfo loottableinfo) {
        LootTable loottable = loottableinfo.a(this.c);

        loottable.populateLootDirect(loottableinfo, consumer);
    }

    @Override
    public void a(LootCollector lootcollector) {
        if (lootcollector.a(this.c)) {
            lootcollector.a("Table " + this.c + " is recursively called");
        } else {
            super.a(lootcollector);
            LootTable loottable = lootcollector.c(this.c);

            if (loottable == null) {
                lootcollector.a("Unknown loot table called " + this.c);
            } else {
                loottable.a(lootcollector.a("->{" + this.c + "}", this.c));
            }

        }
    }

    public static LootSelectorEntry.a<?> a(MinecraftKey minecraftkey) {
        return a((i, j, alootitemcondition, alootitemfunction) -> {
            return new LootSelectorLootTable(minecraftkey, i, j, alootitemcondition, alootitemfunction);
        });
    }

    public static class a extends LootSelectorEntry.e<LootSelectorLootTable> {

        public a() {
            super(new MinecraftKey("loot_table"), LootSelectorLootTable.class);
        }

        public void a(JsonObject jsonobject, LootSelectorLootTable lootselectorloottable, JsonSerializationContext jsonserializationcontext) {
            super.a(jsonobject, (LootSelectorEntry) lootselectorloottable, jsonserializationcontext);
            jsonobject.addProperty("name", lootselectorloottable.c.toString());
        }

        @Override
        protected LootSelectorLootTable b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, int i, int j, LootItemCondition[] alootitemcondition, LootItemFunction[] alootitemfunction) {
            MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "name"));

            return new LootSelectorLootTable(minecraftkey, i, j, alootitemcondition, alootitemfunction);
        }
    }
}
