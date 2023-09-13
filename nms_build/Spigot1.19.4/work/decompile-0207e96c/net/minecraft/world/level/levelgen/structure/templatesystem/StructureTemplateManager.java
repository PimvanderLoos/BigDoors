package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Maps;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.FileUtils;
import net.minecraft.ResourceKeyInvalidException;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderGetter;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.packs.resources.IResourceManager;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.Convertable;
import net.minecraft.world.level.storage.SavedFile;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class StructureTemplateManager {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String STRUCTURE_DIRECTORY_NAME = "structures";
    private static final String TEST_STRUCTURES_DIR = "gameteststructures";
    private static final String STRUCTURE_FILE_EXTENSION = ".nbt";
    private static final String STRUCTURE_TEXT_FILE_EXTENSION = ".snbt";
    public final Map<MinecraftKey, Optional<DefinedStructure>> structureRepository = Maps.newConcurrentMap();
    private final DataFixer fixerUpper;
    private IResourceManager resourceManager;
    private final Path generatedDir;
    private final List<StructureTemplateManager.b> sources;
    private final HolderGetter<Block> blockLookup;
    private static final FileToIdConverter LISTER = new FileToIdConverter("structures", ".nbt");

    public StructureTemplateManager(IResourceManager iresourcemanager, Convertable.ConversionSession convertable_conversionsession, DataFixer datafixer, HolderGetter<Block> holdergetter) {
        this.resourceManager = iresourcemanager;
        this.fixerUpper = datafixer;
        this.generatedDir = convertable_conversionsession.getLevelPath(SavedFile.GENERATED_DIR).normalize();
        this.blockLookup = holdergetter;
        Builder<StructureTemplateManager.b> builder = ImmutableList.builder();

        builder.add(new StructureTemplateManager.b(this::loadFromGenerated, this::listGenerated));
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            builder.add(new StructureTemplateManager.b(this::loadFromTestStructures, this::listTestStructures));
        }

        builder.add(new StructureTemplateManager.b(this::loadFromResource, this::listResources));
        this.sources = builder.build();
    }

    public DefinedStructure getOrCreate(MinecraftKey minecraftkey) {
        Optional<DefinedStructure> optional = this.get(minecraftkey);

        if (optional.isPresent()) {
            return (DefinedStructure) optional.get();
        } else {
            DefinedStructure definedstructure = new DefinedStructure();

            this.structureRepository.put(minecraftkey, Optional.of(definedstructure));
            return definedstructure;
        }
    }

    public Optional<DefinedStructure> get(MinecraftKey minecraftkey) {
        return (Optional) this.structureRepository.computeIfAbsent(minecraftkey, this::tryLoad);
    }

    public Stream<MinecraftKey> listTemplates() {
        return this.sources.stream().flatMap((structuretemplatemanager_b) -> {
            return (Stream) structuretemplatemanager_b.lister().get();
        }).distinct();
    }

    private Optional<DefinedStructure> tryLoad(MinecraftKey minecraftkey) {
        Iterator iterator = this.sources.iterator();

        while (iterator.hasNext()) {
            StructureTemplateManager.b structuretemplatemanager_b = (StructureTemplateManager.b) iterator.next();

            try {
                Optional<DefinedStructure> optional = (Optional) structuretemplatemanager_b.loader().apply(minecraftkey);

                if (optional.isPresent()) {
                    return optional;
                }
            } catch (Exception exception) {
                ;
            }
        }

        return Optional.empty();
    }

    public void onResourceManagerReload(IResourceManager iresourcemanager) {
        this.resourceManager = iresourcemanager;
        this.structureRepository.clear();
    }

    public Optional<DefinedStructure> loadFromResource(MinecraftKey minecraftkey) {
        MinecraftKey minecraftkey1 = StructureTemplateManager.LISTER.idToFile(minecraftkey);

        return this.load(() -> {
            return this.resourceManager.open(minecraftkey1);
        }, (throwable) -> {
            StructureTemplateManager.LOGGER.error("Couldn't load structure {}", minecraftkey, throwable);
        });
    }

    private Stream<MinecraftKey> listResources() {
        Stream stream = StructureTemplateManager.LISTER.listMatchingResources(this.resourceManager).keySet().stream();
        FileToIdConverter filetoidconverter = StructureTemplateManager.LISTER;

        Objects.requireNonNull(filetoidconverter);
        return stream.map(filetoidconverter::fileToId);
    }

    private Optional<DefinedStructure> loadFromTestStructures(MinecraftKey minecraftkey) {
        return this.loadFromSnbt(minecraftkey, Paths.get("gameteststructures"));
    }

    private Stream<MinecraftKey> listTestStructures() {
        return this.listFolderContents(Paths.get("gameteststructures"), "minecraft", ".snbt");
    }

    public Optional<DefinedStructure> loadFromGenerated(MinecraftKey minecraftkey) {
        if (!Files.isDirectory(this.generatedDir, new LinkOption[0])) {
            return Optional.empty();
        } else {
            Path path = createAndValidatePathToStructure(this.generatedDir, minecraftkey, ".nbt");

            return this.load(() -> {
                return new FileInputStream(path.toFile());
            }, (throwable) -> {
                StructureTemplateManager.LOGGER.error("Couldn't load structure from {}", path, throwable);
            });
        }
    }

    private Stream<MinecraftKey> listGenerated() {
        if (!Files.isDirectory(this.generatedDir, new LinkOption[0])) {
            return Stream.empty();
        } else {
            try {
                return Files.list(this.generatedDir).filter((path) -> {
                    return Files.isDirectory(path, new LinkOption[0]);
                }).flatMap((path) -> {
                    return this.listGeneratedInNamespace(path);
                });
            } catch (IOException ioexception) {
                return Stream.empty();
            }
        }
    }

    private Stream<MinecraftKey> listGeneratedInNamespace(Path path) {
        Path path1 = path.resolve("structures");

        return this.listFolderContents(path1, path.getFileName().toString(), ".nbt");
    }

    private Stream<MinecraftKey> listFolderContents(Path path, String s, String s1) {
        if (!Files.isDirectory(path, new LinkOption[0])) {
            return Stream.empty();
        } else {
            int i = s1.length();
            Function function = (s2) -> {
                return s2.substring(0, s2.length() - i);
            };

            try {
                return Files.walk(path).filter((path1) -> {
                    return path1.toString().endsWith(s1);
                }).mapMulti((path1, consumer) -> {
                    try {
                        consumer.accept(new MinecraftKey(s, (String) function.apply(this.relativize(path, path1))));
                    } catch (ResourceKeyInvalidException resourcekeyinvalidexception) {
                        StructureTemplateManager.LOGGER.error("Invalid location while listing pack contents", resourcekeyinvalidexception);
                    }

                });
            } catch (IOException ioexception) {
                StructureTemplateManager.LOGGER.error("Failed to list folder contents", ioexception);
                return Stream.empty();
            }
        }
    }

    private String relativize(Path path, Path path1) {
        return path.relativize(path1).toString().replace(File.separator, "/");
    }

    private Optional<DefinedStructure> loadFromSnbt(MinecraftKey minecraftkey, Path path) {
        if (!Files.isDirectory(path, new LinkOption[0])) {
            return Optional.empty();
        } else {
            Path path1 = FileUtils.createPathToResource(path, minecraftkey.getPath(), ".snbt");

            try {
                BufferedReader bufferedreader = Files.newBufferedReader(path1);

                Optional optional;

                try {
                    String s = IOUtils.toString(bufferedreader);

                    optional = Optional.of(this.readStructure(GameProfileSerializer.snbtToStructure(s)));
                } catch (Throwable throwable) {
                    if (bufferedreader != null) {
                        try {
                            bufferedreader.close();
                        } catch (Throwable throwable1) {
                            throwable.addSuppressed(throwable1);
                        }
                    }

                    throw throwable;
                }

                if (bufferedreader != null) {
                    bufferedreader.close();
                }

                return optional;
            } catch (NoSuchFileException nosuchfileexception) {
                return Optional.empty();
            } catch (CommandSyntaxException | IOException ioexception) {
                StructureTemplateManager.LOGGER.error("Couldn't load structure from {}", path1, ioexception);
                return Optional.empty();
            }
        }
    }

    private Optional<DefinedStructure> load(StructureTemplateManager.a structuretemplatemanager_a, Consumer<Throwable> consumer) {
        try {
            InputStream inputstream = structuretemplatemanager_a.open();

            Optional optional;

            try {
                optional = Optional.of(this.readStructure(inputstream));
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

            return optional;
        } catch (FileNotFoundException filenotfoundexception) {
            return Optional.empty();
        } catch (Throwable throwable2) {
            consumer.accept(throwable2);
            return Optional.empty();
        }
    }

    public DefinedStructure readStructure(InputStream inputstream) throws IOException {
        NBTTagCompound nbttagcompound = NBTCompressedStreamTools.readCompressed(inputstream);

        return this.readStructure(nbttagcompound);
    }

    public DefinedStructure readStructure(NBTTagCompound nbttagcompound) {
        DefinedStructure definedstructure = new DefinedStructure();
        int i = GameProfileSerializer.getDataVersion(nbttagcompound, 500);

        definedstructure.load(this.blockLookup, DataFixTypes.STRUCTURE.updateToCurrentVersion(this.fixerUpper, nbttagcompound, i));
        return definedstructure;
    }

    public boolean save(MinecraftKey minecraftkey) {
        Optional<DefinedStructure> optional = (Optional) this.structureRepository.get(minecraftkey);

        if (!optional.isPresent()) {
            return false;
        } else {
            DefinedStructure definedstructure = (DefinedStructure) optional.get();
            Path path = createAndValidatePathToStructure(this.generatedDir, minecraftkey, ".nbt");
            Path path1 = path.getParent();

            if (path1 == null) {
                return false;
            } else {
                try {
                    Files.createDirectories(Files.exists(path1, new LinkOption[0]) ? path1.toRealPath() : path1);
                } catch (IOException ioexception) {
                    StructureTemplateManager.LOGGER.error("Failed to create parent directory: {}", path1);
                    return false;
                }

                NBTTagCompound nbttagcompound = definedstructure.save(new NBTTagCompound());

                try {
                    FileOutputStream fileoutputstream = new FileOutputStream(path.toFile());

                    try {
                        NBTCompressedStreamTools.writeCompressed(nbttagcompound, (OutputStream) fileoutputstream);
                    } catch (Throwable throwable) {
                        try {
                            fileoutputstream.close();
                        } catch (Throwable throwable1) {
                            throwable.addSuppressed(throwable1);
                        }

                        throw throwable;
                    }

                    fileoutputstream.close();
                    return true;
                } catch (Throwable throwable2) {
                    return false;
                }
            }
        }
    }

    public Path getPathToGeneratedStructure(MinecraftKey minecraftkey, String s) {
        return createPathToStructure(this.generatedDir, minecraftkey, s);
    }

    public static Path createPathToStructure(Path path, MinecraftKey minecraftkey, String s) {
        try {
            Path path1 = path.resolve(minecraftkey.getNamespace());
            Path path2 = path1.resolve("structures");

            return FileUtils.createPathToResource(path2, minecraftkey.getPath(), s);
        } catch (InvalidPathException invalidpathexception) {
            throw new ResourceKeyInvalidException("Invalid resource path: " + minecraftkey, invalidpathexception);
        }
    }

    public static Path createAndValidatePathToStructure(Path path, MinecraftKey minecraftkey, String s) {
        if (minecraftkey.getPath().contains("//")) {
            throw new ResourceKeyInvalidException("Invalid resource path: " + minecraftkey);
        } else {
            Path path1 = createPathToStructure(path, minecraftkey, s);

            if (path1.startsWith(path) && FileUtils.isPathNormalized(path1) && FileUtils.isPathPortable(path1)) {
                return path1;
            } else {
                throw new ResourceKeyInvalidException("Invalid resource path: " + path1);
            }
        }
    }

    public void remove(MinecraftKey minecraftkey) {
        this.structureRepository.remove(minecraftkey);
    }

    private static record b(Function<MinecraftKey, Optional<DefinedStructure>> loader, Supplier<Stream<MinecraftKey>> lister) {

    }

    @FunctionalInterface
    private interface a {

        InputStream open() throws IOException;
    }
}
