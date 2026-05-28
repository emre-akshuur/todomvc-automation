import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.junit.jupiter.api.*;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.interactions.Actions;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
//TODO: Refactor into POM
//TODO: Within the POM have a method for selecting the framework
//TODO: Remove all thread sleeps - replace with waits or abstract into POM
//TODO: Add cross platform edit input clearing method to POM

//TODO: Write test for todo item being re ordered - this may be complex - consider putting on backburner



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
        boolean doesNotExist = driver.findElements(By.cssSelector("[data-testid='todo-item-label'")).isEmpty();
        assertTrue(doesNotExist);


    }

    @Test
    void checkboxTick() throws InterruptedException {
        driver.get("https://todomvc.com/");
        WebElement reactLink = driver.findElement(By.partialLinkText("React"));
        reactLink.click();

        WebElement input = driver.findElement(By.id("todo-input"));
        input.sendKeys("Buy Milk", Keys.RETURN);

        // When checkbox is clicked, the class becomes 'completed'

        WebElement checkbox = driver.findElement(By.cssSelector("input[type='checkbox']"));

        checkbox.click();
        assertTrue(checkbox.isSelected());
        boolean strikeThrough = !driver.findElements(By.className("completed")).isEmpty();
        assertTrue(strikeThrough);

        checkbox.click();
        assertTrue(!checkbox.isSelected());
        boolean strikeThroughCleared = driver.findElements(By.className("completed")).isEmpty();
        assertTrue(strikeThroughCleared);

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

    @Test
    void canEscapeEdit() throws InterruptedException {
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

        editInput.sendKeys(Keys.ESCAPE);
        Thread.sleep(3000);
        // want to assert that this is still in an editable state as we know this test fails
        List<WebElement> editInputPostEscape = driver.findElements(By.cssSelector("[data-testid='todo-item'] [data-testid='text-input']"));
        boolean exists = !editInputPostEscape.isEmpty();
        assertTrue(exists);

    }

    @Test
        void NoStatusBar() {
            driver.get("https://todomvc.com/");
            WebElement reactLink = driver.findElement(By.partialLinkText("React"));
            reactLink.click();

            List<WebElement> footer = driver.findElements(By.cssSelector("[data-testid='footer']"));
            assertTrue(footer.isEmpty());
    }

    @Test
        void statusBarShowingZeroItems(){
            driver.get("https://todomvc.com/");
            WebElement reactLink = driver.findElement(By.partialLinkText("React"));
            reactLink.click();

            WebElement input = driver.findElement(By.id("todo-input"));
            input.sendKeys("Buy milk", Keys.RETURN);

            WebElement checkbox = driver.findElement(By.cssSelector("input[type='checkbox']"));
            checkbox.click();

            String todoCount = driver.findElement(By.className("todo-count")).getText();
            assertEquals("0 items left!", todoCount);
    }

    @Test
        void statusBarShowingOneItem(){
            driver.get("https://todomvc.com/");
            WebElement reactLink = driver.findElement(By.partialLinkText("React"));
            reactLink.click();

            WebElement input = driver.findElement(By.id("todo-input"));
            input.sendKeys("Buy milk", Keys.RETURN);


            String todoCount = driver.findElement(By.className("todo-count")).getText();
            assertEquals("1 item left!", todoCount);
    }

    @Test
        void statusBarShowingMultipleItems(){
            driver.get("https://todomvc.com/");
            WebElement reactLink = driver.findElement(By.partialLinkText("React"));
            reactLink.click();

            WebElement input = driver.findElement(By.id("todo-input"));
            input.sendKeys("Buy milk", Keys.RETURN);
            input.sendKeys("Buy bread", Keys.RETURN);
            input.sendKeys("Go for a walk", Keys.RETURN);

            String todoCount = driver.findElement(By.className("todo-count")).getText();
            assertEquals("3 items left!", todoCount);
    }

    @Test
        void statusBarShowActive() throws InterruptedException {
            driver.get("https://todomvc.com/");
            WebElement reactLink = driver.findElement(By.partialLinkText("React"));
            reactLink.click();

            WebElement input = driver.findElement(By.id("todo-input"));
            input.sendKeys("Buy milk", Keys.RETURN);
            input.sendKeys("Buy bread", Keys.RETURN);

            List<WebElement> checkbox = driver.findElements(By.cssSelector("input[type='checkbox']"));
            System.out.println(checkbox);
            checkbox.get(1).click();

            // now we need to grab the 'active' button and click
            WebElement activeLink = driver.findElement(By.cssSelector("a[href*='active']"));
            activeLink.click();
            Thread.sleep(3000);
            // can assert active tab is active - could be complex - come back to this

            List<WebElement> labels = driver.findElements(By.cssSelector("[data-testid='todo-item-label']"));
            assertEquals(1, labels.size());
            String todoText = labels.getFirst().getText();
            assertEquals("Buy bread", todoText);
        }

    @Test
        void statusBarShowCompleted(){
        driver.get("https://todomvc.com/");
        WebElement reactLink = driver.findElement(By.partialLinkText("React"));
        reactLink.click();

        WebElement input = driver.findElement(By.id("todo-input"));
        input.sendKeys("Buy milk", Keys.RETURN);
        input.sendKeys("Buy bread", Keys.RETURN);

        List<WebElement> checkbox = driver.findElements(By.cssSelector("input[type='checkbox']"));
        System.out.println(checkbox);
        checkbox.get(1).click();

        // now we need to grab the 'active' button and click
        WebElement activeLink = driver.findElement(By.cssSelector("a[href*='completed']"));
        activeLink.click();

        List<WebElement> labels = driver.findElements(By.cssSelector("[data-testid='todo-item-label']"));
        assertEquals(1, labels.size());
        String todoText = labels.getFirst().getText();
        assertEquals("Buy milk", todoText);
    }

    @Test
        void statusBarShowAll(){
        // potentially covered in the add multiple todos test
    }

    @Test
        void charLimit128(){
            // this test passing means there isn't a 128 char limit
            // the lorem string is 129 characters long
            String lorem = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque pena";

            driver.get("https://todomvc.com/");
            WebElement reactLink = driver.findElement(By.partialLinkText("React"));
            reactLink.click();

            WebElement input = driver.findElement(By.id("todo-input"));
            input.sendKeys(lorem, Keys.RETURN);

            List<WebElement> labels = driver.findElements(By.cssSelector("[data-testid='todo-item-label']"));
            String todoText = labels.getFirst().getText();

            assertEquals(lorem, todoText);

    }

    @Test
        void clearCompletedLinkVisible(){
            driver.get("https://todomvc.com/");
            WebElement reactLink = driver.findElement(By.partialLinkText("React"));
            reactLink.click();

            WebElement input = driver.findElement(By.id("todo-input"));
            input.sendKeys("Buy bread", Keys.RETURN);

            String clearCompletedText = driver.findElement(By.className("clear-completed")).getText();
            assertEquals("Clear completed", clearCompletedText);

    }

    @Test
        void clearCompletedClears(){
            driver.get("https://todomvc.com/");
            WebElement reactLink = driver.findElement(By.partialLinkText("React"));
            reactLink.click();

            WebElement input = driver.findElement(By.id("todo-input"));
            input.sendKeys("Buy bread", Keys.RETURN);
            input.sendKeys("Buy milk", Keys.RETURN);

            List<WebElement> checkbox = driver.findElements(By.cssSelector("input[type='checkbox']"));
            checkbox.get(1).click();

            WebElement clearCompletedButton = driver.findElement(By.className("clear-completed"));
            clearCompletedButton.click();

            List<WebElement> labels = driver.findElements(By.cssSelector("[data-testid='todo-item-label']"));

            String todoText = labels.getFirst().getText();
            assertEquals(1, labels.size());
            assertEquals("Buy milk", todoText);

    }

    @Test
    // add multiple todos, click the first checkbox, check all todos are "completed"
    void checkAll() throws InterruptedException {
        driver.get("https://todomvc.com/");
        WebElement reactLink = driver.findElement(By.partialLinkText("React"));
        reactLink.click();

        WebElement input = driver.findElement(By.id("todo-input"));
        input.sendKeys("Buy bread", Keys.RETURN);
        input.sendKeys("Buy milk", Keys.RETURN);
        input.sendKeys("Go for a walk", Keys.RETURN);
        input.sendKeys("Hydrate", Keys.RETURN);

        List<WebElement> toggleAllButton = driver.findElements(By.id("toggle-all"));
        toggleAllButton.getFirst().click();

        List<WebElement> todoItems = driver.findElements(By.cssSelector(".todo-list li"));

        // need to learn about streams for situations like this
        for(WebElement item : todoItems){
            assertTrue(Objects.requireNonNull(item.getAttribute("class")).contains("completed"));
        }
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
