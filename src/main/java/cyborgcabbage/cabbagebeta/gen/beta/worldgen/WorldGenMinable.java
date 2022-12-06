package cyborgcabbage.cabbagebeta.gen.beta.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.StructureWorldAccess;

import java.util.Random;

public class WorldGenMinable extends WorldGenerator {
	private final BlockState minableBlockId;
	private final int numberOfBlocks;

	public WorldGenMinable(BlockState i1, int i2) {
		this.minableBlockId = i1;
		this.numberOfBlocks = i2;
	}

	public boolean generate(StructureWorldAccess world, Random random, int i3, int i4, int i5) {
		float f6 = random.nextFloat() * (float)Math.PI;
		double d7 = (double)((float)(i3 + 8) + MathHelper.sin(f6) * (float)this.numberOfBlocks / 8.0F);
		double d9 = (double)((float)(i3 + 8) - MathHelper.sin(f6) * (float)this.numberOfBlocks / 8.0F);
		double d11 = (double)((float)(i5 + 8) + MathHelper.cos(f6) * (float)this.numberOfBlocks / 8.0F);
		double d13 = (double)((float)(i5 + 8) - MathHelper.cos(f6) * (float)this.numberOfBlocks / 8.0F);
		double d15 = (double)(i4 + random.nextInt(3) + 2);
		double d17 = (double)(i4 + random.nextInt(3) + 2);

		for(int i19 = 0; i19 <= this.numberOfBlocks; ++i19) {
			double d20 = d7 + (d9 - d7) * (double)i19 / (double)this.numberOfBlocks;
			double d22 = d15 + (d17 - d15) * (double)i19 / (double)this.numberOfBlocks;
			double d24 = d11 + (d13 - d11) * (double)i19 / (double)this.numberOfBlocks;
			double d26 = random.nextDouble() * (double)this.numberOfBlocks / 16.0D;
			double d28 = (double)(MathHelper.sin((float)i19 * (float)Math.PI / (float)this.numberOfBlocks) + 1.0F) * d26 + 1.0D;
			double d30 = (double)(MathHelper.sin((float)i19 * (float)Math.PI / (float)this.numberOfBlocks) + 1.0F) * d26 + 1.0D;
			int i32 = (int)Math.floor(d20 - d28 / 2.0D);
			int i33 = (int)Math.floor(d22 - d30 / 2.0D);
			int i34 = (int)Math.floor(d24 - d28 / 2.0D);
			int i35 = (int)Math.floor(d20 + d28 / 2.0D);
			int i36 = (int)Math.floor(d22 + d30 / 2.0D);
			int i37 = (int)Math.floor(d24 + d28 / 2.0D);

			for(int i38 = i32; i38 <= i35; ++i38) {
				double d39 = ((double)i38 + 0.5D - d20) / (d28 / 2.0D);
				if(d39 * d39 < 1.0D) {
					for(int i41 = i33; i41 <= i36; ++i41) {
						double d42 = ((double)i41 + 0.5D - d22) / (d30 / 2.0D);
						if(d39 * d39 + d42 * d42 < 1.0D) {
							for(int i44 = i34; i44 <= i37; ++i44) {
								double d45 = ((double)i44 + 0.5D - d24) / (d28 / 2.0D);
								if(d39 * d39 + d42 * d42 + d45 * d45 < 1.0D && world.getBlockState(new BlockPos(i38, i41, i44)).isOf(Blocks.STONE)) {
									world.setBlockState(new BlockPos(i38, i41, i44), this.minableBlockId, Block.NOTIFY_LISTENERS);
								}
							}
						}
					}
				}
			}
		}

		return true;
	}
}
