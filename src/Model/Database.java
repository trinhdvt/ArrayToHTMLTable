package Model;

import java.sql.*;

public class Database {
    private static final String DB_NAME = "ArrayToHTMLTable";
    private static volatile Database instance = null;
    //    private static String LOCAL_DB_PATH = null;
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
            final String PASS_WORD = "illusion";
            cnn = DriverManager.getConnection(DB_URL, USER_NAME, PASS_WORD);
            createDB();
        }
    }

    public void disconnect() throws SQLException {
        if (cnn != null)
            this.cnn.close();
    }

    private void createDB() throws SQLException {
        if (cnn == null) return;
        Statement stm = null;
        try {
            stm = cnn.createStatement();
            String sql = "create database " + DB_NAME;
            stm.executeUpdate(sql);
            sql = "use " + DB_NAME +
                    ";create table Log ( " +
                    "[id] int primary key," +
                    "[input] nvarchar(1000) not null," +
                    "[output] nvarchar(1000) not null," +
                    "[time] nvarchar(20) not null " +
                    ")";
            stm.executeUpdate(sql);
        } catch (SQLException ignored) {
            if (stm != null)
                stm.execute("use " + DB_NAME);
        } finally {
            if (stm != null)
                stm.close();
        }

    }

/*
    private void createLocalDB() throws IOException {
        String appPath = System.getenv("APPDATA");
        Runtime.getRuntime().exec(String.format("cmd /c cd %s & md %s", appPath, DB_NAME));
        LOCAL_DB_PATH = appPath + DB_NAME;
    }
*/

    public void saveToDB(int id, String input, String output, String time) throws SQLException {
        if (cnn == null) return;
        String insertSql = "insert into Log(id, input, output, time) values (?,?,?,?)";
        PreparedStatement insertStm = cnn.prepareStatement(insertSql);
        insertStm.setInt(1, id);
        insertStm.setString(2, input);
        insertStm.setString(3, output);
        insertStm.setString(4, time);
        insertStm.executeUpdate();
        insertStm.close();
    }
}
