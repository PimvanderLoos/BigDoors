package net.minecraft.commands.arguments.coordinates;

import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.phys.Vec2F;
import net.minecraft.world.phys.Vec3D;

public interface IVectorPosition {

    Vec3D getPosition(CommandListenerWrapper commandlistenerwrapper);

    Vec2F getRotation(CommandListenerWrapper commandlistenerwrapper);

    default BlockPosition getBlockPos(CommandListenerWrapper commandlistenerwrapper) {
        return BlockPosition.containing(this.getPosition(commandlistenerwrapper));
    }

    boolean isXRelative();

    boolean isYRelative();

    boolean isZRelative();
}
