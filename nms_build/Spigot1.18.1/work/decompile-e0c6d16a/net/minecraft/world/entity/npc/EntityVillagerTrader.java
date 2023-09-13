package net.minecraft.world.entity.npc;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IPosition;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityExperienceOrb;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalAvoidTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalInteract;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtTradingPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.world.entity.ai.goal.PathfinderGoalPanic;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.PathfinderGoalTradeWithPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalUseItem;
import net.minecraft.world.entity.monster.EntityEvoker;
import net.minecraft.world.entity.monster.EntityIllagerIllusioner;
import net.minecraft.world.entity.monster.EntityPillager;
import net.minecraft.world.entity.monster.EntityVex;
import net.minecraft.world.entity.monster.EntityVindicator;
import net.minecraft.world.entity.monster.EntityZoglin;
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtil;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.trading.MerchantRecipe;
import net.minecraft.world.item.trading.MerchantRecipeList;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;

public class EntityVillagerTrader extends EntityVillagerAbstract {

    private static final int NUMBER_OF_TRADE_OFFERS = 5;
    @Nullable
    private BlockPosition wanderTarget;
    private int despawnDelay;

    public EntityVillagerTrader(EntityTypes<? extends EntityVillagerTrader> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new PathfinderGoalFloat(this));
        this.goalSelector.addGoal(0, new PathfinderGoalUseItem<>(this, PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.INVISIBILITY), SoundEffects.WANDERING_TRADER_DISAPPEARED, (entityvillagertrader) -> {
            return this.level.isNight() && !entityvillagertrader.isInvisible();
        }));
        this.goalSelector.addGoal(0, new PathfinderGoalUseItem<>(this, new ItemStack(Items.MILK_BUCKET), SoundEffects.WANDERING_TRADER_REAPPEARED, (entityvillagertrader) -> {
            return this.level.isDay() && entityvillagertrader.isInvisible();
        }));
        this.goalSelector.addGoal(1, new PathfinderGoalTradeWithPlayer(this));
        this.goalSelector.addGoal(1, new PathfinderGoalAvoidTarget<>(this, EntityZombie.class, 8.0F, 0.5D, 0.5D));
        this.goalSelector.addGoal(1, new PathfinderGoalAvoidTarget<>(this, EntityEvoker.class, 12.0F, 0.5D, 0.5D));
        this.goalSelector.addGoal(1, new PathfinderGoalAvoidTarget<>(this, EntityVindicator.class, 8.0F, 0.5D, 0.5D));
        this.goalSelector.addGoal(1, new PathfinderGoalAvoidTarget<>(this, EntityVex.class, 8.0F, 0.5D, 0.5D));
        this.goalSelector.addGoal(1, new PathfinderGoalAvoidTarget<>(this, EntityPillager.class, 15.0F, 0.5D, 0.5D));
        this.goalSelector.addGoal(1, new PathfinderGoalAvoidTarget<>(this, EntityIllagerIllusioner.class, 12.0F, 0.5D, 0.5D));
        this.goalSelector.addGoal(1, new PathfinderGoalAvoidTarget<>(this, EntityZoglin.class, 10.0F, 0.5D, 0.5D));
        this.goalSelector.addGoal(1, new PathfinderGoalPanic(this, 0.5D));
        this.goalSelector.addGoal(1, new PathfinderGoalLookAtTradingPlayer(this));
        this.goalSelector.addGoal(2, new EntityVillagerTrader.a(this, 2.0D, 0.35D));
        this.goalSelector.addGoal(4, new PathfinderGoalMoveTowardsRestriction(this, 0.35D));
        this.goalSelector.addGoal(8, new PathfinderGoalRandomStrollLand(this, 0.35D));
        this.goalSelector.addGoal(9, new PathfinderGoalInteract(this, EntityHuman.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new PathfinderGoalLookAtPlayer(this, EntityInsentient.class, 8.0F));
    }

    @Nullable
    @Override
    public EntityAgeable getBreedOffspring(WorldServer worldserver, EntityAgeable entityageable) {
        return null;
    }

    @Override
    public boolean showProgressBar() {
        return false;
    }

    @Override
    public EnumInteractionResult mobInteract(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);

        if (!itemstack.is(Items.VILLAGER_SPAWN_EGG) && this.isAlive() && !this.isTrading() && !this.isBaby()) {
            if (enumhand == EnumHand.MAIN_HAND) {
                entityhuman.awardStat(StatisticList.TALKED_TO_VILLAGER);
            }

            if (this.getOffers().isEmpty()) {
                return EnumInteractionResult.sidedSuccess(this.level.isClientSide);
            } else {
                if (!this.level.isClientSide) {
                    this.setTradingPlayer(entityhuman);
                    this.openTradingScreen(entityhuman, this.getDisplayName(), 1);
                }

                return EnumInteractionResult.sidedSuccess(this.level.isClientSide);
            }
        } else {
            return super.mobInteract(entityhuman, enumhand);
        }
    }

    @Override
    protected void updateTrades() {
        VillagerTrades.IMerchantRecipeOption[] avillagertrades_imerchantrecipeoption = (VillagerTrades.IMerchantRecipeOption[]) VillagerTrades.WANDERING_TRADER_TRADES.get(1);
        VillagerTrades.IMerchantRecipeOption[] avillagertrades_imerchantrecipeoption1 = (VillagerTrades.IMerchantRecipeOption[]) VillagerTrades.WANDERING_TRADER_TRADES.get(2);

        if (avillagertrades_imerchantrecipeoption != null && avillagertrades_imerchantrecipeoption1 != null) {
            MerchantRecipeList merchantrecipelist = this.getOffers();

            this.addOffersFromItemListings(merchantrecipelist, avillagertrades_imerchantrecipeoption, 5);
            int i = this.random.nextInt(avillagertrades_imerchantrecipeoption1.length);
            VillagerTrades.IMerchantRecipeOption villagertrades_imerchantrecipeoption = avillagertrades_imerchantrecipeoption1[i];
            MerchantRecipe merchantrecipe = villagertrades_imerchantrecipeoption.getOffer(this, this.random);

            if (merchantrecipe != null) {
                merchantrecipelist.add(merchantrecipe);
            }

        }
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putInt("DespawnDelay", this.despawnDelay);
        if (this.wanderTarget != null) {
            nbttagcompound.put("WanderTarget", GameProfileSerializer.writeBlockPos(this.wanderTarget));
        }

    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        if (nbttagcompound.contains("DespawnDelay", 99)) {
            this.despawnDelay = nbttagcompound.getInt("DespawnDelay");
        }

        if (nbttagcompound.contains("WanderTarget")) {
            this.wanderTarget = GameProfileSerializer.readBlockPos(nbttagcompound.getCompound("WanderTarget"));
        }

        this.setAge(Math.max(0, this.getAge()));
    }

    @Override
    public boolean removeWhenFarAway(double d0) {
        return false;
    }

    @Override
    protected void rewardTradeXp(MerchantRecipe merchantrecipe) {
        if (merchantrecipe.shouldRewardExp()) {
            int i = 3 + this.random.nextInt(4);

            this.level.addFreshEntity(new EntityExperienceOrb(this.level, this.getX(), this.getY() + 0.5D, this.getZ(), i));
        }

    }

    @Override
    protected SoundEffect getAmbientSound() {
        return this.isTrading() ? SoundEffects.WANDERING_TRADER_TRADE : SoundEffects.WANDERING_TRADER_AMBIENT;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.WANDERING_TRADER_HURT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.WANDERING_TRADER_DEATH;
    }

    @Override
    protected SoundEffect getDrinkingSound(ItemStack itemstack) {
        return itemstack.is(Items.MILK_BUCKET) ? SoundEffects.WANDERING_TRADER_DRINK_MILK : SoundEffects.WANDERING_TRADER_DRINK_POTION;
    }

    @Override
    protected SoundEffect getTradeUpdatedSound(boolean flag) {
        return flag ? SoundEffects.WANDERING_TRADER_YES : SoundEffects.WANDERING_TRADER_NO;
    }

    @Override
    public SoundEffect getNotifyTradeSound() {
        return SoundEffects.WANDERING_TRADER_YES;
    }

    public void setDespawnDelay(int i) {
        this.despawnDelay = i;
    }

    public int getDespawnDelay() {
        return this.despawnDelay;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level.isClientSide) {
            this.maybeDespawn();
        }

    }

    private void maybeDespawn() {
        if (this.despawnDelay > 0 && !this.isTrading() && --this.despawnDelay == 0) {
            this.discard();
        }

    }

    public void setWanderTarget(@Nullable BlockPosition blockposition) {
        this.wanderTarget = blockposition;
    }

    @Nullable
    BlockPosition getWanderTarget() {
        return this.wanderTarget;
    }

    private class a extends PathfinderGoal {

        final EntityVillagerTrader trader;
        final double stopDistance;
        final double speedModifier;

        a(EntityVillagerTrader entityvillagertrader, double d0, double d1) {
            this.trader = entityvillagertrader;
            this.stopDistance = d0;
            this.speedModifier = d1;
            this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        @Override
        public void stop() {
            this.trader.setWanderTarget((BlockPosition) null);
            EntityVillagerTrader.this.navigation.stop();
        }

        @Override
        public boolean canUse() {
            BlockPosition blockposition = this.trader.getWanderTarget();

            return blockposition != null && this.isTooFarAway(blockposition, this.stopDistance);
        }

        @Override
        public void tick() {
            BlockPosition blockposition = this.trader.getWanderTarget();

            if (blockposition != null && EntityVillagerTrader.this.navigation.isDone()) {
                if (this.isTooFarAway(blockposition, 10.0D)) {
                    Vec3D vec3d = (new Vec3D((double) blockposition.getX() - this.trader.getX(), (double) blockposition.getY() - this.trader.getY(), (double) blockposition.getZ() - this.trader.getZ())).normalize();
                    Vec3D vec3d1 = vec3d.scale(10.0D).add(this.trader.getX(), this.trader.getY(), this.trader.getZ());

                    EntityVillagerTrader.this.navigation.moveTo(vec3d1.x, vec3d1.y, vec3d1.z, this.speedModifier);
                } else {
                    EntityVillagerTrader.this.navigation.moveTo((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), this.speedModifier);
                }
            }

        }

        private boolean isTooFarAway(BlockPosition blockposition, double d0) {
            return !blockposition.closerThan((IPosition) this.trader.position(), d0);
        }
    }
}
