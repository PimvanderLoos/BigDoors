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

    public static Codec<VillagePlaceRecord> a(Runnable runnable) {
        return RecordCodecBuilder.create((instance) -> {
            return instance.group(BlockPosition.CODEC.fieldOf("pos").forGetter((villageplacerecord) -> {
                return villageplacerecord.pos;
            }), IRegistry.POINT_OF_INTEREST_TYPE.fieldOf("type").forGetter((villageplacerecord) -> {
                return villageplacerecord.poiType;
            }), Codec.INT.fieldOf("free_tickets").orElse(0).forGetter((villageplacerecord) -> {
                return villageplacerecord.freeTickets;
            }), RecordCodecBuilder.point(runnable)).apply(instance, VillagePlaceRecord::new);
        });
    }

    private VillagePlaceRecord(BlockPosition blockposition, VillagePlaceType villageplacetype, int i, Runnable runnable) {
        this.pos = blockposition.immutableCopy();
        this.poiType = villageplacetype;
        this.freeTickets = i;
        this.setDirty = runnable;
    }

    public VillagePlaceRecord(BlockPosition blockposition, VillagePlaceType villageplacetype, Runnable runnable) {
        this(blockposition, villageplacetype, villageplacetype.b(), runnable);
    }

    @Deprecated
    @VisibleForDebug
    public int a() {
        return this.freeTickets;
    }

    protected boolean b() {
        if (this.freeTickets <= 0) {
            return false;
        } else {
            --this.freeTickets;
            this.setDirty.run();
            return true;
        }
    }

    protected boolean c() {
        if (this.freeTickets >= this.poiType.b()) {
            return false;
        } else {
            ++this.freeTickets;
            this.setDirty.run();
            return true;
        }
    }

    public boolean d() {
        return this.freeTickets > 0;
    }

    public boolean e() {
        return this.freeTickets != this.poiType.b();
    }

    public BlockPosition f() {
        return this.pos;
    }

    public VillagePlaceType g() {
        return this.poiType;
    }

    public boolean equals(Object object) {
        return this == object ? true : (object != null && this.getClass() == object.getClass() ? Objects.equals(this.pos, ((VillagePlaceRecord) object).pos) : false);
    }

    public int hashCode() {
        return this.pos.hashCode();
    }
}
