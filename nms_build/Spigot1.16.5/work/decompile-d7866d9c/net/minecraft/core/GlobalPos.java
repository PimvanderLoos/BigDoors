package net.minecraft.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.World;

public final class GlobalPos {

    public static final Codec<GlobalPos> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(World.f.fieldOf("dimension").forGetter(GlobalPos::getDimensionManager), BlockPosition.a.fieldOf("pos").forGetter(GlobalPos::getBlockPosition)).apply(instance, GlobalPos::create);
    });
    private final ResourceKey<World> dimensionManager;
    private final BlockPosition blockPosition;

    private GlobalPos(ResourceKey<World> resourcekey, BlockPosition blockposition) {
        this.dimensionManager = resourcekey;
        this.blockPosition = blockposition;
    }

    public static GlobalPos create(ResourceKey<World> resourcekey, BlockPosition blockposition) {
        return new GlobalPos(resourcekey, blockposition);
    }

    public ResourceKey<World> getDimensionManager() {
        return this.dimensionManager;
    }

    public BlockPosition getBlockPosition() {
        return this.blockPosition;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            GlobalPos globalpos = (GlobalPos) object;

            return Objects.equals(this.dimensionManager, globalpos.dimensionManager) && Objects.equals(this.blockPosition, globalpos.blockPosition);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.dimensionManager, this.blockPosition});
    }

    public String toString() {
        return this.dimensionManager.toString() + " " + this.blockPosition;
    }
}
