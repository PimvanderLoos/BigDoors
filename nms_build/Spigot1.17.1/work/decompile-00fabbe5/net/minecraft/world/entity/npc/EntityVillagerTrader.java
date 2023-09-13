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
    protected void initPathfinder() {
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(0, new PathfinderGoalUseItem<>(this, PotionUtil.a(new ItemStack(Items.POTION), Potions.INVISIBILITY), SoundEffects.WANDERING_TRADER_DISAPPEARED, (entityvillagertrader) -> {
            return this.level.isNight() && !entityvillagertrader.isInvisible();
        }));
        this.goalSelector.a(0, new PathfinderGoalUseItem<>(this, new ItemStack(Items.MILK_BUCKET), SoundEffects.WANDERING_TRADER_REAPPEARED, (entityvillagertrader) -> {
            return this.level.isDay() && entityvillagertrader.isInvisible();
        }));
        this.goalSelector.a(1, new PathfinderGoalTradeWithPlayer(this));
        this.goalSelector.a(1, new PathfinderGoalAvoidTarget<>(this, EntityZombie.class, 8.0F, 0.5D, 0.5D));
        this.goalSelector.a(1, new PathfinderGoalAvoidTarget<>(this, EntityEvoker.class, 12.0F, 0.5D, 0.5D));
        this.goalSelector.a(1, new PathfinderGoalAvoidTarget<>(this, EntityVindicator.class, 8.0F, 0.5D, 0.5D));
        this.goalSelector.a(1, new PathfinderGoalAvoidTarget<>(this, EntityVex.class, 8.0F, 0.5D, 0.5D));
        this.goalSelector.a(1, new PathfinderGoalAvoidTarget<>(this, EntityPillager.class, 15.0F, 0.5D, 0.5D));
        this.goalSelector.a(1, new PathfinderGoalAvoidTarget<>(this, EntityIllagerIllusioner.class, 12.0F, 0.5D, 0.5D));
        this.goalSelector.a(1, new PathfinderGoalAvoidTarget<>(this, EntityZoglin.class, 10.0F, 0.5D, 0.5D));
        this.goalSelector.a(1, new PathfinderGoalPanic(this, 0.5D));
        this.goalSelector.a(1, new PathfinderGoalLookAtTradingPlayer(this));
        this.goalSelector.a(2, new EntityVillagerTrader.a(this, 2.0D, 0.35D));
        this.goalSelector.a(4, new PathfinderGoalMoveTowardsRestriction(this, 0.35D));
        this.goalSelector.a(8, new PathfinderGoalRandomStrollLand(this, 0.35D));
        this.goalSelector.a(9, new PathfinderGoalInteract(this, EntityHuman.class, 3.0F, 1.0F));
        this.goalSelector.a(10, new PathfinderGoalLookAtPlayer(this, EntityInsentient.class, 8.0F));
    }

    @Nullable
    @Override
    public EntityAgeable createChild(WorldServer worldserver, EntityAgeable entityageable) {
        return null;
    }

    @Override
    public boolean isRegularVillager() {
        return false;
    }

    @Override
    public EnumInteractionResult b(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (!itemstack.a(Items.VILLAGER_SPAWN_EGG) && this.isAlive() && !this.fx() && !this.isBaby()) {
            if (enumhand == EnumHand.MAIN_HAND) {
                entityhuman.a(StatisticList.TALKED_TO_VILLAGER);
            }

            if (this.getOffers().isEmpty()) {
                return EnumInteractionResult.a(this.level.isClientSide);
            } else {
                if (!this.level.isClientSide) {
                    this.setTradingPlayer(entityhuman);
                    this.openTrade(entityhuman, this.getScoreboardDisplayName(), 1);
                }

                return EnumInteractionResult.a(this.level.isClientSide);
            }
        } else {
            return super.b(entityhuman, enumhand);
        }
    }

    @Override
    protected void fF() {
        VillagerTrades.IMerchantRecipeOption[] avillagertrades_imerchantrecipeoption = (VillagerTrades.IMerchantRecipeOption[]) VillagerTrades.WANDERING_TRADER_TRADES.get(1);
        VillagerTrades.IMerchantRecipeOption[] avillagertrades_imerchantrecipeoption1 = (VillagerTrades.IMerchantRecipeOption[]) VillagerTrades.WANDERING_TRADER_TRADES.get(2);

        if (avillagertrades_imerchantrecipeoption != null && avillagertrades_imerchantrecipeoption1 != null) {
            MerchantRecipeList merchantrecipelist = this.getOffers();

            this.a(merchantrecipelist, avillagertrades_imerchantrecipeoption, 5);
            int i = this.random.nextInt(avillagertrades_imerchantrecipeoption1.length);
            VillagerTrades.IMerchantRecipeOption villagertrades_imerchantrecipeoption = avillagertrades_imerchantrecipeoption1[i];
            MerchantRecipe merchantrecipe = villagertrades_imerchantrecipeoption.a(this, this.random);

            if (merchantrecipe != null) {
                merchantrecipelist.add(merchantrecipe);
            }

        }
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setInt("DespawnDelay", this.despawnDelay);
        if (this.wanderTarget != null) {
            nbttagcompound.set("WanderTarget", GameProfileSerializer.a(this.wanderTarget));
        }

    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("DespawnDelay", 99)) {
            this.despawnDelay = nbttagcompound.getInt("DespawnDelay");
        }

        if (nbttagcompound.hasKey("WanderTarget")) {
            this.wanderTarget = GameProfileSerializer.b(nbttagcompound.getCompound("WanderTarget"));
        }

        this.setAgeRaw(Math.max(0, this.getAge()));
    }

    @Override
    public boolean isTypeNotPersistent(double d0) {
        return false;
    }

    @Override
    protected void b(MerchantRecipe merchantrecipe) {
        if (merchantrecipe.isRewardExp()) {
            int i = 3 + this.random.nextInt(4);

            this.level.addEntity(new EntityExperienceOrb(this.level, this.locX(), this.locY() + 0.5D, this.locZ(), i));
        }

    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return this.fx() ? SoundEffects.WANDERING_TRADER_TRADE : SoundEffects.WANDERING_TRADER_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.WANDERING_TRADER_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.WANDERING_TRADER_DEATH;
    }

    @Override
    protected SoundEffect d(ItemStack itemstack) {
        return itemstack.a(Items.MILK_BUCKET) ? SoundEffects.WANDERING_TRADER_DRINK_MILK : SoundEffects.WANDERING_TRADER_DRINK_POTION;
    }

    @Override
    protected SoundEffect v(boolean flag) {
        return flag ? SoundEffects.WANDERING_TRADER_YES : SoundEffects.WANDERING_TRADER_NO;
    }

    @Override
    public SoundEffect getTradeSound() {
        return SoundEffects.WANDERING_TRADER_YES;
    }

    public void setDespawnDelay(int i) {
        this.despawnDelay = i;
    }

    public int getDespawnDelay() {
        return this.despawnDelay;
    }

    @Override
    public void movementTick() {
        super.movementTick();
        if (!this.level.isClientSide) {
            this.fI();
        }

    }

    private void fI() {
        if (this.despawnDelay > 0 && !this.fx() && --this.despawnDelay == 0) {
            this.die();
        }

    }

    public void g(@Nullable BlockPosition blockposition) {
        this.wanderTarget = blockposition;
    }

    @Nullable
    BlockPosition fJ() {
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
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        @Override
        public void d() {
            this.trader.g((BlockPosition) null);
            EntityVillagerTrader.this.navigation.o();
        }

        @Override
        public boolean a() {
            BlockPosition blockposition = this.trader.fJ();

            return blockposition != null && this.a(blockposition, this.stopDistance);
        }

        @Override
        public void e() {
            BlockPosition blockposition = this.trader.fJ();

            if (blockposition != null && EntityVillagerTrader.this.navigation.m()) {
                if (this.a(blockposition, 10.0D)) {
                    Vec3D vec3d = (new Vec3D((double) blockposition.getX() - this.trader.locX(), (double) blockposition.getY() - this.trader.locY(), (double) blockposition.getZ() - this.trader.locZ())).d();
                    Vec3D vec3d1 = vec3d.a(10.0D).add(this.trader.locX(), this.trader.locY(), this.trader.locZ());

                    EntityVillagerTrader.this.navigation.a(vec3d1.x, vec3d1.y, vec3d1.z, this.speedModifier);
                } else {
                    EntityVillagerTrader.this.navigation.a((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), this.speedModifier);
                }
            }

        }

        private boolean a(BlockPosition blockposition, double d0) {
            return !blockposition.a((IPosition) this.trader.getPositionVector(), d0);
        }
    }
}
