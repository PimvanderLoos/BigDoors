package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.Particles;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;

public class BlockMagma extends Block {

    public BlockMagma(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public void stepOn(World world, BlockPosition blockposition, Entity entity) {
        if (!entity.isFireProof() && entity instanceof EntityLiving && !EnchantmentManager.i((EntityLiving) entity)) {
            entity.damageEntity(DamageSource.HOT_FLOOR, 1.0F);
        }

        super.stepOn(world, blockposition, entity);
    }

    @Override
    public void tickAlways(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        BlockBubbleColumn.a(worldserver, blockposition.up(), true);
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (enumdirection == EnumDirection.UP && iblockdata1.a(Blocks.WATER)) {
            generatoraccess.getBlockTickList().a(blockposition, this, 20);
        }

        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        BlockPosition blockposition1 = blockposition.up();

        if (worldserver.getFluid(blockposition).a((Tag) TagsFluid.WATER)) {
            worldserver.playSound((EntityHuman) null, blockposition, SoundEffects.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (worldserver.random.nextFloat() - worldserver.random.nextFloat()) * 0.8F);
            worldserver.a(Particles.LARGE_SMOKE, (double) blockposition1.getX() + 0.5D, (double) blockposition1.getY() + 0.25D, (double) blockposition1.getZ() + 0.5D, 8, 0.5D, 0.25D, 0.5D, 0.0D);
        }

    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        world.getBlockTickList().a(blockposition, this, 20);
    }
}
