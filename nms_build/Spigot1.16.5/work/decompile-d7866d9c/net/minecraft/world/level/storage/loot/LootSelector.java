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
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.entries.LootEntry;
import net.minecraft.world.level.storage.loot.entries.LootEntryAbstract;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionUser;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionUser;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.mutable.MutableInt;

public class LootSelector {

    private final LootEntryAbstract[] a;
    private final LootItemCondition[] b;
    private final Predicate<LootTableInfo> c;
    private final LootItemFunction[] d;
    private final BiFunction<ItemStack, LootTableInfo, ItemStack> e;
    private final LootValue f;
    private final LootValueBounds g;

    private LootSelector(LootEntryAbstract[] alootentryabstract, LootItemCondition[] alootitemcondition, LootItemFunction[] alootitemfunction, LootValue lootvalue, LootValueBounds lootvaluebounds) {
        this.a = alootentryabstract;
        this.b = alootitemcondition;
        this.c = LootItemConditions.a((Predicate[]) alootitemcondition);
        this.d = alootitemfunction;
        this.e = LootItemFunctions.a(alootitemfunction);
        this.f = lootvalue;
        this.g = lootvaluebounds;
    }

    private void b(Consumer<ItemStack> consumer, LootTableInfo loottableinfo) {
        Random random = loottableinfo.a();
        List<LootEntry> list = Lists.newArrayList();
        MutableInt mutableint = new MutableInt();
        LootEntryAbstract[] alootentryabstract = this.a;
        int i = alootentryabstract.length;

        for (int j = 0; j < i; ++j) {
            LootEntryAbstract lootentryabstract = alootentryabstract[j];

            lootentryabstract.expand(loottableinfo, (lootentry) -> {
                int k = lootentry.a(loottableinfo.getLuck());

                if (k > 0) {
                    list.add(lootentry);
                    mutableint.add(k);
                }

            });
        }

        int k = list.size();

        if (mutableint.intValue() != 0 && k != 0) {
            if (k == 1) {
                ((LootEntry) list.get(0)).a(consumer, loottableinfo);
            } else {
                i = random.nextInt(mutableint.intValue());
                Iterator iterator = list.iterator();

                LootEntry lootentry;

                do {
                    if (!iterator.hasNext()) {
                        return;
                    }

                    lootentry = (LootEntry) iterator.next();
                    i -= lootentry.a(loottableinfo.getLuck());
                } while (i >= 0);

                lootentry.a(consumer, loottableinfo);
            }
        }
    }

    public void a(Consumer<ItemStack> consumer, LootTableInfo loottableinfo) {
        if (this.c.test(loottableinfo)) {
            Consumer<ItemStack> consumer1 = LootItemFunction.a(this.e, consumer, loottableinfo);
            Random random = loottableinfo.a();
            int i = this.f.a(random) + MathHelper.d(this.g.b(random) * loottableinfo.getLuck());

            for (int j = 0; j < i; ++j) {
                this.b(consumer1, loottableinfo);
            }

        }
    }

    public void a(LootCollector lootcollector) {
        int i;

        for (i = 0; i < this.b.length; ++i) {
            this.b[i].a(lootcollector.b(".condition[" + i + "]"));
        }

        for (i = 0; i < this.d.length; ++i) {
            this.d[i].a(lootcollector.b(".functions[" + i + "]"));
        }

        for (i = 0; i < this.a.length; ++i) {
            this.a[i].a(lootcollector.b(".entries[" + i + "]"));
        }

    }

    public static LootSelector.a a() {
        return new LootSelector.a();
    }

    public static class b implements JsonDeserializer<LootSelector>, JsonSerializer<LootSelector> {

        public b() {}

        public LootSelector deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "loot pool");
            LootEntryAbstract[] alootentryabstract = (LootEntryAbstract[]) ChatDeserializer.a(jsonobject, "entries", jsondeserializationcontext, LootEntryAbstract[].class);
            LootItemCondition[] alootitemcondition = (LootItemCondition[]) ChatDeserializer.a(jsonobject, "conditions", new LootItemCondition[0], jsondeserializationcontext, LootItemCondition[].class);
            LootItemFunction[] alootitemfunction = (LootItemFunction[]) ChatDeserializer.a(jsonobject, "functions", new LootItemFunction[0], jsondeserializationcontext, LootItemFunction[].class);
            LootValue lootvalue = LootValueGenerators.a(jsonobject.get("rolls"), jsondeserializationcontext);
            LootValueBounds lootvaluebounds = (LootValueBounds) ChatDeserializer.a(jsonobject, "bonus_rolls", new LootValueBounds(0.0F, 0.0F), jsondeserializationcontext, LootValueBounds.class);

            return new LootSelector(alootentryabstract, alootitemcondition, alootitemfunction, lootvalue, lootvaluebounds);
        }

        public JsonElement serialize(LootSelector lootselector, Type type, JsonSerializationContext jsonserializationcontext) {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("rolls", LootValueGenerators.a(lootselector.f, jsonserializationcontext));
            jsonobject.add("entries", jsonserializationcontext.serialize(lootselector.a));
            if (lootselector.g.b() != 0.0F && lootselector.g.c() != 0.0F) {
                jsonobject.add("bonus_rolls", jsonserializationcontext.serialize(lootselector.g));
            }

            if (!ArrayUtils.isEmpty(lootselector.b)) {
                jsonobject.add("conditions", jsonserializationcontext.serialize(lootselector.b));
            }

            if (!ArrayUtils.isEmpty(lootselector.d)) {
                jsonobject.add("functions", jsonserializationcontext.serialize(lootselector.d));
            }

            return jsonobject;
        }
    }

    public static class a implements LootItemFunctionUser<LootSelector.a>, LootItemConditionUser<LootSelector.a> {

        private final List<LootEntryAbstract> a = Lists.newArrayList();
        private final List<LootItemCondition> b = Lists.newArrayList();
        private final List<LootItemFunction> c = Lists.newArrayList();
        private LootValue d = new LootValueBounds(1.0F);
        private LootValueBounds e = new LootValueBounds(0.0F, 0.0F);

        public a() {}

        public LootSelector.a a(LootValue lootvalue) {
            this.d = lootvalue;
            return this;
        }

        @Override
        public LootSelector.a c() {
            return this;
        }

        public LootSelector.a a(LootEntryAbstract.a<?> lootentryabstract_a) {
            this.a.add(lootentryabstract_a.b());
            return this;
        }

        @Override
        public LootSelector.a b(LootItemCondition.a lootitemcondition_a) {
            this.b.add(lootitemcondition_a.build());
            return this;
        }

        @Override
        public LootSelector.a b(LootItemFunction.a lootitemfunction_a) {
            this.c.add(lootitemfunction_a.b());
            return this;
        }

        public LootSelector b() {
            if (this.d == null) {
                throw new IllegalArgumentException("Rolls not set");
            } else {
                return new LootSelector((LootEntryAbstract[]) this.a.toArray(new LootEntryAbstract[0]), (LootItemCondition[]) this.b.toArray(new LootItemCondition[0]), (LootItemFunction[]) this.c.toArray(new LootItemFunction[0]), this.d, this.e);
            }
        }
    }
}
