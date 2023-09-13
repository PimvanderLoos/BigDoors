package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.advancements.Advancements;
import net.minecraft.advancements.critereon.LootDeserializationContext;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.packs.resources.IResourceManager;
import net.minecraft.server.packs.resources.ResourceDataJson;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.util.profiling.GameProfilerFiller;
import net.minecraft.world.level.storage.loot.LootPredicateManager;
import org.slf4j.Logger;

public class AdvancementDataWorld extends ResourceDataJson {

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Gson GSON = (new GsonBuilder()).create();
    public Advancements advancements = new Advancements();
    private final LootPredicateManager predicateManager;

    public AdvancementDataWorld(LootPredicateManager lootpredicatemanager) {
        super(AdvancementDataWorld.GSON, "advancements");
        this.predicateManager = lootpredicatemanager;
    }

    protected void apply(Map<MinecraftKey, JsonElement> map, IResourceManager iresourcemanager, GameProfilerFiller gameprofilerfiller) {
        Map<MinecraftKey, Advancement.SerializedAdvancement> map1 = Maps.newHashMap();

        map.forEach((minecraftkey, jsonelement) -> {
            try {
                JsonObject jsonobject = ChatDeserializer.convertToJsonObject(jsonelement, "advancement");
                Advancement.SerializedAdvancement advancement_serializedadvancement = Advancement.SerializedAdvancement.fromJson(jsonobject, new LootDeserializationContext(minecraftkey, this.predicateManager));

                map1.put(minecraftkey, advancement_serializedadvancement);
            } catch (Exception exception) {
                AdvancementDataWorld.LOGGER.error("Parsing error loading custom advancement {}: {}", minecraftkey, exception.getMessage());
            }

        });
        Advancements advancements = new Advancements();

        advancements.add(map1);
        Iterator iterator = advancements.getRoots().iterator();

        while (iterator.hasNext()) {
            Advancement advancement = (Advancement) iterator.next();

            if (advancement.getDisplay() != null) {
                AdvancementTree.run(advancement);
            }
        }

        this.advancements = advancements;
    }

    @Nullable
    public Advancement getAdvancement(MinecraftKey minecraftkey) {
        return this.advancements.get(minecraftkey);
    }

    public Collection<Advancement> getAllAdvancements() {
        return this.advancements.getAllAdvancements();
    }
}
