package net.minecraft.network.protocol.game;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.stats.RecipeBookSettings;

public class PacketPlayOutRecipes implements Packet<PacketListenerPlayOut> {

    private final PacketPlayOutRecipes.Action state;
    private final List<MinecraftKey> recipes;
    private final List<MinecraftKey> toHighlight;
    private final RecipeBookSettings bookSettings;

    public PacketPlayOutRecipes(PacketPlayOutRecipes.Action packetplayoutrecipes_action, Collection<MinecraftKey> collection, Collection<MinecraftKey> collection1, RecipeBookSettings recipebooksettings) {
        this.state = packetplayoutrecipes_action;
        this.recipes = ImmutableList.copyOf(collection);
        this.toHighlight = ImmutableList.copyOf(collection1);
        this.bookSettings = recipebooksettings;
    }

    public PacketPlayOutRecipes(PacketDataSerializer packetdataserializer) {
        this.state = (PacketPlayOutRecipes.Action) packetdataserializer.a(PacketPlayOutRecipes.Action.class);
        this.bookSettings = RecipeBookSettings.a(packetdataserializer);
        this.recipes = packetdataserializer.a(PacketDataSerializer::q);
        if (this.state == PacketPlayOutRecipes.Action.INIT) {
            this.toHighlight = packetdataserializer.a(PacketDataSerializer::q);
        } else {
            this.toHighlight = ImmutableList.of();
        }

    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a((Enum) this.state);
        this.bookSettings.b(packetdataserializer);
        packetdataserializer.a((Collection) this.recipes, PacketDataSerializer::a);
        if (this.state == PacketPlayOutRecipes.Action.INIT) {
            packetdataserializer.a((Collection) this.toHighlight, PacketDataSerializer::a);
        }

    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public List<MinecraftKey> b() {
        return this.recipes;
    }

    public List<MinecraftKey> c() {
        return this.toHighlight;
    }

    public RecipeBookSettings d() {
        return this.bookSettings;
    }

    public PacketPlayOutRecipes.Action e() {
        return this.state;
    }

    public static enum Action {

        INIT, ADD, REMOVE;

        private Action() {}
    }
}
