package net.minecraft.server;

import java.util.function.Consumer;

public interface ResourcePackSource {

    <T extends ResourcePackLoader> void a(Consumer<T> consumer, ResourcePackLoader.a<T> resourcepackloader_a);
}
