package net.minecraft.world.entity.ai.village.poi;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.util.VisibleForDebug;

public class VillagePlaceRecord {

    private final BlockPosition pos;
    private final VillagePlaceType poiType;
    private int freeTickets;
    private final Runnable setDirty;

    public static Codec<VillagePlaceRecord> codec(Runnable runnable) {
        return RecordCodecBuilder.create((instance) -> {
            return instance.group(BlockPosition.CODEC.fieldOf("pos").forGetter((villageplacerecord) -> {
                return villageplacerecord.pos;
            }), IRegistry.POINT_OF_INTEREST_TYPE.byNameCodec().fieldOf("type").forGetter((villageplacerecord) -> {
                return villageplacerecord.poiType;
            }), Codec.INT.fieldOf("free_tickets").orElse(0).forGetter((villageplacerecord) -> {
                return villageplacerecord.freeTickets;
            }), RecordCodecBuilder.point(runnable)).apply(instance, VillagePlaceRecord::new);
        });
    }

    private VillagePlaceRecord(BlockPosition blockposition, VillagePlaceType villageplacetype, int i, Runnable runnable) {
        this.pos = blockposition.immutable();
        this.poiType = villageplacetype;
        this.freeTickets = i;
        this.setDirty = runnable;
    }

    public VillagePlaceRecord(BlockPosition blockposition, VillagePlaceType villageplacetype, Runnable runnable) {
        this(blockposition, villageplacetype, villageplacetype.getMaxTickets(), runnable);
    }

    /** @deprecated */
    @Deprecated
    @VisibleForDebug
    public int getFreeTickets() {
        return this.freeTickets;
    }

    protected boolean acquireTicket() {
        if (this.freeTickets <= 0) {
            return false;
        } else {
            --this.freeTickets;
            this.setDirty.run();
            return true;
        }
    }

    protected boolean releaseTicket() {
        if (this.freeTickets >= this.poiType.getMaxTickets()) {
            return false;
        } else {
            ++this.freeTickets;
            this.setDirty.run();
            return true;
        }
    }

    public boolean hasSpace() {
        return this.freeTickets > 0;
    }

    public boolean isOccupied() {
        return this.freeTickets != this.poiType.getMaxTickets();
    }

    public BlockPosition getPos() {
        return this.pos;
    }

    public VillagePlaceType getPoiType() {
        return this.poiType;
    }

    public boolean equals(Object object) {
        return this == object ? true : (object != null && this.getClass() == object.getClass() ? Objects.equals(this.pos, ((VillagePlaceRecord) object).pos) : false);
    }

    public int hashCode() {
        return this.pos.hashCode();
    }
}
