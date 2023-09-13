package net.minecraft;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FilenameUtils;

public class FileUtils {

    private static final Pattern COPY_COUNTER_PATTERN = Pattern.compile("(<name>.*) \\((<count>\\d*)\\)", 66);
    private static final int MAX_FILE_NAME = 255;
    private static final Pattern RESERVED_WINDOWS_FILENAMES = Pattern.compile(".*\\.|(?:COM|CLOCK\\$|CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9])(?:\\..*)?", 2);

    public FileUtils() {}

    public static String a(Path path, String s, String s1) throws IOException {
        char[] achar = SharedConstants.ILLEGAL_FILE_CHARACTERS;
        int i = achar.length;

        for (int j = 0; j < i; ++j) {
            char c0 = achar[j];

            s = s.replace(c0, '_');
        }

        s = s.replaceAll("[./\"]", "_");
        if (FileUtils.RESERVED_WINDOWS_FILENAMES.matcher(s).matches()) {
            s = "_" + s + "_";
        }

        Matcher matcher = FileUtils.COPY_COUNTER_PATTERN.matcher(s);

        i = 0;
        if (matcher.matches()) {
            s = matcher.group("name");
            i = Integer.parseInt(matcher.group("count"));
        }

        if (s.length() > 255 - s1.length()) {
            s = s.substring(0, 255 - s1.length());
        }

        while (true) {
            String s2 = s;

            if (i != 0) {
                String s3 = " (" + i + ")";
                int k = 255 - s3.length();

                if (s.length() > k) {
                    s2 = s.substring(0, k);
                }

                s2 = s2 + s3;
            }

            s2 = s2 + s1;
            Path path1 = path.resolve(s2);

            try {
                Path path2 = Files.createDirectory(path1);

                Files.deleteIfExists(path2);
                return path.relativize(path2).toString();
            } catch (FileAlreadyExistsException filealreadyexistsexception) {
                ++i;
            }
        }
    }

    public static boolean a(Path path) {
        Path path1 = path.normalize();

        return path1.equals(path);
    }

    public static boolean b(Path path) {
        Iterator iterator = path.iterator();

        Path path1;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            path1 = (Path) iterator.next();
        } while (!FileUtils.RESERVED_WINDOWS_FILENAMES.matcher(path1.toString()).matches());

        return false;
    }

    public static Path b(Path path, String s, String s1) {
        String s2 = s + s1;
        Path path1 = Paths.get(s2);

        if (path1.endsWith(s1)) {
            throw new InvalidPathException(s2, "empty resource name");
        } else {
            return path.resolve(path1);
        }
    }

    public static String a(String s) {
        return FilenameUtils.getFullPath(s).replace(File.separator, "/");
    }

    public static String b(String s) {
        return FilenameUtils.normalize(s).replace(File.separator, "/");
    }
}
