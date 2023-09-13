package net.minecraft.server.packs;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;

public class ResourceNotFoundException extends FileNotFoundException {

    public ResourceNotFoundException(File file, String s) {
        super(String.format(Locale.ROOT, "'%s' in ResourcePack '%s'", s, file));
    }
}
