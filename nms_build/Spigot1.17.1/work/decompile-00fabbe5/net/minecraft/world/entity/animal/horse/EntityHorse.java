package net.minecraft.world.entity.animal.horse;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.IInventory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.animal.EntityAnimal;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemHorseArmor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.SoundEffectType;

public class EntityHorse extends EntityHorseAbstract {

    private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
    private static final DataWatcherObject<Integer> DATA_ID_TYPE_VARIANT = DataWatcher.a(EntityHorse.class, DataWatcherRegistry.INT);

    public EntityHorse(EntityTypes<? extends EntityHorse> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected void p() {
        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue((double) this.fZ());
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(this.gb());
        this.getAttributeInstance(GenericAttributes.JUMP_STRENGTH).setValue(this.ga());
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityHorse.DATA_ID_TYPE_VARIANT, 0);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setInt("Variant", this.getVariantRaw());
        if (!this.inventory.getItem(1).isEmpty()) {
            nbttagcompound.set("ArmorItem", this.inventory.getItem(1).save(new NBTTagCompound()));
        }

    }

    public ItemStack t() {
        return this.getEquipment(EnumItemSlot.CHEST);
    }

    private void o(ItemStack itemstack) {
        this.setSlot(EnumItemSlot.CHEST, itemstack);
        this.a(EnumItemSlot.CHEST, 0.0F);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.setVariantRaw(nbttagcompound.getInt("Variant"));
        if (nbttagcompound.hasKeyOfType("ArmorItem", 10)) {
            ItemStack itemstack = ItemStack.a(nbttagcompound.getCompound("ArmorItem"));

            if (!itemstack.isEmpty() && this.m(itemstack)) {
                this.inventory.setItem(1, itemstack);
            }
        }

        this.fO();
    }

    private void setVariantRaw(int i) {
        this.entityData.set(EntityHorse.DATA_ID_TYPE_VARIANT, i);
    }

    private int getVariantRaw() {
        return (Integer) this.entityData.get(EntityHorse.DATA_ID_TYPE_VARIANT);
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
    protected void fO() {
        if (!this.level.isClientSide) {
            super.fO();
            this.p(this.inventory.getItem(1));
            this.a(EnumItemSlot.CHEST, 0.0F);
        }
    }

    private void p(ItemStack itemstack) {
        this.o(itemstack);
        if (!this.level.isClientSide) {
            this.getAttributeInstance(GenericAttributes.ARMOR).b(EntityHorse.ARMOR_MODIFIER_UUID);
            if (this.m(itemstack)) {
                int i = ((ItemHorseArmor) itemstack.getItem()).j();

                if (i != 0) {
                    this.getAttributeInstance(GenericAttributes.ARMOR).b(new AttributeModifier(EntityHorse.ARMOR_MODIFIER_UUID, "Horse armor bonus", (double) i, AttributeModifier.Operation.ADDITION));
                }
            }
        }

    }

    @Override
    public void a(IInventory iinventory) {
        ItemStack itemstack = this.t();

        super.a(iinventory);
        ItemStack itemstack1 = this.t();

        if (this.tickCount > 20 && this.m(itemstack1) && itemstack != itemstack1) {
            this.playSound(SoundEffects.HORSE_ARMOR, 0.5F, 1.0F);
        }

    }

    @Override
    protected void a(SoundEffectType soundeffecttype) {
        super.a(soundeffecttype);
        if (this.random.nextInt(10) == 0) {
            this.playSound(SoundEffects.HORSE_BREATHE, soundeffecttype.getVolume() * 0.6F, soundeffecttype.getPitch());
        }

    }

    @Override
    protected SoundEffect getSoundAmbient() {
        super.getSoundAmbient();
        return SoundEffects.HORSE_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        super.getSoundDeath();
        return SoundEffects.HORSE_DEATH;
    }

    @Nullable
    @Override
    protected SoundEffect fQ() {
        return SoundEffects.HORSE_EAT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        super.getSoundHurt(damagesource);
        return SoundEffects.HORSE_HURT;
    }

    @Override
    protected SoundEffect getSoundAngry() {
        super.getSoundAngry();
        return SoundEffects.HORSE_ANGRY;
    }

    @Override
    public EnumInteractionResult b(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (!this.isBaby()) {
            if (this.isTamed() && entityhuman.eZ()) {
                this.f(entityhuman);
                return EnumInteractionResult.a(this.level.isClientSide);
            }

            if (this.isVehicle()) {
                return super.b(entityhuman, enumhand);
            }
        }

        if (!itemstack.isEmpty()) {
            if (this.isBreedItem(itemstack)) {
                return this.a(entityhuman, itemstack);
            }

            EnumInteractionResult enuminteractionresult = itemstack.a(entityhuman, (EntityLiving) this, enumhand);

            if (enuminteractionresult.a()) {
                return enuminteractionresult;
            }

            if (!this.isTamed()) {
                this.fW();
                return EnumInteractionResult.a(this.level.isClientSide);
            }

            boolean flag = !this.isBaby() && !this.hasSaddle() && itemstack.a(Items.SADDLE);

            if (this.m(itemstack) || flag) {
                this.f(entityhuman);
                return EnumInteractionResult.a(this.level.isClientSide);
            }
        }

        if (this.isBaby()) {
            return super.b(entityhuman, enumhand);
        } else {
            this.h(entityhuman);
            return EnumInteractionResult.a(this.level.isClientSide);
        }
    }

    @Override
    public boolean mate(EntityAnimal entityanimal) {
        return entityanimal == this ? false : (!(entityanimal instanceof EntityHorseDonkey) && !(entityanimal instanceof EntityHorse) ? false : this.fY() && ((EntityHorseAbstract) entityanimal).fY());
    }

    @Override
    public EntityAgeable createChild(WorldServer worldserver, EntityAgeable entityageable) {
        EntityHorseAbstract entityhorseabstract;

        if (entityageable instanceof EntityHorseDonkey) {
            entityhorseabstract = (EntityHorseAbstract) EntityTypes.MULE.a((World) worldserver);
        } else {
            EntityHorse entityhorse = (EntityHorse) entityageable;

            entityhorseabstract = (EntityHorseAbstract) EntityTypes.HORSE.a((World) worldserver);
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
    public boolean gc() {
        return true;
    }

    @Override
    public boolean m(ItemStack itemstack) {
        return itemstack.getItem() instanceof ItemHorseArmor;
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        HorseColor horsecolor;

        if (groupdataentity instanceof EntityHorse.a) {
            horsecolor = ((EntityHorse.a) groupdataentity).variant;
        } else {
            horsecolor = (HorseColor) SystemUtils.a((Object[]) HorseColor.values(), this.random);
            groupdataentity = new EntityHorse.a(horsecolor);
        }

        this.setVariant(horsecolor, (HorseStyle) SystemUtils.a((Object[]) HorseStyle.values(), this.random));
        return super.prepare(worldaccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) groupdataentity, nbttagcompound);
    }

    public static class a extends EntityAgeable.a {

        public final HorseColor variant;

        public a(HorseColor horsecolor) {
            super(true);
            this.variant = horsecolor;
        }
    }
}
