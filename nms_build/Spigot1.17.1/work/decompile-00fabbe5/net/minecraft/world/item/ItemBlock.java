package net.minecraft.world.item;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.item.context.ItemActionContext;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockShulkerBox;
import net.minecraft.world.level.block.SoundEffectType;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.IBlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class ItemBlock extends Item {

    public static final String BLOCK_ENTITY_TAG = "BlockEntityTag";
    public static final String BLOCK_STATE_TAG = "BlockStateTag";
    @Deprecated
    private final Block block;

    public ItemBlock(Block block, Item.Info item_info) {
        super(item_info);
        this.block = block;
    }

    @Override
    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        EnumInteractionResult enuminteractionresult = this.a(new BlockActionContext(itemactioncontext));

        if (!enuminteractionresult.a() && this.isFood()) {
            EnumInteractionResult enuminteractionresult1 = this.a(itemactioncontext.getWorld(), itemactioncontext.getEntity(), itemactioncontext.getHand()).a();

            return enuminteractionresult1 == EnumInteractionResult.CONSUME ? EnumInteractionResult.CONSUME_PARTIAL : enuminteractionresult1;
        } else {
            return enuminteractionresult;
        }
    }

    public EnumInteractionResult a(BlockActionContext blockactioncontext) {
        if (!blockactioncontext.b()) {
            return EnumInteractionResult.FAIL;
        } else {
            BlockActionContext blockactioncontext1 = this.b(blockactioncontext);

            if (blockactioncontext1 == null) {
                return EnumInteractionResult.FAIL;
            } else {
                IBlockData iblockdata = this.c(blockactioncontext1);

                if (iblockdata == null) {
                    return EnumInteractionResult.FAIL;
                } else if (!this.a(blockactioncontext1, iblockdata)) {
                    return EnumInteractionResult.FAIL;
                } else {
                    BlockPosition blockposition = blockactioncontext1.getClickPosition();
                    World world = blockactioncontext1.getWorld();
                    EntityHuman entityhuman = blockactioncontext1.getEntity();
                    ItemStack itemstack = blockactioncontext1.getItemStack();
                    IBlockData iblockdata1 = world.getType(blockposition);

                    if (iblockdata1.a(iblockdata.getBlock())) {
                        iblockdata1 = this.a(blockposition, world, itemstack, iblockdata1);
                        this.a(blockposition, world, entityhuman, itemstack, iblockdata1);
                        iblockdata1.getBlock().postPlace(world, blockposition, iblockdata1, entityhuman, itemstack);
                        if (entityhuman instanceof EntityPlayer) {
                            CriterionTriggers.PLACED_BLOCK.a((EntityPlayer) entityhuman, blockposition, itemstack);
                        }
                    }

                    SoundEffectType soundeffecttype = iblockdata1.getStepSound();

                    world.playSound(entityhuman, blockposition, this.a(iblockdata1), SoundCategory.BLOCKS, (soundeffecttype.getVolume() + 1.0F) / 2.0F, soundeffecttype.getPitch() * 0.8F);
                    world.a((Entity) entityhuman, GameEvent.BLOCK_PLACE, blockposition);
                    if (entityhuman == null || !entityhuman.getAbilities().instabuild) {
                        itemstack.subtract(1);
                    }

                    return EnumInteractionResult.a(world.isClientSide);
                }
            }
        }
    }

    protected SoundEffect a(IBlockData iblockdata) {
        return iblockdata.getStepSound().getPlaceSound();
    }

    @Nullable
    public BlockActionContext b(BlockActionContext blockactioncontext) {
        return blockactioncontext;
    }

    protected boolean a(BlockPosition blockposition, World world, @Nullable EntityHuman entityhuman, ItemStack itemstack, IBlockData iblockdata) {
        return a(world, entityhuman, blockposition, itemstack);
    }

    @Nullable
    protected IBlockData c(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = this.getBlock().getPlacedState(blockactioncontext);

        return iblockdata != null && this.b(blockactioncontext, iblockdata) ? iblockdata : null;
    }

    private IBlockData a(BlockPosition blockposition, World world, ItemStack itemstack, IBlockData iblockdata) {
        IBlockData iblockdata1 = iblockdata;
        NBTTagCompound nbttagcompound = itemstack.getTag();

        if (nbttagcompound != null) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("BlockStateTag");
            BlockStateList<Block, IBlockData> blockstatelist = iblockdata.getBlock().getStates();
            Iterator iterator = nbttagcompound1.getKeys().iterator();

            while (iterator.hasNext()) {
                String s = (String) iterator.next();
                IBlockState<?> iblockstate = blockstatelist.a(s);

                if (iblockstate != null) {
                    String s1 = nbttagcompound1.get(s).asString();

                    iblockdata1 = a(iblockdata1, iblockstate, s1);
                }
            }
        }

        if (iblockdata1 != iblockdata) {
            world.setTypeAndData(blockposition, iblockdata1, 2);
        }

        return iblockdata1;
    }

    private static <T extends Comparable<T>> IBlockData a(IBlockData iblockdata, IBlockState<T> iblockstate, String s) {
        return (IBlockData) iblockstate.b(s).map((comparable) -> {
            return (IBlockData) iblockdata.set(iblockstate, comparable);
        }).orElse(iblockdata);
    }

    protected boolean b(BlockActionContext blockactioncontext, IBlockData iblockdata) {
        EntityHuman entityhuman = blockactioncontext.getEntity();
        VoxelShapeCollision voxelshapecollision = entityhuman == null ? VoxelShapeCollision.a() : VoxelShapeCollision.a((Entity) entityhuman);

        return (!this.isCheckCollisions() || iblockdata.canPlace(blockactioncontext.getWorld(), blockactioncontext.getClickPosition())) && blockactioncontext.getWorld().a(iblockdata, blockactioncontext.getClickPosition(), voxelshapecollision);
    }

    protected boolean isCheckCollisions() {
        return true;
    }

    protected boolean a(BlockActionContext blockactioncontext, IBlockData iblockdata) {
        return blockactioncontext.getWorld().setTypeAndData(blockactioncontext.getClickPosition(), iblockdata, 11);
    }

    public static boolean a(World world, @Nullable EntityHuman entityhuman, BlockPosition blockposition, ItemStack itemstack) {
        MinecraftServer minecraftserver = world.getMinecraftServer();

        if (minecraftserver == null) {
            return false;
        } else {
            NBTTagCompound nbttagcompound = itemstack.b("BlockEntityTag");

            if (nbttagcompound != null) {
                TileEntity tileentity = world.getTileEntity(blockposition);

                if (tileentity != null) {
                    if (!world.isClientSide && tileentity.isFilteredNBT() && (entityhuman == null || !entityhuman.isCreativeAndOp())) {
                        return false;
                    }

                    NBTTagCompound nbttagcompound1 = tileentity.save(new NBTTagCompound());
                    NBTTagCompound nbttagcompound2 = nbttagcompound1.clone();

                    nbttagcompound1.a(nbttagcompound);
                    nbttagcompound1.setInt("x", blockposition.getX());
                    nbttagcompound1.setInt("y", blockposition.getY());
                    nbttagcompound1.setInt("z", blockposition.getZ());
                    if (!nbttagcompound1.equals(nbttagcompound2)) {
                        tileentity.load(nbttagcompound1);
                        tileentity.update();
                        return true;
                    }
                }
            }

            return false;
        }
    }

    @Override
    public String getName() {
        return this.getBlock().h();
    }

    @Override
    public void a(CreativeModeTab creativemodetab, NonNullList<ItemStack> nonnulllist) {
        if (this.a(creativemodetab)) {
            this.getBlock().a(creativemodetab, nonnulllist);
        }

    }

    @Override
    public void a(ItemStack itemstack, @Nullable World world, List<IChatBaseComponent> list, TooltipFlag tooltipflag) {
        super.a(itemstack, world, list, tooltipflag);
        this.getBlock().a(itemstack, (IBlockAccess) world, list, tooltipflag);
    }

    public Block getBlock() {
        return this.block;
    }

    public void a(Map<Block, Item> map, Item item) {
        map.put(this.getBlock(), item);
    }

    @Override
    public boolean P_() {
        return !(this.block instanceof BlockShulkerBox);
    }

    @Override
    public void a(EntityItem entityitem) {
        if (this.block instanceof BlockShulkerBox) {
            NBTTagCompound nbttagcompound = entityitem.getItemStack().getTag();

            if (nbttagcompound != null) {
                NBTTagList nbttaglist = nbttagcompound.getCompound("BlockEntityTag").getList("Items", 10);
                Stream stream = nbttaglist.stream();

                Objects.requireNonNull(NBTTagCompound.class);
                ItemLiquidUtil.a(entityitem, stream.map(NBTTagCompound.class::cast).map(ItemStack::a));
            }
        }

    }
}
