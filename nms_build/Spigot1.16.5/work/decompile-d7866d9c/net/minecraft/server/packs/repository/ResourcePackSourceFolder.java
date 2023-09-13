package net.minecraft.server.packs.repository;

import java.io.File;
import java.io.FileFilter;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.server.packs.IResourcePack;
import net.minecraft.server.packs.ResourcePackFile;
import net.minecraft.server.packs.ResourcePackFolder;

public class ResourcePackSourceFolder implements ResourcePackSource {

    private static final FileFilter a = (file) -> {
        boolean flag = file.isFile() && file.getName().endsWith(".zip");
        boolean flag1 = file.isDirectory() && (new File(file, "pack.mcmeta")).isFile();

        return flag || flag1;
    };
    private final File file;
    private final PackSource c;

    public ResourcePackSourceFolder(File file, PackSource packsource) {
        this.file = file;
        this.c = packsource;
    }

    @Override
    public void a(Consumer<ResourcePackLoader> consumer, ResourcePackLoader.a resourcepackloader_a) {
        if (!this.file.isDirectory()) {
            this.file.mkdirs();
        }

        File[] afile = this.file.listFiles(ResourcePackSourceFolder.a);

        if (afile != null) {
            File[] afile1 = afile;
            int i = afile.length;

            for (int j = 0; j < i; ++j) {
                File file = afile1[j];
                String s = "file/" + file.getName();
                ResourcePackLoader resourcepackloader = ResourcePackLoader.a(s, false, this.a(file), resourcepackloader_a, ResourcePackLoader.Position.TOP, this.c);

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
