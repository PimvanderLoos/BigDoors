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
    private static final DataWatcherObject<Integer> DATA_ID_TYPE_VARIANT = DataWatcher.defineId(EntityHorse.class, DataWatcherRegistry.INT);

    public EntityHorse(EntityTypes<? extends EntityHorse> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected void randomizeAttributes() {
        this.getAttribute(GenericAttributes.MAX_HEALTH).setBaseValue((double) this.generateRandomMaxHealth());
        this.getAttribute(GenericAttributes.MOVEMENT_SPEED).setBaseValue(this.generateRandomSpeed());
        this.getAttribute(GenericAttributes.JUMP_STRENGTH).setBaseValue(this.generateRandomJumpStrength());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityHorse.DATA_ID_TYPE_VARIANT, 0);
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putInt("Variant", this.getTypeVariant());
        if (!this.inventory.getItem(1).isEmpty()) {
            nbttagcompound.put("ArmorItem", this.inventory.getItem(1).save(new NBTTagCompound()));
        }

    }

    public ItemStack getArmor() {
        return this.getItemBySlot(EnumItemSlot.CHEST);
    }

    private void setArmor(ItemStack itemstack) {
        this.setItemSlot(EnumItemSlot.CHEST, itemstack);
        this.setDropChance(EnumItemSlot.CHEST, 0.0F);
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        this.setTypeVariant(nbttagcompound.getInt("Variant"));
        if (nbttagcompound.contains("ArmorItem", 10)) {
            ItemStack itemstack = ItemStack.of(nbttagcompound.getCompound("ArmorItem"));

            if (!itemstack.isEmpty() && this.isArmor(itemstack)) {
                this.inventory.setItem(1, itemstack);
            }
        }

        this.updateContainerEquipment();
    }

    private void setTypeVariant(int i) {
        this.entityData.set(EntityHorse.DATA_ID_TYPE_VARIANT, i);
    }

    private int getTypeVariant() {
        return (Integer) this.entityData.get(EntityHorse.DATA_ID_TYPE_VARIANT);
    }

    public void setVariantAndMarkings(HorseColor horsecolor, HorseStyle horsestyle) {
        this.setTypeVariant(horsecolor.getId() & 255 | horsestyle.getId() << 8 & '\uff00');
    }

    public HorseColor getVariant() {
        return HorseColor.byId(this.getTypeVariant() & 255);
    }

    public HorseStyle getMarkings() {
        return HorseStyle.byId((this.getTypeVariant() & '\uff00') >> 8);
    }

    @Override
    protected void updateContainerEquipment() {
        if (!this.level.isClientSide) {
            super.updateContainerEquipment();
            this.setArmorEquipment(this.inventory.getItem(1));
            this.setDropChance(EnumItemSlot.CHEST, 0.0F);
        }
    }

    private void setArmorEquipment(ItemStack itemstack) {
        this.setArmor(itemstack);
        if (!this.level.isClientSide) {
            this.getAttribute(GenericAttributes.ARMOR).removeModifier(EntityHorse.ARMOR_MODIFIER_UUID);
            if (this.isArmor(itemstack)) {
                int i = ((ItemHorseArmor) itemstack.getItem()).getProtection();

                if (i != 0) {
                    this.getAttribute(GenericAttributes.ARMOR).addTransientModifier(new AttributeModifier(EntityHorse.ARMOR_MODIFIER_UUID, "Horse armor bonus", (double) i, AttributeModifier.Operation.ADDITION));
                }
            }
        }

    }

    @Override
    public void containerChanged(IInventory iinventory) {
        ItemStack itemstack = this.getArmor();

        super.containerChanged(iinventory);
        ItemStack itemstack1 = this.getArmor();

        if (this.tickCount > 20 && this.isArmor(itemstack1) && itemstack != itemstack1) {
            this.playSound(SoundEffects.HORSE_ARMOR, 0.5F, 1.0F);
        }

    }

    @Override
    protected void playGallopSound(SoundEffectType soundeffecttype) {
        super.playGallopSound(soundeffecttype);
        if (this.random.nextInt(10) == 0) {
            this.playSound(SoundEffects.HORSE_BREATHE, soundeffecttype.getVolume() * 0.6F, soundeffecttype.getPitch());
        }

    }

    @Override
    protected SoundEffect getAmbientSound() {
        super.getAmbientSound();
        return SoundEffects.HORSE_AMBIENT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        super.getDeathSound();
        return SoundEffects.HORSE_DEATH;
    }

    @Nullable
    @Override
    protected SoundEffect getEatingSound() {
        return SoundEffects.HORSE_EAT;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        super.getHurtSound(damagesource);
        return SoundEffects.HORSE_HURT;
    }

    @Override
    protected SoundEffect getAngrySound() {
        super.getAngrySound();
        return SoundEffects.HORSE_ANGRY;
    }

    @Override
    public EnumInteractionResult mobInteract(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);

        if (!this.isBaby()) {
            if (this.isTamed() && entityhuman.isSecondaryUseActive()) {
                this.openInventory(entityhuman);
                return EnumInteractionResult.sidedSuccess(this.level.isClientSide);
            }

            if (this.isVehicle()) {
                return super.mobInteract(entityhuman, enumhand);
            }
        }

        if (!itemstack.isEmpty()) {
            if (this.isFood(itemstack)) {
                return this.fedFood(entityhuman, itemstack);
            }

            EnumInteractionResult enuminteractionresult = itemstack.interactLivingEntity(entityhuman, this, enumhand);

            if (enuminteractionresult.consumesAction()) {
                return enuminteractionresult;
            }

            if (!this.isTamed()) {
                this.makeMad();
                return EnumInteractionResult.sidedSuccess(this.level.isClientSide);
            }

            boolean flag = !this.isBaby() && !this.isSaddled() && itemstack.is(Items.SADDLE);

            if (this.isArmor(itemstack) || flag) {
                this.openInventory(entityhuman);
                return EnumInteractionResult.sidedSuccess(this.level.isClientSide);
            }
        }

        if (this.isBaby()) {
            return super.mobInteract(entityhuman, enumhand);
        } else {
            this.doPlayerRide(entityhuman);
            return EnumInteractionResult.sidedSuccess(this.level.isClientSide);
        }
    }

    @Override
    public boolean canMate(EntityAnimal entityanimal) {
        return entityanimal == this ? false : (!(entityanimal instanceof EntityHorseDonkey) && !(entityanimal instanceof EntityHorse) ? false : this.canParent() && ((EntityHorseAbstract) entityanimal).canParent());
    }

    @Override
    public EntityAgeable getBreedOffspring(WorldServer worldserver, EntityAgeable entityageable) {
        EntityHorseAbstract entityhorseabstract;

        if (entityageable instanceof EntityHorseDonkey) {
            entityhorseabstract = (EntityHorseAbstract) EntityTypes.MULE.create(worldserver);
        } else {
            EntityHorse entityhorse = (EntityHorse) entityageable;

            entityhorseabstract = (EntityHorseAbstract) EntityTypes.HORSE.create(worldserver);
            int i = this.random.nextInt(9);
            HorseColor horsecolor;

            if (i < 4) {
                horsecolor = this.getVariant();
            } else if (i < 8) {
                horsecolor = entityhorse.getVariant();
            } else {
                horsecolor = (HorseColor) SystemUtils.getRandom((Object[]) HorseColor.values(), this.random);
            }

            int j = this.random.nextInt(5);
            HorseStyle horsestyle;

            if (j < 2) {
                horsestyle = this.getMarkings();
            } else if (j < 4) {
                horsestyle = entityhorse.getMarkings();
            } else {
                horsestyle = (HorseStyle) SystemUtils.getRandom((Object[]) HorseStyle.values(), this.random);
            }

            ((EntityHorse) entityhorseabstract).setVariantAndMarkings(horsecolor, horsestyle);
        }

        this.setOffspringAttributes(entityageable, entityhorseabstract);
        return entityhorseabstract;
    }

    @Override
    public boolean canWearArmor() {
        return true;
    }

    @Override
    public boolean isArmor(ItemStack itemstack) {
        return itemstack.getItem() instanceof ItemHorseArmor;
    }

    @Nullable
    @Override
    public GroupDataEntity finalizeSpawn(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        HorseColor horsecolor;

        if (groupdataentity instanceof EntityHorse.a) {
            horsecolor = ((EntityHorse.a) groupdataentity).variant;
        } else {
            horsecolor = (HorseColor) SystemUtils.getRandom((Object[]) HorseColor.values(), this.random);
            groupdataentity = new EntityHorse.a(horsecolor);
        }

        this.setVariantAndMarkings(horsecolor, (HorseStyle) SystemUtils.getRandom((Object[]) HorseStyle.values(), this.random));
        return super.finalizeSpawn(worldaccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) groupdataentity, nbttagcompound);
    }

    public static class a extends EntityAgeable.a {

        public final HorseColor variant;

        public a(HorseColor horsecolor) {
            super(true);
            this.variant = horsecolor;
        }
    }
}
