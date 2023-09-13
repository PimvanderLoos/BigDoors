package net.minecraft.world.entity.ai.attributes;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
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
    private final Map<AttributeBase, AttributeModifiable> b = Maps.newHashMap();
    private final Set<AttributeModifiable> c = Sets.newHashSet();
    private final AttributeProvider d;

    public AttributeMapBase(AttributeProvider attributeprovider) {
        this.d = attributeprovider;
    }

    private void a(AttributeModifiable attributemodifiable) {
        if (attributemodifiable.getAttribute().b()) {
            this.c.add(attributemodifiable);
        }

    }

    public Set<AttributeModifiable> getAttributes() {
        return this.c;
    }

    public Collection<AttributeModifiable> b() {
        return (Collection) this.b.values().stream().filter((attributemodifiable) -> {
            return attributemodifiable.getAttribute().b();
        }).collect(Collectors.toList());
    }

    @Nullable
    public AttributeModifiable a(AttributeBase attributebase) {
        return (AttributeModifiable) this.b.computeIfAbsent(attributebase, (attributebase1) -> {
            return this.d.a(this::a, attributebase1);
        });
    }

    public boolean b(AttributeBase attributebase) {
        return this.b.get(attributebase) != null || this.d.c(attributebase);
    }

    public boolean a(AttributeBase attributebase, UUID uuid) {
        AttributeModifiable attributemodifiable = (AttributeModifiable) this.b.get(attributebase);

        return attributemodifiable != null ? attributemodifiable.a(uuid) != null : this.d.b(attributebase, uuid);
    }

    public double c(AttributeBase attributebase) {
        AttributeModifiable attributemodifiable = (AttributeModifiable) this.b.get(attributebase);

        return attributemodifiable != null ? attributemodifiable.getValue() : this.d.a(attributebase);
    }

    public double d(AttributeBase attributebase) {
        AttributeModifiable attributemodifiable = (AttributeModifiable) this.b.get(attributebase);

        return attributemodifiable != null ? attributemodifiable.getBaseValue() : this.d.b(attributebase);
    }

    public double b(AttributeBase attributebase, UUID uuid) {
        AttributeModifiable attributemodifiable = (AttributeModifiable) this.b.get(attributebase);

        return attributemodifiable != null ? attributemodifiable.a(uuid).getAmount() : this.d.a(attributebase, uuid);
    }

    public void a(Multimap<AttributeBase, AttributeModifier> multimap) {
        multimap.asMap().forEach((attributebase, collection) -> {
            AttributeModifiable attributemodifiable = (AttributeModifiable) this.b.get(attributebase);

            if (attributemodifiable != null) {
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

    public NBTTagList c() {
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = this.b.values().iterator();

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
