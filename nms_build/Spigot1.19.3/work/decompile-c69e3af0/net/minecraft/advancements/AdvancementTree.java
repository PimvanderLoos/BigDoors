package net.minecraft.advancements;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class AdvancementTree {

    private final Advancement advancement;
    @Nullable
    private final AdvancementTree parent;
    @Nullable
    private final AdvancementTree previousSibling;
    private final int childIndex;
    private final List<AdvancementTree> children = Lists.newArrayList();
    private AdvancementTree ancestor;
    @Nullable
    private AdvancementTree thread;
    private int x;
    private float y;
    private float mod;
    private float change;
    private float shift;

    public AdvancementTree(Advancement advancement, @Nullable AdvancementTree advancementtree, @Nullable AdvancementTree advancementtree1, int i, int j) {
        if (advancement.getDisplay() == null) {
            throw new IllegalArgumentException("Can't position an invisible advancement!");
        } else {
            this.advancement = advancement;
            this.parent = advancementtree;
            this.previousSibling = advancementtree1;
            this.childIndex = i;
            this.ancestor = this;
            this.x = j;
            this.y = -1.0F;
            AdvancementTree advancementtree2 = null;

            Advancement advancement1;

            for (Iterator iterator = advancement.getChildren().iterator(); iterator.hasNext(); advancementtree2 = this.addChild(advancement1, advancementtree2)) {
                advancement1 = (Advancement) iterator.next();
            }

        }
    }

    @Nullable
    private AdvancementTree addChild(Advancement advancement, @Nullable AdvancementTree advancementtree) {
        Advancement advancement1;

        if (advancement.getDisplay() != null) {
            advancementtree = new AdvancementTree(advancement, this, advancementtree, this.children.size() + 1, this.x + 1);
            this.children.add(advancementtree);
        } else {
            for (Iterator iterator = advancement.getChildren().iterator(); iterator.hasNext(); advancementtree = this.addChild(advancement1, advancementtree)) {
                advancement1 = (Advancement) iterator.next();
            }
        }

        return advancementtree;
    }

    private void firstWalk() {
        if (this.children.isEmpty()) {
            if (this.previousSibling != null) {
                this.y = this.previousSibling.y + 1.0F;
            } else {
                this.y = 0.0F;
            }

        } else {
            AdvancementTree advancementtree = null;

            AdvancementTree advancementtree1;

            for (Iterator iterator = this.children.iterator(); iterator.hasNext(); advancementtree = advancementtree1.apportion(advancementtree == null ? advancementtree1 : advancementtree)) {
                advancementtree1 = (AdvancementTree) iterator.next();
                advancementtree1.firstWalk();
            }

            this.executeShifts();
            float f = (((AdvancementTree) this.children.get(0)).y + ((AdvancementTree) this.children.get(this.children.size() - 1)).y) / 2.0F;

            if (this.previousSibling != null) {
                this.y = this.previousSibling.y + 1.0F;
                this.mod = this.y - f;
            } else {
                this.y = f;
            }

        }
    }

    private float secondWalk(float f, int i, float f1) {
        this.y += f;
        this.x = i;
        if (this.y < f1) {
            f1 = this.y;
        }

        AdvancementTree advancementtree;

        for (Iterator iterator = this.children.iterator(); iterator.hasNext(); f1 = advancementtree.secondWalk(f + this.mod, i + 1, f1)) {
            advancementtree = (AdvancementTree) iterator.next();
        }

        return f1;
    }

    private void thirdWalk(float f) {
        this.y += f;
        Iterator iterator = this.children.iterator();

        while (iterator.hasNext()) {
            AdvancementTree advancementtree = (AdvancementTree) iterator.next();

            advancementtree.thirdWalk(f);
        }

    }

    private void executeShifts() {
        float f = 0.0F;
        float f1 = 0.0F;

        for (int i = this.children.size() - 1; i >= 0; --i) {
            AdvancementTree advancementtree = (AdvancementTree) this.children.get(i);

            advancementtree.y += f;
            advancementtree.mod += f;
            f1 += advancementtree.change;
            f += advancementtree.shift + f1;
        }

    }

    @Nullable
    private AdvancementTree previousOrThread() {
        return this.thread != null ? this.thread : (!this.children.isEmpty() ? (AdvancementTree) this.children.get(0) : null);
    }

    @Nullable
    private AdvancementTree nextOrThread() {
        return this.thread != null ? this.thread : (!this.children.isEmpty() ? (AdvancementTree) this.children.get(this.children.size() - 1) : null);
    }

    private AdvancementTree apportion(AdvancementTree advancementtree) {
        if (this.previousSibling == null) {
            return advancementtree;
        } else {
            AdvancementTree advancementtree1 = this;
            AdvancementTree advancementtree2 = this;
            AdvancementTree advancementtree3 = this.previousSibling;
            AdvancementTree advancementtree4 = (AdvancementTree) this.parent.children.get(0);
            float f = this.mod;
            float f1 = this.mod;
            float f2 = advancementtree3.mod;

            float f3;

            for (f3 = advancementtree4.mod; advancementtree3.nextOrThread() != null && advancementtree1.previousOrThread() != null; f1 += advancementtree2.mod) {
                advancementtree3 = advancementtree3.nextOrThread();
                advancementtree1 = advancementtree1.previousOrThread();
                advancementtree4 = advancementtree4.previousOrThread();
                advancementtree2 = advancementtree2.nextOrThread();
                advancementtree2.ancestor = this;
                float f4 = advancementtree3.y + f2 - (advancementtree1.y + f) + 1.0F;

                if (f4 > 0.0F) {
                    advancementtree3.getAncestor(this, advancementtree).moveSubtree(this, f4);
                    f += f4;
                    f1 += f4;
                }

                f2 += advancementtree3.mod;
                f += advancementtree1.mod;
                f3 += advancementtree4.mod;
            }

            if (advancementtree3.nextOrThread() != null && advancementtree2.nextOrThread() == null) {
                advancementtree2.thread = advancementtree3.nextOrThread();
                advancementtree2.mod += f2 - f1;
            } else {
                if (advancementtree1.previousOrThread() != null && advancementtree4.previousOrThread() == null) {
                    advancementtree4.thread = advancementtree1.previousOrThread();
                    advancementtree4.mod += f - f3;
                }

                advancementtree = this;
            }

            return advancementtree;
        }
    }

    private void moveSubtree(AdvancementTree advancementtree, float f) {
        float f1 = (float) (advancementtree.childIndex - this.childIndex);

        if (f1 != 0.0F) {
            advancementtree.change -= f / f1;
            this.change += f / f1;
        }

        advancementtree.shift += f;
        advancementtree.y += f;
        advancementtree.mod += f;
    }

    private AdvancementTree getAncestor(AdvancementTree advancementtree, AdvancementTree advancementtree1) {
        return this.ancestor != null && advancementtree.parent.children.contains(this.ancestor) ? this.ancestor : advancementtree1;
    }

    private void finalizePosition() {
        if (this.advancement.getDisplay() != null) {
            this.advancement.getDisplay().setLocation((float) this.x, this.y);
        }

        if (!this.children.isEmpty()) {
            Iterator iterator = this.children.iterator();

            while (iterator.hasNext()) {
                AdvancementTree advancementtree = (AdvancementTree) iterator.next();

                advancementtree.finalizePosition();
            }
        }

    }

    public static void run(Advancement advancement) {
        if (advancement.getDisplay() == null) {
            throw new IllegalArgumentException("Can't position children of an invisible root!");
        } else {
            AdvancementTree advancementtree = new AdvancementTree(advancement, (AdvancementTree) null, (AdvancementTree) null, 1, 0);

            advancementtree.firstWalk();
            float f = advancementtree.secondWalk(0.0F, 0, advancementtree.y);

            if (f < 0.0F) {
                advancementtree.thirdWalk(-f);
            }

            advancementtree.finalizePosition();
        }
    }
}
