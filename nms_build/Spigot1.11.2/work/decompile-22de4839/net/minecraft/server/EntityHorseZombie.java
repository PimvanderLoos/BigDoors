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
        this.getAttributeInstance(EntityHorseZombie.attributeJumpStrength).setValue(this.dI());
    }

    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.UNDEAD;
    }

    protected SoundEffect G() {
        super.G();
        return SoundEffects.ij;
    }

    protected SoundEffect bX() {
        super.bX();
        return SoundEffects.ik;
    }

    protected SoundEffect bW() {
        super.bW();
        return SoundEffects.il;
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
            this.f(entityhuman);
            return true;
        } else if (this.isVehicle()) {
            return super.a(entityhuman, enumhand);
        } else {
            if (flag) {
                if (!this.dB() && itemstack.getItem() == Items.SADDLE) {
                    this.f(entityhuman);
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
