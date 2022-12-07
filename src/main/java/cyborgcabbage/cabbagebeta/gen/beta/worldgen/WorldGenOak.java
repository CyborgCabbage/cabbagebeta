package cyborgcabbage.cabbagebeta.gen.beta.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

import java.util.Random;

public class WorldGenOak extends WorldGenerator {
	private int height;
	public WorldGenOak(int height) {
		this.height = height;
	}

	public boolean generate(StructureWorldAccess world, Random random, int x, int y, int z) {
		int i6 = random.nextInt(3) + 4;
		boolean z7 = true;
		if(y >= 1 && y + i6 + 1 <= height) {
			for(int i8 = y; i8 <= y + 1 + i6; ++i8) {
				byte b9 = 1;
				if(i8 == y) {
					b9 = 0;
				}

				if(i8 >= y + 1 + i6 - 2) {
					b9 = 2;
				}

				for(int i10 = x - b9; i10 <= x + b9 && z7; ++i10) {
					for(int i11 = z - b9; i11 <= z + b9 && z7; ++i11) {
						if(i8 >= 0 && i8 < height) {
							BlockState blockState = world.getBlockState(new BlockPos(i10, i8, i11));
							if(!blockState.isAir() && !blockState.isIn(BlockTags.LEAVES)){
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
				BlockState i8 = world.getBlockState(new BlockPos(x, y - 1, z));
				if((i8.isOf(Blocks.GRASS_BLOCK) || i8.isOf(Blocks.DIRT)) && y < height - i6 - 1) {
					world.setBlockState(new BlockPos(x, y - 1, z), Blocks.DIRT.getDefaultState(), Block.NOTIFY_LISTENERS);

					for(int i16 = y - 3 + i6; i16 <= y + i6; ++i16) {
						int i10 = i16 - (y + i6);
						int i11 = 1 - i10 / 2;

						for(int i12 = x - i11; i12 <= x + i11; ++i12) {
							int i13 = i12 - x;

							for(int i14 = z - i11; i14 <= z + i11; ++i14) {
								int i15 = i14 - z;
								if((Math.abs(i13) != i11 || Math.abs(i15) != i11 || random.nextInt(2) != 0 && i10 != 0) && !world.getBlockState(new BlockPos(i12, i16, i14)).isOpaque()) {
									world.setBlockState(new BlockPos(i12, i16, i14), Blocks.OAK_LEAVES.getDefaultState(), Block.NOTIFY_LISTENERS);
								}
							}
						}
					}

					for(int i16 = 0; i16 < i6; ++i16) {
						BlockState blockState = world.getBlockState(new BlockPos(x, y + i16, z));
						if(blockState.isAir() || blockState.isIn(BlockTags.LEAVES)) {
							world.setBlockState(new BlockPos(x, y + i16, z), Blocks.OAK_LOG.getDefaultState(), Block.NOTIFY_LISTENERS);
							Blocks.OAK_LOG.getDefaultState().updateNeighbors(world, new BlockPos(x, y + i16, z), Block.NOTIFY_LISTENERS);
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
