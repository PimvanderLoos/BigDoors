package net.minecraft.world.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.level.material.EnumPistonReaction;
import net.minecraft.world.phys.AxisAlignedBB;
import org.slf4j.Logger;

public class Interaction extends Entity implements Attackable, Targeting {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final DataWatcherObject<Float> DATA_WIDTH_ID = DataWatcher.defineId(Interaction.class, DataWatcherRegistry.FLOAT);
    private static final DataWatcherObject<Float> DATA_HEIGHT_ID = DataWatcher.defineId(Interaction.class, DataWatcherRegistry.FLOAT);
    private static final DataWatcherObject<Boolean> DATA_RESPONSE_ID = DataWatcher.defineId(Interaction.class, DataWatcherRegistry.BOOLEAN);
    private static final String TAG_WIDTH = "width";
    private static final String TAG_HEIGHT = "height";
    private static final String TAG_ATTACK = "attack";
    private static final String TAG_INTERACTION = "interaction";
    private static final String TAG_RESPONSE = "response";
    @Nullable
    public Interaction.PlayerAction attack;
    @Nullable
    public Interaction.PlayerAction interaction;

    public Interaction(EntityTypes<?> entitytypes, World world) {
        super(entitytypes, world);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(Interaction.DATA_WIDTH_ID, 1.0F);
        this.entityData.define(Interaction.DATA_HEIGHT_ID, 1.0F);
        this.entityData.define(Interaction.DATA_RESPONSE_ID, false);
    }

    @Override
    protected void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        if (nbttagcompound.contains("width", 99)) {
            this.setWidth(nbttagcompound.getFloat("width"));
        }

        if (nbttagcompound.contains("height", 99)) {
            this.setHeight(nbttagcompound.getFloat("height"));
        }

        DataResult dataresult;
        Logger logger;

        if (nbttagcompound.contains("attack")) {
            dataresult = Interaction.PlayerAction.CODEC.decode(DynamicOpsNBT.INSTANCE, nbttagcompound.get("attack"));
            logger = Interaction.LOGGER;
            Objects.requireNonNull(logger);
            dataresult.resultOrPartial(SystemUtils.prefix("Interaction entity", logger::error)).ifPresent((pair) -> {
                this.attack = (Interaction.PlayerAction) pair.getFirst();
            });
        } else {
            this.attack = null;
        }

        if (nbttagcompound.contains("interaction")) {
            dataresult = Interaction.PlayerAction.CODEC.decode(DynamicOpsNBT.INSTANCE, nbttagcompound.get("interaction"));
            logger = Interaction.LOGGER;
            Objects.requireNonNull(logger);
            dataresult.resultOrPartial(SystemUtils.prefix("Interaction entity", logger::error)).ifPresent((pair) -> {
                this.interaction = (Interaction.PlayerAction) pair.getFirst();
            });
        } else {
            this.interaction = null;
        }

        this.setResponse(nbttagcompound.getBoolean("response"));
        this.setBoundingBox(this.makeBoundingBox());
    }

    @Override
    protected void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        nbttagcompound.putFloat("width", this.getWidth());
        nbttagcompound.putFloat("height", this.getHeight());
        if (this.attack != null) {
            Interaction.PlayerAction.CODEC.encodeStart(DynamicOpsNBT.INSTANCE, this.attack).result().ifPresent((nbtbase) -> {
                nbttagcompound.put("attack", nbtbase);
            });
        }

        if (this.interaction != null) {
            Interaction.PlayerAction.CODEC.encodeStart(DynamicOpsNBT.INSTANCE, this.interaction).result().ifPresent((nbtbase) -> {
                nbttagcompound.put("interaction", nbtbase);
            });
        }

        nbttagcompound.putBoolean("response", this.getResponse());
    }

    @Override
    public void onSyncedDataUpdated(DataWatcherObject<?> datawatcherobject) {
        super.onSyncedDataUpdated(datawatcherobject);
        if (Interaction.DATA_HEIGHT_ID.equals(datawatcherobject) || Interaction.DATA_WIDTH_ID.equals(datawatcherobject)) {
            this.setBoundingBox(this.makeBoundingBox());
        }

    }

    @Override
    public boolean canBeHitByProjectile() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public EnumPistonReaction getPistonPushReaction() {
        return EnumPistonReaction.IGNORE;
    }

    @Override
    public boolean skipAttackInteraction(Entity entity) {
        if (entity instanceof EntityHuman) {
            EntityHuman entityhuman = (EntityHuman) entity;

            this.attack = new Interaction.PlayerAction(entityhuman.getUUID(), this.level.getGameTime());
            if (entityhuman instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer) entityhuman;

                CriterionTriggers.PLAYER_HURT_ENTITY.trigger(entityplayer, this, entityhuman.damageSources().generic(), 1.0F, 1.0F, false);
            }

            return !this.getResponse();
        } else {
            return false;
        }
    }

    @Override
    public EnumInteractionResult interact(EntityHuman entityhuman, EnumHand enumhand) {
        if (this.level.isClientSide) {
            return this.getResponse() ? EnumInteractionResult.SUCCESS : EnumInteractionResult.CONSUME;
        } else {
            this.interaction = new Interaction.PlayerAction(entityhuman.getUUID(), this.level.getGameTime());
            return EnumInteractionResult.CONSUME;
        }
    }

    @Override
    public void tick() {}

    @Nullable
    @Override
    public EntityLiving getLastAttacker() {
        return this.attack != null ? this.level.getPlayerByUUID(this.attack.player()) : null;
    }

    @Nullable
    @Override
    public EntityLiving getTarget() {
        return this.interaction != null ? this.level.getPlayerByUUID(this.interaction.player()) : null;
    }

    public void setWidth(float f) {
        this.entityData.set(Interaction.DATA_WIDTH_ID, f);
    }

    public float getWidth() {
        return (Float) this.entityData.get(Interaction.DATA_WIDTH_ID);
    }

    public void setHeight(float f) {
        this.entityData.set(Interaction.DATA_HEIGHT_ID, f);
    }

    public float getHeight() {
        return (Float) this.entityData.get(Interaction.DATA_HEIGHT_ID);
    }

    public void setResponse(boolean flag) {
        this.entityData.set(Interaction.DATA_RESPONSE_ID, flag);
    }

    public boolean getResponse() {
        return (Boolean) this.entityData.get(Interaction.DATA_RESPONSE_ID);
    }

    private EntitySize getDimensions() {
        return EntitySize.scalable(this.getWidth(), this.getHeight());
    }

    @Override
    public EntitySize getDimensions(EntityPose entitypose) {
        return this.getDimensions();
    }

    @Override
    protected AxisAlignedBB makeBoundingBox() {
        return this.getDimensions().makeBoundingBox(this.position());
    }

    public static record PlayerAction(UUID player, long timestamp) {

        public static final Codec<Interaction.PlayerAction> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(UUIDUtil.CODEC.fieldOf("player").forGetter(Interaction.PlayerAction::player), Codec.LONG.fieldOf("timestamp").forGetter(Interaction.PlayerAction::timestamp)).apply(instance, Interaction.PlayerAction::new);
        });
    }
}
