package Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String DB_NAME = "ArrayToHTMLTable";
    private static volatile Database instance = null;
    private final List<HTMLObject> myObjects = new ArrayList<>();
    private Connection cnn = null;
    //    private static String LOCAL_DB_PATH = null;

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
                    "[input] nvarchar(1000) primary key," +
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

    public void saveToDB(HTMLObject object) throws SQLException {
        if (cnn == null) return;
        if (isDataExist(object))
            updateDB(object);
        else
            insertToDB(object);

    }

    private boolean isDataExist(HTMLObject object) throws SQLException {
        String selectQuery = "select count(*) from Log where id = ?";
        PreparedStatement selectStm = cnn.prepareStatement(selectQuery);
        selectStm.setInt(1, object.getId());
        ResultSet selectResult = selectStm.executeQuery();
        selectResult.next();
        return selectResult.getInt(1) != 0;
    }

    private void updateDB(HTMLObject object) throws SQLException {
        String updateQuery = "update Log where ";
        PreparedStatement updateStm = cnn.prepareStatement(updateQuery);
    }

    private void insertToDB(HTMLObject object) throws SQLException {
        String insertSql = "insert into Log(input, output ,time ) values (?,?,?)";
        PreparedStatement insertStm = cnn.prepareStatement(insertSql);
        int index = 1;
        Object[] insertValue = object.getWritableData();
        insertStm.setString(index++, String.valueOf(insertValue[0]));
        insertStm.setString(index++, String.valueOf(insertValue[1]));
        insertStm.setString(index, String.valueOf(insertValue[2]));
        insertStm.executeUpdate();
        insertStm.close();
    }

    public HTMLObject existObject(HTMLObject object) {
        for (HTMLObject object1 : myObjects) {
            if (object1.equals(object)) {
                return object1;
            }
        }
        return null;
    }

    public HTMLObject findByID(HTMLObject object) {
//        return myObjects.stream().filter(object1 -> object1.getId() == object.getId()).findFirst().orElse(null);
//        int length = myObjects.size();
        for (HTMLObject myObject : myObjects) {
            if (myObject.getId() == object.getId())
                return myObject;
        }
        return null;
    }

    public void addObject(HTMLObject object) {
        this.myObjects.add(object);
    }

    public void deleteObject(int id) {
        int length = myObjects.size();
        for (int i = 0; i < length; i++) {
            if (myObjects.get(i).getId() == id) {
                myObjects.remove(i);
                return;
            }
        }
    }

    public List<HTMLObject> getMyObjects() {
        return myObjects;
    }
    /*
    private void createLocalDB() throws IOException {
        String appPath = System.getenv("APPDATA");
        Runtime.getRuntime().exec(String.format("cmd /c cd %s & md %s", appPath, DB_NAME));
        LOCAL_DB_PATH = appPath + DB_NAME;
    }
*/

}
