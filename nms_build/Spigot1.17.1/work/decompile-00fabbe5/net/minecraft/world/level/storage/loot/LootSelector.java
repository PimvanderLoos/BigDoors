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
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.mutable.MutableInt;

public class LootSelector {

    final LootEntryAbstract[] entries;
    final LootItemCondition[] conditions;
    private final Predicate<LootTableInfo> compositeCondition;
    final LootItemFunction[] functions;
    private final BiFunction<ItemStack, LootTableInfo, ItemStack> compositeFunction;
    final NumberProvider rolls;
    final NumberProvider bonusRolls;

    LootSelector(LootEntryAbstract[] alootentryabstract, LootItemCondition[] alootitemcondition, LootItemFunction[] alootitemfunction, NumberProvider numberprovider, NumberProvider numberprovider1) {
        this.entries = alootentryabstract;
        this.conditions = alootitemcondition;
        this.compositeCondition = LootItemConditions.a((Predicate[]) alootitemcondition);
        this.functions = alootitemfunction;
        this.compositeFunction = LootItemFunctions.a(alootitemfunction);
        this.rolls = numberprovider;
        this.bonusRolls = numberprovider1;
    }

    private void b(Consumer<ItemStack> consumer, LootTableInfo loottableinfo) {
        Random random = loottableinfo.a();
        List<LootEntry> list = Lists.newArrayList();
        MutableInt mutableint = new MutableInt();
        LootEntryAbstract[] alootentryabstract = this.entries;
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
        if (this.compositeCondition.test(loottableinfo)) {
            Consumer<ItemStack> consumer1 = LootItemFunction.a(this.compositeFunction, consumer, loottableinfo);
            int i = this.rolls.a(loottableinfo) + MathHelper.d(this.bonusRolls.b(loottableinfo) * loottableinfo.getLuck());

            for (int j = 0; j < i; ++j) {
                this.b(consumer1, loottableinfo);
            }

        }
    }

    public void a(LootCollector lootcollector) {
        int i;

        for (i = 0; i < this.conditions.length; ++i) {
            this.conditions[i].a(lootcollector.b(".condition[" + i + "]"));
        }

        for (i = 0; i < this.functions.length; ++i) {
            this.functions[i].a(lootcollector.b(".functions[" + i + "]"));
        }

        for (i = 0; i < this.entries.length; ++i) {
            this.entries[i].a(lootcollector.b(".entries[" + i + "]"));
        }

        this.rolls.a(lootcollector.b(".rolls"));
        this.bonusRolls.a(lootcollector.b(".bonusRolls"));
    }

    public static LootSelector.a a() {
        return new LootSelector.a();
    }

    public static class a implements LootItemFunctionUser<LootSelector.a>, LootItemConditionUser<LootSelector.a> {

        private final List<LootEntryAbstract> entries = Lists.newArrayList();
        private final List<LootItemCondition> conditions = Lists.newArrayList();
        private final List<LootItemFunction> functions = Lists.newArrayList();
        private NumberProvider rolls = ConstantValue.a(1.0F);
        private NumberProvider bonusRolls = ConstantValue.a(0.0F);

        public a() {}

        public LootSelector.a a(NumberProvider numberprovider) {
            this.rolls = numberprovider;
            return this;
        }

        @Override
        public LootSelector.a c() {
            return this;
        }

        public LootSelector.a b(NumberProvider numberprovider) {
            this.bonusRolls = numberprovider;
            return this;
        }

        public LootSelector.a a(LootEntryAbstract.a<?> lootentryabstract_a) {
            this.entries.add(lootentryabstract_a.b());
            return this;
        }

        @Override
        public LootSelector.a b(LootItemCondition.a lootitemcondition_a) {
            this.conditions.add(lootitemcondition_a.build());
            return this;
        }

        @Override
        public LootSelector.a b(LootItemFunction.a lootitemfunction_a) {
            this.functions.add(lootitemfunction_a.b());
            return this;
        }

        public LootSelector b() {
            if (this.rolls == null) {
                throw new IllegalArgumentException("Rolls not set");
            } else {
                return new LootSelector((LootEntryAbstract[]) this.entries.toArray(new LootEntryAbstract[0]), (LootItemCondition[]) this.conditions.toArray(new LootItemCondition[0]), (LootItemFunction[]) this.functions.toArray(new LootItemFunction[0]), this.rolls, this.bonusRolls);
            }
        }
    }

    public static class b implements JsonDeserializer<LootSelector>, JsonSerializer<LootSelector> {

        public b() {}

        public LootSelector deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "loot pool");
            LootEntryAbstract[] alootentryabstract = (LootEntryAbstract[]) ChatDeserializer.a(jsonobject, "entries", jsondeserializationcontext, LootEntryAbstract[].class);
            LootItemCondition[] alootitemcondition = (LootItemCondition[]) ChatDeserializer.a(jsonobject, "conditions", new LootItemCondition[0], jsondeserializationcontext, LootItemCondition[].class);
            LootItemFunction[] alootitemfunction = (LootItemFunction[]) ChatDeserializer.a(jsonobject, "functions", new LootItemFunction[0], jsondeserializationcontext, LootItemFunction[].class);
            NumberProvider numberprovider = (NumberProvider) ChatDeserializer.a(jsonobject, "rolls", jsondeserializationcontext, NumberProvider.class);
            NumberProvider numberprovider1 = (NumberProvider) ChatDeserializer.a(jsonobject, "bonus_rolls", ConstantValue.a(0.0F), jsondeserializationcontext, NumberProvider.class);

            return new LootSelector(alootentryabstract, alootitemcondition, alootitemfunction, numberprovider, numberprovider1);
        }

        public JsonElement serialize(LootSelector lootselector, Type type, JsonSerializationContext jsonserializationcontext) {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("rolls", jsonserializationcontext.serialize(lootselector.rolls));
            jsonobject.add("bonus_rolls", jsonserializationcontext.serialize(lootselector.bonusRolls));
            jsonobject.add("entries", jsonserializationcontext.serialize(lootselector.entries));
            if (!ArrayUtils.isEmpty(lootselector.conditions)) {
                jsonobject.add("conditions", jsonserializationcontext.serialize(lootselector.conditions));
            }

            if (!ArrayUtils.isEmpty(lootselector.functions)) {
                jsonobject.add("functions", jsonserializationcontext.serialize(lootselector.functions));
            }

            return jsonobject;
        }
    }
}
