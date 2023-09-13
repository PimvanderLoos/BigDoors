package net.minecraft.world.entity.animal.horse;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsItem;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.IInventory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalArrowAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalBreed;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFollowParent;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLlamaFollow;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalPanic;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.PathfinderGoalTame;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.animal.EntityAnimal;
import net.minecraft.world.entity.animal.EntityWolf;
import net.minecraft.world.entity.monster.IRangedEntity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityLlamaSpit;
import net.minecraft.world.item.EnumColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockCarpet;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3D;

public class EntityLlama extends EntityHorseChestedAbstract implements IRangedEntity {

    private static final int MAX_STRENGTH = 5;
    private static final int VARIANTS = 4;
    private static final RecipeItemStack FOOD_ITEMS = RecipeItemStack.a(Items.WHEAT, Blocks.HAY_BLOCK.getItem());
    private static final DataWatcherObject<Integer> DATA_STRENGTH_ID = DataWatcher.a(EntityLlama.class, DataWatcherRegistry.INT);
    private static final DataWatcherObject<Integer> DATA_SWAG_ID = DataWatcher.a(EntityLlama.class, DataWatcherRegistry.INT);
    private static final DataWatcherObject<Integer> DATA_VARIANT_ID = DataWatcher.a(EntityLlama.class, DataWatcherRegistry.INT);
    boolean didSpit;
    @Nullable
    private EntityLlama caravanHead;
    @Nullable
    private EntityLlama caravanTail;

    public EntityLlama(EntityTypes<? extends EntityLlama> entitytypes, World world) {
        super(entitytypes, world);
    }

    public boolean ge() {
        return false;
    }

    public void setStrength(int i) {
        this.entityData.set(EntityLlama.DATA_STRENGTH_ID, Math.max(1, Math.min(5, i)));
    }

    private void go() {
        int i = this.random.nextFloat() < 0.04F ? 5 : 3;

        this.setStrength(1 + this.random.nextInt(i));
    }

    public int getStrength() {
        return (Integer) this.entityData.get(EntityLlama.DATA_STRENGTH_ID);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setInt("Variant", this.getVariant());
        nbttagcompound.setInt("Strength", this.getStrength());
        if (!this.inventory.getItem(1).isEmpty()) {
            nbttagcompound.set("DecorItem", this.inventory.getItem(1).save(new NBTTagCompound()));
        }

    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        this.setStrength(nbttagcompound.getInt("Strength"));
        super.loadData(nbttagcompound);
        this.setVariant(nbttagcompound.getInt("Variant"));
        if (nbttagcompound.hasKeyOfType("DecorItem", 10)) {
            this.inventory.setItem(1, ItemStack.a(nbttagcompound.getCompound("DecorItem")));
        }

        this.fO();
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalTame(this, 1.2D));
        this.goalSelector.a(2, new PathfinderGoalLlamaFollow(this, 2.0999999046325684D));
        this.goalSelector.a(3, new PathfinderGoalArrowAttack(this, 1.25D, 40, 20.0F));
        this.goalSelector.a(3, new PathfinderGoalPanic(this, 1.2D));
        this.goalSelector.a(4, new PathfinderGoalBreed(this, 1.0D));
        this.goalSelector.a(5, new PathfinderGoalFollowParent(this, 1.0D));
        this.goalSelector.a(6, new PathfinderGoalRandomStrollLand(this, 0.7D));
        this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new EntityLlama.c(this));
        this.targetSelector.a(2, new EntityLlama.a(this));
    }

    public static AttributeProvider.Builder gg() {
        return t().a(GenericAttributes.FOLLOW_RANGE, 40.0D);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityLlama.DATA_STRENGTH_ID, 0);
        this.entityData.register(EntityLlama.DATA_SWAG_ID, -1);
        this.entityData.register(EntityLlama.DATA_VARIANT_ID, 0);
    }

    public int getVariant() {
        return MathHelper.clamp((Integer) this.entityData.get(EntityLlama.DATA_VARIANT_ID), 0, 3);
    }

    public void setVariant(int i) {
        this.entityData.set(EntityLlama.DATA_VARIANT_ID, i);
    }

    @Override
    protected int getChestSlots() {
        return this.isCarryingChest() ? 2 + 3 * this.fE() : super.getChestSlots();
    }

    @Override
    public void i(Entity entity) {
        if (this.u(entity)) {
            float f = MathHelper.cos(this.yBodyRot * 0.017453292F);
            float f1 = MathHelper.sin(this.yBodyRot * 0.017453292F);
            float f2 = 0.3F;

            entity.setPosition(this.locX() + (double) (0.3F * f1), this.locY() + this.bl() + entity.bk(), this.locZ() - (double) (0.3F * f));
        }
    }

    @Override
    public double bl() {
        return (double) this.getHeight() * 0.67D;
    }

    @Override
    public boolean fd() {
        return false;
    }

    @Override
    public boolean isBreedItem(ItemStack itemstack) {
        return EntityLlama.FOOD_ITEMS.test(itemstack);
    }

    @Override
    protected boolean b(EntityHuman entityhuman, ItemStack itemstack) {
        byte b0 = 0;
        byte b1 = 0;
        float f = 0.0F;
        boolean flag = false;

        if (itemstack.a(Items.WHEAT)) {
            b0 = 10;
            b1 = 3;
            f = 2.0F;
        } else if (itemstack.a(Blocks.HAY_BLOCK.getItem())) {
            b0 = 90;
            b1 = 6;
            f = 10.0F;
            if (this.isTamed() && this.getAge() == 0 && this.fz()) {
                flag = true;
                this.g(entityhuman);
            }
        }

        if (this.getHealth() < this.getMaxHealth() && f > 0.0F) {
            this.heal(f);
            flag = true;
        }

        if (this.isBaby() && b0 > 0) {
            this.level.addParticle(Particles.HAPPY_VILLAGER, this.d(1.0D), this.da() + 0.5D, this.g(1.0D), 0.0D, 0.0D, 0.0D);
            if (!this.level.isClientSide) {
                this.setAge(b0);
            }

            flag = true;
        }

        if (b1 > 0 && (flag || !this.isTamed()) && this.getTemper() < this.getMaxDomestication()) {
            flag = true;
            if (!this.level.isClientSide) {
                this.w(b1);
            }
        }

        if (flag) {
            this.a(GameEvent.MOB_INTERACT, this.cT());
            if (!this.isSilent()) {
                SoundEffect soundeffect = this.fQ();

                if (soundeffect != null) {
                    this.level.playSound((EntityHuman) null, this.locX(), this.locY(), this.locZ(), this.fQ(), this.getSoundCategory(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
                }
            }
        }

        return flag;
    }

    @Override
    protected boolean isFrozen() {
        return this.dV() || this.fJ();
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        this.go();
        int i;

        if (groupdataentity instanceof EntityLlama.b) {
            i = ((EntityLlama.b) groupdataentity).variant;
        } else {
            i = this.random.nextInt(4);
            groupdataentity = new EntityLlama.b(i);
        }

        this.setVariant(i);
        return super.prepare(worldaccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) groupdataentity, nbttagcompound);
    }

    @Override
    protected SoundEffect getSoundAngry() {
        return SoundEffects.LLAMA_ANGRY;
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.LLAMA_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.LLAMA_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.LLAMA_DEATH;
    }

    @Nullable
    @Override
    protected SoundEffect fQ() {
        return SoundEffects.LLAMA_EAT;
    }

    @Override
    protected void b(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(SoundEffects.LLAMA_STEP, 0.15F, 1.0F);
    }

    @Override
    protected void fy() {
        this.playSound(SoundEffects.LLAMA_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
    }

    @Override
    public void fW() {
        SoundEffect soundeffect = this.getSoundAngry();

        if (soundeffect != null) {
            this.playSound(soundeffect, this.getSoundVolume(), this.ep());
        }

    }

    @Override
    public int fE() {
        return this.getStrength();
    }

    @Override
    public boolean gc() {
        return true;
    }

    @Override
    public boolean gd() {
        return !this.inventory.getItem(1).isEmpty();
    }

    @Override
    public boolean m(ItemStack itemstack) {
        return itemstack.a((Tag) TagsItem.CARPETS);
    }

    @Override
    public boolean canSaddle() {
        return false;
    }

    @Override
    public void a(IInventory iinventory) {
        EnumColor enumcolor = this.gi();

        super.a(iinventory);
        EnumColor enumcolor1 = this.gi();

        if (this.tickCount > 20 && enumcolor1 != null && enumcolor1 != enumcolor) {
            this.playSound(SoundEffects.LLAMA_SWAG, 0.5F, 1.0F);
        }

    }

    @Override
    protected void fO() {
        if (!this.level.isClientSide) {
            super.fO();
            this.a(o(this.inventory.getItem(1)));
        }
    }

    private void a(@Nullable EnumColor enumcolor) {
        this.entityData.set(EntityLlama.DATA_SWAG_ID, enumcolor == null ? -1 : enumcolor.getColorIndex());
    }

    @Nullable
    private static EnumColor o(ItemStack itemstack) {
        Block block = Block.asBlock(itemstack.getItem());

        return block instanceof BlockCarpet ? ((BlockCarpet) block).c() : null;
    }

    @Nullable
    public EnumColor gi() {
        int i = (Integer) this.entityData.get(EntityLlama.DATA_SWAG_ID);

        return i == -1 ? null : EnumColor.fromColorIndex(i);
    }

    @Override
    public int getMaxDomestication() {
        return 30;
    }

    @Override
    public boolean mate(EntityAnimal entityanimal) {
        return entityanimal != this && entityanimal instanceof EntityLlama && this.fY() && ((EntityLlama) entityanimal).fY();
    }

    @Override
    public EntityLlama createChild(WorldServer worldserver, EntityAgeable entityageable) {
        EntityLlama entityllama = this.gj();

        this.a(entityageable, (EntityHorseAbstract) entityllama);
        EntityLlama entityllama1 = (EntityLlama) entityageable;
        int i = this.random.nextInt(Math.max(this.getStrength(), entityllama1.getStrength())) + 1;

        if (this.random.nextFloat() < 0.03F) {
            ++i;
        }

        entityllama.setStrength(i);
        entityllama.setVariant(this.random.nextBoolean() ? this.getVariant() : entityllama1.getVariant());
        return entityllama;
    }

    protected EntityLlama gj() {
        return (EntityLlama) EntityTypes.LLAMA.a(this.level);
    }

    private void j(EntityLiving entityliving) {
        EntityLlamaSpit entityllamaspit = new EntityLlamaSpit(this.level, this);
        double d0 = entityliving.locX() - this.locX();
        double d1 = entityliving.e(0.3333333333333333D) - entityllamaspit.locY();
        double d2 = entityliving.locZ() - this.locZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2) * 0.20000000298023224D;

        entityllamaspit.shoot(d0, d1 + d3, d2, 1.5F, 10.0F);
        if (!this.isSilent()) {
            this.level.playSound((EntityHuman) null, this.locX(), this.locY(), this.locZ(), SoundEffects.LLAMA_SPIT, this.getSoundCategory(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
        }

        this.level.addEntity(entityllamaspit);
        this.didSpit = true;
    }

    void C(boolean flag) {
        this.didSpit = flag;
    }

    @Override
    public boolean a(float f, float f1, DamageSource damagesource) {
        int i = this.d(f, f1);

        if (i <= 0) {
            return false;
        } else {
            if (f >= 6.0F) {
                this.damageEntity(damagesource, (float) i);
                if (this.isVehicle()) {
                    Iterator iterator = this.getAllPassengers().iterator();

                    while (iterator.hasNext()) {
                        Entity entity = (Entity) iterator.next();

                        entity.damageEntity(damagesource, (float) i);
                    }
                }
            }

            this.playBlockStepSound();
            return true;
        }
    }

    public void gk() {
        if (this.caravanHead != null) {
            this.caravanHead.caravanTail = null;
        }

        this.caravanHead = null;
    }

    public void a(EntityLlama entityllama) {
        this.caravanHead = entityllama;
        this.caravanHead.caravanTail = this;
    }

    public boolean gl() {
        return this.caravanTail != null;
    }

    public boolean gm() {
        return this.caravanHead != null;
    }

    @Nullable
    public EntityLlama gn() {
        return this.caravanHead;
    }

    @Override
    protected double fv() {
        return 2.0D;
    }

    @Override
    protected void fU() {
        if (!this.gm() && this.isBaby()) {
            super.fU();
        }

    }

    @Override
    public boolean fV() {
        return false;
    }

    @Override
    public void a(EntityLiving entityliving, float f) {
        this.j(entityliving);
    }

    @Override
    public Vec3D cu() {
        return new Vec3D(0.0D, 0.75D * (double) this.getHeadHeight(), (double) this.getWidth() * 0.5D);
    }

    private static class c extends PathfinderGoalHurtByTarget {

        public c(EntityLlama entityllama) {
            super(entityllama);
        }

        @Override
        public boolean b() {
            if (this.mob instanceof EntityLlama) {
                EntityLlama entityllama = (EntityLlama) this.mob;

                if (entityllama.didSpit) {
                    entityllama.C(false);
                    return false;
                }
            }

            return super.b();
        }
    }

    private static class a extends PathfinderGoalNearestAttackableTarget<EntityWolf> {

        public a(EntityLlama entityllama) {
            super(entityllama, EntityWolf.class, 16, false, true, (entityliving) -> {
                return !((EntityWolf) entityliving).isTamed();
            });
        }

        @Override
        protected double k() {
            return super.k() * 0.25D;
        }
    }

    private static class b extends EntityAgeable.a {

        public final int variant;

        b(int i) {
            super(true);
            this.variant = i;
        }
    }
}
