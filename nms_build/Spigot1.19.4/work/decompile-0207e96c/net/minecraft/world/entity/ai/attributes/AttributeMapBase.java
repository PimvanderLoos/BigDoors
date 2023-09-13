package net.minecraft.world.entity.ai.attributes;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.resources.MinecraftKey;
import org.slf4j.Logger;

public class AttributeMapBase {

    private static final Logger LOGGER = LogUtils.getLogger();
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

    @Nullable
    public AttributeModifiable getInstance(Holder<AttributeBase> holder) {
        return this.getInstance((AttributeBase) holder.value());
    }

    public boolean hasAttribute(AttributeBase attributebase) {
        return this.attributes.get(attributebase) != null || this.supplier.hasAttribute(attributebase);
    }

    public boolean hasAttribute(Holder<AttributeBase> holder) {
        return this.hasAttribute((AttributeBase) holder.value());
    }

    public boolean hasModifier(AttributeBase attributebase, UUID uuid) {
        AttributeModifiable attributemodifiable = (AttributeModifiable) this.attributes.get(attributebase);

        return attributemodifiable != null ? attributemodifiable.getModifier(uuid) != null : this.supplier.hasModifier(attributebase, uuid);
    }

    public boolean hasModifier(Holder<AttributeBase> holder, UUID uuid) {
        return this.hasModifier((AttributeBase) holder.value(), uuid);
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

    public double getModifierValue(Holder<AttributeBase> holder, UUID uuid) {
        return this.getModifierValue((AttributeBase) holder.value(), uuid);
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

            SystemUtils.ifElse(BuiltInRegistries.ATTRIBUTE.getOptional(MinecraftKey.tryParse(s)), (attributebase) -> {
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
