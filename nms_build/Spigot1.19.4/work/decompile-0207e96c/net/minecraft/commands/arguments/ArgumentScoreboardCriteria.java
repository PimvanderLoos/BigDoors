package net.minecraft.commands.arguments;

import com.google.common.collect.Lists;
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
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.stats.Statistic;
import net.minecraft.stats.StatisticWrapper;
import net.minecraft.world.scores.criteria.IScoreboardCriteria;

public class ArgumentScoreboardCriteria implements ArgumentType<IScoreboardCriteria> {

    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo.bar.baz", "minecraft:foo");
    public static final DynamicCommandExceptionType ERROR_INVALID_VALUE = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("argument.criteria.invalid", object);
    });

    private ArgumentScoreboardCriteria() {}

    public static ArgumentScoreboardCriteria criteria() {
        return new ArgumentScoreboardCriteria();
    }

    public static IScoreboardCriteria getCriteria(CommandContext<CommandListenerWrapper> commandcontext, String s) {
        return (IScoreboardCriteria) commandcontext.getArgument(s, IScoreboardCriteria.class);
    }

    public IScoreboardCriteria parse(StringReader stringreader) throws CommandSyntaxException {
        int i = stringreader.getCursor();

        while (stringreader.canRead() && stringreader.peek() != ' ') {
            stringreader.skip();
        }

        String s = stringreader.getString().substring(i, stringreader.getCursor());

        return (IScoreboardCriteria) IScoreboardCriteria.byName(s).orElseThrow(() -> {
            stringreader.setCursor(i);
            return ArgumentScoreboardCriteria.ERROR_INVALID_VALUE.create(s);
        });
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandcontext, SuggestionsBuilder suggestionsbuilder) {
        List<String> list = Lists.newArrayList(IScoreboardCriteria.getCustomCriteriaNames());
        Iterator iterator = BuiltInRegistries.STAT_TYPE.iterator();

        while (iterator.hasNext()) {
            StatisticWrapper<?> statisticwrapper = (StatisticWrapper) iterator.next();
            Iterator iterator1 = statisticwrapper.getRegistry().iterator();

            while (iterator1.hasNext()) {
                Object object = iterator1.next();
                String s = this.getName(statisticwrapper, object);

                list.add(s);
            }
        }

        return ICompletionProvider.suggest((Iterable) list, suggestionsbuilder);
    }

    public <T> String getName(StatisticWrapper<T> statisticwrapper, Object object) {
        return Statistic.buildName(statisticwrapper, object);
    }

    public Collection<String> getExamples() {
        return ArgumentScoreboardCriteria.EXAMPLES;
    }
}
