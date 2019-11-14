import com.google.gson.JsonParseException;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InstagramGraphQLProcessing {
    private static String charset = "UTF-8";
    //private Date date = new Date();
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static public String parseAndUpdate(InstaframPost instaframPost, String content){

        JSONParser parser = new JSONParser();

        JSONObject parsedPageContent = null;
        JSONObject graphql = null;
        JSONObject shortCodeMedia = null;

        try {
            parsedPageContent = (JSONObject) parser.parse(content);
            //System.out.println("obj to string looks like: " + parsedPageContent.toString());


            graphql = (JSONObject) parsedPageContent.get("graphql");
            //System.out.println(graphql.toString());

            shortCodeMedia = (JSONObject) graphql.get("shortcode_media");

            String typename = (String) shortCodeMedia.get("__typename");
            //System.out.println(typename);

            switch (typename) {
                case "GraphSidecar":
                    JSONObject edgeSidecarToChildren = (JSONObject) shortCodeMedia.get("edge_sidecar_to_children");
                    JSONArray edges = (JSONArray) edgeSidecarToChildren.get("edges");
/*                  for (Object edge: edges) {
                        //JSONObject node =  edge.get

                        System.out.println(edge.toString());
                    }*/
                    for (int i = 0; i < edges.size(); i++){
                        JSONObject edge = (JSONObject) edges.get(i);
                        JSONObject node = (JSONObject) edge.get("node");
                        String edgeTypename = (String) node.get("__typename");
                        //System.out.println(edgeTypename);
                        //System.out.println(edge.toString());
                        switch (edgeTypename) {
                            case "GraphImage":
                                JSONArray displayResources = (JSONArray) node.get("display_resources");
                                JSONObject image = (JSONObject) displayResources.get(2);
                                String imageURL = (String) image.get("src");
                                instaframPost.setPostContent(imageURL);
                                //System.out.println(imageURL);
                                break;
                            case "GraphVideo":
                                if(shortCodeMedia.containsKey("video_url")) {
                                    String videoUrl = (String) shortCodeMedia.get("video_url");
                                    System.out.println("videoUrl: " + videoUrl);
                                    instaframPost.setPostContent(videoUrl);
                                }else{
                                    throw new JsonParseException("GraphVideo expects video_url in shortCodeMedia but cannot find it");
                                }
                                break;
                        }
                    }

                    break;
                case "GraphImage":
                    JSONArray displayResources = (JSONArray) shortCodeMedia.get("display_resources");
                    JSONObject image = (JSONObject) displayResources.get(2);
                    String imageURL = (String) image.get("src");
                    //System.out.println(imageURL);
                    instaframPost.setPostContent(imageURL);
                    //"display_resources"
                    break;
                case "GraphVideo":
                    if(shortCodeMedia.containsKey("video_url")) {
                        String videoUrl = (String) shortCodeMedia.get("video_url");
                        //System.out.println("videoUrl: " + videoUrl);
                        instaframPost.setPostContent(videoUrl);
                    }else{
                        throw new JsonParseException("GraphVideo expects video_url in shortCodeMedia but cannot find it");
                    }
                    break;
            }

            if(shortCodeMedia.containsKey("taken_at_timestamp")) {
                //taken_at_timestamp
                Long takenAtTimestamp = (Long) shortCodeMedia.get("taken_at_timestamp");
                Date date = new Date(takenAtTimestamp*1000L);
                instaframPost.setDate(dateFormat.format(date));
                //System.out.println("takenAtTimestamp: " + takenAtTimestamp + " " + dateFormat.format(date));
            }

//            if(shortCodeMedia.containsKey("edge_media_preview_like")) {
//                //edge_media_preview_like
//                JSONObject edgeMediaPreviewLike = (JSONObject) shortCodeMedia.get("edge_media_preview_like");
//                if(edgeMediaPreviewLike.containsKey("count")) {
//                    //System.out.println("Likes:" + (long)edgeMediaPreviewLike.get("count"));
//                    instaframPost.setLikes((long)edgeMediaPreviewLike.get("count"));
//                }
//            }

//меняет имя оригинального аккаунта в случае просмотра тегов, создает не теги, а имена блогов. 
//
//            if(shortCodeMedia.containsKey("owner")) {
//                JSONObject owner = (JSONObject) shortCodeMedia.get("owner");
//                if(owner.containsKey("username")) {
//                String username = (String) owner.get("username");
//                instaframPost.setBlogName(username);
//                }
//            }


            JSONObject edgeMediaToCaption = (JSONObject) shortCodeMedia.get("edge_media_to_caption");
                //System.out.println("edgeMediaToCaption: " + edgeMediaToCaption.toString());

                JSONArray edgesArrayFromEdgeMediaToCaption = (JSONArray) edgeMediaToCaption.get("edges");

            //taken_at_timestamp


            if (edgesArrayFromEdgeMediaToCaption.size() > 0) {
                    JSONObject firstElementFromEdgeMediaToCaption = (JSONObject) edgesArrayFromEdgeMediaToCaption.get(0);
                    JSONObject nodeFromEdgeMediaToCaption = (JSONObject) firstElementFromEdgeMediaToCaption.get("node"); //nodeFromEdgeMediaToCaption

                    String tags = (String) nodeFromEdgeMediaToCaption.get("text");

                    //String typename = (String) shortCodeMedia.get("__typename");

                    Matcher m = Pattern.compile("#[A-Za-zА-Яа-я_!0-9]*").matcher(tags);
                    String returnTagsString = " ";
                    while (m.find()) {
                        returnTagsString = returnTagsString.concat(m.group()).concat(" ");
                    }
                    //System.out.println("tags: " + returnTagsString);
                    instaframPost.setTags(returnTagsString);
                    instaframPost.fillEmptinesInside();
                }else {
                    //System.out.println("edgesArrayFromEdgeMediaToCaption.size(): " + edgeMediaToCaption.size());
                }

            } catch (ParseException e) {
            e.printStackTrace();
            try {
                FileUtils.writeStringToFile(new File("Error-" + instaframPost.getBlogName() + "-" + instaframPost.getPostId() + ".log"), content);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }

    static public String getPage(String instagramPostId)throws Exception {
            //URL getPhotos;
            URL getPhotos = new URL("https://www.instagram.com/p/" + instagramPostId + "/?__a=1");

            HttpURLConnection connection = (HttpURLConnection) getPhotos.openConnection();
            StringBuilder content = new StringBuilder();

            connection.setRequestProperty("Accept-Charset", charset);
            connection.setUseCaches(false);
            connection.setRequestProperty("User-Agent", "jHateSMM");

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;

            while ((line = bufferedReader.readLine()) != null)
            {
                content.append(line + "\n");
            }
            bufferedReader.close();
            //connection.disconnect();

            //System.out.println(content.toString());

            return content.toString();
    }
}
