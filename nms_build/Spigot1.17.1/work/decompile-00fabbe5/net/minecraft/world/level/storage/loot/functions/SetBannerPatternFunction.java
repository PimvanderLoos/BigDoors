package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Objects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.item.EnumColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.EnumBannerPatternType;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetBannerPatternFunction extends LootItemFunctionConditional {

    final List<Pair<EnumBannerPatternType, EnumColor>> patterns;
    final boolean append;

    SetBannerPatternFunction(LootItemCondition[] alootitemcondition, List<Pair<EnumBannerPatternType, EnumColor>> list, boolean flag) {
        super(alootitemcondition);
        this.patterns = list;
        this.append = flag;
    }

    @Override
    protected ItemStack a(ItemStack itemstack, LootTableInfo loottableinfo) {
        NBTTagCompound nbttagcompound = itemstack.a("BlockEntityTag");
        EnumBannerPatternType.a enumbannerpatterntype_a = new EnumBannerPatternType.a();
        List list = this.patterns;

        Objects.requireNonNull(enumbannerpatterntype_a);
        list.forEach(enumbannerpatterntype_a::a);
        NBTTagList nbttaglist = enumbannerpatterntype_a.a();
        NBTTagList nbttaglist1;

        if (this.append) {
            nbttaglist1 = nbttagcompound.getList("Patterns", 10).clone();
            nbttaglist1.addAll(nbttaglist);
        } else {
            nbttaglist1 = nbttaglist;
        }

        nbttagcompound.set("Patterns", nbttaglist1);
        return itemstack;
    }

    @Override
    public LootItemFunctionType a() {
        return LootItemFunctions.SET_BANNER_PATTERN;
    }

    public static SetBannerPatternFunction.a a(boolean flag) {
        return new SetBannerPatternFunction.a(flag);
    }

    public static class a extends LootItemFunctionConditional.a<SetBannerPatternFunction.a> {

        private final Builder<Pair<EnumBannerPatternType, EnumColor>> patterns = ImmutableList.builder();
        private final boolean append;

        a(boolean flag) {
            this.append = flag;
        }

        @Override
        protected SetBannerPatternFunction.a d() {
            return this;
        }

        @Override
        public LootItemFunction b() {
            return new SetBannerPatternFunction(this.g(), this.patterns.build(), this.append);
        }

        public SetBannerPatternFunction.a a(EnumBannerPatternType enumbannerpatterntype, EnumColor enumcolor) {
            this.patterns.add(Pair.of(enumbannerpatterntype, enumcolor));
            return this;
        }
    }

    public static class b extends LootItemFunctionConditional.c<SetBannerPatternFunction> {

        public b() {}

        public void a(JsonObject jsonobject, SetBannerPatternFunction setbannerpatternfunction, JsonSerializationContext jsonserializationcontext) {
            super.a(jsonobject, (LootItemFunctionConditional) setbannerpatternfunction, jsonserializationcontext);
            JsonArray jsonarray = new JsonArray();

            setbannerpatternfunction.patterns.forEach((pair) -> {
                JsonObject jsonobject1 = new JsonObject();

                jsonobject1.addProperty("pattern", ((EnumBannerPatternType) pair.getFirst()).a());
                jsonobject1.addProperty("color", ((EnumColor) pair.getSecond()).b());
                jsonarray.add(jsonobject1);
            });
            jsonobject.add("patterns", jsonarray);
            jsonobject.addProperty("append", setbannerpatternfunction.append);
        }

        @Override
        public SetBannerPatternFunction b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            Builder<Pair<EnumBannerPatternType, EnumColor>> builder = ImmutableList.builder();
            JsonArray jsonarray = ChatDeserializer.u(jsonobject, "patterns");

            for (int i = 0; i < jsonarray.size(); ++i) {
                JsonObject jsonobject1 = ChatDeserializer.m(jsonarray.get(i), "pattern[" + i + "]");
                String s = ChatDeserializer.h(jsonobject1, "pattern");
                EnumBannerPatternType enumbannerpatterntype = EnumBannerPatternType.b(s);

                if (enumbannerpatterntype == null) {
                    throw new JsonSyntaxException("Unknown pattern: " + s);
                }

                String s1 = ChatDeserializer.h(jsonobject1, "color");
                EnumColor enumcolor = EnumColor.a(s1, (EnumColor) null);

                if (enumcolor == null) {
                    throw new JsonSyntaxException("Unknown color: " + s1);
                }

                builder.add(Pair.of(enumbannerpatterntype, enumcolor));
            }

            boolean flag = ChatDeserializer.j(jsonobject, "append");

            return new SetBannerPatternFunction(alootitemcondition, builder.build(), flag);
        }
    }
}
