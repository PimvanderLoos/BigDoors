package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityHorseZombie extends EntityHorseAbstract {

    public EntityHorseZombie(World world) {
        super(world);
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityHorseAbstract.c(dataconvertermanager, EntityHorseZombie.class);
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(15.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.20000000298023224D);
        this.getAttributeInstance(EntityHorseZombie.attributeJumpStrength).setValue(this.dN());
    }

    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.UNDEAD;
    }

    protected SoundEffect F() {
        super.F();
        return SoundEffects.jn;
    }

    protected SoundEffect cf() {
        super.cf();
        return SoundEffects.jo;
    }

    protected SoundEffect d(DamageSource damagesource) {
        super.d(damagesource);
        return SoundEffects.jp;
    }

    @Nullable
    protected MinecraftKey J() {
        return LootTables.J;
    }

    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);
        boolean flag = !itemstack.isEmpty();

        if (flag && itemstack.getItem() == Items.SPAWN_EGG) {
            return super.a(entityhuman, enumhand);
        } else if (!this.isTamed()) {
            return false;
        } else if (this.isBaby()) {
            return super.a(entityhuman, enumhand);
        } else if (entityhuman.isSneaking()) {
            this.c(entityhuman);
            return true;
        } else if (this.isVehicle()) {
            return super.a(entityhuman, enumhand);
        } else {
            if (flag) {
                if (!this.dG() && itemstack.getItem() == Items.SADDLE) {
                    this.c(entityhuman);
                    return true;
                }

                if (itemstack.a(entityhuman, (EntityLiving) this, enumhand)) {
                    return true;
                }
            }

            this.g(entityhuman);
            return true;
        }
    }
}
