package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.ResourceOrTagArgument;
import net.minecraft.commands.arguments.coordinates.ArgumentPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPosition;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import org.apache.commons.lang3.mutable.MutableInt;

public class FillBiomeCommand {

    private static final int MAX_FILL_AREA = 32768;
    public static final SimpleCommandExceptionType ERROR_NOT_LOADED = new SimpleCommandExceptionType(IChatBaseComponent.translatable("argument.pos.unloaded"));
    private static final Dynamic2CommandExceptionType ERROR_VOLUME_TOO_LARGE = new Dynamic2CommandExceptionType((object, object1) -> {
        return IChatBaseComponent.translatable("commands.fillbiome.toobig", object, object1);
    });

    public FillBiomeCommand() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher, CommandBuildContext commandbuildcontext) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("fillbiome").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.argument("from", ArgumentPosition.blockPos()).then(net.minecraft.commands.CommandDispatcher.argument("to", ArgumentPosition.blockPos()).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("biome", ResourceArgument.resource(commandbuildcontext, Registries.BIOME)).executes((commandcontext) -> {
            return fill((CommandListenerWrapper) commandcontext.getSource(), ArgumentPosition.getLoadedBlockPos(commandcontext, "from"), ArgumentPosition.getLoadedBlockPos(commandcontext, "to"), ResourceArgument.getResource(commandcontext, "biome", Registries.BIOME), (holder) -> {
                return true;
            });
        })).then(net.minecraft.commands.CommandDispatcher.literal("replace").then(net.minecraft.commands.CommandDispatcher.argument("filter", ResourceOrTagArgument.resourceOrTag(commandbuildcontext, Registries.BIOME)).executes((commandcontext) -> {
            CommandListenerWrapper commandlistenerwrapper = (CommandListenerWrapper) commandcontext.getSource();
            BlockPosition blockposition = ArgumentPosition.getLoadedBlockPos(commandcontext, "from");
            BlockPosition blockposition1 = ArgumentPosition.getLoadedBlockPos(commandcontext, "to");
            Holder.c holder_c = ResourceArgument.getResource(commandcontext, "biome", Registries.BIOME);
            ResourceOrTagArgument.c resourceortagargument_c = ResourceOrTagArgument.getResourceOrTag(commandcontext, "filter", Registries.BIOME);

            Objects.requireNonNull(resourceortagargument_c);
            return fill(commandlistenerwrapper, blockposition, blockposition1, holder_c, resourceortagargument_c::test);
        })))))));
    }

    private static int quantize(int i) {
        return QuartPos.toBlock(QuartPos.fromBlock(i));
    }

    private static BlockPosition quantize(BlockPosition blockposition) {
        return new BlockPosition(quantize(blockposition.getX()), quantize(blockposition.getY()), quantize(blockposition.getZ()));
    }

    private static BiomeResolver makeResolver(MutableInt mutableint, IChunkAccess ichunkaccess, StructureBoundingBox structureboundingbox, Holder<BiomeBase> holder, Predicate<Holder<BiomeBase>> predicate) {
        return (i, j, k, climate_sampler) -> {
            int l = QuartPos.toBlock(i);
            int i1 = QuartPos.toBlock(j);
            int j1 = QuartPos.toBlock(k);
            Holder<BiomeBase> holder1 = ichunkaccess.getNoiseBiome(i, j, k);

            if (structureboundingbox.isInside(l, i1, j1) && predicate.test(holder1)) {
                mutableint.increment();
                return holder;
            } else {
                return holder1;
            }
        };
    }

    private static int fill(CommandListenerWrapper commandlistenerwrapper, BlockPosition blockposition, BlockPosition blockposition1, Holder.c<BiomeBase> holder_c, Predicate<Holder<BiomeBase>> predicate) throws CommandSyntaxException {
        BlockPosition blockposition2 = quantize(blockposition);
        BlockPosition blockposition3 = quantize(blockposition1);
        StructureBoundingBox structureboundingbox = StructureBoundingBox.fromCorners(blockposition2, blockposition3);
        int i = structureboundingbox.getXSpan() * structureboundingbox.getYSpan() * structureboundingbox.getZSpan();

        if (i > 32768) {
            throw FillBiomeCommand.ERROR_VOLUME_TOO_LARGE.create(32768, i);
        } else {
            WorldServer worldserver = commandlistenerwrapper.getLevel();
            List<IChunkAccess> list = new ArrayList();

            IChunkAccess ichunkaccess;

            for (int j = SectionPosition.blockToSectionCoord(structureboundingbox.minZ()); j <= SectionPosition.blockToSectionCoord(structureboundingbox.maxZ()); ++j) {
                for (int k = SectionPosition.blockToSectionCoord(structureboundingbox.minX()); k <= SectionPosition.blockToSectionCoord(structureboundingbox.maxX()); ++k) {
                    ichunkaccess = worldserver.getChunk(k, j, ChunkStatus.FULL, false);
                    if (ichunkaccess == null) {
                        throw FillBiomeCommand.ERROR_NOT_LOADED.create();
                    }

                    list.add(ichunkaccess);
                }
            }

            MutableInt mutableint = new MutableInt(0);
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                ichunkaccess = (IChunkAccess) iterator.next();
                ichunkaccess.fillBiomesFromNoise(makeResolver(mutableint, ichunkaccess, structureboundingbox, holder_c, predicate), worldserver.getChunkSource().randomState().sampler());
                ichunkaccess.setUnsaved(true);
                worldserver.getChunkSource().chunkMap.resendChunk(ichunkaccess);
            }

            commandlistenerwrapper.sendSuccess(IChatBaseComponent.translatable("commands.fillbiome.success.count", mutableint.getValue(), structureboundingbox.minX(), structureboundingbox.minY(), structureboundingbox.minZ(), structureboundingbox.maxX(), structureboundingbox.maxY(), structureboundingbox.maxZ()), true);
            return mutableint.getValue();
        }
    }
}
