package net.minecraft.world.level.block;

import java.util.Collection;
import java.util.Iterator;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.material.EnumPistonReaction;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.level.material.Material;

public class SculkVeinBlock extends MultifaceBlock implements SculkBehaviour, IBlockWaterlogged {

    private static final BlockStateBoolean WATERLOGGED = BlockProperties.WATERLOGGED;
    private final MultifaceSpreader veinSpreader;
    private final MultifaceSpreader sameSpaceSpreader;

    public SculkVeinBlock(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.veinSpreader = new MultifaceSpreader(new SculkVeinBlock.a(MultifaceSpreader.DEFAULT_SPREAD_ORDER));
        this.sameSpaceSpreader = new MultifaceSpreader(new SculkVeinBlock.a(new MultifaceSpreader.e[]{MultifaceSpreader.e.SAME_POSITION}));
        this.registerDefaultState((IBlockData) this.defaultBlockState().setValue(SculkVeinBlock.WATERLOGGED, false));
    }

    @Override
    public MultifaceSpreader getSpreader() {
        return this.veinSpreader;
    }

    public MultifaceSpreader getSameSpaceSpreader() {
        return this.sameSpaceSpreader;
    }

    public static boolean regrow(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Collection<EnumDirection> collection) {
        boolean flag = false;
        IBlockData iblockdata1 = Blocks.SCULK_VEIN.defaultBlockState();
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();
            BlockPosition blockposition1 = blockposition.relative(enumdirection);

            if (canAttachTo(generatoraccess, enumdirection, blockposition1, generatoraccess.getBlockState(blockposition1))) {
                iblockdata1 = (IBlockData) iblockdata1.setValue(getFaceProperty(enumdirection), true);
                flag = true;
            }
        }

        if (!flag) {
            return false;
        } else {
            if (!iblockdata.getFluidState().isEmpty()) {
                iblockdata1 = (IBlockData) iblockdata1.setValue(SculkVeinBlock.WATERLOGGED, true);
            }

            generatoraccess.setBlock(blockposition, iblockdata1, 3);
            return true;
        }
    }

    @Override
    public void onDischarged(GeneratorAccess generatoraccess, IBlockData iblockdata, BlockPosition blockposition, RandomSource randomsource) {
        if (iblockdata.is((Block) this)) {
            EnumDirection[] aenumdirection = SculkVeinBlock.DIRECTIONS;
            int i = aenumdirection.length;

            for (int j = 0; j < i; ++j) {
                EnumDirection enumdirection = aenumdirection[j];
                BlockStateBoolean blockstateboolean = getFaceProperty(enumdirection);

                if ((Boolean) iblockdata.getValue(blockstateboolean) && generatoraccess.getBlockState(blockposition.relative(enumdirection)).is(Blocks.SCULK)) {
                    iblockdata = (IBlockData) iblockdata.setValue(blockstateboolean, false);
                }
            }

            if (!hasAnyFace(iblockdata)) {
                Fluid fluid = generatoraccess.getFluidState(blockposition);

                iblockdata = (fluid.isEmpty() ? Blocks.AIR : Blocks.WATER).defaultBlockState();
            }

            generatoraccess.setBlock(blockposition, iblockdata, 3);
            SculkBehaviour.super.onDischarged(generatoraccess, iblockdata, blockposition, randomsource);
        }
    }

    @Override
    public int attemptUseCharge(SculkSpreader.a sculkspreader_a, GeneratorAccess generatoraccess, BlockPosition blockposition, RandomSource randomsource, SculkSpreader sculkspreader, boolean flag) {
        return flag && this.attemptPlaceSculk(sculkspreader, generatoraccess, sculkspreader_a.getPos(), randomsource) ? sculkspreader_a.getCharge() - 1 : (randomsource.nextInt(sculkspreader.chargeDecayRate()) == 0 ? MathHelper.floor((float) sculkspreader_a.getCharge() * 0.5F) : sculkspreader_a.getCharge());
    }

    private boolean attemptPlaceSculk(SculkSpreader sculkspreader, GeneratorAccess generatoraccess, BlockPosition blockposition, RandomSource randomsource) {
        IBlockData iblockdata = generatoraccess.getBlockState(blockposition);
        TagKey<Block> tagkey = sculkspreader.replaceableBlocks();
        Iterator iterator = EnumDirection.allShuffled(randomsource).iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();

            if (hasFace(iblockdata, enumdirection)) {
                BlockPosition blockposition1 = blockposition.relative(enumdirection);
                IBlockData iblockdata1 = generatoraccess.getBlockState(blockposition1);

                if (iblockdata1.is(tagkey)) {
                    IBlockData iblockdata2 = Blocks.SCULK.defaultBlockState();

                    generatoraccess.setBlock(blockposition1, iblockdata2, 3);
                    Block.pushEntitiesUp(iblockdata1, iblockdata2, generatoraccess, blockposition1);
                    generatoraccess.playSound((EntityHuman) null, blockposition1, SoundEffects.SCULK_BLOCK_SPREAD, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    this.veinSpreader.spreadAll(iblockdata2, generatoraccess, blockposition1, sculkspreader.isWorldGeneration());
                    EnumDirection enumdirection1 = enumdirection.getOpposite();
                    EnumDirection[] aenumdirection = SculkVeinBlock.DIRECTIONS;
                    int i = aenumdirection.length;

                    for (int j = 0; j < i; ++j) {
                        EnumDirection enumdirection2 = aenumdirection[j];

                        if (enumdirection2 != enumdirection1) {
                            BlockPosition blockposition2 = blockposition1.relative(enumdirection2);
                            IBlockData iblockdata3 = generatoraccess.getBlockState(blockposition2);

                            if (iblockdata3.is((Block) this)) {
                                this.onDischarged(generatoraccess, iblockdata3, blockposition2, randomsource);
                            }
                        }
                    }

                    return true;
                }
            }
        }

        return false;
    }

    public static boolean hasSubstrateAccess(GeneratorAccess generatoraccess, IBlockData iblockdata, BlockPosition blockposition) {
        if (!iblockdata.is(Blocks.SCULK_VEIN)) {
            return false;
        } else {
            EnumDirection[] aenumdirection = SculkVeinBlock.DIRECTIONS;
            int i = aenumdirection.length;

            for (int j = 0; j < i; ++j) {
                EnumDirection enumdirection = aenumdirection[j];

                if (hasFace(iblockdata, enumdirection) && generatoraccess.getBlockState(blockposition.relative(enumdirection)).is(TagsBlock.SCULK_REPLACEABLE)) {
                    return true;
                }
            }

            return false;
        }
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.getValue(SculkVeinBlock.WATERLOGGED)) {
            generatoraccess.scheduleTick(blockposition, (FluidType) FluidTypes.WATER, FluidTypes.WATER.getTickDelay(generatoraccess));
        }

        return super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        super.createBlockStateDefinition(blockstatelist_a);
        blockstatelist_a.add(SculkVeinBlock.WATERLOGGED);
    }

    @Override
    public boolean canBeReplaced(IBlockData iblockdata, BlockActionContext blockactioncontext) {
        return !blockactioncontext.getItemInHand().is(Items.SCULK_VEIN) || super.canBeReplaced(iblockdata, blockactioncontext);
    }

    @Override
    public Fluid getFluidState(IBlockData iblockdata) {
        return (Boolean) iblockdata.getValue(SculkVeinBlock.WATERLOGGED) ? FluidTypes.WATER.getSource(false) : super.getFluidState(iblockdata);
    }

    @Override
    public EnumPistonReaction getPistonPushReaction(IBlockData iblockdata) {
        return EnumPistonReaction.DESTROY;
    }

    private class a extends MultifaceSpreader.a {

        private final MultifaceSpreader.e[] spreadTypes;

        public a(MultifaceSpreader.e... amultifacespreader_e) {
            super(SculkVeinBlock.this);
            this.spreadTypes = amultifacespreader_e;
        }

        @Override
        public boolean stateCanBeReplaced(IBlockAccess iblockaccess, BlockPosition blockposition, BlockPosition blockposition1, EnumDirection enumdirection, IBlockData iblockdata) {
            IBlockData iblockdata1 = iblockaccess.getBlockState(blockposition1.relative(enumdirection));

            if (!iblockdata1.is(Blocks.SCULK) && !iblockdata1.is(Blocks.SCULK_CATALYST) && !iblockdata1.is(Blocks.MOVING_PISTON)) {
                if (blockposition.distManhattan(blockposition1) == 2) {
                    BlockPosition blockposition2 = blockposition.relative(enumdirection.getOpposite());

                    if (iblockaccess.getBlockState(blockposition2).isFaceSturdy(iblockaccess, blockposition2, enumdirection)) {
                        return false;
                    }
                }

                Fluid fluid = iblockdata.getFluidState();

                if (!fluid.isEmpty() && !fluid.is((FluidType) FluidTypes.WATER)) {
                    return false;
                } else {
                    Material material = iblockdata.getMaterial();

                    return material == Material.FIRE ? false : material.isReplaceable() || super.stateCanBeReplaced(iblockaccess, blockposition, blockposition1, enumdirection, iblockdata);
                }
            } else {
                return false;
            }
        }

        @Override
        public MultifaceSpreader.e[] getSpreadTypes() {
            return this.spreadTypes;
        }

        @Override
        public boolean isOtherBlockValidAsSource(IBlockData iblockdata) {
            return !iblockdata.is(Blocks.SCULK_VEIN);
        }
    }
}
