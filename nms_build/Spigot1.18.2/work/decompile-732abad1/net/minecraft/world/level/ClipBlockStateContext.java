package net.minecraft.world.level;

import java.util.function.Predicate;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec3D;

public class ClipBlockStateContext {

    private final Vec3D from;
    private final Vec3D to;
    private final Predicate<IBlockData> block;

    public ClipBlockStateContext(Vec3D vec3d, Vec3D vec3d1, Predicate<IBlockData> predicate) {
        this.from = vec3d;
        this.to = vec3d1;
        this.block = predicate;
    }

    public Vec3D getTo() {
        return this.to;
    }

    public Vec3D getFrom() {
        return this.from;
    }

    public Predicate<IBlockData> isTargetBlock() {
        return this.block;
    }
}
