/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.util.function.Function
 */
package net.minecraft.client.tutorial;

import java.util.function.Function;
import net.minecraft.client.tutorial.CompletedTutorialStepInstance;
import net.minecraft.client.tutorial.CraftPlanksTutorialStep;
import net.minecraft.client.tutorial.FindTreeTutorialStepInstance;
import net.minecraft.client.tutorial.MovementTutorialStepInstance;
import net.minecraft.client.tutorial.OpenInventoryTutorialStep;
import net.minecraft.client.tutorial.PunchTreeTutorialStepInstance;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.client.tutorial.TutorialStepInstance;

public enum TutorialSteps {
    MOVEMENT("movement", MovementTutorialStepInstance::new),
    FIND_TREE("find_tree", FindTreeTutorialStepInstance::new),
    PUNCH_TREE("punch_tree", PunchTreeTutorialStepInstance::new),
    OPEN_INVENTORY("open_inventory", OpenInventoryTutorialStep::new),
    CRAFT_PLANKS("craft_planks", CraftPlanksTutorialStep::new),
    NONE("none", CompletedTutorialStepInstance::new);

    private final String name;
    private final Function<Tutorial, ? extends TutorialStepInstance> constructor;

    private <T extends TutorialStepInstance> TutorialSteps(String $$0, Function<Tutorial, T> $$1) {
        this.name = $$0;
        this.constructor = $$1;
    }

    public TutorialStepInstance create(Tutorial $$0) {
        return (TutorialStepInstance)this.constructor.apply((Object)$$0);
    }

    public String getName() {
        return this.name;
    }

    public static TutorialSteps getByName(String $$0) {
        for (TutorialSteps $$1 : TutorialSteps.values()) {
            if (!$$1.name.equals((Object)$$0)) continue;
            return $$1;
        }
        return NONE;
    }
}