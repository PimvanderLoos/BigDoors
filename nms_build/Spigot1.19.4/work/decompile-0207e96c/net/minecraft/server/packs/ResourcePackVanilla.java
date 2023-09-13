package net.minecraft.server.packs;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.FileUtils;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.packs.metadata.ResourcePackMetaParser;
import net.minecraft.server.packs.resources.IResource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.slf4j.Logger;

public class ResourcePackVanilla implements IResourcePack {

    private static final Logger LOGGER = LogUtils.getLogger();
    private final BuiltInMetadata metadata;
    private final Set<String> namespaces;
    private final List<Path> rootPaths;
    private final Map<EnumResourcePackType, List<Path>> pathsForType;

    ResourcePackVanilla(BuiltInMetadata builtinmetadata, Set<String> set, List<Path> list, Map<EnumResourcePackType, List<Path>> map) {
        this.metadata = builtinmetadata;
        this.namespaces = set;
        this.rootPaths = list;
        this.pathsForType = map;
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getRootResource(String... astring) {
        FileUtils.validatePath(astring);
        List<String> list = List.of(astring);
        Iterator iterator = this.rootPaths.iterator();

        Path path;

        do {
            if (!iterator.hasNext()) {
                return null;
            }

            Path path1 = (Path) iterator.next();

            path = FileUtils.resolvePath(path1, list);
        } while (!Files.exists(path, new LinkOption[0]) || !PathPackResources.validatePath(path));

        return IoSupplier.create(path);
    }

    public void listRawPaths(EnumResourcePackType enumresourcepacktype, MinecraftKey minecraftkey, Consumer<Path> consumer) {
        FileUtils.decomposePath(minecraftkey.getPath()).get().ifLeft((list) -> {
            String s = minecraftkey.getNamespace();
            Iterator iterator = ((List) this.pathsForType.get(enumresourcepacktype)).iterator();

            while (iterator.hasNext()) {
                Path path = (Path) iterator.next();
                Path path1 = path.resolve(s);

                consumer.accept(FileUtils.resolvePath(path1, list));
            }

        }).ifRight((partialresult) -> {
            ResourcePackVanilla.LOGGER.error("Invalid path {}: {}", minecraftkey, partialresult.message());
        });
    }

    @Override
    public void listResources(EnumResourcePackType enumresourcepacktype, String s, String s1, IResourcePack.a iresourcepack_a) {
        FileUtils.decomposePath(s1).get().ifLeft((list) -> {
            List<Path> list1 = (List) this.pathsForType.get(enumresourcepacktype);
            int i = list1.size();

            if (i == 1) {
                getResources(iresourcepack_a, s, (Path) list1.get(0), list);
            } else if (i > 1) {
                Map<MinecraftKey, IoSupplier<InputStream>> map = new HashMap();

                for (int j = 0; j < i - 1; ++j) {
                    Objects.requireNonNull(map);
                    getResources(map::putIfAbsent, s, (Path) list1.get(j), list);
                }

                Path path = (Path) list1.get(i - 1);

                if (map.isEmpty()) {
                    getResources(iresourcepack_a, s, path, list);
                } else {
                    Objects.requireNonNull(map);
                    getResources(map::putIfAbsent, s, path, list);
                    map.forEach(iresourcepack_a);
                }
            }

        }).ifRight((partialresult) -> {
            ResourcePackVanilla.LOGGER.error("Invalid path {}: {}", s1, partialresult.message());
        });
    }

    private static void getResources(IResourcePack.a iresourcepack_a, String s, Path path, List<String> list) {
        Path path1 = path.resolve(s);

        PathPackResources.listPath(s, path1, list, iresourcepack_a);
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getResource(EnumResourcePackType enumresourcepacktype, MinecraftKey minecraftkey) {
        return (IoSupplier) FileUtils.decomposePath(minecraftkey.getPath()).get().map((list) -> {
            String s = minecraftkey.getNamespace();
            Iterator iterator = ((List) this.pathsForType.get(enumresourcepacktype)).iterator();

            Path path;

            do {
                if (!iterator.hasNext()) {
                    return null;
                }

                Path path1 = (Path) iterator.next();

                path = FileUtils.resolvePath(path1.resolve(s), list);
            } while (!Files.exists(path, new LinkOption[0]) || !PathPackResources.validatePath(path));

            return IoSupplier.create(path);
        }, (partialresult) -> {
            ResourcePackVanilla.LOGGER.error("Invalid path {}: {}", minecraftkey, partialresult.message());
            return null;
        });
    }

    @Override
    public Set<String> getNamespaces(EnumResourcePackType enumresourcepacktype) {
        return this.namespaces;
    }

    @Nullable
    @Override
    public <T> T getMetadataSection(ResourcePackMetaParser<T> resourcepackmetaparser) {
        IoSupplier<InputStream> iosupplier = this.getRootResource("pack.mcmeta");

        if (iosupplier != null) {
            try {
                InputStream inputstream = (InputStream) iosupplier.get();

                label46:
                {
                    Object object;

                    try {
                        T t0 = ResourcePackAbstract.getMetadataFromStream(resourcepackmetaparser, inputstream);

                        if (t0 == null) {
                            break label46;
                        }

                        object = t0;
                    } catch (Throwable throwable) {
                        if (inputstream != null) {
                            try {
                                inputstream.close();
                            } catch (Throwable throwable1) {
                                throwable.addSuppressed(throwable1);
                            }
                        }

                        throw throwable;
                    }

                    if (inputstream != null) {
                        inputstream.close();
                    }

                    return object;
                }

                if (inputstream != null) {
                    inputstream.close();
                }
            } catch (IOException ioexception) {
                ;
            }
        }

        return this.metadata.get(resourcepackmetaparser);
    }

    @Override
    public String packId() {
        return "vanilla";
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public void close() {}

    public ResourceProvider asProvider() {
        return (minecraftkey) -> {
            return Optional.ofNullable(this.getResource(EnumResourcePackType.CLIENT_RESOURCES, minecraftkey)).map((iosupplier) -> {
                return new IResource(this, iosupplier);
            });
        };
    }
}
