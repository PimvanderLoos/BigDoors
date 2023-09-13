package net.minecraft.server;

import javax.annotation.Nullable;

public class EntitySkeletonWither extends EntitySkeletonAbstract {

    public EntitySkeletonWither(World world) {
        super(world);
        this.setSize(0.7F, 2.4F);
        this.fireProof = true;
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityInsentient.a(dataconvertermanager, EntitySkeletonWither.class);
    }

    @Nullable
    protected MinecraftKey J() {
        return LootTables.ap;
    }

    protected SoundEffect F() {
        return SoundEffects.iH;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.iJ;
    }

    protected SoundEffect cf() {
        return SoundEffects.iI;
    }

    SoundEffect p() {
        return SoundEffects.iK;
    }

    public void die(DamageSource damagesource) {
        super.die(damagesource);
        if (damagesource.getEntity() instanceof EntityCreeper) {
            EntityCreeper entitycreeper = (EntityCreeper) damagesource.getEntity();

            if (entitycreeper.isPowered() && entitycreeper.canCauseHeadDrop()) {
                entitycreeper.setCausedHeadDrop();
                this.a(new ItemStack(Items.SKULL, 1, 1), 0.0F);
            }
        }

    }

    protected void a(DifficultyDamageScaler difficultydamagescaler) {
        this.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
    }

    protected void b(DifficultyDamageScaler difficultydamagescaler) {}

    @Nullable
    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, @Nullable GroupDataEntity groupdataentity) {
        GroupDataEntity groupdataentity1 = super.prepare(difficultydamagescaler, groupdataentity);

        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(4.0D);
        this.dm();
        return groupdataentity1;
    }

    public float getHeadHeight() {
        return 2.1F;
    }

    public boolean B(Entity entity) {
        if (!super.B(entity)) {
            return false;
        } else {
            if (entity instanceof EntityLiving) {
                ((EntityLiving) entity).addEffect(new MobEffect(MobEffects.WITHER, 200));
            }

            return true;
        }
    }

    protected EntityArrow a(float f) {
        EntityArrow entityarrow = super.a(f);

        entityarrow.setOnFire(100);
        return entityarrow;
    }
}
