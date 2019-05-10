package be.bluexin.rwbym.weaponry.ammohit;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NullAmmoHit implements IAmmoHit {

	@Override
	public void applyBlock(EntityLivingBase shooter, BlockPos pos) {}

	@Override
	public void applyEntity(EntityLivingBase living, EntityLivingBase shooter) {}

}
