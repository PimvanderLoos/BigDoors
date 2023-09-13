package net.minecraft.world.level.block;

import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.Particles;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.TagsEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.item.EntityFallingBlock;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapeCollisionEntity;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class PowderSnowBlock extends Block implements IFluidSource {

    private static final float HORIZONTAL_PARTICLE_MOMENTUM_FACTOR = 0.083333336F;
    private static final float IN_BLOCK_HORIZONTAL_SPEED_MULTIPLIER = 0.9F;
    private static final float IN_BLOCK_VERTICAL_SPEED_MULTIPLIER = 1.5F;
    private static final float NUM_BLOCKS_TO_FALL_INTO_BLOCK = 2.5F;
    private static final VoxelShape FALLING_COLLISION_SHAPE = VoxelShapes.box(0.0D, 0.0D, 0.0D, 1.0D, 0.8999999761581421D, 1.0D);
    private static final double MINIMUM_FALL_DISTANCE_FOR_SOUND = 4.0D;
    private static final double MINIMUM_FALL_DISTANCE_FOR_BIG_SOUND = 7.0D;

    public PowderSnowBlock(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public boolean skipRendering(IBlockData iblockdata, IBlockData iblockdata1, EnumDirection enumdirection) {
        return iblockdata1.is((Block) this) ? true : super.skipRendering(iblockdata, iblockdata1, enumdirection);
    }

    @Override
    public VoxelShape getOcclusionShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return VoxelShapes.empty();
    }

    @Override
    public void entityInside(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        if (!(entity instanceof EntityLiving) || entity.getFeetBlockState().is((Block) this)) {
            entity.makeStuckInBlock(iblockdata, new Vec3D(0.8999999761581421D, 1.5D, 0.8999999761581421D));
            if (world.isClientSide) {
                RandomSource randomsource = world.getRandom();
                boolean flag = entity.xOld != entity.getX() || entity.zOld != entity.getZ();

                if (flag && randomsource.nextBoolean()) {
                    world.addParticle(Particles.SNOWFLAKE, entity.getX(), (double) (blockposition.getY() + 1), entity.getZ(), (double) (MathHelper.randomBetween(randomsource, -1.0F, 1.0F) * 0.083333336F), 0.05000000074505806D, (double) (MathHelper.randomBetween(randomsource, -1.0F, 1.0F) * 0.083333336F));
                }
            }
        }

        entity.setIsInPowderSnow(true);
        if (!world.isClientSide) {
            if (entity.isOnFire() && (world.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) || entity instanceof EntityHuman) && entity.mayInteract(world, blockposition)) {
                world.destroyBlock(blockposition, false);
            }

            entity.setSharedFlagOnFire(false);
        }

    }

    @Override
    public void fallOn(World world, IBlockData iblockdata, BlockPosition blockposition, Entity entity, float f) {
        if ((double) f >= 4.0D && entity instanceof EntityLiving) {
            EntityLiving entityliving = (EntityLiving) entity;
            EntityLiving.a entityliving_a = entityliving.getFallSounds();
            SoundEffect soundeffect = (double) f < 7.0D ? entityliving_a.small() : entityliving_a.big();

            entity.playSound(soundeffect, 1.0F, 1.0F);
        }
    }

    @Override
    public VoxelShape getCollisionShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        if (voxelshapecollision instanceof VoxelShapeCollisionEntity) {
            VoxelShapeCollisionEntity voxelshapecollisionentity = (VoxelShapeCollisionEntity) voxelshapecollision;
            Entity entity = voxelshapecollisionentity.getEntity();

            if (entity != null) {
                if (entity.fallDistance > 2.5F) {
                    return PowderSnowBlock.FALLING_COLLISION_SHAPE;
                }

                boolean flag = entity instanceof EntityFallingBlock;

                if (flag || canEntityWalkOnPowderSnow(entity) && voxelshapecollision.isAbove(VoxelShapes.block(), blockposition, false) && !voxelshapecollision.isDescending()) {
                    return super.getCollisionShape(iblockdata, iblockaccess, blockposition, voxelshapecollision);
                }
            }
        }

        return VoxelShapes.empty();
    }

    @Override
    public VoxelShape getVisualShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return VoxelShapes.empty();
    }

    public static boolean canEntityWalkOnPowderSnow(Entity entity) {
        return entity.getType().is(TagsEntity.POWDER_SNOW_WALKABLE_MOBS) ? true : (entity instanceof EntityLiving ? ((EntityLiving) entity).getItemBySlot(EnumItemSlot.FEET).is(Items.LEATHER_BOOTS) : false);
    }

    @Override
    public ItemStack pickupBlock(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        generatoraccess.setBlock(blockposition, Blocks.AIR.defaultBlockState(), 11);
        if (!generatoraccess.isClientSide()) {
            generatoraccess.levelEvent(2001, blockposition, Block.getId(iblockdata));
        }

        return new ItemStack(Items.POWDER_SNOW_BUCKET);
    }

    @Override
    public Optional<SoundEffect> getPickupSound() {
        return Optional.of(SoundEffects.BUCKET_FILL_POWDER_SNOW);
    }

    @Override
    public boolean isPathfindable(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return true;
    }
}
