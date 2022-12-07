package cyborgcabbage.cabbagebeta.gen.beta.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

import java.util.Random;

public class WorldGenForest extends WorldGenerator {
	private int height;

	public WorldGenForest(int height) {
		this.height = height;
	}

	public boolean generate(StructureWorldAccess world, Random random, int x, int y, int z) {
		int i6 = random.nextInt(3) + 5;
		boolean z7 = true;
		if(y >= 1 && y + i6 + 1 <= height) {
			int i8;
			int i10;
			int i11;

			for(i8 = y; i8 <= y + 1 + i6; ++i8) {
				byte b9 = 1;
				if(i8 == y) {
					b9 = 0;
				}

				if(i8 >= y + 1 + i6 - 2) {
					b9 = 2;
				}

				for(i10 = x - b9; i10 <= x + b9 && z7; ++i10) {
					for(i11 = z - b9; i11 <= z + b9 && z7; ++i11) {
						if(i8 >= 0 && i8 < height) {
							BlockState state = world.getBlockState(new BlockPos(i10, i8, i11));
							if(!state.isAir() && !state.isIn(BlockTags.LEAVES)) {
								z7 = false;
							}
						} else {
							z7 = false;
						}
					}
				}
			}

			if(!z7) {
				return false;
			} else {
				BlockState state = world.getBlockState(new BlockPos(x, y - 1, z));
				if((state.isOf(Blocks.GRASS_BLOCK) || state.isOf(Blocks.DIRT)) && y < height - i6 - 1) {
					world.setBlockState(new BlockPos(x, y - 1, z), Blocks.DIRT.getDefaultState(), Block.NOTIFY_LISTENERS);

					int i16;
					for(i16 = y - 3 + i6; i16 <= y + i6; ++i16) {
						i10 = i16 - (y + i6);
						i11 = 1 - i10 / 2;

						for(int i12 = x - i11; i12 <= x + i11; ++i12) {
							int i13 = i12 - x;

							for(int i14 = z - i11; i14 <= z + i11; ++i14) {
								int i15 = i14 - z;
								if((Math.abs(i13) != i11 || Math.abs(i15) != i11 || random.nextInt(2) != 0 && i10 != 0) && !world.getBlockState(new BlockPos(i12, i16, i14)).isOpaque()) {
									world.setBlockState(new BlockPos(i12, i16, i14), Blocks.BIRCH_LEAVES.getDefaultState(), Block.NOTIFY_LISTENERS);
								}
							}
						}
					}

					for(i16 = 0; i16 < i6; ++i16) {
						BlockState state2 = world.getBlockState(new BlockPos(x, y + i16, z));
						if(state2.isAir() || state2.isIn(BlockTags.LEAVES)) {
							world.setBlockState(new BlockPos(x, y + i16, z), Blocks.BIRCH_LOG.getDefaultState(), Block.NOTIFY_LISTENERS);
						}
					}

					return true;
				} else {
					return false;
				}
			}
		} else {
			return false;
		}
	}
}
