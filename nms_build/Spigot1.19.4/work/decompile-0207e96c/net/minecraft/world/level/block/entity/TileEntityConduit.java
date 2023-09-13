package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketPlayOutTileEntityData;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.monster.IMonster;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;

public class TileEntityConduit extends TileEntity {

    private static final int BLOCK_REFRESH_RATE = 2;
    private static final int EFFECT_DURATION = 13;
    private static final float ROTATION_SPEED = -0.0375F;
    private static final int MIN_ACTIVE_SIZE = 16;
    private static final int MIN_KILL_SIZE = 42;
    private static final int KILL_RANGE = 8;
    private static final Block[] VALID_BLOCKS = new Block[]{Blocks.PRISMARINE, Blocks.PRISMARINE_BRICKS, Blocks.SEA_LANTERN, Blocks.DARK_PRISMARINE};
    public int tickCount;
    private float activeRotation;
    private boolean isActive;
    private boolean isHunting;
    private final List<BlockPosition> effectBlocks = Lists.newArrayList();
    @Nullable
    private EntityLiving destroyTarget;
    @Nullable
    private UUID destroyTargetUUID;
    private long nextAmbientSoundActivation;

    public TileEntityConduit(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.CONDUIT, blockposition, iblockdata);
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        if (nbttagcompound.hasUUID("Target")) {
            this.destroyTargetUUID = nbttagcompound.getUUID("Target");
        } else {
            this.destroyTargetUUID = null;
        }

    }

    @Override
    protected void saveAdditional(NBTTagCompound nbttagcompound) {
        super.saveAdditional(nbttagcompound);
        if (this.destroyTarget != null) {
            nbttagcompound.putUUID("Target", this.destroyTarget.getUUID());
        }

    }

    @Override
    public PacketPlayOutTileEntityData getUpdatePacket() {
        return PacketPlayOutTileEntityData.create(this);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    public static void clientTick(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityConduit tileentityconduit) {
        ++tileentityconduit.tickCount;
        long i = world.getGameTime();
        List<BlockPosition> list = tileentityconduit.effectBlocks;

        if (i % 40L == 0L) {
            tileentityconduit.isActive = updateShape(world, blockposition, list);
            updateHunting(tileentityconduit, list);
        }

        updateClientTarget(world, blockposition, tileentityconduit);
        animationTick(world, blockposition, list, tileentityconduit.destroyTarget, tileentityconduit.tickCount);
        if (tileentityconduit.isActive()) {
            ++tileentityconduit.activeRotation;
        }

    }

    public static void serverTick(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityConduit tileentityconduit) {
        ++tileentityconduit.tickCount;
        long i = world.getGameTime();
        List<BlockPosition> list = tileentityconduit.effectBlocks;

        if (i % 40L == 0L) {
            boolean flag = updateShape(world, blockposition, list);

            if (flag != tileentityconduit.isActive) {
                SoundEffect soundeffect = flag ? SoundEffects.CONDUIT_ACTIVATE : SoundEffects.CONDUIT_DEACTIVATE;

                world.playSound((EntityHuman) null, blockposition, soundeffect, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }

            tileentityconduit.isActive = flag;
            updateHunting(tileentityconduit, list);
            if (flag) {
                applyEffects(world, blockposition, list);
                updateDestroyTarget(world, blockposition, iblockdata, list, tileentityconduit);
            }
        }

        if (tileentityconduit.isActive()) {
            if (i % 80L == 0L) {
                world.playSound((EntityHuman) null, blockposition, SoundEffects.CONDUIT_AMBIENT, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }

            if (i > tileentityconduit.nextAmbientSoundActivation) {
                tileentityconduit.nextAmbientSoundActivation = i + 60L + (long) world.getRandom().nextInt(40);
                world.playSound((EntityHuman) null, blockposition, SoundEffects.CONDUIT_AMBIENT_SHORT, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        }

    }

    private static void updateHunting(TileEntityConduit tileentityconduit, List<BlockPosition> list) {
        tileentityconduit.setHunting(list.size() >= 42);
    }

    private static boolean updateShape(World world, BlockPosition blockposition, List<BlockPosition> list) {
        list.clear();

        int i;
        int j;
        int k;

        for (i = -1; i <= 1; ++i) {
            for (j = -1; j <= 1; ++j) {
                for (k = -1; k <= 1; ++k) {
                    BlockPosition blockposition1 = blockposition.offset(i, j, k);

                    if (!world.isWaterAt(blockposition1)) {
                        return false;
                    }
                }
            }
        }

        for (i = -2; i <= 2; ++i) {
            for (j = -2; j <= 2; ++j) {
                for (k = -2; k <= 2; ++k) {
                    int l = Math.abs(i);
                    int i1 = Math.abs(j);
                    int j1 = Math.abs(k);

                    if ((l > 1 || i1 > 1 || j1 > 1) && (i == 0 && (i1 == 2 || j1 == 2) || j == 0 && (l == 2 || j1 == 2) || k == 0 && (l == 2 || i1 == 2))) {
                        BlockPosition blockposition2 = blockposition.offset(i, j, k);
                        IBlockData iblockdata = world.getBlockState(blockposition2);
                        Block[] ablock = TileEntityConduit.VALID_BLOCKS;
                        int k1 = ablock.length;

                        for (int l1 = 0; l1 < k1; ++l1) {
                            Block block = ablock[l1];

                            if (iblockdata.is(block)) {
                                list.add(blockposition2);
                            }
                        }
                    }
                }
            }
        }

        return list.size() >= 16;
    }

    private static void applyEffects(World world, BlockPosition blockposition, List<BlockPosition> list) {
        int i = list.size();
        int j = i / 7 * 16;
        int k = blockposition.getX();
        int l = blockposition.getY();
        int i1 = blockposition.getZ();
        AxisAlignedBB axisalignedbb = (new AxisAlignedBB((double) k, (double) l, (double) i1, (double) (k + 1), (double) (l + 1), (double) (i1 + 1))).inflate((double) j).expandTowards(0.0D, (double) world.getHeight(), 0.0D);
        List<EntityHuman> list1 = world.getEntitiesOfClass(EntityHuman.class, axisalignedbb);

        if (!list1.isEmpty()) {
            Iterator iterator = list1.iterator();

            while (iterator.hasNext()) {
                EntityHuman entityhuman = (EntityHuman) iterator.next();

                if (blockposition.closerThan(entityhuman.blockPosition(), (double) j) && entityhuman.isInWaterOrRain()) {
                    entityhuman.addEffect(new MobEffect(MobEffects.CONDUIT_POWER, 260, 0, true, true));
                }
            }

        }
    }

    private static void updateDestroyTarget(World world, BlockPosition blockposition, IBlockData iblockdata, List<BlockPosition> list, TileEntityConduit tileentityconduit) {
        EntityLiving entityliving = tileentityconduit.destroyTarget;
        int i = list.size();

        if (i < 42) {
            tileentityconduit.destroyTarget = null;
        } else if (tileentityconduit.destroyTarget == null && tileentityconduit.destroyTargetUUID != null) {
            tileentityconduit.destroyTarget = findDestroyTarget(world, blockposition, tileentityconduit.destroyTargetUUID);
            tileentityconduit.destroyTargetUUID = null;
        } else if (tileentityconduit.destroyTarget == null) {
            List<EntityLiving> list1 = world.getEntitiesOfClass(EntityLiving.class, getDestroyRangeAABB(blockposition), (entityliving1) -> {
                return entityliving1 instanceof IMonster && entityliving1.isInWaterOrRain();
            });

            if (!list1.isEmpty()) {
                tileentityconduit.destroyTarget = (EntityLiving) list1.get(world.random.nextInt(list1.size()));
            }
        } else if (!tileentityconduit.destroyTarget.isAlive() || !blockposition.closerThan(tileentityconduit.destroyTarget.blockPosition(), 8.0D)) {
            tileentityconduit.destroyTarget = null;
        }

        if (tileentityconduit.destroyTarget != null) {
            world.playSound((EntityHuman) null, tileentityconduit.destroyTarget.getX(), tileentityconduit.destroyTarget.getY(), tileentityconduit.destroyTarget.getZ(), SoundEffects.CONDUIT_ATTACK_TARGET, SoundCategory.BLOCKS, 1.0F, 1.0F);
            tileentityconduit.destroyTarget.hurt(world.damageSources().magic(), 4.0F);
        }

        if (entityliving != tileentityconduit.destroyTarget) {
            world.sendBlockUpdated(blockposition, iblockdata, iblockdata, 2);
        }

    }

    private static void updateClientTarget(World world, BlockPosition blockposition, TileEntityConduit tileentityconduit) {
        if (tileentityconduit.destroyTargetUUID == null) {
            tileentityconduit.destroyTarget = null;
        } else if (tileentityconduit.destroyTarget == null || !tileentityconduit.destroyTarget.getUUID().equals(tileentityconduit.destroyTargetUUID)) {
            tileentityconduit.destroyTarget = findDestroyTarget(world, blockposition, tileentityconduit.destroyTargetUUID);
            if (tileentityconduit.destroyTarget == null) {
                tileentityconduit.destroyTargetUUID = null;
            }
        }

    }

    private static AxisAlignedBB getDestroyRangeAABB(BlockPosition blockposition) {
        int i = blockposition.getX();
        int j = blockposition.getY();
        int k = blockposition.getZ();

        return (new AxisAlignedBB((double) i, (double) j, (double) k, (double) (i + 1), (double) (j + 1), (double) (k + 1))).inflate(8.0D);
    }

    @Nullable
    private static EntityLiving findDestroyTarget(World world, BlockPosition blockposition, UUID uuid) {
        List<EntityLiving> list = world.getEntitiesOfClass(EntityLiving.class, getDestroyRangeAABB(blockposition), (entityliving) -> {
            return entityliving.getUUID().equals(uuid);
        });

        return list.size() == 1 ? (EntityLiving) list.get(0) : null;
    }

    private static void animationTick(World world, BlockPosition blockposition, List<BlockPosition> list, @Nullable Entity entity, int i) {
        RandomSource randomsource = world.random;
        double d0 = (double) (MathHelper.sin((float) (i + 35) * 0.1F) / 2.0F + 0.5F);

        d0 = (d0 * d0 + d0) * 0.30000001192092896D;
        Vec3D vec3d = new Vec3D((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 1.5D + d0, (double) blockposition.getZ() + 0.5D);
        Iterator iterator = list.iterator();

        float f;

        while (iterator.hasNext()) {
            BlockPosition blockposition1 = (BlockPosition) iterator.next();

            if (randomsource.nextInt(50) == 0) {
                BlockPosition blockposition2 = blockposition1.subtract(blockposition);

                f = -0.5F + randomsource.nextFloat() + (float) blockposition2.getX();
                float f1 = -2.0F + randomsource.nextFloat() + (float) blockposition2.getY();
                float f2 = -0.5F + randomsource.nextFloat() + (float) blockposition2.getZ();

                world.addParticle(Particles.NAUTILUS, vec3d.x, vec3d.y, vec3d.z, (double) f, (double) f1, (double) f2);
            }
        }

        if (entity != null) {
            Vec3D vec3d1 = new Vec3D(entity.getX(), entity.getEyeY(), entity.getZ());
            float f3 = (-0.5F + randomsource.nextFloat()) * (3.0F + entity.getBbWidth());
            float f4 = -1.0F + randomsource.nextFloat() * entity.getBbHeight();

            f = (-0.5F + randomsource.nextFloat()) * (3.0F + entity.getBbWidth());
            Vec3D vec3d2 = new Vec3D((double) f3, (double) f4, (double) f);

            world.addParticle(Particles.NAUTILUS, vec3d1.x, vec3d1.y, vec3d1.z, vec3d2.x, vec3d2.y, vec3d2.z);
        }

    }

    public boolean isActive() {
        return this.isActive;
    }

    public boolean isHunting() {
        return this.isHunting;
    }

    private void setHunting(boolean flag) {
        this.isHunting = flag;
    }

    public float getActiveRotation(float f) {
        return (this.activeRotation + f) * -0.0375F;
    }
}
