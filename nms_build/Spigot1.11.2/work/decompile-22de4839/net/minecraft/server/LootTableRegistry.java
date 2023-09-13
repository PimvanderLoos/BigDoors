package net.minecraft.server;

import com.google.common.base.Charsets;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTableRegistry {

    private static final Logger a = LogManager.getLogger();
    private static final Gson b = (new GsonBuilder()).registerTypeAdapter(LootValueBounds.class, new LootValueBounds.a()).registerTypeAdapter(LootSelector.class, new LootSelector.a()).registerTypeAdapter(LootTable.class, new LootTable.a()).registerTypeHierarchyAdapter(LotoSelectorEntry.class, new LotoSelectorEntry.a()).registerTypeHierarchyAdapter(LootItemFunction.class, new LootItemFunctions.a()).registerTypeHierarchyAdapter(LootItemCondition.class, new LootItemConditions.a()).registerTypeHierarchyAdapter(LootTableInfo.EntityTarget.class, new LootTableInfo.EntityTarget.b$a()).create();
    private final LoadingCache<MinecraftKey, LootTable> c = CacheBuilder.newBuilder().build(new LootTableRegistry.a(null));
    private final File d;

    public LootTableRegistry(File file) {
        this.d = file;
        this.a();
    }

    public LootTable a(MinecraftKey minecraftkey) {
        return (LootTable) this.c.getUnchecked(minecraftkey);
    }

    public void a() {
        this.c.invalidateAll();
        Iterator iterator = LootTables.a().iterator();

        while (iterator.hasNext()) {
            MinecraftKey minecraftkey = (MinecraftKey) iterator.next();

            this.a(minecraftkey);
        }

    }

    class a extends CacheLoader<MinecraftKey, LootTable> {

        private a() {}

        public LootTable a(MinecraftKey minecraftkey) throws Exception {
            if (minecraftkey.a().contains(".")) {
                LootTableRegistry.a.debug("Invalid loot table name \'{}\' (can\'t contain periods)", new Object[] { minecraftkey});
                return LootTable.a;
            } else {
                LootTable loottable = this.b(minecraftkey);

                if (loottable == null) {
                    loottable = this.c(minecraftkey);
                }

                if (loottable == null) {
                    loottable = LootTable.a;
                    LootTableRegistry.a.warn("Couldn\'t find resource table {}", new Object[] { minecraftkey});
                }

                return loottable;
            }
        }

        @Nullable
        private LootTable b(MinecraftKey minecraftkey) {
            File file = new File(new File(LootTableRegistry.this.d, minecraftkey.b()), minecraftkey.a() + ".json");

            if (file.exists()) {
                if (file.isFile()) {
                    String s;

                    try {
                        s = Files.toString(file, Charsets.UTF_8);
                    } catch (IOException ioexception) {
                        LootTableRegistry.a.warn("Couldn\'t load loot table {} from {}", new Object[] { minecraftkey, file, ioexception});
                        return LootTable.a;
                    }

                    try {
                        return (LootTable) LootTableRegistry.b.fromJson(s, LootTable.class);
                    } catch (JsonParseException jsonparseexception) {
                        LootTableRegistry.a.error("Couldn\'t load loot table {} from {}", new Object[] { minecraftkey, file, jsonparseexception});
                        return LootTable.a;
                    }
                } else {
                    LootTableRegistry.a.warn("Expected to find loot table {} at {} but it was a folder.", new Object[] { minecraftkey, file});
                    return LootTable.a;
                }
            } else {
                return null;
            }
        }

        @Nullable
        private LootTable c(MinecraftKey minecraftkey) {
            URL url = LootTableRegistry.class.getResource("/assets/" + minecraftkey.b() + "/loot_tables/" + minecraftkey.a() + ".json");

            if (url != null) {
                String s;

                try {
                    s = Resources.toString(url, Charsets.UTF_8);
                } catch (IOException ioexception) {
                    LootTableRegistry.a.warn("Couldn\'t load loot table {} from {}", new Object[] { minecraftkey, url, ioexception});
                    return LootTable.a;
                }

                try {
                    return (LootTable) LootTableRegistry.b.fromJson(s, LootTable.class);
                } catch (JsonParseException jsonparseexception) {
                    LootTableRegistry.a.error("Couldn\'t load loot table {} from {}", new Object[] { minecraftkey, url, jsonparseexception});
                    return LootTable.a;
                }
            } else {
                return null;
            }
        }

        public Object load(Object object) throws Exception {
            return this.a((MinecraftKey) object);
        }

        a(Object object) {
            this();
        }
    }
}
