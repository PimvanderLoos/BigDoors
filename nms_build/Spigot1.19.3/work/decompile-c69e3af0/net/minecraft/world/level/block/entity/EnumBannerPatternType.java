package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.EnumColor;

public class EnumBannerPatternType {

    final String hashname;

    public EnumBannerPatternType(String s) {
        this.hashname = s;
    }

    public static MinecraftKey location(ResourceKey<EnumBannerPatternType> resourcekey, boolean flag) {
        String s = flag ? "banner" : "shield";

        return resourcekey.location().withPrefix("entity/" + s + "/");
    }

    public String getHashname() {
        return this.hashname;
    }

    @Nullable
    public static Holder<EnumBannerPatternType> byHash(String s) {
        return (Holder) BuiltInRegistries.BANNER_PATTERN.holders().filter((holder_c) -> {
            return ((EnumBannerPatternType) holder_c.value()).hashname.equals(s);
        }).findAny().orElse((Object) null);
    }

    public static class a {

        private final List<Pair<Holder<EnumBannerPatternType>, EnumColor>> patterns = Lists.newArrayList();

        public a() {}

        public EnumBannerPatternType.a addPattern(ResourceKey<EnumBannerPatternType> resourcekey, EnumColor enumcolor) {
            return this.addPattern((Holder) BuiltInRegistries.BANNER_PATTERN.getHolderOrThrow(resourcekey), enumcolor);
        }

        public EnumBannerPatternType.a addPattern(Holder<EnumBannerPatternType> holder, EnumColor enumcolor) {
            return this.addPattern(Pair.of(holder, enumcolor));
        }

        public EnumBannerPatternType.a addPattern(Pair<Holder<EnumBannerPatternType>, EnumColor> pair) {
            this.patterns.add(pair);
            return this;
        }

        public NBTTagList toListTag() {
            NBTTagList nbttaglist = new NBTTagList();
            Iterator iterator = this.patterns.iterator();

            while (iterator.hasNext()) {
                Pair<Holder<EnumBannerPatternType>, EnumColor> pair = (Pair) iterator.next();
                NBTTagCompound nbttagcompound = new NBTTagCompound();

                nbttagcompound.putString("Pattern", ((EnumBannerPatternType) ((Holder) pair.getFirst()).value()).hashname);
                nbttagcompound.putInt("Color", ((EnumColor) pair.getSecond()).getId());
                nbttaglist.add(nbttagcompound);
            }

            return nbttaglist;
        }
    }
}
