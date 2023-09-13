package net.minecraft;

import java.util.Objects;

@FunctionalInterface
public interface CharPredicate {

    boolean test(char c0);

    default CharPredicate and(CharPredicate charpredicate) {
        Objects.requireNonNull(charpredicate);
        return (c0) -> {
            return this.test(c0) && charpredicate.test(c0);
        };
    }

    default CharPredicate negate() {
        return (c0) -> {
            return !this.test(c0);
        };
    }

    default CharPredicate or(CharPredicate charpredicate) {
        Objects.requireNonNull(charpredicate);
        return (c0) -> {
            return this.test(c0) || charpredicate.test(c0);
        };
    }
}
