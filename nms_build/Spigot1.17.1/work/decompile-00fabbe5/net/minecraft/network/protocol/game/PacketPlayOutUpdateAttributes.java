package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.entity.ai.attributes.AttributeBase;
import net.minecraft.world.entity.ai.attributes.AttributeModifiable;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class PacketPlayOutUpdateAttributes implements Packet<PacketListenerPlayOut> {

    private final int entityId;
    private final List<PacketPlayOutUpdateAttributes.AttributeSnapshot> attributes;

    public PacketPlayOutUpdateAttributes(int i, Collection<AttributeModifiable> collection) {
        this.entityId = i;
        this.attributes = Lists.newArrayList();
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            AttributeModifiable attributemodifiable = (AttributeModifiable) iterator.next();

            this.attributes.add(new PacketPlayOutUpdateAttributes.AttributeSnapshot(attributemodifiable.getAttribute(), attributemodifiable.getBaseValue(), attributemodifiable.getModifiers()));
        }

    }

    public PacketPlayOutUpdateAttributes(PacketDataSerializer packetdataserializer) {
        this.entityId = packetdataserializer.j();
        this.attributes = packetdataserializer.a((packetdataserializer1) -> {
            MinecraftKey minecraftkey = packetdataserializer1.q();
            AttributeBase attributebase = (AttributeBase) IRegistry.ATTRIBUTE.get(minecraftkey);
            double d0 = packetdataserializer1.readDouble();
            List<AttributeModifier> list = packetdataserializer1.a((packetdataserializer2) -> {
                return new AttributeModifier(packetdataserializer2.l(), "Unknown synced attribute modifier", packetdataserializer2.readDouble(), AttributeModifier.Operation.a(packetdataserializer2.readByte()));
            });

            return new PacketPlayOutUpdateAttributes.AttributeSnapshot(attributebase, d0, list);
        });
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.entityId);
        packetdataserializer.a((Collection) this.attributes, (packetdataserializer1, packetplayoutupdateattributes_attributesnapshot) -> {
            packetdataserializer1.a(IRegistry.ATTRIBUTE.getKey(packetplayoutupdateattributes_attributesnapshot.a()));
            packetdataserializer1.writeDouble(packetplayoutupdateattributes_attributesnapshot.b());
            packetdataserializer1.a(packetplayoutupdateattributes_attributesnapshot.c(), (packetdataserializer2, attributemodifier) -> {
                packetdataserializer2.a(attributemodifier.getUniqueId());
                packetdataserializer2.writeDouble(attributemodifier.getAmount());
                packetdataserializer2.writeByte(attributemodifier.getOperation().a());
            });
        });
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public int b() {
        return this.entityId;
    }

    public List<PacketPlayOutUpdateAttributes.AttributeSnapshot> c() {
        return this.attributes;
    }

    public static class AttributeSnapshot {

        private final AttributeBase attribute;
        private final double base;
        private final Collection<AttributeModifier> modifiers;

        public AttributeSnapshot(AttributeBase attributebase, double d0, Collection<AttributeModifier> collection) {
            this.attribute = attributebase;
            this.base = d0;
            this.modifiers = collection;
        }

        public AttributeBase a() {
            return this.attribute;
        }

        public double b() {
            return this.base;
        }

        public Collection<AttributeModifier> c() {
            return this.modifiers;
        }
    }
}
