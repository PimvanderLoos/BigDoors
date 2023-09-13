package net.minecraft.server;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

public abstract class EntityHorseAbstract extends EntityAnimal implements IInventoryListener, IJumpable {

    private static final Predicate<Entity> bG = new Predicate() {
        public boolean a(@Nullable Entity entity) {
            return entity instanceof EntityHorseAbstract && ((EntityHorseAbstract) entity).hasReproduced();
        }

        public boolean apply(@Nullable Object object) {
            return this.a((Entity) object);
        }
    };
    public static final IAttribute attributeJumpStrength = (new AttributeRanged((IAttribute) null, "horse.jumpStrength", 0.7D, 0.0D, 2.0D)).a("Jump Strength").a(true);
    private static final DataWatcherObject<Byte> bH = DataWatcher.a(EntityHorseAbstract.class, DataWatcherRegistry.a);
    private static final DataWatcherObject<Optional<UUID>> bI = DataWatcher.a(EntityHorseAbstract.class, DataWatcherRegistry.m);
    private int bJ;
    private int bK;
    private int bL;
    public int bx;
    public int by;
    protected boolean bA;
    public InventoryHorseChest inventoryChest;
    protected int bC;
    protected float jumpPower;
    private boolean canSlide;
    private float bN;
    private float bO;
    private float bP;
    private float bQ;
    private float bR;
    private float bS;
    protected boolean bE = true;
    protected int bF;

    public EntityHorseAbstract(World world) {
        super(world);
        this.setSize(1.3964844F, 1.6F);
        this.P = 1.0F;
        this.dx();
    }

    protected void r() {
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalPanic(this, 1.2D));
        this.goalSelector.a(1, new PathfinderGoalTame(this, 1.2D));
        this.goalSelector.a(2, new PathfinderGoalBreed(this, 1.0D, EntityHorseAbstract.class));
        this.goalSelector.a(4, new PathfinderGoalFollowParent(this, 1.0D));
        this.goalSelector.a(6, new PathfinderGoalRandomStrollLand(this, 0.7D));
        this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
    }

    protected void i() {
        super.i();
        this.datawatcher.register(EntityHorseAbstract.bH, Byte.valueOf((byte) 0));
        this.datawatcher.register(EntityHorseAbstract.bI, Optional.absent());
    }

    protected boolean g(int i) {
        return (((Byte) this.datawatcher.get(EntityHorseAbstract.bH)).byteValue() & i) != 0;
    }

    protected void c(int i, boolean flag) {
        byte b0 = ((Byte) this.datawatcher.get(EntityHorseAbstract.bH)).byteValue();

        if (flag) {
            this.datawatcher.set(EntityHorseAbstract.bH, Byte.valueOf((byte) (b0 | i)));
        } else {
            this.datawatcher.set(EntityHorseAbstract.bH, Byte.valueOf((byte) (b0 & ~i)));
        }

    }

    public boolean isTamed() {
        return this.g(2);
    }

    @Nullable
    public UUID getOwnerUUID() {
        return (UUID) ((Optional) this.datawatcher.get(EntityHorseAbstract.bI)).orNull();
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.datawatcher.set(EntityHorseAbstract.bI, Optional.fromNullable(uuid));
    }

    public float dr() {
        return 0.5F;
    }

    public void a(boolean flag) {
        this.a(flag ? this.dr() : 1.0F);
    }

    public boolean ds() {
        return this.bA;
    }

    public void setTamed(boolean flag) {
        this.c(2, flag);
    }

    public void r(boolean flag) {
        this.bA = flag;
    }

    public boolean a(EntityHuman entityhuman) {
        return super.a(entityhuman) && this.getMonsterType() != EnumMonsterType.UNDEAD;
    }

    protected void q(float f) {
        if (f > 6.0F && this.dt()) {
            this.u(false);
        }

    }

    public boolean dt() {
        return this.g(16);
    }

    public boolean du() {
        return this.g(32);
    }

    public boolean hasReproduced() {
        return this.g(8);
    }

    public void s(boolean flag) {
        this.c(8, flag);
    }

    public void t(boolean flag) {
        this.c(4, flag);
    }

    public int getTemper() {
        return this.bC;
    }

    public void setTemper(int i) {
        this.bC = i;
    }

    public int n(int i) {
        int j = MathHelper.clamp(this.getTemper() + i, 0, this.getMaxDomestication());

        this.setTemper(j);
        return j;
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        Entity entity = damagesource.getEntity();

        return this.isVehicle() && entity != null && this.y(entity) ? false : super.damageEntity(damagesource, f);
    }

    public boolean isCollidable() {
        return !this.isVehicle();
    }

    private void dh() {
        this.dl();
        if (!this.isSilent()) {
            this.world.a((EntityHuman) null, this.locX, this.locY, this.locZ, SoundEffects.cE, this.bC(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
        }

    }

    public void e(float f, float f1) {
        if (f > 1.0F) {
            this.a(SoundEffects.cI, 0.4F, 1.0F);
        }

        int i = MathHelper.f((f * 0.5F - 3.0F) * f1);

        if (i > 0) {
            this.damageEntity(DamageSource.FALL, (float) i);
            if (this.isVehicle()) {
                Iterator iterator = this.by().iterator();

                while (iterator.hasNext()) {
                    Entity entity = (Entity) iterator.next();

                    entity.damageEntity(DamageSource.FALL, (float) i);
                }
            }

            IBlockData iblockdata = this.world.getType(new BlockPosition(this.locX, this.locY - 0.2D - (double) this.lastYaw, this.locZ));
            Block block = iblockdata.getBlock();

            if (iblockdata.getMaterial() != Material.AIR && !this.isSilent()) {
                SoundEffectType soundeffecttype = block.getStepSound();

                this.world.a((EntityHuman) null, this.locX, this.locY, this.locZ, soundeffecttype.d(), this.bC(), soundeffecttype.a() * 0.5F, soundeffecttype.b() * 0.75F);
            }

        }
    }

    protected int di() {
        return 2;
    }

    protected void dx() {
        InventoryHorseChest inventoryhorsechest = this.inventoryChest;

        this.inventoryChest = new InventoryHorseChest("HorseChest", this.di());
        this.inventoryChest.a(this.getName());
        if (inventoryhorsechest != null) {
            inventoryhorsechest.b(this);
            int i = Math.min(inventoryhorsechest.getSize(), this.inventoryChest.getSize());

            for (int j = 0; j < i; ++j) {
                ItemStack itemstack = inventoryhorsechest.getItem(j);

                if (!itemstack.isEmpty()) {
                    this.inventoryChest.setItem(j, itemstack.cloneItemStack());
                }
            }
        }

        this.inventoryChest.a((IInventoryListener) this);
        this.dy();
    }

    protected void dy() {
        if (!this.world.isClientSide) {
            this.t(!this.inventoryChest.getItem(0).isEmpty() && this.dA());
        }
    }

    public void a(IInventory iinventory) {
        boolean flag = this.dB();

        this.dy();
        if (this.ticksLived > 20 && !flag && this.dB()) {
            this.a(SoundEffects.cJ, 0.5F, 1.0F);
        }

    }

    @Nullable
    protected EntityHorseAbstract a(Entity entity, double d0) {
        double d1 = Double.MAX_VALUE;
        Entity entity1 = null;
        List list = this.world.getEntities(entity, entity.getBoundingBox().b(d0, d0, d0), EntityHorseAbstract.bG);
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            Entity entity2 = (Entity) iterator.next();
            double d2 = entity2.d(entity.locX, entity.locY, entity.locZ);

            if (d2 < d1) {
                entity1 = entity2;
                d1 = d2;
            }
        }

        return (EntityHorseAbstract) entity1;
    }

    public double getJumpStrength() {
        return this.getAttributeInstance(EntityHorseAbstract.attributeJumpStrength).getValue();
    }

    @Nullable
    protected SoundEffect bX() {
        this.dl();
        return null;
    }

    @Nullable
    protected SoundEffect bW() {
        this.dl();
        if (this.random.nextInt(3) == 0) {
            this.dL();
        }

        return null;
    }

    @Nullable
    protected SoundEffect G() {
        this.dl();
        if (this.random.nextInt(10) == 0 && !this.isFrozen()) {
            this.dL();
        }

        return null;
    }

    public boolean dA() {
        return true;
    }

    public boolean dB() {
        return this.g(4);
    }

    @Nullable
    protected SoundEffect dj() {
        this.dl();
        this.dL();
        return null;
    }

    protected void a(BlockPosition blockposition, Block block) {
        if (!block.getBlockData().getMaterial().isLiquid()) {
            SoundEffectType soundeffecttype = block.getStepSound();

            if (this.world.getType(blockposition.up()).getBlock() == Blocks.SNOW_LAYER) {
                soundeffecttype = Blocks.SNOW_LAYER.getStepSound();
            }

            if (this.isVehicle() && this.bE) {
                ++this.bF;
                if (this.bF > 5 && this.bF % 3 == 0) {
                    this.a(soundeffecttype);
                } else if (this.bF <= 5) {
                    this.a(SoundEffects.cL, soundeffecttype.a() * 0.15F, soundeffecttype.b());
                }
            } else if (soundeffecttype == SoundEffectType.a) {
                this.a(SoundEffects.cL, soundeffecttype.a() * 0.15F, soundeffecttype.b());
            } else {
                this.a(SoundEffects.cK, soundeffecttype.a() * 0.15F, soundeffecttype.b());
            }

        }
    }

    protected void a(SoundEffectType soundeffecttype) {
        this.a(SoundEffects.cF, soundeffecttype.a() * 0.15F, soundeffecttype.b());
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeMap().b(EntityHorseAbstract.attributeJumpStrength);
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(53.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.22499999403953552D);
    }

    public int cQ() {
        return 6;
    }

    public int getMaxDomestication() {
        return 100;
    }

    protected float ci() {
        return 0.8F;
    }

    public int C() {
        return 400;
    }

    public void f(EntityHuman entityhuman) {
        if (!this.world.isClientSide && (!this.isVehicle() || this.w(entityhuman)) && this.isTamed()) {
            this.inventoryChest.a(this.getName());
            entityhuman.openHorseInventory(this, this.inventoryChest);
        }

    }

    protected boolean b(EntityHuman entityhuman, ItemStack itemstack) {
        boolean flag = false;
        float f = 0.0F;
        short short0 = 0;
        byte b0 = 0;
        Item item = itemstack.getItem();

        if (item == Items.WHEAT) {
            f = 2.0F;
            short0 = 20;
            b0 = 3;
        } else if (item == Items.SUGAR) {
            f = 1.0F;
            short0 = 30;
            b0 = 3;
        } else if (item == Item.getItemOf(Blocks.HAY_BLOCK)) {
            f = 20.0F;
            short0 = 180;
        } else if (item == Items.APPLE) {
            f = 3.0F;
            short0 = 60;
            b0 = 3;
        } else if (item == Items.GOLDEN_CARROT) {
            f = 4.0F;
            short0 = 60;
            b0 = 5;
            if (this.isTamed() && this.getAge() == 0 && !this.isInLove()) {
                flag = true;
                this.c(entityhuman);
            }
        } else if (item == Items.GOLDEN_APPLE) {
            f = 10.0F;
            short0 = 240;
            b0 = 10;
            if (this.isTamed() && this.getAge() == 0 && !this.isInLove()) {
                flag = true;
                this.c(entityhuman);
            }
        }

        if (this.getHealth() < this.getMaxHealth() && f > 0.0F) {
            this.heal(f);
            flag = true;
        }

        if (this.isBaby() && short0 > 0) {
            this.world.addParticle(EnumParticle.VILLAGER_HAPPY, this.locX + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width, this.locY + 0.5D + (double) (this.random.nextFloat() * this.length), this.locZ + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width, 0.0D, 0.0D, 0.0D, new int[0]);
            if (!this.world.isClientSide) {
                this.setAge(short0);
            }

            flag = true;
        }

        if (b0 > 0 && (flag || !this.isTamed()) && this.getTemper() < this.getMaxDomestication()) {
            flag = true;
            if (!this.world.isClientSide) {
                this.n(b0);
            }
        }

        if (flag) {
            this.dh();
        }

        return flag;
    }

    protected void g(EntityHuman entityhuman) {
        entityhuman.yaw = this.yaw;
        entityhuman.pitch = this.pitch;
        this.u(false);
        this.setStanding(false);
        if (!this.world.isClientSide) {
            entityhuman.startRiding(this);
        }

    }

    protected boolean isFrozen() {
        return super.isFrozen() && this.isVehicle() && this.dB() || this.dt() || this.du();
    }

    public boolean e(ItemStack itemstack) {
        return false;
    }

    private void dk() {
        this.bx = 1;
    }

    public void die(DamageSource damagesource) {
        super.die(damagesource);
        if (!this.world.isClientSide && this.inventoryChest != null) {
            for (int i = 0; i < this.inventoryChest.getSize(); ++i) {
                ItemStack itemstack = this.inventoryChest.getItem(i);

                if (!itemstack.isEmpty()) {
                    this.a(itemstack, 0.0F);
                }
            }

        }
    }

    public void n() {
        if (this.random.nextInt(200) == 0) {
            this.dk();
        }

        super.n();
        if (!this.world.isClientSide) {
            if (this.random.nextInt(900) == 0 && this.deathTicks == 0) {
                this.heal(1.0F);
            }

            if (this.dE()) {
                if (!this.dt() && !this.isVehicle() && this.random.nextInt(300) == 0 && this.world.getType(new BlockPosition(MathHelper.floor(this.locX), MathHelper.floor(this.locY) - 1, MathHelper.floor(this.locZ))).getBlock() == Blocks.GRASS) {
                    this.u(true);
                }

                if (this.dt() && ++this.bJ > 50) {
                    this.bJ = 0;
                    this.u(false);
                }
            }

            this.dD();
        }
    }

    protected void dD() {
        if (this.hasReproduced() && this.isBaby() && !this.dt()) {
            EntityHorseAbstract entityhorseabstract = this.a(this, 16.0D);

            if (entityhorseabstract != null && this.h((Entity) entityhorseabstract) > 4.0D) {
                this.navigation.a((Entity) entityhorseabstract);
            }
        }

    }

    public boolean dE() {
        return true;
    }

    public void A_() {
        super.A_();
        if (this.bK > 0 && ++this.bK > 30) {
            this.bK = 0;
            this.c(64, false);
        }

        if (this.bA() && this.bL > 0 && ++this.bL > 20) {
            this.bL = 0;
            this.setStanding(false);
        }

        if (this.bx > 0 && ++this.bx > 8) {
            this.bx = 0;
        }

        if (this.by > 0) {
            ++this.by;
            if (this.by > 300) {
                this.by = 0;
            }
        }

        this.bO = this.bN;
        if (this.dt()) {
            this.bN += (1.0F - this.bN) * 0.4F + 0.05F;
            if (this.bN > 1.0F) {
                this.bN = 1.0F;
            }
        } else {
            this.bN += (0.0F - this.bN) * 0.4F - 0.05F;
            if (this.bN < 0.0F) {
                this.bN = 0.0F;
            }
        }

        this.bQ = this.bP;
        if (this.du()) {
            this.bN = 0.0F;
            this.bO = this.bN;
            this.bP += (1.0F - this.bP) * 0.4F + 0.05F;
            if (this.bP > 1.0F) {
                this.bP = 1.0F;
            }
        } else {
            this.canSlide = false;
            this.bP += (0.8F * this.bP * this.bP * this.bP - this.bP) * 0.6F - 0.05F;
            if (this.bP < 0.0F) {
                this.bP = 0.0F;
            }
        }

        this.bS = this.bR;
        if (this.g(64)) {
            this.bR += (1.0F - this.bR) * 0.7F + 0.05F;
            if (this.bR > 1.0F) {
                this.bR = 1.0F;
            }
        } else {
            this.bR += (0.0F - this.bR) * 0.7F - 0.05F;
            if (this.bR < 0.0F) {
                this.bR = 0.0F;
            }
        }

    }

    private void dl() {
        if (!this.world.isClientSide) {
            this.bK = 1;
            this.c(64, true);
        }

    }

    public void u(boolean flag) {
        this.c(16, flag);
    }

    public void setStanding(boolean flag) {
        if (flag) {
            this.u(false);
        }

        this.c(32, flag);
    }

    private void dL() {
        if (this.bA()) {
            this.bL = 1;
            this.setStanding(true);
        }

    }

    public void dF() {
        this.dL();
        SoundEffect soundeffect = this.dj();

        if (soundeffect != null) {
            this.a(soundeffect, this.ci(), this.cj());
        }

    }

    public boolean h(EntityHuman entityhuman) {
        this.setOwnerUUID(entityhuman.getUniqueID());
        this.setTamed(true);
        this.world.broadcastEntityEffect(this, (byte) 7);
        return true;
    }

    public void g(float f, float f1) {
        if (this.isVehicle() && this.cR() && this.dB()) {
            EntityLiving entityliving = (EntityLiving) this.bw();

            this.yaw = entityliving.yaw;
            this.lastYaw = this.yaw;
            this.pitch = entityliving.pitch * 0.5F;
            this.setYawPitch(this.yaw, this.pitch);
            this.aN = this.yaw;
            this.aP = this.aN;
            f = entityliving.be * 0.5F;
            f1 = entityliving.bf;
            if (f1 <= 0.0F) {
                f1 *= 0.25F;
                this.bF = 0;
            }

            if (this.onGround && this.jumpPower == 0.0F && this.du() && !this.canSlide) {
                f = 0.0F;
                f1 = 0.0F;
            }

            if (this.jumpPower > 0.0F && !this.ds() && this.onGround) {
                this.motY = this.getJumpStrength() * (double) this.jumpPower;
                if (this.hasEffect(MobEffects.JUMP)) {
                    this.motY += (double) ((float) (this.getEffect(MobEffects.JUMP).getAmplifier() + 1) * 0.1F);
                }

                this.r(true);
                this.impulse = true;
                if (f1 > 0.0F) {
                    float f2 = MathHelper.sin(this.yaw * 0.017453292F);
                    float f3 = MathHelper.cos(this.yaw * 0.017453292F);

                    this.motX += (double) (-0.4F * f2 * this.jumpPower);
                    this.motZ += (double) (0.4F * f3 * this.jumpPower);
                    this.a(SoundEffects.cH, 0.4F, 1.0F);
                }

                this.jumpPower = 0.0F;
            }

            this.aR = this.cq() * 0.1F;
            if (this.bA()) {
                this.l((float) this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue());
                super.g(f, f1);
            } else if (entityliving instanceof EntityHuman) {
                this.motX = 0.0D;
                this.motY = 0.0D;
                this.motZ = 0.0D;
            }

            if (this.onGround) {
                this.jumpPower = 0.0F;
                this.r(false);
            }

            this.aF = this.aG;
            double d0 = this.locX - this.lastX;
            double d1 = this.locZ - this.lastZ;
            float f4 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;

            if (f4 > 1.0F) {
                f4 = 1.0F;
            }

            this.aG += (f4 - this.aG) * 0.4F;
            this.aH += this.aG;
        } else {
            this.aR = 0.02F;
            super.g(f, f1);
        }
    }

    public static void c(DataConverterManager dataconvertermanager, Class<?> oclass) {
        EntityInsentient.a(dataconvertermanager, oclass);
        dataconvertermanager.a(DataConverterTypes.ENTITY, (DataInspector) (new DataInspectorItem(oclass, new String[] { "SaddleItem"})));
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setBoolean("EatingHaystack", this.dt());
        nbttagcompound.setBoolean("Bred", this.hasReproduced());
        nbttagcompound.setInt("Temper", this.getTemper());
        nbttagcompound.setBoolean("Tame", this.isTamed());
        if (this.getOwnerUUID() != null) {
            nbttagcompound.setString("OwnerUUID", this.getOwnerUUID().toString());
        }

        if (!this.inventoryChest.getItem(0).isEmpty()) {
            nbttagcompound.set("SaddleItem", this.inventoryChest.getItem(0).save(new NBTTagCompound()));
        }

    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.u(nbttagcompound.getBoolean("EatingHaystack"));
        this.s(nbttagcompound.getBoolean("Bred"));
        this.setTemper(nbttagcompound.getInt("Temper"));
        this.setTamed(nbttagcompound.getBoolean("Tame"));
        String s;

        if (nbttagcompound.hasKeyOfType("OwnerUUID", 8)) {
            s = nbttagcompound.getString("OwnerUUID");
        } else {
            String s1 = nbttagcompound.getString("Owner");

            s = NameReferencingFileConverter.a(this.B_(), s1);
        }

        if (!s.isEmpty()) {
            this.setOwnerUUID(UUID.fromString(s));
        }

        AttributeInstance attributeinstance = this.getAttributeMap().a("Speed");

        if (attributeinstance != null) {
            this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(attributeinstance.b() * 0.25D);
        }

        if (nbttagcompound.hasKeyOfType("SaddleItem", 10)) {
            ItemStack itemstack = new ItemStack(nbttagcompound.getCompound("SaddleItem"));

            if (itemstack.getItem() == Items.SADDLE) {
                this.inventoryChest.setItem(0, itemstack);
            }
        }

        this.dy();
    }

    public boolean mate(EntityAnimal entityanimal) {
        return false;
    }

    protected boolean dG() {
        return !this.isVehicle() && !this.isPassenger() && this.isTamed() && !this.isBaby() && this.getHealth() >= this.getMaxHealth() && this.isInLove();
    }

    @Nullable
    public EntityAgeable createChild(EntityAgeable entityageable) {
        return null;
    }

    protected void a(EntityAgeable entityageable, EntityHorseAbstract entityhorseabstract) {
        double d0 = this.getAttributeInstance(GenericAttributes.maxHealth).b() + entityageable.getAttributeInstance(GenericAttributes.maxHealth).b() + (double) this.dH();

        entityhorseabstract.getAttributeInstance(GenericAttributes.maxHealth).setValue(d0 / 3.0D);
        double d1 = this.getAttributeInstance(EntityHorseAbstract.attributeJumpStrength).b() + entityageable.getAttributeInstance(EntityHorseAbstract.attributeJumpStrength).b() + this.dI();

        entityhorseabstract.getAttributeInstance(EntityHorseAbstract.attributeJumpStrength).setValue(d1 / 3.0D);
        double d2 = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).b() + entityageable.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).b() + this.dJ();

        entityhorseabstract.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(d2 / 3.0D);
    }

    public boolean cR() {
        return this.bw() instanceof EntityLiving;
    }

    public boolean a() {
        return this.dB();
    }

    public void b(int i) {
        this.canSlide = true;
        this.dL();
    }

    public void q_() {}

    public void k(Entity entity) {
        super.k(entity);
        if (entity instanceof EntityInsentient) {
            EntityInsentient entityinsentient = (EntityInsentient) entity;

            this.aN = entityinsentient.aN;
        }

        if (this.bQ > 0.0F) {
            float f = MathHelper.sin(this.aN * 0.017453292F);
            float f1 = MathHelper.cos(this.aN * 0.017453292F);
            float f2 = 0.7F * this.bQ;
            float f3 = 0.15F * this.bQ;

            entity.setPosition(this.locX + (double) (f2 * f), this.locY + this.ay() + entity.ax() + (double) f3, this.locZ - (double) (f2 * f1));
            if (entity instanceof EntityLiving) {
                ((EntityLiving) entity).aN = this.aN;
            }
        }

    }

    protected float dH() {
        return 15.0F + (float) this.random.nextInt(8) + (float) this.random.nextInt(9);
    }

    protected double dI() {
        return 0.4000000059604645D + this.random.nextDouble() * 0.2D + this.random.nextDouble() * 0.2D + this.random.nextDouble() * 0.2D;
    }

    protected double dJ() {
        return (0.44999998807907104D + this.random.nextDouble() * 0.3D + this.random.nextDouble() * 0.3D + this.random.nextDouble() * 0.3D) * 0.25D;
    }

    public boolean m_() {
        return false;
    }

    public float getHeadHeight() {
        return this.length;
    }

    public boolean dK() {
        return false;
    }

    public boolean f(ItemStack itemstack) {
        return false;
    }

    public boolean c(int i, ItemStack itemstack) {
        int j = i - 400;

        if (j >= 0 && j < 2 && j < this.inventoryChest.getSize()) {
            if (j == 0 && itemstack.getItem() != Items.SADDLE) {
                return false;
            } else if (j == 1 && (!this.dK() || !this.f(itemstack))) {
                return false;
            } else {
                this.inventoryChest.setItem(j, itemstack);
                this.dy();
                return true;
            }
        } else {
            int k = i - 500 + 2;

            if (k >= 2 && k < this.inventoryChest.getSize()) {
                this.inventoryChest.setItem(k, itemstack);
                return true;
            } else {
                return false;
            }
        }
    }

    @Nullable
    public Entity bw() {
        return this.bx().isEmpty() ? null : (Entity) this.bx().get(0);
    }

    @Nullable
    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, @Nullable GroupDataEntity groupdataentity) {
        groupdataentity = super.prepare(difficultydamagescaler, groupdataentity);
        if (this.random.nextInt(5) == 0) {
            this.setAgeRaw(-24000);
        }

        return groupdataentity;
    }
}
