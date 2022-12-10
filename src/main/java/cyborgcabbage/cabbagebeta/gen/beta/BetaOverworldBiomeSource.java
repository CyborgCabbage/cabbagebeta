package cyborgcabbage.cabbagebeta.gen.beta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cyborgcabbage.cabbagebeta.CabbageBeta;
import cyborgcabbage.cabbagebeta.gen.beta.biome.BetaBiomeProvider;
import cyborgcabbage.cabbagebeta.gen.beta.biome.BiomeGenBase;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

import java.util.stream.Stream;

public class BetaOverworldBiomeSource extends BiomeSource {
    public static final Codec<BetaOverworldBiomeSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryOps.createRegistryCodec(Registry.BIOME_KEY).forGetter(biomeSource -> null),
            Codec.BOOL.fieldOf("substitute_biomes").orElse(false).forGetter(BetaOverworldBiomeSource::getSubstituteBiomes)
    ).apply(instance, instance.stable(BetaOverworldBiomeSource::new)));
    private BetaBiomeProvider gen;
    private final Registry<Biome> biomeRegistry;
    private final boolean substituteBiomes;

    protected BetaOverworldBiomeSource(Registry<Biome> biomeRegistry, boolean substituteBiomes) {
        super(Stream.of(
                BiomeGenBase.rainforest,
                BiomeGenBase.swampland,
                BiomeGenBase.seasonalForest,
                BiomeGenBase.forest,
                BiomeGenBase.savanna,
                BiomeGenBase.shrubland,
                BiomeGenBase.taiga,
                BiomeGenBase.desert,
                BiomeGenBase.plains,
                BiomeGenBase.iceDesert,
                BiomeGenBase.tundra,
                BiomeGenBase.hell,
                BiomeGenBase.sky)
                .map(biomeGenBase -> {
                    if(substituteBiomes){
                        return biomeRegistry.getOrCreateEntry(CabbageBeta.BETA_TO_SUBSTITUTE_BIOME.get(biomeGenBase));
                    }else{
                        return biomeRegistry.getOrCreateEntry(CabbageBeta.BETA_TO_MODERN_BIOME.get(biomeGenBase));
                    }
                })
        );
        this.biomeRegistry = biomeRegistry;
        this.substituteBiomes = substituteBiomes;
    }

    public void setGenerator(BetaBiomeProvider generator){
        this.gen = generator;
    }

    @Override
    protected Codec<? extends BiomeSource> getCodec() {
        return CODEC;
    }

    @Override
    public RegistryEntry<Biome> getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
        if(gen == null) return biomeRegistry.getOrCreateEntry(CabbageBeta.BETA_ICE_DESERT);
        BiomeGenBase b = gen.getBiome(BiomeCoords.toBlock(x), BiomeCoords.toBlock(z));
        if(substituteBiomes){
            return biomeRegistry.getOrCreateEntry(CabbageBeta.BETA_TO_SUBSTITUTE_BIOME.get(b));
        }else{
            return biomeRegistry.getOrCreateEntry(CabbageBeta.BETA_TO_MODERN_BIOME.get(b));
        }
    }

    public boolean getSubstituteBiomes() {
        return substituteBiomes;
    }
}
