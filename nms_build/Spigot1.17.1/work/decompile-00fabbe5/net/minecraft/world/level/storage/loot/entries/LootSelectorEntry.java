package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootCollector;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionUser;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.commons.lang3.ArrayUtils;

public abstract class LootSelectorEntry extends LootEntryAbstract {

    public static final int DEFAULT_WEIGHT = 1;
    public static final int DEFAULT_QUALITY = 0;
    protected final int weight;
    protected final int quality;
    protected final LootItemFunction[] functions;
    final BiFunction<ItemStack, LootTableInfo, ItemStack> compositeFunction;
    private final LootEntry entry = new LootSelectorEntry.c() {
        @Override
        public void a(Consumer<ItemStack> consumer, LootTableInfo loottableinfo) {
            LootSelectorEntry.this.a(LootItemFunction.a(LootSelectorEntry.this.compositeFunction, consumer, loottableinfo), loottableinfo);
        }
    };

    protected LootSelectorEntry(int i, int j, LootItemCondition[] alootitemcondition, LootItemFunction[] alootitemfunction) {
        super(alootitemcondition);
        this.weight = i;
        this.quality = j;
        this.functions = alootitemfunction;
        this.compositeFunction = LootItemFunctions.a(alootitemfunction);
    }

    @Override
    public void a(LootCollector lootcollector) {
        super.a(lootcollector);

        for (int i = 0; i < this.functions.length; ++i) {
            this.functions[i].a(lootcollector.b(".functions[" + i + "]"));
        }

    }

    protected abstract void a(Consumer<ItemStack> consumer, LootTableInfo loottableinfo);

    @Override
    public boolean expand(LootTableInfo loottableinfo, Consumer<LootEntry> consumer) {
        if (this.a(loottableinfo)) {
            consumer.accept(this.entry);
            return true;
        } else {
            return false;
        }
    }

    public static LootSelectorEntry.a<?> a(LootSelectorEntry.d lootselectorentry_d) {
        return new LootSelectorEntry.b(lootselectorentry_d);
    }

    private static class b extends LootSelectorEntry.a<LootSelectorEntry.b> {

        private final LootSelectorEntry.d constructor;

        public b(LootSelectorEntry.d lootselectorentry_d) {
            this.constructor = lootselectorentry_d;
        }

        @Override
        protected LootSelectorEntry.b d() {
            return this;
        }

        @Override
        public LootEntryAbstract b() {
            return this.constructor.build(this.weight, this.quality, this.f(), this.a());
        }
    }

    @FunctionalInterface
    protected interface d {

        LootSelectorEntry build(int i, int j, LootItemCondition[] alootitemcondition, LootItemFunction[] alootitemfunction);
    }

    public abstract static class e<T extends LootSelectorEntry> extends LootEntryAbstract.Serializer<T> {

        public e() {}

        public void a(JsonObject jsonobject, T t0, JsonSerializationContext jsonserializationcontext) {
            if (t0.weight != 1) {
                jsonobject.addProperty("weight", t0.weight);
            }

            if (t0.quality != 0) {
                jsonobject.addProperty("quality", t0.quality);
            }

            if (!ArrayUtils.isEmpty(t0.functions)) {
                jsonobject.add("functions", jsonserializationcontext.serialize(t0.functions));
            }

        }

        @Override
        public final T deserializeType(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            int i = ChatDeserializer.a(jsonobject, "weight", (int) 1);
            int j = ChatDeserializer.a(jsonobject, "quality", (int) 0);
            LootItemFunction[] alootitemfunction = (LootItemFunction[]) ChatDeserializer.a(jsonobject, "functions", new LootItemFunction[0], jsondeserializationcontext, LootItemFunction[].class);

            return this.b(jsonobject, jsondeserializationcontext, i, j, alootitemcondition, alootitemfunction);
        }

        protected abstract T b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, int i, int j, LootItemCondition[] alootitemcondition, LootItemFunction[] alootitemfunction);
    }

    public abstract static class a<T extends LootSelectorEntry.a<T>> extends LootEntryAbstract.a<T> implements LootItemFunctionUser<T> {

        protected int weight = 1;
        protected int quality = 0;
        private final List<LootItemFunction> functions = Lists.newArrayList();

        public a() {}

        @Override
        public T b(LootItemFunction.a lootitemfunction_a) {
            this.functions.add(lootitemfunction_a.b());
            return (LootSelectorEntry.a) this.d();
        }

        protected LootItemFunction[] a() {
            return (LootItemFunction[]) this.functions.toArray(new LootItemFunction[0]);
        }

        public T a(int i) {
            this.weight = i;
            return (LootSelectorEntry.a) this.d();
        }

        public T b(int i) {
            this.quality = i;
            return (LootSelectorEntry.a) this.d();
        }
    }

    protected abstract class c implements LootEntry {

        protected c() {}

        @Override
        public int a(float f) {
            return Math.max(MathHelper.d((float) LootSelectorEntry.this.weight + (float) LootSelectorEntry.this.quality * f), 0);
        }
    }
}
