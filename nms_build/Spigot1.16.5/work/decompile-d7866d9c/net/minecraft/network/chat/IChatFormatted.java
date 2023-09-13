package net.minecraft.network.chat;

import java.util.Optional;
import net.minecraft.util.Unit;

public interface IChatFormatted {

    Optional<Unit> b = Optional.of(Unit.INSTANCE);
    IChatFormatted c = new IChatFormatted() {
        @Override
        public <T> Optional<T> a(IChatFormatted.a<T> ichatformatted_a) {
            return Optional.empty();
        }
    };

    <T> Optional<T> a(IChatFormatted.a<T> ichatformatted_a);

    static IChatFormatted b(final String s) {
        return new IChatFormatted() {
            @Override
            public <T> Optional<T> a(IChatFormatted.a<T> ichatformatted_a) {
                return ichatformatted_a.accept(s);
            }
        };
    }

    default String getString() {
        StringBuilder stringbuilder = new StringBuilder();

        this.a((s) -> {
            stringbuilder.append(s);
            return Optional.empty();
        });
        return stringbuilder.toString();
    }

    public interface a<T> {

        Optional<T> accept(String s);
    }
}
