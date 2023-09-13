package net.minecraft.server.packs.resources;

import java.util.concurrent.CompletableFuture;
import net.minecraft.util.Unit;

public interface IReloadable {

    CompletableFuture<Unit> a();

    float b();

    boolean c();

    void d();
}
