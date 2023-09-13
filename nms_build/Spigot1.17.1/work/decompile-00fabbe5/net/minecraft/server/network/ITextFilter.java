package net.minecraft.server.network;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ITextFilter {

    ITextFilter DUMMY = new ITextFilter() {
        @Override
        public void a() {}

        @Override
        public void b() {}

        @Override
        public CompletableFuture<ITextFilter.a> a(String s) {
            return CompletableFuture.completedFuture(ITextFilter.a.a(s));
        }

        @Override
        public CompletableFuture<List<ITextFilter.a>> a(List<String> list) {
            return CompletableFuture.completedFuture((List) list.stream().map(ITextFilter.a::a).collect(ImmutableList.toImmutableList()));
        }
    };

    void a();

    void b();

    CompletableFuture<ITextFilter.a> a(String s);

    CompletableFuture<List<ITextFilter.a>> a(List<String> list);

    public static class a {

        public static final ITextFilter.a EMPTY = new ITextFilter.a("", "");
        private final String raw;
        private final String filtered;

        public a(String s, String s1) {
            this.raw = s;
            this.filtered = s1;
        }

        public String a() {
            return this.raw;
        }

        public String b() {
            return this.filtered;
        }

        public static ITextFilter.a a(String s) {
            return new ITextFilter.a(s, s);
        }

        public static ITextFilter.a b(String s) {
            return new ITextFilter.a(s, "");
        }
    }
}
