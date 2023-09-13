package net.minecraft.network.chat;

import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.Unit;

public interface IChatFormatted {

    Optional<Unit> STOP_ITERATION = Optional.of(Unit.INSTANCE);
    IChatFormatted EMPTY = new IChatFormatted() {
        @Override
        public <T> Optional<T> a(IChatFormatted.a<T> ichatformatted_a) {
            return Optional.empty();
        }

        @Override
        public <T> Optional<T> a(IChatFormatted.b<T> ichatformatted_b, ChatModifier chatmodifier) {
            return Optional.empty();
        }
    };

    <T> Optional<T> a(IChatFormatted.a<T> ichatformatted_a);

    <T> Optional<T> a(IChatFormatted.b<T> ichatformatted_b, ChatModifier chatmodifier);

    static IChatFormatted b(final String s) {
        return new IChatFormatted() {
            @Override
            public <T> Optional<T> a(IChatFormatted.a<T> ichatformatted_a) {
                return ichatformatted_a.accept(s);
            }

            @Override
            public <T> Optional<T> a(IChatFormatted.b<T> ichatformatted_b, ChatModifier chatmodifier) {
                return ichatformatted_b.accept(chatmodifier, s);
            }
        };
    }

    static IChatFormatted a(final String s, final ChatModifier chatmodifier) {
        return new IChatFormatted() {
            @Override
            public <T> Optional<T> a(IChatFormatted.a<T> ichatformatted_a) {
                return ichatformatted_a.accept(s);
            }

            @Override
            public <T> Optional<T> a(IChatFormatted.b<T> ichatformatted_b, ChatModifier chatmodifier1) {
                return ichatformatted_b.accept(chatmodifier.setChatModifier(chatmodifier1), s);
            }
        };
    }

    static IChatFormatted a(IChatFormatted... aichatformatted) {
        return a((List) ImmutableList.copyOf(aichatformatted));
    }

    static IChatFormatted a(final List<? extends IChatFormatted> list) {
        return new IChatFormatted() {
            @Override
            public <T> Optional<T> a(IChatFormatted.a<T> ichatformatted_a) {
                Iterator iterator = list.iterator();

                Optional optional;

                do {
                    if (!iterator.hasNext()) {
                        return Optional.empty();
                    }

                    IChatFormatted ichatformatted = (IChatFormatted) iterator.next();

                    optional = ichatformatted.a(ichatformatted_a);
                } while (!optional.isPresent());

                return optional;
            }

            @Override
            public <T> Optional<T> a(IChatFormatted.b<T> ichatformatted_b, ChatModifier chatmodifier) {
                Iterator iterator = list.iterator();

                Optional optional;

                do {
                    if (!iterator.hasNext()) {
                        return Optional.empty();
                    }

                    IChatFormatted ichatformatted = (IChatFormatted) iterator.next();

                    optional = ichatformatted.a(ichatformatted_b, chatmodifier);
                } while (!optional.isPresent());

                return optional;
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

    public interface b<T> {

        Optional<T> accept(ChatModifier chatmodifier, String s);
    }
}
