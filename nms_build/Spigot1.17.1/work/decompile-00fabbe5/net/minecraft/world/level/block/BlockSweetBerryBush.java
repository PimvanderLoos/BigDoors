package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockSweetBerryBush extends BlockPlant implements IBlockFragilePlantElement {

    private static final float HURT_SPEED_THRESHOLD = 0.003F;
    public static final int MAX_AGE = 3;
    public static final BlockStateInteger AGE = BlockProperties.AGE_3;
    private static final VoxelShape SAPLING_SHAPE = Block.a(3.0D, 0.0D, 3.0D, 13.0D, 8.0D, 13.0D);
    private static final VoxelShape MID_GROWTH_SHAPE = Block.a(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public BlockSweetBerryBush(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.k((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockSweetBerryBush.AGE, 0));
    }

    @Override
    public ItemStack a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(Items.SWEET_BERRIES);
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return (Integer) iblockdata.get(BlockSweetBerryBush.AGE) == 0 ? BlockSweetBerryBush.SAPLING_SHAPE : ((Integer) iblockdata.get(BlockSweetBerryBush.AGE) < 3 ? BlockSweetBerryBush.MID_GROWTH_SHAPE : super.a(iblockdata, iblockaccess, blockposition, voxelshapecollision));
    }

    @Override
    public boolean isTicking(IBlockData iblockdata) {
        return (Integer) iblockdata.get(BlockSweetBerryBush.AGE) < 3;
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        int i = (Integer) iblockdata.get(BlockSweetBerryBush.AGE);

        if (i < 3 && random.nextInt(5) == 0 && worldserver.getLightLevel(blockposition.up(), 0) >= 9) {
            worldserver.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockSweetBerryBush.AGE, i + 1), 2);
        }

    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        if (entity instanceof EntityLiving && entity.getEntityType() != EntityTypes.FOX && entity.getEntityType() != EntityTypes.BEE) {
            entity.a(iblockdata, new Vec3D(0.800000011920929D, 0.75D, 0.800000011920929D));
            if (!world.isClientSide && (Integer) iblockdata.get(BlockSweetBerryBush.AGE) > 0 && (entity.xOld != entity.locX() || entity.zOld != entity.locZ())) {
                double d0 = Math.abs(entity.locX() - entity.xOld);
                double d1 = Math.abs(entity.locZ() - entity.zOld);

                if (d0 >= 0.003000000026077032D || d1 >= 0.003000000026077032D) {
                    entity.damageEntity(DamageSource.SWEET_BERRY_BUSH, 1.0F);
                }
            }

        }
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        int i = (Integer) iblockdata.get(BlockSweetBerryBush.AGE);
        boolean flag = i == 3;

        if (!flag && entityhuman.b(enumhand).a(Items.BONE_MEAL)) {
            return EnumInteractionResult.PASS;
        } else if (i > 1) {
            int j = 1 + world.random.nextInt(2);

            a(world, blockposition, new ItemStack(Items.SWEET_BERRIES, j + (flag ? 1 : 0)));
            world.playSound((EntityHuman) null, blockposition, SoundEffects.SWEET_BERRY_BUSH_PICK_BERRIES, SoundCategory.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
            world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockSweetBerryBush.AGE, 1), 2);
            return EnumInteractionResult.a(world.isClientSide);
        } else {
            return super.interact(iblockdata, world, blockposition, entityhuman, enumhand, movingobjectpositionblock);
        }
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockSweetBerryBush.AGE);
    }

    @Override
    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return (Integer) iblockdata.get(BlockSweetBerryBush.AGE) < 3;
    }

    @Override
    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    @Override
    public void a(WorldServer worldserver, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        int i = Math.min(3, (Integer) iblockdata.get(BlockSweetBerryBush.AGE) + 1);

        worldserver.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockSweetBerryBush.AGE, i), 2);
    }
}
