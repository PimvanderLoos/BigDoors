package net.minecraft.advancements;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.resources.MinecraftKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Advancements {

    private static final Logger LOGGER = LogManager.getLogger();
    public final Map<MinecraftKey, Advancement> advancements = Maps.newHashMap();
    private final Set<Advancement> roots = Sets.newLinkedHashSet();
    private final Set<Advancement> tasks = Sets.newLinkedHashSet();
    private Advancements.a listener;

    public Advancements() {}

    private void a(Advancement advancement) {
        Iterator iterator = advancement.e().iterator();

        while (iterator.hasNext()) {
            Advancement advancement1 = (Advancement) iterator.next();

            this.a(advancement1);
        }

        Advancements.LOGGER.info("Forgot about advancement {}", advancement.getName());
        this.advancements.remove(advancement.getName());
        if (advancement.b() == null) {
            this.roots.remove(advancement);
            if (this.listener != null) {
                this.listener.b(advancement);
            }
        } else {
            this.tasks.remove(advancement);
            if (this.listener != null) {
                this.listener.d(advancement);
            }
        }

    }

    public void a(Set<MinecraftKey> set) {
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            MinecraftKey minecraftkey = (MinecraftKey) iterator.next();
            Advancement advancement = (Advancement) this.advancements.get(minecraftkey);

            if (advancement == null) {
                Advancements.LOGGER.warn("Told to remove advancement {} but I don't know what that is", minecraftkey);
            } else {
                this.a(advancement);
            }
        }

    }

    public void a(Map<MinecraftKey, Advancement.SerializedAdvancement> map) {
        HashMap hashmap = Maps.newHashMap(map);

        label42:
        while (!hashmap.isEmpty()) {
            boolean flag = false;
            Iterator iterator = hashmap.entrySet().iterator();

            Entry entry;

            while (iterator.hasNext()) {
                entry = (Entry) iterator.next();
                MinecraftKey minecraftkey = (MinecraftKey) entry.getKey();
                Advancement.SerializedAdvancement advancement_serializedadvancement = (Advancement.SerializedAdvancement) entry.getValue();
                Map map1 = this.advancements;

                Objects.requireNonNull(this.advancements);
                if (advancement_serializedadvancement.a(map1::get)) {
                    Advancement advancement = advancement_serializedadvancement.b(minecraftkey);

                    this.advancements.put(minecraftkey, advancement);
                    flag = true;
                    iterator.remove();
                    if (advancement.b() == null) {
                        this.roots.add(advancement);
                        if (this.listener != null) {
                            this.listener.a(advancement);
                        }
                    } else {
                        this.tasks.add(advancement);
                        if (this.listener != null) {
                            this.listener.c(advancement);
                        }
                    }
                }
            }

            if (!flag) {
                iterator = hashmap.entrySet().iterator();

                while (true) {
                    if (!iterator.hasNext()) {
                        break label42;
                    }

                    entry = (Entry) iterator.next();
                    Advancements.LOGGER.error("Couldn't load advancement {}: {}", entry.getKey(), entry.getValue());
                }
            }
        }

        Advancements.LOGGER.info("Loaded {} advancements", this.advancements.size());
    }

    public void a() {
        this.advancements.clear();
        this.roots.clear();
        this.tasks.clear();
        if (this.listener != null) {
            this.listener.a();
        }

    }

    public Iterable<Advancement> b() {
        return this.roots;
    }

    public Collection<Advancement> c() {
        return this.advancements.values();
    }

    @Nullable
    public Advancement a(MinecraftKey minecraftkey) {
        return (Advancement) this.advancements.get(minecraftkey);
    }

    public void a(@Nullable Advancements.a advancements_a) {
        this.listener = advancements_a;
        if (advancements_a != null) {
            Iterator iterator = this.roots.iterator();

            Advancement advancement;

            while (iterator.hasNext()) {
                advancement = (Advancement) iterator.next();
                advancements_a.a(advancement);
            }

            iterator = this.tasks.iterator();

            while (iterator.hasNext()) {
                advancement = (Advancement) iterator.next();
                advancements_a.c(advancement);
            }
        }

    }

    public interface a {

        void a(Advancement advancement);

        void b(Advancement advancement);

        void c(Advancement advancement);

        void d(Advancement advancement);

        void a();
    }
}
