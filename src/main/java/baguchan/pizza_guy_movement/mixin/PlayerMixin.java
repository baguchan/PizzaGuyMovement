package baguchan.pizza_guy_movement.mixin;

import baguchan.pizza_guy_movement.IShadow;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

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
}
