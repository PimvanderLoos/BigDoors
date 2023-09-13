package net.minecraft.world.level.block;

import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.Particles;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsEntity;
import net.minecraft.util.MathHelper;
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
    private static final VoxelShape FALLING_COLLISION_SHAPE = VoxelShapes.create(0.0D, 0.0D, 0.0D, 1.0D, 0.8999999761581421D, 1.0D);

    public PowderSnowBlock(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockData iblockdata1, EnumDirection enumdirection) {
        return iblockdata1.a((Block) this) ? true : super.a(iblockdata, iblockdata1, enumdirection);
    }

    @Override
    public VoxelShape b_(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return VoxelShapes.a();
    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        if (!(entity instanceof EntityLiving) || entity.cS().a((Block) this)) {
            entity.a(iblockdata, new Vec3D(0.8999999761581421D, 1.5D, 0.8999999761581421D));
            if (world.isClientSide) {
                Random random = world.getRandom();
                boolean flag = entity.xOld != entity.locX() || entity.zOld != entity.locZ();

                if (flag && random.nextBoolean()) {
                    world.addParticle(Particles.SNOWFLAKE, entity.locX(), (double) (blockposition.getY() + 1), entity.locZ(), (double) (MathHelper.b(random, -1.0F, 1.0F) * 0.083333336F), 0.05000000074505806D, (double) (MathHelper.b(random, -1.0F, 1.0F) * 0.083333336F));
                }
            }
        }

        entity.o(true);
        if (!world.isClientSide) {
            if (entity.isBurning() && (world.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) || entity instanceof EntityHuman) && entity.a(world, blockposition)) {
                world.b(blockposition, false);
            }

            entity.a_(false);
        }

    }

    @Override
    public VoxelShape c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        if (voxelshapecollision instanceof VoxelShapeCollisionEntity) {
            VoxelShapeCollisionEntity voxelshapecollisionentity = (VoxelShapeCollisionEntity) voxelshapecollision;
            Optional<Entity> optional = voxelshapecollisionentity.c();

            if (optional.isPresent()) {
                Entity entity = (Entity) optional.get();

                if (entity.fallDistance > 2.5F) {
                    return PowderSnowBlock.FALLING_COLLISION_SHAPE;
                }

                boolean flag = entity instanceof EntityFallingBlock;

                if (flag || a(entity) && voxelshapecollision.a(VoxelShapes.b(), blockposition, false) && !voxelshapecollision.b()) {
                    return super.c(iblockdata, iblockaccess, blockposition, voxelshapecollision);
                }
            }
        }

        return VoxelShapes.a();
    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return VoxelShapes.a();
    }

    public static boolean a(Entity entity) {
        return entity.getEntityType().a((Tag) TagsEntity.POWDER_SNOW_WALKABLE_MOBS) ? true : (entity instanceof EntityLiving ? ((EntityLiving) entity).getEquipment(EnumItemSlot.FEET).a(Items.LEATHER_BOOTS) : false);
    }

    @Override
    public ItemStack removeFluid(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        generatoraccess.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 11);
        if (!generatoraccess.isClientSide()) {
            generatoraccess.triggerEffect(2001, blockposition, Block.getCombinedId(iblockdata));
        }

        return new ItemStack(Items.POWDER_SNOW_BUCKET);
    }

    @Override
    public Optional<SoundEffect> V_() {
        return Optional.of(SoundEffects.BUCKET_FILL_POWDER_SNOW);
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return true;
    }
}
