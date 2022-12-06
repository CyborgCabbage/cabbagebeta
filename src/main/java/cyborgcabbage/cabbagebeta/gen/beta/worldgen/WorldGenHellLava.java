package cyborgcabbage.cabbagebeta.gen.beta.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

import java.util.Random;

public class WorldGenHellLava extends WorldGenerator {
	private final BlockState state;

	public WorldGenHellLava(BlockState i1) {
		this.state = i1;
	}

	public boolean generate(StructureWorldAccess world, Random random, int i3, int i4, int i5) {
		if(!world.getBlockState(new BlockPos(i3, i4 + 1, i5)).isOf(Blocks.NETHERRACK)) {
			return false;
		} else if(!world.getBlockState(new BlockPos(i3, i4, i5)).isAir() && !world.getBlockState(new BlockPos(i3, i4, i5)).isOf(Blocks.NETHERRACK)) {
			return false;
		} else {
			int i6 = 0;
			if(world.getBlockState(new BlockPos(i3 - 1, i4, i5)).isOf(Blocks.NETHERRACK)) {
				++i6;
			}

			if(world.getBlockState(new BlockPos(i3 + 1, i4, i5)).isOf(Blocks.NETHERRACK)) {
				++i6;
			}

			if(world.getBlockState(new BlockPos(i3, i4, i5 - 1)).isOf(Blocks.NETHERRACK)) {
				++i6;
			}

			if(world.getBlockState(new BlockPos(i3, i4, i5 + 1)).isOf(Blocks.NETHERRACK)) {
				++i6;
			}

			if(world.getBlockState(new BlockPos(i3, i4 - 1, i5)).isOf(Blocks.NETHERRACK)) {
				++i6;
			}

			int i7 = 0;
			if(world.isAir(new BlockPos(i3 - 1, i4, i5))) {
				++i7;
			}

			if(world.isAir(new BlockPos(i3 + 1, i4, i5))) {
				++i7;
			}

			if(world.isAir(new BlockPos(i3, i4, i5 - 1))) {
				++i7;
			}

			if(world.isAir(new BlockPos(i3, i4, i5 + 1))) {
				++i7;
			}

			if(world.isAir(new BlockPos(i3, i4 - 1, i5))) {
				++i7;
			}

			if(i6 == 4 && i7 == 1) {
				BlockPos pos = new BlockPos(i3, i4, i5);
				world.setBlockState(pos, state, Block.NOTIFY_LISTENERS);
				world.createAndScheduleFluidTick(pos, state.getFluidState().getFluid(), 1);
				//world.scheduledUpdatesAreImmediate = true;
				//this.state.scheduledTick(world, new BlockPos(i3, i4, i5), random);
				//world.scheduledUpdatesAreImmediate = false;
			}

			return true;
		}
	}
}
