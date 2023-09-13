package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdvancementDataWorld {

    private static final Logger a = LogManager.getLogger();
    public static final Gson DESERIALIZER = (new GsonBuilder()).registerTypeHierarchyAdapter(Advancement.SerializedAdvancement.class, new JsonDeserializer() {
        public Advancement.SerializedAdvancement a(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "advancement");

            return Advancement.SerializedAdvancement.a(jsonobject, jsondeserializationcontext);
        }

        public Object deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            return this.a(jsonelement, type, jsondeserializationcontext);
        }
    }).registerTypeAdapter(AdvancementRewards.class, new AdvancementRewards.a()).registerTypeHierarchyAdapter(IChatBaseComponent.class, new IChatBaseComponent.ChatSerializer()).registerTypeHierarchyAdapter(ChatModifier.class, new ChatModifier.ChatModifierSerializer()).registerTypeAdapterFactory(new ChatTypeAdapterFactory()).create();
    public static final Advancements REGISTRY = new Advancements();
    public final File folder;
    private boolean e;

    public AdvancementDataWorld(@Nullable File file) {
        this.folder = file;
        this.reload();
    }

    public void reload() {
        this.e = false;
        AdvancementDataWorld.REGISTRY.a();
        Map map = this.d();

        this.a(map);
        AdvancementDataWorld.REGISTRY.a(map);
        Iterator iterator = AdvancementDataWorld.REGISTRY.b().iterator();

        while (iterator.hasNext()) {
            Advancement advancement = (Advancement) iterator.next();

            if (advancement.c() != null) {
                AdvancementTree.a(advancement);
            }
        }

    }

    public boolean b() {
        return this.e;
    }

    private Map<MinecraftKey, Advancement.SerializedAdvancement> d() {
        if (this.folder == null) {
            return Maps.newHashMap();
        } else {
            HashMap hashmap = Maps.newHashMap();

            this.folder.mkdirs();
            Iterator iterator = FileUtils.listFiles(this.folder, new String[] { "json"}, true).iterator();

            while (iterator.hasNext()) {
                File file = (File) iterator.next();
                String s = FilenameUtils.removeExtension(this.folder.toURI().relativize(file.toURI()).toString());
                String[] astring = s.split("/", 2);

                if (astring.length == 2) {
                    MinecraftKey minecraftkey = new MinecraftKey(astring[0], astring[1]);

                    try {
                        Advancement.SerializedAdvancement advancement_serializedadvancement = (Advancement.SerializedAdvancement) ChatDeserializer.a(AdvancementDataWorld.DESERIALIZER, FileUtils.readFileToString(file, StandardCharsets.UTF_8), Advancement.SerializedAdvancement.class);

                        if (advancement_serializedadvancement == null) {
                            AdvancementDataWorld.a.error("Couldn\'t load custom advancement " + minecraftkey + " from " + file + " as it\'s empty or null");
                        } else {
                            hashmap.put(minecraftkey, advancement_serializedadvancement);
                        }
                    } catch (IllegalArgumentException | JsonParseException jsonparseexception) {
                        AdvancementDataWorld.a.error("Parsing error loading custom advancement " + minecraftkey, jsonparseexception);
                        this.e = true;
                    } catch (IOException ioexception) {
                        AdvancementDataWorld.a.error("Couldn\'t read custom advancement " + minecraftkey + " from " + file, ioexception);
                        this.e = true;
                    }
                }
            }

            return hashmap;
        }
    }

    private void a(Map<MinecraftKey, Advancement.SerializedAdvancement> map) {
        FileSystem filesystem = null;

        try {
            URL url = AdvancementDataWorld.class.getResource("/assets/.mcassetsroot");

            if (url == null) {
                AdvancementDataWorld.a.error("Couldn\'t find .mcassetsroot");
                this.e = true;
            } else {
                URI uri = url.toURI();
                java.nio.file.Path java_nio_file_path;

                if ("file".equals(uri.getScheme())) {
                    java_nio_file_path = Paths.get(CraftingManager.class.getResource("/assets/minecraft/advancements").toURI());
                } else {
                    if (!"jar".equals(uri.getScheme())) {
                        AdvancementDataWorld.a.error("Unsupported scheme " + uri + " trying to list all built-in advancements (NYI?)");
                        this.e = true;
                        return;
                    }

                    filesystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
                    java_nio_file_path = filesystem.getPath("/assets/minecraft/advancements", new String[0]);
                }

                Iterator iterator = Files.walk(java_nio_file_path, new FileVisitOption[0]).iterator();

                while (iterator.hasNext()) {
                    java.nio.file.Path java_nio_file_path1 = (java.nio.file.Path) iterator.next();

                    if ("json".equals(FilenameUtils.getExtension(java_nio_file_path1.toString()))) {
                        java.nio.file.Path java_nio_file_path2 = java_nio_file_path.relativize(java_nio_file_path1);
                        String s = FilenameUtils.removeExtension(java_nio_file_path2.toString()).replaceAll("\\\\", "/");
                        MinecraftKey minecraftkey = new MinecraftKey("minecraft", s);

                        if (!map.containsKey(minecraftkey)) {
                            BufferedReader bufferedreader = null;

                            try {
                                bufferedreader = Files.newBufferedReader(java_nio_file_path1);
                                Advancement.SerializedAdvancement advancement_serializedadvancement = (Advancement.SerializedAdvancement) ChatDeserializer.a(AdvancementDataWorld.DESERIALIZER, (Reader) bufferedreader, Advancement.SerializedAdvancement.class);

                                map.put(minecraftkey, advancement_serializedadvancement);
                            } catch (JsonParseException jsonparseexception) {
                                AdvancementDataWorld.a.error("Parsing error loading built-in advancement " + minecraftkey, jsonparseexception);
                                this.e = true;
                            } catch (IOException ioexception) {
                                AdvancementDataWorld.a.error("Couldn\'t read advancement " + minecraftkey + " from " + java_nio_file_path1, ioexception);
                                this.e = true;
                            } finally {
                                IOUtils.closeQuietly(bufferedreader);
                            }
                        }
                    }
                }

            }
        } catch (IOException | URISyntaxException urisyntaxexception) {
            AdvancementDataWorld.a.error("Couldn\'t get a list of all built-in advancement files", urisyntaxexception);
            this.e = true;
        } finally {
            IOUtils.closeQuietly(filesystem);
        }
    }

    @Nullable
    public Advancement a(MinecraftKey minecraftkey) {
        return AdvancementDataWorld.REGISTRY.a(minecraftkey);
    }

    public Iterable<Advancement> c() {
        return AdvancementDataWorld.REGISTRY.c();
    }
}
