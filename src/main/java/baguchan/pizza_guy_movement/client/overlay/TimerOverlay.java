package baguchan.pizza_guy_movement.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class TimerOverlay implements IGuiOverlay {
    @Override
    public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int width, int height) {
        //gui.getFont().draw(poseStack, StringUtil.formatTickDuration((int)(120500L - gui.getMinecraft().level.getGameTime())), (float)(width / 2),  height - 15.0F, -11534256);
    }


}