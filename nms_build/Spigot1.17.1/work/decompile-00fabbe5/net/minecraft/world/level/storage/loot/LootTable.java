package net.minecraft.world.level.storage.loot;

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
import java.util.Objects;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionUser;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameterSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameterSets;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTable {

    static final Logger LOGGER = LogManager.getLogger();
    public static final LootTable EMPTY = new LootTable(LootContextParameterSets.EMPTY, new LootSelector[0], new LootItemFunction[0]);
    public static final LootContextParameterSet DEFAULT_PARAM_SET = LootContextParameterSets.ALL_PARAMS;
    final LootContextParameterSet paramSet;
    final LootSelector[] pools;
    final LootItemFunction[] functions;
    private final BiFunction<ItemStack, LootTableInfo, ItemStack> compositeFunction;

    LootTable(LootContextParameterSet lootcontextparameterset, LootSelector[] alootselector, LootItemFunction[] alootitemfunction) {
        this.paramSet = lootcontextparameterset;
        this.pools = alootselector;
        this.functions = alootitemfunction;
        this.compositeFunction = LootItemFunctions.a(alootitemfunction);
    }

    public static Consumer<ItemStack> a(Consumer<ItemStack> consumer) {
        return (itemstack) -> {
            if (itemstack.getCount() < itemstack.getMaxStackSize()) {
                consumer.accept(itemstack);
            } else {
                int i = itemstack.getCount();

                while (i > 0) {
                    ItemStack itemstack1 = itemstack.cloneItemStack();

                    itemstack1.setCount(Math.min(itemstack.getMaxStackSize(), i));
                    i -= itemstack1.getCount();
                    consumer.accept(itemstack1);
                }
            }

        };
    }

    public void populateLootDirect(LootTableInfo loottableinfo, Consumer<ItemStack> consumer) {
        if (loottableinfo.a(this)) {
            Consumer<ItemStack> consumer1 = LootItemFunction.a(this.compositeFunction, consumer, loottableinfo);
            LootSelector[] alootselector = this.pools;
            int i = alootselector.length;

            for (int j = 0; j < i; ++j) {
                LootSelector lootselector = alootselector[j];

                lootselector.a(consumer1, loottableinfo);
            }

            loottableinfo.b(this);
        } else {
            LootTable.LOGGER.warn("Detected infinite loop in loot tables");
        }

    }

    public void populateLoot(LootTableInfo loottableinfo, Consumer<ItemStack> consumer) {
        this.populateLootDirect(loottableinfo, a(consumer));
    }

    public List<ItemStack> populateLoot(LootTableInfo loottableinfo) {
        List<ItemStack> list = Lists.newArrayList();

        Objects.requireNonNull(list);
        this.populateLoot(loottableinfo, list::add);
        return list;
    }

    public LootContextParameterSet getLootContextParameterSet() {
        return this.paramSet;
    }

    public void a(LootCollector lootcollector) {
        int i;

        for (i = 0; i < this.pools.length; ++i) {
            this.pools[i].a(lootcollector.b(".pools[" + i + "]"));
        }

        for (i = 0; i < this.functions.length; ++i) {
            this.functions[i].a(lootcollector.b(".functions[" + i + "]"));
        }

    }

    public void fillInventory(IInventory iinventory, LootTableInfo loottableinfo) {
        List<ItemStack> list = this.populateLoot(loottableinfo);
        Random random = loottableinfo.a();
        List<Integer> list1 = this.a(iinventory, random);

        this.a(list, list1.size(), random);
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            ItemStack itemstack = (ItemStack) iterator.next();

            if (list1.isEmpty()) {
                LootTable.LOGGER.warn("Tried to over-fill a container");
                return;
            }

            if (itemstack.isEmpty()) {
                iinventory.setItem((Integer) list1.remove(list1.size() - 1), ItemStack.EMPTY);
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

    public static LootTable.a b() {
        return new LootTable.a();
    }

    public static class a implements LootItemFunctionUser<LootTable.a> {

        private final List<LootSelector> pools = Lists.newArrayList();
        private final List<LootItemFunction> functions = Lists.newArrayList();
        private LootContextParameterSet paramSet;

        public a() {
            this.paramSet = LootTable.DEFAULT_PARAM_SET;
        }

        public LootTable.a a(LootSelector.a lootselector_a) {
            this.pools.add(lootselector_a.b());
            return this;
        }

        public LootTable.a a(LootContextParameterSet lootcontextparameterset) {
            this.paramSet = lootcontextparameterset;
            return this;
        }

        @Override
        public LootTable.a b(LootItemFunction.a lootitemfunction_a) {
            this.functions.add(lootitemfunction_a.b());
            return this;
        }

        @Override
        public LootTable.a c() {
            return this;
        }

        public LootTable b() {
            return new LootTable(this.paramSet, (LootSelector[]) this.pools.toArray(new LootSelector[0]), (LootItemFunction[]) this.functions.toArray(new LootItemFunction[0]));
        }
    }

    public static class b implements JsonDeserializer<LootTable>, JsonSerializer<LootTable> {

        public b() {}

        public LootTable deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "loot table");
            LootSelector[] alootselector = (LootSelector[]) ChatDeserializer.a(jsonobject, "pools", new LootSelector[0], jsondeserializationcontext, LootSelector[].class);
            LootContextParameterSet lootcontextparameterset = null;

            if (jsonobject.has("type")) {
                String s = ChatDeserializer.h(jsonobject, "type");

                lootcontextparameterset = LootContextParameterSets.a(new MinecraftKey(s));
            }

            LootItemFunction[] alootitemfunction = (LootItemFunction[]) ChatDeserializer.a(jsonobject, "functions", new LootItemFunction[0], jsondeserializationcontext, LootItemFunction[].class);

            return new LootTable(lootcontextparameterset != null ? lootcontextparameterset : LootContextParameterSets.ALL_PARAMS, alootselector, alootitemfunction);
        }

        public JsonElement serialize(LootTable loottable, Type type, JsonSerializationContext jsonserializationcontext) {
            JsonObject jsonobject = new JsonObject();

            if (loottable.paramSet != LootTable.DEFAULT_PARAM_SET) {
                MinecraftKey minecraftkey = LootContextParameterSets.a(loottable.paramSet);

                if (minecraftkey != null) {
                    jsonobject.addProperty("type", minecraftkey.toString());
                } else {
                    LootTable.LOGGER.warn("Failed to find id for param set {}", loottable.paramSet);
                }
            }

            if (loottable.pools.length > 0) {
                jsonobject.add("pools", jsonserializationcontext.serialize(loottable.pools));
            }

            if (!ArrayUtils.isEmpty(loottable.functions)) {
                jsonobject.add("functions", jsonserializationcontext.serialize(loottable.functions));
            }

            return jsonobject;
        }
    }
}
