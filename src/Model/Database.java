package Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String DB_NAME = "ArrayToHTMLTable";
    private static volatile Database instance = null;
    private Connection cnn = null;
    private boolean isDBExist = false;

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

/*    public void getCatalogs() throws SQLException {
        if (cnn != null) {
            try (ResultSet rs = cnn.getMetaData().getCatalogs()) {
                while (rs.next()) {
                    System.out.println(rs.getString(1));
                }
            }
        }
    }*/

    private void createDatabase() throws SQLException {

/*        assert cnn != null;
        try (ResultSet rs = cnn.getMetaData().getCatalogs()) {
            while (rs.next()) {
                String catalogs = rs.getString(1);
                if (dbName.equals(catalogs))
                    return;
            }
        }*/
        assert !isDBExist;
        assert cnn != null;
        try (Statement stm = cnn.createStatement()) {
            /*String sql = "use master\n" +
                    "go\n" +
                    "\n" +
                    "create database " + DB_NAME + "\n" +
                    "go\n" +
                    "\n" +
                    "use ArrayToHTMLTable\n" +
                    "go\n" +
                    "\n" +
                    "create table Log\n" +
                    "(\n" +
                    "[INPUT] nvarchar(100) not null,\n" +
                    "[OUTPUT] nvarchar(100) not null,\n" +
                    "[TIME] varchar(50) not null,\n" +
                    "constraint Log_pk\n" +
                    "primary key nonclustered ([INPUT], [OUTPUT], [TIME])\n" +
                    ")\n" +
                    "go\n";*/
//            System.out.println(sql);
//            stm.executeUpdate(sql);
        }
        isDBExist = true;
    }

    public void disconnect() throws SQLException {
        this.cnn.close();
    }
}
