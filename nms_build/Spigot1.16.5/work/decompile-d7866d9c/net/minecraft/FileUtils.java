package net.minecraft;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.regex.Pattern;

public class FileUtils {

    private static final Pattern a = Pattern.compile("(<name>.*) \\((<count>\\d*)\\)", 66);
    private static final Pattern b = Pattern.compile(".*\\.|(?:COM|CLOCK\\$|CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9])(?:\\..*)?", 2);

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
        } while (!FileUtils.b.matcher(path1.toString()).matches());

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
}
