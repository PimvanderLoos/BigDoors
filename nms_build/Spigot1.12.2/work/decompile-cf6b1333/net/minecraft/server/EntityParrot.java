package net.minecraft.server;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;

public class EntityParrot extends EntityPerchable implements EntityBird {

    private static final DataWatcherObject<Integer> bG = DataWatcher.a(EntityParrot.class, DataWatcherRegistry.b);
    private static final Predicate<EntityInsentient> bH = new Predicate() {
        public boolean a(@Nullable EntityInsentient entityinsentient) {
            return entityinsentient != null && EntityParrot.bK.containsKey(EntityTypes.b.a((Object) entityinsentient.getClass()));
        }

        public boolean apply(@Nullable Object object) {
            return this.a((EntityInsentient) object);
        }
    };
    private static final Item bI = Items.COOKIE;
    private static final Set<Item> bJ = Sets.newHashSet(new Item[] { Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS});
    private static final Int2ObjectMap<SoundEffect> bK = new Int2ObjectOpenHashMap(32);
    public float bB;
    public float bC;
    public float bD;
    public float bE;
    public float bF = 1.0F;
    private boolean bL;
    private BlockPosition bM;

    public EntityParrot(World world) {
        super(world);
        this.setSize(0.5F, 0.9F);
        this.moveController = new ControllerMoveFlying(this);
    }

    @Nullable
    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, @Nullable GroupDataEntity groupdataentity) {
        this.setVariant(this.random.nextInt(5));
        return super.prepare(difficultydamagescaler, groupdataentity);
    }

    protected void r() {
        this.goalSit = new PathfinderGoalSit(this);
        this.goalSelector.a(0, new PathfinderGoalPanic(this, 1.25D));
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(2, this.goalSit);
        this.goalSelector.a(2, new PathfinderGoalFollowOwnerParrot(this, 1.0D, 5.0F, 1.0F));
        this.goalSelector.a(2, new PathfinderGoalRandomFly(this, 1.0D));
        this.goalSelector.a(3, new PathfinderGoalPerch(this));
        this.goalSelector.a(3, new PathfinderGoalFollowEntity(this, 1.0D, 3.0F, 7.0F));
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeMap().b(GenericAttributes.e);
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(6.0D);
        this.getAttributeInstance(GenericAttributes.e).setValue(0.4000000059604645D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.20000000298023224D);
    }

    protected NavigationAbstract b(World world) {
        NavigationFlying navigationflying = new NavigationFlying(this, world);

        navigationflying.a(false);
        navigationflying.c(true);
        navigationflying.b(true);
        return navigationflying;
    }

    public float getHeadHeight() {
        return this.length * 0.6F;
    }

    public void n() {
        b(this.world, (Entity) this);
        if (this.bM == null || this.bM.distanceSquared(this.locX, this.locY, this.locZ) > 12.0D || this.world.getType(this.bM).getBlock() != Blocks.JUKEBOX) {
            this.bL = false;
            this.bM = null;
        }

        super.n();
        this.dx();
    }

    private void dx() {
        this.bE = this.bB;
        this.bD = this.bC;
        this.bC = (float) ((double) this.bC + (double) (this.onGround ? -1 : 4) * 0.3D);
        this.bC = MathHelper.a(this.bC, 0.0F, 1.0F);
        if (!this.onGround && this.bF < 1.0F) {
            this.bF = 1.0F;
        }

        this.bF = (float) ((double) this.bF * 0.9D);
        if (!this.onGround && this.motY < 0.0D) {
            this.motY *= 0.6D;
        }

        this.bB += this.bF * 2.0F;
    }

    private static boolean b(World world, Entity entity) {
        if (!entity.isSilent() && world.random.nextInt(50) == 0) {
            List list = world.a(EntityInsentient.class, entity.getBoundingBox().g(20.0D), EntityParrot.bH);

            if (!list.isEmpty()) {
                EntityInsentient entityinsentient = (EntityInsentient) list.get(world.random.nextInt(list.size()));

                if (!entityinsentient.isSilent()) {
                    SoundEffect soundeffect = g(EntityTypes.b.a((Object) entityinsentient.getClass()));

                    world.a((EntityHuman) null, entity.locX, entity.locY, entity.locZ, soundeffect, entity.bK(), 0.7F, b(world.random));
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (!this.isTamed() && EntityParrot.bJ.contains(itemstack.getItem())) {
            if (!entityhuman.abilities.canInstantlyBuild) {
                itemstack.subtract(1);
            }

            if (!this.isSilent()) {
                this.world.a((EntityHuman) null, this.locX, this.locY, this.locZ, SoundEffects.eJ, this.bK(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            }

            if (!this.world.isClientSide) {
                if (this.random.nextInt(10) == 0) {
                    this.c(entityhuman);
                    this.p(true);
                    this.world.broadcastEntityEffect(this, (byte) 7);
                } else {
                    this.p(false);
                    this.world.broadcastEntityEffect(this, (byte) 6);
                }
            }

            return true;
        } else if (itemstack.getItem() == EntityParrot.bI) {
            if (!entityhuman.abilities.canInstantlyBuild) {
                itemstack.subtract(1);
            }

            this.addEffect(new MobEffect(MobEffects.POISON, 900));
            if (entityhuman.z() || !this.be()) {
                this.damageEntity(DamageSource.playerAttack(entityhuman), Float.MAX_VALUE);
            }

            return true;
        } else {
            if (!this.world.isClientSide && !this.a() && this.isTamed() && this.e((EntityLiving) entityhuman)) {
                this.goalSit.setSitting(!this.isSitting());
            }

            return super.a(entityhuman, enumhand);
        }
    }

    public boolean e(ItemStack itemstack) {
        return false;
    }

    public boolean P() {
        int i = MathHelper.floor(this.locX);
        int j = MathHelper.floor(this.getBoundingBox().b);
        int k = MathHelper.floor(this.locZ);
        BlockPosition blockposition = new BlockPosition(i, j, k);
        Block block = this.world.getType(blockposition.down()).getBlock();

        return block instanceof BlockLeaves || block == Blocks.GRASS || block instanceof BlockLogAbstract || block == Blocks.AIR && this.world.j(blockposition) > 8 && super.P();
    }

    public void e(float f, float f1) {}

    protected void a(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {}

    public boolean mate(EntityAnimal entityanimal) {
        return false;
    }

    @Nullable
    public EntityAgeable createChild(EntityAgeable entityageable) {
        return null;
    }

    public static void a(World world, Entity entity) {
        if (!entity.isSilent() && !b(world, entity) && world.random.nextInt(200) == 0) {
            world.a((EntityHuman) null, entity.locX, entity.locY, entity.locZ, a(world.random), entity.bK(), 1.0F, b(world.random));
        }

    }

    public boolean B(Entity entity) {
        return entity.damageEntity(DamageSource.mobAttack(this), 3.0F);
    }

    @Nullable
    public SoundEffect F() {
        return a(this.random);
    }

    private static SoundEffect a(Random random) {
        if (random.nextInt(1000) == 0) {
            ArrayList arraylist = new ArrayList(EntityParrot.bK.keySet());

            return g(((Integer) arraylist.get(random.nextInt(arraylist.size()))).intValue());
        } else {
            return SoundEffects.eH;
        }
    }

    public static SoundEffect g(int i) {
        return EntityParrot.bK.containsKey(i) ? (SoundEffect) EntityParrot.bK.get(i) : SoundEffects.eH;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.eL;
    }

    protected SoundEffect cf() {
        return SoundEffects.eI;
    }

    protected void a(BlockPosition blockposition, Block block) {
        this.a(SoundEffects.fn, 0.15F, 1.0F);
    }

    protected float d(float f) {
        this.a(SoundEffects.eK, 0.15F, 1.0F);
        return f + this.bC / 2.0F;
    }

    protected boolean ah() {
        return true;
    }

    protected float cr() {
        return b(this.random);
    }

    private static float b(Random random) {
        return (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F;
    }

    public SoundCategory bK() {
        return SoundCategory.NEUTRAL;
    }

    public boolean isCollidable() {
        return true;
    }

    protected void C(Entity entity) {
        if (!(entity instanceof EntityHuman)) {
            super.C(entity);
        }
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            if (this.goalSit != null) {
                this.goalSit.setSitting(false);
            }

            return super.damageEntity(damagesource, f);
        }
    }

    public int getVariant() {
        return MathHelper.clamp(((Integer) this.datawatcher.get(EntityParrot.bG)).intValue(), 0, 4);
    }

    public void setVariant(int i) {
        this.datawatcher.set(EntityParrot.bG, Integer.valueOf(i));
    }

    protected void i() {
        super.i();
        this.datawatcher.register(EntityParrot.bG, Integer.valueOf(0));
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("Variant", this.getVariant());
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setVariant(nbttagcompound.getInt("Variant"));
    }

    @Nullable
    protected MinecraftKey J() {
        return LootTables.ax;
    }

    public boolean a() {
        return !this.onGround;
    }

    static {
        EntityParrot.bK.put(EntityTypes.b.a((Object) EntityBlaze.class), SoundEffects.eM);
        EntityParrot.bK.put(EntityTypes.b.a((Object) EntityCaveSpider.class), SoundEffects.fc);
        EntityParrot.bK.put(EntityTypes.b.a((Object) EntityCreeper.class), SoundEffects.eN);
        EntityParrot.bK.put(EntityTypes.b.a((Object) EntityGuardianElder.class), SoundEffects.eO);
        EntityParrot.bK.put(EntityTypes.b.a((Object) EntityEnderDragon.class), SoundEffects.eP);
        EntityParrot.bK.put(EntityTypes.b.a((Object) EntityEnderman.class), SoundEffects.eQ);
        EntityParrot.bK.put(EntityTypes.b.a((Object) EntityEndermite.class), SoundEffects.eR);
        EntityParrot.bK.put(EntityTypes.b.a((Object) EntityEvoker.class), SoundEffects.eS);
        EntityParrot.bK.put(EntityTypes.b.a((Object) EntityGhast.class), SoundEffects.eT);
        EntityParrot.bK.put(EntityTypes.b.a((Object) EntityZombieHusk.class), SoundEffects.eU);
        EntityParrot.bK.put(EntityTypes.b.a((Object) EntityIllagerIllusioner.class), SoundEffects.eV);
        EntityParrot.bK.put(EntityTypes.b.a((Object) EntityMagmaCube.class), SoundEffects.eW);
        EntityParrot.bK.put(EntityTypes.b.a((Object) EntityPigZombie.class), SoundEffects.fl);
        EntityParrot.bK.put(EntityTypes.b.a((Object) EntityPolarBear.class), SoundEffects.eX);
        EntityParrot.bK.put(EntityTypes.b.a((Object) EntityShulker.class), SoundEffects.eY);
        EntityParrot.bK.put(EntityTypes.b.a((Object) EntitySilverfish.class), SoundEffects.eZ);
        EntityParrot.bK.put(EntityTypes.b.a((Object) EntitySkeleton.class), SoundEffects.fa);
        EntityParrot.bK.put(EntityTypes.b.a((Object) EntitySlime.class), SoundEffects.fb);
        EntityParrot.bK.put(EntityTypes.b.a((Object) EntitySpider.class), SoundEffects.fc);
        EntityParrot.bK.put(EntityTypes.b.a((Object) EntitySkeletonStray.class), SoundEffects.fd);
        EntityParrot.bK.put(EntityTypes.b.a((Object) EntityVex.class), SoundEffects.fe);
        EntityParrot.bK.put(EntityTypes.b.a((Object) EntityVindicator.class), SoundEffects.ff);
        EntityParrot.bK.put(EntityTypes.b.a((Object) EntityWitch.class), SoundEffects.fg);
        EntityParrot.bK.put(EntityTypes.b.a((Object) EntityWither.class), SoundEffects.fh);
        EntityParrot.bK.put(EntityTypes.b.a((Object) EntitySkeletonWither.class), SoundEffects.fi);
        EntityParrot.bK.put(EntityTypes.b.a((Object) EntityWolf.class), SoundEffects.fj);
        EntityParrot.bK.put(EntityTypes.b.a((Object) EntityZombie.class), SoundEffects.fk);
        EntityParrot.bK.put(EntityTypes.b.a((Object) EntityZombieVillager.class), SoundEffects.fm);
    }
}
