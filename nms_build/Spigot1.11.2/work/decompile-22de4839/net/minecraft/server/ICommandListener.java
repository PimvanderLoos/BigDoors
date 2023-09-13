package net.minecraft.server;

import javax.annotation.Nullable;

public interface ICommandListener {

    String getName();

    IChatBaseComponent getScoreboardDisplayName();

    void sendMessage(IChatBaseComponent ichatbasecomponent);

    boolean a(int i, String s);

    BlockPosition getChunkCoordinates();

    Vec3D d();

    World getWorld();

    @Nullable
    Entity f();

    boolean getSendCommandFeedback();

    void a(CommandObjectiveExecutor.EnumCommandResult commandobjectiveexecutor_enumcommandresult, int i);

    @Nullable
    MinecraftServer B_();
}
