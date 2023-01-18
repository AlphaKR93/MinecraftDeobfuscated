/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  java.lang.Comparable
 *  java.lang.Exception
 *  java.lang.IllegalStateException
 *  java.lang.NullPointerException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Runnable
 *  java.lang.String
 *  java.util.List
 *  java.util.Locale
 *  java.util.UUID
 *  java.util.function.Consumer
 *  java.util.function.Function
 *  java.util.function.Predicate
 *  java.util.function.Supplier
 *  java.util.stream.LongStream
 *  javax.annotation.Nullable
 */
package net.minecraft.gametest.framework;

import com.mojang.authlib.GameProfile;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.LongStream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestAssertPosException;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.GameTestSequence;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class GameTestHelper {
    private final GameTestInfo testInfo;
    private boolean finalCheckAdded;

    public GameTestHelper(GameTestInfo $$0) {
        this.testInfo = $$0;
    }

    public ServerLevel getLevel() {
        return this.testInfo.getLevel();
    }

    public BlockState getBlockState(BlockPos $$0) {
        return this.getLevel().getBlockState(this.absolutePos($$0));
    }

    @Nullable
    public BlockEntity getBlockEntity(BlockPos $$0) {
        return this.getLevel().getBlockEntity(this.absolutePos($$0));
    }

    public void killAllEntities() {
        AABB $$02 = this.getBounds();
        List $$1 = this.getLevel().getEntitiesOfClass(Entity.class, $$02.inflate(1.0), $$0 -> !($$0 instanceof Player));
        $$1.forEach(Entity::kill);
    }

    public ItemEntity spawnItem(Item $$0, float $$1, float $$2, float $$3) {
        ServerLevel $$4 = this.getLevel();
        Vec3 $$5 = this.absoluteVec(new Vec3($$1, $$2, $$3));
        ItemEntity $$6 = new ItemEntity($$4, $$5.x, $$5.y, $$5.z, new ItemStack($$0, 1));
        $$6.setDeltaMovement(0.0, 0.0, 0.0);
        $$4.addFreshEntity($$6);
        return $$6;
    }

    public ItemEntity spawnItem(Item $$0, BlockPos $$1) {
        return this.spawnItem($$0, $$1.getX(), $$1.getY(), $$1.getZ());
    }

    public <E extends Entity> E spawn(EntityType<E> $$0, BlockPos $$1) {
        return this.spawn($$0, Vec3.atBottomCenterOf($$1));
    }

    public <E extends Entity> E spawn(EntityType<E> $$0, Vec3 $$1) {
        ServerLevel $$2 = this.getLevel();
        E $$3 = $$0.create($$2);
        if ($$3 == null) {
            throw new NullPointerException("Failed to create entity " + $$0.builtInRegistryHolder().key().location());
        }
        if ($$3 instanceof Mob) {
            Mob $$4 = (Mob)$$3;
            $$4.setPersistenceRequired();
        }
        Vec3 $$5 = this.absoluteVec($$1);
        ((Entity)$$3).moveTo($$5.x, $$5.y, $$5.z, ((Entity)$$3).getYRot(), ((Entity)$$3).getXRot());
        $$2.addFreshEntity((Entity)$$3);
        return $$3;
    }

    public <E extends Entity> E spawn(EntityType<E> $$0, int $$1, int $$2, int $$3) {
        return this.spawn($$0, new BlockPos($$1, $$2, $$3));
    }

    public <E extends Entity> E spawn(EntityType<E> $$0, float $$1, float $$2, float $$3) {
        return this.spawn($$0, new Vec3($$1, $$2, $$3));
    }

    public <E extends Mob> E spawnWithNoFreeWill(EntityType<E> $$0, BlockPos $$1) {
        Mob $$2 = (Mob)this.spawn($$0, $$1);
        $$2.removeFreeWill();
        return (E)$$2;
    }

    public <E extends Mob> E spawnWithNoFreeWill(EntityType<E> $$0, int $$1, int $$2, int $$3) {
        return this.spawnWithNoFreeWill($$0, new BlockPos($$1, $$2, $$3));
    }

    public <E extends Mob> E spawnWithNoFreeWill(EntityType<E> $$0, Vec3 $$1) {
        Mob $$2 = (Mob)this.spawn($$0, $$1);
        $$2.removeFreeWill();
        return (E)$$2;
    }

    public <E extends Mob> E spawnWithNoFreeWill(EntityType<E> $$0, float $$1, float $$2, float $$3) {
        return this.spawnWithNoFreeWill($$0, new Vec3($$1, $$2, $$3));
    }

    public GameTestSequence walkTo(Mob $$0, BlockPos $$1, float $$2) {
        return this.startSequence().thenExecuteAfter(2, () -> {
            Path $$3 = $$0.getNavigation().createPath(this.absolutePos($$1), 0);
            $$0.getNavigation().moveTo($$3, (double)$$2);
        });
    }

    public void pressButton(int $$0, int $$1, int $$2) {
        this.pressButton(new BlockPos($$0, $$1, $$2));
    }

    public void pressButton(BlockPos $$02) {
        this.assertBlockState($$02, (Predicate<BlockState>)((Predicate)$$0 -> $$0.is(BlockTags.BUTTONS)), (Supplier<String>)((Supplier)() -> "Expected button"));
        BlockPos $$1 = this.absolutePos($$02);
        BlockState $$2 = this.getLevel().getBlockState($$1);
        ButtonBlock $$3 = (ButtonBlock)$$2.getBlock();
        $$3.press($$2, this.getLevel(), $$1);
    }

    public void useBlock(BlockPos $$0) {
        this.useBlock($$0, this.makeMockPlayer());
    }

    public void useBlock(BlockPos $$0, Player $$1) {
        BlockPos $$2 = this.absolutePos($$0);
        this.useBlock($$0, $$1, new BlockHitResult(Vec3.atCenterOf($$2), Direction.NORTH, $$2, true));
    }

    public void useBlock(BlockPos $$0, Player $$1, BlockHitResult $$2) {
        BlockPos $$3 = this.absolutePos($$0);
        BlockState $$4 = this.getLevel().getBlockState($$3);
        InteractionResult $$5 = $$4.use(this.getLevel(), $$1, InteractionHand.MAIN_HAND, $$2);
        if (!$$5.consumesAction()) {
            UseOnContext $$6 = new UseOnContext($$1, InteractionHand.MAIN_HAND, $$2);
            $$1.getItemInHand(InteractionHand.MAIN_HAND).useOn($$6);
        }
    }

    public LivingEntity makeAboutToDrown(LivingEntity $$0) {
        $$0.setAirSupply(0);
        $$0.setHealth(0.25f);
        return $$0;
    }

    public Player makeMockSurvivalPlayer() {
        return new Player(this.getLevel(), BlockPos.ZERO, 0.0f, new GameProfile(UUID.randomUUID(), "test-mock-player")){

            @Override
            public boolean isSpectator() {
                return false;
            }

            @Override
            public boolean isCreative() {
                return false;
            }
        };
    }

    public Player makeMockPlayer() {
        return new Player(this.getLevel(), BlockPos.ZERO, 0.0f, new GameProfile(UUID.randomUUID(), "test-mock-player")){

            @Override
            public boolean isSpectator() {
                return false;
            }

            @Override
            public boolean isCreative() {
                return true;
            }

            @Override
            public boolean isLocalPlayer() {
                return true;
            }
        };
    }

    public void pullLever(int $$0, int $$1, int $$2) {
        this.pullLever(new BlockPos($$0, $$1, $$2));
    }

    public void pullLever(BlockPos $$0) {
        this.assertBlockPresent(Blocks.LEVER, $$0);
        BlockPos $$1 = this.absolutePos($$0);
        BlockState $$2 = this.getLevel().getBlockState($$1);
        LeverBlock $$3 = (LeverBlock)$$2.getBlock();
        $$3.pull($$2, this.getLevel(), $$1);
    }

    public void pulseRedstone(BlockPos $$0, long $$1) {
        this.setBlock($$0, Blocks.REDSTONE_BLOCK);
        this.runAfterDelay($$1, () -> this.setBlock($$0, Blocks.AIR));
    }

    public void destroyBlock(BlockPos $$0) {
        this.getLevel().destroyBlock(this.absolutePos($$0), false, null);
    }

    public void setBlock(int $$0, int $$1, int $$2, Block $$3) {
        this.setBlock(new BlockPos($$0, $$1, $$2), $$3);
    }

    public void setBlock(int $$0, int $$1, int $$2, BlockState $$3) {
        this.setBlock(new BlockPos($$0, $$1, $$2), $$3);
    }

    public void setBlock(BlockPos $$0, Block $$1) {
        this.setBlock($$0, $$1.defaultBlockState());
    }

    public void setBlock(BlockPos $$0, BlockState $$1) {
        this.getLevel().setBlock(this.absolutePos($$0), $$1, 3);
    }

    public void setNight() {
        this.setDayTime(13000);
    }

    public void setDayTime(int $$0) {
        this.getLevel().setDayTime($$0);
    }

    public void assertBlockPresent(Block $$0, int $$1, int $$2, int $$3) {
        this.assertBlockPresent($$0, new BlockPos($$1, $$2, $$3));
    }

    public void assertBlockPresent(Block $$0, BlockPos $$1) {
        BlockState $$22 = this.getBlockState($$1);
        this.assertBlock($$1, (Predicate<Block>)((Predicate)$$2 -> $$22.is($$0)), "Expected " + $$0.getName().getString() + ", got " + $$22.getBlock().getName().getString());
    }

    public void assertBlockNotPresent(Block $$0, int $$1, int $$2, int $$3) {
        this.assertBlockNotPresent($$0, new BlockPos($$1, $$2, $$3));
    }

    public void assertBlockNotPresent(Block $$0, BlockPos $$1) {
        this.assertBlock($$1, (Predicate<Block>)((Predicate)$$2 -> !this.getBlockState($$1).is($$0)), "Did not expect " + $$0.getName().getString());
    }

    public void succeedWhenBlockPresent(Block $$0, int $$1, int $$2, int $$3) {
        this.succeedWhenBlockPresent($$0, new BlockPos($$1, $$2, $$3));
    }

    public void succeedWhenBlockPresent(Block $$0, BlockPos $$1) {
        this.succeedWhen(() -> this.assertBlockPresent($$0, $$1));
    }

    public void assertBlock(BlockPos $$0, Predicate<Block> $$1, String $$2) {
        this.assertBlock($$0, $$1, (Supplier<String>)((Supplier)() -> $$2));
    }

    public void assertBlock(BlockPos $$0, Predicate<Block> $$12, Supplier<String> $$2) {
        this.assertBlockState($$0, (Predicate<BlockState>)((Predicate)$$1 -> $$12.test((Object)$$1.getBlock())), $$2);
    }

    public <T extends Comparable<T>> void assertBlockProperty(BlockPos $$0, Property<T> $$1, T $$2) {
        BlockState $$3 = this.getBlockState($$0);
        boolean $$4 = $$3.hasProperty($$1);
        if (!$$4 || !$$3.getValue($$1).equals($$2)) {
            String $$5 = $$4 ? "was " + $$3.getValue($$1) : "property " + $$1.getName() + " is missing";
            String $$6 = String.format((Locale)Locale.ROOT, (String)"Expected property %s to be %s, %s", (Object[])new Object[]{$$1.getName(), $$2, $$5});
            throw new GameTestAssertPosException($$6, this.absolutePos($$0), $$0, this.testInfo.getTick());
        }
    }

    public <T extends Comparable<T>> void assertBlockProperty(BlockPos $$0, Property<T> $$1, Predicate<T> $$22, String $$3) {
        this.assertBlockState($$0, (Predicate<BlockState>)((Predicate)$$2 -> $$22.test($$2.getValue($$1))), (Supplier<String>)((Supplier)() -> $$3));
    }

    public void assertBlockState(BlockPos $$0, Predicate<BlockState> $$1, Supplier<String> $$2) {
        BlockState $$3 = this.getBlockState($$0);
        if (!$$1.test((Object)$$3)) {
            throw new GameTestAssertPosException((String)$$2.get(), this.absolutePos($$0), $$0, this.testInfo.getTick());
        }
    }

    public void assertEntityPresent(EntityType<?> $$0) {
        List $$1 = this.getLevel().getEntities($$0, this.getBounds(), Entity::isAlive);
        if ($$1.isEmpty()) {
            throw new GameTestAssertException("Expected " + $$0.toShortString() + " to exist");
        }
    }

    public void assertEntityPresent(EntityType<?> $$0, int $$1, int $$2, int $$3) {
        this.assertEntityPresent($$0, new BlockPos($$1, $$2, $$3));
    }

    public void assertEntityPresent(EntityType<?> $$0, BlockPos $$1) {
        BlockPos $$2 = this.absolutePos($$1);
        List $$3 = this.getLevel().getEntities($$0, new AABB($$2), Entity::isAlive);
        if ($$3.isEmpty()) {
            throw new GameTestAssertPosException("Expected " + $$0.toShortString(), $$2, $$1, this.testInfo.getTick());
        }
    }

    public void assertEntityPresent(EntityType<?> $$0, Vec3 $$1, Vec3 $$2) {
        List $$3 = this.getLevel().getEntities($$0, new AABB($$1, $$2), Entity::isAlive);
        if ($$3.isEmpty()) {
            throw new GameTestAssertPosException("Expected " + $$0.toShortString() + " between ", new BlockPos($$1), new BlockPos($$2), this.testInfo.getTick());
        }
    }

    public void assertEntitiesPresent(EntityType<?> $$0, BlockPos $$1, int $$2, double $$3) {
        BlockPos $$4 = this.absolutePos($$1);
        List<?> $$5 = this.getEntities($$0, $$1, $$3);
        if ($$5.size() != $$2) {
            throw new GameTestAssertPosException("Expected " + $$2 + " entities of type " + $$0.toShortString() + ", actual number of entities found=" + $$5.size(), $$4, $$1, this.testInfo.getTick());
        }
    }

    public void assertEntityPresent(EntityType<?> $$0, BlockPos $$1, double $$2) {
        List<?> $$3 = this.getEntities($$0, $$1, $$2);
        if ($$3.isEmpty()) {
            BlockPos $$4 = this.absolutePos($$1);
            throw new GameTestAssertPosException("Expected " + $$0.toShortString(), $$4, $$1, this.testInfo.getTick());
        }
    }

    public <T extends Entity> List<T> getEntities(EntityType<T> $$0, BlockPos $$1, double $$2) {
        BlockPos $$3 = this.absolutePos($$1);
        return this.getLevel().getEntities($$0, new AABB($$3).inflate($$2), Entity::isAlive);
    }

    public void assertEntityInstancePresent(Entity $$0, int $$1, int $$2, int $$3) {
        this.assertEntityInstancePresent($$0, new BlockPos($$1, $$2, $$3));
    }

    public void assertEntityInstancePresent(Entity $$0, BlockPos $$12) {
        BlockPos $$2 = this.absolutePos($$12);
        List $$3 = this.getLevel().getEntities($$0.getType(), new AABB($$2), Entity::isAlive);
        $$3.stream().filter($$1 -> $$1 == $$0).findFirst().orElseThrow(() -> new GameTestAssertPosException("Expected " + $$0.getType().toShortString(), $$2, $$12, this.testInfo.getTick()));
    }

    public void assertItemEntityCountIs(Item $$0, BlockPos $$1, double $$2, int $$3) {
        BlockPos $$4 = this.absolutePos($$1);
        List $$5 = this.getLevel().getEntities(EntityType.ITEM, new AABB($$4).inflate($$2), Entity::isAlive);
        int $$6 = 0;
        for (Entity $$7 : $$5) {
            ItemEntity $$8 = (ItemEntity)$$7;
            if (!$$8.getItem().getItem().equals($$0)) continue;
            $$6 += $$8.getItem().getCount();
        }
        if ($$6 != $$3) {
            throw new GameTestAssertPosException("Expected " + $$3 + " " + $$0.getDescription().getString() + " items to exist (found " + $$6 + ")", $$4, $$1, this.testInfo.getTick());
        }
    }

    public void assertItemEntityPresent(Item $$0, BlockPos $$1, double $$2) {
        BlockPos $$3 = this.absolutePos($$1);
        List $$4 = this.getLevel().getEntities(EntityType.ITEM, new AABB($$3).inflate($$2), Entity::isAlive);
        for (Entity $$5 : $$4) {
            ItemEntity $$6 = (ItemEntity)$$5;
            if (!$$6.getItem().getItem().equals($$0)) continue;
            return;
        }
        throw new GameTestAssertPosException("Expected " + $$0.getDescription().getString() + " item", $$3, $$1, this.testInfo.getTick());
    }

    public void assertItemEntityNotPresent(Item $$0, BlockPos $$1, double $$2) {
        BlockPos $$3 = this.absolutePos($$1);
        List $$4 = this.getLevel().getEntities(EntityType.ITEM, new AABB($$3).inflate($$2), Entity::isAlive);
        for (Entity $$5 : $$4) {
            ItemEntity $$6 = (ItemEntity)$$5;
            if (!$$6.getItem().getItem().equals($$0)) continue;
            throw new GameTestAssertPosException("Did not expect " + $$0.getDescription().getString() + " item", $$3, $$1, this.testInfo.getTick());
        }
    }

    public void assertEntityNotPresent(EntityType<?> $$0) {
        List $$1 = this.getLevel().getEntities($$0, this.getBounds(), Entity::isAlive);
        if (!$$1.isEmpty()) {
            throw new GameTestAssertException("Did not expect " + $$0.toShortString() + " to exist");
        }
    }

    public void assertEntityNotPresent(EntityType<?> $$0, int $$1, int $$2, int $$3) {
        this.assertEntityNotPresent($$0, new BlockPos($$1, $$2, $$3));
    }

    public void assertEntityNotPresent(EntityType<?> $$0, BlockPos $$1) {
        BlockPos $$2 = this.absolutePos($$1);
        List $$3 = this.getLevel().getEntities($$0, new AABB($$2), Entity::isAlive);
        if (!$$3.isEmpty()) {
            throw new GameTestAssertPosException("Did not expect " + $$0.toShortString(), $$2, $$1, this.testInfo.getTick());
        }
    }

    public void assertEntityTouching(EntityType<?> $$0, double $$12, double $$2, double $$3) {
        Vec3 $$4 = new Vec3($$12, $$2, $$3);
        Vec3 $$5 = this.absoluteVec($$4);
        Predicate $$6 = $$1 -> $$1.getBoundingBox().intersects($$5, $$5);
        List $$7 = this.getLevel().getEntities($$0, this.getBounds(), $$6);
        if ($$7.isEmpty()) {
            throw new GameTestAssertException("Expected " + $$0.toShortString() + " to touch " + $$5 + " (relative " + $$4 + ")");
        }
    }

    public void assertEntityNotTouching(EntityType<?> $$0, double $$12, double $$2, double $$3) {
        Vec3 $$4 = new Vec3($$12, $$2, $$3);
        Vec3 $$5 = this.absoluteVec($$4);
        Predicate $$6 = $$1 -> !$$1.getBoundingBox().intersects($$5, $$5);
        List $$7 = this.getLevel().getEntities($$0, this.getBounds(), $$6);
        if ($$7.isEmpty()) {
            throw new GameTestAssertException("Did not expect " + $$0.toShortString() + " to touch " + $$5 + " (relative " + $$4 + ")");
        }
    }

    public <E extends Entity, T> void assertEntityData(BlockPos $$0, EntityType<E> $$1, Function<? super E, T> $$2, @Nullable T $$3) {
        BlockPos $$4 = this.absolutePos($$0);
        List $$5 = this.getLevel().getEntities($$1, new AABB($$4), Entity::isAlive);
        if ($$5.isEmpty()) {
            throw new GameTestAssertPosException("Expected " + $$1.toShortString(), $$4, $$0, this.testInfo.getTick());
        }
        for (Entity $$6 : $$5) {
            Object $$7 = $$2.apply((Object)$$6);
            if (!($$7 == null ? $$3 != null : !$$7.equals($$3))) continue;
            throw new GameTestAssertException("Expected entity data to be: " + $$3 + ", but was: " + $$7);
        }
    }

    public <E extends LivingEntity> void assertEntityIsHolding(BlockPos $$0, EntityType<E> $$1, Item $$2) {
        BlockPos $$3 = this.absolutePos($$0);
        List $$4 = this.getLevel().getEntities($$1, new AABB($$3), Entity::isAlive);
        if ($$4.isEmpty()) {
            throw new GameTestAssertPosException("Expected entity of type: " + $$1, $$3, $$0, this.getTick());
        }
        for (LivingEntity $$5 : $$4) {
            if (!$$5.isHolding($$2)) continue;
            return;
        }
        throw new GameTestAssertPosException("Entity should be holding: " + $$2, $$3, $$0, this.getTick());
    }

    public <E extends Entity> void assertEntityInventoryContains(BlockPos $$02, EntityType<E> $$12, Item $$2) {
        BlockPos $$3 = this.absolutePos($$02);
        List $$4 = this.getLevel().getEntities($$12, new AABB($$3), $$0 -> ((Entity)$$0).isAlive());
        if ($$4.isEmpty()) {
            throw new GameTestAssertPosException("Expected " + $$12.toShortString() + " to exist", $$3, $$02, this.getTick());
        }
        for (Entity $$5 : $$4) {
            if (!((InventoryCarrier)((Object)$$5)).getInventory().hasAnyMatching($$1 -> $$1.is($$2))) continue;
            return;
        }
        throw new GameTestAssertPosException("Entity inventory should contain: " + $$2, $$3, $$02, this.getTick());
    }

    public void assertContainerEmpty(BlockPos $$0) {
        BlockPos $$1 = this.absolutePos($$0);
        BlockEntity $$2 = this.getLevel().getBlockEntity($$1);
        if ($$2 instanceof BaseContainerBlockEntity && !((BaseContainerBlockEntity)$$2).isEmpty()) {
            throw new GameTestAssertException("Container should be empty");
        }
    }

    public void assertContainerContains(BlockPos $$0, Item $$1) {
        BlockPos $$2 = this.absolutePos($$0);
        BlockEntity $$3 = this.getLevel().getBlockEntity($$2);
        if (!($$3 instanceof BaseContainerBlockEntity)) {
            throw new GameTestAssertException("Expected a container at " + $$0 + ", found " + BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey($$3.getType()));
        }
        if (((BaseContainerBlockEntity)$$3).countItem($$1) != 1) {
            throw new GameTestAssertException("Container should contain: " + $$1);
        }
    }

    public void assertSameBlockStates(BoundingBox $$0, BlockPos $$1) {
        BlockPos.betweenClosedStream($$0).forEach($$2 -> {
            BlockPos $$3 = $$1.offset($$2.getX() - $$0.minX(), $$2.getY() - $$0.minY(), $$2.getZ() - $$0.minZ());
            this.assertSameBlockState((BlockPos)$$2, $$3);
        });
    }

    public void assertSameBlockState(BlockPos $$0, BlockPos $$1) {
        BlockState $$3;
        BlockState $$2 = this.getBlockState($$0);
        if ($$2 != ($$3 = this.getBlockState($$1))) {
            this.fail("Incorrect state. Expected " + $$3 + ", got " + $$2, $$0);
        }
    }

    public void assertAtTickTimeContainerContains(long $$0, BlockPos $$1, Item $$2) {
        this.runAtTickTime($$0, () -> this.assertContainerContains($$1, $$2));
    }

    public void assertAtTickTimeContainerEmpty(long $$0, BlockPos $$1) {
        this.runAtTickTime($$0, () -> this.assertContainerEmpty($$1));
    }

    public <E extends Entity, T> void succeedWhenEntityData(BlockPos $$0, EntityType<E> $$1, Function<E, T> $$2, T $$3) {
        this.succeedWhen(() -> this.assertEntityData($$0, $$1, $$2, $$3));
    }

    public <E extends Entity> void assertEntityProperty(E $$0, Predicate<E> $$1, String $$2) {
        if (!$$1.test($$0)) {
            throw new GameTestAssertException("Entity " + $$0 + " failed " + $$2 + " test");
        }
    }

    public <E extends Entity, T> void assertEntityProperty(E $$0, Function<E, T> $$1, String $$2, T $$3) {
        Object $$4 = $$1.apply($$0);
        if (!$$4.equals($$3)) {
            throw new GameTestAssertException("Entity " + $$0 + " value " + $$2 + "=" + $$4 + " is not equal to expected " + $$3);
        }
    }

    public void succeedWhenEntityPresent(EntityType<?> $$0, int $$1, int $$2, int $$3) {
        this.succeedWhenEntityPresent($$0, new BlockPos($$1, $$2, $$3));
    }

    public void succeedWhenEntityPresent(EntityType<?> $$0, BlockPos $$1) {
        this.succeedWhen(() -> this.assertEntityPresent($$0, $$1));
    }

    public void succeedWhenEntityNotPresent(EntityType<?> $$0, int $$1, int $$2, int $$3) {
        this.succeedWhenEntityNotPresent($$0, new BlockPos($$1, $$2, $$3));
    }

    public void succeedWhenEntityNotPresent(EntityType<?> $$0, BlockPos $$1) {
        this.succeedWhen(() -> this.assertEntityNotPresent($$0, $$1));
    }

    public void succeed() {
        this.testInfo.succeed();
    }

    private void ensureSingleFinalCheck() {
        if (this.finalCheckAdded) {
            throw new IllegalStateException("This test already has final clause");
        }
        this.finalCheckAdded = true;
    }

    public void succeedIf(Runnable $$0) {
        this.ensureSingleFinalCheck();
        this.testInfo.createSequence().thenWaitUntil(0L, $$0).thenSucceed();
    }

    public void succeedWhen(Runnable $$0) {
        this.ensureSingleFinalCheck();
        this.testInfo.createSequence().thenWaitUntil($$0).thenSucceed();
    }

    public void succeedOnTickWhen(int $$0, Runnable $$1) {
        this.ensureSingleFinalCheck();
        this.testInfo.createSequence().thenWaitUntil($$0, $$1).thenSucceed();
    }

    public void runAtTickTime(long $$0, Runnable $$1) {
        this.testInfo.setRunAtTickTime($$0, $$1);
    }

    public void runAfterDelay(long $$0, Runnable $$1) {
        this.runAtTickTime(this.testInfo.getTick() + $$0, $$1);
    }

    public void randomTick(BlockPos $$0) {
        BlockPos $$1 = this.absolutePos($$0);
        ServerLevel $$2 = this.getLevel();
        $$2.getBlockState($$1).randomTick($$2, $$1, $$2.random);
    }

    public int getHeight(Heightmap.Types $$0, int $$1, int $$2) {
        BlockPos $$3 = this.absolutePos(new BlockPos($$1, 0, $$2));
        return this.relativePos(this.getLevel().getHeightmapPos($$0, $$3)).getY();
    }

    public void fail(String $$0, BlockPos $$1) {
        throw new GameTestAssertPosException($$0, this.absolutePos($$1), $$1, this.getTick());
    }

    public void fail(String $$0, Entity $$1) {
        throw new GameTestAssertPosException($$0, $$1.blockPosition(), this.relativePos($$1.blockPosition()), this.getTick());
    }

    public void fail(String $$0) {
        throw new GameTestAssertException($$0);
    }

    public void failIf(Runnable $$0) {
        this.testInfo.createSequence().thenWaitUntil($$0).thenFail((Supplier<Exception>)((Supplier)() -> new GameTestAssertException("Fail conditions met")));
    }

    public void failIfEver(Runnable $$0) {
        LongStream.range((long)this.testInfo.getTick(), (long)this.testInfo.getTimeoutTicks()).forEach($$1 -> this.testInfo.setRunAtTickTime($$1, () -> ((Runnable)$$0).run()));
    }

    public GameTestSequence startSequence() {
        return this.testInfo.createSequence();
    }

    public BlockPos absolutePos(BlockPos $$0) {
        BlockPos $$1 = this.testInfo.getStructureBlockPos();
        Vec3i $$2 = $$1.offset($$0);
        return StructureTemplate.transform((BlockPos)$$2, Mirror.NONE, this.testInfo.getRotation(), $$1);
    }

    public BlockPos relativePos(BlockPos $$0) {
        BlockPos $$1 = this.testInfo.getStructureBlockPos();
        Rotation $$2 = this.testInfo.getRotation().getRotated(Rotation.CLOCKWISE_180);
        BlockPos $$3 = StructureTemplate.transform($$0, Mirror.NONE, $$2, $$1);
        return $$3.subtract($$1);
    }

    public Vec3 absoluteVec(Vec3 $$0) {
        Vec3 $$1 = Vec3.atLowerCornerOf(this.testInfo.getStructureBlockPos());
        return StructureTemplate.transform($$1.add($$0), Mirror.NONE, this.testInfo.getRotation(), this.testInfo.getStructureBlockPos());
    }

    public Vec3 relativeVec(Vec3 $$0) {
        Vec3 $$1 = Vec3.atLowerCornerOf(this.testInfo.getStructureBlockPos());
        return StructureTemplate.transform($$0.subtract($$1), Mirror.NONE, this.testInfo.getRotation(), this.testInfo.getStructureBlockPos());
    }

    public void assertTrue(boolean $$0, String $$1) {
        if (!$$0) {
            throw new GameTestAssertException($$1);
        }
    }

    public long getTick() {
        return this.testInfo.getTick();
    }

    private AABB getBounds() {
        return this.testInfo.getStructureBounds();
    }

    private AABB getRelativeBounds() {
        AABB $$0 = this.testInfo.getStructureBounds();
        return $$0.move((BlockPos)BlockPos.ZERO.subtract(this.absolutePos(BlockPos.ZERO)));
    }

    public void forEveryBlockInStructure(Consumer<BlockPos> $$0) {
        AABB $$1 = this.getRelativeBounds();
        BlockPos.MutableBlockPos.betweenClosedStream($$1.move(0.0, 1.0, 0.0)).forEach($$0);
    }

    public void onEachTick(Runnable $$0) {
        LongStream.range((long)this.testInfo.getTick(), (long)this.testInfo.getTimeoutTicks()).forEach($$1 -> this.testInfo.setRunAtTickTime($$1, () -> ((Runnable)$$0).run()));
    }

    public void placeAt(Player $$0, ItemStack $$1, BlockPos $$2, Direction $$3) {
        BlockPos $$4 = this.absolutePos((BlockPos)$$2.relative($$3));
        BlockHitResult $$5 = new BlockHitResult(Vec3.atCenterOf($$4), $$3, $$4, false);
        UseOnContext $$6 = new UseOnContext($$0, InteractionHand.MAIN_HAND, $$5);
        $$1.useOn($$6);
    }
}