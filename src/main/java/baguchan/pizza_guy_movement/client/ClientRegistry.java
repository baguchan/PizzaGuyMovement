package baguchan.pizza_guy_movement.client;

import baguchan.pizza_guy_movement.PizzaGuysMovement;
import baguchan.pizza_guy_movement.client.overlay.TimerOverlay;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PizzaGuysMovement.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistry {
    @SubscribeEvent
    public static void registerOverlay(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("timer", new TimerOverlay());
    }
}