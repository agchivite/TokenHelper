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
import java.util.List;

public class UserRepositoryImpl implements UserRepository<UserEntity, String> {

    private static final String COLLECTION_NAME = "users_bet";
    private static final String FIELD_USERNAME = "username";
    private static final String FIELD_DATE_BET = "dateBet";
    private static final String FIELD_TIME_BET = "timeBet";
    private static final String FIELD_RELIABLE = "reliable";
    private final DatabaseManager databaseManager;
    Logger logger = LoggerFactory.getLogger(getClass());

    public UserRepositoryImpl(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    private static UserEntity mapDocumentToEntity(Document document) {
        String username = document.getString(FIELD_USERNAME);
        LocalDate dateBet = LocalDate.parse(document.getString(FIELD_DATE_BET));
        String timeBet = document.getString(FIELD_TIME_BET);
        Boolean reliable = document.getBoolean(FIELD_RELIABLE);
        return new UserEntity(username, dateBet, timeBet, reliable);
    }

    @Override
    public UserEntity addItem(UserEntity userEntity) {
        String concatLog = "Adding user " + userEntity;
        logger.info(concatLog);

        // Conectar a la base de datos
        databaseManager.connectDatabase();

        // Obtener la colección de usuarios
        MongoCollection<Document> collection = databaseManager.getDatabase().getCollection(COLLECTION_NAME);

        // Crear un documento a partir del usuario
        Document userDocument = new Document(FIELD_USERNAME, userEntity.getUsername())
                .append(FIELD_DATE_BET, userEntity.getDateBet().toString())
                .append(FIELD_TIME_BET, userEntity.getTimeBet())
                .append(FIELD_RELIABLE, userEntity.getIsReliable());

        // Insertar el documento en la colección
        collection.insertOne(userDocument);

        // Cerrar la conexión
        databaseManager.closeDatabase();

        return userEntity;
    }

    @Override
    public List<UserEntity> findAll() {
        logger.info("Finding all users");

        databaseManager.connectDatabase();

        MongoCollection<Document> collection = databaseManager.getDatabase().getCollection(COLLECTION_NAME);

        // Consulta sin filtro
        Document query = new Document();

        // Itera sobre los resultados de la consulta
        List<UserEntity> usersList = new ArrayList<>();
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
    public List<UserDTO> getAllByTime(String newTime) {
        logger.info("getAllByTime");

        databaseManager.connectDatabase();

        MongoCollection<Document> collection = databaseManager.getDatabase().getCollection(COLLECTION_NAME);

        // Crear un filtro para encontrar documentos con el valor de "timeBet" igual a newTime
        Bson filter = Filters.eq(FIELD_TIME_BET, newTime);
        FindIterable<Document> result = collection.find(filter); // Consulta

        List<UserEntity> usersFiltered = new ArrayList<>();

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
        return userMapper.convertUserEntitiesToDTOs(usersFiltered);
    }

    @Override
    public List<UserDTO> getAllByDateTime(String newTime, LocalDate newDate) {
        logger.info("getAllByDateTime");

        databaseManager.connectDatabase();

        MongoCollection<Document> collection = databaseManager.getDatabase().getCollection(COLLECTION_NAME);

        // Crear un filtro para encontrar documentos con el valor de "timeBet" igual a newTime
        Bson filter = Filters.eq(FIELD_TIME_BET, newTime);
        FindIterable<Document> result = collection.find(filter); // Consulta

        ArrayList<UserEntity> usersFiltered = new ArrayList<>();
        int targetDayOfWeek = newDate.getDayOfWeek().getValue(); // Obtiene el día de la semana de la fecha deseada

        MongoCursor<Document> cursor = result.iterator();
        while (cursor.hasNext()) {
            Document document = cursor.next();
            LocalDate documentDateBet = LocalDate.parse(document.getString(FIELD_DATE_BET));
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
        return userMapper.convertUserEntitiesToDTOs(usersFiltered);
    }


    @Override
    public List<UserDTO> getByUsernameTime(String newUsername, String newTime) {
        logger.info("getByUsernameTime");

        databaseManager.connectDatabase();

        MongoCollection<Document> collection = databaseManager.getDatabase().getCollection(COLLECTION_NAME);

        Bson filter = Filters.and(
                Filters.eq(FIELD_TIME_BET, newTime),
                Filters.eq(FIELD_USERNAME, newUsername)
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

        return userMapper.convertUserEntitiesToDTOs(usersFiltered);
    }

    @Override
    public List<UserDTO> getByUsernameDateTime(String newUsername, String newTime, LocalDate newDate) {
        logger.info("getByUsernameDateTime");

        databaseManager.connectDatabase();

        MongoCollection<Document> collection = databaseManager.getDatabase().getCollection(COLLECTION_NAME);

        Bson filter = Filters.and(
                Filters.eq(FIELD_TIME_BET, newTime),
                Filters.eq(FIELD_USERNAME, newUsername)
        );
        FindIterable<Document> result = collection.find(filter); // Consulta

        ArrayList<UserEntity> usersFiltered = new ArrayList<>();
        int targetDayOfWeek = newDate.getDayOfWeek().getValue(); // Obtiene el día de la semana de la fecha deseada

        MongoCursor<Document> cursor = result.iterator();
        while (cursor.hasNext()) {
            Document document = cursor.next();
            LocalDate documentDateBet = LocalDate.parse(document.getString(FIELD_DATE_BET));
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
        return userMapper.convertUserEntitiesToDTOs(usersFiltered);
    }

    @Override
    public Integer getGlobalTotalBetsByTime(String newTime) {
        logger.info("getGlobalTotalBetsByTime");

        databaseManager.connectDatabase();

        MongoCollection<Document> collection = databaseManager.getDatabase().getCollection(COLLECTION_NAME);

        Bson filter = Filters.and(Filters.eq(FIELD_TIME_BET, newTime));
        long result = collection.countDocuments(filter); // Consulta

        databaseManager.closeDatabase();

        return Math.toIntExact(result);
    }

    @Override
    public Integer getGlobalTotalBetsByDateTime(String newTime, LocalDate newDate) {
        logger.info("getGlobalTotalBetsByDateTime");

        databaseManager.connectDatabase();

        MongoCollection<Document> collection = databaseManager.getDatabase().getCollection(COLLECTION_NAME);

        Bson filter = Filters.and(Filters.eq(FIELD_TIME_BET, newTime));

        FindIterable<Document> result = collection.find(filter); // Consulta

        ArrayList<UserEntity> usersFiltered = new ArrayList<>();
        int targetDayOfWeek = newDate.getDayOfWeek().getValue(); // Obtiene el día de la semana de la fecha deseada

        MongoCursor<Document> cursor = result.iterator();
        while (cursor.hasNext()) {
            Document document = cursor.next();
            LocalDate documentDateBet = LocalDate.parse(document.getString(FIELD_DATE_BET));
            int documentDayOfWeek = documentDateBet.getDayOfWeek().getValue();

            if (documentDayOfWeek == targetDayOfWeek) {
                UserEntity user = mapDocumentToEntity(document);
                usersFiltered.add(user);
            }
        }

        cursor.close();
        databaseManager.closeDatabase();

        return Math.toIntExact(usersFiltered.size());
    }
}
