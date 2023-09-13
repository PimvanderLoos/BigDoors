package net.minecraft.server.packs.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;

public class IResource {

    private final String packId;
    private final IResource.a<InputStream> streamSupplier;
    private final IResource.a<ResourceMetadata> metadataSupplier;
    @Nullable
    private ResourceMetadata cachedMetadata;

    public IResource(String s, IResource.a<InputStream> iresource_a, IResource.a<ResourceMetadata> iresource_a1) {
        this.packId = s;
        this.streamSupplier = iresource_a;
        this.metadataSupplier = iresource_a1;
    }

    public IResource(String s, IResource.a<InputStream> iresource_a) {
        this.packId = s;
        this.streamSupplier = iresource_a;
        this.metadataSupplier = () -> {
            return ResourceMetadata.EMPTY;
        };
        this.cachedMetadata = ResourceMetadata.EMPTY;
    }

    public String sourcePackId() {
        return this.packId;
    }

    public InputStream open() throws IOException {
        return (InputStream) this.streamSupplier.get();
    }

    public BufferedReader openAsReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.open(), StandardCharsets.UTF_8));
    }

    public ResourceMetadata metadata() throws IOException {
        if (this.cachedMetadata == null) {
            this.cachedMetadata = (ResourceMetadata) this.metadataSupplier.get();
        }

        return this.cachedMetadata;
    }

    @FunctionalInterface
    public interface a<T> {

        T get() throws IOException;
    }
}
