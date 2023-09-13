package net.minecraft.server.packs;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.annotation.Nullable;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.packs.resources.IoSupplier;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class ResourcePackFile extends ResourcePackAbstract {

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Splitter SPLITTER = Splitter.on('/').omitEmptyStrings().limit(3);
    private final File file;
    @Nullable
    private ZipFile zipFile;
    private boolean failedToLoad;

    public ResourcePackFile(String s, File file, boolean flag) {
        super(s, flag);
        this.file = file;
    }

    @Nullable
    private ZipFile getOrCreateZipFile() {
        if (this.failedToLoad) {
            return null;
        } else {
            if (this.zipFile == null) {
                try {
                    this.zipFile = new ZipFile(this.file);
                } catch (IOException ioexception) {
                    ResourcePackFile.LOGGER.error("Failed to open pack {}", this.file, ioexception);
                    this.failedToLoad = true;
                    return null;
                }
            }

            return this.zipFile;
        }
    }

    private static String getPathFromLocation(EnumResourcePackType enumresourcepacktype, MinecraftKey minecraftkey) {
        return String.format(Locale.ROOT, "%s/%s/%s", enumresourcepacktype.getDirectory(), minecraftkey.getNamespace(), minecraftkey.getPath());
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getRootResource(String... astring) {
        return this.getResource(String.join("/", astring));
    }

    @Override
    public IoSupplier<InputStream> getResource(EnumResourcePackType enumresourcepacktype, MinecraftKey minecraftkey) {
        return this.getResource(getPathFromLocation(enumresourcepacktype, minecraftkey));
    }

    @Nullable
    private IoSupplier<InputStream> getResource(String s) {
        ZipFile zipfile = this.getOrCreateZipFile();

        if (zipfile == null) {
            return null;
        } else {
            ZipEntry zipentry = zipfile.getEntry(s);

            return zipentry == null ? null : IoSupplier.create(zipfile, zipentry);
        }
    }

    @Override
    public Set<String> getNamespaces(EnumResourcePackType enumresourcepacktype) {
        ZipFile zipfile = this.getOrCreateZipFile();

        if (zipfile == null) {
            return Set.of();
        } else {
            Enumeration<? extends ZipEntry> enumeration = zipfile.entries();
            HashSet hashset = Sets.newHashSet();

            while (enumeration.hasMoreElements()) {
                ZipEntry zipentry = (ZipEntry) enumeration.nextElement();
                String s = zipentry.getName();

                if (s.startsWith(enumresourcepacktype.getDirectory() + "/")) {
                    List<String> list = Lists.newArrayList(ResourcePackFile.SPLITTER.split(s));

                    if (list.size() > 1) {
                        String s1 = (String) list.get(1);

                        if (s1.equals(s1.toLowerCase(Locale.ROOT))) {
                            hashset.add(s1);
                        } else {
                            ResourcePackFile.LOGGER.warn("Ignored non-lowercase namespace: {} in {}", s1, this.file);
                        }
                    }
                }
            }

            return hashset;
        }
    }

    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

    @Override
    public void close() {
        if (this.zipFile != null) {
            IOUtils.closeQuietly(this.zipFile);
            this.zipFile = null;
        }

    }

    @Override
    public void listResources(EnumResourcePackType enumresourcepacktype, String s, String s1, IResourcePack.a iresourcepack_a) {
        ZipFile zipfile = this.getOrCreateZipFile();

        if (zipfile != null) {
            Enumeration<? extends ZipEntry> enumeration = zipfile.entries();
            String s2 = enumresourcepacktype.getDirectory();
            String s3 = s2 + "/" + s + "/";
            String s4 = s3 + s1 + "/";

            while (enumeration.hasMoreElements()) {
                ZipEntry zipentry = (ZipEntry) enumeration.nextElement();

                if (!zipentry.isDirectory()) {
                    String s5 = zipentry.getName();

                    if (s5.startsWith(s4)) {
                        String s6 = s5.substring(s3.length());
                        MinecraftKey minecraftkey = MinecraftKey.tryBuild(s, s6);

                        if (minecraftkey != null) {
                            iresourcepack_a.accept(minecraftkey, IoSupplier.create(zipfile, zipentry));
                        } else {
                            ResourcePackFile.LOGGER.warn("Invalid path in datapack: {}:{}, ignoring", s, s6);
                        }
                    }
                }
            }

        }
    }
}
