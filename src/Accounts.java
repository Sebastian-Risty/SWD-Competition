import javafx.fxml.Initializable;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class Accounts implements Initializable {
    private static final String DATABASE_URL = "";
    private static final String USERNAME = "";
    private static final String PASSWORD = "";
    private static ResultSet resultSet;
    private static ResultSetMetaData metaData;
    private static Connection connection;
    private static Statement statement;

    // refreshes database
    private static void updateData() throws SQLException {
//        connection = DriverManager.getConnection(DATABASE_URL,USERNAME,PASSWORD);
//        statement = connection.createStatement();

        // all accounts ordered by score from highest to lowest
        resultSet = statement.executeQuery("SELECT Accounts FROM WordGame ORDER BY Score DESC");

        // updates the rank to their now sorted row number
        statement.executeQuery("UPDATE Accounts SET Rank = ROW_NUMBER");
        metaData = resultSet.getMetaData();
    }

    // adds an account to the database give its username and password. Accounts are given a default score of 0
    // An exception is thrown if the database cannot be written to, likely meaning the username is already in use
    public static void addAccount(String usernameInp, String passwordInp) throws SQLException {
        // SELECT COUNT(Username) from Accounts or ROW_NUMBER
        PreparedStatement ps = connection.prepareStatement("INSERT INTO Accounts (Rank, Username, Password, Score) VALUES (-100, ?, ?, 0);");
        ps.setString(1, usernameInp);
        ps.setString(2, passwordInp);
        ps.addBatch();
        updateData();
    }

    // updates the score of an existing account
    public static void updateScore(String username, int scoreChange) throws SQLException {
        // prevScore is the user's current score
        // int prevScore = Integer.parseInt(statement.executeQuery("SELECT score from Accounts WHERE Accounts.Username = ?;").toString());
        PreparedStatement ps = connection.prepareStatement("UPDATE Accounts SET Score = ? from Accounts WHERE Accounts.Username = ?;");
        ps.setString(1, String.valueOf(Integer.parseInt(statement.executeQuery("SELECT Score from Accounts WHERE Accounts.Username = ?;").toString()) + scoreChange)); // previous score + change in score
        ps.setString(2, username);
        ps.addBatch();

        updateData();
    }


    // returns a string representation of the leaderboard, goes from the highest score to lowest
    public static String displayLeaderBoard(int num) throws SQLException {
        StringBuilder output = new StringBuilder("Leaderbord:\n\n");
        resultSet = statement.executeQuery("SELECT Rank, Username, Score, FROM Accounts ORDER BY Rank DESC");
        metaData = resultSet.getMetaData();
        int numColumns = metaData.getColumnCount();

        for (int i = 1; i <= numColumns; i++) {
            output.append(metaData.getColumnName(i)).append("\t");
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

    // checks that the user's login is valid. If the function runs without throwing an SQLException, the login is valid
    // if not, either the username or password is wrong
    public static boolean validLogin(String username, String password) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT FROM Accounts WHERE Accounts.Username = ? AND Accounts.Password = ?");
        ps.setString(1, username);
        ps.setString(2, password);
        ps.addBatch();
        return true;
    }

    public static void deleteAccount(String username, String password) throws SQLException {
        // ensures the inputted username and password are valid, this may not be necessary depending on how we implement this function
        if (validLogin(username, password)) {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM Accounts WHERE Accounts.Username = ? AND Accounts.Password = ?");
            ps.setString(1, username);
            ps.setString(2, password);
            ps.addBatch();
        }
    }

    // clears everything from Accounts table except the column titles
    public static void clearAll() throws SQLException {
        statement.executeQuery("DELETE * FROM Accounts.Rank, Accounts.Username, Accounts.Password, Accounts.Score;");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            statement = connection.createStatement();
            // commented out line kills the database between multiple executions
            // resultSet = statement.executeQuery("DROP DATABASE IF EXISTS WordGame; CREATE DATABASE WordGame; USE WordGame;" +

            // this line preserves the database between executions
            resultSet = statement.executeQuery("CREATE DATABASE IF NOT EXISTS WordGame; USE WordGame;" +
                    "CREATE TABLE IF NOT EXISTS Accounts(" +
                    "Rank int NOT NULL PRIMARY KEY " +
                    "Username String NOT NULL PRIMARY KEY," + // primary key ensures each username is unique
                    "Password String NOT NULL," +
                    "Score int NOT NULL);");
            metaData = resultSet.getMetaData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
