package net.minecraft.world.level.block.entity;

import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.IPosition;
import net.minecraft.core.particles.Particles;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
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
    public boolean setProperty(int i, int j) {
        if (i == 1) {
            this.d();
            this.resonationTicks = 0;
            this.clickDirection = EnumDirection.fromType1(j);
            this.ticks = 0;
            this.shaking = true;
            return true;
        } else {
            return super.setProperty(i, j);
        }
    }

    private static void a(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityBell tileentitybell, TileEntityBell.a tileentitybell_a) {
        if (tileentitybell.shaking) {
            ++tileentitybell.ticks;
        }

        if (tileentitybell.ticks >= 50) {
            tileentitybell.shaking = false;
            tileentitybell.ticks = 0;
        }

        if (tileentitybell.ticks >= 5 && tileentitybell.resonationTicks == 0 && a(blockposition, tileentitybell.nearbyEntities)) {
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

    public static void a(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityBell tileentitybell) {
        a(world, blockposition, iblockdata, tileentitybell, TileEntityBell::b);
    }

    public static void b(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityBell tileentitybell) {
        a(world, blockposition, iblockdata, tileentitybell, TileEntityBell::a);
    }

    public void a(EnumDirection enumdirection) {
        BlockPosition blockposition = this.getPosition();

        this.clickDirection = enumdirection;
        if (this.shaking) {
            this.ticks = 0;
        } else {
            this.shaking = true;
        }

        this.level.playBlockAction(blockposition, this.getBlock().getBlock(), 1, enumdirection.b());
    }

    private void d() {
        BlockPosition blockposition = this.getPosition();

        if (this.level.getTime() > this.lastRingTimestamp + 60L || this.nearbyEntities == null) {
            this.lastRingTimestamp = this.level.getTime();
            AxisAlignedBB axisalignedbb = (new AxisAlignedBB(blockposition)).g(48.0D);

            this.nearbyEntities = this.level.a(EntityLiving.class, axisalignedbb);
        }

        if (!this.level.isClientSide) {
            Iterator iterator = this.nearbyEntities.iterator();

            while (iterator.hasNext()) {
                EntityLiving entityliving = (EntityLiving) iterator.next();

                if (entityliving.isAlive() && !entityliving.isRemoved() && blockposition.a((IPosition) entityliving.getPositionVector(), 32.0D)) {
                    entityliving.getBehaviorController().setMemory(MemoryModuleType.HEARD_BELL_TIME, (Object) this.level.getTime());
                }
            }
        }

    }

    private static boolean a(BlockPosition blockposition, List<EntityLiving> list) {
        Iterator iterator = list.iterator();

        EntityLiving entityliving;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            entityliving = (EntityLiving) iterator.next();
        } while (!entityliving.isAlive() || entityliving.isRemoved() || !blockposition.a((IPosition) entityliving.getPositionVector(), 32.0D) || !entityliving.getEntityType().a((Tag) TagsEntity.RAIDERS));

        return true;
    }

    private static void a(World world, BlockPosition blockposition, List<EntityLiving> list) {
        list.stream().filter((entityliving) -> {
            return a(blockposition, entityliving);
        }).forEach(TileEntityBell::a);
    }

    private static void b(World world, BlockPosition blockposition, List<EntityLiving> list) {
        MutableInt mutableint = new MutableInt(16700985);
        int i = (int) list.stream().filter((entityliving) -> {
            return blockposition.a((IPosition) entityliving.getPositionVector(), 48.0D);
        }).count();

        list.stream().filter((entityliving) -> {
            return a(blockposition, entityliving);
        }).forEach((entityliving) -> {
            float f = 1.0F;
            double d0 = Math.sqrt((entityliving.locX() - (double) blockposition.getX()) * (entityliving.locX() - (double) blockposition.getX()) + (entityliving.locZ() - (double) blockposition.getZ()) * (entityliving.locZ() - (double) blockposition.getZ()));
            double d1 = (double) ((float) blockposition.getX() + 0.5F) + 1.0D / d0 * (entityliving.locX() - (double) blockposition.getX());
            double d2 = (double) ((float) blockposition.getZ() + 0.5F) + 1.0D / d0 * (entityliving.locZ() - (double) blockposition.getZ());
            int j = MathHelper.clamp((i - 21) / -2, 3, 15);

            for (int k = 0; k < j; ++k) {
                int l = mutableint.addAndGet(5);
                double d3 = (double) ColorUtil.a.b(l) / 255.0D;
                double d4 = (double) ColorUtil.a.c(l) / 255.0D;
                double d5 = (double) ColorUtil.a.d(l) / 255.0D;

                world.addParticle(Particles.ENTITY_EFFECT, d1, (double) ((float) blockposition.getY() + 0.5F), d2, d3, d4, d5);
            }

        });
    }

    private static boolean a(BlockPosition blockposition, EntityLiving entityliving) {
        return entityliving.isAlive() && !entityliving.isRemoved() && blockposition.a((IPosition) entityliving.getPositionVector(), 48.0D) && entityliving.getEntityType().a((Tag) TagsEntity.RAIDERS);
    }

    private static void a(EntityLiving entityliving) {
        entityliving.addEffect(new MobEffect(MobEffects.GLOWING, 60));
    }

    @FunctionalInterface
    private interface a {

        void run(World world, BlockPosition blockposition, List<EntityLiving> list);
    }
}
