package net.minecraft.world.level.block.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.protocol.game.PacketPlayOutTileEntityData;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.TagsItem;
import net.minecraft.world.InventoryUtils;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;

public class DecoratedPotBlockEntity extends TileEntity {

    private static final String TAG_SHARDS = "shards";
    private static final int SHARDS_IN_POT = 4;
    private boolean isBroken = false;
    private final List<Item> shards = (List) SystemUtils.make(new ArrayList(4), (arraylist) -> {
        arraylist.add(Items.BRICK);
        arraylist.add(Items.BRICK);
        arraylist.add(Items.BRICK);
        arraylist.add(Items.BRICK);
    });

    public DecoratedPotBlockEntity(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.DECORATED_POT, blockposition, iblockdata);
    }

    @Override
    protected void saveAdditional(NBTTagCompound nbttagcompound) {
        super.saveAdditional(nbttagcompound);
        saveShards(this.shards, nbttagcompound);
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        if (nbttagcompound.contains("shards", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList("shards", 8);

            this.shards.clear();
            int i = Math.min(4, nbttaglist.size());

            int j;

            for (j = 0; j < i; ++j) {
                NBTBase nbtbase = nbttaglist.get(j);

                if (nbtbase instanceof NBTTagString) {
                    NBTTagString nbttagstring = (NBTTagString) nbtbase;

                    this.shards.add((Item) BuiltInRegistries.ITEM.get(new MinecraftKey(nbttagstring.getAsString())));
                } else {
                    this.shards.add(Items.BRICK);
                }
            }

            j = 4 - i;

            for (int k = 0; k < j; ++k) {
                this.shards.add(Items.BRICK);
            }
        }

    }

    @Override
    public PacketPlayOutTileEntityData getUpdatePacket() {
        return PacketPlayOutTileEntityData.create(this);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    public static void saveShards(List<Item> list, NBTTagCompound nbttagcompound) {
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            Item item = (Item) iterator.next();

            nbttaglist.add(NBTTagString.valueOf(BuiltInRegistries.ITEM.getKey(item).toString()));
        }

        nbttagcompound.put("shards", nbttaglist);
    }

    public ItemStack getItem() {
        ItemStack itemstack = new ItemStack(Blocks.DECORATED_POT);
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        saveShards(this.shards, nbttagcompound);
        ItemBlock.setBlockEntityData(itemstack, TileEntityTypes.DECORATED_POT, nbttagcompound);
        return itemstack;
    }

    public List<Item> getShards() {
        return this.shards;
    }

    public void playerDestroy(World world, BlockPosition blockposition, ItemStack itemstack, EntityHuman entityhuman) {
        if (entityhuman.isCreative()) {
            this.isBroken = true;
        } else {
            if (itemstack.is(TagsItem.BREAKS_DECORATED_POTS) && !EnchantmentManager.hasSilkTouch(itemstack)) {
                List<Item> list = this.getShards();
                NonNullList<ItemStack> nonnulllist = NonNullList.createWithCapacity(list.size());

                nonnulllist.addAll(0, list.stream().map(Item::getDefaultInstance).toList());
                InventoryUtils.dropContents(world, blockposition, nonnulllist);
                this.isBroken = true;
                world.playSound((EntityHuman) null, blockposition, SoundEffects.DECORATED_POT_SHATTER, SoundCategory.PLAYERS, 1.0F, 1.0F);
            }

        }
    }

    public boolean isBroken() {
        return this.isBroken;
    }

    public EnumDirection getDirection() {
        return (EnumDirection) this.getBlockState().getValue(BlockProperties.HORIZONTAL_FACING);
    }

    public void setFromItem(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = ItemBlock.getBlockEntityData(itemstack);

        if (nbttagcompound != null) {
            this.load(nbttagcompound);
        }

    }
}
