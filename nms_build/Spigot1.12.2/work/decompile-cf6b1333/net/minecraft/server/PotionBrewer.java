package net.minecraft.server;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;

public class PotionBrewer {

    private static final List<PotionBrewer.PredicatedCombination<PotionRegistry>> a = Lists.newArrayList();
    private static final List<PotionBrewer.PredicatedCombination<Item>> b = Lists.newArrayList();
    private static final List<RecipeItemStack> c = Lists.newArrayList();
    private static final Predicate<ItemStack> d = new Predicate() {
        public boolean a(ItemStack itemstack) {
            Iterator iterator = PotionBrewer.c.iterator();

            RecipeItemStack recipeitemstack;

            do {
                if (!iterator.hasNext()) {
                    return false;
                }

                recipeitemstack = (RecipeItemStack) iterator.next();
            } while (!recipeitemstack.a(itemstack));

            return true;
        }

        public boolean apply(Object object) {
            return this.a((ItemStack) object);
        }
    };

    public static boolean a(ItemStack itemstack) {
        return b(itemstack) || c(itemstack);
    }

    protected static boolean b(ItemStack itemstack) {
        int i = 0;

        for (int j = PotionBrewer.b.size(); i < j; ++i) {
            if (((PotionBrewer.PredicatedCombination) PotionBrewer.b.get(i)).b.a(itemstack)) {
                return true;
            }
        }

        return false;
    }

    protected static boolean c(ItemStack itemstack) {
        int i = 0;

        for (int j = PotionBrewer.a.size(); i < j; ++i) {
            if (((PotionBrewer.PredicatedCombination) PotionBrewer.a.get(i)).b.a(itemstack)) {
                return true;
            }
        }

        return false;
    }

    public static boolean a(ItemStack itemstack, ItemStack itemstack1) {
        return !PotionBrewer.d.apply(itemstack) ? false : b(itemstack, itemstack1) || c(itemstack, itemstack1);
    }

    protected static boolean b(ItemStack itemstack, ItemStack itemstack1) {
        Item item = itemstack.getItem();
        int i = 0;

        for (int j = PotionBrewer.b.size(); i < j; ++i) {
            PotionBrewer.PredicatedCombination potionbrewer_predicatedcombination = (PotionBrewer.PredicatedCombination) PotionBrewer.b.get(i);

            if (potionbrewer_predicatedcombination.a == item && potionbrewer_predicatedcombination.b.a(itemstack1)) {
                return true;
            }
        }

        return false;
    }

    protected static boolean c(ItemStack itemstack, ItemStack itemstack1) {
        PotionRegistry potionregistry = PotionUtil.d(itemstack);
        int i = 0;

        for (int j = PotionBrewer.a.size(); i < j; ++i) {
            PotionBrewer.PredicatedCombination potionbrewer_predicatedcombination = (PotionBrewer.PredicatedCombination) PotionBrewer.a.get(i);

            if (potionbrewer_predicatedcombination.a == potionregistry && potionbrewer_predicatedcombination.b.a(itemstack1)) {
                return true;
            }
        }

        return false;
    }

    public static ItemStack d(ItemStack itemstack, ItemStack itemstack1) {
        if (!itemstack1.isEmpty()) {
            PotionRegistry potionregistry = PotionUtil.d(itemstack1);
            Item item = itemstack1.getItem();
            int i = 0;

            int j;
            PotionBrewer.PredicatedCombination potionbrewer_predicatedcombination;

            for (j = PotionBrewer.b.size(); i < j; ++i) {
                potionbrewer_predicatedcombination = (PotionBrewer.PredicatedCombination) PotionBrewer.b.get(i);
                if (potionbrewer_predicatedcombination.a == item && potionbrewer_predicatedcombination.b.a(itemstack)) {
                    return PotionUtil.a(new ItemStack((Item) potionbrewer_predicatedcombination.c), potionregistry);
                }
            }

            i = 0;

            for (j = PotionBrewer.a.size(); i < j; ++i) {
                potionbrewer_predicatedcombination = (PotionBrewer.PredicatedCombination) PotionBrewer.a.get(i);
                if (potionbrewer_predicatedcombination.a == potionregistry && potionbrewer_predicatedcombination.b.a(itemstack)) {
                    return PotionUtil.a(new ItemStack(item), (PotionRegistry) potionbrewer_predicatedcombination.c);
                }
            }
        }

        return itemstack1;
    }

    public static void a() {
        a(Items.POTION);
        a(Items.SPLASH_POTION);
        a(Items.LINGERING_POTION);
        a(Items.POTION, Items.GUNPOWDER, Items.SPLASH_POTION);
        a(Items.SPLASH_POTION, Items.DRAGON_BREATH, Items.LINGERING_POTION);
        a(Potions.b, Items.SPECKLED_MELON, Potions.c);
        a(Potions.b, Items.GHAST_TEAR, Potions.c);
        a(Potions.b, Items.RABBIT_FOOT, Potions.c);
        a(Potions.b, Items.BLAZE_POWDER, Potions.c);
        a(Potions.b, Items.SPIDER_EYE, Potions.c);
        a(Potions.b, Items.SUGAR, Potions.c);
        a(Potions.b, Items.MAGMA_CREAM, Potions.c);
        a(Potions.b, Items.GLOWSTONE_DUST, Potions.d);
        a(Potions.b, Items.REDSTONE, Potions.c);
        a(Potions.b, Items.NETHER_WART, Potions.e);
        a(Potions.e, Items.GOLDEN_CARROT, Potions.f);
        a(Potions.f, Items.REDSTONE, Potions.g);
        a(Potions.f, Items.FERMENTED_SPIDER_EYE, Potions.h);
        a(Potions.g, Items.FERMENTED_SPIDER_EYE, Potions.i);
        a(Potions.h, Items.REDSTONE, Potions.i);
        a(Potions.e, Items.MAGMA_CREAM, Potions.m);
        a(Potions.m, Items.REDSTONE, Potions.n);
        a(Potions.e, Items.RABBIT_FOOT, Potions.j);
        a(Potions.j, Items.REDSTONE, Potions.k);
        a(Potions.j, Items.GLOWSTONE_DUST, Potions.l);
        a(Potions.j, Items.FERMENTED_SPIDER_EYE, Potions.r);
        a(Potions.k, Items.FERMENTED_SPIDER_EYE, Potions.s);
        a(Potions.r, Items.REDSTONE, Potions.s);
        a(Potions.o, Items.FERMENTED_SPIDER_EYE, Potions.r);
        a(Potions.p, Items.FERMENTED_SPIDER_EYE, Potions.s);
        a(Potions.e, Items.SUGAR, Potions.o);
        a(Potions.o, Items.REDSTONE, Potions.p);
        a(Potions.o, Items.GLOWSTONE_DUST, Potions.q);
        a(Potions.e, RecipeItemStack.a(new ItemStack[] { new ItemStack(Items.FISH, 1, ItemFish.EnumFish.PUFFERFISH.a())}), Potions.t);
        a(Potions.t, Items.REDSTONE, Potions.u);
        a(Potions.e, Items.SPECKLED_MELON, Potions.v);
        a(Potions.v, Items.GLOWSTONE_DUST, Potions.w);
        a(Potions.v, Items.FERMENTED_SPIDER_EYE, Potions.x);
        a(Potions.w, Items.FERMENTED_SPIDER_EYE, Potions.y);
        a(Potions.x, Items.GLOWSTONE_DUST, Potions.y);
        a(Potions.z, Items.FERMENTED_SPIDER_EYE, Potions.x);
        a(Potions.A, Items.FERMENTED_SPIDER_EYE, Potions.x);
        a(Potions.B, Items.FERMENTED_SPIDER_EYE, Potions.y);
        a(Potions.e, Items.SPIDER_EYE, Potions.z);
        a(Potions.z, Items.REDSTONE, Potions.A);
        a(Potions.z, Items.GLOWSTONE_DUST, Potions.B);
        a(Potions.e, Items.GHAST_TEAR, Potions.C);
        a(Potions.C, Items.REDSTONE, Potions.D);
        a(Potions.C, Items.GLOWSTONE_DUST, Potions.E);
        a(Potions.e, Items.BLAZE_POWDER, Potions.F);
        a(Potions.F, Items.REDSTONE, Potions.G);
        a(Potions.F, Items.GLOWSTONE_DUST, Potions.H);
        a(Potions.b, Items.FERMENTED_SPIDER_EYE, Potions.I);
        a(Potions.I, Items.REDSTONE, Potions.J);
    }

    private static void a(ItemPotion itempotion, Item item, ItemPotion itempotion1) {
        PotionBrewer.b.add(new PotionBrewer.PredicatedCombination(itempotion, RecipeItemStack.a(new Item[] { item}), itempotion1));
    }

    private static void a(ItemPotion itempotion) {
        PotionBrewer.c.add(RecipeItemStack.a(new Item[] { itempotion}));
    }

    private static void a(PotionRegistry potionregistry, Item item, PotionRegistry potionregistry1) {
        a(potionregistry, RecipeItemStack.a(new Item[] { item}), potionregistry1);
    }

    private static void a(PotionRegistry potionregistry, RecipeItemStack recipeitemstack, PotionRegistry potionregistry1) {
        PotionBrewer.a.add(new PotionBrewer.PredicatedCombination(potionregistry, recipeitemstack, potionregistry1));
    }

    static class PredicatedCombination<T> {

        final T a;
        final RecipeItemStack b;
        final T c;

        public PredicatedCombination(T t0, RecipeItemStack recipeitemstack, T t1) {
            this.a = t0;
            this.b = recipeitemstack;
            this.c = t1;
        }
    }
}
