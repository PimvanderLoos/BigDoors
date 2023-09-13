package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.entity.ai.attributes.AttributeBase;
import net.minecraft.world.entity.ai.attributes.AttributeModifiable;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class PacketPlayOutUpdateAttributes implements Packet<PacketListenerPlayOut> {

    private int a;
    private final List<PacketPlayOutUpdateAttributes.AttributeSnapshot> b = Lists.newArrayList();

    public PacketPlayOutUpdateAttributes() {}

    public PacketPlayOutUpdateAttributes(int i, Collection<AttributeModifiable> collection) {
        this.a = i;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            AttributeModifiable attributemodifiable = (AttributeModifiable) iterator.next();

            this.b.add(new PacketPlayOutUpdateAttributes.AttributeSnapshot(attributemodifiable.getAttribute(), attributemodifiable.getBaseValue(), attributemodifiable.getModifiers()));
        }

    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.i();
        int i = packetdataserializer.readInt();

        for (int j = 0; j < i; ++j) {
            MinecraftKey minecraftkey = packetdataserializer.p();
            AttributeBase attributebase = (AttributeBase) IRegistry.ATTRIBUTE.get(minecraftkey);
            double d0 = packetdataserializer.readDouble();
            List<AttributeModifier> list = Lists.newArrayList();
            int k = packetdataserializer.i();

            for (int l = 0; l < k; ++l) {
                UUID uuid = packetdataserializer.k();

                list.add(new AttributeModifier(uuid, "Unknown synced attribute modifier", packetdataserializer.readDouble(), AttributeModifier.Operation.a(packetdataserializer.readByte())));
            }

            this.b.add(new PacketPlayOutUpdateAttributes.AttributeSnapshot(attributebase, d0, list));
        }

    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.d(this.a);
        packetdataserializer.writeInt(this.b.size());
        Iterator iterator = this.b.iterator();

        while (iterator.hasNext()) {
            PacketPlayOutUpdateAttributes.AttributeSnapshot packetplayoutupdateattributes_attributesnapshot = (PacketPlayOutUpdateAttributes.AttributeSnapshot) iterator.next();

            packetdataserializer.a(IRegistry.ATTRIBUTE.getKey(packetplayoutupdateattributes_attributesnapshot.a()));
            packetdataserializer.writeDouble(packetplayoutupdateattributes_attributesnapshot.b());
            packetdataserializer.d(packetplayoutupdateattributes_attributesnapshot.c().size());
            Iterator iterator1 = packetplayoutupdateattributes_attributesnapshot.c().iterator();

            while (iterator1.hasNext()) {
                AttributeModifier attributemodifier = (AttributeModifier) iterator1.next();

                packetdataserializer.a(attributemodifier.getUniqueId());
                packetdataserializer.writeDouble(attributemodifier.getAmount());
                packetdataserializer.writeByte(attributemodifier.getOperation().a());
            }
        }

    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public class AttributeSnapshot {

        private final AttributeBase b;
        private final double c;
        private final Collection<AttributeModifier> d;

        public AttributeSnapshot(AttributeBase attributebase, double d0, Collection collection) {
            this.b = attributebase;
            this.c = d0;
            this.d = collection;
        }

        public AttributeBase a() {
            return this.b;
        }

        public double b() {
            return this.c;
        }

        public Collection<AttributeModifier> c() {
            return this.d;
        }
    }
}
