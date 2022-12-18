package cyborgcabbage.cabbagebeta.gen.beta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cyborgcabbage.cabbagebeta.CabbageBeta;
import cyborgcabbage.cabbagebeta.gen.FeaturesProperty;
import cyborgcabbage.cabbagebeta.gen.beta.biome.BiomeGenBase;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

import java.util.stream.Stream;

public class BetaOverworldBiomeSource extends BiomeSource {
    public static final Codec<BetaOverworldBiomeSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryOps.createRegistryCodec(Registry.BIOME_KEY).forGetter(biomeSource -> null),
            Codec.INT.fieldOf("features").orElse(0).forGetter(b -> b.featuresProperty.getId())
    ).apply(instance, instance.stable(BetaOverworldBiomeSource::new)));
    private BetaChunkGenerator gen;
    private final Registry<Biome> biomeRegistry;
    private final FeaturesProperty featuresProperty;

    public BetaOverworldBiomeSource(Registry<Biome> biomeRegistry, int fp) {
        this(biomeRegistry, FeaturesProperty.byId(fp));
    }
    public BetaOverworldBiomeSource(Registry<Biome> biomeRegistry, FeaturesProperty featuresProperty) {
        super(Stream.concat(Stream.of(
                BiomeGenBase.rainforest,
                BiomeGenBase.swampland,
                BiomeGenBase.seasonalForest,
                BiomeGenBase.forest,
                BiomeGenBase.savanna,
                BiomeGenBase.shrubland,
                BiomeGenBase.taiga,
                BiomeGenBase.desert,
                BiomeGenBase.plains,
                BiomeGenBase.tundra,
                BiomeGenBase.hell,
                BiomeGenBase.sky)
                .map(biomeGenBase -> biomeRegistry.getOrCreateEntry(CabbageBeta.BETA_TO_MODERN_BIOME.get(biomeGenBase))),
                biomeRegistry.streamEntries())
        );
        this.biomeRegistry = biomeRegistry;
        this.featuresProperty = featuresProperty;
    }

    public void setGenerator(BetaChunkGenerator generator){
        this.gen = generator;
    }

    @Override
    protected Codec<? extends BiomeSource> getCodec() {
        return CODEC;
    }

    @Override
    public RegistryEntry<Biome> getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
        if(gen == null) return biomeRegistry.getOrCreateEntry(CabbageBeta.BETA_SKY);
        if (featuresProperty == FeaturesProperty.MODERN) {
            RegistryKey<Biome> b = gen.getModernBiome(BiomeCoords.toBlock(x), BiomeCoords.toBlock(z));
            return biomeRegistry.getOrCreateEntry(b);
        } else {
            BiomeGenBase b = gen.getBiome(BiomeCoords.toBlock(x), BiomeCoords.toBlock(z));
            return biomeRegistry.getOrCreateEntry(CabbageBeta.BETA_TO_MODERN_BIOME.get(b));
        }
    }
}
