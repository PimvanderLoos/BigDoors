package net.minecraft.world.entity.ai.attributes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;

public class AttributeProvider {

    private final Map<AttributeBase, AttributeModifiable> instances;

    public AttributeProvider(Map<AttributeBase, AttributeModifiable> map) {
        this.instances = ImmutableMap.copyOf(map);
    }

    private AttributeModifiable getAttributeInstance(AttributeBase attributebase) {
        AttributeModifiable attributemodifiable = (AttributeModifiable) this.instances.get(attributebase);

        if (attributemodifiable == null) {
            throw new IllegalArgumentException("Can't find attribute " + BuiltInRegistries.ATTRIBUTE.getKey(attributebase));
        } else {
            return attributemodifiable;
        }
    }

    public double getValue(AttributeBase attributebase) {
        return this.getAttributeInstance(attributebase).getValue();
    }

    public double getBaseValue(AttributeBase attributebase) {
        return this.getAttributeInstance(attributebase).getBaseValue();
    }

    public double getModifierValue(AttributeBase attributebase, UUID uuid) {
        AttributeModifier attributemodifier = this.getAttributeInstance(attributebase).getModifier(uuid);

        if (attributemodifier == null) {
            throw new IllegalArgumentException("Can't find modifier " + uuid + " on attribute " + BuiltInRegistries.ATTRIBUTE.getKey(attributebase));
        } else {
            return attributemodifier.getAmount();
        }
    }

    @Nullable
    public AttributeModifiable createInstance(Consumer<AttributeModifiable> consumer, AttributeBase attributebase) {
        AttributeModifiable attributemodifiable = (AttributeModifiable) this.instances.get(attributebase);

        if (attributemodifiable == null) {
            return null;
        } else {
            AttributeModifiable attributemodifiable1 = new AttributeModifiable(attributebase, consumer);

            attributemodifiable1.replaceFrom(attributemodifiable);
            return attributemodifiable1;
        }
    }

    public static AttributeProvider.Builder builder() {
        return new AttributeProvider.Builder();
    }

    public boolean hasAttribute(AttributeBase attributebase) {
        return this.instances.containsKey(attributebase);
    }

    public boolean hasModifier(AttributeBase attributebase, UUID uuid) {
        AttributeModifiable attributemodifiable = (AttributeModifiable) this.instances.get(attributebase);

        return attributemodifiable != null && attributemodifiable.getModifier(uuid) != null;
    }

    public static class Builder {

        private final Map<AttributeBase, AttributeModifiable> builder = Maps.newHashMap();
        private boolean instanceFrozen;

        public Builder() {}

        private AttributeModifiable create(AttributeBase attributebase) {
            AttributeModifiable attributemodifiable = new AttributeModifiable(attributebase, (attributemodifiable1) -> {
                if (this.instanceFrozen) {
                    throw new UnsupportedOperationException("Tried to change value for default attribute instance: " + BuiltInRegistries.ATTRIBUTE.getKey(attributebase));
                }
            });

            this.builder.put(attributebase, attributemodifiable);
            return attributemodifiable;
        }

        public AttributeProvider.Builder add(AttributeBase attributebase) {
            this.create(attributebase);
            return this;
        }

        public AttributeProvider.Builder add(AttributeBase attributebase, double d0) {
            AttributeModifiable attributemodifiable = this.create(attributebase);

            attributemodifiable.setBaseValue(d0);
            return this;
        }

        public AttributeProvider build() {
            this.instanceFrozen = true;
            return new AttributeProvider(this.builder);
        }
    }
}
