package net.minecraft.world.level.gameevent.vibrations;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipBlockStateContext;
import net.minecraft.world.level.World;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.Vec3D;

public class VibrationListener implements GameEventListener {

    protected final PositionSource listenerSource;
    protected final int listenerRange;
    protected final VibrationListener.a config;
    protected Optional<GameEvent> receivingEvent = Optional.empty();
    protected int receivingDistance;
    protected int travelTimeInTicks = 0;

    public VibrationListener(PositionSource positionsource, int i, VibrationListener.a vibrationlistener_a) {
        this.listenerSource = positionsource;
        this.listenerRange = i;
        this.config = vibrationlistener_a;
    }

    public void tick(World world) {
        if (this.receivingEvent.isPresent()) {
            --this.travelTimeInTicks;
            if (this.travelTimeInTicks <= 0) {
                this.travelTimeInTicks = 0;
                this.config.onSignalReceive(world, this, (GameEvent) this.receivingEvent.get(), this.receivingDistance);
                this.receivingEvent = Optional.empty();
            }
        }

    }

    @Override
    public PositionSource getListenerSource() {
        return this.listenerSource;
    }

    @Override
    public int getListenerRadius() {
        return this.listenerRange;
    }

    @Override
    public boolean handleGameEvent(World world, GameEvent gameevent, @Nullable Entity entity, BlockPosition blockposition) {
        if (!this.isValidVibration(gameevent, entity)) {
            return false;
        } else {
            Optional<BlockPosition> optional = this.listenerSource.getPosition(world);

            if (!optional.isPresent()) {
                return false;
            } else {
                BlockPosition blockposition1 = (BlockPosition) optional.get();

                if (!this.config.shouldListen(world, this, blockposition, gameevent, entity)) {
                    return false;
                } else if (this.isOccluded(world, blockposition, blockposition1)) {
                    return false;
                } else {
                    this.sendSignal(world, gameevent, blockposition, blockposition1);
                    return true;
                }
            }
        }
    }

    private boolean isValidVibration(GameEvent gameevent, @Nullable Entity entity) {
        if (this.receivingEvent.isPresent()) {
            return false;
        } else if (!GameEventTags.VIBRATIONS.contains(gameevent)) {
            return false;
        } else {
            if (entity != null) {
                if (GameEventTags.IGNORE_VIBRATIONS_SNEAKING.contains(gameevent) && entity.isSteppingCarefully()) {
                    return false;
                }

                if (entity.occludesVibrations()) {
                    return false;
                }
            }

            return entity == null || !entity.isSpectator();
        }
    }

    private void sendSignal(World world, GameEvent gameevent, BlockPosition blockposition, BlockPosition blockposition1) {
        this.receivingEvent = Optional.of(gameevent);
        if (world instanceof WorldServer) {
            this.receivingDistance = MathHelper.floor(Math.sqrt(blockposition.distSqr((BaseBlockPosition) blockposition1, false)));
            this.travelTimeInTicks = this.receivingDistance;
            ((WorldServer) world).sendVibrationParticle(new VibrationPath(blockposition, this.listenerSource, this.travelTimeInTicks));
        }

    }

    private boolean isOccluded(World world, BlockPosition blockposition, BlockPosition blockposition1) {
        return world.isBlockInLine(new ClipBlockStateContext(Vec3D.atCenterOf(blockposition), Vec3D.atCenterOf(blockposition1), (iblockdata) -> {
            return iblockdata.is((Tag) TagsBlock.OCCLUDES_VIBRATION_SIGNALS);
        })).getType() == MovingObjectPosition.EnumMovingObjectType.BLOCK;
    }

    public interface a {

        boolean shouldListen(World world, GameEventListener gameeventlistener, BlockPosition blockposition, GameEvent gameevent, @Nullable Entity entity);

        void onSignalReceive(World world, GameEventListener gameeventlistener, GameEvent gameevent, int i);
    }
}
