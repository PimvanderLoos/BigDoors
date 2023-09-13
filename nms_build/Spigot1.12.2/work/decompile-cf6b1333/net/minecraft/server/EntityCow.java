package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityCow extends EntityAnimal {

    public EntityCow(World world) {
        super(world);
        this.setSize(0.9F, 1.4F);
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityInsentient.a(dataconvertermanager, EntityCow.class);
    }

    protected void r() {
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalPanic(this, 2.0D));
        this.goalSelector.a(2, new PathfinderGoalBreed(this, 1.0D));
        this.goalSelector.a(3, new PathfinderGoalTempt(this, 1.25D, Items.WHEAT, false));
        this.goalSelector.a(4, new PathfinderGoalFollowParent(this, 1.25D));
        this.goalSelector.a(5, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(10.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.20000000298023224D);
    }

    protected SoundEffect F() {
        return SoundEffects.ar;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.at;
    }

    protected SoundEffect cf() {
        return SoundEffects.as;
    }

    protected void a(BlockPosition blockposition, Block block) {
        this.a(SoundEffects.av, 0.15F, 1.0F);
    }

    protected float cq() {
        return 0.4F;
    }

    @Nullable
    protected MinecraftKey J() {
        return LootTables.L;
    }

    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (itemstack.getItem() == Items.BUCKET && !entityhuman.abilities.canInstantlyBuild && !this.isBaby()) {
            entityhuman.a(SoundEffects.au, 1.0F, 1.0F);
            itemstack.subtract(1);
            if (itemstack.isEmpty()) {
                entityhuman.a(enumhand, new ItemStack(Items.MILK_BUCKET));
            } else if (!entityhuman.inventory.pickup(new ItemStack(Items.MILK_BUCKET))) {
                entityhuman.drop(new ItemStack(Items.MILK_BUCKET), false);
            }

            return true;
        } else {
            return super.a(entityhuman, enumhand);
        }
    }

    public EntityCow b(EntityAgeable entityageable) {
        return new EntityCow(this.world);
    }

    public float getHeadHeight() {
        return this.isBaby() ? this.length : 1.3F;
    }

    public EntityAgeable createChild(EntityAgeable entityageable) {
        return this.b(entityageable);
    }
}
