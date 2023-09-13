package net.minecraft.server;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import org.apache.commons.lang3.tuple.Pair;

public abstract class EntityLiving extends Entity {

    private static final UUID b = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
    private static final AttributeModifier c = (new AttributeModifier(EntityLiving.b, "Sprinting speed boost", 0.30000001192092896D, AttributeModifier.Operation.MULTIPLY_TOTAL)).a(false);
    protected static final DataWatcherObject<Byte> ao = DataWatcher.a(EntityLiving.class, DataWatcherRegistry.a);
    public static final DataWatcherObject<Float> HEALTH = DataWatcher.a(EntityLiving.class, DataWatcherRegistry.c);
    private static final DataWatcherObject<Integer> e = DataWatcher.a(EntityLiving.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Boolean> f = DataWatcher.a(EntityLiving.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Integer> g = DataWatcher.a(EntityLiving.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Integer> bp = DataWatcher.a(EntityLiving.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Optional<BlockPosition>> bq = DataWatcher.a(EntityLiving.class, DataWatcherRegistry.m);
    protected static final EntitySize ap = EntitySize.c(0.2F, 0.2F);
    private AttributeMapBase attributeMap;
    public CombatTracker combatTracker = new CombatTracker(this);
    public final Map<MobEffectList, MobEffect> effects = Maps.newHashMap();
    private final NonNullList<ItemStack> bu;
    private final NonNullList<ItemStack> bv;
    public boolean aq;
    public EnumHand ar;
    public int as;
    public int at;
    public int au;
    public int hurtTicks;
    public int hurtDuration;
    public float ax;
    public int deathTicks;
    public float az;
    public float aA;
    protected int aB;
    public float aC;
    public float aD;
    public float aE;
    public int maxNoDamageTicks;
    public final float aG;
    public final float aH;
    public float aI;
    public float aJ;
    public float aK;
    public float aL;
    public float aM;
    public EntityHuman killer;
    protected int lastDamageByPlayerTime;
    protected boolean killed;
    protected int ticksFarFromPlayer;
    protected float aR;
    protected float aS;
    protected float aT;
    protected float aU;
    protected float aV;
    protected int aW;
    public float lastDamage;
    protected boolean jumping;
    public float aZ;
    public float ba;
    public float bb;
    protected int bc;
    protected double bd;
    protected double be;
    protected double bf;
    protected double bg;
    protected double bh;
    protected double bi;
    protected int bj;
    public boolean updateEffects;
    @Nullable
    public EntityLiving lastDamager;
    public int hurtTimestamp;
    private EntityLiving bz;
    private int bA;
    private float bB;
    private int jumpTicks;
    private float bD;
    protected ItemStack activeItem;
    protected int bl;
    protected int bm;
    private BlockPosition bE;
    private DamageSource bF;
    private long bG;
    protected int bn;
    private float bH;
    private float bI;
    protected BehaviorController<?> bo;

    protected EntityLiving(EntityTypes<? extends EntityLiving> entitytypes, World world) {
        super(entitytypes, world);
        this.bu = NonNullList.a(2, ItemStack.a);
        this.bv = NonNullList.a(4, ItemStack.a);
        this.maxNoDamageTicks = 20;
        this.aM = 0.02F;
        this.updateEffects = true;
        this.activeItem = ItemStack.a;
        this.initAttributes();
        this.setHealth(this.getMaxHealth());
        this.i = true;
        this.aH = (float) ((Math.random() + 1.0D) * 0.009999999776482582D);
        this.Z();
        this.aG = (float) Math.random() * 12398.0F;
        this.yaw = (float) (Math.random() * 6.2831854820251465D);
        this.aK = this.yaw;
        this.H = 0.6F;
        this.bo = this.a(new Dynamic(DynamicOpsNBT.a, new NBTTagCompound()));
    }

    public BehaviorController<?> getBehaviorController() {
        return this.bo;
    }

    protected BehaviorController<?> a(Dynamic<?> dynamic) {
        return new BehaviorController<>(ImmutableList.of(), ImmutableList.of(), dynamic);
    }

    @Override
    public void killEntity() {
        this.damageEntity(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
    }

    public boolean a(EntityTypes<?> entitytypes) {
        return true;
    }

    @Override
    protected void initDatawatcher() {
        this.datawatcher.register(EntityLiving.ao, (byte) 0);
        this.datawatcher.register(EntityLiving.e, 0);
        this.datawatcher.register(EntityLiving.f, false);
        this.datawatcher.register(EntityLiving.g, 0);
        this.datawatcher.register(EntityLiving.bp, 0);
        this.datawatcher.register(EntityLiving.HEALTH, 1.0F);
        this.datawatcher.register(EntityLiving.bq, Optional.empty());
    }

    protected void initAttributes() {
        this.getAttributeMap().b(GenericAttributes.MAX_HEALTH);
        this.getAttributeMap().b(GenericAttributes.KNOCKBACK_RESISTANCE);
        this.getAttributeMap().b(GenericAttributes.MOVEMENT_SPEED);
        this.getAttributeMap().b(GenericAttributes.ARMOR);
        this.getAttributeMap().b(GenericAttributes.ARMOR_TOUGHNESS);
    }

    @Override
    protected void a(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {
        if (!this.isInWater()) {
            this.aC();
        }

        if (!this.world.isClientSide && this.fallDistance > 3.0F && flag) {
            float f = (float) MathHelper.f(this.fallDistance - 3.0F);

            if (!iblockdata.isAir()) {
                double d1 = Math.min((double) (0.2F + f / 15.0F), 2.5D);
                int i = (int) (150.0D * d1);

                ((WorldServer) this.world).a(new ParticleParamBlock(Particles.BLOCK, iblockdata), this.locX(), this.locY(), this.locZ(), i, 0.0D, 0.0D, 0.0D, 0.15000000596046448D);
            }
        }

        super.a(d0, flag, iblockdata, blockposition);
    }

    public boolean cB() {
        return this.getMonsterType() == EnumMonsterType.UNDEAD;
    }

    @Override
    public void entityBaseTick() {
        this.az = this.aA;
        if (this.justCreated) {
            this.getBedPosition().ifPresent(this::a);
        }

        super.entityBaseTick();
        this.world.getMethodProfiler().enter("livingEntityBaseTick");
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
            if (this.a(TagsFluid.WATER) && this.world.getType(new BlockPosition(this.locX(), this.getHeadY(), this.locZ())).getBlock() != Blocks.BUBBLE_COLUMN) {
                if (!this.cB() && !MobEffectUtil.c(this) && !flag1) {
                    this.setAirTicks(this.l(this.getAirTicks()));
                    if (this.getAirTicks() == -20) {
                        this.setAirTicks(0);
                        Vec3D vec3d = this.getMot();

                        for (int i = 0; i < 8; ++i) {
                            float f = this.random.nextFloat() - this.random.nextFloat();
                            float f1 = this.random.nextFloat() - this.random.nextFloat();
                            float f2 = this.random.nextFloat() - this.random.nextFloat();

                            this.world.addParticle(Particles.BUBBLE, this.locX() + (double) f, this.locY() + (double) f1, this.locZ() + (double) f2, vec3d.x, vec3d.y, vec3d.z);
                        }

                        this.damageEntity(DamageSource.DROWN, 2.0F);
                    }
                }

                if (!this.world.isClientSide && this.isPassenger() && this.getVehicle() != null && !this.getVehicle().bi()) {
                    this.stopRiding();
                }
            } else if (this.getAirTicks() < this.bw()) {
                this.setAirTicks(this.m(this.getAirTicks()));
            }

            if (!this.world.isClientSide) {
                BlockPosition blockposition = new BlockPosition(this);

                if (!Objects.equal(this.bE, blockposition)) {
                    this.bE = blockposition;
                    this.b(blockposition);
                }
            }
        }

        if (this.isAlive() && this.ay()) {
            this.extinguish();
        }

        if (this.hurtTicks > 0) {
            --this.hurtTicks;
        }

        if (this.noDamageTicks > 0 && !(this instanceof EntityPlayer)) {
            --this.noDamageTicks;
        }

        if (this.getHealth() <= 0.0F) {
            this.cD();
        }

        if (this.lastDamageByPlayerTime > 0) {
            --this.lastDamageByPlayerTime;
        } else {
            this.killer = null;
        }

        if (this.bz != null && !this.bz.isAlive()) {
            this.bz = null;
        }

        if (this.lastDamager != null) {
            if (!this.lastDamager.isAlive()) {
                this.setLastDamager((EntityLiving) null);
            } else if (this.ticksLived - this.hurtTimestamp > 100) {
                this.setLastDamager((EntityLiving) null);
            }
        }

        this.tickPotionEffects();
        this.aU = this.aT;
        this.aJ = this.aI;
        this.aL = this.aK;
        this.lastYaw = this.yaw;
        this.lastPitch = this.pitch;
        this.world.getMethodProfiler().exit();
    }

    protected void b(BlockPosition blockposition) {
        int i = EnchantmentManager.a(Enchantments.FROST_WALKER, this);

        if (i > 0) {
            EnchantmentFrostWalker.a(this, this.world, blockposition, i);
        }

    }

    public boolean isBaby() {
        return false;
    }

    public float cC() {
        return this.isBaby() ? 0.5F : 1.0F;
    }

    @Override
    public boolean bi() {
        return false;
    }

    protected void cD() {
        ++this.deathTicks;
        if (this.deathTicks == 20) {
            this.die();

            for (int i = 0; i < 20; ++i) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;

                this.world.addParticle(Particles.POOF, this.d(1.0D), this.cv(), this.g(1.0D), d0, d1, d2);
            }
        }

    }

    protected boolean isDropExperience() {
        return !this.isBaby();
    }

    protected int l(int i) {
        int j = EnchantmentManager.getOxygenEnchantmentLevel(this);

        return j > 0 && this.random.nextInt(j + 1) > 0 ? i : i - 1;
    }

    protected int m(int i) {
        return Math.min(i + 4, this.bw());
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

    public int cI() {
        return this.hurtTimestamp;
    }

    public void setLastDamager(@Nullable EntityLiving entityliving) {
        this.lastDamager = entityliving;
        this.hurtTimestamp = this.ticksLived;
    }

    @Nullable
    public EntityLiving cJ() {
        return this.bz;
    }

    public int cK() {
        return this.bA;
    }

    public void z(Entity entity) {
        if (entity instanceof EntityLiving) {
            this.bz = (EntityLiving) entity;
        } else {
            this.bz = null;
        }

        this.bA = this.ticksLived;
    }

    public int cL() {
        return this.ticksFarFromPlayer;
    }

    public void n(int i) {
        this.ticksFarFromPlayer = i;
    }

    protected void b(ItemStack itemstack) {
        if (!itemstack.isEmpty()) {
            SoundEffect soundeffect = SoundEffects.ITEM_ARMOR_EQUIP_GENERIC;
            Item item = itemstack.getItem();

            if (item instanceof ItemArmor) {
                soundeffect = ((ItemArmor) item).Q_().b();
            } else if (item == Items.ELYTRA) {
                soundeffect = SoundEffects.ITEM_ARMOR_EQUIP_ELYTRA;
            }

            this.a(soundeffect, 1.0F, 1.0F);
        }
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setFloat("Health", this.getHealth());
        nbttagcompound.setShort("HurtTime", (short) this.hurtTicks);
        nbttagcompound.setInt("HurtByTimestamp", this.hurtTimestamp);
        nbttagcompound.setShort("DeathTime", (short) this.deathTicks);
        nbttagcompound.setFloat("AbsorptionAmount", this.getAbsorptionHearts());
        nbttagcompound.set("Attributes", GenericAttributes.a(this.getAttributeMap()));
        if (!this.effects.isEmpty()) {
            NBTTagList nbttaglist = new NBTTagList();
            Iterator iterator = this.effects.values().iterator();

            while (iterator.hasNext()) {
                MobEffect mobeffect = (MobEffect) iterator.next();

                nbttaglist.add(mobeffect.a(new NBTTagCompound()));
            }

            nbttagcompound.set("ActiveEffects", nbttaglist);
        }

        nbttagcompound.setBoolean("FallFlying", this.isGliding());
        this.getBedPosition().ifPresent((blockposition) -> {
            nbttagcompound.setInt("SleepingX", blockposition.getX());
            nbttagcompound.setInt("SleepingY", blockposition.getY());
            nbttagcompound.setInt("SleepingZ", blockposition.getZ());
        });
        nbttagcompound.set("Brain", (NBTBase) this.bo.a((DynamicOps) DynamicOpsNBT.a));
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        this.setAbsorptionHearts(nbttagcompound.getFloat("AbsorptionAmount"));
        if (nbttagcompound.hasKeyOfType("Attributes", 9) && this.world != null && !this.world.isClientSide) {
            GenericAttributes.a(this.getAttributeMap(), nbttagcompound.getList("Attributes", 10));
        }

        if (nbttagcompound.hasKeyOfType("ActiveEffects", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList("ActiveEffects", 10);

            for (int i = 0; i < nbttaglist.size(); ++i) {
                NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(i);
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
            ScoreboardTeam scoreboardteam = this.world.getScoreboard().getTeam(s);
            boolean flag = scoreboardteam != null && this.world.getScoreboard().addPlayerToTeam(this.getUniqueIDString(), scoreboardteam);

            if (!flag) {
                EntityLiving.LOGGER.warn("Unable to add mob to team \"{}\" (that team probably doesn't exist)", s);
            }
        }

        if (nbttagcompound.getBoolean("FallFlying")) {
            this.setFlag(7, true);
        }

        if (nbttagcompound.hasKeyOfType("SleepingX", 99) && nbttagcompound.hasKeyOfType("SleepingY", 99) && nbttagcompound.hasKeyOfType("SleepingZ", 99)) {
            BlockPosition blockposition = new BlockPosition(nbttagcompound.getInt("SleepingX"), nbttagcompound.getInt("SleepingY"), nbttagcompound.getInt("SleepingZ"));

            this.d(blockposition);
            this.datawatcher.set(EntityLiving.POSE, EntityPose.SLEEPING);
            if (!this.justCreated) {
                this.a(blockposition);
            }
        }

        if (nbttagcompound.hasKeyOfType("Brain", 10)) {
            this.bo = this.a(new Dynamic(DynamicOpsNBT.a, nbttagcompound.get("Brain")));
        }

    }

    protected void tickPotionEffects() {
        Iterator iterator = this.effects.keySet().iterator();

        try {
            while (iterator.hasNext()) {
                MobEffectList mobeffectlist = (MobEffectList) iterator.next();
                MobEffect mobeffect = (MobEffect) this.effects.get(mobeffectlist);

                if (!mobeffect.tick(this, () -> {
                    this.a(mobeffect, true);
                })) {
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
                this.C();
            }

            this.updateEffects = false;
        }

        int i = (Integer) this.datawatcher.get(EntityLiving.e);
        boolean flag = (Boolean) this.datawatcher.get(EntityLiving.f);

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

                this.world.addParticle(flag ? Particles.AMBIENT_ENTITY_EFFECT : Particles.ENTITY_EFFECT, this.d(0.5D), this.cv(), this.g(0.5D), d0, d1, d2);
            }
        }

    }

    protected void C() {
        if (this.effects.isEmpty()) {
            this.cN();
            this.setInvisible(false);
        } else {
            Collection<MobEffect> collection = this.effects.values();

            this.datawatcher.set(EntityLiving.f, c(collection));
            this.datawatcher.set(EntityLiving.e, PotionUtil.a(collection));
            this.setInvisible(this.hasEffect(MobEffects.INVISIBILITY));
        }

    }

    public double A(@Nullable Entity entity) {
        double d0 = 1.0D;

        if (this.bm()) {
            d0 *= 0.8D;
        }

        if (this.isInvisible()) {
            float f = this.dl();

            if (f < 0.1F) {
                f = 0.1F;
            }

            d0 *= 0.7D * (double) f;
        }

        if (entity != null) {
            ItemStack itemstack = this.getEquipment(EnumItemSlot.HEAD);
            Item item = itemstack.getItem();
            EntityTypes<?> entitytypes = entity.getEntityType();

            if (entitytypes == EntityTypes.SKELETON && item == Items.SKELETON_SKULL || entitytypes == EntityTypes.ZOMBIE && item == Items.ZOMBIE_HEAD || entitytypes == EntityTypes.CREEPER && item == Items.CREEPER_HEAD) {
                d0 *= 0.5D;
            }
        }

        return d0;
    }

    public boolean c(EntityLiving entityliving) {
        return true;
    }

    public boolean a(EntityLiving entityliving, PathfinderTargetCondition pathfindertargetcondition) {
        return pathfindertargetcondition.a(this, entityliving);
    }

    public static boolean c(Collection<MobEffect> collection) {
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

    protected void cN() {
        this.datawatcher.set(EntityLiving.f, false);
        this.datawatcher.set(EntityLiving.e, 0);
    }

    public boolean removeAllEffects() {
        if (this.world.isClientSide) {
            return false;
        } else {
            Iterator<MobEffect> iterator = this.effects.values().iterator();

            boolean flag;

            for (flag = false; iterator.hasNext(); flag = true) {
                this.b((MobEffect) iterator.next());
                iterator.remove();
            }

            return flag;
        }
    }

    public Collection<MobEffect> getEffects() {
        return this.effects.values();
    }

    public Map<MobEffectList, MobEffect> cQ() {
        return this.effects;
    }

    public boolean hasEffect(MobEffectList mobeffectlist) {
        return this.effects.containsKey(mobeffectlist);
    }

    @Nullable
    public MobEffect getEffect(MobEffectList mobeffectlist) {
        return (MobEffect) this.effects.get(mobeffectlist);
    }

    public boolean addEffect(MobEffect mobeffect) {
        if (!this.d(mobeffect)) {
            return false;
        } else {
            MobEffect mobeffect1 = (MobEffect) this.effects.get(mobeffect.getMobEffect());

            if (mobeffect1 == null) {
                this.effects.put(mobeffect.getMobEffect(), mobeffect);
                this.a(mobeffect);
                return true;
            } else if (mobeffect1.b(mobeffect)) {
                this.a(mobeffect1, true);
                return true;
            } else {
                return false;
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

    public boolean cR() {
        return this.getMonsterType() == EnumMonsterType.UNDEAD;
    }

    @Nullable
    public MobEffect c(@Nullable MobEffectList mobeffectlist) {
        return (MobEffect) this.effects.remove(mobeffectlist);
    }

    public boolean removeEffect(MobEffectList mobeffectlist) {
        MobEffect mobeffect = this.c(mobeffectlist);

        if (mobeffect != null) {
            this.b(mobeffect);
            return true;
        } else {
            return false;
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

    public float getHealth() {
        return (Float) this.datawatcher.get(EntityLiving.HEALTH);
    }

    public void setHealth(float f) {
        this.datawatcher.set(EntityLiving.HEALTH, MathHelper.a(f, 0.0F, this.getMaxHealth()));
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else if (this.world.isClientSide) {
            return false;
        } else if (this.getHealth() <= 0.0F) {
            return false;
        } else if (damagesource.isFire() && this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
            return false;
        } else {
            if (this.isSleeping() && !this.world.isClientSide) {
                this.entityWakeup();
            }

            this.ticksFarFromPlayer = 0;
            float f1 = f;

            if ((damagesource == DamageSource.ANVIL || damagesource == DamageSource.FALLING_BLOCK) && !this.getEquipment(EnumItemSlot.HEAD).isEmpty()) {
                this.getEquipment(EnumItemSlot.HEAD).damage((int) (f * 4.0F + this.random.nextFloat() * f * 2.0F), this, (entityliving) -> {
                    entityliving.broadcastItemBreak(EnumItemSlot.HEAD);
                });
                f *= 0.75F;
            }

            boolean flag = false;
            float f2 = 0.0F;

            if (f > 0.0F && this.applyBlockingModifier(damagesource)) {
                this.damageShield(f);
                f2 = f;
                f = 0.0F;
                if (!damagesource.b()) {
                    Entity entity = damagesource.j();

                    if (entity instanceof EntityLiving) {
                        this.shieldBlock((EntityLiving) entity);
                    }
                }

                flag = true;
            }

            this.aD = 1.5F;
            boolean flag1 = true;

            if ((float) this.noDamageTicks > 10.0F) {
                if (f <= this.lastDamage) {
                    return false;
                }

                this.damageEntity0(damagesource, f - this.lastDamage);
                this.lastDamage = f;
                flag1 = false;
            } else {
                this.lastDamage = f;
                this.noDamageTicks = 20;
                this.damageEntity0(damagesource, f);
                this.hurtDuration = 10;
                this.hurtTicks = this.hurtDuration;
            }

            this.ax = 0.0F;
            Entity entity1 = damagesource.getEntity();

            if (entity1 != null) {
                if (entity1 instanceof EntityLiving) {
                    this.setLastDamager((EntityLiving) entity1);
                }

                if (entity1 instanceof EntityHuman) {
                    this.lastDamageByPlayerTime = 100;
                    this.killer = (EntityHuman) entity1;
                } else if (entity1 instanceof EntityWolf) {
                    EntityWolf entitywolf = (EntityWolf) entity1;

                    if (entitywolf.isTamed()) {
                        this.lastDamageByPlayerTime = 100;
                        EntityLiving entityliving = entitywolf.getOwner();

                        if (entityliving != null && entityliving.getEntityType() == EntityTypes.PLAYER) {
                            this.killer = (EntityHuman) entityliving;
                        } else {
                            this.killer = null;
                        }
                    }
                }
            }

            if (flag1) {
                if (flag) {
                    this.world.broadcastEntityEffect(this, (byte) 29);
                } else if (damagesource instanceof EntityDamageSource && ((EntityDamageSource) damagesource).y()) {
                    this.world.broadcastEntityEffect(this, (byte) 33);
                } else {
                    byte b0;

                    if (damagesource == DamageSource.DROWN) {
                        b0 = 36;
                    } else if (damagesource.isFire()) {
                        b0 = 37;
                    } else if (damagesource == DamageSource.SWEET_BERRY_BUSH) {
                        b0 = 44;
                    } else {
                        b0 = 2;
                    }

                    this.world.broadcastEntityEffect(this, b0);
                }

                if (damagesource != DamageSource.DROWN && (!flag || f > 0.0F)) {
                    this.velocityChanged();
                }

                if (entity1 != null) {
                    double d0 = entity1.locX() - this.locX();

                    double d1;

                    for (d1 = entity1.locZ() - this.locZ(); d0 * d0 + d1 * d1 < 1.0E-4D; d1 = (Math.random() - Math.random()) * 0.01D) {
                        d0 = (Math.random() - Math.random()) * 0.01D;
                    }

                    this.ax = (float) (MathHelper.d(d1, d0) * 57.2957763671875D - (double) this.yaw);
                    this.a(entity1, 0.4F, d0, d1);
                } else {
                    this.ax = (float) ((int) (Math.random() * 2.0D) * 180);
                }
            }

            if (this.getHealth() <= 0.0F) {
                if (!this.f(damagesource)) {
                    SoundEffect soundeffect = this.getSoundDeath();

                    if (flag1 && soundeffect != null) {
                        this.a(soundeffect, this.getSoundVolume(), this.dn());
                    }

                    this.die(damagesource);
                }
            } else if (flag1) {
                this.c(damagesource);
            }

            boolean flag2 = !flag || f > 0.0F;

            if (flag2) {
                this.bF = damagesource;
                this.bG = this.world.getTime();
            }

            if (this instanceof EntityPlayer) {
                CriterionTriggers.h.a((EntityPlayer) this, damagesource, f1, f, flag);
                if (f2 > 0.0F && f2 < 3.4028235E37F) {
                    ((EntityPlayer) this).a(StatisticList.DAMAGE_BLOCKED_BY_SHIELD, Math.round(f2 * 10.0F));
                }
            }

            if (entity1 instanceof EntityPlayer) {
                CriterionTriggers.g.a((EntityPlayer) entity1, this, damagesource, f1, f, flag);
            }

            return flag2;
        }
    }

    protected void shieldBlock(EntityLiving entityliving) {
        entityliving.e(this);
    }

    protected void e(EntityLiving entityliving) {
        entityliving.a(this, 0.5F, entityliving.locX() - this.locX(), entityliving.locZ() - this.locZ());
    }

    private boolean f(DamageSource damagesource) {
        if (damagesource.ignoresInvulnerability()) {
            return false;
        } else {
            ItemStack itemstack = null;
            EnumHand[] aenumhand = EnumHand.values();
            int i = aenumhand.length;

            for (int j = 0; j < i; ++j) {
                EnumHand enumhand = aenumhand[j];
                ItemStack itemstack1 = this.b(enumhand);

                if (itemstack1.getItem() == Items.TOTEM_OF_UNDYING) {
                    itemstack = itemstack1.cloneItemStack();
                    itemstack1.subtract(1);
                    break;
                }
            }

            if (itemstack != null) {
                if (this instanceof EntityPlayer) {
                    EntityPlayer entityplayer = (EntityPlayer) this;

                    entityplayer.b(StatisticList.ITEM_USED.b(Items.TOTEM_OF_UNDYING));
                    CriterionTriggers.B.a(entityplayer, itemstack);
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
    public DamageSource cT() {
        if (this.world.getTime() - this.bG > 40L) {
            this.bF = null;
        }

        return this.bF;
    }

    protected void c(DamageSource damagesource) {
        SoundEffect soundeffect = this.getSoundHurt(damagesource);

        if (soundeffect != null) {
            this.a(soundeffect, this.getSoundVolume(), this.dn());
        }

    }

    private boolean applyBlockingModifier(DamageSource damagesource) {
        Entity entity = damagesource.j();
        boolean flag = false;

        if (entity instanceof EntityArrow) {
            EntityArrow entityarrow = (EntityArrow) entity;

            if (entityarrow.getPierceLevel() > 0) {
                flag = true;
            }
        }

        if (!damagesource.ignoresArmor() && this.isBlocking() && !flag) {
            Vec3D vec3d = damagesource.w();

            if (vec3d != null) {
                Vec3D vec3d1 = this.f(1.0F);
                Vec3D vec3d2 = vec3d.a(this.getPositionVector()).d();

                vec3d2 = new Vec3D(vec3d2.x, 0.0D, vec3d2.z);
                if (vec3d2.b(vec3d1) < 0.0D) {
                    return true;
                }
            }
        }

        return false;
    }

    public void die(DamageSource damagesource) {
        if (!this.dead && !this.killed) {
            Entity entity = damagesource.getEntity();
            EntityLiving entityliving = this.getKillingEntity();

            if (this.aW >= 0 && entityliving != null) {
                entityliving.a(this, this.aW, damagesource);
            }

            if (entity != null) {
                entity.b(this);
            }

            if (this.isSleeping()) {
                this.entityWakeup();
            }

            this.killed = true;
            this.getCombatTracker().g();
            if (!this.world.isClientSide) {
                this.d(damagesource);
                this.f(entityliving);
            }

            this.world.broadcastEntityEffect(this, (byte) 3);
            this.setPose(EntityPose.DYING);
        }
    }

    protected void f(@Nullable EntityLiving entityliving) {
        if (!this.world.isClientSide) {
            boolean flag = false;

            if (entityliving instanceof EntityWither) {
                if (this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
                    BlockPosition blockposition = new BlockPosition(this);
                    IBlockData iblockdata = Blocks.WITHER_ROSE.getBlockData();

                    if (this.world.getType(blockposition).isAir() && iblockdata.canPlace(this.world, blockposition)) {
                        this.world.setTypeAndData(blockposition, iblockdata, 3);
                        flag = true;
                    }
                }

                if (!flag) {
                    EntityItem entityitem = new EntityItem(this.world, this.locX(), this.locY(), this.locZ(), new ItemStack(Items.bg));

                    this.world.addEntity(entityitem);
                }
            }

        }
    }

    protected void d(DamageSource damagesource) {
        Entity entity = damagesource.getEntity();
        int i;

        if (entity instanceof EntityHuman) {
            i = EnchantmentManager.g((EntityLiving) entity);
        } else {
            i = 0;
        }

        boolean flag = this.lastDamageByPlayerTime > 0;

        if (this.isDropExperience() && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            this.a(damagesource, flag);
            this.dropDeathLoot(damagesource, i, flag);
        }

        this.dropInventory();
        this.dropExperience();
    }

    protected void dropInventory() {}

    protected void dropExperience() {
        if (!this.world.isClientSide && (this.alwaysGivesExp() || this.lastDamageByPlayerTime > 0 && this.isDropExperience() && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT))) {
            int i = this.getExpValue(this.killer);

            while (i > 0) {
                int j = EntityExperienceOrb.getOrbValue(i);

                i -= j;
                this.world.addEntity(new EntityExperienceOrb(this.world, this.locX(), this.locY(), this.locZ(), j));
            }
        }

    }

    protected void dropDeathLoot(DamageSource damagesource, int i, boolean flag) {}

    public MinecraftKey cW() {
        return this.getEntityType().h();
    }

    protected void a(DamageSource damagesource, boolean flag) {
        MinecraftKey minecraftkey = this.cW();
        LootTable loottable = this.world.getMinecraftServer().getLootTableRegistry().getLootTable(minecraftkey);
        LootTableInfo.Builder loottableinfo_builder = this.a(flag, damagesource);

        loottable.populateLoot(loottableinfo_builder.build(LootContextParameterSets.ENTITY), this::a);
    }

    protected LootTableInfo.Builder a(boolean flag, DamageSource damagesource) {
        LootTableInfo.Builder loottableinfo_builder = (new LootTableInfo.Builder((WorldServer) this.world)).a(this.random).set(LootContextParameters.THIS_ENTITY, this).set(LootContextParameters.POSITION, new BlockPosition(this)).set(LootContextParameters.DAMAGE_SOURCE, damagesource).setOptional(LootContextParameters.KILLER_ENTITY, damagesource.getEntity()).setOptional(LootContextParameters.DIRECT_KILLER_ENTITY, damagesource.j());

        if (flag && this.killer != null) {
            loottableinfo_builder = loottableinfo_builder.set(LootContextParameters.LAST_DAMAGE_PLAYER, this.killer).a(this.killer.eA());
        }

        return loottableinfo_builder;
    }

    public void a(Entity entity, float f, double d0, double d1) {
        if (this.random.nextDouble() >= this.getAttributeInstance(GenericAttributes.KNOCKBACK_RESISTANCE).getValue()) {
            this.impulse = true;
            Vec3D vec3d = this.getMot();
            Vec3D vec3d1 = (new Vec3D(d0, 0.0D, d1)).d().a((double) f);

            this.setMot(vec3d.x / 2.0D - vec3d1.x, this.onGround ? Math.min(0.4D, vec3d.y / 2.0D + (double) f) : vec3d.y, vec3d.z / 2.0D - vec3d1.z);
        }
    }

    @Nullable
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_GENERIC_HURT;
    }

    @Nullable
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_GENERIC_DEATH;
    }

    protected SoundEffect getSoundFall(int i) {
        return i > 4 ? SoundEffects.ENTITY_GENERIC_BIG_FALL : SoundEffects.ENTITY_GENERIC_SMALL_FALL;
    }

    protected SoundEffect c(ItemStack itemstack) {
        return itemstack.F();
    }

    public SoundEffect d(ItemStack itemstack) {
        return itemstack.G();
    }

    public boolean isClimbing() {
        if (this.isSpectator()) {
            return false;
        } else {
            IBlockData iblockdata = this.cY();
            Block block = iblockdata.getBlock();

            return block != Blocks.LADDER && block != Blocks.VINE && block != Blocks.SCAFFOLDING ? block instanceof BlockTrapdoor && this.b(new BlockPosition(this), iblockdata) : true;
        }
    }

    public IBlockData cY() {
        return this.world.getType(new BlockPosition(this));
    }

    private boolean b(BlockPosition blockposition, IBlockData iblockdata) {
        if ((Boolean) iblockdata.get(BlockTrapdoor.OPEN)) {
            IBlockData iblockdata1 = this.world.getType(blockposition.down());

            if (iblockdata1.getBlock() == Blocks.LADDER && iblockdata1.get(BlockLadder.FACING) == iblockdata.get(BlockTrapdoor.FACING)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isAlive() {
        return !this.dead && this.getHealth() > 0.0F;
    }

    @Override
    public boolean b(float f, float f1) {
        boolean flag = super.b(f, f1);
        int i = this.e(f, f1);

        if (i > 0) {
            this.a(this.getSoundFall(i), 1.0F, 1.0F);
            this.cZ();
            this.damageEntity(DamageSource.FALL, (float) i);
            return true;
        } else {
            return flag;
        }
    }

    protected int e(float f, float f1) {
        MobEffect mobeffect = this.getEffect(MobEffects.JUMP);
        float f2 = mobeffect == null ? 0.0F : (float) (mobeffect.getAmplifier() + 1);

        return MathHelper.f((f - 3.0F - f2) * f1);
    }

    protected void cZ() {
        if (!this.isSilent()) {
            int i = MathHelper.floor(this.locX());
            int j = MathHelper.floor(this.locY() - 0.20000000298023224D);
            int k = MathHelper.floor(this.locZ());
            IBlockData iblockdata = this.world.getType(new BlockPosition(i, j, k));

            if (!iblockdata.isAir()) {
                SoundEffectType soundeffecttype = iblockdata.r();

                this.a(soundeffecttype.g(), soundeffecttype.a() * 0.5F, soundeffecttype.b() * 0.75F);
            }

        }
    }

    public int getArmorStrength() {
        AttributeInstance attributeinstance = this.getAttributeInstance(GenericAttributes.ARMOR);

        return MathHelper.floor(attributeinstance.getValue());
    }

    protected void damageArmor(float f) {}

    protected void damageShield(float f) {}

    protected float applyArmorModifier(DamageSource damagesource, float f) {
        if (!damagesource.ignoresArmor()) {
            this.damageArmor(f);
            f = CombatMath.a(f, (float) this.getArmorStrength(), (float) this.getAttributeInstance(GenericAttributes.ARMOR_TOUGHNESS).getValue());
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
                float f2 = f;

                f = Math.max(f1 / 25.0F, 0.0F);
                float f3 = f2 - f;

                if (f3 > 0.0F && f3 < 3.4028235E37F) {
                    if (this instanceof EntityPlayer) {
                        ((EntityPlayer) this).a(StatisticList.DAMAGE_RESISTED, Math.round(f3 * 10.0F));
                    } else if (damagesource.getEntity() instanceof EntityPlayer) {
                        ((EntityPlayer) damagesource.getEntity()).a(StatisticList.DAMAGE_DEALT_RESISTED, Math.round(f3 * 10.0F));
                    }
                }
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
            float f2 = f1 - f;

            if (f2 > 0.0F && f2 < 3.4028235E37F && damagesource.getEntity() instanceof EntityPlayer) {
                ((EntityPlayer) damagesource.getEntity()).a(StatisticList.DAMAGE_DEALT_ABSORBED, Math.round(f2 * 10.0F));
            }

            if (f != 0.0F) {
                float f3 = this.getHealth();

                this.setHealth(f3 - f);
                this.getCombatTracker().trackDamage(damagesource, f3, f);
                this.setAbsorptionHearts(this.getAbsorptionHearts() - f);
            }
        }
    }

    public CombatTracker getCombatTracker() {
        return this.combatTracker;
    }

    @Nullable
    public EntityLiving getKillingEntity() {
        return (EntityLiving) (this.combatTracker.c() != null ? this.combatTracker.c() : (this.killer != null ? this.killer : (this.lastDamager != null ? this.lastDamager : null)));
    }

    public final float getMaxHealth() {
        return (float) this.getAttributeInstance(GenericAttributes.MAX_HEALTH).getValue();
    }

    public final int getArrowCount() {
        return (Integer) this.datawatcher.get(EntityLiving.g);
    }

    public final void setArrowCount(int i) {
        this.datawatcher.set(EntityLiving.g, i);
    }

    public final int df() {
        return (Integer) this.datawatcher.get(EntityLiving.bp);
    }

    public final void q(int i) {
        this.datawatcher.set(EntityLiving.bp, i);
    }

    private int l() {
        return MobEffectUtil.a(this) ? 6 - (1 + MobEffectUtil.b(this)) : (this.hasEffect(MobEffects.SLOWER_DIG) ? 6 + (1 + this.getEffect(MobEffects.SLOWER_DIG).getAmplifier()) * 2 : 6);
    }

    public void a(EnumHand enumhand) {
        this.a(enumhand, false);
    }

    public void a(EnumHand enumhand, boolean flag) {
        if (!this.aq || this.as >= this.l() / 2 || this.as < 0) {
            this.as = -1;
            this.aq = true;
            this.ar = enumhand;
            if (this.world instanceof WorldServer) {
                PacketPlayOutAnimation packetplayoutanimation = new PacketPlayOutAnimation(this, enumhand == EnumHand.MAIN_HAND ? 0 : 3);
                ChunkProviderServer chunkproviderserver = ((WorldServer) this.world).getChunkProvider();

                if (flag) {
                    chunkproviderserver.broadcastIncludingSelf(this, packetplayoutanimation);
                } else {
                    chunkproviderserver.broadcast(this, packetplayoutanimation);
                }
            }
        }

    }

    @Override
    protected void af() {
        this.damageEntity(DamageSource.OUT_OF_WORLD, 4.0F);
    }

    protected void dg() {
        int i = this.l();

        if (this.aq) {
            ++this.as;
            if (this.as >= i) {
                this.as = 0;
                this.aq = false;
            }
        } else {
            this.as = 0;
        }

        this.aA = (float) this.as / (float) i;
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

    @Override
    public abstract Iterable<ItemStack> getArmorItems();

    public abstract ItemStack getEquipment(EnumItemSlot enumitemslot);

    public abstract void setSlot(EnumItemSlot enumitemslot, ItemStack itemstack);

    public float dl() {
        Iterable<ItemStack> iterable = this.getArmorItems();
        int i = 0;
        int j = 0;

        for (Iterator iterator = iterable.iterator(); iterator.hasNext(); ++i) {
            ItemStack itemstack = (ItemStack) iterator.next();

            if (!itemstack.isEmpty()) {
                ++j;
            }
        }

        return i > 0 ? (float) j / (float) i : 0.0F;
    }

    @Override
    public void setSprinting(boolean flag) {
        super.setSprinting(flag);
        AttributeInstance attributeinstance = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);

        if (attributeinstance.a(EntityLiving.b) != null) {
            attributeinstance.removeModifier(EntityLiving.c);
        }

        if (flag) {
            attributeinstance.addModifier(EntityLiving.c);
        }

    }

    protected float getSoundVolume() {
        return 1.0F;
    }

    protected float dn() {
        return this.isBaby() ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.5F : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
    }

    protected boolean isFrozen() {
        return this.getHealth() <= 0.0F;
    }

    @Override
    public void collide(Entity entity) {
        if (!this.isSleeping()) {
            super.collide(entity);
        }

    }

    private void a(Entity entity) {
        if (this.world.getType(new BlockPosition(entity)).getBlock().a(TagsBlock.PORTALS)) {
            this.setPosition(entity.locX(), entity.e(1.0D) + 0.001D, entity.locZ());
        } else {
            double d0;
            double d1;
            double d2;
            int i;

            if (!(entity instanceof EntityBoat) && !(entity instanceof EntityHorseAbstract)) {
                double d3 = entity.locX();
                double d4 = entity.e(1.0D);
                double d5 = entity.locZ();
                EnumDirection enumdirection = entity.getAdjustedDirection();

                if (enumdirection != null && enumdirection.m() != EnumDirection.EnumAxis.Y) {
                    EnumDirection enumdirection1 = enumdirection.f();
                    int[][] aint = new int[][]{{0, 1}, {0, -1}, {-1, 1}, {-1, -1}, {1, 1}, {1, -1}, {-1, 0}, {1, 0}, {0, 1}};

                    d0 = Math.floor(this.locX()) + 0.5D;
                    double d6 = Math.floor(this.locZ()) + 0.5D;

                    d1 = this.getBoundingBox().maxX - this.getBoundingBox().minX;
                    d2 = this.getBoundingBox().maxZ - this.getBoundingBox().minZ;
                    AxisAlignedBB axisalignedbb = new AxisAlignedBB(d0 - d1 / 2.0D, entity.getBoundingBox().minY, d6 - d2 / 2.0D, d0 + d1 / 2.0D, Math.floor(entity.getBoundingBox().minY) + (double) this.getHeight(), d6 + d2 / 2.0D);
                    int[][] aint1 = aint;

                    i = aint.length;

                    for (int j = 0; j < i; ++j) {
                        int[] aint2 = aint1[j];
                        double d7 = (double) (enumdirection.getAdjacentX() * aint2[0] + enumdirection1.getAdjacentX() * aint2[1]);
                        double d8 = (double) (enumdirection.getAdjacentZ() * aint2[0] + enumdirection1.getAdjacentZ() * aint2[1]);
                        double d9 = d0 + d7;
                        double d10 = d6 + d8;
                        AxisAlignedBB axisalignedbb1 = axisalignedbb.d(d7, 0.0D, d8);
                        BlockPosition blockposition;

                        if (this.world.getCubes(this, axisalignedbb1)) {
                            blockposition = new BlockPosition(d9, this.locY(), d10);
                            if (this.world.getType(blockposition).a((IBlockAccess) this.world, blockposition, (Entity) this)) {
                                this.enderTeleportTo(d9, this.locY() + 1.0D, d10);
                                return;
                            }

                            BlockPosition blockposition1 = new BlockPosition(d9, this.locY() - 1.0D, d10);

                            if (this.world.getType(blockposition1).a((IBlockAccess) this.world, blockposition1, (Entity) this) || this.world.getFluid(blockposition1).a(TagsFluid.WATER)) {
                                d3 = d9;
                                d4 = this.locY() + 1.0D;
                                d5 = d10;
                            }
                        } else {
                            blockposition = new BlockPosition(d9, this.locY() + 1.0D, d10);
                            if (this.world.getCubes(this, axisalignedbb1.d(0.0D, 1.0D, 0.0D)) && this.world.getType(blockposition).a((IBlockAccess) this.world, blockposition, (Entity) this)) {
                                d3 = d9;
                                d4 = this.locY() + 2.0D;
                                d5 = d10;
                            }
                        }
                    }
                }

                this.enderTeleportTo(d3, d4, d5);
            } else {
                double d11 = (double) (this.getWidth() / 2.0F + entity.getWidth() / 2.0F) + 0.4D;
                AxisAlignedBB axisalignedbb2 = entity.getBoundingBox();
                double d12;
                byte b0;
                float f;

                if (entity instanceof EntityBoat) {
                    d12 = axisalignedbb2.maxY;
                    b0 = 2;
                    f = 0.0F;
                } else {
                    d12 = axisalignedbb2.minY;
                    b0 = 3;
                    f = 1.5707964F * (float) (this.getMainHand() == EnumMainHand.RIGHT ? -1 : 1);
                }

                float f1 = -this.yaw * 0.017453292F - 3.1415927F + f;
                float f2 = -MathHelper.sin(f1);
                float f3 = -MathHelper.cos(f1);

                d0 = Math.abs(f2) > Math.abs(f3) ? d11 / (double) Math.abs(f2) : d11 / (double) Math.abs(f3);
                AxisAlignedBB axisalignedbb3 = this.getBoundingBox().d(-this.locX(), -this.locY(), -this.locZ());
                ImmutableSet<Entity> immutableset = ImmutableSet.of(this, entity);

                d1 = this.locX() + (double) f2 * d0;
                d2 = this.locZ() + (double) f3 * d0;
                double d13 = 0.001D;

                for (i = 0; i < b0; ++i) {
                    double d14 = d12 + d13;

                    if (this.world.a((Entity) this, axisalignedbb3.d(d1, d14, d2), (Set) immutableset)) {
                        this.setPosition(d1, d14, d2);
                        return;
                    }

                    ++d13;
                }

                this.setPosition(entity.locX(), entity.e(1.0D) + 0.001D, entity.locZ());
            }
        }
    }

    protected float dp() {
        return 0.42F * this.ah();
    }

    protected void jump() {
        float f = this.dp();

        if (this.hasEffect(MobEffects.JUMP)) {
            f += 0.1F * (float) (this.getEffect(MobEffects.JUMP).getAmplifier() + 1);
        }

        Vec3D vec3d = this.getMot();

        this.setMot(vec3d.x, (double) f, vec3d.z);
        if (this.isSprinting()) {
            float f1 = this.yaw * 0.017453292F;

            this.setMot(this.getMot().add((double) (-MathHelper.sin(f1) * 0.2F), 0.0D, (double) (MathHelper.cos(f1) * 0.2F)));
        }

        this.impulse = true;
    }

    protected void c(Tag<FluidType> tag) {
        this.setMot(this.getMot().add(0.0D, 0.03999999910593033D, 0.0D));
    }

    protected float ds() {
        return 0.8F;
    }

    public void e(Vec3D vec3d) {
        double d0;
        float f;

        if (this.doAITick() || this.cj()) {
            d0 = 0.08D;
            boolean flag = this.getMot().y <= 0.0D;

            if (flag && this.hasEffect(MobEffects.SLOW_FALLING)) {
                d0 = 0.01D;
                this.fallDistance = 0.0F;
            }

            double d1;
            float f1;
            double d2;

            if (this.isInWater() && (!(this instanceof EntityHuman) || !((EntityHuman) this).abilities.isFlying)) {
                d1 = this.locY();
                f1 = this.isSprinting() ? 0.9F : this.ds();
                f = 0.02F;
                float f2 = (float) EnchantmentManager.e(this);

                if (f2 > 3.0F) {
                    f2 = 3.0F;
                }

                if (!this.onGround) {
                    f2 *= 0.5F;
                }

                if (f2 > 0.0F) {
                    f1 += (0.54600006F - f1) * f2 / 3.0F;
                    f += (this.dt() - f) * f2 / 3.0F;
                }

                if (this.hasEffect(MobEffects.DOLPHINS_GRACE)) {
                    f1 = 0.96F;
                }

                this.a(f, vec3d);
                this.move(EnumMoveType.SELF, this.getMot());
                Vec3D vec3d1 = this.getMot();

                if (this.positionChanged && this.isClimbing()) {
                    vec3d1 = new Vec3D(vec3d1.x, 0.2D, vec3d1.z);
                }

                this.setMot(vec3d1.d((double) f1, 0.800000011920929D, (double) f1));
                Vec3D vec3d2;

                if (!this.isNoGravity() && !this.isSprinting()) {
                    vec3d2 = this.getMot();
                    if (flag && Math.abs(vec3d2.y - 0.005D) >= 0.003D && Math.abs(vec3d2.y - d0 / 16.0D) < 0.003D) {
                        d2 = -0.003D;
                    } else {
                        d2 = vec3d2.y - d0 / 16.0D;
                    }

                    this.setMot(vec3d2.x, d2, vec3d2.z);
                }

                vec3d2 = this.getMot();
                if (this.positionChanged && this.e(vec3d2.x, vec3d2.y + 0.6000000238418579D - this.locY() + d1, vec3d2.z)) {
                    this.setMot(vec3d2.x, 0.30000001192092896D, vec3d2.z);
                }
            } else if (this.aH() && (!(this instanceof EntityHuman) || !((EntityHuman) this).abilities.isFlying)) {
                d1 = this.locY();
                this.a(0.02F, vec3d);
                this.move(EnumMoveType.SELF, this.getMot());
                this.setMot(this.getMot().a(0.5D));
                if (!this.isNoGravity()) {
                    this.setMot(this.getMot().add(0.0D, -d0 / 4.0D, 0.0D));
                }

                Vec3D vec3d3 = this.getMot();

                if (this.positionChanged && this.e(vec3d3.x, vec3d3.y + 0.6000000238418579D - this.locY() + d1, vec3d3.z)) {
                    this.setMot(vec3d3.x, 0.30000001192092896D, vec3d3.z);
                }
            } else if (this.isGliding()) {
                Vec3D vec3d4 = this.getMot();

                if (vec3d4.y > -0.5D) {
                    this.fallDistance = 1.0F;
                }

                Vec3D vec3d5 = this.getLookDirection();

                f1 = this.pitch * 0.017453292F;
                double d3 = Math.sqrt(vec3d5.x * vec3d5.x + vec3d5.z * vec3d5.z);
                double d4 = Math.sqrt(b(vec3d4));

                d2 = vec3d5.f();
                float f3 = MathHelper.cos(f1);

                f3 = (float) ((double) f3 * (double) f3 * Math.min(1.0D, d2 / 0.4D));
                vec3d4 = this.getMot().add(0.0D, d0 * (-1.0D + (double) f3 * 0.75D), 0.0D);
                double d5;

                if (vec3d4.y < 0.0D && d3 > 0.0D) {
                    d5 = vec3d4.y * -0.1D * (double) f3;
                    vec3d4 = vec3d4.add(vec3d5.x * d5 / d3, d5, vec3d5.z * d5 / d3);
                }

                if (f1 < 0.0F && d3 > 0.0D) {
                    d5 = d4 * (double) (-MathHelper.sin(f1)) * 0.04D;
                    vec3d4 = vec3d4.add(-vec3d5.x * d5 / d3, d5 * 3.2D, -vec3d5.z * d5 / d3);
                }

                if (d3 > 0.0D) {
                    vec3d4 = vec3d4.add((vec3d5.x / d3 * d4 - vec3d4.x) * 0.1D, 0.0D, (vec3d5.z / d3 * d4 - vec3d4.z) * 0.1D);
                }

                this.setMot(vec3d4.d(0.9900000095367432D, 0.9800000190734863D, 0.9900000095367432D));
                this.move(EnumMoveType.SELF, this.getMot());
                if (this.positionChanged && !this.world.isClientSide) {
                    d5 = Math.sqrt(b(this.getMot()));
                    double d6 = d4 - d5;
                    float f4 = (float) (d6 * 10.0D - 3.0D);

                    if (f4 > 0.0F) {
                        this.a(this.getSoundFall((int) f4), 1.0F, 1.0F);
                        this.damageEntity(DamageSource.FLY_INTO_WALL, f4);
                    }
                }

                if (this.onGround && !this.world.isClientSide) {
                    this.setFlag(7, false);
                }
            } else {
                BlockPosition blockposition = this.aj();
                float f5 = this.world.getType(blockposition).getBlock().l();

                f1 = this.onGround ? f5 * 0.91F : 0.91F;
                this.a(this.r(f5), vec3d);
                this.setMot(this.f(this.getMot()));
                this.move(EnumMoveType.SELF, this.getMot());
                Vec3D vec3d6 = this.getMot();

                if ((this.positionChanged || this.jumping) && this.isClimbing()) {
                    vec3d6 = new Vec3D(vec3d6.x, 0.2D, vec3d6.z);
                }

                double d7 = vec3d6.y;

                if (this.hasEffect(MobEffects.LEVITATION)) {
                    d7 += (0.05D * (double) (this.getEffect(MobEffects.LEVITATION).getAmplifier() + 1) - vec3d6.y) * 0.2D;
                    this.fallDistance = 0.0F;
                } else if (this.world.isClientSide && !this.world.isLoaded(blockposition)) {
                    if (this.locY() > 0.0D) {
                        d7 = -0.1D;
                    } else {
                        d7 = 0.0D;
                    }
                } else if (!this.isNoGravity()) {
                    d7 -= d0;
                }

                this.setMot(vec3d6.x * (double) f1, d7 * 0.9800000190734863D, vec3d6.z * (double) f1);
            }
        }

        this.aC = this.aD;
        d0 = this.locX() - this.lastX;
        double d8 = this.locZ() - this.lastZ;
        double d9 = this instanceof EntityBird ? this.locY() - this.lastY : 0.0D;

        f = MathHelper.sqrt(d0 * d0 + d9 * d9 + d8 * d8) * 4.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        this.aD += (f - this.aD) * 0.4F;
        this.aE += this.aD;
    }

    private Vec3D f(Vec3D vec3d) {
        if (this.isClimbing()) {
            this.fallDistance = 0.0F;
            float f = 0.15F;
            double d0 = MathHelper.a(vec3d.x, -0.15000000596046448D, 0.15000000596046448D);
            double d1 = MathHelper.a(vec3d.z, -0.15000000596046448D, 0.15000000596046448D);
            double d2 = Math.max(vec3d.y, -0.15000000596046448D);

            if (d2 < 0.0D && this.cY().getBlock() != Blocks.SCAFFOLDING && this.dJ() && this instanceof EntityHuman) {
                d2 = 0.0D;
            }

            vec3d = new Vec3D(d0, d2, d1);
        }

        return vec3d;
    }

    private float r(float f) {
        return this.onGround ? this.dt() * (0.21600002F / (f * f * f)) : this.aM;
    }

    public float dt() {
        return this.bB;
    }

    public void o(float f) {
        this.bB = f;
    }

    public boolean B(Entity entity) {
        this.z(entity);
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        this.o();
        this.r();
        if (!this.world.isClientSide) {
            int i = this.getArrowCount();

            if (i > 0) {
                if (this.at <= 0) {
                    this.at = 20 * (30 - i);
                }

                --this.at;
                if (this.at <= 0) {
                    this.setArrowCount(i - 1);
                }
            }

            int j = this.df();

            if (j > 0) {
                if (this.au <= 0) {
                    this.au = 20 * (30 - j);
                }

                --this.au;
                if (this.au <= 0) {
                    this.q(j - 1);
                }
            }

            EnumItemSlot[] aenumitemslot = EnumItemSlot.values();
            int k = aenumitemslot.length;

            for (int l = 0; l < k; ++l) {
                EnumItemSlot enumitemslot = aenumitemslot[l];
                ItemStack itemstack;

                switch (enumitemslot.a()) {
                    case HAND:
                        itemstack = (ItemStack) this.bu.get(enumitemslot.b());
                        break;
                    case ARMOR:
                        itemstack = (ItemStack) this.bv.get(enumitemslot.b());
                        break;
                    default:
                        continue;
                }

                ItemStack itemstack1 = this.getEquipment(enumitemslot);

                if (!ItemStack.matches(itemstack1, itemstack)) {
                    ((WorldServer) this.world).getChunkProvider().broadcast(this, new PacketPlayOutEntityEquipment(this.getId(), enumitemslot, itemstack1));
                    if (!itemstack.isEmpty()) {
                        this.getAttributeMap().a(itemstack.a(enumitemslot));
                    }

                    if (!itemstack1.isEmpty()) {
                        this.getAttributeMap().b(itemstack1.a(enumitemslot));
                    }

                    switch (enumitemslot.a()) {
                        case HAND:
                            this.bu.set(enumitemslot.b(), itemstack1.cloneItemStack());
                            break;
                        case ARMOR:
                            this.bv.set(enumitemslot.b(), itemstack1.cloneItemStack());
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

            if (this.isSleeping() && !this.s()) {
                this.entityWakeup();
            }
        }

        this.movementTick();
        double d0 = this.locX() - this.lastX;
        double d1 = this.locZ() - this.lastZ;
        float f = (float) (d0 * d0 + d1 * d1);
        float f1 = this.aI;
        float f2 = 0.0F;

        this.aR = this.aS;
        float f3 = 0.0F;

        if (f > 0.0025000002F) {
            f3 = 1.0F;
            f2 = (float) Math.sqrt((double) f) * 3.0F;
            float f4 = (float) MathHelper.d(d1, d0) * 57.295776F - 90.0F;
            float f5 = MathHelper.e(MathHelper.g(this.yaw) - f4);

            if (95.0F < f5 && f5 < 265.0F) {
                f1 = f4 - 180.0F;
            } else {
                f1 = f4;
            }
        }

        if (this.aA > 0.0F) {
            f1 = this.yaw;
        }

        if (!this.onGround) {
            f3 = 0.0F;
        }

        this.aS += (f3 - this.aS) * 0.3F;
        this.world.getMethodProfiler().enter("headTurn");
        f2 = this.f(f1, f2);
        this.world.getMethodProfiler().exit();
        this.world.getMethodProfiler().enter("rangeChecks");

        while (this.yaw - this.lastYaw < -180.0F) {
            this.lastYaw -= 360.0F;
        }

        while (this.yaw - this.lastYaw >= 180.0F) {
            this.lastYaw += 360.0F;
        }

        while (this.aI - this.aJ < -180.0F) {
            this.aJ -= 360.0F;
        }

        while (this.aI - this.aJ >= 180.0F) {
            this.aJ += 360.0F;
        }

        while (this.pitch - this.lastPitch < -180.0F) {
            this.lastPitch -= 360.0F;
        }

        while (this.pitch - this.lastPitch >= 180.0F) {
            this.lastPitch += 360.0F;
        }

        while (this.aK - this.aL < -180.0F) {
            this.aL -= 360.0F;
        }

        while (this.aK - this.aL >= 180.0F) {
            this.aL += 360.0F;
        }

        this.world.getMethodProfiler().exit();
        this.aT += f2;
        if (this.isGliding()) {
            ++this.bm;
        } else {
            this.bm = 0;
        }

        if (this.isSleeping()) {
            this.pitch = 0.0F;
        }

    }

    protected float f(float f, float f1) {
        float f2 = MathHelper.g(f - this.aI);

        this.aI += f2 * 0.3F;
        float f3 = MathHelper.g(this.yaw - this.aI);
        boolean flag = f3 < -90.0F || f3 >= 90.0F;

        if (f3 < -75.0F) {
            f3 = -75.0F;
        }

        if (f3 >= 75.0F) {
            f3 = 75.0F;
        }

        this.aI = this.yaw - f3;
        if (f3 * f3 > 2500.0F) {
            this.aI += f3 * 0.2F;
        }

        if (flag) {
            f1 *= -1.0F;
        }

        return f1;
    }

    public void movementTick() {
        if (this.jumpTicks > 0) {
            --this.jumpTicks;
        }

        if (this.cj()) {
            this.bc = 0;
            this.c(this.locX(), this.locY(), this.locZ());
        }

        if (this.bc > 0) {
            double d0 = this.locX() + (this.bd - this.locX()) / (double) this.bc;
            double d1 = this.locY() + (this.be - this.locY()) / (double) this.bc;
            double d2 = this.locZ() + (this.bf - this.locZ()) / (double) this.bc;
            double d3 = MathHelper.g(this.bg - (double) this.yaw);

            this.yaw = (float) ((double) this.yaw + d3 / (double) this.bc);
            this.pitch = (float) ((double) this.pitch + (this.bh - (double) this.pitch) / (double) this.bc);
            --this.bc;
            this.setPosition(d0, d1, d2);
            this.setYawPitch(this.yaw, this.pitch);
        } else if (!this.doAITick()) {
            this.setMot(this.getMot().a(0.98D));
        }

        if (this.bj > 0) {
            this.aK = (float) ((double) this.aK + MathHelper.g(this.bi - (double) this.aK) / (double) this.bj);
            --this.bj;
        }

        Vec3D vec3d = this.getMot();
        double d4 = vec3d.x;
        double d5 = vec3d.y;
        double d6 = vec3d.z;

        if (Math.abs(vec3d.x) < 0.003D) {
            d4 = 0.0D;
        }

        if (Math.abs(vec3d.y) < 0.003D) {
            d5 = 0.0D;
        }

        if (Math.abs(vec3d.z) < 0.003D) {
            d6 = 0.0D;
        }

        this.setMot(d4, d5, d6);
        this.world.getMethodProfiler().enter("ai");
        if (this.isFrozen()) {
            this.jumping = false;
            this.aZ = 0.0F;
            this.bb = 0.0F;
        } else if (this.doAITick()) {
            this.world.getMethodProfiler().enter("newAi");
            this.doTick();
            this.world.getMethodProfiler().exit();
        }

        this.world.getMethodProfiler().exit();
        this.world.getMethodProfiler().enter("jump");
        if (this.jumping) {
            if (this.N > 0.0D && (!this.onGround || this.N > 0.4D)) {
                this.c(TagsFluid.WATER);
            } else if (this.aH()) {
                this.c(TagsFluid.LAVA);
            } else if ((this.onGround || this.N > 0.0D && this.N <= 0.4D) && this.jumpTicks == 0) {
                this.jump();
                this.jumpTicks = 10;
            }
        } else {
            this.jumpTicks = 0;
        }

        this.world.getMethodProfiler().exit();
        this.world.getMethodProfiler().enter("travel");
        this.aZ *= 0.98F;
        this.bb *= 0.98F;
        this.n();
        AxisAlignedBB axisalignedbb = this.getBoundingBox();

        this.e(new Vec3D((double) this.aZ, (double) this.ba, (double) this.bb));
        this.world.getMethodProfiler().exit();
        this.world.getMethodProfiler().enter("push");
        if (this.bn > 0) {
            --this.bn;
            this.a(axisalignedbb, this.getBoundingBox());
        }

        this.collideNearby();
        this.world.getMethodProfiler().exit();
    }

    private void n() {
        boolean flag = this.getFlag(7);

        if (flag && !this.onGround && !this.isPassenger()) {
            ItemStack itemstack = this.getEquipment(EnumItemSlot.CHEST);

            if (itemstack.getItem() == Items.ELYTRA && ItemElytra.e(itemstack)) {
                flag = true;
                if (!this.world.isClientSide && (this.bm + 1) % 20 == 0) {
                    itemstack.damage(1, this, (entityliving) -> {
                        entityliving.broadcastItemBreak(EnumItemSlot.CHEST);
                    });
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

    protected void collideNearby() {
        List<Entity> list = this.world.getEntities(this, this.getBoundingBox(), IEntitySelector.a(this));

        if (!list.isEmpty()) {
            int i = this.world.getGameRules().getInt(GameRules.MAX_ENTITY_CRAMMING);
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

    protected void a(AxisAlignedBB axisalignedbb, AxisAlignedBB axisalignedbb1) {
        AxisAlignedBB axisalignedbb2 = axisalignedbb.b(axisalignedbb1);
        List<Entity> list = this.world.getEntities(this, axisalignedbb2);

        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); ++i) {
                Entity entity = (Entity) list.get(i);

                if (entity instanceof EntityLiving) {
                    this.g((EntityLiving) entity);
                    this.bn = 0;
                    this.setMot(this.getMot().a(-0.2D));
                    break;
                }
            }
        } else if (this.positionChanged) {
            this.bn = 0;
        }

        if (!this.world.isClientSide && this.bn <= 0) {
            this.c(4, false);
        }

    }

    protected void C(Entity entity) {
        entity.collide(this);
    }

    protected void g(EntityLiving entityliving) {}

    public void r(int i) {
        this.bn = i;
        if (!this.world.isClientSide) {
            this.c(4, true);
        }

    }

    public boolean isRiptiding() {
        return ((Byte) this.datawatcher.get(EntityLiving.ao) & 4) != 0;
    }

    @Override
    public void stopRiding() {
        Entity entity = this.getVehicle();

        super.stopRiding();
        if (entity != null && entity != this.getVehicle() && !this.world.isClientSide) {
            this.a(entity);
        }

    }

    @Override
    public void passengerTick() {
        super.passengerTick();
        this.aR = this.aS;
        this.aS = 0.0F;
        this.fallDistance = 0.0F;
    }

    public void setJumping(boolean flag) {
        this.jumping = flag;
    }

    public void receive(Entity entity, int i) {
        if (!entity.dead && !this.world.isClientSide && (entity instanceof EntityItem || entity instanceof EntityArrow || entity instanceof EntityExperienceOrb)) {
            ((WorldServer) this.world).getChunkProvider().broadcast(entity, new PacketPlayOutCollect(entity.getId(), this.getId(), i));
        }

    }

    public boolean hasLineOfSight(Entity entity) {
        Vec3D vec3d = new Vec3D(this.locX(), this.getHeadY(), this.locZ());
        Vec3D vec3d1 = new Vec3D(entity.locX(), entity.getHeadY(), entity.locZ());

        return this.world.rayTrace(new RayTrace(vec3d, vec3d1, RayTrace.BlockCollisionOption.COLLIDER, RayTrace.FluidCollisionOption.NONE, this)).getType() == MovingObjectPosition.EnumMovingObjectType.MISS;
    }

    @Override
    public float h(float f) {
        return f == 1.0F ? this.aK : MathHelper.g(f, this.aL, this.aK);
    }

    public boolean doAITick() {
        return !this.world.isClientSide;
    }

    @Override
    public boolean isInteractable() {
        return !this.dead;
    }

    @Override
    public boolean isCollidable() {
        return this.isAlive() && !this.isClimbing();
    }

    @Override
    protected void velocityChanged() {
        this.velocityChanged = this.random.nextDouble() >= this.getAttributeInstance(GenericAttributes.KNOCKBACK_RESISTANCE).getValue();
    }

    @Override
    public float getHeadRotation() {
        return this.aK;
    }

    @Override
    public void setHeadRotation(float f) {
        this.aK = f;
    }

    @Override
    public void l(float f) {
        this.aI = f;
    }

    public float getAbsorptionHearts() {
        return this.bD;
    }

    public void setAbsorptionHearts(float f) {
        if (f < 0.0F) {
            f = 0.0F;
        }

        this.bD = f;
    }

    public void enterCombat() {}

    public void exitCombat() {}

    protected void dz() {
        this.updateEffects = true;
    }

    public abstract EnumMainHand getMainHand();

    public boolean isHandRaised() {
        return ((Byte) this.datawatcher.get(EntityLiving.ao) & 1) > 0;
    }

    public EnumHand getRaisedHand() {
        return ((Byte) this.datawatcher.get(EntityLiving.ao) & 2) > 0 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
    }

    private void o() {
        if (this.isHandRaised()) {
            if (ItemStack.d(this.b(this.getRaisedHand()), this.activeItem)) {
                this.activeItem.b(this.world, this, this.dE());
                if (this.p()) {
                    this.b(this.activeItem, 5);
                }

                if (--this.bl == 0 && !this.world.isClientSide && !this.activeItem.m()) {
                    this.q();
                }
            } else {
                this.dH();
            }
        }

    }

    private boolean p() {
        int i = this.dE();
        FoodInfo foodinfo = this.activeItem.getItem().getFoodInfo();
        boolean flag = foodinfo != null && foodinfo.e();

        flag |= i <= this.activeItem.k() - 7;
        return flag && i % 4 == 0;
    }

    private void r() {
        this.bI = this.bH;
        if (this.br()) {
            this.bH = Math.min(1.0F, this.bH + 0.09F);
        } else {
            this.bH = Math.max(0.0F, this.bH - 0.09F);
        }

    }

    protected void c(int i, boolean flag) {
        byte b0 = (Byte) this.datawatcher.get(EntityLiving.ao);
        int j;

        if (flag) {
            j = b0 | i;
        } else {
            j = b0 & ~i;
        }

        this.datawatcher.set(EntityLiving.ao, (byte) j);
    }

    public void c(EnumHand enumhand) {
        ItemStack itemstack = this.b(enumhand);

        if (!itemstack.isEmpty() && !this.isHandRaised()) {
            this.activeItem = itemstack;
            this.bl = itemstack.k();
            if (!this.world.isClientSide) {
                this.c(1, true);
                this.c(2, enumhand == EnumHand.OFF_HAND);
            }

        }
    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        super.a(datawatcherobject);
        if (EntityLiving.bq.equals(datawatcherobject)) {
            if (this.world.isClientSide) {
                this.getBedPosition().ifPresent(this::a);
            }
        } else if (EntityLiving.ao.equals(datawatcherobject) && this.world.isClientSide) {
            if (this.isHandRaised() && this.activeItem.isEmpty()) {
                this.activeItem = this.b(this.getRaisedHand());
                if (!this.activeItem.isEmpty()) {
                    this.bl = this.activeItem.k();
                }
            } else if (!this.isHandRaised() && !this.activeItem.isEmpty()) {
                this.activeItem = ItemStack.a;
                this.bl = 0;
            }
        }

    }

    @Override
    public void a(ArgumentAnchor.Anchor argumentanchor_anchor, Vec3D vec3d) {
        super.a(argumentanchor_anchor, vec3d);
        this.aL = this.aK;
        this.aI = this.aK;
        this.aJ = this.aI;
    }

    protected void b(ItemStack itemstack, int i) {
        if (!itemstack.isEmpty() && this.isHandRaised()) {
            if (itemstack.l() == EnumAnimation.DRINK) {
                this.a(this.c(itemstack), 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
            }

            if (itemstack.l() == EnumAnimation.EAT) {
                this.a(itemstack, i);
                this.a(this.d(itemstack), 0.5F + 0.5F * (float) this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            }

        }
    }

    private void a(ItemStack itemstack, int i) {
        for (int j = 0; j < i; ++j) {
            Vec3D vec3d = new Vec3D(((double) this.random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);

            vec3d = vec3d.a(-this.pitch * 0.017453292F);
            vec3d = vec3d.b(-this.yaw * 0.017453292F);
            double d0 = (double) (-this.random.nextFloat()) * 0.6D - 0.3D;
            Vec3D vec3d1 = new Vec3D(((double) this.random.nextFloat() - 0.5D) * 0.3D, d0, 0.6D);

            vec3d1 = vec3d1.a(-this.pitch * 0.017453292F);
            vec3d1 = vec3d1.b(-this.yaw * 0.017453292F);
            vec3d1 = vec3d1.add(this.locX(), this.getHeadY(), this.locZ());
            this.world.addParticle(new ParticleParamItem(Particles.ITEM, itemstack), vec3d1.x, vec3d1.y, vec3d1.z, vec3d.x, vec3d.y + 0.05D, vec3d.z);
        }

    }

    protected void q() {
        if (!this.activeItem.equals(this.b(this.getRaisedHand()))) {
            this.clearActiveItem();
        } else {
            if (!this.activeItem.isEmpty() && this.isHandRaised()) {
                this.b(this.activeItem, 16);
                this.a(this.getRaisedHand(), this.activeItem.a(this.world, this));
                this.dH();
            }

        }
    }

    public ItemStack dD() {
        return this.activeItem;
    }

    public int dE() {
        return this.bl;
    }

    public int dF() {
        return this.isHandRaised() ? this.activeItem.k() - this.dE() : 0;
    }

    public void clearActiveItem() {
        if (!this.activeItem.isEmpty()) {
            this.activeItem.a(this.world, this, this.dE());
            if (this.activeItem.m()) {
                this.o();
            }
        }

        this.dH();
    }

    public void dH() {
        if (!this.world.isClientSide) {
            this.c(1, false);
        }

        this.activeItem = ItemStack.a;
        this.bl = 0;
    }

    public boolean isBlocking() {
        if (this.isHandRaised() && !this.activeItem.isEmpty()) {
            Item item = this.activeItem.getItem();

            return item.e_(this.activeItem) != EnumAnimation.BLOCK ? false : item.f_(this.activeItem) - this.bl >= 5;
        } else {
            return false;
        }
    }

    public boolean dJ() {
        return this.isSneaking();
    }

    public boolean isGliding() {
        return this.getFlag(7);
    }

    @Override
    public boolean br() {
        return super.br() || !this.isGliding() && this.getPose() == EntityPose.FALL_FLYING;
    }

    public boolean a(double d0, double d1, double d2, boolean flag) {
        double d3 = this.locX();
        double d4 = this.locY();
        double d5 = this.locZ();
        double d6 = d1;
        boolean flag1 = false;
        BlockPosition blockposition = new BlockPosition(d0, d1, d2);
        World world = this.world;

        if (world.isLoaded(blockposition)) {
            boolean flag2 = false;

            while (!flag2 && blockposition.getY() > 0) {
                BlockPosition blockposition1 = blockposition.down();
                IBlockData iblockdata = world.getType(blockposition1);

                if (iblockdata.getMaterial().isSolid()) {
                    flag2 = true;
                } else {
                    --d6;
                    blockposition = blockposition1;
                }
            }

            if (flag2) {
                this.enderTeleportTo(d0, d6, d2);
                if (world.getCubes(this) && !world.containsLiquid(this.getBoundingBox())) {
                    flag1 = true;
                }
            }
        }

        if (!flag1) {
            this.enderTeleportTo(d3, d4, d5);
            return false;
        } else {
            if (flag) {
                world.broadcastEntityEffect(this, (byte) 46);
            }

            if (this instanceof EntityCreature) {
                ((EntityCreature) this).getNavigation().o();
            }

            return true;
        }
    }

    public boolean dM() {
        return true;
    }

    public boolean dN() {
        return true;
    }

    public boolean e(ItemStack itemstack) {
        return false;
    }

    @Override
    public Packet<?> L() {
        return new PacketPlayOutSpawnEntityLiving(this);
    }

    @Override
    public EntitySize a(EntityPose entitypose) {
        return entitypose == EntityPose.SLEEPING ? EntityLiving.ap : super.a(entitypose).a(this.cC());
    }

    public Optional<BlockPosition> getBedPosition() {
        return (Optional) this.datawatcher.get(EntityLiving.bq);
    }

    public void d(BlockPosition blockposition) {
        this.datawatcher.set(EntityLiving.bq, Optional.of(blockposition));
    }

    public void dP() {
        this.datawatcher.set(EntityLiving.bq, Optional.empty());
    }

    public boolean isSleeping() {
        return this.getBedPosition().isPresent();
    }

    public void entitySleep(BlockPosition blockposition) {
        if (this.isPassenger()) {
            this.stopRiding();
        }

        IBlockData iblockdata = this.world.getType(blockposition);

        if (iblockdata.getBlock() instanceof BlockBed) {
            this.world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockBed.OCCUPIED, true), 3);
        }

        this.setPose(EntityPose.SLEEPING);
        this.a(blockposition);
        this.d(blockposition);
        this.setMot(Vec3D.a);
        this.impulse = true;
    }

    private void a(BlockPosition blockposition) {
        this.setPosition((double) blockposition.getX() + 0.5D, (double) ((float) blockposition.getY() + 0.6875F), (double) blockposition.getZ() + 0.5D);
    }

    private boolean s() {
        return (Boolean) this.getBedPosition().map((blockposition) -> {
            return this.world.getType(blockposition).getBlock() instanceof BlockBed;
        }).orElse(false);
    }

    public void entityWakeup() {
        Optional optional = this.getBedPosition();
        World world = this.world;

        this.world.getClass();
        optional.filter(world::isLoaded).ifPresent((blockposition) -> {
            IBlockData iblockdata = this.world.getType(blockposition);

            if (iblockdata.getBlock() instanceof BlockBed) {
                this.world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockBed.OCCUPIED, false), 3);
                Vec3D vec3d = (Vec3D) BlockBed.a(this.getEntityType(), (IWorldReader) this.world, blockposition, 0).orElseGet(() -> {
                    BlockPosition blockposition1 = blockposition.up();

                    return new Vec3D((double) blockposition1.getX() + 0.5D, (double) blockposition1.getY() + 0.1D, (double) blockposition1.getZ() + 0.5D);
                });

                this.setPosition(vec3d.x, vec3d.y, vec3d.z);
            }

        });
        this.setPose(EntityPose.STANDING);
        this.dP();
    }

    @Override
    public boolean inBlock() {
        return !this.isSleeping() && super.inBlock();
    }

    @Override
    protected final float getHeadHeight(EntityPose entitypose, EntitySize entitysize) {
        return entitypose == EntityPose.SLEEPING ? 0.2F : this.b(entitypose, entitysize);
    }

    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return super.getHeadHeight(entitypose, entitysize);
    }

    public ItemStack f(ItemStack itemstack) {
        return ItemStack.a;
    }

    public ItemStack a(World world, ItemStack itemstack) {
        if (itemstack.E()) {
            world.playSound((EntityHuman) null, this.locX(), this.locY(), this.locZ(), this.d(itemstack), SoundCategory.NEUTRAL, 1.0F, 1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F);
            this.a(itemstack, world, this);
            if (!(this instanceof EntityHuman) || !((EntityHuman) this).abilities.canInstantlyBuild) {
                itemstack.subtract(1);
            }
        }

        return itemstack;
    }

    private void a(ItemStack itemstack, World world, EntityLiving entityliving) {
        Item item = itemstack.getItem();

        if (item.isFood()) {
            List<Pair<MobEffect, Float>> list = item.getFoodInfo().f();
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                Pair<MobEffect, Float> pair = (Pair) iterator.next();

                if (!world.isClientSide && pair.getLeft() != null && world.random.nextFloat() < (Float) pair.getRight()) {
                    entityliving.addEffect(new MobEffect((MobEffect) pair.getLeft()));
                }
            }
        }

    }

    private static byte d(EnumItemSlot enumitemslot) {
        switch (enumitemslot) {
            case MAINHAND:
                return 47;
            case OFFHAND:
                return 48;
            case HEAD:
                return 49;
            case CHEST:
                return 50;
            case FEET:
                return 52;
            case LEGS:
                return 51;
            default:
                return 47;
        }
    }

    public void broadcastItemBreak(EnumItemSlot enumitemslot) {
        this.world.broadcastEntityEffect(this, d(enumitemslot));
    }

    public void broadcastItemBreak(EnumHand enumhand) {
        this.broadcastItemBreak(enumhand == EnumHand.MAIN_HAND ? EnumItemSlot.MAINHAND : EnumItemSlot.OFFHAND);
    }
}
