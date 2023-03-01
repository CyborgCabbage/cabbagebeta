package cyborgcabbage.cabbagebeta.gen.beta.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

import java.util.Random;

public class WorldGenTallGrass extends WorldGenerator {
	private final BlockState state;
	private final int height;
	public WorldGenTallGrass(BlockState i1, int h) {
		this.state = i1;
		this.height = h;
	}

	public boolean generate(StructureWorldAccess world, Random random, int x, int y, int z) {
		BlockPos.Mutable blockPos = new BlockPos.Mutable(0,0,0);
		BlockState i11;
		while (((i11 = world.getBlockState(blockPos.set(x, y, z))).isAir() || i11.isIn(BlockTags.LEAVES)) && y > 0) {
			--y;
		}

		for(int i = 0; i < height; ++i) {
			int rx = x + random.nextInt(8) - random.nextInt(8);
			int ry = y + random.nextInt(4) - random.nextInt(4);
			int rz = z + random.nextInt(8) - random.nextInt(8);
			blockPos.set(rx, ry, rz);
			if(world.isAir(blockPos) && state.canPlaceAt(world, blockPos)) {
				world.setBlockState(blockPos, state, Block.NOTIFY_LISTENERS);
			}
		}
		return true;
	}
}
