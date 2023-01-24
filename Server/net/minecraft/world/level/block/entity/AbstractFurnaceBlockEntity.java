/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.Iterator
 *  java.util.LinkedHashMap
 *  java.util.List
 *  java.util.Map
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractFurnaceBlockEntity
extends BaseContainerBlockEntity
implements WorldlyContainer,
RecipeHolder,
StackedContentsCompatible {
    protected static final int SLOT_INPUT = 0;
    protected static final int SLOT_FUEL = 1;
    protected static final int SLOT_RESULT = 2;
    public static final int DATA_LIT_TIME = 0;
    private static final int[] SLOTS_FOR_UP = new int[]{0};
    private static final int[] SLOTS_FOR_DOWN = new int[]{2, 1};
    private static final int[] SLOTS_FOR_SIDES = new int[]{1};
    public static final int DATA_LIT_DURATION = 1;
    public static final int DATA_COOKING_PROGRESS = 2;
    public static final int DATA_COOKING_TOTAL_TIME = 3;
    public static final int NUM_DATA_VALUES = 4;
    public static final int BURN_TIME_STANDARD = 200;
    public static final int BURN_COOL_SPEED = 2;
    protected NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
    int litTime;
    int litDuration;
    int cookingProgress;
    int cookingTotalTime;
    protected final ContainerData dataAccess = new ContainerData(){

        @Override
        public int get(int $$0) {
            switch ($$0) {
                case 0: {
                    return AbstractFurnaceBlockEntity.this.litTime;
                }
                case 1: {
                    return AbstractFurnaceBlockEntity.this.litDuration;
                }
                case 2: {
                    return AbstractFurnaceBlockEntity.this.cookingProgress;
                }
                case 3: {
                    return AbstractFurnaceBlockEntity.this.cookingTotalTime;
                }
            }
            return 0;
        }

        @Override
        public void set(int $$0, int $$1) {
            switch ($$0) {
                case 0: {
                    AbstractFurnaceBlockEntity.this.litTime = $$1;
                    break;
                }
                case 1: {
                    AbstractFurnaceBlockEntity.this.litDuration = $$1;
                    break;
                }
                case 2: {
                    AbstractFurnaceBlockEntity.this.cookingProgress = $$1;
                    break;
                }
                case 3: {
                    AbstractFurnaceBlockEntity.this.cookingTotalTime = $$1;
                    break;
                }
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };
    private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap();
    private final RecipeManager.CachedCheck<Container, ? extends AbstractCookingRecipe> quickCheck;

    protected AbstractFurnaceBlockEntity(BlockEntityType<?> $$0, BlockPos $$1, BlockState $$2, RecipeType<? extends AbstractCookingRecipe> $$3) {
        super($$0, $$1, $$2);
        this.quickCheck = RecipeManager.createCheck($$3);
    }

    public static Map<Item, Integer> getFuel() {
        LinkedHashMap $$0 = Maps.newLinkedHashMap();
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Items.LAVA_BUCKET, 20000);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Blocks.COAL_BLOCK, 16000);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Items.BLAZE_ROD, 2400);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Items.COAL, 1600);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Items.CHARCOAL, 1600);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, ItemTags.LOGS, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, ItemTags.BAMBOO_BLOCKS, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, ItemTags.PLANKS, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Blocks.BAMBOO_MOSAIC, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, ItemTags.WOODEN_STAIRS, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Blocks.BAMBOO_MOSAIC_STAIRS, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, ItemTags.WOODEN_SLABS, 150);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Blocks.BAMBOO_MOSAIC_SLAB, 150);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, ItemTags.WOODEN_TRAPDOORS, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, ItemTags.WOODEN_PRESSURE_PLATES, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, ItemTags.WOODEN_FENCES, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, ItemTags.FENCE_GATES, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Blocks.NOTE_BLOCK, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Blocks.BOOKSHELF, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Blocks.CHISELED_BOOKSHELF, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Blocks.LECTERN, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Blocks.JUKEBOX, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Blocks.CHEST, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Blocks.TRAPPED_CHEST, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Blocks.CRAFTING_TABLE, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Blocks.DAYLIGHT_DETECTOR, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, ItemTags.BANNERS, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Items.BOW, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Items.FISHING_ROD, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Blocks.LADDER, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, ItemTags.SIGNS, 200);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, ItemTags.HANGING_SIGNS, 800);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Items.WOODEN_SHOVEL, 200);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Items.WOODEN_SWORD, 200);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Items.WOODEN_HOE, 200);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Items.WOODEN_AXE, 200);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Items.WOODEN_PICKAXE, 200);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, ItemTags.WOODEN_DOORS, 200);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, ItemTags.BOATS, 1200);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, ItemTags.WOOL, 100);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, ItemTags.WOODEN_BUTTONS, 100);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Items.STICK, 100);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, ItemTags.SAPLINGS, 100);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Items.BOWL, 100);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, ItemTags.WOOL_CARPETS, 67);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Blocks.DRIED_KELP_BLOCK, 4001);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Items.CROSSBOW, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Blocks.BAMBOO, 50);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Blocks.DEAD_BUSH, 100);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Blocks.SCAFFOLDING, 50);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Blocks.LOOM, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Blocks.BARREL, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Blocks.CARTOGRAPHY_TABLE, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Blocks.FLETCHING_TABLE, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Blocks.SMITHING_TABLE, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Blocks.COMPOSTER, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Blocks.AZALEA, 100);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Blocks.FLOWERING_AZALEA, 100);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)$$0, Blocks.MANGROVE_ROOTS, 300);
        return $$0;
    }

    private static boolean isNeverAFurnaceFuel(Item $$0) {
        return $$0.builtInRegistryHolder().is(ItemTags.NON_FLAMMABLE_WOOD);
    }

    private static void add(Map<Item, Integer> $$0, TagKey<Item> $$1, int $$2) {
        for (Holder $$3 : BuiltInRegistries.ITEM.getTagOrEmpty($$1)) {
            if (AbstractFurnaceBlockEntity.isNeverAFurnaceFuel((Item)$$3.value())) continue;
            $$0.put((Object)((Item)$$3.value()), (Object)$$2);
        }
    }

    private static void add(Map<Item, Integer> $$0, ItemLike $$1, int $$2) {
        Item $$3 = $$1.asItem();
        if (AbstractFurnaceBlockEntity.isNeverAFurnaceFuel($$3)) {
            if (SharedConstants.IS_RUNNING_IN_IDE) {
                throw Util.pauseInIde(new IllegalStateException("A developer tried to explicitly make fire resistant item " + $$3.getName(null).getString() + " a furnace fuel. That will not work!"));
            }
            return;
        }
        $$0.put((Object)$$3, (Object)$$2);
    }

    private boolean isLit() {
        return this.litTime > 0;
    }

    @Override
    public void load(CompoundTag $$0) {
        super.load($$0);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems($$0, this.items);
        this.litTime = $$0.getShort("BurnTime");
        this.cookingProgress = $$0.getShort("CookTime");
        this.cookingTotalTime = $$0.getShort("CookTimeTotal");
        this.litDuration = this.getBurnDuration(this.items.get(1));
        CompoundTag $$1 = $$0.getCompound("RecipesUsed");
        for (String $$2 : $$1.getAllKeys()) {
            this.recipesUsed.put((Object)new ResourceLocation($$2), $$1.getInt($$2));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag $$0) {
        super.saveAdditional($$0);
        $$0.putShort("BurnTime", (short)this.litTime);
        $$0.putShort("CookTime", (short)this.cookingProgress);
        $$0.putShort("CookTimeTotal", (short)this.cookingTotalTime);
        ContainerHelper.saveAllItems($$0, this.items);
        CompoundTag $$12 = new CompoundTag();
        this.recipesUsed.forEach(($$1, $$2) -> $$12.putInt($$1.toString(), (int)$$2));
        $$0.put("RecipesUsed", $$12);
    }

    public static void serverTick(Level $$0, BlockPos $$1, BlockState $$2, AbstractFurnaceBlockEntity $$3) {
        boolean $$8;
        boolean $$4 = $$3.isLit();
        boolean $$5 = false;
        if ($$3.isLit()) {
            --$$3.litTime;
        }
        ItemStack $$6 = $$3.items.get(1);
        boolean $$7 = !$$3.items.get(0).isEmpty();
        boolean bl = $$8 = !$$6.isEmpty();
        if ($$3.isLit() || $$8 && $$7) {
            Recipe<?> $$10;
            if ($$7) {
                Recipe $$9 = (Recipe)$$3.quickCheck.getRecipeFor($$3, $$0).orElse(null);
            } else {
                $$10 = null;
            }
            int $$11 = $$3.getMaxStackSize();
            if (!$$3.isLit() && AbstractFurnaceBlockEntity.canBurn($$0.registryAccess(), $$10, $$3.items, $$11)) {
                $$3.litDuration = $$3.litTime = $$3.getBurnDuration($$6);
                if ($$3.isLit()) {
                    $$5 = true;
                    if ($$8) {
                        Item $$12 = $$6.getItem();
                        $$6.shrink(1);
                        if ($$6.isEmpty()) {
                            Item $$13 = $$12.getCraftingRemainingItem();
                            $$3.items.set(1, $$13 == null ? ItemStack.EMPTY : new ItemStack($$13));
                        }
                    }
                }
            }
            if ($$3.isLit() && AbstractFurnaceBlockEntity.canBurn($$0.registryAccess(), $$10, $$3.items, $$11)) {
                ++$$3.cookingProgress;
                if ($$3.cookingProgress == $$3.cookingTotalTime) {
                    $$3.cookingProgress = 0;
                    $$3.cookingTotalTime = AbstractFurnaceBlockEntity.getTotalCookTime($$0, $$3);
                    if (AbstractFurnaceBlockEntity.burn($$0.registryAccess(), $$10, $$3.items, $$11)) {
                        $$3.setRecipeUsed($$10);
                    }
                    $$5 = true;
                }
            } else {
                $$3.cookingProgress = 0;
            }
        } else if (!$$3.isLit() && $$3.cookingProgress > 0) {
            $$3.cookingProgress = Mth.clamp($$3.cookingProgress - 2, 0, $$3.cookingTotalTime);
        }
        if ($$4 != $$3.isLit()) {
            $$5 = true;
            $$2 = (BlockState)$$2.setValue(AbstractFurnaceBlock.LIT, $$3.isLit());
            $$0.setBlock($$1, $$2, 3);
        }
        if ($$5) {
            AbstractFurnaceBlockEntity.setChanged($$0, $$1, $$2);
        }
    }

    private static boolean canBurn(RegistryAccess $$0, @Nullable Recipe<?> $$1, NonNullList<ItemStack> $$2, int $$3) {
        if ($$2.get(0).isEmpty() || $$1 == null) {
            return false;
        }
        ItemStack $$4 = $$1.getResultItem($$0);
        if ($$4.isEmpty()) {
            return false;
        }
        ItemStack $$5 = $$2.get(2);
        if ($$5.isEmpty()) {
            return true;
        }
        if (!$$5.sameItem($$4)) {
            return false;
        }
        if ($$5.getCount() < $$3 && $$5.getCount() < $$5.getMaxStackSize()) {
            return true;
        }
        return $$5.getCount() < $$4.getMaxStackSize();
    }

    private static boolean burn(RegistryAccess $$0, @Nullable Recipe<?> $$1, NonNullList<ItemStack> $$2, int $$3) {
        if ($$1 == null || !AbstractFurnaceBlockEntity.canBurn($$0, $$1, $$2, $$3)) {
            return false;
        }
        ItemStack $$4 = $$2.get(0);
        ItemStack $$5 = $$1.getResultItem($$0);
        ItemStack $$6 = $$2.get(2);
        if ($$6.isEmpty()) {
            $$2.set(2, $$5.copy());
        } else if ($$6.is($$5.getItem())) {
            $$6.grow(1);
        }
        if ($$4.is(Blocks.WET_SPONGE.asItem()) && !$$2.get(1).isEmpty() && $$2.get(1).is(Items.BUCKET)) {
            $$2.set(1, new ItemStack(Items.WATER_BUCKET));
        }
        $$4.shrink(1);
        return true;
    }

    protected int getBurnDuration(ItemStack $$0) {
        if ($$0.isEmpty()) {
            return 0;
        }
        Item $$1 = $$0.getItem();
        return (Integer)AbstractFurnaceBlockEntity.getFuel().getOrDefault((Object)$$1, (Object)0);
    }

    private static int getTotalCookTime(Level $$0, AbstractFurnaceBlockEntity $$1) {
        return (Integer)$$1.quickCheck.getRecipeFor($$1, $$0).map(AbstractCookingRecipe::getCookingTime).orElse((Object)200);
    }

    public static boolean isFuel(ItemStack $$0) {
        return AbstractFurnaceBlockEntity.getFuel().containsKey((Object)$$0.getItem());
    }

    @Override
    public int[] getSlotsForFace(Direction $$0) {
        if ($$0 == Direction.DOWN) {
            return SLOTS_FOR_DOWN;
        }
        if ($$0 == Direction.UP) {
            return SLOTS_FOR_UP;
        }
        return SLOTS_FOR_SIDES;
    }

    @Override
    public boolean canPlaceItemThroughFace(int $$0, ItemStack $$1, @Nullable Direction $$2) {
        return this.canPlaceItem($$0, $$1);
    }

    @Override
    public boolean canTakeItemThroughFace(int $$0, ItemStack $$1, Direction $$2) {
        if ($$2 == Direction.DOWN && $$0 == 1) {
            return $$1.is(Items.WATER_BUCKET) || $$1.is(Items.BUCKET);
        }
        return true;
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        Iterator iterator = this.items.iterator();
        while (iterator.hasNext()) {
            ItemStack $$0 = (ItemStack)iterator.next();
            if ($$0.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int $$0) {
        return this.items.get($$0);
    }

    @Override
    public ItemStack removeItem(int $$0, int $$1) {
        return ContainerHelper.removeItem(this.items, $$0, $$1);
    }

    @Override
    public ItemStack removeItemNoUpdate(int $$0) {
        return ContainerHelper.takeItem(this.items, $$0);
    }

    @Override
    public void setItem(int $$0, ItemStack $$1) {
        ItemStack $$2 = this.items.get($$0);
        boolean $$3 = !$$1.isEmpty() && $$1.sameItem($$2) && ItemStack.tagMatches($$1, $$2);
        this.items.set($$0, $$1);
        if ($$1.getCount() > this.getMaxStackSize()) {
            $$1.setCount(this.getMaxStackSize());
        }
        if ($$0 == 0 && !$$3) {
            this.cookingTotalTime = AbstractFurnaceBlockEntity.getTotalCookTime(this.level, this);
            this.cookingProgress = 0;
            this.setChanged();
        }
    }

    @Override
    public boolean stillValid(Player $$0) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        }
        return $$0.distanceToSqr((double)this.worldPosition.getX() + 0.5, (double)this.worldPosition.getY() + 0.5, (double)this.worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public boolean canPlaceItem(int $$0, ItemStack $$1) {
        if ($$0 == 2) {
            return false;
        }
        if ($$0 == 1) {
            ItemStack $$2 = this.items.get(1);
            return AbstractFurnaceBlockEntity.isFuel($$1) || $$1.is(Items.BUCKET) && !$$2.is(Items.BUCKET);
        }
        return true;
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public void setRecipeUsed(@Nullable Recipe<?> $$0) {
        if ($$0 != null) {
            ResourceLocation $$1 = $$0.getId();
            this.recipesUsed.addTo((Object)$$1, 1);
        }
    }

    @Override
    @Nullable
    public Recipe<?> getRecipeUsed() {
        return null;
    }

    @Override
    public void awardUsedRecipes(Player $$0) {
    }

    public void awardUsedRecipesAndPopExperience(ServerPlayer $$0) {
        List<Recipe<?>> $$1 = this.getRecipesToAwardAndPopExperience($$0.getLevel(), $$0.position());
        $$0.awardRecipes((Collection<Recipe<?>>)$$1);
        this.recipesUsed.clear();
    }

    public List<Recipe<?>> getRecipesToAwardAndPopExperience(ServerLevel $$0, Vec3 $$1) {
        ArrayList $$2 = Lists.newArrayList();
        for (Object2IntMap.Entry $$3 : this.recipesUsed.object2IntEntrySet()) {
            $$0.getRecipeManager().byKey((ResourceLocation)$$3.getKey()).ifPresent(arg_0 -> AbstractFurnaceBlockEntity.lambda$getRecipesToAwardAndPopExperience$1((List)$$2, $$0, $$1, $$3, arg_0));
        }
        return $$2;
    }

    private static void createExperience(ServerLevel $$0, Vec3 $$1, int $$2, float $$3) {
        int $$4 = Mth.floor((float)$$2 * $$3);
        float $$5 = Mth.frac((float)$$2 * $$3);
        if ($$5 != 0.0f && Math.random() < (double)$$5) {
            ++$$4;
        }
        ExperienceOrb.award($$0, $$1, $$4);
    }

    @Override
    public void fillStackedContents(StackedContents $$0) {
        Iterator iterator = this.items.iterator();
        while (iterator.hasNext()) {
            ItemStack $$1 = (ItemStack)iterator.next();
            $$0.accountStack($$1);
        }
    }

    private static /* synthetic */ void lambda$getRecipesToAwardAndPopExperience$1(List $$0, ServerLevel $$1, Vec3 $$2, Object2IntMap.Entry $$3, Recipe $$4) {
        $$0.add((Object)$$4);
        AbstractFurnaceBlockEntity.createExperience($$1, $$2, $$3.getIntValue(), ((AbstractCookingRecipe)$$4).getExperience());
    }
}