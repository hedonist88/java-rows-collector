package services;

import model.Row;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class RowCollectorResult {
    private Set<Row> rows = new HashSet<>();

    public void addRow(Row row) {
        rows.add(row);
    }

    public void saveToFile(){
        //rows.forEach(row -> System.out.println(row));
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("result.json"));
            writer.write("[\n");
            int cnt = 1;
            for (Row row:rows) {
                writer.write("  " + row.toString());
                if (rows.size() != cnt) {
                    writer.write(",\n");
                }
                cnt++;
            }
            writer.write("\n]");
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
