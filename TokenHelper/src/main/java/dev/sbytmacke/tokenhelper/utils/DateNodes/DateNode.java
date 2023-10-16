package dev.sbytmacke.tokenhelper.utils.DateNodes;

import java.time.LocalDate;
import java.util.UUID;

public class DateNode {
    private final UUID key;
    private final LocalDate date;
    private UUID previousKey;
    private UUID nextKey;

    public DateNode(UUID key, LocalDate date) {
        this.key = key;
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    public UUID getKey() {
        return key;
    }

    public UUID getPreviousKey() {
        return previousKey;
    }

    public void setPreviousKey(UUID previousKey) {
        this.previousKey = previousKey;
    }

    public UUID getNextKey() {
        return nextKey;
    }

    public void setNextKey(UUID nextKey) {
        this.nextKey = nextKey;
    }
}
