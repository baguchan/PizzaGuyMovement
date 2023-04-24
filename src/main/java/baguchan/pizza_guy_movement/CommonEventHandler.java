package baguchan.pizza_guy_movement;

import net.minecraft.tags.DamageTypeTags;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PizzaGuysMovement.MODID)
public class CommonEventHandler {

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof IShadow shadow && !event.getSource().is(DamageTypeTags.BYPASSES_ARMOR) && !event.getSource().is(DamageTypeTags.IS_EXPLOSION) && !event.getSource().is(DamageTypeTags.IS_FIRE)) {
            event.setAmount(event.getAmount() * (1.0F - shadow.getPercentBoost()));
            if (shadow.getPercentBoost() > 1.0F) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingKnockback(LivingKnockBackEvent event) {
        if (event.getEntity() instanceof IShadow shadow) {
            if (shadow.getPercentBoost() > 1.0F) {
                event.setCanceled(true);
            }
        }
    }
}
