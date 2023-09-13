package net.minecraft.world.entity.monster;

import net.minecraft.core.BlockPosition;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.entity.projectile.EntityTippedArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.Blocks;

public class EntitySkeletonStray extends EntitySkeletonAbstract {

    public EntitySkeletonStray(EntityTypes<? extends EntitySkeletonStray> entitytypes, World world) {
        super(entitytypes, world);
    }

    public static boolean checkStraySpawnRules(EntityTypes<EntitySkeletonStray> entitytypes, WorldAccess worldaccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, RandomSource randomsource) {
        BlockPosition blockposition1 = blockposition;

        do {
            blockposition1 = blockposition1.above();
        } while (worldaccess.getBlockState(blockposition1).is(Blocks.POWDER_SNOW));

        return checkMonsterSpawnRules(entitytypes, worldaccess, enummobspawn, blockposition, randomsource) && (enummobspawn == EnumMobSpawn.SPAWNER || worldaccess.canSeeSky(blockposition1.below()));
    }

    @Override
    protected SoundEffect getAmbientSound() {
        return SoundEffects.STRAY_AMBIENT;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.STRAY_HURT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.STRAY_DEATH;
    }

    @Override
    SoundEffect getStepSound() {
        return SoundEffects.STRAY_STEP;
    }

    @Override
    protected EntityArrow getArrow(ItemStack itemstack, float f) {
        EntityArrow entityarrow = super.getArrow(itemstack, f);

        if (entityarrow instanceof EntityTippedArrow) {
            ((EntityTippedArrow) entityarrow).addEffect(new MobEffect(MobEffects.MOVEMENT_SLOWDOWN, 600));
        }

        return entityarrow;
    }
}
