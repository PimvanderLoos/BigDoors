package net.minecraft.server;

import javax.annotation.Nullable;

public interface ICommandListener {

    String getName();

    default IChatBaseComponent getScoreboardDisplayName() {
        return new ChatComponentText(this.getName());
    }

    default void sendMessage(IChatBaseComponent ichatbasecomponent) {}

    boolean a(int i, String s);

    default BlockPosition getChunkCoordinates() {
        return BlockPosition.ZERO;
    }

    default Vec3D d() {
        return Vec3D.a;
    }

    World getWorld();

    @Nullable
    default Entity f() {
        return null;
    }

    default boolean getSendCommandFeedback() {
        return false;
    }

    default void a(CommandObjectiveExecutor.EnumCommandResult commandobjectiveexecutor_enumcommandresult, int i) {}

    @Nullable
    MinecraftServer C_();
}
