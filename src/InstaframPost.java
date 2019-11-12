import org.json.simple.JSONObject;

import java.util.ArrayList;

public class InstaframPost {

    private String blogName;
    private String date;
    private String postId;
    private String tags;
    private ArrayList<String> postContent;
    private boolean filled;
    //private ArrayList<ImageView> storedImages;


    public InstaframPost(String blogName, String date, String postId, String tags){
        this.blogName = blogName;
        this.date = date;
        this.postId = postId;
        this.tags = tags;
        this.filled = false;
        this.postContent = new ArrayList<String>();
    }

    //нужно чтобы морфия работала
    public InstaframPost(){
        this.postContent = new ArrayList<String>();
    }

    public String getBlogName() {return this.blogName;}

    public String getDate(){
        return date;
    }

    public String getPostId(){
        return postId;
    }

    public String getTags(){
        return tags;
    }

    public void setBlogName(String blogName) {this.blogName = blogName;}

    public void setDate(String date){
        this.date = date;
    }

    public void setPostId(String postId){
        this.postId = postId;
    }

    public void setTags(String tags){
        this.tags = tags;
    }

    public ArrayList getPostContent() {
        return this.postContent;
    }

    public void setPostContent(String postContentToAdd) {
        this.postContent.add(postContentToAdd);
        //System.out.println("postContent.lenght(): " + this.postContent.size());
        //System.out.println("url addeded: " + postContentToAdd);
    }

    @Override
    public String toString() {
        return "blogName: ".concat(blogName).concat(", date: ").concat(date).concat(", postId: ").concat(postId).concat(", tags: ").concat(tags);
    }

    public void showAll(){
        System.out.println(this.toString());

        System.out.println("postContent.size(): " + this.postContent.size());

        if (postContent.size() > 0 && postContent != null) {
            for (String item : this.postContent) {
                System.out.println(item);
                System.out.println(item.substring(item.lastIndexOf("/") + 1, item.lastIndexOf("?")));
            }
        }else{
            System.out.println("postContent empty");
        }
    }

    //TODO не добавил еще поле blogMane для вывода в джосне !!!!!!!!!!!!!!!!!!!!
    public JSONObject ruturnJsonString(){

        JSONObject blogAsJsonObject  = new JSONObject();

        blogAsJsonObject.put("blogName", this.blogName);
        blogAsJsonObject.put("date", this.date);
        blogAsJsonObject.put("postId", this.postId);
        blogAsJsonObject.put("tags", this.tags);
        //    private ArrayList<String> postContent;


        return blogAsJsonObject;
    }

    public boolean iSequals(InstaframPost obj) {
        //instagramPostOne.getPostId().equals(instagramPostTwo.getPostId())
        return this.getPostId().equals(obj.getPostId());
        //return super.equals(obj);
    }

    public void fillEmptinesInside(){
        this.filled = true;
    }

    public String returnFilenamesInOneString(){
        String filenamesInOneString = "";

        if (this.getPostContent().size() > 0 && this.getPostContent() != null) {

            for (String item : this.postContent) {
                filenamesInOneString = filenamesInOneString + item.substring(item.lastIndexOf("/") + 1, item.lastIndexOf("?")) + " ";
            }

        }else{
            System.out.println("postContent empty");
        }

        return filenamesInOneString;
    }



}
