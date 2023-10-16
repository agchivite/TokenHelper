package dev.sbytmacke.tokenhelper.utils.DateNodes;

import javafx.scene.control.DatePicker;

import java.time.LocalDate;
import java.util.*;

public class MapNodes {
    private final Map<UUID, DateNode> dateNodes = new HashMap<>();
    private final List<DateNode> dateList = new ArrayList<>();
    private int currentDateIndex = -1;

    public void addDate(LocalDate date) {
        UUID key = generateDateKey();
        DateNode newNode = new DateNode(key, date);

        if (currentDateIndex == -1) {
            currentDateIndex = 0;
        } else if (currentDateIndex < dateList.size() - 1) {
            // Update nextKey of the current node
            DateNode current = dateList.get(currentDateIndex);
            current.setNextKey(key);

            // Update previousKey of the new node
            newNode.setPreviousKey(current.getKey());
        }

        // Add the new node to the list and map
        dateList.add(newNode);
        dateNodes.put(key, newNode);
        currentDateIndex++;
    }

    public Boolean navigateToPreviousDate(DatePicker datePickerFilter) {
        if (currentDateIndex > 0) {
            DateNode previousNode = dateList.get(currentDateIndex - 1);
            currentDateIndex--;

            // Set the current date in the DatePicker
            datePickerFilter.setValue(previousNode.getDate());
            return true;
        }
        return false;
    }

    public Boolean navigateToNextDate(DatePicker datePickerFilter) {
        if (currentDateIndex >= 0 && currentDateIndex < dateList.size() - 1) {
            DateNode nextNode = dateList.get(currentDateIndex + 1);
            currentDateIndex++;

            // Set the current date in the DatePicker
            datePickerFilter.setValue(nextNode.getDate());
            return true;
        }
        return false;
    }

    private UUID generateDateKey() {
        return UUID.randomUUID();
    }

    public int getCurrentDateIndex() {
        return currentDateIndex;
    }

    public void setCurrentDateIndex(int currentDateIndex) {
        this.currentDateIndex = currentDateIndex;
    }
}
