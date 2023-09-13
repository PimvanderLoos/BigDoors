package net.minecraft.world.item;

import net.minecraft.network.protocol.game.PacketPlayOutSetCooldown;
import net.minecraft.server.level.EntityPlayer;

public class ItemCooldownPlayer extends ItemCooldown {

    private final EntityPlayer player;

    public ItemCooldownPlayer(EntityPlayer entityplayer) {
        this.player = entityplayer;
    }

    @Override
    protected void onCooldownStarted(Item item, int i) {
        super.onCooldownStarted(item, i);
        this.player.connection.send(new PacketPlayOutSetCooldown(item, i));
    }

    @Override
    protected void onCooldownEnded(Item item) {
        super.onCooldownEnded(item);
        this.player.connection.send(new PacketPlayOutSetCooldown(item, 0));
    }
}
