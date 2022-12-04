import java.io.*;
import java.sql.*;
import java.util.Objects;

public class Database {
    private static final String DATABASE_URL = "jdbc:postgresql://s-l112.engr.uiowa.edu/swd_db08";
    private static final String USERNAME = "swd_student08";
    private static final String PASSWORD = "engr-2022-08";
    private static ResultSet resultSet;
    private static ResultSetMetaData metaData;
    private static Statement statement;
    private static String table;
    private static final String KEY_NAME = "key";
    private static String[] keyInfo;

    public static void setTable(String table) {
        Database.table = table;
    }

    public static void getKeyInfo() throws FileNotFoundException {
        keyInfo = Encryptor.getFileInfo(KEY_NAME);
        if (Objects.equals(keyInfo[0], "Error: file \"" + KEY_NAME + "\" not found")) { // if the key is not found
            System.out.println("Error: key \"" + KEY_NAME + "\" not found");
            throw new FileNotFoundException();
        }
    }

    // refreshes database
    private static void updateData() throws SQLException {
//        connection = DriverManager.getConnection(DATABASE_URL,USERNAME,PASSWORD);
//        statement = connection.createStatement();

        // all accounts ordered by score from highest to lowest
        if (Objects.equals(table, "Accounts"))
            resultSet = statement.executeQuery("SELECT * FROM Accounts ORDER BY totalwins DESC");
        else if (Objects.equals(table, "Test"))
            resultSet = statement.executeQuery("SELECT * FROM Test ORDER BY score DESC");
        else if (Objects.equals(table, "mastertournament"))
            resultSet = statement.executeQuery("SELECT * FROM mastertournament ORDER BY tournamentid DESC");
        else if (Objects.equals(table, "Login"))
            resultSet = statement.executeQuery("SELECT * FROM Login ORDER BY username ");
        else // non-master tournament table
            resultSet = statement.executeQuery("SELECT * FROM " + table + " ORDER BY wins DESC");

        metaData = resultSet.getMetaData();
    }

    // returns true if the username is already used, false if not
    private static boolean usernameTaken(String username) throws SQLException {
        if (!Objects.equals(table, "Test")) { // if you're not testing, table should be login
            table = "Login";
            resultSet = statement.executeQuery("SELECT COUNT(1) FROM login WHERE username = '" + username + "';");
        } else{
            resultSet = statement.executeQuery("SELECT COUNT(1) FROM test WHERE username = '" + username + "';");

        }
        return inDB(resultSet);
    }

    private static boolean tournamentTaken(String tournamentID) throws SQLException {
        //if (!Objects.equals(table, "Test")) // if you're not testing, table should be mastertournament
        table = "mastertournament";
        resultSet = statement.executeQuery("SELECT COUNT(1) FROM mastertournament WHERE tournamentid = '" + tournamentID + "';");
        return inDB(resultSet);
    }


    // checks that the user's login is valid. If the function runs without throwing an SQLException, the login is valid
    // if not, either the username or password is wrong
    public static boolean validLogin(String username, String password) throws SQLException, FileNotFoundException {
        if (!Objects.equals(table, "Test")) { // if you're not testing, table should be login
            table = "login";
            resultSet = statement.executeQuery("SELECT COUNT(1) FROM login WHERE username = '" + username + "' AND password = '" + Encryptor.shiftMessage(password, keyInfo, false) + "';");
        } else {
            resultSet = statement.executeQuery("SELECT COUNT(1) FROM test WHERE username = '" + username + "' AND password = '" + Encryptor.shiftMessage(password, keyInfo, false) + "';");
        }
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

        String encryptedPassword = Encryptor.shiftMessage(passwordInp, keyInfo, false);

        if (Objects.equals(table, "Test"))
            statement.executeUpdate("INSERT INTO test (username, password, score) VALUES ('" + usernameInp + "', '" + encryptedPassword + "', 0);");
        else {
            statement.executeUpdate("INSERT INTO Login (username, password) VALUES ('" + usernameInp + "', '" + encryptedPassword + "');");
            statement.executeUpdate("INSERT INTO accounts (username) VALUES ('" + usernameInp + "');");
        }

        updateData();
        return true;
    }

    public static int countRows(String table) throws SQLException {
        resultSet = statement.executeQuery("SELECT COUNT(*) FROM " + table);
        metaData = resultSet.getMetaData();

        int rows = 0;
        while (resultSet.next()) {
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                rows = resultSet.getInt(i);
            }
        }
        return rows;

    }

    // returns false if username is already in inputted tournament table, true if added successfully
    public static boolean addToTournament(String usernameInp, String tournamentTable) throws SQLException {
        table = tournamentTable;
        resultSet = statement.executeQuery("SELECT COUNT(1) FROM " + table + " WHERE username = '" + usernameInp + "';");
        if (!inDB(resultSet)) {
            statement.executeUpdate("INSERT INTO " + table + "(username,wins,ranking) VALUES ('" + usernameInp + "', 0, '" + (countRows(tournamentTable) + 1) + "');");
            updateData();
            return true;
        }
        return false; // account already in tournament
    }

    // returns false if username was not in tournament table, true if removed successfully
    public static boolean removeFromTournament(String usernameInp, String tournamentTable) throws SQLException {
        table = tournamentTable;
        resultSet = statement.executeQuery("SELECT COUNT(1) FROM " + table + " WHERE username = '" + usernameInp + "';");
        if (inDB(resultSet)) {
            statement.executeUpdate("DELETE FROM " + table + " WHERE " + table + ".username = '" + usernameInp + "'");
            updateData();
            return true;
        }
        return false; // wasn't in tournament in the first place
    }

    // returns false if table name is already in use
    public static boolean createTournament(String tableName, int timeStarted) throws SQLException {
        if (tournamentTaken(tableName))
            return false;

        statement.executeUpdate("CREATE TABLE " + tableName + "\n" +
                "(\n" +
                "    Username varchar(50) NOT NULL PRIMARY KEY ,\n" +
                "    Wins int NOT NULL DEFAULT 0,\n" +
                "    Ranking varchar(50) NOT NULL\n" +
                ");");
        statement.executeUpdate("INSERT INTO mastertournament (tournamentid, timestarted) VALUES ('" + tableName + "', " + timeStarted + ");");
        table = "mastertournament";
        updateData();
        // create table and remove form master table
        return true;
    }

    // returns false if table cannot be found
    public static boolean deleteTournament(String tableName) throws SQLException {
        if (!tournamentTaken(tableName))
            return false;

        statement.executeUpdate("DROP TABLE " + tableName);
        statement.executeUpdate("DELETE FROM mastertournament WHERE tournamentid = '" + tableName + "';");
        table = "mastertournament";
        updateData();
        return true;
    }

    // returns the info of the inputted username
    // should specify table before running (Accounts.setTable())
    public static String[] getInfo(String username) throws SQLException {
        if (!Objects.equals(table, "mastertournament"))
            resultSet = statement.executeQuery("SELECT * FROM " + table + " WHERE username = '" + username + "';");
        else
            resultSet = statement.executeQuery("SELECT * FROM mastertournament WHERE tournamentid = '" + username + "';");
        metaData = resultSet.getMetaData();
        int numColumns = metaData.getColumnCount();

        StringBuilder output = new StringBuilder();
        while (resultSet.next()) {
            for (int i = 1; i <= numColumns; i++) {
                if (Objects.equals(table, "Login") || Objects.equals(table, "Test") && (i == 2)) { // if working with the password column
                    output.append(Encryptor.shiftMessage(resultSet.getObject(i).toString(), keyInfo, true)).append(",");
                } else
                    output.append(resultSet.getObject(i)).append(",");
            }
        }
        // System.out.println(output);

        return output.toString().split(",");

    }

    // returns true if account was successfully deleted, false if not
    public static boolean deleteAccount(String username, String password) throws SQLException {
        if (!Objects.equals(table, "Test"))
            table = "Login";
        // ensures the inputted username and password are valid, this may not be necessary depending on how we implement this function
        try {
            if (validLogin(username, password)) {
                String encryptedPassword = Encryptor.shiftMessage(password, keyInfo, false);

                if (Objects.equals(table, "Test")) // if testing
                    statement.executeUpdate("DELETE FROM " + table + " WHERE " + table + ".Username = '" + username + "' AND " + table + ".Password = '" + encryptedPassword + "'");
                else { // if not testing
                    statement.executeUpdate("DELETE FROM accounts WHERE accounts.Username = '" + username + "'");
                    // statement.executeUpdate("DELETE FROM tournament WHERE tournament.username = '" + username + "'");
                    statement.executeUpdate("DELETE FROM login WHERE login.Username = '" + username + "'");
                }
                //updateData();
                return true;
            }
        } catch (FileNotFoundException ex) {
            System.out.println("key not found");
        }
        return false;
    }

    // data essentially is a row of a database (username, password, score). Therefore, index 0 is associated with column 1 (username), index 1 with column 2 (password), etc
    // if data has a length longer than the number of columns, I decided to through an IndexOutOfBoundsException. We can handle this differently if we want
    // should specify table before running (Accounts.setTable())
    public static void update(String[] data) throws SQLException {
        updateData();
        if (data.length > metaData.getColumnCount()) {
            throw new IndexOutOfBoundsException();
        }

        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            if (Objects.equals(table, "mastertournament") && i == 1) { // changing tournament id
                statement.executeUpdate("UPDATE mastertournament SET " + metaData.getColumnName(i) + " = '" + data[i - 1] + "' WHERE tournamentid = '" + data[0] + "';");
            } else if (Objects.equals(table, "mastertournament")) { // changing time started
                statement.executeUpdate("UPDATE mastertournament SET " + metaData.getColumnName(i) + " = '" + Integer.parseInt(data[i - 1]) + "' WHERE tournamentid = '" + data[0] + "';");
            } else if (i == 1) { // changing a username
                statement.executeUpdate("UPDATE " + table + " SET " + metaData.getColumnName(i) + " = '" + data[i - 1] + "' WHERE username = '" + data[0] + "';");
            } else if ((Objects.equals(table, "Test") || Objects.equals(table, "Login")) && i == 2) // changing a password
                statement.executeUpdate("UPDATE " + table + " SET " + metaData.getColumnName(i) + " = '" + Encryptor.shiftMessage(data[i - 1], keyInfo, false) + "' WHERE username = '" + data[0] + "';");
            else // changing any column other than the username or password (all other columns are integers)
                statement.executeUpdate("UPDATE " + table + " SET " + metaData.getColumnName(i) + " = " + Integer.parseInt(data[i - 1]) + " WHERE username = '" + data[0] + "';");
        }
        updateData();
    }

    // returns an array in this format [user1,score1,user2,score2,...] where the user and their corresponding score
    // are in descending order (i.e. index 0 is the username for the player with the highest score, index 1 is their highest score)
    // should specify table before running (Accounts.setTable())
    public static String[] getTopPlayers(int num) throws SQLException { // MAKE SURE TABLE IS SET !!!
        StringBuilder output = new StringBuilder();
        if (Objects.equals(table, "Accounts") || Objects.equals(table, "Test"))
            resultSet = statement.executeQuery("SELECT Username, Score FROM " + table + " ORDER BY Score DESC");
        else  // getting from one of the tournament tables
            resultSet = statement.executeQuery("SELECT Username, Wins FROM " + table + " ORDER BY Wins DESC");

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
    // should specify table before running (Accounts.setTable())
    public static void clearAll() throws SQLException {
        statement.executeUpdate("DELETE FROM " + table);
        if (!Objects.equals(table, "Test")) // if not testing
            table = "Accounts";
        updateData();
    }

    public static void initialize(String table) {
        try {
//            Class.forName("org.postgresql.Driver");
            Database.table = table;
            getKeyInfo(); // reads in and stores info from key file

            Connection connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            statement = connection.createStatement();
            updateData();
        } catch (SQLException e) { // | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) { // key file not found
            throw new RuntimeException(e);
        }
//        catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }

    }
}
