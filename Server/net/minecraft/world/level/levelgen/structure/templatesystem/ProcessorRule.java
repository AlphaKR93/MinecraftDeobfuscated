/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.util.Optional
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosAlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

public class ProcessorRule {
    public static final Codec<ProcessorRule> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)RuleTest.CODEC.fieldOf("input_predicate").forGetter($$0 -> $$0.inputPredicate), (App)RuleTest.CODEC.fieldOf("location_predicate").forGetter($$0 -> $$0.locPredicate), (App)PosRuleTest.CODEC.optionalFieldOf("position_predicate", (Object)PosAlwaysTrueTest.INSTANCE).forGetter($$0 -> $$0.posPredicate), (App)BlockState.CODEC.fieldOf("output_state").forGetter($$0 -> $$0.outputState), (App)CompoundTag.CODEC.optionalFieldOf("output_nbt").forGetter($$0 -> Optional.ofNullable((Object)$$0.outputTag))).apply((Applicative)$$02, ProcessorRule::new));
    private final RuleTest inputPredicate;
    private final RuleTest locPredicate;
    private final PosRuleTest posPredicate;
    private final BlockState outputState;
    @Nullable
    private final CompoundTag outputTag;

    public ProcessorRule(RuleTest $$0, RuleTest $$1, BlockState $$2) {
        this($$0, $$1, PosAlwaysTrueTest.INSTANCE, $$2, (Optional<CompoundTag>)Optional.empty());
    }

    public ProcessorRule(RuleTest $$0, RuleTest $$1, PosRuleTest $$2, BlockState $$3) {
        this($$0, $$1, $$2, $$3, (Optional<CompoundTag>)Optional.empty());
    }

    public ProcessorRule(RuleTest $$0, RuleTest $$1, PosRuleTest $$2, BlockState $$3, Optional<CompoundTag> $$4) {
        this.inputPredicate = $$0;
        this.locPredicate = $$1;
        this.posPredicate = $$2;
        this.outputState = $$3;
        this.outputTag = (CompoundTag)$$4.orElse(null);
    }

    public boolean test(BlockState $$0, BlockState $$1, BlockPos $$2, BlockPos $$3, BlockPos $$4, RandomSource $$5) {
        return this.inputPredicate.test($$0, $$5) && this.locPredicate.test($$1, $$5) && this.posPredicate.test($$2, $$3, $$4, $$5);
    }

    public BlockState getOutputState() {
        return this.outputState;
    }

    @Nullable
    public CompoundTag getOutputTag() {
        return this.outputTag;
    }
}