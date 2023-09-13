package net.minecraft.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.logging.LogUtils;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.Tlhelp32.MODULEENTRY32W;
import com.sun.jna.platform.win32.Version;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import net.minecraft.CrashReportSystemDetails;
import org.slf4j.Logger;

public class NativeModuleLister {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int LANG_MASK = 65535;
    private static final int DEFAULT_LANG = 1033;
    private static final int CODEPAGE_MASK = -65536;
    private static final int DEFAULT_CODEPAGE = 78643200;

    public NativeModuleLister() {}

    public static List<NativeModuleLister.a> listModules() {
        if (!Platform.isWindows()) {
            return ImmutableList.of();
        } else {
            int i = Kernel32.INSTANCE.GetCurrentProcessId();
            Builder<NativeModuleLister.a> builder = ImmutableList.builder();
            List<MODULEENTRY32W> list = Kernel32Util.getModules(i);
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                MODULEENTRY32W moduleentry32w = (MODULEENTRY32W) iterator.next();
                String s = moduleentry32w.szModule();
                Optional<NativeModuleLister.b> optional = tryGetVersion(moduleentry32w.szExePath());

                builder.add(new NativeModuleLister.a(s, optional));
            }

            return builder.build();
        }
    }

    private static Optional<NativeModuleLister.b> tryGetVersion(String s) {
        try {
            IntByReference intbyreference = new IntByReference();
            int i = Version.INSTANCE.GetFileVersionInfoSize(s, intbyreference);

            if (i == 0) {
                int j = Native.getLastError();

                if (j != 1813 && j != 1812) {
                    throw new Win32Exception(j);
                } else {
                    return Optional.empty();
                }
            } else {
                Memory memory = new Memory((long) i);

                if (!Version.INSTANCE.GetFileVersionInfo(s, 0, i, memory)) {
                    throw new Win32Exception(Native.getLastError());
                } else {
                    IntByReference intbyreference1 = new IntByReference();
                    Pointer pointer = queryVersionValue(memory, "\\VarFileInfo\\Translation", intbyreference1);
                    int[] aint = pointer.getIntArray(0L, intbyreference1.getValue() / 4);
                    OptionalInt optionalint = findLangAndCodepage(aint);

                    if (!optionalint.isPresent()) {
                        return Optional.empty();
                    } else {
                        int k = optionalint.getAsInt();
                        int l = k & '\uffff';
                        int i1 = (k & -65536) >> 16;
                        String s1 = queryVersionString(memory, langTableKey("FileDescription", l, i1), intbyreference1);
                        String s2 = queryVersionString(memory, langTableKey("CompanyName", l, i1), intbyreference1);
                        String s3 = queryVersionString(memory, langTableKey("FileVersion", l, i1), intbyreference1);

                        return Optional.of(new NativeModuleLister.b(s1, s3, s2));
                    }
                }
            }
        } catch (Exception exception) {
            NativeModuleLister.LOGGER.info("Failed to find module info for {}", s, exception);
            return Optional.empty();
        }
    }

    private static String langTableKey(String s, int i, int j) {
        return String.format(Locale.ROOT, "\\StringFileInfo\\%04x%04x\\%s", i, j, s);
    }

    private static OptionalInt findLangAndCodepage(int[] aint) {
        OptionalInt optionalint = OptionalInt.empty();
        int[] aint1 = aint;
        int i = aint.length;

        for (int j = 0; j < i; ++j) {
            int k = aint1[j];

            if ((k & -65536) == 78643200 && (k & '\uffff') == 1033) {
                return OptionalInt.of(k);
            }

            optionalint = OptionalInt.of(k);
        }

        return optionalint;
    }

    private static Pointer queryVersionValue(Pointer pointer, String s, IntByReference intbyreference) {
        PointerByReference pointerbyreference = new PointerByReference();

        if (!Version.INSTANCE.VerQueryValue(pointer, s, pointerbyreference, intbyreference)) {
            throw new UnsupportedOperationException("Can't get version value " + s);
        } else {
            return pointerbyreference.getValue();
        }
    }

    private static String queryVersionString(Pointer pointer, String s, IntByReference intbyreference) {
        try {
            Pointer pointer1 = queryVersionValue(pointer, s, intbyreference);
            byte[] abyte = pointer1.getByteArray(0L, (intbyreference.getValue() - 1) * 2);

            return new String(abyte, StandardCharsets.UTF_16LE);
        } catch (Exception exception) {
            return "";
        }
    }

    public static void addCrashSection(CrashReportSystemDetails crashreportsystemdetails) {
        crashreportsystemdetails.setDetail("Modules", () -> {
            return (String) listModules().stream().sorted(Comparator.comparing((nativemodulelister_a) -> {
                return nativemodulelister_a.name;
            })).map((nativemodulelister_a) -> {
                return "\n\t\t" + nativemodulelister_a;
            }).collect(Collectors.joining());
        });
    }

    public static class a {

        public final String name;
        public final Optional<NativeModuleLister.b> version;

        public a(String s, Optional<NativeModuleLister.b> optional) {
            this.name = s;
            this.version = optional;
        }

        public String toString() {
            return (String) this.version.map((nativemodulelister_b) -> {
                return this.name + ":" + nativemodulelister_b;
            }).orElse(this.name);
        }
    }

    public static class b {

        public final String description;
        public final String version;
        public final String company;

        public b(String s, String s1, String s2) {
            this.description = s;
            this.version = s1;
            this.company = s2;
        }

        public String toString() {
            return this.description + ":" + this.version + ":" + this.company;
        }
    }
}
