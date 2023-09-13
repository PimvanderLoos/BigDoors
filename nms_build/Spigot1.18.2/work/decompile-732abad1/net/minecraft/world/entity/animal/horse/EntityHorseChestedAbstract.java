package net.minecraft.world.entity.animal.horse;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;

public abstract class EntityHorseChestedAbstract extends EntityHorseAbstract {

    private static final DataWatcherObject<Boolean> DATA_ID_CHEST = DataWatcher.defineId(EntityHorseChestedAbstract.class, DataWatcherRegistry.BOOLEAN);
    public static final int INV_CHEST_COUNT = 15;

    protected EntityHorseChestedAbstract(EntityTypes<? extends EntityHorseChestedAbstract> entitytypes, World world) {
        super(entitytypes, world);
        this.canGallop = false;
    }

    @Override
    protected void randomizeAttributes() {
        this.getAttribute(GenericAttributes.MAX_HEALTH).setBaseValue((double) this.generateRandomMaxHealth());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityHorseChestedAbstract.DATA_ID_CHEST, false);
    }

    public static AttributeProvider.Builder createBaseChestedHorseAttributes() {
        return createBaseHorseAttributes().add(GenericAttributes.MOVEMENT_SPEED, 0.17499999701976776D).add(GenericAttributes.JUMP_STRENGTH, 0.5D);
    }

    public boolean hasChest() {
        return (Boolean) this.entityData.get(EntityHorseChestedAbstract.DATA_ID_CHEST);
    }

    public void setChest(boolean flag) {
        this.entityData.set(EntityHorseChestedAbstract.DATA_ID_CHEST, flag);
    }

    @Override
    protected int getInventorySize() {
        return this.hasChest() ? 17 : super.getInventorySize();
    }

    @Override
    public double getPassengersRidingOffset() {
        return super.getPassengersRidingOffset() - 0.25D;
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        if (this.hasChest()) {
            if (!this.level.isClientSide) {
                this.spawnAtLocation((IMaterial) Blocks.CHEST);
            }

            this.setChest(false);
        }

    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putBoolean("ChestedHorse", this.hasChest());
        if (this.hasChest()) {
            NBTTagList nbttaglist = new NBTTagList();

            for (int i = 2; i < this.inventory.getContainerSize(); ++i) {
                ItemStack itemstack = this.inventory.getItem(i);

                if (!itemstack.isEmpty()) {
                    NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                    nbttagcompound1.putByte("Slot", (byte) i);
                    itemstack.save(nbttagcompound1);
                    nbttaglist.add(nbttagcompound1);
                }
            }

            nbttagcompound.put("Items", nbttaglist);
        }

    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        this.setChest(nbttagcompound.getBoolean("ChestedHorse"));
        this.createInventory();
        if (this.hasChest()) {
            NBTTagList nbttaglist = nbttagcompound.getList("Items", 10);

            for (int i = 0; i < nbttaglist.size(); ++i) {
                NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(i);
                int j = nbttagcompound1.getByte("Slot") & 255;

                if (j >= 2 && j < this.inventory.getContainerSize()) {
                    this.inventory.setItem(j, ItemStack.of(nbttagcompound1));
                }
            }
        }

        this.updateContainerEquipment();
    }

    @Override
    public SlotAccess getSlot(int i) {
        return i == 499 ? new SlotAccess() {
            @Override
            public ItemStack get() {
                return EntityHorseChestedAbstract.this.hasChest() ? new ItemStack(Items.CHEST) : ItemStack.EMPTY;
            }

            @Override
            public boolean set(ItemStack itemstack) {
                if (itemstack.isEmpty()) {
                    if (EntityHorseChestedAbstract.this.hasChest()) {
                        EntityHorseChestedAbstract.this.setChest(false);
                        EntityHorseChestedAbstract.this.createInventory();
                    }

                    return true;
                } else if (itemstack.is(Items.CHEST)) {
                    if (!EntityHorseChestedAbstract.this.hasChest()) {
                        EntityHorseChestedAbstract.this.setChest(true);
                        EntityHorseChestedAbstract.this.createInventory();
                    }

                    return true;
                } else {
                    return false;
                }
            }
        } : super.getSlot(i);
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

            if (!this.isTamed()) {
                this.makeMad();
                return EnumInteractionResult.sidedSuccess(this.level.isClientSide);
            }

            if (!this.hasChest() && itemstack.is(Blocks.CHEST.asItem())) {
                this.setChest(true);
                this.playChestEquipsSound();
                if (!entityhuman.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

                this.createInventory();
                return EnumInteractionResult.sidedSuccess(this.level.isClientSide);
            }

            if (!this.isBaby() && !this.isSaddled() && itemstack.is(Items.SADDLE)) {
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

    protected void playChestEquipsSound() {
        this.playSound(SoundEffects.DONKEY_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
    }

    public int getInventoryColumns() {
        return 5;
    }
}
