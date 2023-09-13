package net.minecraft.world.entity.ai.attributes;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.resources.MinecraftKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AttributeMapBase {

    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<AttributeBase, AttributeModifiable> attributes = Maps.newHashMap();
    private final Set<AttributeModifiable> dirtyAttributes = Sets.newHashSet();
    private final AttributeProvider supplier;

    public AttributeMapBase(AttributeProvider attributeprovider) {
        this.supplier = attributeprovider;
    }

    private void a(AttributeModifiable attributemodifiable) {
        if (attributemodifiable.getAttribute().b()) {
            this.dirtyAttributes.add(attributemodifiable);
        }

    }

    public Set<AttributeModifiable> getAttributes() {
        return this.dirtyAttributes;
    }

    public Collection<AttributeModifiable> b() {
        return (Collection) this.attributes.values().stream().filter((attributemodifiable) -> {
            return attributemodifiable.getAttribute().b();
        }).collect(Collectors.toList());
    }

    @Nullable
    public AttributeModifiable a(AttributeBase attributebase) {
        return (AttributeModifiable) this.attributes.computeIfAbsent(attributebase, (attributebase1) -> {
            return this.supplier.a(this::a, attributebase1);
        });
    }

    public boolean b(AttributeBase attributebase) {
        return this.attributes.get(attributebase) != null || this.supplier.c(attributebase);
    }

    public boolean a(AttributeBase attributebase, UUID uuid) {
        AttributeModifiable attributemodifiable = (AttributeModifiable) this.attributes.get(attributebase);

        return attributemodifiable != null ? attributemodifiable.a(uuid) != null : this.supplier.b(attributebase, uuid);
    }

    public double c(AttributeBase attributebase) {
        AttributeModifiable attributemodifiable = (AttributeModifiable) this.attributes.get(attributebase);

        return attributemodifiable != null ? attributemodifiable.getValue() : this.supplier.a(attributebase);
    }

    public double d(AttributeBase attributebase) {
        AttributeModifiable attributemodifiable = (AttributeModifiable) this.attributes.get(attributebase);

        return attributemodifiable != null ? attributemodifiable.getBaseValue() : this.supplier.b(attributebase);
    }

    public double b(AttributeBase attributebase, UUID uuid) {
        AttributeModifiable attributemodifiable = (AttributeModifiable) this.attributes.get(attributebase);

        return attributemodifiable != null ? attributemodifiable.a(uuid).getAmount() : this.supplier.a(attributebase, uuid);
    }

    public void a(Multimap<AttributeBase, AttributeModifier> multimap) {
        multimap.asMap().forEach((attributebase, collection) -> {
            AttributeModifiable attributemodifiable = (AttributeModifiable) this.attributes.get(attributebase);

            if (attributemodifiable != null) {
                Objects.requireNonNull(attributemodifiable);
                collection.forEach(attributemodifiable::removeModifier);
            }

        });
    }

    public void b(Multimap<AttributeBase, AttributeModifier> multimap) {
        multimap.forEach((attributebase, attributemodifier) -> {
            AttributeModifiable attributemodifiable = this.a(attributebase);

            if (attributemodifiable != null) {
                attributemodifiable.removeModifier(attributemodifier);
                attributemodifiable.b(attributemodifier);
            }

        });
    }

    public void a(AttributeMapBase attributemapbase) {
        attributemapbase.attributes.values().forEach((attributemodifiable) -> {
            AttributeModifiable attributemodifiable1 = this.a(attributemodifiable.getAttribute());

            if (attributemodifiable1 != null) {
                attributemodifiable1.a(attributemodifiable);
            }

        });
    }

    public NBTTagList c() {
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = this.attributes.values().iterator();

        while (iterator.hasNext()) {
            AttributeModifiable attributemodifiable = (AttributeModifiable) iterator.next();

            nbttaglist.add(attributemodifiable.g());
        }

        return nbttaglist;
    }

    public void a(NBTTagList nbttaglist) {
        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);
            String s = nbttagcompound.getString("Name");

            SystemUtils.a(IRegistry.ATTRIBUTE.getOptional(MinecraftKey.a(s)), (attributebase) -> {
                AttributeModifiable attributemodifiable = this.a(attributebase);

                if (attributemodifiable != null) {
                    attributemodifiable.a(nbttagcompound);
                }

            }, () -> {
                AttributeMapBase.LOGGER.warn("Ignoring unknown attribute '{}'", s);
            });
        }

    }
}
