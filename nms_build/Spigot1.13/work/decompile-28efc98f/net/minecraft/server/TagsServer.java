package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;

public class TagsServer<T> extends Tags<T> {

    private final RegistryMaterials<MinecraftKey, T> a;

    public TagsServer(RegistryMaterials<MinecraftKey, T> registrymaterials, String s, String s1) {
        super(registrymaterials::d, registrymaterials::get, s, false, s1);
        this.a = registrymaterials;
    }

    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.c().size());
        Iterator iterator = this.c().entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            packetdataserializer.a((MinecraftKey) entry.getKey());
            packetdataserializer.d(((Tag) entry.getValue()).a().size());
            Iterator iterator1 = ((Tag) entry.getValue()).a().iterator();

            while (iterator1.hasNext()) {
                Object object = iterator1.next();

                packetdataserializer.d(this.a.a(object));
            }
        }

    }

    public void b(PacketDataSerializer packetdataserializer) {
        int i = packetdataserializer.g();

        for (int j = 0; j < i; ++j) {
            MinecraftKey minecraftkey = packetdataserializer.l();
            int k = packetdataserializer.g();
            ArrayList arraylist = Lists.newArrayList();

            for (int l = 0; l < k; ++l) {
                arraylist.add(this.a.getId(packetdataserializer.g()));
            }

            this.c().put(minecraftkey, Tag.a.a().a((Collection) arraylist).b(minecraftkey));
        }

    }
}
