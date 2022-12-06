package cyborgcabbage.cabbagebeta.gen.beta.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

import java.util.Random;

public class WorldGenFlowers extends WorldGenerator {
	private final BlockState state;

	public WorldGenFlowers(BlockState plant) {
		this.state = plant;
	}

	public boolean generate(StructureWorldAccess world, Random random, int x, int y, int z) {
		for(int i6 = 0; i6 < 64; ++i6) {
			int i7 = x + random.nextInt(8) - random.nextInt(8);
			int i8 = y + random.nextInt(4) - random.nextInt(4);
			int i9 = z + random.nextInt(8) - random.nextInt(8);
			var pos = new BlockPos(i7, i8, i9);
			if(world.isAir(pos) && state.canPlaceAt(world, pos)) {
				world.setBlockState(pos, state, Block.NOTIFY_LISTENERS);
			}
		}

		return true;
	}
}
