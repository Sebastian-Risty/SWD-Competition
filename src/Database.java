import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Objects;

/**
 * Contains all the information and methods necessary to write to and manipulate a database
 *
 * @author mddutton
 */
public class Database {
    /**
     * Website that hosts the database
     */
    private static final String DATABASE_URL = "jdbc:postgresql://s-l112.engr.uiowa.edu/swd_db08";
    /**
     * Username to log in to the database
     */
    private static final String USERNAME = "swd_student08";
    /**
     * Password to log in to the database
     */
    private static final String PASSWORD = "engr-2022-08";
    /**
     * Information gotten from the database
     */
    private static ResultSet resultSet;
    /**
     * The metadata gotten from the database
     */
    private static ResultSetMetaData metaData;
    /**
     * The result of connecting to the database
     */
    private static Statement statement;
    /**
     * the name of the table being manipulated in the database
     */
    private static String table;
    /**
     * filename of the key file, used for password encryption
     */
    private static final String KEY_NAME = "key";
    /**
     * Information gotten from the key file, used for password encryption
     */
    private static String[] keyInfo;

    /**
     * sets table to the inputted string
     *
     * @param table desired table
     */
    public static void setTable(String table) {
        Database.table = table;
    }

    /**
     * sets parameter keyInfo to the information read in from the key file
     *
     * @throws FileNotFoundException thrown if the key file is not found
     */
    public static void getKeyInfo() throws FileNotFoundException {
        keyInfo = Encryptor.getFileInfo(KEY_NAME);
        if (Objects.equals(keyInfo[0], "Error: file \"" + KEY_NAME + "\" not found")) { // if the key is not found
            System.out.println("Error: key \"" + KEY_NAME + "\" not found");
            throw new FileNotFoundException();
        }
    }

    /**
     * Refreshes the resultSet and metaData parameters
     *
     * @throws SQLException thrown if failed to execute query on database
     */
    private static void updateData() throws SQLException {
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

    /**
     * Determines whether an inputted username is available. Be sure to call Database.setTable() beforehand to view
     * the correct table.
     *
     * @param username the username that's being checked if it's available
     * @return true if the username is available, false if not
     * @throws SQLException thrown if failed to execute query on database
     */
    private static boolean usernameTaken(String username) throws SQLException {
        if (!Objects.equals(table, "Test")) { // if you're not testing, table should be login
            table = "Login";
            resultSet = statement.executeQuery("SELECT COUNT(1) FROM login WHERE username = '" + username + "';");
        } else {
            resultSet = statement.executeQuery("SELECT COUNT(1) FROM test WHERE username = '" + username + "';");

        }
        return inDB(resultSet);
    }

    /**
     * Determines whether an inputted tournament name is available.
     *
     * @param tournamentID name of tournament
     * @return true if tournament name is available, false if not
     * @throws SQLException thrown if failed to execute query on database
     */
    private static boolean tournamentTaken(String tournamentID) throws SQLException {
        //if (!Objects.equals(table, "Test")) // if you're not testing, table should be mastertournament
        table = "mastertournament";
        resultSet = statement.executeQuery("SELECT COUNT(1) FROM mastertournament WHERE tournamentid = '" + tournamentID + "';");
        return inDB(resultSet);
    }

    /**
     * Determines if the inputted username password combo is valid by checking if it is in the login database
     *
     * @param username the username being checked
     * @param password the password being checked
     * @return true if the login is valid, false if not
     * @throws SQLException          thrown if failed to check database info
     * @throws FileNotFoundException thrown if failed to get info from key file
     */
    public static boolean validLogin(String username, String password) throws SQLException, FileNotFoundException {
        if (!Objects.equals(table, "Test")) { // if you're not testing, table should be login
            table = "login";
            resultSet = statement.executeQuery("SELECT COUNT(1) FROM login WHERE username = '" + username + "' AND password = '" + Encryptor.shiftMessage(password, keyInfo, false) + "';");
        } else {
            resultSet = statement.executeQuery("SELECT COUNT(1) FROM test WHERE username = '" + username + "' AND password = '" + Encryptor.shiftMessage(password, keyInfo, false) + "';");
        }
        return inDB(resultSet);
    }

    /**
     * Determines whether the resultSet found what the query looked for
     *
     * @param rs the result of the counting query
     * @return true if the counting query is 1, false if 0
     * @throws SQLException thrown if failed to check database info
     */
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

    /**
     * Adds an account to the database, specifically the login and accounts table. Unless you're testing, then it only adds to test table.
     * Only adds account if the username is not taken
     *
     * @param usernameInp username of account to add
     * @param passwordInp password of account to add
     * @return true if added successfully, false if username is already being used
     * @throws SQLException thrown if database failed to add row
     */
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

    /**
     * Adds a user to a tournament table
     *
     * @param usernameInp     the username to be added to the tournament table
     * @param tournamentTable the tournament table to be added to
     * @return true if the account was added successfully, false if the username is already taken in the table
     * @throws SQLException thrown if failed to write to database
     */
    public static boolean addToTournament(String usernameInp, String tournamentTable) throws SQLException {
        table = tournamentTable;
        resultSet = statement.executeQuery("SELECT COUNT(1) FROM " + table + " WHERE username = '" + usernameInp + "';");
        if (!inDB(resultSet)) {
            statement.executeUpdate("INSERT INTO " + table + "(username,wins,gamesleft) VALUES ('" + usernameInp + "', 0, '5');");
            updateData();
            return true;
        }
        return false; // account already in tournament
    }

    /**
     * Removes a row/user from a tournament table
     *
     * @param usernameInp     the row/user to be removed
     * @param tournamentTable the table the user is being removed from
     * @return true if removed successfully, false if there is no account with the inputted username in the table
     * @throws SQLException thrown if failed to write to database
     */
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

    /**
     * Creates a tournament table with columns for the username, wins, and games left of the players in the tournament.
     * Also writes the tournament name and time started to the mastertournament table, which keeps track of all tournaments
     *
     * @param tableName   name of the tournament/table
     * @param timeStarted the time the tournament started
     * @return true if table was created successfully, false if a tournament with the same name is being used
     * @throws SQLException thrown if failed to write to database
     */
    public static boolean createTournament(String tableName, int timeStarted) throws SQLException {
        if (tournamentTaken(tableName))
            return false;

        statement.executeUpdate("CREATE TABLE " + tableName + "\n" +
                "(\n" +
                "    Username varchar(50) NOT NULL PRIMARY KEY ,\n" +
                "    Wins int NOT NULL DEFAULT 0,\n" +
                "    GamesLeft varchar(50) NOT NULL DEFAULT '5'\n" +
                ");");
        statement.executeUpdate("INSERT INTO mastertournament (tournamentid, timestarted) VALUES ('" + tableName + "', " + timeStarted + ");");
        table = "mastertournament";
        updateData();
        // create table and remove form master table
        return true;
    }

    /**
     * Deletes a tournament table. Also removes this tournament's row from the mastertournament table
     *
     * @param tableName name of the tournament/table to be deleted
     * @return true if deleted successfully, false if there is no tournament table with the inputted name
     * @throws SQLException thrown if failed to write to database
     */
    public static boolean deleteTournament(String tableName) throws SQLException {
        if (!tournamentTaken(tableName))
            return false;

        statement.executeUpdate("DROP TABLE " + tableName);
        statement.executeUpdate("DELETE FROM mastertournament WHERE tournamentid = '" + tableName + "';");
        table = "mastertournament";
        updateData();
        return true;
    }

    /**
     * Returns info from the row corresponding to the inputted username. Be sure to set the desired table in advance.
     * If the table is mastertournament, the username parameter is ignored and all information from mastertournament is outputted
     *
     * @param username the account where information will be gotten from
     * @return String array containing account information (ex: if the table is set to "Login", [username, password (encrypted)])
     * @throws SQLException thrown if failed to write to database
     */
    public static String[] getInfo(String username) throws SQLException {
        if (!Objects.equals(table, "mastertournament"))
            resultSet = statement.executeQuery("SELECT * FROM " + table + " WHERE username = '" + username + "';");
        else
            try {
                resultSet = statement.executeQuery("SELECT * FROM mastertournament");
            } catch (NullPointerException e) {
                return new String[0];
            }
        metaData = resultSet.getMetaData();
        return getHelper(metaData);
    }

    /**
     * Returns all the data from an inputted tournament table
     *
     * @param tournament the tournament name/table where information will be gotten from
     * @return String array containing all the data from the tournament table
     * @throws SQLException thrown if failed to write to database
     */
    public static String[] getUserData(String tournament) throws SQLException {
        resultSet = statement.executeQuery("SELECT * FROM " + tournament);
        metaData = resultSet.getMetaData();
        return getHelper(metaData);
    }

    /**
     * Returns all data objects from the inputted metadata
     *
     * @param metaData the metadata from the result set
     * @return String array containing all data objects from metaData
     * @throws SQLException thrown if failed to write to database
     */
    private static String[] getHelper(ResultSetMetaData metaData) throws SQLException {
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

    /**
     * Removes account from tables. If not testing, removes from Accounts and Login. If testing, only removes from Test.
     * Validates that the username password combo is valid
     *
     * @param username username of account to be deleted
     * @param password password of account to be deleted
     * @return true account deleted successfully, false if username password combo invalid
     * @throws SQLException thrown if failed to write to database
     */
    public static boolean deleteAccount(String username, String password) throws SQLException {
        if (!Objects.equals(table, "Test"))
            table = "Login";
        try {
            if (validLogin(username, password)) {
                String encryptedPassword = Encryptor.shiftMessage(password, keyInfo, false);

                if (Objects.equals(table, "Test")) // if testing
                    statement.executeUpdate("DELETE FROM " + table + " WHERE " + table + ".Username = '" + username + "' AND " + table + ".Password = '" + encryptedPassword + "'");
                else { // if not testing
                    statement.executeUpdate("DELETE FROM accounts WHERE accounts.Username = '" + username + "'");
                    statement.executeUpdate("DELETE FROM login WHERE login.Username = '" + username + "'");
                }
                return true;
            }
        } catch (FileNotFoundException ex) {
            System.out.println("key not found");
        }
        return false;
    }

    /**
     * Updates a row of a table. Be sure to call setTable() beforehand to update the desired table. Data represents
     * what you would like the row to change to. The indexes correspond to the table's column
     *
     * @param data the new row information
     * @throws SQLException thrown if failed to write to database
     */
    public static void update(String[] data) throws SQLException {
        updateData();
        if (data.length > metaData.getColumnCount()) {
            throw new IndexOutOfBoundsException();
        }

        for (int i = 2; i <= metaData.getColumnCount(); i++) {
            if (Objects.equals(table, "mastertournament")) { // changing time started
                statement.executeUpdate("UPDATE mastertournament SET " + metaData.getColumnName(i) + " = '" + Integer.parseInt(data[i - 1]) + "' WHERE tournamentid = '" + data[0] + "';");
            } else if ((Objects.equals(table, "Test") || Objects.equals(table, "Login")) && i == 2) // changing a password
                statement.executeUpdate("UPDATE " + table + " SET " + metaData.getColumnName(i) + " = '" + Encryptor.shiftMessage(data[i - 1], keyInfo, false) + "' WHERE username = '" + data[0] + "';");
            else // changing any column other than the username or password (all other columns are integers)
                statement.executeUpdate("UPDATE " + table + " SET " + metaData.getColumnName(i) + " = " + Integer.parseInt(data[i - 1]) + " WHERE username = '" + data[0] + "';");
        }
        updateData();
    }

    /**
     * Returns an array in this format [user1,score1,user2,score2,...] where the user and their corresponding score
     * are in descending order (i.e. index 0 is the username for the player with the highest score, index 1 is their highest score)
     * should specify table before running (Accounts.setTable())
     *
     * @param num the number of users in the table to return, if zero, returns all users
     * @return String array containing user information
     * @throws SQLException thrown if failed to write to database
     */
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

    /**
     * Clears all rows from a table. Be sure to call setTable() beforehand
     *
     * @throws SQLException thrown if failed to write to database
     */
    public static void clearAll() throws SQLException {
        statement.executeUpdate("DELETE FROM " + table);
        if (!Objects.equals(table, "Test")) // if not testing
            table = "Accounts";
        updateData();
    }

    /**
     * Creates a connection with the database. Calls getKeyInfo. Sets table to the inputted table
     *
     * @param table the desired table to be worked with
     */
    public static void initialize(String table) {
        try {
            Database.table = table;
            getKeyInfo(); // reads in and stores info from key file

            Connection connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            statement = connection.createStatement();
            updateData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) { // key file not found
            throw new RuntimeException(e);
        }
    }
}