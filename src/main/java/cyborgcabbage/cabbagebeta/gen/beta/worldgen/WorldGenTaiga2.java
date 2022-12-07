package cyborgcabbage.cabbagebeta.gen.beta.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

import java.util.Random;

public class WorldGenTaiga2 extends WorldGenerator {
	private int height;

	public WorldGenTaiga2(int height) {
		this.height = height;
	}

	public boolean generate(StructureWorldAccess world, Random random, int x, int y, int z) {
		int i6 = random.nextInt(4) + 6;
		int i7 = 1 + random.nextInt(2);
		int i8 = i6 - i7;
		int i9 = 2 + random.nextInt(2);
		boolean z10 = true;
		if(y >= 1 && y + i6 + 1 <= height) {
			for(int i11 = y; i11 <= y + 1 + i6 && z10; ++i11) {
				boolean z12 = true;
				int i21;
				if(i11 - y < i7) {
					i21 = 0;
				} else {
					i21 = i9;
				}

				for(int i13 = x - i21; i13 <= x + i21 && z10; ++i13) {
					for(int i14 = z - i21; i14 <= z + i21 && z10; ++i14) {
						if(i11 >= 0 && i11 < height) {
							BlockState i15 = world.getBlockState(new BlockPos(i13, i11, i14));
							if(!i15.isAir() && !i15.isIn(BlockTags.LEAVES)) {
								z10 = false;
							}
						} else {
							z10 = false;
						}
					}
				}
			}

			if(!z10) {
				return false;
			} else {
				BlockState i11 = world.getBlockState(new BlockPos(x, y - 1, z));
				if((i11.isOf(Blocks.GRASS_BLOCK) || i11.isOf(Blocks.DIRT)) && y < height - i6 - 1) {
					world.setBlockState(new BlockPos(x, y - 1, z), Blocks.DIRT.getDefaultState(), Block.NOTIFY_LISTENERS);
					int i21 = random.nextInt(2);
					int i13 = 1;
					byte b22 = 0;

					for(int i15 = 0; i15 <= i8; ++i15) {
						int i16 = y + i6 - i15;

						for(int i17 = x - i21; i17 <= x + i21; ++i17) {
							int i18 = i17 - x;

							for(int i19 = z - i21; i19 <= z + i21; ++i19) {
								int i20 = i19 - z;
								if((Math.abs(i18) != i21 || Math.abs(i20) != i21 || i21 <= 0) && !world.getBlockState(new BlockPos(i17, i16, i19)).isOpaque()) {
									world.setBlockState(new BlockPos(i17, i16, i19), Blocks.SPRUCE_LEAVES.getDefaultState(), Block.NOTIFY_LISTENERS);
								}
							}
						}

						if(i21 >= i13) {
							i21 = b22;
							b22 = 1;
							++i13;
							if(i13 > i9) {
								i13 = i9;
							}
						} else {
							++i21;
						}
					}

					int i15 = random.nextInt(3);

					for(int i16 = 0; i16 < i6 - i15; ++i16) {
						BlockState blockState = world.getBlockState(new BlockPos(x, y + i16, z));
						if(blockState.isAir() || blockState.isIn(BlockTags.LEAVES)) {
							world.setBlockState(new BlockPos(x, y + i16, z), Blocks.SPRUCE_LOG.getDefaultState(), Block.NOTIFY_LISTENERS);
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
