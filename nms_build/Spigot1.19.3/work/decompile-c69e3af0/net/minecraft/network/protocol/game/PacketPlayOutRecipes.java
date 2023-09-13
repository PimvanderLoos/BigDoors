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
        this.state = (PacketPlayOutRecipes.Action) packetdataserializer.readEnum(PacketPlayOutRecipes.Action.class);
        this.bookSettings = RecipeBookSettings.read(packetdataserializer);
        this.recipes = packetdataserializer.readList(PacketDataSerializer::readResourceLocation);
        if (this.state == PacketPlayOutRecipes.Action.INIT) {
            this.toHighlight = packetdataserializer.readList(PacketDataSerializer::readResourceLocation);
        } else {
            this.toHighlight = ImmutableList.of();
        }

    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeEnum(this.state);
        this.bookSettings.write(packetdataserializer);
        packetdataserializer.writeCollection(this.recipes, PacketDataSerializer::writeResourceLocation);
        if (this.state == PacketPlayOutRecipes.Action.INIT) {
            packetdataserializer.writeCollection(this.toHighlight, PacketDataSerializer::writeResourceLocation);
        }

    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleAddOrRemoveRecipes(this);
    }

    public List<MinecraftKey> getRecipes() {
        return this.recipes;
    }

    public List<MinecraftKey> getHighlights() {
        return this.toHighlight;
    }

    public RecipeBookSettings getBookSettings() {
        return this.bookSettings;
    }

    public PacketPlayOutRecipes.Action getState() {
        return this.state;
    }

    public static enum Action {

        INIT, ADD, REMOVE;

        private Action() {}
    }
}
