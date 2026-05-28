import org.openqa.selenium.*;

import java.util.List;
import java.util.stream.Collectors;

public class TodoFrameworkPage {
    private WebDriver driver;
    private By todoInput = By.id("todo-input");
    private By todoItemLabel = By.cssSelector(("[data-testid='todo-item-label']"));
    private By editInputLocator = By.cssSelector(("[data-testid='todo-item'] [data-testid='text-input']"));

    public TodoFrameworkPage(WebDriver driver) {
        this.driver = driver;
    }

    public void addTodo(String todo) {
        driver.findElement(todoInput).sendKeys(todo, Keys.ENTER);
    }

    public List<String> getTodoText() {
        List<WebElement> todos = driver.findElements(todoItemLabel);
        return todos.stream().map(WebElement::getText).toList();
    }
}
