package cyborgcabbage.cabbagebeta.gen.beta.noise;

import java.util.Random;

public class MoonPerlin {
	private static int GradientSizeTable = 256;
	private static Random _random;
	private static float[] _gradients = new float[GradientSizeTable * 3];
	private static byte[] _perm = new byte[GradientSizeTable];

	public static void initGenerator(long seed) {
		_random = new Random(seed);
		_random.nextBytes(_perm);
		InitGradients();
	}

	public static float Noise(float x, float y, float z) {
		int ix = (int)Math.floor((double)x);
		float fx0 = x - (float)ix;
		float fx1 = fx0 - 1.0F;
		float wx = Smooth(fx0);
		int iy = (int)Math.floor((double)y);
		float fy0 = y - (float)iy;
		float fy1 = fy0 - 1.0F;
		float wy = Smooth(fy0);
		int iz = (int)Math.floor((double)z);
		float fz0 = z - (float)iz;
		float fz1 = fz0 - 1.0F;
		float wz = Smooth(fz0);
		float vx0 = Lattice(ix, iy, iz, fx0, fy0, fz0);
		float vx1 = Lattice(ix + 1, iy, iz, fx1, fy0, fz0);
		float vy0 = Lerp(wx, vx0, vx1);
		vx0 = Lattice(ix, iy + 1, iz, fx0, fy1, fz0);
		vx1 = Lattice(ix + 1, iy + 1, iz, fx1, fy1, fz0);
		float vy1 = Lerp(wx, vx0, vx1);
		float vz0 = Lerp(wy, vy0, vy1);
		vx0 = Lattice(ix, iy, iz + 1, fx0, fy0, fz1);
		vx1 = Lattice(ix + 1, iy, iz + 1, fx1, fy0, fz1);
		vy0 = Lerp(wx, vx0, vx1);
		vx0 = Lattice(ix, iy + 1, iz + 1, fx0, fy1, fz1);
		vx1 = Lattice(ix + 1, iy + 1, iz + 1, fx1, fy1, fz1);
		vy1 = Lerp(wx, vx0, vx1);
		float vz1 = Lerp(wy, vy0, vy1);
		return Lerp(wz, vz0, vz1);
	}

	private static void InitGradients() {
		for(int i = 0; i < GradientSizeTable; ++i) {
			float z = 1.0F - 2.0F * (float)_random.nextDouble();
			float r = (float)Math.sqrt((double)(1.0F - z * z));
			float theta = 6.2831855F * (float)_random.nextDouble();
			_gradients[i * 3] = r * (float)Math.cos((double)theta);
			_gradients[i * 3 + 1] = r * (float)Math.sin((double)theta);
			_gradients[i * 3 + 2] = z;
		}

	}

	private static int Permutate(int x) {
		int mask = GradientSizeTable - 1;
		return _perm[x & mask];
	}

	private static int Index(int ix, int iy, int iz) {
		return Permutate(ix + Permutate(iy + Permutate(iz)));
	}

	private static float Lattice(int ix, int iy, int iz, float fx, float fy, float fz) {
		int index = Index(ix, iy, iz);
		int g = index * 3;
		g = g < 0 ? -g : g;
		return _gradients[g] * fx + _gradients[g + 1] * fy + _gradients[g + 2] * fz;
	}

	private static float Lerp(float t, float value0, float value1) {
		return value0 + t * (value1 - value0);
	}

	private static float Smooth(float x) {
		return x * x * (3.0F - 2.0F * x);
	}
}
