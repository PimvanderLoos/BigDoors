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

        if (shapedetector_shapedetectorcollection != null) {
            EntitySnowman entitysnowman = (EntitySnowman) EntityTypes.SNOW_GOLEM.create(world);

            if (entitysnowman != null) {
                spawnGolemInWorld(world, shapedetector_shapedetectorcollection, entitysnowman, shapedetector_shapedetectorcollection.getBlock(0, 2, 0).getPos());
            }
        } else {
            ShapeDetector.ShapeDetectorCollection shapedetector_shapedetectorcollection1 = this.getOrCreateIronGolemFull().find(world, blockposition);

            if (shapedetector_shapedetectorcollection1 != null) {
                EntityIronGolem entityirongolem = (EntityIronGolem) EntityTypes.IRON_GOLEM.create(world);

                if (entityirongolem != null) {
                    entityirongolem.setPlayerCreated(true);
                    spawnGolemInWorld(world, shapedetector_shapedetectorcollection1, entityirongolem, shapedetector_shapedetectorcollection1.getBlock(1, 2, 0).getPos());
                }
            }
        }

    }

    private static void spawnGolemInWorld(World world, ShapeDetector.ShapeDetectorCollection shapedetector_shapedetectorcollection, Entity entity, BlockPosition blockposition) {
        clearPatternBlocks(world, shapedetector_shapedetectorcollection);
        entity.moveTo((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.05D, (double) blockposition.getZ() + 0.5D, 0.0F, 0.0F);
        world.addFreshEntity(entity);
        Iterator iterator = world.getEntitiesOfClass(EntityPlayer.class, entity.getBoundingBox().inflate(5.0D)).iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            CriterionTriggers.SUMMONED_ENTITY.trigger(entityplayer, entity);
        }

        updatePatternBlocks(world, shapedetector_shapedetectorcollection);
    }

    public static void clearPatternBlocks(World world, ShapeDetector.ShapeDetectorCollection shapedetector_shapedetectorcollection) {
        for (int i = 0; i < shapedetector_shapedetectorcollection.getWidth(); ++i) {
            for (int j = 0; j < shapedetector_shapedetectorcollection.getHeight(); ++j) {
                ShapeDetectorBlock shapedetectorblock = shapedetector_shapedetectorcollection.getBlock(i, j, 0);

                world.setBlock(shapedetectorblock.getPos(), Blocks.AIR.defaultBlockState(), 2);
                world.levelEvent(2001, shapedetectorblock.getPos(), Block.getId(shapedetectorblock.getState()));
            }
        }

    }

    public static void updatePatternBlocks(World world, ShapeDetector.ShapeDetectorCollection shapedetector_shapedetectorcollection) {
        for (int i = 0; i < shapedetector_shapedetectorcollection.getWidth(); ++i) {
            for (int j = 0; j < shapedetector_shapedetectorcollection.getHeight(); ++j) {
                ShapeDetectorBlock shapedetectorblock = shapedetector_shapedetectorcollection.getBlock(i, j, 0);

                world.blockUpdated(shapedetectorblock.getPos(), Blocks.AIR);
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
