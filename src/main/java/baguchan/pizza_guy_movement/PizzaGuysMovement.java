package baguchan.pizza_guy_movement;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(PizzaGuysMovement.MODID)
public class PizzaGuysMovement
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "pizza_guy_movement";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
   public PizzaGuysMovement()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }
}
