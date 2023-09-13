package net.minecraft.server.packs.resources;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.packs.IResourcePack;

public interface IResourceManager extends ResourceProvider {

    Set<String> a();

    boolean b(MinecraftKey minecraftkey);

    List<IResource> c(MinecraftKey minecraftkey) throws IOException;

    Collection<MinecraftKey> a(String s, Predicate<String> predicate);

    Stream<IResourcePack> b();

    public static enum Empty implements IResourceManager {

        INSTANCE;

        private Empty() {}

        @Override
        public Set<String> a() {
            return ImmutableSet.of();
        }

        @Override
        public IResource a(MinecraftKey minecraftkey) throws IOException {
            throw new FileNotFoundException(minecraftkey.toString());
        }

        @Override
        public boolean b(MinecraftKey minecraftkey) {
            return false;
        }

        @Override
        public List<IResource> c(MinecraftKey minecraftkey) {
            return ImmutableList.of();
        }

        @Override
        public Collection<MinecraftKey> a(String s, Predicate<String> predicate) {
            return ImmutableSet.of();
        }

        @Override
        public Stream<IResourcePack> b() {
            return Stream.of();
        }
    }
}
