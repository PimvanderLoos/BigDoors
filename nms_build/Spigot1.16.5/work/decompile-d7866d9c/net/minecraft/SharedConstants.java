package net.minecraft;

import com.mojang.bridge.game.GameVersion;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;
import java.time.Duration;
import net.minecraft.commands.CommandExceptionProvider;

public class SharedConstants {

    public static final Level a = Level.DISABLED;
    public static final long b = Duration.ofMillis(300L).toNanos();
    public static boolean c = true;
    public static boolean d;
    public static final char[] allowedCharacters = new char[]{'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '"', ':'};
    private static GameVersion f;

    public static boolean isAllowedChatCharacter(char c0) {
        return c0 != 167 && c0 >= ' ' && c0 != 127;
    }

    public static String a(String s) {
        StringBuilder stringbuilder = new StringBuilder();
        char[] achar = s.toCharArray();
        int i = achar.length;

        for (int j = 0; j < i; ++j) {
            char c0 = achar[j];

            if (isAllowedChatCharacter(c0)) {
                stringbuilder.append(c0);
            }
        }

        return stringbuilder.toString();
    }

    public static GameVersion getGameVersion() {
        if (SharedConstants.f == null) {
            SharedConstants.f = MinecraftVersion.a();
        }

        return SharedConstants.f;
    }

    public static int b() {
        return 754;
    }

    static {
        ResourceLeakDetector.setLevel(SharedConstants.a);
        CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES = false;
        CommandSyntaxException.BUILT_IN_EXCEPTIONS = new CommandExceptionProvider();
    }
}
