package net.minecraft.world.item.alchemy;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.level.IMaterial;

public class PotionBrewer {

    public static final int BREWING_TIME_SECONDS = 20;
    private static final List<PotionBrewer.PredicatedCombination<PotionRegistry>> POTION_MIXES = Lists.newArrayList();
    private static final List<PotionBrewer.PredicatedCombination<Item>> CONTAINER_MIXES = Lists.newArrayList();
    private static final List<RecipeItemStack> ALLOWED_CONTAINERS = Lists.newArrayList();
    private static final Predicate<ItemStack> ALLOWED_CONTAINER = (itemstack) -> {
        Iterator iterator = PotionBrewer.ALLOWED_CONTAINERS.iterator();

        RecipeItemStack recipeitemstack;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            recipeitemstack = (RecipeItemStack) iterator.next();
        } while (!recipeitemstack.test(itemstack));

        return true;
    };

    public PotionBrewer() {}

    public static boolean isIngredient(ItemStack itemstack) {
        return isContainerIngredient(itemstack) || isPotionIngredient(itemstack);
    }

    protected static boolean isContainerIngredient(ItemStack itemstack) {
        int i = 0;

        for (int j = PotionBrewer.CONTAINER_MIXES.size(); i < j; ++i) {
            if (((PotionBrewer.PredicatedCombination) PotionBrewer.CONTAINER_MIXES.get(i)).ingredient.test(itemstack)) {
                return true;
            }
        }

        return false;
    }

    protected static boolean isPotionIngredient(ItemStack itemstack) {
        int i = 0;

        for (int j = PotionBrewer.POTION_MIXES.size(); i < j; ++i) {
            if (((PotionBrewer.PredicatedCombination) PotionBrewer.POTION_MIXES.get(i)).ingredient.test(itemstack)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isBrewablePotion(PotionRegistry potionregistry) {
        int i = 0;

        for (int j = PotionBrewer.POTION_MIXES.size(); i < j; ++i) {
            if (((PotionBrewer.PredicatedCombination) PotionBrewer.POTION_MIXES.get(i)).to == potionregistry) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasMix(ItemStack itemstack, ItemStack itemstack1) {
        return !PotionBrewer.ALLOWED_CONTAINER.test(itemstack) ? false : hasContainerMix(itemstack, itemstack1) || hasPotionMix(itemstack, itemstack1);
    }

    protected static boolean hasContainerMix(ItemStack itemstack, ItemStack itemstack1) {
        Item item = itemstack.getItem();
        int i = 0;

        for (int j = PotionBrewer.CONTAINER_MIXES.size(); i < j; ++i) {
            PotionBrewer.PredicatedCombination<Item> potionbrewer_predicatedcombination = (PotionBrewer.PredicatedCombination) PotionBrewer.CONTAINER_MIXES.get(i);

            if (potionbrewer_predicatedcombination.from == item && potionbrewer_predicatedcombination.ingredient.test(itemstack1)) {
                return true;
            }
        }

        return false;
    }

    protected static boolean hasPotionMix(ItemStack itemstack, ItemStack itemstack1) {
        PotionRegistry potionregistry = PotionUtil.getPotion(itemstack);
        int i = 0;

        for (int j = PotionBrewer.POTION_MIXES.size(); i < j; ++i) {
            PotionBrewer.PredicatedCombination<PotionRegistry> potionbrewer_predicatedcombination = (PotionBrewer.PredicatedCombination) PotionBrewer.POTION_MIXES.get(i);

            if (potionbrewer_predicatedcombination.from == potionregistry && potionbrewer_predicatedcombination.ingredient.test(itemstack1)) {
                return true;
            }
        }

        return false;
    }

    public static ItemStack mix(ItemStack itemstack, ItemStack itemstack1) {
        if (!itemstack1.isEmpty()) {
            PotionRegistry potionregistry = PotionUtil.getPotion(itemstack1);
            Item item = itemstack1.getItem();
            int i = 0;

            PotionBrewer.PredicatedCombination potionbrewer_predicatedcombination;
            int j;

            for (j = PotionBrewer.CONTAINER_MIXES.size(); i < j; ++i) {
                potionbrewer_predicatedcombination = (PotionBrewer.PredicatedCombination) PotionBrewer.CONTAINER_MIXES.get(i);
                if (potionbrewer_predicatedcombination.from == item && potionbrewer_predicatedcombination.ingredient.test(itemstack)) {
                    return PotionUtil.setPotion(new ItemStack((IMaterial) potionbrewer_predicatedcombination.to), potionregistry);
                }
            }

            i = 0;

            for (j = PotionBrewer.POTION_MIXES.size(); i < j; ++i) {
                potionbrewer_predicatedcombination = (PotionBrewer.PredicatedCombination) PotionBrewer.POTION_MIXES.get(i);
                if (potionbrewer_predicatedcombination.from == potionregistry && potionbrewer_predicatedcombination.ingredient.test(itemstack)) {
                    return PotionUtil.setPotion(new ItemStack(item), (PotionRegistry) potionbrewer_predicatedcombination.to);
                }
            }
        }

        return itemstack1;
    }

    public static void bootStrap() {
        addContainer(Items.POTION);
        addContainer(Items.SPLASH_POTION);
        addContainer(Items.LINGERING_POTION);
        addContainerRecipe(Items.POTION, Items.GUNPOWDER, Items.SPLASH_POTION);
        addContainerRecipe(Items.SPLASH_POTION, Items.DRAGON_BREATH, Items.LINGERING_POTION);
        addMix(Potions.WATER, Items.GLISTERING_MELON_SLICE, Potions.MUNDANE);
        addMix(Potions.WATER, Items.GHAST_TEAR, Potions.MUNDANE);
        addMix(Potions.WATER, Items.RABBIT_FOOT, Potions.MUNDANE);
        addMix(Potions.WATER, Items.BLAZE_POWDER, Potions.MUNDANE);
        addMix(Potions.WATER, Items.SPIDER_EYE, Potions.MUNDANE);
        addMix(Potions.WATER, Items.SUGAR, Potions.MUNDANE);
        addMix(Potions.WATER, Items.MAGMA_CREAM, Potions.MUNDANE);
        addMix(Potions.WATER, Items.GLOWSTONE_DUST, Potions.THICK);
        addMix(Potions.WATER, Items.REDSTONE, Potions.MUNDANE);
        addMix(Potions.WATER, Items.NETHER_WART, Potions.AWKWARD);
        addMix(Potions.AWKWARD, Items.GOLDEN_CARROT, Potions.NIGHT_VISION);
        addMix(Potions.NIGHT_VISION, Items.REDSTONE, Potions.LONG_NIGHT_VISION);
        addMix(Potions.NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, Potions.INVISIBILITY);
        addMix(Potions.LONG_NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, Potions.LONG_INVISIBILITY);
        addMix(Potions.INVISIBILITY, Items.REDSTONE, Potions.LONG_INVISIBILITY);
        addMix(Potions.AWKWARD, Items.MAGMA_CREAM, Potions.FIRE_RESISTANCE);
        addMix(Potions.FIRE_RESISTANCE, Items.REDSTONE, Potions.LONG_FIRE_RESISTANCE);
        addMix(Potions.AWKWARD, Items.RABBIT_FOOT, Potions.LEAPING);
        addMix(Potions.LEAPING, Items.REDSTONE, Potions.LONG_LEAPING);
        addMix(Potions.LEAPING, Items.GLOWSTONE_DUST, Potions.STRONG_LEAPING);
        addMix(Potions.LEAPING, Items.FERMENTED_SPIDER_EYE, Potions.SLOWNESS);
        addMix(Potions.LONG_LEAPING, Items.FERMENTED_SPIDER_EYE, Potions.LONG_SLOWNESS);
        addMix(Potions.SLOWNESS, Items.REDSTONE, Potions.LONG_SLOWNESS);
        addMix(Potions.SLOWNESS, Items.GLOWSTONE_DUST, Potions.STRONG_SLOWNESS);
        addMix(Potions.AWKWARD, Items.TURTLE_HELMET, Potions.TURTLE_MASTER);
        addMix(Potions.TURTLE_MASTER, Items.REDSTONE, Potions.LONG_TURTLE_MASTER);
        addMix(Potions.TURTLE_MASTER, Items.GLOWSTONE_DUST, Potions.STRONG_TURTLE_MASTER);
        addMix(Potions.SWIFTNESS, Items.FERMENTED_SPIDER_EYE, Potions.SLOWNESS);
        addMix(Potions.LONG_SWIFTNESS, Items.FERMENTED_SPIDER_EYE, Potions.LONG_SLOWNESS);
        addMix(Potions.AWKWARD, Items.SUGAR, Potions.SWIFTNESS);
        addMix(Potions.SWIFTNESS, Items.REDSTONE, Potions.LONG_SWIFTNESS);
        addMix(Potions.SWIFTNESS, Items.GLOWSTONE_DUST, Potions.STRONG_SWIFTNESS);
        addMix(Potions.AWKWARD, Items.PUFFERFISH, Potions.WATER_BREATHING);
        addMix(Potions.WATER_BREATHING, Items.REDSTONE, Potions.LONG_WATER_BREATHING);
        addMix(Potions.AWKWARD, Items.GLISTERING_MELON_SLICE, Potions.HEALING);
        addMix(Potions.HEALING, Items.GLOWSTONE_DUST, Potions.STRONG_HEALING);
        addMix(Potions.HEALING, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
        addMix(Potions.STRONG_HEALING, Items.FERMENTED_SPIDER_EYE, Potions.STRONG_HARMING);
        addMix(Potions.HARMING, Items.GLOWSTONE_DUST, Potions.STRONG_HARMING);
        addMix(Potions.POISON, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
        addMix(Potions.LONG_POISON, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
        addMix(Potions.STRONG_POISON, Items.FERMENTED_SPIDER_EYE, Potions.STRONG_HARMING);
        addMix(Potions.AWKWARD, Items.SPIDER_EYE, Potions.POISON);
        addMix(Potions.POISON, Items.REDSTONE, Potions.LONG_POISON);
        addMix(Potions.POISON, Items.GLOWSTONE_DUST, Potions.STRONG_POISON);
        addMix(Potions.AWKWARD, Items.GHAST_TEAR, Potions.REGENERATION);
        addMix(Potions.REGENERATION, Items.REDSTONE, Potions.LONG_REGENERATION);
        addMix(Potions.REGENERATION, Items.GLOWSTONE_DUST, Potions.STRONG_REGENERATION);
        addMix(Potions.AWKWARD, Items.BLAZE_POWDER, Potions.STRENGTH);
        addMix(Potions.STRENGTH, Items.REDSTONE, Potions.LONG_STRENGTH);
        addMix(Potions.STRENGTH, Items.GLOWSTONE_DUST, Potions.STRONG_STRENGTH);
        addMix(Potions.WATER, Items.FERMENTED_SPIDER_EYE, Potions.WEAKNESS);
        addMix(Potions.WEAKNESS, Items.REDSTONE, Potions.LONG_WEAKNESS);
        addMix(Potions.AWKWARD, Items.PHANTOM_MEMBRANE, Potions.SLOW_FALLING);
        addMix(Potions.SLOW_FALLING, Items.REDSTONE, Potions.LONG_SLOW_FALLING);
    }

    private static void addContainerRecipe(Item item, Item item1, Item item2) {
        if (!(item instanceof ItemPotion)) {
            throw new IllegalArgumentException("Expected a potion, got: " + BuiltInRegistries.ITEM.getKey(item));
        } else if (!(item2 instanceof ItemPotion)) {
            throw new IllegalArgumentException("Expected a potion, got: " + BuiltInRegistries.ITEM.getKey(item2));
        } else {
            PotionBrewer.CONTAINER_MIXES.add(new PotionBrewer.PredicatedCombination<>(item, RecipeItemStack.of(item1), item2));
        }
    }

    private static void addContainer(Item item) {
        if (!(item instanceof ItemPotion)) {
            throw new IllegalArgumentException("Expected a potion, got: " + BuiltInRegistries.ITEM.getKey(item));
        } else {
            PotionBrewer.ALLOWED_CONTAINERS.add(RecipeItemStack.of(item));
        }
    }

    private static void addMix(PotionRegistry potionregistry, Item item, PotionRegistry potionregistry1) {
        PotionBrewer.POTION_MIXES.add(new PotionBrewer.PredicatedCombination<>(potionregistry, RecipeItemStack.of(item), potionregistry1));
    }

    private static class PredicatedCombination<T> {

        final T from;
        final RecipeItemStack ingredient;
        final T to;

        public PredicatedCombination(T t0, RecipeItemStack recipeitemstack, T t1) {
            this.from = t0;
            this.ingredient = recipeitemstack;
            this.to = t1;
        }
    }
}
