import java.sql.*;

// on executeUpdate vs executeQuery
// https://stackoverflow.com/questions/21276059/no-results-returned-by-the-query-error-in-postgresql

public class Accounts {
    public static String DATABASE_URL = "jdbc:postgresql://s-l112.engr.uiowa.edu/swd_db08";
    public static String USERNAME = "swd_student08";
    public static String PASSWORD = "engr-2022-08";
    private static ResultSet resultSet;
    private static ResultSetMetaData metaData;
    public static Connection connection;
    static Statement statement;

    // refreshes database
    private static void updateData() throws SQLException {
//        connection = DriverManager.getConnection(DATABASE_URL,USERNAME,PASSWORD);
//        statement = connection.createStatement();

        // all accounts ordered by score from highest to lowest
        resultSet = statement.executeQuery("SELECT * FROM accounts ORDER BY Score DESC;");
        metaData = resultSet.getMetaData();
    }

    public static boolean usernameTaken(String username) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT COUNT(1) FROM Accounts WHERE username = ?;");
        ps.setString(1, username);
        //ps.addBatch();
        return getCount(ps) == 1;
    }

    // checks that the user's login is valid. If the function runs without throwing an SQLException, the login is valid
    // if not, either the username or password is wrong
    public static boolean validLogin(String username, String password) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT COUNT(1) FROM Accounts WHERE username = ? AND password = ?;");
        ps.setString(1, username);
        ps.setString(2, password);
        ps.addBatch();
        int count = getCount(ps);
        // System.out.println("validlogin count: " + count);
        return count == 1;
    }


    // returns 1 if the account referenced in ps is found, 0 if not
    private static int getCount(PreparedStatement ps) throws SQLException {
        resultSet = statement.executeQuery(ps.toString());
        metaData = resultSet.getMetaData();

        int count = -1;
        while(resultSet.next()){
            for(int i = 1; i <= metaData.getColumnCount(); i++){
                count = resultSet.getInt(i);
            }
        }
        return count;
    }

    // adds an account to the database give its username and password. Accounts are given a default score of 0
    // Returns true if account was added successfully, returns false if username is already in database
    public static boolean addAccount(String usernameInp, String passwordInp) throws SQLException {
        if(usernameTaken(usernameInp)){
            // username in use
            return false;
        } else {
            PreparedStatement ps2 = connection.prepareStatement("INSERT INTO Accounts (username, password, score) VALUES (?, ?, 0);");
            ps2.setString(1, usernameInp);
            ps2.setString(2, passwordInp);
            // ps2.addBatch();
            statement.executeUpdate(ps2.toString());
            updateData();
            return true;
        }

    }

    // returns true if account was successfully deleted, false if not
    public static boolean deleteAccount(String username, String password) throws SQLException {
        // ensures the inputted username and password are valid, this may not be necessary depending on how we implement this function
        if (validLogin(username, password)) {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM Accounts WHERE Accounts.Username = ? AND Accounts.Password = ?");
            ps.setString(1, username);
            ps.setString(2, password);
//            ps.addBatch();
            statement.executeUpdate(ps.toString());
            return true;
        }
        return false;
    }


    // updates the score of an existing account
    public static void updateScore(String username, int scoreChange) throws SQLException {
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


        ps.setString(1, String.valueOf(prevScore + scoreChange)); // previous score + change in score
        ps.setString(2, username);
        // ps.addBatch();

        statement.executeUpdate(ps.toString());

        updateData();
    }


    // returns a string representation of the leaderboard, goes from the highest score to lowest
    public static String displayLeaderBoard(int num) throws SQLException {
        StringBuilder output = new StringBuilder("Leaderboard:\n\n");
        resultSet = statement.executeQuery("SELECT Username, Score FROM Accounts ORDER BY Score DESC");
        metaData = resultSet.getMetaData();
        int numColumns = metaData.getColumnCount();

        for (int i = 1; i <= numColumns; i++) {
            String str = metaData.getColumnName(i);
            //String strCap = str.substring(0,1).toUpperCase() + str.substring(1);
            output.append(str.substring(0, 1).toUpperCase()).append(str.substring(1)).append("\t");
            /*

            String firstLetter = metaData.getColumnName(i).substring(0,1);
            String afterFirstLetter = metaData.getColumnName(i).substring(1);
            output.append(firstLetter).append(afterFirstLetter).append("\t");

             */
        }
        output.append("\n");

        int x = 0;
        if (num == 0) // if zero, display the entire leaderboard
            num = Integer.parseInt(statement.executeQuery("SELECT COUNT(Username) from Accounts").toString());
        while (resultSet.next() && x < num) {
            for (int i = 1; i <= numColumns; i++) {
                output.append(resultSet.getObject(i)).append("\t");
            }
            output.append("\n");
            x++;
        }

        return output.toString();
    }





    // clears everything from Accounts table except the column titles
    public static void clearAll() throws SQLException {
        statement.executeUpdate("DELETE FROM accounts WHERE TRUE");

        // statement.executeQuery("DELETE FROM accounts WHERE EXISTS(SELECT * FROM accounts);");
    }

    public static void initialize() {
        try {
//            Class.forName("org.postgresql.Driver");

            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM accounts ORDER BY Score DESC");
            metaData = resultSet.getMetaData();
        } catch (SQLException e) { // | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
//        catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }

    }
}
