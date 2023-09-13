package net.minecraft.network.protocol.game;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.stats.RecipeBookSettings;

public class PacketPlayOutRecipes implements Packet<PacketListenerPlayOut> {

    private PacketPlayOutRecipes.Action a;
    private List<MinecraftKey> b;
    private List<MinecraftKey> c;
    private RecipeBookSettings d;

    public PacketPlayOutRecipes() {}

    public PacketPlayOutRecipes(PacketPlayOutRecipes.Action packetplayoutrecipes_action, Collection<MinecraftKey> collection, Collection<MinecraftKey> collection1, RecipeBookSettings recipebooksettings) {
        this.a = packetplayoutrecipes_action;
        this.b = ImmutableList.copyOf(collection);
        this.c = ImmutableList.copyOf(collection1);
        this.d = recipebooksettings;
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = (PacketPlayOutRecipes.Action) packetdataserializer.a(PacketPlayOutRecipes.Action.class);
        this.d = RecipeBookSettings.a(packetdataserializer);
        int i = packetdataserializer.i();

        this.b = Lists.newArrayList();

        int j;

        for (j = 0; j < i; ++j) {
            this.b.add(packetdataserializer.p());
        }

        if (this.a == PacketPlayOutRecipes.Action.INIT) {
            i = packetdataserializer.i();
            this.c = Lists.newArrayList();

            for (j = 0; j < i; ++j) {
                this.c.add(packetdataserializer.p());
            }
        }

    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a((Enum) this.a);
        this.d.b(packetdataserializer);
        packetdataserializer.d(this.b.size());
        Iterator iterator = this.b.iterator();

        MinecraftKey minecraftkey;

        while (iterator.hasNext()) {
            minecraftkey = (MinecraftKey) iterator.next();
            packetdataserializer.a(minecraftkey);
        }

        if (this.a == PacketPlayOutRecipes.Action.INIT) {
            packetdataserializer.d(this.c.size());
            iterator = this.c.iterator();

            while (iterator.hasNext()) {
                minecraftkey = (MinecraftKey) iterator.next();
                packetdataserializer.a(minecraftkey);
            }
        }

    }

    public static enum Action {

        INIT, ADD, REMOVE;

        private Action() {}
    }
}
