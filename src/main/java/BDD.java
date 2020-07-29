import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class BDD {

    private Connection connection;
    private Properties properties;

    BDD() throws IOException {
        this.properties = loadProperties();
    }

    /* Properties loading */

    /**
     * Load app.properties into an Properties object
     * @return Properties Object
     * @throws IOException File properties not found
     */
    Properties loadProperties() throws IOException {
        Path root = Paths.get(".").normalize().toAbsolutePath();
        String appConfigPath = root.toAbsolutePath() + "/app.properties";

        Properties appProps = new Properties();
        appProps.load(new FileInputStream(appConfigPath));

        return appProps;
    }

    /* Connection management */

    /**
     * Connect to the database provided by the app.properties file
     * @throws SQLException Error when connecting to database
     */
    void connect() throws SQLException {
        String DB_HOST = this.properties.getProperty("DB_HOST");
        String DB_PORT = this.properties.getProperty("DB_PORT");
        String DB_DATABASE = this.properties.getProperty("DB_DATABASE");
        String DB_USER = this.properties.getProperty("DB_USER");
        String DB_PASS = this.properties.getProperty("DB_PASS");

        this.connection = DriverManager.getConnection("jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_DATABASE+"?serverTimezone=UTC", DB_USER, DB_PASS);

        this.connection.setAutoCommit(false);
    }

    /**
     * Close database connection
     * @throws SQLException Error when closing database connection
     */
    void close() throws SQLException {
        if (this.connection != null) {
            this.connection.close();
        }
    }

    /**
     * Commit all transactions done before
     * @throws SQLException Error when committing to database
     */
    void commit() throws SQLException {
        this.connection.commit();
    }

    /**
     * Rollback all transactions done before
     * @throws SQLException Error when executing rollback
     */
    void rollback() throws SQLException {
        this.connection.rollback();
    }

    /* Query */

    boolean insertData(String firstName, String lastName, Date dob) throws SQLException {
        String query = "INSERT INTO person(first_name,last_name,dob) VALUES (?,?,?)";
        PreparedStatement stmt = this.connection.prepareStatement(query);

        stmt.setString(1,firstName);
        stmt.setString(2,lastName);
        stmt.setDate(3, dob);

        return stmt.execute();
    }

    ArrayList<String> getFirstNames() throws SQLException {
        String query = "SELECT first_name FROM person";
        Statement stmt = this.connection.createStatement();
        ResultSet set = stmt.executeQuery(query);

        ArrayList<String> list = new ArrayList<>();
        while(set.next()){
            list.add(set.getString("first_name"));
        }

        return list;
    }

    ResultSet getDataScrollable() throws SQLException {
        String query = "SELECT * FROM person";
        Statement stmt = this.connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

        return stmt.executeQuery(query);
    }

    ResultSet getUpdatableData() throws SQLException {
        String query = "SELECT * FROM person";
        Statement stmt = this.connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

        return stmt.executeQuery(query);
    }

    boolean updateLine(int line_id, String firstName, String lastName, Date dob) throws SQLException {
        String query = "UPDATE person SET first_name=?,last_name=?,dob=? WHERE SERIAL=?";
        PreparedStatement stmt = this.connection.prepareStatement(query);

        stmt.setInt(1,line_id);
        stmt.setString(2,firstName);
        stmt.setString(3,lastName);
        stmt.setDate(4,dob);

        return stmt.execute();
    }

    boolean deleteLine(int line_id) throws SQLException {
        String query = "DELETE FROM person WHERE SERIAL=?";
        PreparedStatement stmt = this.connection.prepareStatement(query);

        stmt.setInt(1,line_id);

        return stmt.execute();
    }


}
