package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.Particles;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.IProjectile;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;

public abstract class AbstractCandleBlock extends Block {

    public static final int LIGHT_PER_CANDLE = 3;
    public static final BlockStateBoolean LIT = BlockProperties.LIT;

    protected AbstractCandleBlock(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    protected abstract Iterable<Vec3D> a(IBlockData iblockdata);

    public static boolean b(IBlockData iblockdata) {
        return iblockdata.b(AbstractCandleBlock.LIT) && (iblockdata.a((Tag) TagsBlock.CANDLES) || iblockdata.a((Tag) TagsBlock.CANDLE_CAKES)) && (Boolean) iblockdata.get(AbstractCandleBlock.LIT);
    }

    @Override
    public void a(World world, IBlockData iblockdata, MovingObjectPositionBlock movingobjectpositionblock, IProjectile iprojectile) {
        if (!world.isClientSide && iprojectile.isBurning() && this.c(iblockdata)) {
            a(world, iblockdata, movingobjectpositionblock.getBlockPosition(), true);
        }

    }

    protected boolean c(IBlockData iblockdata) {
        return !(Boolean) iblockdata.get(AbstractCandleBlock.LIT);
    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if ((Boolean) iblockdata.get(AbstractCandleBlock.LIT)) {
            this.a(iblockdata).forEach((vec3d) -> {
                a(world, vec3d.add((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ()), random);
            });
        }
    }

    private static void a(World world, Vec3D vec3d, Random random) {
        float f = random.nextFloat();

        if (f < 0.3F) {
            world.addParticle(Particles.SMOKE, vec3d.x, vec3d.y, vec3d.z, 0.0D, 0.0D, 0.0D);
            if (f < 0.17F) {
                world.a(vec3d.x + 0.5D, vec3d.y + 0.5D, vec3d.z + 0.5D, SoundEffects.CANDLE_AMBIENT, SoundCategory.BLOCKS, 1.0F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);
            }
        }

        world.addParticle(Particles.SMALL_FLAME, vec3d.x, vec3d.y, vec3d.z, 0.0D, 0.0D, 0.0D);
    }

    public static void a(@Nullable EntityHuman entityhuman, IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition) {
        a(generatoraccess, iblockdata, blockposition, false);
        if (iblockdata.getBlock() instanceof AbstractCandleBlock) {
            ((AbstractCandleBlock) iblockdata.getBlock()).a(iblockdata).forEach((vec3d) -> {
                generatoraccess.addParticle(Particles.SMOKE, (double) blockposition.getX() + vec3d.getX(), (double) blockposition.getY() + vec3d.getY(), (double) blockposition.getZ() + vec3d.getZ(), 0.0D, 0.10000000149011612D, 0.0D);
            });
        }

        generatoraccess.playSound((EntityHuman) null, blockposition, SoundEffects.CANDLE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.0F);
        generatoraccess.a((Entity) entityhuman, GameEvent.BLOCK_CHANGE, blockposition);
    }

    private static void a(GeneratorAccess generatoraccess, IBlockData iblockdata, BlockPosition blockposition, boolean flag) {
        generatoraccess.setTypeAndData(blockposition, (IBlockData) iblockdata.set(AbstractCandleBlock.LIT, flag), 11);
    }
}
