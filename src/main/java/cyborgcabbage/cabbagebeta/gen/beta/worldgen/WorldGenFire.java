package cyborgcabbage.cabbagebeta.gen.beta.worldgen;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

import java.util.Random;

public class WorldGenFire extends WorldGenerator {
	public boolean generate(StructureWorldAccess world, Random random, int x, int y, int z) {
		for(int i6 = 0; i6 < 64; ++i6) {
			int i7 = x + random.nextInt(8) - random.nextInt(8);
			int i8 = y + random.nextInt(4) - random.nextInt(4);
			int i9 = z + random.nextInt(8) - random.nextInt(8);
			var pos = new BlockPos(i7, i8, i9);
			if(world.isAir(pos) && world.getBlockState(pos.down()).isOf(Blocks.NETHERRACK)) {
				setBlockWithNotify(world, pos, Blocks.FIRE.getDefaultState());
			}
		}

		return true;
	}
}
