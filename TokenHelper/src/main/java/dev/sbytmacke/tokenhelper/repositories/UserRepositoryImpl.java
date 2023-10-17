package dev.sbytmacke.tokenhelper.repositories;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
                .append(FIELD_RELIABLE, userEntity.getReliable());

        // Insertar el documento en la colección
        collection.insertOne(userDocument);

        // Cerrar la conexión
        databaseManager.closeDatabase();

        return userEntity;
    }

    @Override
    public List<UserDTO> getAll() {
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

        // Mapeamos a los usuarios filtrados
        UserMapper userMapper = new UserMapper();
        return userMapper.convertUserEntitiesToDTOs(usersList);
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
    public List<UserDTO> getAllByDate(LocalDate newDate) {
        logger.info("getAllByDate");

        databaseManager.connectDatabase();

        MongoCollection<Document> collection = databaseManager.getDatabase().getCollection(COLLECTION_NAME);

        FindIterable<Document> result = collection.find(); // Todos los documentos

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
    public List<UserEntity> getAllEntity() {
        logger.info("Finding all users entity");

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
    public List<UserEntity> getAllEntityByTime(String newTime) {
        logger.info("getAllEntityByTime");

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

        return usersFiltered;
    }

    @Override
    public List<UserEntity> getAllEntityByDate(LocalDate newDate) {
        logger.info("getAllEntityByDate");

        databaseManager.connectDatabase();

        MongoCollection<Document> collection = databaseManager.getDatabase().getCollection(COLLECTION_NAME);

        Bson filter = Filters.eq(FIELD_DATE_BET, newDate.toString());
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

        return usersFiltered;
    }

    @Override
    public List<UserEntity> getAllEntityByDateTime(String newTime, LocalDate newDate) {

        databaseManager.connectDatabase();

        MongoCollection<Document> collection = databaseManager.getDatabase().getCollection(COLLECTION_NAME);

        Bson filter = Filters.and(
                Filters.eq(FIELD_TIME_BET, newTime),
                Filters.eq(FIELD_DATE_BET, newDate.toString())
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

        return usersFiltered;
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

    @Override
    public List<String> getAllUsernamesWithoutRepeat() {
        logger.info("getAllUsernames");

        databaseManager.connectDatabase();

        MongoCollection<Document> collection = databaseManager.getDatabase().getCollection(COLLECTION_NAME);

        Set<String> usernames = new HashSet<>();

        // Consulta para obtener todos los documentos
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                String username = document.getString(FIELD_USERNAME);
                usernames.add(username);
            }
        }

        // Convierte el HashSet en una List
        return new ArrayList<>(usernames);
    }

    @Override
    public Integer getGlobalTotalBetsByDate(LocalDate newDate) {
        logger.info("getGlobalTotalBetsByDate");

        databaseManager.connectDatabase();

        MongoCollection<Document> collection = databaseManager.getDatabase().getCollection(COLLECTION_NAME);

        FindIterable<Document> result = collection.find(); // Todos los documentos

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

    public void deleteItem(UserEntity item) {
        logger.info("deleteItem");

        databaseManager.connectDatabase();

        MongoCollection<Document> collection = databaseManager.getDatabase().getCollection(COLLECTION_NAME);

        // Construir un filtro basado en todos los campos del objeto item
        Bson filter = Filters.and(
                Filters.eq(FIELD_TIME_BET, item.getTimeBet()),
                Filters.eq(FIELD_DATE_BET, item.getDateBet().toString()), // Cuidado que me llega el valor como LocalDate y en la base de datos lo almacenamos como String
                Filters.eq(FIELD_USERNAME, item.getUsername()),
                Filters.eq(FIELD_RELIABLE, item.getReliable())
        ); // Debería hacer el filtro con el ObjectId, pero la cagué aquí

        DeleteResult deleteResult = collection.deleteOne(filter); // Solamente uno, por si alguno tiene lo mismo

        if (deleteResult.getDeletedCount() == 1) {
            logger.info("Usuario eliminado con éxito.");
        } else {
            logger.error("No se pudo eliminar el usuario.");
        }

        databaseManager.closeDatabase();
    }

}
