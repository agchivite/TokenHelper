package dev.sbytmacke.tokenhelper.repositories;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import dev.sbytmacke.tokenhelper.models.User;
import dev.sbytmacke.tokenhelper.services.database.DatabaseManager;
import org.bson.Document;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class UserRepositoryImpl implements Repository<User, String> {

    private final DatabaseManager databaseManager;

    public UserRepositoryImpl(DatabaseManager databaseManager) {

        this.databaseManager = databaseManager;
    }


    @Override
    public User addItem(User user) {
        // Conectar a la base de datos
        databaseManager.connectDatabase();

        // Obtener la colección de usuarios
        MongoCollection<Document> collection = databaseManager.getDatabase().getCollection("users_bet");

        // Crear un documento a partir del usuario
        Document userDocument = new Document("username", user.getUsername())
                .append("dateBet", user.getDateBet().toString())
                .append("timeBet", user.getTimeBet().toString())
                .append("reliable", user.getIsReliable().toString());

        // Insertar el documento en la colección
        collection.insertOne(userDocument);

        // Cerrar la conexión
        databaseManager.closeDatabase();

        return user;
    }

    @Override
    public User findById(String username) {
        return null;
    }

    @Override
    public ArrayList<User> findAll() {
        databaseManager.connectDatabase();

        MongoCollection<Document> collection = databaseManager.getDatabase().getCollection("users_bet");

        // Construye una consulta (por ejemplo, encontrar todos los documentos)
        Document query = new Document();

        // Ejecuta la consulta
        MongoCursor<Document> cursor = collection.find(query).iterator();

        // Itera sobre los resultados de la consulta
        ArrayList<User> usersList = new ArrayList<>();
        while (cursor.hasNext()) {
            Document document = cursor.next();

            // DTO para almacenar con el documento en String o JSON como veamos
            User user = new User("Angelito", LocalDate.now(), LocalTime.now(), true);
            usersList.add(user);
            System.out.println(document.toJson());
        }

        // Cierra el cursor y la conexión
        cursor.close();
        databaseManager.closeDatabase();

        return usersList;
    }
}
