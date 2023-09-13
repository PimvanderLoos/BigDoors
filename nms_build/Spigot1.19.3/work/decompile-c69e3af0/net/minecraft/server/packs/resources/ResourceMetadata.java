package net.minecraft.server.packs.resources;

import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import net.minecraft.server.packs.metadata.ResourcePackMetaParser;
import net.minecraft.util.ChatDeserializer;

public interface ResourceMetadata {

    ResourceMetadata EMPTY = new ResourceMetadata() {
        @Override
        public <T> Optional<T> getSection(ResourcePackMetaParser<T> resourcepackmetaparser) {
            return Optional.empty();
        }
    };
    IoSupplier<ResourceMetadata> EMPTY_SUPPLIER = () -> {
        return ResourceMetadata.EMPTY;
    };

    static ResourceMetadata fromJsonStream(InputStream inputstream) throws IOException {
        BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));

        ResourceMetadata resourcemetadata;

        try {
            final JsonObject jsonobject = ChatDeserializer.parse((Reader) bufferedreader);

            resourcemetadata = new ResourceMetadata() {
                @Override
                public <T> Optional<T> getSection(ResourcePackMetaParser<T> resourcepackmetaparser) {
                    String s = resourcepackmetaparser.getMetadataSectionName();

                    return jsonobject.has(s) ? Optional.of(resourcepackmetaparser.fromJson(ChatDeserializer.getAsJsonObject(jsonobject, s))) : Optional.empty();
                }
            };
        } catch (Throwable throwable) {
            try {
                bufferedreader.close();
            } catch (Throwable throwable1) {
                throwable.addSuppressed(throwable1);
            }

            throw throwable;
        }

        bufferedreader.close();
        return resourcemetadata;
    }

    <T> Optional<T> getSection(ResourcePackMetaParser<T> resourcepackmetaparser);
}
