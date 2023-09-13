package net.minecraft.commands.arguments.item;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.tags.Tags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.properties.IBlockState;

public class ArgumentParserItemStack {

    public static final SimpleCommandExceptionType a = new SimpleCommandExceptionType(new ChatMessage("argument.item.tag.disallowed"));
    public static final DynamicCommandExceptionType b = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("argument.item.id.invalid", new Object[]{object});
    });
    private static final BiFunction<SuggestionsBuilder, Tags<Item>, CompletableFuture<Suggestions>> c = (suggestionsbuilder, tags) -> {
        return suggestionsbuilder.buildFuture();
    };
    private final StringReader d;
    private final boolean e;
    private final Map<IBlockState<?>, Comparable<?>> f = Maps.newHashMap();
    private Item g;
    @Nullable
    private NBTTagCompound h;
    private MinecraftKey i = new MinecraftKey("");
    private int j;
    private BiFunction<SuggestionsBuilder, Tags<Item>, CompletableFuture<Suggestions>> k;

    public ArgumentParserItemStack(StringReader stringreader, boolean flag) {
        this.k = ArgumentParserItemStack.c;
        this.d = stringreader;
        this.e = flag;
    }

    public Item b() {
        return this.g;
    }

    @Nullable
    public NBTTagCompound c() {
        return this.h;
    }

    public MinecraftKey d() {
        return this.i;
    }

    public void e() throws CommandSyntaxException {
        int i = this.d.getCursor();
        MinecraftKey minecraftkey = MinecraftKey.a(this.d);

        this.g = (Item) IRegistry.ITEM.getOptional(minecraftkey).orElseThrow(() -> {
            this.d.setCursor(i);
            return ArgumentParserItemStack.b.createWithContext(this.d, minecraftkey.toString());
        });
    }

    public void f() throws CommandSyntaxException {
        if (!this.e) {
            throw ArgumentParserItemStack.a.create();
        } else {
            this.k = this::c;
            this.d.expect('#');
            this.j = this.d.getCursor();
            this.i = MinecraftKey.a(this.d);
        }
    }

    public void g() throws CommandSyntaxException {
        this.h = (new MojangsonParser(this.d)).f();
    }

    public ArgumentParserItemStack h() throws CommandSyntaxException {
        this.k = this::d;
        if (this.d.canRead() && this.d.peek() == '#') {
            this.f();
        } else {
            this.e();
            this.k = this::b;
        }

        if (this.d.canRead() && this.d.peek() == '{') {
            this.k = ArgumentParserItemStack.c;
            this.g();
        }

        return this;
    }

    private CompletableFuture<Suggestions> b(SuggestionsBuilder suggestionsbuilder, Tags<Item> tags) {
        if (suggestionsbuilder.getRemaining().isEmpty()) {
            suggestionsbuilder.suggest(String.valueOf('{'));
        }

        return suggestionsbuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> c(SuggestionsBuilder suggestionsbuilder, Tags<Item> tags) {
        return ICompletionProvider.a((Iterable) tags.b(), suggestionsbuilder.createOffset(this.j));
    }

    private CompletableFuture<Suggestions> d(SuggestionsBuilder suggestionsbuilder, Tags<Item> tags) {
        if (this.e) {
            ICompletionProvider.a((Iterable) tags.b(), suggestionsbuilder, String.valueOf('#'));
        }

        return ICompletionProvider.a((Iterable) IRegistry.ITEM.keySet(), suggestionsbuilder);
    }

    public CompletableFuture<Suggestions> a(SuggestionsBuilder suggestionsbuilder, Tags<Item> tags) {
        return (CompletableFuture) this.k.apply(suggestionsbuilder.createOffset(this.d.getCursor()), tags);
    }
}
