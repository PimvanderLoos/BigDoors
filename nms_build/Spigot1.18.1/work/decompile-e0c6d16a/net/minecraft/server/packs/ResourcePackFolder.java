package net.minecraft.server.packs;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.ResourceKeyInvalidException;
import net.minecraft.SystemUtils;
import net.minecraft.resources.MinecraftKey;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourcePackFolder extends ResourcePackAbstract {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final boolean ON_WINDOWS = SystemUtils.getPlatform() == SystemUtils.OS.WINDOWS;
    private static final CharMatcher BACKSLASH_MATCHER = CharMatcher.is('\\');

    public ResourcePackFolder(File file) {
        super(file);
    }

    public static boolean validatePath(File file, String s) throws IOException {
        String s1 = file.getCanonicalPath();

        if (ResourcePackFolder.ON_WINDOWS) {
            s1 = ResourcePackFolder.BACKSLASH_MATCHER.replaceFrom(s1, '/');
        }

        return s1.endsWith(s);
    }

    @Override
    protected InputStream getResource(String s) throws IOException {
        File file = this.getFile(s);

        if (file == null) {
            throw new ResourceNotFoundException(this.file, s);
        } else {
            return new FileInputStream(file);
        }
    }

    @Override
    protected boolean hasResource(String s) {
        return this.getFile(s) != null;
    }

    @Nullable
    private File getFile(String s) {
        try {
            File file = new File(this.file, s);

            if (file.isFile() && validatePath(file, s)) {
                return file;
            }
        } catch (IOException ioexception) {
            ;
        }

        return null;
    }

    @Override
    public Set<String> getNamespaces(EnumResourcePackType enumresourcepacktype) {
        Set<String> set = Sets.newHashSet();
        File file = new File(this.file, enumresourcepacktype.getDirectory());
        File[] afile = file.listFiles(DirectoryFileFilter.DIRECTORY);

        if (afile != null) {
            File[] afile1 = afile;
            int i = afile.length;

            for (int j = 0; j < i; ++j) {
                File file1 = afile1[j];
                String s = getRelativePath(file, file1);

                if (s.equals(s.toLowerCase(Locale.ROOT))) {
                    set.add(s.substring(0, s.length() - 1));
                } else {
                    this.logWarning(s);
                }
            }
        }

        return set;
    }

    @Override
    public void close() {}

    @Override
    public Collection<MinecraftKey> getResources(EnumResourcePackType enumresourcepacktype, String s, String s1, int i, Predicate<String> predicate) {
        File file = new File(this.file, enumresourcepacktype.getDirectory());
        List<MinecraftKey> list = Lists.newArrayList();

        this.listResources(new File(new File(file, s), s1), i, s, list, s1 + "/", predicate);
        return list;
    }

    private void listResources(File file, int i, String s, List<MinecraftKey> list, String s1, Predicate<String> predicate) {
        File[] afile = file.listFiles();

        if (afile != null) {
            File[] afile1 = afile;
            int j = afile.length;

            for (int k = 0; k < j; ++k) {
                File file1 = afile1[k];

                if (file1.isDirectory()) {
                    if (i > 0) {
                        this.listResources(file1, i - 1, s, list, s1 + file1.getName() + "/", predicate);
                    }
                } else if (!file1.getName().endsWith(".mcmeta") && predicate.test(file1.getName())) {
                    try {
                        list.add(new MinecraftKey(s, s1 + file1.getName()));
                    } catch (ResourceKeyInvalidException resourcekeyinvalidexception) {
                        ResourcePackFolder.LOGGER.error(resourcekeyinvalidexception.getMessage());
                    }
                }
            }
        }

    }
}
