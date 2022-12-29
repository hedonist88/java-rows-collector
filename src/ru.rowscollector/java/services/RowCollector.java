package services;

import error.RowCollectorException;
import model.GroupsContainer;
import model.Row;
import org.json.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import util.Helpers;

import java.io.FileReader;
import java.io.IOException;

import java.util.*;

public class RowCollector {
    private Map<String, Set<Row>> maskedRows = new HashMap<>();
    private int rowLength = 0;

    public void collect(String filename){
        readFile(filename);
    }

    private void readFile(String filename){
        JSONParser jsonParser = new JSONParser();
        try {
            Object jsonObject = jsonParser.parse(new FileReader(
                    getClass().getClassLoader().getResource(filename).getPath()
            ));
            JSONArray jsonArray = new JSONArray(jsonObject.toString());
            if(jsonArray.length() <= 1) throw new RowCollectorException("Few lines to match");
            jsonArray.forEach(str -> {
                makeRow(str.toString());
            });
            collectRows();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void makeRow(String json){
        JSONArray jsonStrArray = new JSONArray(json);
        List<String> params = new LinkedList<>();
        if(rowLength > 0) {
            if(jsonStrArray.length() != rowLength) throw new RowCollectorException("Invalid number of items");
        }
        rowLength = jsonStrArray.length();
        jsonStrArray.forEach(param -> {
            if("null".equals(param.toString())){
                params.add(null);
            } else {
                params.add(param.toString());
            }
        });
        saveRow(new Row(params));
    }

    private void saveRow(Row row){
        String rowMask = row.getRowMask();
        if(maskedRows.containsKey(rowMask)){
            maskedRows.get(rowMask).add(row);
        } else {
            Set<Row> set = new HashSet<>();
            set.add(row);
            maskedRows.put(rowMask, set);
        }
    }

    private void collectRows(){
        RowCollectorResult result = new RowCollectorResult();
        List<List<String>> filteredSubsetsMask = getSubsetMasks();
        for(int i = 0; i < filteredSubsetsMask.size(); i++){
            List<GroupsContainer<Row>> containers = new ArrayList<>();
            for(int j = 0; j < filteredSubsetsMask.get(i).size(); j++){
                GroupsContainer<Row> container = new GroupsContainer();
                container.setItems(new ArrayList<>(maskedRows.get(filteredSubsetsMask.get(i).get(j))));
                containers.add(container);
            }
            List<List<Row>> combinations = Helpers.getCombinations(0, containers);
            for(int k = 0; k < combinations.size(); k++) {
                saveReportRow(result, combinations.get(k));
            }
        }
        result.saveToFile();
    }

    private void saveReportRow(RowCollectorResult result, List<Row> rows){
        List<String> params = new ArrayList<>();
        for (int i = 0; i < rowLength; i++){
            for (Row row:rows) {
                if(row.getCols().get(i) != null){
                    params.add(row.getCols().get(i));
                    break;
                }
            }
        }
        result.addRow(new Row(params));
    }

    private List<List<String>> getSubsetMasks(){
        List<List<String>> subsetMasks = new ArrayList<>();
        Helpers.getSubsets(subsetMasks, new ArrayList<>(maskedRows.keySet()), new ArrayList<>(), 0);
        return getMatchingMasks(subsetMasks);
    }

    /**
     *
     * @param input
     * @return
     * Поиск всех совпадающих масок
     * {100, 011, 111, 101} -> {{100,011}, {111}}
     */
    private List<List<String>> getMatchingMasks(List<List<String>> input){
        List<List<String>> result = new ArrayList<>();
        for(int i = 0; i < input.size(); i++){
            if(input.get(i).size() == 1 && !input.get(i).get(0).contains("0")){
                result.add(input.get(i));
            } else if (input.get(i).size() > 1){
                String[] mask = input.get(i).get(0).split("");
                for(int j = 1; j < input.get(i).size(); j++){
                    String[] nextMask = input.get(i).get(j).split("");
                    for (int c = 0; c < mask.length; c++){
                        if(mask[c].equals("0") && nextMask[c].equals("1")){
                            mask[c] = "1";
                        } else if(mask[c].equals("1") && nextMask[c].equals("1")){
                            mask[c] = "2";
                        }
                    }
                }
                Boolean match = true;
                for (int k = 0; k < mask.length; k++){
                    if(mask[k].equals("0") || mask[k].equals("2")){
                        match = false;
                        break;
                    }
                }
                if(match){
                    result.add(input.get(i));
                }
            }
        }
        return result;
    }
}
