package net.minecraft.world.entity.monster;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.ReputationHandler;
import net.minecraft.world.entity.ai.village.ReputationEvent;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.item.trading.MerchantRecipeList;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.BlockBed;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;
import org.apache.logging.log4j.Logger;

public class EntityZombieVillager extends EntityZombie implements VillagerDataHolder {

    public static final DataWatcherObject<Boolean> DATA_CONVERTING_ID = DataWatcher.a(EntityZombieVillager.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<VillagerData> DATA_VILLAGER_DATA = DataWatcher.a(EntityZombieVillager.class, DataWatcherRegistry.VILLAGER_DATA);
    private static final int VILLAGER_CONVERSION_WAIT_MIN = 3600;
    private static final int VILLAGER_CONVERSION_WAIT_MAX = 6000;
    private static final int MAX_SPECIAL_BLOCKS_COUNT = 14;
    private static final int SPECIAL_BLOCK_RADIUS = 4;
    public int villagerConversionTime;
    public UUID conversionStarter;
    private NBTBase gossips;
    private NBTTagCompound tradeOffers;
    private int villagerXp;

    public EntityZombieVillager(EntityTypes<? extends EntityZombieVillager> entitytypes, World world) {
        super(entitytypes, world);
        this.setVillagerData(this.getVillagerData().withProfession((VillagerProfession) IRegistry.VILLAGER_PROFESSION.a(this.random)));
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityZombieVillager.DATA_CONVERTING_ID, false);
        this.entityData.register(EntityZombieVillager.DATA_VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1));
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        DataResult dataresult = VillagerData.CODEC.encodeStart(DynamicOpsNBT.INSTANCE, this.getVillagerData());
        Logger logger = EntityZombieVillager.LOGGER;

        Objects.requireNonNull(logger);
        dataresult.resultOrPartial(logger::error).ifPresent((nbtbase) -> {
            nbttagcompound.set("VillagerData", nbtbase);
        });
        if (this.tradeOffers != null) {
            nbttagcompound.set("Offers", this.tradeOffers);
        }

        if (this.gossips != null) {
            nbttagcompound.set("Gossips", this.gossips);
        }

        nbttagcompound.setInt("ConversionTime", this.isConverting() ? this.villagerConversionTime : -1);
        if (this.conversionStarter != null) {
            nbttagcompound.a("ConversionPlayer", this.conversionStarter);
        }

        nbttagcompound.setInt("Xp", this.villagerXp);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("VillagerData", 10)) {
            DataResult<VillagerData> dataresult = VillagerData.CODEC.parse(new Dynamic(DynamicOpsNBT.INSTANCE, nbttagcompound.get("VillagerData")));
            Logger logger = EntityZombieVillager.LOGGER;

            Objects.requireNonNull(logger);
            dataresult.resultOrPartial(logger::error).ifPresent(this::setVillagerData);
        }

        if (nbttagcompound.hasKeyOfType("Offers", 10)) {
            this.tradeOffers = nbttagcompound.getCompound("Offers");
        }

        if (nbttagcompound.hasKeyOfType("Gossips", 10)) {
            this.gossips = nbttagcompound.getList("Gossips", 10);
        }

        if (nbttagcompound.hasKeyOfType("ConversionTime", 99) && nbttagcompound.getInt("ConversionTime") > -1) {
            this.startConversion(nbttagcompound.b("ConversionPlayer") ? nbttagcompound.a("ConversionPlayer") : null, nbttagcompound.getInt("ConversionTime"));
        }

        if (nbttagcompound.hasKeyOfType("Xp", 3)) {
            this.villagerXp = nbttagcompound.getInt("Xp");
        }

    }

    @Override
    public void tick() {
        if (!this.level.isClientSide && this.isAlive() && this.isConverting()) {
            int i = this.getConversionProgress();

            this.villagerConversionTime -= i;
            if (this.villagerConversionTime <= 0) {
                this.c((WorldServer) this.level);
            }
        }

        super.tick();
    }

    @Override
    public EnumInteractionResult b(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (itemstack.a(Items.GOLDEN_APPLE)) {
            if (this.hasEffect(MobEffects.WEAKNESS)) {
                if (!entityhuman.getAbilities().instabuild) {
                    itemstack.subtract(1);
                }

                if (!this.level.isClientSide) {
                    this.startConversion(entityhuman.getUniqueID(), this.random.nextInt(2401) + 3600);
                }

                this.a(GameEvent.MOB_INTERACT, this.cT());
                return EnumInteractionResult.SUCCESS;
            } else {
                return EnumInteractionResult.CONSUME;
            }
        } else {
            return super.b(entityhuman, enumhand);
        }
    }

    @Override
    protected boolean fx() {
        return false;
    }

    @Override
    public boolean isTypeNotPersistent(double d0) {
        return !this.isConverting() && this.villagerXp == 0;
    }

    public boolean isConverting() {
        return (Boolean) this.getDataWatcher().get(EntityZombieVillager.DATA_CONVERTING_ID);
    }

    public void startConversion(@Nullable UUID uuid, int i) {
        this.conversionStarter = uuid;
        this.villagerConversionTime = i;
        this.getDataWatcher().set(EntityZombieVillager.DATA_CONVERTING_ID, true);
        this.removeEffect(MobEffects.WEAKNESS);
        this.addEffect(new MobEffect(MobEffects.DAMAGE_BOOST, i, Math.min(this.level.getDifficulty().a() - 1, 0)));
        this.level.broadcastEntityEffect(this, (byte) 16);
    }

    @Override
    public void a(byte b0) {
        if (b0 == 16) {
            if (!this.isSilent()) {
                this.level.a(this.locX(), this.getHeadY(), this.locZ(), SoundEffects.ZOMBIE_VILLAGER_CURE, this.getSoundCategory(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
            }

        } else {
            super.a(b0);
        }
    }

    private void c(WorldServer worldserver) {
        EntityVillager entityvillager = (EntityVillager) this.a(EntityTypes.VILLAGER, false);
        EnumItemSlot[] aenumitemslot = EnumItemSlot.values();
        int i = aenumitemslot.length;

        for (int j = 0; j < i; ++j) {
            EnumItemSlot enumitemslot = aenumitemslot[j];
            ItemStack itemstack = this.getEquipment(enumitemslot);

            if (!itemstack.isEmpty()) {
                if (EnchantmentManager.d(itemstack)) {
                    entityvillager.k(enumitemslot.b() + 300).a(itemstack);
                } else {
                    double d0 = (double) this.e(enumitemslot);

                    if (d0 > 1.0D) {
                        this.b(itemstack);
                    }
                }
            }
        }

        entityvillager.setVillagerData(this.getVillagerData());
        if (this.gossips != null) {
            entityvillager.a(this.gossips);
        }

        if (this.tradeOffers != null) {
            entityvillager.b(new MerchantRecipeList(this.tradeOffers));
        }

        entityvillager.setExperience(this.villagerXp);
        entityvillager.prepare(worldserver, worldserver.getDamageScaler(entityvillager.getChunkCoordinates()), EnumMobSpawn.CONVERSION, (GroupDataEntity) null, (NBTTagCompound) null);
        if (this.conversionStarter != null) {
            EntityHuman entityhuman = worldserver.b(this.conversionStarter);

            if (entityhuman instanceof EntityPlayer) {
                CriterionTriggers.CURED_ZOMBIE_VILLAGER.a((EntityPlayer) entityhuman, (EntityZombie) this, entityvillager);
                worldserver.a(ReputationEvent.ZOMBIE_VILLAGER_CURED, (Entity) entityhuman, (ReputationHandler) entityvillager);
            }
        }

        entityvillager.addEffect(new MobEffect(MobEffects.CONFUSION, 200, 0));
        if (!this.isSilent()) {
            worldserver.a((EntityHuman) null, 1027, this.getChunkCoordinates(), 0);
        }

    }

    private int getConversionProgress() {
        int i = 1;

        if (this.random.nextFloat() < 0.01F) {
            int j = 0;
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

            for (int k = (int) this.locX() - 4; k < (int) this.locX() + 4 && j < 14; ++k) {
                for (int l = (int) this.locY() - 4; l < (int) this.locY() + 4 && j < 14; ++l) {
                    for (int i1 = (int) this.locZ() - 4; i1 < (int) this.locZ() + 4 && j < 14; ++i1) {
                        IBlockData iblockdata = this.level.getType(blockposition_mutableblockposition.d(k, l, i1));

                        if (iblockdata.a(Blocks.IRON_BARS) || iblockdata.getBlock() instanceof BlockBed) {
                            if (this.random.nextFloat() < 0.3F) {
                                ++i;
                            }

                            ++j;
                        }
                    }
                }
            }
        }

        return i;
    }

    @Override
    public float ep() {
        return this.isBaby() ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 2.0F : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
    }

    @Override
    public SoundEffect getSoundAmbient() {
        return SoundEffects.ZOMBIE_VILLAGER_AMBIENT;
    }

    @Override
    public SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ZOMBIE_VILLAGER_HURT;
    }

    @Override
    public SoundEffect getSoundDeath() {
        return SoundEffects.ZOMBIE_VILLAGER_DEATH;
    }

    @Override
    public SoundEffect getSoundStep() {
        return SoundEffects.ZOMBIE_VILLAGER_STEP;
    }

    @Override
    protected ItemStack fw() {
        return ItemStack.EMPTY;
    }

    public void setOffers(NBTTagCompound nbttagcompound) {
        this.tradeOffers = nbttagcompound;
    }

    public void a(NBTBase nbtbase) {
        this.gossips = nbtbase;
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        this.setVillagerData(this.getVillagerData().withType(VillagerType.a(worldaccess.j(this.getChunkCoordinates()))));
        return super.prepare(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    @Override
    public void setVillagerData(VillagerData villagerdata) {
        VillagerData villagerdata1 = this.getVillagerData();

        if (villagerdata1.getProfession() != villagerdata.getProfession()) {
            this.tradeOffers = null;
        }

        this.entityData.set(EntityZombieVillager.DATA_VILLAGER_DATA, villagerdata);
    }

    @Override
    public VillagerData getVillagerData() {
        return (VillagerData) this.entityData.get(EntityZombieVillager.DATA_VILLAGER_DATA);
    }

    public int fI() {
        return this.villagerXp;
    }

    public void a(int i) {
        this.villagerXp = i;
    }
}
