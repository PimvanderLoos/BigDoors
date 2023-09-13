package net.minecraft.network.chat.contents;

public class TranslatableFormatException extends IllegalArgumentException {

    public TranslatableFormatException(TranslatableContents translatablecontents, String s) {
        super(String.format("Error parsing: %s: %s", translatablecontents, s));
    }

    public TranslatableFormatException(TranslatableContents translatablecontents, int i) {
        super(String.format("Invalid index %d requested for %s", i, translatablecontents));
    }

    public TranslatableFormatException(TranslatableContents translatablecontents, Throwable throwable) {
        super(String.format("Error while parsing: %s", translatablecontents), throwable);
    }
}
