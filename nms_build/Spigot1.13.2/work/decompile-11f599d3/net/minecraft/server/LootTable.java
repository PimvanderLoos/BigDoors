package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTable {

    private static final Logger b = LogManager.getLogger();
    public static final LootTable a = new LootTable(new LootSelector[0]);
    private final LootSelector[] c;

    public LootTable(LootSelector[] alootselector) {
        this.c = alootselector;
    }

    public List<ItemStack> populateLoot(Random random, LootTableInfo loottableinfo) {
        List<ItemStack> list = Lists.newArrayList();

        if (loottableinfo.a(this)) {
            LootSelector[] alootselector = this.c;
            int i = alootselector.length;

            for (int j = 0; j < i; ++j) {
                LootSelector lootselector = alootselector[j];

                lootselector.b(list, random, loottableinfo);
            }

            loottableinfo.b(this);
        } else {
            LootTable.b.warn("Detected infinite loop in loot tables");
        }

        return list;
    }

    public void fillInventory(IInventory iinventory, Random random, LootTableInfo loottableinfo) {
        List<ItemStack> list = this.populateLoot(random, loottableinfo);
        List<Integer> list1 = this.a(iinventory, random);

        this.a(list, list1.size(), random);
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            ItemStack itemstack = (ItemStack) iterator.next();

            if (list1.isEmpty()) {
                LootTable.b.warn("Tried to over-fill a container");
                return;
            }

            if (itemstack.isEmpty()) {
                iinventory.setItem((Integer) list1.remove(list1.size() - 1), ItemStack.a);
            } else {
                iinventory.setItem((Integer) list1.remove(list1.size() - 1), itemstack);
            }
        }

    }

    private void a(List<ItemStack> list, int i, Random random) {
        List<ItemStack> list1 = Lists.newArrayList();
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            ItemStack itemstack = (ItemStack) iterator.next();

            if (itemstack.isEmpty()) {
                iterator.remove();
            } else if (itemstack.getCount() > 1) {
                list1.add(itemstack);
                iterator.remove();
            }
        }

        while (i - list.size() - list1.size() > 0 && !list1.isEmpty()) {
            ItemStack itemstack1 = (ItemStack) list1.remove(MathHelper.nextInt(random, 0, list1.size() - 1));
            int j = MathHelper.nextInt(random, 1, itemstack1.getCount() / 2);
            ItemStack itemstack2 = itemstack1.cloneAndSubtract(j);

            if (itemstack1.getCount() > 1 && random.nextBoolean()) {
                list1.add(itemstack1);
            } else {
                list.add(itemstack1);
            }

            if (itemstack2.getCount() > 1 && random.nextBoolean()) {
                list1.add(itemstack2);
            } else {
                list.add(itemstack2);
            }
        }

        list.addAll(list1);
        Collections.shuffle(list, random);
    }

    private List<Integer> a(IInventory iinventory, Random random) {
        List<Integer> list = Lists.newArrayList();

        for (int i = 0; i < iinventory.getSize(); ++i) {
            if (iinventory.getItem(i).isEmpty()) {
                list.add(i);
            }
        }

        Collections.shuffle(list, random);
        return list;
    }

    public static class a implements JsonDeserializer<LootTable>, JsonSerializer<LootTable> {

        public a() {}

        public LootTable deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "loot table");
            LootSelector[] alootselector = (LootSelector[]) ChatDeserializer.a(jsonobject, "pools", new LootSelector[0], jsondeserializationcontext, LootSelector[].class);

            return new LootTable(alootselector);
        }

        public JsonElement serialize(LootTable loottable, Type type, JsonSerializationContext jsonserializationcontext) {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("pools", jsonserializationcontext.serialize(loottable.c));
            return jsonobject;
        }
    }
}
