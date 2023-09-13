package net.minecraft.network.chat.contents;

import java.util.Locale;

public class TranslatableFormatException extends IllegalArgumentException {

    public TranslatableFormatException(TranslatableContents translatablecontents, String s) {
        super(String.format(Locale.ROOT, "Error parsing: %s: %s", translatablecontents, s));
    }

    public TranslatableFormatException(TranslatableContents translatablecontents, int i) {
        super(String.format(Locale.ROOT, "Invalid index %d requested for %s", i, translatablecontents));
    }

    public TranslatableFormatException(TranslatableContents translatablecontents, Throwable throwable) {
        super(String.format(Locale.ROOT, "Error while parsing: %s", translatablecontents), throwable);
    }
}
