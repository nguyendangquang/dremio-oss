/*
 * Copyright (C) 2017 Dremio Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dremio.exec.store;

import org.apache.arrow.vector.types.pojo.Field;

import com.dremio.common.types.Types;
import com.dremio.common.types.TypeProtos.MinorType;
import com.dremio.common.util.MajorTypeHelper;
import com.dremio.exec.record.BatchSchema;
import com.dremio.exec.record.VectorAccessible;
import com.dremio.exec.record.BatchSchema.SelectionVectorMode;

import java.io.IOException;

/**
 * Record writer interface for writing a record batch to persistent storage.
 */
public interface RecordWriter extends AutoCloseable {

  BatchSchema SCHEMA = BatchSchema.newBuilder()
      .addField(MajorTypeHelper.getFieldForNameAndMajorType("Fragment", Types.optional(MinorType.VARCHAR)))
      .addField(MajorTypeHelper.getFieldForNameAndMajorType("Records", Types.optional(MinorType.BIGINT)))
      .addField(MajorTypeHelper.getFieldForNameAndMajorType("Path", Types.optional(MinorType.VARCHAR)))
      .addField(MajorTypeHelper.getFieldForNameAndMajorType("Metadata", Types.optional(MinorType.VARBINARY)))
      .addField(MajorTypeHelper.getFieldForNameAndMajorType("Partition", Types.optional(MinorType.INT)))
      .setSelectionVectorMode(SelectionVectorMode.NONE)
      .build();

  Field FRAGMENT = SCHEMA.getColumn(0);
  Field RECORDS = SCHEMA.getColumn(1);
  Field PATH = SCHEMA.getColumn(2);
  Field METADATA = SCHEMA.getColumn(3);
  Field PARTITION = SCHEMA.getColumn(4);


  /**
   *
   * @param incoming
   * @param listener
   */
  void setup(final VectorAccessible incoming, OutputEntryListener listener) throws IOException;

  /**
   * Write the given record batch. It is callers responsibility to release the
   * batch.
   *
   * @return Number of records written.
   */

  void startPartition(WritePartition partition) throws Exception;

  /**
   * Write the given record batch. It is callers responsibility to release the
   * batch.
   * @param partitionNumber The partition number associated with this write.
   * @param offset The offset
   * @param length
   * @return
   * @throws IOException
   */
  int writeBatch(int offset, int length) throws IOException;

  /**
   * Stop writing, delete any written contents and cleanup.
   *
   * @throws IOException
   */
  void abort() throws IOException;

  /**
   * Listener that is informed of any output entries that have been returned.
   * Depending on the source, this could be files, a database path, etc.
   */
  public interface OutputEntryListener {
    void recordsWritten(long recordCount, String path, byte[] metadata, Integer partitionNumber);
  }


}
