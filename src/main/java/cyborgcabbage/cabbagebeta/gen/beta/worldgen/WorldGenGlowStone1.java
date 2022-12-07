package cyborgcabbage.cabbagebeta.gen.beta.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

import java.util.Random;

public class WorldGenGlowStone1 extends WorldGenerator {
	public boolean generate(StructureWorldAccess world, Random random, int x, int y, int z) {
		if(!world.isAir(new BlockPos(x, y, z))) {
			return false;
		}
		if(!world.getBlockState(new BlockPos(x, y + 1, z)).isOf(Blocks.NETHERRACK)) {
			return false;
		}
		world.setBlockState(new BlockPos(x, y, z), Blocks.GLOWSTONE.getDefaultState(), Block.NOTIFY_LISTENERS);

		for(int i6 = 0; i6 < 1500; ++i6) {
			int i7 = x + random.nextInt(8) - random.nextInt(8);
			int i8 = y - random.nextInt(12);
			int i9 = z + random.nextInt(8) - random.nextInt(8);
			if(world.getBlockState(new BlockPos(i7, i8, i9)).isAir()) {
				int i10 = 0;

				for(int i11 = 0; i11 < 6; ++i11) {
					BlockState i12 = Blocks.AIR.getDefaultState();
					if(i11 == 0) {
						i12 = world.getBlockState(new BlockPos(i7 - 1, i8, i9));
					}

					if(i11 == 1) {
						i12 = world.getBlockState(new BlockPos(i7 + 1, i8, i9));
					}

					if(i11 == 2) {
						i12 = world.getBlockState(new BlockPos(i7, i8 - 1, i9));
					}

					if(i11 == 3) {
						i12 = world.getBlockState(new BlockPos(i7, i8 + 1, i9));
					}

					if(i11 == 4) {
						i12 = world.getBlockState(new BlockPos(i7, i8, i9 - 1));
					}

					if(i11 == 5) {
						i12 = world.getBlockState(new BlockPos(i7, i8, i9 + 1));
					}

					if(i12.isOf(Blocks.GLOWSTONE)) {
						++i10;
					}
				}

				if(i10 == 1) {
					world.setBlockState(new BlockPos(i7, i8, i9), Blocks.GLOWSTONE.getDefaultState(), Block.NOTIFY_LISTENERS);
				}
			}
		}

		return true;
	}
}
