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

    private void onAttributeModified(AttributeModifiable attributemodifiable) {
        if (attributemodifiable.getAttribute().isClientSyncable()) {
            this.dirtyAttributes.add(attributemodifiable);
        }

    }

    public Set<AttributeModifiable> getDirtyAttributes() {
        return this.dirtyAttributes;
    }

    public Collection<AttributeModifiable> getSyncableAttributes() {
        return (Collection) this.attributes.values().stream().filter((attributemodifiable) -> {
            return attributemodifiable.getAttribute().isClientSyncable();
        }).collect(Collectors.toList());
    }

    @Nullable
    public AttributeModifiable getInstance(AttributeBase attributebase) {
        return (AttributeModifiable) this.attributes.computeIfAbsent(attributebase, (attributebase1) -> {
            return this.supplier.createInstance(this::onAttributeModified, attributebase1);
        });
    }

    public boolean hasAttribute(AttributeBase attributebase) {
        return this.attributes.get(attributebase) != null || this.supplier.hasAttribute(attributebase);
    }

    public boolean hasModifier(AttributeBase attributebase, UUID uuid) {
        AttributeModifiable attributemodifiable = (AttributeModifiable) this.attributes.get(attributebase);

        return attributemodifiable != null ? attributemodifiable.getModifier(uuid) != null : this.supplier.hasModifier(attributebase, uuid);
    }

    public double getValue(AttributeBase attributebase) {
        AttributeModifiable attributemodifiable = (AttributeModifiable) this.attributes.get(attributebase);

        return attributemodifiable != null ? attributemodifiable.getValue() : this.supplier.getValue(attributebase);
    }

    public double getBaseValue(AttributeBase attributebase) {
        AttributeModifiable attributemodifiable = (AttributeModifiable) this.attributes.get(attributebase);

        return attributemodifiable != null ? attributemodifiable.getBaseValue() : this.supplier.getBaseValue(attributebase);
    }

    public double getModifierValue(AttributeBase attributebase, UUID uuid) {
        AttributeModifiable attributemodifiable = (AttributeModifiable) this.attributes.get(attributebase);

        return attributemodifiable != null ? attributemodifiable.getModifier(uuid).getAmount() : this.supplier.getModifierValue(attributebase, uuid);
    }

    public void removeAttributeModifiers(Multimap<AttributeBase, AttributeModifier> multimap) {
        multimap.asMap().forEach((attributebase, collection) -> {
            AttributeModifiable attributemodifiable = (AttributeModifiable) this.attributes.get(attributebase);

            if (attributemodifiable != null) {
                Objects.requireNonNull(attributemodifiable);
                collection.forEach(attributemodifiable::removeModifier);
            }

        });
    }

    public void addTransientAttributeModifiers(Multimap<AttributeBase, AttributeModifier> multimap) {
        multimap.forEach((attributebase, attributemodifier) -> {
            AttributeModifiable attributemodifiable = this.getInstance(attributebase);

            if (attributemodifiable != null) {
                attributemodifiable.removeModifier(attributemodifier);
                attributemodifiable.addTransientModifier(attributemodifier);
            }

        });
    }

    public void assignValues(AttributeMapBase attributemapbase) {
        attributemapbase.attributes.values().forEach((attributemodifiable) -> {
            AttributeModifiable attributemodifiable1 = this.getInstance(attributemodifiable.getAttribute());

            if (attributemodifiable1 != null) {
                attributemodifiable1.replaceFrom(attributemodifiable);
            }

        });
    }

    public NBTTagList save() {
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = this.attributes.values().iterator();

        while (iterator.hasNext()) {
            AttributeModifiable attributemodifiable = (AttributeModifiable) iterator.next();

            nbttaglist.add(attributemodifiable.save());
        }

        return nbttaglist;
    }

    public void load(NBTTagList nbttaglist) {
        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);
            String s = nbttagcompound.getString("Name");

            SystemUtils.ifElse(IRegistry.ATTRIBUTE.getOptional(MinecraftKey.tryParse(s)), (attributebase) -> {
                AttributeModifiable attributemodifiable = this.getInstance(attributebase);

                if (attributemodifiable != null) {
                    attributemodifiable.load(nbttagcompound);
                }

            }, () -> {
                AttributeMapBase.LOGGER.warn("Ignoring unknown attribute '{}'", s);
            });
        }

    }
}
