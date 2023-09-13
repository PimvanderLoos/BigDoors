package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class PotionBrewer {

    private static final List<PotionBrewer.PredicatedCombination<PotionRegistry>> a = Lists.newArrayList();
    private static final List<PotionBrewer.PredicatedCombination<Item>> b = Lists.newArrayList();
    private static final List<RecipeItemStack> c = Lists.newArrayList();
    private static final Predicate<ItemStack> d = (itemstack) -> {
        Iterator iterator = PotionBrewer.c.iterator();

        RecipeItemStack recipeitemstack;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            recipeitemstack = (RecipeItemStack) iterator.next();
        } while (!recipeitemstack.test(itemstack));

        return true;
    };

    public static boolean a(ItemStack itemstack) {
        return b(itemstack) || c(itemstack);
    }

    protected static boolean b(ItemStack itemstack) {
        int i = 0;

        for (int j = PotionBrewer.b.size(); i < j; ++i) {
            if (((PotionBrewer.PredicatedCombination) PotionBrewer.b.get(i)).b.test(itemstack)) {
                return true;
            }
        }

        return false;
    }

    protected static boolean c(ItemStack itemstack) {
        int i = 0;

        for (int j = PotionBrewer.a.size(); i < j; ++i) {
            if (((PotionBrewer.PredicatedCombination) PotionBrewer.a.get(i)).b.test(itemstack)) {
                return true;
            }
        }

        return false;
    }

    public static boolean a(ItemStack itemstack, ItemStack itemstack1) {
        return !PotionBrewer.d.test(itemstack) ? false : b(itemstack, itemstack1) || c(itemstack, itemstack1);
    }

    protected static boolean b(ItemStack itemstack, ItemStack itemstack1) {
        Item item = itemstack.getItem();
        int i = 0;

        for (int j = PotionBrewer.b.size(); i < j; ++i) {
            PotionBrewer.PredicatedCombination<Item> potionbrewer_predicatedcombination = (PotionBrewer.PredicatedCombination) PotionBrewer.b.get(i);

            if (potionbrewer_predicatedcombination.a == item && potionbrewer_predicatedcombination.b.test(itemstack1)) {
                return true;
            }
        }

        return false;
    }

    protected static boolean c(ItemStack itemstack, ItemStack itemstack1) {
        PotionRegistry potionregistry = PotionUtil.d(itemstack);
        int i = 0;

        for (int j = PotionBrewer.a.size(); i < j; ++i) {
            PotionBrewer.PredicatedCombination<PotionRegistry> potionbrewer_predicatedcombination = (PotionBrewer.PredicatedCombination) PotionBrewer.a.get(i);

            if (potionbrewer_predicatedcombination.a == potionregistry && potionbrewer_predicatedcombination.b.test(itemstack1)) {
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

            PotionBrewer.PredicatedCombination potionbrewer_predicatedcombination;
            int j;

            for (j = PotionBrewer.b.size(); i < j; ++i) {
                potionbrewer_predicatedcombination = (PotionBrewer.PredicatedCombination) PotionBrewer.b.get(i);
                if (potionbrewer_predicatedcombination.a == item && potionbrewer_predicatedcombination.b.test(itemstack)) {
                    return PotionUtil.a(new ItemStack((IMaterial) potionbrewer_predicatedcombination.c), potionregistry);
                }
            }

            i = 0;

            for (j = PotionBrewer.a.size(); i < j; ++i) {
                potionbrewer_predicatedcombination = (PotionBrewer.PredicatedCombination) PotionBrewer.a.get(i);
                if (potionbrewer_predicatedcombination.a == potionregistry && potionbrewer_predicatedcombination.b.test(itemstack)) {
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
        a(Potions.b, Items.GLISTERING_MELON_SLICE, Potions.c);
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
        a(Potions.r, Items.GLOWSTONE_DUST, Potions.t);
        a(Potions.e, Items.TURTLE_HELMET, Potions.u);
        a(Potions.u, Items.REDSTONE, Potions.v);
        a(Potions.u, Items.GLOWSTONE_DUST, Potions.w);
        a(Potions.o, Items.FERMENTED_SPIDER_EYE, Potions.r);
        a(Potions.p, Items.FERMENTED_SPIDER_EYE, Potions.s);
        a(Potions.e, Items.SUGAR, Potions.o);
        a(Potions.o, Items.REDSTONE, Potions.p);
        a(Potions.o, Items.GLOWSTONE_DUST, Potions.q);
        a(Potions.e, Items.PUFFERFISH, Potions.x);
        a(Potions.x, Items.REDSTONE, Potions.y);
        a(Potions.e, Items.GLISTERING_MELON_SLICE, Potions.z);
        a(Potions.z, Items.GLOWSTONE_DUST, Potions.A);
        a(Potions.z, Items.FERMENTED_SPIDER_EYE, Potions.B);
        a(Potions.A, Items.FERMENTED_SPIDER_EYE, Potions.C);
        a(Potions.B, Items.GLOWSTONE_DUST, Potions.C);
        a(Potions.D, Items.FERMENTED_SPIDER_EYE, Potions.B);
        a(Potions.E, Items.FERMENTED_SPIDER_EYE, Potions.B);
        a(Potions.F, Items.FERMENTED_SPIDER_EYE, Potions.C);
        a(Potions.e, Items.SPIDER_EYE, Potions.D);
        a(Potions.D, Items.REDSTONE, Potions.E);
        a(Potions.D, Items.GLOWSTONE_DUST, Potions.F);
        a(Potions.e, Items.GHAST_TEAR, Potions.G);
        a(Potions.G, Items.REDSTONE, Potions.H);
        a(Potions.G, Items.GLOWSTONE_DUST, Potions.I);
        a(Potions.e, Items.BLAZE_POWDER, Potions.J);
        a(Potions.J, Items.REDSTONE, Potions.K);
        a(Potions.J, Items.GLOWSTONE_DUST, Potions.L);
        a(Potions.b, Items.FERMENTED_SPIDER_EYE, Potions.M);
        a(Potions.M, Items.REDSTONE, Potions.N);
        a(Potions.e, Items.PHANTOM_MEMBRANE, Potions.O);
        a(Potions.O, Items.REDSTONE, Potions.P);
    }

    private static void a(Item item, Item item1, Item item2) {
        if (!(item instanceof ItemPotion)) {
            throw new IllegalArgumentException("Expected a potion, got: " + IRegistry.ITEM.getKey(item));
        } else if (!(item2 instanceof ItemPotion)) {
            throw new IllegalArgumentException("Expected a potion, got: " + IRegistry.ITEM.getKey(item2));
        } else {
            PotionBrewer.b.add(new PotionBrewer.PredicatedCombination<>(item, RecipeItemStack.a(item1), item2));
        }
    }

    private static void a(Item item) {
        if (!(item instanceof ItemPotion)) {
            throw new IllegalArgumentException("Expected a potion, got: " + IRegistry.ITEM.getKey(item));
        } else {
            PotionBrewer.c.add(RecipeItemStack.a(item));
        }
    }

    private static void a(PotionRegistry potionregistry, Item item, PotionRegistry potionregistry1) {
        PotionBrewer.a.add(new PotionBrewer.PredicatedCombination<>(potionregistry, RecipeItemStack.a(item), potionregistry1));
    }

    static class PredicatedCombination<T> {

        private final T a;
        private final RecipeItemStack b;
        private final T c;

        public PredicatedCombination(T t0, RecipeItemStack recipeitemstack, T t1) {
            this.a = t0;
            this.b = recipeitemstack;
            this.c = t1;
        }
    }
}
