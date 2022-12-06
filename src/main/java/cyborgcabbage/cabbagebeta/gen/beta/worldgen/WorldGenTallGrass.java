package cyborgcabbage.cabbagebeta.gen.beta.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

import java.util.Random;

public class WorldGenTallGrass extends WorldGenerator {
	private final BlockState state;

	public WorldGenTallGrass(BlockState i1) {
		this.state = i1;
	}

	public boolean generate(StructureWorldAccess world, Random random, int i3, int i4, int i5) {
		BlockState i11;
		while (((i11 = world.getBlockState(new BlockPos(i3, i4, i5))).isAir() || i11.isIn(BlockTags.LEAVES)) && i4 > 0) {
			--i4;
		}

		for(int i7 = 0; i7 < 128; ++i7) {
			int i8 = i3 + random.nextInt(8) - random.nextInt(8);
			int i9 = i4 + random.nextInt(4) - random.nextInt(4);
			int i10 = i5 + random.nextInt(8) - random.nextInt(8);
			if(world.isAir(new BlockPos(i8, i9, i10)) && state.canPlaceAt(world, new BlockPos(i8, i9, i10))) {
				world.setBlockState(new BlockPos(i8, i9, i10), state, Block.NOTIFY_LISTENERS);
			}
		}

		return true;
	}
}
