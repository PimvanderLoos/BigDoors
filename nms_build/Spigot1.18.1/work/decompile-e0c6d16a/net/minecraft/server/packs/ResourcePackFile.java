package net.minecraft.server.packs;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.annotation.Nullable;
import net.minecraft.resources.MinecraftKey;
import org.apache.commons.io.IOUtils;

public class ResourcePackFile extends ResourcePackAbstract {

    public static final Splitter SPLITTER = Splitter.on('/').omitEmptyStrings().limit(3);
    @Nullable
    private ZipFile zipFile;

    public ResourcePackFile(File file) {
        super(file);
    }

    private ZipFile getOrCreateZipFile() throws IOException {
        if (this.zipFile == null) {
            this.zipFile = new ZipFile(this.file);
        }

        return this.zipFile;
    }

    @Override
    protected InputStream getResource(String s) throws IOException {
        ZipFile zipfile = this.getOrCreateZipFile();
        ZipEntry zipentry = zipfile.getEntry(s);

        if (zipentry == null) {
            throw new ResourceNotFoundException(this.file, s);
        } else {
            return zipfile.getInputStream(zipentry);
        }
    }

    @Override
    public boolean hasResource(String s) {
        try {
            return this.getOrCreateZipFile().getEntry(s) != null;
        } catch (IOException ioexception) {
            return false;
        }
    }

    @Override
    public Set<String> getNamespaces(EnumResourcePackType enumresourcepacktype) {
        ZipFile zipfile;

        try {
            zipfile = this.getOrCreateZipFile();
        } catch (IOException ioexception) {
            return Collections.emptySet();
        }

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
                        this.logWarning(s1);
                    }
                }
            }
        }

        return hashset;
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
    public Collection<MinecraftKey> getResources(EnumResourcePackType enumresourcepacktype, String s, String s1, int i, Predicate<String> predicate) {
        ZipFile zipfile;

        try {
            zipfile = this.getOrCreateZipFile();
        } catch (IOException ioexception) {
            return Collections.emptySet();
        }

        Enumeration<? extends ZipEntry> enumeration = zipfile.entries();
        List<MinecraftKey> list = Lists.newArrayList();
        String s2 = enumresourcepacktype.getDirectory();
        String s3 = s2 + "/" + s + "/";
        String s4 = s3 + s1 + "/";

        while (enumeration.hasMoreElements()) {
            ZipEntry zipentry = (ZipEntry) enumeration.nextElement();

            if (!zipentry.isDirectory()) {
                String s5 = zipentry.getName();

                if (!s5.endsWith(".mcmeta") && s5.startsWith(s4)) {
                    String s6 = s5.substring(s3.length());
                    String[] astring = s6.split("/");

                    if (astring.length >= i + 1 && predicate.test(astring[astring.length - 1])) {
                        list.add(new MinecraftKey(s, s6));
                    }
                }
            }
        }

        return list;
    }
}
