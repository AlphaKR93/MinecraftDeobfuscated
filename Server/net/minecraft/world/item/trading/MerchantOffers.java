/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.ArrayList
 *  javax.annotation.Nullable
 */
package net.minecraft.world.item.trading;

import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;

public class MerchantOffers
extends ArrayList<MerchantOffer> {
    public MerchantOffers() {
    }

    private MerchantOffers(int $$0) {
        super($$0);
    }

    public MerchantOffers(CompoundTag $$0) {
        ListTag $$1 = $$0.getList("Recipes", 10);
        for (int $$2 = 0; $$2 < $$1.size(); ++$$2) {
            this.add(new MerchantOffer($$1.getCompound($$2)));
        }
    }

    @Nullable
    public MerchantOffer getRecipeFor(ItemStack $$0, ItemStack $$1, int $$2) {
        if ($$2 > 0 && $$2 < this.size()) {
            MerchantOffer $$3 = (MerchantOffer)this.get($$2);
            if ($$3.satisfiedBy($$0, $$1)) {
                return $$3;
            }
            return null;
        }
        for (int $$4 = 0; $$4 < this.size(); ++$$4) {
            MerchantOffer $$5 = (MerchantOffer)this.get($$4);
            if (!$$5.satisfiedBy($$0, $$1)) continue;
            return $$5;
        }
        return null;
    }

    public void writeToStream(FriendlyByteBuf $$02) {
        $$02.writeCollection(this, ($$0, $$1) -> {
            $$0.writeItem($$1.getBaseCostA());
            $$0.writeItem($$1.getResult());
            $$0.writeItem($$1.getCostB());
            $$0.writeBoolean($$1.isOutOfStock());
            $$0.writeInt($$1.getUses());
            $$0.writeInt($$1.getMaxUses());
            $$0.writeInt($$1.getXp());
            $$0.writeInt($$1.getSpecialPriceDiff());
            $$0.writeFloat($$1.getPriceMultiplier());
            $$0.writeInt($$1.getDemand());
        });
    }

    public static MerchantOffers createFromStream(FriendlyByteBuf $$02) {
        return (MerchantOffers)((Object)$$02.readCollection(MerchantOffers::new, $$0 -> {
            ItemStack $$1 = $$0.readItem();
            ItemStack $$2 = $$0.readItem();
            ItemStack $$3 = $$0.readItem();
            boolean $$4 = $$0.readBoolean();
            int $$5 = $$0.readInt();
            int $$6 = $$0.readInt();
            int $$7 = $$0.readInt();
            int $$8 = $$0.readInt();
            float $$9 = $$0.readFloat();
            int $$10 = $$0.readInt();
            MerchantOffer $$11 = new MerchantOffer($$1, $$3, $$2, $$5, $$6, $$7, $$9, $$10);
            if ($$4) {
                $$11.setToOutOfStock();
            }
            $$11.setSpecialPriceDiff($$8);
            return $$11;
        }));
    }

    public CompoundTag createTag() {
        CompoundTag $$0 = new CompoundTag();
        ListTag $$1 = new ListTag();
        for (int $$2 = 0; $$2 < this.size(); ++$$2) {
            MerchantOffer $$3 = (MerchantOffer)this.get($$2);
            $$1.add($$3.createTag());
        }
        $$0.put("Recipes", $$1);
        return $$0;
    }
}