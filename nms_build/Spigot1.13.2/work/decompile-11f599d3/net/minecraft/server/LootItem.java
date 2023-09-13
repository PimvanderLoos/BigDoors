package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Collection;
import java.util.Random;

public class LootItem extends LootSelectorEntry {

    protected final Item a;
    protected final LootItemFunction[] b;

    public LootItem(Item item, int i, int j, LootItemFunction[] alootitemfunction, LootItemCondition[] alootitemcondition) {
        super(i, j, alootitemcondition);
        this.a = item;
        this.b = alootitemfunction;
    }

    public void a(Collection<ItemStack> collection, Random random, LootTableInfo loottableinfo) {
        ItemStack itemstack = new ItemStack(this.a);
        LootItemFunction[] alootitemfunction = this.b;
        int i = alootitemfunction.length;

        for (int j = 0; j < i; ++j) {
            LootItemFunction lootitemfunction = alootitemfunction[j];

            if (LootItemConditions.a(lootitemfunction.b(), random, loottableinfo)) {
                itemstack = lootitemfunction.a(itemstack, random, loottableinfo);
            }
        }

        if (!itemstack.isEmpty()) {
            if (itemstack.getCount() < this.a.getMaxStackSize()) {
                collection.add(itemstack);
            } else {
                int k = itemstack.getCount();

                while (k > 0) {
                    ItemStack itemstack1 = itemstack.cloneItemStack();

                    itemstack1.setCount(Math.min(itemstack.getMaxStackSize(), k));
                    k -= itemstack1.getCount();
                    collection.add(itemstack1);
                }
            }
        }

    }

    protected void a(JsonObject jsonobject, JsonSerializationContext jsonserializationcontext) {
        if (this.b != null && this.b.length > 0) {
            jsonobject.add("functions", jsonserializationcontext.serialize(this.b));
        }

        MinecraftKey minecraftkey = IRegistry.ITEM.getKey(this.a);

        if (minecraftkey == null) {
            throw new IllegalArgumentException("Can't serialize unknown item " + this.a);
        } else {
            jsonobject.addProperty("name", minecraftkey.toString());
        }
    }

    public static LootItem a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, int i, int j, LootItemCondition[] alootitemcondition) {
        Item item = ChatDeserializer.i(jsonobject, "name");
        LootItemFunction[] alootitemfunction;

        if (jsonobject.has("functions")) {
            alootitemfunction = (LootItemFunction[]) ChatDeserializer.a(jsonobject, "functions", jsondeserializationcontext, LootItemFunction[].class);
        } else {
            alootitemfunction = new LootItemFunction[0];
        }

        return new LootItem(item, i, j, alootitemfunction, alootitemcondition);
    }
}
