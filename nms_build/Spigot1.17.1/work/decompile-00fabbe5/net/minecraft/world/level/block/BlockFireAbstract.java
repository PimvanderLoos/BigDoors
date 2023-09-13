package net.minecraft.world.level.block;

import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.Particles;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.portal.BlockPortalShape;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public abstract class BlockFireAbstract extends Block {

    private static final int SECONDS_ON_FIRE = 8;
    private final float fireDamage;
    protected static final float AABB_OFFSET = 1.0F;
    protected static final VoxelShape DOWN_AABB = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

    public BlockFireAbstract(BlockBase.Info blockbase_info, float f) {
        super(blockbase_info);
        this.fireDamage = f;
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return a((IBlockAccess) blockactioncontext.getWorld(), blockactioncontext.getClickPosition());
    }

    public static IBlockData a(IBlockAccess iblockaccess, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.down();
        IBlockData iblockdata = iblockaccess.getType(blockposition1);

        return BlockSoulFire.h(iblockdata) ? Blocks.SOUL_FIRE.getBlockData() : ((BlockFire) Blocks.FIRE).getPlacedState(iblockaccess, blockposition);
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockFireAbstract.DOWN_AABB;
    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if (random.nextInt(24) == 0) {
            world.a((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D, SoundEffects.FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);
        }

        BlockPosition blockposition1 = blockposition.down();
        IBlockData iblockdata1 = world.getType(blockposition1);
        double d0;
        double d1;
        double d2;
        int i;

        if (!this.f(iblockdata1) && !iblockdata1.d(world, blockposition1, EnumDirection.UP)) {
            if (this.f(world.getType(blockposition.west()))) {
                for (i = 0; i < 2; ++i) {
                    d0 = (double) blockposition.getX() + random.nextDouble() * 0.10000000149011612D;
                    d1 = (double) blockposition.getY() + random.nextDouble();
                    d2 = (double) blockposition.getZ() + random.nextDouble();
                    world.addParticle(Particles.LARGE_SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                }
            }

            if (this.f(world.getType(blockposition.east()))) {
                for (i = 0; i < 2; ++i) {
                    d0 = (double) (blockposition.getX() + 1) - random.nextDouble() * 0.10000000149011612D;
                    d1 = (double) blockposition.getY() + random.nextDouble();
                    d2 = (double) blockposition.getZ() + random.nextDouble();
                    world.addParticle(Particles.LARGE_SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                }
            }

            if (this.f(world.getType(blockposition.north()))) {
                for (i = 0; i < 2; ++i) {
                    d0 = (double) blockposition.getX() + random.nextDouble();
                    d1 = (double) blockposition.getY() + random.nextDouble();
                    d2 = (double) blockposition.getZ() + random.nextDouble() * 0.10000000149011612D;
                    world.addParticle(Particles.LARGE_SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                }
            }

            if (this.f(world.getType(blockposition.south()))) {
                for (i = 0; i < 2; ++i) {
                    d0 = (double) blockposition.getX() + random.nextDouble();
                    d1 = (double) blockposition.getY() + random.nextDouble();
                    d2 = (double) (blockposition.getZ() + 1) - random.nextDouble() * 0.10000000149011612D;
                    world.addParticle(Particles.LARGE_SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                }
            }

            if (this.f(world.getType(blockposition.up()))) {
                for (i = 0; i < 2; ++i) {
                    d0 = (double) blockposition.getX() + random.nextDouble();
                    d1 = (double) (blockposition.getY() + 1) - random.nextDouble() * 0.10000000149011612D;
                    d2 = (double) blockposition.getZ() + random.nextDouble();
                    world.addParticle(Particles.LARGE_SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                }
            }
        } else {
            for (i = 0; i < 3; ++i) {
                d0 = (double) blockposition.getX() + random.nextDouble();
                d1 = (double) blockposition.getY() + random.nextDouble() * 0.5D + 0.5D;
                d2 = (double) blockposition.getZ() + random.nextDouble();
                world.addParticle(Particles.LARGE_SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
            }
        }

    }

    protected abstract boolean f(IBlockData iblockdata);

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        if (!entity.isFireProof()) {
            entity.setFireTicks(entity.getFireTicks() + 1);
            if (entity.getFireTicks() == 0) {
                entity.setOnFire(8);
            }

            entity.damageEntity(DamageSource.IN_FIRE, this.fireDamage);
        }

        super.a(iblockdata, world, blockposition, entity);
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata1.a(iblockdata.getBlock())) {
            if (a(world)) {
                Optional<BlockPortalShape> optional = BlockPortalShape.a((GeneratorAccess) world, blockposition, EnumDirection.EnumAxis.X);

                if (optional.isPresent()) {
                    ((BlockPortalShape) optional.get()).createPortal();
                    return;
                }
            }

            if (!iblockdata.canPlace(world, blockposition)) {
                world.a(blockposition, false);
            }

        }
    }

    private static boolean a(World world) {
        return world.getDimensionKey() == World.OVERWORLD || world.getDimensionKey() == World.NETHER;
    }

    @Override
    protected void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata) {}

    @Override
    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        if (!world.isClientSide()) {
            world.a((EntityHuman) null, 1009, blockposition, 0);
        }

        super.a(world, blockposition, iblockdata, entityhuman);
    }

    public static boolean a(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        IBlockData iblockdata = world.getType(blockposition);

        return !iblockdata.isAir() ? false : a((IBlockAccess) world, blockposition).canPlace(world, blockposition) || b(world, blockposition, enumdirection);
    }

    private static boolean b(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        if (!a(world)) {
            return false;
        } else {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.i();
            boolean flag = false;
            EnumDirection[] aenumdirection = EnumDirection.values();
            int i = aenumdirection.length;

            for (int j = 0; j < i; ++j) {
                EnumDirection enumdirection1 = aenumdirection[j];

                if (world.getType(blockposition_mutableblockposition.g(blockposition).c(enumdirection1)).a(Blocks.OBSIDIAN)) {
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                return false;
            } else {
                EnumDirection.EnumAxis enumdirection_enumaxis = enumdirection.n().d() ? enumdirection.h().n() : EnumDirection.EnumDirectionLimit.HORIZONTAL.b(world.random);

                return BlockPortalShape.a((GeneratorAccess) world, blockposition, enumdirection_enumaxis).isPresent();
            }
        }
    }
}
