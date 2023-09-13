package net.minecraft.server;

import io.netty.buffer.Unpooled;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public class CommandStopSound extends CommandAbstract {

    public CommandStopSound() {}

    public String getCommand() {
        return "stopsound";
    }

    public int a() {
        return 2;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.stopsound.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (astring.length >= 1 && astring.length <= 3) {
            byte b0 = 0;
            int i = b0 + 1;
            EntityPlayer entityplayer = b(minecraftserver, icommandlistener, astring[b0]);
            String s = "";
            String s1 = "";

            if (astring.length >= 2) {
                String s2 = astring[i++];
                SoundCategory soundcategory = SoundCategory.a(s2);

                if (soundcategory == null) {
                    throw new CommandException("commands.stopsound.unknownSoundSource", new Object[] { s2});
                }

                s = soundcategory.a();
            }

            if (astring.length == 3) {
                s1 = astring[i++];
            }

            PacketDataSerializer packetdataserializer = new PacketDataSerializer(Unpooled.buffer());

            packetdataserializer.a(s);
            packetdataserializer.a(s1);
            entityplayer.playerConnection.sendPacket(new PacketPlayOutCustomPayload("MC|StopSound", packetdataserializer));
            if (s.isEmpty() && s1.isEmpty()) {
                a(icommandlistener, (ICommand) this, "commands.stopsound.success.all", new Object[] { entityplayer.getName()});
            } else if (s1.isEmpty()) {
                a(icommandlistener, (ICommand) this, "commands.stopsound.success.soundSource", new Object[] { s, entityplayer.getName()});
            } else {
                a(icommandlistener, (ICommand) this, "commands.stopsound.success.individualSound", new Object[] { s1, s, entityplayer.getName()});
            }

        } else {
            throw new ExceptionUsage(this.getUsage(icommandlistener), new Object[0]);
        }
    }

    public List<String> tabComplete(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring, @Nullable BlockPosition blockposition) {
        return astring.length == 1 ? a(astring, minecraftserver.getPlayers()) : (astring.length == 2 ? a(astring, (Collection) SoundCategory.b()) : (astring.length == 3 ? a(astring, (Collection) SoundEffect.a.keySet()) : Collections.emptyList()));
    }

    public boolean isListStart(String[] astring, int i) {
        return i == 0;
    }
}
