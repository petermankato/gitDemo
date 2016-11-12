/* childrenInvites.java - how to connect to MySQL and run some basic commands
Michelle Griebel - michelle.griebel@my.southcentral.edu
Project: prjSQL
Sources: Web Explorations
Written: 11/09/16
Revised: 11/12/16
 */
package prjProjects;

/*
1. Download the JDBC driver from here https://dev.mysql.com/downloads/connector/j/
2. Unzip the file and put the .jar folder in a folder that is in the CLASSPATH
   For Mac this is  [yourHardDrive]/Library/Java/Extensions
   Or add the directory where it is located to the CLASSPATH (https://docs.oracle.com/javase/tutorial/essential/environment/paths.html)
3. Using phpMyAdmin and your localhost (WAMP or AMPPS) to create a database and table with sample data
4. Change the database name, userID, and password in the getConnection( ) string.
5. Change the field names in the rs.getString( ) functions
 */


/**
 * <strong>childrenInvites.java</strong> - description
 *
 * @author Michelle Griebel - michelle.griebel@my.southcentral.edu
 *         Written: 11/09/16<br >
 *         Revised: 11/12/16<br>
 */
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

    /**
     * This class demonstrates how to connect to MySQL and run some basic commands.
     * I will use CRUD to do the heavy lifting.
     *
     *Access to localhost using: userName "root"; password ""; serverName "localhost".
     */
    public class childrenInvites
    {
        // Set up two constants to be used in this test for database and table.
        private final static String DBF_NAME = "testingsql";
        private final static String TABLE_NAME = "inventory";

        // The mySQL username
        private final String userName = "root";

        // The mySQL password (left empty "" )
        private final String password = "";

        // Name of the computer running mySQL
        private final String serverName = "localhost";


        public static void main(String[] args)
        {
            // Simulate data input by user, stored in an array
            String[ ] dataInput = {"Beauty and the Beast", "BB18624NFCBE", "11", "55", "44"};

            childrenInvites app = new childrenInvites();

            app.dropTable(TABLE_NAME);
            app.createTable(TABLE_NAME);
            app.insertData(dataInput, TABLE_NAME);
            app.insertData(dataInput, TABLE_NAME);
            app.showTable(TABLE_NAME);

            // Simulate new data input by user
            dataInput[0] = "Cinderella";
            dataInput[1] = "CIN18624NFCLA";
            dataInput[2] = "24";
            dataInput[3] = "100";
            dataInput[4] = "90";
            app.update(dataInput,1,TABLE_NAME);
            app.showTable(TABLE_NAME);
            app.delete(1,TABLE_NAME);
            app.showTable(TABLE_NAME);
            // app.dropTable(TABLE_NAME);
        } // end of main( )

        /**
         * createTable - create a new table in the database
         *               if one already exists no action is taken
         * @param tableName
         */
        public void createTable(String tableName) {
            Connection conn = null;
            @SuppressWarnings("unused")
            String sql = "";
            ResultSet rs = null;
            Statement stmt = null;
            boolean createTable = true;

            // Connecting to MySQL
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                // javaMySQL is database - root is username - mysql is password
                conn = DriverManager.getConnection("jdbc:mysql://localhost/testingsql", "root", "mysql");
                stmt = conn.createStatement();
                rs = stmt.executeQuery("SELECT * FROM inventory");
            } catch (Exception e) {
                System.out.println(e);
            }

        try
        {
           conn = this.getConnection();
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: Could not connect to the database");
            e.printStackTrace();
            return;
        }

        // Creating a table
        try
        {
            // Check to see if a table already exists
            DatabaseMetaData meta = conn.getMetaData();
            rs = meta.getTables(null, null, "%", null);
            // Does the table already exist?
            // Loop through looking for the table name
            while (rs.next())
            {
              if(rs.getString(3).equals(tableName))
              {
                   createTable=false;
                   break;
               }
            }

            if(createTable)
            {
                String createString =
                        "CREATE TABLE " + tableName + " ( " +
                                "id INTEGER NOT NULL AUTO_INCREMENT, " +
                                "description varchar(40) NOT NULL, " +
                                "cardNumber varchar(40) NOT NULL, " +
                                "quantity varchar(20) NOT NULL, " +
                                "originalCost char(20) NOT NULL, " +
                                "sellingPrice char(5), " +
                                "PRIMARY KEY (id))";
                this.executeUpdate(conn, createString);
                System.out.println("Created table named:" + tableName);
            }
            else // table already exists
            {
                System.out.println(tableName + " already exists. No action taken.");
            }
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: Could not create the table named: " + tableName);
            e.printStackTrace();

        }
        // release the resources
    } // end of createTable( )


    /**
     * delete( ) - remove a record based on id
     * @param thisID - the id of the record to be removed
     * @param thisTable
     */
    public void delete(int thisID, String thisTable)
    {
        Connection conn = null;
        String sql = "";

        try
        {
            conn = this.getConnection();
        } catch (SQLException e) {
            System.out.println("ERROR: Could not connect to the database");
            e.printStackTrace();
        }

        // Delete a record
        try
        {
          /* REFERENCE SQL:
           *  sql = "DELETE FROM inventory WHERE id = 5";
           */
            sql = "DELETE FROM " + thisTable + " WHERE id = " + thisID;
            this.executeUpdate(conn, sql);
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: Could delete the record using this SQL: " + sql);
            e.printStackTrace();
        }
        // Release the resources
        finally { releaseResource(null, null, conn); }
    }// end of delete( )

    /**
     * dropTable - removes the specific table from the database
     * @param inventory
     */
    public void dropTable(String inventory)
    {
        String sql = "";
        Connection conn = null;
        try
        {
            conn = this.getConnection();
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: Could not connect to the database");
            e.printStackTrace();
        }
        try
        {
            sql = "DROP TABLE IF EXISTS " + inventory;
            this.executeUpdate(conn, sql);
            System.out.println("Dropped the table named:" + inventory);
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: Could not drop the table using this SQL: " + sql);
            e.printStackTrace();
            return;
        }
        finally { releaseResource(null,null, conn);}
    } // end of dropTable( )


    /**
     * insertData - inserts data from the array into the designated table
     * @param dataArray  - Must be in the indexed order:<br>
     *                     0-description 1-cardNumber 2-quantity 3-originalCost 4-sellingPrice
     * @param thisTable
     */
    public void insertData(String[ ] dataArray, String thisTable)
    {
        Connection conn = null;
        String sql = "";
        try
        {
            conn = this.getConnection();
        } catch (SQLException e) {
            System.out.println("ERROR: Could not connect to the database");
            e.printStackTrace();
        }

        // Insert the data
        try
        {
          /* REFERENCE SQL:
           *  sql = "INSERT INTO inventory (description, cardNumber, quantity, originalCost, sellingPrice)"
           *    + "VALUES ('Beauty and the Beast', 'BB18624NFCBE', '11', '55', '44')";
           */
            sql = "INSERT INTO " + thisTable + " (description, cardNumber, quantity, originalCost, sellingPrice) VALUES("
                    + "'" + dataArray[0] + "', "
                    + "'" + dataArray[1] + "', "
                    + "'" + dataArray[2] + "', "
                    + "'" + dataArray[3] + "', "
                    + "'" + dataArray[4] + "')";
            this.executeUpdate(conn, sql);
            System.out.println("Inserted a record:" + dataArray[0] + " " + dataArray[1]);
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: Could not insert the data using this SQL: " + sql);
            e.printStackTrace();
        }
        // Release the resources
        finally { releaseResource(null, null, conn); }
    }// end of insertData( )


    /**
     * showTable( ) - display the contents of the designated table
     * @param tableName
     */
    public void showTable(String tableName)
    {
        String sql = "";
        Statement stmt = null;
        ResultSet rs = null;
        int id = 0;
        String description = "";
        String cardNumber = "";
        String quantity = "";
        String originalCost = "";
        String sellingPrice = "";

        // Connect to MySQL
        Connection conn = null;
        try {
            conn = this.getConnection();
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: Could not connect to the database");
            e.printStackTrace();
        }

        // Select the data
        try
        {
            sql = "SELECT * FROM inventory";
            // Run the SQL and save in Result Set
            stmt = conn.createStatement( );
            rs = stmt.executeQuery(sql);
            System.out.println("\n\t\t\t\tOUR Inventory:");
            System.out.println("\nID\tDESCRIPTION\t\t\t\t\tCARD NUMBER\t\tQUANTITY\tORIGINAL COST - SELLING PRICE");
            System.out.println("***************************************************************************************"
                    + "******************************");
            while (rs.next())
            {
                id = rs.getInt("id");
                description = rs.getString("description");
                cardNumber = rs.getString("cardNumber");
                quantity = rs.getString("quantity");
                originalCost = rs.getString("originalCost");
                sellingPrice = rs.getString("sellingPrice");
                System.out.printf("%d\t%s\t\t%s\t\t%s \t\t$%s \t\t$%s\n",
                        id, description, cardNumber, quantity, originalCost, sellingPrice);
            }
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: Could not SELECT data using this SQL: " + sql);
            e.printStackTrace();
        }
        // Release the resources
        finally { releaseResource(rs, stmt, conn); }
    } // end of showTable( )


    /**
     * update( ) - update a specific record using the contents of the array based on a specific ID
     * @param dataArray Must be in the indexed order:<br>
     *                  0-description 1-partNumber 2-quantity 3-originalCost 4-sellingPrice
     * @param thisID
     * @param thisTable
     */
    public void update(String[ ] dataArray, int thisID, String thisTable)
    {
        Connection conn = null;
        String sql = "";

        try
        {
            conn = this.getConnection();
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: Could not connect to the database");
            e.printStackTrace();
        }

        // Update a record
        try
        {
          /* REFERENCE SQL:
           * sql = "UPDATE inventory "
           *      + "SET description='Cinderella', cardNumber='CIN18624NFCLA', "
           *      + "quantity='24', originalCost='100', sellingPrice='90' "
           *      + "WHERE id=";
           */
            sql = "UPDATE " + thisTable
                    + " SET description='"  + dataArray[0] + "', "
                    + "cardNumber='"     + dataArray[1] + "', "
                    + "quantity='"       + dataArray[2] + "', "
                    + "originalCost='"      + dataArray[3] + "', "
                    + "sellingPrice='" + dataArray[4] + "' "
                    + "WHERE id=" + thisID;
            this.executeUpdate(conn, sql);
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: Could not update the record using this SQL: " + sql);
            e.printStackTrace();
        }
        // Release the resources
        finally { releaseResource(null, null, conn); }
    }// end of insertData( )


    /* ******************************
     * HEAVY LIFTING - Common to the CRUD methods.
     * Create - Insert; Read - Select; Update - Update; Delete - Delete
     *********************************/
    /**
     * getConnection( ) - Get a new database connection
     *
     * @return Connection
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException
    {
        Connection conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", this.userName);
        connectionProps.put("password", this.password);
        conn = DriverManager.getConnection("jdbc:mysql://localhost/testingsql", "root", "mysql");
        return conn;
    }

    /**
     * executeUpdate( ) - run a SQL command which does NOT return a resultSet:
     * CREATE/INSERT/UPDATE/DELETE/DROP
     *
     * @throws SQLException If something goes wrong
     * @return boolean if command was successful or not
     */
    public boolean executeUpdate(Connection conn, String command) throws SQLException
    {
        Statement stmt = null;
        try
        {
            stmt = conn.createStatement();
            stmt.executeUpdate(command); //throws an SQLException if it fails
            return true;
        }
        finally
        {
            // This will run whether we throw an exception or not
            if (stmt != null) { stmt.close(); }
        }
    } // end of executeUpdate( )


    /**
     * executeQuery - Run a SQL command which returns a result set:
     * SELECT
     *
     * @throws SQLException If something goes wrong
     * @return ResultSet containing data from the table
     */
    public ResultSet executeQuery(Connection conn, String command) throws SQLException
    {
        ResultSet rs;
        Statement stmt = null;
        try
        {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(command); // This will throw a SQLException if it fails
            return rs;
        }
        finally
        {
            // This will run whether we throw an exception or not
            if (stmt != null) { stmt.close(); }
        }
    } // end of executeQuery( )


    /**
     * releaseResource( ) - Free up the system resources that were opened.
     *                      If not used,  a null will be passed in for that parameter.
     * @param rs - Resultset
     * @param ps - Statement
     * @param conn - Connection
     */
    public void releaseResource(ResultSet rs, Statement ps, Connection conn )
    {
        if (rs != null)
        {
            try { rs.close(); }
            catch (SQLException e) { /* ignored */}
        }
        if (ps != null)
        {
            try { ps.close(); }
            catch (SQLException e) { /* ignored */}
        }
        if (conn != null)
        {
            try { conn.close();}
            catch (SQLException e) { /* ignored */}
        }
    } // end of releaseResource( )
} // End of childrenInvites

