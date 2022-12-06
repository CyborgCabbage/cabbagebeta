package cyborgcabbage.cabbagebeta.gen.beta.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

import java.util.Random;

public class WorldGenTaiga1 extends WorldGenerator {
	public boolean generate(StructureWorldAccess world, Random random, int i3, int i4, int i5) {
		int i6 = random.nextInt(5) + 7;
		int i7 = i6 - random.nextInt(2) - 3;
		int i8 = i6 - i7;
		int i9 = 1 + random.nextInt(i8 + 1);
		boolean z10 = true;
		if(i4 >= 1 && i4 + i6 + 1 <= 128) {
			for(int i11 = i4; i11 <= i4 + 1 + i6 && z10; ++i11) {
				boolean z12 = true;
				int i18;
				if(i11 - i4 < i7) {
					i18 = 0;
				} else {
					i18 = i9;
				}

				for(int i13 = i3 - i18; i13 <= i3 + i18 && z10; ++i13) {
					for(int i14 = i5 - i18; i14 <= i5 + i18 && z10; ++i14) {
						if(i11 >= 0 && i11 < 128) {
							BlockState blockState = world.getBlockState(new BlockPos(i13, i11, i14));
							if(!blockState.isAir() && !blockState.isIn(BlockTags.LEAVES)){
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
					int i18 = 0;

					for(int i13 = i4 + i6; i13 >= i4 + i7; --i13) {
						for(int i14 = i3 - i18; i14 <= i3 + i18; ++i14) {
							int i15 = i14 - i3;

							for(int i16 = i5 - i18; i16 <= i5 + i18; ++i16) {
								int i17 = i16 - i5;
								if((Math.abs(i15) != i18 || Math.abs(i17) != i18 || i18 <= 0) && !world.getBlockState(new BlockPos(i14, i13, i16)).isOpaque()) {
									world.setBlockState(new BlockPos(i14, i13, i16), Blocks.SPRUCE_LEAVES.getDefaultState(), Block.NOTIFY_LISTENERS);
								}
							}
						}

						if(i18 >= 1 && i13 == i4 + i7 + 1) {
							--i18;
						} else if(i18 < i9) {
							++i18;
						}
					}

					for(int i13 = 0; i13 < i6 - 1; ++i13) {
						BlockState blockState = world.getBlockState(new BlockPos(i3, i4 + i13, i5));
						if(blockState.isAir() || blockState.isIn(BlockTags.LEAVES)) {
							world.setBlockState(new BlockPos(i3, i4 + i13, i5), Blocks.SPRUCE_LOG.getDefaultState(), Block.NOTIFY_LISTENERS);
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
