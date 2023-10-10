package dev.sbytmacke.tokenhelper.dto;

public class UserDTO {

    private final String username;
    private final String percentReliable;
    private final int totalBets;

    public UserDTO(String username, String percentReliable, int totalBets) {
        this.username = username;
        this.percentReliable = percentReliable;
        this.totalBets = totalBets;
    }

    public String getUsername() {
        return username;
    }

    public String getPercentReliable() {
        return percentReliable;
    }

    public int getTotalBets() {
        return totalBets;
    }
}
