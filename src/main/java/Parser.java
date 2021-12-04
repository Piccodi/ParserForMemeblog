import com.google.gson.Gson;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import static org.openqa.selenium.support.locators.RelativeLocator.with;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;

public class Parser extends TimerTask {
    @Override
    public void run() {
        try{
        System.setProperty("webdriver.gecko.driver", "/home/piccodi/Drivers/geckodriver");
        WebDriver driver = new FirefoxDriver();
        driver.manage().window().maximize();
        JavascriptExecutor jse = (JavascriptExecutor)driver;
        parse(driver, jse);
        }
        catch (Exception e){ e.printStackTrace();}
    }

    void parse(WebDriver driver, JavascriptExecutor jse){
        try{
            //todo создать json обьект со списком картинок и отслыать пост запрос с ним;
            driver.get("http://www.reddit.com/r/memes/new/");

            List<MemeModel> images = new ArrayList<>();
            var element =  driver.findElement(By.cssSelector("img[alt = 'Post image']"));
            WebElement nextElem;
            int i = 0;
            while (i != 30){
                nextElem = driver.findElement(with(By.cssSelector("img[alt = 'Post image']")).below(element));
                Thread.sleep(500);
                jse.executeScript("window.scrollBy(0, 1200)");
                Thread.sleep(500);
                jse.executeScript("arguments[0].scrollIntoView();", nextElem);
                if(!nextElem.getAttribute("src").startsWith("https://ex")){
                    images.add(MemeModel.setModel(nextElem.getAttribute("src"), 0, 0));
                    System.out.println(images.get(i));
                    i++;
                }
                element = nextElem;

            }


            for (MemeModel m: images) {
                driver.get(m.getReference());
                var img = driver.findElement(By.cssSelector("img"));
                System.out.println(img.getSize().height + " : " + img.getSize().width);
                m.setLength(img.getSize().height);
                m.setWidth(img.getSize().width);
            }
            driver.close();

            String url_query = "http://localhost:8080/memes";
            URL url = new URL(url_query);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setDoInput(true);
            http.addRequestProperty("Content-Type", "application/json; charset=UTF-8");

            String json = new Gson().toJson(images);
            System.out.println(json);

            OutputStream outputStream = http.getOutputStream();
            outputStream.write(json.getBytes(StandardCharsets.UTF_8));
            outputStream.close();

            InputStream in = new BufferedInputStream(http.getInputStream());
            String response = Arrays.toString(in.readAllBytes());
            System.out.println(response);


        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
