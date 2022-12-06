package cyborgcabbage.cabbagebeta.gen.beta.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

import java.util.Random;

public class WorldGenCactus extends WorldGenerator {
	public boolean generate(StructureWorldAccess world, Random random, int i3, int i4, int i5) {
		for(int i6 = 0; i6 < 10; ++i6) {
			int i7 = i3 + random.nextInt(8) - random.nextInt(8);
			int i8 = i4 + random.nextInt(4) - random.nextInt(4);
			int i9 = i5 + random.nextInt(8) - random.nextInt(8);
			if(world.isAir(new BlockPos(i7, i8, i9))) {
				int height = 1 + random.nextInt(random.nextInt(3) + 1);

				for(int i11 = 0; i11 < height; ++i11) {
					var state = Blocks.CACTUS.getDefaultState();
					var pos = new BlockPos(i7, i8 + i11, i9);
					if(state.canPlaceAt(world, pos)) {
						world.setBlockState(pos, state, Block.NOTIFY_LISTENERS);
					}
				}
			}
		}
		return true;
	}
}
