package net.minecraft.world.item;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.animal.EntitySheep;
import net.minecraft.world.entity.player.EntityHuman;

public class ItemDye extends Item {

    private static final Map<EnumColor, ItemDye> ITEM_BY_COLOR = Maps.newEnumMap(EnumColor.class);
    private final EnumColor dyeColor;

    public ItemDye(EnumColor enumcolor, Item.Info item_info) {
        super(item_info);
        this.dyeColor = enumcolor;
        ItemDye.ITEM_BY_COLOR.put(enumcolor, this);
    }

    @Override
    public EnumInteractionResult a(ItemStack itemstack, EntityHuman entityhuman, EntityLiving entityliving, EnumHand enumhand) {
        if (entityliving instanceof EntitySheep) {
            EntitySheep entitysheep = (EntitySheep) entityliving;

            if (entitysheep.isAlive() && !entitysheep.isSheared() && entitysheep.getColor() != this.dyeColor) {
                entitysheep.level.playSound(entityhuman, (Entity) entitysheep, SoundEffects.DYE_USE, SoundCategory.PLAYERS, 1.0F, 1.0F);
                if (!entityhuman.level.isClientSide) {
                    entitysheep.setColor(this.dyeColor);
                    itemstack.subtract(1);
                }

                return EnumInteractionResult.a(entityhuman.level.isClientSide);
            }
        }

        return EnumInteractionResult.PASS;
    }

    public EnumColor d() {
        return this.dyeColor;
    }

    public static ItemDye a(EnumColor enumcolor) {
        return (ItemDye) ItemDye.ITEM_BY_COLOR.get(enumcolor);
    }
}
