package net.minecraft.server.advancements;

import it.unimi.dsi.fastutil.Stack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Iterator;
import java.util.function.Predicate;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementDisplay;

public class AdvancementVisibilityEvaluator {

    private static final int VISIBILITY_DEPTH = 2;

    public AdvancementVisibilityEvaluator() {}

    private static AdvancementVisibilityEvaluator.b evaluateVisibilityRule(Advancement advancement, boolean flag) {
        AdvancementDisplay advancementdisplay = advancement.getDisplay();

        return advancementdisplay == null ? AdvancementVisibilityEvaluator.b.HIDE : (flag ? AdvancementVisibilityEvaluator.b.SHOW : (advancementdisplay.isHidden() ? AdvancementVisibilityEvaluator.b.HIDE : AdvancementVisibilityEvaluator.b.NO_CHANGE));
    }

    private static boolean evaluateVisiblityForUnfinishedNode(Stack<AdvancementVisibilityEvaluator.b> stack) {
        for (int i = 0; i <= 2; ++i) {
            AdvancementVisibilityEvaluator.b advancementvisibilityevaluator_b = (AdvancementVisibilityEvaluator.b) stack.peek(i);

            if (advancementvisibilityevaluator_b == AdvancementVisibilityEvaluator.b.SHOW) {
                return true;
            }

            if (advancementvisibilityevaluator_b == AdvancementVisibilityEvaluator.b.HIDE) {
                return false;
            }
        }

        return false;
    }

    private static boolean evaluateVisibility(Advancement advancement, Stack<AdvancementVisibilityEvaluator.b> stack, Predicate<Advancement> predicate, AdvancementVisibilityEvaluator.a advancementvisibilityevaluator_a) {
        boolean flag = predicate.test(advancement);
        AdvancementVisibilityEvaluator.b advancementvisibilityevaluator_b = evaluateVisibilityRule(advancement, flag);
        boolean flag1 = flag;

        stack.push(advancementvisibilityevaluator_b);

        Advancement advancement1;

        for (Iterator iterator = advancement.getChildren().iterator(); iterator.hasNext(); flag1 |= evaluateVisibility(advancement1, stack, predicate, advancementvisibilityevaluator_a)) {
            advancement1 = (Advancement) iterator.next();
        }

        boolean flag2 = flag1 || evaluateVisiblityForUnfinishedNode(stack);

        stack.pop();
        advancementvisibilityevaluator_a.accept(advancement, flag2);
        return flag1;
    }

    public static void evaluateVisibility(Advancement advancement, Predicate<Advancement> predicate, AdvancementVisibilityEvaluator.a advancementvisibilityevaluator_a) {
        Advancement advancement1 = advancement.getRoot();
        Stack<AdvancementVisibilityEvaluator.b> stack = new ObjectArrayList();

        for (int i = 0; i <= 2; ++i) {
            stack.push(AdvancementVisibilityEvaluator.b.NO_CHANGE);
        }

        evaluateVisibility(advancement1, stack, predicate, advancementvisibilityevaluator_a);
    }

    private static enum b {

        SHOW, HIDE, NO_CHANGE;

        private b() {}
    }

    @FunctionalInterface
    public interface a {

        void accept(Advancement advancement, boolean flag);
    }
}
