package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.ContainerUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootCollector;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.entries.LootEntryAbstract;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootItemFunctionSetContents extends LootItemFunctionConditional {

    final List<LootEntryAbstract> entries;

    LootItemFunctionSetContents(LootItemCondition[] alootitemcondition, List<LootEntryAbstract> list) {
        super(alootitemcondition);
        this.entries = ImmutableList.copyOf(list);
    }

    @Override
    public LootItemFunctionType a() {
        return LootItemFunctions.SET_CONTENTS;
    }

    @Override
    public ItemStack a(ItemStack itemstack, LootTableInfo loottableinfo) {
        if (itemstack.isEmpty()) {
            return itemstack;
        } else {
            NonNullList<ItemStack> nonnulllist = NonNullList.a();

            this.entries.forEach((lootentryabstract) -> {
                lootentryabstract.expand(loottableinfo, (lootentry) -> {
                    Objects.requireNonNull(nonnulllist);
                    lootentry.a(LootTable.a(nonnulllist::add), loottableinfo);
                });
            });
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            ContainerUtil.a(nbttagcompound, nonnulllist);
            NBTTagCompound nbttagcompound1 = itemstack.getOrCreateTag();

            nbttagcompound1.set("BlockEntityTag", nbttagcompound.a(nbttagcompound1.getCompound("BlockEntityTag")));
            return itemstack;
        }
    }

    @Override
    public void a(LootCollector lootcollector) {
        super.a(lootcollector);

        for (int i = 0; i < this.entries.size(); ++i) {
            ((LootEntryAbstract) this.entries.get(i)).a(lootcollector.b(".entry[" + i + "]"));
        }

    }

    public static LootItemFunctionSetContents.a c() {
        return new LootItemFunctionSetContents.a();
    }

    public static class a extends LootItemFunctionConditional.a<LootItemFunctionSetContents.a> {

        private final List<LootEntryAbstract> entries = Lists.newArrayList();

        public a() {}

        @Override
        protected LootItemFunctionSetContents.a d() {
            return this;
        }

        public LootItemFunctionSetContents.a a(LootEntryAbstract.a<?> lootentryabstract_a) {
            this.entries.add(lootentryabstract_a.b());
            return this;
        }

        @Override
        public LootItemFunction b() {
            return new LootItemFunctionSetContents(this.g(), this.entries);
        }
    }

    public static class b extends LootItemFunctionConditional.c<LootItemFunctionSetContents> {

        public b() {}

        public void a(JsonObject jsonobject, LootItemFunctionSetContents lootitemfunctionsetcontents, JsonSerializationContext jsonserializationcontext) {
            super.a(jsonobject, (LootItemFunctionConditional) lootitemfunctionsetcontents, jsonserializationcontext);
            jsonobject.add("entries", jsonserializationcontext.serialize(lootitemfunctionsetcontents.entries));
        }

        @Override
        public LootItemFunctionSetContents b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            LootEntryAbstract[] alootentryabstract = (LootEntryAbstract[]) ChatDeserializer.a(jsonobject, "entries", jsondeserializationcontext, LootEntryAbstract[].class);

            return new LootItemFunctionSetContents(alootitemcondition, Arrays.asList(alootentryabstract));
        }
    }
}
