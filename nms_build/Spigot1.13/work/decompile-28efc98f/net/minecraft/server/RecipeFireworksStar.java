package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class RecipeFireworksStar extends IRecipeComplex {

    private static final RecipeItemStack a = RecipeItemStack.a(new IMaterial[] { Items.FIRE_CHARGE, Items.FEATHER, Items.GOLD_NUGGET, Items.SKELETON_SKULL, Items.WITHER_SKELETON_SKULL, Items.CREEPER_HEAD, Items.PLAYER_HEAD, Items.DRAGON_HEAD, Items.ZOMBIE_HEAD});
    private static final RecipeItemStack b = RecipeItemStack.a(new IMaterial[] { Items.DIAMOND});
    private static final RecipeItemStack c = RecipeItemStack.a(new IMaterial[] { Items.GLOWSTONE_DUST});
    private static final Map<Item, ItemFireworks.EffectType> d = (Map) SystemUtils.a((Object) Maps.newHashMap(), (hashmap) -> {
        hashmap.put(Items.FIRE_CHARGE, ItemFireworks.EffectType.LARGE_BALL);
        hashmap.put(Items.FEATHER, ItemFireworks.EffectType.BURST);
        hashmap.put(Items.GOLD_NUGGET, ItemFireworks.EffectType.STAR);
        hashmap.put(Items.SKELETON_SKULL, ItemFireworks.EffectType.CREEPER);
        hashmap.put(Items.WITHER_SKELETON_SKULL, ItemFireworks.EffectType.CREEPER);
        hashmap.put(Items.CREEPER_HEAD, ItemFireworks.EffectType.CREEPER);
        hashmap.put(Items.PLAYER_HEAD, ItemFireworks.EffectType.CREEPER);
        hashmap.put(Items.DRAGON_HEAD, ItemFireworks.EffectType.CREEPER);
        hashmap.put(Items.ZOMBIE_HEAD, ItemFireworks.EffectType.CREEPER);
    });
    private static final RecipeItemStack e = RecipeItemStack.a(new IMaterial[] { Items.GUNPOWDER});

    public RecipeFireworksStar(MinecraftKey minecraftkey) {
        super(minecraftkey);
    }

    public boolean a(IInventory iinventory, World world) {
        if (!(iinventory instanceof InventoryCrafting)) {
            return false;
        } else {
            boolean flag = false;
            boolean flag1 = false;
            boolean flag2 = false;
            boolean flag3 = false;
            boolean flag4 = false;

            for (int i = 0; i < iinventory.getSize(); ++i) {
                ItemStack itemstack = iinventory.getItem(i);

                if (!itemstack.isEmpty()) {
                    if (RecipeFireworksStar.a.a(itemstack)) {
                        if (flag2) {
                            return false;
                        }

                        flag2 = true;
                    } else if (RecipeFireworksStar.c.a(itemstack)) {
                        if (flag4) {
                            return false;
                        }

                        flag4 = true;
                    } else if (RecipeFireworksStar.b.a(itemstack)) {
                        if (flag3) {
                            return false;
                        }

                        flag3 = true;
                    } else if (RecipeFireworksStar.e.a(itemstack)) {
                        if (flag) {
                            return false;
                        }

                        flag = true;
                    } else {
                        if (!(itemstack.getItem() instanceof ItemDye)) {
                            return false;
                        }

                        flag1 = true;
                    }
                }
            }

            return flag && flag1;
        }
    }

    public ItemStack craftItem(IInventory iinventory) {
        ItemStack itemstack = new ItemStack(Items.FIREWORK_STAR);
        NBTTagCompound nbttagcompound = itemstack.a("Explosion");
        ItemFireworks.EffectType itemfireworks_effecttype = ItemFireworks.EffectType.SMALL_BALL;
        ArrayList arraylist = Lists.newArrayList();

        for (int i = 0; i < iinventory.getSize(); ++i) {
            ItemStack itemstack1 = iinventory.getItem(i);

            if (!itemstack1.isEmpty()) {
                if (RecipeFireworksStar.a.a(itemstack1)) {
                    itemfireworks_effecttype = (ItemFireworks.EffectType) RecipeFireworksStar.d.get(itemstack1.getItem());
                } else if (RecipeFireworksStar.c.a(itemstack1)) {
                    nbttagcompound.setBoolean("Flicker", true);
                } else if (RecipeFireworksStar.b.a(itemstack1)) {
                    nbttagcompound.setBoolean("Trail", true);
                } else if (itemstack1.getItem() instanceof ItemDye) {
                    arraylist.add(Integer.valueOf(((ItemDye) itemstack1.getItem()).d().f()));
                }
            }
        }

        nbttagcompound.b("Colors", (List) arraylist);
        nbttagcompound.setByte("Type", (byte) itemfireworks_effecttype.a());
        return itemstack;
    }

    public ItemStack d() {
        return new ItemStack(Items.FIREWORK_STAR);
    }

    public RecipeSerializer<?> a() {
        return RecipeSerializers.h;
    }
}
