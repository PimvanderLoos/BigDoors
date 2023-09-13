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
        return iblockdata != null && (iblockdata.a(Blocks.CARVED_PUMPKIN) || iblockdata.a(Blocks.JACK_O_LANTERN));
    };

    protected BlockPumpkinCarved(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.k((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockPumpkinCarved.FACING, EnumDirection.NORTH));
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata1.a(iblockdata.getBlock())) {
            this.a(world, blockposition);
        }
    }

    public boolean a(IWorldReader iworldreader, BlockPosition blockposition) {
        return this.c().a(iworldreader, blockposition) != null || this.u().a(iworldreader, blockposition) != null;
    }

    private void a(World world, BlockPosition blockposition) {
        ShapeDetector.ShapeDetectorCollection shapedetector_shapedetectorcollection = this.getSnowmanShape().a(world, blockposition);
        int i;
        Iterator iterator;
        EntityPlayer entityplayer;
        int j;

        if (shapedetector_shapedetectorcollection != null) {
            for (i = 0; i < this.getSnowmanShape().b(); ++i) {
                ShapeDetectorBlock shapedetectorblock = shapedetector_shapedetectorcollection.a(0, i, 0);

                world.setTypeAndData(shapedetectorblock.getPosition(), Blocks.AIR.getBlockData(), 2);
                world.triggerEffect(2001, shapedetectorblock.getPosition(), Block.getCombinedId(shapedetectorblock.a()));
            }

            EntitySnowman entitysnowman = (EntitySnowman) EntityTypes.SNOW_GOLEM.a(world);
            BlockPosition blockposition1 = shapedetector_shapedetectorcollection.a(0, 2, 0).getPosition();

            entitysnowman.setPositionRotation((double) blockposition1.getX() + 0.5D, (double) blockposition1.getY() + 0.05D, (double) blockposition1.getZ() + 0.5D, 0.0F, 0.0F);
            world.addEntity(entitysnowman);
            iterator = world.a(EntityPlayer.class, entitysnowman.getBoundingBox().g(5.0D)).iterator();

            while (iterator.hasNext()) {
                entityplayer = (EntityPlayer) iterator.next();
                CriterionTriggers.SUMMONED_ENTITY.a(entityplayer, (Entity) entitysnowman);
            }

            for (j = 0; j < this.getSnowmanShape().b(); ++j) {
                ShapeDetectorBlock shapedetectorblock1 = shapedetector_shapedetectorcollection.a(0, j, 0);

                world.update(shapedetectorblock1.getPosition(), Blocks.AIR);
            }
        } else {
            shapedetector_shapedetectorcollection = this.getIronGolemShape().a(world, blockposition);
            if (shapedetector_shapedetectorcollection != null) {
                for (i = 0; i < this.getIronGolemShape().c(); ++i) {
                    for (int k = 0; k < this.getIronGolemShape().b(); ++k) {
                        ShapeDetectorBlock shapedetectorblock2 = shapedetector_shapedetectorcollection.a(i, k, 0);

                        world.setTypeAndData(shapedetectorblock2.getPosition(), Blocks.AIR.getBlockData(), 2);
                        world.triggerEffect(2001, shapedetectorblock2.getPosition(), Block.getCombinedId(shapedetectorblock2.a()));
                    }
                }

                BlockPosition blockposition2 = shapedetector_shapedetectorcollection.a(1, 2, 0).getPosition();
                EntityIronGolem entityirongolem = (EntityIronGolem) EntityTypes.IRON_GOLEM.a(world);

                entityirongolem.setPlayerCreated(true);
                entityirongolem.setPositionRotation((double) blockposition2.getX() + 0.5D, (double) blockposition2.getY() + 0.05D, (double) blockposition2.getZ() + 0.5D, 0.0F, 0.0F);
                world.addEntity(entityirongolem);
                iterator = world.a(EntityPlayer.class, entityirongolem.getBoundingBox().g(5.0D)).iterator();

                while (iterator.hasNext()) {
                    entityplayer = (EntityPlayer) iterator.next();
                    CriterionTriggers.SUMMONED_ENTITY.a(entityplayer, (Entity) entityirongolem);
                }

                for (j = 0; j < this.getIronGolemShape().c(); ++j) {
                    for (int l = 0; l < this.getIronGolemShape().b(); ++l) {
                        ShapeDetectorBlock shapedetectorblock3 = shapedetector_shapedetectorcollection.a(j, l, 0);

                        world.update(shapedetectorblock3.getPosition(), Blocks.AIR);
                    }
                }
            }
        }

    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return (IBlockData) this.getBlockData().set(BlockPumpkinCarved.FACING, blockactioncontext.g().opposite());
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockPumpkinCarved.FACING);
    }

    private ShapeDetector c() {
        if (this.snowGolemBase == null) {
            this.snowGolemBase = ShapeDetectorBuilder.a().a(" ", "#", "#").a('#', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.SNOW_BLOCK))).b();
        }

        return this.snowGolemBase;
    }

    private ShapeDetector getSnowmanShape() {
        if (this.snowGolemFull == null) {
            this.snowGolemFull = ShapeDetectorBuilder.a().a("^", "#", "#").a('^', ShapeDetectorBlock.a(BlockPumpkinCarved.PUMPKINS_PREDICATE)).a('#', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.SNOW_BLOCK))).b();
        }

        return this.snowGolemFull;
    }

    private ShapeDetector u() {
        if (this.ironGolemBase == null) {
            this.ironGolemBase = ShapeDetectorBuilder.a().a("~ ~", "###", "~#~").a('#', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.IRON_BLOCK))).a('~', ShapeDetectorBlock.a(MaterialPredicate.a(Material.AIR))).b();
        }

        return this.ironGolemBase;
    }

    private ShapeDetector getIronGolemShape() {
        if (this.ironGolemFull == null) {
            this.ironGolemFull = ShapeDetectorBuilder.a().a("~^~", "###", "~#~").a('^', ShapeDetectorBlock.a(BlockPumpkinCarved.PUMPKINS_PREDICATE)).a('#', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.IRON_BLOCK))).a('~', ShapeDetectorBlock.a(MaterialPredicate.a(Material.AIR))).b();
        }

        return this.ironGolemFull;
    }
}
