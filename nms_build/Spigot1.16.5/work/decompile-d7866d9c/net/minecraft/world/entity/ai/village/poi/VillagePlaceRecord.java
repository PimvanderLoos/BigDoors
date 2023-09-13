package net.minecraft.world.entity.ai.village.poi;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;

public class VillagePlaceRecord {

    private final BlockPosition a;
    private final VillagePlaceType b;
    private int c;
    private final Runnable d;

    public static Codec<VillagePlaceRecord> a(Runnable runnable) {
        return RecordCodecBuilder.create((instance) -> {
            return instance.group(BlockPosition.a.fieldOf("pos").forGetter((villageplacerecord) -> {
                return villageplacerecord.a;
            }), IRegistry.POINT_OF_INTEREST_TYPE.fieldOf("type").forGetter((villageplacerecord) -> {
                return villageplacerecord.b;
            }), Codec.INT.fieldOf("free_tickets").orElse(0).forGetter((villageplacerecord) -> {
                return villageplacerecord.c;
            }), RecordCodecBuilder.point(runnable)).apply(instance, VillagePlaceRecord::new);
        });
    }

    private VillagePlaceRecord(BlockPosition blockposition, VillagePlaceType villageplacetype, int i, Runnable runnable) {
        this.a = blockposition.immutableCopy();
        this.b = villageplacetype;
        this.c = i;
        this.d = runnable;
    }

    public VillagePlaceRecord(BlockPosition blockposition, VillagePlaceType villageplacetype, Runnable runnable) {
        this(blockposition, villageplacetype, villageplacetype.b(), runnable);
    }

    protected boolean b() {
        if (this.c <= 0) {
            return false;
        } else {
            --this.c;
            this.d.run();
            return true;
        }
    }

    protected boolean c() {
        if (this.c >= this.b.b()) {
            return false;
        } else {
            ++this.c;
            this.d.run();
            return true;
        }
    }

    public boolean d() {
        return this.c > 0;
    }

    public boolean e() {
        return this.c != this.b.b();
    }

    public BlockPosition f() {
        return this.a;
    }

    public VillagePlaceType g() {
        return this.b;
    }

    public boolean equals(Object object) {
        return this == object ? true : (object != null && this.getClass() == object.getClass() ? Objects.equals(this.a, ((VillagePlaceRecord) object).a) : false);
    }

    public int hashCode() {
        return this.a.hashCode();
    }
}
