package org.apache.spark.sql.mlsql.sources.hbase.wal.io;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.sql.mlsql.sources.hbase.wal.RawHBaseWALEvent;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 12/12/2019 WilliamZhu(allwefantasy@gmail.com)
 */
public class PutWriter extends AbstractEventWriter {
    @Override
    public List<String> writeEvent(RawHBaseWALEvent event) {
        List<String> items = new ArrayList<>();
        try {
            StringWriter writer = new StringWriter();
            startJson(writer, event);

            jsonGenerator.writeArrayFieldStart("rows");
            jsonGenerator.writeStartObject();
            Put put = event.put();
            jsonGenerator.writeObjectField("rowkey", Bytes.toString(put.getRow()));
            for (Map.Entry<byte[], List<Cell>> entry : put.getFamilyCellMap().entrySet()) {
                for (Cell cell : entry.getValue()) {
                    String f = Bytes.toString(cell.getFamilyArray());
                    String col = Bytes.toString(cell.getQualifierArray());
                    jsonGenerator.writeObjectField(f + ":" + col, Bytes.toString(cell.getValueArray()));
                }
            }

            jsonGenerator.writeEndObject();
            jsonGenerator.writeEndArray();

            endJson();
            items.add(writer.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return items;

    }
}
