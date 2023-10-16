package dev.sbytmacke.tokenhelper.dto;

public class UserDTO {

    private final int totalBets;
    private final double percentReliable;
    private final int totalSuccess;
    private String username;

    public UserDTO(String username, double percentReliable, int totalBets, int totalSuccess) {
        this.username = username;
        this.percentReliable = percentReliable;
        this.totalBets = totalBets;
        this.totalSuccess = totalSuccess;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String newUsername) {
        this.username = newUsername;
    }

    public double getPercentReliable() {
        String formattedValue = String.format("%.2f", percentReliable);
        formattedValue = formattedValue.replace(',', '.');
        return Double.parseDouble(formattedValue);
    }

    public int getTotalBets() {
        return totalBets;
    }

    public int getTotalSuccess() {
        return totalSuccess;
    }
}
