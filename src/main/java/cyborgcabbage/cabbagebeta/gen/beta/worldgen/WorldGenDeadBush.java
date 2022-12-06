package cyborgcabbage.cabbagebeta.gen.beta.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

import java.util.Random;

public class WorldGenDeadBush extends WorldGenerator {
	private final BlockState state;

	public WorldGenDeadBush(BlockState i1) {
		this.state = i1;
	}

	public boolean generate(StructureWorldAccess world, Random random, int x, int y, int z) {
		BlockState block;
		while(((block = world.getBlockState(new BlockPos(x, y, z))).isAir() || block.getBlock() instanceof LeavesBlock) && y > 0){
			--y;
		}

		for(int i = 0; i < 4; ++i) {
			int xR = x + random.nextInt(8) - random.nextInt(8);
			int yR = y + random.nextInt(4) - random.nextInt(4);
			int zR = z + random.nextInt(8) - random.nextInt(8);
			var pos = new BlockPos(xR, yR, zR);
			if(world.isAir(pos) && state.canPlaceAt(world, pos)) {
				world.setBlockState(pos, state, Block.NOTIFY_LISTENERS);
			}
		}

		return true;
	}
}
