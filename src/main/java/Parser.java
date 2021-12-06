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
import java.util.*;

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
            //todo получать информацию из бд по последней картинке

            driver.get("http://www.reddit.com/r/memes/new/");

            List<MemeModel> images = new ArrayList<>();
            var element =  Optional.of(driver.findElement(By.cssSelector("img[alt = 'Post image']")));
            Optional<WebElement> nextElem;
            int i = 0;
            Boolean alreadyFoundedImage = false;
            while (i != 40){

                nextElem = Parser.getElement(driver, element.get());
                if(nextElem.isEmpty()){
                    while(true){
                        jse.executeScript("arguments[0].scrollIntoView();", element.get());
                        jse.executeScript("window.scrollBy(0, 1200)");
                        Thread.sleep(100);
                        nextElem = Parser.getElement(driver, element.get());
                        if(nextElem.isPresent()){break;}
                    }
                }
                if(!nextElem.get().getAttribute("src").startsWith("https://ex")){
                    images.add(MemeModel.setModel(nextElem.get().getAttribute("src"), 0, 0));
                }
                element = nextElem;
                i++;
            }

            for (MemeModel m: images) {
                driver.get(m.getReference());
                var img = driver.findElement(By.cssSelector("img"));
                System.out.println(img.getAttribute("src") + " --- " + img.getSize().height + " : " + img.getSize().width);
                m.setLength(img.getSize().height);
                m.setWidth(img.getSize().width);
            }
            System.out.println(images.size());
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
    public static Optional<WebElement> getElement(WebDriver driver, WebElement previousElem){
        try{
            return Optional.of(driver.findElement(with(By.cssSelector("img[alt = 'Post image']")).below(previousElem)));
        }catch (Exception e) {
            return Optional.empty();
        }
    }
}
