package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsItem;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ContainerUtil;
import net.minecraft.world.IInventory;
import net.minecraft.world.IWorldInventory;
import net.minecraft.world.entity.EntityExperienceOrb;
import net.minecraft.world.entity.player.AutoRecipeStackManager;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.inventory.AutoRecipeOutput;
import net.minecraft.world.inventory.IContainerProperties;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.IRecipe;
import net.minecraft.world.item.crafting.RecipeCooking;
import net.minecraft.world.item.crafting.Recipes;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockFurnace;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec3D;

public abstract class TileEntityFurnace extends TileEntityContainer implements IWorldInventory, RecipeHolder, AutoRecipeOutput {

    protected static final int SLOT_INPUT = 0;
    protected static final int SLOT_FUEL = 1;
    protected static final int SLOT_RESULT = 2;
    public static final int DATA_LIT_TIME = 0;
    private static final int[] SLOTS_FOR_UP = new int[]{0};
    private static final int[] SLOTS_FOR_DOWN = new int[]{2, 1};
    private static final int[] SLOTS_FOR_SIDES = new int[]{1};
    public static final int DATA_LIT_DURATION = 1;
    public static final int DATA_COOKING_PROGRESS = 2;
    public static final int DATA_COOKING_TOTAL_TIME = 3;
    public static final int NUM_DATA_VALUES = 4;
    public static final int BURN_TIME_STANDARD = 200;
    public static final int BURN_COOL_SPEED = 2;
    protected NonNullList<ItemStack> items;
    public int litTime;
    int litDuration;
    public int cookingProgress;
    public int cookingTotalTime;
    protected final IContainerProperties dataAccess;
    private final Object2IntOpenHashMap<MinecraftKey> recipesUsed;
    private final Recipes<? extends RecipeCooking> recipeType;

    protected TileEntityFurnace(TileEntityTypes<?> tileentitytypes, BlockPosition blockposition, IBlockData iblockdata, Recipes<? extends RecipeCooking> recipes) {
        super(tileentitytypes, blockposition, iblockdata);
        this.items = NonNullList.withSize(3, ItemStack.EMPTY);
        this.dataAccess = new IContainerProperties() {
            @Override
            public int get(int i) {
                switch (i) {
                    case 0:
                        return TileEntityFurnace.this.litTime;
                    case 1:
                        return TileEntityFurnace.this.litDuration;
                    case 2:
                        return TileEntityFurnace.this.cookingProgress;
                    case 3:
                        return TileEntityFurnace.this.cookingTotalTime;
                    default:
                        return 0;
                }
            }

            @Override
            public void set(int i, int j) {
                switch (i) {
                    case 0:
                        TileEntityFurnace.this.litTime = j;
                        break;
                    case 1:
                        TileEntityFurnace.this.litDuration = j;
                        break;
                    case 2:
                        TileEntityFurnace.this.cookingProgress = j;
                        break;
                    case 3:
                        TileEntityFurnace.this.cookingTotalTime = j;
                }

            }

            @Override
            public int getCount() {
                return 4;
            }
        };
        this.recipesUsed = new Object2IntOpenHashMap();
        this.recipeType = recipes;
    }

    public static Map<Item, Integer> getFuel() {
        Map<Item, Integer> map = Maps.newLinkedHashMap();

        add(map, (IMaterial) Items.LAVA_BUCKET, 20000);
        add(map, (IMaterial) Blocks.COAL_BLOCK, 16000);
        add(map, (IMaterial) Items.BLAZE_ROD, 2400);
        add(map, (IMaterial) Items.COAL, 1600);
        add(map, (IMaterial) Items.CHARCOAL, 1600);
        add(map, (Tag) TagsItem.LOGS, 300);
        add(map, (Tag) TagsItem.PLANKS, 300);
        add(map, (Tag) TagsItem.WOODEN_STAIRS, 300);
        add(map, (Tag) TagsItem.WOODEN_SLABS, 150);
        add(map, (Tag) TagsItem.WOODEN_TRAPDOORS, 300);
        add(map, (Tag) TagsItem.WOODEN_PRESSURE_PLATES, 300);
        add(map, (IMaterial) Blocks.OAK_FENCE, 300);
        add(map, (IMaterial) Blocks.BIRCH_FENCE, 300);
        add(map, (IMaterial) Blocks.SPRUCE_FENCE, 300);
        add(map, (IMaterial) Blocks.JUNGLE_FENCE, 300);
        add(map, (IMaterial) Blocks.DARK_OAK_FENCE, 300);
        add(map, (IMaterial) Blocks.ACACIA_FENCE, 300);
        add(map, (IMaterial) Blocks.OAK_FENCE_GATE, 300);
        add(map, (IMaterial) Blocks.BIRCH_FENCE_GATE, 300);
        add(map, (IMaterial) Blocks.SPRUCE_FENCE_GATE, 300);
        add(map, (IMaterial) Blocks.JUNGLE_FENCE_GATE, 300);
        add(map, (IMaterial) Blocks.DARK_OAK_FENCE_GATE, 300);
        add(map, (IMaterial) Blocks.ACACIA_FENCE_GATE, 300);
        add(map, (IMaterial) Blocks.NOTE_BLOCK, 300);
        add(map, (IMaterial) Blocks.BOOKSHELF, 300);
        add(map, (IMaterial) Blocks.LECTERN, 300);
        add(map, (IMaterial) Blocks.JUKEBOX, 300);
        add(map, (IMaterial) Blocks.CHEST, 300);
        add(map, (IMaterial) Blocks.TRAPPED_CHEST, 300);
        add(map, (IMaterial) Blocks.CRAFTING_TABLE, 300);
        add(map, (IMaterial) Blocks.DAYLIGHT_DETECTOR, 300);
        add(map, (Tag) TagsItem.BANNERS, 300);
        add(map, (IMaterial) Items.BOW, 300);
        add(map, (IMaterial) Items.FISHING_ROD, 300);
        add(map, (IMaterial) Blocks.LADDER, 300);
        add(map, (Tag) TagsItem.SIGNS, 200);
        add(map, (IMaterial) Items.WOODEN_SHOVEL, 200);
        add(map, (IMaterial) Items.WOODEN_SWORD, 200);
        add(map, (IMaterial) Items.WOODEN_HOE, 200);
        add(map, (IMaterial) Items.WOODEN_AXE, 200);
        add(map, (IMaterial) Items.WOODEN_PICKAXE, 200);
        add(map, (Tag) TagsItem.WOODEN_DOORS, 200);
        add(map, (Tag) TagsItem.BOATS, 1200);
        add(map, (Tag) TagsItem.WOOL, 100);
        add(map, (Tag) TagsItem.WOODEN_BUTTONS, 100);
        add(map, (IMaterial) Items.STICK, 100);
        add(map, (Tag) TagsItem.SAPLINGS, 100);
        add(map, (IMaterial) Items.BOWL, 100);
        add(map, (Tag) TagsItem.CARPETS, 67);
        add(map, (IMaterial) Blocks.DRIED_KELP_BLOCK, 4001);
        add(map, (IMaterial) Items.CROSSBOW, 300);
        add(map, (IMaterial) Blocks.BAMBOO, 50);
        add(map, (IMaterial) Blocks.DEAD_BUSH, 100);
        add(map, (IMaterial) Blocks.SCAFFOLDING, 400);
        add(map, (IMaterial) Blocks.LOOM, 300);
        add(map, (IMaterial) Blocks.BARREL, 300);
        add(map, (IMaterial) Blocks.CARTOGRAPHY_TABLE, 300);
        add(map, (IMaterial) Blocks.FLETCHING_TABLE, 300);
        add(map, (IMaterial) Blocks.SMITHING_TABLE, 300);
        add(map, (IMaterial) Blocks.COMPOSTER, 300);
        add(map, (IMaterial) Blocks.AZALEA, 100);
        add(map, (IMaterial) Blocks.FLOWERING_AZALEA, 100);
        return map;
    }

    private static boolean isNeverAFurnaceFuel(Item item) {
        return TagsItem.NON_FLAMMABLE_WOOD.contains(item);
    }

    private static void add(Map<Item, Integer> map, Tag<Item> tag, int i) {
        Iterator iterator = tag.getValues().iterator();

        while (iterator.hasNext()) {
            Item item = (Item) iterator.next();

            if (!isNeverAFurnaceFuel(item)) {
                map.put(item, i);
            }
        }

    }

    private static void add(Map<Item, Integer> map, IMaterial imaterial, int i) {
        Item item = imaterial.asItem();

        if (isNeverAFurnaceFuel(item)) {
            if (SharedConstants.IS_RUNNING_IN_IDE) {
                throw (IllegalStateException) SystemUtils.pauseInIde(new IllegalStateException("A developer tried to explicitly make fire resistant item " + item.getName((ItemStack) null).getString() + " a furnace fuel. That will not work!"));
            }
        } else {
            map.put(item, i);
        }
    }

    private boolean isLit() {
        return this.litTime > 0;
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerUtil.loadAllItems(nbttagcompound, this.items);
        this.litTime = nbttagcompound.getShort("BurnTime");
        this.cookingProgress = nbttagcompound.getShort("CookTime");
        this.cookingTotalTime = nbttagcompound.getShort("CookTimeTotal");
        this.litDuration = this.getBurnDuration((ItemStack) this.items.get(1));
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("RecipesUsed");
        Iterator iterator = nbttagcompound1.getAllKeys().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();

            this.recipesUsed.put(new MinecraftKey(s), nbttagcompound1.getInt(s));
        }

    }

    @Override
    protected void saveAdditional(NBTTagCompound nbttagcompound) {
        super.saveAdditional(nbttagcompound);
        nbttagcompound.putShort("BurnTime", (short) this.litTime);
        nbttagcompound.putShort("CookTime", (short) this.cookingProgress);
        nbttagcompound.putShort("CookTimeTotal", (short) this.cookingTotalTime);
        ContainerUtil.saveAllItems(nbttagcompound, this.items);
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();

        this.recipesUsed.forEach((minecraftkey, integer) -> {
            nbttagcompound1.putInt(minecraftkey.toString(), integer);
        });
        nbttagcompound.put("RecipesUsed", nbttagcompound1);
    }

    public static void serverTick(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityFurnace tileentityfurnace) {
        boolean flag = tileentityfurnace.isLit();
        boolean flag1 = false;

        if (tileentityfurnace.isLit()) {
            --tileentityfurnace.litTime;
        }

        ItemStack itemstack = (ItemStack) tileentityfurnace.items.get(1);

        if (!tileentityfurnace.isLit() && (itemstack.isEmpty() || ((ItemStack) tileentityfurnace.items.get(0)).isEmpty())) {
            if (!tileentityfurnace.isLit() && tileentityfurnace.cookingProgress > 0) {
                tileentityfurnace.cookingProgress = MathHelper.clamp(tileentityfurnace.cookingProgress - 2, (int) 0, tileentityfurnace.cookingTotalTime);
            }
        } else {
            IRecipe<?> irecipe = (IRecipe) world.getRecipeManager().getRecipeFor(tileentityfurnace.recipeType, tileentityfurnace, world).orElse((Object) null);
            int i = tileentityfurnace.getMaxStackSize();

            if (!tileentityfurnace.isLit() && canBurn(irecipe, tileentityfurnace.items, i)) {
                tileentityfurnace.litTime = tileentityfurnace.getBurnDuration(itemstack);
                tileentityfurnace.litDuration = tileentityfurnace.litTime;
                if (tileentityfurnace.isLit()) {
                    flag1 = true;
                    if (!itemstack.isEmpty()) {
                        Item item = itemstack.getItem();

                        itemstack.shrink(1);
                        if (itemstack.isEmpty()) {
                            Item item1 = item.getCraftingRemainingItem();

                            tileentityfurnace.items.set(1, item1 == null ? ItemStack.EMPTY : new ItemStack(item1));
                        }
                    }
                }
            }

            if (tileentityfurnace.isLit() && canBurn(irecipe, tileentityfurnace.items, i)) {
                ++tileentityfurnace.cookingProgress;
                if (tileentityfurnace.cookingProgress == tileentityfurnace.cookingTotalTime) {
                    tileentityfurnace.cookingProgress = 0;
                    tileentityfurnace.cookingTotalTime = getTotalCookTime(world, tileentityfurnace.recipeType, tileentityfurnace);
                    if (burn(irecipe, tileentityfurnace.items, i)) {
                        tileentityfurnace.setRecipeUsed(irecipe);
                    }

                    flag1 = true;
                }
            } else {
                tileentityfurnace.cookingProgress = 0;
            }
        }

        if (flag != tileentityfurnace.isLit()) {
            flag1 = true;
            iblockdata = (IBlockData) iblockdata.setValue(BlockFurnace.LIT, tileentityfurnace.isLit());
            world.setBlock(blockposition, iblockdata, 3);
        }

        if (flag1) {
            setChanged(world, blockposition, iblockdata);
        }

    }

    private static boolean canBurn(@Nullable IRecipe<?> irecipe, NonNullList<ItemStack> nonnulllist, int i) {
        if (!((ItemStack) nonnulllist.get(0)).isEmpty() && irecipe != null) {
            ItemStack itemstack = irecipe.getResultItem();

            if (itemstack.isEmpty()) {
                return false;
            } else {
                ItemStack itemstack1 = (ItemStack) nonnulllist.get(2);

                return itemstack1.isEmpty() ? true : (!itemstack1.sameItem(itemstack) ? false : (itemstack1.getCount() < i && itemstack1.getCount() < itemstack1.getMaxStackSize() ? true : itemstack1.getCount() < itemstack.getMaxStackSize()));
            }
        } else {
            return false;
        }
    }

    private static boolean burn(@Nullable IRecipe<?> irecipe, NonNullList<ItemStack> nonnulllist, int i) {
        if (irecipe != null && canBurn(irecipe, nonnulllist, i)) {
            ItemStack itemstack = (ItemStack) nonnulllist.get(0);
            ItemStack itemstack1 = irecipe.getResultItem();
            ItemStack itemstack2 = (ItemStack) nonnulllist.get(2);

            if (itemstack2.isEmpty()) {
                nonnulllist.set(2, itemstack1.copy());
            } else if (itemstack2.is(itemstack1.getItem())) {
                itemstack2.grow(1);
            }

            if (itemstack.is(Blocks.WET_SPONGE.asItem()) && !((ItemStack) nonnulllist.get(1)).isEmpty() && ((ItemStack) nonnulllist.get(1)).is(Items.BUCKET)) {
                nonnulllist.set(1, new ItemStack(Items.WATER_BUCKET));
            }

            itemstack.shrink(1);
            return true;
        } else {
            return false;
        }
    }

    protected int getBurnDuration(ItemStack itemstack) {
        if (itemstack.isEmpty()) {
            return 0;
        } else {
            Item item = itemstack.getItem();

            return (Integer) getFuel().getOrDefault(item, 0);
        }
    }

    private static int getTotalCookTime(World world, Recipes<? extends RecipeCooking> recipes, IInventory iinventory) {
        return (Integer) world.getRecipeManager().getRecipeFor(recipes, iinventory, world).map(RecipeCooking::getCookingTime).orElse(200);
    }

    public static boolean isFuel(ItemStack itemstack) {
        return getFuel().containsKey(itemstack.getItem());
    }

    @Override
    public int[] getSlotsForFace(EnumDirection enumdirection) {
        return enumdirection == EnumDirection.DOWN ? TileEntityFurnace.SLOTS_FOR_DOWN : (enumdirection == EnumDirection.UP ? TileEntityFurnace.SLOTS_FOR_UP : TileEntityFurnace.SLOTS_FOR_SIDES);
    }

    @Override
    public boolean canPlaceItemThroughFace(int i, ItemStack itemstack, @Nullable EnumDirection enumdirection) {
        return this.canPlaceItem(i, itemstack);
    }

    @Override
    public boolean canTakeItemThroughFace(int i, ItemStack itemstack, EnumDirection enumdirection) {
        return enumdirection == EnumDirection.DOWN && i == 1 ? itemstack.is(Items.WATER_BUCKET) || itemstack.is(Items.BUCKET) : true;
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        Iterator iterator = this.items.iterator();

        ItemStack itemstack;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            itemstack = (ItemStack) iterator.next();
        } while (itemstack.isEmpty());

        return false;
    }

    @Override
    public ItemStack getItem(int i) {
        return (ItemStack) this.items.get(i);
    }

    @Override
    public ItemStack removeItem(int i, int j) {
        return ContainerUtil.removeItem(this.items, i, j);
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        return ContainerUtil.takeItem(this.items, i);
    }

    @Override
    public void setItem(int i, ItemStack itemstack) {
        ItemStack itemstack1 = (ItemStack) this.items.get(i);
        boolean flag = !itemstack.isEmpty() && itemstack.sameItem(itemstack1) && ItemStack.tagMatches(itemstack, itemstack1);

        this.items.set(i, itemstack);
        if (itemstack.getCount() > this.getMaxStackSize()) {
            itemstack.setCount(this.getMaxStackSize());
        }

        if (i == 0 && !flag) {
            this.cookingTotalTime = getTotalCookTime(this.level, this.recipeType, this);
            this.cookingProgress = 0;
            this.setChanged();
        }

    }

    @Override
    public boolean stillValid(EntityHuman entityhuman) {
        return this.level.getBlockEntity(this.worldPosition) != this ? false : entityhuman.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public boolean canPlaceItem(int i, ItemStack itemstack) {
        if (i == 2) {
            return false;
        } else if (i != 1) {
            return true;
        } else {
            ItemStack itemstack1 = (ItemStack) this.items.get(1);

            return isFuel(itemstack) || itemstack.is(Items.BUCKET) && !itemstack1.is(Items.BUCKET);
        }
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public void setRecipeUsed(@Nullable IRecipe<?> irecipe) {
        if (irecipe != null) {
            MinecraftKey minecraftkey = irecipe.getId();

            this.recipesUsed.addTo(minecraftkey, 1);
        }

    }

    @Nullable
    @Override
    public IRecipe<?> getRecipeUsed() {
        return null;
    }

    @Override
    public void awardUsedRecipes(EntityHuman entityhuman) {}

    public void awardUsedRecipesAndPopExperience(EntityPlayer entityplayer) {
        List<IRecipe<?>> list = this.getRecipesToAwardAndPopExperience(entityplayer.getLevel(), entityplayer.position());

        entityplayer.awardRecipes(list);
        this.recipesUsed.clear();
    }

    public List<IRecipe<?>> getRecipesToAwardAndPopExperience(WorldServer worldserver, Vec3D vec3d) {
        List<IRecipe<?>> list = Lists.newArrayList();
        ObjectIterator objectiterator = this.recipesUsed.object2IntEntrySet().iterator();

        while (objectiterator.hasNext()) {
            Entry<MinecraftKey> entry = (Entry) objectiterator.next();

            worldserver.getRecipeManager().byKey((MinecraftKey) entry.getKey()).ifPresent((irecipe) -> {
                list.add(irecipe);
                createExperience(worldserver, vec3d, entry.getIntValue(), ((RecipeCooking) irecipe).getExperience());
            });
        }

        return list;
    }

    private static void createExperience(WorldServer worldserver, Vec3D vec3d, int i, float f) {
        int j = MathHelper.floor((float) i * f);
        float f1 = MathHelper.frac((float) i * f);

        if (f1 != 0.0F && Math.random() < (double) f1) {
            ++j;
        }

        EntityExperienceOrb.award(worldserver, vec3d, j);
    }

    @Override
    public void fillStackedContents(AutoRecipeStackManager autorecipestackmanager) {
        Iterator iterator = this.items.iterator();

        while (iterator.hasNext()) {
            ItemStack itemstack = (ItemStack) iterator.next();

            autorecipestackmanager.accountStack(itemstack);
        }

    }
}
