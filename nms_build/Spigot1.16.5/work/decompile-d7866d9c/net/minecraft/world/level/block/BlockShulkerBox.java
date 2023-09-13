package net.minecraft.world.level.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.IInventory;
import net.minecraft.world.ShulkerUtil;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.monster.piglin.PiglinAI;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.item.EnumColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityShulkerBox;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockStateEnum;
import net.minecraft.world.level.material.EnumPistonReaction;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class BlockShulkerBox extends BlockTileEntity {

    public static final BlockStateEnum<EnumDirection> a = BlockDirectional.FACING;
    public static final MinecraftKey b = new MinecraftKey("contents");
    @Nullable
    public final EnumColor color;

    public BlockShulkerBox(@Nullable EnumColor enumcolor, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.color = enumcolor;
        this.j((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockShulkerBox.a, EnumDirection.UP));
    }

    @Override
    public TileEntity createTile(IBlockAccess iblockaccess) {
        return new TileEntityShulkerBox(this.color);
    }

    @Override
    public EnumRenderType b(IBlockData iblockdata) {
        return EnumRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (world.isClientSide) {
            return EnumInteractionResult.SUCCESS;
        } else if (entityhuman.isSpectator()) {
            return EnumInteractionResult.CONSUME;
        } else {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityShulkerBox) {
                TileEntityShulkerBox tileentityshulkerbox = (TileEntityShulkerBox) tileentity;
                boolean flag;

                if (tileentityshulkerbox.j() == TileEntityShulkerBox.AnimationPhase.CLOSED) {
                    EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockShulkerBox.a);

                    flag = world.b(ShulkerUtil.a(blockposition, enumdirection));
                } else {
                    flag = true;
                }

                if (flag) {
                    entityhuman.openContainer(tileentityshulkerbox);
                    entityhuman.a(StatisticList.OPEN_SHULKER_BOX);
                    PiglinAI.a(entityhuman, true);
                }

                return EnumInteractionResult.CONSUME;
            } else {
                return EnumInteractionResult.PASS;
            }
        }
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return (IBlockData) this.getBlockData().set(BlockShulkerBox.a, blockactioncontext.getClickedFace());
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockShulkerBox.a);
    }

    @Override
    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (tileentity instanceof TileEntityShulkerBox) {
            TileEntityShulkerBox tileentityshulkerbox = (TileEntityShulkerBox) tileentity;

            if (!world.isClientSide && entityhuman.isCreative() && !tileentityshulkerbox.isEmpty()) {
                ItemStack itemstack = b(this.c());
                NBTTagCompound nbttagcompound = tileentityshulkerbox.e(new NBTTagCompound());

                if (!nbttagcompound.isEmpty()) {
                    itemstack.a("BlockEntityTag", (NBTBase) nbttagcompound);
                }

                if (tileentityshulkerbox.hasCustomName()) {
                    itemstack.a(tileentityshulkerbox.getCustomName());
                }

                EntityItem entityitem = new EntityItem(world, (double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D, itemstack);

                entityitem.defaultPickupDelay();
                world.addEntity(entityitem);
            } else {
                tileentityshulkerbox.d(entityhuman);
            }
        }

        super.a(world, blockposition, iblockdata, entityhuman);
    }

    @Override
    public List<ItemStack> a(IBlockData iblockdata, LootTableInfo.Builder loottableinfo_builder) {
        TileEntity tileentity = (TileEntity) loottableinfo_builder.b(LootContextParameters.BLOCK_ENTITY);

        if (tileentity instanceof TileEntityShulkerBox) {
            TileEntityShulkerBox tileentityshulkerbox = (TileEntityShulkerBox) tileentity;

            loottableinfo_builder = loottableinfo_builder.a(BlockShulkerBox.b, (loottableinfo, consumer) -> {
                for (int i = 0; i < tileentityshulkerbox.getSize(); ++i) {
                    consumer.accept(tileentityshulkerbox.getItem(i));
                }

            });
        }

        return super.a(iblockdata, loottableinfo_builder);
    }

    @Override
    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        if (itemstack.hasName()) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityShulkerBox) {
                ((TileEntityShulkerBox) tileentity).setCustomName(itemstack.getName());
            }
        }

    }

    @Override
    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata.a(iblockdata1.getBlock())) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityShulkerBox) {
                world.updateAdjacentComparators(blockposition, iblockdata.getBlock());
            }

            super.remove(iblockdata, world, blockposition, iblockdata1, flag);
        }
    }

    @Override
    public EnumPistonReaction getPushReaction(IBlockData iblockdata) {
        return EnumPistonReaction.DESTROY;
    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        TileEntity tileentity = iblockaccess.getTileEntity(blockposition);

        return tileentity instanceof TileEntityShulkerBox ? VoxelShapes.a(((TileEntityShulkerBox) tileentity).a(iblockdata)) : VoxelShapes.b();
    }

    @Override
    public boolean isComplexRedstone(IBlockData iblockdata) {
        return true;
    }

    @Override
    public int a(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return Container.b((IInventory) world.getTileEntity(blockposition));
    }

    public static Block a(@Nullable EnumColor enumcolor) {
        if (enumcolor == null) {
            return Blocks.SHULKER_BOX;
        } else {
            switch (enumcolor) {
                case WHITE:
                    return Blocks.WHITE_SHULKER_BOX;
                case ORANGE:
                    return Blocks.ORANGE_SHULKER_BOX;
                case MAGENTA:
                    return Blocks.MAGENTA_SHULKER_BOX;
                case LIGHT_BLUE:
                    return Blocks.LIGHT_BLUE_SHULKER_BOX;
                case YELLOW:
                    return Blocks.YELLOW_SHULKER_BOX;
                case LIME:
                    return Blocks.LIME_SHULKER_BOX;
                case PINK:
                    return Blocks.PINK_SHULKER_BOX;
                case GRAY:
                    return Blocks.GRAY_SHULKER_BOX;
                case LIGHT_GRAY:
                    return Blocks.LIGHT_GRAY_SHULKER_BOX;
                case CYAN:
                    return Blocks.CYAN_SHULKER_BOX;
                case PURPLE:
                default:
                    return Blocks.PURPLE_SHULKER_BOX;
                case BLUE:
                    return Blocks.BLUE_SHULKER_BOX;
                case BROWN:
                    return Blocks.BROWN_SHULKER_BOX;
                case GREEN:
                    return Blocks.GREEN_SHULKER_BOX;
                case RED:
                    return Blocks.RED_SHULKER_BOX;
                case BLACK:
                    return Blocks.BLACK_SHULKER_BOX;
            }
        }
    }

    @Nullable
    public EnumColor c() {
        return this.color;
    }

    public static ItemStack b(@Nullable EnumColor enumcolor) {
        return new ItemStack(a(enumcolor));
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockShulkerBox.a, enumblockrotation.a((EnumDirection) iblockdata.get(BlockShulkerBox.a)));
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockShulkerBox.a)));
    }
}
