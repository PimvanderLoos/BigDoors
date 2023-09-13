package net.minecraft.world.entity.ai.attributes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.IRegistry;

public class AttributeProvider {

    private final Map<AttributeBase, AttributeModifiable> a;

    public AttributeProvider(Map<AttributeBase, AttributeModifiable> map) {
        this.a = ImmutableMap.copyOf(map);
    }

    private AttributeModifiable d(AttributeBase attributebase) {
        AttributeModifiable attributemodifiable = (AttributeModifiable) this.a.get(attributebase);

        if (attributemodifiable == null) {
            throw new IllegalArgumentException("Can't find attribute " + IRegistry.ATTRIBUTE.getKey(attributebase));
        } else {
            return attributemodifiable;
        }
    }

    public double a(AttributeBase attributebase) {
        return this.d(attributebase).getValue();
    }

    public double b(AttributeBase attributebase) {
        return this.d(attributebase).getBaseValue();
    }

    public double a(AttributeBase attributebase, UUID uuid) {
        AttributeModifier attributemodifier = this.d(attributebase).a(uuid);

        if (attributemodifier == null) {
            throw new IllegalArgumentException("Can't find modifier " + uuid + " on attribute " + IRegistry.ATTRIBUTE.getKey(attributebase));
        } else {
            return attributemodifier.getAmount();
        }
    }

    @Nullable
    public AttributeModifiable a(Consumer<AttributeModifiable> consumer, AttributeBase attributebase) {
        AttributeModifiable attributemodifiable = (AttributeModifiable) this.a.get(attributebase);

        if (attributemodifiable == null) {
            return null;
        } else {
            AttributeModifiable attributemodifiable1 = new AttributeModifiable(attributebase, consumer);

            attributemodifiable1.a(attributemodifiable);
            return attributemodifiable1;
        }
    }

    public static AttributeProvider.Builder a() {
        return new AttributeProvider.Builder();
    }

    public boolean c(AttributeBase attributebase) {
        return this.a.containsKey(attributebase);
    }

    public boolean b(AttributeBase attributebase, UUID uuid) {
        AttributeModifiable attributemodifiable = (AttributeModifiable) this.a.get(attributebase);

        return attributemodifiable != null && attributemodifiable.a(uuid) != null;
    }

    public static class Builder {

        private final Map<AttributeBase, AttributeModifiable> a = Maps.newHashMap();
        private boolean b;

        public Builder() {}

        private AttributeModifiable b(AttributeBase attributebase) {
            AttributeModifiable attributemodifiable = new AttributeModifiable(attributebase, (attributemodifiable1) -> {
                if (this.b) {
                    throw new UnsupportedOperationException("Tried to change value for default attribute instance: " + IRegistry.ATTRIBUTE.getKey(attributebase));
                }
            });

            this.a.put(attributebase, attributemodifiable);
            return attributemodifiable;
        }

        public AttributeProvider.Builder a(AttributeBase attributebase) {
            this.b(attributebase);
            return this;
        }

        public AttributeProvider.Builder a(AttributeBase attributebase, double d0) {
            AttributeModifiable attributemodifiable = this.b(attributebase);

            attributemodifiable.setValue(d0);
            return this;
        }

        public AttributeProvider a() {
            this.b = true;
            return new AttributeProvider(this.a);
        }
    }
}
