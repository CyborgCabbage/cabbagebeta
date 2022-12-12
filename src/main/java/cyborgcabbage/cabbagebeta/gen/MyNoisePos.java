package cyborgcabbage.cabbagebeta.gen;

import net.minecraft.world.gen.densityfunction.DensityFunction;

public class MyNoisePos implements DensityFunction.NoisePos {
    public int x = 0;
    public int y = 0;
    public int z = 0;

    @Override
    public int blockX() {
        return x;
    }

    @Override
    public int blockY() {
        return y;
    }

    @Override
    public int blockZ() {
        return z;
    }
}
