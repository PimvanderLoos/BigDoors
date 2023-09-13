package net.minecraft.server.packs.linkfs;

import java.nio.file.Path;
import java.util.Map;

interface PathContents {

    PathContents MISSING = new PathContents() {
        public String toString() {
            return "empty";
        }
    };
    PathContents RELATIVE = new PathContents() {
        public String toString() {
            return "relative";
        }
    };

    public static record a(Map<String, LinkFSPath> children) implements PathContents {

    }

    public static record b(Path contents) implements PathContents {

    }
}
