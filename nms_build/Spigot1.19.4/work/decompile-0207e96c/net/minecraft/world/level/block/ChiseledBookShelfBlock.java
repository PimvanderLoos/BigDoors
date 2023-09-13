package net.minecraft.world.level.block;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.tags.TagsItem;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.InventoryUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.IBlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec2F;
import net.minecraft.world.phys.Vec3D;
import org.jetbrains.annotations.Nullable;

public class ChiseledBookShelfBlock extends BlockTileEntity {

    private static final int MAX_BOOKS_IN_STORAGE = 6;
    public static final int BOOKS_PER_ROW = 3;
    public static final List<BlockStateBoolean> SLOT_OCCUPIED_PROPERTIES = List.of(BlockProperties.CHISELED_BOOKSHELF_SLOT_0_OCCUPIED, BlockProperties.CHISELED_BOOKSHELF_SLOT_1_OCCUPIED, BlockProperties.CHISELED_BOOKSHELF_SLOT_2_OCCUPIED, BlockProperties.CHISELED_BOOKSHELF_SLOT_3_OCCUPIED, BlockProperties.CHISELED_BOOKSHELF_SLOT_4_OCCUPIED, BlockProperties.CHISELED_BOOKSHELF_SLOT_5_OCCUPIED);

    public ChiseledBookShelfBlock(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        IBlockData iblockdata = (IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockFacingHorizontal.FACING, EnumDirection.NORTH);

        BlockStateBoolean blockstateboolean;

        for (Iterator iterator = ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.iterator(); iterator.hasNext(); iblockdata = (IBlockData) iblockdata.setValue(blockstateboolean, false)) {
            blockstateboolean = (BlockStateBoolean) iterator.next();
        }

        this.registerDefaultState(iblockdata);
    }

    @Override
    public EnumRenderType getRenderShape(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    @Override
    public EnumInteractionResult use(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        TileEntity tileentity = world.getBlockEntity(blockposition);

        if (tileentity instanceof ChiseledBookShelfBlockEntity) {
            ChiseledBookShelfBlockEntity chiseledbookshelfblockentity = (ChiseledBookShelfBlockEntity) tileentity;
            Optional optional = getRelativeHitCoordinatesForBlockFace(movingobjectpositionblock, (EnumDirection) iblockdata.getValue(BlockFacingHorizontal.FACING));

            if (optional.isEmpty()) {
                return EnumInteractionResult.PASS;
            } else {
                int i = getHitSlot((Vec2F) optional.get());

                if ((Boolean) iblockdata.getValue((IBlockState) ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.get(i))) {
                    removeBook(world, blockposition, entityhuman, chiseledbookshelfblockentity, i);
                    return EnumInteractionResult.sidedSuccess(world.isClientSide);
                } else {
                    ItemStack itemstack = entityhuman.getItemInHand(enumhand);

                    if (itemstack.is(TagsItem.BOOKSHELF_BOOKS)) {
                        addBook(world, blockposition, entityhuman, chiseledbookshelfblockentity, itemstack, i);
                        return EnumInteractionResult.sidedSuccess(world.isClientSide);
                    } else {
                        return EnumInteractionResult.CONSUME;
                    }
                }
            }
        } else {
            return EnumInteractionResult.PASS;
        }
    }

    private static Optional<Vec2F> getRelativeHitCoordinatesForBlockFace(MovingObjectPositionBlock movingobjectpositionblock, EnumDirection enumdirection) {
        EnumDirection enumdirection1 = movingobjectpositionblock.getDirection();

        if (enumdirection != enumdirection1) {
            return Optional.empty();
        } else {
            BlockPosition blockposition = movingobjectpositionblock.getBlockPos().relative(enumdirection1);
            Vec3D vec3d = movingobjectpositionblock.getLocation().subtract((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ());
            double d0 = vec3d.x();
            double d1 = vec3d.y();
            double d2 = vec3d.z();
            Optional optional;

            switch (enumdirection1) {
                case NORTH:
                    optional = Optional.of(new Vec2F((float) (1.0D - d0), (float) d1));
                    break;
                case SOUTH:
                    optional = Optional.of(new Vec2F((float) d0, (float) d1));
                    break;
                case WEST:
                    optional = Optional.of(new Vec2F((float) d2, (float) d1));
                    break;
                case EAST:
                    optional = Optional.of(new Vec2F((float) (1.0D - d2), (float) d1));
                    break;
                case DOWN:
                case UP:
                    optional = Optional.empty();
                    break;
                default:
                    throw new IncompatibleClassChangeError();
            }

            return optional;
        }
    }

    private static int getHitSlot(Vec2F vec2f) {
        int i = vec2f.y >= 0.5F ? 0 : 1;
        int j = getSection(vec2f.x);

        return j + i * 3;
    }

    private static int getSection(float f) {
        float f1 = 0.0625F;
        float f2 = 0.375F;

        if (f < 0.375F) {
            return 0;
        } else {
            float f3 = 0.6875F;

            return f < 0.6875F ? 1 : 2;
        }
    }

    private static void addBook(World world, BlockPosition blockposition, EntityHuman entityhuman, ChiseledBookShelfBlockEntity chiseledbookshelfblockentity, ItemStack itemstack, int i) {
        if (!world.isClientSide) {
            entityhuman.awardStat(StatisticList.ITEM_USED.get(itemstack.getItem()));
            SoundEffect soundeffect = itemstack.is(Items.ENCHANTED_BOOK) ? SoundEffects.CHISELED_BOOKSHELF_INSERT_ENCHANTED : SoundEffects.CHISELED_BOOKSHELF_INSERT;

            chiseledbookshelfblockentity.setItem(i, itemstack.split(1));
            world.playSound((EntityHuman) null, blockposition, soundeffect, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (entityhuman.isCreative()) {
                itemstack.grow(1);
            }

            world.gameEvent((Entity) entityhuman, GameEvent.BLOCK_CHANGE, blockposition);
        }
    }

    private static void removeBook(World world, BlockPosition blockposition, EntityHuman entityhuman, ChiseledBookShelfBlockEntity chiseledbookshelfblockentity, int i) {
        if (!world.isClientSide) {
            ItemStack itemstack = chiseledbookshelfblockentity.removeItem(i, 1);
            SoundEffect soundeffect = itemstack.is(Items.ENCHANTED_BOOK) ? SoundEffects.CHISELED_BOOKSHELF_PICKUP_ENCHANTED : SoundEffects.CHISELED_BOOKSHELF_PICKUP;

            world.playSound((EntityHuman) null, blockposition, soundeffect, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (!entityhuman.getInventory().add(itemstack)) {
                entityhuman.drop(itemstack, false);
            }

            world.gameEvent((Entity) entityhuman, GameEvent.BLOCK_CHANGE, blockposition);
        }
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(BlockPosition blockposition, IBlockData iblockdata) {
        return new ChiseledBookShelfBlockEntity(blockposition, iblockdata);
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockFacingHorizontal.FACING);
        List list = ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES;

        Objects.requireNonNull(blockstatelist_a);
        list.forEach((iblockstate) -> {
            blockstatelist_a.add(iblockstate);
        });
    }

    @Override
    public void onRemove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata.is(iblockdata1.getBlock())) {
            TileEntity tileentity = world.getBlockEntity(blockposition);

            if (tileentity instanceof ChiseledBookShelfBlockEntity) {
                ChiseledBookShelfBlockEntity chiseledbookshelfblockentity = (ChiseledBookShelfBlockEntity) tileentity;

                if (!chiseledbookshelfblockentity.isEmpty()) {
                    for (int i = 0; i < 6; ++i) {
                        ItemStack itemstack = chiseledbookshelfblockentity.getItem(i);

                        if (!itemstack.isEmpty()) {
                            InventoryUtils.dropItemStack(world, (double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), itemstack);
                        }
                    }

                    chiseledbookshelfblockentity.clearContent();
                    world.updateNeighbourForOutputSignal(blockposition, this);
                }
            }

            super.onRemove(iblockdata, world, blockposition, iblockdata1, flag);
        }
    }

    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        return (IBlockData) this.defaultBlockState().setValue(BlockFacingHorizontal.FACING, blockactioncontext.getHorizontalDirection().getOpposite());
    }

    @Override
    public boolean hasAnalogOutputSignal(IBlockData iblockdata) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(IBlockData iblockdata, World world, BlockPosition blockposition) {
        if (world.isClientSide()) {
            return 0;
        } else {
            TileEntity tileentity = world.getBlockEntity(blockposition);

            if (tileentity instanceof ChiseledBookShelfBlockEntity) {
                ChiseledBookShelfBlockEntity chiseledbookshelfblockentity = (ChiseledBookShelfBlockEntity) tileentity;

                return chiseledbookshelfblockentity.getLastInteractedSlot() + 1;
            } else {
                return 0;
            }
        }
    }
}
