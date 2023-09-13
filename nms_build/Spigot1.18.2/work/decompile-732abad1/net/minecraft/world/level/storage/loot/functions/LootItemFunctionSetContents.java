package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.IRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.ContainerUtil;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.storage.loot.LootCollector;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.entries.LootEntryAbstract;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootItemFunctionSetContents extends LootItemFunctionConditional {

    final List<LootEntryAbstract> entries;
    final TileEntityTypes<?> type;

    LootItemFunctionSetContents(LootItemCondition[] alootitemcondition, TileEntityTypes<?> tileentitytypes, List<LootEntryAbstract> list) {
        super(alootitemcondition);
        this.type = tileentitytypes;
        this.entries = ImmutableList.copyOf(list);
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.SET_CONTENTS;
    }

    @Override
    public ItemStack run(ItemStack itemstack, LootTableInfo loottableinfo) {
        if (itemstack.isEmpty()) {
            return itemstack;
        } else {
            NonNullList<ItemStack> nonnulllist = NonNullList.create();

            this.entries.forEach((lootentryabstract) -> {
                lootentryabstract.expand(loottableinfo, (lootentry) -> {
                    Objects.requireNonNull(nonnulllist);
                    lootentry.createItemStack(LootTable.createStackSplitter(nonnulllist::add), loottableinfo);
                });
            });
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            ContainerUtil.saveAllItems(nbttagcompound, nonnulllist);
            NBTTagCompound nbttagcompound1 = ItemBlock.getBlockEntityData(itemstack);

            if (nbttagcompound1 == null) {
                nbttagcompound1 = nbttagcompound;
            } else {
                nbttagcompound1.merge(nbttagcompound);
            }

            ItemBlock.setBlockEntityData(itemstack, this.type, nbttagcompound1);
            return itemstack;
        }
    }

    @Override
    public void validate(LootCollector lootcollector) {
        super.validate(lootcollector);

        for (int i = 0; i < this.entries.size(); ++i) {
            ((LootEntryAbstract) this.entries.get(i)).validate(lootcollector.forChild(".entry[" + i + "]"));
        }

    }

    public static LootItemFunctionSetContents.a setContents(TileEntityTypes<?> tileentitytypes) {
        return new LootItemFunctionSetContents.a(tileentitytypes);
    }

    public static class a extends LootItemFunctionConditional.a<LootItemFunctionSetContents.a> {

        private final List<LootEntryAbstract> entries = Lists.newArrayList();
        private final TileEntityTypes<?> type;

        public a(TileEntityTypes<?> tileentitytypes) {
            this.type = tileentitytypes;
        }

        @Override
        protected LootItemFunctionSetContents.a getThis() {
            return this;
        }

        public LootItemFunctionSetContents.a withEntry(LootEntryAbstract.a<?> lootentryabstract_a) {
            this.entries.add(lootentryabstract_a.build());
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new LootItemFunctionSetContents(this.getConditions(), this.type, this.entries);
        }
    }

    public static class b extends LootItemFunctionConditional.c<LootItemFunctionSetContents> {

        public b() {}

        public void serialize(JsonObject jsonobject, LootItemFunctionSetContents lootitemfunctionsetcontents, JsonSerializationContext jsonserializationcontext) {
            super.serialize(jsonobject, (LootItemFunctionConditional) lootitemfunctionsetcontents, jsonserializationcontext);
            jsonobject.addProperty("type", IRegistry.BLOCK_ENTITY_TYPE.getKey(lootitemfunctionsetcontents.type).toString());
            jsonobject.add("entries", jsonserializationcontext.serialize(lootitemfunctionsetcontents.entries));
        }

        @Override
        public LootItemFunctionSetContents deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            LootEntryAbstract[] alootentryabstract = (LootEntryAbstract[]) ChatDeserializer.getAsObject(jsonobject, "entries", jsondeserializationcontext, LootEntryAbstract[].class);
            MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "type"));
            TileEntityTypes<?> tileentitytypes = (TileEntityTypes) IRegistry.BLOCK_ENTITY_TYPE.getOptional(minecraftkey).orElseThrow(() -> {
                return new JsonSyntaxException("Unknown block entity type id '" + minecraftkey + "'");
            });

            return new LootItemFunctionSetContents(alootitemcondition, tileentitytypes, Arrays.asList(alootentryabstract));
        }
    }
}
