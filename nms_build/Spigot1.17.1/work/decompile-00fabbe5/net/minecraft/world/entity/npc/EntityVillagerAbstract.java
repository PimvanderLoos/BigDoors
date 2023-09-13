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

    private static final DataWatcherObject<Integer> DATA_UNHAPPY_COUNTER = DataWatcher.a(EntityVillagerAbstract.class, DataWatcherRegistry.INT);
    public static final int VILLAGER_SLOT_OFFSET = 300;
    private static final int VILLAGER_INVENTORY_SIZE = 8;
    @Nullable
    private EntityHuman tradingPlayer;
    @Nullable
    protected MerchantRecipeList offers;
    private final InventorySubcontainer inventory = new InventorySubcontainer(8);

    public EntityVillagerAbstract(EntityTypes<? extends EntityVillagerAbstract> entitytypes, World world) {
        super(entitytypes, world);
        this.a(PathType.DANGER_FIRE, 16.0F);
        this.a(PathType.DAMAGE_FIRE, -1.0F);
    }

    @Override
    public GroupDataEntity prepare(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        if (groupdataentity == null) {
            groupdataentity = new EntityAgeable.a(false);
        }

        return super.prepare(worldaccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) groupdataentity, nbttagcompound);
    }

    public int p() {
        return (Integer) this.entityData.get(EntityVillagerAbstract.DATA_UNHAPPY_COUNTER);
    }

    public void t(int i) {
        this.entityData.set(EntityVillagerAbstract.DATA_UNHAPPY_COUNTER, i);
    }

    @Override
    public int getExperience() {
        return 0;
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return this.isBaby() ? 0.81F : 1.62F;
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityVillagerAbstract.DATA_UNHAPPY_COUNTER, 0);
    }

    @Override
    public void setTradingPlayer(@Nullable EntityHuman entityhuman) {
        this.tradingPlayer = entityhuman;
    }

    @Nullable
    @Override
    public EntityHuman getTrader() {
        return this.tradingPlayer;
    }

    public boolean fx() {
        return this.tradingPlayer != null;
    }

    @Override
    public MerchantRecipeList getOffers() {
        if (this.offers == null) {
            this.offers = new MerchantRecipeList();
            this.fF();
        }

        return this.offers;
    }

    @Override
    public void a(@Nullable MerchantRecipeList merchantrecipelist) {}

    @Override
    public void setForcedExperience(int i) {}

    @Override
    public void a(MerchantRecipe merchantrecipe) {
        merchantrecipe.increaseUses();
        this.ambientSoundTime = -this.J();
        this.b(merchantrecipe);
        if (this.tradingPlayer instanceof EntityPlayer) {
            CriterionTriggers.TRADE.a((EntityPlayer) this.tradingPlayer, this, merchantrecipe.getSellingItem());
        }

    }

    protected abstract void b(MerchantRecipe merchantrecipe);

    @Override
    public boolean isRegularVillager() {
        return true;
    }

    @Override
    public void m(ItemStack itemstack) {
        if (!this.level.isClientSide && this.ambientSoundTime > -this.J() + 20) {
            this.ambientSoundTime = -this.J();
            this.playSound(this.v(!itemstack.isEmpty()), this.getSoundVolume(), this.ep());
        }

    }

    @Override
    public SoundEffect getTradeSound() {
        return SoundEffects.VILLAGER_YES;
    }

    protected SoundEffect v(boolean flag) {
        return flag ? SoundEffects.VILLAGER_YES : SoundEffects.VILLAGER_NO;
    }

    public void fB() {
        this.playSound(SoundEffects.VILLAGER_CELEBRATE, this.getSoundVolume(), this.ep());
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        MerchantRecipeList merchantrecipelist = this.getOffers();

        if (!merchantrecipelist.isEmpty()) {
            nbttagcompound.set("Offers", merchantrecipelist.a());
        }

        nbttagcompound.set("Inventory", this.inventory.g());
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("Offers", 10)) {
            this.offers = new MerchantRecipeList(nbttagcompound.getCompound("Offers"));
        }

        this.inventory.a(nbttagcompound.getList("Inventory", 10));
    }

    @Nullable
    @Override
    public Entity b(WorldServer worldserver) {
        this.fC();
        return super.b(worldserver);
    }

    protected void fC() {
        this.setTradingPlayer((EntityHuman) null);
    }

    @Override
    public void die(DamageSource damagesource) {
        super.die(damagesource);
        this.fC();
    }

    protected void a(ParticleParam particleparam) {
        for (int i = 0; i < 5; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;

            this.level.addParticle(particleparam, this.d(1.0D), this.da() + 1.0D, this.g(1.0D), d0, d1, d2);
        }

    }

    @Override
    public boolean a(EntityHuman entityhuman) {
        return false;
    }

    @Override
    public InventorySubcontainer getInventory() {
        return this.inventory;
    }

    @Override
    public SlotAccess k(int i) {
        int j = i - 300;

        return j >= 0 && j < this.inventory.getSize() ? SlotAccess.a(this.inventory, j) : super.k(i);
    }

    @Override
    public World getWorld() {
        return this.level;
    }

    protected abstract void fF();

    protected void a(MerchantRecipeList merchantrecipelist, VillagerTrades.IMerchantRecipeOption[] avillagertrades_imerchantrecipeoption, int i) {
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
            MerchantRecipe merchantrecipe = villagertrades_imerchantrecipeoption.a(this, this.random);

            if (merchantrecipe != null) {
                merchantrecipelist.add(merchantrecipe);
            }
        }

    }

    @Override
    public Vec3D n(float f) {
        float f1 = MathHelper.h(f, this.yBodyRotO, this.yBodyRot) * 0.017453292F;
        Vec3D vec3d = new Vec3D(0.0D, this.getBoundingBox().c() - 1.0D, 0.2D);

        return this.k(f).e(vec3d.b(-f1));
    }
}
