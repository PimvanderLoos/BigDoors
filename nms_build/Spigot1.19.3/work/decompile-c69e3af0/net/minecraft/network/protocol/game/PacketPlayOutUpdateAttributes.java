package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
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
        this.entityId = packetdataserializer.readVarInt();
        this.attributes = packetdataserializer.readList((packetdataserializer1) -> {
            MinecraftKey minecraftkey = packetdataserializer1.readResourceLocation();
            AttributeBase attributebase = (AttributeBase) BuiltInRegistries.ATTRIBUTE.get(minecraftkey);
            double d0 = packetdataserializer1.readDouble();
            List<AttributeModifier> list = packetdataserializer1.readList((packetdataserializer2) -> {
                return new AttributeModifier(packetdataserializer2.readUUID(), "Unknown synced attribute modifier", packetdataserializer2.readDouble(), AttributeModifier.Operation.fromValue(packetdataserializer2.readByte()));
            });

            return new PacketPlayOutUpdateAttributes.AttributeSnapshot(attributebase, d0, list);
        });
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.entityId);
        packetdataserializer.writeCollection(this.attributes, (packetdataserializer1, packetplayoutupdateattributes_attributesnapshot) -> {
            packetdataserializer1.writeResourceLocation(BuiltInRegistries.ATTRIBUTE.getKey(packetplayoutupdateattributes_attributesnapshot.getAttribute()));
            packetdataserializer1.writeDouble(packetplayoutupdateattributes_attributesnapshot.getBase());
            packetdataserializer1.writeCollection(packetplayoutupdateattributes_attributesnapshot.getModifiers(), (packetdataserializer2, attributemodifier) -> {
                packetdataserializer2.writeUUID(attributemodifier.getId());
                packetdataserializer2.writeDouble(attributemodifier.getAmount());
                packetdataserializer2.writeByte(attributemodifier.getOperation().toValue());
            });
        });
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleUpdateAttributes(this);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public List<PacketPlayOutUpdateAttributes.AttributeSnapshot> getValues() {
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

        public AttributeBase getAttribute() {
            return this.attribute;
        }

        public double getBase() {
            return this.base;
        }

        public Collection<AttributeModifier> getModifiers() {
            return this.modifiers;
        }
    }
}
