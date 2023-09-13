package net.minecraft.core.cauldron;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.IDyeable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemLiquidUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtil;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockShulkerBox;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.TileEntityBanner;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.IBlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public interface CauldronInteraction {

    Map<Item, CauldronInteraction> EMPTY = a();
    Map<Item, CauldronInteraction> WATER = a();
    Map<Item, CauldronInteraction> LAVA = a();
    Map<Item, CauldronInteraction> POWDER_SNOW = a();
    CauldronInteraction FILL_WATER = (iblockdata, world, blockposition, entityhuman, enumhand, itemstack) -> {
        return a(world, blockposition, entityhuman, enumhand, itemstack, (IBlockData) Blocks.WATER_CAULDRON.getBlockData().set(LayeredCauldronBlock.LEVEL, 3), SoundEffects.BUCKET_EMPTY);
    };
    CauldronInteraction FILL_LAVA = (iblockdata, world, blockposition, entityhuman, enumhand, itemstack) -> {
        return a(world, blockposition, entityhuman, enumhand, itemstack, Blocks.LAVA_CAULDRON.getBlockData(), SoundEffects.BUCKET_EMPTY_LAVA);
    };
    CauldronInteraction FILL_POWDER_SNOW = (iblockdata, world, blockposition, entityhuman, enumhand, itemstack) -> {
        return a(world, blockposition, entityhuman, enumhand, itemstack, (IBlockData) Blocks.POWDER_SNOW_CAULDRON.getBlockData().set(LayeredCauldronBlock.LEVEL, 3), SoundEffects.BUCKET_EMPTY_POWDER_SNOW);
    };
    CauldronInteraction SHULKER_BOX = (iblockdata, world, blockposition, entityhuman, enumhand, itemstack) -> {
        Block block = Block.asBlock(itemstack.getItem());

        if (!(block instanceof BlockShulkerBox)) {
            return EnumInteractionResult.PASS;
        } else {
            if (!world.isClientSide) {
                ItemStack itemstack1 = new ItemStack(Blocks.SHULKER_BOX);

                if (itemstack.hasTag()) {
                    itemstack1.setTag(itemstack.getTag().clone());
                }

                entityhuman.a(enumhand, itemstack1);
                entityhuman.a(StatisticList.CLEAN_SHULKER_BOX);
                LayeredCauldronBlock.e(iblockdata, world, blockposition);
            }

            return EnumInteractionResult.a(world.isClientSide);
        }
    };
    CauldronInteraction BANNER = (iblockdata, world, blockposition, entityhuman, enumhand, itemstack) -> {
        if (TileEntityBanner.b(itemstack) <= 0) {
            return EnumInteractionResult.PASS;
        } else {
            if (!world.isClientSide) {
                ItemStack itemstack1 = itemstack.cloneItemStack();

                itemstack1.setCount(1);
                TileEntityBanner.c(itemstack1);
                if (!entityhuman.getAbilities().instabuild) {
                    itemstack.subtract(1);
                }

                if (itemstack.isEmpty()) {
                    entityhuman.a(enumhand, itemstack1);
                } else if (entityhuman.getInventory().pickup(itemstack1)) {
                    entityhuman.inventoryMenu.updateInventory();
                } else {
                    entityhuman.drop(itemstack1, false);
                }

                entityhuman.a(StatisticList.CLEAN_BANNER);
                LayeredCauldronBlock.e(iblockdata, world, blockposition);
            }

            return EnumInteractionResult.a(world.isClientSide);
        }
    };
    CauldronInteraction DYED_ITEM = (iblockdata, world, blockposition, entityhuman, enumhand, itemstack) -> {
        Item item = itemstack.getItem();

        if (!(item instanceof IDyeable)) {
            return EnumInteractionResult.PASS;
        } else {
            IDyeable idyeable = (IDyeable) item;

            if (!idyeable.c_(itemstack)) {
                return EnumInteractionResult.PASS;
            } else {
                if (!world.isClientSide) {
                    idyeable.e_(itemstack);
                    entityhuman.a(StatisticList.CLEAN_ARMOR);
                    LayeredCauldronBlock.e(iblockdata, world, blockposition);
                }

                return EnumInteractionResult.a(world.isClientSide);
            }
        }
    };

    static Object2ObjectOpenHashMap<Item, CauldronInteraction> a() {
        return (Object2ObjectOpenHashMap) SystemUtils.a((Object) (new Object2ObjectOpenHashMap()), (object2objectopenhashmap) -> {
            object2objectopenhashmap.defaultReturnValue((iblockdata, world, blockposition, entityhuman, enumhand, itemstack) -> {
                return EnumInteractionResult.PASS;
            });
        });
    }

    EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, ItemStack itemstack);

    static void b() {
        a(CauldronInteraction.EMPTY);
        CauldronInteraction.EMPTY.put(Items.POTION, (iblockdata, world, blockposition, entityhuman, enumhand, itemstack) -> {
            if (PotionUtil.d(itemstack) != Potions.WATER) {
                return EnumInteractionResult.PASS;
            } else {
                if (!world.isClientSide) {
                    Item item = itemstack.getItem();

                    entityhuman.a(enumhand, ItemLiquidUtil.a(itemstack, entityhuman, new ItemStack(Items.GLASS_BOTTLE)));
                    entityhuman.a(StatisticList.USE_CAULDRON);
                    entityhuman.b(StatisticList.ITEM_USED.b(item));
                    world.setTypeUpdate(blockposition, Blocks.WATER_CAULDRON.getBlockData());
                    world.playSound((EntityHuman) null, blockposition, SoundEffects.BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    world.a((Entity) null, GameEvent.FLUID_PLACE, blockposition);
                }

                return EnumInteractionResult.a(world.isClientSide);
            }
        });
        a(CauldronInteraction.WATER);
        CauldronInteraction.WATER.put(Items.BUCKET, (iblockdata, world, blockposition, entityhuman, enumhand, itemstack) -> {
            return a(iblockdata, world, blockposition, entityhuman, enumhand, itemstack, new ItemStack(Items.WATER_BUCKET), (iblockdata1) -> {
                return (Integer) iblockdata1.get(LayeredCauldronBlock.LEVEL) == 3;
            }, SoundEffects.BUCKET_FILL);
        });
        CauldronInteraction.WATER.put(Items.GLASS_BOTTLE, (iblockdata, world, blockposition, entityhuman, enumhand, itemstack) -> {
            if (!world.isClientSide) {
                Item item = itemstack.getItem();

                entityhuman.a(enumhand, ItemLiquidUtil.a(itemstack, entityhuman, PotionUtil.a(new ItemStack(Items.POTION), Potions.WATER)));
                entityhuman.a(StatisticList.USE_CAULDRON);
                entityhuman.b(StatisticList.ITEM_USED.b(item));
                LayeredCauldronBlock.e(iblockdata, world, blockposition);
                world.playSound((EntityHuman) null, blockposition, SoundEffects.BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.a((Entity) null, GameEvent.FLUID_PICKUP, blockposition);
            }

            return EnumInteractionResult.a(world.isClientSide);
        });
        CauldronInteraction.WATER.put(Items.POTION, (iblockdata, world, blockposition, entityhuman, enumhand, itemstack) -> {
            if ((Integer) iblockdata.get(LayeredCauldronBlock.LEVEL) != 3 && PotionUtil.d(itemstack) == Potions.WATER) {
                if (!world.isClientSide) {
                    entityhuman.a(enumhand, ItemLiquidUtil.a(itemstack, entityhuman, new ItemStack(Items.GLASS_BOTTLE)));
                    entityhuman.a(StatisticList.USE_CAULDRON);
                    entityhuman.b(StatisticList.ITEM_USED.b(itemstack.getItem()));
                    world.setTypeUpdate(blockposition, (IBlockData) iblockdata.a((IBlockState) LayeredCauldronBlock.LEVEL));
                    world.playSound((EntityHuman) null, blockposition, SoundEffects.BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    world.a((Entity) null, GameEvent.FLUID_PLACE, blockposition);
                }

                return EnumInteractionResult.a(world.isClientSide);
            } else {
                return EnumInteractionResult.PASS;
            }
        });
        CauldronInteraction.WATER.put(Items.LEATHER_BOOTS, CauldronInteraction.DYED_ITEM);
        CauldronInteraction.WATER.put(Items.LEATHER_LEGGINGS, CauldronInteraction.DYED_ITEM);
        CauldronInteraction.WATER.put(Items.LEATHER_CHESTPLATE, CauldronInteraction.DYED_ITEM);
        CauldronInteraction.WATER.put(Items.LEATHER_HELMET, CauldronInteraction.DYED_ITEM);
        CauldronInteraction.WATER.put(Items.LEATHER_HORSE_ARMOR, CauldronInteraction.DYED_ITEM);
        CauldronInteraction.WATER.put(Items.WHITE_BANNER, CauldronInteraction.BANNER);
        CauldronInteraction.WATER.put(Items.GRAY_BANNER, CauldronInteraction.BANNER);
        CauldronInteraction.WATER.put(Items.BLACK_BANNER, CauldronInteraction.BANNER);
        CauldronInteraction.WATER.put(Items.BLUE_BANNER, CauldronInteraction.BANNER);
        CauldronInteraction.WATER.put(Items.BROWN_BANNER, CauldronInteraction.BANNER);
        CauldronInteraction.WATER.put(Items.CYAN_BANNER, CauldronInteraction.BANNER);
        CauldronInteraction.WATER.put(Items.GREEN_BANNER, CauldronInteraction.BANNER);
        CauldronInteraction.WATER.put(Items.LIGHT_BLUE_BANNER, CauldronInteraction.BANNER);
        CauldronInteraction.WATER.put(Items.LIGHT_GRAY_BANNER, CauldronInteraction.BANNER);
        CauldronInteraction.WATER.put(Items.LIME_BANNER, CauldronInteraction.BANNER);
        CauldronInteraction.WATER.put(Items.MAGENTA_BANNER, CauldronInteraction.BANNER);
        CauldronInteraction.WATER.put(Items.ORANGE_BANNER, CauldronInteraction.BANNER);
        CauldronInteraction.WATER.put(Items.PINK_BANNER, CauldronInteraction.BANNER);
        CauldronInteraction.WATER.put(Items.PURPLE_BANNER, CauldronInteraction.BANNER);
        CauldronInteraction.WATER.put(Items.RED_BANNER, CauldronInteraction.BANNER);
        CauldronInteraction.WATER.put(Items.YELLOW_BANNER, CauldronInteraction.BANNER);
        CauldronInteraction.WATER.put(Items.WHITE_SHULKER_BOX, CauldronInteraction.SHULKER_BOX);
        CauldronInteraction.WATER.put(Items.GRAY_SHULKER_BOX, CauldronInteraction.SHULKER_BOX);
        CauldronInteraction.WATER.put(Items.BLACK_SHULKER_BOX, CauldronInteraction.SHULKER_BOX);
        CauldronInteraction.WATER.put(Items.BLUE_SHULKER_BOX, CauldronInteraction.SHULKER_BOX);
        CauldronInteraction.WATER.put(Items.BROWN_SHULKER_BOX, CauldronInteraction.SHULKER_BOX);
        CauldronInteraction.WATER.put(Items.CYAN_SHULKER_BOX, CauldronInteraction.SHULKER_BOX);
        CauldronInteraction.WATER.put(Items.GREEN_SHULKER_BOX, CauldronInteraction.SHULKER_BOX);
        CauldronInteraction.WATER.put(Items.LIGHT_BLUE_SHULKER_BOX, CauldronInteraction.SHULKER_BOX);
        CauldronInteraction.WATER.put(Items.LIGHT_GRAY_SHULKER_BOX, CauldronInteraction.SHULKER_BOX);
        CauldronInteraction.WATER.put(Items.LIME_SHULKER_BOX, CauldronInteraction.SHULKER_BOX);
        CauldronInteraction.WATER.put(Items.MAGENTA_SHULKER_BOX, CauldronInteraction.SHULKER_BOX);
        CauldronInteraction.WATER.put(Items.ORANGE_SHULKER_BOX, CauldronInteraction.SHULKER_BOX);
        CauldronInteraction.WATER.put(Items.PINK_SHULKER_BOX, CauldronInteraction.SHULKER_BOX);
        CauldronInteraction.WATER.put(Items.PURPLE_SHULKER_BOX, CauldronInteraction.SHULKER_BOX);
        CauldronInteraction.WATER.put(Items.RED_SHULKER_BOX, CauldronInteraction.SHULKER_BOX);
        CauldronInteraction.WATER.put(Items.YELLOW_SHULKER_BOX, CauldronInteraction.SHULKER_BOX);
        CauldronInteraction.LAVA.put(Items.BUCKET, (iblockdata, world, blockposition, entityhuman, enumhand, itemstack) -> {
            return a(iblockdata, world, blockposition, entityhuman, enumhand, itemstack, new ItemStack(Items.LAVA_BUCKET), (iblockdata1) -> {
                return true;
            }, SoundEffects.BUCKET_FILL_LAVA);
        });
        a(CauldronInteraction.LAVA);
        CauldronInteraction.POWDER_SNOW.put(Items.BUCKET, (iblockdata, world, blockposition, entityhuman, enumhand, itemstack) -> {
            return a(iblockdata, world, blockposition, entityhuman, enumhand, itemstack, new ItemStack(Items.POWDER_SNOW_BUCKET), (iblockdata1) -> {
                return (Integer) iblockdata1.get(LayeredCauldronBlock.LEVEL) == 3;
            }, SoundEffects.BUCKET_FILL_POWDER_SNOW);
        });
        a(CauldronInteraction.POWDER_SNOW);
    }

    static void a(Map<Item, CauldronInteraction> map) {
        map.put(Items.LAVA_BUCKET, CauldronInteraction.FILL_LAVA);
        map.put(Items.WATER_BUCKET, CauldronInteraction.FILL_WATER);
        map.put(Items.POWDER_SNOW_BUCKET, CauldronInteraction.FILL_POWDER_SNOW);
    }

    static EnumInteractionResult a(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, ItemStack itemstack, ItemStack itemstack1, Predicate<IBlockData> predicate, SoundEffect soundeffect) {
        if (!predicate.test(iblockdata)) {
            return EnumInteractionResult.PASS;
        } else {
            if (!world.isClientSide) {
                Item item = itemstack.getItem();

                entityhuman.a(enumhand, ItemLiquidUtil.a(itemstack, entityhuman, itemstack1));
                entityhuman.a(StatisticList.USE_CAULDRON);
                entityhuman.b(StatisticList.ITEM_USED.b(item));
                world.setTypeUpdate(blockposition, Blocks.CAULDRON.getBlockData());
                world.playSound((EntityHuman) null, blockposition, soundeffect, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.a((Entity) null, GameEvent.FLUID_PICKUP, blockposition);
            }

            return EnumInteractionResult.a(world.isClientSide);
        }
    }

    static EnumInteractionResult a(World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, ItemStack itemstack, IBlockData iblockdata, SoundEffect soundeffect) {
        if (!world.isClientSide) {
            Item item = itemstack.getItem();

            entityhuman.a(enumhand, ItemLiquidUtil.a(itemstack, entityhuman, new ItemStack(Items.BUCKET)));
            entityhuman.a(StatisticList.FILL_CAULDRON);
            entityhuman.b(StatisticList.ITEM_USED.b(item));
            world.setTypeUpdate(blockposition, iblockdata);
            world.playSound((EntityHuman) null, blockposition, soundeffect, SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.a((Entity) null, GameEvent.FLUID_PLACE, blockposition);
        }

        return EnumInteractionResult.a(world.isClientSide);
    }
}
