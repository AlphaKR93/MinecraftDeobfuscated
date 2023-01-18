/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.Stack
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  java.util.function.Predicate
 */
package net.minecraft.server.advancements;

import it.unimi.dsi.fastutil.Stack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.function.Predicate;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;

public class AdvancementVisibilityEvaluator {
    private static final int VISIBILITY_DEPTH = 2;

    private static VisibilityRule evaluateVisibilityRule(Advancement $$0, boolean $$1) {
        DisplayInfo $$2 = $$0.getDisplay();
        if ($$2 == null) {
            return VisibilityRule.HIDE;
        }
        if ($$1) {
            return VisibilityRule.SHOW;
        }
        if ($$2.isHidden()) {
            return VisibilityRule.HIDE;
        }
        return VisibilityRule.NO_CHANGE;
    }

    private static boolean evaluateVisiblityForUnfinishedNode(Stack<VisibilityRule> $$0) {
        for (int $$1 = 0; $$1 <= 2; ++$$1) {
            VisibilityRule $$2 = (VisibilityRule)((Object)$$0.peek($$1));
            if ($$2 == VisibilityRule.SHOW) {
                return true;
            }
            if ($$2 != VisibilityRule.HIDE) continue;
            return false;
        }
        return false;
    }

    private static boolean evaluateVisibility(Advancement $$0, Stack<VisibilityRule> $$1, Predicate<Advancement> $$2, Output $$3) {
        boolean $$4 = $$2.test((Object)$$0);
        VisibilityRule $$5 = AdvancementVisibilityEvaluator.evaluateVisibilityRule($$0, $$4);
        boolean $$6 = $$4;
        $$1.push((Object)$$5);
        for (Advancement $$7 : $$0.getChildren()) {
            $$6 |= AdvancementVisibilityEvaluator.evaluateVisibility($$7, $$1, $$2, $$3);
        }
        boolean $$8 = $$6 || AdvancementVisibilityEvaluator.evaluateVisiblityForUnfinishedNode($$1);
        $$1.pop();
        $$3.accept($$0, $$8);
        return $$6;
    }

    public static void evaluateVisibility(Advancement $$0, Predicate<Advancement> $$1, Output $$2) {
        Advancement $$3 = $$0.getRoot();
        ObjectArrayList $$4 = new ObjectArrayList();
        for (int $$5 = 0; $$5 <= 2; ++$$5) {
            $$4.push((Object)VisibilityRule.NO_CHANGE);
        }
        AdvancementVisibilityEvaluator.evaluateVisibility($$3, (Stack<VisibilityRule>)$$4, $$1, $$2);
    }

    static enum VisibilityRule {
        SHOW,
        HIDE,
        NO_CHANGE;

    }

    @FunctionalInterface
    public static interface Output {
        public void accept(Advancement var1, boolean var2);
    }
}