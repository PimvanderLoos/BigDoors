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

    private final AttributeBase a;
    private final Map<AttributeModifier.Operation, Set<AttributeModifier>> b = Maps.newEnumMap(AttributeModifier.Operation.class);
    private final Map<UUID, AttributeModifier> c = new Object2ObjectArrayMap();
    private final Set<AttributeModifier> d = new ObjectArraySet();
    private double e;
    private boolean f = true;
    private double g;
    private final Consumer<AttributeModifiable> h;

    public AttributeModifiable(AttributeBase attributebase, Consumer<AttributeModifiable> consumer) {
        this.a = attributebase;
        this.h = consumer;
        this.e = attributebase.getDefault();
    }

    public AttributeBase getAttribute() {
        return this.a;
    }

    public double getBaseValue() {
        return this.e;
    }

    public void setValue(double d0) {
        if (d0 != this.e) {
            this.e = d0;
            this.d();
        }
    }

    public Set<AttributeModifier> a(AttributeModifier.Operation attributemodifier_operation) {
        return (Set) this.b.computeIfAbsent(attributemodifier_operation, (attributemodifier_operation1) -> {
            return Sets.newHashSet();
        });
    }

    public Set<AttributeModifier> getModifiers() {
        return ImmutableSet.copyOf(this.c.values());
    }

    @Nullable
    public AttributeModifier a(UUID uuid) {
        return (AttributeModifier) this.c.get(uuid);
    }

    public boolean a(AttributeModifier attributemodifier) {
        return this.c.get(attributemodifier.getUniqueId()) != null;
    }

    private void e(AttributeModifier attributemodifier) {
        AttributeModifier attributemodifier1 = (AttributeModifier) this.c.putIfAbsent(attributemodifier.getUniqueId(), attributemodifier);

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
        this.d.add(attributemodifier);
    }

    protected void d() {
        this.f = true;
        this.h.accept(this);
    }

    public void removeModifier(AttributeModifier attributemodifier) {
        this.a(attributemodifier.getOperation()).remove(attributemodifier);
        this.c.remove(attributemodifier.getUniqueId());
        this.d.remove(attributemodifier);
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

        if (attributemodifier != null && this.d.contains(attributemodifier)) {
            this.removeModifier(attributemodifier);
            return true;
        } else {
            return false;
        }
    }

    public double getValue() {
        if (this.f) {
            this.g = this.h();
            this.f = false;
        }

        return this.g;
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

        return this.a.a(d1);
    }

    private Collection<AttributeModifier> b(AttributeModifier.Operation attributemodifier_operation) {
        return (Collection) this.b.getOrDefault(attributemodifier_operation, Collections.emptySet());
    }

    public void a(AttributeModifiable attributemodifiable) {
        this.e = attributemodifiable.e;
        this.c.clear();
        this.c.putAll(attributemodifiable.c);
        this.d.clear();
        this.d.addAll(attributemodifiable.d);
        this.b.clear();
        attributemodifiable.b.forEach((attributemodifier_operation, set) -> {
            this.a(attributemodifier_operation).addAll(set);
        });
        this.d();
    }

    public NBTTagCompound g() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.setString("Name", IRegistry.ATTRIBUTE.getKey(this.a).toString());
        nbttagcompound.setDouble("Base", this.e);
        if (!this.d.isEmpty()) {
            NBTTagList nbttaglist = new NBTTagList();
            Iterator iterator = this.d.iterator();

            while (iterator.hasNext()) {
                AttributeModifier attributemodifier = (AttributeModifier) iterator.next();

                nbttaglist.add(attributemodifier.save());
            }

            nbttagcompound.set("Modifiers", nbttaglist);
        }

        return nbttagcompound;
    }

    public void a(NBTTagCompound nbttagcompound) {
        this.e = nbttagcompound.getDouble("Base");
        if (nbttagcompound.hasKeyOfType("Modifiers", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList("Modifiers", 10);

            for (int i = 0; i < nbttaglist.size(); ++i) {
                AttributeModifier attributemodifier = AttributeModifier.a(nbttaglist.getCompound(i));

                if (attributemodifier != null) {
                    this.c.put(attributemodifier.getUniqueId(), attributemodifier);
                    this.a(attributemodifier.getOperation()).add(attributemodifier);
                    this.d.add(attributemodifier);
                }
            }
        }

        this.d();
    }
}
