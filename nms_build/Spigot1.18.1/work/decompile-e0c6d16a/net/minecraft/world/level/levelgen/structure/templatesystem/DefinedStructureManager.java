package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import net.minecraft.FileUtils;
import net.minecraft.ResourceKeyInvalidException;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.packs.resources.IResource;
import net.minecraft.server.packs.resources.IResourceManager;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.storage.Convertable;
import net.minecraft.world.level.storage.SavedFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefinedStructureManager {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String STRUCTURE_DIRECTORY_NAME = "structures";
    private static final String STRUCTURE_FILE_EXTENSION = ".nbt";
    private static final String STRUCTURE_TEXT_FILE_EXTENSION = ".snbt";
    public final Map<MinecraftKey, Optional<DefinedStructure>> structureRepository = Maps.newConcurrentMap();
    private final DataFixer fixerUpper;
    private IResourceManager resourceManager;
    private final Path generatedDir;

    public DefinedStructureManager(IResourceManager iresourcemanager, Convertable.ConversionSession convertable_conversionsession, DataFixer datafixer) {
        this.resourceManager = iresourcemanager;
        this.fixerUpper = datafixer;
        this.generatedDir = convertable_conversionsession.getLevelPath(SavedFile.GENERATED_DIR).normalize();
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
        return (Optional) this.structureRepository.computeIfAbsent(minecraftkey, (minecraftkey1) -> {
            Optional<DefinedStructure> optional = this.loadFromGenerated(minecraftkey1);

            return optional.isPresent() ? optional : this.loadFromResource(minecraftkey1);
        });
    }

    public void onResourceManagerReload(IResourceManager iresourcemanager) {
        this.resourceManager = iresourcemanager;
        this.structureRepository.clear();
    }

    public Optional<DefinedStructure> loadFromResource(MinecraftKey minecraftkey) {
        MinecraftKey minecraftkey1 = new MinecraftKey(minecraftkey.getNamespace(), "structures/" + minecraftkey.getPath() + ".nbt");

        try {
            IResource iresource = this.resourceManager.getResource(minecraftkey1);

            Optional optional;

            try {
                optional = Optional.of(this.readStructure(iresource.getInputStream()));
            } catch (Throwable throwable) {
                if (iresource != null) {
                    try {
                        iresource.close();
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                }

                throw throwable;
            }

            if (iresource != null) {
                iresource.close();
            }

            return optional;
        } catch (FileNotFoundException filenotfoundexception) {
            return Optional.empty();
        } catch (Throwable throwable2) {
            DefinedStructureManager.LOGGER.error("Couldn't load structure {}: {}", minecraftkey, throwable2.toString());
            return Optional.empty();
        }
    }

    public Optional<DefinedStructure> loadFromGenerated(MinecraftKey minecraftkey) {
        if (!this.generatedDir.toFile().isDirectory()) {
            return Optional.empty();
        } else {
            Path path = this.createAndValidatePathToStructure(minecraftkey, ".nbt");

            try {
                FileInputStream fileinputstream = new FileInputStream(path.toFile());

                Optional optional;

                try {
                    optional = Optional.of(this.readStructure((InputStream) fileinputstream));
                } catch (Throwable throwable) {
                    try {
                        fileinputstream.close();
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }

                    throw throwable;
                }

                fileinputstream.close();
                return optional;
            } catch (FileNotFoundException filenotfoundexception) {
                return Optional.empty();
            } catch (IOException ioexception) {
                DefinedStructureManager.LOGGER.error("Couldn't load structure from {}", path, ioexception);
                return Optional.empty();
            }
        }
    }

    public DefinedStructure readStructure(InputStream inputstream) throws IOException {
        NBTTagCompound nbttagcompound = NBTCompressedStreamTools.readCompressed(inputstream);

        return this.readStructure(nbttagcompound);
    }

    public DefinedStructure readStructure(NBTTagCompound nbttagcompound) {
        if (!nbttagcompound.contains("DataVersion", 99)) {
            nbttagcompound.putInt("DataVersion", 500);
        }

        DefinedStructure definedstructure = new DefinedStructure();

        definedstructure.load(GameProfileSerializer.update(this.fixerUpper, DataFixTypes.STRUCTURE, nbttagcompound, nbttagcompound.getInt("DataVersion")));
        return definedstructure;
    }

    public boolean save(MinecraftKey minecraftkey) {
        Optional<DefinedStructure> optional = (Optional) this.structureRepository.get(minecraftkey);

        if (!optional.isPresent()) {
            return false;
        } else {
            DefinedStructure definedstructure = (DefinedStructure) optional.get();
            Path path = this.createAndValidatePathToStructure(minecraftkey, ".nbt");
            Path path1 = path.getParent();

            if (path1 == null) {
                return false;
            } else {
                try {
                    Files.createDirectories(Files.exists(path1, new LinkOption[0]) ? path1.toRealPath() : path1);
                } catch (IOException ioexception) {
                    DefinedStructureManager.LOGGER.error("Failed to create parent directory: {}", path1);
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

    public Path createPathToStructure(MinecraftKey minecraftkey, String s) {
        try {
            Path path = this.generatedDir.resolve(minecraftkey.getNamespace());
            Path path1 = path.resolve("structures");

            return FileUtils.createPathToResource(path1, minecraftkey.getPath(), s);
        } catch (InvalidPathException invalidpathexception) {
            throw new ResourceKeyInvalidException("Invalid resource path: " + minecraftkey, invalidpathexception);
        }
    }

    public Path createAndValidatePathToStructure(MinecraftKey minecraftkey, String s) {
        if (minecraftkey.getPath().contains("//")) {
            throw new ResourceKeyInvalidException("Invalid resource path: " + minecraftkey);
        } else {
            Path path = this.createPathToStructure(minecraftkey, s);

            if (path.startsWith(this.generatedDir) && FileUtils.isPathNormalized(path) && FileUtils.isPathPortable(path)) {
                return path;
            } else {
                throw new ResourceKeyInvalidException("Invalid resource path: " + path);
            }
        }
    }

    public void remove(MinecraftKey minecraftkey) {
        this.structureRepository.remove(minecraftkey);
    }
}
