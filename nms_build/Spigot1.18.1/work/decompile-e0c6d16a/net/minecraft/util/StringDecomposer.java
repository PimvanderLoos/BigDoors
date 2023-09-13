package net.minecraft.util;

import java.util.Optional;
import net.minecraft.EnumChatFormat;
import net.minecraft.network.chat.ChatModifier;
import net.minecraft.network.chat.IChatFormatted;

public class StringDecomposer {

    private static final char REPLACEMENT_CHAR = '\ufffd';
    private static final Optional<Object> STOP_ITERATION = Optional.of(Unit.INSTANCE);

    public StringDecomposer() {}

    private static boolean feedChar(ChatModifier chatmodifier, FormattedStringEmpty formattedstringempty, int i, char c0) {
        return Character.isSurrogate(c0) ? formattedstringempty.accept(i, chatmodifier, 65533) : formattedstringempty.accept(i, chatmodifier, c0);
    }

    public static boolean iterate(String s, ChatModifier chatmodifier, FormattedStringEmpty formattedstringempty) {
        int i = s.length();

        for (int j = 0; j < i; ++j) {
            char c0 = s.charAt(j);

            if (Character.isHighSurrogate(c0)) {
                if (j + 1 >= i) {
                    if (!formattedstringempty.accept(j, chatmodifier, 65533)) {
                        return false;
                    }
                    break;
                }

                char c1 = s.charAt(j + 1);

                if (Character.isLowSurrogate(c1)) {
                    if (!formattedstringempty.accept(j, chatmodifier, Character.toCodePoint(c0, c1))) {
                        return false;
                    }

                    ++j;
                } else if (!formattedstringempty.accept(j, chatmodifier, 65533)) {
                    return false;
                }
            } else if (!feedChar(chatmodifier, formattedstringempty, j, c0)) {
                return false;
            }
        }

        return true;
    }

    public static boolean iterateBackwards(String s, ChatModifier chatmodifier, FormattedStringEmpty formattedstringempty) {
        int i = s.length();

        for (int j = i - 1; j >= 0; --j) {
            char c0 = s.charAt(j);

            if (Character.isLowSurrogate(c0)) {
                if (j - 1 < 0) {
                    if (!formattedstringempty.accept(0, chatmodifier, 65533)) {
                        return false;
                    }
                    break;
                }

                char c1 = s.charAt(j - 1);

                if (Character.isHighSurrogate(c1)) {
                    --j;
                    if (!formattedstringempty.accept(j, chatmodifier, Character.toCodePoint(c1, c0))) {
                        return false;
                    }
                } else if (!formattedstringempty.accept(j, chatmodifier, 65533)) {
                    return false;
                }
            } else if (!feedChar(chatmodifier, formattedstringempty, j, c0)) {
                return false;
            }
        }

        return true;
    }

    public static boolean iterateFormatted(String s, ChatModifier chatmodifier, FormattedStringEmpty formattedstringempty) {
        return iterateFormatted(s, 0, chatmodifier, formattedstringempty);
    }

    public static boolean iterateFormatted(String s, int i, ChatModifier chatmodifier, FormattedStringEmpty formattedstringempty) {
        return iterateFormatted(s, i, chatmodifier, chatmodifier, formattedstringempty);
    }

    public static boolean iterateFormatted(String s, int i, ChatModifier chatmodifier, ChatModifier chatmodifier1, FormattedStringEmpty formattedstringempty) {
        int j = s.length();
        ChatModifier chatmodifier2 = chatmodifier;

        for (int k = i; k < j; ++k) {
            char c0 = s.charAt(k);
            char c1;

            if (c0 == 167) {
                if (k + 1 >= j) {
                    break;
                }

                c1 = s.charAt(k + 1);
                EnumChatFormat enumchatformat = EnumChatFormat.getByCode(c1);

                if (enumchatformat != null) {
                    chatmodifier2 = enumchatformat == EnumChatFormat.RESET ? chatmodifier1 : chatmodifier2.applyLegacyFormat(enumchatformat);
                }

                ++k;
            } else if (Character.isHighSurrogate(c0)) {
                if (k + 1 >= j) {
                    if (!formattedstringempty.accept(k, chatmodifier2, 65533)) {
                        return false;
                    }
                    break;
                }

                c1 = s.charAt(k + 1);
                if (Character.isLowSurrogate(c1)) {
                    if (!formattedstringempty.accept(k, chatmodifier2, Character.toCodePoint(c0, c1))) {
                        return false;
                    }

                    ++k;
                } else if (!formattedstringempty.accept(k, chatmodifier2, 65533)) {
                    return false;
                }
            } else if (!feedChar(chatmodifier2, formattedstringempty, k, c0)) {
                return false;
            }
        }

        return true;
    }

    public static boolean iterateFormatted(IChatFormatted ichatformatted, ChatModifier chatmodifier, FormattedStringEmpty formattedstringempty) {
        return !ichatformatted.visit((chatmodifier1, s) -> {
            return iterateFormatted(s, 0, chatmodifier1, formattedstringempty) ? Optional.empty() : StringDecomposer.STOP_ITERATION;
        }, chatmodifier).isPresent();
    }

    public static String filterBrokenSurrogates(String s) {
        StringBuilder stringbuilder = new StringBuilder();

        iterate(s, ChatModifier.EMPTY, (i, chatmodifier, j) -> {
            stringbuilder.appendCodePoint(j);
            return true;
        });
        return stringbuilder.toString();
    }

    public static String getPlainText(IChatFormatted ichatformatted) {
        StringBuilder stringbuilder = new StringBuilder();

        iterateFormatted(ichatformatted, ChatModifier.EMPTY, (i, chatmodifier, j) -> {
            stringbuilder.appendCodePoint(j);
            return true;
        });
        return stringbuilder.toString();
    }
}
