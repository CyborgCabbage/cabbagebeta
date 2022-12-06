package cyborgcabbage.cabbagebeta.gen.beta.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

import java.util.Random;

public class WorldGenTaiga2 extends WorldGenerator {
	public boolean generate(StructureWorldAccess world, Random random, int i3, int i4, int i5) {
		int i6 = random.nextInt(4) + 6;
		int i7 = 1 + random.nextInt(2);
		int i8 = i6 - i7;
		int i9 = 2 + random.nextInt(2);
		boolean z10 = true;
		if(i4 >= 1 && i4 + i6 + 1 <= 128) {
			for(int i11 = i4; i11 <= i4 + 1 + i6 && z10; ++i11) {
				boolean z12 = true;
				int i21;
				if(i11 - i4 < i7) {
					i21 = 0;
				} else {
					i21 = i9;
				}

				for(int i13 = i3 - i21; i13 <= i3 + i21 && z10; ++i13) {
					for(int i14 = i5 - i21; i14 <= i5 + i21 && z10; ++i14) {
						if(i11 >= 0 && i11 < 128) {
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
				BlockState i11 = world.getBlockState(new BlockPos(i3, i4 - 1, i5));
				if((i11.isOf(Blocks.GRASS_BLOCK) || i11.isOf(Blocks.DIRT)) && i4 < 128 - i6 - 1) {
					world.setBlockState(new BlockPos(i3, i4 - 1, i5), Blocks.DIRT.getDefaultState(), Block.NOTIFY_LISTENERS);
					int i21 = random.nextInt(2);
					int i13 = 1;
					byte b22 = 0;

					for(int i15 = 0; i15 <= i8; ++i15) {
						int i16 = i4 + i6 - i15;

						for(int i17 = i3 - i21; i17 <= i3 + i21; ++i17) {
							int i18 = i17 - i3;

							for(int i19 = i5 - i21; i19 <= i5 + i21; ++i19) {
								int i20 = i19 - i5;
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
						BlockState blockState = world.getBlockState(new BlockPos(i3, i4 + i16, i5));
						if(blockState.isAir() || blockState.isIn(BlockTags.LEAVES)) {
							world.setBlockState(new BlockPos(i3, i4 + i16, i5), Blocks.SPRUCE_LOG.getDefaultState(), Block.NOTIFY_LISTENERS);
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
