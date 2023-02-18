package baguchan.pizza_guy_movement;

import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public interface IShadow {
    Vec2 getShadowRot();

    Vec2 getShadowRot2();

    Vec2 getPrevShadowRot();

    Vec2 getPrevShadowRot2();

    Vec3 getShadow();

    Vec3 getShadow2();

    Vec3 getPrevShadow();

    Vec3 getPrevShadow2();
}
