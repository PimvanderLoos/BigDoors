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

    private static final DataWatcherObject<Boolean> DATA_ID_CHEST = DataWatcher.a(EntityHorseChestedAbstract.class, DataWatcherRegistry.BOOLEAN);
    public static final int INV_CHEST_COUNT = 15;

    protected EntityHorseChestedAbstract(EntityTypes<? extends EntityHorseChestedAbstract> entitytypes, World world) {
        super(entitytypes, world);
        this.canGallop = false;
    }

    @Override
    protected void p() {
        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue((double) this.fZ());
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityHorseChestedAbstract.DATA_ID_CHEST, false);
    }

    public static AttributeProvider.Builder t() {
        return fS().a(GenericAttributes.MOVEMENT_SPEED, 0.17499999701976776D).a(GenericAttributes.JUMP_STRENGTH, 0.5D);
    }

    public boolean isCarryingChest() {
        return (Boolean) this.entityData.get(EntityHorseChestedAbstract.DATA_ID_CHEST);
    }

    public void setCarryingChest(boolean flag) {
        this.entityData.set(EntityHorseChestedAbstract.DATA_ID_CHEST, flag);
    }

    @Override
    protected int getChestSlots() {
        return this.isCarryingChest() ? 17 : super.getChestSlots();
    }

    @Override
    public double bl() {
        return super.bl() - 0.25D;
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (this.isCarryingChest()) {
            if (!this.level.isClientSide) {
                this.a((IMaterial) Blocks.CHEST);
            }

            this.setCarryingChest(false);
        }

    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setBoolean("ChestedHorse", this.isCarryingChest());
        if (this.isCarryingChest()) {
            NBTTagList nbttaglist = new NBTTagList();

            for (int i = 2; i < this.inventory.getSize(); ++i) {
                ItemStack itemstack = this.inventory.getItem(i);

                if (!itemstack.isEmpty()) {
                    NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                    nbttagcompound1.setByte("Slot", (byte) i);
                    itemstack.save(nbttagcompound1);
                    nbttaglist.add(nbttagcompound1);
                }
            }

            nbttagcompound.set("Items", nbttaglist);
        }

    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.setCarryingChest(nbttagcompound.getBoolean("ChestedHorse"));
        this.loadChest();
        if (this.isCarryingChest()) {
            NBTTagList nbttaglist = nbttagcompound.getList("Items", 10);

            for (int i = 0; i < nbttaglist.size(); ++i) {
                NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(i);
                int j = nbttagcompound1.getByte("Slot") & 255;

                if (j >= 2 && j < this.inventory.getSize()) {
                    this.inventory.setItem(j, ItemStack.a(nbttagcompound1));
                }
            }
        }

        this.fO();
    }

    @Override
    public SlotAccess k(int i) {
        return i == 499 ? new SlotAccess() {
            @Override
            public ItemStack a() {
                return EntityHorseChestedAbstract.this.isCarryingChest() ? new ItemStack(Items.CHEST) : ItemStack.EMPTY;
            }

            @Override
            public boolean a(ItemStack itemstack) {
                if (itemstack.isEmpty()) {
                    if (EntityHorseChestedAbstract.this.isCarryingChest()) {
                        EntityHorseChestedAbstract.this.setCarryingChest(false);
                        EntityHorseChestedAbstract.this.loadChest();
                    }

                    return true;
                } else if (itemstack.a(Items.CHEST)) {
                    if (!EntityHorseChestedAbstract.this.isCarryingChest()) {
                        EntityHorseChestedAbstract.this.setCarryingChest(true);
                        EntityHorseChestedAbstract.this.loadChest();
                    }

                    return true;
                } else {
                    return false;
                }
            }
        } : super.k(i);
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

            if (!this.isTamed()) {
                this.fW();
                return EnumInteractionResult.a(this.level.isClientSide);
            }

            if (!this.isCarryingChest() && itemstack.a(Blocks.CHEST.getItem())) {
                this.setCarryingChest(true);
                this.fy();
                if (!entityhuman.getAbilities().instabuild) {
                    itemstack.subtract(1);
                }

                this.loadChest();
                return EnumInteractionResult.a(this.level.isClientSide);
            }

            if (!this.isBaby() && !this.hasSaddle() && itemstack.a(Items.SADDLE)) {
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

    protected void fy() {
        this.playSound(SoundEffects.DONKEY_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
    }

    public int fE() {
        return 5;
    }
}
