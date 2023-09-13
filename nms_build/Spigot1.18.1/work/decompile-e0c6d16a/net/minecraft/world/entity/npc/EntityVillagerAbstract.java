package net.minecraft.world.entity.npc;

import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.InventorySubcontainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.IMerchant;
import net.minecraft.world.item.trading.MerchantRecipe;
import net.minecraft.world.item.trading.MerchantRecipeList;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3D;

public abstract class EntityVillagerAbstract extends EntityAgeable implements InventoryCarrier, NPC, IMerchant {

    private static final DataWatcherObject<Integer> DATA_UNHAPPY_COUNTER = DataWatcher.defineId(EntityVillagerAbstract.class, DataWatcherRegistry.INT);
    public static final int VILLAGER_SLOT_OFFSET = 300;
    private static final int VILLAGER_INVENTORY_SIZE = 8;
    @Nullable
    private EntityHuman tradingPlayer;
    @Nullable
    protected MerchantRecipeList offers;
    private final InventorySubcontainer inventory = new InventorySubcontainer(8);

    public EntityVillagerAbstract(EntityTypes<? extends EntityVillagerAbstract> entitytypes, World world) {
        super(entitytypes, world);
        this.setPathfindingMalus(PathType.DANGER_FIRE, 16.0F);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0F);
    }

    @Override
    public GroupDataEntity finalizeSpawn(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        if (groupdataentity == null) {
            groupdataentity = new EntityAgeable.a(false);
        }

        return super.finalizeSpawn(worldaccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) groupdataentity, nbttagcompound);
    }

    public int getUnhappyCounter() {
        return (Integer) this.entityData.get(EntityVillagerAbstract.DATA_UNHAPPY_COUNTER);
    }

    public void setUnhappyCounter(int i) {
        this.entityData.set(EntityVillagerAbstract.DATA_UNHAPPY_COUNTER, i);
    }

    @Override
    public int getVillagerXp() {
        return 0;
    }

    @Override
    protected float getStandingEyeHeight(EntityPose entitypose, EntitySize entitysize) {
        return this.isBaby() ? 0.81F : 1.62F;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityVillagerAbstract.DATA_UNHAPPY_COUNTER, 0);
    }

    @Override
    public void setTradingPlayer(@Nullable EntityHuman entityhuman) {
        this.tradingPlayer = entityhuman;
    }

    @Nullable
    @Override
    public EntityHuman getTradingPlayer() {
        return this.tradingPlayer;
    }

    public boolean isTrading() {
        return this.tradingPlayer != null;
    }

    @Override
    public MerchantRecipeList getOffers() {
        if (this.offers == null) {
            this.offers = new MerchantRecipeList();
            this.updateTrades();
        }

        return this.offers;
    }

    @Override
    public void overrideOffers(@Nullable MerchantRecipeList merchantrecipelist) {}

    @Override
    public void overrideXp(int i) {}

    @Override
    public void notifyTrade(MerchantRecipe merchantrecipe) {
        merchantrecipe.increaseUses();
        this.ambientSoundTime = -this.getAmbientSoundInterval();
        this.rewardTradeXp(merchantrecipe);
        if (this.tradingPlayer instanceof EntityPlayer) {
            CriterionTriggers.TRADE.trigger((EntityPlayer) this.tradingPlayer, this, merchantrecipe.getResult());
        }

    }

    protected abstract void rewardTradeXp(MerchantRecipe merchantrecipe);

    @Override
    public boolean showProgressBar() {
        return true;
    }

    @Override
    public void notifyTradeUpdated(ItemStack itemstack) {
        if (!this.level.isClientSide && this.ambientSoundTime > -this.getAmbientSoundInterval() + 20) {
            this.ambientSoundTime = -this.getAmbientSoundInterval();
            this.playSound(this.getTradeUpdatedSound(!itemstack.isEmpty()), this.getSoundVolume(), this.getVoicePitch());
        }

    }

    @Override
    public SoundEffect getNotifyTradeSound() {
        return SoundEffects.VILLAGER_YES;
    }

    protected SoundEffect getTradeUpdatedSound(boolean flag) {
        return flag ? SoundEffects.VILLAGER_YES : SoundEffects.VILLAGER_NO;
    }

    public void playCelebrateSound() {
        this.playSound(SoundEffects.VILLAGER_CELEBRATE, this.getSoundVolume(), this.getVoicePitch());
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        MerchantRecipeList merchantrecipelist = this.getOffers();

        if (!merchantrecipelist.isEmpty()) {
            nbttagcompound.put("Offers", merchantrecipelist.createTag());
        }

        nbttagcompound.put("Inventory", this.inventory.createTag());
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        if (nbttagcompound.contains("Offers", 10)) {
            this.offers = new MerchantRecipeList(nbttagcompound.getCompound("Offers"));
        }

        this.inventory.fromTag(nbttagcompound.getList("Inventory", 10));
    }

    @Nullable
    @Override
    public Entity changeDimension(WorldServer worldserver) {
        this.stopTrading();
        return super.changeDimension(worldserver);
    }

    protected void stopTrading() {
        this.setTradingPlayer((EntityHuman) null);
    }

    @Override
    public void die(DamageSource damagesource) {
        super.die(damagesource);
        this.stopTrading();
    }

    protected void addParticlesAroundSelf(ParticleParam particleparam) {
        for (int i = 0; i < 5; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;

            this.level.addParticle(particleparam, this.getRandomX(1.0D), this.getRandomY() + 1.0D, this.getRandomZ(1.0D), d0, d1, d2);
        }

    }

    @Override
    public boolean canBeLeashed(EntityHuman entityhuman) {
        return false;
    }

    @Override
    public InventorySubcontainer getInventory() {
        return this.inventory;
    }

    @Override
    public SlotAccess getSlot(int i) {
        int j = i - 300;

        return j >= 0 && j < this.inventory.getContainerSize() ? SlotAccess.forContainer(this.inventory, j) : super.getSlot(i);
    }

    protected abstract void updateTrades();

    protected void addOffersFromItemListings(MerchantRecipeList merchantrecipelist, VillagerTrades.IMerchantRecipeOption[] avillagertrades_imerchantrecipeoption, int i) {
        Set<Integer> set = Sets.newHashSet();

        if (avillagertrades_imerchantrecipeoption.length > i) {
            while (set.size() < i) {
                set.add(this.random.nextInt(avillagertrades_imerchantrecipeoption.length));
            }
        } else {
            for (int j = 0; j < avillagertrades_imerchantrecipeoption.length; ++j) {
                set.add(j);
            }
        }

        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            Integer integer = (Integer) iterator.next();
            VillagerTrades.IMerchantRecipeOption villagertrades_imerchantrecipeoption = avillagertrades_imerchantrecipeoption[integer];
            MerchantRecipe merchantrecipe = villagertrades_imerchantrecipeoption.getOffer(this, this.random);

            if (merchantrecipe != null) {
                merchantrecipelist.add(merchantrecipe);
            }
        }

    }

    @Override
    public Vec3D getRopeHoldPosition(float f) {
        float f1 = MathHelper.lerp(f, this.yBodyRotO, this.yBodyRot) * 0.017453292F;
        Vec3D vec3d = new Vec3D(0.0D, this.getBoundingBox().getYsize() - 1.0D, 0.2D);

        return this.getPosition(f).add(vec3d.yRot(-f1));
    }

    @Override
    public boolean isClientSide() {
        return this.level.isClientSide;
    }
}
