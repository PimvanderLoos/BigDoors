package net.minecraft.world.level.block;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.boss.wither.EntityWither;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntitySkull;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.pattern.ShapeDetector;
import net.minecraft.world.level.block.state.pattern.ShapeDetectorBlock;
import net.minecraft.world.level.block.state.pattern.ShapeDetectorBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.block.state.predicate.MaterialPredicate;
import net.minecraft.world.level.material.Material;

public class BlockWitherSkull extends BlockSkull {

    @Nullable
    private static ShapeDetector witherPatternFull;
    @Nullable
    private static ShapeDetector witherPatternBase;

    protected BlockWitherSkull(BlockBase.Info blockbase_info) {
        super(BlockSkull.Type.WITHER_SKELETON, blockbase_info);
    }

    @Override
    public void setPlacedBy(World world, BlockPosition blockposition, IBlockData iblockdata, @Nullable EntityLiving entityliving, ItemStack itemstack) {
        super.setPlacedBy(world, blockposition, iblockdata, entityliving, itemstack);
        TileEntity tileentity = world.getBlockEntity(blockposition);

        if (tileentity instanceof TileEntitySkull) {
            checkSpawn(world, blockposition, (TileEntitySkull) tileentity);
        }

    }

    public static void checkSpawn(World world, BlockPosition blockposition, TileEntitySkull tileentityskull) {
        if (!world.isClientSide) {
            IBlockData iblockdata = tileentityskull.getBlockState();
            boolean flag = iblockdata.is(Blocks.WITHER_SKELETON_SKULL) || iblockdata.is(Blocks.WITHER_SKELETON_WALL_SKULL);

            if (flag && blockposition.getY() >= world.getMinBuildHeight() && world.getDifficulty() != EnumDifficulty.PEACEFUL) {
                ShapeDetector shapedetector = getOrCreateWitherFull();
                ShapeDetector.ShapeDetectorCollection shapedetector_shapedetectorcollection = shapedetector.find(world, blockposition);

                if (shapedetector_shapedetectorcollection != null) {
                    for (int i = 0; i < shapedetector.getWidth(); ++i) {
                        for (int j = 0; j < shapedetector.getHeight(); ++j) {
                            ShapeDetectorBlock shapedetectorblock = shapedetector_shapedetectorcollection.getBlock(i, j, 0);

                            world.setBlock(shapedetectorblock.getPos(), Blocks.AIR.defaultBlockState(), 2);
                            world.levelEvent(2001, shapedetectorblock.getPos(), Block.getId(shapedetectorblock.getState()));
                        }
                    }

                    EntityWither entitywither = (EntityWither) EntityTypes.WITHER.create(world);
                    BlockPosition blockposition1 = shapedetector_shapedetectorcollection.getBlock(1, 2, 0).getPos();

                    entitywither.moveTo((double) blockposition1.getX() + 0.5D, (double) blockposition1.getY() + 0.55D, (double) blockposition1.getZ() + 0.5D, shapedetector_shapedetectorcollection.getForwards().getAxis() == EnumDirection.EnumAxis.X ? 0.0F : 90.0F, 0.0F);
                    entitywither.yBodyRot = shapedetector_shapedetectorcollection.getForwards().getAxis() == EnumDirection.EnumAxis.X ? 0.0F : 90.0F;
                    entitywither.makeInvulnerable();
                    Iterator iterator = world.getEntitiesOfClass(EntityPlayer.class, entitywither.getBoundingBox().inflate(50.0D)).iterator();

                    while (iterator.hasNext()) {
                        EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                        CriterionTriggers.SUMMONED_ENTITY.trigger(entityplayer, (Entity) entitywither);
                    }

                    world.addFreshEntity(entitywither);

                    for (int k = 0; k < shapedetector.getWidth(); ++k) {
                        for (int l = 0; l < shapedetector.getHeight(); ++l) {
                            world.blockUpdated(shapedetector_shapedetectorcollection.getBlock(k, l, 0).getPos(), Blocks.AIR);
                        }
                    }

                }
            }
        }
    }

    public static boolean canSpawnMob(World world, BlockPosition blockposition, ItemStack itemstack) {
        return itemstack.is(Items.WITHER_SKELETON_SKULL) && blockposition.getY() >= world.getMinBuildHeight() + 2 && world.getDifficulty() != EnumDifficulty.PEACEFUL && !world.isClientSide ? getOrCreateWitherBase().find(world, blockposition) != null : false;
    }

    private static ShapeDetector getOrCreateWitherFull() {
        if (BlockWitherSkull.witherPatternFull == null) {
            BlockWitherSkull.witherPatternFull = ShapeDetectorBuilder.start().aisle("^^^", "###", "~#~").where('#', (shapedetectorblock) -> {
                return shapedetectorblock.getState().is((Tag) TagsBlock.WITHER_SUMMON_BASE_BLOCKS);
            }).where('^', ShapeDetectorBlock.hasState(BlockStatePredicate.forBlock(Blocks.WITHER_SKELETON_SKULL).or(BlockStatePredicate.forBlock(Blocks.WITHER_SKELETON_WALL_SKULL)))).where('~', ShapeDetectorBlock.hasState(MaterialPredicate.forMaterial(Material.AIR))).build();
        }

        return BlockWitherSkull.witherPatternFull;
    }

    private static ShapeDetector getOrCreateWitherBase() {
        if (BlockWitherSkull.witherPatternBase == null) {
            BlockWitherSkull.witherPatternBase = ShapeDetectorBuilder.start().aisle("   ", "###", "~#~").where('#', (shapedetectorblock) -> {
                return shapedetectorblock.getState().is((Tag) TagsBlock.WITHER_SUMMON_BASE_BLOCKS);
            }).where('~', ShapeDetectorBlock.hasState(MaterialPredicate.forMaterial(Material.AIR))).build();
        }

        return BlockWitherSkull.witherPatternBase;
    }
}
