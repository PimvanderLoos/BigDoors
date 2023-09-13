package net.minecraft;

import java.util.Objects;

@FunctionalInterface
public interface CharPredicate {

    boolean test(char c0);

    default CharPredicate a(CharPredicate charpredicate) {
        Objects.requireNonNull(charpredicate);
        return (c0) -> {
            return this.test(c0) && charpredicate.test(c0);
        };
    }

    default CharPredicate a() {
        return (c0) -> {
            return !this.test(c0);
        };
    }

    default CharPredicate b(CharPredicate charpredicate) {
        Objects.requireNonNull(charpredicate);
        return (c0) -> {
            return this.test(c0) || charpredicate.test(c0);
        };
    }
}
