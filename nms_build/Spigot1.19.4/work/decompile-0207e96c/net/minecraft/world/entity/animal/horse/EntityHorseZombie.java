package net.minecraft.world.entity.animal.horse;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.RandomSource;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMonsterType;
import net.minecraft.world.entity.ai.attributes.AttributeModifiable;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;

public class EntityHorseZombie extends EntityHorseAbstract {

    public EntityHorseZombie(EntityTypes<? extends EntityHorseZombie> entitytypes, World world) {
        super(entitytypes, world);
    }

    public static AttributeProvider.Builder createAttributes() {
        return createBaseHorseAttributes().add(GenericAttributes.MAX_HEALTH, 15.0D).add(GenericAttributes.MOVEMENT_SPEED, 0.20000000298023224D);
    }

    @Override
    protected void randomizeAttributes(RandomSource randomsource) {
        AttributeModifiable attributemodifiable = this.getAttribute(GenericAttributes.JUMP_STRENGTH);

        Objects.requireNonNull(randomsource);
        attributemodifiable.setBaseValue(generateJumpStrength(randomsource::nextDouble));
    }

    @Override
    public EnumMonsterType getMobType() {
        return EnumMonsterType.UNDEAD;
    }

    @Override
    protected SoundEffect getAmbientSound() {
        return SoundEffects.ZOMBIE_HORSE_AMBIENT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.ZOMBIE_HORSE_DEATH;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.ZOMBIE_HORSE_HURT;
    }

    @Nullable
    @Override
    public EntityAgeable getBreedOffspring(WorldServer worldserver, EntityAgeable entityageable) {
        return (EntityAgeable) EntityTypes.ZOMBIE_HORSE.create(worldserver);
    }

    @Override
    public EnumInteractionResult mobInteract(EntityHuman entityhuman, EnumHand enumhand) {
        return !this.isTamed() ? EnumInteractionResult.PASS : super.mobInteract(entityhuman, enumhand);
    }

    @Override
    protected void addBehaviourGoals() {}
}
