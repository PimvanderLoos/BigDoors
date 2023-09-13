package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.MinecraftKey;

public class PacketPlayInAdvancements implements Packet<PacketListenerPlayIn> {

    private final PacketPlayInAdvancements.Status action;
    @Nullable
    private final MinecraftKey tab;

    public PacketPlayInAdvancements(PacketPlayInAdvancements.Status packetplayinadvancements_status, @Nullable MinecraftKey minecraftkey) {
        this.action = packetplayinadvancements_status;
        this.tab = minecraftkey;
    }

    public static PacketPlayInAdvancements openedTab(Advancement advancement) {
        return new PacketPlayInAdvancements(PacketPlayInAdvancements.Status.OPENED_TAB, advancement.getId());
    }

    public static PacketPlayInAdvancements closedScreen() {
        return new PacketPlayInAdvancements(PacketPlayInAdvancements.Status.CLOSED_SCREEN, (MinecraftKey) null);
    }

    public PacketPlayInAdvancements(PacketDataSerializer packetdataserializer) {
        this.action = (PacketPlayInAdvancements.Status) packetdataserializer.readEnum(PacketPlayInAdvancements.Status.class);
        if (this.action == PacketPlayInAdvancements.Status.OPENED_TAB) {
            this.tab = packetdataserializer.readResourceLocation();
        } else {
            this.tab = null;
        }

    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeEnum(this.action);
        if (this.action == PacketPlayInAdvancements.Status.OPENED_TAB) {
            packetdataserializer.writeResourceLocation(this.tab);
        }

    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleSeenAdvancements(this);
    }

    public PacketPlayInAdvancements.Status getAction() {
        return this.action;
    }

    @Nullable
    public MinecraftKey getTab() {
        return this.tab;
    }

    public static enum Status {

        OPENED_TAB, CLOSED_SCREEN;

        private Status() {}
    }
}
