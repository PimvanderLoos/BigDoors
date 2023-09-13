package net.minecraft.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.World;

public final class GlobalPos {

    public static final Codec<GlobalPos> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(World.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter(GlobalPos::dimension), BlockPosition.CODEC.fieldOf("pos").forGetter(GlobalPos::pos)).apply(instance, GlobalPos::of);
    });
    private final ResourceKey<World> dimension;
    private final BlockPosition pos;

    private GlobalPos(ResourceKey<World> resourcekey, BlockPosition blockposition) {
        this.dimension = resourcekey;
        this.pos = blockposition;
    }

    public static GlobalPos of(ResourceKey<World> resourcekey, BlockPosition blockposition) {
        return new GlobalPos(resourcekey, blockposition);
    }

    public ResourceKey<World> dimension() {
        return this.dimension;
    }

    public BlockPosition pos() {
        return this.pos;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            GlobalPos globalpos = (GlobalPos) object;

            return Objects.equals(this.dimension, globalpos.dimension) && Objects.equals(this.pos, globalpos.pos);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.dimension, this.pos});
    }

    public String toString() {
        return this.dimension + " " + this.pos;
    }
}
