import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InstagramPageLoad {

    private ArrayList<InstaframPost> instagramPostArray = new ArrayList<>();
    private static InstagramPageLoad instance = null;
    private WebDriver driver = null;

    public static InstagramPageLoad getInstance() { // #3
        if (instance == null) {        //если объект еще не создан
            instance = new InstagramPageLoad();    //создать новый объект
        }

        return instance;
    }

    private InstagramPageLoad() {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--blink-settings=imagesEnabled=false");
        /*options.addArguments("--no-sandbox");
        options.addArguments("--disable-gpu");*/
        /*options.addArguments("test-type");
        options.addArguments("start-maximized");*/
        //options.addArguments("--window-size=1920,1080");
        /*options.addArguments("--enable-precise-memory-info");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-default-apps");
        options.addArguments("test-type=browser");*/

        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        this.driver = new ChromeDriver(options);
    }

    public ArrayList<InstaframPost> getInstagramPostArray(String instagramBlogName){
        driver.get("https://www.instagram.com/" + instagramBlogName + "/");

        Actions builder = new Actions(driver);
        builder.sendKeys(Keys.PAGE_DOWN).perform();

        //PseudoDBPush blogPostsCumLoad = new PseudoDBPush(new File ("config.json"));

        //List<WebElement> el = driver.findElements(By.cssSelector("img"));
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        //System.out.println("Constructor 2: " + dateFormat.format( currentDate ) );

        List<WebElement> el = driver.findElements(By.tagName("a"));
        for (WebElement e : el) {
            //System.out.println(e.getAttribute("srcset"));
            if (e.getAttribute("href").contains("/p/")) {
                //System.out.println(e.getAttribute("href"));
                //System.out.println(ExtractPostID.ExtractPostID(e.getAttribute("href")));

                instagramPostArray.add(new InstaframPost(instagramBlogName, dateFormat.format(date), ExtractPostID.ExtractPostID(e.getAttribute("href")), ""));
                //boolean result = blogPostsCumLoad.addInstagramPost(new InstaframPost("", ExtractPostID.ExtractPostID(e.getAttribute("href")), ""));

                //System.out.println(result);
                //TODO: тут нужно пушить в БД
                //TODO: если пушВБД возвращает тру, значит этот элемнт уже там есть, прекоращаим парсить
            }
        }

        return instagramPostArray;
    }

    public void destroyWebDriver(){
        this.driver.close();
        this.driver.quit();
    }

    public ArrayList<InstaframPost> getInstagramAllPostsArray(String instagramBlogName){
        driver.get("https://www.instagram.com/" + instagramBlogName + "/");

        Actions builder = new Actions(driver);
        builder.sendKeys(Keys.PAGE_DOWN).perform();

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");

        String sumPostIds = "";
        String previousSumPostIds = "x";

        do{

            previousSumPostIds = sumPostIds;
            sumPostIds = "";

            List<WebElement> el = driver.findElements(By.tagName("a"));

            for (WebElement e : el) {
                if (e.getAttribute("href").contains("/p/")) {
                    instagramPostArray.add(new InstaframPost(instagramBlogName, dateFormat.format(date), ExtractPostID.ExtractPostID(e.getAttribute("href")), ""));
                    sumPostIds = sumPostIds + ExtractPostID.ExtractPostID(e.getAttribute("href"));
                }
            }

            //System.out.println("sumPostIds: "+sumPostIds);
            //System.out.println("previousSumPostIds: "+previousSumPostIds);

            /*if (sumPostIds.equals(previousSumPostIds)){
                System.out.println("!!!!!!!!! OMG ITS EQUALS !!!!!!!!!" + sumPostIds.hashCode() + " " + previousSumPostIds.hashCode());
            }else {
                System.out.println("sumPostIds.hashCode(): " + sumPostIds.hashCode() + " previousSumPostIds.hashCode(): " + previousSumPostIds.hashCode());
            }*/


            builder.sendKeys(Keys.PAGE_DOWN).perform();
            builder.sendKeys(Keys.PAGE_DOWN).perform();

            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            el.clear();
        }while(!sumPostIds.equals(previousSumPostIds));

        return instagramPostArray;
    }

    public ArrayList<InstaframPost> getInstagram666PostsArray(String instagramBlogName){
        driver.get("https://www.instagram.com/" + instagramBlogName + "/");

        Actions builder = new Actions(driver);
        builder.sendKeys(Keys.PAGE_DOWN).perform();

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");

        int counter = 0;
        do{

            counter++;

            List<WebElement> el = driver.findElements(By.tagName("a"));

            for (WebElement e : el) {
                if (e.getAttribute("href").contains("/p/")) {
                    instagramPostArray.add(new InstaframPost(instagramBlogName, dateFormat.format(date), ExtractPostID.ExtractPostID(e.getAttribute("href")), ""));
                    //sumPostIds = sumPostIds + ExtractPostID.ExtractPostID(e.getAttribute("href"));
                }
            }

            builder.sendKeys(Keys.PAGE_DOWN).perform();
            builder.sendKeys(Keys.PAGE_DOWN).perform();

            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            el.clear();
        }while(counter < 666);

        return instagramPostArray;
    }
}
