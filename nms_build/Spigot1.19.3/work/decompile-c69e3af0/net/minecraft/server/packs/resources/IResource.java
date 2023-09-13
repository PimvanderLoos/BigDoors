package net.minecraft.server.packs.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import net.minecraft.server.packs.IResourcePack;

public class IResource {

    private final IResourcePack source;
    private final IoSupplier<InputStream> streamSupplier;
    private final IoSupplier<ResourceMetadata> metadataSupplier;
    @Nullable
    private ResourceMetadata cachedMetadata;

    public IResource(IResourcePack iresourcepack, IoSupplier<InputStream> iosupplier, IoSupplier<ResourceMetadata> iosupplier1) {
        this.source = iresourcepack;
        this.streamSupplier = iosupplier;
        this.metadataSupplier = iosupplier1;
    }

    public IResource(IResourcePack iresourcepack, IoSupplier<InputStream> iosupplier) {
        this.source = iresourcepack;
        this.streamSupplier = iosupplier;
        this.metadataSupplier = ResourceMetadata.EMPTY_SUPPLIER;
        this.cachedMetadata = ResourceMetadata.EMPTY;
    }

    public IResourcePack source() {
        return this.source;
    }

    public String sourcePackId() {
        return this.source.packId();
    }

    public boolean isBuiltin() {
        return this.source.isBuiltin();
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
}
