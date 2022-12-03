import java.sql.*;

// on executeUpdate vs executeQuery
// https://stackoverflow.com/questions/21276059/no-results-returned-by-the-query-error-in-postgresql

public class Accounts {
    private static final String DATABASE_URL = "jdbc:postgresql://s-l112.engr.uiowa.edu/swd_db08";
    private static final String USERNAME = "swd_student08";
    private static final String PASSWORD = "engr-2022-08";
    private static ResultSet resultSet;
    private static ResultSetMetaData metaData;
    private static Connection connection;
    private static Statement statement;
    private static String table;

    public static void setTable(String table) {
        Accounts.table = table;
    }

    // refreshes database
    private static void updateData() throws SQLException {
//        connection = DriverManager.getConnection(DATABASE_URL,USERNAME,PASSWORD);
//        statement = connection.createStatement();

        // all accounts ordered by score from highest to lowest
        resultSet = statement.executeQuery("SELECT * FROM " + table + " ORDER BY Score DESC;");
        metaData = resultSet.getMetaData();
    }

    private static boolean usernameTaken(String username) throws SQLException {
        resultSet = statement.executeQuery("SELECT COUNT(1) FROM " + table + " WHERE username = '" + username + "';");
        return inDB(resultSet);
    }


    // checks that the user's login is valid. If the function runs without throwing an SQLException, the login is valid
    // if not, either the username or password is wrong
    public static boolean validLogin(String username, String password) throws SQLException {
        resultSet = statement.executeQuery("SELECT COUNT(1) FROM " + table + " WHERE username = '" + username + "' AND password = '" + password + "';");
        return inDB(resultSet);
    }

    private static boolean inDB(ResultSet rs) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        int count = -1;
        while (rs.next()) {
            for (int i = 1; i <= md.getColumnCount(); i++) {
                count = resultSet.getInt(i);
            }
        }
        return count == 1;
    }

    // adds an account to the database give its username and password. Accounts are given a default score of 0
    // Returns true if account was added successfully, returns false if username is already in database
    public static boolean addAccount(String usernameInp, String passwordInp) throws SQLException {
        if (usernameTaken(usernameInp)) {
            // username in use
            return false;
        }
        statement.executeUpdate("INSERT INTO " + table + " (username, password, score) VALUES ('" + usernameInp + "', '" + passwordInp + "', 0);");
        updateData();
        return true;
    }

    // returns true if account was successfully deleted, false if not
    public static boolean deleteAccount(String username, String password) throws SQLException {
        // ensures the inputted username and password are valid, this may not be necessary depending on how we implement this function
        if (validLogin(username, password)) {
            statement.executeUpdate("DELETE FROM " + table + " WHERE " + table + ".Username = '" + username + "' AND " + table + ".Password = '" + password + "'");
            return true;
        }
        return false;
    }


    // updates the score of an existing account
    // no longer needed due to update()
    /*
    public static void updateScore(String[] data) throws SQLException {
        String username = data[0];
        int score = Integer.parseInt(data[1]);

        String query = "SELECT score from accounts WHERE username = '" + username + "';";

        PreparedStatement ps = connection.prepareStatement("UPDATE accounts SET score = ? WHERE username = ?;");

        resultSet = statement.executeQuery(query);
        metaData = resultSet.getMetaData();
        int prevScore = -100;
        while(resultSet.next()){
            for(int i = 1; i <= metaData.getColumnCount(); i++){
                prevScore = resultSet.getInt(i);
            }
        }


        //ps.setString(1, String.valueOf(prevScore + scoreChange)); // previous score + change in score
        ps.setString(2, username);
        // ps.addBatch();

        statement.executeUpdate(ps.toString());

        updateData();
    }

     */

    // data essentially is a row of a database (username, password, score). Therefore, index 0 is associated with column 1 (username), index 1 with column 2 (password), etc
    // if data has a length longer than the number of columns, I decided to through an IndexOutOfBoundsException. We can handle this differently if we want
    public static void update(String[] data) throws SQLException {
        updateData();
        if (data.length > metaData.getColumnCount()) {
            throw new IndexOutOfBoundsException();
        }

        for (int i = 1; i <= metaData.getColumnCount(); i++) {
//            System.out.println(metaData.getColumnName(i));
//            System.out.println(data[i - 1]);
//            System.out.println(data[0]);

            if (i == 3) // i = 3 at the score
                statement.executeUpdate("UPDATE " + table + " SET " + metaData.getColumnName(i) + " = " + Integer.parseInt(data[i - 1]) + " WHERE username = '" + data[0] + "';");
            else
                statement.executeUpdate("UPDATE " + table + " SET " + metaData.getColumnName(i) + " = '" + data[i - 1] + "' WHERE username = '" + data[0] + "';");
        }
        updateData();

    }

    // returns an array in this format [user1,score1,user2,score2,...] where the user and their corresponding score
    // are in descending order (i.e. index 0 is the username for the player with the highest score, index 1 is their highest score)
    public static String[] getTopPlayers(int num) throws SQLException {
        StringBuilder output = new StringBuilder();
        resultSet = statement.executeQuery("SELECT Username, Score FROM " + table + " ORDER BY Score DESC");
        metaData = resultSet.getMetaData();
        int numColumns = metaData.getColumnCount();

        int x = 0;
        if (num == 0) // if zero, display the entire leaderboard
            num = Integer.parseInt(statement.executeQuery("SELECT COUNT(Username) from " + table).toString());
        while (resultSet.next() && x < num) {
            for (int i = 1; i <= numColumns; i++) {
                output.append(resultSet.getObject(i)).append(",");
            }
            x++;
        }

        return output.toString().split(",");
    }


    // clears everything from Accounts table except the column titles
    public static void clearAll() throws SQLException {
        statement.executeUpdate("DELETE FROM " + table + " WHERE TRUE");
    }

    public static void initialize(String table) {
        try {
//            Class.forName("org.postgresql.Driver");
            Accounts.table = table;
            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM " + table + " ORDER BY Score DESC");
            metaData = resultSet.getMetaData();
        } catch (SQLException e) { // | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
//        catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }

    }
}
