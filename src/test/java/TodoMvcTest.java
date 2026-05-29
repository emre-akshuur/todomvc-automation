import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.junit.jupiter.api.*;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.safari.SafariDriver;

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
    private static String FRAMEWORK = "React";
    private TodoMVCHomePage homePage;
    private TodoFrameworkPage frameworkPage;

    @BeforeAll
    static void launchBrowser() {
        driver = new ChromeDriver();
    }

    @BeforeEach
    void setup(){
         homePage = new TodoMVCHomePage(driver);
         frameworkPage = new TodoFrameworkPage(driver);
         homePage.gotoHomePage();
         homePage.selectFramework(FRAMEWORK);
    }

    @Test
    void testHomepageTitle() throws Exception {
        TodoMVCHomePage homePage = new TodoMVCHomePage(driver);
        homePage.gotoHomePage();
        homePage.selectFramework("React");
        driver.manage().window().maximize();
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
        frameworkPage.addTodo("Buy milk");
        List<String> todoText = frameworkPage.getTodosText();
        assertEquals("Buy milk", todoText.getFirst());
    }

    @Test
    void addMultipleValidTodos(){
        frameworkPage.addTodo("Buy milk");
        frameworkPage.addTodo("Wash up");
        frameworkPage.addTodo("Dry clothes");

        List<String> todosText = frameworkPage.getTodosText();
        assertEquals(List.of("Buy milk", "Wash up", "Dry clothes"), todosText);
    }

    @Test
    void noCharacters(){
        frameworkPage.addTodo("");
        int todoCount = frameworkPage.getTodoCount();
        assertEquals(0, todoCount );
    }

    @Disabled("BUG: Single character todo should be added but it is not - nothing gets added")
    @Test
    void addSingleCharacter(){
        frameworkPage.addTodo("a");
        int todoCount = frameworkPage.getTodoCount();
        assertEquals(1, todoCount);
    }

    @Test
    void checkboxTick() throws InterruptedException {

        frameworkPage.addTodo("Buy milk");
        frameworkPage.clickCheckbox(1);

        assertTrue(frameworkPage.isTodoComplete(0));

        frameworkPage.clickCheckbox(1);
        assertTrue(frameworkPage.isTodoNotComplete(0));

    }

    @Test
    void canEditTodo() throws InterruptedException {
        frameworkPage.addTodo("Buy milk");
        frameworkPage.editTodo(0, "Buy bread");
        List<String> todoText = frameworkPage.getTodosText();
        assertEquals("Buy bread", todoText.getFirst());
    }

    @Disabled("BUG: ESC key should exit edit mode but does not - todo item remains in editable state")
    @Test
    void canEscapeEdit() {
        frameworkPage.addTodo("Buy Milk");
        List<WebElement> todoList = frameworkPage.getTodos();
        frameworkPage.doubleClick(todoList.getFirst());
        frameworkPage.pressEscape();

        List<WebElement> editInputPostEscape = driver.findElements(By.cssSelector("[data-testid='todo-item'] [data-testid='text-input']"));
        boolean exitedEditMode = editInputPostEscape.isEmpty();
        assertTrue(exitedEditMode);

    }

    @Test
        void NoStatusBar() {
            List<WebElement> footer = driver.findElements(By.cssSelector("[data-testid='footer']"));
            assertTrue(footer.isEmpty());
    }

    @Test
        void statusBarShowingZeroItems(){
        frameworkPage.addTodo("Buy milk");
        frameworkPage.clickCheckbox(1);
        assertEquals("0 items left!", frameworkPage.itemsLeftText());
    }

    @Test
        void statusBarShowingOneItem(){
        frameworkPage.addTodo("Buy milk");
        frameworkPage.addTodo("Get Liam a G7 job");
        frameworkPage.clickCheckbox(1);
        assertEquals("1 item left!", frameworkPage.itemsLeftText());
    }

    @Test
        void statusBarShowingMultipleItems(){
        frameworkPage.addTodo("Buy milk");
        frameworkPage.addTodo("Buy bread");
        frameworkPage.addTodo("Go for a walk");
        assertEquals("3 items left!", frameworkPage.itemsLeftText());
    }

    @Test
        void statusBarShowActive() throws InterruptedException {
//            driver.get("https://todomvc.com/");
//            WebElement reactLink = driver.findElement(By.partialLinkText("React"));
//            reactLink.click();
//
//            WebElement input = driver.findElement(By.id("todo-input"));
//            input.sendKeys("Buy milk", Keys.RETURN);
//            input.sendKeys("Buy bread", Keys.RETURN);
//
//            List<WebElement> checkbox = driver.findElements(By.cssSelector("input[type='checkbox']"));
//            System.out.println(checkbox);
//            checkbox.get(1).click();
//
//            // now we need to grab the 'active' button and click
//            WebElement activeLink = driver.findElement(By.cssSelector("a[href*='active']"));
//            activeLink.click();
//            Thread.sleep(3000);
//            // can assert active tab is active - could be complex - come back to this
//
//            List<WebElement> labels = driver.findElements(By.cssSelector("[data-testid='todo-item-label']"));
//            assertEquals(1, labels.size());
//            String todoText = labels.getFirst().getText();
//            assertEquals("Buy bread", todoText);
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
    void checkAll() throws InterruptedException {


        frameworkPage.addTodo("Buy bread");
        frameworkPage.addTodo("Buy milk");
        frameworkPage.addTodo("Go for a walk");
        frameworkPage.addTodo("Hydrate");

        frameworkPage.toggleAll();

        assertTrue(frameworkPage.areAllTodosCompleted());
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
