package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.ParticleParamBlock;
import net.minecraft.core.particles.Particles;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.entity.item.EntityFallingBlock;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.Material;

public class BlockFalling extends Block implements Fallable {

    public BlockFalling(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        world.getBlockTickList().a(blockposition, this, this.c());
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        generatoraccess.getBlockTickList().a(blockposition, this, this.c());
        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public void tickAlways(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        if (canFallThrough(worldserver.getType(blockposition.down())) && blockposition.getY() >= worldserver.getMinBuildHeight()) {
            EntityFallingBlock entityfallingblock = new EntityFallingBlock(worldserver, (double) blockposition.getX() + 0.5D, (double) blockposition.getY(), (double) blockposition.getZ() + 0.5D, worldserver.getType(blockposition));

            this.a(entityfallingblock);
            worldserver.addEntity(entityfallingblock);
        }
    }

    protected void a(EntityFallingBlock entityfallingblock) {}

    protected int c() {
        return 2;
    }

    public static boolean canFallThrough(IBlockData iblockdata) {
        Material material = iblockdata.getMaterial();

        return iblockdata.isAir() || iblockdata.a((Tag) TagsBlock.FIRE) || material.isLiquid() || material.isReplaceable();
    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if (random.nextInt(16) == 0) {
            BlockPosition blockposition1 = blockposition.down();

            if (canFallThrough(world.getType(blockposition1))) {
                double d0 = (double) blockposition.getX() + random.nextDouble();
                double d1 = (double) blockposition.getY() - 0.05D;
                double d2 = (double) blockposition.getZ() + random.nextDouble();

                world.addParticle(new ParticleParamBlock(Particles.FALLING_DUST, iblockdata), d0, d1, d2, 0.0D, 0.0D, 0.0D);
            }
        }

    }

    public int d(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return -16777216;
    }
}
