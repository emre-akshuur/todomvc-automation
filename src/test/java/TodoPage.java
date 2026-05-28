import org.openqa.selenium.*;

public class TodoPage {
    private WebDriver driver;
    private By todoInput = By.id("todo-input");
    private By todoItemLabel = By.cssSelector(("[data-testid='todo-item-label']"));
    private By editInputLocator = By.cssSelector(("[data-testid='todo-item'] [data-testid='text-input']"));

}
