import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.junit.jupiter.api.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TodoMvcTest {
    private static ChromeDriver driver;

    @BeforeAll
    static void launchBrowser() {
        driver = new ChromeDriver();
    }

    @Test
    void testHomepageTitle() throws Exception {
        driver.get("https://todomvc.com/");
        driver.manage().window().maximize();

        String title = driver.getTitle();
        assertEquals("TodoMVC", title);
        takeScreenshot(driver, "todomvc.png");
    }

    @Test
//    Consider the need for this test
    void testClickReact() throws Exception {
        driver.get("https://todomvc.com/");
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));

        WebElement reactLink = driver.findElement(By.cssSelector("a[href*='react']"));

//        Alternative way of locating - not sure which is more stable
//        WebElement reactLink = driver.findElement(By.partialLinkText("React"));

        reactLink.click();
        takeScreenshot(driver, "todomvc_react.png");
    }

    @Test
    void addValidTodo(){
        driver.get("https://todomvc.com/");
        WebElement reactLink = driver.findElement(By.partialLinkText("React"));
        reactLink.click();

        WebElement input = driver.findElement(By.id("todo-input"));
        input.sendKeys("Buy milk", Keys.RETURN);

        WebElement todoLabel = driver.findElement(By.cssSelector("[data-testid='todo-item-label'"));
        String todoText = todoLabel.getText();
        assertEquals("Buy milk", todoText);
    }

    @Test
    void addMultipleValidTodos(){
        driver.get("https://todomvc.com/");
        WebElement reactLink = driver.findElement(By.partialLinkText("React"));
        reactLink.click();

        WebElement input = driver.findElement(By.id("todo-input"));
        input.sendKeys("Buy milk", Keys.RETURN);
        input.sendKeys("Wash up", Keys.RETURN);
        input.sendKeys("Dry clothes", Keys.RETURN);


        List<WebElement> todos = driver.findElements(By.cssSelector("[data-testid='todo-item-label'"));

//      I've considered fragility in relation to order here. Current assuming insertion order is preserved.
//      If this proves fragile - consider alternate assertion method that is order agnostic

        String todo1 = todos.get(0).getText();
        String todo2 = todos.get(1).getText();
        String todo3 = todos.get(2).getText();

        assertEquals("Buy milk", todo1);
        assertEquals("Wash up", todo2);
        assertEquals("Dry clothes", todo3);

//        An inline version
//        assertEquals("Buy milk", todos.get(0).getText());
//        assertEquals("Wash up", todos.get(1).getText());
//        assertEquals("Dry clothes", todos.get(2).getText());

    }

    @AfterAll
    static void closeBrowser() {
        driver.quit();
    }

    // Helper function for taking screenshots using WebDriver
    public static void takeScreenshot(WebDriver webdriver, String desiredPath) throws Exception{
        TakesScreenshot screenshot = ((TakesScreenshot)webdriver);
        File screenshotFile = screenshot.getScreenshotAs(OutputType.FILE);
        File targetFile = new File(desiredPath);
        FileUtils.copyFile(screenshotFile, targetFile);
    }
}
