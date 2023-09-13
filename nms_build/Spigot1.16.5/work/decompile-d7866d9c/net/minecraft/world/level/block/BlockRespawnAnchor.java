package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.vehicle.DismountUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.ICollisionAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;

public class BlockRespawnAnchor extends Block {

    public static final BlockStateInteger a = BlockProperties.aC;
    private static final ImmutableList<BaseBlockPosition> b = ImmutableList.of(new BaseBlockPosition(0, 0, -1), new BaseBlockPosition(-1, 0, 0), new BaseBlockPosition(0, 0, 1), new BaseBlockPosition(1, 0, 0), new BaseBlockPosition(-1, 0, -1), new BaseBlockPosition(1, 0, -1), new BaseBlockPosition(-1, 0, 1), new BaseBlockPosition(1, 0, 1));
    private static final ImmutableList<BaseBlockPosition> c = (new Builder()).addAll(BlockRespawnAnchor.b).addAll(BlockRespawnAnchor.b.stream().map(BaseBlockPosition::down).iterator()).addAll(BlockRespawnAnchor.b.stream().map(BaseBlockPosition::up).iterator()).add(new BaseBlockPosition(0, 1, 0)).build();

    public BlockRespawnAnchor(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.j((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockRespawnAnchor.a, 0));
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (enumhand == EnumHand.MAIN_HAND && !a(itemstack) && a(entityhuman.b(EnumHand.OFF_HAND))) {
            return EnumInteractionResult.PASS;
        } else if (a(itemstack) && h(iblockdata)) {
            a(world, blockposition, iblockdata);
            if (!entityhuman.abilities.canInstantlyBuild) {
                itemstack.subtract(1);
            }

            return EnumInteractionResult.a(world.isClientSide);
        } else if ((Integer) iblockdata.get(BlockRespawnAnchor.a) == 0) {
            return EnumInteractionResult.PASS;
        } else if (!a(world)) {
            if (!world.isClientSide) {
                this.d(iblockdata, world, blockposition);
            }

            return EnumInteractionResult.a(world.isClientSide);
        } else {
            if (!world.isClientSide) {
                EntityPlayer entityplayer = (EntityPlayer) entityhuman;

                if (entityplayer.getSpawnDimension() != world.getDimensionKey() || !entityplayer.getSpawn().equals(blockposition)) {
                    entityplayer.setRespawnPosition(world.getDimensionKey(), blockposition, 0.0F, false, true);
                    world.playSound((EntityHuman) null, (double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D, SoundEffects.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    return EnumInteractionResult.SUCCESS;
                }
            }

            return EnumInteractionResult.CONSUME;
        }
    }

    private static boolean a(ItemStack itemstack) {
        return itemstack.getItem() == Items.dq;
    }

    private static boolean h(IBlockData iblockdata) {
        return (Integer) iblockdata.get(BlockRespawnAnchor.a) < 4;
    }

    private static boolean a(BlockPosition blockposition, World world) {
        Fluid fluid = world.getFluid(blockposition);

        if (!fluid.a((Tag) TagsFluid.WATER)) {
            return false;
        } else if (fluid.isSource()) {
            return true;
        } else {
            float f = (float) fluid.e();

            if (f < 2.0F) {
                return false;
            } else {
                Fluid fluid1 = world.getFluid(blockposition.down());

                return !fluid1.a((Tag) TagsFluid.WATER);
            }
        }
    }

    private void d(IBlockData iblockdata, World world, final BlockPosition blockposition) {
        world.a(blockposition, false);
        Stream stream = EnumDirection.EnumDirectionLimit.HORIZONTAL.a();

        blockposition.getClass();
        boolean flag = stream.map(blockposition::shift).anyMatch((blockposition1) -> {
            return a(blockposition1, world);
        });
        final boolean flag1 = flag || world.getFluid(blockposition.up()).a((Tag) TagsFluid.WATER);
        ExplosionDamageCalculator explosiondamagecalculator = new ExplosionDamageCalculator() {
            @Override
            public Optional<Float> a(Explosion explosion, IBlockAccess iblockaccess, BlockPosition blockposition1, IBlockData iblockdata1, Fluid fluid) {
                return blockposition1.equals(blockposition) && flag1 ? Optional.of(Blocks.WATER.getDurability()) : super.a(explosion, iblockaccess, blockposition1, iblockdata1, fluid);
            }
        };

        world.createExplosion((Entity) null, DamageSource.a(), explosiondamagecalculator, (double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D, 5.0F, true, Explosion.Effect.DESTROY);
    }

    public static boolean a(World world) {
        return world.getDimensionManager().isRespawnAnchorWorks();
    }

    public static void a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockRespawnAnchor.a, (Integer) iblockdata.get(BlockRespawnAnchor.a) + 1), 3);
        world.playSound((EntityHuman) null, (double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D, SoundEffects.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockRespawnAnchor.a);
    }

    @Override
    public boolean isComplexRedstone(IBlockData iblockdata) {
        return true;
    }

    public static int a(IBlockData iblockdata, int i) {
        return MathHelper.d((float) ((Integer) iblockdata.get(BlockRespawnAnchor.a) - 0) / 4.0F * (float) i);
    }

    @Override
    public int a(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return a(iblockdata, 15);
    }

    public static Optional<Vec3D> a(EntityTypes<?> entitytypes, ICollisionAccess icollisionaccess, BlockPosition blockposition) {
        Optional<Vec3D> optional = a(entitytypes, icollisionaccess, blockposition, true);

        return optional.isPresent() ? optional : a(entitytypes, icollisionaccess, blockposition, false);
    }

    private static Optional<Vec3D> a(EntityTypes<?> entitytypes, ICollisionAccess icollisionaccess, BlockPosition blockposition, boolean flag) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        UnmodifiableIterator unmodifiableiterator = BlockRespawnAnchor.c.iterator();

        Vec3D vec3d;

        do {
            if (!unmodifiableiterator.hasNext()) {
                return Optional.empty();
            }

            BaseBlockPosition baseblockposition = (BaseBlockPosition) unmodifiableiterator.next();

            blockposition_mutableblockposition.g(blockposition).h(baseblockposition);
            vec3d = DismountUtil.a(entitytypes, icollisionaccess, blockposition_mutableblockposition, flag);
        } while (vec3d == null);

        return Optional.of(vec3d);
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
