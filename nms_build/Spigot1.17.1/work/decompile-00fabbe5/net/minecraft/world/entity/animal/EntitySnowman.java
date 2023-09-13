package net.minecraft.world.entity.animal;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.IShearable;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalArrowAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.monster.IMonster;
import net.minecraft.world.entity.monster.IRangedEntity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntitySnowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3D;

public class EntitySnowman extends EntityGolem implements IShearable, IRangedEntity {

    private static final DataWatcherObject<Byte> DATA_PUMPKIN_ID = DataWatcher.a(EntitySnowman.class, DataWatcherRegistry.BYTE);
    private static final byte PUMPKIN_FLAG = 16;
    private static final float EYE_HEIGHT = 1.7F;

    public EntitySnowman(EntityTypes<? extends EntitySnowman> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(1, new PathfinderGoalArrowAttack(this, 1.25D, 20, 10.0F));
        this.goalSelector.a(2, new PathfinderGoalRandomStrollLand(this, 1.0D, 1.0000001E-5F));
        this.goalSelector.a(3, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(4, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget<>(this, EntityInsentient.class, 10, true, false, (entityliving) -> {
            return entityliving instanceof IMonster;
        }));
    }

    public static AttributeProvider.Builder n() {
        return EntityInsentient.w().a(GenericAttributes.MAX_HEALTH, 4.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.20000000298023224D);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntitySnowman.DATA_PUMPKIN_ID, (byte) 16);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setBoolean("Pumpkin", this.hasPumpkin());
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        if (nbttagcompound.hasKey("Pumpkin")) {
            this.setHasPumpkin(nbttagcompound.getBoolean("Pumpkin"));
        }

    }

    @Override
    public boolean ex() {
        return true;
    }

    @Override
    public void movementTick() {
        super.movementTick();
        if (!this.level.isClientSide) {
            int i = MathHelper.floor(this.locX());
            int j = MathHelper.floor(this.locY());
            int k = MathHelper.floor(this.locZ());

            if (this.level.getBiome(new BlockPosition(i, 0, k)).getAdjustedTemperature(new BlockPosition(i, j, k)) > 1.0F) {
                this.damageEntity(DamageSource.ON_FIRE, 1.0F);
            }

            if (!this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                return;
            }

            IBlockData iblockdata = Blocks.SNOW.getBlockData();

            for (int l = 0; l < 4; ++l) {
                i = MathHelper.floor(this.locX() + (double) ((float) (l % 2 * 2 - 1) * 0.25F));
                j = MathHelper.floor(this.locY());
                k = MathHelper.floor(this.locZ() + (double) ((float) (l / 2 % 2 * 2 - 1) * 0.25F));
                BlockPosition blockposition = new BlockPosition(i, j, k);

                if (this.level.getType(blockposition).isAir() && this.level.getBiome(blockposition).getAdjustedTemperature(blockposition) < 0.8F && iblockdata.canPlace(this.level, blockposition)) {
                    this.level.setTypeUpdate(blockposition, iblockdata);
                }
            }
        }

    }

    @Override
    public void a(EntityLiving entityliving, float f) {
        EntitySnowball entitysnowball = new EntitySnowball(this.level, this);
        double d0 = entityliving.getHeadY() - 1.100000023841858D;
        double d1 = entityliving.locX() - this.locX();
        double d2 = d0 - entitysnowball.locY();
        double d3 = entityliving.locZ() - this.locZ();
        double d4 = Math.sqrt(d1 * d1 + d3 * d3) * 0.20000000298023224D;

        entitysnowball.shoot(d1, d2 + d4, d3, 1.6F, 12.0F);
        this.playSound(SoundEffects.SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level.addEntity(entitysnowball);
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return 1.7F;
    }

    @Override
    protected EnumInteractionResult b(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (itemstack.a(Items.SHEARS) && this.canShear()) {
            this.shear(SoundCategory.PLAYERS);
            this.a(GameEvent.SHEAR, (Entity) entityhuman);
            if (!this.level.isClientSide) {
                itemstack.damage(1, entityhuman, (entityhuman1) -> {
                    entityhuman1.broadcastItemBreak(enumhand);
                });
            }

            return EnumInteractionResult.a(this.level.isClientSide);
        } else {
            return EnumInteractionResult.PASS;
        }
    }

    @Override
    public void shear(SoundCategory soundcategory) {
        this.level.playSound((EntityHuman) null, (Entity) this, SoundEffects.SNOW_GOLEM_SHEAR, soundcategory, 1.0F, 1.0F);
        if (!this.level.isClientSide()) {
            this.setHasPumpkin(false);
            this.a(new ItemStack(Items.CARVED_PUMPKIN), 1.7F);
        }

    }

    @Override
    public boolean canShear() {
        return this.isAlive() && this.hasPumpkin();
    }

    public boolean hasPumpkin() {
        return ((Byte) this.entityData.get(EntitySnowman.DATA_PUMPKIN_ID) & 16) != 0;
    }

    public void setHasPumpkin(boolean flag) {
        byte b0 = (Byte) this.entityData.get(EntitySnowman.DATA_PUMPKIN_ID);

        if (flag) {
            this.entityData.set(EntitySnowman.DATA_PUMPKIN_ID, (byte) (b0 | 16));
        } else {
            this.entityData.set(EntitySnowman.DATA_PUMPKIN_ID, (byte) (b0 & -17));
        }

    }

    @Nullable
    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.SNOW_GOLEM_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.SNOW_GOLEM_HURT;
    }

    @Nullable
    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.SNOW_GOLEM_DEATH;
    }

    @Override
    public Vec3D cu() {
        return new Vec3D(0.0D, (double) (0.75F * this.getHeadHeight()), (double) (this.getWidth() * 0.4F));
    }
}
