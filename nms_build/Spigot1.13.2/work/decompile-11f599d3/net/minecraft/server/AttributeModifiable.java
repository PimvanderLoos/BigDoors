package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

public class AttributeModifiable implements AttributeInstance {

    private final AttributeMapBase a;
    private final IAttribute b;
    private final Map<Integer, Set<AttributeModifier>> c = Maps.newHashMap();
    private final Map<String, Set<AttributeModifier>> d = Maps.newHashMap();
    private final Map<UUID, AttributeModifier> e = Maps.newHashMap();
    private double f;
    private boolean g = true;
    private double h;

    public AttributeModifiable(AttributeMapBase attributemapbase, IAttribute iattribute) {
        this.a = attributemapbase;
        this.b = iattribute;
        this.f = iattribute.getDefault();

        for (int i = 0; i < 3; ++i) {
            this.c.put(i, Sets.newHashSet());
        }

    }

    public IAttribute getAttribute() {
        return this.b;
    }

    public double b() {
        return this.f;
    }

    public void setValue(double d0) {
        if (d0 != this.b()) {
            this.f = d0;
            this.f();
        }
    }

    public Collection<AttributeModifier> a(int i) {
        return (Collection) this.c.get(i);
    }

    public Collection<AttributeModifier> c() {
        Set<AttributeModifier> set = Sets.newHashSet();

        for (int i = 0; i < 3; ++i) {
            set.addAll(this.a(i));
        }

        return set;
    }

    @Nullable
    public AttributeModifier a(UUID uuid) {
        return (AttributeModifier) this.e.get(uuid);
    }

    public boolean a(AttributeModifier attributemodifier) {
        return this.e.get(attributemodifier.a()) != null;
    }

    public void b(AttributeModifier attributemodifier) {
        if (this.a(attributemodifier.a()) != null) {
            throw new IllegalArgumentException("Modifier is already applied on this attribute!");
        } else {
            Set<AttributeModifier> set = (Set) this.d.get(attributemodifier.b());

            if (set == null) {
                set = Sets.newHashSet();
                this.d.put(attributemodifier.b(), set);
            }

            ((Set) this.c.get(attributemodifier.c())).add(attributemodifier);
            ((Set) set).add(attributemodifier);
            this.e.put(attributemodifier.a(), attributemodifier);
            this.f();
        }
    }

    protected void f() {
        this.g = true;
        this.a.a((AttributeInstance) this);
    }

    public void c(AttributeModifier attributemodifier) {
        for (int i = 0; i < 3; ++i) {
            Set<AttributeModifier> set = (Set) this.c.get(i);

            set.remove(attributemodifier);
        }

        Set<AttributeModifier> set1 = (Set) this.d.get(attributemodifier.b());

        if (set1 != null) {
            set1.remove(attributemodifier);
            if (set1.isEmpty()) {
                this.d.remove(attributemodifier.b());
            }
        }

        this.e.remove(attributemodifier.a());
        this.f();
    }

    public void b(UUID uuid) {
        AttributeModifier attributemodifier = this.a(uuid);

        if (attributemodifier != null) {
            this.c(attributemodifier);
        }

    }

    public double getValue() {
        if (this.g) {
            this.h = this.g();
            this.g = false;
        }

        return this.h;
    }

    private double g() {
        double d0 = this.b();

        AttributeModifier attributemodifier;

        for (Iterator iterator = this.b(0).iterator(); iterator.hasNext(); d0 += attributemodifier.d()) {
            attributemodifier = (AttributeModifier) iterator.next();
        }

        double d1 = d0;

        AttributeModifier attributemodifier1;
        Iterator iterator1;

        for (iterator1 = this.b(1).iterator(); iterator1.hasNext(); d1 += d0 * attributemodifier1.d()) {
            attributemodifier1 = (AttributeModifier) iterator1.next();
        }

        for (iterator1 = this.b(2).iterator(); iterator1.hasNext(); d1 *= 1.0D + attributemodifier1.d()) {
            attributemodifier1 = (AttributeModifier) iterator1.next();
        }

        return this.b.a(d1);
    }

    private Collection<AttributeModifier> b(int i) {
        Set<AttributeModifier> set = Sets.newHashSet(this.a(i));

        for (IAttribute iattribute = this.b.d(); iattribute != null; iattribute = iattribute.d()) {
            AttributeInstance attributeinstance = this.a.a(iattribute);

            if (attributeinstance != null) {
                set.addAll(attributeinstance.a(i));
            }
        }

        return set;
    }
}
