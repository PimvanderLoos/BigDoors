package net.minecraft.server;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class EntityLiving extends Entity {

    private static final Logger a = LogManager.getLogger();
    private static final UUID b = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
    private static final AttributeModifier c = (new AttributeModifier(EntityLiving.b, "Sprinting speed boost", 0.30000001192092896D, 2)).a(false);
    protected static final DataWatcherObject<Byte> at = DataWatcher.a(EntityLiving.class, DataWatcherRegistry.a);
    public static final DataWatcherObject<Float> HEALTH = DataWatcher.a(EntityLiving.class, DataWatcherRegistry.c);
    private static final DataWatcherObject<Integer> g = DataWatcher.a(EntityLiving.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Boolean> h = DataWatcher.a(EntityLiving.class, DataWatcherRegistry.h);
    private static final DataWatcherObject<Integer> br = DataWatcher.a(EntityLiving.class, DataWatcherRegistry.b);
    private AttributeMapBase attributeMap;
    public CombatTracker combatTracker = new CombatTracker(this);
    public final Map<MobEffectList, MobEffect> effects = Maps.newHashMap();
    private final NonNullList<ItemStack> bv;
    private final NonNullList<ItemStack> bw;
    public boolean au;
    public EnumHand av;
    public int aw;
    public int ax;
    public int hurtTicks;
    public int az;
    public float aA;
    public int deathTicks;
    public float aC;
    public float aD;
    protected int aE;
    public float aF;
    public float aG;
    public float aH;
    public int maxNoDamageTicks;
    public float aJ;
    public float aK;
    public float aL;
    public float aM;
    public float aN;
    public float aO;
    public float aP;
    public float aQ;
    public float aR;
    public EntityHuman killer;
    protected int lastDamageByPlayerTime;
    protected boolean aU;
    protected int ticksFarFromPlayer;
    protected float aW;
    protected float aX;
    protected float aY;
    protected float aZ;
    protected float ba;
    protected int bb;
    public float lastDamage;
    protected boolean bd;
    public float be;
    public float bf;
    public float bg;
    public float bh;
    protected int bi;
    protected double bj;
    protected double bk;
    protected double bl;
    protected double bm;
    protected double bn;
    public boolean updateEffects;
    public EntityLiving lastDamager;
    public int hurtTimestamp;
    private EntityLiving bA;
    private int bB;
    private float bC;
    private int bD;
    private float bE;
    protected ItemStack activeItem;
    protected int bp;
    protected int bq;
    private BlockPosition bF;
    private DamageSource bG;
    private long bH;

    public void killEntity() {
        this.damageEntity(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
    }

    public EntityLiving(World world) {
        super(world);
        this.bv = NonNullList.a(2, ItemStack.a);
        this.bw = NonNullList.a(4, ItemStack.a);
        this.maxNoDamageTicks = 20;
        this.aR = 0.02F;
        this.updateEffects = true;
        this.activeItem = ItemStack.a;
        this.initAttributes();
        this.setHealth(this.getMaxHealth());
        this.i = true;
        this.aM = (float) ((Math.random() + 1.0D) * 0.009999999776482582D);
        this.setPosition(this.locX, this.locY, this.locZ);
        this.aL = (float) Math.random() * 12398.0F;
        this.yaw = (float) (Math.random() * 6.2831854820251465D);
        this.aP = this.yaw;
        this.P = 0.6F;
    }

    protected void i() {
        this.datawatcher.register(EntityLiving.at, Byte.valueOf((byte) 0));
        this.datawatcher.register(EntityLiving.g, Integer.valueOf(0));
        this.datawatcher.register(EntityLiving.h, Boolean.valueOf(false));
        this.datawatcher.register(EntityLiving.br, Integer.valueOf(0));
        this.datawatcher.register(EntityLiving.HEALTH, Float.valueOf(1.0F));
    }

    protected void initAttributes() {
        this.getAttributeMap().b(GenericAttributes.maxHealth);
        this.getAttributeMap().b(GenericAttributes.c);
        this.getAttributeMap().b(GenericAttributes.MOVEMENT_SPEED);
        this.getAttributeMap().b(GenericAttributes.h);
        this.getAttributeMap().b(GenericAttributes.i);
    }

    protected void a(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {
        if (!this.isInWater()) {
            this.aq();
        }

        if (!this.world.isClientSide && this.fallDistance > 3.0F && flag) {
            float f = (float) MathHelper.f(this.fallDistance - 3.0F);

            if (iblockdata.getMaterial() != Material.AIR) {
                double d1 = Math.min((double) (0.2F + f / 15.0F), 2.5D);
                int i = (int) (150.0D * d1);

                ((WorldServer) this.world).a(EnumParticle.BLOCK_DUST, this.locX, this.locY, this.locZ, i, 0.0D, 0.0D, 0.0D, 0.15000000596046448D, new int[] { Block.getCombinedId(iblockdata)});
            }
        }

        super.a(d0, flag, iblockdata, blockposition);
    }

    public boolean bN() {
        return false;
    }

    public void Y() {
        this.aC = this.aD;
        super.Y();
        this.world.methodProfiler.a("livingEntityBaseTick");
        boolean flag = this instanceof EntityHuman;

        if (this.isAlive()) {
            if (this.inBlock()) {
                this.damageEntity(DamageSource.STUCK, 1.0F);
            } else if (flag && !this.world.getWorldBorder().a(this.getBoundingBox())) {
                double d0 = this.world.getWorldBorder().a((Entity) this) + this.world.getWorldBorder().getDamageBuffer();

                if (d0 < 0.0D) {
                    double d1 = this.world.getWorldBorder().getDamageAmount();

                    if (d1 > 0.0D) {
                        this.damageEntity(DamageSource.STUCK, (float) Math.max(1, MathHelper.floor(-d0 * d1)));
                    }
                }
            }
        }

        if (this.isFireProof() || this.world.isClientSide) {
            this.extinguish();
        }

        boolean flag1 = flag && ((EntityHuman) this).abilities.isInvulnerable;

        if (this.isAlive()) {
            if (this.a(Material.WATER)) {
                if (!this.bN() && !this.hasEffect(MobEffects.WATER_BREATHING) && !flag1) {
                    this.setAirTicks(this.d(this.getAirTicks()));
                    if (this.getAirTicks() == -20) {
                        this.setAirTicks(0);

                        for (int i = 0; i < 8; ++i) {
                            float f = this.random.nextFloat() - this.random.nextFloat();
                            float f1 = this.random.nextFloat() - this.random.nextFloat();
                            float f2 = this.random.nextFloat() - this.random.nextFloat();

                            this.world.addParticle(EnumParticle.WATER_BUBBLE, this.locX + (double) f, this.locY + (double) f1, this.locZ + (double) f2, this.motX, this.motY, this.motZ, new int[0]);
                        }

                        this.damageEntity(DamageSource.DROWN, 2.0F);
                    }
                }

                if (!this.world.isClientSide && this.isPassenger() && this.bJ() instanceof EntityLiving) {
                    this.stopRiding();
                }
            } else {
                this.setAirTicks(300);
            }

            if (!this.world.isClientSide) {
                BlockPosition blockposition = new BlockPosition(this);

                if (!Objects.equal(this.bF, blockposition)) {
                    this.bF = blockposition;
                    this.b(blockposition);
                }
            }
        }

        if (this.isAlive() && this.an()) {
            this.extinguish();
        }

        this.aJ = this.aK;
        if (this.hurtTicks > 0) {
            --this.hurtTicks;
        }

        if (this.noDamageTicks > 0 && !(this instanceof EntityPlayer)) {
            --this.noDamageTicks;
        }

        if (this.getHealth() <= 0.0F) {
            this.bO();
        }

        if (this.lastDamageByPlayerTime > 0) {
            --this.lastDamageByPlayerTime;
        } else {
            this.killer = null;
        }

        if (this.bA != null && !this.bA.isAlive()) {
            this.bA = null;
        }

        if (this.lastDamager != null) {
            if (!this.lastDamager.isAlive()) {
                this.a((EntityLiving) null);
            } else if (this.ticksLived - this.hurtTimestamp > 100) {
                this.a((EntityLiving) null);
            }
        }

        this.tickPotionEffects();
        this.aZ = this.aY;
        this.aO = this.aN;
        this.aQ = this.aP;
        this.lastYaw = this.yaw;
        this.lastPitch = this.pitch;
        this.world.methodProfiler.b();
    }

    protected void b(BlockPosition blockposition) {
        int i = EnchantmentManager.a(Enchantments.j, this);

        if (i > 0) {
            EnchantmentFrostWalker.a(this, this.world, blockposition, i);
        }

    }

    public boolean isBaby() {
        return false;
    }

    protected void bO() {
        ++this.deathTicks;
        if (this.deathTicks == 20) {
            int i;

            if (!this.world.isClientSide && (this.alwaysGivesExp() || this.lastDamageByPlayerTime > 0 && this.isDropExperience() && this.world.getGameRules().getBoolean("doMobLoot"))) {
                i = this.getExpValue(this.killer);

                while (i > 0) {
                    int j = EntityExperienceOrb.getOrbValue(i);

                    i -= j;
                    this.world.addEntity(new EntityExperienceOrb(this.world, this.locX, this.locY, this.locZ, j));
                }
            }

            this.die();

            for (i = 0; i < 20; ++i) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;

                this.world.addParticle(EnumParticle.EXPLOSION_NORMAL, this.locX + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width, this.locY + (double) (this.random.nextFloat() * this.length), this.locZ + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width, d0, d1, d2, new int[0]);
            }
        }

    }

    protected boolean isDropExperience() {
        return !this.isBaby();
    }

    protected int d(int i) {
        int j = EnchantmentManager.getOxygenEnchantmentLevel(this);

        return j > 0 && this.random.nextInt(j + 1) > 0 ? i : i - 1;
    }

    protected int getExpValue(EntityHuman entityhuman) {
        return 0;
    }

    protected boolean alwaysGivesExp() {
        return false;
    }

    public Random getRandom() {
        return this.random;
    }

    @Nullable
    public EntityLiving getLastDamager() {
        return this.lastDamager;
    }

    public int bT() {
        return this.hurtTimestamp;
    }

    public void a(@Nullable EntityLiving entityliving) {
        this.lastDamager = entityliving;
        this.hurtTimestamp = this.ticksLived;
    }

    public EntityLiving bU() {
        return this.bA;
    }

    public int bV() {
        return this.bB;
    }

    public void z(Entity entity) {
        if (entity instanceof EntityLiving) {
            this.bA = (EntityLiving) entity;
        } else {
            this.bA = null;
        }

        this.bB = this.ticksLived;
    }

    public int bW() {
        return this.ticksFarFromPlayer;
    }

    protected void a_(ItemStack itemstack) {
        if (!itemstack.isEmpty()) {
            SoundEffect soundeffect = SoundEffects.q;
            Item item = itemstack.getItem();

            if (item instanceof ItemArmor) {
                soundeffect = ((ItemArmor) item).d().b();
            } else if (item == Items.cS) {
                soundeffect = SoundEffects.p;
            }

            this.a(soundeffect, 1.0F, 1.0F);
        }
    }

    public void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setFloat("Health", this.getHealth());
        nbttagcompound.setShort("HurtTime", (short) this.hurtTicks);
        nbttagcompound.setInt("HurtByTimestamp", this.hurtTimestamp);
        nbttagcompound.setShort("DeathTime", (short) this.deathTicks);
        nbttagcompound.setFloat("AbsorptionAmount", this.getAbsorptionHearts());
        EnumItemSlot[] aenumitemslot = EnumItemSlot.values();
        int i = aenumitemslot.length;

        int j;
        EnumItemSlot enumitemslot;
        ItemStack itemstack;

        for (j = 0; j < i; ++j) {
            enumitemslot = aenumitemslot[j];
            itemstack = this.getEquipment(enumitemslot);
            if (!itemstack.isEmpty()) {
                this.getAttributeMap().a(itemstack.a(enumitemslot));
            }
        }

        nbttagcompound.set("Attributes", GenericAttributes.a(this.getAttributeMap()));
        aenumitemslot = EnumItemSlot.values();
        i = aenumitemslot.length;

        for (j = 0; j < i; ++j) {
            enumitemslot = aenumitemslot[j];
            itemstack = this.getEquipment(enumitemslot);
            if (!itemstack.isEmpty()) {
                this.getAttributeMap().b(itemstack.a(enumitemslot));
            }
        }

        if (!this.effects.isEmpty()) {
            NBTTagList nbttaglist = new NBTTagList();
            Iterator iterator = this.effects.values().iterator();

            while (iterator.hasNext()) {
                MobEffect mobeffect = (MobEffect) iterator.next();

                nbttaglist.add(mobeffect.a(new NBTTagCompound()));
            }

            nbttagcompound.set("ActiveEffects", nbttaglist);
        }

        nbttagcompound.setBoolean("FallFlying", this.cP());
    }

    public void a(NBTTagCompound nbttagcompound) {
        this.setAbsorptionHearts(nbttagcompound.getFloat("AbsorptionAmount"));
        if (nbttagcompound.hasKeyOfType("Attributes", 9) && this.world != null && !this.world.isClientSide) {
            GenericAttributes.a(this.getAttributeMap(), nbttagcompound.getList("Attributes", 10));
        }

        if (nbttagcompound.hasKeyOfType("ActiveEffects", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList("ActiveEffects", 10);

            for (int i = 0; i < nbttaglist.size(); ++i) {
                NBTTagCompound nbttagcompound1 = nbttaglist.get(i);
                MobEffect mobeffect = MobEffect.b(nbttagcompound1);

                if (mobeffect != null) {
                    this.effects.put(mobeffect.getMobEffect(), mobeffect);
                }
            }
        }

        if (nbttagcompound.hasKeyOfType("Health", 99)) {
            this.setHealth(nbttagcompound.getFloat("Health"));
        }

        this.hurtTicks = nbttagcompound.getShort("HurtTime");
        this.deathTicks = nbttagcompound.getShort("DeathTime");
        this.hurtTimestamp = nbttagcompound.getInt("HurtByTimestamp");
        if (nbttagcompound.hasKeyOfType("Team", 8)) {
            String s = nbttagcompound.getString("Team");
            boolean flag = this.world.getScoreboard().addPlayerToTeam(this.bn(), s);

            if (!flag) {
                EntityLiving.a.warn("Unable to add mob to team \"" + s + "\" (that team probably doesn\'t exist)");
            }
        }

        if (nbttagcompound.getBoolean("FallFlying")) {
            this.setFlag(7, true);
        }

    }

    protected void tickPotionEffects() {
        Iterator iterator = this.effects.keySet().iterator();

        try {
            while (iterator.hasNext()) {
                MobEffectList mobeffectlist = (MobEffectList) iterator.next();
                MobEffect mobeffect = (MobEffect) this.effects.get(mobeffectlist);

                if (!mobeffect.tick(this)) {
                    if (!this.world.isClientSide) {
                        iterator.remove();
                        this.b(mobeffect);
                    }
                } else if (mobeffect.getDuration() % 600 == 0) {
                    this.a(mobeffect, false);
                }
            }
        } catch (ConcurrentModificationException concurrentmodificationexception) {
            ;
        }

        if (this.updateEffects) {
            if (!this.world.isClientSide) {
                this.G();
            }

            this.updateEffects = false;
        }

        int i = ((Integer) this.datawatcher.get(EntityLiving.g)).intValue();
        boolean flag = ((Boolean) this.datawatcher.get(EntityLiving.h)).booleanValue();

        if (i > 0) {
            boolean flag1;

            if (this.isInvisible()) {
                flag1 = this.random.nextInt(15) == 0;
            } else {
                flag1 = this.random.nextBoolean();
            }

            if (flag) {
                flag1 &= this.random.nextInt(5) == 0;
            }

            if (flag1 && i > 0) {
                double d0 = (double) (i >> 16 & 255) / 255.0D;
                double d1 = (double) (i >> 8 & 255) / 255.0D;
                double d2 = (double) (i >> 0 & 255) / 255.0D;

                this.world.addParticle(flag ? EnumParticle.SPELL_MOB_AMBIENT : EnumParticle.SPELL_MOB, this.locX + (this.random.nextDouble() - 0.5D) * (double) this.width, this.locY + this.random.nextDouble() * (double) this.length, this.locZ + (this.random.nextDouble() - 0.5D) * (double) this.width, d0, d1, d2, new int[0]);
            }
        }

    }

    protected void G() {
        if (this.effects.isEmpty()) {
            this.bY();
            this.setInvisible(false);
        } else {
            Collection collection = this.effects.values();

            this.datawatcher.set(EntityLiving.h, Boolean.valueOf(a(collection)));
            this.datawatcher.set(EntityLiving.g, Integer.valueOf(PotionUtil.a(collection)));
            this.setInvisible(this.hasEffect(MobEffects.INVISIBILITY));
        }

    }

    public static boolean a(Collection<MobEffect> collection) {
        Iterator iterator = collection.iterator();

        MobEffect mobeffect;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            mobeffect = (MobEffect) iterator.next();
        } while (mobeffect.isAmbient());

        return false;
    }

    protected void bY() {
        this.datawatcher.set(EntityLiving.h, Boolean.valueOf(false));
        this.datawatcher.set(EntityLiving.g, Integer.valueOf(0));
    }

    public void removeAllEffects() {
        if (!this.world.isClientSide) {
            Iterator iterator = this.effects.values().iterator();

            while (iterator.hasNext()) {
                this.b((MobEffect) iterator.next());
                iterator.remove();
            }

        }
    }

    public Collection<MobEffect> getEffects() {
        return this.effects.values();
    }

    public Map<MobEffectList, MobEffect> cb() {
        return this.effects;
    }

    public boolean hasEffect(MobEffectList mobeffectlist) {
        return this.effects.containsKey(mobeffectlist);
    }

    @Nullable
    public MobEffect getEffect(MobEffectList mobeffectlist) {
        return (MobEffect) this.effects.get(mobeffectlist);
    }

    public void addEffect(MobEffect mobeffect) {
        if (this.d(mobeffect)) {
            MobEffect mobeffect1 = (MobEffect) this.effects.get(mobeffect.getMobEffect());

            if (mobeffect1 == null) {
                this.effects.put(mobeffect.getMobEffect(), mobeffect);
                this.a(mobeffect);
            } else {
                mobeffect1.a(mobeffect);
                this.a(mobeffect1, true);
            }

        }
    }

    public boolean d(MobEffect mobeffect) {
        if (this.getMonsterType() == EnumMonsterType.UNDEAD) {
            MobEffectList mobeffectlist = mobeffect.getMobEffect();

            if (mobeffectlist == MobEffects.REGENERATION || mobeffectlist == MobEffects.POISON) {
                return false;
            }
        }

        return true;
    }

    public boolean cc() {
        return this.getMonsterType() == EnumMonsterType.UNDEAD;
    }

    @Nullable
    public MobEffect c(@Nullable MobEffectList mobeffectlist) {
        return (MobEffect) this.effects.remove(mobeffectlist);
    }

    public void removeEffect(MobEffectList mobeffectlist) {
        MobEffect mobeffect = this.c(mobeffectlist);

        if (mobeffect != null) {
            this.b(mobeffect);
        }

    }

    protected void a(MobEffect mobeffect) {
        this.updateEffects = true;
        if (!this.world.isClientSide) {
            mobeffect.getMobEffect().b(this, this.getAttributeMap(), mobeffect.getAmplifier());
        }

    }

    protected void a(MobEffect mobeffect, boolean flag) {
        this.updateEffects = true;
        if (flag && !this.world.isClientSide) {
            MobEffectList mobeffectlist = mobeffect.getMobEffect();

            mobeffectlist.a(this, this.getAttributeMap(), mobeffect.getAmplifier());
            mobeffectlist.b(this, this.getAttributeMap(), mobeffect.getAmplifier());
        }

    }

    protected void b(MobEffect mobeffect) {
        this.updateEffects = true;
        if (!this.world.isClientSide) {
            mobeffect.getMobEffect().a(this, this.getAttributeMap(), mobeffect.getAmplifier());
        }

    }

    public void heal(float f) {
        float f1 = this.getHealth();

        if (f1 > 0.0F) {
            this.setHealth(f1 + f);
        }

    }

    public final float getHealth() {
        return ((Float) this.datawatcher.get(EntityLiving.HEALTH)).floatValue();
    }

    public void setHealth(float f) {
        this.datawatcher.set(EntityLiving.HEALTH, Float.valueOf(MathHelper.a(f, 0.0F, this.getMaxHealth())));
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else if (this.world.isClientSide) {
            return false;
        } else {
            this.ticksFarFromPlayer = 0;
            if (this.getHealth() <= 0.0F) {
                return false;
            } else if (damagesource.o() && this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                return false;
            } else {
                float f1 = f;

                if ((damagesource == DamageSource.ANVIL || damagesource == DamageSource.FALLING_BLOCK) && !this.getEquipment(EnumItemSlot.HEAD).isEmpty()) {
                    this.getEquipment(EnumItemSlot.HEAD).damage((int) (f * 4.0F + this.random.nextFloat() * f * 2.0F), this);
                    f *= 0.75F;
                }

                boolean flag = false;

                if (f > 0.0F && this.applyBlockingModifier(damagesource)) {
                    this.damageShield(f);
                    f = 0.0F;
                    if (!damagesource.a()) {
                        Entity entity = damagesource.i();

                        if (entity instanceof EntityLiving) {
                            this.c((EntityLiving) entity);
                        }
                    }

                    flag = true;
                }

                this.aG = 1.5F;
                boolean flag1 = true;

                if ((float) this.noDamageTicks > (float) this.maxNoDamageTicks / 2.0F) {
                    if (f <= this.lastDamage) {
                        return false;
                    }

                    this.damageEntity0(damagesource, f - this.lastDamage);
                    this.lastDamage = f;
                    flag1 = false;
                } else {
                    this.lastDamage = f;
                    this.noDamageTicks = this.maxNoDamageTicks;
                    this.damageEntity0(damagesource, f);
                    this.az = 10;
                    this.hurtTicks = this.az;
                }

                this.aA = 0.0F;
                Entity entity1 = damagesource.getEntity();

                if (entity1 != null) {
                    if (entity1 instanceof EntityLiving) {
                        this.a((EntityLiving) entity1);
                    }

                    if (entity1 instanceof EntityHuman) {
                        this.lastDamageByPlayerTime = 100;
                        this.killer = (EntityHuman) entity1;
                    } else if (entity1 instanceof EntityWolf) {
                        EntityWolf entitywolf = (EntityWolf) entity1;

                        if (entitywolf.isTamed()) {
                            this.lastDamageByPlayerTime = 100;
                            this.killer = null;
                        }
                    }
                }

                if (flag1) {
                    if (flag) {
                        this.world.broadcastEntityEffect(this, (byte) 29);
                    } else if (damagesource instanceof EntityDamageSource && ((EntityDamageSource) damagesource).x()) {
                        this.world.broadcastEntityEffect(this, (byte) 33);
                    } else {
                        byte b0;

                        if (damagesource == DamageSource.DROWN) {
                            b0 = 36;
                        } else if (damagesource.o()) {
                            b0 = 37;
                        } else {
                            b0 = 2;
                        }

                        this.world.broadcastEntityEffect(this, b0);
                    }

                    if (damagesource != DamageSource.DROWN && (!flag || f > 0.0F)) {
                        this.ax();
                    }

                    if (entity1 != null) {
                        double d0 = entity1.locX - this.locX;

                        double d1;

                        for (d1 = entity1.locZ - this.locZ; d0 * d0 + d1 * d1 < 1.0E-4D; d1 = (Math.random() - Math.random()) * 0.01D) {
                            d0 = (Math.random() - Math.random()) * 0.01D;
                        }

                        this.aA = (float) (MathHelper.c(d1, d0) * 57.2957763671875D - (double) this.yaw);
                        this.a(entity1, 0.4F, d0, d1);
                    } else {
                        this.aA = (float) ((int) (Math.random() * 2.0D) * 180);
                    }
                }

                if (this.getHealth() <= 0.0F) {
                    if (!this.e(damagesource)) {
                        SoundEffect soundeffect = this.cf();

                        if (flag1 && soundeffect != null) {
                            this.a(soundeffect, this.cq(), this.cr());
                        }

                        this.die(damagesource);
                    }
                } else if (flag1) {
                    this.c(damagesource);
                }

                boolean flag2 = !flag || f > 0.0F;

                if (flag2) {
                    this.bG = damagesource;
                    this.bH = this.world.getTime();
                }

                if (this instanceof EntityPlayer) {
                    CriterionTriggers.h.a((EntityPlayer) this, damagesource, f1, f, flag);
                }

                if (entity1 instanceof EntityPlayer) {
                    CriterionTriggers.g.a((EntityPlayer) entity1, this, damagesource, f1, f, flag);
                }

                return flag2;
            }
        }
    }

    protected void c(EntityLiving entityliving) {
        entityliving.a(this, 0.5F, this.locX - entityliving.locX, this.locZ - entityliving.locZ);
    }

    private boolean e(DamageSource damagesource) {
        if (damagesource.ignoresInvulnerability()) {
            return false;
        } else {
            ItemStack itemstack = null;
            EnumHand[] aenumhand = EnumHand.values();
            int i = aenumhand.length;

            for (int j = 0; j < i; ++j) {
                EnumHand enumhand = aenumhand[j];
                ItemStack itemstack1 = this.b(enumhand);

                if (itemstack1.getItem() == Items.cY) {
                    itemstack = itemstack1.cloneItemStack();
                    itemstack1.subtract(1);
                    break;
                }
            }

            if (itemstack != null) {
                if (this instanceof EntityPlayer) {
                    EntityPlayer entityplayer = (EntityPlayer) this;

                    entityplayer.b(StatisticList.b(Items.cY));
                    CriterionTriggers.A.a(entityplayer, itemstack);
                }

                this.setHealth(1.0F);
                this.removeAllEffects();
                this.addEffect(new MobEffect(MobEffects.REGENERATION, 900, 1));
                this.addEffect(new MobEffect(MobEffects.ABSORBTION, 100, 1));
                this.world.broadcastEntityEffect(this, (byte) 35);
            }

            return itemstack != null;
        }
    }

    @Nullable
    public DamageSource ce() {
        if (this.world.getTime() - this.bH > 40L) {
            this.bG = null;
        }

        return this.bG;
    }

    protected void c(DamageSource damagesource) {
        SoundEffect soundeffect = this.d(damagesource);

        if (soundeffect != null) {
            this.a(soundeffect, this.cq(), this.cr());
        }

    }

    private boolean applyBlockingModifier(DamageSource damagesource) {
        if (!damagesource.ignoresArmor() && this.isBlocking()) {
            Vec3D vec3d = damagesource.v();

            if (vec3d != null) {
                Vec3D vec3d1 = this.e(1.0F);
                Vec3D vec3d2 = vec3d.a(new Vec3D(this.locX, this.locY, this.locZ)).a();

                vec3d2 = new Vec3D(vec3d2.x, 0.0D, vec3d2.z);
                if (vec3d2.b(vec3d1) < 0.0D) {
                    return true;
                }
            }
        }

        return false;
    }

    public void b(ItemStack itemstack) {
        this.a(SoundEffects.dw, 0.8F, 0.8F + this.world.random.nextFloat() * 0.4F);

        for (int i = 0; i < 5; ++i) {
            Vec3D vec3d = new Vec3D(((double) this.random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);

            vec3d = vec3d.a(-this.pitch * 0.017453292F);
            vec3d = vec3d.b(-this.yaw * 0.017453292F);
            double d0 = (double) (-this.random.nextFloat()) * 0.6D - 0.3D;
            Vec3D vec3d1 = new Vec3D(((double) this.random.nextFloat() - 0.5D) * 0.3D, d0, 0.6D);

            vec3d1 = vec3d1.a(-this.pitch * 0.017453292F);
            vec3d1 = vec3d1.b(-this.yaw * 0.017453292F);
            vec3d1 = vec3d1.add(this.locX, this.locY + (double) this.getHeadHeight(), this.locZ);
            this.world.addParticle(EnumParticle.ITEM_CRACK, vec3d1.x, vec3d1.y, vec3d1.z, vec3d.x, vec3d.y + 0.05D, vec3d.z, new int[] { Item.getId(itemstack.getItem())});
        }

    }

    public void die(DamageSource damagesource) {
        if (!this.aU) {
            Entity entity = damagesource.getEntity();
            EntityLiving entityliving = this.ci();

            if (this.bb >= 0 && entityliving != null) {
                entityliving.a(this, this.bb, damagesource);
            }

            if (entity != null) {
                entity.b(this);
            }

            this.aU = true;
            this.getCombatTracker().g();
            if (!this.world.isClientSide) {
                int i = 0;

                if (entity instanceof EntityHuman) {
                    i = EnchantmentManager.g((EntityLiving) entity);
                }

                if (this.isDropExperience() && this.world.getGameRules().getBoolean("doMobLoot")) {
                    boolean flag = this.lastDamageByPlayerTime > 0;

                    this.a(flag, i, damagesource);
                }
            }

            this.world.broadcastEntityEffect(this, (byte) 3);
        }
    }

    protected void a(boolean flag, int i, DamageSource damagesource) {
        this.dropDeathLoot(flag, i);
        this.dropEquipment(flag, i);
    }

    protected void dropEquipment(boolean flag, int i) {}

    public void a(Entity entity, float f, double d0, double d1) {
        if (this.random.nextDouble() >= this.getAttributeInstance(GenericAttributes.c).getValue()) {
            this.impulse = true;
            float f1 = MathHelper.sqrt(d0 * d0 + d1 * d1);

            this.motX /= 2.0D;
            this.motZ /= 2.0D;
            this.motX -= d0 / (double) f1 * (double) f;
            this.motZ -= d1 / (double) f1 * (double) f;
            if (this.onGround) {
                this.motY /= 2.0D;
                this.motY += (double) f;
                if (this.motY > 0.4000000059604645D) {
                    this.motY = 0.4000000059604645D;
                }
            }

        }
    }

    @Nullable
    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.bX;
    }

    @Nullable
    protected SoundEffect cf() {
        return SoundEffects.bS;
    }

    protected SoundEffect e(int i) {
        return i > 4 ? SoundEffects.bQ : SoundEffects.bY;
    }

    protected void dropDeathLoot(boolean flag, int i) {}

    public boolean m_() {
        int i = MathHelper.floor(this.locX);
        int j = MathHelper.floor(this.getBoundingBox().b);
        int k = MathHelper.floor(this.locZ);

        if (this instanceof EntityHuman && ((EntityHuman) this).isSpectator()) {
            return false;
        } else {
            BlockPosition blockposition = new BlockPosition(i, j, k);
            IBlockData iblockdata = this.world.getType(blockposition);
            Block block = iblockdata.getBlock();

            return block != Blocks.LADDER && block != Blocks.VINE ? block instanceof BlockTrapdoor && this.a(blockposition, iblockdata) : true;
        }
    }

    private boolean a(BlockPosition blockposition, IBlockData iblockdata) {
        if (((Boolean) iblockdata.get(BlockTrapdoor.OPEN)).booleanValue()) {
            IBlockData iblockdata1 = this.world.getType(blockposition.down());

            if (iblockdata1.getBlock() == Blocks.LADDER && iblockdata1.get(BlockLadder.FACING) == iblockdata.get(BlockTrapdoor.FACING)) {
                return true;
            }
        }

        return false;
    }

    public boolean isAlive() {
        return !this.dead && this.getHealth() > 0.0F;
    }

    public void e(float f, float f1) {
        super.e(f, f1);
        MobEffect mobeffect = this.getEffect(MobEffects.JUMP);
        float f2 = mobeffect == null ? 0.0F : (float) (mobeffect.getAmplifier() + 1);
        int i = MathHelper.f((f - 3.0F - f2) * f1);

        if (i > 0) {
            this.a(this.e(i), 1.0F, 1.0F);
            this.damageEntity(DamageSource.FALL, (float) i);
            int j = MathHelper.floor(this.locX);
            int k = MathHelper.floor(this.locY - 0.20000000298023224D);
            int l = MathHelper.floor(this.locZ);
            IBlockData iblockdata = this.world.getType(new BlockPosition(j, k, l));

            if (iblockdata.getMaterial() != Material.AIR) {
                SoundEffectType soundeffecttype = iblockdata.getBlock().getStepSound();

                this.a(soundeffecttype.g(), soundeffecttype.a() * 0.5F, soundeffecttype.b() * 0.75F);
            }
        }

    }

    public int getArmorStrength() {
        AttributeInstance attributeinstance = this.getAttributeInstance(GenericAttributes.h);

        return MathHelper.floor(attributeinstance.getValue());
    }

    protected void damageArmor(float f) {}

    protected void damageShield(float f) {}

    protected float applyArmorModifier(DamageSource damagesource, float f) {
        if (!damagesource.ignoresArmor()) {
            this.damageArmor(f);
            f = CombatMath.a(f, (float) this.getArmorStrength(), (float) this.getAttributeInstance(GenericAttributes.i).getValue());
        }

        return f;
    }

    protected float applyMagicModifier(DamageSource damagesource, float f) {
        if (damagesource.isStarvation()) {
            return f;
        } else {
            int i;

            if (this.hasEffect(MobEffects.RESISTANCE) && damagesource != DamageSource.OUT_OF_WORLD) {
                i = (this.getEffect(MobEffects.RESISTANCE).getAmplifier() + 1) * 5;
                int j = 25 - i;
                float f1 = f * (float) j;

                f = f1 / 25.0F;
            }

            if (f <= 0.0F) {
                return 0.0F;
            } else {
                i = EnchantmentManager.a(this.getArmorItems(), damagesource);
                if (i > 0) {
                    f = CombatMath.a(f, (float) i);
                }

                return f;
            }
        }
    }

    protected void damageEntity0(DamageSource damagesource, float f) {
        if (!this.isInvulnerable(damagesource)) {
            f = this.applyArmorModifier(damagesource, f);
            f = this.applyMagicModifier(damagesource, f);
            float f1 = f;

            f = Math.max(f - this.getAbsorptionHearts(), 0.0F);
            this.setAbsorptionHearts(this.getAbsorptionHearts() - (f1 - f));
            if (f != 0.0F) {
                float f2 = this.getHealth();

                this.setHealth(f2 - f);
                this.getCombatTracker().trackDamage(damagesource, f2, f);
                this.setAbsorptionHearts(this.getAbsorptionHearts() - f);
            }
        }
    }

    public CombatTracker getCombatTracker() {
        return this.combatTracker;
    }

    @Nullable
    public EntityLiving ci() {
        return (EntityLiving) (this.combatTracker.c() != null ? this.combatTracker.c() : (this.killer != null ? this.killer : (this.lastDamager != null ? this.lastDamager : null)));
    }

    public final float getMaxHealth() {
        return (float) this.getAttributeInstance(GenericAttributes.maxHealth).getValue();
    }

    public final int getArrowCount() {
        return ((Integer) this.datawatcher.get(EntityLiving.br)).intValue();
    }

    public final void setArrowCount(int i) {
        this.datawatcher.set(EntityLiving.br, Integer.valueOf(i));
    }

    private int p() {
        return this.hasEffect(MobEffects.FASTER_DIG) ? 6 - (1 + this.getEffect(MobEffects.FASTER_DIG).getAmplifier()) : (this.hasEffect(MobEffects.SLOWER_DIG) ? 6 + (1 + this.getEffect(MobEffects.SLOWER_DIG).getAmplifier()) * 2 : 6);
    }

    public void a(EnumHand enumhand) {
        if (!this.au || this.aw >= this.p() / 2 || this.aw < 0) {
            this.aw = -1;
            this.au = true;
            this.av = enumhand;
            if (this.world instanceof WorldServer) {
                ((WorldServer) this.world).getTracker().a((Entity) this, (Packet) (new PacketPlayOutAnimation(this, enumhand == EnumHand.MAIN_HAND ? 0 : 3)));
            }
        }

    }

    protected void ac() {
        this.damageEntity(DamageSource.OUT_OF_WORLD, 4.0F);
    }

    protected void cl() {
        int i = this.p();

        if (this.au) {
            ++this.aw;
            if (this.aw >= i) {
                this.aw = 0;
                this.au = false;
            }
        } else {
            this.aw = 0;
        }

        this.aD = (float) this.aw / (float) i;
    }

    public AttributeInstance getAttributeInstance(IAttribute iattribute) {
        return this.getAttributeMap().a(iattribute);
    }

    public AttributeMapBase getAttributeMap() {
        if (this.attributeMap == null) {
            this.attributeMap = new AttributeMapServer();
        }

        return this.attributeMap;
    }

    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.UNDEFINED;
    }

    public ItemStack getItemInMainHand() {
        return this.getEquipment(EnumItemSlot.MAINHAND);
    }

    public ItemStack getItemInOffHand() {
        return this.getEquipment(EnumItemSlot.OFFHAND);
    }

    public ItemStack b(EnumHand enumhand) {
        if (enumhand == EnumHand.MAIN_HAND) {
            return this.getEquipment(EnumItemSlot.MAINHAND);
        } else if (enumhand == EnumHand.OFF_HAND) {
            return this.getEquipment(EnumItemSlot.OFFHAND);
        } else {
            throw new IllegalArgumentException("Invalid hand " + enumhand);
        }
    }

    public void a(EnumHand enumhand, ItemStack itemstack) {
        if (enumhand == EnumHand.MAIN_HAND) {
            this.setSlot(EnumItemSlot.MAINHAND, itemstack);
        } else {
            if (enumhand != EnumHand.OFF_HAND) {
                throw new IllegalArgumentException("Invalid hand " + enumhand);
            }

            this.setSlot(EnumItemSlot.OFFHAND, itemstack);
        }

    }

    public boolean a(EnumItemSlot enumitemslot) {
        return !this.getEquipment(enumitemslot).isEmpty();
    }

    public abstract Iterable<ItemStack> getArmorItems();

    public abstract ItemStack getEquipment(EnumItemSlot enumitemslot);

    public abstract void setSlot(EnumItemSlot enumitemslot, ItemStack itemstack);

    public void setSprinting(boolean flag) {
        super.setSprinting(flag);
        AttributeInstance attributeinstance = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);

        if (attributeinstance.a(EntityLiving.b) != null) {
            attributeinstance.c(EntityLiving.c);
        }

        if (flag) {
            attributeinstance.b(EntityLiving.c);
        }

    }

    protected float cq() {
        return 1.0F;
    }

    protected float cr() {
        return this.isBaby() ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.5F : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
    }

    protected boolean isFrozen() {
        return this.getHealth() <= 0.0F;
    }

    public void A(Entity entity) {
        double d0;

        if (!(entity instanceof EntityBoat) && !(entity instanceof EntityHorseAbstract)) {
            double d1 = entity.locX;
            double d2 = entity.getBoundingBox().b + (double) entity.length;

            d0 = entity.locZ;
            EnumDirection enumdirection = entity.bu();

            if (enumdirection != null) {
                EnumDirection enumdirection1 = enumdirection.e();
                int[][] aint = new int[][] { { 0, 1}, { 0, -1}, { -1, 1}, { -1, -1}, { 1, 1}, { 1, -1}, { -1, 0}, { 1, 0}, { 0, 1}};
                double d3 = Math.floor(this.locX) + 0.5D;
                double d4 = Math.floor(this.locZ) + 0.5D;
                double d5 = this.getBoundingBox().d - this.getBoundingBox().a;
                double d6 = this.getBoundingBox().f - this.getBoundingBox().c;
                AxisAlignedBB axisalignedbb = new AxisAlignedBB(d3 - d5 / 2.0D, entity.getBoundingBox().b, d4 - d6 / 2.0D, d3 + d5 / 2.0D, Math.floor(entity.getBoundingBox().b) + (double) this.length, d4 + d6 / 2.0D);
                int[][] aint1 = aint;
                int i = aint.length;

                for (int j = 0; j < i; ++j) {
                    int[] aint2 = aint1[j];
                    double d7 = (double) (enumdirection.getAdjacentX() * aint2[0] + enumdirection1.getAdjacentX() * aint2[1]);
                    double d8 = (double) (enumdirection.getAdjacentZ() * aint2[0] + enumdirection1.getAdjacentZ() * aint2[1]);
                    double d9 = d3 + d7;
                    double d10 = d4 + d8;
                    AxisAlignedBB axisalignedbb1 = axisalignedbb.d(d7, 0.0D, d8);

                    if (!this.world.a(axisalignedbb1)) {
                        if (this.world.getType(new BlockPosition(d9, this.locY, d10)).q()) {
                            this.enderTeleportTo(d9, this.locY + 1.0D, d10);
                            return;
                        }

                        BlockPosition blockposition = new BlockPosition(d9, this.locY - 1.0D, d10);

                        if (this.world.getType(blockposition).q() || this.world.getType(blockposition).getMaterial() == Material.WATER) {
                            d1 = d9;
                            d2 = this.locY + 1.0D;
                            d0 = d10;
                        }
                    } else if (!this.world.a(axisalignedbb1.d(0.0D, 1.0D, 0.0D)) && this.world.getType(new BlockPosition(d9, this.locY + 1.0D, d10)).q()) {
                        d1 = d9;
                        d2 = this.locY + 2.0D;
                        d0 = d10;
                    }
                }
            }

            this.enderTeleportTo(d1, d2, d0);
        } else {
            double d11 = (double) (this.width / 2.0F + entity.width / 2.0F) + 0.4D;
            float f;

            if (entity instanceof EntityBoat) {
                f = 0.0F;
            } else {
                f = 1.5707964F * (float) (this.getMainHand() == EnumMainHand.RIGHT ? -1 : 1);
            }

            float f1 = -MathHelper.sin(-this.yaw * 0.017453292F - 3.1415927F + f);
            float f2 = -MathHelper.cos(-this.yaw * 0.017453292F - 3.1415927F + f);

            d0 = Math.abs(f1) > Math.abs(f2) ? d11 / (double) Math.abs(f1) : d11 / (double) Math.abs(f2);
            double d12 = this.locX + (double) f1 * d0;
            double d13 = this.locZ + (double) f2 * d0;

            this.setPosition(d12, entity.locY + (double) entity.length + 0.001D, d13);
            if (this.world.a(this.getBoundingBox())) {
                this.setPosition(d12, entity.locY + (double) entity.length + 1.001D, d13);
                if (this.world.a(this.getBoundingBox())) {
                    this.setPosition(entity.locX, entity.locY + (double) this.length + 0.001D, entity.locZ);
                }
            }
        }
    }

    protected float ct() {
        return 0.42F;
    }

    protected void cu() {
        this.motY = (double) this.ct();
        if (this.hasEffect(MobEffects.JUMP)) {
            this.motY += (double) ((float) (this.getEffect(MobEffects.JUMP).getAmplifier() + 1) * 0.1F);
        }

        if (this.isSprinting()) {
            float f = this.yaw * 0.017453292F;

            this.motX -= (double) (MathHelper.sin(f) * 0.2F);
            this.motZ += (double) (MathHelper.cos(f) * 0.2F);
        }

        this.impulse = true;
    }

    protected void cv() {
        this.motY += 0.03999999910593033D;
    }

    protected void cw() {
        this.motY += 0.03999999910593033D;
    }

    protected float cx() {
        return 0.8F;
    }

    public void a(float f, float f1, float f2) {
        double d0;
        double d1;
        double d2;

        if (this.cC() || this.bI()) {
            float f3;
            float f4;
            float f5;

            if (this.isInWater() && (!(this instanceof EntityHuman) || !((EntityHuman) this).abilities.isFlying)) {
                d2 = this.locY;
                f4 = this.cx();
                f3 = 0.02F;
                f5 = (float) EnchantmentManager.e(this);
                if (f5 > 3.0F) {
                    f5 = 3.0F;
                }

                if (!this.onGround) {
                    f5 *= 0.5F;
                }

                if (f5 > 0.0F) {
                    f4 += (0.54600006F - f4) * f5 / 3.0F;
                    f3 += (this.cy() - f3) * f5 / 3.0F;
                }

                this.b(f, f1, f2, f3);
                this.move(EnumMoveType.SELF, this.motX, this.motY, this.motZ);
                this.motX *= (double) f4;
                this.motY *= 0.800000011920929D;
                this.motZ *= (double) f4;
                if (!this.isNoGravity()) {
                    this.motY -= 0.02D;
                }

                if (this.positionChanged && this.c(this.motX, this.motY + 0.6000000238418579D - this.locY + d2, this.motZ)) {
                    this.motY = 0.30000001192092896D;
                }
            } else if (this.au() && (!(this instanceof EntityHuman) || !((EntityHuman) this).abilities.isFlying)) {
                d2 = this.locY;
                this.b(f, f1, f2, 0.02F);
                this.move(EnumMoveType.SELF, this.motX, this.motY, this.motZ);
                this.motX *= 0.5D;
                this.motY *= 0.5D;
                this.motZ *= 0.5D;
                if (!this.isNoGravity()) {
                    this.motY -= 0.02D;
                }

                if (this.positionChanged && this.c(this.motX, this.motY + 0.6000000238418579D - this.locY + d2, this.motZ)) {
                    this.motY = 0.30000001192092896D;
                }
            } else if (this.cP()) {
                if (this.motY > -0.5D) {
                    this.fallDistance = 1.0F;
                }

                Vec3D vec3d = this.aJ();
                float f6 = this.pitch * 0.017453292F;

                d0 = Math.sqrt(vec3d.x * vec3d.x + vec3d.z * vec3d.z);
                d1 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);
                double d3 = vec3d.b();
                float f7 = MathHelper.cos(f6);

                f7 = (float) ((double) f7 * (double) f7 * Math.min(1.0D, d3 / 0.4D));
                this.motY += -0.08D + (double) f7 * 0.06D;
                double d4;

                if (this.motY < 0.0D && d0 > 0.0D) {
                    d4 = this.motY * -0.1D * (double) f7;
                    this.motY += d4;
                    this.motX += vec3d.x * d4 / d0;
                    this.motZ += vec3d.z * d4 / d0;
                }

                if (f6 < 0.0F) {
                    d4 = d1 * (double) (-MathHelper.sin(f6)) * 0.04D;
                    this.motY += d4 * 3.2D;
                    this.motX -= vec3d.x * d4 / d0;
                    this.motZ -= vec3d.z * d4 / d0;
                }

                if (d0 > 0.0D) {
                    this.motX += (vec3d.x / d0 * d1 - this.motX) * 0.1D;
                    this.motZ += (vec3d.z / d0 * d1 - this.motZ) * 0.1D;
                }

                this.motX *= 0.9900000095367432D;
                this.motY *= 0.9800000190734863D;
                this.motZ *= 0.9900000095367432D;
                this.move(EnumMoveType.SELF, this.motX, this.motY, this.motZ);
                if (this.positionChanged && !this.world.isClientSide) {
                    d4 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);
                    double d5 = d1 - d4;
                    float f8 = (float) (d5 * 10.0D - 3.0D);

                    if (f8 > 0.0F) {
                        this.a(this.e((int) f8), 1.0F, 1.0F);
                        this.damageEntity(DamageSource.FLY_INTO_WALL, f8);
                    }
                }

                if (this.onGround && !this.world.isClientSide) {
                    this.setFlag(7, false);
                }
            } else {
                float f9 = 0.91F;
                BlockPosition.PooledBlockPosition blockposition_pooledblockposition = BlockPosition.PooledBlockPosition.d(this.locX, this.getBoundingBox().b - 1.0D, this.locZ);

                if (this.onGround) {
                    f9 = this.world.getType(blockposition_pooledblockposition).getBlock().frictionFactor * 0.91F;
                }

                f4 = 0.16277136F / (f9 * f9 * f9);
                if (this.onGround) {
                    f3 = this.cy() * f4;
                } else {
                    f3 = this.aR;
                }

                this.b(f, f1, f2, f3);
                f9 = 0.91F;
                if (this.onGround) {
                    f9 = this.world.getType(blockposition_pooledblockposition.e(this.locX, this.getBoundingBox().b - 1.0D, this.locZ)).getBlock().frictionFactor * 0.91F;
                }

                if (this.m_()) {
                    f5 = 0.15F;
                    this.motX = MathHelper.a(this.motX, -0.15000000596046448D, 0.15000000596046448D);
                    this.motZ = MathHelper.a(this.motZ, -0.15000000596046448D, 0.15000000596046448D);
                    this.fallDistance = 0.0F;
                    if (this.motY < -0.15D) {
                        this.motY = -0.15D;
                    }

                    boolean flag = this.isSneaking() && this instanceof EntityHuman;

                    if (flag && this.motY < 0.0D) {
                        this.motY = 0.0D;
                    }
                }

                this.move(EnumMoveType.SELF, this.motX, this.motY, this.motZ);
                if (this.positionChanged && this.m_()) {
                    this.motY = 0.2D;
                }

                if (this.hasEffect(MobEffects.LEVITATION)) {
                    this.motY += (0.05D * (double) (this.getEffect(MobEffects.LEVITATION).getAmplifier() + 1) - this.motY) * 0.2D;
                } else {
                    blockposition_pooledblockposition.e(this.locX, 0.0D, this.locZ);
                    if (this.world.isClientSide && (!this.world.isLoaded(blockposition_pooledblockposition) || !this.world.getChunkAtWorldCoords(blockposition_pooledblockposition).p())) {
                        if (this.locY > 0.0D) {
                            this.motY = -0.1D;
                        } else {
                            this.motY = 0.0D;
                        }
                    } else if (!this.isNoGravity()) {
                        this.motY -= 0.08D;
                    }
                }

                this.motY *= 0.9800000190734863D;
                this.motX *= (double) f9;
                this.motZ *= (double) f9;
                blockposition_pooledblockposition.t();
            }
        }

        this.aF = this.aG;
        d2 = this.locX - this.lastX;
        d0 = this.locZ - this.lastZ;
        d1 = this instanceof EntityBird ? this.locY - this.lastY : 0.0D;
        float f10 = MathHelper.sqrt(d2 * d2 + d1 * d1 + d0 * d0) * 4.0F;

        if (f10 > 1.0F) {
            f10 = 1.0F;
        }

        this.aG += (f10 - this.aG) * 0.4F;
        this.aH += this.aG;
    }

    public float cy() {
        return this.bC;
    }

    public void k(float f) {
        this.bC = f;
    }

    public boolean B(Entity entity) {
        this.z(entity);
        return false;
    }

    public boolean isSleeping() {
        return false;
    }

    public void B_() {
        super.B_();
        this.cI();
        if (!this.world.isClientSide) {
            int i = this.getArrowCount();

            if (i > 0) {
                if (this.ax <= 0) {
                    this.ax = 20 * (30 - i);
                }

                --this.ax;
                if (this.ax <= 0) {
                    this.setArrowCount(i - 1);
                }
            }

            EnumItemSlot[] aenumitemslot = EnumItemSlot.values();
            int j = aenumitemslot.length;

            for (int k = 0; k < j; ++k) {
                EnumItemSlot enumitemslot = aenumitemslot[k];
                ItemStack itemstack;

                switch (enumitemslot.a()) {
                case HAND:
                    itemstack = (ItemStack) this.bv.get(enumitemslot.b());
                    break;

                case ARMOR:
                    itemstack = (ItemStack) this.bw.get(enumitemslot.b());
                    break;

                default:
                    continue;
                }

                ItemStack itemstack1 = this.getEquipment(enumitemslot);

                if (!ItemStack.matches(itemstack1, itemstack)) {
                    ((WorldServer) this.world).getTracker().a((Entity) this, (Packet) (new PacketPlayOutEntityEquipment(this.getId(), enumitemslot, itemstack1)));
                    if (!itemstack.isEmpty()) {
                        this.getAttributeMap().a(itemstack.a(enumitemslot));
                    }

                    if (!itemstack1.isEmpty()) {
                        this.getAttributeMap().b(itemstack1.a(enumitemslot));
                    }

                    switch (enumitemslot.a()) {
                    case HAND:
                        this.bv.set(enumitemslot.b(), itemstack1.isEmpty() ? ItemStack.a : itemstack1.cloneItemStack());
                        break;

                    case ARMOR:
                        this.bw.set(enumitemslot.b(), itemstack1.isEmpty() ? ItemStack.a : itemstack1.cloneItemStack());
                    }
                }
            }

            if (this.ticksLived % 20 == 0) {
                this.getCombatTracker().g();
            }

            if (!this.glowing) {
                boolean flag = this.hasEffect(MobEffects.GLOWING);

                if (this.getFlag(6) != flag) {
                    this.setFlag(6, flag);
                }
            }
        }

        this.n();
        double d0 = this.locX - this.lastX;
        double d1 = this.locZ - this.lastZ;
        float f = (float) (d0 * d0 + d1 * d1);
        float f1 = this.aN;
        float f2 = 0.0F;

        this.aW = this.aX;
        float f3 = 0.0F;

        if (f > 0.0025000002F) {
            f3 = 1.0F;
            f2 = (float) Math.sqrt((double) f) * 3.0F;
            float f4 = (float) MathHelper.c(d1, d0) * 57.295776F - 90.0F;
            float f5 = MathHelper.e(MathHelper.g(this.yaw) - f4);

            if (95.0F < f5 && f5 < 265.0F) {
                f1 = f4 - 180.0F;
            } else {
                f1 = f4;
            }
        }

        if (this.aD > 0.0F) {
            f1 = this.yaw;
        }

        if (!this.onGround) {
            f3 = 0.0F;
        }

        this.aX += (f3 - this.aX) * 0.3F;
        this.world.methodProfiler.a("headTurn");
        f2 = this.g(f1, f2);
        this.world.methodProfiler.b();
        this.world.methodProfiler.a("rangeChecks");

        while (this.yaw - this.lastYaw < -180.0F) {
            this.lastYaw -= 360.0F;
        }

        while (this.yaw - this.lastYaw >= 180.0F) {
            this.lastYaw += 360.0F;
        }

        while (this.aN - this.aO < -180.0F) {
            this.aO -= 360.0F;
        }

        while (this.aN - this.aO >= 180.0F) {
            this.aO += 360.0F;
        }

        while (this.pitch - this.lastPitch < -180.0F) {
            this.lastPitch -= 360.0F;
        }

        while (this.pitch - this.lastPitch >= 180.0F) {
            this.lastPitch += 360.0F;
        }

        while (this.aP - this.aQ < -180.0F) {
            this.aQ -= 360.0F;
        }

        while (this.aP - this.aQ >= 180.0F) {
            this.aQ += 360.0F;
        }

        this.world.methodProfiler.b();
        this.aY += f2;
        if (this.cP()) {
            ++this.bq;
        } else {
            this.bq = 0;
        }

    }

    protected float g(float f, float f1) {
        float f2 = MathHelper.g(f - this.aN);

        this.aN += f2 * 0.3F;
        float f3 = MathHelper.g(this.yaw - this.aN);
        boolean flag = f3 < -90.0F || f3 >= 90.0F;

        if (f3 < -75.0F) {
            f3 = -75.0F;
        }

        if (f3 >= 75.0F) {
            f3 = 75.0F;
        }

        this.aN = this.yaw - f3;
        if (f3 * f3 > 2500.0F) {
            this.aN += f3 * 0.2F;
        }

        if (flag) {
            f1 *= -1.0F;
        }

        return f1;
    }

    public void n() {
        if (this.bD > 0) {
            --this.bD;
        }

        if (this.bi > 0 && !this.bI()) {
            double d0 = this.locX + (this.bj - this.locX) / (double) this.bi;
            double d1 = this.locY + (this.bk - this.locY) / (double) this.bi;
            double d2 = this.locZ + (this.bl - this.locZ) / (double) this.bi;
            double d3 = MathHelper.g(this.bm - (double) this.yaw);

            this.yaw = (float) ((double) this.yaw + d3 / (double) this.bi);
            this.pitch = (float) ((double) this.pitch + (this.bn - (double) this.pitch) / (double) this.bi);
            --this.bi;
            this.setPosition(d0, d1, d2);
            this.setYawPitch(this.yaw, this.pitch);
        } else if (!this.cC()) {
            this.motX *= 0.98D;
            this.motY *= 0.98D;
            this.motZ *= 0.98D;
        }

        if (Math.abs(this.motX) < 0.003D) {
            this.motX = 0.0D;
        }

        if (Math.abs(this.motY) < 0.003D) {
            this.motY = 0.0D;
        }

        if (Math.abs(this.motZ) < 0.003D) {
            this.motZ = 0.0D;
        }

        this.world.methodProfiler.a("ai");
        if (this.isFrozen()) {
            this.bd = false;
            this.be = 0.0F;
            this.bg = 0.0F;
            this.bh = 0.0F;
        } else if (this.cC()) {
            this.world.methodProfiler.a("newAi");
            this.doTick();
            this.world.methodProfiler.b();
        }

        this.world.methodProfiler.b();
        this.world.methodProfiler.a("jump");
        if (this.bd) {
            if (this.isInWater()) {
                this.cv();
            } else if (this.au()) {
                this.cw();
            } else if (this.onGround && this.bD == 0) {
                this.cu();
                this.bD = 10;
            }
        } else {
            this.bD = 0;
        }

        this.world.methodProfiler.b();
        this.world.methodProfiler.a("travel");
        this.be *= 0.98F;
        this.bg *= 0.98F;
        this.bh *= 0.9F;
        this.r();
        this.a(this.be, this.bf, this.bg);
        this.world.methodProfiler.b();
        this.world.methodProfiler.a("push");
        this.cB();
        this.world.methodProfiler.b();
    }

    private void r() {
        boolean flag = this.getFlag(7);

        if (flag && !this.onGround && !this.isPassenger()) {
            ItemStack itemstack = this.getEquipment(EnumItemSlot.CHEST);

            if (itemstack.getItem() == Items.cS && ItemElytra.d(itemstack)) {
                flag = true;
                if (!this.world.isClientSide && (this.bq + 1) % 20 == 0) {
                    itemstack.damage(1, this);
                }
            } else {
                flag = false;
            }
        } else {
            flag = false;
        }

        if (!this.world.isClientSide) {
            this.setFlag(7, flag);
        }

    }

    protected void doTick() {}

    protected void cB() {
        List list = this.world.getEntities(this, this.getBoundingBox(), IEntitySelector.a(this));

        if (!list.isEmpty()) {
            int i = this.world.getGameRules().c("maxEntityCramming");
            int j;

            if (i > 0 && list.size() > i - 1 && this.random.nextInt(4) == 0) {
                j = 0;

                for (int k = 0; k < list.size(); ++k) {
                    if (!((Entity) list.get(k)).isPassenger()) {
                        ++j;
                    }
                }

                if (j > i - 1) {
                    this.damageEntity(DamageSource.CRAMMING, 6.0F);
                }
            }

            for (j = 0; j < list.size(); ++j) {
                Entity entity = (Entity) list.get(j);

                this.C(entity);
            }
        }

    }

    protected void C(Entity entity) {
        entity.collide(this);
    }

    public void stopRiding() {
        Entity entity = this.bJ();

        super.stopRiding();
        if (entity != null && entity != this.bJ() && !this.world.isClientSide) {
            this.A(entity);
        }

    }

    public void aE() {
        super.aE();
        this.aW = this.aX;
        this.aX = 0.0F;
        this.fallDistance = 0.0F;
    }

    public void l(boolean flag) {
        this.bd = flag;
    }

    public void receive(Entity entity, int i) {
        if (!entity.dead && !this.world.isClientSide) {
            EntityTracker entitytracker = ((WorldServer) this.world).getTracker();

            if (entity instanceof EntityItem || entity instanceof EntityArrow || entity instanceof EntityExperienceOrb) {
                entitytracker.a(entity, (Packet) (new PacketPlayOutCollect(entity.getId(), this.getId(), i)));
            }
        }

    }

    public boolean hasLineOfSight(Entity entity) {
        return this.world.rayTrace(new Vec3D(this.locX, this.locY + (double) this.getHeadHeight(), this.locZ), new Vec3D(entity.locX, entity.locY + (double) entity.getHeadHeight(), entity.locZ), false, true, false) == null;
    }

    public Vec3D e(float f) {
        if (f == 1.0F) {
            return this.f(this.pitch, this.aP);
        } else {
            float f1 = this.lastPitch + (this.pitch - this.lastPitch) * f;
            float f2 = this.aQ + (this.aP - this.aQ) * f;

            return this.f(f1, f2);
        }
    }

    public boolean cC() {
        return !this.world.isClientSide;
    }

    public boolean isInteractable() {
        return !this.dead;
    }

    public boolean isCollidable() {
        return this.isAlive() && !this.m_();
    }

    protected void ax() {
        this.velocityChanged = this.random.nextDouble() >= this.getAttributeInstance(GenericAttributes.c).getValue();
    }

    public float getHeadRotation() {
        return this.aP;
    }

    public void setHeadRotation(float f) {
        this.aP = f;
    }

    public void h(float f) {
        this.aN = f;
    }

    public float getAbsorptionHearts() {
        return this.bE;
    }

    public void setAbsorptionHearts(float f) {
        if (f < 0.0F) {
            f = 0.0F;
        }

        this.bE = f;
    }

    public void enterCombat() {}

    public void exitCombat() {}

    protected void cE() {
        this.updateEffects = true;
    }

    public abstract EnumMainHand getMainHand();

    public boolean isHandRaised() {
        return (((Byte) this.datawatcher.get(EntityLiving.at)).byteValue() & 1) > 0;
    }

    public EnumHand cH() {
        return (((Byte) this.datawatcher.get(EntityLiving.at)).byteValue() & 2) > 0 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
    }

    protected void cI() {
        if (this.isHandRaised()) {
            ItemStack itemstack = this.b(this.cH());

            if (itemstack == this.activeItem) {
                if (this.cK() <= 25 && this.cK() % 4 == 0) {
                    this.b(this.activeItem, 5);
                }

                if (--this.bp == 0 && !this.world.isClientSide) {
                    this.v();
                }
            } else {
                this.cN();
            }
        }

    }

    public void c(EnumHand enumhand) {
        ItemStack itemstack = this.b(enumhand);

        if (!itemstack.isEmpty() && !this.isHandRaised()) {
            this.activeItem = itemstack;
            this.bp = itemstack.m();
            if (!this.world.isClientSide) {
                int i = 1;

                if (enumhand == EnumHand.OFF_HAND) {
                    i |= 2;
                }

                this.datawatcher.set(EntityLiving.at, Byte.valueOf((byte) i));
            }

        }
    }

    public void a(DataWatcherObject<?> datawatcherobject) {
        super.a(datawatcherobject);
        if (EntityLiving.at.equals(datawatcherobject) && this.world.isClientSide) {
            if (this.isHandRaised() && this.activeItem.isEmpty()) {
                this.activeItem = this.b(this.cH());
                if (!this.activeItem.isEmpty()) {
                    this.bp = this.activeItem.m();
                }
            } else if (!this.isHandRaised() && !this.activeItem.isEmpty()) {
                this.activeItem = ItemStack.a;
                this.bp = 0;
            }
        }

    }

    protected void b(ItemStack itemstack, int i) {
        if (!itemstack.isEmpty() && this.isHandRaised()) {
            if (itemstack.n() == EnumAnimation.DRINK) {
                this.a(SoundEffects.bT, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
            }

            if (itemstack.n() == EnumAnimation.EAT) {
                for (int j = 0; j < i; ++j) {
                    Vec3D vec3d = new Vec3D(((double) this.random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);

                    vec3d = vec3d.a(-this.pitch * 0.017453292F);
                    vec3d = vec3d.b(-this.yaw * 0.017453292F);
                    double d0 = (double) (-this.random.nextFloat()) * 0.6D - 0.3D;
                    Vec3D vec3d1 = new Vec3D(((double) this.random.nextFloat() - 0.5D) * 0.3D, d0, 0.6D);

                    vec3d1 = vec3d1.a(-this.pitch * 0.017453292F);
                    vec3d1 = vec3d1.b(-this.yaw * 0.017453292F);
                    vec3d1 = vec3d1.add(this.locX, this.locY + (double) this.getHeadHeight(), this.locZ);
                    if (itemstack.usesData()) {
                        this.world.addParticle(EnumParticle.ITEM_CRACK, vec3d1.x, vec3d1.y, vec3d1.z, vec3d.x, vec3d.y + 0.05D, vec3d.z, new int[] { Item.getId(itemstack.getItem()), itemstack.getData()});
                    } else {
                        this.world.addParticle(EnumParticle.ITEM_CRACK, vec3d1.x, vec3d1.y, vec3d1.z, vec3d.x, vec3d.y + 0.05D, vec3d.z, new int[] { Item.getId(itemstack.getItem())});
                    }
                }

                this.a(SoundEffects.bU, 0.5F + 0.5F * (float) this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            }

        }
    }

    protected void v() {
        if (!this.activeItem.isEmpty() && this.isHandRaised()) {
            this.b(this.activeItem, 16);
            this.a(this.cH(), this.activeItem.a(this.world, this));
            this.cN();
        }

    }

    public ItemStack cJ() {
        return this.activeItem;
    }

    public int cK() {
        return this.bp;
    }

    public int cL() {
        return this.isHandRaised() ? this.activeItem.m() - this.cK() : 0;
    }

    public void clearActiveItem() {
        if (!this.activeItem.isEmpty()) {
            this.activeItem.a(this.world, this, this.cK());
        }

        this.cN();
    }

    public void cN() {
        if (!this.world.isClientSide) {
            this.datawatcher.set(EntityLiving.at, Byte.valueOf((byte) 0));
        }

        this.activeItem = ItemStack.a;
        this.bp = 0;
    }

    public boolean isBlocking() {
        if (this.isHandRaised() && !this.activeItem.isEmpty()) {
            Item item = this.activeItem.getItem();

            return item.f(this.activeItem) != EnumAnimation.BLOCK ? false : item.e(this.activeItem) - this.bp >= 5;
        } else {
            return false;
        }
    }

    public boolean cP() {
        return this.getFlag(7);
    }

    public boolean j(double d0, double d1, double d2) {
        double d3 = this.locX;
        double d4 = this.locY;
        double d5 = this.locZ;

        this.locX = d0;
        this.locY = d1;
        this.locZ = d2;
        boolean flag = false;
        BlockPosition blockposition = new BlockPosition(this);
        World world = this.world;
        Random random = this.getRandom();
        boolean flag1;

        if (world.isLoaded(blockposition)) {
            flag1 = false;

            while (!flag1 && blockposition.getY() > 0) {
                BlockPosition blockposition1 = blockposition.down();
                IBlockData iblockdata = world.getType(blockposition1);

                if (iblockdata.getMaterial().isSolid()) {
                    flag1 = true;
                } else {
                    --this.locY;
                    blockposition = blockposition1;
                }
            }

            if (flag1) {
                this.enderTeleportTo(this.locX, this.locY, this.locZ);
                if (world.getCubes(this, this.getBoundingBox()).isEmpty() && !world.containsLiquid(this.getBoundingBox())) {
                    flag = true;
                }
            }
        }

        if (!flag) {
            this.enderTeleportTo(d3, d4, d5);
            return false;
        } else {
            flag1 = true;

            for (int i = 0; i < 128; ++i) {
                double d6 = (double) i / 127.0D;
                float f = (random.nextFloat() - 0.5F) * 0.2F;
                float f1 = (random.nextFloat() - 0.5F) * 0.2F;
                float f2 = (random.nextFloat() - 0.5F) * 0.2F;
                double d7 = d3 + (this.locX - d3) * d6 + (random.nextDouble() - 0.5D) * (double) this.width * 2.0D;
                double d8 = d4 + (this.locY - d4) * d6 + random.nextDouble() * (double) this.length;
                double d9 = d5 + (this.locZ - d5) * d6 + (random.nextDouble() - 0.5D) * (double) this.width * 2.0D;

                world.addParticle(EnumParticle.PORTAL, d7, d8, d9, (double) f, (double) f1, (double) f2, new int[0]);
            }

            if (this instanceof EntityCreature) {
                ((EntityCreature) this).getNavigation().p();
            }

            return true;
        }
    }

    public boolean cR() {
        return true;
    }

    public boolean cS() {
        return true;
    }
}
