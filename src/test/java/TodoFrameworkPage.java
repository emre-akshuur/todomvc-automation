import org.openqa.selenium.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

//COMPLETING
//TODO: clickToggleAll
//TODO: clickCheckbox(index)
//TODO: areAllTodosCompleted
//TODO: isTodoCompleted(index)

//EDITING
//TODO: doubleClickTodo(index)
//TODO: clearEditInput(element) - backspace loop
//TODO: editTodo(index, newText)

//COUNTS
//TODO: getTodoCount
//TODO: isTodoListEmpty

//CLEARING
//TODO: clearCompleted

public class TodoFrameworkPage {
    // I have a feeling that we'll start finding issues with these selectors when we test another framework
    // Shall cross that bridge when we come to it
    private WebDriver driver;
    private By todoInput = By.id("todo-input");
    private By todoItemLabels = By.cssSelector(("[data-testid='todo-item-label']"));
    private By editInputLocator = By.cssSelector(("[data-testid='todo-item'] [data-testid='text-input']"));
    private By toggleAllCheckbox = By.id("toggle-all");
    private By todoItems = By.cssSelector(".todo-list li");

    public TodoFrameworkPage(WebDriver driver) {
        this.driver = driver;
    }

    public void addTodo(String todo) {
        driver.findElement(todoInput).sendKeys(todo, Keys.ENTER);
    }

    public List<String> getTodosText() {
        List<WebElement> todos = driver.findElements(todoItemLabels);
        return todos.stream().map(WebElement::getText).toList();
    }

    public int getTodoCount(){
       return driver.findElements(todoItemLabels).size();
    }

    public void toggleAll(){
        driver.findElement(toggleAllCheckbox).click();
        // could consider creating a toggle all on and toggle all off
        // potentially using conditionals to check toggle state
    }

    public boolean areAllTodosCompleted(){
        // all match will return true if every element satisfies a condition
        // our condition is that every todo's class attribute contains the text completed
        return driver.findElements(todoItems).stream()
                .allMatch(todo -> Objects.requireNonNull(todo.getAttribute("class"))
                        .contains("completed"));
    }
}
