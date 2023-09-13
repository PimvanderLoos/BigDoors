package net.minecraft.server;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

public abstract class EntityHuman extends EntityLiving {

    private static final DataWatcherObject<Float> a = DataWatcher.a(EntityHuman.class, DataWatcherRegistry.c);
    private static final DataWatcherObject<Integer> b = DataWatcher.a(EntityHuman.class, DataWatcherRegistry.b);
    protected static final DataWatcherObject<Byte> br = DataWatcher.a(EntityHuman.class, DataWatcherRegistry.a);
    protected static final DataWatcherObject<Byte> bs = DataWatcher.a(EntityHuman.class, DataWatcherRegistry.a);
    protected static final DataWatcherObject<NBTTagCompound> bt = DataWatcher.a(EntityHuman.class, DataWatcherRegistry.n);
    protected static final DataWatcherObject<NBTTagCompound> bu = DataWatcher.a(EntityHuman.class, DataWatcherRegistry.n);
    public PlayerInventory inventory = new PlayerInventory(this);
    protected InventoryEnderChest enderChest = new InventoryEnderChest();
    public Container defaultContainer;
    public Container activeContainer;
    protected FoodMetaData foodData = new FoodMetaData();
    protected int bA;
    public float bB;
    public float bC;
    public int bD;
    public double bE;
    public double bF;
    public double bG;
    public double bH;
    public double bI;
    public double bJ;
    public boolean sleeping;
    public BlockPosition bedPosition;
    public int sleepTicks;
    public float bM;
    public float bN;
    private BlockPosition d;
    private boolean e;
    public PlayerAbilities abilities = new PlayerAbilities();
    public int expLevel;
    public int expTotal;
    public float exp;
    protected int bS;
    protected float bT = 0.02F;
    private int f;
    private final GameProfile g;
    private ItemStack bV;
    private final ItemCooldown bW;
    @Nullable
    public EntityFishingHook hookedFish;

    protected ItemCooldown l() {
        return new ItemCooldown();
    }

    public EntityHuman(World world, GameProfile gameprofile) {
        super(world);
        this.bV = ItemStack.a;
        this.bW = this.l();
        this.a(a(gameprofile));
        this.g = gameprofile;
        this.defaultContainer = new ContainerPlayer(this.inventory, !world.isClientSide, this);
        this.activeContainer = this.defaultContainer;
        BlockPosition blockposition = world.getSpawn();

        this.setPositionRotation((double) blockposition.getX() + 0.5D, (double) (blockposition.getY() + 1), (double) blockposition.getZ() + 0.5D, 0.0F, 0.0F);
        this.ba = 180.0F;
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeMap().b(GenericAttributes.ATTACK_DAMAGE).setValue(1.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.10000000149011612D);
        this.getAttributeMap().b(GenericAttributes.g);
        this.getAttributeMap().b(GenericAttributes.j);
    }

    protected void i() {
        super.i();
        this.datawatcher.register(EntityHuman.a, Float.valueOf(0.0F));
        this.datawatcher.register(EntityHuman.b, Integer.valueOf(0));
        this.datawatcher.register(EntityHuman.br, Byte.valueOf((byte) 0));
        this.datawatcher.register(EntityHuman.bs, Byte.valueOf((byte) 1));
        this.datawatcher.register(EntityHuman.bt, new NBTTagCompound());
        this.datawatcher.register(EntityHuman.bu, new NBTTagCompound());
    }

    public void B_() {
        this.noclip = this.isSpectator();
        if (this.isSpectator()) {
            this.onGround = false;
        }

        if (this.bD > 0) {
            --this.bD;
        }

        if (this.isSleeping()) {
            ++this.sleepTicks;
            if (this.sleepTicks > 100) {
                this.sleepTicks = 100;
            }

            if (!this.world.isClientSide) {
                if (!this.s()) {
                    this.a(true, true, false);
                } else if (this.world.D()) {
                    this.a(false, true, true);
                }
            }
        } else if (this.sleepTicks > 0) {
            ++this.sleepTicks;
            if (this.sleepTicks >= 110) {
                this.sleepTicks = 0;
            }
        }

        super.B_();
        if (!this.world.isClientSide && this.activeContainer != null && !this.activeContainer.canUse(this)) {
            this.closeInventory();
            this.activeContainer = this.defaultContainer;
        }

        if (this.isBurning() && this.abilities.isInvulnerable) {
            this.extinguish();
        }

        this.r();
        if (!this.world.isClientSide) {
            this.foodData.a(this);
            this.b(StatisticList.g);
            if (this.isAlive()) {
                this.b(StatisticList.h);
            }

            if (this.isSneaking()) {
                this.b(StatisticList.i);
            }
        }

        int i = 29999999;
        double d0 = MathHelper.a(this.locX, -2.9999999E7D, 2.9999999E7D);
        double d1 = MathHelper.a(this.locZ, -2.9999999E7D, 2.9999999E7D);

        if (d0 != this.locX || d1 != this.locZ) {
            this.setPosition(d0, this.locY, d1);
        }

        ++this.aE;
        ItemStack itemstack = this.getItemInMainHand();

        if (!ItemStack.matches(this.bV, itemstack)) {
            if (!ItemStack.d(this.bV, itemstack)) {
                this.ds();
            }

            this.bV = itemstack.isEmpty() ? ItemStack.a : itemstack.cloneItemStack();
        }

        this.bW.a();
        this.cT();
    }

    private void r() {
        this.bE = this.bH;
        this.bF = this.bI;
        this.bG = this.bJ;
        double d0 = this.locX - this.bH;
        double d1 = this.locY - this.bI;
        double d2 = this.locZ - this.bJ;
        double d3 = 10.0D;

        if (d0 > 10.0D) {
            this.bH = this.locX;
            this.bE = this.bH;
        }

        if (d2 > 10.0D) {
            this.bJ = this.locZ;
            this.bG = this.bJ;
        }

        if (d1 > 10.0D) {
            this.bI = this.locY;
            this.bF = this.bI;
        }

        if (d0 < -10.0D) {
            this.bH = this.locX;
            this.bE = this.bH;
        }

        if (d2 < -10.0D) {
            this.bJ = this.locZ;
            this.bG = this.bJ;
        }

        if (d1 < -10.0D) {
            this.bI = this.locY;
            this.bF = this.bI;
        }

        this.bH += d0 * 0.25D;
        this.bJ += d2 * 0.25D;
        this.bI += d1 * 0.25D;
    }

    protected void cT() {
        float f;
        float f1;

        if (this.cP()) {
            f = 0.6F;
            f1 = 0.6F;
        } else if (this.isSleeping()) {
            f = 0.2F;
            f1 = 0.2F;
        } else if (this.isSneaking()) {
            f = 0.6F;
            f1 = 1.65F;
        } else {
            f = 0.6F;
            f1 = 1.8F;
        }

        if (f != this.width || f1 != this.length) {
            AxisAlignedBB axisalignedbb = this.getBoundingBox();

            axisalignedbb = new AxisAlignedBB(axisalignedbb.a, axisalignedbb.b, axisalignedbb.c, axisalignedbb.a + (double) f, axisalignedbb.b + (double) f1, axisalignedbb.c + (double) f);
            if (!this.world.a(axisalignedbb)) {
                this.setSize(f, f1);
            }
        }

    }

    public int Z() {
        return this.abilities.isInvulnerable ? 1 : 80;
    }

    protected SoundEffect ae() {
        return SoundEffects.fL;
    }

    protected SoundEffect af() {
        return SoundEffects.fK;
    }

    public int aM() {
        return 10;
    }

    public void a(SoundEffect soundeffect, float f, float f1) {
        this.world.a(this, this.locX, this.locY, this.locZ, soundeffect, this.bK(), f, f1);
    }

    public SoundCategory bK() {
        return SoundCategory.PLAYERS;
    }

    public int getMaxFireTicks() {
        return 20;
    }

    protected boolean isFrozen() {
        return this.getHealth() <= 0.0F || this.isSleeping();
    }

    public void closeInventory() {
        this.activeContainer = this.defaultContainer;
    }

    public void aE() {
        if (!this.world.isClientSide && this.isSneaking() && this.isPassenger()) {
            this.stopRiding();
            this.setSneaking(false);
        } else {
            double d0 = this.locX;
            double d1 = this.locY;
            double d2 = this.locZ;
            float f = this.yaw;
            float f1 = this.pitch;

            super.aE();
            this.bB = this.bC;
            this.bC = 0.0F;
            this.l(this.locX - d0, this.locY - d1, this.locZ - d2);
            if (this.bJ() instanceof EntityPig) {
                this.pitch = f1;
                this.yaw = f;
                this.aN = ((EntityPig) this.bJ()).aN;
            }

        }
    }

    protected void doTick() {
        super.doTick();
        this.cl();
        this.aP = this.yaw;
    }

    public void n() {
        if (this.bA > 0) {
            --this.bA;
        }

        if (this.world.getDifficulty() == EnumDifficulty.PEACEFUL && this.world.getGameRules().getBoolean("naturalRegeneration")) {
            if (this.getHealth() < this.getMaxHealth() && this.ticksLived % 20 == 0) {
                this.heal(1.0F);
            }

            if (this.foodData.c() && this.ticksLived % 10 == 0) {
                this.foodData.a(this.foodData.getFoodLevel() + 1);
            }
        }

        this.inventory.n();
        this.bB = this.bC;
        super.n();
        AttributeInstance attributeinstance = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);

        if (!this.world.isClientSide) {
            attributeinstance.setValue((double) this.abilities.b());
        }

        this.aR = this.bT;
        if (this.isSprinting()) {
            this.aR = (float) ((double) this.aR + (double) this.bT * 0.3D);
        }

        this.k((float) attributeinstance.getValue());
        float f = MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ);
        float f1 = (float) (Math.atan(-this.motY * 0.20000000298023224D) * 15.0D);

        if (f > 0.1F) {
            f = 0.1F;
        }

        if (!this.onGround || this.getHealth() <= 0.0F) {
            f = 0.0F;
        }

        if (this.onGround || this.getHealth() <= 0.0F) {
            f1 = 0.0F;
        }

        this.bC += (f - this.bC) * 0.4F;
        this.aK += (f1 - this.aK) * 0.8F;
        if (this.getHealth() > 0.0F && !this.isSpectator()) {
            AxisAlignedBB axisalignedbb;

            if (this.isPassenger() && !this.bJ().dead) {
                axisalignedbb = this.getBoundingBox().b(this.bJ().getBoundingBox()).grow(1.0D, 0.0D, 1.0D);
            } else {
                axisalignedbb = this.getBoundingBox().grow(1.0D, 0.5D, 1.0D);
            }

            List list = this.world.getEntities(this, axisalignedbb);

            for (int i = 0; i < list.size(); ++i) {
                Entity entity = (Entity) list.get(i);

                if (!entity.dead) {
                    this.c(entity);
                }
            }
        }

        this.j(this.getShoulderEntityLeft());
        this.j(this.getShoulderEntityRight());
        if (!this.world.isClientSide && (this.fallDistance > 0.5F || this.isInWater() || this.isPassenger()) || this.abilities.isFlying) {
            this.releaseShoulderEntities();
        }

    }

    private void j(@Nullable NBTTagCompound nbttagcompound) {
        if (nbttagcompound != null && !nbttagcompound.hasKey("Silent") || !nbttagcompound.getBoolean("Silent")) {
            String s = nbttagcompound.getString("id");

            if (s.equals(EntityTypes.getName(EntityParrot.class).toString())) {
                EntityParrot.a(this.world, (Entity) this);
            }
        }

    }

    private void c(Entity entity) {
        entity.d(this);
    }

    public int getScore() {
        return ((Integer) this.datawatcher.get(EntityHuman.b)).intValue();
    }

    public void setScore(int i) {
        this.datawatcher.set(EntityHuman.b, Integer.valueOf(i));
    }

    public void addScore(int i) {
        int j = this.getScore();

        this.datawatcher.set(EntityHuman.b, Integer.valueOf(j + i));
    }

    public void die(DamageSource damagesource) {
        super.die(damagesource);
        this.setSize(0.2F, 0.2F);
        this.setPosition(this.locX, this.locY, this.locZ);
        this.motY = 0.10000000149011612D;
        if ("Notch".equals(this.getName())) {
            this.a(new ItemStack(Items.APPLE, 1), true, false);
        }

        if (!this.world.getGameRules().getBoolean("keepInventory") && !this.isSpectator()) {
            this.cV();
            this.inventory.o();
        }

        if (damagesource != null) {
            this.motX = (double) (-MathHelper.cos((this.aA + this.yaw) * 0.017453292F) * 0.1F);
            this.motZ = (double) (-MathHelper.sin((this.aA + this.yaw) * 0.017453292F) * 0.1F);
        } else {
            this.motX = 0.0D;
            this.motZ = 0.0D;
        }

        this.b(StatisticList.A);
        this.a(StatisticList.h);
        this.extinguish();
        this.setFlag(0, false);
    }

    protected void cV() {
        for (int i = 0; i < this.inventory.getSize(); ++i) {
            ItemStack itemstack = this.inventory.getItem(i);

            if (!itemstack.isEmpty() && EnchantmentManager.shouldNotDrop(itemstack)) {
                this.inventory.splitWithoutUpdate(i);
            }
        }

    }

    protected SoundEffect d(DamageSource damagesource) {
        return damagesource == DamageSource.BURN ? SoundEffects.fH : (damagesource == DamageSource.DROWN ? SoundEffects.fG : SoundEffects.fF);
    }

    protected SoundEffect cf() {
        return SoundEffects.fE;
    }

    @Nullable
    public EntityItem a(boolean flag) {
        return this.a(this.inventory.splitStack(this.inventory.itemInHandIndex, flag && !this.inventory.getItemInHand().isEmpty() ? this.inventory.getItemInHand().getCount() : 1), false, true);
    }

    @Nullable
    public EntityItem drop(ItemStack itemstack, boolean flag) {
        return this.a(itemstack, false, flag);
    }

    @Nullable
    public EntityItem a(ItemStack itemstack, boolean flag, boolean flag1) {
        if (itemstack.isEmpty()) {
            return null;
        } else {
            double d0 = this.locY - 0.30000001192092896D + (double) this.getHeadHeight();
            EntityItem entityitem = new EntityItem(this.world, this.locX, d0, this.locZ, itemstack);

            entityitem.a(40);
            if (flag1) {
                entityitem.e(this.getName());
            }

            float f;
            float f1;

            if (flag) {
                f = this.random.nextFloat() * 0.5F;
                f1 = this.random.nextFloat() * 6.2831855F;
                entityitem.motX = (double) (-MathHelper.sin(f1) * f);
                entityitem.motZ = (double) (MathHelper.cos(f1) * f);
                entityitem.motY = 0.20000000298023224D;
            } else {
                f = 0.3F;
                entityitem.motX = (double) (-MathHelper.sin(this.yaw * 0.017453292F) * MathHelper.cos(this.pitch * 0.017453292F) * f);
                entityitem.motZ = (double) (MathHelper.cos(this.yaw * 0.017453292F) * MathHelper.cos(this.pitch * 0.017453292F) * f);
                entityitem.motY = (double) (-MathHelper.sin(this.pitch * 0.017453292F) * f + 0.1F);
                f1 = this.random.nextFloat() * 6.2831855F;
                f = 0.02F * this.random.nextFloat();
                entityitem.motX += Math.cos((double) f1) * (double) f;
                entityitem.motY += (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
                entityitem.motZ += Math.sin((double) f1) * (double) f;
            }

            ItemStack itemstack1 = this.a(entityitem);

            if (flag1) {
                if (!itemstack1.isEmpty()) {
                    this.a(StatisticList.e(itemstack1.getItem()), itemstack.getCount());
                }

                this.b(StatisticList.x);
            }

            return entityitem;
        }
    }

    protected ItemStack a(EntityItem entityitem) {
        this.world.addEntity(entityitem);
        return entityitem.getItemStack();
    }

    public float b(IBlockData iblockdata) {
        float f = this.inventory.a(iblockdata);

        if (f > 1.0F) {
            int i = EnchantmentManager.getDigSpeedEnchantmentLevel(this);
            ItemStack itemstack = this.getItemInMainHand();

            if (i > 0 && !itemstack.isEmpty()) {
                f += (float) (i * i + 1);
            }
        }

        if (this.hasEffect(MobEffects.FASTER_DIG)) {
            f *= 1.0F + (float) (this.getEffect(MobEffects.FASTER_DIG).getAmplifier() + 1) * 0.2F;
        }

        if (this.hasEffect(MobEffects.SLOWER_DIG)) {
            float f1;

            switch (this.getEffect(MobEffects.SLOWER_DIG).getAmplifier()) {
            case 0:
                f1 = 0.3F;
                break;

            case 1:
                f1 = 0.09F;
                break;

            case 2:
                f1 = 0.0027F;
                break;

            case 3:
            default:
                f1 = 8.1E-4F;
            }

            f *= f1;
        }

        if (this.a(Material.WATER) && !EnchantmentManager.h(this)) {
            f /= 5.0F;
        }

        if (!this.onGround) {
            f /= 5.0F;
        }

        return f;
    }

    public boolean hasBlock(IBlockData iblockdata) {
        return this.inventory.b(iblockdata);
    }

    public static void c(DataConverterManager dataconvertermanager) {
        dataconvertermanager.a(DataConverterTypes.PLAYER, new DataInspector() {
            public NBTTagCompound a(DataConverter dataconverter, NBTTagCompound nbttagcompound, int i) {
                DataConverterRegistry.b(dataconverter, nbttagcompound, i, "Inventory");
                DataConverterRegistry.b(dataconverter, nbttagcompound, i, "EnderItems");
                if (nbttagcompound.hasKeyOfType("ShoulderEntityLeft", 10)) {
                    nbttagcompound.set("ShoulderEntityLeft", dataconverter.a(DataConverterTypes.ENTITY, nbttagcompound.getCompound("ShoulderEntityLeft"), i));
                }

                if (nbttagcompound.hasKeyOfType("ShoulderEntityRight", 10)) {
                    nbttagcompound.set("ShoulderEntityRight", dataconverter.a(DataConverterTypes.ENTITY, nbttagcompound.getCompound("ShoulderEntityRight"), i));
                }

                return nbttagcompound;
            }
        });
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.a(a(this.g));
        NBTTagList nbttaglist = nbttagcompound.getList("Inventory", 10);

        this.inventory.b(nbttaglist);
        this.inventory.itemInHandIndex = nbttagcompound.getInt("SelectedItemSlot");
        this.sleeping = nbttagcompound.getBoolean("Sleeping");
        this.sleepTicks = nbttagcompound.getShort("SleepTimer");
        this.exp = nbttagcompound.getFloat("XpP");
        this.expLevel = nbttagcompound.getInt("XpLevel");
        this.expTotal = nbttagcompound.getInt("XpTotal");
        this.bS = nbttagcompound.getInt("XpSeed");
        if (this.bS == 0) {
            this.bS = this.random.nextInt();
        }

        this.setScore(nbttagcompound.getInt("Score"));
        if (this.sleeping) {
            this.bedPosition = new BlockPosition(this);
            this.a(true, true, false);
        }

        if (nbttagcompound.hasKeyOfType("SpawnX", 99) && nbttagcompound.hasKeyOfType("SpawnY", 99) && nbttagcompound.hasKeyOfType("SpawnZ", 99)) {
            this.d = new BlockPosition(nbttagcompound.getInt("SpawnX"), nbttagcompound.getInt("SpawnY"), nbttagcompound.getInt("SpawnZ"));
            this.e = nbttagcompound.getBoolean("SpawnForced");
        }

        this.foodData.a(nbttagcompound);
        this.abilities.b(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("EnderItems", 9)) {
            NBTTagList nbttaglist1 = nbttagcompound.getList("EnderItems", 10);

            this.enderChest.a(nbttaglist1);
        }

        if (nbttagcompound.hasKeyOfType("ShoulderEntityLeft", 10)) {
            this.setShoulderEntityLeft(nbttagcompound.getCompound("ShoulderEntityLeft"));
        }

        if (nbttagcompound.hasKeyOfType("ShoulderEntityRight", 10)) {
            this.setShoulderEntityRight(nbttagcompound.getCompound("ShoulderEntityRight"));
        }

    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("DataVersion", 1343);
        nbttagcompound.set("Inventory", this.inventory.a(new NBTTagList()));
        nbttagcompound.setInt("SelectedItemSlot", this.inventory.itemInHandIndex);
        nbttagcompound.setBoolean("Sleeping", this.sleeping);
        nbttagcompound.setShort("SleepTimer", (short) this.sleepTicks);
        nbttagcompound.setFloat("XpP", this.exp);
        nbttagcompound.setInt("XpLevel", this.expLevel);
        nbttagcompound.setInt("XpTotal", this.expTotal);
        nbttagcompound.setInt("XpSeed", this.bS);
        nbttagcompound.setInt("Score", this.getScore());
        if (this.d != null) {
            nbttagcompound.setInt("SpawnX", this.d.getX());
            nbttagcompound.setInt("SpawnY", this.d.getY());
            nbttagcompound.setInt("SpawnZ", this.d.getZ());
            nbttagcompound.setBoolean("SpawnForced", this.e);
        }

        this.foodData.b(nbttagcompound);
        this.abilities.a(nbttagcompound);
        nbttagcompound.set("EnderItems", this.enderChest.i());
        if (!this.getShoulderEntityLeft().isEmpty()) {
            nbttagcompound.set("ShoulderEntityLeft", this.getShoulderEntityLeft());
        }

        if (!this.getShoulderEntityRight().isEmpty()) {
            nbttagcompound.set("ShoulderEntityRight", this.getShoulderEntityRight());
        }

    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else if (this.abilities.isInvulnerable && !damagesource.ignoresInvulnerability()) {
            return false;
        } else {
            this.ticksFarFromPlayer = 0;
            if (this.getHealth() <= 0.0F) {
                return false;
            } else {
                if (this.isSleeping() && !this.world.isClientSide) {
                    this.a(true, true, false);
                }

                this.releaseShoulderEntities();
                if (damagesource.r()) {
                    if (this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
                        f = 0.0F;
                    }

                    if (this.world.getDifficulty() == EnumDifficulty.EASY) {
                        f = Math.min(f / 2.0F + 1.0F, f);
                    }

                    if (this.world.getDifficulty() == EnumDifficulty.HARD) {
                        f = f * 3.0F / 2.0F;
                    }
                }

                return f == 0.0F ? false : super.damageEntity(damagesource, f);
            }
        }
    }

    protected void c(EntityLiving entityliving) {
        super.c(entityliving);
        if (entityliving.getItemInMainHand().getItem() instanceof ItemAxe) {
            this.m(true);
        }

    }

    public boolean a(EntityHuman entityhuman) {
        ScoreboardTeamBase scoreboardteambase = this.aY();
        ScoreboardTeamBase scoreboardteambase1 = entityhuman.aY();

        return scoreboardteambase == null ? true : (!scoreboardteambase.isAlly(scoreboardteambase1) ? true : scoreboardteambase.allowFriendlyFire());
    }

    protected void damageArmor(float f) {
        this.inventory.a(f);
    }

    protected void damageShield(float f) {
        if (f >= 3.0F && this.activeItem.getItem() == Items.SHIELD) {
            int i = 1 + MathHelper.d(f);

            this.activeItem.damage(i, this);
            if (this.activeItem.isEmpty()) {
                EnumHand enumhand = this.cH();

                if (enumhand == EnumHand.MAIN_HAND) {
                    this.setSlot(EnumItemSlot.MAINHAND, ItemStack.a);
                } else {
                    this.setSlot(EnumItemSlot.OFFHAND, ItemStack.a);
                }

                this.activeItem = ItemStack.a;
                this.a(SoundEffects.gy, 0.8F, 0.8F + this.world.random.nextFloat() * 0.4F);
            }
        }

    }

    public float cW() {
        int i = 0;
        Iterator iterator = this.inventory.armor.iterator();

        while (iterator.hasNext()) {
            ItemStack itemstack = (ItemStack) iterator.next();

            if (!itemstack.isEmpty()) {
                ++i;
            }
        }

        return (float) i / (float) this.inventory.armor.size();
    }

    protected void damageEntity0(DamageSource damagesource, float f) {
        if (!this.isInvulnerable(damagesource)) {
            f = this.applyArmorModifier(damagesource, f);
            f = this.applyMagicModifier(damagesource, f);
            float f1 = f;

            f = Math.max(f - this.getAbsorptionHearts(), 0.0F);
            this.setAbsorptionHearts(this.getAbsorptionHearts() - (f1 - f));
            if (f != 0.0F) {
                this.applyExhaustion(damagesource.getExhaustionCost());
                float f2 = this.getHealth();

                this.setHealth(this.getHealth() - f);
                this.getCombatTracker().trackDamage(damagesource, f2, f);
                if (f < 3.4028235E37F) {
                    this.a(StatisticList.z, Math.round(f * 10.0F));
                }

            }
        }
    }

    public void openSign(TileEntitySign tileentitysign) {}

    public void a(CommandBlockListenerAbstract commandblocklistenerabstract) {}

    public void a(TileEntityCommand tileentitycommand) {}

    public void a(TileEntityStructure tileentitystructure) {}

    public void openTrade(IMerchant imerchant) {}

    public void openContainer(IInventory iinventory) {}

    public void openHorseInventory(EntityHorseAbstract entityhorseabstract, IInventory iinventory) {}

    public void openTileEntity(ITileEntityContainer itileentitycontainer) {}

    public void a(ItemStack itemstack, EnumHand enumhand) {}

    public EnumInteractionResult a(Entity entity, EnumHand enumhand) {
        if (this.isSpectator()) {
            if (entity instanceof IInventory) {
                this.openContainer((IInventory) entity);
            }

            return EnumInteractionResult.PASS;
        } else {
            ItemStack itemstack = this.b(enumhand);
            ItemStack itemstack1 = itemstack.isEmpty() ? ItemStack.a : itemstack.cloneItemStack();

            if (entity.b(this, enumhand)) {
                if (this.abilities.canInstantlyBuild && itemstack == this.b(enumhand) && itemstack.getCount() < itemstack1.getCount()) {
                    itemstack.setCount(itemstack1.getCount());
                }

                return EnumInteractionResult.SUCCESS;
            } else {
                if (!itemstack.isEmpty() && entity instanceof EntityLiving) {
                    if (this.abilities.canInstantlyBuild) {
                        itemstack = itemstack1;
                    }

                    if (itemstack.a(this, (EntityLiving) entity, enumhand)) {
                        if (itemstack.isEmpty() && !this.abilities.canInstantlyBuild) {
                            this.a(enumhand, ItemStack.a);
                        }

                        return EnumInteractionResult.SUCCESS;
                    }
                }

                return EnumInteractionResult.PASS;
            }
        }
    }

    public double aF() {
        return -0.35D;
    }

    public void stopRiding() {
        super.stopRiding();
        this.j = 0;
    }

    public void attack(Entity entity) {
        if (entity.bd()) {
            if (!entity.t(this)) {
                float f = (float) this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue();
                float f1;

                if (entity instanceof EntityLiving) {
                    f1 = EnchantmentManager.a(this.getItemInMainHand(), ((EntityLiving) entity).getMonsterType());
                } else {
                    f1 = EnchantmentManager.a(this.getItemInMainHand(), EnumMonsterType.UNDEFINED);
                }

                float f2 = this.n(0.5F);

                f *= 0.2F + f2 * f2 * 0.8F;
                f1 *= f2;
                this.ds();
                if (f > 0.0F || f1 > 0.0F) {
                    boolean flag = f2 > 0.9F;
                    boolean flag1 = false;
                    byte b0 = 0;
                    int i = b0 + EnchantmentManager.b((EntityLiving) this);

                    if (this.isSprinting() && flag) {
                        this.world.a((EntityHuman) null, this.locX, this.locY, this.locZ, SoundEffects.fw, this.bK(), 1.0F, 1.0F);
                        ++i;
                        flag1 = true;
                    }

                    boolean flag2 = flag && this.fallDistance > 0.0F && !this.onGround && !this.m_() && !this.isInWater() && !this.hasEffect(MobEffects.BLINDNESS) && !this.isPassenger() && entity instanceof EntityLiving;

                    flag2 = flag2 && !this.isSprinting();
                    if (flag2) {
                        f *= 1.5F;
                    }

                    f += f1;
                    boolean flag3 = false;
                    double d0 = (double) (this.J - this.I);

                    if (flag && !flag2 && !flag1 && this.onGround && d0 < (double) this.cy()) {
                        ItemStack itemstack = this.b(EnumHand.MAIN_HAND);

                        if (itemstack.getItem() instanceof ItemSword) {
                            flag3 = true;
                        }
                    }

                    float f3 = 0.0F;
                    boolean flag4 = false;
                    int j = EnchantmentManager.getFireAspectEnchantmentLevel(this);

                    if (entity instanceof EntityLiving) {
                        f3 = ((EntityLiving) entity).getHealth();
                        if (j > 0 && !entity.isBurning()) {
                            flag4 = true;
                            entity.setOnFire(1);
                        }
                    }

                    double d1 = entity.motX;
                    double d2 = entity.motY;
                    double d3 = entity.motZ;
                    boolean flag5 = entity.damageEntity(DamageSource.playerAttack(this), f);

                    if (flag5) {
                        if (i > 0) {
                            if (entity instanceof EntityLiving) {
                                ((EntityLiving) entity).a(this, (float) i * 0.5F, (double) MathHelper.sin(this.yaw * 0.017453292F), (double) (-MathHelper.cos(this.yaw * 0.017453292F)));
                            } else {
                                entity.f((double) (-MathHelper.sin(this.yaw * 0.017453292F) * (float) i * 0.5F), 0.1D, (double) (MathHelper.cos(this.yaw * 0.017453292F) * (float) i * 0.5F));
                            }

                            this.motX *= 0.6D;
                            this.motZ *= 0.6D;
                            this.setSprinting(false);
                        }

                        if (flag3) {
                            float f4 = 1.0F + EnchantmentManager.a((EntityLiving) this) * f;
                            List list = this.world.a(EntityLiving.class, entity.getBoundingBox().grow(1.0D, 0.25D, 1.0D));
                            Iterator iterator = list.iterator();

                            while (iterator.hasNext()) {
                                EntityLiving entityliving = (EntityLiving) iterator.next();

                                if (entityliving != this && entityliving != entity && !this.r(entityliving) && this.h(entityliving) < 9.0D) {
                                    entityliving.a(this, 0.4F, (double) MathHelper.sin(this.yaw * 0.017453292F), (double) (-MathHelper.cos(this.yaw * 0.017453292F)));
                                    entityliving.damageEntity(DamageSource.playerAttack(this), f4);
                                }
                            }

                            this.world.a((EntityHuman) null, this.locX, this.locY, this.locZ, SoundEffects.fz, this.bK(), 1.0F, 1.0F);
                            this.cX();
                        }

                        if (entity instanceof EntityPlayer && entity.velocityChanged) {
                            ((EntityPlayer) entity).playerConnection.sendPacket(new PacketPlayOutEntityVelocity(entity));
                            entity.velocityChanged = false;
                            entity.motX = d1;
                            entity.motY = d2;
                            entity.motZ = d3;
                        }

                        if (flag2) {
                            this.world.a((EntityHuman) null, this.locX, this.locY, this.locZ, SoundEffects.fv, this.bK(), 1.0F, 1.0F);
                            this.a(entity);
                        }

                        if (!flag2 && !flag3) {
                            if (flag) {
                                this.world.a((EntityHuman) null, this.locX, this.locY, this.locZ, SoundEffects.fy, this.bK(), 1.0F, 1.0F);
                            } else {
                                this.world.a((EntityHuman) null, this.locX, this.locY, this.locZ, SoundEffects.fA, this.bK(), 1.0F, 1.0F);
                            }
                        }

                        if (f1 > 0.0F) {
                            this.b(entity);
                        }

                        this.z(entity);
                        if (entity instanceof EntityLiving) {
                            EnchantmentManager.a((EntityLiving) entity, (Entity) this);
                        }

                        EnchantmentManager.b((EntityLiving) this, entity);
                        ItemStack itemstack1 = this.getItemInMainHand();
                        Object object = entity;

                        if (entity instanceof EntityComplexPart) {
                            IComplex icomplex = ((EntityComplexPart) entity).owner;

                            if (icomplex instanceof EntityLiving) {
                                object = (EntityLiving) icomplex;
                            }
                        }

                        if (!itemstack1.isEmpty() && object instanceof EntityLiving) {
                            itemstack1.a((EntityLiving) object, this);
                            if (itemstack1.isEmpty()) {
                                this.a(EnumHand.MAIN_HAND, ItemStack.a);
                            }
                        }

                        if (entity instanceof EntityLiving) {
                            float f5 = f3 - ((EntityLiving) entity).getHealth();

                            this.a(StatisticList.y, Math.round(f5 * 10.0F));
                            if (j > 0) {
                                entity.setOnFire(j * 4);
                            }

                            if (this.world instanceof WorldServer && f5 > 2.0F) {
                                int k = (int) ((double) f5 * 0.5D);

                                ((WorldServer) this.world).a(EnumParticle.DAMAGE_INDICATOR, entity.locX, entity.locY + (double) (entity.length * 0.5F), entity.locZ, k, 0.1D, 0.0D, 0.1D, 0.2D, new int[0]);
                            }
                        }

                        this.applyExhaustion(0.1F);
                    } else {
                        this.world.a((EntityHuman) null, this.locX, this.locY, this.locZ, SoundEffects.fx, this.bK(), 1.0F, 1.0F);
                        if (flag4) {
                            entity.extinguish();
                        }
                    }
                }

            }
        }
    }

    public void m(boolean flag) {
        float f = 0.25F + (float) EnchantmentManager.getDigSpeedEnchantmentLevel(this) * 0.05F;

        if (flag) {
            f += 0.75F;
        }

        if (this.random.nextFloat() < f) {
            this.getCooldownTracker().a(Items.SHIELD, 100);
            this.cN();
            this.world.broadcastEntityEffect(this, (byte) 30);
        }

    }

    public void a(Entity entity) {}

    public void b(Entity entity) {}

    public void cX() {
        double d0 = (double) (-MathHelper.sin(this.yaw * 0.017453292F));
        double d1 = (double) MathHelper.cos(this.yaw * 0.017453292F);

        if (this.world instanceof WorldServer) {
            ((WorldServer) this.world).a(EnumParticle.SWEEP_ATTACK, this.locX + d0, this.locY + (double) this.length * 0.5D, this.locZ + d1, 0, d0, 0.0D, d1, 0.0D, new int[0]);
        }

    }

    public void die() {
        super.die();
        this.defaultContainer.b(this);
        if (this.activeContainer != null) {
            this.activeContainer.b(this);
        }

    }

    public boolean inBlock() {
        return !this.sleeping && super.inBlock();
    }

    public boolean cZ() {
        return false;
    }

    public GameProfile getProfile() {
        return this.g;
    }

    public EntityHuman.EnumBedResult a(BlockPosition blockposition) {
        EnumDirection enumdirection = (EnumDirection) this.world.getType(blockposition).get(BlockFacingHorizontal.FACING);

        if (!this.world.isClientSide) {
            if (this.isSleeping() || !this.isAlive()) {
                return EntityHuman.EnumBedResult.OTHER_PROBLEM;
            }

            if (!this.world.worldProvider.d()) {
                return EntityHuman.EnumBedResult.NOT_POSSIBLE_HERE;
            }

            if (this.world.D()) {
                return EntityHuman.EnumBedResult.NOT_POSSIBLE_NOW;
            }

            if (!this.a(blockposition, enumdirection)) {
                return EntityHuman.EnumBedResult.TOO_FAR_AWAY;
            }

            double d0 = 8.0D;
            double d1 = 5.0D;
            List list = this.world.a(EntityMonster.class, new AxisAlignedBB((double) blockposition.getX() - 8.0D, (double) blockposition.getY() - 5.0D, (double) blockposition.getZ() - 8.0D, (double) blockposition.getX() + 8.0D, (double) blockposition.getY() + 5.0D, (double) blockposition.getZ() + 8.0D), (Predicate) (new EntityHuman.c(this, null)));

            if (!list.isEmpty()) {
                return EntityHuman.EnumBedResult.NOT_SAFE;
            }
        }

        if (this.isPassenger()) {
            this.stopRiding();
        }

        this.releaseShoulderEntities();
        this.setSize(0.2F, 0.2F);
        if (this.world.isLoaded(blockposition)) {
            float f = 0.5F + (float) enumdirection.getAdjacentX() * 0.4F;
            float f1 = 0.5F + (float) enumdirection.getAdjacentZ() * 0.4F;

            this.a(enumdirection);
            this.setPosition((double) ((float) blockposition.getX() + f), (double) ((float) blockposition.getY() + 0.6875F), (double) ((float) blockposition.getZ() + f1));
        } else {
            this.setPosition((double) ((float) blockposition.getX() + 0.5F), (double) ((float) blockposition.getY() + 0.6875F), (double) ((float) blockposition.getZ() + 0.5F));
        }

        this.sleeping = true;
        this.sleepTicks = 0;
        this.bedPosition = blockposition;
        this.motX = 0.0D;
        this.motY = 0.0D;
        this.motZ = 0.0D;
        if (!this.world.isClientSide) {
            this.world.everyoneSleeping();
        }

        return EntityHuman.EnumBedResult.OK;
    }

    private boolean a(BlockPosition blockposition, EnumDirection enumdirection) {
        if (Math.abs(this.locX - (double) blockposition.getX()) <= 3.0D && Math.abs(this.locY - (double) blockposition.getY()) <= 2.0D && Math.abs(this.locZ - (double) blockposition.getZ()) <= 3.0D) {
            return true;
        } else {
            BlockPosition blockposition1 = blockposition.shift(enumdirection.opposite());

            return Math.abs(this.locX - (double) blockposition1.getX()) <= 3.0D && Math.abs(this.locY - (double) blockposition1.getY()) <= 2.0D && Math.abs(this.locZ - (double) blockposition1.getZ()) <= 3.0D;
        }
    }

    private void a(EnumDirection enumdirection) {
        this.bM = -1.8F * (float) enumdirection.getAdjacentX();
        this.bN = -1.8F * (float) enumdirection.getAdjacentZ();
    }

    public void a(boolean flag, boolean flag1, boolean flag2) {
        this.setSize(0.6F, 1.8F);
        IBlockData iblockdata = this.world.getType(this.bedPosition);

        if (this.bedPosition != null && iblockdata.getBlock() == Blocks.BED) {
            this.world.setTypeAndData(this.bedPosition, iblockdata.set(BlockBed.OCCUPIED, Boolean.valueOf(false)), 4);
            BlockPosition blockposition = BlockBed.a(this.world, this.bedPosition, 0);

            if (blockposition == null) {
                blockposition = this.bedPosition.up();
            }

            this.setPosition((double) ((float) blockposition.getX() + 0.5F), (double) ((float) blockposition.getY() + 0.1F), (double) ((float) blockposition.getZ() + 0.5F));
        }

        this.sleeping = false;
        if (!this.world.isClientSide && flag1) {
            this.world.everyoneSleeping();
        }

        this.sleepTicks = flag ? 0 : 100;
        if (flag2) {
            this.setRespawnPosition(this.bedPosition, false);
        }

    }

    private boolean s() {
        return this.world.getType(this.bedPosition).getBlock() == Blocks.BED;
    }

    @Nullable
    public static BlockPosition getBed(World world, BlockPosition blockposition, boolean flag) {
        Block block = world.getType(blockposition).getBlock();

        if (block != Blocks.BED) {
            if (!flag) {
                return null;
            } else {
                boolean flag1 = block.d();
                boolean flag2 = world.getType(blockposition.up()).getBlock().d();

                return flag1 && flag2 ? blockposition : null;
            }
        } else {
            return BlockBed.a(world, blockposition, 0);
        }
    }

    public boolean isSleeping() {
        return this.sleeping;
    }

    public boolean isDeeplySleeping() {
        return this.sleeping && this.sleepTicks >= 100;
    }

    public void a(IChatBaseComponent ichatbasecomponent, boolean flag) {}

    public BlockPosition getBed() {
        return this.d;
    }

    public boolean isRespawnForced() {
        return this.e;
    }

    public void setRespawnPosition(BlockPosition blockposition, boolean flag) {
        if (blockposition != null) {
            this.d = blockposition;
            this.e = flag;
        } else {
            this.d = null;
            this.e = false;
        }

    }

    public void b(Statistic statistic) {
        this.a(statistic, 1);
    }

    public void a(Statistic statistic, int i) {}

    public void a(Statistic statistic) {}

    public void a(List<IRecipe> list) {}

    public void a(MinecraftKey[] aminecraftkey) {}

    public void b(List<IRecipe> list) {}

    public void cu() {
        super.cu();
        this.b(StatisticList.w);
        if (this.isSprinting()) {
            this.applyExhaustion(0.2F);
        } else {
            this.applyExhaustion(0.05F);
        }

    }

    public void a(float f, float f1, float f2) {
        double d0 = this.locX;
        double d1 = this.locY;
        double d2 = this.locZ;

        if (this.abilities.isFlying && !this.isPassenger()) {
            double d3 = this.motY;
            float f3 = this.aR;

            this.aR = this.abilities.a() * (float) (this.isSprinting() ? 2 : 1);
            super.a(f, f1, f2);
            this.motY = d3 * 0.6D;
            this.aR = f3;
            this.fallDistance = 0.0F;
            this.setFlag(7, false);
        } else {
            super.a(f, f1, f2);
        }

        this.checkMovement(this.locX - d0, this.locY - d1, this.locZ - d2);
    }

    public float cy() {
        return (float) this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue();
    }

    public void checkMovement(double d0, double d1, double d2) {
        if (!this.isPassenger()) {
            int i;

            if (this.a(Material.WATER)) {
                i = Math.round(MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 100.0F);
                if (i > 0) {
                    this.a(StatisticList.q, i);
                    this.applyExhaustion(0.01F * (float) i * 0.01F);
                }
            } else if (this.isInWater()) {
                i = Math.round(MathHelper.sqrt(d0 * d0 + d2 * d2) * 100.0F);
                if (i > 0) {
                    this.a(StatisticList.m, i);
                    this.applyExhaustion(0.01F * (float) i * 0.01F);
                }
            } else if (this.m_()) {
                if (d1 > 0.0D) {
                    this.a(StatisticList.o, (int) Math.round(d1 * 100.0D));
                }
            } else if (this.onGround) {
                i = Math.round(MathHelper.sqrt(d0 * d0 + d2 * d2) * 100.0F);
                if (i > 0) {
                    if (this.isSprinting()) {
                        this.a(StatisticList.l, i);
                        this.applyExhaustion(0.1F * (float) i * 0.01F);
                    } else if (this.isSneaking()) {
                        this.a(StatisticList.k, i);
                        this.applyExhaustion(0.0F * (float) i * 0.01F);
                    } else {
                        this.a(StatisticList.j, i);
                        this.applyExhaustion(0.0F * (float) i * 0.01F);
                    }
                }
            } else if (this.cP()) {
                i = Math.round(MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 100.0F);
                this.a(StatisticList.v, i);
            } else {
                i = Math.round(MathHelper.sqrt(d0 * d0 + d2 * d2) * 100.0F);
                if (i > 25) {
                    this.a(StatisticList.p, i);
                }
            }

        }
    }

    private void l(double d0, double d1, double d2) {
        if (this.isPassenger()) {
            int i = Math.round(MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 100.0F);

            if (i > 0) {
                if (this.bJ() instanceof EntityMinecartAbstract) {
                    this.a(StatisticList.r, i);
                } else if (this.bJ() instanceof EntityBoat) {
                    this.a(StatisticList.s, i);
                } else if (this.bJ() instanceof EntityPig) {
                    this.a(StatisticList.t, i);
                } else if (this.bJ() instanceof EntityHorseAbstract) {
                    this.a(StatisticList.u, i);
                }
            }
        }

    }

    public void e(float f, float f1) {
        if (!this.abilities.canFly) {
            if (f >= 2.0F) {
                this.a(StatisticList.n, (int) Math.round((double) f * 100.0D));
            }

            super.e(f, f1);
        }
    }

    protected void ar() {
        if (!this.isSpectator()) {
            super.ar();
        }

    }

    protected SoundEffect e(int i) {
        return i > 4 ? SoundEffects.fB : SoundEffects.fJ;
    }

    public void b(EntityLiving entityliving) {
        EntityTypes.MonsterEggInfo entitytypes_monsteregginfo = (EntityTypes.MonsterEggInfo) EntityTypes.eggInfo.get(EntityTypes.a((Entity) entityliving));

        if (entitytypes_monsteregginfo != null) {
            this.b(entitytypes_monsteregginfo.killEntityStatistic);
        }

    }

    public void ba() {
        if (!this.abilities.isFlying) {
            super.ba();
        }

    }

    public void giveExp(int i) {
        this.addScore(i);
        int j = Integer.MAX_VALUE - this.expTotal;

        if (i > j) {
            i = j;
        }

        this.exp += (float) i / (float) this.getExpToLevel();

        for (this.expTotal += i; this.exp >= 1.0F; this.exp /= (float) this.getExpToLevel()) {
            this.exp = (this.exp - 1.0F) * (float) this.getExpToLevel();
            this.levelDown(1);
        }

    }

    public int dg() {
        return this.bS;
    }

    public void enchantDone(ItemStack itemstack, int i) {
        this.expLevel -= i;
        if (this.expLevel < 0) {
            this.expLevel = 0;
            this.exp = 0.0F;
            this.expTotal = 0;
        }

        this.bS = this.random.nextInt();
    }

    public void levelDown(int i) {
        this.expLevel += i;
        if (this.expLevel < 0) {
            this.expLevel = 0;
            this.exp = 0.0F;
            this.expTotal = 0;
        }

        if (i > 0 && this.expLevel % 5 == 0 && (float) this.f < (float) this.ticksLived - 100.0F) {
            float f = this.expLevel > 30 ? 1.0F : (float) this.expLevel / 30.0F;

            this.world.a((EntityHuman) null, this.locX, this.locY, this.locZ, SoundEffects.fI, this.bK(), f * 0.75F, 1.0F);
            this.f = this.ticksLived;
        }

    }

    public int getExpToLevel() {
        return this.expLevel >= 30 ? 112 + (this.expLevel - 30) * 9 : (this.expLevel >= 15 ? 37 + (this.expLevel - 15) * 5 : 7 + this.expLevel * 2);
    }

    public void applyExhaustion(float f) {
        if (!this.abilities.isInvulnerable) {
            if (!this.world.isClientSide) {
                this.foodData.a(f);
            }

        }
    }

    public FoodMetaData getFoodData() {
        return this.foodData;
    }

    public boolean n(boolean flag) {
        return (flag || this.foodData.c()) && !this.abilities.isInvulnerable;
    }

    public boolean dj() {
        return this.getHealth() > 0.0F && this.getHealth() < this.getMaxHealth();
    }

    public boolean dk() {
        return this.abilities.mayBuild;
    }

    public boolean a(BlockPosition blockposition, EnumDirection enumdirection, ItemStack itemstack) {
        if (this.abilities.mayBuild) {
            return true;
        } else if (itemstack.isEmpty()) {
            return false;
        } else {
            BlockPosition blockposition1 = blockposition.shift(enumdirection.opposite());
            Block block = this.world.getType(blockposition1).getBlock();

            return itemstack.b(block) || itemstack.y();
        }
    }

    protected int getExpValue(EntityHuman entityhuman) {
        if (!this.world.getGameRules().getBoolean("keepInventory") && !this.isSpectator()) {
            int i = this.expLevel * 7;

            return i > 100 ? 100 : i;
        } else {
            return 0;
        }
    }

    protected boolean alwaysGivesExp() {
        return true;
    }

    protected boolean playStepSound() {
        return !this.abilities.isFlying;
    }

    public void updateAbilities() {}

    public void a(EnumGamemode enumgamemode) {}

    public String getName() {
        return this.g.getName();
    }

    public InventoryEnderChest getEnderChest() {
        return this.enderChest;
    }

    public ItemStack getEquipment(EnumItemSlot enumitemslot) {
        return enumitemslot == EnumItemSlot.MAINHAND ? this.inventory.getItemInHand() : (enumitemslot == EnumItemSlot.OFFHAND ? (ItemStack) this.inventory.extraSlots.get(0) : (enumitemslot.a() == EnumItemSlot.Function.ARMOR ? (ItemStack) this.inventory.armor.get(enumitemslot.b()) : ItemStack.a));
    }

    public void setSlot(EnumItemSlot enumitemslot, ItemStack itemstack) {
        if (enumitemslot == EnumItemSlot.MAINHAND) {
            this.a_(itemstack);
            this.inventory.items.set(this.inventory.itemInHandIndex, itemstack);
        } else if (enumitemslot == EnumItemSlot.OFFHAND) {
            this.a_(itemstack);
            this.inventory.extraSlots.set(0, itemstack);
        } else if (enumitemslot.a() == EnumItemSlot.Function.ARMOR) {
            this.a_(itemstack);
            this.inventory.armor.set(enumitemslot.b(), itemstack);
        }

    }

    public boolean c(ItemStack itemstack) {
        this.a_(itemstack);
        return this.inventory.pickup(itemstack);
    }

    public Iterable<ItemStack> aO() {
        return Lists.newArrayList(new ItemStack[] { this.getItemInMainHand(), this.getItemInOffHand()});
    }

    public Iterable<ItemStack> getArmorItems() {
        return this.inventory.armor;
    }

    public boolean g(NBTTagCompound nbttagcompound) {
        if (!this.isPassenger() && this.onGround && !this.isInWater()) {
            if (this.getShoulderEntityLeft().isEmpty()) {
                this.setShoulderEntityLeft(nbttagcompound);
                return true;
            } else if (this.getShoulderEntityRight().isEmpty()) {
                this.setShoulderEntityRight(nbttagcompound);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    protected void releaseShoulderEntities() {
        this.spawnEntityFromShoulder(this.getShoulderEntityLeft());
        this.setShoulderEntityLeft(new NBTTagCompound());
        this.spawnEntityFromShoulder(this.getShoulderEntityRight());
        this.setShoulderEntityRight(new NBTTagCompound());
    }

    private void spawnEntityFromShoulder(@Nullable NBTTagCompound nbttagcompound) {
        if (!this.world.isClientSide && !nbttagcompound.isEmpty()) {
            Entity entity = EntityTypes.a(nbttagcompound, this.world);

            if (entity instanceof EntityTameableAnimal) {
                ((EntityTameableAnimal) entity).setOwnerUUID(this.uniqueID);
            }

            entity.setPosition(this.locX, this.locY + 0.699999988079071D, this.locZ);
            this.world.addEntity(entity);
        }

    }

    public abstract boolean isSpectator();

    public abstract boolean z();

    public boolean bo() {
        return !this.abilities.isFlying;
    }

    public Scoreboard getScoreboard() {
        return this.world.getScoreboard();
    }

    public ScoreboardTeamBase aY() {
        return this.getScoreboard().getPlayerTeam(this.getName());
    }

    public IChatBaseComponent getScoreboardDisplayName() {
        ChatComponentText chatcomponenttext = new ChatComponentText(ScoreboardTeam.getPlayerDisplayName(this.aY(), this.getName()));

        chatcomponenttext.getChatModifier().setChatClickable(new ChatClickable(ChatClickable.EnumClickAction.SUGGEST_COMMAND, "/msg " + this.getName() + " "));
        chatcomponenttext.getChatModifier().setChatHoverable(this.bv());
        chatcomponenttext.getChatModifier().setInsertion(this.getName());
        return chatcomponenttext;
    }

    public float getHeadHeight() {
        float f = 1.62F;

        if (this.isSleeping()) {
            f = 0.2F;
        } else if (!this.isSneaking() && this.length != 1.65F) {
            if (this.cP() || this.length == 0.6F) {
                f = 0.4F;
            }
        } else {
            f -= 0.08F;
        }

        return f;
    }

    public void setAbsorptionHearts(float f) {
        if (f < 0.0F) {
            f = 0.0F;
        }

        this.getDataWatcher().set(EntityHuman.a, Float.valueOf(f));
    }

    public float getAbsorptionHearts() {
        return ((Float) this.getDataWatcher().get(EntityHuman.a)).floatValue();
    }

    public static UUID a(GameProfile gameprofile) {
        UUID uuid = gameprofile.getId();

        if (uuid == null) {
            uuid = d(gameprofile.getName());
        }

        return uuid;
    }

    public static UUID d(String s) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + s).getBytes(StandardCharsets.UTF_8));
    }

    public boolean a(ChestLock chestlock) {
        if (chestlock.a()) {
            return true;
        } else {
            ItemStack itemstack = this.getItemInMainHand();

            return !itemstack.isEmpty() && itemstack.hasName() ? itemstack.getName().equals(chestlock.getKey()) : false;
        }
    }

    public boolean getSendCommandFeedback() {
        return this.C_().worldServer[0].getGameRules().getBoolean("sendCommandFeedback");
    }

    public boolean c(int i, ItemStack itemstack) {
        if (i >= 0 && i < this.inventory.items.size()) {
            this.inventory.setItem(i, itemstack);
            return true;
        } else {
            EnumItemSlot enumitemslot;

            if (i == 100 + EnumItemSlot.HEAD.b()) {
                enumitemslot = EnumItemSlot.HEAD;
            } else if (i == 100 + EnumItemSlot.CHEST.b()) {
                enumitemslot = EnumItemSlot.CHEST;
            } else if (i == 100 + EnumItemSlot.LEGS.b()) {
                enumitemslot = EnumItemSlot.LEGS;
            } else if (i == 100 + EnumItemSlot.FEET.b()) {
                enumitemslot = EnumItemSlot.FEET;
            } else {
                enumitemslot = null;
            }

            if (i == 98) {
                this.setSlot(EnumItemSlot.MAINHAND, itemstack);
                return true;
            } else if (i == 99) {
                this.setSlot(EnumItemSlot.OFFHAND, itemstack);
                return true;
            } else if (enumitemslot == null) {
                int j = i - 200;

                if (j >= 0 && j < this.enderChest.getSize()) {
                    this.enderChest.setItem(j, itemstack);
                    return true;
                } else {
                    return false;
                }
            } else {
                if (!itemstack.isEmpty()) {
                    if (!(itemstack.getItem() instanceof ItemArmor) && !(itemstack.getItem() instanceof ItemElytra)) {
                        if (enumitemslot != EnumItemSlot.HEAD) {
                            return false;
                        }
                    } else if (EntityInsentient.d(itemstack) != enumitemslot) {
                        return false;
                    }
                }

                this.inventory.setItem(enumitemslot.b() + this.inventory.items.size(), itemstack);
                return true;
            }
        }
    }

    public EnumMainHand getMainHand() {
        return ((Byte) this.datawatcher.get(EntityHuman.bs)).byteValue() == 0 ? EnumMainHand.LEFT : EnumMainHand.RIGHT;
    }

    public void a(EnumMainHand enummainhand) {
        this.datawatcher.set(EntityHuman.bs, Byte.valueOf((byte) (enummainhand == EnumMainHand.LEFT ? 0 : 1)));
    }

    public NBTTagCompound getShoulderEntityLeft() {
        return (NBTTagCompound) this.datawatcher.get(EntityHuman.bt);
    }

    public void setShoulderEntityLeft(NBTTagCompound nbttagcompound) {
        this.datawatcher.set(EntityHuman.bt, nbttagcompound);
    }

    public NBTTagCompound getShoulderEntityRight() {
        return (NBTTagCompound) this.datawatcher.get(EntityHuman.bu);
    }

    public void setShoulderEntityRight(NBTTagCompound nbttagcompound) {
        this.datawatcher.set(EntityHuman.bu, nbttagcompound);
    }

    public float dr() {
        return (float) (1.0D / this.getAttributeInstance(GenericAttributes.g).getValue() * 20.0D);
    }

    public float n(float f) {
        return MathHelper.a(((float) this.aE + f) / this.dr(), 0.0F, 1.0F);
    }

    public void ds() {
        this.aE = 0;
    }

    public ItemCooldown getCooldownTracker() {
        return this.bW;
    }

    public void collide(Entity entity) {
        if (!this.isSleeping()) {
            super.collide(entity);
        }

    }

    public float du() {
        return (float) this.getAttributeInstance(GenericAttributes.j).getValue();
    }

    public boolean isCreativeAndOp() {
        return this.abilities.canInstantlyBuild && this.a(2, "");
    }

    static class c implements Predicate<EntityMonster> {

        private final EntityHuman a;

        private c(EntityHuman entityhuman) {
            this.a = entityhuman;
        }

        public boolean a(@Nullable EntityMonster entitymonster) {
            return entitymonster.c(this.a);
        }

        public boolean apply(@Nullable Object object) {
            return this.a((EntityMonster) object);
        }

        c(EntityHuman entityhuman, Object object) {
            this(entityhuman);
        }
    }

    public static enum EnumBedResult {

        OK, NOT_POSSIBLE_HERE, NOT_POSSIBLE_NOW, TOO_FAR_AWAY, OTHER_PROBLEM, NOT_SAFE;

        private EnumBedResult() {}
    }

    public static enum EnumChatVisibility {

        FULL(0, "options.chat.visibility.full"), SYSTEM(1, "options.chat.visibility.system"), HIDDEN(2, "options.chat.visibility.hidden");

        private static final EntityHuman.EnumChatVisibility[] d = new EntityHuman.EnumChatVisibility[values().length];
        private final int e;
        private final String f;

        private EnumChatVisibility(int i, String s) {
            this.e = i;
            this.f = s;
        }

        static {
            EntityHuman.EnumChatVisibility[] aentityhuman_enumchatvisibility = values();
            int i = aentityhuman_enumchatvisibility.length;

            for (int j = 0; j < i; ++j) {
                EntityHuman.EnumChatVisibility entityhuman_enumchatvisibility = aentityhuman_enumchatvisibility[j];

                EntityHuman.EnumChatVisibility.d[entityhuman_enumchatvisibility.e] = entityhuman_enumchatvisibility;
            }

        }
    }
}
