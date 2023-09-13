package net.minecraft.world.level.block;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.level.IMaterial;

public interface SuspiciousEffectHolder {

    MobEffectList getSuspiciousEffect();

    int getEffectDuration();

    static List<SuspiciousEffectHolder> getAllEffectHolders() {
        return (List) BuiltInRegistries.ITEM.stream().map(SuspiciousEffectHolder::tryGet).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Nullable
    static SuspiciousEffectHolder tryGet(IMaterial imaterial) {
        Item item = imaterial.asItem();

        if (item instanceof ItemBlock) {
            ItemBlock itemblock = (ItemBlock) item;
            Block block = itemblock.getBlock();

            if (block instanceof SuspiciousEffectHolder) {
                SuspiciousEffectHolder suspiciouseffectholder = (SuspiciousEffectHolder) block;

                return suspiciouseffectholder;
            }
        }

        Item item1 = imaterial.asItem();

        if (item1 instanceof SuspiciousEffectHolder) {
            SuspiciousEffectHolder suspiciouseffectholder1 = (SuspiciousEffectHolder) item1;

            return suspiciouseffectholder1;
        } else {
            return null;
        }
    }
}
