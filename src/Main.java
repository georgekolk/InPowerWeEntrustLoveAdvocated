import java.io.File;
import java.util.ArrayList;

public class Main {

    private static ArrayList<String> instagramBlogList = new ArrayList<String>();
    private static ArrayList<InstaframPost> singleInstagramPagePosts = null;

    public static void main(String[] args)throws Exception {


        LoadConf config = new LoadConf(new File("config.json"));
        DbHandler dbHandler = DbHandler.getInstance(config.returnDBConnectionString());
        instagramBlogList = config.returnBlogList();

        InstagramPageLoad instagramPageLoader = InstagramPageLoad.getInstance();

        if (args.length > 0) {


            singleInstagramPagePosts = instagramPageLoader.getInstagramAllPostsArray(args[0].toString());
            System.out.println(args[0] + " " + singleInstagramPagePosts.size());

                    
                }else{

        for (String omgBlogFromBlogLIst:instagramBlogList) {
            singleInstagramPagePosts = instagramPageLoader.getInstagramPostArray(omgBlogFromBlogLIst);
            System.out.println(omgBlogFromBlogLIst + " " + singleInstagramPagePosts.size());
        }

                
                }


        instagramPageLoader.destroyWebDriver();

        for (InstaframPost item:singleInstagramPagePosts) {
            try {
                String content = InstagramGraphQLProcessing.getPage(item.getPostId());
                InstagramGraphQLProcessing.parseAndUpdate(item, content);

                dbHandler.createTable(item.getBlogName());

                boolean succees =  dbHandler.updatePosts(item);

                if (item.getPostContent().size() > 0 && item.getPostContent() != null && succees) {
                    for (Object postContentUrl : item.getPostContent()) {
                        HttpDownloadUtility.downloadFile(postContentUrl.toString(), config.returnSaveDir() + item.getBlogName());
                    }
                }else{
                    //System.out.println("CANT DOWNLOAD postContent empty");
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
