package net.minecraft.server.network;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface ITextFilter {

    void a();

    void b();

    CompletableFuture<Optional<String>> a(String s);

    CompletableFuture<Optional<List<String>>> a(List<String> list);
}
