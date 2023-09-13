package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class LootSelectorLootTable extends LotoSelectorEntry {

    protected final MinecraftKey a;

    public LootSelectorLootTable(MinecraftKey minecraftkey, int i, int j, LootItemCondition[] alootitemcondition) {
        super(i, j, alootitemcondition);
        this.a = minecraftkey;
    }

    public void a(Collection<ItemStack> collection, Random random, LootTableInfo loottableinfo) {
        LootTable loottable = loottableinfo.e().a(this.a);
        List list = loottable.a(random, loottableinfo);

        collection.addAll(list);
    }

    protected void a(JsonObject jsonobject, JsonSerializationContext jsonserializationcontext) {
        jsonobject.addProperty("name", this.a.toString());
    }

    public static LootSelectorLootTable a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, int i, int j, LootItemCondition[] alootitemcondition) {
        MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "name"));

        return new LootSelectorLootTable(minecraftkey, i, j, alootitemcondition);
    }
}
