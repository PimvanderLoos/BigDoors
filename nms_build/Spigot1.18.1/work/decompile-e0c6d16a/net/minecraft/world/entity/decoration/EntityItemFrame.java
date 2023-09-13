package net.minecraft.world.entity.decoration;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemWorldMap;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockDiodeAbstract;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.saveddata.maps.WorldMap;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityItemFrame extends EntityHanging {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final DataWatcherObject<ItemStack> DATA_ITEM = DataWatcher.defineId(EntityItemFrame.class, DataWatcherRegistry.ITEM_STACK);
    private static final DataWatcherObject<Integer> DATA_ROTATION = DataWatcher.defineId(EntityItemFrame.class, DataWatcherRegistry.INT);
    public static final int NUM_ROTATIONS = 8;
    public float dropChance;
    public boolean fixed;

    public EntityItemFrame(EntityTypes<? extends EntityItemFrame> entitytypes, World world) {
        super(entitytypes, world);
        this.dropChance = 1.0F;
    }

    public EntityItemFrame(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        this(EntityTypes.ITEM_FRAME, world, blockposition, enumdirection);
    }

    public EntityItemFrame(EntityTypes<? extends EntityItemFrame> entitytypes, World world, BlockPosition blockposition, EnumDirection enumdirection) {
        super(entitytypes, world, blockposition);
        this.dropChance = 1.0F;
        this.setDirection(enumdirection);
    }

    @Override
    protected float getEyeHeight(EntityPose entitypose, EntitySize entitysize) {
        return 0.0F;
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(EntityItemFrame.DATA_ITEM, ItemStack.EMPTY);
        this.getEntityData().define(EntityItemFrame.DATA_ROTATION, 0);
    }

    @Override
    public void setDirection(EnumDirection enumdirection) {
        Validate.notNull(enumdirection);
        this.direction = enumdirection;
        if (enumdirection.getAxis().isHorizontal()) {
            this.setXRot(0.0F);
            this.setYRot((float) (this.direction.get2DDataValue() * 90));
        } else {
            this.setXRot((float) (-90 * enumdirection.getAxisDirection().getStep()));
            this.setYRot(0.0F);
        }

        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();
        this.recalculateBoundingBox();
    }

    @Override
    protected void recalculateBoundingBox() {
        if (this.direction != null) {
            double d0 = 0.46875D;
            double d1 = (double) this.pos.getX() + 0.5D - (double) this.direction.getStepX() * 0.46875D;
            double d2 = (double) this.pos.getY() + 0.5D - (double) this.direction.getStepY() * 0.46875D;
            double d3 = (double) this.pos.getZ() + 0.5D - (double) this.direction.getStepZ() * 0.46875D;

            this.setPosRaw(d1, d2, d3);
            double d4 = (double) this.getWidth();
            double d5 = (double) this.getHeight();
            double d6 = (double) this.getWidth();
            EnumDirection.EnumAxis enumdirection_enumaxis = this.direction.getAxis();

            switch (enumdirection_enumaxis) {
                case X:
                    d4 = 1.0D;
                    break;
                case Y:
                    d5 = 1.0D;
                    break;
                case Z:
                    d6 = 1.0D;
            }

            d4 /= 32.0D;
            d5 /= 32.0D;
            d6 /= 32.0D;
            this.setBoundingBox(new AxisAlignedBB(d1 - d4, d2 - d5, d3 - d6, d1 + d4, d2 + d5, d3 + d6));
        }
    }

    @Override
    public boolean survives() {
        if (this.fixed) {
            return true;
        } else if (!this.level.noCollision((Entity) this)) {
            return false;
        } else {
            IBlockData iblockdata = this.level.getBlockState(this.pos.relative(this.direction.getOpposite()));

            return !iblockdata.getMaterial().isSolid() && (!this.direction.getAxis().isHorizontal() || !BlockDiodeAbstract.isDiode(iblockdata)) ? false : this.level.getEntities((Entity) this, this.getBoundingBox(), EntityItemFrame.HANGING_ENTITY).isEmpty();
        }
    }

    @Override
    public void move(EnumMoveType enummovetype, Vec3D vec3d) {
        if (!this.fixed) {
            super.move(enummovetype, vec3d);
        }

    }

    @Override
    public void push(double d0, double d1, double d2) {
        if (!this.fixed) {
            super.push(d0, d1, d2);
        }

    }

    @Override
    public float getPickRadius() {
        return 0.0F;
    }

    @Override
    public void kill() {
        this.removeFramedMap(this.getItem());
        super.kill();
    }

    @Override
    public boolean hurt(DamageSource damagesource, float f) {
        if (this.fixed) {
            return damagesource != DamageSource.OUT_OF_WORLD && !damagesource.isCreativePlayer() ? false : super.hurt(damagesource, f);
        } else if (this.isInvulnerableTo(damagesource)) {
            return false;
        } else if (!damagesource.isExplosion() && !this.getItem().isEmpty()) {
            if (!this.level.isClientSide) {
                this.dropItem(damagesource.getEntity(), false);
                this.playSound(this.getRemoveItemSound(), 1.0F, 1.0F);
            }

            return true;
        } else {
            return super.hurt(damagesource, f);
        }
    }

    public SoundEffect getRemoveItemSound() {
        return SoundEffects.ITEM_FRAME_REMOVE_ITEM;
    }

    @Override
    public int getWidth() {
        return 12;
    }

    @Override
    public int getHeight() {
        return 12;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double d0) {
        double d1 = 16.0D;

        d1 *= 64.0D * getViewScale();
        return d0 < d1 * d1;
    }

    @Override
    public void dropItem(@Nullable Entity entity) {
        this.playSound(this.getBreakSound(), 1.0F, 1.0F);
        this.dropItem(entity, true);
    }

    public SoundEffect getBreakSound() {
        return SoundEffects.ITEM_FRAME_BREAK;
    }

    @Override
    public void playPlacementSound() {
        this.playSound(this.getPlaceSound(), 1.0F, 1.0F);
    }

    public SoundEffect getPlaceSound() {
        return SoundEffects.ITEM_FRAME_PLACE;
    }

    private void dropItem(@Nullable Entity entity, boolean flag) {
        if (!this.fixed) {
            ItemStack itemstack = this.getItem();

            this.setItem(ItemStack.EMPTY);
            if (!this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                if (entity == null) {
                    this.removeFramedMap(itemstack);
                }

            } else {
                if (entity instanceof EntityHuman) {
                    EntityHuman entityhuman = (EntityHuman) entity;

                    if (entityhuman.getAbilities().instabuild) {
                        this.removeFramedMap(itemstack);
                        return;
                    }
                }

                if (flag) {
                    this.spawnAtLocation(this.getFrameItemStack());
                }

                if (!itemstack.isEmpty()) {
                    itemstack = itemstack.copy();
                    this.removeFramedMap(itemstack);
                    if (this.random.nextFloat() < this.dropChance) {
                        this.spawnAtLocation(itemstack);
                    }
                }

            }
        }
    }

    private void removeFramedMap(ItemStack itemstack) {
        if (itemstack.is(Items.FILLED_MAP)) {
            WorldMap worldmap = ItemWorldMap.getSavedData(itemstack, this.level);

            if (worldmap != null) {
                worldmap.removedFromFrame(this.pos, this.getId());
                worldmap.setDirty(true);
            }
        }

        itemstack.setEntityRepresentation((Entity) null);
    }

    public ItemStack getItem() {
        return (ItemStack) this.getEntityData().get(EntityItemFrame.DATA_ITEM);
    }

    public void setItem(ItemStack itemstack) {
        this.setItem(itemstack, true);
    }

    public void setItem(ItemStack itemstack, boolean flag) {
        if (!itemstack.isEmpty()) {
            itemstack = itemstack.copy();
            itemstack.setCount(1);
            itemstack.setEntityRepresentation(this);
        }

        this.getEntityData().set(EntityItemFrame.DATA_ITEM, itemstack);
        if (!itemstack.isEmpty()) {
            this.playSound(this.getAddItemSound(), 1.0F, 1.0F);
        }

        if (flag && this.pos != null) {
            this.level.updateNeighbourForOutputSignal(this.pos, Blocks.AIR);
        }

    }

    public SoundEffect getAddItemSound() {
        return SoundEffects.ITEM_FRAME_ADD_ITEM;
    }

    @Override
    public SlotAccess getSlot(int i) {
        return i == 0 ? new SlotAccess() {
            @Override
            public ItemStack get() {
                return EntityItemFrame.this.getItem();
            }

            @Override
            public boolean set(ItemStack itemstack) {
                EntityItemFrame.this.setItem(itemstack);
                return true;
            }
        } : super.getSlot(i);
    }

    @Override
    public void onSyncedDataUpdated(DataWatcherObject<?> datawatcherobject) {
        if (datawatcherobject.equals(EntityItemFrame.DATA_ITEM)) {
            ItemStack itemstack = this.getItem();

            if (!itemstack.isEmpty() && itemstack.getFrame() != this) {
                itemstack.setEntityRepresentation(this);
            }
        }

    }

    public int getRotation() {
        return (Integer) this.getEntityData().get(EntityItemFrame.DATA_ROTATION);
    }

    public void setRotation(int i) {
        this.setRotation(i, true);
    }

    private void setRotation(int i, boolean flag) {
        this.getEntityData().set(EntityItemFrame.DATA_ROTATION, i % 8);
        if (flag && this.pos != null) {
            this.level.updateNeighbourForOutputSignal(this.pos, Blocks.AIR);
        }

    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        if (!this.getItem().isEmpty()) {
            nbttagcompound.put("Item", this.getItem().save(new NBTTagCompound()));
            nbttagcompound.putByte("ItemRotation", (byte) this.getRotation());
            nbttagcompound.putFloat("ItemDropChance", this.dropChance);
        }

        nbttagcompound.putByte("Facing", (byte) this.direction.get3DDataValue());
        nbttagcompound.putBoolean("Invisible", this.isInvisible());
        nbttagcompound.putBoolean("Fixed", this.fixed);
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Item");

        if (nbttagcompound1 != null && !nbttagcompound1.isEmpty()) {
            ItemStack itemstack = ItemStack.of(nbttagcompound1);

            if (itemstack.isEmpty()) {
                EntityItemFrame.LOGGER.warn("Unable to load item from: {}", nbttagcompound1);
            }

            ItemStack itemstack1 = this.getItem();

            if (!itemstack1.isEmpty() && !ItemStack.matches(itemstack, itemstack1)) {
                this.removeFramedMap(itemstack1);
            }

            this.setItem(itemstack, false);
            this.setRotation(nbttagcompound.getByte("ItemRotation"), false);
            if (nbttagcompound.contains("ItemDropChance", 99)) {
                this.dropChance = nbttagcompound.getFloat("ItemDropChance");
            }
        }

        this.setDirection(EnumDirection.from3DDataValue(nbttagcompound.getByte("Facing")));
        this.setInvisible(nbttagcompound.getBoolean("Invisible"));
        this.fixed = nbttagcompound.getBoolean("Fixed");
    }

    @Override
    public EnumInteractionResult interact(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);
        boolean flag = !this.getItem().isEmpty();
        boolean flag1 = !itemstack.isEmpty();

        if (this.fixed) {
            return EnumInteractionResult.PASS;
        } else if (!this.level.isClientSide) {
            if (!flag) {
                if (flag1 && !this.isRemoved()) {
                    if (itemstack.is(Items.FILLED_MAP)) {
                        WorldMap worldmap = ItemWorldMap.getSavedData(itemstack, this.level);

                        if (worldmap != null && worldmap.isTrackedCountOverLimit(256)) {
                            return EnumInteractionResult.FAIL;
                        }
                    }

                    this.setItem(itemstack);
                    if (!entityhuman.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                }
            } else {
                this.playSound(this.getRotateItemSound(), 1.0F, 1.0F);
                this.setRotation(this.getRotation() + 1);
            }

            return EnumInteractionResult.CONSUME;
        } else {
            return !flag && !flag1 ? EnumInteractionResult.PASS : EnumInteractionResult.SUCCESS;
        }
    }

    public SoundEffect getRotateItemSound() {
        return SoundEffects.ITEM_FRAME_ROTATE_ITEM;
    }

    public int getAnalogOutput() {
        return this.getItem().isEmpty() ? 0 : this.getRotation() % 8 + 1;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new PacketPlayOutSpawnEntity(this, this.getType(), this.direction.get3DDataValue(), this.getPos());
    }

    @Override
    public void recreateFromPacket(PacketPlayOutSpawnEntity packetplayoutspawnentity) {
        super.recreateFromPacket(packetplayoutspawnentity);
        this.setDirection(EnumDirection.from3DDataValue(packetplayoutspawnentity.getData()));
    }

    @Override
    public ItemStack getPickResult() {
        ItemStack itemstack = this.getItem();

        return itemstack.isEmpty() ? this.getFrameItemStack() : itemstack.copy();
    }

    protected ItemStack getFrameItemStack() {
        return new ItemStack(Items.ITEM_FRAME);
    }
}
