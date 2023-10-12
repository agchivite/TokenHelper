package dev.sbytmacke.tokenhelper.dto;

public class UserDTO {

    private final String username;
    private final String percentReliable;
    private final String totalBets;

    public UserDTO(String username, String percentReliable, String totalBets) {
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

    public String getTotalBets() {
        return totalBets;
    }
}
