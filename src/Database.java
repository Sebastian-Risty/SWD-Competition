import java.io.*;
import java.nio.channels.FileLockInterruptionException;
import java.sql.*;
import java.util.InputMismatchException;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

// on executeUpdate vs executeQuery
// https://stackoverflow.com/questions/21276059/no-results-returned-by-the-query-error-in-postgresql

public class Database {
    private static final String DATABASE_URL = "jdbc:postgresql://s-l112.engr.uiowa.edu/swd_db08";
    private static final String USERNAME = "swd_student08";
    private static final String PASSWORD = "engr-2022-08";
    private static ResultSet resultSet;
    private static ResultSetMetaData metaData;
    private static Statement statement;
    private static String table;
    private static final String KEY_NAME = "key";

    public static void setTable(String table) {
        Database.table = table;
    }

    // refreshes database
    private static void updateData() throws SQLException {
//        connection = DriverManager.getConnection(DATABASE_URL,USERNAME,PASSWORD);
//        statement = connection.createStatement();

        // all accounts ordered by score from highest to lowest
        if(Objects.equals(table, "Accounts"))
            resultSet = statement.executeQuery("SELECT * FROM " + table + " ORDER BY totalwins DESC");
        else if (Objects.equals(table, "Test")) {
            resultSet = statement.executeQuery("SELECT * FROM " + table + " ORDER BY score DESC");
        } else if (Objects.equals(table, "Tournament"))
            resultSet = statement.executeQuery("SELECT * FROM " + table + " ORDER BY wins DESC");
        else
            resultSet = statement.executeQuery("SELECT * FROM " + table + " ORDER BY username ");


        metaData = resultSet.getMetaData();
    }

    private static boolean usernameTaken(String username) throws SQLException {
        if(!Objects.equals(table, "Test")) // if you're not testing, table should be login
            table = "Login";

        resultSet = statement.executeQuery("SELECT COUNT(1) FROM " + table + " WHERE username = '" + username + "';");
        return inDB(resultSet);
    }


    // checks that the user's login is valid. If the function runs without throwing an SQLException, the login is valid
    // if not, either the username or password is wrong
    public static boolean validLogin(String username, String password) throws SQLException, FileNotFoundException {
        if (!Objects.equals(table, "Test")) // if you're not testing, table should be login
            table = "login";

        String[] keyInfo = Encryptor.getFileInfo(KEY_NAME);
        if (Objects.equals(keyInfo[0], "Error: file \"" + KEY_NAME + "\" not found")) { // if the key is not found
            System.out.println("Error: key \"" + KEY_NAME + "\" not found");
            throw new FileNotFoundException();
        }

//        String[] updatedKeyInfo = keyInfo;
//        resultSet = statement.executeQuery("SELECT shiftindex FROM login WHERE username = " + username);
//
//        updatedKeyInfo[0] = resultSet.getMetaData().toString();
//
//        String encryptedPassword = Encryptor.shiftMessage(password,updatedKeyInfo,false);

        resultSet = statement.executeQuery("SELECT COUNT(1) FROM " + table + " WHERE username = '" + username + "' AND password = '" + Encryptor.shiftMessage(password,keyInfo,false) + "';");
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
    public static boolean addAccount(String usernameInp, String passwordInp) throws SQLException, FileNotFoundException {
        if (usernameTaken(usernameInp)) {
            // username in use
            return false;
        }

        String[] keyInfo = Encryptor.getFileInfo(KEY_NAME);
        if (Objects.equals(keyInfo[0], "Error: file \"" + KEY_NAME + "\" not found")) { // if the key is not found
            System.out.println("Error: key \"" + KEY_NAME + "\" not found");
            throw new FileNotFoundException();
        }

        String encryptedPassword = Encryptor.shiftMessage(passwordInp,keyInfo,false);


        if(Objects.equals(table, "Test"))
            statement.executeUpdate("INSERT INTO test (username, password, score) VALUES ('" + usernameInp + "', '" + encryptedPassword + "', 0);");
        else {
            statement.executeUpdate("INSERT INTO Login (username, password, shiftindex) VALUES ('" + usernameInp + "', '" + encryptedPassword + "', '" + keyInfo[0] + "');");
            statement.executeUpdate("INSERT INTO accounts (username) VALUES ('" + usernameInp + "');");
            statement.executeUpdate("INSERT INTO tournament (username) VALUES ('" + usernameInp + "');");
        }
//        Random r = new Random();
//        int newPos = r.nextInt((25 - 1) + 1) + 1; // update key shiftIndex
//        Encryptor.updateKeyFile(KEY_NAME, newPos);


        updateData();
        return true;
    }

    // returns the info of the inputted username
    // should specify table before running (Accounts.setTable())
    public static String[] getInfo(String username) throws SQLException, FileNotFoundException {
        resultSet = statement.executeQuery("SELECT * FROM " + table + " WHERE username = '" + username + "';");
        metaData = resultSet.getMetaData();
        int numColumns = metaData.getColumnCount();

        StringBuilder output = new StringBuilder();
        while (resultSet.next()) {
            for (int i = 1; i <= numColumns; i++) {
                if(Objects.equals(table, "Login") || Objects.equals(table, "Test") && (i == 2)){
                    String[] keyInfo = Encryptor.getFileInfo(KEY_NAME);
                    if (Objects.equals(keyInfo[0], "Error: file \"" + KEY_NAME + "\" not found")) { // if the key is not found
                        System.out.println("Error: key \"" + KEY_NAME + "\" not found");
                        throw new FileNotFoundException();
                    }
                    output.append(Encryptor.shiftMessage(resultSet.getObject(i).toString(),keyInfo,true)).append(",");
                }
                else
                    output.append(resultSet.getObject(i)).append(",");
            }
        }
        System.out.println(output);

        return output.toString().split(",");
        
    }

    // returns true if account was successfully deleted, false if not
    public static boolean deleteAccount(String username, String password) throws SQLException {
        if(!Objects.equals(table, "Test"))
            table = "Login";
        // ensures the inputted username and password are valid, this may not be necessary depending on how we implement this function
        try {
            if (validLogin(username, password)) {
                String[] keyInfo = Encryptor.getFileInfo(KEY_NAME);
                if (Objects.equals(keyInfo[0], "Error: file \"" + KEY_NAME + "\" not found")) { // if the key is not found
                    System.out.println("Error: key \"" + KEY_NAME + "\" not found");
                    throw new FileNotFoundException();
                }

                String encryptedPassword = Encryptor.shiftMessage(password,keyInfo,false);


                if (Objects.equals(table, "Test"))
                    statement.executeUpdate("DELETE FROM " + table + " WHERE " + table + ".Username = '" + username + "' AND " + table + ".Password = '" + encryptedPassword + "'");
                else {
                    statement.executeUpdate("DELETE FROM accounts WHERE accounts.Username = " + username);
                    statement.executeUpdate("DELETE FROM tournament WHERE tournament.Username = " + username);
                    statement.executeUpdate("DELETE FROM login WHERE login.Username = " + username);
                }
                updateData();
                return true;
            }
        } catch (FileNotFoundException ex){
            System.out.println("key not found");
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
    // should specify table before running (Accounts.setTable())
    public static void update(String[] data) throws SQLException, FileNotFoundException {
        updateData();
        if (data.length > metaData.getColumnCount()) {
            throw new IndexOutOfBoundsException();
        }

        for (int i = 1; i <= metaData.getColumnCount(); i++) {
//            System.out.println(metaData.getColumnName(i));
//            System.out.println(data[i - 1]);
//            System.out.println(data[0]);
            if (Objects.equals(table,"Test") && i != 3) // not changing the username
                statement.executeUpdate("UPDATE " + table + " SET " + metaData.getColumnName(i) + " = '" + data[i - 1] + "' WHERE username = '" + data[0] + "';");
            else if (Objects.equals(table,"Test") )// changing the username
                    statement.executeUpdate("UPDATE " + table + " SET " + metaData.getColumnName(i) + " = " + Integer.parseInt(data[i - 1]) + " WHERE username = '" + data[0] + "';");
            else if (Objects.equals(table,"login")) {
                String[] keyInfo = Encryptor.getFileInfo(KEY_NAME);
                if (Objects.equals(keyInfo[0], "Error: file \"" + KEY_NAME + "\" not found")) { // if the key is not found
                    System.out.println("Error: key \"" + KEY_NAME + "\" not found");
                    throw new FileNotFoundException();
                }

                if (i == 1)
                    statement.executeUpdate("UPDATE " + table + " SET " + metaData.getColumnName(i) + " = '" + data[i - 1] + "' WHERE username = '" + data[0] + "';");
                else if (i == 2) {
                    statement.executeUpdate("UPDATE " + table + " SET " + metaData.getColumnName(i) + " = '" + Encryptor.shiftMessage(data[i - 1],keyInfo,false) + "' WHERE username = '" + data[0] + "';");
                } else// changing the username
                    statement.executeUpdate("UPDATE " + table + " SET " + metaData.getColumnName(i) + " = " + Integer.parseInt(data[i - 1]) + " WHERE username = '" + data[0] + "';");
            }
            else if (i != 1) // not changing the username
                statement.executeUpdate("UPDATE " + table + " SET " + metaData.getColumnName(i) + " = " + Integer.parseInt(data[i - 1]) + " WHERE username = '" + data[0] + "';");
            else // changing the username
                statement.executeUpdate("UPDATE " + table + " SET " + metaData.getColumnName(i) + " = '" + data[i - 1] + "' WHERE username = '" + data[0] + "';");
            }
        updateData();

    }



    // returns an array in this format [user1,score1,user2,score2,...] where the user and their corresponding score
    // are in descending order (i.e. index 0 is the username for the player with the highest score, index 1 is their highest score)
    // should specify table before running (Accounts.setTable())
    public static String[] getTopPlayers(int num) throws SQLException { // MAKE SURE TABLE IS SET TO ACCOUNTS !!!
        StringBuilder output = new StringBuilder();
        if(Objects.equals(table, "Accounts"))
            resultSet = statement.executeQuery("SELECT Username, Score FROM " + table + " ORDER BY Score DESC");
        else if (Objects.equals(table,"Tournament")) {
            resultSet = statement.executeQuery("SELECT Username, Wins FROM " + table + " ORDER BY Wins DESC");
        } else if (Objects.equals(table,"Test")) {
            resultSet = statement.executeQuery("SELECT Username, Score FROM " + table + " ORDER BY Score DESC");
        } else{
            throw new InputMismatchException(); // if thrown, you need to set table to either accounts or tournament before the function is called
        }
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
        updateData();
    }

    public static void initialize(String table) {
        try {
//            generateKey();
//            Class.forName("org.postgresql.Driver");
            Database.table = table;
            Connection connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            statement = connection.createStatement();
            updateData();
        } catch (SQLException e) { // | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
//        catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }

    }

//    private static void generateKey(){
//        try {
//            Random r = new Random();
//            int position = r.nextInt((25 - 1) + 1) + 1; // shiftIndex
//            int numN = r.nextInt((25 - 8) + 1) + 8; // number of key values
//
//            System.out.println("Enter the name for your key file:");
//            String fileName = "key";
//            boolean alreadyExists = false;
//            String output;
//            try {
//                File keyFile = new File(fileName);
//                if (!keyFile.createNewFile()) {
//                    alreadyExists = true;
//                    output = "Key file \"" + keyFile.getName() + "\" created successfully";
//                } else {
//                    output = "Filename \"" + fileName + "\" already exists";
//
//                }
//            } catch (IOException e) {
//                output = "Error creating file";
//            }
//            if(!alreadyExists) {
//                try {
//                    FileWriter keyWriter = new FileWriter(fileName);
//                    StringBuilder fileContent = new StringBuilder(position + "\n");
//                    for (int i = 0; i < numN; i++)
//                        fileContent.append(r.nextInt(alphabet.length - 1) + 1).append(",");
//                    fileContent.deleteCharAt(fileContent.length() - 1); // remove the last comma
//                    keyWriter.write(fileContent.toString());
//                    keyWriter.close();
//                    // return "Successfully wrote to file \"" + fileName + "\"";
//                } catch (IOException ex) {
//                    // return "Error writing to key file";
//                }
//            }
//
//
//        } catch (InputMismatchException ex) {
//            System.out.println("Error: non numerical input");
//        }
//    }


}
