package net.minecraft.resources;

import java.util.List;
import java.util.Map;
import net.minecraft.server.packs.resources.IResource;
import net.minecraft.server.packs.resources.IResourceManager;

public class FileToIdConverter {

    private final String prefix;
    private final String extension;

    public FileToIdConverter(String s, String s1) {
        this.prefix = s;
        this.extension = s1;
    }

    public static FileToIdConverter json(String s) {
        return new FileToIdConverter(s, ".json");
    }

    public MinecraftKey idToFile(MinecraftKey minecraftkey) {
        return minecraftkey.withPath(this.prefix + "/" + minecraftkey.getPath() + this.extension);
    }

    public MinecraftKey fileToId(MinecraftKey minecraftkey) {
        String s = minecraftkey.getPath();

        return minecraftkey.withPath(s.substring(this.prefix.length() + 1, s.length() - this.extension.length()));
    }

    public Map<MinecraftKey, IResource> listMatchingResources(IResourceManager iresourcemanager) {
        return iresourcemanager.listResources(this.prefix, (minecraftkey) -> {
            return minecraftkey.getPath().endsWith(this.extension);
        });
    }

    public Map<MinecraftKey, List<IResource>> listMatchingResourceStacks(IResourceManager iresourcemanager) {
        return iresourcemanager.listResourceStacks(this.prefix, (minecraftkey) -> {
            return minecraftkey.getPath().endsWith(this.extension);
        });
    }
}
