package net.minecraft.world.level.storage.loot;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.SystemUtils;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.IInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionUser;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameterSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameterSets;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;

public class LootTable {

    static final Logger LOGGER = LogUtils.getLogger();
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
        this.compositeFunction = LootItemFunctions.compose(alootitemfunction);
    }

    public static Consumer<ItemStack> createStackSplitter(Consumer<ItemStack> consumer) {
        return (itemstack) -> {
            if (itemstack.getCount() < itemstack.getMaxStackSize()) {
                consumer.accept(itemstack);
            } else {
                int i = itemstack.getCount();

                while (i > 0) {
                    ItemStack itemstack1 = itemstack.copy();

                    itemstack1.setCount(Math.min(itemstack.getMaxStackSize(), i));
                    i -= itemstack1.getCount();
                    consumer.accept(itemstack1);
                }
            }

        };
    }

    public void getRandomItemsRaw(LootTableInfo loottableinfo, Consumer<ItemStack> consumer) {
        if (loottableinfo.addVisitedTable(this)) {
            Consumer<ItemStack> consumer1 = LootItemFunction.decorate(this.compositeFunction, consumer, loottableinfo);
            LootSelector[] alootselector = this.pools;
            int i = alootselector.length;

            for (int j = 0; j < i; ++j) {
                LootSelector lootselector = alootselector[j];

                lootselector.addRandomItems(consumer1, loottableinfo);
            }

            loottableinfo.removeVisitedTable(this);
        } else {
            LootTable.LOGGER.warn("Detected infinite loop in loot tables");
        }

    }

    public void getRandomItems(LootTableInfo loottableinfo, Consumer<ItemStack> consumer) {
        this.getRandomItemsRaw(loottableinfo, createStackSplitter(consumer));
    }

    public ObjectArrayList<ItemStack> getRandomItems(LootTableInfo loottableinfo) {
        ObjectArrayList<ItemStack> objectarraylist = new ObjectArrayList();

        Objects.requireNonNull(objectarraylist);
        this.getRandomItems(loottableinfo, objectarraylist::add);
        return objectarraylist;
    }

    public LootContextParameterSet getParamSet() {
        return this.paramSet;
    }

    public void validate(LootCollector lootcollector) {
        int i;

        for (i = 0; i < this.pools.length; ++i) {
            this.pools[i].validate(lootcollector.forChild(".pools[" + i + "]"));
        }

        for (i = 0; i < this.functions.length; ++i) {
            this.functions[i].validate(lootcollector.forChild(".functions[" + i + "]"));
        }

    }

    public void fill(IInventory iinventory, LootTableInfo loottableinfo) {
        ObjectArrayList<ItemStack> objectarraylist = this.getRandomItems(loottableinfo);
        RandomSource randomsource = loottableinfo.getRandom();
        List<Integer> list = this.getAvailableSlots(iinventory, randomsource);

        this.shuffleAndSplitItems(objectarraylist, list.size(), randomsource);
        ObjectListIterator objectlistiterator = objectarraylist.iterator();

        while (objectlistiterator.hasNext()) {
            ItemStack itemstack = (ItemStack) objectlistiterator.next();

            if (list.isEmpty()) {
                LootTable.LOGGER.warn("Tried to over-fill a container");
                return;
            }

            if (itemstack.isEmpty()) {
                iinventory.setItem((Integer) list.remove(list.size() - 1), ItemStack.EMPTY);
            } else {
                iinventory.setItem((Integer) list.remove(list.size() - 1), itemstack);
            }
        }

    }

    private void shuffleAndSplitItems(ObjectArrayList<ItemStack> objectarraylist, int i, RandomSource randomsource) {
        List<ItemStack> list = Lists.newArrayList();
        ObjectListIterator objectlistiterator = objectarraylist.iterator();

        while (objectlistiterator.hasNext()) {
            ItemStack itemstack = (ItemStack) objectlistiterator.next();

            if (itemstack.isEmpty()) {
                objectlistiterator.remove();
            } else if (itemstack.getCount() > 1) {
                list.add(itemstack);
                objectlistiterator.remove();
            }
        }

        while (i - objectarraylist.size() - list.size() > 0 && !list.isEmpty()) {
            ItemStack itemstack1 = (ItemStack) list.remove(MathHelper.nextInt(randomsource, 0, list.size() - 1));
            int j = MathHelper.nextInt(randomsource, 1, itemstack1.getCount() / 2);
            ItemStack itemstack2 = itemstack1.split(j);

            if (itemstack1.getCount() > 1 && randomsource.nextBoolean()) {
                list.add(itemstack1);
            } else {
                objectarraylist.add(itemstack1);
            }

            if (itemstack2.getCount() > 1 && randomsource.nextBoolean()) {
                list.add(itemstack2);
            } else {
                objectarraylist.add(itemstack2);
            }
        }

        objectarraylist.addAll(list);
        SystemUtils.shuffle(objectarraylist, randomsource);
    }

    private List<Integer> getAvailableSlots(IInventory iinventory, RandomSource randomsource) {
        ObjectArrayList<Integer> objectarraylist = new ObjectArrayList();

        for (int i = 0; i < iinventory.getContainerSize(); ++i) {
            if (iinventory.getItem(i).isEmpty()) {
                objectarraylist.add(i);
            }
        }

        SystemUtils.shuffle(objectarraylist, randomsource);
        return objectarraylist;
    }

    public static LootTable.a lootTable() {
        return new LootTable.a();
    }

    public static class a implements LootItemFunctionUser<LootTable.a> {

        private final List<LootSelector> pools = Lists.newArrayList();
        private final List<LootItemFunction> functions = Lists.newArrayList();
        private LootContextParameterSet paramSet;

        public a() {
            this.paramSet = LootTable.DEFAULT_PARAM_SET;
        }

        public LootTable.a withPool(LootSelector.a lootselector_a) {
            this.pools.add(lootselector_a.build());
            return this;
        }

        public LootTable.a setParamSet(LootContextParameterSet lootcontextparameterset) {
            this.paramSet = lootcontextparameterset;
            return this;
        }

        @Override
        public LootTable.a apply(LootItemFunction.a lootitemfunction_a) {
            this.functions.add(lootitemfunction_a.build());
            return this;
        }

        @Override
        public LootTable.a unwrap() {
            return this;
        }

        public LootTable build() {
            return new LootTable(this.paramSet, (LootSelector[]) this.pools.toArray(new LootSelector[0]), (LootItemFunction[]) this.functions.toArray(new LootItemFunction[0]));
        }
    }

    public static class b implements JsonDeserializer<LootTable>, JsonSerializer<LootTable> {

        public b() {}

        public LootTable deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            JsonObject jsonobject = ChatDeserializer.convertToJsonObject(jsonelement, "loot table");
            LootSelector[] alootselector = (LootSelector[]) ChatDeserializer.getAsObject(jsonobject, "pools", new LootSelector[0], jsondeserializationcontext, LootSelector[].class);
            LootContextParameterSet lootcontextparameterset = null;

            if (jsonobject.has("type")) {
                String s = ChatDeserializer.getAsString(jsonobject, "type");

                lootcontextparameterset = LootContextParameterSets.get(new MinecraftKey(s));
            }

            LootItemFunction[] alootitemfunction = (LootItemFunction[]) ChatDeserializer.getAsObject(jsonobject, "functions", new LootItemFunction[0], jsondeserializationcontext, LootItemFunction[].class);

            return new LootTable(lootcontextparameterset != null ? lootcontextparameterset : LootContextParameterSets.ALL_PARAMS, alootselector, alootitemfunction);
        }

        public JsonElement serialize(LootTable loottable, Type type, JsonSerializationContext jsonserializationcontext) {
            JsonObject jsonobject = new JsonObject();

            if (loottable.paramSet != LootTable.DEFAULT_PARAM_SET) {
                MinecraftKey minecraftkey = LootContextParameterSets.getKey(loottable.paramSet);

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
