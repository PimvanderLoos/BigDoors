package net.minecraft.server;

import javax.annotation.Nullable;

public class EntitySkeleton extends EntitySkeletonAbstract {

    public EntitySkeleton(World world) {
        super(world);
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityInsentient.a(dataconvertermanager, EntitySkeleton.class);
    }

    @Nullable
    protected MinecraftKey J() {
        return LootTables.ao;
    }

    protected SoundEffect F() {
        return SoundEffects.gQ;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.gV;
    }

    protected SoundEffect cf() {
        return SoundEffects.gR;
    }

    SoundEffect p() {
        return SoundEffects.gX;
    }

    public void die(DamageSource damagesource) {
        super.die(damagesource);
        if (damagesource.getEntity() instanceof EntityCreeper) {
            EntityCreeper entitycreeper = (EntityCreeper) damagesource.getEntity();

            if (entitycreeper.isPowered() && entitycreeper.canCauseHeadDrop()) {
                entitycreeper.setCausedHeadDrop();
                this.a(new ItemStack(Items.SKULL, 1, 0), 0.0F);
            }
        }

    }

    protected EntityArrow a(float f) {
        ItemStack itemstack = this.getEquipment(EnumItemSlot.OFFHAND);

        if (itemstack.getItem() == Items.SPECTRAL_ARROW) {
            EntitySpectralArrow entityspectralarrow = new EntitySpectralArrow(this.world, this);

            entityspectralarrow.a((EntityLiving) this, f);
            return entityspectralarrow;
        } else {
            EntityArrow entityarrow = super.a(f);

            if (itemstack.getItem() == Items.TIPPED_ARROW && entityarrow instanceof EntityTippedArrow) {
                ((EntityTippedArrow) entityarrow).a(itemstack);
            }

            return entityarrow;
        }
    }
}
