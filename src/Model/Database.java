package Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Database {
    private static final String DB_NAME = "ArrayToHTMLTable";
    private static volatile Database instance = null;
    private final List<HTMLObject> myObjects;
    private Connection cnn = null;

    private Database() {
        if (instance != null)
            throw new RuntimeException("Use getInstance method instead");
        myObjects = new ArrayList<>();
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
            final String DB_URL = "jdbc:sqlserver://localhost;integratedSecurity=true";
//            final String USER_NAME = "sa";
//            final String PASS_WORD = "illusion";
            cnn = DriverManager.getConnection(DB_URL);
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
                    "[id] int identity (1,1), " +
                    "[input] nvarchar(1000) primary key," +
                    "[output] nvarchar(1000) not null," +
                    "[date] nvarchar(20) not null " +
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

    public void loadDB() throws SQLException {
        if (cnn == null) return;
        myObjects.clear();
        String selectQuery = "select [id], [input], [output], [date] from Log order by [id]";
        Statement selectStm = cnn.createStatement();
        ResultSet resultSet = selectStm.executeQuery(selectQuery);
        while (resultSet.next()) {
            int id = resultSet.getInt(1);
            String input = resultSet.getString(2);
            String output = resultSet.getString(3);
            String date = resultSet.getString(4);
            myObjects.add(HTMLObject.createObjectFromProperty(id, input, output, date));
        }
        HTMLObject.count = myObjects.size() + 1;
        resultSet.close();
        selectStm.close();
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
        boolean res = selectResult.getInt(1) != 0;
        selectStm.close();
        selectResult.close();
        return res;
    }

    private void updateDB(HTMLObject object) throws SQLException {
        String updateQuery = "update Log set [input] = ?, [output] = ?, [date] = ? where [id] = ?";
        PreparedStatement updateStm = cnn.prepareStatement(updateQuery);
        int index = 1;
        Object[] value = object.getWritableData();
        updateStm.setString(index++, String.valueOf(value[0]));
        updateStm.setString(index++, String.valueOf(value[1]));
        updateStm.setString(index++, String.valueOf(value[2]));
        updateStm.setInt(index, object.getId());
        updateStm.executeUpdate();
        updateStm.close();
    }

    private void insertToDB(HTMLObject object) throws SQLException {
        String insertSql = "insert into Log([input], [output] ,[date]) values (?,?,?)";
        PreparedStatement insertStm = cnn.prepareStatement(insertSql);
        int index = 1;
        Object[] insertValue = object.getWritableData();
        insertStm.setString(index++, String.valueOf(insertValue[0]));
        insertStm.setString(index++, String.valueOf(insertValue[1]));
        insertStm.setString(index, String.valueOf(insertValue[2]));
        insertStm.executeUpdate();
        insertStm.close();
    }

    public HTMLObject findByID(int id) {
        for (HTMLObject myObject : myObjects)
            if (myObject.getId() == id)
                return myObject;
        return null;
    }

    public void addObject(HTMLObject object) {
        this.myObjects.add(object);
    }

    public void replaceByID(HTMLObject newObject) {
        int length = myObjects.size();
        for (int i = 0; i < length; i++) {
            if (myObjects.get(i).getId() == newObject.getId())
                myObjects.set(i, newObject);
        }
    }

    public List<HTMLObject> getMyObjects() {
        return Collections.unmodifiableList(myObjects);
    }

/*
    public boolean testConnection(int port, String user, String password) {
        String url = "jdbc:sqlserver://localhost:" + port;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection testCnn = DriverManager.getConnection(url, user, password);
            testCnn.close();
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            return false;
        }

    }
*/

}
