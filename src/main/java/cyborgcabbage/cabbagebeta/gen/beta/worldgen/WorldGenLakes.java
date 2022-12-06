package cyborgcabbage.cabbagebeta.gen.beta.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.StructureWorldAccess;

import java.util.Random;

public class WorldGenLakes extends WorldGenerator {
	private final BlockState state;

	public WorldGenLakes(BlockState i1) {
		this.state = i1;
	}

	public boolean generate(StructureWorldAccess world, Random random, int i3, int i4, int i5) {
		i3 -= 8;

		for(i5 -= 8; i4 > 0 && world.isAir(new BlockPos(i3, i4, i5)); --i4) {
		}

		i4 -= 4;
		boolean[] z6 = new boolean[2048];
		int i7 = random.nextInt(4) + 4;

		int i8;
		for(i8 = 0; i8 < i7; ++i8) {
			double d9 = random.nextDouble() * 6.0D + 3.0D;
			double d11 = random.nextDouble() * 4.0D + 2.0D;
			double d13 = random.nextDouble() * 6.0D + 3.0D;
			double d15 = random.nextDouble() * (16.0D - d9 - 2.0D) + 1.0D + d9 / 2.0D;
			double d17 = random.nextDouble() * (8.0D - d11 - 4.0D) + 2.0D + d11 / 2.0D;
			double d19 = random.nextDouble() * (16.0D - d13 - 2.0D) + 1.0D + d13 / 2.0D;

			for(int i21 = 1; i21 < 15; ++i21) {
				for(int i22 = 1; i22 < 15; ++i22) {
					for(int i23 = 1; i23 < 7; ++i23) {
						double d24 = ((double)i21 - d15) / (d9 / 2.0D);
						double d26 = ((double)i23 - d17) / (d11 / 2.0D);
						double d28 = ((double)i22 - d19) / (d13 / 2.0D);
						double d30 = d24 * d24 + d26 * d26 + d28 * d28;
						if(d30 < 1.0D) {
							z6[(i21 * 16 + i22) * 8 + i23] = true;
						}
					}
				}
			}
		}

		int i10;
		int i32;
		boolean z33;
		for(i8 = 0; i8 < 16; ++i8) {
			for(i32 = 0; i32 < 16; ++i32) {
				for(i10 = 0; i10 < 8; ++i10) {
					z33 = !z6[(i8 * 16 + i32) * 8 + i10] && (i8 < 15 && z6[((i8 + 1) * 16 + i32) * 8 + i10] || i8 > 0 && z6[((i8 - 1) * 16 + i32) * 8 + i10] || i32 < 15 && z6[(i8 * 16 + i32 + 1) * 8 + i10] || i32 > 0 && z6[(i8 * 16 + (i32 - 1)) * 8 + i10] || i10 < 7 && z6[(i8 * 16 + i32) * 8 + i10 + 1] || i10 > 0 && z6[(i8 * 16 + i32) * 8 + (i10 - 1)]);
					if(z33) {
						Material material12 = world.getBlockState(new BlockPos(i3 + i8, i4 + i10, i5 + i32)).getMaterial();
						if(i10 >= 4 && material12.isLiquid()) {
							return false;
						}

						if(i10 < 4 && !material12.isSolid() && world.getBlockState(new BlockPos(i3 + i8, i4 + i10, i5 + i32)).getBlock() != this.state.getBlock()) {
							return false;
						}
					}
				}
			}
		}

		for(i8 = 0; i8 < 16; ++i8) {
			for(i32 = 0; i32 < 16; ++i32) {
				for(i10 = 0; i10 < 8; ++i10) {
					if(z6[(i8 * 16 + i32) * 8 + i10]) {
						world.setBlockState(new BlockPos(i3 + i8, i4 + i10, i5 + i32), i10 >= 4 ? Blocks.AIR.getDefaultState() : this.state, Block.NOTIFY_LISTENERS);
					}
				}
			}
		}

		for(i8 = 0; i8 < 16; ++i8) {
			for(i32 = 0; i32 < 16; ++i32) {
				for(i10 = 4; i10 < 8; ++i10) {
					if(z6[(i8 * 16 + i32) * 8 + i10] && world.getBlockState(new BlockPos(i3 + i8, i4 + i10 - 1, i5 + i32)).isOf(Blocks.DIRT) && world.getLightLevel(LightType.SKY, new BlockPos(i3 + i8, i4 + i10, i5 + i32)) > 0) {
						world.setBlockState(new BlockPos(i3 + i8, i4 + i10 - 1, i5 + i32), Blocks.GRASS_BLOCK.getDefaultState(), Block.NOTIFY_LISTENERS);
					}
				}
			}
		}

		if(state.getMaterial() == Material.LAVA) {
			for(i8 = 0; i8 < 16; ++i8) {
				for(i32 = 0; i32 < 16; ++i32) {
					for(i10 = 0; i10 < 8; ++i10) {
						z33 = !z6[(i8 * 16 + i32) * 8 + i10] && (i8 < 15 && z6[((i8 + 1) * 16 + i32) * 8 + i10] || i8 > 0 && z6[((i8 - 1) * 16 + i32) * 8 + i10] || i32 < 15 && z6[(i8 * 16 + i32 + 1) * 8 + i10] || i32 > 0 && z6[(i8 * 16 + (i32 - 1)) * 8 + i10] || i10 < 7 && z6[(i8 * 16 + i32) * 8 + i10 + 1] || i10 > 0 && z6[(i8 * 16 + i32) * 8 + (i10 - 1)]);
						if(z33 && (i10 < 4 || random.nextInt(2) != 0) && world.getBlockState(new BlockPos(i3 + i8, i4 + i10, i5 + i32)).getMaterial().isSolid()) {
							world.setBlockState(new BlockPos(i3 + i8, i4 + i10, i5 + i32), Blocks.STONE.getDefaultState(), Block.NOTIFY_LISTENERS);
						}
					}
				}
			}
		}

		return true;
	}
}
