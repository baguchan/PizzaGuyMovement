package baguchan.pizza_guy_movement.client;

import baguchan.pizza_guy_movement.IShadow;
import baguchan.pizza_guy_movement.PizzaGuysMovement;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.minecraft.client.renderer.entity.LivingEntityRenderer.getOverlayCoords;

@Mod.EventBusSubscriber(modid = PizzaGuysMovement.MODID, value = Dist.CLIENT)
public class ClientEvents {
    public static final ResourceLocation LOCATION = new ResourceLocation(PizzaGuysMovement.MODID, "textures/gui/icons.png");

    @SubscribeEvent
    public static void renderEvent(RenderLivingEvent.Post<LivingEntity, EntityModel<LivingEntity>> event) {
        MultiBufferSource buffer = event.getMultiBufferSource();
        LivingEntity entity = event.getEntity();
        LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>> renderer = event.getRenderer();
        PoseStack posestack = event.getPoseStack();
        int light = event.getPackedLight();
        float partialtick = event.getPartialTick();


        if (entity instanceof IShadow shadow && shadow.getPercentBoost() >= 0.5F) {
            posestack.pushPose();
            boolean shouldSit = entity.isPassenger() && (entity.getVehicle() != null && entity.getVehicle().shouldRiderSit());
            float f = Mth.rotLerp(partialtick, entity.yBodyRotO, entity.yBodyRot);
            float f1 = Mth.rotLerp(partialtick, entity.yHeadRotO, entity.yHeadRot);
            float f2 = f1 - f;
            if (shouldSit && entity.getVehicle() instanceof LivingEntity) {
                LivingEntity livingentity = (LivingEntity) entity.getVehicle();
                f = Mth.rotLerp(partialtick, shadow.getShadowRot().y, livingentity.yBodyRot);
                f2 = f1 - f;
                float f3 = Mth.wrapDegrees(f2);
                if (f3 < -85.0F) {
                    f3 = -85.0F;
                }

                if (f3 >= 85.0F) {
                    f3 = 85.0F;
                }

                f = f1 - f3;
                if (f3 * f3 > 2500.0F) {
                        f += f3 * 0.2F;
                    }

                    f2 = f1 - f;
                }

                float f6 = Mth.lerp(partialtick, entity.xRotO, entity.getXRot());

                if (entity.getPose() == Pose.SLEEPING) {
                    Direction direction = entity.getBedOrientation();
                    if (direction != null) {
                        float f4 = entity.getEyeHeight(Pose.STANDING) - 0.1F;
                        posestack.translate((double) ((float) (-direction.getStepX()) * f4), 0.0D, (double) ((float) (-direction.getStepZ()) * f4));
                    }
                }

                float f7 = getBob(entity, partialtick);

                double shadowX = (shadow.getPrevShadow().x + (shadow.getShadow().x - shadow.getPrevShadow().x) * partialtick);
                double shadowY = (shadow.getPrevShadow().y + (shadow.getShadow().y - shadow.getPrevShadow().y) * partialtick);
                double shadowZ = (shadow.getPrevShadow().z + (shadow.getShadow().z - shadow.getPrevShadow().z) * partialtick);
                double shadowX2 = (shadow.getPrevShadow2().x + (shadow.getShadow2().x - shadow.getPrevShadow2().x) * partialtick);
                double shadowY2 = (shadow.getPrevShadow2().y + (shadow.getShadow2().y - shadow.getPrevShadow2().y) * partialtick);
                double shadowZ2 = (shadow.getPrevShadow2().z + (shadow.getShadow2().z - shadow.getPrevShadow2().z) * partialtick);
                double ownerInX = entity.xo + (entity.getX() - entity.xo) * partialtick;
                double ownerInY = entity.yo + (entity.getY() - entity.yo) * partialtick;
                double ownerInZ = entity.zo + (entity.getZ() - entity.zo) * partialtick;
                double deltaX = shadowX - ownerInX;
            double deltaY = shadowY - ownerInY;
            double deltaZ = shadowZ - ownerInZ;
            double deltaX2 = shadowX2 - shadowX;
            double deltaY2 = shadowY2 - shadowY;
            double deltaZ2 = shadowZ2 - shadowZ;

            Pose pose = entity.getPose();

            posestack.translate(deltaX, deltaY, deltaZ);

            if (!entity.hasPose(Pose.SLEEPING)) {
                posestack.mulPose(Axis.YP.rotationDegrees(180.0F - f));
            }
            //renderer.setupRotations(entity, posestack, f7, f, partialtick);
            posestack.scale(-1.0F, -1.0F, 1.0F);
            //renderer.scale(entity, posestack, partialtick);
            posestack.translate(0.0F, (double) -1.501F, 0.0F);


            float f8 = 0.0F;
            float f5 = 0.0F;
            if (!shouldSit && entity.isAlive()) {
                f8 = Mth.lerp(partialtick, entity.animationSpeedOld, entity.animationSpeed);
                f5 = entity.animationPosition - entity.animationSpeed * (1.0F - partialtick);
                if (entity.isBaby()) {
                    f5 *= 3.0F;
                }

                if (f8 > 1.0F) {
                    f8 = 1.0F;
                }
            }

            renderer.getModel().prepareMobModel(entity, f5, f8, partialtick);
            renderer.getModel().setupAnim(entity, f5, f8, f7, f2, f6);
            VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityTranslucent(renderer.getTextureLocation(entity)));
            int i = getOverlayCoords(entity, 0.0F);
            renderer.getModel().renderToBuffer(posestack, vertexconsumer, light, i, 1.0F, 0.3F, 0.3F, (0.45F));
            posestack.popPose();
            posestack.pushPose();
            if (shouldSit && entity.getVehicle() instanceof LivingEntity) {
                f = Mth.rotLerp(partialtick, ((IShadow) entity).getShadowRot2().y, ((IShadow) entity).getShadowRot().y);
                f2 = f1 - f;
                float f3 = Mth.wrapDegrees(f2);
                if (f3 < -85.0F) {
                    f3 = -85.0F;
                }

                if (f3 >= 85.0F) {
                        f3 = 85.0F;
                    }

                    f = f1 - f3;
                    if (f3 * f3 > 2500.0F) {
                        f += f3 * 0.2F;
                    }

                    f2 = f1 - f;
                }

            if (entity.getPose() == Pose.SLEEPING) {
                Direction direction = entity.getBedOrientation();
                if (direction != null) {
                    float f4 = entity.getEyeHeight(Pose.STANDING) - 0.1F;
                    posestack.translate((double) ((float) (-direction.getStepX()) * f4), 0.0D, (double) ((float) (-direction.getStepZ()) * f4));
                }
            }

            posestack.translate(deltaX2, deltaY2, deltaZ2);
            if (!entity.hasPose(Pose.SLEEPING)) {
                posestack.mulPose(Axis.YP.rotationDegrees(180.0F - f));
            }
            //renderer.setupRotations(entity, posestack, f7, f, partialtick);
            posestack.scale(-1.0F, -1.0F, 1.0F);
            //renderer.scale(entity, posestack, partialtick);
            posestack.translate(0.0F, (double) -1.501F, 0.0F);


            renderer.getModel().prepareMobModel(entity, f5, f8, partialtick);
            renderer.getModel().setupAnim(entity, f5, f8, f7, f2, f6);
            renderer.getModel().renderToBuffer(posestack, vertexconsumer, light, i, 0.3F, 1.0F, 0.3F, 0.15F);

            posestack.popPose();
        }
    }

    protected static float getBob(LivingEntity p_115305_, float p_115306_) {
        return (float) p_115305_.tickCount + p_115306_;
    }

    @SubscribeEvent
    public static void renderHudEvent(RenderGuiOverlayEvent.Post event) {
        PoseStack stack = event.getPoseStack();
        Minecraft mc = Minecraft.getInstance();
        Entity entity = mc.getCameraEntity();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight() - ((ForgeGui) mc.gui).rightHeight;
        if (entity != null && event.getOverlay() == VanillaGuiOverlay.FOOD_LEVEL.type()) {
            stack.pushPose();
            RenderSystem.enableBlend();
            if (entity instanceof IShadow shadow) {
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderTexture(0, LOCATION);
                float l = shadow.getPercentBoost() * 20;
                int j1 = screenWidth / 2 + 91;
                int k1 = screenHeight;
                for (int k6 = 0; k6 < 10; k6++) {
                    int i7 = k1;
                    int k7 = shadow.getPercentBoost() >= 1 && entity.tickCount / 3 % 2 == 0 ? 18 : 0;
                    int i8 = 0;
                    int k8 = j1 - k6 * 8 - 9;
                    if (shadow.getPercentBoost() >= 1.9F) {
                        k7 += 9;
                    }

                    mc.gui.blit(stack, k8, i7, 0 + i8 * 9, 0, 9, 9);
                    if (k6 * 2 + 1 < l) {
                        mc.gui.blit(stack, k8, i7, k7 + 46, 0, 9, 9);
                    }
                }
            }
            ;
            RenderSystem.disableBlend();
            ((ForgeGui) mc.gui).rightHeight += 10;
            stack.popPose();
        }
    }
}