package net.minecraft.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class PacketPlayOutRecipeUpdate implements Packet<PacketListenerPlayOut> {

    private List<IRecipe> a;

    public PacketPlayOutRecipeUpdate() {}

    public PacketPlayOutRecipeUpdate(Collection<IRecipe> collection) {
        this.a = Lists.newArrayList(collection);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = Lists.newArrayList();
        int i = packetdataserializer.g();

        for (int j = 0; j < i; ++j) {
            this.a.add(RecipeSerializers.a(packetdataserializer));
        }

    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.d(this.a.size());
        Iterator iterator = this.a.iterator();

        while (iterator.hasNext()) {
            IRecipe irecipe = (IRecipe) iterator.next();

            RecipeSerializers.a(irecipe, packetdataserializer);
        }

    }
}
