import org.sqlite.JDBC;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbHandler {

    private static String CON_STR = null;
    private static DbHandler instance = null;

    public static synchronized DbHandler getInstance(String connectionString) throws SQLException {
        if (instance == null)
            instance = new DbHandler(connectionString);
        return instance;
    }

    private Connection connection;

    private DbHandler(String connectionString) throws SQLException {
        CON_STR = connectionString;
        DriverManager.registerDriver(new JDBC());
        this.connection = DriverManager.getConnection(CON_STR);
    }

    public void createTable(String tableName){
        String sql = "CREATE TABLE IF NOT EXISTS " + this.prepareYourAnus(tableName) + " (\n" //private String blogName
                + "	fileId integer PRIMARY KEY,\n"
                + "	postId text NOT NULL,\n"
                + "	tags text NOT NULL,\n"
                + "	state text NOT NULL,\n"
                + "	fileName text NOT NULL,\n"
                + " filePostDate TIMESTAMP NOT NULL,\n"
                + " UNIQUE(fileName));";

        try (Statement stmt = this.connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean updatePosts(InstaframPost instaframPost){
        boolean successful = true;

        String[] list = instaframPost.returnFilenamesInOneString().split(" ");

        for (String instaframPostFile: list) {

            String sql = "INSERT INTO '" + this.prepareYourAnus(instaframPost.getBlogName()) + "' ('postId', 'tags', 'state', 'fileName', 'filePostDate') VALUES('" + instaframPost.getPostId() + "', '" + this.prepareYourAnus(instaframPost.getTags()) + "', 'NOTHING', '" + instaframPostFile + "', CURRENT_TIMESTAMP);";
            //System.out.println("sql string: " + sql.toString());

            try (Statement stmt = this.connection.createStatement()) {
                stmt.execute(sql);
            } catch (SQLException e) {
                System.out.println("DbHandler insert Error: " + instaframPost.toString() + e.getMessage());
                if (e.getMessage().contains("SQLITE_CONSTRAINT_UNIQUE")){
                    successful = false;
                }
            }
        }

        return successful;
    }

    public void getDatabaseMetaData(){
        try {
            DatabaseMetaData dbmd = this.connection.getMetaData();
            String[] types = {"TABLE"};
            ResultSet rs = dbmd.getTables(null, null, "%", types);
            while (rs.next()) {
                System.out.println(rs.getString("TABLE_NAME"));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String prepareYourAnus(String stringToPrepareYourAnus){
        stringToPrepareYourAnus = stringToPrepareYourAnus.replace(".", "");
        stringToPrepareYourAnus = stringToPrepareYourAnus.replace("explore/tags/", "");
        return stringToPrepareYourAnus;
    }

    public void selectAll(){
        //String sql = "SELECT postId, tags, fileName, date FROM latex WHERE tags LIKE '%latex%';";
        String sql = "SELECT postId, tags, fileName, filePostDate FROM latex WHERE filePostDate('now');";


        List<InstaframPost> tehInstaframPost = new ArrayList<InstaframPost>();

        try (Statement stmt  = connection.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set



            while (rs.next()) {


                System.out.println(rs.getString("postId") +  "\t" +
                        rs.getString("tags") + "\t" +
                        rs.getString("fileName"));

                /*
    public InstaframPost(String blogName, String filePostDate, String postId, String tags){

                /
                 */

                tehInstaframPost.add(new InstaframPost("latex",rs.getString("filePostDate"),rs.getString("postId"),rs.getString("tags")));

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        for (InstaframPost item:tehInstaframPost) {
            item.showAll();
        }

    }


}