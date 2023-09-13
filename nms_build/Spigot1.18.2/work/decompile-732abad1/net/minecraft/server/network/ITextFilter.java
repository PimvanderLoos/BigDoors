package net.minecraft.server.network;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ITextFilter {

    ITextFilter DUMMY = new ITextFilter() {
        @Override
        public void join() {}

        @Override
        public void leave() {}

        @Override
        public CompletableFuture<ITextFilter.a> processStreamMessage(String s) {
            return CompletableFuture.completedFuture(ITextFilter.a.passThrough(s));
        }

        @Override
        public CompletableFuture<List<ITextFilter.a>> processMessageBundle(List<String> list) {
            return CompletableFuture.completedFuture((List) list.stream().map(ITextFilter.a::passThrough).collect(ImmutableList.toImmutableList()));
        }
    };

    void join();

    void leave();

    CompletableFuture<ITextFilter.a> processStreamMessage(String s);

    CompletableFuture<List<ITextFilter.a>> processMessageBundle(List<String> list);

    public static class a {

        public static final ITextFilter.a EMPTY = new ITextFilter.a("", "");
        private final String raw;
        private final String filtered;

        public a(String s, String s1) {
            this.raw = s;
            this.filtered = s1;
        }

        public String getRaw() {
            return this.raw;
        }

        public String getFiltered() {
            return this.filtered;
        }

        public static ITextFilter.a passThrough(String s) {
            return new ITextFilter.a(s, s);
        }

        public static ITextFilter.a fullyFiltered(String s) {
            return new ITextFilter.a(s, "");
        }
    }
}
