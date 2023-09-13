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
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.item.EnumColor;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.EnumBannerPatternType;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetBannerPatternFunction extends LootItemFunctionConditional {

    final List<Pair<Holder<EnumBannerPatternType>, EnumColor>> patterns;
    final boolean append;

    SetBannerPatternFunction(LootItemCondition[] alootitemcondition, List<Pair<Holder<EnumBannerPatternType>, EnumColor>> list, boolean flag) {
        super(alootitemcondition);
        this.patterns = list;
        this.append = flag;
    }

    @Override
    protected ItemStack run(ItemStack itemstack, LootTableInfo loottableinfo) {
        NBTTagCompound nbttagcompound = ItemBlock.getBlockEntityData(itemstack);

        if (nbttagcompound == null) {
            nbttagcompound = new NBTTagCompound();
        }

        EnumBannerPatternType.a enumbannerpatterntype_a = new EnumBannerPatternType.a();
        List list = this.patterns;

        Objects.requireNonNull(enumbannerpatterntype_a);
        list.forEach(enumbannerpatterntype_a::addPattern);
        NBTTagList nbttaglist = enumbannerpatterntype_a.toListTag();
        NBTTagList nbttaglist1;

        if (this.append) {
            nbttaglist1 = nbttagcompound.getList("Patterns", 10).copy();
            nbttaglist1.addAll(nbttaglist);
        } else {
            nbttaglist1 = nbttaglist;
        }

        nbttagcompound.put("Patterns", nbttaglist1);
        ItemBlock.setBlockEntityData(itemstack, TileEntityTypes.BANNER, nbttagcompound);
        return itemstack;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.SET_BANNER_PATTERN;
    }

    public static SetBannerPatternFunction.a setBannerPattern(boolean flag) {
        return new SetBannerPatternFunction.a(flag);
    }

    public static class a extends LootItemFunctionConditional.a<SetBannerPatternFunction.a> {

        private final Builder<Pair<Holder<EnumBannerPatternType>, EnumColor>> patterns = ImmutableList.builder();
        private final boolean append;

        a(boolean flag) {
            this.append = flag;
        }

        @Override
        protected SetBannerPatternFunction.a getThis() {
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new SetBannerPatternFunction(this.getConditions(), this.patterns.build(), this.append);
        }

        public SetBannerPatternFunction.a addPattern(ResourceKey<EnumBannerPatternType> resourcekey, EnumColor enumcolor) {
            return this.addPattern((Holder) BuiltInRegistries.BANNER_PATTERN.getHolderOrThrow(resourcekey), enumcolor);
        }

        public SetBannerPatternFunction.a addPattern(Holder<EnumBannerPatternType> holder, EnumColor enumcolor) {
            this.patterns.add(Pair.of(holder, enumcolor));
            return this;
        }
    }

    public static class b extends LootItemFunctionConditional.c<SetBannerPatternFunction> {

        public b() {}

        public void serialize(JsonObject jsonobject, SetBannerPatternFunction setbannerpatternfunction, JsonSerializationContext jsonserializationcontext) {
            super.serialize(jsonobject, (LootItemFunctionConditional) setbannerpatternfunction, jsonserializationcontext);
            JsonArray jsonarray = new JsonArray();

            setbannerpatternfunction.patterns.forEach((pair) -> {
                JsonObject jsonobject1 = new JsonObject();

                jsonobject1.addProperty("pattern", ((ResourceKey) ((Holder) pair.getFirst()).unwrapKey().orElseThrow(() -> {
                    return new JsonSyntaxException("Unknown pattern: " + pair.getFirst());
                })).location().toString());
                jsonobject1.addProperty("color", ((EnumColor) pair.getSecond()).getName());
                jsonarray.add(jsonobject1);
            });
            jsonobject.add("patterns", jsonarray);
            jsonobject.addProperty("append", setbannerpatternfunction.append);
        }

        @Override
        public SetBannerPatternFunction deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            Builder<Pair<Holder<EnumBannerPatternType>, EnumColor>> builder = ImmutableList.builder();
            JsonArray jsonarray = ChatDeserializer.getAsJsonArray(jsonobject, "patterns");

            for (int i = 0; i < jsonarray.size(); ++i) {
                JsonObject jsonobject1 = ChatDeserializer.convertToJsonObject(jsonarray.get(i), "pattern[" + i + "]");
                String s = ChatDeserializer.getAsString(jsonobject1, "pattern");
                Optional<? extends Holder<EnumBannerPatternType>> optional = BuiltInRegistries.BANNER_PATTERN.getHolder(ResourceKey.create(Registries.BANNER_PATTERN, new MinecraftKey(s)));

                if (optional.isEmpty()) {
                    throw new JsonSyntaxException("Unknown pattern: " + s);
                }

                String s1 = ChatDeserializer.getAsString(jsonobject1, "color");
                EnumColor enumcolor = EnumColor.byName(s1, (EnumColor) null);

                if (enumcolor == null) {
                    throw new JsonSyntaxException("Unknown color: " + s1);
                }

                builder.add(Pair.of((Holder) optional.get(), enumcolor));
            }

            boolean flag = ChatDeserializer.getAsBoolean(jsonobject, "append");

            return new SetBannerPatternFunction(alootitemcondition, builder.build(), flag);
        }
    }
}
