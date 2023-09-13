package net.minecraft.world.entity.monster;

import java.util.function.Predicate;
import net.minecraft.core.BlockPosition;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.RandomSource;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemProjectileWeapon;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.EnumSkyBlock;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.dimension.DimensionManager;

public abstract class EntityMonster extends EntityCreature implements IMonster {

    protected EntityMonster(EntityTypes<? extends EntityMonster> entitytypes, World world) {
        super(entitytypes, world);
        this.xpReward = 5;
    }

    @Override
    public SoundCategory getSoundSource() {
        return SoundCategory.HOSTILE;
    }

    @Override
    public void aiStep() {
        this.updateSwingTime();
        this.updateNoActionTime();
        super.aiStep();
    }

    protected void updateNoActionTime() {
        float f = this.getLightLevelDependentMagicValue();

        if (f > 0.5F) {
            this.noActionTime += 2;
        }

    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return true;
    }

    @Override
    protected SoundEffect getSwimSound() {
        return SoundEffects.HOSTILE_SWIM;
    }

    @Override
    protected SoundEffect getSwimSplashSound() {
        return SoundEffects.HOSTILE_SPLASH;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.HOSTILE_HURT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.HOSTILE_DEATH;
    }

    @Override
    public EntityLiving.a getFallSounds() {
        return new EntityLiving.a(SoundEffects.HOSTILE_SMALL_FALL, SoundEffects.HOSTILE_BIG_FALL);
    }

    @Override
    public float getWalkTargetValue(BlockPosition blockposition, IWorldReader iworldreader) {
        return -iworldreader.getPathfindingCostFromLightLevels(blockposition);
    }

    public static boolean isDarkEnoughToSpawn(WorldAccess worldaccess, BlockPosition blockposition, RandomSource randomsource) {
        if (worldaccess.getBrightness(EnumSkyBlock.SKY, blockposition) > randomsource.nextInt(32)) {
            return false;
        } else {
            DimensionManager dimensionmanager = worldaccess.dimensionType();
            int i = dimensionmanager.monsterSpawnBlockLightLimit();

            if (i < 15 && worldaccess.getBrightness(EnumSkyBlock.BLOCK, blockposition) > i) {
                return false;
            } else {
                int j = worldaccess.getLevel().isThundering() ? worldaccess.getMaxLocalRawBrightness(blockposition, 10) : worldaccess.getMaxLocalRawBrightness(blockposition);

                return j <= dimensionmanager.monsterSpawnLightTest().sample(randomsource);
            }
        }
    }

    public static boolean checkMonsterSpawnRules(EntityTypes<? extends EntityMonster> entitytypes, WorldAccess worldaccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, RandomSource randomsource) {
        return worldaccess.getDifficulty() != EnumDifficulty.PEACEFUL && isDarkEnoughToSpawn(worldaccess, blockposition, randomsource) && checkMobSpawnRules(entitytypes, worldaccess, enummobspawn, blockposition, randomsource);
    }

    public static boolean checkAnyLightMonsterSpawnRules(EntityTypes<? extends EntityMonster> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, RandomSource randomsource) {
        return generatoraccess.getDifficulty() != EnumDifficulty.PEACEFUL && checkMobSpawnRules(entitytypes, generatoraccess, enummobspawn, blockposition, randomsource);
    }

    public static AttributeProvider.Builder createMonsterAttributes() {
        return EntityInsentient.createMobAttributes().add(GenericAttributes.ATTACK_DAMAGE);
    }

    @Override
    public boolean shouldDropExperience() {
        return true;
    }

    @Override
    protected boolean shouldDropLoot() {
        return true;
    }

    public boolean isPreventingPlayerRest(EntityHuman entityhuman) {
        return true;
    }

    @Override
    public ItemStack getProjectile(ItemStack itemstack) {
        if (itemstack.getItem() instanceof ItemProjectileWeapon) {
            Predicate<ItemStack> predicate = ((ItemProjectileWeapon) itemstack.getItem()).getSupportedHeldProjectiles();
            ItemStack itemstack1 = ItemProjectileWeapon.getHeldProjectile(this, predicate);

            return itemstack1.isEmpty() ? new ItemStack(Items.ARROW) : itemstack1;
        } else {
            return ItemStack.EMPTY;
        }
    }
}
