package net.minecraft.world.entity.monster;

import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
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

    public static boolean a(EntityTypes<EntitySkeletonStray> entitytypes, WorldAccess worldaccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        BlockPosition blockposition1 = blockposition;

        do {
            blockposition1 = blockposition1.up();
        } while (worldaccess.getType(blockposition1).a(Blocks.POWDER_SNOW));

        return b(entitytypes, worldaccess, enummobspawn, blockposition, random) && (enummobspawn == EnumMobSpawn.SPAWNER || worldaccess.g(blockposition1.down()));
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.STRAY_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.STRAY_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.STRAY_DEATH;
    }

    @Override
    SoundEffect p() {
        return SoundEffects.STRAY_STEP;
    }

    @Override
    protected EntityArrow b(ItemStack itemstack, float f) {
        EntityArrow entityarrow = super.b(itemstack, f);

        if (entityarrow instanceof EntityTippedArrow) {
            ((EntityTippedArrow) entityarrow).addEffect(new MobEffect(MobEffects.MOVEMENT_SLOWDOWN, 600));
        }

        return entityarrow;
    }
}
