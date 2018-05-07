/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.skywalking.apm.collector.storage.es.dao.armp;

import java.util.HashMap;
import java.util.Map;
import org.apache.skywalking.apm.collector.client.elasticsearch.ElasticSearchClient;
import org.apache.skywalking.apm.collector.core.annotations.trace.GraphComputingMetric;
import org.apache.skywalking.apm.collector.storage.es.MetricTransformUtil;
import org.apache.skywalking.apm.collector.storage.es.base.dao.AbstractPersistenceEsDAO;
import org.apache.skywalking.apm.collector.storage.table.application.ApplicationReferenceMetric;
import org.apache.skywalking.apm.collector.storage.table.application.ApplicationReferenceMetricTable;

/**
 * @author peng-yongsheng
 */
public abstract class AbstractApplicationReferenceMetricEsPersistenceDAO extends AbstractPersistenceEsDAO<ApplicationReferenceMetric> {

    AbstractApplicationReferenceMetricEsPersistenceDAO(ElasticSearchClient client) {
        super(client);
    }

    @Override protected final String timeBucketColumnNameForDelete() {
        return ApplicationReferenceMetricTable.TIME_BUCKET.getName();
    }

    @Override protected final ApplicationReferenceMetric esDataToStreamData(Map<String, Object> source) {
        ApplicationReferenceMetric applicationReferenceMetric = new ApplicationReferenceMetric();
        applicationReferenceMetric.setMetricId((String)source.get(ApplicationReferenceMetricTable.METRIC_ID.getName()));

        applicationReferenceMetric.setFrontApplicationId(((Number)source.get(ApplicationReferenceMetricTable.FRONT_APPLICATION_ID.getName())).intValue());
        applicationReferenceMetric.setBehindApplicationId(((Number)source.get(ApplicationReferenceMetricTable.BEHIND_APPLICATION_ID.getName())).intValue());

        MetricTransformUtil.INSTANCE.esDataToStreamData(source, applicationReferenceMetric);

        applicationReferenceMetric.setSatisfiedCount(((Number)source.get(ApplicationReferenceMetricTable.SATISFIED_COUNT.getName())).longValue());
        applicationReferenceMetric.setToleratingCount(((Number)source.get(ApplicationReferenceMetricTable.TOLERATING_COUNT.getName())).longValue());
        applicationReferenceMetric.setFrustratedCount(((Number)source.get(ApplicationReferenceMetricTable.FRUSTRATED_COUNT.getName())).longValue());

        return applicationReferenceMetric;
    }

    @Override protected final Map<String, Object> esStreamDataToEsData(ApplicationReferenceMetric streamData) {
        Map<String, Object> target = new HashMap<>();
        target.put(ApplicationReferenceMetricTable.METRIC_ID.getName(), streamData.getMetricId());

        target.put(ApplicationReferenceMetricTable.FRONT_APPLICATION_ID.getName(), streamData.getFrontApplicationId());
        target.put(ApplicationReferenceMetricTable.BEHIND_APPLICATION_ID.getName(), streamData.getBehindApplicationId());

        MetricTransformUtil.INSTANCE.esStreamDataToEsData(streamData, target);

        target.put(ApplicationReferenceMetricTable.SATISFIED_COUNT.getName(), streamData.getSatisfiedCount());
        target.put(ApplicationReferenceMetricTable.TOLERATING_COUNT.getName(), streamData.getToleratingCount());
        target.put(ApplicationReferenceMetricTable.FRUSTRATED_COUNT.getName(), streamData.getFrustratedCount());

        return target;
    }

    @GraphComputingMetric(name = "/persistence/get/" + ApplicationReferenceMetricTable.TABLE)
    @Override public final ApplicationReferenceMetric get(String id) {
        return super.get(id);
    }
}
