package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Objects;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.MathHelper;
import net.minecraft.util.SpawnUtil;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.monster.warden.WardenSpawnTracker;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.IProjectile;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.vibrations.VibrationListener;
import net.minecraft.world.phys.Vec3D;
import org.slf4j.Logger;

public class SculkShriekerBlockEntity extends TileEntity implements VibrationListener.b {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int LISTENER_RADIUS = 8;
    private static final int WARNING_SOUND_RADIUS = 10;
    private static final int WARDEN_SPAWN_ATTEMPTS = 20;
    private static final int WARDEN_SPAWN_RANGE_XZ = 5;
    private static final int WARDEN_SPAWN_RANGE_Y = 6;
    private static final int DARKNESS_RADIUS = 40;
    private static final Int2ObjectMap<SoundEffect> SOUND_BY_LEVEL = (Int2ObjectMap) SystemUtils.make(new Int2ObjectOpenHashMap(), (int2objectopenhashmap) -> {
        int2objectopenhashmap.put(1, SoundEffects.WARDEN_NEARBY_CLOSE);
        int2objectopenhashmap.put(2, SoundEffects.WARDEN_NEARBY_CLOSER);
        int2objectopenhashmap.put(3, SoundEffects.WARDEN_NEARBY_CLOSEST);
        int2objectopenhashmap.put(4, SoundEffects.WARDEN_LISTENING_ANGRY);
    });
    private static final int SHRIEKING_TICKS = 90;
    public int warningLevel;
    private VibrationListener listener;

    public SculkShriekerBlockEntity(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.SCULK_SHRIEKER, blockposition, iblockdata);
        this.listener = new VibrationListener(new BlockPositionSource(this.worldPosition), 8, this, (VibrationListener.a) null, 0.0F, 0);
    }

    public VibrationListener getListener() {
        return this.listener;
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        if (nbttagcompound.contains("warning_level", 99)) {
            this.warningLevel = nbttagcompound.getInt("warning_level");
        }

        if (nbttagcompound.contains("listener", 10)) {
            DataResult dataresult = VibrationListener.codec(this).parse(new Dynamic(DynamicOpsNBT.INSTANCE, nbttagcompound.getCompound("listener")));
            Logger logger = SculkShriekerBlockEntity.LOGGER;

            Objects.requireNonNull(logger);
            dataresult.resultOrPartial(logger::error).ifPresent((vibrationlistener) -> {
                this.listener = vibrationlistener;
            });
        }

    }

    @Override
    protected void saveAdditional(NBTTagCompound nbttagcompound) {
        super.saveAdditional(nbttagcompound);
        nbttagcompound.putInt("warning_level", this.warningLevel);
        DataResult dataresult = VibrationListener.codec(this).encodeStart(DynamicOpsNBT.INSTANCE, this.listener);
        Logger logger = SculkShriekerBlockEntity.LOGGER;

        Objects.requireNonNull(logger);
        dataresult.resultOrPartial(logger::error).ifPresent((nbtbase) -> {
            nbttagcompound.put("listener", nbtbase);
        });
    }

    @Override
    public TagKey<GameEvent> getListenableEvents() {
        return GameEventTags.SHRIEKER_CAN_LISTEN;
    }

    @Override
    public boolean shouldListen(WorldServer worldserver, GameEventListener gameeventlistener, BlockPosition blockposition, GameEvent gameevent, GameEvent.a gameevent_a) {
        return !this.isRemoved() && !(Boolean) this.getBlockState().getValue(SculkShriekerBlock.SHRIEKING) && tryGetPlayer(gameevent_a.sourceEntity()) != null;
    }

    @Nullable
    public static EntityPlayer tryGetPlayer(@Nullable Entity entity) {
        EntityPlayer entityplayer;

        if (entity instanceof EntityPlayer) {
            entityplayer = (EntityPlayer) entity;
            return entityplayer;
        } else {
            if (entity != null) {
                Entity entity1 = entity.getControllingPassenger();

                if (entity1 instanceof EntityPlayer) {
                    entityplayer = (EntityPlayer) entity1;
                    return entityplayer;
                }
            }

            Entity entity2;
            EntityPlayer entityplayer1;

            if (entity instanceof IProjectile) {
                IProjectile iprojectile = (IProjectile) entity;

                entity2 = iprojectile.getOwner();
                if (entity2 instanceof EntityPlayer) {
                    entityplayer1 = (EntityPlayer) entity2;
                    return entityplayer1;
                }
            }

            if (entity instanceof EntityItem) {
                EntityItem entityitem = (EntityItem) entity;

                entity2 = entityitem.getThrowingEntity();
                if (entity2 instanceof EntityPlayer) {
                    entityplayer1 = (EntityPlayer) entity2;
                    return entityplayer1;
                }
            }

            return null;
        }
    }

    @Override
    public void onSignalReceive(WorldServer worldserver, GameEventListener gameeventlistener, BlockPosition blockposition, GameEvent gameevent, @Nullable Entity entity, @Nullable Entity entity1, float f) {
        this.tryShriek(worldserver, tryGetPlayer(entity1 != null ? entity1 : entity));
    }

    public void tryShriek(WorldServer worldserver, @Nullable EntityPlayer entityplayer) {
        if (entityplayer != null) {
            IBlockData iblockdata = this.getBlockState();

            if (!(Boolean) iblockdata.getValue(SculkShriekerBlock.SHRIEKING)) {
                this.warningLevel = 0;
                if (!this.canRespond(worldserver) || this.tryToWarn(worldserver, entityplayer)) {
                    this.shriek(worldserver, entityplayer);
                }
            }
        }
    }

    private boolean tryToWarn(WorldServer worldserver, EntityPlayer entityplayer) {
        OptionalInt optionalint = WardenSpawnTracker.tryWarn(worldserver, this.getBlockPos(), entityplayer);

        optionalint.ifPresent((i) -> {
            this.warningLevel = i;
        });
        return optionalint.isPresent();
    }

    private void shriek(WorldServer worldserver, @Nullable Entity entity) {
        BlockPosition blockposition = this.getBlockPos();
        IBlockData iblockdata = this.getBlockState();

        worldserver.setBlock(blockposition, (IBlockData) iblockdata.setValue(SculkShriekerBlock.SHRIEKING, true), 2);
        worldserver.scheduleTick(blockposition, iblockdata.getBlock(), 90);
        worldserver.levelEvent(3007, blockposition, 0);
        worldserver.gameEvent(GameEvent.SHRIEK, blockposition, GameEvent.a.of(entity));
    }

    private boolean canRespond(WorldServer worldserver) {
        return (Boolean) this.getBlockState().getValue(SculkShriekerBlock.CAN_SUMMON) && worldserver.getDifficulty() != EnumDifficulty.PEACEFUL && worldserver.getGameRules().getBoolean(GameRules.RULE_DO_WARDEN_SPAWNING);
    }

    public void tryRespond(WorldServer worldserver) {
        if (this.canRespond(worldserver) && this.warningLevel > 0) {
            if (!this.trySummonWarden(worldserver)) {
                this.playWardenReplySound();
            }

            Warden.applyDarknessAround(worldserver, Vec3D.atCenterOf(this.getBlockPos()), (Entity) null, 40);
        }

    }

    private void playWardenReplySound() {
        SoundEffect soundeffect = (SoundEffect) SculkShriekerBlockEntity.SOUND_BY_LEVEL.get(this.warningLevel);

        if (soundeffect != null) {
            BlockPosition blockposition = this.getBlockPos();
            int i = blockposition.getX() + MathHelper.randomBetweenInclusive(this.level.random, -10, 10);
            int j = blockposition.getY() + MathHelper.randomBetweenInclusive(this.level.random, -10, 10);
            int k = blockposition.getZ() + MathHelper.randomBetweenInclusive(this.level.random, -10, 10);

            this.level.playSound((EntityHuman) null, (double) i, (double) j, (double) k, soundeffect, SoundCategory.HOSTILE, 5.0F, 1.0F);
        }

    }

    private boolean trySummonWarden(WorldServer worldserver) {
        return this.warningLevel < 4 ? false : SpawnUtil.trySpawnMob(EntityTypes.WARDEN, EnumMobSpawn.TRIGGERED, worldserver, this.getBlockPos(), 20, 5, 6, SpawnUtil.a.ON_TOP_OF_COLLIDER).isPresent();
    }

    @Override
    public void onSignalSchedule() {
        this.setChanged();
    }
}
