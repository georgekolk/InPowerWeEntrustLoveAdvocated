public class ExtractPostID {

    static String result;

    static public String ExtractPostID(String postId){

        result = postId.substring(postId.indexOf("/p/") + 3, postId.length() - 1 );
        return result;
    }
}
