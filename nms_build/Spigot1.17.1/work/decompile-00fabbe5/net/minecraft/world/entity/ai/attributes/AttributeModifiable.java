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
import net.minecraft.core.IRegistry;
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
        this.baseValue = attributebase.getDefault();
    }

    public AttributeBase getAttribute() {
        return this.attribute;
    }

    public double getBaseValue() {
        return this.baseValue;
    }

    public void setValue(double d0) {
        if (d0 != this.baseValue) {
            this.baseValue = d0;
            this.d();
        }
    }

    public Set<AttributeModifier> a(AttributeModifier.Operation attributemodifier_operation) {
        return (Set) this.modifiersByOperation.computeIfAbsent(attributemodifier_operation, (attributemodifier_operation1) -> {
            return Sets.newHashSet();
        });
    }

    public Set<AttributeModifier> getModifiers() {
        return ImmutableSet.copyOf(this.modifierById.values());
    }

    @Nullable
    public AttributeModifier a(UUID uuid) {
        return (AttributeModifier) this.modifierById.get(uuid);
    }

    public boolean a(AttributeModifier attributemodifier) {
        return this.modifierById.get(attributemodifier.getUniqueId()) != null;
    }

    private void e(AttributeModifier attributemodifier) {
        AttributeModifier attributemodifier1 = (AttributeModifier) this.modifierById.putIfAbsent(attributemodifier.getUniqueId(), attributemodifier);

        if (attributemodifier1 != null) {
            throw new IllegalArgumentException("Modifier is already applied on this attribute!");
        } else {
            this.a(attributemodifier.getOperation()).add(attributemodifier);
            this.d();
        }
    }

    public void b(AttributeModifier attributemodifier) {
        this.e(attributemodifier);
    }

    public void addModifier(AttributeModifier attributemodifier) {
        this.e(attributemodifier);
        this.permanentModifiers.add(attributemodifier);
    }

    protected void d() {
        this.dirty = true;
        this.onDirty.accept(this);
    }

    public void removeModifier(AttributeModifier attributemodifier) {
        this.a(attributemodifier.getOperation()).remove(attributemodifier);
        this.modifierById.remove(attributemodifier.getUniqueId());
        this.permanentModifiers.remove(attributemodifier);
        this.d();
    }

    public void b(UUID uuid) {
        AttributeModifier attributemodifier = this.a(uuid);

        if (attributemodifier != null) {
            this.removeModifier(attributemodifier);
        }

    }

    public boolean c(UUID uuid) {
        AttributeModifier attributemodifier = this.a(uuid);

        if (attributemodifier != null && this.permanentModifiers.contains(attributemodifier)) {
            this.removeModifier(attributemodifier);
            return true;
        } else {
            return false;
        }
    }

    public void e() {
        Iterator iterator = this.getModifiers().iterator();

        while (iterator.hasNext()) {
            AttributeModifier attributemodifier = (AttributeModifier) iterator.next();

            this.removeModifier(attributemodifier);
        }

    }

    public double getValue() {
        if (this.dirty) {
            this.cachedValue = this.h();
            this.dirty = false;
        }

        return this.cachedValue;
    }

    private double h() {
        double d0 = this.getBaseValue();

        AttributeModifier attributemodifier;

        for (Iterator iterator = this.b(AttributeModifier.Operation.ADDITION).iterator(); iterator.hasNext(); d0 += attributemodifier.getAmount()) {
            attributemodifier = (AttributeModifier) iterator.next();
        }

        double d1 = d0;

        AttributeModifier attributemodifier1;
        Iterator iterator1;

        for (iterator1 = this.b(AttributeModifier.Operation.MULTIPLY_BASE).iterator(); iterator1.hasNext(); d1 += d0 * attributemodifier1.getAmount()) {
            attributemodifier1 = (AttributeModifier) iterator1.next();
        }

        for (iterator1 = this.b(AttributeModifier.Operation.MULTIPLY_TOTAL).iterator(); iterator1.hasNext(); d1 *= 1.0D + attributemodifier1.getAmount()) {
            attributemodifier1 = (AttributeModifier) iterator1.next();
        }

        return this.attribute.a(d1);
    }

    private Collection<AttributeModifier> b(AttributeModifier.Operation attributemodifier_operation) {
        return (Collection) this.modifiersByOperation.getOrDefault(attributemodifier_operation, Collections.emptySet());
    }

    public void a(AttributeModifiable attributemodifiable) {
        this.baseValue = attributemodifiable.baseValue;
        this.modifierById.clear();
        this.modifierById.putAll(attributemodifiable.modifierById);
        this.permanentModifiers.clear();
        this.permanentModifiers.addAll(attributemodifiable.permanentModifiers);
        this.modifiersByOperation.clear();
        attributemodifiable.modifiersByOperation.forEach((attributemodifier_operation, set) -> {
            this.a(attributemodifier_operation).addAll(set);
        });
        this.d();
    }

    public NBTTagCompound g() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.setString("Name", IRegistry.ATTRIBUTE.getKey(this.attribute).toString());
        nbttagcompound.setDouble("Base", this.baseValue);
        if (!this.permanentModifiers.isEmpty()) {
            NBTTagList nbttaglist = new NBTTagList();
            Iterator iterator = this.permanentModifiers.iterator();

            while (iterator.hasNext()) {
                AttributeModifier attributemodifier = (AttributeModifier) iterator.next();

                nbttaglist.add(attributemodifier.save());
            }

            nbttagcompound.set("Modifiers", nbttaglist);
        }

        return nbttagcompound;
    }

    public void a(NBTTagCompound nbttagcompound) {
        this.baseValue = nbttagcompound.getDouble("Base");
        if (nbttagcompound.hasKeyOfType("Modifiers", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList("Modifiers", 10);

            for (int i = 0; i < nbttaglist.size(); ++i) {
                AttributeModifier attributemodifier = AttributeModifier.a(nbttaglist.getCompound(i));

                if (attributemodifier != null) {
                    this.modifierById.put(attributemodifier.getUniqueId(), attributemodifier);
                    this.a(attributemodifier.getOperation()).add(attributemodifier);
                    this.permanentModifiers.add(attributemodifier);
                }
            }
        }

        this.d();
    }
}
