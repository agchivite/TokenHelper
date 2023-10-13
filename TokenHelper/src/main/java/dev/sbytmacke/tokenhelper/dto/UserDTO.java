package dev.sbytmacke.tokenhelper.dto;

public class UserDTO {

    private final String username;
    private final String totalBets;
    private final String percentReliable;
    private final int totalSuccess;

    public UserDTO(String username, String percentReliable, String totalBets, int totalSuccess) {
        this.username = username;
        this.percentReliable = percentReliable;
        this.totalBets = totalBets;
        this.totalSuccess = totalSuccess;
    }

    public String getUsername() {
        return username;
    }

    public double getPercentReliable() {
        return Double.parseDouble(percentReliable);
    }

    public String getTotalBets() {
        return totalBets;
    }

    public int getTotalSuccess() {
        return totalSuccess;
    }

}
