package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.advancements.critereon.CriterionConditionValue;
import net.minecraft.commands.CommandListenerWrapper;

public interface ArgumentCriterionValue<T extends CriterionConditionValue<?>> extends ArgumentType<T> {

    static ArgumentCriterionValue.b intRange() {
        return new ArgumentCriterionValue.b();
    }

    static ArgumentCriterionValue.a floatRange() {
        return new ArgumentCriterionValue.a();
    }

    public static class b implements ArgumentCriterionValue<CriterionConditionValue.IntegerRange> {

        private static final Collection<String> EXAMPLES = Arrays.asList("0..5", "0", "-5", "-100..", "..100");

        public b() {}

        public static CriterionConditionValue.IntegerRange getRange(CommandContext<CommandListenerWrapper> commandcontext, String s) {
            return (CriterionConditionValue.IntegerRange) commandcontext.getArgument(s, CriterionConditionValue.IntegerRange.class);
        }

        public CriterionConditionValue.IntegerRange parse(StringReader stringreader) throws CommandSyntaxException {
            return CriterionConditionValue.IntegerRange.fromReader(stringreader);
        }

        public Collection<String> getExamples() {
            return ArgumentCriterionValue.b.EXAMPLES;
        }
    }

    public static class a implements ArgumentCriterionValue<CriterionConditionValue.DoubleRange> {

        private static final Collection<String> EXAMPLES = Arrays.asList("0..5.2", "0", "-5.4", "-100.76..", "..100");

        public a() {}

        public static CriterionConditionValue.DoubleRange getRange(CommandContext<CommandListenerWrapper> commandcontext, String s) {
            return (CriterionConditionValue.DoubleRange) commandcontext.getArgument(s, CriterionConditionValue.DoubleRange.class);
        }

        public CriterionConditionValue.DoubleRange parse(StringReader stringreader) throws CommandSyntaxException {
            return CriterionConditionValue.DoubleRange.fromReader(stringreader);
        }

        public Collection<String> getExamples() {
            return ArgumentCriterionValue.a.EXAMPLES;
        }
    }
}
