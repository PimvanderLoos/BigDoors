package net.minecraft.server;

public class MojangsonParseException extends Exception {

    public MojangsonParseException(String s, String s1, int i) {
        super(s + " at: " + a(s1, i));
    }

    private static String a(String s, int i) {
        StringBuilder stringbuilder = new StringBuilder();
        int j = Math.min(s.length(), i);

        if (j > 35) {
            stringbuilder.append("...");
        }

        stringbuilder.append(s.substring(Math.max(0, j - 35), j));
        stringbuilder.append("<--[HERE]");
        return stringbuilder.toString();
    }
}
