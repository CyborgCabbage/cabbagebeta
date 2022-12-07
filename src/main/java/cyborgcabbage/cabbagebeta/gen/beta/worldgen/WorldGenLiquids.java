package cyborgcabbage.cabbagebeta.gen.beta.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

import java.util.Random;

public class WorldGenLiquids extends WorldGenerator {
	private final BlockState state;

	public WorldGenLiquids(BlockState i1) {
		this.state = i1;
	}

	public boolean generate(StructureWorldAccess world, Random random, int x, int y, int z) {
		if(!world.getBlockState(new BlockPos(x, y + 1, z)).isOf(Blocks.STONE)) {
			return false;
		} else if(!world.getBlockState(new BlockPos(x, y - 1, z)).isOf(Blocks.STONE)) {
			return false;
		} else if(!world.getBlockState(new BlockPos(x, y, z)).isAir() && !world.getBlockState(new BlockPos(x, y, z)).isOf(Blocks.STONE)) {
			return false;
		} else {
			int i6 = 0;
			if(world.getBlockState(new BlockPos(x - 1, y, z)).isOf(Blocks.STONE)) {
				++i6;
			}

			if(world.getBlockState(new BlockPos(x + 1, y, z)).isOf(Blocks.STONE)) {
				++i6;
			}

			if(world.getBlockState(new BlockPos(x, y, z - 1)).isOf(Blocks.STONE)) {
				++i6;
			}

			if(world.getBlockState(new BlockPos(x, y, z + 1)).isOf(Blocks.STONE)) {
				++i6;
			}

			int i7 = 0;
			if(world.isAir(new BlockPos(x - 1, y, z))) {
				++i7;
			}

			if(world.isAir(new BlockPos(x + 1, y, z))) {
				++i7;
			}

			if(world.isAir(new BlockPos(x, y, z - 1))) {
				++i7;
			}

			if(world.isAir(new BlockPos(x, y, z + 1))) {
				++i7;
			}

			if(i6 == 3 && i7 == 1) {
				BlockPos pos = new BlockPos(x, y, z);
				world.setBlockState(pos, state, Block.NOTIFY_LISTENERS);
				world.createAndScheduleFluidTick(pos, state.getFluidState().getFluid(), 1);
				//world.scheduledUpdatesAreImmediate = true;
				//Block.blocksList[this.liquidBlockId].updateTick(world, i3, i4, i5, random);
				//world.scheduledUpdatesAreImmediate = false;
			}

			return true;
		}
	}
}
