/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.Objects
 *  java.util.function.Supplier
 */
package net.minecraft.client.gui.components.toasts;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

public class AdvancementToast
implements Toast {
    private final Advancement advancement;
    private boolean playedSound;

    public AdvancementToast(Advancement $$0) {
        this.advancement = $$0;
    }

    @Override
    public Toast.Visibility render(PoseStack $$0, ToastComponent $$1, long $$2) {
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        DisplayInfo $$3 = this.advancement.getDisplay();
        $$1.blit($$0, 0, 0, 0, 0, this.width(), this.height());
        if ($$3 != null) {
            int $$5;
            List<FormattedCharSequence> $$4 = $$1.getMinecraft().font.split($$3.getTitle(), 125);
            int n = $$5 = $$3.getFrame() == FrameType.CHALLENGE ? 0xFF88FF : 0xFFFF00;
            if ($$4.size() == 1) {
                $$1.getMinecraft().font.draw($$0, $$3.getFrame().getDisplayName(), 30.0f, 7.0f, $$5 | 0xFF000000);
                $$1.getMinecraft().font.draw($$0, (FormattedCharSequence)$$4.get(0), 30.0f, 18.0f, -1);
            } else {
                int $$6 = 1500;
                float $$7 = 300.0f;
                if ($$2 < 1500L) {
                    int $$8 = Mth.floor(Mth.clamp((float)(1500L - $$2) / 300.0f, 0.0f, 1.0f) * 255.0f) << 24 | 0x4000000;
                    $$1.getMinecraft().font.draw($$0, $$3.getFrame().getDisplayName(), 30.0f, 11.0f, $$5 | $$8);
                } else {
                    int $$9 = Mth.floor(Mth.clamp((float)($$2 - 1500L) / 300.0f, 0.0f, 1.0f) * 252.0f) << 24 | 0x4000000;
                    int n2 = this.height() / 2;
                    int n3 = $$4.size();
                    Objects.requireNonNull((Object)$$1.getMinecraft().font);
                    int $$10 = n2 - n3 * 9 / 2;
                    for (FormattedCharSequence $$11 : $$4) {
                        $$1.getMinecraft().font.draw($$0, $$11, 30.0f, (float)$$10, 0xFFFFFF | $$9);
                        Objects.requireNonNull((Object)$$1.getMinecraft().font);
                        $$10 += 9;
                    }
                }
            }
            if (!this.playedSound && $$2 > 0L) {
                this.playedSound = true;
                if ($$3.getFrame() == FrameType.CHALLENGE) {
                    $$1.getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f));
                }
            }
            $$1.getMinecraft().getItemRenderer().renderAndDecorateFakeItem($$3.getIcon(), 8, 8);
            return $$2 >= 5000L ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
        }
        return Toast.Visibility.HIDE;
    }
}