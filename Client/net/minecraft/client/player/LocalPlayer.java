/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  java.lang.Byte
 *  java.lang.Double
 *  java.lang.Float
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Objects
 *  java.util.Spliterator
 *  java.util.stream.StreamSupport
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.player;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.inventory.CommandBlockEditScreen;
import net.minecraft.client.gui.screens.inventory.HangingSignEditScreen;
import net.minecraft.client.gui.screens.inventory.JigsawBlockEditScreen;
import net.minecraft.client.gui.screens.inventory.MinecartCommandBlockEditScreen;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.minecraft.client.gui.screens.inventory.StructureBlockEditScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.resources.sounds.AmbientSoundHandler;
import net.minecraft.client.resources.sounds.BiomeAmbientSoundsHandler;
import net.minecraft.client.resources.sounds.BubbleColumnAmbientSoundHandler;
import net.minecraft.client.resources.sounds.ElytraOnPlayerSoundInstance;
import net.minecraft.client.resources.sounds.RidingMinecartSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.UnderwaterAmbientSoundHandler;
import net.minecraft.client.resources.sounds.UnderwaterAmbientSoundInstances;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.protocol.game.ServerboundRecipeBookSeenRecipePacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.StatsCounter;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.slf4j.Logger;

public class LocalPlayer
extends AbstractClientPlayer {
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final int POSITION_REMINDER_INTERVAL = 20;
    private static final int WATER_VISION_MAX_TIME = 600;
    private static final int WATER_VISION_QUICK_TIME = 100;
    private static final float WATER_VISION_QUICK_PERCENT = 0.6f;
    private static final double SUFFOCATING_COLLISION_CHECK_SCALE = 0.35;
    private static final double MINOR_COLLISION_ANGLE_THRESHOLD_RADIAN = 0.13962633907794952;
    private static final float DEFAULT_SNEAKING_MOVEMENT_FACTOR = 0.3f;
    public final ClientPacketListener connection;
    private final StatsCounter stats;
    private final ClientRecipeBook recipeBook;
    private final List<AmbientSoundHandler> ambientSoundHandlers = Lists.newArrayList();
    private int permissionLevel = 0;
    private double xLast;
    private double yLast1;
    private double zLast;
    private float yRotLast;
    private float xRotLast;
    private boolean lastOnGround;
    private boolean crouching;
    private boolean wasShiftKeyDown;
    private boolean wasSprinting;
    private int positionReminder;
    private boolean flashOnSetHealth;
    @Nullable
    private String serverBrand;
    public Input input;
    protected final Minecraft minecraft;
    protected int sprintTriggerTime;
    public float yBob;
    public float xBob;
    public float yBobO;
    public float xBobO;
    private int jumpRidingTicks;
    private float jumpRidingScale;
    public float portalTime;
    public float oPortalTime;
    private boolean startedUsingItem;
    @Nullable
    private InteractionHand usingItemHand;
    private boolean handsBusy;
    private boolean autoJumpEnabled = true;
    private int autoJumpTime;
    private boolean wasFallFlying;
    private int waterVisionTime;
    private boolean showDeathScreen = true;

    public LocalPlayer(Minecraft $$0, ClientLevel $$1, ClientPacketListener $$2, StatsCounter $$3, ClientRecipeBook $$4, boolean $$5, boolean $$6) {
        super($$1, $$2.getLocalGameProfile());
        this.minecraft = $$0;
        this.connection = $$2;
        this.stats = $$3;
        this.recipeBook = $$4;
        this.wasShiftKeyDown = $$5;
        this.wasSprinting = $$6;
        this.ambientSoundHandlers.add((Object)new UnderwaterAmbientSoundHandler(this, $$0.getSoundManager()));
        this.ambientSoundHandlers.add((Object)new BubbleColumnAmbientSoundHandler(this));
        this.ambientSoundHandlers.add((Object)new BiomeAmbientSoundsHandler(this, $$0.getSoundManager(), $$1.getBiomeManager()));
    }

    @Override
    public boolean hurt(DamageSource $$0, float $$1) {
        return false;
    }

    @Override
    public void heal(float $$0) {
    }

    @Override
    public boolean startRiding(Entity $$0, boolean $$1) {
        if (!super.startRiding($$0, $$1)) {
            return false;
        }
        if ($$0 instanceof AbstractMinecart) {
            this.minecraft.getSoundManager().play(new RidingMinecartSoundInstance(this, (AbstractMinecart)$$0, true));
            this.minecraft.getSoundManager().play(new RidingMinecartSoundInstance(this, (AbstractMinecart)$$0, false));
        }
        return true;
    }

    @Override
    public void removeVehicle() {
        super.removeVehicle();
        this.handsBusy = false;
    }

    @Override
    public float getViewXRot(float $$0) {
        return this.getXRot();
    }

    @Override
    public float getViewYRot(float $$0) {
        if (this.isPassenger()) {
            return super.getViewYRot($$0);
        }
        return this.getYRot();
    }

    @Override
    public void tick() {
        if (!this.level.hasChunkAt(this.getBlockX(), this.getBlockZ())) {
            return;
        }
        super.tick();
        if (this.isPassenger()) {
            this.connection.send(new ServerboundMovePlayerPacket.Rot(this.getYRot(), this.getXRot(), this.onGround));
            this.connection.send(new ServerboundPlayerInputPacket(this.xxa, this.zza, this.input.jumping, this.input.shiftKeyDown));
            Entity $$0 = this.getRootVehicle();
            if ($$0 != this && $$0.isControlledByLocalInstance()) {
                this.connection.send(new ServerboundMoveVehiclePacket($$0));
                this.sendIsSprintingIfNeeded();
            }
        } else {
            this.sendPosition();
        }
        for (AmbientSoundHandler $$1 : this.ambientSoundHandlers) {
            $$1.tick();
        }
    }

    public float getCurrentMood() {
        for (AmbientSoundHandler $$0 : this.ambientSoundHandlers) {
            if (!($$0 instanceof BiomeAmbientSoundsHandler)) continue;
            return ((BiomeAmbientSoundsHandler)$$0).getMoodiness();
        }
        return 0.0f;
    }

    private void sendPosition() {
        this.sendIsSprintingIfNeeded();
        boolean $$0 = this.isShiftKeyDown();
        if ($$0 != this.wasShiftKeyDown) {
            ServerboundPlayerCommandPacket.Action $$1 = $$0 ? ServerboundPlayerCommandPacket.Action.PRESS_SHIFT_KEY : ServerboundPlayerCommandPacket.Action.RELEASE_SHIFT_KEY;
            this.connection.send(new ServerboundPlayerCommandPacket(this, $$1));
            this.wasShiftKeyDown = $$0;
        }
        if (this.isControlledCamera()) {
            boolean $$8;
            double $$2 = this.getX() - this.xLast;
            double $$3 = this.getY() - this.yLast1;
            double $$4 = this.getZ() - this.zLast;
            double $$5 = this.getYRot() - this.yRotLast;
            double $$6 = this.getXRot() - this.xRotLast;
            ++this.positionReminder;
            boolean $$7 = Mth.lengthSquared($$2, $$3, $$4) > Mth.square(2.0E-4) || this.positionReminder >= 20;
            boolean bl = $$8 = $$5 != 0.0 || $$6 != 0.0;
            if (this.isPassenger()) {
                Vec3 $$9 = this.getDeltaMovement();
                this.connection.send(new ServerboundMovePlayerPacket.PosRot($$9.x, -999.0, $$9.z, this.getYRot(), this.getXRot(), this.onGround));
                $$7 = false;
            } else if ($$7 && $$8) {
                this.connection.send(new ServerboundMovePlayerPacket.PosRot(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot(), this.onGround));
            } else if ($$7) {
                this.connection.send(new ServerboundMovePlayerPacket.Pos(this.getX(), this.getY(), this.getZ(), this.onGround));
            } else if ($$8) {
                this.connection.send(new ServerboundMovePlayerPacket.Rot(this.getYRot(), this.getXRot(), this.onGround));
            } else if (this.lastOnGround != this.onGround) {
                this.connection.send(new ServerboundMovePlayerPacket.StatusOnly(this.onGround));
            }
            if ($$7) {
                this.xLast = this.getX();
                this.yLast1 = this.getY();
                this.zLast = this.getZ();
                this.positionReminder = 0;
            }
            if ($$8) {
                this.yRotLast = this.getYRot();
                this.xRotLast = this.getXRot();
            }
            this.lastOnGround = this.onGround;
            this.autoJumpEnabled = this.minecraft.options.autoJump().get();
        }
    }

    private void sendIsSprintingIfNeeded() {
        boolean $$0 = this.isSprinting();
        if ($$0 != this.wasSprinting) {
            ServerboundPlayerCommandPacket.Action $$1 = $$0 ? ServerboundPlayerCommandPacket.Action.START_SPRINTING : ServerboundPlayerCommandPacket.Action.STOP_SPRINTING;
            this.connection.send(new ServerboundPlayerCommandPacket(this, $$1));
            this.wasSprinting = $$0;
        }
    }

    public boolean drop(boolean $$0) {
        ServerboundPlayerActionPacket.Action $$1 = $$0 ? ServerboundPlayerActionPacket.Action.DROP_ALL_ITEMS : ServerboundPlayerActionPacket.Action.DROP_ITEM;
        ItemStack $$2 = this.getInventory().removeFromSelected($$0);
        this.connection.send(new ServerboundPlayerActionPacket($$1, BlockPos.ZERO, Direction.DOWN));
        return !$$2.isEmpty();
    }

    @Override
    public void swing(InteractionHand $$0) {
        super.swing($$0);
        this.connection.send(new ServerboundSwingPacket($$0));
    }

    @Override
    public void respawn() {
        this.connection.send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN));
    }

    @Override
    protected void actuallyHurt(DamageSource $$0, float $$1) {
        if (this.isInvulnerableTo($$0)) {
            return;
        }
        this.setHealth(this.getHealth() - $$1);
    }

    @Override
    public void closeContainer() {
        this.connection.send(new ServerboundContainerClosePacket(this.containerMenu.containerId));
        this.clientSideCloseContainer();
    }

    public void clientSideCloseContainer() {
        super.closeContainer();
        this.minecraft.setScreen(null);
    }

    public void hurtTo(float $$0) {
        if (this.flashOnSetHealth) {
            float $$1 = this.getHealth() - $$0;
            if ($$1 <= 0.0f) {
                this.setHealth($$0);
                if ($$1 < 0.0f) {
                    this.invulnerableTime = 10;
                }
            } else {
                this.lastHurt = $$1;
                this.invulnerableTime = 20;
                this.setHealth($$0);
                this.hurtTime = this.hurtDuration = 10;
            }
        } else {
            this.setHealth($$0);
            this.flashOnSetHealth = true;
        }
    }

    @Override
    public void onUpdateAbilities() {
        this.connection.send(new ServerboundPlayerAbilitiesPacket(this.getAbilities()));
    }

    @Override
    public boolean isLocalPlayer() {
        return true;
    }

    @Override
    public boolean isSuppressingSlidingDownLadder() {
        return !this.getAbilities().flying && super.isSuppressingSlidingDownLadder();
    }

    @Override
    public boolean canSpawnSprintParticle() {
        return !this.getAbilities().flying && super.canSpawnSprintParticle();
    }

    @Override
    public boolean canSpawnSoulSpeedParticle() {
        return !this.getAbilities().flying && super.canSpawnSoulSpeedParticle();
    }

    protected void sendRidingJump() {
        this.connection.send(new ServerboundPlayerCommandPacket(this, ServerboundPlayerCommandPacket.Action.START_RIDING_JUMP, Mth.floor(this.getJumpRidingScale() * 100.0f)));
    }

    public void sendOpenInventory() {
        this.connection.send(new ServerboundPlayerCommandPacket(this, ServerboundPlayerCommandPacket.Action.OPEN_INVENTORY));
    }

    public void setServerBrand(@Nullable String $$0) {
        this.serverBrand = $$0;
    }

    @Nullable
    public String getServerBrand() {
        return this.serverBrand;
    }

    public StatsCounter getStats() {
        return this.stats;
    }

    public ClientRecipeBook getRecipeBook() {
        return this.recipeBook;
    }

    public void removeRecipeHighlight(Recipe<?> $$0) {
        if (this.recipeBook.willHighlight($$0)) {
            this.recipeBook.removeHighlight($$0);
            this.connection.send(new ServerboundRecipeBookSeenRecipePacket($$0));
        }
    }

    @Override
    protected int getPermissionLevel() {
        return this.permissionLevel;
    }

    public void setPermissionLevel(int $$0) {
        this.permissionLevel = $$0;
    }

    @Override
    public void displayClientMessage(Component $$0, boolean $$1) {
        this.minecraft.getChatListener().handleSystemMessage($$0, $$1);
    }

    private void moveTowardsClosestSpace(double $$0, double $$1) {
        Direction[] $$7;
        BlockPos $$2 = new BlockPos($$0, this.getY(), $$1);
        if (!this.suffocatesAt($$2)) {
            return;
        }
        double $$3 = $$0 - (double)$$2.getX();
        double $$4 = $$1 - (double)$$2.getZ();
        Direction $$5 = null;
        double $$6 = Double.MAX_VALUE;
        for (Direction $$8 : $$7 = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH}) {
            double $$10;
            double $$9 = $$8.getAxis().choose($$3, 0.0, $$4);
            double d = $$10 = $$8.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.0 - $$9 : $$9;
            if (!($$10 < $$6) || this.suffocatesAt((BlockPos)$$2.relative($$8))) continue;
            $$6 = $$10;
            $$5 = $$8;
        }
        if ($$5 != null) {
            Vec3 $$11 = this.getDeltaMovement();
            if ($$5.getAxis() == Direction.Axis.X) {
                this.setDeltaMovement(0.1 * (double)$$5.getStepX(), $$11.y, $$11.z);
            } else {
                this.setDeltaMovement($$11.x, $$11.y, 0.1 * (double)$$5.getStepZ());
            }
        }
    }

    private boolean suffocatesAt(BlockPos $$0) {
        AABB $$1 = this.getBoundingBox();
        AABB $$2 = new AABB($$0.getX(), $$1.minY, $$0.getZ(), (double)$$0.getX() + 1.0, $$1.maxY, (double)$$0.getZ() + 1.0).deflate(1.0E-7);
        return this.level.collidesWithSuffocatingBlock(this, $$2);
    }

    public void setExperienceValues(float $$0, int $$1, int $$2) {
        this.experienceProgress = $$0;
        this.totalExperience = $$1;
        this.experienceLevel = $$2;
    }

    @Override
    public void sendSystemMessage(Component $$0) {
        this.minecraft.gui.getChat().addMessage($$0);
    }

    @Override
    public void handleEntityEvent(byte $$0) {
        if ($$0 >= 24 && $$0 <= 28) {
            this.setPermissionLevel($$0 - 24);
        } else {
            super.handleEntityEvent($$0);
        }
    }

    public void setShowDeathScreen(boolean $$0) {
        this.showDeathScreen = $$0;
    }

    public boolean shouldShowDeathScreen() {
        return this.showDeathScreen;
    }

    @Override
    public void playSound(SoundEvent $$0, float $$1, float $$2) {
        this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), $$0, this.getSoundSource(), $$1, $$2, false);
    }

    @Override
    public void playNotifySound(SoundEvent $$0, SoundSource $$1, float $$2, float $$3) {
        this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), $$0, $$1, $$2, $$3, false);
    }

    @Override
    public boolean isEffectiveAi() {
        return true;
    }

    @Override
    public void startUsingItem(InteractionHand $$0) {
        ItemStack $$1 = this.getItemInHand($$0);
        if ($$1.isEmpty() || this.isUsingItem()) {
            return;
        }
        super.startUsingItem($$0);
        this.startedUsingItem = true;
        this.usingItemHand = $$0;
    }

    @Override
    public boolean isUsingItem() {
        return this.startedUsingItem;
    }

    @Override
    public void stopUsingItem() {
        super.stopUsingItem();
        this.startedUsingItem = false;
    }

    @Override
    public InteractionHand getUsedItemHand() {
        return (InteractionHand)((Object)Objects.requireNonNullElse((Object)((Object)this.usingItemHand), (Object)((Object)InteractionHand.MAIN_HAND)));
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> $$0) {
        super.onSyncedDataUpdated($$0);
        if (DATA_LIVING_ENTITY_FLAGS.equals($$0)) {
            InteractionHand $$2;
            boolean $$1 = ((Byte)this.entityData.get(DATA_LIVING_ENTITY_FLAGS) & 1) > 0;
            InteractionHand interactionHand = $$2 = ((Byte)this.entityData.get(DATA_LIVING_ENTITY_FLAGS) & 2) > 0 ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
            if ($$1 && !this.startedUsingItem) {
                this.startUsingItem($$2);
            } else if (!$$1 && this.startedUsingItem) {
                this.stopUsingItem();
            }
        }
        if (DATA_SHARED_FLAGS_ID.equals($$0) && this.isFallFlying() && !this.wasFallFlying) {
            this.minecraft.getSoundManager().play(new ElytraOnPlayerSoundInstance(this));
        }
    }

    @Nullable
    public PlayerRideableJumping jumpableVehicle() {
        PlayerRideableJumping $$0;
        Entity entity = this.getVehicle();
        return entity instanceof PlayerRideableJumping && ($$0 = (PlayerRideableJumping)((Object)entity)).canJump(this) ? $$0 : null;
    }

    public float getJumpRidingScale() {
        return this.jumpRidingScale;
    }

    @Override
    public boolean isTextFilteringEnabled() {
        return this.minecraft.isTextFilteringEnabled();
    }

    @Override
    public void openTextEdit(SignBlockEntity $$0) {
        if ($$0 instanceof HangingSignBlockEntity) {
            HangingSignBlockEntity $$1 = (HangingSignBlockEntity)$$0;
            this.minecraft.setScreen(new HangingSignEditScreen($$1, this.minecraft.isTextFilteringEnabled()));
        } else {
            this.minecraft.setScreen(new SignEditScreen($$0, this.minecraft.isTextFilteringEnabled()));
        }
    }

    @Override
    public void openMinecartCommandBlock(BaseCommandBlock $$0) {
        this.minecraft.setScreen(new MinecartCommandBlockEditScreen($$0));
    }

    @Override
    public void openCommandBlock(CommandBlockEntity $$0) {
        this.minecraft.setScreen(new CommandBlockEditScreen($$0));
    }

    @Override
    public void openStructureBlock(StructureBlockEntity $$0) {
        this.minecraft.setScreen(new StructureBlockEditScreen($$0));
    }

    @Override
    public void openJigsawBlock(JigsawBlockEntity $$0) {
        this.minecraft.setScreen(new JigsawBlockEditScreen($$0));
    }

    @Override
    public void openItemGui(ItemStack $$0, InteractionHand $$1) {
        if ($$0.is(Items.WRITABLE_BOOK)) {
            this.minecraft.setScreen(new BookEditScreen(this, $$0, $$1));
        }
    }

    @Override
    public void crit(Entity $$0) {
        this.minecraft.particleEngine.createTrackingEmitter($$0, ParticleTypes.CRIT);
    }

    @Override
    public void magicCrit(Entity $$0) {
        this.minecraft.particleEngine.createTrackingEmitter($$0, ParticleTypes.ENCHANTED_HIT);
    }

    @Override
    public boolean isShiftKeyDown() {
        return this.input != null && this.input.shiftKeyDown;
    }

    @Override
    public boolean isCrouching() {
        return this.crouching;
    }

    public boolean isMovingSlowly() {
        return this.isCrouching() || this.isVisuallyCrawling();
    }

    @Override
    public void serverAiStep() {
        super.serverAiStep();
        if (this.isControlledCamera()) {
            this.xxa = this.input.leftImpulse;
            this.zza = this.input.forwardImpulse;
            this.jumping = this.input.jumping;
            this.yBobO = this.yBob;
            this.xBobO = this.xBob;
            this.xBob += (this.getXRot() - this.xBob) * 0.5f;
            this.yBob += (this.getYRot() - this.yBob) * 0.5f;
        }
    }

    protected boolean isControlledCamera() {
        return this.minecraft.getCameraEntity() == this;
    }

    public void resetPos() {
        this.setPose(Pose.STANDING);
        if (this.level != null) {
            for (double $$0 = this.getY(); $$0 > (double)this.level.getMinBuildHeight() && $$0 < (double)this.level.getMaxBuildHeight(); $$0 += 1.0) {
                this.setPos(this.getX(), $$0, this.getZ());
                if (this.level.noCollision(this)) break;
            }
            this.setDeltaMovement(Vec3.ZERO);
            this.setXRot(0.0f);
        }
        this.setHealth(this.getMaxHealth());
        this.deathTime = 0;
    }

    @Override
    public void aiStep() {
        PlayerRideableJumping $$14;
        ItemStack $$11;
        boolean $$7;
        if (this.sprintTriggerTime > 0) {
            --this.sprintTriggerTime;
        }
        this.handleNetherPortalClient();
        boolean $$0 = this.input.jumping;
        boolean $$1 = this.input.shiftKeyDown;
        boolean $$2 = this.hasEnoughImpulseToStartSprinting();
        this.crouching = !this.getAbilities().flying && !this.isSwimming() && this.canEnterPose(Pose.CROUCHING) && (this.isShiftKeyDown() || !this.isSleeping() && !this.canEnterPose(Pose.STANDING));
        float $$3 = Mth.clamp(0.3f + EnchantmentHelper.getSneakingSpeedBonus(this), 0.0f, 1.0f);
        this.input.tick(this.isMovingSlowly(), $$3);
        this.minecraft.getTutorial().onInput(this.input);
        if (this.isUsingItem() && !this.isPassenger()) {
            this.input.leftImpulse *= 0.2f;
            this.input.forwardImpulse *= 0.2f;
            this.sprintTriggerTime = 0;
        }
        boolean $$4 = false;
        if (this.autoJumpTime > 0) {
            --this.autoJumpTime;
            $$4 = true;
            this.input.jumping = true;
        }
        if (!this.noPhysics) {
            this.moveTowardsClosestSpace(this.getX() - (double)this.getBbWidth() * 0.35, this.getZ() + (double)this.getBbWidth() * 0.35);
            this.moveTowardsClosestSpace(this.getX() - (double)this.getBbWidth() * 0.35, this.getZ() - (double)this.getBbWidth() * 0.35);
            this.moveTowardsClosestSpace(this.getX() + (double)this.getBbWidth() * 0.35, this.getZ() - (double)this.getBbWidth() * 0.35);
            this.moveTowardsClosestSpace(this.getX() + (double)this.getBbWidth() * 0.35, this.getZ() + (double)this.getBbWidth() * 0.35);
        }
        if ($$1) {
            this.sprintTriggerTime = 0;
        }
        boolean $$5 = this.canStartSprinting();
        boolean $$6 = this.isPassenger() ? this.getVehicle().isOnGround() : this.onGround;
        boolean bl = $$7 = !$$1 && !$$2;
        if (($$6 || this.isUnderWater()) && $$7 && $$5) {
            if (this.sprintTriggerTime > 0 || this.minecraft.options.keySprint.isDown()) {
                this.setSprinting(true);
            } else {
                this.sprintTriggerTime = 7;
            }
        }
        if ((!this.isInWater() || this.isUnderWater()) && $$5 && this.minecraft.options.keySprint.isDown()) {
            this.setSprinting(true);
        }
        if (this.isSprinting()) {
            boolean $$9;
            boolean $$8 = !this.input.hasForwardImpulse() || !this.hasEnoughFoodToStartSprinting();
            boolean bl2 = $$9 = $$8 || this.horizontalCollision && !this.minorHorizontalCollision || this.isInWater() && !this.isUnderWater();
            if (this.isSwimming()) {
                if (!this.onGround && !this.input.shiftKeyDown && $$8 || !this.isInWater()) {
                    this.setSprinting(false);
                }
            } else if ($$9) {
                this.setSprinting(false);
            }
        }
        boolean $$10 = false;
        if (this.getAbilities().mayfly) {
            if (this.minecraft.gameMode.isAlwaysFlying()) {
                if (!this.getAbilities().flying) {
                    this.getAbilities().flying = true;
                    $$10 = true;
                    this.onUpdateAbilities();
                }
            } else if (!$$0 && this.input.jumping && !$$4) {
                if (this.jumpTriggerTime == 0) {
                    this.jumpTriggerTime = 7;
                } else if (!this.isSwimming()) {
                    this.getAbilities().flying = !this.getAbilities().flying;
                    $$10 = true;
                    this.onUpdateAbilities();
                    this.jumpTriggerTime = 0;
                }
            }
        }
        if (this.input.jumping && !$$10 && !$$0 && !this.getAbilities().flying && !this.isPassenger() && !this.onClimbable() && ($$11 = this.getItemBySlot(EquipmentSlot.CHEST)).is(Items.ELYTRA) && ElytraItem.isFlyEnabled($$11) && this.tryToStartFallFlying()) {
            this.connection.send(new ServerboundPlayerCommandPacket(this, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
        }
        this.wasFallFlying = this.isFallFlying();
        if (this.isInWater() && this.input.shiftKeyDown && this.isAffectedByFluids()) {
            this.goDownInWater();
        }
        if (this.isEyeInFluid(FluidTags.WATER)) {
            int $$12 = this.isSpectator() ? 10 : 1;
            this.waterVisionTime = Mth.clamp(this.waterVisionTime + $$12, 0, 600);
        } else if (this.waterVisionTime > 0) {
            this.isEyeInFluid(FluidTags.WATER);
            this.waterVisionTime = Mth.clamp(this.waterVisionTime - 10, 0, 600);
        }
        if (this.getAbilities().flying && this.isControlledCamera()) {
            int $$13 = 0;
            if (this.input.shiftKeyDown) {
                --$$13;
            }
            if (this.input.jumping) {
                ++$$13;
            }
            if ($$13 != 0) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, (float)$$13 * this.getAbilities().getFlyingSpeed() * 3.0f, 0.0));
            }
        }
        if (($$14 = this.jumpableVehicle()) != null && $$14.getJumpCooldown() == 0) {
            if (this.jumpRidingTicks < 0) {
                ++this.jumpRidingTicks;
                if (this.jumpRidingTicks == 0) {
                    this.jumpRidingScale = 0.0f;
                }
            }
            if ($$0 && !this.input.jumping) {
                this.jumpRidingTicks = -10;
                $$14.onPlayerJump(Mth.floor(this.getJumpRidingScale() * 100.0f));
                this.sendRidingJump();
            } else if (!$$0 && this.input.jumping) {
                this.jumpRidingTicks = 0;
                this.jumpRidingScale = 0.0f;
            } else if ($$0) {
                ++this.jumpRidingTicks;
                this.jumpRidingScale = this.jumpRidingTicks < 10 ? (float)this.jumpRidingTicks * 0.1f : 0.8f + 2.0f / (float)(this.jumpRidingTicks - 9) * 0.1f;
            }
        } else {
            this.jumpRidingScale = 0.0f;
        }
        super.aiStep();
        if (this.onGround && this.getAbilities().flying && !this.minecraft.gameMode.isAlwaysFlying()) {
            this.getAbilities().flying = false;
            this.onUpdateAbilities();
        }
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 20) {
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    private void handleNetherPortalClient() {
        this.oPortalTime = this.portalTime;
        if (this.isInsidePortal) {
            if (!(this.minecraft.screen == null || this.minecraft.screen.isPauseScreen() || this.minecraft.screen instanceof DeathScreen || this.minecraft.screen instanceof ReceivingLevelScreen)) {
                if (this.minecraft.screen instanceof AbstractContainerScreen) {
                    this.closeContainer();
                }
                this.minecraft.setScreen(null);
            }
            if (this.portalTime == 0.0f) {
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forLocalAmbience(SoundEvents.PORTAL_TRIGGER, this.random.nextFloat() * 0.4f + 0.8f, 0.25f));
            }
            this.portalTime += 0.0125f;
            if (this.portalTime >= 1.0f) {
                this.portalTime = 1.0f;
            }
            this.isInsidePortal = false;
        } else if (this.hasEffect(MobEffects.CONFUSION) && this.getEffect(MobEffects.CONFUSION).getDuration() > 60) {
            this.portalTime += 0.006666667f;
            if (this.portalTime > 1.0f) {
                this.portalTime = 1.0f;
            }
        } else {
            if (this.portalTime > 0.0f) {
                this.portalTime -= 0.05f;
            }
            if (this.portalTime < 0.0f) {
                this.portalTime = 0.0f;
            }
        }
        this.processPortalCooldown();
    }

    @Override
    public void rideTick() {
        super.rideTick();
        this.handsBusy = false;
        if (this.getVehicle() instanceof Boat) {
            Boat $$0 = (Boat)this.getVehicle();
            $$0.setInput(this.input.left, this.input.right, this.input.up, this.input.down);
            this.handsBusy |= this.input.left || this.input.right || this.input.up || this.input.down;
        }
    }

    public boolean isHandsBusy() {
        return this.handsBusy;
    }

    @Override
    @Nullable
    public MobEffectInstance removeEffectNoUpdate(@Nullable MobEffect $$0) {
        if ($$0 == MobEffects.CONFUSION) {
            this.oPortalTime = 0.0f;
            this.portalTime = 0.0f;
        }
        return super.removeEffectNoUpdate($$0);
    }

    @Override
    public void move(MoverType $$0, Vec3 $$1) {
        double $$2 = this.getX();
        double $$3 = this.getZ();
        super.move($$0, $$1);
        this.updateAutoJump((float)(this.getX() - $$2), (float)(this.getZ() - $$3));
    }

    public boolean isAutoJumpEnabled() {
        return this.autoJumpEnabled;
    }

    protected void updateAutoJump(float $$02, float $$1) {
        if (!this.canAutoJump()) {
            return;
        }
        Vec3 $$2 = this.position();
        Vec3 $$3 = $$2.add($$02, 0.0, $$1);
        Vec3 $$4 = new Vec3($$02, 0.0, $$1);
        float $$5 = this.getSpeed();
        float $$6 = (float)$$4.lengthSqr();
        if ($$6 <= 0.001f) {
            Vec2 $$7 = this.input.getMoveVector();
            float $$8 = $$5 * $$7.x;
            float $$9 = $$5 * $$7.y;
            float $$10 = Mth.sin(this.getYRot() * ((float)Math.PI / 180));
            float $$11 = Mth.cos(this.getYRot() * ((float)Math.PI / 180));
            $$4 = new Vec3($$8 * $$11 - $$9 * $$10, $$4.y, $$9 * $$11 + $$8 * $$10);
            $$6 = (float)$$4.lengthSqr();
            if ($$6 <= 0.001f) {
                return;
            }
        }
        float $$12 = Mth.invSqrt($$6);
        Vec3 $$13 = $$4.scale($$12);
        Vec3 $$14 = this.getForward();
        float $$15 = (float)($$14.x * $$13.x + $$14.z * $$13.z);
        if ($$15 < -0.15f) {
            return;
        }
        CollisionContext $$16 = CollisionContext.of(this);
        Vec3i $$17 = new BlockPos(this.getX(), this.getBoundingBox().maxY, this.getZ());
        BlockState $$18 = this.level.getBlockState((BlockPos)$$17);
        if (!$$18.getCollisionShape(this.level, (BlockPos)$$17, $$16).isEmpty()) {
            return;
        }
        BlockState $$19 = this.level.getBlockState((BlockPos)($$17 = ((BlockPos)$$17).above()));
        if (!$$19.getCollisionShape(this.level, (BlockPos)$$17, $$16).isEmpty()) {
            return;
        }
        float $$20 = 7.0f;
        float $$21 = 1.2f;
        if (this.hasEffect(MobEffects.JUMP)) {
            $$21 += (float)(this.getEffect(MobEffects.JUMP).getAmplifier() + 1) * 0.75f;
        }
        float $$22 = Math.max((float)($$5 * 7.0f), (float)(1.0f / $$12));
        Vec3 $$23 = $$2;
        Vec3 $$24 = $$3.add($$13.scale($$22));
        float $$25 = this.getBbWidth();
        float $$26 = this.getBbHeight();
        AABB $$27 = new AABB($$23, $$24.add(0.0, $$26, 0.0)).inflate($$25, 0.0, $$25);
        $$23 = $$23.add(0.0, 0.51f, 0.0);
        $$24 = $$24.add(0.0, 0.51f, 0.0);
        Vec3 $$28 = $$13.cross(new Vec3(0.0, 1.0, 0.0));
        Vec3 $$29 = $$28.scale($$25 * 0.5f);
        Vec3 $$30 = $$23.subtract($$29);
        Vec3 $$31 = $$24.subtract($$29);
        Vec3 $$32 = $$23.add($$29);
        Vec3 $$33 = $$24.add($$29);
        Iterable $$34 = this.level.getCollisions(this, $$27);
        Iterator $$35 = StreamSupport.stream((Spliterator)$$34.spliterator(), (boolean)false).flatMap($$0 -> $$0.toAabbs().stream()).iterator();
        float $$36 = Float.MIN_VALUE;
        while ($$35.hasNext()) {
            AABB $$37 = (AABB)$$35.next();
            if (!$$37.intersects($$30, $$31) && !$$37.intersects($$32, $$33)) continue;
            $$36 = (float)$$37.maxY;
            Vec3 $$38 = $$37.getCenter();
            BlockPos $$39 = new BlockPos($$38);
            int $$40 = 1;
            while ((float)$$40 < $$21) {
                BlockState $$44;
                Vec3i $$41 = $$39.above($$40);
                BlockState $$42 = this.level.getBlockState((BlockPos)$$41);
                VoxelShape $$43 = $$42.getCollisionShape(this.level, (BlockPos)$$41, $$16);
                if (!$$43.isEmpty() && (double)($$36 = (float)$$43.max(Direction.Axis.Y) + (float)$$41.getY()) - this.getY() > (double)$$21) {
                    return;
                }
                if ($$40 > 1 && !($$44 = this.level.getBlockState((BlockPos)($$17 = ((BlockPos)$$17).above()))).getCollisionShape(this.level, (BlockPos)$$17, $$16).isEmpty()) {
                    return;
                }
                ++$$40;
            }
            break block0;
        }
        if ($$36 == Float.MIN_VALUE) {
            return;
        }
        float $$45 = (float)((double)$$36 - this.getY());
        if ($$45 <= 0.5f || $$45 > $$21) {
            return;
        }
        this.autoJumpTime = 1;
    }

    @Override
    protected boolean isHorizontalCollisionMinor(Vec3 $$0) {
        float $$1 = this.getYRot() * ((float)Math.PI / 180);
        double $$2 = Mth.sin($$1);
        double $$3 = Mth.cos($$1);
        double $$4 = (double)this.xxa * $$3 - (double)this.zza * $$2;
        double $$5 = (double)this.zza * $$3 + (double)this.xxa * $$2;
        double $$6 = Mth.square($$4) + Mth.square($$5);
        double $$7 = Mth.square($$0.x) + Mth.square($$0.z);
        if ($$6 < (double)1.0E-5f || $$7 < (double)1.0E-5f) {
            return false;
        }
        double $$8 = $$4 * $$0.x + $$5 * $$0.z;
        double $$9 = Math.acos((double)($$8 / Math.sqrt((double)($$6 * $$7))));
        return $$9 < 0.13962633907794952;
    }

    private boolean canAutoJump() {
        return this.isAutoJumpEnabled() && this.autoJumpTime <= 0 && this.onGround && !this.isStayingOnGroundSurface() && !this.isPassenger() && this.isMoving() && (double)this.getBlockJumpFactor() >= 1.0;
    }

    private boolean isMoving() {
        Vec2 $$0 = this.input.getMoveVector();
        return $$0.x != 0.0f || $$0.y != 0.0f;
    }

    private boolean canStartSprinting() {
        return !this.isSprinting() && this.hasEnoughImpulseToStartSprinting() && this.hasEnoughFoodToStartSprinting() && !this.isUsingItem() && !this.hasEffect(MobEffects.BLINDNESS) && (!this.isPassenger() || this.vehicleCanSprint(this.getVehicle())) && !this.isFallFlying();
    }

    private boolean vehicleCanSprint(Entity $$0) {
        return $$0.canSprint() && $$0.isControlledByLocalInstance();
    }

    private boolean hasEnoughImpulseToStartSprinting() {
        double $$0 = 0.8;
        return this.isUnderWater() ? this.input.hasForwardImpulse() : (double)this.input.forwardImpulse >= 0.8;
    }

    private boolean hasEnoughFoodToStartSprinting() {
        return this.isPassenger() || (float)this.getFoodData().getFoodLevel() > 6.0f || this.getAbilities().mayfly;
    }

    public float getWaterVision() {
        if (!this.isEyeInFluid(FluidTags.WATER)) {
            return 0.0f;
        }
        float $$0 = 600.0f;
        float $$1 = 100.0f;
        if ((float)this.waterVisionTime >= 600.0f) {
            return 1.0f;
        }
        float $$2 = Mth.clamp((float)this.waterVisionTime / 100.0f, 0.0f, 1.0f);
        float $$3 = (float)this.waterVisionTime < 100.0f ? 0.0f : Mth.clamp(((float)this.waterVisionTime - 100.0f) / 500.0f, 0.0f, 1.0f);
        return $$2 * 0.6f + $$3 * 0.39999998f;
    }

    @Override
    public boolean isUnderWater() {
        return this.wasUnderwater;
    }

    @Override
    protected boolean updateIsUnderwater() {
        boolean $$0 = this.wasUnderwater;
        boolean $$1 = super.updateIsUnderwater();
        if (this.isSpectator()) {
            return this.wasUnderwater;
        }
        if (!$$0 && $$1) {
            this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.AMBIENT_UNDERWATER_ENTER, SoundSource.AMBIENT, 1.0f, 1.0f, false);
            this.minecraft.getSoundManager().play(new UnderwaterAmbientSoundInstances.UnderwaterAmbientSoundInstance(this));
        }
        if ($$0 && !$$1) {
            this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.AMBIENT_UNDERWATER_EXIT, SoundSource.AMBIENT, 1.0f, 1.0f, false);
        }
        return this.wasUnderwater;
    }

    @Override
    public Vec3 getRopeHoldPosition(float $$0) {
        if (this.minecraft.options.getCameraType().isFirstPerson()) {
            float $$1 = Mth.lerp($$0 * 0.5f, this.getYRot(), this.yRotO) * ((float)Math.PI / 180);
            float $$2 = Mth.lerp($$0 * 0.5f, this.getXRot(), this.xRotO) * ((float)Math.PI / 180);
            double $$3 = this.getMainArm() == HumanoidArm.RIGHT ? -1.0 : 1.0;
            Vec3 $$4 = new Vec3(0.39 * $$3, -0.6, 0.3);
            return $$4.xRot(-$$2).yRot(-$$1).add(this.getEyePosition($$0));
        }
        return super.getRopeHoldPosition($$0);
    }

    @Override
    public void updateTutorialInventoryAction(ItemStack $$0, ItemStack $$1, ClickAction $$2) {
        this.minecraft.getTutorial().onInventoryAction($$0, $$1, $$2);
    }

    @Override
    public float getVisualRotationYInDegrees() {
        return this.getYRot();
    }
}