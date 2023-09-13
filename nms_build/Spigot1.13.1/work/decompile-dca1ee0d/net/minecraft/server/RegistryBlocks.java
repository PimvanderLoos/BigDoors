package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RegistryBlocks<V> extends RegistryMaterials<V> {

    private final MinecraftKey x;
    private V y;

    public RegistryBlocks(MinecraftKey minecraftkey) {
        this.x = minecraftkey;
    }

    public void a(int i, MinecraftKey minecraftkey, V v0) {
        if (this.x.equals(minecraftkey)) {
            this.y = v0;
        }

        super.a(i, minecraftkey, v0);
    }

    public int a(@Nullable V v0) {
        int i = super.a(v0);

        return i == -1 ? super.a(this.y) : i;
    }

    public MinecraftKey getKey(V v0) {
        MinecraftKey minecraftkey = super.getKey(v0);

        return minecraftkey == null ? this.x : minecraftkey;
    }

    public V getOrDefault(@Nullable MinecraftKey minecraftkey) {
        Object object = this.get(minecraftkey);

        return object == null ? this.y : object;
    }

    @Nonnull
    public V fromId(int i) {
        Object object = super.fromId(i);

        return object == null ? this.y : object;
    }

    @Nonnull
    public V a(Random random) {
        Object object = super.a(random);

        return object == null ? this.y : object;
    }

    public MinecraftKey b() {
        return this.x;
    }
}
