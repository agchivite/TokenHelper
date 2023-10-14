package dev.sbytmacke.tokenhelper.dto;

public class UserDTO {

    private final String username;
    private final String totalBets;
    private final double percentReliable;
    private final int totalSuccess;

    public UserDTO(String username, double percentReliable, String totalBets, int totalSuccess) {
        this.username = username;
        this.percentReliable = percentReliable;
        this.totalBets = totalBets;
        this.totalSuccess = totalSuccess;
    }

    public String getUsername() {
        return username;
    }

    public double getPercentReliable() {
        String formattedValue = String.format("%.2f", percentReliable);
        formattedValue = formattedValue.replace(',', '.');
        return Double.parseDouble(formattedValue);
    }

    public String getTotalBets() {
        return totalBets;
    }

    public int getTotalSuccess() {
        return totalSuccess;
    }

}
