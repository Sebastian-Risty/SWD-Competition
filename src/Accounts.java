import java.sql.*;

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

    // adds an account to the database give its username and password. Accounts are given a default score of 0
    // Returns true if account was added successfully, returns false if username is already in database
    public static boolean addAccount(String usernameInp, String passwordInp) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT COUNT(1) FROM Accounts WHERE username = ?;");
        ps.setString(1, usernameInp);
        resultSet = statement.executeQuery(ps.toString());
        metaData = resultSet.getMetaData();
        /*
        if(Integer.parseInt(String.valueOf()) == 0){ // the account isn't in the database
            PreparedStatement ps2 = connection.prepareStatement("INSERT INTO Accounts (username, password, score) VALUES (?, ?, 0);");
            ps2.setString(1, usernameInp);
            ps2.setString(2, passwordInp);
            ps2.addBatch();
            resultSet = statement.executeQuery(ps2.toString());
            updateData();
            return true;
        } else if(Integer.parseInt(String.valueOf(resultSet)) == 1){ // the account is in the database
            System.out.println("duplicate account");
        }

         */


        int count = -1;
        while(resultSet.next()){
            for(int i = 1; i <= metaData.getColumnCount(); i++){
                count = resultSet.getInt(1);
            }
        }

        if(count >= 1){
            System.out.println("duplicate account");
        } else {
            System.out.println(count);
            PreparedStatement ps2 = connection.prepareStatement("INSERT INTO Accounts (username, password, score) VALUES (?, ?, 0);");
            ps2.setString(1, usernameInp);
            ps2.setString(2, passwordInp);
            ps2.addBatch();
            //statement.executeQuery(ps2.toString());
            updateData();
            return true;
        }
        return false;




//        PreparedStatement ps = connection.prepareStatement("INSERT INTO Accounts (username, password, score) VALUES (?, ?, 0);");
//        ps.setString(1, usernameInp);
//        ps.setString(2, passwordInp);
//        ps.addBatch();
//        resultSet = statement.executeQuery(ps.toString());
//        updateData();
    }


    // updates the score of an existing account
    public static void updateScore(String username, int scoreChange) throws SQLException {
        String query = "SELECT Score from Accounts WHERE Username = " + username + ";";

        PreparedStatement ps = connection.prepareStatement("UPDATE Accounts SET Score = ? from Accounts WHERE Username = ?;");
        ps.setString(1, String.valueOf(Integer.parseInt(statement.executeQuery(query).toString()) + scoreChange)); // previous score + change in score
        ps.setString(2, username);
        ps.addBatch();

        updateData();
    }


    // returns a string representation of the leaderboard, goes from the highest score to lowest
    public static String displayLeaderBoard(int num) throws SQLException {
        StringBuilder output = new StringBuilder("Leaderbord:\n\n");
        resultSet = statement.executeQuery("SELECT Username, Score FROM Accounts ORDER BY Score DESC");
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
        statement.executeQuery("DELETE FROM accounts;");
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
