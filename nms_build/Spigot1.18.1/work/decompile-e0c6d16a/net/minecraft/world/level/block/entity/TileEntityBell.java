package net.minecraft.world.level.block.entity;

import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.IPosition;
import net.minecraft.core.particles.Particles;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.TagsEntity;
import net.minecraft.util.ColorUtil;
import net.minecraft.util.MathHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.AxisAlignedBB;
import org.apache.commons.lang3.mutable.MutableInt;

public class TileEntityBell extends TileEntity {

    private static final int DURATION = 50;
    private static final int GLOW_DURATION = 60;
    private static final int MIN_TICKS_BETWEEN_SEARCHES = 60;
    private static final int MAX_RESONATION_TICKS = 40;
    private static final int TICKS_BEFORE_RESONATION = 5;
    private static final int SEARCH_RADIUS = 48;
    private static final int HEAR_BELL_RADIUS = 32;
    private static final int HIGHLIGHT_RAIDERS_RADIUS = 48;
    private long lastRingTimestamp;
    public int ticks;
    public boolean shaking;
    public EnumDirection clickDirection;
    private List<EntityLiving> nearbyEntities;
    private boolean resonating;
    private int resonationTicks;

    public TileEntityBell(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.BELL, blockposition, iblockdata);
    }

    @Override
    public boolean triggerEvent(int i, int j) {
        if (i == 1) {
            this.updateEntities();
            this.resonationTicks = 0;
            this.clickDirection = EnumDirection.from3DDataValue(j);
            this.ticks = 0;
            this.shaking = true;
            return true;
        } else {
            return super.triggerEvent(i, j);
        }
    }

    private static void tick(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityBell tileentitybell, TileEntityBell.a tileentitybell_a) {
        if (tileentitybell.shaking) {
            ++tileentitybell.ticks;
        }

        if (tileentitybell.ticks >= 50) {
            tileentitybell.shaking = false;
            tileentitybell.ticks = 0;
        }

        if (tileentitybell.ticks >= 5 && tileentitybell.resonationTicks == 0 && areRaidersNearby(blockposition, tileentitybell.nearbyEntities)) {
            tileentitybell.resonating = true;
            world.playSound((EntityHuman) null, blockposition, SoundEffects.BELL_RESONATE, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }

        if (tileentitybell.resonating) {
            if (tileentitybell.resonationTicks < 40) {
                ++tileentitybell.resonationTicks;
            } else {
                tileentitybell_a.run(world, blockposition, tileentitybell.nearbyEntities);
                tileentitybell.resonating = false;
            }
        }

    }

    public static void clientTick(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityBell tileentitybell) {
        tick(world, blockposition, iblockdata, tileentitybell, TileEntityBell::showBellParticles);
    }

    public static void serverTick(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityBell tileentitybell) {
        tick(world, blockposition, iblockdata, tileentitybell, TileEntityBell::makeRaidersGlow);
    }

    public void onHit(EnumDirection enumdirection) {
        BlockPosition blockposition = this.getBlockPos();

        this.clickDirection = enumdirection;
        if (this.shaking) {
            this.ticks = 0;
        } else {
            this.shaking = true;
        }

        this.level.blockEvent(blockposition, this.getBlockState().getBlock(), 1, enumdirection.get3DDataValue());
    }

    private void updateEntities() {
        BlockPosition blockposition = this.getBlockPos();

        if (this.level.getGameTime() > this.lastRingTimestamp + 60L || this.nearbyEntities == null) {
            this.lastRingTimestamp = this.level.getGameTime();
            AxisAlignedBB axisalignedbb = (new AxisAlignedBB(blockposition)).inflate(48.0D);

            this.nearbyEntities = this.level.getEntitiesOfClass(EntityLiving.class, axisalignedbb);
        }

        if (!this.level.isClientSide) {
            Iterator iterator = this.nearbyEntities.iterator();

            while (iterator.hasNext()) {
                EntityLiving entityliving = (EntityLiving) iterator.next();

                if (entityliving.isAlive() && !entityliving.isRemoved() && blockposition.closerThan((IPosition) entityliving.position(), 32.0D)) {
                    entityliving.getBrain().setMemory(MemoryModuleType.HEARD_BELL_TIME, (Object) this.level.getGameTime());
                }
            }
        }

    }

    private static boolean areRaidersNearby(BlockPosition blockposition, List<EntityLiving> list) {
        Iterator iterator = list.iterator();

        EntityLiving entityliving;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            entityliving = (EntityLiving) iterator.next();
        } while (!entityliving.isAlive() || entityliving.isRemoved() || !blockposition.closerThan((IPosition) entityliving.position(), 32.0D) || !entityliving.getType().is(TagsEntity.RAIDERS));

        return true;
    }

    private static void makeRaidersGlow(World world, BlockPosition blockposition, List<EntityLiving> list) {
        list.stream().filter((entityliving) -> {
            return isRaiderWithinRange(blockposition, entityliving);
        }).forEach(TileEntityBell::glow);
    }

    private static void showBellParticles(World world, BlockPosition blockposition, List<EntityLiving> list) {
        MutableInt mutableint = new MutableInt(16700985);
        int i = (int) list.stream().filter((entityliving) -> {
            return blockposition.closerThan((IPosition) entityliving.position(), 48.0D);
        }).count();

        list.stream().filter((entityliving) -> {
            return isRaiderWithinRange(blockposition, entityliving);
        }).forEach((entityliving) -> {
            float f = 1.0F;
            double d0 = Math.sqrt((entityliving.getX() - (double) blockposition.getX()) * (entityliving.getX() - (double) blockposition.getX()) + (entityliving.getZ() - (double) blockposition.getZ()) * (entityliving.getZ() - (double) blockposition.getZ()));
            double d1 = (double) ((float) blockposition.getX() + 0.5F) + 1.0D / d0 * (entityliving.getX() - (double) blockposition.getX());
            double d2 = (double) ((float) blockposition.getZ() + 0.5F) + 1.0D / d0 * (entityliving.getZ() - (double) blockposition.getZ());
            int j = MathHelper.clamp((i - 21) / -2, (int) 3, (int) 15);

            for (int k = 0; k < j; ++k) {
                int l = mutableint.addAndGet(5);
                double d3 = (double) ColorUtil.a.red(l) / 255.0D;
                double d4 = (double) ColorUtil.a.green(l) / 255.0D;
                double d5 = (double) ColorUtil.a.blue(l) / 255.0D;

                world.addParticle(Particles.ENTITY_EFFECT, d1, (double) ((float) blockposition.getY() + 0.5F), d2, d3, d4, d5);
            }

        });
    }

    private static boolean isRaiderWithinRange(BlockPosition blockposition, EntityLiving entityliving) {
        return entityliving.isAlive() && !entityliving.isRemoved() && blockposition.closerThan((IPosition) entityliving.position(), 48.0D) && entityliving.getType().is(TagsEntity.RAIDERS);
    }

    private static void glow(EntityLiving entityliving) {
        entityliving.addEffect(new MobEffect(MobEffects.GLOWING, 60));
    }

    @FunctionalInterface
    private interface a {

        void run(World world, BlockPosition blockposition, List<EntityLiving> list);
    }
}
