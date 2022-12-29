package model;

import java.util.List;
import java.util.Objects;

public class Row {
    private List<String> cols;

    public Row(List<String> cols) {
        this.cols = cols;
    }

    public List<String> getCols() {
        return cols;
    }

    public String getRowMask(){
        StringBuilder mask = new StringBuilder();
        cols.forEach(col -> {
            if(col == null){
                mask.append(0);
            } else {
                mask.append(1);
            }
        });
        return mask.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Row row = (Row) o;
        return cols.equals(row.cols);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cols);
    }

    @Override
    public String toString() {
        return "[ \"" + String.join("\",   \"", cols) + "\" ]";
    }
}
