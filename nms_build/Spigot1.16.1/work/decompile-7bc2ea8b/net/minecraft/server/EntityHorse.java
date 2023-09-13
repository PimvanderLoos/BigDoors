package net.minecraft.server;

import java.util.UUID;
import javax.annotation.Nullable;

public class EntityHorse extends EntityHorseAbstract {

    private static final UUID bD = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
    private static final DataWatcherObject<Integer> bE = DataWatcher.a(EntityHorse.class, DataWatcherRegistry.b);

    public EntityHorse(EntityTypes<? extends EntityHorse> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected void eL() {
        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue((double) this.fq());
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(this.fs());
        this.getAttributeInstance(GenericAttributes.JUMP_STRENGTH).setValue(this.fr());
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityHorse.bE, 0);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setInt("Variant", this.getVariantRaw());
        if (!this.inventoryChest.getItem(1).isEmpty()) {
            nbttagcompound.set("ArmorItem", this.inventoryChest.getItem(1).save(new NBTTagCompound()));
        }

    }

    public ItemStack eM() {
        return this.getEquipment(EnumItemSlot.CHEST);
    }

    private void m(ItemStack itemstack) {
        this.setSlot(EnumItemSlot.CHEST, itemstack);
        this.a(EnumItemSlot.CHEST, 0.0F);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.setVariantRaw(nbttagcompound.getInt("Variant"));
        if (nbttagcompound.hasKeyOfType("ArmorItem", 10)) {
            ItemStack itemstack = ItemStack.a(nbttagcompound.getCompound("ArmorItem"));

            if (!itemstack.isEmpty() && this.l(itemstack)) {
                this.inventoryChest.setItem(1, itemstack);
            }
        }

        this.ff();
    }

    private void setVariantRaw(int i) {
        this.datawatcher.set(EntityHorse.bE, i);
    }

    private int getVariantRaw() {
        return (Integer) this.datawatcher.get(EntityHorse.bE);
    }

    public void setVariant(HorseColor horsecolor, HorseStyle horsestyle) {
        this.setVariantRaw(horsecolor.a() & 255 | horsestyle.a() << 8 & '\uff00');
    }

    public HorseColor getColor() {
        return HorseColor.a(this.getVariantRaw() & 255);
    }

    public HorseStyle getStyle() {
        return HorseStyle.a((this.getVariantRaw() & '\uff00') >> 8);
    }

    @Override
    protected void ff() {
        if (!this.world.isClientSide) {
            super.ff();
            this.n(this.inventoryChest.getItem(1));
            this.a(EnumItemSlot.CHEST, 0.0F);
        }
    }

    private void n(ItemStack itemstack) {
        this.m(itemstack);
        if (!this.world.isClientSide) {
            this.getAttributeInstance(GenericAttributes.ARMOR).b(EntityHorse.bD);
            if (this.l(itemstack)) {
                int i = ((ItemHorseArmor) itemstack.getItem()).g();

                if (i != 0) {
                    this.getAttributeInstance(GenericAttributes.ARMOR).b(new AttributeModifier(EntityHorse.bD, "Horse armor bonus", (double) i, AttributeModifier.Operation.ADDITION));
                }
            }
        }

    }

    @Override
    public void a(IInventory iinventory) {
        ItemStack itemstack = this.eM();

        super.a(iinventory);
        ItemStack itemstack1 = this.eM();

        if (this.ticksLived > 20 && this.l(itemstack1) && itemstack != itemstack1) {
            this.playSound(SoundEffects.ENTITY_HORSE_ARMOR, 0.5F, 1.0F);
        }

    }

    @Override
    protected void a(SoundEffectType soundeffecttype) {
        super.a(soundeffecttype);
        if (this.random.nextInt(10) == 0) {
            this.playSound(SoundEffects.ENTITY_HORSE_BREATHE, soundeffecttype.a() * 0.6F, soundeffecttype.b());
        }

    }

    @Override
    protected SoundEffect getSoundAmbient() {
        super.getSoundAmbient();
        return SoundEffects.ENTITY_HORSE_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        super.getSoundDeath();
        return SoundEffects.ENTITY_HORSE_DEATH;
    }

    @Nullable
    @Override
    protected SoundEffect fh() {
        return SoundEffects.ENTITY_HORSE_EAT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        super.getSoundHurt(damagesource);
        return SoundEffects.ENTITY_HORSE_HURT;
    }

    @Override
    protected SoundEffect getSoundAngry() {
        super.getSoundAngry();
        return SoundEffects.ENTITY_HORSE_ANGRY;
    }

    @Override
    public EnumInteractionResult b(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (!this.isBaby()) {
            if (this.isTamed() && entityhuman.ep()) {
                this.f(entityhuman);
                return EnumInteractionResult.a(this.world.isClientSide);
            }

            if (this.isVehicle()) {
                return super.b(entityhuman, enumhand);
            }
        }

        if (!itemstack.isEmpty()) {
            if (this.k(itemstack)) {
                return this.b(entityhuman, itemstack);
            }

            EnumInteractionResult enuminteractionresult = itemstack.a(entityhuman, (EntityLiving) this, enumhand);

            if (enuminteractionresult.a()) {
                return enuminteractionresult;
            }

            if (!this.isTamed()) {
                this.fn();
                return EnumInteractionResult.a(this.world.isClientSide);
            }

            boolean flag = !this.isBaby() && !this.hasSaddle() && itemstack.getItem() == Items.SADDLE;

            if (this.l(itemstack) || flag) {
                this.f(entityhuman);
                return EnumInteractionResult.a(this.world.isClientSide);
            }
        }

        if (this.isBaby()) {
            return super.b(entityhuman, enumhand);
        } else {
            this.h(entityhuman);
            return EnumInteractionResult.a(this.world.isClientSide);
        }
    }

    @Override
    public boolean mate(EntityAnimal entityanimal) {
        return entityanimal == this ? false : (!(entityanimal instanceof EntityHorseDonkey) && !(entityanimal instanceof EntityHorse) ? false : this.fp() && ((EntityHorseAbstract) entityanimal).fp());
    }

    @Override
    public EntityAgeable createChild(EntityAgeable entityageable) {
        EntityHorseAbstract entityhorseabstract;

        if (entityageable instanceof EntityHorseDonkey) {
            entityhorseabstract = (EntityHorseAbstract) EntityTypes.MULE.a(this.world);
        } else {
            EntityHorse entityhorse = (EntityHorse) entityageable;

            entityhorseabstract = (EntityHorseAbstract) EntityTypes.HORSE.a(this.world);
            int i = this.random.nextInt(9);
            HorseColor horsecolor;

            if (i < 4) {
                horsecolor = this.getColor();
            } else if (i < 8) {
                horsecolor = entityhorse.getColor();
            } else {
                horsecolor = (HorseColor) SystemUtils.a((Object[]) HorseColor.values(), this.random);
            }

            int j = this.random.nextInt(5);
            HorseStyle horsestyle;

            if (j < 2) {
                horsestyle = this.getStyle();
            } else if (j < 4) {
                horsestyle = entityhorse.getStyle();
            } else {
                horsestyle = (HorseStyle) SystemUtils.a((Object[]) HorseStyle.values(), this.random);
            }

            ((EntityHorse) entityhorseabstract).setVariant(horsecolor, horsestyle);
        }

        this.a(entityageable, entityhorseabstract);
        return entityhorseabstract;
    }

    @Override
    public boolean ft() {
        return true;
    }

    @Override
    public boolean l(ItemStack itemstack) {
        return itemstack.getItem() instanceof ItemHorseArmor;
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        HorseColor horsecolor;

        if (groupdataentity instanceof EntityHorse.a) {
            horsecolor = ((EntityHorse.a) groupdataentity).a;
        } else {
            horsecolor = (HorseColor) SystemUtils.a((Object[]) HorseColor.values(), this.random);
            groupdataentity = new EntityHorse.a(horsecolor);
        }

        this.setVariant(horsecolor, (HorseStyle) SystemUtils.a((Object[]) HorseStyle.values(), this.random));
        return super.prepare(generatoraccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) groupdataentity, nbttagcompound);
    }

    public static class a extends EntityAgeable.a {

        public final HorseColor a;

        public a(HorseColor horsecolor) {
            this.a = horsecolor;
        }
    }
}
