import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class LoadConf {

    private String tempDir = "C://Temp";
    private ArrayList<String> instagramBlogListArray = new ArrayList<String>();
    private ArrayList<String> instagramTagsListArray = new ArrayList<String>();
    private String saveDir = "";

    private String DBConnectionString = null;

    public LoadConf(File configFile){

        try {
            Scanner s = new Scanner(configFile);
            StringBuilder builder = new StringBuilder();

            while (s.hasNextLine()) builder.append(s.nextLine());

            JSONParser pars = new JSONParser();

            try {

                Object obj = pars.parse(builder.toString());
                JSONObject overallConfig = (JSONObject) obj;


                if(overallConfig.containsKey("instagramBlog666List")){
                    JSONArray instagramBlogList = (JSONArray)overallConfig.get("instagramBlogList");

                    for (int i = 0; i < instagramBlogList.size(); i++) {
                        JSONObject instagramBlogName = (JSONObject) instagramBlogList.get(i);
                        //System.out.println(instagramBlogName.get("name"));
                        instagramBlogListArray.add((String) instagramBlogName.get("name"));
                    }
                }


                    if(overallConfig.containsKey("instagramBlogList")){
                    JSONArray instagramBlogList = (JSONArray)overallConfig.get("instagramBlogList");

                    for (int i = 0; i < instagramBlogList.size(); i++) {
                        JSONObject instagramBlogName = (JSONObject) instagramBlogList.get(i);
                        //System.out.println(instagramBlogName.get("name"));
                        instagramBlogListArray.add((String) instagramBlogName.get("name"));
                    }
                }

                if(overallConfig.containsKey("instagramTagsList")){
                    JSONArray instagramBlogList = (JSONArray)overallConfig.get("instagramTagsList");

                    for (int i = 0; i < instagramBlogList.size(); i++) {
                        JSONObject instagramBlogName = (JSONObject) instagramBlogList.get(i);
                        instagramTagsListArray.add((String) instagramBlogName.get("name"));
                    }
                }

                if(overallConfig.containsKey("DBConnectionString")){
                    this.DBConnectionString = (String)overallConfig.get("DBConnectionString");
                }

                if(overallConfig.containsKey("tempDir")){
                    this.tempDir = (String)overallConfig.get("tempDir");
                }

                if(overallConfig.containsKey("saveDir")){
                    this.saveDir = (String)overallConfig.get("saveDir");
                }


            } catch (ParseException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ArrayList<String> returnBlogList(){
        return instagramBlogListArray;
    }
    public ArrayList<String> returnTagsList(){
        return instagramTagsListArray;
    }
    public String returnDBConnectionString(){
        return DBConnectionString;
    }

    public String returnSaveDir(){
        return saveDir;
    }
}
