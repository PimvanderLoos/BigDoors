package net.minecraft.server;

public class RemoteControlCommandListener implements ICommandListener {

    private final StringBuffer a = new StringBuffer();
    private final MinecraftServer b;

    public RemoteControlCommandListener(MinecraftServer minecraftserver) {
        this.b = minecraftserver;
    }

    public void clearMessages() {
        this.a.setLength(0);
    }

    public String getMessages() {
        return this.a.toString();
    }

    public String getName() {
        return "Rcon";
    }

    public IChatBaseComponent getScoreboardDisplayName() {
        return new ChatComponentText(this.getName());
    }

    public void sendMessage(IChatBaseComponent ichatbasecomponent) {
        this.a.append(ichatbasecomponent.toPlainText());
    }

    public boolean a(int i, String s) {
        return true;
    }

    public BlockPosition getChunkCoordinates() {
        return BlockPosition.ZERO;
    }

    public Vec3D d() {
        return Vec3D.a;
    }

    public World getWorld() {
        return this.b.getWorld();
    }

    public Entity f() {
        return null;
    }

    public boolean getSendCommandFeedback() {
        return true;
    }

    public void a(CommandObjectiveExecutor.EnumCommandResult commandobjectiveexecutor_enumcommandresult, int i) {}

    public MinecraftServer B_() {
        return this.b;
    }
}
