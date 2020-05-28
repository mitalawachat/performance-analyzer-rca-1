/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 */

package com.amazon.opendistro.elasticsearch.performanceanalyzer.rca.framework.core;

import com.amazon.opendistro.elasticsearch.performanceanalyzer.grpc.FlowUnitMessage;
import com.google.common.annotations.VisibleForTesting;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.protobuf.GeneratedMessageV3;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jooq.Field;
import org.jooq.Record;

public abstract class GenericSummary {

  public List<GenericSummary> getNestedSummaryList() {
    return new ArrayList<>();
  };

  /**
   * get the list of Summary Class object for the nested summary list
   * this is to de-serialize summary object from the SQL tables
   * @return list of Summary Class object
   */
  public List<String> getNestedSummaryTables() {
    return null;
  }

  public GenericSummary appendNestedSummary(String summaryTable, Record record) {
    return null;
  }

  public abstract <T extends GeneratedMessageV3> T buildSummaryMessage();

  public abstract void buildSummaryMessageAndAddToFlowUnit(FlowUnitMessage.Builder messageBuilder);

  //get the name of SQL table for each summary
  public abstract String getTableName();

  public abstract List<Field<?>> getSqlSchema();

  public abstract List<Object> getSqlValue();

  /**
   * convert the summary object to Gson object
   */
  public abstract JsonElement toJson();

  /**
   * convert summary list into a json array
   * @return JsonArray object
   */
  public JsonArray nestedSummaryListToJson(List<? extends GenericSummary> nestedSummaryList) {
    JsonArray nestedSummaryArray = new JsonArray();
    if (!nestedSummaryList.isEmpty()) {
      nestedSummaryList.forEach(
          summary -> {
            nestedSummaryArray.add(summary.toJson());
          }
      );
    }
    return nestedSummaryArray;
  }
}
