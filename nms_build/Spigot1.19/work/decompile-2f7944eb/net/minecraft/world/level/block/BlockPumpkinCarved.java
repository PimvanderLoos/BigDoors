package net.minecraft.world.level.block;

import java.util.Iterator;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.EntityIronGolem;
import net.minecraft.world.entity.animal.EntitySnowman;
import net.minecraft.world.item.ItemWearable;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.pattern.ShapeDetector;
import net.minecraft.world.level.block.state.pattern.ShapeDetectorBlock;
import net.minecraft.world.level.block.state.pattern.ShapeDetectorBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.block.state.predicate.MaterialPredicate;
import net.minecraft.world.level.block.state.properties.BlockStateDirection;
import net.minecraft.world.level.material.Material;

public class BlockPumpkinCarved extends BlockFacingHorizontal implements ItemWearable {

    public static final BlockStateDirection FACING = BlockFacingHorizontal.FACING;
    @Nullable
    private ShapeDetector snowGolemBase;
    @Nullable
    private ShapeDetector snowGolemFull;
    @Nullable
    private ShapeDetector ironGolemBase;
    @Nullable
    private ShapeDetector ironGolemFull;
    private static final Predicate<IBlockData> PUMPKINS_PREDICATE = (iblockdata) -> {
        return iblockdata != null && (iblockdata.is(Blocks.CARVED_PUMPKIN) || iblockdata.is(Blocks.JACK_O_LANTERN));
    };

    protected BlockPumpkinCarved(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockPumpkinCarved.FACING, EnumDirection.NORTH));
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata1.is(iblockdata.getBlock())) {
            this.trySpawnGolem(world, blockposition);
        }
    }

    public boolean canSpawnGolem(IWorldReader iworldreader, BlockPosition blockposition) {
        return this.getOrCreateSnowGolemBase().find(iworldreader, blockposition) != null || this.getOrCreateIronGolemBase().find(iworldreader, blockposition) != null;
    }

    private void trySpawnGolem(World world, BlockPosition blockposition) {
        ShapeDetector.ShapeDetectorCollection shapedetector_shapedetectorcollection = this.getOrCreateSnowGolemFull().find(world, blockposition);
        int i;
        Iterator iterator;
        EntityPlayer entityplayer;
        int j;

        if (shapedetector_shapedetectorcollection != null) {
            for (i = 0; i < this.getOrCreateSnowGolemFull().getHeight(); ++i) {
                ShapeDetectorBlock shapedetectorblock = shapedetector_shapedetectorcollection.getBlock(0, i, 0);

                world.setBlock(shapedetectorblock.getPos(), Blocks.AIR.defaultBlockState(), 2);
                world.levelEvent(2001, shapedetectorblock.getPos(), Block.getId(shapedetectorblock.getState()));
            }

            EntitySnowman entitysnowman = (EntitySnowman) EntityTypes.SNOW_GOLEM.create(world);
            BlockPosition blockposition1 = shapedetector_shapedetectorcollection.getBlock(0, 2, 0).getPos();

            entitysnowman.moveTo((double) blockposition1.getX() + 0.5D, (double) blockposition1.getY() + 0.05D, (double) blockposition1.getZ() + 0.5D, 0.0F, 0.0F);
            world.addFreshEntity(entitysnowman);
            iterator = world.getEntitiesOfClass(EntityPlayer.class, entitysnowman.getBoundingBox().inflate(5.0D)).iterator();

            while (iterator.hasNext()) {
                entityplayer = (EntityPlayer) iterator.next();
                CriterionTriggers.SUMMONED_ENTITY.trigger(entityplayer, (Entity) entitysnowman);
            }

            for (j = 0; j < this.getOrCreateSnowGolemFull().getHeight(); ++j) {
                ShapeDetectorBlock shapedetectorblock1 = shapedetector_shapedetectorcollection.getBlock(0, j, 0);

                world.blockUpdated(shapedetectorblock1.getPos(), Blocks.AIR);
            }
        } else {
            shapedetector_shapedetectorcollection = this.getOrCreateIronGolemFull().find(world, blockposition);
            if (shapedetector_shapedetectorcollection != null) {
                for (i = 0; i < this.getOrCreateIronGolemFull().getWidth(); ++i) {
                    for (int k = 0; k < this.getOrCreateIronGolemFull().getHeight(); ++k) {
                        ShapeDetectorBlock shapedetectorblock2 = shapedetector_shapedetectorcollection.getBlock(i, k, 0);

                        world.setBlock(shapedetectorblock2.getPos(), Blocks.AIR.defaultBlockState(), 2);
                        world.levelEvent(2001, shapedetectorblock2.getPos(), Block.getId(shapedetectorblock2.getState()));
                    }
                }

                BlockPosition blockposition2 = shapedetector_shapedetectorcollection.getBlock(1, 2, 0).getPos();
                EntityIronGolem entityirongolem = (EntityIronGolem) EntityTypes.IRON_GOLEM.create(world);

                entityirongolem.setPlayerCreated(true);
                entityirongolem.moveTo((double) blockposition2.getX() + 0.5D, (double) blockposition2.getY() + 0.05D, (double) blockposition2.getZ() + 0.5D, 0.0F, 0.0F);
                world.addFreshEntity(entityirongolem);
                iterator = world.getEntitiesOfClass(EntityPlayer.class, entityirongolem.getBoundingBox().inflate(5.0D)).iterator();

                while (iterator.hasNext()) {
                    entityplayer = (EntityPlayer) iterator.next();
                    CriterionTriggers.SUMMONED_ENTITY.trigger(entityplayer, (Entity) entityirongolem);
                }

                for (j = 0; j < this.getOrCreateIronGolemFull().getWidth(); ++j) {
                    for (int l = 0; l < this.getOrCreateIronGolemFull().getHeight(); ++l) {
                        ShapeDetectorBlock shapedetectorblock3 = shapedetector_shapedetectorcollection.getBlock(j, l, 0);

                        world.blockUpdated(shapedetectorblock3.getPos(), Blocks.AIR);
                    }
                }
            }
        }

    }

    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        return (IBlockData) this.defaultBlockState().setValue(BlockPumpkinCarved.FACING, blockactioncontext.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockPumpkinCarved.FACING);
    }

    private ShapeDetector getOrCreateSnowGolemBase() {
        if (this.snowGolemBase == null) {
            this.snowGolemBase = ShapeDetectorBuilder.start().aisle(" ", "#", "#").where('#', ShapeDetectorBlock.hasState(BlockStatePredicate.forBlock(Blocks.SNOW_BLOCK))).build();
        }

        return this.snowGolemBase;
    }

    private ShapeDetector getOrCreateSnowGolemFull() {
        if (this.snowGolemFull == null) {
            this.snowGolemFull = ShapeDetectorBuilder.start().aisle("^", "#", "#").where('^', ShapeDetectorBlock.hasState(BlockPumpkinCarved.PUMPKINS_PREDICATE)).where('#', ShapeDetectorBlock.hasState(BlockStatePredicate.forBlock(Blocks.SNOW_BLOCK))).build();
        }

        return this.snowGolemFull;
    }

    private ShapeDetector getOrCreateIronGolemBase() {
        if (this.ironGolemBase == null) {
            this.ironGolemBase = ShapeDetectorBuilder.start().aisle("~ ~", "###", "~#~").where('#', ShapeDetectorBlock.hasState(BlockStatePredicate.forBlock(Blocks.IRON_BLOCK))).where('~', ShapeDetectorBlock.hasState(MaterialPredicate.forMaterial(Material.AIR))).build();
        }

        return this.ironGolemBase;
    }

    private ShapeDetector getOrCreateIronGolemFull() {
        if (this.ironGolemFull == null) {
            this.ironGolemFull = ShapeDetectorBuilder.start().aisle("~^~", "###", "~#~").where('^', ShapeDetectorBlock.hasState(BlockPumpkinCarved.PUMPKINS_PREDICATE)).where('#', ShapeDetectorBlock.hasState(BlockStatePredicate.forBlock(Blocks.IRON_BLOCK))).where('~', ShapeDetectorBlock.hasState(MaterialPredicate.forMaterial(Material.AIR))).build();
        }

        return this.ironGolemFull;
    }
}
