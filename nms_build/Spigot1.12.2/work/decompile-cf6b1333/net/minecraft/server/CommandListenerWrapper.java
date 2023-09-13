package net.minecraft.server;

import java.util.Objects;
import javax.annotation.Nullable;

public class CommandListenerWrapper implements ICommandListener {

    public final ICommandListener base;
    @Nullable
    private final Vec3D b;
    @Nullable
    private final BlockPosition c;
    @Nullable
    private final Integer d;
    @Nullable
    private final Entity e;
    @Nullable
    private final Boolean f;

    public CommandListenerWrapper(ICommandListener icommandlistener, @Nullable Vec3D vec3d, @Nullable BlockPosition blockposition, @Nullable Integer integer, @Nullable Entity entity, @Nullable Boolean obool) {
        this.base = icommandlistener;
        this.b = vec3d;
        this.c = blockposition;
        this.d = integer;
        this.e = entity;
        this.f = obool;
    }

    public static CommandListenerWrapper a(ICommandListener icommandlistener) {
        return icommandlistener instanceof CommandListenerWrapper ? (CommandListenerWrapper) icommandlistener : new CommandListenerWrapper(icommandlistener, (Vec3D) null, (BlockPosition) null, (Integer) null, (Entity) null, (Boolean) null);
    }

    public CommandListenerWrapper a(Entity entity, Vec3D vec3d) {
        return this.e == entity && Objects.equals(this.b, vec3d) ? this : new CommandListenerWrapper(this.base, vec3d, new BlockPosition(vec3d), this.d, entity, this.f);
    }

    public CommandListenerWrapper a(int i) {
        return this.d != null && this.d.intValue() <= i ? this : new CommandListenerWrapper(this.base, this.b, this.c, Integer.valueOf(i), this.e, this.f);
    }

    public CommandListenerWrapper a(boolean flag) {
        return this.f != null && (!this.f.booleanValue() || flag) ? this : new CommandListenerWrapper(this.base, this.b, this.c, this.d, this.e, Boolean.valueOf(flag));
    }

    public CommandListenerWrapper i() {
        return this.b != null ? this : new CommandListenerWrapper(this.base, this.d(), this.getChunkCoordinates(), this.d, this.e, this.f);
    }

    public String getName() {
        return this.e != null ? this.e.getName() : this.base.getName();
    }

    public IChatBaseComponent getScoreboardDisplayName() {
        return this.e != null ? this.e.getScoreboardDisplayName() : this.base.getScoreboardDisplayName();
    }

    public void sendMessage(IChatBaseComponent ichatbasecomponent) {
        if (this.f == null || this.f.booleanValue()) {
            this.base.sendMessage(ichatbasecomponent);
        }
    }

    public boolean a(int i, String s) {
        return this.d != null && this.d.intValue() < i ? false : this.base.a(i, s);
    }

    public BlockPosition getChunkCoordinates() {
        return this.c != null ? this.c : (this.e != null ? this.e.getChunkCoordinates() : this.base.getChunkCoordinates());
    }

    public Vec3D d() {
        return this.b != null ? this.b : (this.e != null ? this.e.d() : this.base.d());
    }

    public World getWorld() {
        return this.e != null ? this.e.getWorld() : this.base.getWorld();
    }

    @Nullable
    public Entity f() {
        return this.e != null ? this.e.f() : this.base.f();
    }

    public boolean getSendCommandFeedback() {
        return this.f != null ? this.f.booleanValue() : this.base.getSendCommandFeedback();
    }

    public void a(CommandObjectiveExecutor.EnumCommandResult commandobjectiveexecutor_enumcommandresult, int i) {
        if (this.e != null) {
            this.e.a(commandobjectiveexecutor_enumcommandresult, i);
        } else {
            this.base.a(commandobjectiveexecutor_enumcommandresult, i);
        }
    }

    @Nullable
    public MinecraftServer C_() {
        return this.base.C_();
    }
}
