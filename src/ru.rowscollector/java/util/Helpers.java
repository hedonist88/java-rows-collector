package util;

import model.GroupsContainer;
import java.util.ArrayList;
import java.util.List;

public class Helpers {

    private Helpers() { }

    /**
     *
     * @param subset
     * @param input
     * @param output
     * @param index
     * @param <T>
     *     Из набора input возвращает все возможные сочения массивов в subset
     *     {001, 011, 111} ->
     *     {{001}, {001, 011}, {001, 111}, {011}, ...}
     */
    public static <T> void getSubsets(List<List<T>> subset,
                                      ArrayList<T> input, ArrayList<T> output, int index){
        if(index == input.size()){
            subset.add(output);
            return;
        }
        getSubsets(subset, input, new ArrayList<>(output), index + 1);
        output.add(input.get(index));
        getSubsets(subset, input, new ArrayList<>(output), index + 1);
    }

    /**
     *
     * @param index
     * @param containers
     * @return
     * @param <T>
     *     Из контейнеров списков возвращает все возможные сочетания елементов
     *     {
     *         {row1, row2},
     *         {row3, row4}
     *     }
     *     ->
     *     {
     *         {row1, row3},
     *         {row1, row4},
     *         {row2, row3},
     *         {row2, row4}
     *     }
     */
    public static <T> List<List<T>> getCombinations(int index, List<GroupsContainer<T>> containers) {
        if (index == containers.size()) {
            List<List<T>> combinations = new ArrayList<List<T>>();
            combinations.add(new ArrayList<>());
            return combinations;
        }
        List<List<T>> combinations = new ArrayList<List<T>>();
        GroupsContainer<T> container = containers.get(index);
        List<T> containerItemList = container.getItems();
        List<List<T>> suffixes = getCombinations(index + 1, containers);
        int size = containerItemList.size();
        for (int i = 0; i < size; i++) {
            T containerItem = containerItemList.get(i);
            if (suffixes != null) {
                for (List<T> suffix : suffixes) {
                    List<T> nextCombination = new ArrayList<>();
                    nextCombination.add(containerItem);
                    nextCombination.addAll(suffix);
                    combinations.add(nextCombination);
                }
            }
        }
        return combinations;
    }
}
