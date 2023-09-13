package net.minecraft.world.entity.animal;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.tags.TagsItem;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.effect.MobEffectList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityLightning;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.IShearable;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemLiquidUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemSuspiciousStew;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockFlowers;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;
import org.apache.commons.lang3.tuple.Pair;

public class EntityMushroomCow extends EntityCow implements IShearable {

    private static final DataWatcherObject<String> DATA_TYPE = DataWatcher.defineId(EntityMushroomCow.class, DataWatcherRegistry.STRING);
    private static final int MUTATE_CHANCE = 1024;
    @Nullable
    private MobEffectList effect;
    private int effectDuration;
    @Nullable
    private UUID lastLightningBoltUUID;

    public EntityMushroomCow(EntityTypes<? extends EntityMushroomCow> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    public float getWalkTargetValue(BlockPosition blockposition, IWorldReader iworldreader) {
        return iworldreader.getBlockState(blockposition.below()).is(Blocks.MYCELIUM) ? 10.0F : iworldreader.getBrightness(blockposition) - 0.5F;
    }

    public static boolean checkMushroomSpawnRules(EntityTypes<EntityMushroomCow> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return generatoraccess.getBlockState(blockposition.below()).is((Tag) TagsBlock.MOOSHROOMS_SPAWNABLE_ON) && isBrightEnoughToSpawn(generatoraccess, blockposition);
    }

    @Override
    public void thunderHit(WorldServer worldserver, EntityLightning entitylightning) {
        UUID uuid = entitylightning.getUUID();

        if (!uuid.equals(this.lastLightningBoltUUID)) {
            this.setMushroomType(this.getMushroomType() == EntityMushroomCow.Type.RED ? EntityMushroomCow.Type.BROWN : EntityMushroomCow.Type.RED);
            this.lastLightningBoltUUID = uuid;
            this.playSound(SoundEffects.MOOSHROOM_CONVERT, 2.0F, 1.0F);
        }

    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityMushroomCow.DATA_TYPE, EntityMushroomCow.Type.RED.type);
    }

    @Override
    public EnumInteractionResult mobInteract(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);

        if (itemstack.is(Items.BOWL) && !this.isBaby()) {
            boolean flag = false;
            ItemStack itemstack1;

            if (this.effect != null) {
                flag = true;
                itemstack1 = new ItemStack(Items.SUSPICIOUS_STEW);
                ItemSuspiciousStew.saveMobEffect(itemstack1, this.effect, this.effectDuration);
                this.effect = null;
                this.effectDuration = 0;
            } else {
                itemstack1 = new ItemStack(Items.MUSHROOM_STEW);
            }

            ItemStack itemstack2 = ItemLiquidUtil.createFilledResult(itemstack, entityhuman, itemstack1, false);

            entityhuman.setItemInHand(enumhand, itemstack2);
            SoundEffect soundeffect;

            if (flag) {
                soundeffect = SoundEffects.MOOSHROOM_MILK_SUSPICIOUSLY;
            } else {
                soundeffect = SoundEffects.MOOSHROOM_MILK;
            }

            this.playSound(soundeffect, 1.0F, 1.0F);
            return EnumInteractionResult.sidedSuccess(this.level.isClientSide);
        } else if (itemstack.is(Items.SHEARS) && this.readyForShearing()) {
            this.shear(SoundCategory.PLAYERS);
            this.gameEvent(GameEvent.SHEAR, (Entity) entityhuman);
            if (!this.level.isClientSide) {
                itemstack.hurtAndBreak(1, entityhuman, (entityhuman1) -> {
                    entityhuman1.broadcastBreakEvent(enumhand);
                });
            }

            return EnumInteractionResult.sidedSuccess(this.level.isClientSide);
        } else if (this.getMushroomType() == EntityMushroomCow.Type.BROWN && itemstack.is((Tag) TagsItem.SMALL_FLOWERS)) {
            if (this.effect != null) {
                for (int i = 0; i < 2; ++i) {
                    this.level.addParticle(Particles.SMOKE, this.getX() + this.random.nextDouble() / 2.0D, this.getY(0.5D), this.getZ() + this.random.nextDouble() / 2.0D, 0.0D, this.random.nextDouble() / 5.0D, 0.0D);
                }
            } else {
                Optional<Pair<MobEffectList, Integer>> optional = this.getEffectFromItemStack(itemstack);

                if (!optional.isPresent()) {
                    return EnumInteractionResult.PASS;
                }

                Pair<MobEffectList, Integer> pair = (Pair) optional.get();

                if (!entityhuman.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

                for (int j = 0; j < 4; ++j) {
                    this.level.addParticle(Particles.EFFECT, this.getX() + this.random.nextDouble() / 2.0D, this.getY(0.5D), this.getZ() + this.random.nextDouble() / 2.0D, 0.0D, this.random.nextDouble() / 5.0D, 0.0D);
                }

                this.effect = (MobEffectList) pair.getLeft();
                this.effectDuration = (Integer) pair.getRight();
                this.playSound(SoundEffects.MOOSHROOM_EAT, 2.0F, 1.0F);
            }

            return EnumInteractionResult.sidedSuccess(this.level.isClientSide);
        } else {
            return super.mobInteract(entityhuman, enumhand);
        }
    }

    @Override
    public void shear(SoundCategory soundcategory) {
        this.level.playSound((EntityHuman) null, (Entity) this, SoundEffects.MOOSHROOM_SHEAR, soundcategory, 1.0F, 1.0F);
        if (!this.level.isClientSide()) {
            ((WorldServer) this.level).sendParticles(Particles.EXPLOSION, this.getX(), this.getY(0.5D), this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            this.discard();
            EntityCow entitycow = (EntityCow) EntityTypes.COW.create(this.level);

            entitycow.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
            entitycow.setHealth(this.getHealth());
            entitycow.yBodyRot = this.yBodyRot;
            if (this.hasCustomName()) {
                entitycow.setCustomName(this.getCustomName());
                entitycow.setCustomNameVisible(this.isCustomNameVisible());
            }

            if (this.isPersistenceRequired()) {
                entitycow.setPersistenceRequired();
            }

            entitycow.setInvulnerable(this.isInvulnerable());
            this.level.addFreshEntity(entitycow);

            for (int i = 0; i < 5; ++i) {
                this.level.addFreshEntity(new EntityItem(this.level, this.getX(), this.getY(1.0D), this.getZ(), new ItemStack(this.getMushroomType().blockState.getBlock())));
            }
        }

    }

    @Override
    public boolean readyForShearing() {
        return this.isAlive() && !this.isBaby();
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putString("Type", this.getMushroomType().type);
        if (this.effect != null) {
            nbttagcompound.putByte("EffectId", (byte) MobEffectList.getId(this.effect));
            nbttagcompound.putInt("EffectDuration", this.effectDuration);
        }

    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        this.setMushroomType(EntityMushroomCow.Type.byType(nbttagcompound.getString("Type")));
        if (nbttagcompound.contains("EffectId", 1)) {
            this.effect = MobEffectList.byId(nbttagcompound.getByte("EffectId"));
        }

        if (nbttagcompound.contains("EffectDuration", 3)) {
            this.effectDuration = nbttagcompound.getInt("EffectDuration");
        }

    }

    private Optional<Pair<MobEffectList, Integer>> getEffectFromItemStack(ItemStack itemstack) {
        Item item = itemstack.getItem();

        if (item instanceof ItemBlock) {
            Block block = ((ItemBlock) item).getBlock();

            if (block instanceof BlockFlowers) {
                BlockFlowers blockflowers = (BlockFlowers) block;

                return Optional.of(Pair.of(blockflowers.getSuspiciousStewEffect(), blockflowers.getEffectDuration()));
            }
        }

        return Optional.empty();
    }

    public void setMushroomType(EntityMushroomCow.Type entitymushroomcow_type) {
        this.entityData.set(EntityMushroomCow.DATA_TYPE, entitymushroomcow_type.type);
    }

    public EntityMushroomCow.Type getMushroomType() {
        return EntityMushroomCow.Type.byType((String) this.entityData.get(EntityMushroomCow.DATA_TYPE));
    }

    @Override
    public EntityMushroomCow getBreedOffspring(WorldServer worldserver, EntityAgeable entityageable) {
        EntityMushroomCow entitymushroomcow = (EntityMushroomCow) EntityTypes.MOOSHROOM.create(worldserver);

        entitymushroomcow.setMushroomType(this.getOffspringType((EntityMushroomCow) entityageable));
        return entitymushroomcow;
    }

    private EntityMushroomCow.Type getOffspringType(EntityMushroomCow entitymushroomcow) {
        EntityMushroomCow.Type entitymushroomcow_type = this.getMushroomType();
        EntityMushroomCow.Type entitymushroomcow_type1 = entitymushroomcow.getMushroomType();
        EntityMushroomCow.Type entitymushroomcow_type2;

        if (entitymushroomcow_type == entitymushroomcow_type1 && this.random.nextInt(1024) == 0) {
            entitymushroomcow_type2 = entitymushroomcow_type == EntityMushroomCow.Type.BROWN ? EntityMushroomCow.Type.RED : EntityMushroomCow.Type.BROWN;
        } else {
            entitymushroomcow_type2 = this.random.nextBoolean() ? entitymushroomcow_type : entitymushroomcow_type1;
        }

        return entitymushroomcow_type2;
    }

    public static enum Type {

        RED("red", Blocks.RED_MUSHROOM.defaultBlockState()), BROWN("brown", Blocks.BROWN_MUSHROOM.defaultBlockState());

        final String type;
        final IBlockData blockState;

        private Type(String s, IBlockData iblockdata) {
            this.type = s;
            this.blockState = iblockdata;
        }

        public IBlockData getBlockState() {
            return this.blockState;
        }

        static EntityMushroomCow.Type byType(String s) {
            EntityMushroomCow.Type[] aentitymushroomcow_type = values();
            int i = aentitymushroomcow_type.length;

            for (int j = 0; j < i; ++j) {
                EntityMushroomCow.Type entitymushroomcow_type = aentitymushroomcow_type[j];

                if (entitymushroomcow_type.type.equals(s)) {
                    return entitymushroomcow_type;
                }
            }

            return EntityMushroomCow.Type.RED;
        }
    }
}
