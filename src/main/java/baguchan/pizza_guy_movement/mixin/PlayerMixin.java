package baguchan.pizza_guy_movement.mixin;

import baguchan.pizza_guy_movement.IShadow;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import java.util.List;
import java.util.UUID;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements IShadow {
    private static final UUID SPEED_MODIFIER_BOOST_UUID = UUID.fromString("a4be9598-fd19-8c8b-7e3d-142defd78b7c");
    public Vec3 prevShadow = Vec3.ZERO;

    public Vec3 shadow = Vec3.ZERO;

    public Vec3 prevShadow2 = Vec3.ZERO;

    public Vec3 shadow2 = Vec3.ZERO;

    public Vec2 shadowRot = Vec2.ZERO;
    public Vec2 shadowRot2 = Vec2.ZERO;
    public Vec2 prevShadowRot = Vec2.ZERO;
    public Vec2 prevShadowRot2 = Vec2.ZERO;

    private float percentBoost = 0.0F;

    protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    public void aiStep() {
        if (!this.level.isClientSide) {
            removeDashBoost(this);
        }
        if (this.level.isClientSide) {

            double elasticity = 0.25D;
            this.prevShadow = this.shadow;
            this.prevShadow2 = this.shadow2;
            this.prevShadowRot = this.shadowRot;
            this.prevShadowRot2 = this.shadowRot2;
            this.shadowRot = new Vec2((float) (this.getXRot() + (this.shadowRot.x - this.getXRot()) * elasticity * 0.75D), (float) (this.yBodyRot + (this.shadowRot.y - this.yBodyRot) * elasticity * 0.75D));
            this.shadowRot2 = new Vec2((float) (this.shadowRot.x + (this.shadowRot2.x - this.shadowRot.x) * elasticity * 0.3499999940395355D), (float) (this.shadowRot.y + (this.shadowRot2.y - this.shadowRot.y) * elasticity * 0.3499999940395355D));
            float shadowX = (float) (this.shadow.x + (this.getX() - this.shadow.x) * elasticity);
            float shadowY = (float) (this.shadow.y + (this.getY() - this.shadow.y) * elasticity);
            float shadowZ = (float) (this.shadow.z + (this.getZ() - this.shadow.z) * elasticity);
            float shadowX2 = (float) (this.shadow2.x + (this.shadow.x - this.shadow2.x) * elasticity * 0.375D);
            float shadowY2 = (float) (this.shadow2.y + (this.shadow.y - this.shadow2.y) * elasticity * 0.375D);
            float shadowZ2 = (float) (this.shadow2.z + (this.shadow.z - this.shadow2.z) * elasticity * 0.375D);
            this.shadow = new Vec3(shadowX, shadowY, shadowZ);
            this.shadow2 = new Vec3(shadowX2, shadowY2, shadowZ2);
        }

        if (percentBoost >= 0.5F) {
            pushEntitiesWhenDashed(this);
        }
        tryAddDashBooster(this);
    }

    public float getPercentBoost() {
        return percentBoost;
    }

    public AABB getAttackBoundingBox() {
        Vec3 vec3d = this.getViewVector(1.0F);

        Vec3 vec3 = new Vec3(this.getX() - (double) (this.getBbWidth() * 0.5D), this.getY(), this.getZ() - (double) (this.getBbWidth() * 0.5D));
        Vec3 vec31 = new Vec3(this.getX() + (double) (this.getBbWidth() * 0.5D), this.getY() + this.getBbHeight(), this.getZ() + (double) (this.getBbWidth() * 0.5D));
        return new AABB(vec3, vec31).move(vec3d.x * 1.5D, vec3d.y * 1.5D, vec3d.z * 1.5D);
    }

    protected void pushEntitiesWhenDashed(LivingEntity entity) {
        if (!entity.level.isClientSide()) {
            List<LivingEntity> list = entity.level.getEntities(EntityTypeTest.forClass(LivingEntity.class), getAttackBoundingBox(), EntitySelector.pushableBy(entity));
            if (!list.isEmpty()) {
                for (int l = 0; l < list.size(); ++l) {
                    LivingEntity entity2 = list.get(l);
                    if (entity != entity2 && !entity.isAlliedTo(entity2)) {
                        entity2.knockback(2.0D * percentBoost, entity2.getX() - entity.getX(), entity2.getZ() - entity.getZ());
                        entity2.hurt(DamageSource.mobAttack(entity), Mth.floor(8.0F * percentBoost));
                    }
                }
            }
        }
    }

    protected void removeDashBoost(LivingEntity entity) {
        AttributeInstance attributeinstance = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attributeinstance != null) {
            if (attributeinstance.getModifier(SPEED_MODIFIER_BOOST_UUID) != null) {
                attributeinstance.removeModifier(SPEED_MODIFIER_BOOST_UUID);
            }

        }
    }

    protected void tryAddDashBooster(LivingEntity entity) {
        if (entity.isSprinting()) {
            if (percentBoost <= 1) {
                percentBoost += 0.01F;
            } else {
                percentBoost = 1;
            }

        } else {
            if (percentBoost >= 0) {
                percentBoost -= 0.1F;
            } else {
                percentBoost = 0;
            }
        }
        if (percentBoost > 0) {
            if (!entity.level.isClientSide) {
                AttributeInstance attributeinstance = entity.getAttribute(Attributes.MOVEMENT_SPEED);
                if (attributeinstance == null) {
                    return;
                }

                float f = 0.125F * percentBoost;
                attributeinstance.addTransientModifier(new AttributeModifier(SPEED_MODIFIER_BOOST_UUID, "Spark Boost", (double) f, AttributeModifier.Operation.ADDITION));
            }
        }
    }

    public Vec2 getShadowRot() {
        return shadowRot;
    }

    public Vec2 getShadowRot2() {
        return shadowRot2;
    }

    public Vec2 getPrevShadowRot() {
        return prevShadowRot;
    }

    public Vec2 getPrevShadowRot2() {
        return prevShadowRot2;
    }

    public Vec3 getShadow() {
        return shadow;
    }

    public Vec3 getShadow2() {
        return shadow2;
    }

    public Vec3 getPrevShadow() {
        return prevShadow;
    }

    public Vec3 getPrevShadow2() {
        return prevShadow2;
    }

    @Override
    public void setPos(double p_20210_, double p_20211_, double p_20212_) {
        super.setPos(p_20210_, p_20211_, p_20212_);
        if (this.shadow.distanceTo(this.position()) >= 30) {
            this.shadow = new Vec3(p_20210_, p_20211_, p_20212_);
            this.shadow2 = new Vec3(p_20210_, p_20211_, p_20212_);
        }
    }

    @Override
    protected void setRot(float p_19916_, float p_19917_) {
        super.setRot(p_19916_, p_19917_);
    }

    @Override
    public boolean hurt(DamageSource p_21016_, float p_21017_) {
        return super.hurt(p_21016_, p_21017_);
    }
}
