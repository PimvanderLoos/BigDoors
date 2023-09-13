package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class RecipesFurnace {

    private static final RecipesFurnace a = new RecipesFurnace();
    public final Map<ItemStack, ItemStack> recipes = Maps.newHashMap();
    private final Map<ItemStack, Float> experience = Maps.newHashMap();

    public static RecipesFurnace getInstance() {
        return RecipesFurnace.a;
    }

    public RecipesFurnace() {
        this.registerRecipe(Blocks.IRON_ORE, new ItemStack(Items.IRON_INGOT), 0.7F);
        this.registerRecipe(Blocks.GOLD_ORE, new ItemStack(Items.GOLD_INGOT), 1.0F);
        this.registerRecipe(Blocks.DIAMOND_ORE, new ItemStack(Items.DIAMOND), 1.0F);
        this.registerRecipe(Blocks.SAND, new ItemStack(Blocks.GLASS), 0.1F);
        this.a(Items.PORKCHOP, new ItemStack(Items.COOKED_PORKCHOP), 0.35F);
        this.a(Items.BEEF, new ItemStack(Items.COOKED_BEEF), 0.35F);
        this.a(Items.CHICKEN, new ItemStack(Items.COOKED_CHICKEN), 0.35F);
        this.a(Items.RABBIT, new ItemStack(Items.COOKED_RABBIT), 0.35F);
        this.a(Items.MUTTON, new ItemStack(Items.COOKED_MUTTON), 0.35F);
        this.registerRecipe(Blocks.COBBLESTONE, new ItemStack(Blocks.STONE), 0.1F);
        this.a(new ItemStack(Blocks.STONEBRICK, 1, BlockSmoothBrick.b), new ItemStack(Blocks.STONEBRICK, 1, BlockSmoothBrick.d), 0.1F);
        this.a(Items.CLAY_BALL, new ItemStack(Items.BRICK), 0.3F);
        this.registerRecipe(Blocks.CLAY, new ItemStack(Blocks.HARDENED_CLAY), 0.35F);
        this.registerRecipe(Blocks.CACTUS, new ItemStack(Items.DYE, 1, EnumColor.GREEN.getInvColorIndex()), 0.2F);
        this.registerRecipe(Blocks.LOG, new ItemStack(Items.COAL, 1, 1), 0.15F);
        this.registerRecipe(Blocks.LOG2, new ItemStack(Items.COAL, 1, 1), 0.15F);
        this.registerRecipe(Blocks.EMERALD_ORE, new ItemStack(Items.EMERALD), 1.0F);
        this.a(Items.POTATO, new ItemStack(Items.BAKED_POTATO), 0.35F);
        this.registerRecipe(Blocks.NETHERRACK, new ItemStack(Items.NETHERBRICK), 0.1F);
        this.a(new ItemStack(Blocks.SPONGE, 1, 1), new ItemStack(Blocks.SPONGE, 1, 0), 0.15F);
        this.a(Items.CHORUS_FRUIT, new ItemStack(Items.CHORUS_FRUIT_POPPED), 0.1F);
        ItemFish.EnumFish[] aitemfish_enumfish = ItemFish.EnumFish.values();
        int i = aitemfish_enumfish.length;

        for (int j = 0; j < i; ++j) {
            ItemFish.EnumFish itemfish_enumfish = aitemfish_enumfish[j];

            if (itemfish_enumfish.g()) {
                this.a(new ItemStack(Items.FISH, 1, itemfish_enumfish.a()), new ItemStack(Items.COOKED_FISH, 1, itemfish_enumfish.a()), 0.35F);
            }
        }

        this.registerRecipe(Blocks.COAL_ORE, new ItemStack(Items.COAL), 0.1F);
        this.registerRecipe(Blocks.REDSTONE_ORE, new ItemStack(Items.REDSTONE), 0.7F);
        this.registerRecipe(Blocks.LAPIS_ORE, new ItemStack(Items.DYE, 1, EnumColor.BLUE.getInvColorIndex()), 0.2F);
        this.registerRecipe(Blocks.QUARTZ_ORE, new ItemStack(Items.QUARTZ), 0.2F);
        this.a((Item) Items.CHAINMAIL_HELMET, new ItemStack(Items.da), 0.1F);
        this.a((Item) Items.CHAINMAIL_CHESTPLATE, new ItemStack(Items.da), 0.1F);
        this.a((Item) Items.CHAINMAIL_LEGGINGS, new ItemStack(Items.da), 0.1F);
        this.a((Item) Items.CHAINMAIL_BOOTS, new ItemStack(Items.da), 0.1F);
        this.a(Items.IRON_PICKAXE, new ItemStack(Items.da), 0.1F);
        this.a(Items.IRON_SHOVEL, new ItemStack(Items.da), 0.1F);
        this.a(Items.IRON_AXE, new ItemStack(Items.da), 0.1F);
        this.a(Items.IRON_HOE, new ItemStack(Items.da), 0.1F);
        this.a(Items.IRON_SWORD, new ItemStack(Items.da), 0.1F);
        this.a((Item) Items.IRON_HELMET, new ItemStack(Items.da), 0.1F);
        this.a((Item) Items.IRON_CHESTPLATE, new ItemStack(Items.da), 0.1F);
        this.a((Item) Items.IRON_LEGGINGS, new ItemStack(Items.da), 0.1F);
        this.a((Item) Items.IRON_BOOTS, new ItemStack(Items.da), 0.1F);
        this.a(Items.IRON_HORSE_ARMOR, new ItemStack(Items.da), 0.1F);
        this.a(Items.GOLDEN_PICKAXE, new ItemStack(Items.GOLD_NUGGET), 0.1F);
        this.a(Items.GOLDEN_SHOVEL, new ItemStack(Items.GOLD_NUGGET), 0.1F);
        this.a(Items.GOLDEN_AXE, new ItemStack(Items.GOLD_NUGGET), 0.1F);
        this.a(Items.GOLDEN_HOE, new ItemStack(Items.GOLD_NUGGET), 0.1F);
        this.a(Items.GOLDEN_SWORD, new ItemStack(Items.GOLD_NUGGET), 0.1F);
        this.a((Item) Items.GOLDEN_HELMET, new ItemStack(Items.GOLD_NUGGET), 0.1F);
        this.a((Item) Items.GOLDEN_CHESTPLATE, new ItemStack(Items.GOLD_NUGGET), 0.1F);
        this.a((Item) Items.GOLDEN_LEGGINGS, new ItemStack(Items.GOLD_NUGGET), 0.1F);
        this.a((Item) Items.GOLDEN_BOOTS, new ItemStack(Items.GOLD_NUGGET), 0.1F);
        this.a(Items.GOLDEN_HORSE_ARMOR, new ItemStack(Items.GOLD_NUGGET), 0.1F);
        this.a(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, EnumColor.WHITE.getColorIndex()), new ItemStack(Blocks.dB), 0.1F);
        this.a(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, EnumColor.ORANGE.getColorIndex()), new ItemStack(Blocks.dC), 0.1F);
        this.a(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, EnumColor.MAGENTA.getColorIndex()), new ItemStack(Blocks.dD), 0.1F);
        this.a(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, EnumColor.LIGHT_BLUE.getColorIndex()), new ItemStack(Blocks.dE), 0.1F);
        this.a(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, EnumColor.YELLOW.getColorIndex()), new ItemStack(Blocks.dF), 0.1F);
        this.a(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, EnumColor.LIME.getColorIndex()), new ItemStack(Blocks.dG), 0.1F);
        this.a(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, EnumColor.PINK.getColorIndex()), new ItemStack(Blocks.dH), 0.1F);
        this.a(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, EnumColor.GRAY.getColorIndex()), new ItemStack(Blocks.dI), 0.1F);
        this.a(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, EnumColor.SILVER.getColorIndex()), new ItemStack(Blocks.dJ), 0.1F);
        this.a(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, EnumColor.CYAN.getColorIndex()), new ItemStack(Blocks.dK), 0.1F);
        this.a(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, EnumColor.PURPLE.getColorIndex()), new ItemStack(Blocks.dL), 0.1F);
        this.a(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, EnumColor.BLUE.getColorIndex()), new ItemStack(Blocks.dM), 0.1F);
        this.a(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, EnumColor.BROWN.getColorIndex()), new ItemStack(Blocks.dN), 0.1F);
        this.a(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, EnumColor.GREEN.getColorIndex()), new ItemStack(Blocks.dO), 0.1F);
        this.a(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, EnumColor.RED.getColorIndex()), new ItemStack(Blocks.dP), 0.1F);
        this.a(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, EnumColor.BLACK.getColorIndex()), new ItemStack(Blocks.dQ), 0.1F);
    }

    public void registerRecipe(Block block, ItemStack itemstack, float f) {
        this.a(Item.getItemOf(block), itemstack, f);
    }

    public void a(Item item, ItemStack itemstack, float f) {
        this.a(new ItemStack(item, 1, 32767), itemstack, f);
    }

    public void a(ItemStack itemstack, ItemStack itemstack1, float f) {
        this.recipes.put(itemstack, itemstack1);
        this.experience.put(itemstack1, Float.valueOf(f));
    }

    public ItemStack getResult(ItemStack itemstack) {
        Iterator iterator = this.recipes.entrySet().iterator();

        Entry entry;

        do {
            if (!iterator.hasNext()) {
                return ItemStack.a;
            }

            entry = (Entry) iterator.next();
        } while (!this.a(itemstack, (ItemStack) entry.getKey()));

        return (ItemStack) entry.getValue();
    }

    private boolean a(ItemStack itemstack, ItemStack itemstack1) {
        return itemstack1.getItem() == itemstack.getItem() && (itemstack1.getData() == 32767 || itemstack1.getData() == itemstack.getData());
    }

    public Map<ItemStack, ItemStack> getRecipes() {
        return this.recipes;
    }

    public float b(ItemStack itemstack) {
        Iterator iterator = this.experience.entrySet().iterator();

        Entry entry;

        do {
            if (!iterator.hasNext()) {
                return 0.0F;
            }

            entry = (Entry) iterator.next();
        } while (!this.a(itemstack, (ItemStack) entry.getKey()));

        return ((Float) entry.getValue()).floatValue();
    }
}
