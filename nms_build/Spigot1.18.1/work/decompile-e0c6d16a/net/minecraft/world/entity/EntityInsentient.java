package net.minecraft.world.entity;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.network.protocol.game.PacketPlayOutAttachEntity;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.tags.Tag;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.control.ControllerJump;
import net.minecraft.world.entity.ai.control.ControllerLook;
import net.minecraft.world.entity.ai.control.ControllerMove;
import net.minecraft.world.entity.ai.control.EntityAIBodyControl;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalSelector;
import net.minecraft.world.entity.ai.navigation.Navigation;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.ai.sensing.EntitySenses;
import net.minecraft.world.entity.decoration.EntityHanging;
import net.minecraft.world.entity.decoration.EntityLeash;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.monster.IMonster;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.vehicle.EntityBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemArmor;
import net.minecraft.world.item.ItemAxe;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemBow;
import net.minecraft.world.item.ItemCrossbow;
import net.minecraft.world.item.ItemMonsterEgg;
import net.minecraft.world.item.ItemProjectileWeapon;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemSword;
import net.minecraft.world.item.ItemTool;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.loot.LootTableInfo;

public abstract class EntityInsentient extends EntityLiving {

    private static final DataWatcherObject<Byte> DATA_MOB_FLAGS_ID = DataWatcher.defineId(EntityInsentient.class, DataWatcherRegistry.BYTE);
    private static final int MOB_FLAG_NO_AI = 1;
    private static final int MOB_FLAG_LEFTHANDED = 2;
    private static final int MOB_FLAG_AGGRESSIVE = 4;
    public static final float MAX_WEARING_ARMOR_CHANCE = 0.15F;
    public static final float MAX_PICKUP_LOOT_CHANCE = 0.55F;
    public static final float MAX_ENCHANTED_ARMOR_CHANCE = 0.5F;
    public static final float MAX_ENCHANTED_WEAPON_CHANCE = 0.25F;
    public static final String LEASH_TAG = "Leash";
    private static final int PICKUP_REACH = 1;
    public static final float DEFAULT_EQUIPMENT_DROP_CHANCE = 0.085F;
    public static final int UPDATE_GOAL_SELECTOR_EVERY_N_TICKS = 2;
    public int ambientSoundTime;
    protected int xpReward;
    protected ControllerLook lookControl;
    protected ControllerMove moveControl;
    protected ControllerJump jumpControl;
    private final EntityAIBodyControl bodyRotationControl;
    protected NavigationAbstract navigation;
    public PathfinderGoalSelector goalSelector;
    public PathfinderGoalSelector targetSelector;
    @Nullable
    private EntityLiving target;
    private final EntitySenses sensing;
    private final NonNullList<ItemStack> handItems;
    public final float[] handDropChances;
    private final NonNullList<ItemStack> armorItems;
    public final float[] armorDropChances;
    private boolean canPickUpLoot;
    private boolean persistenceRequired;
    private final Map<PathType, Float> pathfindingMalus;
    @Nullable
    public MinecraftKey lootTable;
    public long lootTableSeed;
    @Nullable
    private Entity leashHolder;
    private int delayedLeashHolderId;
    @Nullable
    private NBTTagCompound leashInfoTag;
    private BlockPosition restrictCenter;
    private float restrictRadius;

    protected EntityInsentient(EntityTypes<? extends EntityInsentient> entitytypes, World world) {
        super(entitytypes, world);
        this.handItems = NonNullList.withSize(2, ItemStack.EMPTY);
        this.handDropChances = new float[2];
        this.armorItems = NonNullList.withSize(4, ItemStack.EMPTY);
        this.armorDropChances = new float[4];
        this.pathfindingMalus = Maps.newEnumMap(PathType.class);
        this.restrictCenter = BlockPosition.ZERO;
        this.restrictRadius = -1.0F;
        this.goalSelector = new PathfinderGoalSelector(world.getProfilerSupplier());
        this.targetSelector = new PathfinderGoalSelector(world.getProfilerSupplier());
        this.lookControl = new ControllerLook(this);
        this.moveControl = new ControllerMove(this);
        this.jumpControl = new ControllerJump(this);
        this.bodyRotationControl = this.createBodyControl();
        this.navigation = this.createNavigation(world);
        this.sensing = new EntitySenses(this);
        Arrays.fill(this.armorDropChances, 0.085F);
        Arrays.fill(this.handDropChances, 0.085F);
        if (world != null && !world.isClientSide) {
            this.registerGoals();
        }

    }

    protected void registerGoals() {}

    public static AttributeProvider.Builder createMobAttributes() {
        return EntityLiving.createLivingAttributes().add(GenericAttributes.FOLLOW_RANGE, 16.0D).add(GenericAttributes.ATTACK_KNOCKBACK);
    }

    protected NavigationAbstract createNavigation(World world) {
        return new Navigation(this, world);
    }

    protected boolean shouldPassengersInheritMalus() {
        return false;
    }

    public float getPathfindingMalus(PathType pathtype) {
        EntityInsentient entityinsentient;

        if (this.getVehicle() instanceof EntityInsentient && ((EntityInsentient) this.getVehicle()).shouldPassengersInheritMalus()) {
            entityinsentient = (EntityInsentient) this.getVehicle();
        } else {
            entityinsentient = this;
        }

        Float ofloat = (Float) entityinsentient.pathfindingMalus.get(pathtype);

        return ofloat == null ? pathtype.getMalus() : ofloat;
    }

    public void setPathfindingMalus(PathType pathtype, float f) {
        this.pathfindingMalus.put(pathtype, f);
    }

    public boolean canCutCorner(PathType pathtype) {
        return pathtype != PathType.DANGER_FIRE && pathtype != PathType.DANGER_CACTUS && pathtype != PathType.DANGER_OTHER && pathtype != PathType.WALKABLE_DOOR;
    }

    protected EntityAIBodyControl createBodyControl() {
        return new EntityAIBodyControl(this);
    }

    public ControllerLook getLookControl() {
        return this.lookControl;
    }

    public ControllerMove getMoveControl() {
        if (this.isPassenger() && this.getVehicle() instanceof EntityInsentient) {
            EntityInsentient entityinsentient = (EntityInsentient) this.getVehicle();

            return entityinsentient.getMoveControl();
        } else {
            return this.moveControl;
        }
    }

    public ControllerJump getJumpControl() {
        return this.jumpControl;
    }

    public NavigationAbstract getNavigation() {
        if (this.isPassenger() && this.getVehicle() instanceof EntityInsentient) {
            EntityInsentient entityinsentient = (EntityInsentient) this.getVehicle();

            return entityinsentient.getNavigation();
        } else {
            return this.navigation;
        }
    }

    public EntitySenses getSensing() {
        return this.sensing;
    }

    @Nullable
    public EntityLiving getTarget() {
        return this.target;
    }

    public void setTarget(@Nullable EntityLiving entityliving) {
        this.target = entityliving;
    }

    @Override
    public boolean canAttackType(EntityTypes<?> entitytypes) {
        return entitytypes != EntityTypes.GHAST;
    }

    public boolean canFireProjectileWeapon(ItemProjectileWeapon itemprojectileweapon) {
        return false;
    }

    public void ate() {}

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityInsentient.DATA_MOB_FLAGS_ID, (byte) 0);
    }

    public int getAmbientSoundInterval() {
        return 80;
    }

    public void playAmbientSound() {
        SoundEffect soundeffect = this.getAmbientSound();

        if (soundeffect != null) {
            this.playSound(soundeffect, this.getSoundVolume(), this.getVoicePitch());
        }

    }

    @Override
    public void baseTick() {
        super.baseTick();
        this.level.getProfiler().push("mobBaseTick");
        if (this.isAlive() && this.random.nextInt(1000) < this.ambientSoundTime++) {
            this.resetAmbientSoundTime();
            this.playAmbientSound();
        }

        this.level.getProfiler().pop();
    }

    @Override
    protected void playHurtSound(DamageSource damagesource) {
        this.resetAmbientSoundTime();
        super.playHurtSound(damagesource);
    }

    private void resetAmbientSoundTime() {
        this.ambientSoundTime = -this.getAmbientSoundInterval();
    }

    @Override
    protected int getExperienceReward(EntityHuman entityhuman) {
        if (this.xpReward > 0) {
            int i = this.xpReward;

            int j;

            for (j = 0; j < this.armorItems.size(); ++j) {
                if (!((ItemStack) this.armorItems.get(j)).isEmpty() && this.armorDropChances[j] <= 1.0F) {
                    i += 1 + this.random.nextInt(3);
                }
            }

            for (j = 0; j < this.handItems.size(); ++j) {
                if (!((ItemStack) this.handItems.get(j)).isEmpty() && this.handDropChances[j] <= 1.0F) {
                    i += 1 + this.random.nextInt(3);
                }
            }

            return i;
        } else {
            return this.xpReward;
        }
    }

    public void spawnAnim() {
        if (this.level.isClientSide) {
            for (int i = 0; i < 20; ++i) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;
                double d3 = 10.0D;

                this.level.addParticle(Particles.POOF, this.getX(1.0D) - d0 * 10.0D, this.getRandomY() - d1 * 10.0D, this.getRandomZ(1.0D) - d2 * 10.0D, d0, d1, d2);
            }
        } else {
            this.level.broadcastEntityEvent(this, (byte) 20);
        }

    }

    @Override
    public void handleEntityEvent(byte b0) {
        if (b0 == 20) {
            this.spawnAnim();
        } else {
            super.handleEntityEvent(b0);
        }

    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            this.tickLeash();
            if (this.tickCount % 5 == 0) {
                this.updateControlFlags();
            }
        }

    }

    protected void updateControlFlags() {
        boolean flag = !(this.getControllingPassenger() instanceof EntityInsentient);
        boolean flag1 = !(this.getVehicle() instanceof EntityBoat);

        this.goalSelector.setControlFlag(PathfinderGoal.Type.MOVE, flag);
        this.goalSelector.setControlFlag(PathfinderGoal.Type.JUMP, flag && flag1);
        this.goalSelector.setControlFlag(PathfinderGoal.Type.LOOK, flag);
    }

    @Override
    protected float tickHeadTurn(float f, float f1) {
        this.bodyRotationControl.clientTick();
        return f1;
    }

    @Nullable
    protected SoundEffect getAmbientSound() {
        return null;
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putBoolean("CanPickUpLoot", this.canPickUpLoot());
        nbttagcompound.putBoolean("PersistenceRequired", this.persistenceRequired);
        NBTTagList nbttaglist = new NBTTagList();

        NBTTagCompound nbttagcompound1;

        for (Iterator iterator = this.armorItems.iterator(); iterator.hasNext(); nbttaglist.add(nbttagcompound1)) {
            ItemStack itemstack = (ItemStack) iterator.next();

            nbttagcompound1 = new NBTTagCompound();
            if (!itemstack.isEmpty()) {
                itemstack.save(nbttagcompound1);
            }
        }

        nbttagcompound.put("ArmorItems", nbttaglist);
        NBTTagList nbttaglist1 = new NBTTagList();

        NBTTagCompound nbttagcompound2;

        for (Iterator iterator1 = this.handItems.iterator(); iterator1.hasNext(); nbttaglist1.add(nbttagcompound2)) {
            ItemStack itemstack1 = (ItemStack) iterator1.next();

            nbttagcompound2 = new NBTTagCompound();
            if (!itemstack1.isEmpty()) {
                itemstack1.save(nbttagcompound2);
            }
        }

        nbttagcompound.put("HandItems", nbttaglist1);
        NBTTagList nbttaglist2 = new NBTTagList();
        float[] afloat = this.armorDropChances;
        int i = afloat.length;

        int j;

        for (j = 0; j < i; ++j) {
            float f = afloat[j];

            nbttaglist2.add(NBTTagFloat.valueOf(f));
        }

        nbttagcompound.put("ArmorDropChances", nbttaglist2);
        NBTTagList nbttaglist3 = new NBTTagList();
        float[] afloat1 = this.handDropChances;

        j = afloat1.length;

        for (int k = 0; k < j; ++k) {
            float f1 = afloat1[k];

            nbttaglist3.add(NBTTagFloat.valueOf(f1));
        }

        nbttagcompound.put("HandDropChances", nbttaglist3);
        if (this.leashHolder != null) {
            nbttagcompound2 = new NBTTagCompound();
            if (this.leashHolder instanceof EntityLiving) {
                UUID uuid = this.leashHolder.getUUID();

                nbttagcompound2.putUUID("UUID", uuid);
            } else if (this.leashHolder instanceof EntityHanging) {
                BlockPosition blockposition = ((EntityHanging) this.leashHolder).getPos();

                nbttagcompound2.putInt("X", blockposition.getX());
                nbttagcompound2.putInt("Y", blockposition.getY());
                nbttagcompound2.putInt("Z", blockposition.getZ());
            }

            nbttagcompound.put("Leash", nbttagcompound2);
        } else if (this.leashInfoTag != null) {
            nbttagcompound.put("Leash", this.leashInfoTag.copy());
        }

        nbttagcompound.putBoolean("LeftHanded", this.isLeftHanded());
        if (this.lootTable != null) {
            nbttagcompound.putString("DeathLootTable", this.lootTable.toString());
            if (this.lootTableSeed != 0L) {
                nbttagcompound.putLong("DeathLootTableSeed", this.lootTableSeed);
            }
        }

        if (this.isNoAi()) {
            nbttagcompound.putBoolean("NoAI", this.isNoAi());
        }

    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        if (nbttagcompound.contains("CanPickUpLoot", 1)) {
            this.setCanPickUpLoot(nbttagcompound.getBoolean("CanPickUpLoot"));
        }

        this.persistenceRequired = nbttagcompound.getBoolean("PersistenceRequired");
        NBTTagList nbttaglist;
        int i;

        if (nbttagcompound.contains("ArmorItems", 9)) {
            nbttaglist = nbttagcompound.getList("ArmorItems", 10);

            for (i = 0; i < this.armorItems.size(); ++i) {
                this.armorItems.set(i, ItemStack.of(nbttaglist.getCompound(i)));
            }
        }

        if (nbttagcompound.contains("HandItems", 9)) {
            nbttaglist = nbttagcompound.getList("HandItems", 10);

            for (i = 0; i < this.handItems.size(); ++i) {
                this.handItems.set(i, ItemStack.of(nbttaglist.getCompound(i)));
            }
        }

        if (nbttagcompound.contains("ArmorDropChances", 9)) {
            nbttaglist = nbttagcompound.getList("ArmorDropChances", 5);

            for (i = 0; i < nbttaglist.size(); ++i) {
                this.armorDropChances[i] = nbttaglist.getFloat(i);
            }
        }

        if (nbttagcompound.contains("HandDropChances", 9)) {
            nbttaglist = nbttagcompound.getList("HandDropChances", 5);

            for (i = 0; i < nbttaglist.size(); ++i) {
                this.handDropChances[i] = nbttaglist.getFloat(i);
            }
        }

        if (nbttagcompound.contains("Leash", 10)) {
            this.leashInfoTag = nbttagcompound.getCompound("Leash");
        }

        this.setLeftHanded(nbttagcompound.getBoolean("LeftHanded"));
        if (nbttagcompound.contains("DeathLootTable", 8)) {
            this.lootTable = new MinecraftKey(nbttagcompound.getString("DeathLootTable"));
            this.lootTableSeed = nbttagcompound.getLong("DeathLootTableSeed");
        }

        this.setNoAi(nbttagcompound.getBoolean("NoAI"));
    }

    @Override
    protected void dropFromLootTable(DamageSource damagesource, boolean flag) {
        super.dropFromLootTable(damagesource, flag);
        this.lootTable = null;
    }

    @Override
    protected LootTableInfo.Builder createLootContext(boolean flag, DamageSource damagesource) {
        return super.createLootContext(flag, damagesource).withOptionalRandomSeed(this.lootTableSeed, this.random);
    }

    @Override
    public final MinecraftKey getLootTable() {
        return this.lootTable == null ? this.getDefaultLootTable() : this.lootTable;
    }

    public MinecraftKey getDefaultLootTable() {
        return super.getLootTable();
    }

    public void setZza(float f) {
        this.zza = f;
    }

    public void setYya(float f) {
        this.yya = f;
    }

    public void setXxa(float f) {
        this.xxa = f;
    }

    @Override
    public void setSpeed(float f) {
        super.setSpeed(f);
        this.setZza(f);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.level.getProfiler().push("looting");
        if (!this.level.isClientSide && this.canPickUpLoot() && this.isAlive() && !this.dead && this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            List<EntityItem> list = this.level.getEntitiesOfClass(EntityItem.class, this.getBoundingBox().inflate(1.0D, 0.0D, 1.0D));
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityItem entityitem = (EntityItem) iterator.next();

                if (!entityitem.isRemoved() && !entityitem.getItem().isEmpty() && !entityitem.hasPickUpDelay() && this.wantsToPickUp(entityitem.getItem())) {
                    this.pickUpItem(entityitem);
                }
            }
        }

        this.level.getProfiler().pop();
    }

    protected void pickUpItem(EntityItem entityitem) {
        ItemStack itemstack = entityitem.getItem();

        if (this.equipItemIfPossible(itemstack)) {
            this.onItemPickup(entityitem);
            this.take(entityitem, itemstack.getCount());
            entityitem.discard();
        }

    }

    public boolean equipItemIfPossible(ItemStack itemstack) {
        EnumItemSlot enumitemslot = getEquipmentSlotForItem(itemstack);
        ItemStack itemstack1 = this.getItemBySlot(enumitemslot);
        boolean flag = this.canReplaceCurrentItem(itemstack, itemstack1);

        if (flag && this.canHoldItem(itemstack)) {
            double d0 = (double) this.getEquipmentDropChance(enumitemslot);

            if (!itemstack1.isEmpty() && (double) Math.max(this.random.nextFloat() - 0.1F, 0.0F) < d0) {
                this.spawnAtLocation(itemstack1);
            }

            this.setItemSlotAndDropWhenKilled(enumitemslot, itemstack);
            this.equipEventAndSound(itemstack);
            return true;
        } else {
            return false;
        }
    }

    protected void setItemSlotAndDropWhenKilled(EnumItemSlot enumitemslot, ItemStack itemstack) {
        this.setItemSlot(enumitemslot, itemstack);
        this.setGuaranteedDrop(enumitemslot);
        this.persistenceRequired = true;
    }

    public void setGuaranteedDrop(EnumItemSlot enumitemslot) {
        switch (enumitemslot.getType()) {
            case HAND:
                this.handDropChances[enumitemslot.getIndex()] = 2.0F;
                break;
            case ARMOR:
                this.armorDropChances[enumitemslot.getIndex()] = 2.0F;
        }

    }

    protected boolean canReplaceCurrentItem(ItemStack itemstack, ItemStack itemstack1) {
        if (itemstack1.isEmpty()) {
            return true;
        } else if (itemstack.getItem() instanceof ItemSword) {
            if (!(itemstack1.getItem() instanceof ItemSword)) {
                return true;
            } else {
                ItemSword itemsword = (ItemSword) itemstack.getItem();
                ItemSword itemsword1 = (ItemSword) itemstack1.getItem();

                return itemsword.getDamage() != itemsword1.getDamage() ? itemsword.getDamage() > itemsword1.getDamage() : this.canReplaceEqualItem(itemstack, itemstack1);
            }
        } else if (itemstack.getItem() instanceof ItemBow && itemstack1.getItem() instanceof ItemBow) {
            return this.canReplaceEqualItem(itemstack, itemstack1);
        } else if (itemstack.getItem() instanceof ItemCrossbow && itemstack1.getItem() instanceof ItemCrossbow) {
            return this.canReplaceEqualItem(itemstack, itemstack1);
        } else if (itemstack.getItem() instanceof ItemArmor) {
            if (EnchantmentManager.hasBindingCurse(itemstack1)) {
                return false;
            } else if (!(itemstack1.getItem() instanceof ItemArmor)) {
                return true;
            } else {
                ItemArmor itemarmor = (ItemArmor) itemstack.getItem();
                ItemArmor itemarmor1 = (ItemArmor) itemstack1.getItem();

                return itemarmor.getDefense() != itemarmor1.getDefense() ? itemarmor.getDefense() > itemarmor1.getDefense() : (itemarmor.getToughness() != itemarmor1.getToughness() ? itemarmor.getToughness() > itemarmor1.getToughness() : this.canReplaceEqualItem(itemstack, itemstack1));
            }
        } else {
            if (itemstack.getItem() instanceof ItemTool) {
                if (itemstack1.getItem() instanceof ItemBlock) {
                    return true;
                }

                if (itemstack1.getItem() instanceof ItemTool) {
                    ItemTool itemtool = (ItemTool) itemstack.getItem();
                    ItemTool itemtool1 = (ItemTool) itemstack1.getItem();

                    if (itemtool.getAttackDamage() != itemtool1.getAttackDamage()) {
                        return itemtool.getAttackDamage() > itemtool1.getAttackDamage();
                    }

                    return this.canReplaceEqualItem(itemstack, itemstack1);
                }
            }

            return false;
        }
    }

    public boolean canReplaceEqualItem(ItemStack itemstack, ItemStack itemstack1) {
        return itemstack.getDamageValue() >= itemstack1.getDamageValue() && (!itemstack.hasTag() || itemstack1.hasTag()) ? (itemstack.hasTag() && itemstack1.hasTag() ? itemstack.getTag().getAllKeys().stream().anyMatch((s) -> {
            return !s.equals("Damage");
        }) && !itemstack1.getTag().getAllKeys().stream().anyMatch((s) -> {
            return !s.equals("Damage");
        }) : false) : true;
    }

    public boolean canHoldItem(ItemStack itemstack) {
        return true;
    }

    public boolean wantsToPickUp(ItemStack itemstack) {
        return this.canHoldItem(itemstack);
    }

    public boolean removeWhenFarAway(double d0) {
        return true;
    }

    public boolean requiresCustomPersistence() {
        return this.isPassenger();
    }

    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    public void checkDespawn() {
        if (this.level.getDifficulty() == EnumDifficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
            this.discard();
        } else if (!this.isPersistenceRequired() && !this.requiresCustomPersistence()) {
            EntityHuman entityhuman = this.level.getNearestPlayer(this, -1.0D);

            if (entityhuman != null) {
                double d0 = entityhuman.distanceToSqr((Entity) this);
                int i = this.getType().getCategory().getDespawnDistance();
                int j = i * i;

                if (d0 > (double) j && this.removeWhenFarAway(d0)) {
                    this.discard();
                }

                int k = this.getType().getCategory().getNoDespawnDistance();
                int l = k * k;

                if (this.noActionTime > 600 && this.random.nextInt(800) == 0 && d0 > (double) l && this.removeWhenFarAway(d0)) {
                    this.discard();
                } else if (d0 < (double) l) {
                    this.noActionTime = 0;
                }
            }

        } else {
            this.noActionTime = 0;
        }
    }

    @Override
    protected final void serverAiStep() {
        ++this.noActionTime;
        this.level.getProfiler().push("sensing");
        this.sensing.tick();
        this.level.getProfiler().pop();
        int i = this.level.getServer().getTickCount() + this.getId();

        if (i % 2 != 0 && this.tickCount > 1) {
            this.level.getProfiler().push("targetSelector");
            this.targetSelector.tickRunningGoals(false);
            this.level.getProfiler().pop();
            this.level.getProfiler().push("goalSelector");
            this.goalSelector.tickRunningGoals(false);
            this.level.getProfiler().pop();
        } else {
            this.level.getProfiler().push("targetSelector");
            this.targetSelector.tick();
            this.level.getProfiler().pop();
            this.level.getProfiler().push("goalSelector");
            this.goalSelector.tick();
            this.level.getProfiler().pop();
        }

        this.level.getProfiler().push("navigation");
        this.navigation.tick();
        this.level.getProfiler().pop();
        this.level.getProfiler().push("mob tick");
        this.customServerAiStep();
        this.level.getProfiler().pop();
        this.level.getProfiler().push("controls");
        this.level.getProfiler().push("move");
        this.moveControl.tick();
        this.level.getProfiler().popPush("look");
        this.lookControl.tick();
        this.level.getProfiler().popPush("jump");
        this.jumpControl.tick();
        this.level.getProfiler().pop();
        this.level.getProfiler().pop();
        this.sendDebugPackets();
    }

    protected void sendDebugPackets() {
        PacketDebug.sendGoalSelector(this.level, this, this.goalSelector);
    }

    protected void customServerAiStep() {}

    public int getMaxHeadXRot() {
        return 40;
    }

    public int getMaxHeadYRot() {
        return 75;
    }

    public int getHeadRotSpeed() {
        return 10;
    }

    public void lookAt(Entity entity, float f, float f1) {
        double d0 = entity.getX() - this.getX();
        double d1 = entity.getZ() - this.getZ();
        double d2;

        if (entity instanceof EntityLiving) {
            EntityLiving entityliving = (EntityLiving) entity;

            d2 = entityliving.getEyeY() - this.getEyeY();
        } else {
            d2 = (entity.getBoundingBox().minY + entity.getBoundingBox().maxY) / 2.0D - this.getEyeY();
        }

        double d3 = Math.sqrt(d0 * d0 + d1 * d1);
        float f2 = (float) (MathHelper.atan2(d1, d0) * 57.2957763671875D) - 90.0F;
        float f3 = (float) (-(MathHelper.atan2(d2, d3) * 57.2957763671875D));

        this.setXRot(this.rotlerp(this.getXRot(), f3, f1));
        this.setYRot(this.rotlerp(this.getYRot(), f2, f));
    }

    private float rotlerp(float f, float f1, float f2) {
        float f3 = MathHelper.wrapDegrees(f1 - f);

        if (f3 > f2) {
            f3 = f2;
        }

        if (f3 < -f2) {
            f3 = -f2;
        }

        return f + f3;
    }

    public static boolean checkMobSpawnRules(EntityTypes<? extends EntityInsentient> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        BlockPosition blockposition1 = blockposition.below();

        return enummobspawn == EnumMobSpawn.SPAWNER || generatoraccess.getBlockState(blockposition1).isValidSpawn(generatoraccess, blockposition1, entitytypes);
    }

    public boolean checkSpawnRules(GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn) {
        return true;
    }

    public boolean checkSpawnObstruction(IWorldReader iworldreader) {
        return !iworldreader.containsAnyLiquid(this.getBoundingBox()) && iworldreader.isUnobstructed(this);
    }

    public int getMaxSpawnClusterSize() {
        return 4;
    }

    public boolean isMaxGroupSizeReached(int i) {
        return false;
    }

    @Override
    public int getMaxFallDistance() {
        if (this.getTarget() == null) {
            return 3;
        } else {
            int i = (int) (this.getHealth() - this.getMaxHealth() * 0.33F);

            i -= (3 - this.level.getDifficulty().getId()) * 4;
            if (i < 0) {
                i = 0;
            }

            return i + 3;
        }
    }

    @Override
    public Iterable<ItemStack> getHandSlots() {
        return this.handItems;
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return this.armorItems;
    }

    @Override
    public ItemStack getItemBySlot(EnumItemSlot enumitemslot) {
        switch (enumitemslot.getType()) {
            case HAND:
                return (ItemStack) this.handItems.get(enumitemslot.getIndex());
            case ARMOR:
                return (ItemStack) this.armorItems.get(enumitemslot.getIndex());
            default:
                return ItemStack.EMPTY;
        }
    }

    @Override
    public void setItemSlot(EnumItemSlot enumitemslot, ItemStack itemstack) {
        this.verifyEquippedItem(itemstack);
        switch (enumitemslot.getType()) {
            case HAND:
                this.handItems.set(enumitemslot.getIndex(), itemstack);
                break;
            case ARMOR:
                this.armorItems.set(enumitemslot.getIndex(), itemstack);
        }

    }

    @Override
    protected void dropCustomDeathLoot(DamageSource damagesource, int i, boolean flag) {
        super.dropCustomDeathLoot(damagesource, i, flag);
        EnumItemSlot[] aenumitemslot = EnumItemSlot.values();
        int j = aenumitemslot.length;

        for (int k = 0; k < j; ++k) {
            EnumItemSlot enumitemslot = aenumitemslot[k];
            ItemStack itemstack = this.getItemBySlot(enumitemslot);
            float f = this.getEquipmentDropChance(enumitemslot);
            boolean flag1 = f > 1.0F;

            if (!itemstack.isEmpty() && !EnchantmentManager.hasVanishingCurse(itemstack) && (flag || flag1) && Math.max(this.random.nextFloat() - (float) i * 0.01F, 0.0F) < f) {
                if (!flag1 && itemstack.isDamageableItem()) {
                    itemstack.setDamageValue(itemstack.getMaxDamage() - this.random.nextInt(1 + this.random.nextInt(Math.max(itemstack.getMaxDamage() - 3, 1))));
                }

                this.spawnAtLocation(itemstack);
                this.setItemSlot(enumitemslot, ItemStack.EMPTY);
            }
        }

    }

    protected float getEquipmentDropChance(EnumItemSlot enumitemslot) {
        float f;

        switch (enumitemslot.getType()) {
            case HAND:
                f = this.handDropChances[enumitemslot.getIndex()];
                break;
            case ARMOR:
                f = this.armorDropChances[enumitemslot.getIndex()];
                break;
            default:
                f = 0.0F;
        }

        return f;
    }

    protected void populateDefaultEquipmentSlots(DifficultyDamageScaler difficultydamagescaler) {
        if (this.random.nextFloat() < 0.15F * difficultydamagescaler.getSpecialMultiplier()) {
            int i = this.random.nextInt(2);
            float f = this.level.getDifficulty() == EnumDifficulty.HARD ? 0.1F : 0.25F;

            if (this.random.nextFloat() < 0.095F) {
                ++i;
            }

            if (this.random.nextFloat() < 0.095F) {
                ++i;
            }

            if (this.random.nextFloat() < 0.095F) {
                ++i;
            }

            boolean flag = true;
            EnumItemSlot[] aenumitemslot = EnumItemSlot.values();
            int j = aenumitemslot.length;

            for (int k = 0; k < j; ++k) {
                EnumItemSlot enumitemslot = aenumitemslot[k];

                if (enumitemslot.getType() == EnumItemSlot.Function.ARMOR) {
                    ItemStack itemstack = this.getItemBySlot(enumitemslot);

                    if (!flag && this.random.nextFloat() < f) {
                        break;
                    }

                    flag = false;
                    if (itemstack.isEmpty()) {
                        Item item = getEquipmentForSlot(enumitemslot, i);

                        if (item != null) {
                            this.setItemSlot(enumitemslot, new ItemStack(item));
                        }
                    }
                }
            }
        }

    }

    @Nullable
    public static Item getEquipmentForSlot(EnumItemSlot enumitemslot, int i) {
        switch (enumitemslot) {
            case HEAD:
                if (i == 0) {
                    return Items.LEATHER_HELMET;
                } else if (i == 1) {
                    return Items.GOLDEN_HELMET;
                } else if (i == 2) {
                    return Items.CHAINMAIL_HELMET;
                } else if (i == 3) {
                    return Items.IRON_HELMET;
                } else if (i == 4) {
                    return Items.DIAMOND_HELMET;
                }
            case CHEST:
                if (i == 0) {
                    return Items.LEATHER_CHESTPLATE;
                } else if (i == 1) {
                    return Items.GOLDEN_CHESTPLATE;
                } else if (i == 2) {
                    return Items.CHAINMAIL_CHESTPLATE;
                } else if (i == 3) {
                    return Items.IRON_CHESTPLATE;
                } else if (i == 4) {
                    return Items.DIAMOND_CHESTPLATE;
                }
            case LEGS:
                if (i == 0) {
                    return Items.LEATHER_LEGGINGS;
                } else if (i == 1) {
                    return Items.GOLDEN_LEGGINGS;
                } else if (i == 2) {
                    return Items.CHAINMAIL_LEGGINGS;
                } else if (i == 3) {
                    return Items.IRON_LEGGINGS;
                } else if (i == 4) {
                    return Items.DIAMOND_LEGGINGS;
                }
            case FEET:
                if (i == 0) {
                    return Items.LEATHER_BOOTS;
                } else if (i == 1) {
                    return Items.GOLDEN_BOOTS;
                } else if (i == 2) {
                    return Items.CHAINMAIL_BOOTS;
                } else if (i == 3) {
                    return Items.IRON_BOOTS;
                } else if (i == 4) {
                    return Items.DIAMOND_BOOTS;
                }
            default:
                return null;
        }
    }

    protected void populateDefaultEquipmentEnchantments(DifficultyDamageScaler difficultydamagescaler) {
        float f = difficultydamagescaler.getSpecialMultiplier();

        this.enchantSpawnedWeapon(f);
        EnumItemSlot[] aenumitemslot = EnumItemSlot.values();
        int i = aenumitemslot.length;

        for (int j = 0; j < i; ++j) {
            EnumItemSlot enumitemslot = aenumitemslot[j];

            if (enumitemslot.getType() == EnumItemSlot.Function.ARMOR) {
                this.enchantSpawnedArmor(f, enumitemslot);
            }
        }

    }

    protected void enchantSpawnedWeapon(float f) {
        if (!this.getMainHandItem().isEmpty() && this.random.nextFloat() < 0.25F * f) {
            this.setItemSlot(EnumItemSlot.MAINHAND, EnchantmentManager.enchantItem(this.random, this.getMainHandItem(), (int) (5.0F + f * (float) this.random.nextInt(18)), false));
        }

    }

    protected void enchantSpawnedArmor(float f, EnumItemSlot enumitemslot) {
        ItemStack itemstack = this.getItemBySlot(enumitemslot);

        if (!itemstack.isEmpty() && this.random.nextFloat() < 0.5F * f) {
            this.setItemSlot(enumitemslot, EnchantmentManager.enchantItem(this.random, itemstack, (int) (5.0F + f * (float) this.random.nextInt(18)), false));
        }

    }

    @Nullable
    public GroupDataEntity finalizeSpawn(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        this.getAttribute(GenericAttributes.FOLLOW_RANGE).addPermanentModifier(new AttributeModifier("Random spawn bonus", this.random.nextGaussian() * 0.05D, AttributeModifier.Operation.MULTIPLY_BASE));
        if (this.random.nextFloat() < 0.05F) {
            this.setLeftHanded(true);
        } else {
            this.setLeftHanded(false);
        }

        return groupdataentity;
    }

    public boolean canBeControlledByRider() {
        return false;
    }

    public void setPersistenceRequired() {
        this.persistenceRequired = true;
    }

    public void setDropChance(EnumItemSlot enumitemslot, float f) {
        switch (enumitemslot.getType()) {
            case HAND:
                this.handDropChances[enumitemslot.getIndex()] = f;
                break;
            case ARMOR:
                this.armorDropChances[enumitemslot.getIndex()] = f;
        }

    }

    public boolean canPickUpLoot() {
        return this.canPickUpLoot;
    }

    public void setCanPickUpLoot(boolean flag) {
        this.canPickUpLoot = flag;
    }

    @Override
    public boolean canTakeItem(ItemStack itemstack) {
        EnumItemSlot enumitemslot = getEquipmentSlotForItem(itemstack);

        return this.getItemBySlot(enumitemslot).isEmpty() && this.canPickUpLoot();
    }

    public boolean isPersistenceRequired() {
        return this.persistenceRequired;
    }

    @Override
    public final EnumInteractionResult interact(EntityHuman entityhuman, EnumHand enumhand) {
        if (!this.isAlive()) {
            return EnumInteractionResult.PASS;
        } else if (this.getLeashHolder() == entityhuman) {
            this.dropLeash(true, !entityhuman.getAbilities().instabuild);
            return EnumInteractionResult.sidedSuccess(this.level.isClientSide);
        } else {
            EnumInteractionResult enuminteractionresult = this.checkAndHandleImportantInteractions(entityhuman, enumhand);

            if (enuminteractionresult.consumesAction()) {
                return enuminteractionresult;
            } else {
                enuminteractionresult = this.mobInteract(entityhuman, enumhand);
                return enuminteractionresult.consumesAction() ? enuminteractionresult : super.interact(entityhuman, enumhand);
            }
        }
    }

    private EnumInteractionResult checkAndHandleImportantInteractions(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);

        if (itemstack.is(Items.LEAD) && this.canBeLeashed(entityhuman)) {
            this.setLeashedTo(entityhuman, true);
            itemstack.shrink(1);
            return EnumInteractionResult.sidedSuccess(this.level.isClientSide);
        } else {
            if (itemstack.is(Items.NAME_TAG)) {
                EnumInteractionResult enuminteractionresult = itemstack.interactLivingEntity(entityhuman, this, enumhand);

                if (enuminteractionresult.consumesAction()) {
                    return enuminteractionresult;
                }
            }

            if (itemstack.getItem() instanceof ItemMonsterEgg) {
                if (this.level instanceof WorldServer) {
                    ItemMonsterEgg itemmonsteregg = (ItemMonsterEgg) itemstack.getItem();
                    Optional<EntityInsentient> optional = itemmonsteregg.spawnOffspringFromSpawnEgg(entityhuman, this, this.getType(), (WorldServer) this.level, this.position(), itemstack);

                    optional.ifPresent((entityinsentient) -> {
                        this.onOffspringSpawnedFromEgg(entityhuman, entityinsentient);
                    });
                    return optional.isPresent() ? EnumInteractionResult.SUCCESS : EnumInteractionResult.PASS;
                } else {
                    return EnumInteractionResult.CONSUME;
                }
            } else {
                return EnumInteractionResult.PASS;
            }
        }
    }

    protected void onOffspringSpawnedFromEgg(EntityHuman entityhuman, EntityInsentient entityinsentient) {}

    protected EnumInteractionResult mobInteract(EntityHuman entityhuman, EnumHand enumhand) {
        return EnumInteractionResult.PASS;
    }

    public boolean isWithinRestriction() {
        return this.isWithinRestriction(this.blockPosition());
    }

    public boolean isWithinRestriction(BlockPosition blockposition) {
        return this.restrictRadius == -1.0F ? true : this.restrictCenter.distSqr(blockposition) < (double) (this.restrictRadius * this.restrictRadius);
    }

    public void restrictTo(BlockPosition blockposition, int i) {
        this.restrictCenter = blockposition;
        this.restrictRadius = (float) i;
    }

    public BlockPosition getRestrictCenter() {
        return this.restrictCenter;
    }

    public float getRestrictRadius() {
        return this.restrictRadius;
    }

    public void clearRestriction() {
        this.restrictRadius = -1.0F;
    }

    public boolean hasRestriction() {
        return this.restrictRadius != -1.0F;
    }

    @Nullable
    public <T extends EntityInsentient> T convertTo(EntityTypes<T> entitytypes, boolean flag) {
        if (this.isRemoved()) {
            return null;
        } else {
            T t0 = (EntityInsentient) entitytypes.create(this.level);

            t0.copyPosition(this);
            t0.setBaby(this.isBaby());
            t0.setNoAi(this.isNoAi());
            if (this.hasCustomName()) {
                t0.setCustomName(this.getCustomName());
                t0.setCustomNameVisible(this.isCustomNameVisible());
            }

            if (this.isPersistenceRequired()) {
                t0.setPersistenceRequired();
            }

            t0.setInvulnerable(this.isInvulnerable());
            if (flag) {
                t0.setCanPickUpLoot(this.canPickUpLoot());
                EnumItemSlot[] aenumitemslot = EnumItemSlot.values();
                int i = aenumitemslot.length;

                for (int j = 0; j < i; ++j) {
                    EnumItemSlot enumitemslot = aenumitemslot[j];
                    ItemStack itemstack = this.getItemBySlot(enumitemslot);

                    if (!itemstack.isEmpty()) {
                        t0.setItemSlot(enumitemslot, itemstack.copy());
                        t0.setDropChance(enumitemslot, this.getEquipmentDropChance(enumitemslot));
                        itemstack.setCount(0);
                    }
                }
            }

            this.level.addFreshEntity(t0);
            if (this.isPassenger()) {
                Entity entity = this.getVehicle();

                this.stopRiding();
                t0.startRiding(entity, true);
            }

            this.discard();
            return t0;
        }
    }

    protected void tickLeash() {
        if (this.leashInfoTag != null) {
            this.restoreLeashFromSave();
        }

        if (this.leashHolder != null) {
            if (!this.isAlive() || !this.leashHolder.isAlive()) {
                this.dropLeash(true, true);
            }

        }
    }

    public void dropLeash(boolean flag, boolean flag1) {
        if (this.leashHolder != null) {
            this.leashHolder = null;
            this.leashInfoTag = null;
            if (!this.level.isClientSide && flag1) {
                this.spawnAtLocation((IMaterial) Items.LEAD);
            }

            if (!this.level.isClientSide && flag && this.level instanceof WorldServer) {
                ((WorldServer) this.level).getChunkSource().broadcast(this, new PacketPlayOutAttachEntity(this, (Entity) null));
            }
        }

    }

    public boolean canBeLeashed(EntityHuman entityhuman) {
        return !this.isLeashed() && !(this instanceof IMonster);
    }

    public boolean isLeashed() {
        return this.leashHolder != null;
    }

    @Nullable
    public Entity getLeashHolder() {
        if (this.leashHolder == null && this.delayedLeashHolderId != 0 && this.level.isClientSide) {
            this.leashHolder = this.level.getEntity(this.delayedLeashHolderId);
        }

        return this.leashHolder;
    }

    public void setLeashedTo(Entity entity, boolean flag) {
        this.leashHolder = entity;
        this.leashInfoTag = null;
        if (!this.level.isClientSide && flag && this.level instanceof WorldServer) {
            ((WorldServer) this.level).getChunkSource().broadcast(this, new PacketPlayOutAttachEntity(this, this.leashHolder));
        }

        if (this.isPassenger()) {
            this.stopRiding();
        }

    }

    public void setDelayedLeashHolderId(int i) {
        this.delayedLeashHolderId = i;
        this.dropLeash(false, false);
    }

    @Override
    public boolean startRiding(Entity entity, boolean flag) {
        boolean flag1 = super.startRiding(entity, flag);

        if (flag1 && this.isLeashed()) {
            this.dropLeash(true, true);
        }

        return flag1;
    }

    private void restoreLeashFromSave() {
        if (this.leashInfoTag != null && this.level instanceof WorldServer) {
            if (this.leashInfoTag.hasUUID("UUID")) {
                UUID uuid = this.leashInfoTag.getUUID("UUID");
                Entity entity = ((WorldServer) this.level).getEntity(uuid);

                if (entity != null) {
                    this.setLeashedTo(entity, true);
                    return;
                }
            } else if (this.leashInfoTag.contains("X", 99) && this.leashInfoTag.contains("Y", 99) && this.leashInfoTag.contains("Z", 99)) {
                BlockPosition blockposition = GameProfileSerializer.readBlockPos(this.leashInfoTag);

                this.setLeashedTo(EntityLeash.getOrCreateKnot(this.level, blockposition), true);
                return;
            }

            if (this.tickCount > 100) {
                this.spawnAtLocation((IMaterial) Items.LEAD);
                this.leashInfoTag = null;
            }
        }

    }

    @Override
    public boolean isControlledByLocalInstance() {
        return this.canBeControlledByRider() && super.isControlledByLocalInstance();
    }

    @Override
    public boolean isEffectiveAi() {
        return super.isEffectiveAi() && !this.isNoAi();
    }

    public void setNoAi(boolean flag) {
        byte b0 = (Byte) this.entityData.get(EntityInsentient.DATA_MOB_FLAGS_ID);

        this.entityData.set(EntityInsentient.DATA_MOB_FLAGS_ID, flag ? (byte) (b0 | 1) : (byte) (b0 & -2));
    }

    public void setLeftHanded(boolean flag) {
        byte b0 = (Byte) this.entityData.get(EntityInsentient.DATA_MOB_FLAGS_ID);

        this.entityData.set(EntityInsentient.DATA_MOB_FLAGS_ID, flag ? (byte) (b0 | 2) : (byte) (b0 & -3));
    }

    public void setAggressive(boolean flag) {
        byte b0 = (Byte) this.entityData.get(EntityInsentient.DATA_MOB_FLAGS_ID);

        this.entityData.set(EntityInsentient.DATA_MOB_FLAGS_ID, flag ? (byte) (b0 | 4) : (byte) (b0 & -5));
    }

    public boolean isNoAi() {
        return ((Byte) this.entityData.get(EntityInsentient.DATA_MOB_FLAGS_ID) & 1) != 0;
    }

    public boolean isLeftHanded() {
        return ((Byte) this.entityData.get(EntityInsentient.DATA_MOB_FLAGS_ID) & 2) != 0;
    }

    public boolean isAggressive() {
        return ((Byte) this.entityData.get(EntityInsentient.DATA_MOB_FLAGS_ID) & 4) != 0;
    }

    public void setBaby(boolean flag) {}

    @Override
    public EnumMainHand getMainArm() {
        return this.isLeftHanded() ? EnumMainHand.LEFT : EnumMainHand.RIGHT;
    }

    public double getMeleeAttackRangeSqr(EntityLiving entityliving) {
        return (double) (this.getBbWidth() * 2.0F * this.getBbWidth() * 2.0F + entityliving.getBbWidth());
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        float f = (float) this.getAttributeValue(GenericAttributes.ATTACK_DAMAGE);
        float f1 = (float) this.getAttributeValue(GenericAttributes.ATTACK_KNOCKBACK);

        if (entity instanceof EntityLiving) {
            f += EnchantmentManager.getDamageBonus(this.getMainHandItem(), ((EntityLiving) entity).getMobType());
            f1 += (float) EnchantmentManager.getKnockbackBonus(this);
        }

        int i = EnchantmentManager.getFireAspect(this);

        if (i > 0) {
            entity.setSecondsOnFire(i * 4);
        }

        boolean flag = entity.hurt(DamageSource.mobAttack(this), f);

        if (flag) {
            if (f1 > 0.0F && entity instanceof EntityLiving) {
                ((EntityLiving) entity).knockback((double) (f1 * 0.5F), (double) MathHelper.sin(this.getYRot() * 0.017453292F), (double) (-MathHelper.cos(this.getYRot() * 0.017453292F)));
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
            }

            if (entity instanceof EntityHuman) {
                EntityHuman entityhuman = (EntityHuman) entity;

                this.maybeDisableShield(entityhuman, this.getMainHandItem(), entityhuman.isUsingItem() ? entityhuman.getUseItem() : ItemStack.EMPTY);
            }

            this.doEnchantDamageEffects(this, entity);
            this.setLastHurtMob(entity);
        }

        return flag;
    }

    private void maybeDisableShield(EntityHuman entityhuman, ItemStack itemstack, ItemStack itemstack1) {
        if (!itemstack.isEmpty() && !itemstack1.isEmpty() && itemstack.getItem() instanceof ItemAxe && itemstack1.is(Items.SHIELD)) {
            float f = 0.25F + (float) EnchantmentManager.getBlockEfficiency(this) * 0.05F;

            if (this.random.nextFloat() < f) {
                entityhuman.getCooldowns().addCooldown(Items.SHIELD, 100);
                this.level.broadcastEntityEvent(entityhuman, (byte) 30);
            }
        }

    }

    protected boolean isSunBurnTick() {
        if (this.level.isDay() && !this.level.isClientSide) {
            float f = this.getBrightness();
            BlockPosition blockposition = new BlockPosition(this.getX(), this.getEyeY(), this.getZ());
            boolean flag = this.isInWaterRainOrBubble() || this.isInPowderSnow || this.wasInPowderSnow;

            if (f > 0.5F && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && !flag && this.level.canSeeSky(blockposition)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void jumpInLiquid(Tag<FluidType> tag) {
        if (this.getNavigation().canFloat()) {
            super.jumpInLiquid(tag);
        } else {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.3D, 0.0D));
        }

    }

    public void removeFreeWill() {
        this.goalSelector.removeAllGoals();
        this.getBrain().removeAllBehaviors();
    }

    @Override
    protected void removeAfterChangingDimensions() {
        super.removeAfterChangingDimensions();
        this.dropLeash(true, false);
        this.getAllSlots().forEach((itemstack) -> {
            itemstack.setCount(0);
        });
    }

    @Nullable
    @Override
    public ItemStack getPickResult() {
        ItemMonsterEgg itemmonsteregg = ItemMonsterEgg.byId(this.getType());

        return itemmonsteregg == null ? null : new ItemStack(itemmonsteregg);
    }
}
