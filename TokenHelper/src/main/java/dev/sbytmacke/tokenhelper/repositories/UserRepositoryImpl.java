package dev.sbytmacke.tokenhelper.repositories;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import dev.sbytmacke.tokenhelper.dto.UserDTO;
import dev.sbytmacke.tokenhelper.mappers.UserMapper;
import dev.sbytmacke.tokenhelper.models.UserEntity;
import dev.sbytmacke.tokenhelper.services.database.DatabaseManager;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;

public class UserRepositoryImpl implements UserRepository<UserEntity, String> {

    private final DatabaseManager databaseManager;
    Logger logger = LoggerFactory.getLogger(getClass());

    public UserRepositoryImpl(DatabaseManager databaseManager) {

        this.databaseManager = databaseManager;
    }

    private static UserEntity mapDocumentToEntity(Document document) {
        String username = document.getString("username");
        LocalDate dateBet = LocalDate.parse(document.getString("dateBet"));
        String timeBet = document.getString("timeBet");
        Boolean reliable = document.getBoolean("reliable");
        UserEntity userEntity = new UserEntity(username, dateBet, timeBet, reliable);
        return userEntity;
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
    public ArrayList<UserEntity> findAll() {
        logger.info("Finding all users");

        databaseManager.connectDatabase();

        MongoCollection<Document> collection = databaseManager.getDatabase().getCollection("users_bet");

        // Consulta sin filtro
        Document query = new Document();

        // Itera sobre los resultados de la consulta
        ArrayList<UserEntity> usersList = new ArrayList<>();
        MongoCursor<Document> cursor = collection.find(query).iterator();
        while (cursor.hasNext()) {
            Document document = cursor.next();
            UserEntity userEntity = mapDocumentToEntity(document);
            usersList.add(userEntity);
        }

        cursor.close();
        databaseManager.closeDatabase();

        return usersList;
    }

    @Override
    public ArrayList<UserDTO> getAllByTime(String newTime) {
        logger.info("getAllByTime");

        databaseManager.connectDatabase();

        MongoCollection<Document> collection = databaseManager.getDatabase().getCollection("users_bet");

        // Crear un filtro para encontrar documentos con el valor de "timeBet" igual a newTime
        Bson filter = Filters.eq("timeBet", newTime);
        FindIterable<Document> result = collection.find(filter); // Consulta

        ArrayList<UserEntity> usersFiltered = new ArrayList<>();
        MongoCursor<Document> cursor = result.iterator();
        while (cursor.hasNext()) {
            Document document = cursor.next();
            UserEntity user = mapDocumentToEntity(document);
            usersFiltered.add(user);
        }

        cursor.close();
        databaseManager.closeDatabase();

        // Mapeamos a los usuarios filtrados
        UserMapper userMapper = new UserMapper();
        ArrayList<UserDTO> usersDTO = userMapper.convertUserEntitiesToDTOs(usersFiltered);

        return usersDTO;
    }

    @Override
    public ArrayList<UserDTO> getAllByDateTime(String newTime, LocalDate newDate) {
        logger.info("getAllByDateTime");

        databaseManager.connectDatabase();

        MongoCollection<Document> collection = databaseManager.getDatabase().getCollection("users_bet");

        // Crear un filtro para encontrar documentos con el valor de "timeBet" igual a newTime
        Bson filter = Filters.eq("timeBet", newTime);
        FindIterable<Document> result = collection.find(filter); // Consulta

        ArrayList<UserEntity> usersFiltered = new ArrayList<>();
        int targetDayOfWeek = newDate.getDayOfWeek().getValue(); // Obtiene el día de la semana de la fecha deseada

        MongoCursor<Document> cursor = result.iterator();
        while (cursor.hasNext()) {
            Document document = cursor.next();
            LocalDate documentDateBet = LocalDate.parse(document.getString("dateBet"));
            int documentDayOfWeek = documentDateBet.getDayOfWeek().getValue();

            if (documentDayOfWeek == targetDayOfWeek) {
                UserEntity user = mapDocumentToEntity(document);
                usersFiltered.add(user);
            }
        }

        cursor.close();
        databaseManager.closeDatabase();

        // Mapeamos a los usuarios filtrados
        UserMapper userMapper = new UserMapper();
        ArrayList<UserDTO> usersDTO = userMapper.convertUserEntitiesToDTOs(usersFiltered);

        return usersDTO;
    }

    @Override
    public ArrayList<UserDTO> getByUsernameTime(String newUsername, String newTime) {
        logger.info("getByUsernameTime");

        databaseManager.connectDatabase();

        MongoCollection<Document> collection = databaseManager.getDatabase().getCollection("users_bet");

        Bson filter = Filters.and(
                Filters.eq("timeBet", newTime),
                Filters.eq("username", newUsername)
        );
        FindIterable<Document> result = collection.find(filter); // Consulta

        ArrayList<UserEntity> usersFiltered = new ArrayList<>();
        MongoCursor<Document> cursor = result.iterator();
        while (cursor.hasNext()) {
            Document document = cursor.next();
            UserEntity user = mapDocumentToEntity(document);
            usersFiltered.add(user);
        }

        cursor.close();
        databaseManager.closeDatabase();

        // Mapeamos a los usuarios filtrados
        UserMapper userMapper = new UserMapper();
        ArrayList<UserDTO> usersDTO = userMapper.convertUserEntitiesToDTOs(usersFiltered);

        return usersDTO;
    }

    @Override
    public ArrayList<UserDTO> getByUsernameDateTime(String newUsername, String newTime, LocalDate newDate) {
        logger.info("getByUsernameDateTime");

        databaseManager.connectDatabase();

        MongoCollection<Document> collection = databaseManager.getDatabase().getCollection("users_bet");

        Bson filter = Filters.and(
                Filters.eq("timeBet", newTime),
                Filters.eq("username", newUsername)
        );
        FindIterable<Document> result = collection.find(filter); // Consulta

        ArrayList<UserEntity> usersFiltered = new ArrayList<>();
        int targetDayOfWeek = newDate.getDayOfWeek().getValue(); // Obtiene el día de la semana de la fecha deseada

        MongoCursor<Document> cursor = result.iterator();
        while (cursor.hasNext()) {
            Document document = cursor.next();
            LocalDate documentDateBet = LocalDate.parse(document.getString("dateBet"));
            int documentDayOfWeek = documentDateBet.getDayOfWeek().getValue();

            if (documentDayOfWeek == targetDayOfWeek) {
                UserEntity user = mapDocumentToEntity(document);
                usersFiltered.add(user);
            }
        }

        cursor.close();
        databaseManager.closeDatabase();

        // Mapeamos a los usuarios filtrados
        UserMapper userMapper = new UserMapper();
        ArrayList<UserDTO> usersDTO = userMapper.convertUserEntitiesToDTOs(usersFiltered);

        return usersDTO;
    }
}
