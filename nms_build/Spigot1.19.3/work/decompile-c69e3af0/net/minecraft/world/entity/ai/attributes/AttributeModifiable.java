package net.minecraft.world.entity.ai.attributes;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class AttributeModifiable {

    private final AttributeBase attribute;
    private final Map<AttributeModifier.Operation, Set<AttributeModifier>> modifiersByOperation = Maps.newEnumMap(AttributeModifier.Operation.class);
    private final Map<UUID, AttributeModifier> modifierById = new Object2ObjectArrayMap();
    private final Set<AttributeModifier> permanentModifiers = new ObjectArraySet();
    private double baseValue;
    private boolean dirty = true;
    private double cachedValue;
    private final Consumer<AttributeModifiable> onDirty;

    public AttributeModifiable(AttributeBase attributebase, Consumer<AttributeModifiable> consumer) {
        this.attribute = attributebase;
        this.onDirty = consumer;
        this.baseValue = attributebase.getDefaultValue();
    }

    public AttributeBase getAttribute() {
        return this.attribute;
    }

    public double getBaseValue() {
        return this.baseValue;
    }

    public void setBaseValue(double d0) {
        if (d0 != this.baseValue) {
            this.baseValue = d0;
            this.setDirty();
        }
    }

    public Set<AttributeModifier> getModifiers(AttributeModifier.Operation attributemodifier_operation) {
        return (Set) this.modifiersByOperation.computeIfAbsent(attributemodifier_operation, (attributemodifier_operation1) -> {
            return Sets.newHashSet();
        });
    }

    public Set<AttributeModifier> getModifiers() {
        return ImmutableSet.copyOf(this.modifierById.values());
    }

    @Nullable
    public AttributeModifier getModifier(UUID uuid) {
        return (AttributeModifier) this.modifierById.get(uuid);
    }

    public boolean hasModifier(AttributeModifier attributemodifier) {
        return this.modifierById.get(attributemodifier.getId()) != null;
    }

    private void addModifier(AttributeModifier attributemodifier) {
        AttributeModifier attributemodifier1 = (AttributeModifier) this.modifierById.putIfAbsent(attributemodifier.getId(), attributemodifier);

        if (attributemodifier1 != null) {
            throw new IllegalArgumentException("Modifier is already applied on this attribute!");
        } else {
            this.getModifiers(attributemodifier.getOperation()).add(attributemodifier);
            this.setDirty();
        }
    }

    public void addTransientModifier(AttributeModifier attributemodifier) {
        this.addModifier(attributemodifier);
    }

    public void addPermanentModifier(AttributeModifier attributemodifier) {
        this.addModifier(attributemodifier);
        this.permanentModifiers.add(attributemodifier);
    }

    protected void setDirty() {
        this.dirty = true;
        this.onDirty.accept(this);
    }

    public void removeModifier(AttributeModifier attributemodifier) {
        this.getModifiers(attributemodifier.getOperation()).remove(attributemodifier);
        this.modifierById.remove(attributemodifier.getId());
        this.permanentModifiers.remove(attributemodifier);
        this.setDirty();
    }

    public void removeModifier(UUID uuid) {
        AttributeModifier attributemodifier = this.getModifier(uuid);

        if (attributemodifier != null) {
            this.removeModifier(attributemodifier);
        }

    }

    public boolean removePermanentModifier(UUID uuid) {
        AttributeModifier attributemodifier = this.getModifier(uuid);

        if (attributemodifier != null && this.permanentModifiers.contains(attributemodifier)) {
            this.removeModifier(attributemodifier);
            return true;
        } else {
            return false;
        }
    }

    public void removeModifiers() {
        Iterator iterator = this.getModifiers().iterator();

        while (iterator.hasNext()) {
            AttributeModifier attributemodifier = (AttributeModifier) iterator.next();

            this.removeModifier(attributemodifier);
        }

    }

    public double getValue() {
        if (this.dirty) {
            this.cachedValue = this.calculateValue();
            this.dirty = false;
        }

        return this.cachedValue;
    }

    private double calculateValue() {
        double d0 = this.getBaseValue();

        AttributeModifier attributemodifier;

        for (Iterator iterator = this.getModifiersOrEmpty(AttributeModifier.Operation.ADDITION).iterator(); iterator.hasNext(); d0 += attributemodifier.getAmount()) {
            attributemodifier = (AttributeModifier) iterator.next();
        }

        double d1 = d0;

        AttributeModifier attributemodifier1;
        Iterator iterator1;

        for (iterator1 = this.getModifiersOrEmpty(AttributeModifier.Operation.MULTIPLY_BASE).iterator(); iterator1.hasNext(); d1 += d0 * attributemodifier1.getAmount()) {
            attributemodifier1 = (AttributeModifier) iterator1.next();
        }

        for (iterator1 = this.getModifiersOrEmpty(AttributeModifier.Operation.MULTIPLY_TOTAL).iterator(); iterator1.hasNext(); d1 *= 1.0D + attributemodifier1.getAmount()) {
            attributemodifier1 = (AttributeModifier) iterator1.next();
        }

        return this.attribute.sanitizeValue(d1);
    }

    private Collection<AttributeModifier> getModifiersOrEmpty(AttributeModifier.Operation attributemodifier_operation) {
        return (Collection) this.modifiersByOperation.getOrDefault(attributemodifier_operation, Collections.emptySet());
    }

    public void replaceFrom(AttributeModifiable attributemodifiable) {
        this.baseValue = attributemodifiable.baseValue;
        this.modifierById.clear();
        this.modifierById.putAll(attributemodifiable.modifierById);
        this.permanentModifiers.clear();
        this.permanentModifiers.addAll(attributemodifiable.permanentModifiers);
        this.modifiersByOperation.clear();
        attributemodifiable.modifiersByOperation.forEach((attributemodifier_operation, set) -> {
            this.getModifiers(attributemodifier_operation).addAll(set);
        });
        this.setDirty();
    }

    public NBTTagCompound save() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.putString("Name", BuiltInRegistries.ATTRIBUTE.getKey(this.attribute).toString());
        nbttagcompound.putDouble("Base", this.baseValue);
        if (!this.permanentModifiers.isEmpty()) {
            NBTTagList nbttaglist = new NBTTagList();
            Iterator iterator = this.permanentModifiers.iterator();

            while (iterator.hasNext()) {
                AttributeModifier attributemodifier = (AttributeModifier) iterator.next();

                nbttaglist.add(attributemodifier.save());
            }

            nbttagcompound.put("Modifiers", nbttaglist);
        }

        return nbttagcompound;
    }

    public void load(NBTTagCompound nbttagcompound) {
        this.baseValue = nbttagcompound.getDouble("Base");
        if (nbttagcompound.contains("Modifiers", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList("Modifiers", 10);

            for (int i = 0; i < nbttaglist.size(); ++i) {
                AttributeModifier attributemodifier = AttributeModifier.load(nbttaglist.getCompound(i));

                if (attributemodifier != null) {
                    this.modifierById.put(attributemodifier.getId(), attributemodifier);
                    this.getModifiers(attributemodifier.getOperation()).add(attributemodifier);
                    this.permanentModifiers.add(attributemodifier);
                }
            }
        }

        this.setDirty();
    }
}
