package cyborgcabbage.cabbagebeta.gen.beta.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;

import java.util.Random;

public class WorldGenPumpkin extends WorldGenerator {
	public boolean generate(StructureWorldAccess world, Random random, int i3, int i4, int i5) {
		for(int i6 = 0; i6 < 64; ++i6) {
			int i7 = i3 + random.nextInt(8) - random.nextInt(8);
			int i8 = i4 + random.nextInt(4) - random.nextInt(4);
			int i9 = i5 + random.nextInt(8) - random.nextInt(8);
			if(world.isAir(new BlockPos(i7, i8, i9)) && world.getBlockState(new BlockPos(i7, i8 - 1, i9)).isOf(Blocks.GRASS_BLOCK) && Blocks.CARVED_PUMPKIN.getDefaultState().canPlaceAt(world, new BlockPos(i7, i8, i9))) {
				world.setBlockState(new BlockPos(i7, i8, i9), Blocks.CARVED_PUMPKIN.getDefaultState().with(CarvedPumpkinBlock.FACING, Direction.fromHorizontal(random.nextInt(4))), Block.NOTIFY_LISTENERS);
			}
		}

		return true;
	}
}
