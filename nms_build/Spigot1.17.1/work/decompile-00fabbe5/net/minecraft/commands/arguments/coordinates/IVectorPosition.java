package net.minecraft.commands.arguments.coordinates;

import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.phys.Vec2F;
import net.minecraft.world.phys.Vec3D;

public interface IVectorPosition {

    Vec3D a(CommandListenerWrapper commandlistenerwrapper);

    Vec2F b(CommandListenerWrapper commandlistenerwrapper);

    default BlockPosition c(CommandListenerWrapper commandlistenerwrapper) {
        return new BlockPosition(this.a(commandlistenerwrapper));
    }

    boolean a();

    boolean b();

    boolean c();
}
