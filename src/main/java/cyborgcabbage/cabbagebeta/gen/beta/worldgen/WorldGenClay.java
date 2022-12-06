package cyborgcabbage.cabbagebeta.gen.beta.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.StructureWorldAccess;

import java.util.Random;

public class WorldGenClay extends WorldGenerator {
	private final BlockState clay = Blocks.CLAY.getDefaultState();
	private final int numberOfBlocks;

	public WorldGenClay(int num) {
		this.numberOfBlocks = num;
	}

	public boolean generate(StructureWorldAccess world, Random random, int x, int y, int z) {
		if(!world.isWater(new BlockPos(x, y, z))) {
			return false;
		} else {
			float angle = random.nextFloat() * (float)Math.PI;
			double d7 = (float)(x + 8) + MathHelper.sin(angle) * (float)this.numberOfBlocks / 8.0F;
			double d9 = (float)(x + 8) - MathHelper.sin(angle) * (float)this.numberOfBlocks / 8.0F;
			double d11 = (float)(z + 8) + MathHelper.cos(angle) * (float)this.numberOfBlocks / 8.0F;
			double d13 = (float)(z + 8) - MathHelper.cos(angle) * (float)this.numberOfBlocks / 8.0F;
			double d15 = y + random.nextInt(3) + 2;
			double d17 = y + random.nextInt(3) + 2;

			for(int block = 0; block <= this.numberOfBlocks; ++block) {
				double d20 = d7 + (d9 - d7) * (double)block / (double)this.numberOfBlocks;
				double d22 = d15 + (d17 - d15) * (double)block / (double)this.numberOfBlocks;
				double d24 = d11 + (d13 - d11) * (double)block / (double)this.numberOfBlocks;
				double d26 = random.nextDouble() * (double)this.numberOfBlocks / 16.0D;
				double d28 = (double)(MathHelper.sin((float)block * (float)Math.PI / (float)this.numberOfBlocks) + 1.0F) * d26 + 1.0D;
				double d30 = (double)(MathHelper.sin((float)block * (float)Math.PI / (float)this.numberOfBlocks) + 1.0F) * d26 + 1.0D;
				int i32 = (int)Math.floor(d20 - d28 / 2.0D);
				int i33 = (int)Math.floor(d20 + d28 / 2.0D);
				int i34 = (int)Math.floor(d22 - d30 / 2.0D);
				int i35 = (int)Math.floor(d22 + d30 / 2.0D);
				int i36 = (int)Math.floor(d24 - d28 / 2.0D);
				int i37 = (int)Math.floor(d24 + d28 / 2.0D);

				for(int xBlock = i32; xBlock <= i33; ++xBlock) {
					for(int yBlock = i34; yBlock <= i35; ++yBlock) {
						for(int zBlock = i36; zBlock <= i37; ++zBlock) {
							double d41 = ((double)xBlock + 0.5D - d20) / (d28 / 2.0D);
							double d43 = ((double)yBlock + 0.5D - d22) / (d30 / 2.0D);
							double d45 = ((double)zBlock + 0.5D - d24) / (d28 / 2.0D);
							if(d41 * d41 + d43 * d43 + d45 * d45 < 1.0D) {
								var pos = new BlockPos(xBlock, yBlock, zBlock);
								if(world.getBlockState(pos).isOf(Blocks.SAND)) {
									world.setBlockState(pos, this.clay, Block.NOTIFY_LISTENERS);
								}
							}
						}
					}
				}
			}

			return true;
		}
	}
}
