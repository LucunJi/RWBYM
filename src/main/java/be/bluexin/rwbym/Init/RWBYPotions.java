package be.bluexin.rwbym.Init;

import be.bluexin.rwbym.potion.PotionDamage;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class RWBYPotions {
	
	public static final Potion INSTANT_DAMAGE = new PotionDamage();

	public void registerPotions() {
		GameRegistry.findRegistry(Potion.class).register(INSTANT_DAMAGE);
	}

}
