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
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("arguments.block.tag.unknown", new Object[]{object});
    });

    public ArgumentBlockPredicate() {}

    public static ArgumentBlockPredicate a() {
        return new ArgumentBlockPredicate();
    }

    public ArgumentBlockPredicate.b parse(StringReader stringreader) throws CommandSyntaxException {
        ArgumentBlock argumentblock = (new ArgumentBlock(stringreader, true)).a(true);

        if (argumentblock.getBlockData() != null) {
            ArgumentBlockPredicate.a argumentblockpredicate_a = new ArgumentBlockPredicate.a(argumentblock.getBlockData(), argumentblock.getStateMap().keySet(), argumentblock.c());

            return (itagregistry) -> {
                return argumentblockpredicate_a;
            };
        } else {
            MinecraftKey minecraftkey = argumentblock.d();

            return (itagregistry) -> {
                Tag<Block> tag = itagregistry.a(IRegistry.BLOCK_REGISTRY, minecraftkey, (minecraftkey1) -> {
                    return ArgumentBlockPredicate.ERROR_UNKNOWN_TAG.create(minecraftkey1.toString());
                });

                return new ArgumentBlockPredicate.c(tag, argumentblock.j(), argumentblock.c());
            };
        }
    }

    public static Predicate<ShapeDetectorBlock> a(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        return ((ArgumentBlockPredicate.b) commandcontext.getArgument(s, ArgumentBlockPredicate.b.class)).create(((CommandListenerWrapper) commandcontext.getSource()).getServer().getTagRegistry());
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandcontext, SuggestionsBuilder suggestionsbuilder) {
        StringReader stringreader = new StringReader(suggestionsbuilder.getInput());

        stringreader.setCursor(suggestionsbuilder.getStart());
        ArgumentBlock argumentblock = new ArgumentBlock(stringreader, true);

        try {
            argumentblock.a(true);
        } catch (CommandSyntaxException commandsyntaxexception) {
            ;
        }

        return argumentblock.a(suggestionsbuilder, TagsBlock.a());
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
            IBlockData iblockdata = shapedetectorblock.a();

            if (!iblockdata.a(this.state.getBlock())) {
                return false;
            } else {
                Iterator iterator = this.properties.iterator();

                while (iterator.hasNext()) {
                    IBlockState<?> iblockstate = (IBlockState) iterator.next();

                    if (iblockdata.get(iblockstate) != this.state.get(iblockstate)) {
                        return false;
                    }
                }

                if (this.nbt == null) {
                    return true;
                } else {
                    TileEntity tileentity = shapedetectorblock.b();

                    return tileentity != null && GameProfileSerializer.a(this.nbt, tileentity.save(new NBTTagCompound()), true);
                }
            }
        }
    }

    public interface b {

        Predicate<ShapeDetectorBlock> create(ITagRegistry itagregistry) throws CommandSyntaxException;
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
            IBlockData iblockdata = shapedetectorblock.a();

            if (!iblockdata.a(this.tag)) {
                return false;
            } else {
                Iterator iterator = this.vagueProperties.entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry<String, String> entry = (Entry) iterator.next();
                    IBlockState<?> iblockstate = iblockdata.getBlock().getStates().a((String) entry.getKey());

                    if (iblockstate == null) {
                        return false;
                    }

                    Comparable<?> comparable = (Comparable) iblockstate.b((String) entry.getValue()).orElse((Object) null);

                    if (comparable == null) {
                        return false;
                    }

                    if (iblockdata.get(iblockstate) != comparable) {
                        return false;
                    }
                }

                if (this.nbt == null) {
                    return true;
                } else {
                    TileEntity tileentity = shapedetectorblock.b();

                    return tileentity != null && GameProfileSerializer.a(this.nbt, tileentity.save(new NBTTagCompound()), true);
                }
            }
        }
    }
}
