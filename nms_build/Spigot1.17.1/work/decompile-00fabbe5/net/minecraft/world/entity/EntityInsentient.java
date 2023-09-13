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
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.loot.LootTableInfo;

public abstract class EntityInsentient extends EntityLiving {

    private static final DataWatcherObject<Byte> DATA_MOB_FLAGS_ID = DataWatcher.a(EntityInsentient.class, DataWatcherRegistry.BYTE);
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
    public int ambientSoundTime;
    protected int xpReward;
    protected ControllerLook lookControl;
    protected ControllerMove moveControl;
    protected ControllerJump jumpControl;
    private final EntityAIBodyControl bodyRotationControl;
    protected NavigationAbstract navigation;
    public PathfinderGoalSelector goalSelector;
    public PathfinderGoalSelector targetSelector;
    private EntityLiving target;
    private final EntitySenses sensing;
    private final NonNullList<ItemStack> handItems;
    public final float[] handDropChances;
    private final NonNullList<ItemStack> armorItems;
    public final float[] armorDropChances;
    private boolean canPickUpLoot;
    public boolean persistenceRequired;
    private final Map<PathType, Float> pathfindingMalus;
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
        this.handItems = NonNullList.a(2, ItemStack.EMPTY);
        this.handDropChances = new float[2];
        this.armorItems = NonNullList.a(4, ItemStack.EMPTY);
        this.armorDropChances = new float[4];
        this.pathfindingMalus = Maps.newEnumMap(PathType.class);
        this.restrictCenter = BlockPosition.ZERO;
        this.restrictRadius = -1.0F;
        this.goalSelector = new PathfinderGoalSelector(world.getMethodProfilerSupplier());
        this.targetSelector = new PathfinderGoalSelector(world.getMethodProfilerSupplier());
        this.lookControl = new ControllerLook(this);
        this.moveControl = new ControllerMove(this);
        this.jumpControl = new ControllerJump(this);
        this.bodyRotationControl = this.z();
        this.navigation = this.a(world);
        this.sensing = new EntitySenses(this);
        Arrays.fill(this.armorDropChances, 0.085F);
        Arrays.fill(this.handDropChances, 0.085F);
        if (world != null && !world.isClientSide) {
            this.initPathfinder();
        }

    }

    protected void initPathfinder() {}

    public static AttributeProvider.Builder w() {
        return EntityLiving.dq().a(GenericAttributes.FOLLOW_RANGE, 16.0D).a(GenericAttributes.ATTACK_KNOCKBACK);
    }

    protected NavigationAbstract a(World world) {
        return new Navigation(this, world);
    }

    protected boolean x() {
        return false;
    }

    public float a(PathType pathtype) {
        EntityInsentient entityinsentient;

        if (this.getVehicle() instanceof EntityInsentient && ((EntityInsentient) this.getVehicle()).x()) {
            entityinsentient = (EntityInsentient) this.getVehicle();
        } else {
            entityinsentient = this;
        }

        Float ofloat = (Float) entityinsentient.pathfindingMalus.get(pathtype);

        return ofloat == null ? pathtype.a() : ofloat;
    }

    public void a(PathType pathtype, float f) {
        this.pathfindingMalus.put(pathtype, f);
    }

    public boolean b(PathType pathtype) {
        return pathtype != PathType.DANGER_FIRE && pathtype != PathType.DANGER_CACTUS && pathtype != PathType.DANGER_OTHER && pathtype != PathType.WALKABLE_DOOR;
    }

    protected EntityAIBodyControl z() {
        return new EntityAIBodyControl(this);
    }

    public ControllerLook getControllerLook() {
        return this.lookControl;
    }

    public ControllerMove getControllerMove() {
        if (this.isPassenger() && this.getVehicle() instanceof EntityInsentient) {
            EntityInsentient entityinsentient = (EntityInsentient) this.getVehicle();

            return entityinsentient.getControllerMove();
        } else {
            return this.moveControl;
        }
    }

    public ControllerJump getControllerJump() {
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

    public EntitySenses getEntitySenses() {
        return this.sensing;
    }

    @Nullable
    public EntityLiving getGoalTarget() {
        return this.target;
    }

    public void setGoalTarget(@Nullable EntityLiving entityliving) {
        this.target = entityliving;
    }

    @Override
    public boolean a(EntityTypes<?> entitytypes) {
        return entitytypes != EntityTypes.GHAST;
    }

    public boolean a(ItemProjectileWeapon itemprojectileweapon) {
        return false;
    }

    public void blockEaten() {}

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityInsentient.DATA_MOB_FLAGS_ID, (byte) 0);
    }

    public int J() {
        return 80;
    }

    public void K() {
        SoundEffect soundeffect = this.getSoundAmbient();

        if (soundeffect != null) {
            this.playSound(soundeffect, this.getSoundVolume(), this.ep());
        }

    }

    @Override
    public void entityBaseTick() {
        super.entityBaseTick();
        this.level.getMethodProfiler().enter("mobBaseTick");
        if (this.isAlive() && this.random.nextInt(1000) < this.ambientSoundTime++) {
            this.n();
            this.K();
        }

        this.level.getMethodProfiler().exit();
    }

    @Override
    protected void d(DamageSource damagesource) {
        this.n();
        super.d(damagesource);
    }

    private void n() {
        this.ambientSoundTime = -this.J();
    }

    @Override
    protected int getExpValue(EntityHuman entityhuman) {
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

    public void doSpawnEffect() {
        if (this.level.isClientSide) {
            for (int i = 0; i < 20; ++i) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;
                double d3 = 10.0D;

                this.level.addParticle(Particles.POOF, this.c(1.0D) - d0 * 10.0D, this.da() - d1 * 10.0D, this.g(1.0D) - d2 * 10.0D, d0, d1, d2);
            }
        } else {
            this.level.broadcastEntityEffect(this, (byte) 20);
        }

    }

    @Override
    public void a(byte b0) {
        if (b0 == 20) {
            this.doSpawnEffect();
        } else {
            super.a(b0);
        }

    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            this.fm();
            if (this.tickCount % 5 == 0) {
                this.M();
            }
        }

    }

    protected void M() {
        boolean flag = !(this.getRidingPassenger() instanceof EntityInsentient);
        boolean flag1 = !(this.getVehicle() instanceof EntityBoat);

        this.goalSelector.a(PathfinderGoal.Type.MOVE, flag);
        this.goalSelector.a(PathfinderGoal.Type.JUMP, flag && flag1);
        this.goalSelector.a(PathfinderGoal.Type.LOOK, flag);
    }

    @Override
    protected float e(float f, float f1) {
        this.bodyRotationControl.a();
        return f1;
    }

    @Nullable
    protected SoundEffect getSoundAmbient() {
        return null;
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setBoolean("CanPickUpLoot", this.canPickupLoot());
        nbttagcompound.setBoolean("PersistenceRequired", this.persistenceRequired);
        NBTTagList nbttaglist = new NBTTagList();

        NBTTagCompound nbttagcompound1;

        for (Iterator iterator = this.armorItems.iterator(); iterator.hasNext(); nbttaglist.add(nbttagcompound1)) {
            ItemStack itemstack = (ItemStack) iterator.next();

            nbttagcompound1 = new NBTTagCompound();
            if (!itemstack.isEmpty()) {
                itemstack.save(nbttagcompound1);
            }
        }

        nbttagcompound.set("ArmorItems", nbttaglist);
        NBTTagList nbttaglist1 = new NBTTagList();

        NBTTagCompound nbttagcompound2;

        for (Iterator iterator1 = this.handItems.iterator(); iterator1.hasNext(); nbttaglist1.add(nbttagcompound2)) {
            ItemStack itemstack1 = (ItemStack) iterator1.next();

            nbttagcompound2 = new NBTTagCompound();
            if (!itemstack1.isEmpty()) {
                itemstack1.save(nbttagcompound2);
            }
        }

        nbttagcompound.set("HandItems", nbttaglist1);
        NBTTagList nbttaglist2 = new NBTTagList();
        float[] afloat = this.armorDropChances;
        int i = afloat.length;

        int j;

        for (j = 0; j < i; ++j) {
            float f = afloat[j];

            nbttaglist2.add(NBTTagFloat.a(f));
        }

        nbttagcompound.set("ArmorDropChances", nbttaglist2);
        NBTTagList nbttaglist3 = new NBTTagList();
        float[] afloat1 = this.handDropChances;

        j = afloat1.length;

        for (int k = 0; k < j; ++k) {
            float f1 = afloat1[k];

            nbttaglist3.add(NBTTagFloat.a(f1));
        }

        nbttagcompound.set("HandDropChances", nbttaglist3);
        if (this.leashHolder != null) {
            nbttagcompound2 = new NBTTagCompound();
            if (this.leashHolder instanceof EntityLiving) {
                UUID uuid = this.leashHolder.getUniqueID();

                nbttagcompound2.a("UUID", uuid);
            } else if (this.leashHolder instanceof EntityHanging) {
                BlockPosition blockposition = ((EntityHanging) this.leashHolder).getBlockPosition();

                nbttagcompound2.setInt("X", blockposition.getX());
                nbttagcompound2.setInt("Y", blockposition.getY());
                nbttagcompound2.setInt("Z", blockposition.getZ());
            }

            nbttagcompound.set("Leash", nbttagcompound2);
        } else if (this.leashInfoTag != null) {
            nbttagcompound.set("Leash", this.leashInfoTag.clone());
        }

        nbttagcompound.setBoolean("LeftHanded", this.isLeftHanded());
        if (this.lootTable != null) {
            nbttagcompound.setString("DeathLootTable", this.lootTable.toString());
            if (this.lootTableSeed != 0L) {
                nbttagcompound.setLong("DeathLootTableSeed", this.lootTableSeed);
            }
        }

        if (this.isNoAI()) {
            nbttagcompound.setBoolean("NoAI", this.isNoAI());
        }

    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("CanPickUpLoot", 1)) {
            this.setCanPickupLoot(nbttagcompound.getBoolean("CanPickUpLoot"));
        }

        this.persistenceRequired = nbttagcompound.getBoolean("PersistenceRequired");
        NBTTagList nbttaglist;
        int i;

        if (nbttagcompound.hasKeyOfType("ArmorItems", 9)) {
            nbttaglist = nbttagcompound.getList("ArmorItems", 10);

            for (i = 0; i < this.armorItems.size(); ++i) {
                this.armorItems.set(i, ItemStack.a(nbttaglist.getCompound(i)));
            }
        }

        if (nbttagcompound.hasKeyOfType("HandItems", 9)) {
            nbttaglist = nbttagcompound.getList("HandItems", 10);

            for (i = 0; i < this.handItems.size(); ++i) {
                this.handItems.set(i, ItemStack.a(nbttaglist.getCompound(i)));
            }
        }

        if (nbttagcompound.hasKeyOfType("ArmorDropChances", 9)) {
            nbttaglist = nbttagcompound.getList("ArmorDropChances", 5);

            for (i = 0; i < nbttaglist.size(); ++i) {
                this.armorDropChances[i] = nbttaglist.i(i);
            }
        }

        if (nbttagcompound.hasKeyOfType("HandDropChances", 9)) {
            nbttaglist = nbttagcompound.getList("HandDropChances", 5);

            for (i = 0; i < nbttaglist.size(); ++i) {
                this.handDropChances[i] = nbttaglist.i(i);
            }
        }

        if (nbttagcompound.hasKeyOfType("Leash", 10)) {
            this.leashInfoTag = nbttagcompound.getCompound("Leash");
        }

        this.setLeftHanded(nbttagcompound.getBoolean("LeftHanded"));
        if (nbttagcompound.hasKeyOfType("DeathLootTable", 8)) {
            this.lootTable = new MinecraftKey(nbttagcompound.getString("DeathLootTable"));
            this.lootTableSeed = nbttagcompound.getLong("DeathLootTableSeed");
        }

        this.setNoAI(nbttagcompound.getBoolean("NoAI"));
    }

    @Override
    protected void a(DamageSource damagesource, boolean flag) {
        super.a(damagesource, flag);
        this.lootTable = null;
    }

    @Override
    protected LootTableInfo.Builder a(boolean flag, DamageSource damagesource) {
        return super.a(flag, damagesource).a(this.lootTableSeed, this.random);
    }

    @Override
    public final MinecraftKey dZ() {
        return this.lootTable == null ? this.getDefaultLootTable() : this.lootTable;
    }

    public MinecraftKey getDefaultLootTable() {
        return super.dZ();
    }

    public void u(float f) {
        this.zza = f;
    }

    public void v(float f) {
        this.yya = f;
    }

    public void w(float f) {
        this.xxa = f;
    }

    @Override
    public void r(float f) {
        super.r(f);
        this.u(f);
    }

    @Override
    public void movementTick() {
        super.movementTick();
        this.level.getMethodProfiler().enter("looting");
        if (!this.level.isClientSide && this.canPickupLoot() && this.isAlive() && !this.dead && this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            List<EntityItem> list = this.level.a(EntityItem.class, this.getBoundingBox().grow(1.0D, 0.0D, 1.0D));
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityItem entityitem = (EntityItem) iterator.next();

                if (!entityitem.isRemoved() && !entityitem.getItemStack().isEmpty() && !entityitem.q() && this.l(entityitem.getItemStack())) {
                    this.b(entityitem);
                }
            }
        }

        this.level.getMethodProfiler().exit();
    }

    protected void b(EntityItem entityitem) {
        ItemStack itemstack = entityitem.getItemStack();

        if (this.j(itemstack)) {
            this.a(entityitem);
            this.receive(entityitem, itemstack.getCount());
            entityitem.die();
        }

    }

    public boolean j(ItemStack itemstack) {
        EnumItemSlot enumitemslot = getEquipmentSlotForItem(itemstack);
        ItemStack itemstack1 = this.getEquipment(enumitemslot);
        boolean flag = this.a(itemstack, itemstack1);

        if (flag && this.canPickup(itemstack)) {
            double d0 = (double) this.e(enumitemslot);

            if (!itemstack1.isEmpty() && (double) Math.max(this.random.nextFloat() - 0.1F, 0.0F) < d0) {
                this.b(itemstack1);
            }

            this.b(enumitemslot, itemstack);
            this.playEquipSound(itemstack);
            return true;
        } else {
            return false;
        }
    }

    protected void b(EnumItemSlot enumitemslot, ItemStack itemstack) {
        this.setSlot(enumitemslot, itemstack);
        this.d(enumitemslot);
        this.persistenceRequired = true;
    }

    public void d(EnumItemSlot enumitemslot) {
        switch (enumitemslot.a()) {
            case HAND:
                this.handDropChances[enumitemslot.b()] = 2.0F;
                break;
            case ARMOR:
                this.armorDropChances[enumitemslot.b()] = 2.0F;
        }

    }

    protected boolean a(ItemStack itemstack, ItemStack itemstack1) {
        if (itemstack1.isEmpty()) {
            return true;
        } else if (itemstack.getItem() instanceof ItemSword) {
            if (!(itemstack1.getItem() instanceof ItemSword)) {
                return true;
            } else {
                ItemSword itemsword = (ItemSword) itemstack.getItem();
                ItemSword itemsword1 = (ItemSword) itemstack1.getItem();

                return itemsword.i() != itemsword1.i() ? itemsword.i() > itemsword1.i() : this.b(itemstack, itemstack1);
            }
        } else if (itemstack.getItem() instanceof ItemBow && itemstack1.getItem() instanceof ItemBow) {
            return this.b(itemstack, itemstack1);
        } else if (itemstack.getItem() instanceof ItemCrossbow && itemstack1.getItem() instanceof ItemCrossbow) {
            return this.b(itemstack, itemstack1);
        } else if (itemstack.getItem() instanceof ItemArmor) {
            if (EnchantmentManager.d(itemstack1)) {
                return false;
            } else if (!(itemstack1.getItem() instanceof ItemArmor)) {
                return true;
            } else {
                ItemArmor itemarmor = (ItemArmor) itemstack.getItem();
                ItemArmor itemarmor1 = (ItemArmor) itemstack1.getItem();

                return itemarmor.e() != itemarmor1.e() ? itemarmor.e() > itemarmor1.e() : (itemarmor.f() != itemarmor1.f() ? itemarmor.f() > itemarmor1.f() : this.b(itemstack, itemstack1));
            }
        } else {
            if (itemstack.getItem() instanceof ItemTool) {
                if (itemstack1.getItem() instanceof ItemBlock) {
                    return true;
                }

                if (itemstack1.getItem() instanceof ItemTool) {
                    ItemTool itemtool = (ItemTool) itemstack.getItem();
                    ItemTool itemtool1 = (ItemTool) itemstack1.getItem();

                    if (itemtool.d() != itemtool1.d()) {
                        return itemtool.d() > itemtool1.d();
                    }

                    return this.b(itemstack, itemstack1);
                }
            }

            return false;
        }
    }

    public boolean b(ItemStack itemstack, ItemStack itemstack1) {
        return itemstack.getDamage() >= itemstack1.getDamage() && (!itemstack.hasTag() || itemstack1.hasTag()) ? (itemstack.hasTag() && itemstack1.hasTag() ? itemstack.getTag().getKeys().stream().anyMatch((s) -> {
            return !s.equals("Damage");
        }) && !itemstack1.getTag().getKeys().stream().anyMatch((s) -> {
            return !s.equals("Damage");
        }) : false) : true;
    }

    public boolean canPickup(ItemStack itemstack) {
        return true;
    }

    public boolean l(ItemStack itemstack) {
        return this.canPickup(itemstack);
    }

    public boolean isTypeNotPersistent(double d0) {
        return true;
    }

    public boolean isSpecialPersistence() {
        return this.isPassenger();
    }

    protected boolean Q() {
        return false;
    }

    @Override
    public void checkDespawn() {
        if (this.level.getDifficulty() == EnumDifficulty.PEACEFUL && this.Q()) {
            this.die();
        } else if (!this.isPersistent() && !this.isSpecialPersistence()) {
            EntityHuman entityhuman = this.level.findNearbyPlayer(this, -1.0D);

            if (entityhuman != null) {
                double d0 = entityhuman.f(this);
                int i = this.getEntityType().f().f();
                int j = i * i;

                if (d0 > (double) j && this.isTypeNotPersistent(d0)) {
                    this.die();
                }

                int k = this.getEntityType().f().g();
                int l = k * k;

                if (this.noActionTime > 600 && this.random.nextInt(800) == 0 && d0 > (double) l && this.isTypeNotPersistent(d0)) {
                    this.die();
                } else if (d0 < (double) l) {
                    this.noActionTime = 0;
                }
            }

        } else {
            this.noActionTime = 0;
        }
    }

    @Override
    protected final void doTick() {
        ++this.noActionTime;
        this.level.getMethodProfiler().enter("sensing");
        this.sensing.a();
        this.level.getMethodProfiler().exit();
        this.level.getMethodProfiler().enter("targetSelector");
        this.targetSelector.doTick();
        this.level.getMethodProfiler().exit();
        this.level.getMethodProfiler().enter("goalSelector");
        this.goalSelector.doTick();
        this.level.getMethodProfiler().exit();
        this.level.getMethodProfiler().enter("navigation");
        this.navigation.c();
        this.level.getMethodProfiler().exit();
        this.level.getMethodProfiler().enter("mob tick");
        this.mobTick();
        this.level.getMethodProfiler().exit();
        this.level.getMethodProfiler().enter("controls");
        this.level.getMethodProfiler().enter("move");
        this.moveControl.a();
        this.level.getMethodProfiler().exitEnter("look");
        this.lookControl.a();
        this.level.getMethodProfiler().exitEnter("jump");
        this.jumpControl.b();
        this.level.getMethodProfiler().exit();
        this.level.getMethodProfiler().exit();
        this.R();
    }

    protected void R() {
        PacketDebug.a(this.level, this, this.goalSelector);
    }

    protected void mobTick() {}

    public int eZ() {
        return 40;
    }

    public int fa() {
        return 75;
    }

    public int fb() {
        return 10;
    }

    public void a(Entity entity, float f, float f1) {
        double d0 = entity.locX() - this.locX();
        double d1 = entity.locZ() - this.locZ();
        double d2;

        if (entity instanceof EntityLiving) {
            EntityLiving entityliving = (EntityLiving) entity;

            d2 = entityliving.getHeadY() - this.getHeadY();
        } else {
            d2 = (entity.getBoundingBox().minY + entity.getBoundingBox().maxY) / 2.0D - this.getHeadY();
        }

        double d3 = Math.sqrt(d0 * d0 + d1 * d1);
        float f2 = (float) (MathHelper.d(d1, d0) * 57.2957763671875D) - 90.0F;
        float f3 = (float) (-(MathHelper.d(d2, d3) * 57.2957763671875D));

        this.setXRot(this.a(this.getXRot(), f3, f1));
        this.setYRot(this.a(this.getYRot(), f2, f));
    }

    private float a(float f, float f1, float f2) {
        float f3 = MathHelper.g(f1 - f);

        if (f3 > f2) {
            f3 = f2;
        }

        if (f3 < -f2) {
            f3 = -f2;
        }

        return f + f3;
    }

    public static boolean a(EntityTypes<? extends EntityInsentient> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        BlockPosition blockposition1 = blockposition.down();

        return enummobspawn == EnumMobSpawn.SPAWNER || generatoraccess.getType(blockposition1).a((IBlockAccess) generatoraccess, blockposition1, entitytypes);
    }

    public boolean a(GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn) {
        return true;
    }

    public boolean a(IWorldReader iworldreader) {
        return !iworldreader.containsLiquid(this.getBoundingBox()) && iworldreader.f((Entity) this);
    }

    public int getMaxSpawnGroup() {
        return 4;
    }

    public boolean c(int i) {
        return false;
    }

    @Override
    public int ce() {
        if (this.getGoalTarget() == null) {
            return 3;
        } else {
            int i = (int) (this.getHealth() - this.getMaxHealth() * 0.33F);

            i -= (3 - this.level.getDifficulty().a()) * 4;
            if (i < 0) {
                i = 0;
            }

            return i + 3;
        }
    }

    @Override
    public Iterable<ItemStack> bw() {
        return this.handItems;
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return this.armorItems;
    }

    @Override
    public ItemStack getEquipment(EnumItemSlot enumitemslot) {
        switch (enumitemslot.a()) {
            case HAND:
                return (ItemStack) this.handItems.get(enumitemslot.b());
            case ARMOR:
                return (ItemStack) this.armorItems.get(enumitemslot.b());
            default:
                return ItemStack.EMPTY;
        }
    }

    @Override
    public void setSlot(EnumItemSlot enumitemslot, ItemStack itemstack) {
        this.f(itemstack);
        switch (enumitemslot.a()) {
            case HAND:
                this.handItems.set(enumitemslot.b(), itemstack);
                break;
            case ARMOR:
                this.armorItems.set(enumitemslot.b(), itemstack);
        }

    }

    @Override
    protected void dropDeathLoot(DamageSource damagesource, int i, boolean flag) {
        super.dropDeathLoot(damagesource, i, flag);
        EnumItemSlot[] aenumitemslot = EnumItemSlot.values();
        int j = aenumitemslot.length;

        for (int k = 0; k < j; ++k) {
            EnumItemSlot enumitemslot = aenumitemslot[k];
            ItemStack itemstack = this.getEquipment(enumitemslot);
            float f = this.e(enumitemslot);
            boolean flag1 = f > 1.0F;

            if (!itemstack.isEmpty() && !EnchantmentManager.shouldNotDrop(itemstack) && (flag || flag1) && Math.max(this.random.nextFloat() - (float) i * 0.01F, 0.0F) < f) {
                if (!flag1 && itemstack.f()) {
                    itemstack.setDamage(itemstack.i() - this.random.nextInt(1 + this.random.nextInt(Math.max(itemstack.i() - 3, 1))));
                }

                this.b(itemstack);
                this.setSlot(enumitemslot, ItemStack.EMPTY);
            }
        }

    }

    protected float e(EnumItemSlot enumitemslot) {
        float f;

        switch (enumitemslot.a()) {
            case HAND:
                f = this.handDropChances[enumitemslot.b()];
                break;
            case ARMOR:
                f = this.armorDropChances[enumitemslot.b()];
                break;
            default:
                f = 0.0F;
        }

        return f;
    }

    protected void a(DifficultyDamageScaler difficultydamagescaler) {
        if (this.random.nextFloat() < 0.15F * difficultydamagescaler.d()) {
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

                if (enumitemslot.a() == EnumItemSlot.Function.ARMOR) {
                    ItemStack itemstack = this.getEquipment(enumitemslot);

                    if (!flag && this.random.nextFloat() < f) {
                        break;
                    }

                    flag = false;
                    if (itemstack.isEmpty()) {
                        Item item = a(enumitemslot, i);

                        if (item != null) {
                            this.setSlot(enumitemslot, new ItemStack(item));
                        }
                    }
                }
            }
        }

    }

    @Nullable
    public static Item a(EnumItemSlot enumitemslot, int i) {
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

    protected void b(DifficultyDamageScaler difficultydamagescaler) {
        float f = difficultydamagescaler.d();

        this.x(f);
        EnumItemSlot[] aenumitemslot = EnumItemSlot.values();
        int i = aenumitemslot.length;

        for (int j = 0; j < i; ++j) {
            EnumItemSlot enumitemslot = aenumitemslot[j];

            if (enumitemslot.a() == EnumItemSlot.Function.ARMOR) {
                this.a(f, enumitemslot);
            }
        }

    }

    protected void x(float f) {
        if (!this.getItemInMainHand().isEmpty() && this.random.nextFloat() < 0.25F * f) {
            this.setSlot(EnumItemSlot.MAINHAND, EnchantmentManager.a(this.random, this.getItemInMainHand(), (int) (5.0F + f * (float) this.random.nextInt(18)), false));
        }

    }

    protected void a(float f, EnumItemSlot enumitemslot) {
        ItemStack itemstack = this.getEquipment(enumitemslot);

        if (!itemstack.isEmpty() && this.random.nextFloat() < 0.5F * f) {
            this.setSlot(enumitemslot, EnchantmentManager.a(this.random, itemstack, (int) (5.0F + f * (float) this.random.nextInt(18)), false));
        }

    }

    @Nullable
    public GroupDataEntity prepare(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).addModifier(new AttributeModifier("Random spawn bonus", this.random.nextGaussian() * 0.05D, AttributeModifier.Operation.MULTIPLY_BASE));
        if (this.random.nextFloat() < 0.05F) {
            this.setLeftHanded(true);
        } else {
            this.setLeftHanded(false);
        }

        return groupdataentity;
    }

    public boolean fd() {
        return false;
    }

    public void setPersistent() {
        this.persistenceRequired = true;
    }

    public void a(EnumItemSlot enumitemslot, float f) {
        switch (enumitemslot.a()) {
            case HAND:
                this.handDropChances[enumitemslot.b()] = f;
                break;
            case ARMOR:
                this.armorDropChances[enumitemslot.b()] = f;
        }

    }

    public boolean canPickupLoot() {
        return this.canPickUpLoot;
    }

    public void setCanPickupLoot(boolean flag) {
        this.canPickUpLoot = flag;
    }

    @Override
    public boolean g(ItemStack itemstack) {
        EnumItemSlot enumitemslot = getEquipmentSlotForItem(itemstack);

        return this.getEquipment(enumitemslot).isEmpty() && this.canPickupLoot();
    }

    public boolean isPersistent() {
        return this.persistenceRequired;
    }

    @Override
    public final EnumInteractionResult a(EntityHuman entityhuman, EnumHand enumhand) {
        if (!this.isAlive()) {
            return EnumInteractionResult.PASS;
        } else if (this.getLeashHolder() == entityhuman) {
            this.unleash(true, !entityhuman.getAbilities().instabuild);
            return EnumInteractionResult.a(this.level.isClientSide);
        } else {
            EnumInteractionResult enuminteractionresult = this.c(entityhuman, enumhand);

            if (enuminteractionresult.a()) {
                return enuminteractionresult;
            } else {
                enuminteractionresult = this.b(entityhuman, enumhand);
                return enuminteractionresult.a() ? enuminteractionresult : super.a(entityhuman, enumhand);
            }
        }
    }

    private EnumInteractionResult c(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (itemstack.a(Items.LEAD) && this.a(entityhuman)) {
            this.setLeashHolder(entityhuman, true);
            itemstack.subtract(1);
            return EnumInteractionResult.a(this.level.isClientSide);
        } else {
            if (itemstack.a(Items.NAME_TAG)) {
                EnumInteractionResult enuminteractionresult = itemstack.a(entityhuman, (EntityLiving) this, enumhand);

                if (enuminteractionresult.a()) {
                    return enuminteractionresult;
                }
            }

            if (itemstack.getItem() instanceof ItemMonsterEgg) {
                if (this.level instanceof WorldServer) {
                    ItemMonsterEgg itemmonsteregg = (ItemMonsterEgg) itemstack.getItem();
                    Optional<EntityInsentient> optional = itemmonsteregg.a(entityhuman, this, this.getEntityType(), (WorldServer) this.level, this.getPositionVector(), itemstack);

                    optional.ifPresent((entityinsentient) -> {
                        this.a(entityhuman, entityinsentient);
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

    protected void a(EntityHuman entityhuman, EntityInsentient entityinsentient) {}

    protected EnumInteractionResult b(EntityHuman entityhuman, EnumHand enumhand) {
        return EnumInteractionResult.PASS;
    }

    public boolean fh() {
        return this.a(this.getChunkCoordinates());
    }

    public boolean a(BlockPosition blockposition) {
        return this.restrictRadius == -1.0F ? true : this.restrictCenter.j(blockposition) < (double) (this.restrictRadius * this.restrictRadius);
    }

    public void a(BlockPosition blockposition, int i) {
        this.restrictCenter = blockposition;
        this.restrictRadius = (float) i;
    }

    public BlockPosition fi() {
        return this.restrictCenter;
    }

    public float fj() {
        return this.restrictRadius;
    }

    public void fk() {
        this.restrictRadius = -1.0F;
    }

    public boolean fl() {
        return this.restrictRadius != -1.0F;
    }

    @Nullable
    public <T extends EntityInsentient> T a(EntityTypes<T> entitytypes, boolean flag) {
        if (this.isRemoved()) {
            return null;
        } else {
            T t0 = (EntityInsentient) entitytypes.a(this.level);

            t0.s(this);
            t0.setBaby(this.isBaby());
            t0.setNoAI(this.isNoAI());
            if (this.hasCustomName()) {
                t0.setCustomName(this.getCustomName());
                t0.setCustomNameVisible(this.getCustomNameVisible());
            }

            if (this.isPersistent()) {
                t0.setPersistent();
            }

            t0.setInvulnerable(this.isInvulnerable());
            if (flag) {
                t0.setCanPickupLoot(this.canPickupLoot());
                EnumItemSlot[] aenumitemslot = EnumItemSlot.values();
                int i = aenumitemslot.length;

                for (int j = 0; j < i; ++j) {
                    EnumItemSlot enumitemslot = aenumitemslot[j];
                    ItemStack itemstack = this.getEquipment(enumitemslot);

                    if (!itemstack.isEmpty()) {
                        t0.setSlot(enumitemslot, itemstack.cloneItemStack());
                        t0.a(enumitemslot, this.e(enumitemslot));
                        itemstack.setCount(0);
                    }
                }
            }

            this.level.addEntity(t0);
            if (this.isPassenger()) {
                Entity entity = this.getVehicle();

                this.stopRiding();
                t0.a(entity, true);
            }

            this.die();
            return t0;
        }
    }

    protected void fm() {
        if (this.leashInfoTag != null) {
            this.fu();
        }

        if (this.leashHolder != null) {
            if (!this.isAlive() || !this.leashHolder.isAlive()) {
                this.unleash(true, true);
            }

        }
    }

    public void unleash(boolean flag, boolean flag1) {
        if (this.leashHolder != null) {
            this.leashHolder = null;
            this.leashInfoTag = null;
            if (!this.level.isClientSide && flag1) {
                this.a((IMaterial) Items.LEAD);
            }

            if (!this.level.isClientSide && flag && this.level instanceof WorldServer) {
                ((WorldServer) this.level).getChunkProvider().broadcast(this, new PacketPlayOutAttachEntity(this, (Entity) null));
            }
        }

    }

    public boolean a(EntityHuman entityhuman) {
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

    public void setLeashHolder(Entity entity, boolean flag) {
        this.leashHolder = entity;
        this.leashInfoTag = null;
        if (!this.level.isClientSide && flag && this.level instanceof WorldServer) {
            ((WorldServer) this.level).getChunkProvider().broadcast(this, new PacketPlayOutAttachEntity(this, this.leashHolder));
        }

        if (this.isPassenger()) {
            this.stopRiding();
        }

    }

    public void d(int i) {
        this.delayedLeashHolderId = i;
        this.unleash(false, false);
    }

    @Override
    public boolean a(Entity entity, boolean flag) {
        boolean flag1 = super.a(entity, flag);

        if (flag1 && this.isLeashed()) {
            this.unleash(true, true);
        }

        return flag1;
    }

    private void fu() {
        if (this.leashInfoTag != null && this.level instanceof WorldServer) {
            if (this.leashInfoTag.b("UUID")) {
                UUID uuid = this.leashInfoTag.a("UUID");
                Entity entity = ((WorldServer) this.level).getEntity(uuid);

                if (entity != null) {
                    this.setLeashHolder(entity, true);
                    return;
                }
            } else if (this.leashInfoTag.hasKeyOfType("X", 99) && this.leashInfoTag.hasKeyOfType("Y", 99) && this.leashInfoTag.hasKeyOfType("Z", 99)) {
                BlockPosition blockposition = new BlockPosition(this.leashInfoTag.getInt("X"), this.leashInfoTag.getInt("Y"), this.leashInfoTag.getInt("Z"));

                this.setLeashHolder(EntityLeash.b(this.level, blockposition), true);
                return;
            }

            if (this.tickCount > 100) {
                this.a((IMaterial) Items.LEAD);
                this.leashInfoTag = null;
            }
        }

    }

    @Override
    public boolean cH() {
        return this.fd() && super.cH();
    }

    @Override
    public boolean doAITick() {
        return super.doAITick() && !this.isNoAI();
    }

    public void setNoAI(boolean flag) {
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

    public boolean isNoAI() {
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
    public EnumMainHand getMainHand() {
        return this.isLeftHanded() ? EnumMainHand.LEFT : EnumMainHand.RIGHT;
    }

    public double i(EntityLiving entityliving) {
        return (double) (this.getWidth() * 2.0F * this.getWidth() * 2.0F + entityliving.getWidth());
    }

    @Override
    public boolean attackEntity(Entity entity) {
        float f = (float) this.b(GenericAttributes.ATTACK_DAMAGE);
        float f1 = (float) this.b(GenericAttributes.ATTACK_KNOCKBACK);

        if (entity instanceof EntityLiving) {
            f += EnchantmentManager.a(this.getItemInMainHand(), ((EntityLiving) entity).getMonsterType());
            f1 += (float) EnchantmentManager.b((EntityLiving) this);
        }

        int i = EnchantmentManager.getFireAspectEnchantmentLevel(this);

        if (i > 0) {
            entity.setOnFire(i * 4);
        }

        boolean flag = entity.damageEntity(DamageSource.mobAttack(this), f);

        if (flag) {
            if (f1 > 0.0F && entity instanceof EntityLiving) {
                ((EntityLiving) entity).p((double) (f1 * 0.5F), (double) MathHelper.sin(this.getYRot() * 0.017453292F), (double) (-MathHelper.cos(this.getYRot() * 0.017453292F)));
                this.setMot(this.getMot().d(0.6D, 1.0D, 0.6D));
            }

            if (entity instanceof EntityHuman) {
                EntityHuman entityhuman = (EntityHuman) entity;

                this.a(entityhuman, this.getItemInMainHand(), entityhuman.isHandRaised() ? entityhuman.getActiveItem() : ItemStack.EMPTY);
            }

            this.a((EntityLiving) this, entity);
            this.x(entity);
        }

        return flag;
    }

    private void a(EntityHuman entityhuman, ItemStack itemstack, ItemStack itemstack1) {
        if (!itemstack.isEmpty() && !itemstack1.isEmpty() && itemstack.getItem() instanceof ItemAxe && itemstack1.a(Items.SHIELD)) {
            float f = 0.25F + (float) EnchantmentManager.getDigSpeedEnchantmentLevel(this) * 0.05F;

            if (this.random.nextFloat() < f) {
                entityhuman.getCooldownTracker().setCooldown(Items.SHIELD, 100);
                this.level.broadcastEntityEffect(entityhuman, (byte) 30);
            }
        }

    }

    protected boolean fs() {
        if (this.level.isDay() && !this.level.isClientSide) {
            float f = this.aY();
            BlockPosition blockposition = new BlockPosition(this.locX(), this.getHeadY(), this.locZ());
            boolean flag = this.aN() || this.isInPowderSnow || this.wasInPowderSnow;

            if (f > 0.5F && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && !flag && this.level.g(blockposition)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void c(Tag<FluidType> tag) {
        if (this.getNavigation().r()) {
            super.c(tag);
        } else {
            this.setMot(this.getMot().add(0.0D, 0.3D, 0.0D));
        }

    }

    public void ft() {
        this.goalSelector.a();
        this.getBehaviorController().g();
    }

    @Override
    protected void cc() {
        super.cc();
        this.unleash(true, false);
        this.by().forEach((itemstack) -> {
            itemstack.setCount(0);
        });
    }

    @Nullable
    @Override
    public ItemStack df() {
        ItemMonsterEgg itemmonsteregg = ItemMonsterEgg.a(this.getEntityType());

        return itemmonsteregg == null ? null : new ItemStack(itemmonsteregg);
    }
}
