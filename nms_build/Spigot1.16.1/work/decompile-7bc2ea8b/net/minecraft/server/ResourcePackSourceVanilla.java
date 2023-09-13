package net.minecraft.server;

import java.util.function.Consumer;

public class ResourcePackSourceVanilla implements ResourcePackSource {

    private final ResourcePackVanilla a = new ResourcePackVanilla(new String[]{"minecraft"});

    public ResourcePackSourceVanilla() {}

    @Override
    public <T extends ResourcePackLoader> void a(Consumer<T> consumer, ResourcePackLoader.a<T> resourcepackloader_a) {
        T t0 = ResourcePackLoader.a("vanilla", false, () -> {
            return this.a;
        }, resourcepackloader_a, ResourcePackLoader.Position.BOTTOM, PackSource.b);

        if (t0 != null) {
            consumer.accept(t0);
        }

    }
}
