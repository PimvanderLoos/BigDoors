package net.minecraft.world.item;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.ParticleParamBlock;
import net.minecraft.core.particles.Particles;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.context.ItemActionContext;
import net.minecraft.world.level.RayTrace;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SuspiciousSandBlockEntity;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;

public class BrushItem extends Item {

    public static final int TICKS_BETWEEN_SWEEPS = 10;
    private static final int USE_DURATION = 225;

    public BrushItem(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public EnumInteractionResult useOn(ItemActionContext itemactioncontext) {
        EntityHuman entityhuman = itemactioncontext.getPlayer();

        if (entityhuman != null) {
            entityhuman.startUsingItem(itemactioncontext.getHand());
        }

        return EnumInteractionResult.CONSUME;
    }

    @Override
    public EnumAnimation getUseAnimation(ItemStack itemstack) {
        return EnumAnimation.BRUSH;
    }

    @Override
    public int getUseDuration(ItemStack itemstack) {
        return 225;
    }

    @Override
    public void onUseTick(World world, EntityLiving entityliving, ItemStack itemstack, int i) {
        if (i >= 0 && entityliving instanceof EntityHuman) {
            EntityHuman entityhuman = (EntityHuman) entityliving;
            MovingObjectPositionBlock movingobjectpositionblock = Item.getPlayerPOVHitResult(world, entityhuman, RayTrace.FluidCollisionOption.NONE);
            BlockPosition blockposition = movingobjectpositionblock.getBlockPos();

            if (movingobjectpositionblock.getType() == MovingObjectPosition.EnumMovingObjectType.MISS) {
                entityliving.releaseUsingItem();
            } else {
                int j = this.getUseDuration(itemstack) - i + 1;

                if (j == 1 || j % 10 == 0) {
                    IBlockData iblockdata = world.getBlockState(blockposition);

                    this.spawnDustParticles(world, movingobjectpositionblock, iblockdata, entityliving.getViewVector(0.0F));
                    world.playSound(entityhuman, blockposition, SoundEffects.BRUSH_BRUSHING, SoundCategory.PLAYERS);
                    if (!world.isClientSide() && iblockdata.is(Blocks.SUSPICIOUS_SAND)) {
                        TileEntity tileentity = world.getBlockEntity(blockposition);

                        if (tileentity instanceof SuspiciousSandBlockEntity) {
                            SuspiciousSandBlockEntity suspicioussandblockentity = (SuspiciousSandBlockEntity) tileentity;
                            boolean flag = suspicioussandblockentity.brush(world.getGameTime(), entityhuman, movingobjectpositionblock.getDirection());

                            if (flag) {
                                itemstack.hurtAndBreak(1, entityliving, (entityliving1) -> {
                                    entityliving1.broadcastBreakEvent(EnumItemSlot.MAINHAND);
                                });
                            }
                        }
                    }
                }

            }
        } else {
            entityliving.releaseUsingItem();
        }
    }

    public void spawnDustParticles(World world, MovingObjectPositionBlock movingobjectpositionblock, IBlockData iblockdata, Vec3D vec3d) {
        double d0 = 3.0D;
        int i = world.getRandom().nextInt(7, 12);
        ParticleParamBlock particleparamblock = new ParticleParamBlock(Particles.BLOCK, iblockdata);
        EnumDirection enumdirection = movingobjectpositionblock.getDirection();
        BrushItem.a brushitem_a = BrushItem.a.fromDirection(vec3d, enumdirection);
        Vec3D vec3d1 = movingobjectpositionblock.getLocation();

        for (int j = 0; j < i; ++j) {
            world.addParticle(particleparamblock, vec3d1.x - (double) (enumdirection == EnumDirection.WEST ? 1.0E-6F : 0.0F), vec3d1.y, vec3d1.z - (double) (enumdirection == EnumDirection.NORTH ? 1.0E-6F : 0.0F), brushitem_a.xd() * 3.0D * world.getRandom().nextDouble(), 0.0D, brushitem_a.zd() * 3.0D * world.getRandom().nextDouble());
        }

    }

    private static record a(double xd, double yd, double zd) {

        private static final double ALONG_SIDE_DELTA = 1.0D;
        private static final double OUT_FROM_SIDE_DELTA = 0.1D;

        public static BrushItem.a fromDirection(Vec3D vec3d, EnumDirection enumdirection) {
            double d0 = 0.0D;
            BrushItem.a brushitem_a;

            switch (enumdirection) {
                case DOWN:
                    brushitem_a = new BrushItem.a(-vec3d.x(), 0.0D, vec3d.z());
                    break;
                case UP:
                    brushitem_a = new BrushItem.a(vec3d.z(), 0.0D, -vec3d.x());
                    break;
                case NORTH:
                    brushitem_a = new BrushItem.a(1.0D, 0.0D, -0.1D);
                    break;
                case SOUTH:
                    brushitem_a = new BrushItem.a(-1.0D, 0.0D, 0.1D);
                    break;
                case WEST:
                    brushitem_a = new BrushItem.a(-0.1D, 0.0D, -1.0D);
                    break;
                case EAST:
                    brushitem_a = new BrushItem.a(0.1D, 0.0D, 1.0D);
                    break;
                default:
                    throw new IncompatibleClassChangeError();
            }

            return brushitem_a;
        }
    }
}
