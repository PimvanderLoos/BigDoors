package net.minecraft.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ResourcePackAbstract implements IResourcePack {

    private static final Logger b = LogManager.getLogger();
    protected final File a;

    public ResourcePackAbstract(File file) {
        this.a = file;
    }

    private static String c(EnumResourcePackType enumresourcepacktype, MinecraftKey minecraftkey) {
        return String.format("%s/%s/%s", enumresourcepacktype.a(), minecraftkey.b(), minecraftkey.getKey());
    }

    protected static String a(File file, File file1) {
        return file.toURI().relativize(file1.toURI()).getPath();
    }

    public InputStream a(EnumResourcePackType enumresourcepacktype, MinecraftKey minecraftkey) throws IOException {
        return this.a(c(enumresourcepacktype, minecraftkey));
    }

    public boolean b(EnumResourcePackType enumresourcepacktype, MinecraftKey minecraftkey) {
        return this.c(c(enumresourcepacktype, minecraftkey));
    }

    protected abstract InputStream a(String s) throws IOException;

    protected abstract boolean c(String s);

    protected void d(String s) {
        ResourcePackAbstract.b.warn("ResourcePack: ignored non-lowercase namespace: {} in {}", s, this.a);
    }

    @Nullable
    public <T> T a(ResourcePackMetaParser<T> resourcepackmetaparser) throws IOException {
        return a(resourcepackmetaparser, this.a("pack.mcmeta"));
    }

    @Nullable
    public static <T> T a(ResourcePackMetaParser<T> resourcepackmetaparser, InputStream inputstream) {
        JsonObject jsonobject;

        try {
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));
            Throwable throwable = null;

            try {
                jsonobject = ChatDeserializer.a((Reader) bufferedreader);
            } catch (Throwable throwable1) {
                throwable = throwable1;
                throw throwable1;
            } finally {
                if (bufferedreader != null) {
                    if (throwable != null) {
                        try {
                            bufferedreader.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    } else {
                        bufferedreader.close();
                    }
                }

            }
        } catch (JsonParseException | IOException ioexception) {
            ResourcePackAbstract.b.error("Couldn't load {} metadata", resourcepackmetaparser.a(), ioexception);
            return null;
        }

        if (!jsonobject.has(resourcepackmetaparser.a())) {
            return null;
        } else {
            try {
                return resourcepackmetaparser.a(ChatDeserializer.t(jsonobject, resourcepackmetaparser.a()));
            } catch (JsonParseException jsonparseexception) {
                ResourcePackAbstract.b.error("Couldn't load {} metadata", resourcepackmetaparser.a(), jsonparseexception);
                return null;
            }
        }
    }

    public String a() {
        return this.a.getName();
    }
}
