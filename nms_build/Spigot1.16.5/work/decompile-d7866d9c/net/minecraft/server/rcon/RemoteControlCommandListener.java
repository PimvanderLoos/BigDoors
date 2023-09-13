package net.minecraft.server.rcon;

import java.util.UUID;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICommandListener;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2F;
import net.minecraft.world.phys.Vec3D;

public class RemoteControlCommandListener implements ICommandListener {

    private static final ChatComponentText b = new ChatComponentText("Rcon");
    private final StringBuffer buffer = new StringBuffer();
    private final MinecraftServer server;

    public RemoteControlCommandListener(MinecraftServer minecraftserver) {
        this.server = minecraftserver;
    }

    public void clearMessages() {
        this.buffer.setLength(0);
    }

    public String getMessages() {
        return this.buffer.toString();
    }

    public CommandListenerWrapper getWrapper() {
        WorldServer worldserver = this.server.E();

        return new CommandListenerWrapper(this, Vec3D.b((BaseBlockPosition) worldserver.getSpawn()), Vec2F.a, worldserver, 4, "Rcon", RemoteControlCommandListener.b, this.server, (Entity) null);
    }

    @Override
    public void sendMessage(IChatBaseComponent ichatbasecomponent, UUID uuid) {
        this.buffer.append(ichatbasecomponent.getString());
    }

    @Override
    public boolean shouldSendSuccess() {
        return true;
    }

    @Override
    public boolean shouldSendFailure() {
        return true;
    }

    @Override
    public boolean shouldBroadcastCommands() {
        return this.server.i();
    }
}
