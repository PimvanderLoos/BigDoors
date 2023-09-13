package net.minecraft.server;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;

public class PotionBrewer {

    private static final List<PotionBrewer.PredicatedCombination<PotionRegistry>> a = Lists.newArrayList();
    private static final List<PotionBrewer.PredicatedCombination<Item>> b = Lists.newArrayList();
    private static final List<PotionBrewer.PredicateItem> c = Lists.newArrayList();
    private static final Predicate<ItemStack> d = new Predicate() {
        public boolean a(ItemStack itemstack) {
            Iterator iterator = PotionBrewer.c.iterator();

            PotionBrewer.PredicateItem potionbrewer_predicateitem;

            do {
                if (!iterator.hasNext()) {
                    return false;
                }

                potionbrewer_predicateitem = (PotionBrewer.PredicateItem) iterator.next();
            } while (!potionbrewer_predicateitem.a(itemstack));

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
            if (((PotionBrewer.PredicatedCombination) PotionBrewer.b.get(i)).b.apply(itemstack)) {
                return true;
            }
        }

        return false;
    }

    protected static boolean c(ItemStack itemstack) {
        int i = 0;

        for (int j = PotionBrewer.a.size(); i < j; ++i) {
            if (((PotionBrewer.PredicatedCombination) PotionBrewer.a.get(i)).b.apply(itemstack)) {
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

            if (potionbrewer_predicatedcombination.a == item && potionbrewer_predicatedcombination.b.apply(itemstack1)) {
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

            if (potionbrewer_predicatedcombination.a == potionregistry && potionbrewer_predicatedcombination.b.apply(itemstack1)) {
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
                if (potionbrewer_predicatedcombination.a == item && potionbrewer_predicatedcombination.b.apply(itemstack)) {
                    return PotionUtil.a(new ItemStack((Item) potionbrewer_predicatedcombination.c), potionregistry);
                }
            }

            i = 0;

            for (j = PotionBrewer.a.size(); i < j; ++i) {
                potionbrewer_predicatedcombination = (PotionBrewer.PredicatedCombination) PotionBrewer.a.get(i);
                if (potionbrewer_predicatedcombination.a == potionregistry && potionbrewer_predicatedcombination.b.apply(itemstack)) {
                    return PotionUtil.a(new ItemStack(item), (PotionRegistry) potionbrewer_predicatedcombination.c);
                }
            }
        }

        return itemstack1;
    }

    public static void a() {
        PotionBrewer.PredicateItem potionbrewer_predicateitem = new PotionBrewer.PredicateItem(Items.NETHER_WART);
        PotionBrewer.PredicateItem potionbrewer_predicateitem1 = new PotionBrewer.PredicateItem(Items.GOLDEN_CARROT);
        PotionBrewer.PredicateItem potionbrewer_predicateitem2 = new PotionBrewer.PredicateItem(Items.REDSTONE);
        PotionBrewer.PredicateItem potionbrewer_predicateitem3 = new PotionBrewer.PredicateItem(Items.FERMENTED_SPIDER_EYE);
        PotionBrewer.PredicateItem potionbrewer_predicateitem4 = new PotionBrewer.PredicateItem(Items.RABBIT_FOOT);
        PotionBrewer.PredicateItem potionbrewer_predicateitem5 = new PotionBrewer.PredicateItem(Items.GLOWSTONE_DUST);
        PotionBrewer.PredicateItem potionbrewer_predicateitem6 = new PotionBrewer.PredicateItem(Items.MAGMA_CREAM);
        PotionBrewer.PredicateItem potionbrewer_predicateitem7 = new PotionBrewer.PredicateItem(Items.SUGAR);
        PotionBrewer.PredicateItem potionbrewer_predicateitem8 = new PotionBrewer.PredicateItem(Items.FISH, ItemFish.EnumFish.PUFFERFISH.a());
        PotionBrewer.PredicateItem potionbrewer_predicateitem9 = new PotionBrewer.PredicateItem(Items.SPECKLED_MELON);
        PotionBrewer.PredicateItem potionbrewer_predicateitem10 = new PotionBrewer.PredicateItem(Items.SPIDER_EYE);
        PotionBrewer.PredicateItem potionbrewer_predicateitem11 = new PotionBrewer.PredicateItem(Items.GHAST_TEAR);
        PotionBrewer.PredicateItem potionbrewer_predicateitem12 = new PotionBrewer.PredicateItem(Items.BLAZE_POWDER);

        a(new PotionBrewer.PredicateItem(Items.POTION));
        a(new PotionBrewer.PredicateItem(Items.SPLASH_POTION));
        a(new PotionBrewer.PredicateItem(Items.LINGERING_POTION));
        a(Items.POTION, new PotionBrewer.PredicateItem(Items.GUNPOWDER), Items.SPLASH_POTION);
        a(Items.SPLASH_POTION, new PotionBrewer.PredicateItem(Items.DRAGON_BREATH), Items.LINGERING_POTION);
        a(Potions.b, (Predicate) potionbrewer_predicateitem9, Potions.c);
        a(Potions.b, (Predicate) potionbrewer_predicateitem11, Potions.c);
        a(Potions.b, (Predicate) potionbrewer_predicateitem4, Potions.c);
        a(Potions.b, (Predicate) potionbrewer_predicateitem12, Potions.c);
        a(Potions.b, (Predicate) potionbrewer_predicateitem10, Potions.c);
        a(Potions.b, (Predicate) potionbrewer_predicateitem7, Potions.c);
        a(Potions.b, (Predicate) potionbrewer_predicateitem6, Potions.c);
        a(Potions.b, (Predicate) potionbrewer_predicateitem5, Potions.d);
        a(Potions.b, (Predicate) potionbrewer_predicateitem2, Potions.c);
        a(Potions.b, (Predicate) potionbrewer_predicateitem, Potions.e);
        a(Potions.e, (Predicate) potionbrewer_predicateitem1, Potions.f);
        a(Potions.f, (Predicate) potionbrewer_predicateitem2, Potions.g);
        a(Potions.f, (Predicate) potionbrewer_predicateitem3, Potions.h);
        a(Potions.g, (Predicate) potionbrewer_predicateitem3, Potions.i);
        a(Potions.h, (Predicate) potionbrewer_predicateitem2, Potions.i);
        a(Potions.e, (Predicate) potionbrewer_predicateitem6, Potions.m);
        a(Potions.m, (Predicate) potionbrewer_predicateitem2, Potions.n);
        a(Potions.e, (Predicate) potionbrewer_predicateitem4, Potions.j);
        a(Potions.j, (Predicate) potionbrewer_predicateitem2, Potions.k);
        a(Potions.j, (Predicate) potionbrewer_predicateitem5, Potions.l);
        a(Potions.j, (Predicate) potionbrewer_predicateitem3, Potions.r);
        a(Potions.k, (Predicate) potionbrewer_predicateitem3, Potions.s);
        a(Potions.r, (Predicate) potionbrewer_predicateitem2, Potions.s);
        a(Potions.o, (Predicate) potionbrewer_predicateitem3, Potions.r);
        a(Potions.p, (Predicate) potionbrewer_predicateitem3, Potions.s);
        a(Potions.e, (Predicate) potionbrewer_predicateitem7, Potions.o);
        a(Potions.o, (Predicate) potionbrewer_predicateitem2, Potions.p);
        a(Potions.o, (Predicate) potionbrewer_predicateitem5, Potions.q);
        a(Potions.e, (Predicate) potionbrewer_predicateitem8, Potions.t);
        a(Potions.t, (Predicate) potionbrewer_predicateitem2, Potions.u);
        a(Potions.e, (Predicate) potionbrewer_predicateitem9, Potions.v);
        a(Potions.v, (Predicate) potionbrewer_predicateitem5, Potions.w);
        a(Potions.v, (Predicate) potionbrewer_predicateitem3, Potions.x);
        a(Potions.w, (Predicate) potionbrewer_predicateitem3, Potions.y);
        a(Potions.x, (Predicate) potionbrewer_predicateitem5, Potions.y);
        a(Potions.z, (Predicate) potionbrewer_predicateitem3, Potions.x);
        a(Potions.A, (Predicate) potionbrewer_predicateitem3, Potions.x);
        a(Potions.B, (Predicate) potionbrewer_predicateitem3, Potions.y);
        a(Potions.e, (Predicate) potionbrewer_predicateitem10, Potions.z);
        a(Potions.z, (Predicate) potionbrewer_predicateitem2, Potions.A);
        a(Potions.z, (Predicate) potionbrewer_predicateitem5, Potions.B);
        a(Potions.e, (Predicate) potionbrewer_predicateitem11, Potions.C);
        a(Potions.C, (Predicate) potionbrewer_predicateitem2, Potions.D);
        a(Potions.C, (Predicate) potionbrewer_predicateitem5, Potions.E);
        a(Potions.e, (Predicate) potionbrewer_predicateitem12, Potions.F);
        a(Potions.F, (Predicate) potionbrewer_predicateitem2, Potions.G);
        a(Potions.F, (Predicate) potionbrewer_predicateitem5, Potions.H);
        a(Potions.b, (Predicate) potionbrewer_predicateitem3, Potions.I);
        a(Potions.I, (Predicate) potionbrewer_predicateitem2, Potions.J);
    }

    private static void a(ItemPotion itempotion, PotionBrewer.PredicateItem potionbrewer_predicateitem, ItemPotion itempotion1) {
        PotionBrewer.b.add(new PotionBrewer.PredicatedCombination(itempotion, potionbrewer_predicateitem, itempotion1));
    }

    private static void a(PotionBrewer.PredicateItem potionbrewer_predicateitem) {
        PotionBrewer.c.add(potionbrewer_predicateitem);
    }

    private static void a(PotionRegistry potionregistry, Predicate<ItemStack> predicate, PotionRegistry potionregistry1) {
        PotionBrewer.a.add(new PotionBrewer.PredicatedCombination(potionregistry, predicate, potionregistry1));
    }

    static class PredicateItem implements Predicate<ItemStack> {

        private final Item a;
        private final int b;

        public PredicateItem(Item item) {
            this(item, -1);
        }

        public PredicateItem(Item item, int i) {
            this.a = item;
            this.b = i;
        }

        public boolean a(ItemStack itemstack) {
            return itemstack.getItem() == this.a && (this.b == -1 || this.b == itemstack.getData());
        }

        public boolean apply(Object object) {
            return this.a((ItemStack) object);
        }
    }

    static class PredicatedCombination<T> {

        final T a;
        final Predicate<ItemStack> b;
        final T c;

        public PredicatedCombination(T t0, Predicate<ItemStack> predicate, T t1) {
            this.a = t0;
            this.b = predicate;
            this.c = t1;
        }
    }
}
