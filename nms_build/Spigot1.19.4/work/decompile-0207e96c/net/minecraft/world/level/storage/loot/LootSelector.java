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
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
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
        this.compositeCondition = LootItemConditions.andConditions(alootitemcondition);
        this.functions = alootitemfunction;
        this.compositeFunction = LootItemFunctions.compose(alootitemfunction);
        this.rolls = numberprovider;
        this.bonusRolls = numberprovider1;
    }

    private void addRandomItem(Consumer<ItemStack> consumer, LootTableInfo loottableinfo) {
        RandomSource randomsource = loottableinfo.getRandom();
        List<LootEntry> list = Lists.newArrayList();
        MutableInt mutableint = new MutableInt();
        LootEntryAbstract[] alootentryabstract = this.entries;
        int i = alootentryabstract.length;

        for (int j = 0; j < i; ++j) {
            LootEntryAbstract lootentryabstract = alootentryabstract[j];

            lootentryabstract.expand(loottableinfo, (lootentry) -> {
                int k = lootentry.getWeight(loottableinfo.getLuck());

                if (k > 0) {
                    list.add(lootentry);
                    mutableint.add(k);
                }

            });
        }

        int k = list.size();

        if (mutableint.intValue() != 0 && k != 0) {
            if (k == 1) {
                ((LootEntry) list.get(0)).createItemStack(consumer, loottableinfo);
            } else {
                i = randomsource.nextInt(mutableint.intValue());
                Iterator iterator = list.iterator();

                LootEntry lootentry;

                do {
                    if (!iterator.hasNext()) {
                        return;
                    }

                    lootentry = (LootEntry) iterator.next();
                    i -= lootentry.getWeight(loottableinfo.getLuck());
                } while (i >= 0);

                lootentry.createItemStack(consumer, loottableinfo);
            }
        }
    }

    public void addRandomItems(Consumer<ItemStack> consumer, LootTableInfo loottableinfo) {
        if (this.compositeCondition.test(loottableinfo)) {
            Consumer<ItemStack> consumer1 = LootItemFunction.decorate(this.compositeFunction, consumer, loottableinfo);
            int i = this.rolls.getInt(loottableinfo) + MathHelper.floor(this.bonusRolls.getFloat(loottableinfo) * loottableinfo.getLuck());

            for (int j = 0; j < i; ++j) {
                this.addRandomItem(consumer1, loottableinfo);
            }

        }
    }

    public void validate(LootCollector lootcollector) {
        int i;

        for (i = 0; i < this.conditions.length; ++i) {
            this.conditions[i].validate(lootcollector.forChild(".condition[" + i + "]"));
        }

        for (i = 0; i < this.functions.length; ++i) {
            this.functions[i].validate(lootcollector.forChild(".functions[" + i + "]"));
        }

        for (i = 0; i < this.entries.length; ++i) {
            this.entries[i].validate(lootcollector.forChild(".entries[" + i + "]"));
        }

        this.rolls.validate(lootcollector.forChild(".rolls"));
        this.bonusRolls.validate(lootcollector.forChild(".bonusRolls"));
    }

    public static LootSelector.a lootPool() {
        return new LootSelector.a();
    }

    public static class a implements LootItemFunctionUser<LootSelector.a>, LootItemConditionUser<LootSelector.a> {

        private final List<LootEntryAbstract> entries = Lists.newArrayList();
        private final List<LootItemCondition> conditions = Lists.newArrayList();
        private final List<LootItemFunction> functions = Lists.newArrayList();
        private NumberProvider rolls = ConstantValue.exactly(1.0F);
        private NumberProvider bonusRolls = ConstantValue.exactly(0.0F);

        public a() {}

        public LootSelector.a setRolls(NumberProvider numberprovider) {
            this.rolls = numberprovider;
            return this;
        }

        @Override
        public LootSelector.a unwrap() {
            return this;
        }

        public LootSelector.a setBonusRolls(NumberProvider numberprovider) {
            this.bonusRolls = numberprovider;
            return this;
        }

        public LootSelector.a add(LootEntryAbstract.a<?> lootentryabstract_a) {
            this.entries.add(lootentryabstract_a.build());
            return this;
        }

        @Override
        public LootSelector.a when(LootItemCondition.a lootitemcondition_a) {
            this.conditions.add(lootitemcondition_a.build());
            return this;
        }

        @Override
        public LootSelector.a apply(LootItemFunction.a lootitemfunction_a) {
            this.functions.add(lootitemfunction_a.build());
            return this;
        }

        public LootSelector build() {
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
            JsonObject jsonobject = ChatDeserializer.convertToJsonObject(jsonelement, "loot pool");
            LootEntryAbstract[] alootentryabstract = (LootEntryAbstract[]) ChatDeserializer.getAsObject(jsonobject, "entries", jsondeserializationcontext, LootEntryAbstract[].class);
            LootItemCondition[] alootitemcondition = (LootItemCondition[]) ChatDeserializer.getAsObject(jsonobject, "conditions", new LootItemCondition[0], jsondeserializationcontext, LootItemCondition[].class);
            LootItemFunction[] alootitemfunction = (LootItemFunction[]) ChatDeserializer.getAsObject(jsonobject, "functions", new LootItemFunction[0], jsondeserializationcontext, LootItemFunction[].class);
            NumberProvider numberprovider = (NumberProvider) ChatDeserializer.getAsObject(jsonobject, "rolls", jsondeserializationcontext, NumberProvider.class);
            NumberProvider numberprovider1 = (NumberProvider) ChatDeserializer.getAsObject(jsonobject, "bonus_rolls", ConstantValue.exactly(0.0F), jsondeserializationcontext, NumberProvider.class);

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
