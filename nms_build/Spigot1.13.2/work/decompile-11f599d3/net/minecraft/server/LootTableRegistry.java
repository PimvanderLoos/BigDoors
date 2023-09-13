package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTableRegistry implements IResourcePackListener {

    private static final Logger c = LogManager.getLogger();
    private static final Gson d = (new GsonBuilder()).registerTypeAdapter(LootValueBounds.class, new LootValueBounds.a()).registerTypeAdapter(LootSelector.class, new LootSelector.a()).registerTypeAdapter(LootTable.class, new LootTable.a()).registerTypeHierarchyAdapter(LootSelectorEntry.class, new LootSelectorEntry.a()).registerTypeHierarchyAdapter(LootItemFunction.class, new LootItemFunctions.a()).registerTypeHierarchyAdapter(LootItemCondition.class, new LootItemConditions.a()).registerTypeHierarchyAdapter(LootTableInfo.EntityTarget.class, new LootTableInfo.EntityTarget.a()).create();
    private final Map<MinecraftKey, LootTable> e = Maps.newHashMap();
    public static final int a = "loot_tables/".length();
    public static final int b = ".json".length();

    public LootTableRegistry() {}

    public LootTable getLootTable(MinecraftKey minecraftkey) {
        return (LootTable) this.e.getOrDefault(minecraftkey, LootTable.a);
    }

    public void a(IResourceManager iresourcemanager) {
        this.e.clear();
        Iterator iterator = iresourcemanager.a("loot_tables", (s) -> {
            return s.endsWith(".json");
        }).iterator();

        while (iterator.hasNext()) {
            MinecraftKey minecraftkey = (MinecraftKey) iterator.next();
            String s = minecraftkey.getKey();
            MinecraftKey minecraftkey1 = new MinecraftKey(minecraftkey.b(), s.substring(LootTableRegistry.a, s.length() - LootTableRegistry.b));

            try {
                IResource iresource = iresourcemanager.a(minecraftkey);
                Throwable throwable = null;

                try {
                    LootTable loottable = (LootTable) ChatDeserializer.a(LootTableRegistry.d, IOUtils.toString(iresource.b(), StandardCharsets.UTF_8), LootTable.class);

                    if (loottable != null) {
                        this.e.put(minecraftkey1, loottable);
                    }
                } catch (Throwable throwable1) {
                    throwable = throwable1;
                    throw throwable1;
                } finally {
                    if (iresource != null) {
                        if (throwable != null) {
                            try {
                                iresource.close();
                            } catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        } else {
                            iresource.close();
                        }
                    }

                }
            } catch (Throwable throwable3) {
                LootTableRegistry.c.error("Couldn't read loot table {} from {}", minecraftkey1, minecraftkey, throwable3);
            }
        }

    }
}
