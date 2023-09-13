package net.minecraft.server.packs.repository;

import java.io.File;
import java.io.FileFilter;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.server.packs.IResourcePack;
import net.minecraft.server.packs.ResourcePackFile;
import net.minecraft.server.packs.ResourcePackFolder;

public class ResourcePackSourceFolder implements ResourcePackSource {

    private static final FileFilter RESOURCEPACK_FILTER = (file) -> {
        boolean flag = file.isFile() && file.getName().endsWith(".zip");
        boolean flag1 = file.isDirectory() && (new File(file, "pack.mcmeta")).isFile();

        return flag || flag1;
    };
    private final File folder;
    private final PackSource packSource;

    public ResourcePackSourceFolder(File file, PackSource packsource) {
        this.folder = file;
        this.packSource = packsource;
    }

    @Override
    public void a(Consumer<ResourcePackLoader> consumer, ResourcePackLoader.a resourcepackloader_a) {
        if (!this.folder.isDirectory()) {
            this.folder.mkdirs();
        }

        File[] afile = this.folder.listFiles(ResourcePackSourceFolder.RESOURCEPACK_FILTER);

        if (afile != null) {
            File[] afile1 = afile;
            int i = afile.length;

            for (int j = 0; j < i; ++j) {
                File file = afile1[j];
                String s = "file/" + file.getName();
                ResourcePackLoader resourcepackloader = ResourcePackLoader.a(s, false, this.a(file), resourcepackloader_a, ResourcePackLoader.Position.TOP, this.packSource);

                if (resourcepackloader != null) {
                    consumer.accept(resourcepackloader);
                }
            }

        }
    }

    private Supplier<IResourcePack> a(File file) {
        return file.isDirectory() ? () -> {
            return new ResourcePackFolder(file);
        } : () -> {
            return new ResourcePackFile(file);
        };
    }
}
