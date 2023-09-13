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
    private static final DataWatcherObject<ItemStack> DATA_ITEM = DataWatcher.a(EntityItemFrame.class, DataWatcherRegistry.ITEM_STACK);
    private static final DataWatcherObject<Integer> DATA_ROTATION = DataWatcher.a(EntityItemFrame.class, DataWatcherRegistry.INT);
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
    protected float getHeadHeight(EntityPose entitypose, EntitySize entitysize) {
        return 0.0F;
    }

    @Override
    protected void initDatawatcher() {
        this.getDataWatcher().register(EntityItemFrame.DATA_ITEM, ItemStack.EMPTY);
        this.getDataWatcher().register(EntityItemFrame.DATA_ROTATION, 0);
    }

    @Override
    public void setDirection(EnumDirection enumdirection) {
        Validate.notNull(enumdirection);
        this.direction = enumdirection;
        if (enumdirection.n().d()) {
            this.setXRot(0.0F);
            this.setYRot((float) (this.direction.get2DRotationValue() * 90));
        } else {
            this.setXRot((float) (-90 * enumdirection.e().a()));
            this.setYRot(0.0F);
        }

        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();
        this.updateBoundingBox();
    }

    @Override
    protected void updateBoundingBox() {
        if (this.direction != null) {
            double d0 = 0.46875D;
            double d1 = (double) this.pos.getX() + 0.5D - (double) this.direction.getAdjacentX() * 0.46875D;
            double d2 = (double) this.pos.getY() + 0.5D - (double) this.direction.getAdjacentY() * 0.46875D;
            double d3 = (double) this.pos.getZ() + 0.5D - (double) this.direction.getAdjacentZ() * 0.46875D;

            this.setPositionRaw(d1, d2, d3);
            double d4 = (double) this.getHangingWidth();
            double d5 = (double) this.getHangingHeight();
            double d6 = (double) this.getHangingWidth();
            EnumDirection.EnumAxis enumdirection_enumaxis = this.direction.n();

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
            this.a(new AxisAlignedBB(d1 - d4, d2 - d5, d3 - d6, d1 + d4, d2 + d5, d3 + d6));
        }
    }

    @Override
    public boolean survives() {
        if (this.fixed) {
            return true;
        } else if (!this.level.getCubes(this)) {
            return false;
        } else {
            IBlockData iblockdata = this.level.getType(this.pos.shift(this.direction.opposite()));

            return !iblockdata.getMaterial().isBuildable() && (!this.direction.n().d() || !BlockDiodeAbstract.isDiode(iblockdata)) ? false : this.level.getEntities(this, this.getBoundingBox(), EntityItemFrame.HANGING_ENTITY).isEmpty();
        }
    }

    @Override
    public void move(EnumMoveType enummovetype, Vec3D vec3d) {
        if (!this.fixed) {
            super.move(enummovetype, vec3d);
        }

    }

    @Override
    public void i(double d0, double d1, double d2) {
        if (!this.fixed) {
            super.i(d0, d1, d2);
        }

    }

    @Override
    public float bp() {
        return 0.0F;
    }

    @Override
    public void killEntity() {
        this.c(this.getItem());
        super.killEntity();
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.fixed) {
            return damagesource != DamageSource.OUT_OF_WORLD && !damagesource.B() ? false : super.damageEntity(damagesource, f);
        } else if (this.isInvulnerable(damagesource)) {
            return false;
        } else if (!damagesource.isExplosion() && !this.getItem().isEmpty()) {
            if (!this.level.isClientSide) {
                this.b(damagesource.getEntity(), false);
                this.playSound(this.h(), 1.0F, 1.0F);
            }

            return true;
        } else {
            return super.damageEntity(damagesource, f);
        }
    }

    public SoundEffect h() {
        return SoundEffects.ITEM_FRAME_REMOVE_ITEM;
    }

    @Override
    public int getHangingWidth() {
        return 12;
    }

    @Override
    public int getHangingHeight() {
        return 12;
    }

    @Override
    public boolean a(double d0) {
        double d1 = 16.0D;

        d1 *= 64.0D * cl();
        return d0 < d1 * d1;
    }

    @Override
    public void a(@Nullable Entity entity) {
        this.playSound(this.i(), 1.0F, 1.0F);
        this.b(entity, true);
    }

    public SoundEffect i() {
        return SoundEffects.ITEM_FRAME_BREAK;
    }

    @Override
    public void playPlaceSound() {
        this.playSound(this.j(), 1.0F, 1.0F);
    }

    public SoundEffect j() {
        return SoundEffects.ITEM_FRAME_PLACE;
    }

    private void b(@Nullable Entity entity, boolean flag) {
        if (!this.fixed) {
            ItemStack itemstack = this.getItem();

            this.setItem(ItemStack.EMPTY);
            if (!this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                if (entity == null) {
                    this.c(itemstack);
                }

            } else {
                if (entity instanceof EntityHuman) {
                    EntityHuman entityhuman = (EntityHuman) entity;

                    if (entityhuman.getAbilities().instabuild) {
                        this.c(itemstack);
                        return;
                    }
                }

                if (flag) {
                    this.b(this.o());
                }

                if (!itemstack.isEmpty()) {
                    itemstack = itemstack.cloneItemStack();
                    this.c(itemstack);
                    if (this.random.nextFloat() < this.dropChance) {
                        this.b(itemstack);
                    }
                }

            }
        }
    }

    private void c(ItemStack itemstack) {
        if (itemstack.a(Items.FILLED_MAP)) {
            WorldMap worldmap = ItemWorldMap.getSavedMap(itemstack, this.level);

            if (worldmap != null) {
                worldmap.a(this.pos, this.getId());
                worldmap.a(true);
            }
        }

        itemstack.a((Entity) null);
    }

    public ItemStack getItem() {
        return (ItemStack) this.getDataWatcher().get(EntityItemFrame.DATA_ITEM);
    }

    public void setItem(ItemStack itemstack) {
        this.setItem(itemstack, true);
    }

    public void setItem(ItemStack itemstack, boolean flag) {
        if (!itemstack.isEmpty()) {
            itemstack = itemstack.cloneItemStack();
            itemstack.setCount(1);
            itemstack.a((Entity) this);
        }

        this.getDataWatcher().set(EntityItemFrame.DATA_ITEM, itemstack);
        if (!itemstack.isEmpty()) {
            this.playSound(this.l(), 1.0F, 1.0F);
        }

        if (flag && this.pos != null) {
            this.level.updateAdjacentComparators(this.pos, Blocks.AIR);
        }

    }

    public SoundEffect l() {
        return SoundEffects.ITEM_FRAME_ADD_ITEM;
    }

    @Override
    public SlotAccess k(int i) {
        return i == 0 ? new SlotAccess() {
            @Override
            public ItemStack a() {
                return EntityItemFrame.this.getItem();
            }

            @Override
            public boolean a(ItemStack itemstack) {
                EntityItemFrame.this.setItem(itemstack);
                return true;
            }
        } : super.k(i);
    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        if (datawatcherobject.equals(EntityItemFrame.DATA_ITEM)) {
            ItemStack itemstack = this.getItem();

            if (!itemstack.isEmpty() && itemstack.D() != this) {
                itemstack.a((Entity) this);
            }
        }

    }

    public int getRotation() {
        return (Integer) this.getDataWatcher().get(EntityItemFrame.DATA_ROTATION);
    }

    public void setRotation(int i) {
        this.setRotation(i, true);
    }

    private void setRotation(int i, boolean flag) {
        this.getDataWatcher().set(EntityItemFrame.DATA_ROTATION, i % 8);
        if (flag && this.pos != null) {
            this.level.updateAdjacentComparators(this.pos, Blocks.AIR);
        }

    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        if (!this.getItem().isEmpty()) {
            nbttagcompound.set("Item", this.getItem().save(new NBTTagCompound()));
            nbttagcompound.setByte("ItemRotation", (byte) this.getRotation());
            nbttagcompound.setFloat("ItemDropChance", this.dropChance);
        }

        nbttagcompound.setByte("Facing", (byte) this.direction.b());
        nbttagcompound.setBoolean("Invisible", this.isInvisible());
        nbttagcompound.setBoolean("Fixed", this.fixed);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Item");

        if (nbttagcompound1 != null && !nbttagcompound1.isEmpty()) {
            ItemStack itemstack = ItemStack.a(nbttagcompound1);

            if (itemstack.isEmpty()) {
                EntityItemFrame.LOGGER.warn("Unable to load item from: {}", nbttagcompound1);
            }

            ItemStack itemstack1 = this.getItem();

            if (!itemstack1.isEmpty() && !ItemStack.matches(itemstack, itemstack1)) {
                this.c(itemstack1);
            }

            this.setItem(itemstack, false);
            this.setRotation(nbttagcompound.getByte("ItemRotation"), false);
            if (nbttagcompound.hasKeyOfType("ItemDropChance", 99)) {
                this.dropChance = nbttagcompound.getFloat("ItemDropChance");
            }
        }

        this.setDirection(EnumDirection.fromType1(nbttagcompound.getByte("Facing")));
        this.setInvisible(nbttagcompound.getBoolean("Invisible"));
        this.fixed = nbttagcompound.getBoolean("Fixed");
    }

    @Override
    public EnumInteractionResult a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);
        boolean flag = !this.getItem().isEmpty();
        boolean flag1 = !itemstack.isEmpty();

        if (this.fixed) {
            return EnumInteractionResult.PASS;
        } else if (!this.level.isClientSide) {
            if (!flag) {
                if (flag1 && !this.isRemoved()) {
                    if (itemstack.a(Items.FILLED_MAP)) {
                        WorldMap worldmap = ItemWorldMap.getSavedMap(itemstack, this.level);

                        if (worldmap != null && worldmap.b(256)) {
                            return EnumInteractionResult.FAIL;
                        }
                    }

                    this.setItem(itemstack);
                    if (!entityhuman.getAbilities().instabuild) {
                        itemstack.subtract(1);
                    }
                }
            } else {
                this.playSound(this.n(), 1.0F, 1.0F);
                this.setRotation(this.getRotation() + 1);
            }

            return EnumInteractionResult.CONSUME;
        } else {
            return !flag && !flag1 ? EnumInteractionResult.PASS : EnumInteractionResult.SUCCESS;
        }
    }

    public SoundEffect n() {
        return SoundEffects.ITEM_FRAME_ROTATE_ITEM;
    }

    public int z() {
        return this.getItem().isEmpty() ? 0 : this.getRotation() % 8 + 1;
    }

    @Override
    public Packet<?> getPacket() {
        return new PacketPlayOutSpawnEntity(this, this.getEntityType(), this.direction.b(), this.getBlockPosition());
    }

    @Override
    public void a(PacketPlayOutSpawnEntity packetplayoutspawnentity) {
        super.a(packetplayoutspawnentity);
        this.setDirection(EnumDirection.fromType1(packetplayoutspawnentity.m()));
    }

    @Override
    public ItemStack df() {
        ItemStack itemstack = this.getItem();

        return itemstack.isEmpty() ? this.o() : itemstack.cloneItemStack();
    }

    protected ItemStack o() {
        return new ItemStack(Items.ITEM_FRAME);
    }
}
