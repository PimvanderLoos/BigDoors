package net.minecraft.server;

import java.io.IOException;

public class PacketPlayInRecipeDisplayed implements Packet<PacketListenerPlayIn> {

    private PacketPlayInRecipeDisplayed.Status a;
    private MinecraftKey b;
    private boolean c;
    private boolean d;
    private boolean e;
    private boolean f;

    public PacketPlayInRecipeDisplayed() {}

    public PacketPlayInRecipeDisplayed(IRecipe irecipe) {
        this.a = PacketPlayInRecipeDisplayed.Status.SHOWN;
        this.b = irecipe.getKey();
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = (PacketPlayInRecipeDisplayed.Status) packetdataserializer.a(PacketPlayInRecipeDisplayed.Status.class);
        if (this.a == PacketPlayInRecipeDisplayed.Status.SHOWN) {
            this.b = packetdataserializer.l();
        } else if (this.a == PacketPlayInRecipeDisplayed.Status.SETTINGS) {
            this.c = packetdataserializer.readBoolean();
            this.d = packetdataserializer.readBoolean();
            this.e = packetdataserializer.readBoolean();
            this.f = packetdataserializer.readBoolean();
        }

    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a((Enum) this.a);
        if (this.a == PacketPlayInRecipeDisplayed.Status.SHOWN) {
            packetdataserializer.a(this.b);
        } else if (this.a == PacketPlayInRecipeDisplayed.Status.SETTINGS) {
            packetdataserializer.writeBoolean(this.c);
            packetdataserializer.writeBoolean(this.d);
            packetdataserializer.writeBoolean(this.e);
            packetdataserializer.writeBoolean(this.f);
        }

    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public PacketPlayInRecipeDisplayed.Status b() {
        return this.a;
    }

    public MinecraftKey c() {
        return this.b;
    }

    public boolean d() {
        return this.c;
    }

    public boolean e() {
        return this.d;
    }

    public boolean f() {
        return this.e;
    }

    public boolean g() {
        return this.f;
    }

    public static enum Status {

        SHOWN, SETTINGS;

        private Status() {}
    }
}
