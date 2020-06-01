package com.amazon.opendistro.elasticsearch.performanceanalyzer.rca.store.rca.temperature.dimension;

import com.amazon.opendistro.elasticsearch.performanceanalyzer.rca.framework.api.Rca;
import com.amazon.opendistro.elasticsearch.performanceanalyzer.rca.framework.api.flow_units.temperature.DimensionalTemperatureFlowUnit;
import com.amazon.opendistro.elasticsearch.performanceanalyzer.rca.framework.core.temperature.ShardStore;
import com.amazon.opendistro.elasticsearch.performanceanalyzer.rca.framework.core.temperature.TemperatureVector;
import com.amazon.opendistro.elasticsearch.performanceanalyzer.rca.scheduler.FlowUnitOperationArgWrapper;
import com.amazon.opendistro.elasticsearch.performanceanalyzer.rca.store.metric.temperature.byShard.ShardSizeAvgTemperatureCalculator;
import com.amazon.opendistro.elasticsearch.performanceanalyzer.rca.store.metric.temperature.byShard.ShardSizeByShardTemperatureCalculator;
import com.amazon.opendistro.elasticsearch.performanceanalyzer.rca.store.metric.temperature.capacity.ShardSizePeakUsageTemperatureCalculator;
import com.amazon.opendistro.elasticsearch.performanceanalyzer.rca.store.metric.temperature.shardIndependent.DiskUsageShardIndependentTemperatureCalculator;
import com.amazon.opendistro.elasticsearch.performanceanalyzer.rca.store.rca.temperature.DimensionalTemperatureCalculator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShardSizeDimensionTemperatureRca extends Rca<DimensionalTemperatureFlowUnit> {

    private static final Logger LOG = LogManager.getLogger(ShardSizeDimensionTemperatureRca.class);
    // The threshold set here is an initial threshold only.
    // TODO: Update the threshold appropriately after testing so that we assign heat correctly.
    private static final TemperatureVector.NormalizedValue THRESHOLD = new TemperatureVector.NormalizedValue((short) 2);
    private static final int EVALUATION_INTERVAL_IN_S = 5;
    private final ShardSizeByShardTemperatureCalculator SHARD_SIZE_BY_SHARD;
    private final ShardSizeAvgTemperatureCalculator SHARD_SIZE_AVG;
    private final DiskUsageShardIndependentTemperatureCalculator DISK_USAGE;
    private final ShardSizePeakUsageTemperatureCalculator PEAK_SHARD_SIZE_USAGE;
    private final ShardStore SHARD_STORE;

    public ShardSizeDimensionTemperatureRca(final ShardStore shardStore,
                                            final ShardSizeByShardTemperatureCalculator shardSizeByShard,
                                            final ShardSizeAvgTemperatureCalculator shardSizeAvg,
                                            final DiskUsageShardIndependentTemperatureCalculator diskUsageShardIndependent,
                                            final ShardSizePeakUsageTemperatureCalculator shardSizePeakUsage) {
       super(EVALUATION_INTERVAL_IN_S);
       this.SHARD_STORE = shardStore;
       this.SHARD_SIZE_BY_SHARD = shardSizeByShard;
       this.SHARD_SIZE_AVG = shardSizeAvg;
       this.DISK_USAGE = diskUsageShardIndependent;
       this.PEAK_SHARD_SIZE_USAGE = shardSizePeakUsage;
    }

    @Override
    public void generateFlowUnitListFromWire(FlowUnitOperationArgWrapper args) {
        throw new IllegalStateException("This node: [" + name() + "] should not have received flow "
                + "units from remote nodes.");
    }

    @Override
    public DimensionalTemperatureFlowUnit operate() {
        LOG.debug("executing : {}", name());
        DimensionalTemperatureFlowUnit shardSizeTemperatureFlowUnit =
                DimensionalTemperatureCalculator.getTemperatureForDimension(SHARD_STORE,
                        TemperatureVector.Dimension.Shard_Size_In_Bytes, SHARD_SIZE_BY_SHARD, SHARD_SIZE_AVG, DISK_USAGE,
                        PEAK_SHARD_SIZE_USAGE, THRESHOLD);
        LOG.info("Shard Size temperature calculated: {}",
                shardSizeTemperatureFlowUnit.getNodeDimensionProfile());
        return shardSizeTemperatureFlowUnit;
    }
}
