import services.RowCollector;

public class Main {
    public static void main(String[] args) {
       RowCollector rowCollector = new RowCollector();
       rowCollector.collect("data.json");
    }
}