package net.minecraft;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.state.IBlockData;

public class CrashReportSystemDetails {

    private final String title;
    private final List<CrashReportSystemDetails.CrashReportDetail> entries = Lists.newArrayList();
    private StackTraceElement[] stackTrace = new StackTraceElement[0];

    public CrashReportSystemDetails(String s) {
        this.title = s;
    }

    public static String formatLocation(LevelHeightAccessor levelheightaccessor, double d0, double d1, double d2) {
        return String.format(Locale.ROOT, "%.2f,%.2f,%.2f - %s", d0, d1, d2, formatLocation(levelheightaccessor, BlockPosition.containing(d0, d1, d2)));
    }

    public static String formatLocation(LevelHeightAccessor levelheightaccessor, BlockPosition blockposition) {
        return formatLocation(levelheightaccessor, blockposition.getX(), blockposition.getY(), blockposition.getZ());
    }

    public static String formatLocation(LevelHeightAccessor levelheightaccessor, int i, int j, int k) {
        StringBuilder stringbuilder = new StringBuilder();

        try {
            stringbuilder.append(String.format(Locale.ROOT, "World: (%d,%d,%d)", i, j, k));
        } catch (Throwable throwable) {
            stringbuilder.append("(Error finding world loc)");
        }

        stringbuilder.append(", ");

        int l;
        int i1;
        int j1;
        int k1;
        int l1;
        int i2;
        int j2;
        int k2;
        int l2;
        int i3;
        int j3;
        int k3;

        try {
            l = SectionPosition.blockToSectionCoord(i);
            i1 = SectionPosition.blockToSectionCoord(j);
            j1 = SectionPosition.blockToSectionCoord(k);
            k1 = i & 15;
            l1 = j & 15;
            i2 = k & 15;
            j2 = SectionPosition.sectionToBlockCoord(l);
            k2 = levelheightaccessor.getMinBuildHeight();
            l2 = SectionPosition.sectionToBlockCoord(j1);
            i3 = SectionPosition.sectionToBlockCoord(l + 1) - 1;
            j3 = levelheightaccessor.getMaxBuildHeight() - 1;
            k3 = SectionPosition.sectionToBlockCoord(j1 + 1) - 1;
            stringbuilder.append(String.format(Locale.ROOT, "Section: (at %d,%d,%d in %d,%d,%d; chunk contains blocks %d,%d,%d to %d,%d,%d)", k1, l1, i2, l, i1, j1, j2, k2, l2, i3, j3, k3));
        } catch (Throwable throwable1) {
            stringbuilder.append("(Error finding chunk loc)");
        }

        stringbuilder.append(", ");

        try {
            l = i >> 9;
            i1 = k >> 9;
            j1 = l << 5;
            k1 = i1 << 5;
            l1 = (l + 1 << 5) - 1;
            i2 = (i1 + 1 << 5) - 1;
            j2 = l << 9;
            k2 = levelheightaccessor.getMinBuildHeight();
            l2 = i1 << 9;
            i3 = (l + 1 << 9) - 1;
            j3 = levelheightaccessor.getMaxBuildHeight() - 1;
            k3 = (i1 + 1 << 9) - 1;
            stringbuilder.append(String.format(Locale.ROOT, "Region: (%d,%d; contains chunks %d,%d to %d,%d, blocks %d,%d,%d to %d,%d,%d)", l, i1, j1, k1, l1, i2, j2, k2, l2, i3, j3, k3));
        } catch (Throwable throwable2) {
            stringbuilder.append("(Error finding world loc)");
        }

        return stringbuilder.toString();
    }

    public CrashReportSystemDetails setDetail(String s, CrashReportCallable<String> crashreportcallable) {
        try {
            this.setDetail(s, crashreportcallable.call());
        } catch (Throwable throwable) {
            this.setDetailError(s, throwable);
        }

        return this;
    }

    public CrashReportSystemDetails setDetail(String s, Object object) {
        this.entries.add(new CrashReportSystemDetails.CrashReportDetail(s, object));
        return this;
    }

    public void setDetailError(String s, Throwable throwable) {
        this.setDetail(s, (Object) throwable);
    }

    public int fillInStackTrace(int i) {
        StackTraceElement[] astacktraceelement = Thread.currentThread().getStackTrace();

        if (astacktraceelement.length <= 0) {
            return 0;
        } else {
            this.stackTrace = new StackTraceElement[astacktraceelement.length - 3 - i];
            System.arraycopy(astacktraceelement, 3 + i, this.stackTrace, 0, this.stackTrace.length);
            return this.stackTrace.length;
        }
    }

    public boolean validateStackTrace(StackTraceElement stacktraceelement, StackTraceElement stacktraceelement1) {
        if (this.stackTrace.length != 0 && stacktraceelement != null) {
            StackTraceElement stacktraceelement2 = this.stackTrace[0];

            if (stacktraceelement2.isNativeMethod() == stacktraceelement.isNativeMethod() && stacktraceelement2.getClassName().equals(stacktraceelement.getClassName()) && stacktraceelement2.getFileName().equals(stacktraceelement.getFileName()) && stacktraceelement2.getMethodName().equals(stacktraceelement.getMethodName())) {
                if (stacktraceelement1 != null != this.stackTrace.length > 1) {
                    return false;
                } else if (stacktraceelement1 != null && !this.stackTrace[1].equals(stacktraceelement1)) {
                    return false;
                } else {
                    this.stackTrace[0] = stacktraceelement;
                    return true;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void trimStacktrace(int i) {
        StackTraceElement[] astacktraceelement = new StackTraceElement[this.stackTrace.length - i];

        System.arraycopy(this.stackTrace, 0, astacktraceelement, 0, astacktraceelement.length);
        this.stackTrace = astacktraceelement;
    }

    public void getDetails(StringBuilder stringbuilder) {
        stringbuilder.append("-- ").append(this.title).append(" --\n");
        stringbuilder.append("Details:");
        Iterator iterator = this.entries.iterator();

        while (iterator.hasNext()) {
            CrashReportSystemDetails.CrashReportDetail crashreportsystemdetails_crashreportdetail = (CrashReportSystemDetails.CrashReportDetail) iterator.next();

            stringbuilder.append("\n\t");
            stringbuilder.append(crashreportsystemdetails_crashreportdetail.getKey());
            stringbuilder.append(": ");
            stringbuilder.append(crashreportsystemdetails_crashreportdetail.getValue());
        }

        if (this.stackTrace != null && this.stackTrace.length > 0) {
            stringbuilder.append("\nStacktrace:");
            StackTraceElement[] astacktraceelement = this.stackTrace;
            int i = astacktraceelement.length;

            for (int j = 0; j < i; ++j) {
                StackTraceElement stacktraceelement = astacktraceelement[j];

                stringbuilder.append("\n\tat ");
                stringbuilder.append(stacktraceelement);
            }
        }

    }

    public StackTraceElement[] getStacktrace() {
        return this.stackTrace;
    }

    public static void populateBlockDetails(CrashReportSystemDetails crashreportsystemdetails, LevelHeightAccessor levelheightaccessor, BlockPosition blockposition, @Nullable IBlockData iblockdata) {
        if (iblockdata != null) {
            Objects.requireNonNull(iblockdata);
            crashreportsystemdetails.setDetail("Block", iblockdata::toString);
        }

        crashreportsystemdetails.setDetail("Block location", () -> {
            return formatLocation(levelheightaccessor, blockposition);
        });
    }

    private static class CrashReportDetail {

        private final String key;
        private final String value;

        public CrashReportDetail(String s, @Nullable Object object) {
            this.key = s;
            if (object == null) {
                this.value = "~~NULL~~";
            } else if (object instanceof Throwable) {
                Throwable throwable = (Throwable) object;
                String s1 = throwable.getClass().getSimpleName();

                this.value = "~~ERROR~~ " + s1 + ": " + throwable.getMessage();
            } else {
                this.value = object.toString();
            }

        }

        public String getKey() {
            return this.key;
        }

        public String getValue() {
            return this.value;
        }
    }
}
