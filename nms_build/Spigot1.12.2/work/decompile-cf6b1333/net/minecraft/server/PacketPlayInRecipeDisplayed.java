package net.minecraft.server;

import java.io.IOException;

public class PacketPlayInRecipeDisplayed implements Packet<PacketListenerPlayIn> {

    private PacketPlayInRecipeDisplayed.Status a;
    private IRecipe b;
    private boolean c;
    private boolean d;

    public PacketPlayInRecipeDisplayed() {}

    public PacketPlayInRecipeDisplayed(IRecipe irecipe) {
        this.a = PacketPlayInRecipeDisplayed.Status.SHOWN;
        this.b = irecipe;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = (PacketPlayInRecipeDisplayed.Status) packetdataserializer.a(PacketPlayInRecipeDisplayed.Status.class);
        if (this.a == PacketPlayInRecipeDisplayed.Status.SHOWN) {
            this.b = CraftingManager.a(packetdataserializer.readInt());
        } else if (this.a == PacketPlayInRecipeDisplayed.Status.SETTINGS) {
            this.c = packetdataserializer.readBoolean();
            this.d = packetdataserializer.readBoolean();
        }

    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a((Enum) this.a);
        if (this.a == PacketPlayInRecipeDisplayed.Status.SHOWN) {
            packetdataserializer.writeInt(CraftingManager.a(this.b));
        } else if (this.a == PacketPlayInRecipeDisplayed.Status.SETTINGS) {
            packetdataserializer.writeBoolean(this.c);
            packetdataserializer.writeBoolean(this.d);
        }

    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public PacketPlayInRecipeDisplayed.Status a() {
        return this.a;
    }

    public IRecipe b() {
        return this.b;
    }

    public boolean c() {
        return this.c;
    }

    public boolean d() {
        return this.d;
    }

    public static enum Status {

        SHOWN, SETTINGS;

        private Status() {}
    }
}
