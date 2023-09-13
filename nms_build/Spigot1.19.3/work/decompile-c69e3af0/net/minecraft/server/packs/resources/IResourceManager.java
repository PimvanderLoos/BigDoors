package net.minecraft.server.packs.resources;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.packs.IResourcePack;

public interface IResourceManager extends ResourceProvider {

    Set<String> getNamespaces();

    List<IResource> getResourceStack(MinecraftKey minecraftkey);

    Map<MinecraftKey, IResource> listResources(String s, Predicate<MinecraftKey> predicate);

    Map<MinecraftKey, List<IResource>> listResourceStacks(String s, Predicate<MinecraftKey> predicate);

    Stream<IResourcePack> listPacks();

    public static enum Empty implements IResourceManager {

        INSTANCE;

        private Empty() {}

        @Override
        public Set<String> getNamespaces() {
            return Set.of();
        }

        @Override
        public Optional<IResource> getResource(MinecraftKey minecraftkey) {
            return Optional.empty();
        }

        @Override
        public List<IResource> getResourceStack(MinecraftKey minecraftkey) {
            return List.of();
        }

        @Override
        public Map<MinecraftKey, IResource> listResources(String s, Predicate<MinecraftKey> predicate) {
            return Map.of();
        }

        @Override
        public Map<MinecraftKey, List<IResource>> listResourceStacks(String s, Predicate<MinecraftKey> predicate) {
            return Map.of();
        }

        @Override
        public Stream<IResourcePack> listPacks() {
            return Stream.of();
        }
    }
}
