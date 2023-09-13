package net.minecraft.world.entity.ai.attributes;

import io.netty.util.internal.ThreadLocalRandom;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AttributeModifier {

    private static final Logger LOGGER = LogManager.getLogger();
    private final double b;
    private final AttributeModifier.Operation c;
    private final Supplier<String> d;
    private final UUID e;

    public AttributeModifier(String s, double d0, AttributeModifier.Operation attributemodifier_operation) {
        this(MathHelper.a((Random) ThreadLocalRandom.current()), () -> {
            return s;
        }, d0, attributemodifier_operation);
    }

    public AttributeModifier(UUID uuid, String s, double d0, AttributeModifier.Operation attributemodifier_operation) {
        this(uuid, () -> {
            return s;
        }, d0, attributemodifier_operation);
    }

    public AttributeModifier(UUID uuid, Supplier<String> supplier, double d0, AttributeModifier.Operation attributemodifier_operation) {
        this.e = uuid;
        this.d = supplier;
        this.b = d0;
        this.c = attributemodifier_operation;
    }

    public UUID getUniqueId() {
        return this.e;
    }

    public String getName() {
        return (String) this.d.get();
    }

    public AttributeModifier.Operation getOperation() {
        return this.c;
    }

    public double getAmount() {
        return this.b;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            AttributeModifier attributemodifier = (AttributeModifier) object;

            return Objects.equals(this.e, attributemodifier.e);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.e.hashCode();
    }

    public String toString() {
        return "AttributeModifier{amount=" + this.b + ", operation=" + this.c + ", name='" + (String) this.d.get() + '\'' + ", id=" + this.e + '}';
    }

    public NBTTagCompound save() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.setString("Name", this.getName());
        nbttagcompound.setDouble("Amount", this.b);
        nbttagcompound.setInt("Operation", this.c.a());
        nbttagcompound.a("UUID", this.e);
        return nbttagcompound;
    }

    @Nullable
    public static AttributeModifier a(NBTTagCompound nbttagcompound) {
        try {
            UUID uuid = nbttagcompound.a("UUID");
            AttributeModifier.Operation attributemodifier_operation = AttributeModifier.Operation.a(nbttagcompound.getInt("Operation"));

            return new AttributeModifier(uuid, nbttagcompound.getString("Name"), nbttagcompound.getDouble("Amount"), attributemodifier_operation);
        } catch (Exception exception) {
            AttributeModifier.LOGGER.warn("Unable to create attribute: {}", exception.getMessage());
            return null;
        }
    }

    public static enum Operation {

        ADDITION(0), MULTIPLY_BASE(1), MULTIPLY_TOTAL(2);

        private static final AttributeModifier.Operation[] d = new AttributeModifier.Operation[]{AttributeModifier.Operation.ADDITION, AttributeModifier.Operation.MULTIPLY_BASE, AttributeModifier.Operation.MULTIPLY_TOTAL};
        private final int e;

        private Operation(int i) {
            this.e = i;
        }

        public int a() {
            return this.e;
        }

        public static AttributeModifier.Operation a(int i) {
            if (i >= 0 && i < AttributeModifier.Operation.d.length) {
                return AttributeModifier.Operation.d[i];
            } else {
                throw new IllegalArgumentException("No operation with value " + i);
            }
        }
    }
}
