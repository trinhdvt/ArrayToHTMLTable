package Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String DB_NAME = "ArrayToHTMLTable";
    private static volatile Database instance = null;
    private Connection cnn = null;


    private Database() {
        if (instance != null)
            throw new RuntimeException("Use getInstance method instead");
    }

    public static Database getInstance() {
        if (instance == null) synchronized (Database.class) {
            if (instance == null)
                instance = new Database();
        }
        return instance;
    }

    public void connect() throws ClassNotFoundException, SQLException {
        if (cnn == null) {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            final String DB_URL = "jdbc:sqlserver://localhost";
            final String USER_NAME = "sa";
            cnn = DriverManager.getConnection(DB_URL, USER_NAME, "illusion");
            createDatabase();
        }
    }

    private void createDatabase() throws SQLException {
        assert cnn != null;
        Statement stm = null;
        try {
            stm = cnn.createStatement();
            String sql = "create database " + DB_NAME;
            stm.executeUpdate(sql);
            sql = "use " + DB_NAME +
                    ";create table Log (" +
                    "[input] nvarchar(1000) primary key," +
                    "[output] nvarchar(1000) not null," +
                    "[time] nvarchar(20) not null " +
                    ")";
            stm.executeUpdate(sql);
        } catch (SQLException ignored) {
            assert stm != null;
            stm.execute("use " + DB_NAME);
        } finally {
            assert stm != null;
            stm.close();
        }

    }

    public void disconnect() throws SQLException {
        assert cnn != null;
        this.cnn.close();
    }
}
