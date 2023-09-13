package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Optional;
import net.minecraft.ResourceKeyInvalidException;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.commands.arguments.ArgumentMinecraftKeyRegistered;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.commands.arguments.TemplateMirrorArgument;
import net.minecraft.commands.arguments.TemplateRotationArgument;
import net.minecraft.commands.arguments.coordinates.ArgumentPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.SectionPosition;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.block.EnumBlockMirror;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.block.entity.TileEntityStructure;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pools.WorldGenFeatureDefinedStructureJigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.WorldGenFeatureDefinedStructurePoolTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureInfo;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessorRotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class PlaceCommand {

    private static final SimpleCommandExceptionType ERROR_FEATURE_FAILED = new SimpleCommandExceptionType(IChatBaseComponent.translatable("commands.place.feature.failed"));
    private static final SimpleCommandExceptionType ERROR_JIGSAW_FAILED = new SimpleCommandExceptionType(IChatBaseComponent.translatable("commands.place.jigsaw.failed"));
    private static final SimpleCommandExceptionType ERROR_STRUCTURE_FAILED = new SimpleCommandExceptionType(IChatBaseComponent.translatable("commands.place.structure.failed"));
    private static final DynamicCommandExceptionType ERROR_TEMPLATE_INVALID = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("commands.place.template.invalid", object);
    });
    private static final SimpleCommandExceptionType ERROR_TEMPLATE_FAILED = new SimpleCommandExceptionType(IChatBaseComponent.translatable("commands.place.template.failed"));
    private static final SuggestionProvider<CommandListenerWrapper> SUGGEST_TEMPLATES = (commandcontext, suggestionsbuilder) -> {
        StructureTemplateManager structuretemplatemanager = ((CommandListenerWrapper) commandcontext.getSource()).getLevel().getStructureManager();

        return ICompletionProvider.suggestResource(structuretemplatemanager.listTemplates(), suggestionsbuilder);
    };

    public PlaceCommand() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("place").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.literal("feature").then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("feature", ResourceKeyArgument.key(IRegistry.CONFIGURED_FEATURE_REGISTRY)).executes((commandcontext) -> {
            return placeFeature((CommandListenerWrapper) commandcontext.getSource(), ResourceKeyArgument.getConfiguredFeature(commandcontext, "feature"), new BlockPosition(((CommandListenerWrapper) commandcontext.getSource()).getPosition()));
        })).then(net.minecraft.commands.CommandDispatcher.argument("pos", ArgumentPosition.blockPos()).executes((commandcontext) -> {
            return placeFeature((CommandListenerWrapper) commandcontext.getSource(), ResourceKeyArgument.getConfiguredFeature(commandcontext, "feature"), ArgumentPosition.getLoadedBlockPos(commandcontext, "pos"));
        }))))).then(net.minecraft.commands.CommandDispatcher.literal("jigsaw").then(net.minecraft.commands.CommandDispatcher.argument("pool", ResourceKeyArgument.key(IRegistry.TEMPLATE_POOL_REGISTRY)).then(net.minecraft.commands.CommandDispatcher.argument("target", ArgumentMinecraftKeyRegistered.id()).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("max_depth", IntegerArgumentType.integer(1, 7)).executes((commandcontext) -> {
            return placeJigsaw((CommandListenerWrapper) commandcontext.getSource(), ResourceKeyArgument.getStructureTemplatePool(commandcontext, "pool"), ArgumentMinecraftKeyRegistered.getId(commandcontext, "target"), IntegerArgumentType.getInteger(commandcontext, "max_depth"), new BlockPosition(((CommandListenerWrapper) commandcontext.getSource()).getPosition()));
        })).then(net.minecraft.commands.CommandDispatcher.argument("position", ArgumentPosition.blockPos()).executes((commandcontext) -> {
            return placeJigsaw((CommandListenerWrapper) commandcontext.getSource(), ResourceKeyArgument.getStructureTemplatePool(commandcontext, "pool"), ArgumentMinecraftKeyRegistered.getId(commandcontext, "target"), IntegerArgumentType.getInteger(commandcontext, "max_depth"), ArgumentPosition.getLoadedBlockPos(commandcontext, "position"));
        }))))))).then(net.minecraft.commands.CommandDispatcher.literal("structure").then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("structure", ResourceKeyArgument.key(IRegistry.STRUCTURE_REGISTRY)).executes((commandcontext) -> {
            return placeStructure((CommandListenerWrapper) commandcontext.getSource(), ResourceKeyArgument.getStructure(commandcontext, "structure"), new BlockPosition(((CommandListenerWrapper) commandcontext.getSource()).getPosition()));
        })).then(net.minecraft.commands.CommandDispatcher.argument("pos", ArgumentPosition.blockPos()).executes((commandcontext) -> {
            return placeStructure((CommandListenerWrapper) commandcontext.getSource(), ResourceKeyArgument.getStructure(commandcontext, "structure"), ArgumentPosition.getLoadedBlockPos(commandcontext, "pos"));
        }))))).then(net.minecraft.commands.CommandDispatcher.literal("template").then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("template", ArgumentMinecraftKeyRegistered.id()).suggests(PlaceCommand.SUGGEST_TEMPLATES).executes((commandcontext) -> {
            return placeTemplate((CommandListenerWrapper) commandcontext.getSource(), ArgumentMinecraftKeyRegistered.getId(commandcontext, "template"), new BlockPosition(((CommandListenerWrapper) commandcontext.getSource()).getPosition()), EnumBlockRotation.NONE, EnumBlockMirror.NONE, 1.0F, 0);
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("pos", ArgumentPosition.blockPos()).executes((commandcontext) -> {
            return placeTemplate((CommandListenerWrapper) commandcontext.getSource(), ArgumentMinecraftKeyRegistered.getId(commandcontext, "template"), ArgumentPosition.getLoadedBlockPos(commandcontext, "pos"), EnumBlockRotation.NONE, EnumBlockMirror.NONE, 1.0F, 0);
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("rotation", TemplateRotationArgument.templateRotation()).executes((commandcontext) -> {
            return placeTemplate((CommandListenerWrapper) commandcontext.getSource(), ArgumentMinecraftKeyRegistered.getId(commandcontext, "template"), ArgumentPosition.getLoadedBlockPos(commandcontext, "pos"), TemplateRotationArgument.getRotation(commandcontext, "rotation"), EnumBlockMirror.NONE, 1.0F, 0);
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("mirror", TemplateMirrorArgument.templateMirror()).executes((commandcontext) -> {
            return placeTemplate((CommandListenerWrapper) commandcontext.getSource(), ArgumentMinecraftKeyRegistered.getId(commandcontext, "template"), ArgumentPosition.getLoadedBlockPos(commandcontext, "pos"), TemplateRotationArgument.getRotation(commandcontext, "rotation"), TemplateMirrorArgument.getMirror(commandcontext, "mirror"), 1.0F, 0);
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("integrity", FloatArgumentType.floatArg(0.0F, 1.0F)).executes((commandcontext) -> {
            return placeTemplate((CommandListenerWrapper) commandcontext.getSource(), ArgumentMinecraftKeyRegistered.getId(commandcontext, "template"), ArgumentPosition.getLoadedBlockPos(commandcontext, "pos"), TemplateRotationArgument.getRotation(commandcontext, "rotation"), TemplateMirrorArgument.getMirror(commandcontext, "mirror"), FloatArgumentType.getFloat(commandcontext, "integrity"), 0);
        })).then(net.minecraft.commands.CommandDispatcher.argument("seed", IntegerArgumentType.integer()).executes((commandcontext) -> {
            return placeTemplate((CommandListenerWrapper) commandcontext.getSource(), ArgumentMinecraftKeyRegistered.getId(commandcontext, "template"), ArgumentPosition.getLoadedBlockPos(commandcontext, "pos"), TemplateRotationArgument.getRotation(commandcontext, "rotation"), TemplateMirrorArgument.getMirror(commandcontext, "mirror"), FloatArgumentType.getFloat(commandcontext, "integrity"), IntegerArgumentType.getInteger(commandcontext, "seed"));
        })))))))));
    }

    public static int placeFeature(CommandListenerWrapper commandlistenerwrapper, Holder<WorldGenFeatureConfigured<?, ?>> holder, BlockPosition blockposition) throws CommandSyntaxException {
        WorldServer worldserver = commandlistenerwrapper.getLevel();
        WorldGenFeatureConfigured<?, ?> worldgenfeatureconfigured = (WorldGenFeatureConfigured) holder.value();
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(blockposition);

        checkLoaded(worldserver, new ChunkCoordIntPair(chunkcoordintpair.x - 1, chunkcoordintpair.z - 1), new ChunkCoordIntPair(chunkcoordintpair.x + 1, chunkcoordintpair.z + 1));
        if (!worldgenfeatureconfigured.place(worldserver, worldserver.getChunkSource().getGenerator(), worldserver.getRandom(), blockposition)) {
            throw PlaceCommand.ERROR_FEATURE_FAILED.create();
        } else {
            String s = (String) holder.unwrapKey().map((resourcekey) -> {
                return resourcekey.location().toString();
            }).orElse("[unregistered]");

            commandlistenerwrapper.sendSuccess(IChatBaseComponent.translatable("commands.place.feature.success", s, blockposition.getX(), blockposition.getY(), blockposition.getZ()), true);
            return 1;
        }
    }

    public static int placeJigsaw(CommandListenerWrapper commandlistenerwrapper, Holder<WorldGenFeatureDefinedStructurePoolTemplate> holder, MinecraftKey minecraftkey, int i, BlockPosition blockposition) throws CommandSyntaxException {
        WorldServer worldserver = commandlistenerwrapper.getLevel();

        if (!WorldGenFeatureDefinedStructureJigsawPlacement.generateJigsaw(worldserver, holder, minecraftkey, i, blockposition, false)) {
            throw PlaceCommand.ERROR_JIGSAW_FAILED.create();
        } else {
            commandlistenerwrapper.sendSuccess(IChatBaseComponent.translatable("commands.place.jigsaw.success", blockposition.getX(), blockposition.getY(), blockposition.getZ()), true);
            return 1;
        }
    }

    public static int placeStructure(CommandListenerWrapper commandlistenerwrapper, Holder<Structure> holder, BlockPosition blockposition) throws CommandSyntaxException {
        WorldServer worldserver = commandlistenerwrapper.getLevel();
        Structure structure = (Structure) holder.value();
        ChunkGenerator chunkgenerator = worldserver.getChunkSource().getGenerator();
        StructureStart structurestart = structure.generate(commandlistenerwrapper.registryAccess(), chunkgenerator, chunkgenerator.getBiomeSource(), worldserver.getChunkSource().randomState(), worldserver.getStructureManager(), worldserver.getSeed(), new ChunkCoordIntPair(blockposition), 0, worldserver, (holder1) -> {
            return true;
        });

        if (!structurestart.isValid()) {
            throw PlaceCommand.ERROR_STRUCTURE_FAILED.create();
        } else {
            StructureBoundingBox structureboundingbox = structurestart.getBoundingBox();
            ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(SectionPosition.blockToSectionCoord(structureboundingbox.minX()), SectionPosition.blockToSectionCoord(structureboundingbox.minZ()));
            ChunkCoordIntPair chunkcoordintpair1 = new ChunkCoordIntPair(SectionPosition.blockToSectionCoord(structureboundingbox.maxX()), SectionPosition.blockToSectionCoord(structureboundingbox.maxZ()));

            checkLoaded(worldserver, chunkcoordintpair, chunkcoordintpair1);
            ChunkCoordIntPair.rangeClosed(chunkcoordintpair, chunkcoordintpair1).forEach((chunkcoordintpair2) -> {
                structurestart.placeInChunk(worldserver, worldserver.structureManager(), chunkgenerator, worldserver.getRandom(), new StructureBoundingBox(chunkcoordintpair2.getMinBlockX(), worldserver.getMinBuildHeight(), chunkcoordintpair2.getMinBlockZ(), chunkcoordintpair2.getMaxBlockX(), worldserver.getMaxBuildHeight(), chunkcoordintpair2.getMaxBlockZ()), chunkcoordintpair2);
            });
            String s = (String) holder.unwrapKey().map((resourcekey) -> {
                return resourcekey.location().toString();
            }).orElse("[unregistered]");

            commandlistenerwrapper.sendSuccess(IChatBaseComponent.translatable("commands.place.structure.success", s, blockposition.getX(), blockposition.getY(), blockposition.getZ()), true);
            return 1;
        }
    }

    public static int placeTemplate(CommandListenerWrapper commandlistenerwrapper, MinecraftKey minecraftkey, BlockPosition blockposition, EnumBlockRotation enumblockrotation, EnumBlockMirror enumblockmirror, float f, int i) throws CommandSyntaxException {
        WorldServer worldserver = commandlistenerwrapper.getLevel();
        StructureTemplateManager structuretemplatemanager = worldserver.getStructureManager();

        Optional optional;

        try {
            optional = structuretemplatemanager.get(minecraftkey);
        } catch (ResourceKeyInvalidException resourcekeyinvalidexception) {
            throw PlaceCommand.ERROR_TEMPLATE_INVALID.create(minecraftkey);
        }

        if (optional.isEmpty()) {
            throw PlaceCommand.ERROR_TEMPLATE_INVALID.create(minecraftkey);
        } else {
            DefinedStructure definedstructure = (DefinedStructure) optional.get();

            checkLoaded(worldserver, new ChunkCoordIntPair(blockposition), new ChunkCoordIntPair(blockposition.offset(definedstructure.getSize())));
            DefinedStructureInfo definedstructureinfo = (new DefinedStructureInfo()).setMirror(enumblockmirror).setRotation(enumblockrotation);

            if (f < 1.0F) {
                definedstructureinfo.clearProcessors().addProcessor(new DefinedStructureProcessorRotation(f)).setRandom(TileEntityStructure.createRandom((long) i));
            }

            boolean flag = definedstructure.placeInWorld(worldserver, blockposition, blockposition, definedstructureinfo, TileEntityStructure.createRandom((long) i), 2);

            if (!flag) {
                throw PlaceCommand.ERROR_TEMPLATE_FAILED.create();
            } else {
                commandlistenerwrapper.sendSuccess(IChatBaseComponent.translatable("commands.place.template.success", minecraftkey, blockposition.getX(), blockposition.getY(), blockposition.getZ()), true);
                return 1;
            }
        }
    }

    private static void checkLoaded(WorldServer worldserver, ChunkCoordIntPair chunkcoordintpair, ChunkCoordIntPair chunkcoordintpair1) throws CommandSyntaxException {
        if (ChunkCoordIntPair.rangeClosed(chunkcoordintpair, chunkcoordintpair1).filter((chunkcoordintpair2) -> {
            return !worldserver.isLoaded(chunkcoordintpair2.getWorldPosition());
        }).findAny().isPresent()) {
            throw ArgumentPosition.ERROR_NOT_LOADED.create();
        }
    }
}
