package cyborgcabbage.cabbagebeta.gen.beta.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

import java.util.Random;

public class WorldGenReed extends WorldGenerator {
	public boolean generate(StructureWorldAccess world, Random random, int x, int y, int z) {
		for(int i6 = 0; i6 < 20; ++i6) {
			int i7 = x + random.nextInt(4) - random.nextInt(4);
			int i8 = y;
			int i9 = z + random.nextInt(4) - random.nextInt(4);
			if(world.isAir(new BlockPos(i7, y, i9)) && (
					world.getBlockState(new BlockPos(i7 - 1, y - 1, i9)).getMaterial() == Material.WATER ||
					world.getBlockState(new BlockPos(i7 + 1, y - 1, i9)).getMaterial() == Material.WATER ||
					world.getBlockState(new BlockPos(i7, y - 1, i9 - 1)).getMaterial() == Material.WATER ||
					world.getBlockState(new BlockPos(i7, y - 1, i9 + 1)).getMaterial() == Material.WATER)) {
				int i10 = 2 + random.nextInt(random.nextInt(3) + 1);

				for(int i11 = 0; i11 < i10; ++i11) {
					if(Blocks.SUGAR_CANE.getDefaultState().canPlaceAt(world, new BlockPos(i7, i8 + i11, i9))) {
						world.setBlockState(new BlockPos(i7, i8 + i11, i9), Blocks.SUGAR_CANE.getDefaultState(), Block.NOTIFY_LISTENERS);
					}
				}
			}
		}

		return true;
	}
}
