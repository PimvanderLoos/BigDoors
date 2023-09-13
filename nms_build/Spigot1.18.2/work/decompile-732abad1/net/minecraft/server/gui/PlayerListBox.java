package net.minecraft.server.gui;

import java.util.Vector;
import javax.swing.JList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;

public class PlayerListBox extends JList<String> {

    private final MinecraftServer server;
    private int tickCount;

    public PlayerListBox(MinecraftServer minecraftserver) {
        this.server = minecraftserver;
        minecraftserver.addTickable(this::tick);
    }

    public void tick() {
        if (this.tickCount++ % 20 == 0) {
            Vector<String> vector = new Vector();

            for (int i = 0; i < this.server.getPlayerList().getPlayers().size(); ++i) {
                vector.add(((EntityPlayer) this.server.getPlayerList().getPlayers().get(i)).getGameProfile().getName());
            }

            this.setListData(vector);
        }

    }
}
