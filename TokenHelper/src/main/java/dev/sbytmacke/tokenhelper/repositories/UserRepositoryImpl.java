package dev.sbytmacke.tokenhelper.repositories;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import dev.sbytmacke.tokenhelper.models.UserEntity;
import dev.sbytmacke.tokenhelper.services.database.DatabaseManager;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;

public class UserRepositoryImpl implements UserRepository<UserEntity, String> {

    private final DatabaseManager databaseManager;
    Logger logger = LoggerFactory.getLogger(getClass());

    public UserRepositoryImpl(DatabaseManager databaseManager) {

        this.databaseManager = databaseManager;
    }

    @Override
    public UserEntity addItem(UserEntity userEntity) {
        logger.info("Adding user " + userEntity);

        // Conectar a la base de datos
        databaseManager.connectDatabase();

        // Obtener la colección de usuarios
        MongoCollection<Document> collection = databaseManager.getDatabase().getCollection("users_bet");

        // Crear un documento a partir del usuario
        Document userDocument = new Document("username", userEntity.getUsername())
                .append("dateBet", userEntity.getDateBet().toString())
                .append("timeBet", userEntity.getTimeBet())
                .append("reliable", userEntity.getIsReliable());

        // Insertar el documento en la colección
        collection.insertOne(userDocument);

        // Cerrar la conexión
        databaseManager.closeDatabase();

        return userEntity;
    }

    @Override
    public UserEntity findById(String username) {
        logger.info("Finding user by id " + username);
        return null;
    }

    @Override
    public ArrayList<UserEntity> findAll() {
        logger.info("Finding all users");

        databaseManager.connectDatabase();

        MongoCollection<Document> collection = databaseManager.getDatabase().getCollection("users_bet");

        // Construye una consulta (por ejemplo, encontrar todos los documentos)
        Document query = new Document();

        // Ejecuta la consulta
        MongoCursor<Document> cursor = collection.find(query).iterator();

        // Itera sobre los resultados de la consulta
        ArrayList<UserEntity> usersList = new ArrayList<>();
        while (cursor.hasNext()) {
            Document document = cursor.next();

            // Extraer valores del documento y crear una instancia de UserEntity
            String username = document.getString("username");
            LocalDate dateBet = LocalDate.parse(document.getString("dateBet"));
            String timeBet = document.getString("timeBet");
            Boolean reliable = document.getBoolean("reliable");

            UserEntity userEntity = new UserEntity(username, dateBet, timeBet, reliable);

            usersList.add(userEntity);
        }

        // Cierra el cursor y la conexión
        cursor.close();
        databaseManager.closeDatabase();

        return usersList;
    }

    @Override
    public int calculateTotalBetsByUsername(String username) {
        logger.info("Calculating total bets for username: " + username);

        // Conectar a la base de datos
        databaseManager.connectDatabase();

        // Obtener la colección de usuarios
        MongoCollection<Document> collection = databaseManager.getDatabase().getCollection("users_bet");

        // Crear un filtro para buscar documentos con el nombre de usuario especificado
        Bson filter = Filters.eq("username", username);

        // Realizar la consulta para contar el número de documentos que coinciden con el filtro
        long totalCount = collection.countDocuments(filter);

        // Cerrar la conexión
        databaseManager.closeDatabase();

        // Devolver el total como entero
        return Integer.parseInt(String.valueOf(totalCount));
    }

    @Override
    public String calculatePercentSuccess(UserEntity userEntity, int totalBets) {
        databaseManager.connectDatabase();

        MongoCollection<Document> collection = databaseManager.getDatabase().getCollection("users_bet");

        // Realizar una consulta para contar el número de apuestas exitosas del usuario
        double numReliable = collection.countDocuments(new Document("username", userEntity.getUsername()).append("reliable", true));
        // Calcular el porcentaje de éxito
        String percent;
        if (totalBets > 0) {
            double percentValue = (numReliable / (double) totalBets) * 100;
            DecimalFormat df = new DecimalFormat("#.##"); // Establecemos el formato a dos decimales
            percent = df.format(percentValue); // Redondeamos el valor y se convierte a String
        } else {
            percent = "No hay datos";
        }

        databaseManager.closeDatabase();
        return percent;
    }
}
