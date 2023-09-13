package net.minecraft.commands.arguments.blocks;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.pattern.ShapeDetectorBlock;
import net.minecraft.world.level.block.state.properties.IBlockState;

public class ArgumentBlockPredicate implements ArgumentType<ArgumentBlockPredicate.b> {

    private static final Collection<String> EXAMPLES = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "#stone", "#stone[foo=bar]{baz=nbt}");
    private final HolderLookup<Block> blocks;

    public ArgumentBlockPredicate(CommandBuildContext commandbuildcontext) {
        this.blocks = commandbuildcontext.holderLookup(Registries.BLOCK);
    }

    public static ArgumentBlockPredicate blockPredicate(CommandBuildContext commandbuildcontext) {
        return new ArgumentBlockPredicate(commandbuildcontext);
    }

    public ArgumentBlockPredicate.b parse(StringReader stringreader) throws CommandSyntaxException {
        return parse(this.blocks, stringreader);
    }

    public static ArgumentBlockPredicate.b parse(HolderLookup<Block> holderlookup, StringReader stringreader) throws CommandSyntaxException {
        return (ArgumentBlockPredicate.b) ArgumentBlock.parseForTesting(holderlookup, stringreader, true).map((argumentblock_a) -> {
            return new ArgumentBlockPredicate.a(argumentblock_a.blockState(), argumentblock_a.properties().keySet(), argumentblock_a.nbt());
        }, (argumentblock_b) -> {
            return new ArgumentBlockPredicate.c(argumentblock_b.tag(), argumentblock_b.vagueProperties(), argumentblock_b.nbt());
        });
    }

    public static Predicate<ShapeDetectorBlock> getBlockPredicate(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        return (Predicate) commandcontext.getArgument(s, ArgumentBlockPredicate.b.class);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandcontext, SuggestionsBuilder suggestionsbuilder) {
        return ArgumentBlock.fillSuggestions(this.blocks, suggestionsbuilder, true, true);
    }

    public Collection<String> getExamples() {
        return ArgumentBlockPredicate.EXAMPLES;
    }

    public interface b extends Predicate<ShapeDetectorBlock> {

        boolean requiresNbt();
    }

    private static class c implements ArgumentBlockPredicate.b {

        private final HolderSet<Block> tag;
        @Nullable
        private final NBTTagCompound nbt;
        private final Map<String, String> vagueProperties;

        c(HolderSet<Block> holderset, Map<String, String> map, @Nullable NBTTagCompound nbttagcompound) {
            this.tag = holderset;
            this.vagueProperties = map;
            this.nbt = nbttagcompound;
        }

        public boolean test(ShapeDetectorBlock shapedetectorblock) {
            IBlockData iblockdata = shapedetectorblock.getState();

            if (!iblockdata.is(this.tag)) {
                return false;
            } else {
                Iterator iterator = this.vagueProperties.entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry<String, String> entry = (Entry) iterator.next();
                    IBlockState<?> iblockstate = iblockdata.getBlock().getStateDefinition().getProperty((String) entry.getKey());

                    if (iblockstate == null) {
                        return false;
                    }

                    Comparable<?> comparable = (Comparable) iblockstate.getValue((String) entry.getValue()).orElse((Object) null);

                    if (comparable == null) {
                        return false;
                    }

                    if (iblockdata.getValue(iblockstate) != comparable) {
                        return false;
                    }
                }

                if (this.nbt == null) {
                    return true;
                } else {
                    TileEntity tileentity = shapedetectorblock.getEntity();

                    return tileentity != null && GameProfileSerializer.compareNbt(this.nbt, tileentity.saveWithFullMetadata(), true);
                }
            }
        }

        @Override
        public boolean requiresNbt() {
            return this.nbt != null;
        }
    }

    private static class a implements ArgumentBlockPredicate.b {

        private final IBlockData state;
        private final Set<IBlockState<?>> properties;
        @Nullable
        private final NBTTagCompound nbt;

        public a(IBlockData iblockdata, Set<IBlockState<?>> set, @Nullable NBTTagCompound nbttagcompound) {
            this.state = iblockdata;
            this.properties = set;
            this.nbt = nbttagcompound;
        }

        public boolean test(ShapeDetectorBlock shapedetectorblock) {
            IBlockData iblockdata = shapedetectorblock.getState();

            if (!iblockdata.is(this.state.getBlock())) {
                return false;
            } else {
                Iterator iterator = this.properties.iterator();

                while (iterator.hasNext()) {
                    IBlockState<?> iblockstate = (IBlockState) iterator.next();

                    if (iblockdata.getValue(iblockstate) != this.state.getValue(iblockstate)) {
                        return false;
                    }
                }

                if (this.nbt == null) {
                    return true;
                } else {
                    TileEntity tileentity = shapedetectorblock.getEntity();

                    return tileentity != null && GameProfileSerializer.compareNbt(this.nbt, tileentity.saveWithFullMetadata(), true);
                }
            }
        }

        @Override
        public boolean requiresNbt() {
            return this.nbt != null;
        }
    }
}
