package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootItemFunctionSetLore extends LootItemFunctionConditional {

    final boolean replace;
    final List<IChatBaseComponent> lore;
    @Nullable
    final LootTableInfo.EntityTarget resolutionContext;

    public LootItemFunctionSetLore(LootItemCondition[] alootitemcondition, boolean flag, List<IChatBaseComponent> list, @Nullable LootTableInfo.EntityTarget loottableinfo_entitytarget) {
        super(alootitemcondition);
        this.replace = flag;
        this.lore = ImmutableList.copyOf(list);
        this.resolutionContext = loottableinfo_entitytarget;
    }

    @Override
    public LootItemFunctionType a() {
        return LootItemFunctions.SET_LORE;
    }

    @Override
    public Set<LootContextParameter<?>> b() {
        return this.resolutionContext != null ? ImmutableSet.of(this.resolutionContext.a()) : ImmutableSet.of();
    }

    @Override
    public ItemStack a(ItemStack itemstack, LootTableInfo loottableinfo) {
        NBTTagList nbttaglist = this.a(itemstack, !this.lore.isEmpty());

        if (nbttaglist != null) {
            if (this.replace) {
                nbttaglist.clear();
            }

            UnaryOperator<IChatBaseComponent> unaryoperator = LootItemFunctionSetName.a(loottableinfo, this.resolutionContext);
            Stream stream = this.lore.stream().map(unaryoperator).map(IChatBaseComponent.ChatSerializer::a).map(NBTTagString::a);

            Objects.requireNonNull(nbttaglist);
            stream.forEach(nbttaglist::add);
        }

        return itemstack;
    }

    @Nullable
    private NBTTagList a(ItemStack itemstack, boolean flag) {
        NBTTagCompound nbttagcompound;

        if (itemstack.hasTag()) {
            nbttagcompound = itemstack.getTag();
        } else {
            if (!flag) {
                return null;
            }

            nbttagcompound = new NBTTagCompound();
            itemstack.setTag(nbttagcompound);
        }

        NBTTagCompound nbttagcompound1;

        if (nbttagcompound.hasKeyOfType("display", 10)) {
            nbttagcompound1 = nbttagcompound.getCompound("display");
        } else {
            if (!flag) {
                return null;
            }

            nbttagcompound1 = new NBTTagCompound();
            nbttagcompound.set("display", nbttagcompound1);
        }

        if (nbttagcompound1.hasKeyOfType("Lore", 9)) {
            return nbttagcompound1.getList("Lore", 8);
        } else if (flag) {
            NBTTagList nbttaglist = new NBTTagList();

            nbttagcompound1.set("Lore", nbttaglist);
            return nbttaglist;
        } else {
            return null;
        }
    }

    public static LootItemFunctionSetLore.a c() {
        return new LootItemFunctionSetLore.a();
    }

    public static class a extends LootItemFunctionConditional.a<LootItemFunctionSetLore.a> {

        private boolean replace;
        private LootTableInfo.EntityTarget resolutionContext;
        private final List<IChatBaseComponent> lore = Lists.newArrayList();

        public a() {}

        public LootItemFunctionSetLore.a a(boolean flag) {
            this.replace = flag;
            return this;
        }

        public LootItemFunctionSetLore.a a(LootTableInfo.EntityTarget loottableinfo_entitytarget) {
            this.resolutionContext = loottableinfo_entitytarget;
            return this;
        }

        public LootItemFunctionSetLore.a a(IChatBaseComponent ichatbasecomponent) {
            this.lore.add(ichatbasecomponent);
            return this;
        }

        @Override
        protected LootItemFunctionSetLore.a d() {
            return this;
        }

        @Override
        public LootItemFunction b() {
            return new LootItemFunctionSetLore(this.g(), this.replace, this.lore, this.resolutionContext);
        }
    }

    public static class b extends LootItemFunctionConditional.c<LootItemFunctionSetLore> {

        public b() {}

        public void a(JsonObject jsonobject, LootItemFunctionSetLore lootitemfunctionsetlore, JsonSerializationContext jsonserializationcontext) {
            super.a(jsonobject, (LootItemFunctionConditional) lootitemfunctionsetlore, jsonserializationcontext);
            jsonobject.addProperty("replace", lootitemfunctionsetlore.replace);
            JsonArray jsonarray = new JsonArray();
            Iterator iterator = lootitemfunctionsetlore.lore.iterator();

            while (iterator.hasNext()) {
                IChatBaseComponent ichatbasecomponent = (IChatBaseComponent) iterator.next();

                jsonarray.add(IChatBaseComponent.ChatSerializer.b(ichatbasecomponent));
            }

            jsonobject.add("lore", jsonarray);
            if (lootitemfunctionsetlore.resolutionContext != null) {
                jsonobject.add("entity", jsonserializationcontext.serialize(lootitemfunctionsetlore.resolutionContext));
            }

        }

        @Override
        public LootItemFunctionSetLore b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            boolean flag = ChatDeserializer.a(jsonobject, "replace", false);
            List<IChatBaseComponent> list = (List) Streams.stream(ChatDeserializer.u(jsonobject, "lore")).map(IChatBaseComponent.ChatSerializer::a).collect(ImmutableList.toImmutableList());
            LootTableInfo.EntityTarget loottableinfo_entitytarget = (LootTableInfo.EntityTarget) ChatDeserializer.a(jsonobject, "entity", (Object) null, jsondeserializationcontext, LootTableInfo.EntityTarget.class);

            return new LootItemFunctionSetLore(alootitemcondition, flag, list, loottableinfo_entitytarget);
        }
    }
}
