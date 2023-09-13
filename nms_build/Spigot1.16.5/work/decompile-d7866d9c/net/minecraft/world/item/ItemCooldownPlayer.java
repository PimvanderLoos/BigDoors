package net.minecraft.world.item;

import net.minecraft.network.protocol.game.PacketPlayOutSetCooldown;
import net.minecraft.server.level.EntityPlayer;

public class ItemCooldownPlayer extends ItemCooldown {

    private final EntityPlayer a;

    public ItemCooldownPlayer(EntityPlayer entityplayer) {
        this.a = entityplayer;
    }

    @Override
    protected void b(Item item, int i) {
        super.b(item, i);
        this.a.playerConnection.sendPacket(new PacketPlayOutSetCooldown(item, i));
    }

    @Override
    protected void c(Item item) {
        super.c(item);
        this.a.playerConnection.sendPacket(new PacketPlayOutSetCooldown(item, 0));
    }
}
