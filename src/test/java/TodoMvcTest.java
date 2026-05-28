import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.junit.jupiter.api.*;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.interactions.Actions;

import java.io.File;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
//TODO: Refactor into POM
//TODO: Within the POM have a method for selecting the framework
//TODO: Remove all thread sleeps - replace with waits or abstract into POM
//TODO: Add cross platform edit input clearing method to POM

//TODO: Write test for adding empty value
//TODO: Write test for clicking escape during edit - should exit editing
//TODO: Write test for todo being checked/ ticked off by clicking the circle - strikethrough appears
//TODO: Write test for todo being unchecked by clicking the circle - strikethrough disappears
//TODO: Write test for todo item being re ordered - this may be complex - consider putting on backburner
//TODO: Write test for status bar displaying 0 items left with no items
//TODO: Write test for status bar displaying 1 items left with 1 items
//TODO: Write test for status bar displaying 2 items left with 2 items - could this be wrapped up into the add multiple
// todos test or should we keep separate?
//TODO: Write test for status bar being hidden where there are no todos
//TODO: Write test regarding filters - active, completed, show all
//TODO: Write test for 128 char limit
//TODO: Write test for "Clear completed" link appearing in status bar once todo has been added
//TODO: Write test for "Clear completed" link deleting all completed items
//TODO: Write test for clicking down arrow symbol toggling all items completed or not completed



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

//      I've considered fragility in relation to order here. Currently assuming insertion order is preserved.
//      If this proves fragile - consider alternative assertion method that is order agnostic

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

    @Test
    void noCharacters(){
        driver.get("https://todomvc.com/");
        WebElement reactLink = driver.findElement(By.partialLinkText("React"));
        reactLink.click();

        WebElement input = driver.findElement(By.id("todo-input"));
        input.sendKeys("", Keys.RETURN);

        String populatedInputText = input.getAttribute("value");
        assertEquals("", populatedInputText);

        boolean exists = driver.findElements(By.cssSelector("[data-testid='todo-item-label'")).isEmpty();
        assertTrue(exists);

    }

    @Test
    void addSingleCharacter(){
        driver.get("https://todomvc.com/");
        WebElement reactLink = driver.findElement(By.partialLinkText("React"));
        reactLink.click();

        WebElement input = driver.findElement(By.id("todo-input"));
        input.sendKeys("a", Keys.RETURN);

        // When you try to enter a single character to-do, the character remains present in the input box
        // So here we are asserting that the 'a' still exists within the input box
        String populatedInputText = input.getAttribute("value");
        assertEquals("a", populatedInputText);

        // When you add a to-do, 'todo-item-label' appears in the DOM
        // Here we are asserting that it DOES NOT exist - this makes the test slightly more robust
        boolean exists = driver.findElements(By.cssSelector("[data-testid='todo-item-label'")).isEmpty();
        assertTrue(exists);


    }

    // modify a todo item by double clicking
    // div with a class of view within a div with the class of container

    @Test
    void canEditTodo() throws InterruptedException {
        driver.get("https://todomvc.com/");
        WebElement reactLink = driver.findElement(By.partialLinkText("React"));
        reactLink.click();

        WebElement input = driver.findElement(By.id("todo-input"));
        input.sendKeys("Buy milk", Keys.RETURN);

        WebElement todoLabel = driver.findElement(By.cssSelector("[data-testid='todo-item-label'"));
        new Actions(driver)
                .doubleClick(todoLabel)
                .perform();
        WebElement editInput = driver.findElement(By.cssSelector("[data-testid='todo-item'] [data-testid='text-input']"));

        // for some reason .clear() doesn't currently work
        // Keys.CONTROL doesn't work - Keys.COMMAND does (running on macOS), but this will be flaky depending on OS running test
        // Need to make it universal
        // When POM is introduced, consider creating a method that clears the editable text via a loop or !empty
        // Remember to look for the value

        editInput.sendKeys(Keys.COMMAND + "a");
        editInput.sendKeys(Keys.BACK_SPACE);
        Thread.sleep(3000);
        editInput.sendKeys("Buy bread", Keys.RETURN);
        Thread.sleep(3000);

        WebElement editedTodo = driver.findElement(By.cssSelector("[data-testid='todo-item-label'"));

        assertEquals("Buy bread", editedTodo.getText());



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
