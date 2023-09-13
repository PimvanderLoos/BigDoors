package net.minecraft.server.packs.resources;

import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.packs.metadata.ResourcePackMetaParser;
import net.minecraft.util.ChatDeserializer;
import org.apache.commons.io.IOUtils;

public class Resource implements IResource {

    private final String sourceName;
    private final MinecraftKey location;
    private final InputStream resourceStream;
    private final InputStream metadataStream;
    private boolean triedMetadata;
    private JsonObject metadata;

    public Resource(String s, MinecraftKey minecraftkey, InputStream inputstream, @Nullable InputStream inputstream1) {
        this.sourceName = s;
        this.location = minecraftkey;
        this.resourceStream = inputstream;
        this.metadataStream = inputstream1;
    }

    @Override
    public MinecraftKey a() {
        return this.location;
    }

    @Override
    public InputStream b() {
        return this.resourceStream;
    }

    @Override
    public boolean c() {
        return this.metadataStream != null;
    }

    @Nullable
    @Override
    public <T> T a(ResourcePackMetaParser<T> resourcepackmetaparser) {
        if (!this.c()) {
            return null;
        } else {
            if (this.metadata == null && !this.triedMetadata) {
                this.triedMetadata = true;
                BufferedReader bufferedreader = null;

                try {
                    bufferedreader = new BufferedReader(new InputStreamReader(this.metadataStream, StandardCharsets.UTF_8));
                    this.metadata = ChatDeserializer.a((Reader) bufferedreader);
                } finally {
                    IOUtils.closeQuietly(bufferedreader);
                }
            }

            if (this.metadata == null) {
                return null;
            } else {
                String s = resourcepackmetaparser.a();

                return this.metadata.has(s) ? resourcepackmetaparser.a(ChatDeserializer.t(this.metadata, s)) : null;
            }
        }
    }

    @Override
    public String d() {
        return this.sourceName;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof Resource)) {
            return false;
        } else {
            Resource resource;
            label32:
            {
                resource = (Resource) object;
                if (this.location != null) {
                    if (this.location.equals(resource.location)) {
                        break label32;
                    }
                } else if (resource.location == null) {
                    break label32;
                }

                return false;
            }

            if (this.sourceName != null) {
                if (this.sourceName.equals(resource.sourceName)) {
                    return true;
                }
            } else if (resource.sourceName == null) {
                return true;
            }

            return false;
        }
    }

    public int hashCode() {
        int i = this.sourceName != null ? this.sourceName.hashCode() : 0;

        i = 31 * i + (this.location != null ? this.location.hashCode() : 0);
        return i;
    }

    public void close() throws IOException {
        this.resourceStream.close();
        if (this.metadataStream != null) {
            this.metadataStream.close();
        }

    }
}
