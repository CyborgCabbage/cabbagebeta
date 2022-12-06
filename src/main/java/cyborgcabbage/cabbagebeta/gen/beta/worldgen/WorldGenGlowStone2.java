package cyborgcabbage.cabbagebeta.gen.beta.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

import java.util.Random;

public class WorldGenGlowStone2 extends WorldGenerator {
	public boolean generate(StructureWorldAccess world, Random random, int i3, int i4, int i5) {
		if(!world.isAir(new BlockPos(i3, i4, i5))) {
			return false;
		} else if(!world.getBlockState(new BlockPos(i3, i4 + 1, i5)).isOf(Blocks.NETHERRACK)) {
			return false;
		} else {
			world.setBlockState(new BlockPos(i3, i4, i5), Blocks.GLOWSTONE.getDefaultState(), Block.NOTIFY_LISTENERS);

			for(int i6 = 0; i6 < 1500; ++i6) {
				int i7 = i3 + random.nextInt(8) - random.nextInt(8);
				int i8 = i4 - random.nextInt(12);
				int i9 = i5 + random.nextInt(8) - random.nextInt(8);
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
}
