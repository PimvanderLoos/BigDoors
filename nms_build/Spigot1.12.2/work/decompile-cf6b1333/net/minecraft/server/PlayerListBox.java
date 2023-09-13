package net.minecraft.server;

import java.util.Vector;
import javax.swing.JList;

public class PlayerListBox extends JList implements ITickable {

    private final MinecraftServer a;
    private int b;

    public PlayerListBox(MinecraftServer minecraftserver) {
        this.a = minecraftserver;
        minecraftserver.a((ITickable) this);
    }

    public void e() {
        if (this.b++ % 20 == 0) {
            Vector vector = new Vector();

            for (int i = 0; i < this.a.getPlayerList().v().size(); ++i) {
                vector.add(((EntityPlayer) this.a.getPlayerList().v().get(i)).getName());
            }

            this.setListData(vector);
        }

    }
}
