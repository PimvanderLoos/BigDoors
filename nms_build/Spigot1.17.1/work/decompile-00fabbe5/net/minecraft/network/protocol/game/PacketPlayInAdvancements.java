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

    public static PacketPlayInAdvancements a(Advancement advancement) {
        return new PacketPlayInAdvancements(PacketPlayInAdvancements.Status.OPENED_TAB, advancement.getName());
    }

    public static PacketPlayInAdvancements b() {
        return new PacketPlayInAdvancements(PacketPlayInAdvancements.Status.CLOSED_SCREEN, (MinecraftKey) null);
    }

    public PacketPlayInAdvancements(PacketDataSerializer packetdataserializer) {
        this.action = (PacketPlayInAdvancements.Status) packetdataserializer.a(PacketPlayInAdvancements.Status.class);
        if (this.action == PacketPlayInAdvancements.Status.OPENED_TAB) {
            this.tab = packetdataserializer.q();
        } else {
            this.tab = null;
        }

    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a((Enum) this.action);
        if (this.action == PacketPlayInAdvancements.Status.OPENED_TAB) {
            packetdataserializer.a(this.tab);
        }

    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public PacketPlayInAdvancements.Status c() {
        return this.action;
    }

    @Nullable
    public MinecraftKey d() {
        return this.tab;
    }

    public static enum Status {

        OPENED_TAB, CLOSED_SCREEN;

        private Status() {}
    }
}
