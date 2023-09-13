package net.minecraft.server.packs.resources;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.resources.MinecraftKey;

public interface IResourceManager {

    IResource a(MinecraftKey minecraftkey) throws IOException;

    List<IResource> c(MinecraftKey minecraftkey) throws IOException;

    Collection<MinecraftKey> a(String s, Predicate<String> predicate);

    public static enum Empty implements IResourceManager {

        INSTANCE;

        private Empty() {}

        @Override
        public IResource a(MinecraftKey minecraftkey) throws IOException {
            throw new FileNotFoundException(minecraftkey.toString());
        }

        @Override
        public List<IResource> c(MinecraftKey minecraftkey) {
            return ImmutableList.of();
        }

        @Override
        public Collection<MinecraftKey> a(String s, Predicate<String> predicate) {
            return ImmutableSet.of();
        }
    }
}
