package dev.sbytmacke.tokenhelper.models;

import java.time.LocalDate;
import java.time.LocalTime;


//  Martes 7am-8am -> 20 usuarios en esta franja han apostado100veces ->  da un 60% porque han acertado 60 veces
//  Con un ratio mínimo de 3 personas por franja (si no, no des resultado)
//  % de tíos han acertado más veces en esa franja (individualmente y en grupo)

public class User {
    private final String username;
    private final LocalDate dateBet; // La fecha tenemos que hacer el cálculo para los que corresponden al mismo día de la semana
    private final LocalTime timeBet;
    private final Boolean reliable; // Menos del x5 son malos

    public User(String username, LocalDate dateBet, LocalTime timeBet, Boolean reliable) {
        this.username = username;
        this.dateBet = dateBet;
        this.timeBet = timeBet;
        this.reliable = reliable;
    }

    public String getUsername() {
        return username;
    }

    public LocalDate getDateBet() {
        return dateBet;
    }

    public LocalTime getTimeBet() {
        return timeBet;
    }

    public Boolean getIsReliable() {
        return reliable;
    }
}
