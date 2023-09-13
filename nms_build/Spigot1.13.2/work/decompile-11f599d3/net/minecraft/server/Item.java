package net.minecraft.server;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;

public class Item implements IMaterial {

    public static final Map<Block, Item> f = Maps.newHashMap();
    private static final IDynamicTexture a = (itemstack, world, entityliving) -> {
        return itemstack.f() ? 1.0F : 0.0F;
    };
    private static final IDynamicTexture b = (itemstack, world, entityliving) -> {
        return MathHelper.a((float) itemstack.getDamage() / (float) itemstack.h(), 0.0F, 1.0F);
    };
    private static final IDynamicTexture c = (itemstack, world, entityliving) -> {
        return entityliving != null && entityliving.getMainHand() != EnumMainHand.RIGHT ? 1.0F : 0.0F;
    };
    private static final IDynamicTexture d = (itemstack, world, entityliving) -> {
        return entityliving instanceof EntityHuman ? ((EntityHuman) entityliving).getCooldownTracker().a(itemstack.getItem(), 0.0F) : 0.0F;
    };
    protected static final UUID g = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    protected static final UUID h = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
    protected static Random i = new Random();
    private final Map<MinecraftKey, IDynamicTexture> e = Maps.newHashMap();
    protected final CreativeModeTab j;
    private final EnumItemRarity k;
    private final int maxStackSize;
    private final int durability;
    private final Item craftingResult;
    @Nullable
    private String name;

    public static int getId(Item item) {
        return item == null ? 0 : IRegistry.ITEM.a((Object) item);
    }

    public static Item getById(int i) {
        return (Item) IRegistry.ITEM.fromId(i);
    }

    @Deprecated
    public static Item getItemOf(Block block) {
        Item item = (Item) Item.f.get(block);

        return item == null ? Items.AIR : item;
    }

    public Item(Item.Info item_info) {
        this.a(new MinecraftKey("lefthanded"), Item.c);
        this.a(new MinecraftKey("cooldown"), Item.d);
        this.j = item_info.d;
        this.k = item_info.e;
        this.craftingResult = item_info.c;
        this.durability = item_info.b;
        this.maxStackSize = item_info.a;
        if (this.durability > 0) {
            this.a(new MinecraftKey("damaged"), Item.a);
            this.a(new MinecraftKey("damage"), Item.b);
        }

    }

    public boolean a(NBTTagCompound nbttagcompound) {
        return false;
    }

    public boolean a(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman) {
        return true;
    }

    public Item getItem() {
        return this;
    }

    public final void a(MinecraftKey minecraftkey, IDynamicTexture idynamictexture) {
        this.e.put(minecraftkey, idynamictexture);
    }

    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        return EnumInteractionResult.PASS;
    }

    public float getDestroySpeed(ItemStack itemstack, IBlockData iblockdata) {
        return 1.0F;
    }

    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        return new InteractionResultWrapper<>(EnumInteractionResult.PASS, entityhuman.b(enumhand));
    }

    public ItemStack a(ItemStack itemstack, World world, EntityLiving entityliving) {
        return itemstack;
    }

    public final int getMaxStackSize() {
        return this.maxStackSize;
    }

    public final int getMaxDurability() {
        return this.durability;
    }

    public boolean usesDurability() {
        return this.durability > 0;
    }

    public boolean a(ItemStack itemstack, EntityLiving entityliving, EntityLiving entityliving1) {
        return false;
    }

    public boolean a(ItemStack itemstack, World world, IBlockData iblockdata, BlockPosition blockposition, EntityLiving entityliving) {
        return false;
    }

    public boolean canDestroySpecialBlock(IBlockData iblockdata) {
        return false;
    }

    public boolean a(ItemStack itemstack, EntityHuman entityhuman, EntityLiving entityliving, EnumHand enumhand) {
        return false;
    }

    protected String m() {
        if (this.name == null) {
            this.name = SystemUtils.a("item", IRegistry.ITEM.getKey(this));
        }

        return this.name;
    }

    public String getName() {
        return this.m();
    }

    public String h(ItemStack itemstack) {
        return this.getName();
    }

    public boolean n() {
        return true;
    }

    @Nullable
    public final Item o() {
        return this.craftingResult;
    }

    public boolean p() {
        return this.craftingResult != null;
    }

    public void a(ItemStack itemstack, World world, Entity entity, int i, boolean flag) {}

    public void b(ItemStack itemstack, World world, EntityHuman entityhuman) {}

    public boolean W_() {
        return false;
    }

    public EnumAnimation d(ItemStack itemstack) {
        return EnumAnimation.NONE;
    }

    public int c(ItemStack itemstack) {
        return 0;
    }

    public void a(ItemStack itemstack, World world, EntityLiving entityliving, int i) {}

    public IChatBaseComponent i(ItemStack itemstack) {
        return new ChatMessage(this.h(itemstack), new Object[0]);
    }

    public EnumItemRarity j(ItemStack itemstack) {
        if (!itemstack.hasEnchantments()) {
            return this.k;
        } else {
            switch (this.k) {
            case COMMON:
            case UNCOMMON:
                return EnumItemRarity.RARE;
            case RARE:
                return EnumItemRarity.EPIC;
            case EPIC:
            default:
                return this.k;
            }
        }
    }

    public boolean a(ItemStack itemstack) {
        return this.getMaxStackSize() == 1 && this.usesDurability();
    }

    @Nullable
    protected MovingObjectPosition a(World world, EntityHuman entityhuman, boolean flag) {
        float f = entityhuman.pitch;
        float f1 = entityhuman.yaw;
        double d0 = entityhuman.locX;
        double d1 = entityhuman.locY + (double) entityhuman.getHeadHeight();
        double d2 = entityhuman.locZ;
        Vec3D vec3d = new Vec3D(d0, d1, d2);
        float f2 = MathHelper.cos(-f1 * 0.017453292F - 3.1415927F);
        float f3 = MathHelper.sin(-f1 * 0.017453292F - 3.1415927F);
        float f4 = -MathHelper.cos(-f * 0.017453292F);
        float f5 = MathHelper.sin(-f * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d3 = 5.0D;
        Vec3D vec3d1 = vec3d.add((double) f6 * 5.0D, (double) f5 * 5.0D, (double) f7 * 5.0D);

        return world.rayTrace(vec3d, vec3d1, flag ? FluidCollisionOption.SOURCE_ONLY : FluidCollisionOption.NEVER, false, false);
    }

    public int c() {
        return 0;
    }

    public void a(CreativeModeTab creativemodetab, NonNullList<ItemStack> nonnulllist) {
        if (this.a(creativemodetab)) {
            nonnulllist.add(new ItemStack(this));
        }

    }

    protected boolean a(CreativeModeTab creativemodetab) {
        CreativeModeTab creativemodetab1 = this.q();

        return creativemodetab1 != null && (creativemodetab == CreativeModeTab.g || creativemodetab == creativemodetab1);
    }

    @Nullable
    public final CreativeModeTab q() {
        return this.j;
    }

    public boolean a(ItemStack itemstack, ItemStack itemstack1) {
        return false;
    }

    public Multimap<String, AttributeModifier> a(EnumItemSlot enumitemslot) {
        return HashMultimap.create();
    }

    public static void r() {
        a(Blocks.AIR, (Item) (new ItemAir(Blocks.AIR, new Item.Info())));
        a(Blocks.STONE, CreativeModeTab.b);
        a(Blocks.GRANITE, CreativeModeTab.b);
        a(Blocks.POLISHED_GRANITE, CreativeModeTab.b);
        a(Blocks.DIORITE, CreativeModeTab.b);
        a(Blocks.POLISHED_DIORITE, CreativeModeTab.b);
        a(Blocks.ANDESITE, CreativeModeTab.b);
        a(Blocks.POLISHED_ANDESITE, CreativeModeTab.b);
        a(Blocks.GRASS_BLOCK, CreativeModeTab.b);
        a(Blocks.DIRT, CreativeModeTab.b);
        a(Blocks.COARSE_DIRT, CreativeModeTab.b);
        a(Blocks.PODZOL, CreativeModeTab.b);
        a(Blocks.COBBLESTONE, CreativeModeTab.b);
        a(Blocks.OAK_PLANKS, CreativeModeTab.b);
        a(Blocks.SPRUCE_PLANKS, CreativeModeTab.b);
        a(Blocks.BIRCH_PLANKS, CreativeModeTab.b);
        a(Blocks.JUNGLE_PLANKS, CreativeModeTab.b);
        a(Blocks.ACACIA_PLANKS, CreativeModeTab.b);
        a(Blocks.DARK_OAK_PLANKS, CreativeModeTab.b);
        a(Blocks.OAK_SAPLING, CreativeModeTab.c);
        a(Blocks.SPRUCE_SAPLING, CreativeModeTab.c);
        a(Blocks.BIRCH_SAPLING, CreativeModeTab.c);
        a(Blocks.JUNGLE_SAPLING, CreativeModeTab.c);
        a(Blocks.ACACIA_SAPLING, CreativeModeTab.c);
        a(Blocks.DARK_OAK_SAPLING, CreativeModeTab.c);
        a(Blocks.BEDROCK, CreativeModeTab.b);
        a(Blocks.SAND, CreativeModeTab.b);
        a(Blocks.RED_SAND, CreativeModeTab.b);
        a(Blocks.GRAVEL, CreativeModeTab.b);
        a(Blocks.GOLD_ORE, CreativeModeTab.b);
        a(Blocks.IRON_ORE, CreativeModeTab.b);
        a(Blocks.COAL_ORE, CreativeModeTab.b);
        a(Blocks.OAK_LOG, CreativeModeTab.b);
        a(Blocks.SPRUCE_LOG, CreativeModeTab.b);
        a(Blocks.BIRCH_LOG, CreativeModeTab.b);
        a(Blocks.JUNGLE_LOG, CreativeModeTab.b);
        a(Blocks.ACACIA_LOG, CreativeModeTab.b);
        a(Blocks.DARK_OAK_LOG, CreativeModeTab.b);
        a(Blocks.STRIPPED_OAK_LOG, CreativeModeTab.b);
        a(Blocks.STRIPPED_SPRUCE_LOG, CreativeModeTab.b);
        a(Blocks.STRIPPED_BIRCH_LOG, CreativeModeTab.b);
        a(Blocks.STRIPPED_JUNGLE_LOG, CreativeModeTab.b);
        a(Blocks.STRIPPED_ACACIA_LOG, CreativeModeTab.b);
        a(Blocks.STRIPPED_DARK_OAK_LOG, CreativeModeTab.b);
        a(Blocks.STRIPPED_OAK_WOOD, CreativeModeTab.b);
        a(Blocks.STRIPPED_SPRUCE_WOOD, CreativeModeTab.b);
        a(Blocks.STRIPPED_BIRCH_WOOD, CreativeModeTab.b);
        a(Blocks.STRIPPED_JUNGLE_WOOD, CreativeModeTab.b);
        a(Blocks.STRIPPED_ACACIA_WOOD, CreativeModeTab.b);
        a(Blocks.STRIPPED_DARK_OAK_WOOD, CreativeModeTab.b);
        a(Blocks.OAK_WOOD, CreativeModeTab.b);
        a(Blocks.SPRUCE_WOOD, CreativeModeTab.b);
        a(Blocks.BIRCH_WOOD, CreativeModeTab.b);
        a(Blocks.JUNGLE_WOOD, CreativeModeTab.b);
        a(Blocks.ACACIA_WOOD, CreativeModeTab.b);
        a(Blocks.DARK_OAK_WOOD, CreativeModeTab.b);
        a(Blocks.OAK_LEAVES, CreativeModeTab.c);
        a(Blocks.SPRUCE_LEAVES, CreativeModeTab.c);
        a(Blocks.BIRCH_LEAVES, CreativeModeTab.c);
        a(Blocks.JUNGLE_LEAVES, CreativeModeTab.c);
        a(Blocks.ACACIA_LEAVES, CreativeModeTab.c);
        a(Blocks.DARK_OAK_LEAVES, CreativeModeTab.c);
        a(Blocks.SPONGE, CreativeModeTab.b);
        a(Blocks.WET_SPONGE, CreativeModeTab.b);
        a(Blocks.GLASS, CreativeModeTab.b);
        a(Blocks.LAPIS_ORE, CreativeModeTab.b);
        a(Blocks.LAPIS_BLOCK, CreativeModeTab.b);
        a(Blocks.DISPENSER, CreativeModeTab.d);
        a(Blocks.SANDSTONE, CreativeModeTab.b);
        a(Blocks.CHISELED_SANDSTONE, CreativeModeTab.b);
        a(Blocks.CUT_SANDSTONE, CreativeModeTab.b);
        a(Blocks.NOTE_BLOCK, CreativeModeTab.d);
        a(Blocks.POWERED_RAIL, CreativeModeTab.e);
        a(Blocks.DETECTOR_RAIL, CreativeModeTab.e);
        a(Blocks.STICKY_PISTON, CreativeModeTab.d);
        a(Blocks.COBWEB, CreativeModeTab.c);
        a(Blocks.GRASS, CreativeModeTab.c);
        a(Blocks.FERN, CreativeModeTab.c);
        a(Blocks.DEAD_BUSH, CreativeModeTab.c);
        a(Blocks.SEAGRASS, CreativeModeTab.c);
        a(Blocks.SEA_PICKLE, CreativeModeTab.c);
        a(Blocks.PISTON, CreativeModeTab.d);
        a(Blocks.WHITE_WOOL, CreativeModeTab.b);
        a(Blocks.ORANGE_WOOL, CreativeModeTab.b);
        a(Blocks.MAGENTA_WOOL, CreativeModeTab.b);
        a(Blocks.LIGHT_BLUE_WOOL, CreativeModeTab.b);
        a(Blocks.YELLOW_WOOL, CreativeModeTab.b);
        a(Blocks.LIME_WOOL, CreativeModeTab.b);
        a(Blocks.PINK_WOOL, CreativeModeTab.b);
        a(Blocks.GRAY_WOOL, CreativeModeTab.b);
        a(Blocks.LIGHT_GRAY_WOOL, CreativeModeTab.b);
        a(Blocks.CYAN_WOOL, CreativeModeTab.b);
        a(Blocks.PURPLE_WOOL, CreativeModeTab.b);
        a(Blocks.BLUE_WOOL, CreativeModeTab.b);
        a(Blocks.BROWN_WOOL, CreativeModeTab.b);
        a(Blocks.GREEN_WOOL, CreativeModeTab.b);
        a(Blocks.RED_WOOL, CreativeModeTab.b);
        a(Blocks.BLACK_WOOL, CreativeModeTab.b);
        a(Blocks.DANDELION, CreativeModeTab.c);
        a(Blocks.POPPY, CreativeModeTab.c);
        a(Blocks.BLUE_ORCHID, CreativeModeTab.c);
        a(Blocks.ALLIUM, CreativeModeTab.c);
        a(Blocks.AZURE_BLUET, CreativeModeTab.c);
        a(Blocks.RED_TULIP, CreativeModeTab.c);
        a(Blocks.ORANGE_TULIP, CreativeModeTab.c);
        a(Blocks.WHITE_TULIP, CreativeModeTab.c);
        a(Blocks.PINK_TULIP, CreativeModeTab.c);
        a(Blocks.OXEYE_DAISY, CreativeModeTab.c);
        a(Blocks.BROWN_MUSHROOM, CreativeModeTab.c);
        a(Blocks.RED_MUSHROOM, CreativeModeTab.c);
        a(Blocks.GOLD_BLOCK, CreativeModeTab.b);
        a(Blocks.IRON_BLOCK, CreativeModeTab.b);
        a(Blocks.OAK_SLAB, CreativeModeTab.b);
        a(Blocks.SPRUCE_SLAB, CreativeModeTab.b);
        a(Blocks.BIRCH_SLAB, CreativeModeTab.b);
        a(Blocks.JUNGLE_SLAB, CreativeModeTab.b);
        a(Blocks.ACACIA_SLAB, CreativeModeTab.b);
        a(Blocks.DARK_OAK_SLAB, CreativeModeTab.b);
        a(Blocks.STONE_SLAB, CreativeModeTab.b);
        a(Blocks.SANDSTONE_SLAB, CreativeModeTab.b);
        a(Blocks.PETRIFIED_OAK_SLAB, CreativeModeTab.b);
        a(Blocks.COBBLESTONE_SLAB, CreativeModeTab.b);
        a(Blocks.BRICK_SLAB, CreativeModeTab.b);
        a(Blocks.STONE_BRICK_SLAB, CreativeModeTab.b);
        a(Blocks.NETHER_BRICK_SLAB, CreativeModeTab.b);
        a(Blocks.QUARTZ_SLAB, CreativeModeTab.b);
        a(Blocks.RED_SANDSTONE_SLAB, CreativeModeTab.b);
        a(Blocks.PURPUR_SLAB, CreativeModeTab.b);
        a(Blocks.PRISMARINE_SLAB, CreativeModeTab.b);
        a(Blocks.PRISMARINE_BRICK_SLAB, CreativeModeTab.b);
        a(Blocks.DARK_PRISMARINE_SLAB, CreativeModeTab.b);
        a(Blocks.SMOOTH_QUARTZ, CreativeModeTab.b);
        a(Blocks.SMOOTH_RED_SANDSTONE, CreativeModeTab.b);
        a(Blocks.SMOOTH_SANDSTONE, CreativeModeTab.b);
        a(Blocks.SMOOTH_STONE, CreativeModeTab.b);
        a(Blocks.BRICKS, CreativeModeTab.b);
        a(Blocks.TNT, CreativeModeTab.d);
        a(Blocks.BOOKSHELF, CreativeModeTab.b);
        a(Blocks.MOSSY_COBBLESTONE, CreativeModeTab.b);
        a(Blocks.OBSIDIAN, CreativeModeTab.b);
        a((ItemBlock) (new ItemBlockWallable(Blocks.TORCH, Blocks.WALL_TORCH, (new Item.Info()).a(CreativeModeTab.c))));
        a(Blocks.END_ROD, CreativeModeTab.c);
        a(Blocks.CHORUS_PLANT, CreativeModeTab.c);
        a(Blocks.CHORUS_FLOWER, CreativeModeTab.c);
        a(Blocks.PURPUR_BLOCK, CreativeModeTab.b);
        a(Blocks.PURPUR_PILLAR, CreativeModeTab.b);
        a(Blocks.PURPUR_STAIRS, CreativeModeTab.b);
        b(Blocks.SPAWNER);
        a(Blocks.OAK_STAIRS, CreativeModeTab.b);
        a(Blocks.CHEST, CreativeModeTab.c);
        a(Blocks.DIAMOND_ORE, CreativeModeTab.b);
        a(Blocks.DIAMOND_BLOCK, CreativeModeTab.b);
        a(Blocks.CRAFTING_TABLE, CreativeModeTab.c);
        a(Blocks.FARMLAND, CreativeModeTab.c);
        a(Blocks.FURNACE, CreativeModeTab.c);
        a(Blocks.LADDER, CreativeModeTab.c);
        a(Blocks.RAIL, CreativeModeTab.e);
        a(Blocks.COBBLESTONE_STAIRS, CreativeModeTab.b);
        a(Blocks.LEVER, CreativeModeTab.d);
        a(Blocks.STONE_PRESSURE_PLATE, CreativeModeTab.d);
        a(Blocks.OAK_PRESSURE_PLATE, CreativeModeTab.d);
        a(Blocks.SPRUCE_PRESSURE_PLATE, CreativeModeTab.d);
        a(Blocks.BIRCH_PRESSURE_PLATE, CreativeModeTab.d);
        a(Blocks.JUNGLE_PRESSURE_PLATE, CreativeModeTab.d);
        a(Blocks.ACACIA_PRESSURE_PLATE, CreativeModeTab.d);
        a(Blocks.DARK_OAK_PRESSURE_PLATE, CreativeModeTab.d);
        a(Blocks.REDSTONE_ORE, CreativeModeTab.b);
        a((ItemBlock) (new ItemBlockWallable(Blocks.REDSTONE_TORCH, Blocks.REDSTONE_WALL_TORCH, (new Item.Info()).a(CreativeModeTab.d))));
        a(Blocks.STONE_BUTTON, CreativeModeTab.d);
        a(Blocks.SNOW, CreativeModeTab.c);
        a(Blocks.ICE, CreativeModeTab.b);
        a(Blocks.SNOW_BLOCK, CreativeModeTab.b);
        a(Blocks.CACTUS, CreativeModeTab.c);
        a(Blocks.CLAY, CreativeModeTab.b);
        a(Blocks.JUKEBOX, CreativeModeTab.c);
        a(Blocks.OAK_FENCE, CreativeModeTab.c);
        a(Blocks.SPRUCE_FENCE, CreativeModeTab.c);
        a(Blocks.BIRCH_FENCE, CreativeModeTab.c);
        a(Blocks.JUNGLE_FENCE, CreativeModeTab.c);
        a(Blocks.ACACIA_FENCE, CreativeModeTab.c);
        a(Blocks.DARK_OAK_FENCE, CreativeModeTab.c);
        a(Blocks.PUMPKIN, CreativeModeTab.b);
        a(Blocks.CARVED_PUMPKIN, CreativeModeTab.b);
        a(Blocks.NETHERRACK, CreativeModeTab.b);
        a(Blocks.SOUL_SAND, CreativeModeTab.b);
        a(Blocks.GLOWSTONE, CreativeModeTab.b);
        a(Blocks.JACK_O_LANTERN, CreativeModeTab.b);
        a(Blocks.OAK_TRAPDOOR, CreativeModeTab.d);
        a(Blocks.SPRUCE_TRAPDOOR, CreativeModeTab.d);
        a(Blocks.BIRCH_TRAPDOOR, CreativeModeTab.d);
        a(Blocks.JUNGLE_TRAPDOOR, CreativeModeTab.d);
        a(Blocks.ACACIA_TRAPDOOR, CreativeModeTab.d);
        a(Blocks.DARK_OAK_TRAPDOOR, CreativeModeTab.d);
        a(Blocks.INFESTED_STONE, CreativeModeTab.c);
        a(Blocks.INFESTED_COBBLESTONE, CreativeModeTab.c);
        a(Blocks.INFESTED_STONE_BRICKS, CreativeModeTab.c);
        a(Blocks.INFESTED_MOSSY_STONE_BRICKS, CreativeModeTab.c);
        a(Blocks.INFESTED_CRACKED_STONE_BRICKS, CreativeModeTab.c);
        a(Blocks.INFESTED_CHISELED_STONE_BRICKS, CreativeModeTab.c);
        a(Blocks.STONE_BRICKS, CreativeModeTab.b);
        a(Blocks.MOSSY_STONE_BRICKS, CreativeModeTab.b);
        a(Blocks.CRACKED_STONE_BRICKS, CreativeModeTab.b);
        a(Blocks.CHISELED_STONE_BRICKS, CreativeModeTab.b);
        a(Blocks.BROWN_MUSHROOM_BLOCK, CreativeModeTab.c);
        a(Blocks.RED_MUSHROOM_BLOCK, CreativeModeTab.c);
        a(Blocks.MUSHROOM_STEM, CreativeModeTab.c);
        a(Blocks.IRON_BARS, CreativeModeTab.c);
        a(Blocks.GLASS_PANE, CreativeModeTab.c);
        a(Blocks.MELON, CreativeModeTab.b);
        a(Blocks.VINE, CreativeModeTab.c);
        a(Blocks.OAK_FENCE_GATE, CreativeModeTab.d);
        a(Blocks.SPRUCE_FENCE_GATE, CreativeModeTab.d);
        a(Blocks.BIRCH_FENCE_GATE, CreativeModeTab.d);
        a(Blocks.JUNGLE_FENCE_GATE, CreativeModeTab.d);
        a(Blocks.ACACIA_FENCE_GATE, CreativeModeTab.d);
        a(Blocks.DARK_OAK_FENCE_GATE, CreativeModeTab.d);
        a(Blocks.BRICK_STAIRS, CreativeModeTab.b);
        a(Blocks.STONE_BRICK_STAIRS, CreativeModeTab.b);
        a(Blocks.MYCELIUM, CreativeModeTab.b);
        a((ItemBlock) (new ItemWaterLily(Blocks.LILY_PAD, (new Item.Info()).a(CreativeModeTab.c))));
        a(Blocks.NETHER_BRICKS, CreativeModeTab.b);
        a(Blocks.NETHER_BRICK_FENCE, CreativeModeTab.c);
        a(Blocks.NETHER_BRICK_STAIRS, CreativeModeTab.b);
        a(Blocks.ENCHANTING_TABLE, CreativeModeTab.c);
        a(Blocks.END_PORTAL_FRAME, CreativeModeTab.c);
        a(Blocks.END_STONE, CreativeModeTab.b);
        a(Blocks.END_STONE_BRICKS, CreativeModeTab.b);
        a(new ItemBlock(Blocks.DRAGON_EGG, (new Item.Info()).a(EnumItemRarity.EPIC)));
        a(Blocks.REDSTONE_LAMP, CreativeModeTab.d);
        a(Blocks.SANDSTONE_STAIRS, CreativeModeTab.b);
        a(Blocks.EMERALD_ORE, CreativeModeTab.b);
        a(Blocks.ENDER_CHEST, CreativeModeTab.c);
        a(Blocks.TRIPWIRE_HOOK, CreativeModeTab.d);
        a(Blocks.EMERALD_BLOCK, CreativeModeTab.b);
        a(Blocks.SPRUCE_STAIRS, CreativeModeTab.b);
        a(Blocks.BIRCH_STAIRS, CreativeModeTab.b);
        a(Blocks.JUNGLE_STAIRS, CreativeModeTab.b);
        a((ItemBlock) (new ItemRestricted(Blocks.COMMAND_BLOCK, (new Item.Info()).a(EnumItemRarity.EPIC))));
        a(new ItemBlock(Blocks.BEACON, (new Item.Info()).a(CreativeModeTab.f).a(EnumItemRarity.RARE)));
        a(Blocks.COBBLESTONE_WALL, CreativeModeTab.c);
        a(Blocks.MOSSY_COBBLESTONE_WALL, CreativeModeTab.c);
        a(Blocks.OAK_BUTTON, CreativeModeTab.d);
        a(Blocks.SPRUCE_BUTTON, CreativeModeTab.d);
        a(Blocks.BIRCH_BUTTON, CreativeModeTab.d);
        a(Blocks.JUNGLE_BUTTON, CreativeModeTab.d);
        a(Blocks.ACACIA_BUTTON, CreativeModeTab.d);
        a(Blocks.DARK_OAK_BUTTON, CreativeModeTab.d);
        a(Blocks.ANVIL, CreativeModeTab.c);
        a(Blocks.CHIPPED_ANVIL, CreativeModeTab.c);
        a(Blocks.DAMAGED_ANVIL, CreativeModeTab.c);
        a(Blocks.TRAPPED_CHEST, CreativeModeTab.d);
        a(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, CreativeModeTab.d);
        a(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, CreativeModeTab.d);
        a(Blocks.DAYLIGHT_DETECTOR, CreativeModeTab.d);
        a(Blocks.REDSTONE_BLOCK, CreativeModeTab.d);
        a(Blocks.NETHER_QUARTZ_ORE, CreativeModeTab.b);
        a(Blocks.HOPPER, CreativeModeTab.d);
        a(Blocks.CHISELED_QUARTZ_BLOCK, CreativeModeTab.b);
        a(Blocks.QUARTZ_BLOCK, CreativeModeTab.b);
        a(Blocks.QUARTZ_PILLAR, CreativeModeTab.b);
        a(Blocks.QUARTZ_STAIRS, CreativeModeTab.b);
        a(Blocks.ACTIVATOR_RAIL, CreativeModeTab.e);
        a(Blocks.DROPPER, CreativeModeTab.d);
        a(Blocks.WHITE_TERRACOTTA, CreativeModeTab.b);
        a(Blocks.ORANGE_TERRACOTTA, CreativeModeTab.b);
        a(Blocks.MAGENTA_TERRACOTTA, CreativeModeTab.b);
        a(Blocks.LIGHT_BLUE_TERRACOTTA, CreativeModeTab.b);
        a(Blocks.YELLOW_TERRACOTTA, CreativeModeTab.b);
        a(Blocks.LIME_TERRACOTTA, CreativeModeTab.b);
        a(Blocks.PINK_TERRACOTTA, CreativeModeTab.b);
        a(Blocks.GRAY_TERRACOTTA, CreativeModeTab.b);
        a(Blocks.LIGHT_GRAY_TERRACOTTA, CreativeModeTab.b);
        a(Blocks.CYAN_TERRACOTTA, CreativeModeTab.b);
        a(Blocks.PURPLE_TERRACOTTA, CreativeModeTab.b);
        a(Blocks.BLUE_TERRACOTTA, CreativeModeTab.b);
        a(Blocks.BROWN_TERRACOTTA, CreativeModeTab.b);
        a(Blocks.GREEN_TERRACOTTA, CreativeModeTab.b);
        a(Blocks.RED_TERRACOTTA, CreativeModeTab.b);
        a(Blocks.BLACK_TERRACOTTA, CreativeModeTab.b);
        b(Blocks.BARRIER);
        a(Blocks.IRON_TRAPDOOR, CreativeModeTab.d);
        a(Blocks.HAY_BLOCK, CreativeModeTab.b);
        a(Blocks.WHITE_CARPET, CreativeModeTab.c);
        a(Blocks.ORANGE_CARPET, CreativeModeTab.c);
        a(Blocks.MAGENTA_CARPET, CreativeModeTab.c);
        a(Blocks.LIGHT_BLUE_CARPET, CreativeModeTab.c);
        a(Blocks.YELLOW_CARPET, CreativeModeTab.c);
        a(Blocks.LIME_CARPET, CreativeModeTab.c);
        a(Blocks.PINK_CARPET, CreativeModeTab.c);
        a(Blocks.GRAY_CARPET, CreativeModeTab.c);
        a(Blocks.LIGHT_GRAY_CARPET, CreativeModeTab.c);
        a(Blocks.CYAN_CARPET, CreativeModeTab.c);
        a(Blocks.PURPLE_CARPET, CreativeModeTab.c);
        a(Blocks.BLUE_CARPET, CreativeModeTab.c);
        a(Blocks.BROWN_CARPET, CreativeModeTab.c);
        a(Blocks.GREEN_CARPET, CreativeModeTab.c);
        a(Blocks.RED_CARPET, CreativeModeTab.c);
        a(Blocks.BLACK_CARPET, CreativeModeTab.c);
        a(Blocks.TERRACOTTA, CreativeModeTab.b);
        a(Blocks.COAL_BLOCK, CreativeModeTab.b);
        a(Blocks.PACKED_ICE, CreativeModeTab.b);
        a(Blocks.ACACIA_STAIRS, CreativeModeTab.b);
        a(Blocks.DARK_OAK_STAIRS, CreativeModeTab.b);
        a(Blocks.SLIME_BLOCK, CreativeModeTab.c);
        a(Blocks.GRASS_PATH, CreativeModeTab.c);
        a((ItemBlock) (new ItemBisected(Blocks.SUNFLOWER, (new Item.Info()).a(CreativeModeTab.c))));
        a((ItemBlock) (new ItemBisected(Blocks.LILAC, (new Item.Info()).a(CreativeModeTab.c))));
        a((ItemBlock) (new ItemBisected(Blocks.ROSE_BUSH, (new Item.Info()).a(CreativeModeTab.c))));
        a((ItemBlock) (new ItemBisected(Blocks.PEONY, (new Item.Info()).a(CreativeModeTab.c))));
        a((ItemBlock) (new ItemBisected(Blocks.TALL_GRASS, (new Item.Info()).a(CreativeModeTab.c))));
        a((ItemBlock) (new ItemBisected(Blocks.LARGE_FERN, (new Item.Info()).a(CreativeModeTab.c))));
        a(Blocks.WHITE_STAINED_GLASS, CreativeModeTab.b);
        a(Blocks.ORANGE_STAINED_GLASS, CreativeModeTab.b);
        a(Blocks.MAGENTA_STAINED_GLASS, CreativeModeTab.b);
        a(Blocks.LIGHT_BLUE_STAINED_GLASS, CreativeModeTab.b);
        a(Blocks.YELLOW_STAINED_GLASS, CreativeModeTab.b);
        a(Blocks.LIME_STAINED_GLASS, CreativeModeTab.b);
        a(Blocks.PINK_STAINED_GLASS, CreativeModeTab.b);
        a(Blocks.GRAY_STAINED_GLASS, CreativeModeTab.b);
        a(Blocks.LIGHT_GRAY_STAINED_GLASS, CreativeModeTab.b);
        a(Blocks.CYAN_STAINED_GLASS, CreativeModeTab.b);
        a(Blocks.PURPLE_STAINED_GLASS, CreativeModeTab.b);
        a(Blocks.BLUE_STAINED_GLASS, CreativeModeTab.b);
        a(Blocks.BROWN_STAINED_GLASS, CreativeModeTab.b);
        a(Blocks.GREEN_STAINED_GLASS, CreativeModeTab.b);
        a(Blocks.RED_STAINED_GLASS, CreativeModeTab.b);
        a(Blocks.BLACK_STAINED_GLASS, CreativeModeTab.b);
        a(Blocks.WHITE_STAINED_GLASS_PANE, CreativeModeTab.c);
        a(Blocks.ORANGE_STAINED_GLASS_PANE, CreativeModeTab.c);
        a(Blocks.MAGENTA_STAINED_GLASS_PANE, CreativeModeTab.c);
        a(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, CreativeModeTab.c);
        a(Blocks.YELLOW_STAINED_GLASS_PANE, CreativeModeTab.c);
        a(Blocks.LIME_STAINED_GLASS_PANE, CreativeModeTab.c);
        a(Blocks.PINK_STAINED_GLASS_PANE, CreativeModeTab.c);
        a(Blocks.GRAY_STAINED_GLASS_PANE, CreativeModeTab.c);
        a(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, CreativeModeTab.c);
        a(Blocks.CYAN_STAINED_GLASS_PANE, CreativeModeTab.c);
        a(Blocks.PURPLE_STAINED_GLASS_PANE, CreativeModeTab.c);
        a(Blocks.BLUE_STAINED_GLASS_PANE, CreativeModeTab.c);
        a(Blocks.BROWN_STAINED_GLASS_PANE, CreativeModeTab.c);
        a(Blocks.GREEN_STAINED_GLASS_PANE, CreativeModeTab.c);
        a(Blocks.RED_STAINED_GLASS_PANE, CreativeModeTab.c);
        a(Blocks.BLACK_STAINED_GLASS_PANE, CreativeModeTab.c);
        a(Blocks.PRISMARINE, CreativeModeTab.b);
        a(Blocks.PRISMARINE_BRICKS, CreativeModeTab.b);
        a(Blocks.DARK_PRISMARINE, CreativeModeTab.b);
        a(Blocks.PRISMARINE_STAIRS, CreativeModeTab.b);
        a(Blocks.PRISMARINE_BRICK_STAIRS, CreativeModeTab.b);
        a(Blocks.DARK_PRISMARINE_STAIRS, CreativeModeTab.b);
        a(Blocks.SEA_LANTERN, CreativeModeTab.b);
        a(Blocks.RED_SANDSTONE, CreativeModeTab.b);
        a(Blocks.CHISELED_RED_SANDSTONE, CreativeModeTab.b);
        a(Blocks.CUT_RED_SANDSTONE, CreativeModeTab.b);
        a(Blocks.RED_SANDSTONE_STAIRS, CreativeModeTab.b);
        a((ItemBlock) (new ItemRestricted(Blocks.REPEATING_COMMAND_BLOCK, (new Item.Info()).a(EnumItemRarity.EPIC))));
        a((ItemBlock) (new ItemRestricted(Blocks.CHAIN_COMMAND_BLOCK, (new Item.Info()).a(EnumItemRarity.EPIC))));
        a(Blocks.MAGMA_BLOCK, CreativeModeTab.b);
        a(Blocks.NETHER_WART_BLOCK, CreativeModeTab.b);
        a(Blocks.RED_NETHER_BRICKS, CreativeModeTab.b);
        a(Blocks.BONE_BLOCK, CreativeModeTab.b);
        b(Blocks.STRUCTURE_VOID);
        a(Blocks.OBSERVER, CreativeModeTab.d);
        a(new ItemBlock(Blocks.SHULKER_BOX, (new Item.Info()).a(1).a(CreativeModeTab.c)));
        a(new ItemBlock(Blocks.WHITE_SHULKER_BOX, (new Item.Info()).a(1).a(CreativeModeTab.c)));
        a(new ItemBlock(Blocks.ORANGE_SHULKER_BOX, (new Item.Info()).a(1).a(CreativeModeTab.c)));
        a(new ItemBlock(Blocks.MAGENTA_SHULKER_BOX, (new Item.Info()).a(1).a(CreativeModeTab.c)));
        a(new ItemBlock(Blocks.LIGHT_BLUE_SHULKER_BOX, (new Item.Info()).a(1).a(CreativeModeTab.c)));
        a(new ItemBlock(Blocks.YELLOW_SHULKER_BOX, (new Item.Info()).a(1).a(CreativeModeTab.c)));
        a(new ItemBlock(Blocks.LIME_SHULKER_BOX, (new Item.Info()).a(1).a(CreativeModeTab.c)));
        a(new ItemBlock(Blocks.PINK_SHULKER_BOX, (new Item.Info()).a(1).a(CreativeModeTab.c)));
        a(new ItemBlock(Blocks.GRAY_SHULKER_BOX, (new Item.Info()).a(1).a(CreativeModeTab.c)));
        a(new ItemBlock(Blocks.LIGHT_GRAY_SHULKER_BOX, (new Item.Info()).a(1).a(CreativeModeTab.c)));
        a(new ItemBlock(Blocks.CYAN_SHULKER_BOX, (new Item.Info()).a(1).a(CreativeModeTab.c)));
        a(new ItemBlock(Blocks.PURPLE_SHULKER_BOX, (new Item.Info()).a(1).a(CreativeModeTab.c)));
        a(new ItemBlock(Blocks.BLUE_SHULKER_BOX, (new Item.Info()).a(1).a(CreativeModeTab.c)));
        a(new ItemBlock(Blocks.BROWN_SHULKER_BOX, (new Item.Info()).a(1).a(CreativeModeTab.c)));
        a(new ItemBlock(Blocks.GREEN_SHULKER_BOX, (new Item.Info()).a(1).a(CreativeModeTab.c)));
        a(new ItemBlock(Blocks.RED_SHULKER_BOX, (new Item.Info()).a(1).a(CreativeModeTab.c)));
        a(new ItemBlock(Blocks.BLACK_SHULKER_BOX, (new Item.Info()).a(1).a(CreativeModeTab.c)));
        a(Blocks.WHITE_GLAZED_TERRACOTTA, CreativeModeTab.c);
        a(Blocks.ORANGE_GLAZED_TERRACOTTA, CreativeModeTab.c);
        a(Blocks.MAGENTA_GLAZED_TERRACOTTA, CreativeModeTab.c);
        a(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, CreativeModeTab.c);
        a(Blocks.YELLOW_GLAZED_TERRACOTTA, CreativeModeTab.c);
        a(Blocks.LIME_GLAZED_TERRACOTTA, CreativeModeTab.c);
        a(Blocks.PINK_GLAZED_TERRACOTTA, CreativeModeTab.c);
        a(Blocks.GRAY_GLAZED_TERRACOTTA, CreativeModeTab.c);
        a(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA, CreativeModeTab.c);
        a(Blocks.CYAN_GLAZED_TERRACOTTA, CreativeModeTab.c);
        a(Blocks.PURPLE_GLAZED_TERRACOTTA, CreativeModeTab.c);
        a(Blocks.BLUE_GLAZED_TERRACOTTA, CreativeModeTab.c);
        a(Blocks.BROWN_GLAZED_TERRACOTTA, CreativeModeTab.c);
        a(Blocks.GREEN_GLAZED_TERRACOTTA, CreativeModeTab.c);
        a(Blocks.RED_GLAZED_TERRACOTTA, CreativeModeTab.c);
        a(Blocks.BLACK_GLAZED_TERRACOTTA, CreativeModeTab.c);
        a(Blocks.WHITE_CONCRETE, CreativeModeTab.b);
        a(Blocks.ORANGE_CONCRETE, CreativeModeTab.b);
        a(Blocks.MAGENTA_CONCRETE, CreativeModeTab.b);
        a(Blocks.LIGHT_BLUE_CONCRETE, CreativeModeTab.b);
        a(Blocks.YELLOW_CONCRETE, CreativeModeTab.b);
        a(Blocks.LIME_CONCRETE, CreativeModeTab.b);
        a(Blocks.PINK_CONCRETE, CreativeModeTab.b);
        a(Blocks.GRAY_CONCRETE, CreativeModeTab.b);
        a(Blocks.LIGHT_GRAY_CONCRETE, CreativeModeTab.b);
        a(Blocks.CYAN_CONCRETE, CreativeModeTab.b);
        a(Blocks.PURPLE_CONCRETE, CreativeModeTab.b);
        a(Blocks.BLUE_CONCRETE, CreativeModeTab.b);
        a(Blocks.BROWN_CONCRETE, CreativeModeTab.b);
        a(Blocks.GREEN_CONCRETE, CreativeModeTab.b);
        a(Blocks.RED_CONCRETE, CreativeModeTab.b);
        a(Blocks.BLACK_CONCRETE, CreativeModeTab.b);
        a(Blocks.WHITE_CONCRETE_POWDER, CreativeModeTab.b);
        a(Blocks.ORANGE_CONCRETE_POWDER, CreativeModeTab.b);
        a(Blocks.MAGENTA_CONCRETE_POWDER, CreativeModeTab.b);
        a(Blocks.LIGHT_BLUE_CONCRETE_POWDER, CreativeModeTab.b);
        a(Blocks.YELLOW_CONCRETE_POWDER, CreativeModeTab.b);
        a(Blocks.LIME_CONCRETE_POWDER, CreativeModeTab.b);
        a(Blocks.PINK_CONCRETE_POWDER, CreativeModeTab.b);
        a(Blocks.GRAY_CONCRETE_POWDER, CreativeModeTab.b);
        a(Blocks.LIGHT_GRAY_CONCRETE_POWDER, CreativeModeTab.b);
        a(Blocks.CYAN_CONCRETE_POWDER, CreativeModeTab.b);
        a(Blocks.PURPLE_CONCRETE_POWDER, CreativeModeTab.b);
        a(Blocks.BLUE_CONCRETE_POWDER, CreativeModeTab.b);
        a(Blocks.BROWN_CONCRETE_POWDER, CreativeModeTab.b);
        a(Blocks.GREEN_CONCRETE_POWDER, CreativeModeTab.b);
        a(Blocks.RED_CONCRETE_POWDER, CreativeModeTab.b);
        a(Blocks.BLACK_CONCRETE_POWDER, CreativeModeTab.b);
        a(Blocks.TURTLE_EGG, CreativeModeTab.f);
        a(Blocks.DEAD_TUBE_CORAL_BLOCK, CreativeModeTab.b);
        a(Blocks.DEAD_BRAIN_CORAL_BLOCK, CreativeModeTab.b);
        a(Blocks.DEAD_BUBBLE_CORAL_BLOCK, CreativeModeTab.b);
        a(Blocks.DEAD_FIRE_CORAL_BLOCK, CreativeModeTab.b);
        a(Blocks.DEAD_HORN_CORAL_BLOCK, CreativeModeTab.b);
        a(Blocks.TUBE_CORAL_BLOCK, CreativeModeTab.b);
        a(Blocks.BRAIN_CORAL_BLOCK, CreativeModeTab.b);
        a(Blocks.BUBBLE_CORAL_BLOCK, CreativeModeTab.b);
        a(Blocks.FIRE_CORAL_BLOCK, CreativeModeTab.b);
        a(Blocks.HORN_CORAL_BLOCK, CreativeModeTab.b);
        a(Blocks.TUBE_CORAL, CreativeModeTab.c);
        a(Blocks.BRAIN_CORAL, CreativeModeTab.c);
        a(Blocks.BUBBLE_CORAL, CreativeModeTab.c);
        a(Blocks.FIRE_CORAL, CreativeModeTab.c);
        a(Blocks.HORN_CORAL, CreativeModeTab.c);
        a(Blocks.DEAD_BRAIN_CORAL, CreativeModeTab.c);
        a(Blocks.DEAD_BUBBLE_CORAL, CreativeModeTab.c);
        a(Blocks.DEAD_FIRE_CORAL, CreativeModeTab.c);
        a(Blocks.DEAD_HORN_CORAL, CreativeModeTab.c);
        a(Blocks.DEAD_TUBE_CORAL, CreativeModeTab.c);
        a((ItemBlock) (new ItemBlockWallable(Blocks.TUBE_CORAL_FAN, Blocks.TUBE_CORAL_WALL_FAN, (new Item.Info()).a(CreativeModeTab.c))));
        a((ItemBlock) (new ItemBlockWallable(Blocks.BRAIN_CORAL_FAN, Blocks.BRAIN_CORAL_WALL_FAN, (new Item.Info()).a(CreativeModeTab.c))));
        a((ItemBlock) (new ItemBlockWallable(Blocks.BUBBLE_CORAL_FAN, Blocks.BUBBLE_CORAL_WALL_FAN, (new Item.Info()).a(CreativeModeTab.c))));
        a((ItemBlock) (new ItemBlockWallable(Blocks.FIRE_CORAL_FAN, Blocks.FIRE_CORAL_WALL_FAN, (new Item.Info()).a(CreativeModeTab.c))));
        a((ItemBlock) (new ItemBlockWallable(Blocks.HORN_CORAL_FAN, Blocks.HORN_CORAL_WALL_FAN, (new Item.Info()).a(CreativeModeTab.c))));
        a((ItemBlock) (new ItemBlockWallable(Blocks.DEAD_TUBE_CORAL_FAN, Blocks.DEAD_TUBE_CORAL_WALL_FAN, (new Item.Info()).a(CreativeModeTab.c))));
        a((ItemBlock) (new ItemBlockWallable(Blocks.DEAD_BRAIN_CORAL_FAN, Blocks.DEAD_BRAIN_CORAL_WALL_FAN, (new Item.Info()).a(CreativeModeTab.c))));
        a((ItemBlock) (new ItemBlockWallable(Blocks.DEAD_BUBBLE_CORAL_FAN, Blocks.DEAD_BUBBLE_CORAL_WALL_FAN, (new Item.Info()).a(CreativeModeTab.c))));
        a((ItemBlock) (new ItemBlockWallable(Blocks.DEAD_FIRE_CORAL_FAN, Blocks.DEAD_FIRE_CORAL_WALL_FAN, (new Item.Info()).a(CreativeModeTab.c))));
        a((ItemBlock) (new ItemBlockWallable(Blocks.DEAD_HORN_CORAL_FAN, Blocks.DEAD_HORN_CORAL_WALL_FAN, (new Item.Info()).a(CreativeModeTab.c))));
        a(Blocks.BLUE_ICE, CreativeModeTab.b);
        a(new ItemBlock(Blocks.CONDUIT, (new Item.Info()).a(CreativeModeTab.f).a(EnumItemRarity.RARE)));
        a((ItemBlock) (new ItemBisected(Blocks.IRON_DOOR, (new Item.Info()).a(CreativeModeTab.d))));
        a((ItemBlock) (new ItemBisected(Blocks.OAK_DOOR, (new Item.Info()).a(CreativeModeTab.d))));
        a((ItemBlock) (new ItemBisected(Blocks.SPRUCE_DOOR, (new Item.Info()).a(CreativeModeTab.d))));
        a((ItemBlock) (new ItemBisected(Blocks.BIRCH_DOOR, (new Item.Info()).a(CreativeModeTab.d))));
        a((ItemBlock) (new ItemBisected(Blocks.JUNGLE_DOOR, (new Item.Info()).a(CreativeModeTab.d))));
        a((ItemBlock) (new ItemBisected(Blocks.ACACIA_DOOR, (new Item.Info()).a(CreativeModeTab.d))));
        a((ItemBlock) (new ItemBisected(Blocks.DARK_OAK_DOOR, (new Item.Info()).a(CreativeModeTab.d))));
        a(Blocks.REPEATER, CreativeModeTab.d);
        a(Blocks.COMPARATOR, CreativeModeTab.d);
        a((ItemBlock) (new ItemRestricted(Blocks.STRUCTURE_BLOCK, (new Item.Info()).a(EnumItemRarity.EPIC))));
        a("turtle_helmet", (Item) (new ItemArmor(EnumArmorMaterial.TURTLE, EnumItemSlot.HEAD, (new Item.Info()).a(CreativeModeTab.j))));
        a("scute", new Item((new Item.Info()).a(CreativeModeTab.l)));
        a("iron_shovel", (Item) (new ItemSpade(EnumToolMaterial.IRON, 1.5F, -3.0F, (new Item.Info()).a(CreativeModeTab.i))));
        a("iron_pickaxe", (Item) (new ItemPickaxe(EnumToolMaterial.IRON, 1, -2.8F, (new Item.Info()).a(CreativeModeTab.i))));
        a("iron_axe", (Item) (new ItemAxe(EnumToolMaterial.IRON, 6.0F, -3.1F, (new Item.Info()).a(CreativeModeTab.i))));
        a("flint_and_steel", (Item) (new ItemFlintAndSteel((new Item.Info()).c(64).a(CreativeModeTab.i))));
        a("apple", (Item) (new ItemFood(4, 0.3F, false, (new Item.Info()).a(CreativeModeTab.h))));
        a("bow", (Item) (new ItemBow((new Item.Info()).c(384).a(CreativeModeTab.j))));
        a("arrow", (Item) (new ItemArrow((new Item.Info()).a(CreativeModeTab.j))));
        a("coal", new Item((new Item.Info()).a(CreativeModeTab.l)));
        a("charcoal", new Item((new Item.Info()).a(CreativeModeTab.l)));
        a("diamond", new Item((new Item.Info()).a(CreativeModeTab.l)));
        a("iron_ingot", new Item((new Item.Info()).a(CreativeModeTab.l)));
        a("gold_ingot", new Item((new Item.Info()).a(CreativeModeTab.l)));
        a("iron_sword", (Item) (new ItemSword(EnumToolMaterial.IRON, 3, -2.4F, (new Item.Info()).a(CreativeModeTab.j))));
        a("wooden_sword", (Item) (new ItemSword(EnumToolMaterial.WOOD, 3, -2.4F, (new Item.Info()).a(CreativeModeTab.j))));
        a("wooden_shovel", (Item) (new ItemSpade(EnumToolMaterial.WOOD, 1.5F, -3.0F, (new Item.Info()).a(CreativeModeTab.i))));
        a("wooden_pickaxe", (Item) (new ItemPickaxe(EnumToolMaterial.WOOD, 1, -2.8F, (new Item.Info()).a(CreativeModeTab.i))));
        a("wooden_axe", (Item) (new ItemAxe(EnumToolMaterial.WOOD, 6.0F, -3.2F, (new Item.Info()).a(CreativeModeTab.i))));
        a("stone_sword", (Item) (new ItemSword(EnumToolMaterial.STONE, 3, -2.4F, (new Item.Info()).a(CreativeModeTab.j))));
        a("stone_shovel", (Item) (new ItemSpade(EnumToolMaterial.STONE, 1.5F, -3.0F, (new Item.Info()).a(CreativeModeTab.i))));
        a("stone_pickaxe", (Item) (new ItemPickaxe(EnumToolMaterial.STONE, 1, -2.8F, (new Item.Info()).a(CreativeModeTab.i))));
        a("stone_axe", (Item) (new ItemAxe(EnumToolMaterial.STONE, 7.0F, -3.2F, (new Item.Info()).a(CreativeModeTab.i))));
        a("diamond_sword", (Item) (new ItemSword(EnumToolMaterial.DIAMOND, 3, -2.4F, (new Item.Info()).a(CreativeModeTab.j))));
        a("diamond_shovel", (Item) (new ItemSpade(EnumToolMaterial.DIAMOND, 1.5F, -3.0F, (new Item.Info()).a(CreativeModeTab.i))));
        a("diamond_pickaxe", (Item) (new ItemPickaxe(EnumToolMaterial.DIAMOND, 1, -2.8F, (new Item.Info()).a(CreativeModeTab.i))));
        a("diamond_axe", (Item) (new ItemAxe(EnumToolMaterial.DIAMOND, 5.0F, -3.0F, (new Item.Info()).a(CreativeModeTab.i))));
        a("stick", new Item((new Item.Info()).a(CreativeModeTab.l)));
        a("bowl", new Item((new Item.Info()).a(CreativeModeTab.l)));
        a("mushroom_stew", (Item) (new ItemSoup(6, (new Item.Info()).a(1).a(CreativeModeTab.h))));
        a("golden_sword", (Item) (new ItemSword(EnumToolMaterial.GOLD, 3, -2.4F, (new Item.Info()).a(CreativeModeTab.j))));
        a("golden_shovel", (Item) (new ItemSpade(EnumToolMaterial.GOLD, 1.5F, -3.0F, (new Item.Info()).a(CreativeModeTab.i))));
        a("golden_pickaxe", (Item) (new ItemPickaxe(EnumToolMaterial.GOLD, 1, -2.8F, (new Item.Info()).a(CreativeModeTab.i))));
        a("golden_axe", (Item) (new ItemAxe(EnumToolMaterial.GOLD, 6.0F, -3.0F, (new Item.Info()).a(CreativeModeTab.i))));
        a("string", (Item) (new ItemString((new Item.Info()).a(CreativeModeTab.f))));
        a("feather", new Item((new Item.Info()).a(CreativeModeTab.l)));
        a("gunpowder", new Item((new Item.Info()).a(CreativeModeTab.l)));
        a("wooden_hoe", (Item) (new ItemHoe(EnumToolMaterial.WOOD, -3.0F, (new Item.Info()).a(CreativeModeTab.i))));
        a("stone_hoe", (Item) (new ItemHoe(EnumToolMaterial.STONE, -2.0F, (new Item.Info()).a(CreativeModeTab.i))));
        a("iron_hoe", (Item) (new ItemHoe(EnumToolMaterial.IRON, -1.0F, (new Item.Info()).a(CreativeModeTab.i))));
        a("diamond_hoe", (Item) (new ItemHoe(EnumToolMaterial.DIAMOND, 0.0F, (new Item.Info()).a(CreativeModeTab.i))));
        a("golden_hoe", (Item) (new ItemHoe(EnumToolMaterial.GOLD, -3.0F, (new Item.Info()).a(CreativeModeTab.i))));
        a("wheat_seeds", (Item) (new ItemSeeds(Blocks.WHEAT, (new Item.Info()).a(CreativeModeTab.l))));
        a("wheat", new Item((new Item.Info()).a(CreativeModeTab.l)));
        a("bread", (Item) (new ItemFood(5, 0.6F, false, (new Item.Info()).a(CreativeModeTab.h))));
        a("leather_helmet", (Item) (new ItemArmorColorable(EnumArmorMaterial.LEATHER, EnumItemSlot.HEAD, (new Item.Info()).a(CreativeModeTab.j))));
        a("leather_chestplate", (Item) (new ItemArmorColorable(EnumArmorMaterial.LEATHER, EnumItemSlot.CHEST, (new Item.Info()).a(CreativeModeTab.j))));
        a("leather_leggings", (Item) (new ItemArmorColorable(EnumArmorMaterial.LEATHER, EnumItemSlot.LEGS, (new Item.Info()).a(CreativeModeTab.j))));
        a("leather_boots", (Item) (new ItemArmorColorable(EnumArmorMaterial.LEATHER, EnumItemSlot.FEET, (new Item.Info()).a(CreativeModeTab.j))));
        a("chainmail_helmet", (Item) (new ItemArmor(EnumArmorMaterial.CHAIN, EnumItemSlot.HEAD, (new Item.Info()).a(CreativeModeTab.j))));
        a("chainmail_chestplate", (Item) (new ItemArmor(EnumArmorMaterial.CHAIN, EnumItemSlot.CHEST, (new Item.Info()).a(CreativeModeTab.j))));
        a("chainmail_leggings", (Item) (new ItemArmor(EnumArmorMaterial.CHAIN, EnumItemSlot.LEGS, (new Item.Info()).a(CreativeModeTab.j))));
        a("chainmail_boots", (Item) (new ItemArmor(EnumArmorMaterial.CHAIN, EnumItemSlot.FEET, (new Item.Info()).a(CreativeModeTab.j))));
        a("iron_helmet", (Item) (new ItemArmor(EnumArmorMaterial.IRON, EnumItemSlot.HEAD, (new Item.Info()).a(CreativeModeTab.j))));
        a("iron_chestplate", (Item) (new ItemArmor(EnumArmorMaterial.IRON, EnumItemSlot.CHEST, (new Item.Info()).a(CreativeModeTab.j))));
        a("iron_leggings", (Item) (new ItemArmor(EnumArmorMaterial.IRON, EnumItemSlot.LEGS, (new Item.Info()).a(CreativeModeTab.j))));
        a("iron_boots", (Item) (new ItemArmor(EnumArmorMaterial.IRON, EnumItemSlot.FEET, (new Item.Info()).a(CreativeModeTab.j))));
        a("diamond_helmet", (Item) (new ItemArmor(EnumArmorMaterial.DIAMOND, EnumItemSlot.HEAD, (new Item.Info()).a(CreativeModeTab.j))));
        a("diamond_chestplate", (Item) (new ItemArmor(EnumArmorMaterial.DIAMOND, EnumItemSlot.CHEST, (new Item.Info()).a(CreativeModeTab.j))));
        a("diamond_leggings", (Item) (new ItemArmor(EnumArmorMaterial.DIAMOND, EnumItemSlot.LEGS, (new Item.Info()).a(CreativeModeTab.j))));
        a("diamond_boots", (Item) (new ItemArmor(EnumArmorMaterial.DIAMOND, EnumItemSlot.FEET, (new Item.Info()).a(CreativeModeTab.j))));
        a("golden_helmet", (Item) (new ItemArmor(EnumArmorMaterial.GOLD, EnumItemSlot.HEAD, (new Item.Info()).a(CreativeModeTab.j))));
        a("golden_chestplate", (Item) (new ItemArmor(EnumArmorMaterial.GOLD, EnumItemSlot.CHEST, (new Item.Info()).a(CreativeModeTab.j))));
        a("golden_leggings", (Item) (new ItemArmor(EnumArmorMaterial.GOLD, EnumItemSlot.LEGS, (new Item.Info()).a(CreativeModeTab.j))));
        a("golden_boots", (Item) (new ItemArmor(EnumArmorMaterial.GOLD, EnumItemSlot.FEET, (new Item.Info()).a(CreativeModeTab.j))));
        a("flint", new Item((new Item.Info()).a(CreativeModeTab.l)));
        a("porkchop", (Item) (new ItemFood(3, 0.3F, true, (new Item.Info()).a(CreativeModeTab.h))));
        a("cooked_porkchop", (Item) (new ItemFood(8, 0.8F, true, (new Item.Info()).a(CreativeModeTab.h))));
        a("painting", (Item) (new ItemHanging(EntityPainting.class, (new Item.Info()).a(CreativeModeTab.c))));
        a("golden_apple", (Item) (new ItemGoldenApple(4, 1.2F, false, (new Item.Info()).a(CreativeModeTab.h).a(EnumItemRarity.RARE))).e());
        a("enchanted_golden_apple", (Item) (new ItemGoldenAppleEnchanted(4, 1.2F, false, (new Item.Info()).a(CreativeModeTab.h).a(EnumItemRarity.EPIC))).e());
        a("sign", (Item) (new ItemSign((new Item.Info()).a(16).a(CreativeModeTab.c))));
        ItemBucket itembucket = new ItemBucket(FluidTypes.EMPTY, (new Item.Info()).a(16).a(CreativeModeTab.f));

        a("bucket", (Item) itembucket);
        a("water_bucket", (Item) (new ItemBucket(FluidTypes.WATER, (new Item.Info()).a((Item) itembucket).a(1).a(CreativeModeTab.f))));
        a("lava_bucket", (Item) (new ItemBucket(FluidTypes.LAVA, (new Item.Info()).a((Item) itembucket).a(1).a(CreativeModeTab.f))));
        a("minecart", (Item) (new ItemMinecart(EntityMinecartAbstract.EnumMinecartType.RIDEABLE, (new Item.Info()).a(1).a(CreativeModeTab.e))));
        a("saddle", (Item) (new ItemSaddle((new Item.Info()).a(1).a(CreativeModeTab.e))));
        a("redstone", (Item) (new ItemBlock(Blocks.REDSTONE_WIRE, (new Item.Info()).a(CreativeModeTab.d))));
        a("snowball", (Item) (new ItemSnowball((new Item.Info()).a(16).a(CreativeModeTab.f))));
        a("oak_boat", (Item) (new ItemBoat(EntityBoat.EnumBoatType.OAK, (new Item.Info()).a(1).a(CreativeModeTab.e))));
        a("leather", new Item((new Item.Info()).a(CreativeModeTab.l)));
        a("milk_bucket", (Item) (new ItemMilkBucket((new Item.Info()).a((Item) itembucket).a(1).a(CreativeModeTab.f))));
        a("pufferfish_bucket", (Item) (new ItemFishBucket(EntityTypes.PUFFERFISH, FluidTypes.WATER, (new Item.Info()).a(1).a(CreativeModeTab.f))));
        a("salmon_bucket", (Item) (new ItemFishBucket(EntityTypes.SALMON, FluidTypes.WATER, (new Item.Info()).a(1).a(CreativeModeTab.f))));
        a("cod_bucket", (Item) (new ItemFishBucket(EntityTypes.COD, FluidTypes.WATER, (new Item.Info()).a(1).a(CreativeModeTab.f))));
        a("tropical_fish_bucket", (Item) (new ItemFishBucket(EntityTypes.TROPICAL_FISH, FluidTypes.WATER, (new Item.Info()).a(1).a(CreativeModeTab.f))));
        a("brick", new Item((new Item.Info()).a(CreativeModeTab.l)));
        a("clay_ball", new Item((new Item.Info()).a(CreativeModeTab.l)));
        a(Blocks.SUGAR_CANE, CreativeModeTab.f);
        a(Blocks.KELP, CreativeModeTab.f);
        a(Blocks.DRIED_KELP_BLOCK, CreativeModeTab.b);
        a("paper", new Item((new Item.Info()).a(CreativeModeTab.f)));
        a("book", (Item) (new ItemBook((new Item.Info()).a(CreativeModeTab.f))));
        a("slime_ball", new Item((new Item.Info()).a(CreativeModeTab.f)));
        a("chest_minecart", (Item) (new ItemMinecart(EntityMinecartAbstract.EnumMinecartType.CHEST, (new Item.Info()).a(1).a(CreativeModeTab.e))));
        a("furnace_minecart", (Item) (new ItemMinecart(EntityMinecartAbstract.EnumMinecartType.FURNACE, (new Item.Info()).a(1).a(CreativeModeTab.e))));
        a("egg", (Item) (new ItemEgg((new Item.Info()).a(16).a(CreativeModeTab.l))));
        a("compass", (Item) (new ItemCompass((new Item.Info()).a(CreativeModeTab.i))));
        a("fishing_rod", (Item) (new ItemFishingRod((new Item.Info()).c(64).a(CreativeModeTab.i))));
        a("clock", (Item) (new ItemClock((new Item.Info()).a(CreativeModeTab.i))));
        a("glowstone_dust", new Item((new Item.Info()).a(CreativeModeTab.l)));
        a("cod", (Item) (new ItemFish(ItemFish.EnumFish.COD, false, (new Item.Info()).a(CreativeModeTab.h))));
        a("salmon", (Item) (new ItemFish(ItemFish.EnumFish.SALMON, false, (new Item.Info()).a(CreativeModeTab.h))));
        a("tropical_fish", (Item) (new ItemFish(ItemFish.EnumFish.TROPICAL_FISH, false, (new Item.Info()).a(CreativeModeTab.h))));
        a("pufferfish", (Item) (new ItemFish(ItemFish.EnumFish.PUFFERFISH, false, (new Item.Info()).a(CreativeModeTab.h))));
        a("cooked_cod", (Item) (new ItemFish(ItemFish.EnumFish.COD, true, (new Item.Info()).a(CreativeModeTab.h))));
        a("cooked_salmon", (Item) (new ItemFish(ItemFish.EnumFish.SALMON, true, (new Item.Info()).a(CreativeModeTab.h))));
        a("ink_sac", (Item) (new ItemDye(EnumColor.BLACK, (new Item.Info()).a(CreativeModeTab.l))));
        a("rose_red", (Item) (new ItemDye(EnumColor.RED, (new Item.Info()).a(CreativeModeTab.l))));
        a("cactus_green", (Item) (new ItemDye(EnumColor.GREEN, (new Item.Info()).a(CreativeModeTab.l))));
        a("cocoa_beans", (Item) (new ItemCocoa(EnumColor.BROWN, (new Item.Info()).a(CreativeModeTab.l))));
        a("lapis_lazuli", (Item) (new ItemDye(EnumColor.BLUE, (new Item.Info()).a(CreativeModeTab.l))));
        a("purple_dye", (Item) (new ItemDye(EnumColor.PURPLE, (new Item.Info()).a(CreativeModeTab.l))));
        a("cyan_dye", (Item) (new ItemDye(EnumColor.CYAN, (new Item.Info()).a(CreativeModeTab.l))));
        a("light_gray_dye", (Item) (new ItemDye(EnumColor.LIGHT_GRAY, (new Item.Info()).a(CreativeModeTab.l))));
        a("gray_dye", (Item) (new ItemDye(EnumColor.GRAY, (new Item.Info()).a(CreativeModeTab.l))));
        a("pink_dye", (Item) (new ItemDye(EnumColor.PINK, (new Item.Info()).a(CreativeModeTab.l))));
        a("lime_dye", (Item) (new ItemDye(EnumColor.LIME, (new Item.Info()).a(CreativeModeTab.l))));
        a("dandelion_yellow", (Item) (new ItemDye(EnumColor.YELLOW, (new Item.Info()).a(CreativeModeTab.l))));
        a("light_blue_dye", (Item) (new ItemDye(EnumColor.LIGHT_BLUE, (new Item.Info()).a(CreativeModeTab.l))));
        a("magenta_dye", (Item) (new ItemDye(EnumColor.MAGENTA, (new Item.Info()).a(CreativeModeTab.l))));
        a("orange_dye", (Item) (new ItemDye(EnumColor.ORANGE, (new Item.Info()).a(CreativeModeTab.l))));
        a("bone_meal", (Item) (new ItemBoneMeal(EnumColor.WHITE, (new Item.Info()).a(CreativeModeTab.l))));
        a("bone", new Item((new Item.Info()).a(CreativeModeTab.f)));
        a("sugar", new Item((new Item.Info()).a(CreativeModeTab.l)));
        a(new ItemBlock(Blocks.CAKE, (new Item.Info()).a(1).a(CreativeModeTab.h)));
        a((ItemBlock) (new ItemBed(Blocks.WHITE_BED, (new Item.Info()).a(1).a(CreativeModeTab.c))));
        a((ItemBlock) (new ItemBed(Blocks.ORANGE_BED, (new Item.Info()).a(1).a(CreativeModeTab.c))));
        a((ItemBlock) (new ItemBed(Blocks.MAGENTA_BED, (new Item.Info()).a(1).a(CreativeModeTab.c))));
        a((ItemBlock) (new ItemBed(Blocks.LIGHT_BLUE_BED, (new Item.Info()).a(1).a(CreativeModeTab.c))));
        a((ItemBlock) (new ItemBed(Blocks.YELLOW_BED, (new Item.Info()).a(1).a(CreativeModeTab.c))));
        a((ItemBlock) (new ItemBed(Blocks.LIME_BED, (new Item.Info()).a(1).a(CreativeModeTab.c))));
        a((ItemBlock) (new ItemBed(Blocks.PINK_BED, (new Item.Info()).a(1).a(CreativeModeTab.c))));
        a((ItemBlock) (new ItemBed(Blocks.GRAY_BED, (new Item.Info()).a(1).a(CreativeModeTab.c))));
        a((ItemBlock) (new ItemBed(Blocks.LIGHT_GRAY_BED, (new Item.Info()).a(1).a(CreativeModeTab.c))));
        a((ItemBlock) (new ItemBed(Blocks.CYAN_BED, (new Item.Info()).a(1).a(CreativeModeTab.c))));
        a((ItemBlock) (new ItemBed(Blocks.PURPLE_BED, (new Item.Info()).a(1).a(CreativeModeTab.c))));
        a((ItemBlock) (new ItemBed(Blocks.BLUE_BED, (new Item.Info()).a(1).a(CreativeModeTab.c))));
        a((ItemBlock) (new ItemBed(Blocks.BROWN_BED, (new Item.Info()).a(1).a(CreativeModeTab.c))));
        a((ItemBlock) (new ItemBed(Blocks.GREEN_BED, (new Item.Info()).a(1).a(CreativeModeTab.c))));
        a((ItemBlock) (new ItemBed(Blocks.RED_BED, (new Item.Info()).a(1).a(CreativeModeTab.c))));
        a((ItemBlock) (new ItemBed(Blocks.BLACK_BED, (new Item.Info()).a(1).a(CreativeModeTab.c))));
        a("cookie", (Item) (new ItemFood(2, 0.1F, false, (new Item.Info()).a(CreativeModeTab.h))));
        a("filled_map", (Item) (new ItemWorldMap(new Item.Info())));
        a("shears", (Item) (new ItemShears((new Item.Info()).c(238).a(CreativeModeTab.i))));
        a("melon_slice", (Item) (new ItemFood(2, 0.3F, false, (new Item.Info()).a(CreativeModeTab.h))));
        a("dried_kelp", (Item) (new ItemFood(1, 0.3F, false, (new Item.Info()).a(CreativeModeTab.h))).f());
        a("pumpkin_seeds", (Item) (new ItemSeeds(Blocks.PUMPKIN_STEM, (new Item.Info()).a(CreativeModeTab.l))));
        a("melon_seeds", (Item) (new ItemSeeds(Blocks.MELON_STEM, (new Item.Info()).a(CreativeModeTab.l))));
        a("beef", (Item) (new ItemFood(3, 0.3F, true, (new Item.Info()).a(CreativeModeTab.h))));
        a("cooked_beef", (Item) (new ItemFood(8, 0.8F, true, (new Item.Info()).a(CreativeModeTab.h))));
        a("chicken", (Item) (new ItemFood(2, 0.3F, true, (new Item.Info()).a(CreativeModeTab.h))).a(new MobEffect(MobEffects.HUNGER, 600, 0), 0.3F));
        a("cooked_chicken", (Item) (new ItemFood(6, 0.6F, true, (new Item.Info()).a(CreativeModeTab.h))));
        a("rotten_flesh", (Item) (new ItemFood(4, 0.1F, true, (new Item.Info()).a(CreativeModeTab.h))).a(new MobEffect(MobEffects.HUNGER, 600, 0), 0.8F));
        a("ender_pearl", (Item) (new ItemEnderPearl((new Item.Info()).a(16).a(CreativeModeTab.f))));
        a("blaze_rod", new Item((new Item.Info()).a(CreativeModeTab.l)));
        a("ghast_tear", new Item((new Item.Info()).a(CreativeModeTab.k)));
        a("gold_nugget", new Item((new Item.Info()).a(CreativeModeTab.l)));
        a("nether_wart", (Item) (new ItemSeeds(Blocks.NETHER_WART, (new Item.Info()).a(CreativeModeTab.l))));
        a("potion", (Item) (new ItemPotion((new Item.Info()).a(1).a(CreativeModeTab.k))));
        ItemGlassBottle itemglassbottle = new ItemGlassBottle((new Item.Info()).a(CreativeModeTab.k));

        a("glass_bottle", (Item) itemglassbottle);
        a("spider_eye", (Item) (new ItemFood(2, 0.8F, false, (new Item.Info()).a(CreativeModeTab.h))).a(new MobEffect(MobEffects.POISON, 100, 0), 1.0F));
        a("fermented_spider_eye", new Item((new Item.Info()).a(CreativeModeTab.k)));
        a("blaze_powder", new Item((new Item.Info()).a(CreativeModeTab.k)));
        a("magma_cream", new Item((new Item.Info()).a(CreativeModeTab.k)));
        a(Blocks.BREWING_STAND, CreativeModeTab.k);
        a(Blocks.CAULDRON, CreativeModeTab.k);
        a("ender_eye", (Item) (new ItemEnderEye((new Item.Info()).a(CreativeModeTab.f))));
        a("glistering_melon_slice", new Item((new Item.Info()).a(CreativeModeTab.k)));
        a("bat_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.BAT, 4996656, 986895, (new Item.Info()).a(CreativeModeTab.f))));
        a("blaze_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.BLAZE, 16167425, 16775294, (new Item.Info()).a(CreativeModeTab.f))));
        a("cave_spider_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.CAVE_SPIDER, 803406, 11013646, (new Item.Info()).a(CreativeModeTab.f))));
        a("chicken_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.CHICKEN, 10592673, 16711680, (new Item.Info()).a(CreativeModeTab.f))));
        a("cod_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.COD, 12691306, 15058059, (new Item.Info()).a(CreativeModeTab.f))));
        a("cow_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.COW, 4470310, 10592673, (new Item.Info()).a(CreativeModeTab.f))));
        a("creeper_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.CREEPER, 894731, 0, (new Item.Info()).a(CreativeModeTab.f))));
        a("dolphin_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.DOLPHIN, 2243405, 16382457, (new Item.Info()).a(CreativeModeTab.f))));
        a("donkey_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.DONKEY, 5457209, 8811878, (new Item.Info()).a(CreativeModeTab.f))));
        a("drowned_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.DROWNED, 9433559, 7969893, (new Item.Info()).a(CreativeModeTab.f))));
        a("elder_guardian_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.ELDER_GUARDIAN, 13552826, 7632531, (new Item.Info()).a(CreativeModeTab.f))));
        a("enderman_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.ENDERMAN, 1447446, 0, (new Item.Info()).a(CreativeModeTab.f))));
        a("endermite_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.ENDERMITE, 1447446, 7237230, (new Item.Info()).a(CreativeModeTab.f))));
        a("evoker_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.EVOKER, 9804699, 1973274, (new Item.Info()).a(CreativeModeTab.f))));
        a("ghast_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.GHAST, 16382457, 12369084, (new Item.Info()).a(CreativeModeTab.f))));
        a("guardian_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.GUARDIAN, 5931634, 15826224, (new Item.Info()).a(CreativeModeTab.f))));
        a("horse_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.HORSE, 12623485, 15656192, (new Item.Info()).a(CreativeModeTab.f))));
        a("husk_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.HUSK, 7958625, 15125652, (new Item.Info()).a(CreativeModeTab.f))));
        a("llama_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.LLAMA, 12623485, 10051392, (new Item.Info()).a(CreativeModeTab.f))));
        a("magma_cube_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.MAGMA_CUBE, 3407872, 16579584, (new Item.Info()).a(CreativeModeTab.f))));
        a("mooshroom_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.MOOSHROOM, 10489616, 12040119, (new Item.Info()).a(CreativeModeTab.f))));
        a("mule_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.MULE, 1769984, 5321501, (new Item.Info()).a(CreativeModeTab.f))));
        a("ocelot_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.OCELOT, 15720061, 5653556, (new Item.Info()).a(CreativeModeTab.f))));
        a("parrot_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.PARROT, 894731, 16711680, (new Item.Info()).a(CreativeModeTab.f))));
        a("phantom_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.PHANTOM, 4411786, 8978176, (new Item.Info()).a(CreativeModeTab.f))));
        a("pig_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.PIG, 15771042, 14377823, (new Item.Info()).a(CreativeModeTab.f))));
        a("polar_bear_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.POLAR_BEAR, 15921906, 9803152, (new Item.Info()).a(CreativeModeTab.f))));
        a("pufferfish_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.PUFFERFISH, 16167425, 3654642, (new Item.Info()).a(CreativeModeTab.f))));
        a("rabbit_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.RABBIT, 10051392, 7555121, (new Item.Info()).a(CreativeModeTab.f))));
        a("salmon_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.SALMON, 10489616, 951412, (new Item.Info()).a(CreativeModeTab.f))));
        a("sheep_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.SHEEP, 15198183, 16758197, (new Item.Info()).a(CreativeModeTab.f))));
        a("shulker_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.SHULKER, 9725844, 5060690, (new Item.Info()).a(CreativeModeTab.f))));
        a("silverfish_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.SILVERFISH, 7237230, 3158064, (new Item.Info()).a(CreativeModeTab.f))));
        a("skeleton_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.SKELETON, 12698049, 4802889, (new Item.Info()).a(CreativeModeTab.f))));
        a("skeleton_horse_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.SKELETON_HORSE, 6842447, 15066584, (new Item.Info()).a(CreativeModeTab.f))));
        a("slime_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.SLIME, 5349438, 8306542, (new Item.Info()).a(CreativeModeTab.f))));
        a("spider_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.SPIDER, 3419431, 11013646, (new Item.Info()).a(CreativeModeTab.f))));
        a("squid_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.SQUID, 2243405, 7375001, (new Item.Info()).a(CreativeModeTab.f))));
        a("stray_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.STRAY, 6387319, 14543594, (new Item.Info()).a(CreativeModeTab.f))));
        a("tropical_fish_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.TROPICAL_FISH, 15690005, 16775663, (new Item.Info()).a(CreativeModeTab.f))));
        a("turtle_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.TURTLE, 15198183, 44975, (new Item.Info()).a(CreativeModeTab.f))));
        a("vex_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.VEX, 8032420, 15265265, (new Item.Info()).a(CreativeModeTab.f))));
        a("villager_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.VILLAGER, 5651507, 12422002, (new Item.Info()).a(CreativeModeTab.f))));
        a("vindicator_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.VINDICATOR, 9804699, 2580065, (new Item.Info()).a(CreativeModeTab.f))));
        a("witch_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.WITCH, 3407872, 5349438, (new Item.Info()).a(CreativeModeTab.f))));
        a("wither_skeleton_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.WITHER_SKELETON, 1315860, 4672845, (new Item.Info()).a(CreativeModeTab.f))));
        a("wolf_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.WOLF, 14144467, 13545366, (new Item.Info()).a(CreativeModeTab.f))));
        a("zombie_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.ZOMBIE, 44975, 7969893, (new Item.Info()).a(CreativeModeTab.f))));
        a("zombie_horse_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.ZOMBIE_HORSE, 3232308, 9945732, (new Item.Info()).a(CreativeModeTab.f))));
        a("zombie_pigman_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.ZOMBIE_PIGMAN, 15373203, 5009705, (new Item.Info()).a(CreativeModeTab.f))));
        a("zombie_villager_spawn_egg", (Item) (new ItemMonsterEgg(EntityTypes.ZOMBIE_VILLAGER, 5651507, 7969893, (new Item.Info()).a(CreativeModeTab.f))));
        a("experience_bottle", (Item) (new ItemExpBottle((new Item.Info()).a(CreativeModeTab.f).a(EnumItemRarity.UNCOMMON))));
        a("fire_charge", (Item) (new ItemFireball((new Item.Info()).a(CreativeModeTab.f))));
        a("writable_book", (Item) (new ItemBookAndQuill((new Item.Info()).a(1).a(CreativeModeTab.f))));
        a("written_book", (Item) (new ItemWrittenBook((new Item.Info()).a(16))));
        a("emerald", new Item((new Item.Info()).a(CreativeModeTab.l)));
        a("item_frame", (Item) (new ItemItemFrame((new Item.Info()).a(CreativeModeTab.c))));
        a(Blocks.FLOWER_POT, CreativeModeTab.c);
        a("carrot", (Item) (new ItemSeedFood(3, 0.6F, Blocks.CARROTS, (new Item.Info()).a(CreativeModeTab.h))));
        a("potato", (Item) (new ItemSeedFood(1, 0.3F, Blocks.POTATOES, (new Item.Info()).a(CreativeModeTab.h))));
        a("baked_potato", (Item) (new ItemFood(5, 0.6F, false, (new Item.Info()).a(CreativeModeTab.h))));
        a("poisonous_potato", (Item) (new ItemFood(2, 0.3F, false, (new Item.Info()).a(CreativeModeTab.h))).a(new MobEffect(MobEffects.POISON, 100, 0), 0.6F));
        a("map", (Item) (new ItemMapEmpty((new Item.Info()).a(CreativeModeTab.f))));
        a("golden_carrot", (Item) (new ItemFood(6, 1.2F, false, (new Item.Info()).a(CreativeModeTab.k))));
        a((ItemBlock) (new ItemBlockWallable(Blocks.SKELETON_SKULL, Blocks.SKELETON_WALL_SKULL, (new Item.Info()).a(CreativeModeTab.c).a(EnumItemRarity.UNCOMMON))));
        a((ItemBlock) (new ItemBlockWallable(Blocks.WITHER_SKELETON_SKULL, Blocks.WITHER_SKELETON_WALL_SKULL, (new Item.Info()).a(CreativeModeTab.c).a(EnumItemRarity.UNCOMMON))));
        a((ItemBlock) (new ItemSkullPlayer(Blocks.PLAYER_HEAD, Blocks.PLAYER_WALL_HEAD, (new Item.Info()).a(CreativeModeTab.c).a(EnumItemRarity.UNCOMMON))));
        a((ItemBlock) (new ItemBlockWallable(Blocks.ZOMBIE_HEAD, Blocks.ZOMBIE_WALL_HEAD, (new Item.Info()).a(CreativeModeTab.c).a(EnumItemRarity.UNCOMMON))));
        a((ItemBlock) (new ItemBlockWallable(Blocks.CREEPER_HEAD, Blocks.CREEPER_WALL_HEAD, (new Item.Info()).a(CreativeModeTab.c).a(EnumItemRarity.UNCOMMON))));
        a((ItemBlock) (new ItemBlockWallable(Blocks.DRAGON_HEAD, Blocks.DRAGON_WALL_HEAD, (new Item.Info()).a(CreativeModeTab.c).a(EnumItemRarity.UNCOMMON))));
        a("carrot_on_a_stick", (Item) (new ItemCarrotStick((new Item.Info()).c(25).a(CreativeModeTab.e))));
        a("nether_star", (Item) (new ItemNetherStar((new Item.Info()).a(CreativeModeTab.l).a(EnumItemRarity.UNCOMMON))));
        a("pumpkin_pie", (Item) (new ItemFood(8, 0.3F, false, (new Item.Info()).a(CreativeModeTab.h))));
        a("firework_rocket", (Item) (new ItemFireworks((new Item.Info()).a(CreativeModeTab.f))));
        a("firework_star", (Item) (new ItemFireworksCharge((new Item.Info()).a(CreativeModeTab.f))));
        a("enchanted_book", (Item) (new ItemEnchantedBook((new Item.Info()).a(1).a(EnumItemRarity.UNCOMMON))));
        a("nether_brick", new Item((new Item.Info()).a(CreativeModeTab.l)));
        a("quartz", new Item((new Item.Info()).a(CreativeModeTab.l)));
        a("tnt_minecart", (Item) (new ItemMinecart(EntityMinecartAbstract.EnumMinecartType.TNT, (new Item.Info()).a(1).a(CreativeModeTab.e))));
        a("hopper_minecart", (Item) (new ItemMinecart(EntityMinecartAbstract.EnumMinecartType.HOPPER, (new Item.Info()).a(1).a(CreativeModeTab.e))));
        a("prismarine_shard", new Item((new Item.Info()).a(CreativeModeTab.l)));
        a("prismarine_crystals", new Item((new Item.Info()).a(CreativeModeTab.l)));
        a("rabbit", (Item) (new ItemFood(3, 0.3F, true, (new Item.Info()).a(CreativeModeTab.h))));
        a("cooked_rabbit", (Item) (new ItemFood(5, 0.6F, true, (new Item.Info()).a(CreativeModeTab.h))));
        a("rabbit_stew", (Item) (new ItemSoup(10, (new Item.Info()).a(1).a(CreativeModeTab.h))));
        a("rabbit_foot", new Item((new Item.Info()).a(CreativeModeTab.k)));
        a("rabbit_hide", new Item((new Item.Info()).a(CreativeModeTab.l)));
        a("armor_stand", (Item) (new ItemArmorStand((new Item.Info()).a(16).a(CreativeModeTab.c))));
        a("iron_horse_armor", new Item((new Item.Info()).a(1).a(CreativeModeTab.f)));
        a("golden_horse_armor", new Item((new Item.Info()).a(1).a(CreativeModeTab.f)));
        a("diamond_horse_armor", new Item((new Item.Info()).a(1).a(CreativeModeTab.f)));
        a("lead", (Item) (new ItemLeash((new Item.Info()).a(CreativeModeTab.i))));
        a("name_tag", (Item) (new ItemNameTag((new Item.Info()).a(CreativeModeTab.i))));
        a("command_block_minecart", (Item) (new ItemMinecart(EntityMinecartAbstract.EnumMinecartType.COMMAND_BLOCK, (new Item.Info()).a(1))));
        a("mutton", (Item) (new ItemFood(2, 0.3F, true, (new Item.Info()).a(CreativeModeTab.h))));
        a("cooked_mutton", (Item) (new ItemFood(6, 0.8F, true, (new Item.Info()).a(CreativeModeTab.h))));
        a("white_banner", (Item) (new ItemBanner(Blocks.WHITE_BANNER, Blocks.WHITE_WALL_BANNER, (new Item.Info()).a(16).a(CreativeModeTab.c))));
        a("orange_banner", (Item) (new ItemBanner(Blocks.ORANGE_BANNER, Blocks.ORANGE_WALL_BANNER, (new Item.Info()).a(16).a(CreativeModeTab.c))));
        a("magenta_banner", (Item) (new ItemBanner(Blocks.MAGENTA_BANNER, Blocks.MAGENTA_WALL_BANNER, (new Item.Info()).a(16).a(CreativeModeTab.c))));
        a("light_blue_banner", (Item) (new ItemBanner(Blocks.LIGHT_BLUE_BANNER, Blocks.LIGHT_BLUE_WALL_BANNER, (new Item.Info()).a(16).a(CreativeModeTab.c))));
        a("yellow_banner", (Item) (new ItemBanner(Blocks.YELLOW_BANNER, Blocks.YELLOW_WALL_BANNER, (new Item.Info()).a(16).a(CreativeModeTab.c))));
        a("lime_banner", (Item) (new ItemBanner(Blocks.LIME_BANNER, Blocks.LIME_WALL_BANNER, (new Item.Info()).a(16).a(CreativeModeTab.c))));
        a("pink_banner", (Item) (new ItemBanner(Blocks.PINK_BANNER, Blocks.PINK_WALL_BANNER, (new Item.Info()).a(16).a(CreativeModeTab.c))));
        a("gray_banner", (Item) (new ItemBanner(Blocks.GRAY_BANNER, Blocks.GRAY_WALL_BANNER, (new Item.Info()).a(16).a(CreativeModeTab.c))));
        a("light_gray_banner", (Item) (new ItemBanner(Blocks.LIGHT_GRAY_BANNER, Blocks.LIGHT_GRAY_WALL_BANNER, (new Item.Info()).a(16).a(CreativeModeTab.c))));
        a("cyan_banner", (Item) (new ItemBanner(Blocks.CYAN_BANNER, Blocks.CYAN_WALL_BANNER, (new Item.Info()).a(16).a(CreativeModeTab.c))));
        a("purple_banner", (Item) (new ItemBanner(Blocks.PURPLE_BANNER, Blocks.PURPLE_WALL_BANNER, (new Item.Info()).a(16).a(CreativeModeTab.c))));
        a("blue_banner", (Item) (new ItemBanner(Blocks.BLUE_BANNER, Blocks.BLUE_WALL_BANNER, (new Item.Info()).a(16).a(CreativeModeTab.c))));
        a("brown_banner", (Item) (new ItemBanner(Blocks.BROWN_BANNER, Blocks.BROWN_WALL_BANNER, (new Item.Info()).a(16).a(CreativeModeTab.c))));
        a("green_banner", (Item) (new ItemBanner(Blocks.GREEN_BANNER, Blocks.GREEN_WALL_BANNER, (new Item.Info()).a(16).a(CreativeModeTab.c))));
        a("red_banner", (Item) (new ItemBanner(Blocks.RED_BANNER, Blocks.RED_WALL_BANNER, (new Item.Info()).a(16).a(CreativeModeTab.c))));
        a("black_banner", (Item) (new ItemBanner(Blocks.BLACK_BANNER, Blocks.BLACK_WALL_BANNER, (new Item.Info()).a(16).a(CreativeModeTab.c))));
        a("end_crystal", (Item) (new ItemEndCrystal((new Item.Info()).a(CreativeModeTab.c).a(EnumItemRarity.RARE))));
        a("chorus_fruit", (Item) (new ItemChorusFruit(4, 0.3F, (new Item.Info()).a(CreativeModeTab.l))).e());
        a("popped_chorus_fruit", new Item((new Item.Info()).a(CreativeModeTab.l)));
        a("beetroot", (Item) (new ItemFood(1, 0.6F, false, (new Item.Info()).a(CreativeModeTab.h))));
        a("beetroot_seeds", (Item) (new ItemSeeds(Blocks.BEETROOTS, (new Item.Info()).a(CreativeModeTab.l))));
        a("beetroot_soup", (Item) (new ItemSoup(6, (new Item.Info()).a(1).a(CreativeModeTab.h))));
        a("dragon_breath", new Item((new Item.Info()).a((Item) itemglassbottle).a(CreativeModeTab.k).a(EnumItemRarity.UNCOMMON)));
        a("splash_potion", (Item) (new ItemSplashPotion((new Item.Info()).a(1).a(CreativeModeTab.k))));
        a("spectral_arrow", (Item) (new ItemSpectralArrow((new Item.Info()).a(CreativeModeTab.j))));
        a("tipped_arrow", (Item) (new ItemTippedArrow((new Item.Info()).a(CreativeModeTab.j))));
        a("lingering_potion", (Item) (new ItemLingeringPotion((new Item.Info()).a(1).a(CreativeModeTab.k))));
        a("shield", (Item) (new ItemShield((new Item.Info()).c(336).a(CreativeModeTab.j))));
        a("elytra", (Item) (new ItemElytra((new Item.Info()).c(432).a(CreativeModeTab.e).a(EnumItemRarity.UNCOMMON))));
        a("spruce_boat", (Item) (new ItemBoat(EntityBoat.EnumBoatType.SPRUCE, (new Item.Info()).a(1).a(CreativeModeTab.e))));
        a("birch_boat", (Item) (new ItemBoat(EntityBoat.EnumBoatType.BIRCH, (new Item.Info()).a(1).a(CreativeModeTab.e))));
        a("jungle_boat", (Item) (new ItemBoat(EntityBoat.EnumBoatType.JUNGLE, (new Item.Info()).a(1).a(CreativeModeTab.e))));
        a("acacia_boat", (Item) (new ItemBoat(EntityBoat.EnumBoatType.ACACIA, (new Item.Info()).a(1).a(CreativeModeTab.e))));
        a("dark_oak_boat", (Item) (new ItemBoat(EntityBoat.EnumBoatType.DARK_OAK, (new Item.Info()).a(1).a(CreativeModeTab.e))));
        a("totem_of_undying", new Item((new Item.Info()).a(1).a(CreativeModeTab.j).a(EnumItemRarity.UNCOMMON)));
        a("shulker_shell", new Item((new Item.Info()).a(CreativeModeTab.l)));
        a("iron_nugget", new Item((new Item.Info()).a(CreativeModeTab.l)));
        a("knowledge_book", (Item) (new ItemKnowledgeBook((new Item.Info()).a(1))));
        a("debug_stick", (Item) (new ItemDebugStick((new Item.Info()).a(1))));
        a("music_disc_13", (Item) (new ItemRecord(1, SoundEffects.MUSIC_DISC_13, (new Item.Info()).a(1).a(CreativeModeTab.f).a(EnumItemRarity.RARE))));
        a("music_disc_cat", (Item) (new ItemRecord(2, SoundEffects.MUSIC_DISC_CAT, (new Item.Info()).a(1).a(CreativeModeTab.f).a(EnumItemRarity.RARE))));
        a("music_disc_blocks", (Item) (new ItemRecord(3, SoundEffects.MUSIC_DISC_BLOCKS, (new Item.Info()).a(1).a(CreativeModeTab.f).a(EnumItemRarity.RARE))));
        a("music_disc_chirp", (Item) (new ItemRecord(4, SoundEffects.MUSIC_DISC_CHIRP, (new Item.Info()).a(1).a(CreativeModeTab.f).a(EnumItemRarity.RARE))));
        a("music_disc_far", (Item) (new ItemRecord(5, SoundEffects.MUSIC_DISC_FAR, (new Item.Info()).a(1).a(CreativeModeTab.f).a(EnumItemRarity.RARE))));
        a("music_disc_mall", (Item) (new ItemRecord(6, SoundEffects.MUSIC_DISC_MALL, (new Item.Info()).a(1).a(CreativeModeTab.f).a(EnumItemRarity.RARE))));
        a("music_disc_mellohi", (Item) (new ItemRecord(7, SoundEffects.MUSIC_DISC_MELLOHI, (new Item.Info()).a(1).a(CreativeModeTab.f).a(EnumItemRarity.RARE))));
        a("music_disc_stal", (Item) (new ItemRecord(8, SoundEffects.MUSIC_DISC_STAL, (new Item.Info()).a(1).a(CreativeModeTab.f).a(EnumItemRarity.RARE))));
        a("music_disc_strad", (Item) (new ItemRecord(9, SoundEffects.MUSIC_DISC_STRAD, (new Item.Info()).a(1).a(CreativeModeTab.f).a(EnumItemRarity.RARE))));
        a("music_disc_ward", (Item) (new ItemRecord(10, SoundEffects.MUSIC_DISC_WARD, (new Item.Info()).a(1).a(CreativeModeTab.f).a(EnumItemRarity.RARE))));
        a("music_disc_11", (Item) (new ItemRecord(11, SoundEffects.MUSIC_DISC_11, (new Item.Info()).a(1).a(CreativeModeTab.f).a(EnumItemRarity.RARE))));
        a("music_disc_wait", (Item) (new ItemRecord(12, SoundEffects.MUSIC_DISC_WAIT, (new Item.Info()).a(1).a(CreativeModeTab.f).a(EnumItemRarity.RARE))));
        a("trident", (Item) (new ItemTrident((new Item.Info()).c(250).a(CreativeModeTab.j))));
        a("phantom_membrane", new Item((new Item.Info()).a(CreativeModeTab.k)));
        a("nautilus_shell", new Item((new Item.Info()).a(CreativeModeTab.l)));
        a("heart_of_the_sea", new Item((new Item.Info()).a(CreativeModeTab.l).a(EnumItemRarity.UNCOMMON)));
    }

    private static void b(Block block) {
        a(new ItemBlock(block, new Item.Info()));
    }

    private static void a(Block block, CreativeModeTab creativemodetab) {
        a(new ItemBlock(block, (new Item.Info()).a(creativemodetab)));
    }

    private static void a(ItemBlock itemblock) {
        a(itemblock.getBlock(), (Item) itemblock);
    }

    protected static void a(Block block, Item item) {
        a(IRegistry.BLOCK.getKey(block), item);
    }

    private static void a(String s, Item item) {
        a(new MinecraftKey(s), item);
    }

    private static void a(MinecraftKey minecraftkey, Item item) {
        if (item instanceof ItemBlock) {
            ((ItemBlock) item).a(Item.f, item);
        }

        IRegistry.ITEM.a(minecraftkey, (Object) item);
    }

    public boolean a(Tag<Item> tag) {
        return tag.isTagged(this);
    }

    public static class Info {

        private int a = 64;
        private int b;
        private Item c;
        private CreativeModeTab d;
        private EnumItemRarity e;

        public Info() {
            this.e = EnumItemRarity.COMMON;
        }

        public Item.Info a(int i) {
            if (this.b > 0) {
                throw new RuntimeException("Unable to have damage AND stack.");
            } else {
                this.a = i;
                return this;
            }
        }

        public Item.Info b(int i) {
            return this.b == 0 ? this.c(i) : this;
        }

        private Item.Info c(int i) {
            this.b = i;
            this.a = 1;
            return this;
        }

        public Item.Info a(Item item) {
            this.c = item;
            return this;
        }

        public Item.Info a(CreativeModeTab creativemodetab) {
            this.d = creativemodetab;
            return this;
        }

        public Item.Info a(EnumItemRarity enumitemrarity) {
            this.e = enumitemrarity;
            return this;
        }
    }
}
