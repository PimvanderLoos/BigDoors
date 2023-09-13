package net.minecraft.server;

public class ItemCooldownPlayer extends ItemCooldown {

    private final EntityPlayer a;

    public ItemCooldownPlayer(EntityPlayer entityplayer) {
        this.a = entityplayer;
    }

    protected void b(Item item, int i) {
        super.b(item, i);
        this.a.playerConnection.sendPacket(new PacketPlayOutSetCooldown(item, i));
    }

    protected void c(Item item) {
        super.c(item);
        this.a.playerConnection.sendPacket(new PacketPlayOutSetCooldown(item, 0));
    }
}
