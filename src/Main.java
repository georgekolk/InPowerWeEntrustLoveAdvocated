import java.io.File;
import java.util.ArrayList;

public class Main {

    private static ArrayList<String> instagramBlogList = new ArrayList<String>();
    private static ArrayList<InstaframPost> singleInstagramPagePosts = null;

    public static void main(String[] args)throws Exception {

        LoadConf config = new LoadConf(new File("config.json"));
        //System.out.println("config.returnDBConnectionString(): " + config.returnDBConnectionString());

        DbHandler dbHandler = DbHandler.getInstance(config.returnDBConnectionString());
        MariaDbHandler mariaDbHandler = MariaDbHandler.getInstance("jdbc:mariadb://172.22.33.7/testdatabase");

        instagramBlogList = config.returnBlogList();

        InstagramPageLoad instagramPageLoader = InstagramPageLoad.getInstance();

        for (String omgBlogFromBlogLIst:instagramBlogList) {
            singleInstagramPagePosts = instagramPageLoader.getInstagramPostArray(omgBlogFromBlogLIst);
            //System.out.println(omgBlogFromBlogLIst + " " + singleInstagramPagePosts.size());
        }

        instagramPageLoader.destroyWebDriwer();


        for (InstaframPost item:singleInstagramPagePosts) {
            try {
                String content = InstagramGraphQLProcessing.getPage(item.getPostId());
                InstagramGraphQLProcessing.parseAndUpdate(item, content);

                dbHandler.createTable(item.getBlogName());

                mariaDbHandler.createTable(item.getBlogName());

                boolean succees =  dbHandler.updatePosts(item);
                boolean succeesMaria =  mariaDbHandler.updatePosts(item);

                if (item.postContent().size() > 0 && item.postContent() != null && succees) {
                    for (Object postContentUrl : item.postContent()) {
                        HttpDownloadUtility.downloadFile(postContentUrl.toString(), "C:\\KKK\\" + item.getBlogName());
                        //System.out.println("dbHandler.updatePosts(item): " + item.toString() + " " + succees);
                        //System.out.println("mariaDbHandler.updatePosts(item): " + item.toString() + " " + succeesMaria);
                    }
                }else{
                    //System.out.println("CANT DOWNLOAD postContent empty");
                }
                /*try {
                    dbHandler.addColumn(item.getBlogName(),"likes","INTEGER");
                }catch (Exception eS){
                    eS.printStackTrace();
                }*/

                //dbHandler.updatePostsLikes(item);

            }catch (Exception e){
                e.printStackTrace();
            }
        }



        //https://www.instagram.com/p/BvwY3HOBNg9/?__a=1

    }
}
