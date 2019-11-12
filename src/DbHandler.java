import org.sqlite.JDBC;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbHandler {

    private static String CON_STR = null;
    private static DbHandler instance = null;

    public static synchronized DbHandler getInstance(String connectionString) throws SQLException {
        //CON_STR = connectionString;
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
                + "	postId text NOT NULL UNIQUE,\n"
                + "	tags text NOT NULL,\n"
                + "	filenames text NOT NULL,\n"
                + " date TIMESTAMP NOT NULL);";

        try (Statement stmt = this.connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean updatePosts(InstaframPost instaframPost){
        boolean successful = true;
        String sql = "INSERT INTO '" + this.prepareYourAnus(instaframPost.getBlogName()) + "' ('postId', 'tags', 'filenames', 'date') VALUES('" + instaframPost.getPostId() + "', '" + this.prepareYourAnus(instaframPost.getTags()) + "', '" + instaframPost.returnFilenamesInOneString() + "', CURRENT_TIMESTAMP);";
        //System.out.println("sql string: " + sql.toString());

        try (Statement stmt = this.connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("DbHandler insert Error: " + instaframPost.toString() + e.getMessage());
            if (e.getMessage().contains("SQLITE_CONSTRAINT_UNIQUE")){
                successful = false;
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
        //String sql = "SELECT postId, tags, filenames, date FROM latex WHERE tags LIKE '%latex%';";
        String sql = "SELECT postId, tags, filenames, date FROM latex WHERE date('now');";


        List<InstaframPost> tehInstaframPost = new ArrayList<InstaframPost>();

        try (Statement stmt  = connection.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set



            while (rs.next()) {


                System.out.println(rs.getString("postId") +  "\t" +
                        rs.getString("tags") + "\t" +
                        rs.getString("filenames"));

                /*
    public InstaframPost(String blogName, String date, String postId, String tags){

                /
                 */

                tehInstaframPost.add(new InstaframPost("latex",rs.getString("date"),rs.getString("postId"),rs.getString("tags")));

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        for (InstaframPost item:tehInstaframPost) {
            item.showAll();
        }

    }


}