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
import net.minecraft.resources.MinecraftKey;
import org.apache.commons.io.IOUtils;

public class ResourcePackFile extends ResourcePackAbstract {

    public static final Splitter b = Splitter.on('/').omitEmptyStrings().limit(3);
    private ZipFile c;

    public ResourcePackFile(File file) {
        super(file);
    }

    private ZipFile b() throws IOException {
        if (this.c == null) {
            this.c = new ZipFile(this.a);
        }

        return this.c;
    }

    @Override
    protected InputStream a(String s) throws IOException {
        ZipFile zipfile = this.b();
        ZipEntry zipentry = zipfile.getEntry(s);

        if (zipentry == null) {
            throw new ResourceNotFoundException(this.a, s);
        } else {
            return zipfile.getInputStream(zipentry);
        }
    }

    @Override
    public boolean c(String s) {
        try {
            return this.b().getEntry(s) != null;
        } catch (IOException ioexception) {
            return false;
        }
    }

    @Override
    public Set<String> a(EnumResourcePackType enumresourcepacktype) {
        ZipFile zipfile;

        try {
            zipfile = this.b();
        } catch (IOException ioexception) {
            return Collections.emptySet();
        }

        Enumeration<? extends ZipEntry> enumeration = zipfile.entries();
        HashSet hashset = Sets.newHashSet();

        while (enumeration.hasMoreElements()) {
            ZipEntry zipentry = (ZipEntry) enumeration.nextElement();
            String s = zipentry.getName();

            if (s.startsWith(enumresourcepacktype.a() + "/")) {
                List<String> list = Lists.newArrayList(ResourcePackFile.b.split(s));

                if (list.size() > 1) {
                    String s1 = (String) list.get(1);

                    if (s1.equals(s1.toLowerCase(Locale.ROOT))) {
                        hashset.add(s1);
                    } else {
                        this.d(s1);
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
        if (this.c != null) {
            IOUtils.closeQuietly(this.c);
            this.c = null;
        }

    }

    @Override
    public Collection<MinecraftKey> a(EnumResourcePackType enumresourcepacktype, String s, String s1, int i, Predicate<String> predicate) {
        ZipFile zipfile;

        try {
            zipfile = this.b();
        } catch (IOException ioexception) {
            return Collections.emptySet();
        }

        Enumeration<? extends ZipEntry> enumeration = zipfile.entries();
        List<MinecraftKey> list = Lists.newArrayList();
        String s2 = enumresourcepacktype.a() + "/" + s + "/";
        String s3 = s2 + s1 + "/";

        while (enumeration.hasMoreElements()) {
            ZipEntry zipentry = (ZipEntry) enumeration.nextElement();

            if (!zipentry.isDirectory()) {
                String s4 = zipentry.getName();

                if (!s4.endsWith(".mcmeta") && s4.startsWith(s3)) {
                    String s5 = s4.substring(s2.length());
                    String[] astring = s5.split("/");

                    if (astring.length >= i + 1 && predicate.test(astring[astring.length - 1])) {
                        list.add(new MinecraftKey(s, s5));
                    }
                }
            }
        }

        return list;
    }
}
