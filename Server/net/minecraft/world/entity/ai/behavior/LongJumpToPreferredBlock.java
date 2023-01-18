/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Optional
 *  java.util.function.BiPredicate
 *  java.util.function.Function
 */
package net.minecraft.world.entity.ai.behavior;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.LongJumpToRandomPos;
import net.minecraft.world.level.block.Block;

public class LongJumpToPreferredBlock<E extends Mob>
extends LongJumpToRandomPos<E> {
    private final TagKey<Block> preferredBlockTag;
    private final float preferredBlocksChance;
    private final List<LongJumpToRandomPos.PossibleJump> notPrefferedJumpCandidates = new ArrayList();
    private boolean currentlyWantingPreferredOnes;

    public LongJumpToPreferredBlock(UniformInt $$0, int $$1, int $$2, float $$3, Function<E, SoundEvent> $$4, TagKey<Block> $$5, float $$6, BiPredicate<E, BlockPos> $$7) {
        super($$0, $$1, $$2, $$3, $$4, $$7);
        this.preferredBlockTag = $$5;
        this.preferredBlocksChance = $$6;
    }

    @Override
    protected void start(ServerLevel $$0, E $$1, long $$2) {
        super.start($$0, $$1, $$2);
        this.notPrefferedJumpCandidates.clear();
        this.currentlyWantingPreferredOnes = ((LivingEntity)$$1).getRandom().nextFloat() < this.preferredBlocksChance;
    }

    @Override
    protected Optional<LongJumpToRandomPos.PossibleJump> getJumpCandidate(ServerLevel $$0) {
        if (!this.currentlyWantingPreferredOnes) {
            return super.getJumpCandidate($$0);
        }
        BlockPos.MutableBlockPos $$1 = new BlockPos.MutableBlockPos();
        while (!this.jumpCandidates.isEmpty()) {
            Optional<LongJumpToRandomPos.PossibleJump> $$2 = super.getJumpCandidate($$0);
            if (!$$2.isPresent()) continue;
            LongJumpToRandomPos.PossibleJump $$3 = (LongJumpToRandomPos.PossibleJump)$$2.get();
            if ($$0.getBlockState($$1.setWithOffset((Vec3i)$$3.getJumpTarget(), Direction.DOWN)).is(this.preferredBlockTag)) {
                return $$2;
            }
            this.notPrefferedJumpCandidates.add((Object)$$3);
        }
        if (!this.notPrefferedJumpCandidates.isEmpty()) {
            return Optional.of((Object)((LongJumpToRandomPos.PossibleJump)this.notPrefferedJumpCandidates.remove(0)));
        }
        return Optional.empty();
    }
}