package net.minecraft.commands.arguments.blocks;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
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
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.tags.ITagRegistry;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.pattern.ShapeDetectorBlock;
import net.minecraft.world.level.block.state.properties.IBlockState;

public class ArgumentBlockPredicate implements ArgumentType<ArgumentBlockPredicate.b> {

    private static final Collection<String> EXAMPLES = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "#stone", "#stone[foo=bar]{baz=nbt}");
    static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("arguments.block.tag.unknown", new Object[]{object});
    });

    public ArgumentBlockPredicate() {}

    public static ArgumentBlockPredicate blockPredicate() {
        return new ArgumentBlockPredicate();
    }

    public ArgumentBlockPredicate.b parse(StringReader stringreader) throws CommandSyntaxException {
        final ArgumentBlock argumentblock = (new ArgumentBlock(stringreader, true)).parse(true);

        if (argumentblock.getState() != null) {
            final ArgumentBlockPredicate.a argumentblockpredicate_a = new ArgumentBlockPredicate.a(argumentblock.getState(), argumentblock.getProperties().keySet(), argumentblock.getNbt());

            return new ArgumentBlockPredicate.b() {
                @Override
                public Predicate<ShapeDetectorBlock> create(ITagRegistry itagregistry) {
                    return argumentblockpredicate_a;
                }

                @Override
                public boolean requiresNbt() {
                    return argumentblockpredicate_a.requiresNbt();
                }
            };
        } else {
            final MinecraftKey minecraftkey = argumentblock.getTag();

            return new ArgumentBlockPredicate.b() {
                @Override
                public Predicate<ShapeDetectorBlock> create(ITagRegistry itagregistry) throws CommandSyntaxException {
                    Tag<Block> tag = itagregistry.getTagOrThrow(IRegistry.BLOCK_REGISTRY, minecraftkey, (minecraftkey1) -> {
                        return ArgumentBlockPredicate.ERROR_UNKNOWN_TAG.create(minecraftkey1.toString());
                    });

                    return new ArgumentBlockPredicate.c(tag, argumentblock.getVagueProperties(), argumentblock.getNbt());
                }

                @Override
                public boolean requiresNbt() {
                    return argumentblock.getNbt() != null;
                }
            };
        }
    }

    public static Predicate<ShapeDetectorBlock> getBlockPredicate(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        return ((ArgumentBlockPredicate.b) commandcontext.getArgument(s, ArgumentBlockPredicate.b.class)).create(((CommandListenerWrapper) commandcontext.getSource()).getServer().getTags());
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandcontext, SuggestionsBuilder suggestionsbuilder) {
        StringReader stringreader = new StringReader(suggestionsbuilder.getInput());

        stringreader.setCursor(suggestionsbuilder.getStart());
        ArgumentBlock argumentblock = new ArgumentBlock(stringreader, true);

        try {
            argumentblock.parse(true);
        } catch (CommandSyntaxException commandsyntaxexception) {
            ;
        }

        return argumentblock.fillSuggestions(suggestionsbuilder, TagsBlock.getAllTags());
    }

    public Collection<String> getExamples() {
        return ArgumentBlockPredicate.EXAMPLES;
    }

    private static class a implements Predicate<ShapeDetectorBlock> {

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

        public boolean requiresNbt() {
            return this.nbt != null;
        }
    }

    public interface b {

        Predicate<ShapeDetectorBlock> create(ITagRegistry itagregistry) throws CommandSyntaxException;

        boolean requiresNbt();
    }

    private static class c implements Predicate<ShapeDetectorBlock> {

        private final Tag<Block> tag;
        @Nullable
        private final NBTTagCompound nbt;
        private final Map<String, String> vagueProperties;

        c(Tag<Block> tag, Map<String, String> map, @Nullable NBTTagCompound nbttagcompound) {
            this.tag = tag;
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
    }
}
