package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugReportAdvancement implements DebugReportProvider {

    private static final Logger b = LogManager.getLogger();
    private static final Gson c = (new GsonBuilder()).setPrettyPrinting().create();
    private final DebugReportGenerator d;
    private final List<Consumer<Consumer<Advancement>>> e = ImmutableList.of(new DebugReportAdvancementTheEnd(), new DebugReportAdvancementHusbandry(), new DebugReportAdvancementAdventure(), new DebugReportAdvancementNether(), new DebugReportAdvancementStory());

    public DebugReportAdvancement(DebugReportGenerator debugreportgenerator) {
        this.d = debugreportgenerator;
    }

    public void a(HashCache hashcache) throws IOException {
        java.nio.file.Path java_nio_file_path = this.d.b();
        Set<MinecraftKey> set = Sets.newHashSet();
        Consumer<Advancement> consumer = (advancement) -> {
            if (!set.add(advancement.getName())) {
                throw new IllegalStateException("Duplicate advancement " + advancement.getName());
            } else {
                this.a(hashcache, advancement.a().b(), java_nio_file_path.resolve("data/" + advancement.getName().b() + "/advancements/" + advancement.getName().getKey() + ".json"));
            }
        };
        Iterator iterator = this.e.iterator();

        while (iterator.hasNext()) {
            Consumer<Consumer<Advancement>> consumer1 = (Consumer) iterator.next();

            consumer1.accept(consumer);
        }

    }

    private void a(HashCache hashcache, JsonObject jsonobject, java.nio.file.Path java_nio_file_path) {
        try {
            String s = DebugReportAdvancement.c.toJson(jsonobject);
            String s1 = DebugReportAdvancement.a.hashUnencodedChars(s).toString();

            if (!Objects.equals(hashcache.a(java_nio_file_path), s1) || !Files.exists(java_nio_file_path, new LinkOption[0])) {
                Files.createDirectories(java_nio_file_path.getParent());
                BufferedWriter bufferedwriter = Files.newBufferedWriter(java_nio_file_path);
                Throwable throwable = null;

                try {
                    bufferedwriter.write(s);
                } catch (Throwable throwable1) {
                    throwable = throwable1;
                    throw throwable1;
                } finally {
                    if (bufferedwriter != null) {
                        if (throwable != null) {
                            try {
                                bufferedwriter.close();
                            } catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        } else {
                            bufferedwriter.close();
                        }
                    }

                }
            }

            hashcache.a(java_nio_file_path, s1);
        } catch (IOException ioexception) {
            DebugReportAdvancement.b.error("Couldn't save advancement {}", java_nio_file_path, ioexception);
        }

    }

    public String a() {
        return "Advancements";
    }
}
