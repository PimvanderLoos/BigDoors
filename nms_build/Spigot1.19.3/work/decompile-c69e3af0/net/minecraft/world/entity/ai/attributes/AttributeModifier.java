package net.minecraft.world.entity.ai.attributes;

import com.mojang.logging.LogUtils;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import org.slf4j.Logger;

public class AttributeModifier {

    private static final Logger LOGGER = LogUtils.getLogger();
    private final double amount;
    private final AttributeModifier.Operation operation;
    private final Supplier<String> nameGetter;
    private final UUID id;

    public AttributeModifier(String s, double d0, AttributeModifier.Operation attributemodifier_operation) {
        this(MathHelper.createInsecureUUID(RandomSource.createNewThreadLocalInstance()), () -> {
            return s;
        }, d0, attributemodifier_operation);
    }

    public AttributeModifier(UUID uuid, String s, double d0, AttributeModifier.Operation attributemodifier_operation) {
        this(uuid, () -> {
            return s;
        }, d0, attributemodifier_operation);
    }

    public AttributeModifier(UUID uuid, Supplier<String> supplier, double d0, AttributeModifier.Operation attributemodifier_operation) {
        this.id = uuid;
        this.nameGetter = supplier;
        this.amount = d0;
        this.operation = attributemodifier_operation;
    }

    public UUID getId() {
        return this.id;
    }

    public String getName() {
        return (String) this.nameGetter.get();
    }

    public AttributeModifier.Operation getOperation() {
        return this.operation;
    }

    public double getAmount() {
        return this.amount;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            AttributeModifier attributemodifier = (AttributeModifier) object;

            return Objects.equals(this.id, attributemodifier.id);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public String toString() {
        return "AttributeModifier{amount=" + this.amount + ", operation=" + this.operation + ", name='" + (String) this.nameGetter.get() + "', id=" + this.id + "}";
    }

    public NBTTagCompound save() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.putString("Name", this.getName());
        nbttagcompound.putDouble("Amount", this.amount);
        nbttagcompound.putInt("Operation", this.operation.toValue());
        nbttagcompound.putUUID("UUID", this.id);
        return nbttagcompound;
    }

    @Nullable
    public static AttributeModifier load(NBTTagCompound nbttagcompound) {
        try {
            UUID uuid = nbttagcompound.getUUID("UUID");
            AttributeModifier.Operation attributemodifier_operation = AttributeModifier.Operation.fromValue(nbttagcompound.getInt("Operation"));

            return new AttributeModifier(uuid, nbttagcompound.getString("Name"), nbttagcompound.getDouble("Amount"), attributemodifier_operation);
        } catch (Exception exception) {
            AttributeModifier.LOGGER.warn("Unable to create attribute: {}", exception.getMessage());
            return null;
        }
    }

    public static enum Operation {

        ADDITION(0), MULTIPLY_BASE(1), MULTIPLY_TOTAL(2);

        private static final AttributeModifier.Operation[] OPERATIONS = new AttributeModifier.Operation[]{AttributeModifier.Operation.ADDITION, AttributeModifier.Operation.MULTIPLY_BASE, AttributeModifier.Operation.MULTIPLY_TOTAL};
        private final int value;

        private Operation(int i) {
            this.value = i;
        }

        public int toValue() {
            return this.value;
        }

        public static AttributeModifier.Operation fromValue(int i) {
            if (i >= 0 && i < AttributeModifier.Operation.OPERATIONS.length) {
                return AttributeModifier.Operation.OPERATIONS[i];
            } else {
                throw new IllegalArgumentException("No operation with value " + i);
            }
        }
    }
}
