package net.minecraft.world.entity.animal;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;
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

    private static final DataWatcherObject<String> DATA_TYPE = DataWatcher.a(EntityMushroomCow.class, DataWatcherRegistry.STRING);
    private static final int MUTATE_CHANCE = 1024;
    private MobEffectList effect;
    private int effectDuration;
    private UUID lastLightningBoltUUID;

    public EntityMushroomCow(EntityTypes<? extends EntityMushroomCow> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    public float a(BlockPosition blockposition, IWorldReader iworldreader) {
        return iworldreader.getType(blockposition.down()).a(Blocks.MYCELIUM) ? 10.0F : iworldreader.z(blockposition) - 0.5F;
    }

    public static boolean c(EntityTypes<EntityMushroomCow> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return generatoraccess.getType(blockposition.down()).a(Blocks.MYCELIUM) && generatoraccess.getLightLevel(blockposition, 0) > 8;
    }

    @Override
    public void onLightningStrike(WorldServer worldserver, EntityLightning entitylightning) {
        UUID uuid = entitylightning.getUniqueID();

        if (!uuid.equals(this.lastLightningBoltUUID)) {
            this.setVariant(this.getVariant() == EntityMushroomCow.Type.RED ? EntityMushroomCow.Type.BROWN : EntityMushroomCow.Type.RED);
            this.lastLightningBoltUUID = uuid;
            this.playSound(SoundEffects.MOOSHROOM_CONVERT, 2.0F, 1.0F);
        }

    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityMushroomCow.DATA_TYPE, EntityMushroomCow.Type.RED.type);
    }

    @Override
    public EnumInteractionResult b(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (itemstack.a(Items.BOWL) && !this.isBaby()) {
            boolean flag = false;
            ItemStack itemstack1;

            if (this.effect != null) {
                flag = true;
                itemstack1 = new ItemStack(Items.SUSPICIOUS_STEW);
                ItemSuspiciousStew.a(itemstack1, this.effect, this.effectDuration);
                this.effect = null;
                this.effectDuration = 0;
            } else {
                itemstack1 = new ItemStack(Items.MUSHROOM_STEW);
            }

            ItemStack itemstack2 = ItemLiquidUtil.a(itemstack, entityhuman, itemstack1, false);

            entityhuman.a(enumhand, itemstack2);
            SoundEffect soundeffect;

            if (flag) {
                soundeffect = SoundEffects.MOOSHROOM_MILK_SUSPICIOUSLY;
            } else {
                soundeffect = SoundEffects.MOOSHROOM_MILK;
            }

            this.playSound(soundeffect, 1.0F, 1.0F);
            return EnumInteractionResult.a(this.level.isClientSide);
        } else if (itemstack.a(Items.SHEARS) && this.canShear()) {
            this.shear(SoundCategory.PLAYERS);
            this.a(GameEvent.SHEAR, (Entity) entityhuman);
            if (!this.level.isClientSide) {
                itemstack.damage(1, entityhuman, (entityhuman1) -> {
                    entityhuman1.broadcastItemBreak(enumhand);
                });
            }

            return EnumInteractionResult.a(this.level.isClientSide);
        } else if (this.getVariant() == EntityMushroomCow.Type.BROWN && itemstack.a((Tag) TagsItem.SMALL_FLOWERS)) {
            if (this.effect != null) {
                for (int i = 0; i < 2; ++i) {
                    this.level.addParticle(Particles.SMOKE, this.locX() + this.random.nextDouble() / 2.0D, this.e(0.5D), this.locZ() + this.random.nextDouble() / 2.0D, 0.0D, this.random.nextDouble() / 5.0D, 0.0D);
                }
            } else {
                Optional<Pair<MobEffectList, Integer>> optional = this.m(itemstack);

                if (!optional.isPresent()) {
                    return EnumInteractionResult.PASS;
                }

                Pair<MobEffectList, Integer> pair = (Pair) optional.get();

                if (!entityhuman.getAbilities().instabuild) {
                    itemstack.subtract(1);
                }

                for (int j = 0; j < 4; ++j) {
                    this.level.addParticle(Particles.EFFECT, this.locX() + this.random.nextDouble() / 2.0D, this.e(0.5D), this.locZ() + this.random.nextDouble() / 2.0D, 0.0D, this.random.nextDouble() / 5.0D, 0.0D);
                }

                this.effect = (MobEffectList) pair.getLeft();
                this.effectDuration = (Integer) pair.getRight();
                this.playSound(SoundEffects.MOOSHROOM_EAT, 2.0F, 1.0F);
            }

            return EnumInteractionResult.a(this.level.isClientSide);
        } else {
            return super.b(entityhuman, enumhand);
        }
    }

    @Override
    public void shear(SoundCategory soundcategory) {
        this.level.playSound((EntityHuman) null, (Entity) this, SoundEffects.MOOSHROOM_SHEAR, soundcategory, 1.0F, 1.0F);
        if (!this.level.isClientSide()) {
            ((WorldServer) this.level).a(Particles.EXPLOSION, this.locX(), this.e(0.5D), this.locZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            this.die();
            EntityCow entitycow = (EntityCow) EntityTypes.COW.a(this.level);

            entitycow.setPositionRotation(this.locX(), this.locY(), this.locZ(), this.getYRot(), this.getXRot());
            entitycow.setHealth(this.getHealth());
            entitycow.yBodyRot = this.yBodyRot;
            if (this.hasCustomName()) {
                entitycow.setCustomName(this.getCustomName());
                entitycow.setCustomNameVisible(this.getCustomNameVisible());
            }

            if (this.isPersistent()) {
                entitycow.setPersistent();
            }

            entitycow.setInvulnerable(this.isInvulnerable());
            this.level.addEntity(entitycow);

            for (int i = 0; i < 5; ++i) {
                this.level.addEntity(new EntityItem(this.level, this.locX(), this.e(1.0D), this.locZ(), new ItemStack(this.getVariant().blockState.getBlock())));
            }
        }

    }

    @Override
    public boolean canShear() {
        return this.isAlive() && !this.isBaby();
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setString("Type", this.getVariant().type);
        if (this.effect != null) {
            nbttagcompound.setByte("EffectId", (byte) MobEffectList.getId(this.effect));
            nbttagcompound.setInt("EffectDuration", this.effectDuration);
        }

    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.setVariant(EntityMushroomCow.Type.a(nbttagcompound.getString("Type")));
        if (nbttagcompound.hasKeyOfType("EffectId", 1)) {
            this.effect = MobEffectList.fromId(nbttagcompound.getByte("EffectId"));
        }

        if (nbttagcompound.hasKeyOfType("EffectDuration", 3)) {
            this.effectDuration = nbttagcompound.getInt("EffectDuration");
        }

    }

    private Optional<Pair<MobEffectList, Integer>> m(ItemStack itemstack) {
        Item item = itemstack.getItem();

        if (item instanceof ItemBlock) {
            Block block = ((ItemBlock) item).getBlock();

            if (block instanceof BlockFlowers) {
                BlockFlowers blockflowers = (BlockFlowers) block;

                return Optional.of(Pair.of(blockflowers.c(), blockflowers.d()));
            }
        }

        return Optional.empty();
    }

    public void setVariant(EntityMushroomCow.Type entitymushroomcow_type) {
        this.entityData.set(EntityMushroomCow.DATA_TYPE, entitymushroomcow_type.type);
    }

    public EntityMushroomCow.Type getVariant() {
        return EntityMushroomCow.Type.a((String) this.entityData.get(EntityMushroomCow.DATA_TYPE));
    }

    @Override
    public EntityMushroomCow createChild(WorldServer worldserver, EntityAgeable entityageable) {
        EntityMushroomCow entitymushroomcow = (EntityMushroomCow) EntityTypes.MOOSHROOM.a((World) worldserver);

        entitymushroomcow.setVariant(this.a((EntityMushroomCow) entityageable));
        return entitymushroomcow;
    }

    private EntityMushroomCow.Type a(EntityMushroomCow entitymushroomcow) {
        EntityMushroomCow.Type entitymushroomcow_type = this.getVariant();
        EntityMushroomCow.Type entitymushroomcow_type1 = entitymushroomcow.getVariant();
        EntityMushroomCow.Type entitymushroomcow_type2;

        if (entitymushroomcow_type == entitymushroomcow_type1 && this.random.nextInt(1024) == 0) {
            entitymushroomcow_type2 = entitymushroomcow_type == EntityMushroomCow.Type.BROWN ? EntityMushroomCow.Type.RED : EntityMushroomCow.Type.BROWN;
        } else {
            entitymushroomcow_type2 = this.random.nextBoolean() ? entitymushroomcow_type : entitymushroomcow_type1;
        }

        return entitymushroomcow_type2;
    }

    public static enum Type {

        RED("red", Blocks.RED_MUSHROOM.getBlockData()), BROWN("brown", Blocks.BROWN_MUSHROOM.getBlockData());

        final String type;
        final IBlockData blockState;

        private Type(String s, IBlockData iblockdata) {
            this.type = s;
            this.blockState = iblockdata;
        }

        public IBlockData a() {
            return this.blockState;
        }

        static EntityMushroomCow.Type a(String s) {
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
