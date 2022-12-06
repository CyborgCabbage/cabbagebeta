package cyborgcabbage.cabbagebeta.gen.beta.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

import java.util.Random;

public class WorldGenFire extends WorldGenerator {
	public boolean generate(StructureWorldAccess world, Random random, int i3, int i4, int i5) {
		for(int i6 = 0; i6 < 64; ++i6) {
			int i7 = i3 + random.nextInt(8) - random.nextInt(8);
			int i8 = i4 + random.nextInt(4) - random.nextInt(4);
			int i9 = i5 + random.nextInt(8) - random.nextInt(8);
			var pos = new BlockPos(i7, i8, i9);
			if(world.isAir(pos) && world.getBlockState(pos.down()).isOf(Blocks.NETHERRACK)) {
				world.setBlockState(pos, Blocks.FIRE.getDefaultState(), Block.NOTIFY_LISTENERS);
			}
		}

		return true;
	}
}
