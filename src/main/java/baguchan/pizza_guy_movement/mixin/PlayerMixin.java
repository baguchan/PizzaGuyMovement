package baguchan.pizza_guy_movement.mixin;

import baguchan.pizza_guy_movement.IShadow;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.UUID;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements IShadow {
    private static final UUID SPEED_MODIFIER_BOOST_UUID = UUID.fromString("4e73365d-036e-d940-a1dd-e7767c2e7e55");
    public Vec3 prevShadow = Vec3.ZERO;

    public Vec3 shadow = Vec3.ZERO;

    public Vec3 prevShadow2 = Vec3.ZERO;

    public Vec3 shadow2 = Vec3.ZERO;

    public Vec2 shadowRot = Vec2.ZERO;
    public Vec2 shadowRot2 = Vec2.ZERO;
    public Vec2 prevShadowRot = Vec2.ZERO;
    public Vec2 prevShadowRot2 = Vec2.ZERO;

    private float percentBoost = 0.0F;

    @Shadow
    @Final
    private Abilities abilities;

    protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Inject(method = "aiStep", at = @At("TAIL"))
    public void aiStep(CallbackInfo callbackInfo) {
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


    // climing stuff like peppino
    @Override
    public boolean onClimbable() {
        return super.onClimbable() || this.percentBoost > 0.2F && this.horizontalCollision;
    }

    private Vec3 handleOnClimbable(Vec3 p_21298_) {
        if (this.onClimbable()) {
            this.resetFallDistance();
            float f = 0.15F;
            double d0 = Mth.clamp(p_21298_.x, (double) -0.15F, (double) 0.15F);
            double d1 = Mth.clamp(p_21298_.z, (double) -0.15F, (double) 0.15F);
            double d2 = Math.max(p_21298_.y, (double) -0.15F);
            if (d2 < 0.0D && !this.getFeetBlockState().isScaffolding(this) && this.isSuppressingSlidingDownLadder()) {
                d2 = 0.0D;
            }

            p_21298_ = new Vec3(d0, d2, d1);
        }

        return p_21298_;
    }

    private float getFrictionInfluencedSpeed(float p_21331_) {
        if (!this.abilities.flying && !this.onGround && this.percentBoost > 0.5F) {
            return this.getSpeed() * (0.21600002F / 0.98F);
        } else if (this.abilities.flying) {
            return this.getFlyingSpeed();
        }
        return this.onGround ? this.getSpeed() * (0.21600002F / (p_21331_ * p_21331_ * p_21331_)) : this.getFlyingSpeed();
    }

    @Override
    public Vec3 handleRelativeFrictionAndCalculateMovement(Vec3 p_21075_, float p_21076_) {
        this.moveRelative(this.getFrictionInfluencedSpeed(p_21076_), p_21075_);
        this.setDeltaMovement(this.handleOnClimbable(this.getDeltaMovement()));
        this.move(MoverType.SELF, this.getDeltaMovement());
        Vec3 vec3 = this.getDeltaMovement();
        if ((this.horizontalCollision || this.jumping) && (this.onClimbable() || this.getFeetBlockState().is(Blocks.POWDER_SNOW) && PowderSnowBlock.canEntityWalkOnPowderSnow(this))) {
            vec3 = new Vec3(vec3.x, 0.2D + 0.2F * percentBoost, vec3.z);
        }

        return vec3;
    }

    /*
     * Dashing Attack Stuff
     */

    public AABB getAttackBoundingBox() {
        Vec3 vec3d = this.getViewVector(1.0F);

        Vec3 vec3 = new Vec3(this.getX() - (double) (this.getBbWidth() * 0.85D), this.getY(), this.getZ() - (double) (this.getBbWidth() * 0.85D));
        Vec3 vec31 = new Vec3(this.getX() + (double) (this.getBbWidth() * 0.85D), this.getY() + this.getBbHeight(), this.getZ() + (double) (this.getBbWidth() * 0.85D));
        return new AABB(vec3, vec31).move(vec3d.x * 1.5D, vec3d.y * 1.5D, vec3d.z * 1.5D);
    }

    protected void pushEntitiesWhenDashed(LivingEntity entity) {
        if (!entity.level.isClientSide()) {
            List<LivingEntity> list = entity.level.getEntities(EntityTypeTest.forClass(LivingEntity.class), getAttackBoundingBox(), EntitySelector.pushableBy(entity));
            if (!list.isEmpty()) {
                for (int l = 0; l < list.size(); ++l) {
                    LivingEntity entity2 = list.get(l);
                    if (entity != entity2 && !entity.isAlliedTo(entity2)) {
                        entity2.knockback(2.0D * percentBoost, entity.getX() - entity2.getX(), entity.getZ() - entity2.getZ());
                        entity2.hurt(entity.damageSources().mobAttack(entity2), Mth.floor(8.0F * percentBoost));
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
        if ((entity.isSprinting()) && entity.getPose() == Pose.STANDING) {
            if (percentBoost <= 1) {
                percentBoost += 0.01F;
            } else if (percentBoost <= 2) {
                percentBoost += 0.005F;
            } else {
                percentBoost = 2;
            }
            entity.walkAnimation.setSpeed(percentBoost + 1.0F);
        } else if (!this.horizontalCollision) {
            if (percentBoost >= 0) {
                percentBoost -= 0.1F;
            } else {
                percentBoost = 0;
            }
        } else {
            if (!this.onGround && this.verticalCollision) {
                percentBoost = 0;
            } else {
                if (percentBoost <= 1) {
                    percentBoost += 0.01F;
                }
                entity.walkAnimation.setSpeed(percentBoost + 1.0F);
            }
        }

        if (percentBoost > 0) {
            if (!entity.level.isClientSide) {
                AttributeInstance attributeinstance = entity.getAttribute(Attributes.MOVEMENT_SPEED);
                if (attributeinstance == null) {
                    return;
                }

                float f = 0.325F * percentBoost;
                attributeinstance.addTransientModifier(new AttributeModifier(SPEED_MODIFIER_BOOST_UUID, "Spark Boost", (double) f, AttributeModifier.Operation.MULTIPLY_TOTAL));
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
        //when shadow is too far or nothing reset
        if (this.shadow == null || this.shadow2 == null || this.shadow.distanceTo(this.position()) >= 30) {
            this.shadow = new Vec3(p_20210_, p_20211_, p_20212_);
            this.shadow2 = new Vec3(p_20210_, p_20211_, p_20212_);
        }
    }
}
