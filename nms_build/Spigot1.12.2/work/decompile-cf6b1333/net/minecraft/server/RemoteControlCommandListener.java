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

    public void sendMessage(IChatBaseComponent ichatbasecomponent) {
        this.a.append(ichatbasecomponent.toPlainText());
    }

    public boolean a(int i, String s) {
        return true;
    }

    public World getWorld() {
        return this.b.getWorld();
    }

    public boolean getSendCommandFeedback() {
        return true;
    }

    public MinecraftServer C_() {
        return this.b;
    }
}
