package net.minecraft.util;

import java.util.function.Supplier;
import org.apache.commons.lang3.ObjectUtils;

public record ModCheck(ModCheck.a confidence, String description) {

    public static ModCheck identify(String s, Supplier<String> supplier, String s1, Class<?> oclass) {
        String s2 = (String) supplier.get();

        return !s.equals(s2) ? new ModCheck(ModCheck.a.DEFINITELY, s1 + " brand changed to '" + s2 + "'") : (oclass.getSigners() == null ? new ModCheck(ModCheck.a.VERY_LIKELY, s1 + " jar signature invalidated") : new ModCheck(ModCheck.a.PROBABLY_NOT, s1 + " jar signature and brand is untouched"));
    }

    public boolean shouldReportAsModified() {
        return this.confidence.shouldReportAsModified;
    }

    public ModCheck merge(ModCheck modcheck) {
        return new ModCheck((ModCheck.a) ObjectUtils.max(new ModCheck.a[]{this.confidence, modcheck.confidence}), this.description + "; " + modcheck.description);
    }

    public String fullDescription() {
        return this.confidence.description + " " + this.description;
    }

    public static enum a {

        PROBABLY_NOT("Probably not.", false), VERY_LIKELY("Very likely;", true), DEFINITELY("Definitely;", true);

        final String description;
        final boolean shouldReportAsModified;

        private a(String s, boolean flag) {
            this.description = s;
            this.shouldReportAsModified = flag;
        }
    }
}
