package dev.sbytmacke.tokenhelper.repositories;

public interface UserRepository<T, ID> extends Repository<T, ID> {

    int calculateTotalBetsByUsername(ID username);

    String calculatePercentSuccess(T userEntity, int totalBets);
}

