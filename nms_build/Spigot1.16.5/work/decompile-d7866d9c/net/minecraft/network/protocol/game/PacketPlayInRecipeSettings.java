package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.inventory.RecipeBookType;

public class PacketPlayInRecipeSettings implements Packet<PacketListenerPlayIn> {

    private RecipeBookType a;
    private boolean b;
    private boolean c;

    public PacketPlayInRecipeSettings() {}

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = (RecipeBookType) packetdataserializer.a(RecipeBookType.class);
        this.b = packetdataserializer.readBoolean();
        this.c = packetdataserializer.readBoolean();
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a((Enum) this.a);
        packetdataserializer.writeBoolean(this.b);
        packetdataserializer.writeBoolean(this.c);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public RecipeBookType b() {
        return this.a;
    }

    public boolean c() {
        return this.b;
    }

    public boolean d() {
        return this.c;
    }
}
