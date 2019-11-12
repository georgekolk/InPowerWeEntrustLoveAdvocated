import java.io.File;
import java.util.ArrayList;

public class Main {

    private static ArrayList<String> instagramBlogList = new ArrayList<String>();
    private static ArrayList<InstaframPost> singleInstagramPagePosts = null;

    public static void main(String[] args)throws Exception {
        LoadConf config = new LoadConf(new File("config.json"));
        instagramBlogList = config.returnBlogList();

        InstagramPageLoad instagramPageLoader = InstagramPageLoad.getInstance();

        for (String omgBlogFromBlogLIst:instagramBlogList) {
            singleInstagramPagePosts = instagramPageLoader.getInstagramPostArray(omgBlogFromBlogLIst);
            System.out.println(omgBlogFromBlogLIst + " " + singleInstagramPagePosts.size());
        }

        instagramPageLoader.destroyWebDriver();

        for (InstaframPost item:singleInstagramPagePosts) {
            try {
                String content = InstagramGraphQLProcessing.getPage(item.getPostId());
                InstagramGraphQLProcessing.parseAndUpdate(item, content);

                if (item.getPostContent().size() > 0 && item.getPostContent() != null) {
                    for (Object postContentUrl : item.getPostContent()) {
                        HttpDownloadUtility.downloadFile(postContentUrl.toString(), config.returnSaveDir(), item);
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }
}
