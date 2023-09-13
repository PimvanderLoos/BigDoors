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
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
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

    public static final DataWatcherObject<Boolean> DATA_CONVERTING_ID = DataWatcher.defineId(EntityZombieVillager.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<VillagerData> DATA_VILLAGER_DATA = DataWatcher.defineId(EntityZombieVillager.class, DataWatcherRegistry.VILLAGER_DATA);
    private static final int VILLAGER_CONVERSION_WAIT_MIN = 3600;
    private static final int VILLAGER_CONVERSION_WAIT_MAX = 6000;
    private static final int MAX_SPECIAL_BLOCKS_COUNT = 14;
    private static final int SPECIAL_BLOCK_RADIUS = 4;
    public int villagerConversionTime;
    @Nullable
    public UUID conversionStarter;
    @Nullable
    private NBTBase gossips;
    @Nullable
    private NBTTagCompound tradeOffers;
    private int villagerXp;

    public EntityZombieVillager(EntityTypes<? extends EntityZombieVillager> entitytypes, World world) {
        super(entitytypes, world);
        this.setVillagerData(this.getVillagerData().setProfession((VillagerProfession) IRegistry.VILLAGER_PROFESSION.getRandom(this.random)));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityZombieVillager.DATA_CONVERTING_ID, false);
        this.entityData.define(EntityZombieVillager.DATA_VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1));
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        DataResult dataresult = VillagerData.CODEC.encodeStart(DynamicOpsNBT.INSTANCE, this.getVillagerData());
        Logger logger = EntityZombieVillager.LOGGER;

        Objects.requireNonNull(logger);
        dataresult.resultOrPartial(logger::error).ifPresent((nbtbase) -> {
            nbttagcompound.put("VillagerData", nbtbase);
        });
        if (this.tradeOffers != null) {
            nbttagcompound.put("Offers", this.tradeOffers);
        }

        if (this.gossips != null) {
            nbttagcompound.put("Gossips", this.gossips);
        }

        nbttagcompound.putInt("ConversionTime", this.isConverting() ? this.villagerConversionTime : -1);
        if (this.conversionStarter != null) {
            nbttagcompound.putUUID("ConversionPlayer", this.conversionStarter);
        }

        nbttagcompound.putInt("Xp", this.villagerXp);
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        if (nbttagcompound.contains("VillagerData", 10)) {
            DataResult<VillagerData> dataresult = VillagerData.CODEC.parse(new Dynamic(DynamicOpsNBT.INSTANCE, nbttagcompound.get("VillagerData")));
            Logger logger = EntityZombieVillager.LOGGER;

            Objects.requireNonNull(logger);
            dataresult.resultOrPartial(logger::error).ifPresent(this::setVillagerData);
        }

        if (nbttagcompound.contains("Offers", 10)) {
            this.tradeOffers = nbttagcompound.getCompound("Offers");
        }

        if (nbttagcompound.contains("Gossips", 10)) {
            this.gossips = nbttagcompound.getList("Gossips", 10);
        }

        if (nbttagcompound.contains("ConversionTime", 99) && nbttagcompound.getInt("ConversionTime") > -1) {
            this.startConverting(nbttagcompound.hasUUID("ConversionPlayer") ? nbttagcompound.getUUID("ConversionPlayer") : null, nbttagcompound.getInt("ConversionTime"));
        }

        if (nbttagcompound.contains("Xp", 3)) {
            this.villagerXp = nbttagcompound.getInt("Xp");
        }

    }

    @Override
    public void tick() {
        if (!this.level.isClientSide && this.isAlive() && this.isConverting()) {
            int i = this.getConversionProgress();

            this.villagerConversionTime -= i;
            if (this.villagerConversionTime <= 0) {
                this.finishConversion((WorldServer) this.level);
            }
        }

        super.tick();
    }

    @Override
    public EnumInteractionResult mobInteract(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);

        if (itemstack.is(Items.GOLDEN_APPLE)) {
            if (this.hasEffect(MobEffects.WEAKNESS)) {
                if (!entityhuman.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

                if (!this.level.isClientSide) {
                    this.startConverting(entityhuman.getUUID(), this.random.nextInt(2401) + 3600);
                }

                this.gameEvent(GameEvent.MOB_INTERACT, this.eyeBlockPosition());
                return EnumInteractionResult.SUCCESS;
            } else {
                return EnumInteractionResult.CONSUME;
            }
        } else {
            return super.mobInteract(entityhuman, enumhand);
        }
    }

    @Override
    protected boolean convertsInWater() {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double d0) {
        return !this.isConverting() && this.villagerXp == 0;
    }

    public boolean isConverting() {
        return (Boolean) this.getEntityData().get(EntityZombieVillager.DATA_CONVERTING_ID);
    }

    public void startConverting(@Nullable UUID uuid, int i) {
        this.conversionStarter = uuid;
        this.villagerConversionTime = i;
        this.getEntityData().set(EntityZombieVillager.DATA_CONVERTING_ID, true);
        this.removeEffect(MobEffects.WEAKNESS);
        this.addEffect(new MobEffect(MobEffects.DAMAGE_BOOST, i, Math.min(this.level.getDifficulty().getId() - 1, 0)));
        this.level.broadcastEntityEvent(this, (byte) 16);
    }

    @Override
    public void handleEntityEvent(byte b0) {
        if (b0 == 16) {
            if (!this.isSilent()) {
                this.level.playLocalSound(this.getX(), this.getEyeY(), this.getZ(), SoundEffects.ZOMBIE_VILLAGER_CURE, this.getSoundSource(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
            }

        } else {
            super.handleEntityEvent(b0);
        }
    }

    private void finishConversion(WorldServer worldserver) {
        EntityVillager entityvillager = (EntityVillager) this.convertTo(EntityTypes.VILLAGER, false);
        EnumItemSlot[] aenumitemslot = EnumItemSlot.values();
        int i = aenumitemslot.length;

        for (int j = 0; j < i; ++j) {
            EnumItemSlot enumitemslot = aenumitemslot[j];
            ItemStack itemstack = this.getItemBySlot(enumitemslot);

            if (!itemstack.isEmpty()) {
                if (EnchantmentManager.hasBindingCurse(itemstack)) {
                    entityvillager.getSlot(enumitemslot.getIndex() + 300).set(itemstack);
                } else {
                    double d0 = (double) this.getEquipmentDropChance(enumitemslot);

                    if (d0 > 1.0D) {
                        this.spawnAtLocation(itemstack);
                    }
                }
            }
        }

        entityvillager.setVillagerData(this.getVillagerData());
        if (this.gossips != null) {
            entityvillager.setGossips(this.gossips);
        }

        if (this.tradeOffers != null) {
            entityvillager.setOffers(new MerchantRecipeList(this.tradeOffers));
        }

        entityvillager.setVillagerXp(this.villagerXp);
        entityvillager.finalizeSpawn(worldserver, worldserver.getCurrentDifficultyAt(entityvillager.blockPosition()), EnumMobSpawn.CONVERSION, (GroupDataEntity) null, (NBTTagCompound) null);
        if (this.conversionStarter != null) {
            EntityHuman entityhuman = worldserver.getPlayerByUUID(this.conversionStarter);

            if (entityhuman instanceof EntityPlayer) {
                CriterionTriggers.CURED_ZOMBIE_VILLAGER.trigger((EntityPlayer) entityhuman, this, entityvillager);
                worldserver.onReputationEvent(ReputationEvent.ZOMBIE_VILLAGER_CURED, entityhuman, entityvillager);
            }
        }

        entityvillager.addEffect(new MobEffect(MobEffects.CONFUSION, 200, 0));
        if (!this.isSilent()) {
            worldserver.levelEvent((EntityHuman) null, 1027, this.blockPosition(), 0);
        }

    }

    private int getConversionProgress() {
        int i = 1;

        if (this.random.nextFloat() < 0.01F) {
            int j = 0;
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

            for (int k = (int) this.getX() - 4; k < (int) this.getX() + 4 && j < 14; ++k) {
                for (int l = (int) this.getY() - 4; l < (int) this.getY() + 4 && j < 14; ++l) {
                    for (int i1 = (int) this.getZ() - 4; i1 < (int) this.getZ() + 4 && j < 14; ++i1) {
                        IBlockData iblockdata = this.level.getBlockState(blockposition_mutableblockposition.set(k, l, i1));

                        if (iblockdata.is(Blocks.IRON_BARS) || iblockdata.getBlock() instanceof BlockBed) {
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
    public float getVoicePitch() {
        return this.isBaby() ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 2.0F : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
    }

    @Override
    public SoundEffect getAmbientSound() {
        return SoundEffects.ZOMBIE_VILLAGER_AMBIENT;
    }

    @Override
    public SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.ZOMBIE_VILLAGER_HURT;
    }

    @Override
    public SoundEffect getDeathSound() {
        return SoundEffects.ZOMBIE_VILLAGER_DEATH;
    }

    @Override
    public SoundEffect getStepSound() {
        return SoundEffects.ZOMBIE_VILLAGER_STEP;
    }

    @Override
    protected ItemStack getSkull() {
        return ItemStack.EMPTY;
    }

    public void setTradeOffers(NBTTagCompound nbttagcompound) {
        this.tradeOffers = nbttagcompound;
    }

    public void setGossips(NBTBase nbtbase) {
        this.gossips = nbtbase;
    }

    @Nullable
    @Override
    public GroupDataEntity finalizeSpawn(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        this.setVillagerData(this.getVillagerData().setType(VillagerType.byBiome(worldaccess.getBiomeName(this.blockPosition()))));
        return super.finalizeSpawn(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
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

    public int getVillagerXp() {
        return this.villagerXp;
    }

    public void setVillagerXp(int i) {
        this.villagerXp = i;
    }
}
