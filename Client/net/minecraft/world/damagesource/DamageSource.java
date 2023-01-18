/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  javax.annotation.Nullable
 */
package net.minecraft.world.damagesource;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.BadRespawnPointDamage;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;

public class DamageSource {
    public static final DamageSource IN_FIRE = new DamageSource("inFire").bypassArmor().setIsFire();
    public static final DamageSource LIGHTNING_BOLT = new DamageSource("lightningBolt");
    public static final DamageSource ON_FIRE = new DamageSource("onFire").bypassArmor().setIsFire();
    public static final DamageSource LAVA = new DamageSource("lava").setIsFire();
    public static final DamageSource HOT_FLOOR = new DamageSource("hotFloor").setIsFire();
    public static final DamageSource IN_WALL = new DamageSource("inWall").bypassArmor();
    public static final DamageSource CRAMMING = new DamageSource("cramming").bypassArmor();
    public static final DamageSource DROWN = new DamageSource("drown").bypassArmor();
    public static final DamageSource STARVE = new DamageSource("starve").bypassArmor().bypassMagic();
    public static final DamageSource CACTUS = new DamageSource("cactus");
    public static final DamageSource FALL = new DamageSource("fall").bypassArmor().setIsFall();
    public static final DamageSource FLY_INTO_WALL = new DamageSource("flyIntoWall").bypassArmor();
    public static final DamageSource OUT_OF_WORLD = new DamageSource("outOfWorld").bypassArmor().bypassInvul();
    public static final DamageSource GENERIC = new DamageSource("generic").bypassArmor();
    public static final DamageSource MAGIC = new DamageSource("magic").bypassArmor().setMagic();
    public static final DamageSource WITHER = new DamageSource("wither").bypassArmor();
    public static final DamageSource DRAGON_BREATH = new DamageSource("dragonBreath").bypassArmor();
    public static final DamageSource DRY_OUT = new DamageSource("dryout");
    public static final DamageSource SWEET_BERRY_BUSH = new DamageSource("sweetBerryBush");
    public static final DamageSource FREEZE = new DamageSource("freeze").bypassArmor();
    public static final DamageSource STALAGMITE = new DamageSource("stalagmite").bypassArmor().setIsFall();
    private boolean damageHelmet;
    private boolean bypassArmor;
    private boolean bypassInvul;
    private boolean bypassMagic;
    private boolean bypassEnchantments;
    private float exhaustion = 0.1f;
    private boolean isFireSource;
    private boolean isProjectile;
    private boolean scalesWithDifficulty;
    private boolean isMagic;
    private boolean isExplosion;
    private boolean isFall;
    private boolean noAggro;
    public final String msgId;

    public static DamageSource fallingBlock(Entity $$0) {
        return new EntityDamageSource("fallingBlock", $$0).damageHelmet();
    }

    public static DamageSource anvil(Entity $$0) {
        return new EntityDamageSource("anvil", $$0).damageHelmet();
    }

    public static DamageSource fallingStalactite(Entity $$0) {
        return new EntityDamageSource("fallingStalactite", $$0).damageHelmet();
    }

    public static DamageSource sting(LivingEntity $$0) {
        return new EntityDamageSource("sting", $$0);
    }

    public static DamageSource mobAttack(LivingEntity $$0) {
        return new EntityDamageSource("mob", $$0);
    }

    public static DamageSource indirectMobAttack(Entity $$0, @Nullable LivingEntity $$1) {
        return new IndirectEntityDamageSource("mob", $$0, $$1);
    }

    public static DamageSource playerAttack(Player $$0) {
        return new EntityDamageSource("player", $$0);
    }

    public static DamageSource arrow(AbstractArrow $$0, @Nullable Entity $$1) {
        return new IndirectEntityDamageSource("arrow", $$0, $$1).setProjectile();
    }

    public static DamageSource trident(Entity $$0, @Nullable Entity $$1) {
        return new IndirectEntityDamageSource("trident", $$0, $$1).setProjectile();
    }

    public static DamageSource fireworks(FireworkRocketEntity $$0, @Nullable Entity $$1) {
        return new IndirectEntityDamageSource("fireworks", $$0, $$1).setExplosion();
    }

    public static DamageSource fireball(Fireball $$0, @Nullable Entity $$1) {
        if ($$1 == null) {
            return new IndirectEntityDamageSource("onFire", $$0, $$0).setIsFire().setProjectile();
        }
        return new IndirectEntityDamageSource("fireball", $$0, $$1).setIsFire().setProjectile();
    }

    public static DamageSource witherSkull(WitherSkull $$0, Entity $$1) {
        return new IndirectEntityDamageSource("witherSkull", $$0, $$1).setProjectile();
    }

    public static DamageSource thrown(Entity $$0, @Nullable Entity $$1) {
        return new IndirectEntityDamageSource("thrown", $$0, $$1).setProjectile();
    }

    public static DamageSource indirectMagic(Entity $$0, @Nullable Entity $$1) {
        return new IndirectEntityDamageSource("indirectMagic", $$0, $$1).bypassArmor().setMagic();
    }

    public static DamageSource thorns(Entity $$0) {
        return new EntityDamageSource("thorns", $$0).setThorns().setMagic();
    }

    public static DamageSource explosion(@Nullable Explosion $$0) {
        return $$0 != null ? DamageSource.explosion($$0.getDirectSourceEntity(), $$0.getIndirectSourceEntity()) : DamageSource.explosion(null, null);
    }

    public static DamageSource explosion(@Nullable Entity $$0, @Nullable Entity $$1) {
        if ($$1 != null && $$0 != null) {
            return new IndirectEntityDamageSource("explosion.player", $$0, $$1).setScalesWithDifficulty().setExplosion();
        }
        if ($$0 != null) {
            return new EntityDamageSource("explosion", $$0).setScalesWithDifficulty().setExplosion();
        }
        return new DamageSource("explosion").setScalesWithDifficulty().setExplosion();
    }

    public static DamageSource sonicBoom(Entity $$0) {
        return new EntityDamageSource("sonic_boom", $$0).bypassArmor().bypassEnchantments().setMagic();
    }

    public static DamageSource badRespawnPointExplosion(Vec3 $$0) {
        return new BadRespawnPointDamage($$0);
    }

    public String toString() {
        return "DamageSource (" + this.msgId + ")";
    }

    public boolean isProjectile() {
        return this.isProjectile;
    }

    public DamageSource setProjectile() {
        this.isProjectile = true;
        return this;
    }

    public boolean isExplosion() {
        return this.isExplosion;
    }

    public DamageSource setExplosion() {
        this.isExplosion = true;
        return this;
    }

    public boolean isBypassArmor() {
        return this.bypassArmor;
    }

    public boolean isDamageHelmet() {
        return this.damageHelmet;
    }

    public float getFoodExhaustion() {
        return this.exhaustion;
    }

    public boolean isBypassInvul() {
        return this.bypassInvul;
    }

    public boolean isBypassMagic() {
        return this.bypassMagic;
    }

    public boolean isBypassEnchantments() {
        return this.bypassEnchantments;
    }

    protected DamageSource(String $$0) {
        this.msgId = $$0;
    }

    @Nullable
    public Entity getDirectEntity() {
        return this.getEntity();
    }

    @Nullable
    public Entity getEntity() {
        return null;
    }

    protected DamageSource bypassArmor() {
        this.bypassArmor = true;
        this.exhaustion = 0.0f;
        return this;
    }

    protected DamageSource damageHelmet() {
        this.damageHelmet = true;
        return this;
    }

    protected DamageSource bypassInvul() {
        this.bypassInvul = true;
        return this;
    }

    protected DamageSource bypassMagic() {
        this.bypassMagic = true;
        this.exhaustion = 0.0f;
        return this;
    }

    protected DamageSource bypassEnchantments() {
        this.bypassEnchantments = true;
        return this;
    }

    protected DamageSource setIsFire() {
        this.isFireSource = true;
        return this;
    }

    public DamageSource setNoAggro() {
        this.noAggro = true;
        return this;
    }

    public Component getLocalizedDeathMessage(LivingEntity $$0) {
        LivingEntity $$1 = $$0.getKillCredit();
        String $$2 = "death.attack." + this.msgId;
        String $$3 = $$2 + ".player";
        if ($$1 != null) {
            return Component.translatable($$3, $$0.getDisplayName(), $$1.getDisplayName());
        }
        return Component.translatable($$2, $$0.getDisplayName());
    }

    public boolean isFire() {
        return this.isFireSource;
    }

    public boolean isNoAggro() {
        return this.noAggro;
    }

    public String getMsgId() {
        return this.msgId;
    }

    public DamageSource setScalesWithDifficulty() {
        this.scalesWithDifficulty = true;
        return this;
    }

    public boolean scalesWithDifficulty() {
        return this.scalesWithDifficulty;
    }

    public boolean isMagic() {
        return this.isMagic;
    }

    public DamageSource setMagic() {
        this.isMagic = true;
        return this;
    }

    public boolean isFall() {
        return this.isFall;
    }

    public DamageSource setIsFall() {
        this.isFall = true;
        return this;
    }

    public boolean isCreativePlayer() {
        Entity $$0 = this.getEntity();
        return $$0 instanceof Player && ((Player)$$0).getAbilities().instabuild;
    }

    @Nullable
    public Vec3 getSourcePosition() {
        return null;
    }
}