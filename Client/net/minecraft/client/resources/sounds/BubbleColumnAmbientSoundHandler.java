/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.resources.sounds;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AmbientSoundHandler;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BubbleColumnAmbientSoundHandler
implements AmbientSoundHandler {
    private final LocalPlayer player;
    private boolean wasInBubbleColumn;
    private boolean firstTick = true;

    public BubbleColumnAmbientSoundHandler(LocalPlayer $$0) {
        this.player = $$0;
    }

    @Override
    public void tick() {
        Level $$02 = this.player.level;
        BlockState $$1 = (BlockState)$$02.getBlockStatesIfLoaded(this.player.getBoundingBox().inflate(0.0, -0.4f, 0.0).deflate(1.0E-6)).filter($$0 -> $$0.is(Blocks.BUBBLE_COLUMN)).findFirst().orElse(null);
        if ($$1 != null) {
            if (!this.wasInBubbleColumn && !this.firstTick && $$1.is(Blocks.BUBBLE_COLUMN) && !this.player.isSpectator()) {
                boolean $$2 = $$1.getValue(BubbleColumnBlock.DRAG_DOWN);
                if ($$2) {
                    this.player.playSound(SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_INSIDE, 1.0f, 1.0f);
                } else {
                    this.player.playSound(SoundEvents.BUBBLE_COLUMN_UPWARDS_INSIDE, 1.0f, 1.0f);
                }
            }
            this.wasInBubbleColumn = true;
        } else {
            this.wasInBubbleColumn = false;
        }
        this.firstTick = false;
    }
}