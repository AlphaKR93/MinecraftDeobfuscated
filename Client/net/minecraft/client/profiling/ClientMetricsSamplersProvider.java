/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Set
 *  java.util.function.LongSupplier
 *  java.util.function.Supplier
 */
package net.minecraft.client.profiling;

import com.mojang.blaze3d.systems.TimerQuery;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Set;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.util.profiling.ProfileCollector;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.util.profiling.metrics.MetricSampler;
import net.minecraft.util.profiling.metrics.MetricsSamplerProvider;
import net.minecraft.util.profiling.metrics.profiling.ProfilerSamplerAdapter;
import net.minecraft.util.profiling.metrics.profiling.ServerMetricsSamplersProvider;

public class ClientMetricsSamplersProvider
implements MetricsSamplerProvider {
    private final LevelRenderer levelRenderer;
    private final Set<MetricSampler> samplers = new ObjectOpenHashSet();
    private final ProfilerSamplerAdapter samplerFactory = new ProfilerSamplerAdapter();

    public ClientMetricsSamplersProvider(LongSupplier $$0, LevelRenderer $$1) {
        this.levelRenderer = $$1;
        this.samplers.add((Object)ServerMetricsSamplersProvider.tickTimeSampler($$0));
        this.registerStaticSamplers();
    }

    private void registerStaticSamplers() {
        this.samplers.addAll(ServerMetricsSamplersProvider.runtimeIndependentSamplers());
        this.samplers.add((Object)MetricSampler.create("totalChunks", MetricCategory.CHUNK_RENDERING, this.levelRenderer, LevelRenderer::getTotalChunks));
        this.samplers.add((Object)MetricSampler.create("renderedChunks", MetricCategory.CHUNK_RENDERING, this.levelRenderer, LevelRenderer::countRenderedChunks));
        this.samplers.add((Object)MetricSampler.create("lastViewDistance", MetricCategory.CHUNK_RENDERING, this.levelRenderer, LevelRenderer::getLastViewDistance));
        ChunkRenderDispatcher $$0 = this.levelRenderer.getChunkRenderDispatcher();
        this.samplers.add((Object)MetricSampler.create("toUpload", MetricCategory.CHUNK_RENDERING_DISPATCHING, $$0, ChunkRenderDispatcher::getToUpload));
        this.samplers.add((Object)MetricSampler.create("freeBufferCount", MetricCategory.CHUNK_RENDERING_DISPATCHING, $$0, ChunkRenderDispatcher::getFreeBufferCount));
        this.samplers.add((Object)MetricSampler.create("toBatchCount", MetricCategory.CHUNK_RENDERING_DISPATCHING, $$0, ChunkRenderDispatcher::getToBatchCount));
        if (TimerQuery.getInstance().isPresent()) {
            this.samplers.add((Object)MetricSampler.create("gpuUtilization", MetricCategory.GPU, Minecraft.getInstance(), Minecraft::getGpuUtilization));
        }
    }

    @Override
    public Set<MetricSampler> samplers(Supplier<ProfileCollector> $$0) {
        this.samplers.addAll(this.samplerFactory.newSamplersFoundInProfiler($$0));
        return this.samplers;
    }
}