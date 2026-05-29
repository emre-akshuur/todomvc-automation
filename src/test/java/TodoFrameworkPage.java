import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

//COMPLETING

//EDITING
//TODO: clearEditInput(element) - backspace loop
//TODO: editTodo(index, newText)

//COUNTS
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
    private By checkboxes = By.cssSelector("input[type='checkbox'");

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
        // allMatch will return true if every element satisfies a condition
        // our condition is that every todo's class attribute contains the text completed
        return driver.findElements(todoItems).stream()
                .allMatch(todo -> Objects.requireNonNull(todo.getAttribute("class"))
                        .contains("completed"));
    }

    public boolean isTodoComplete(int index){
        WebElement todo = driver.findElements(todoItems).get(index);
        return todo.getAttribute("class").contains("completed");
    }

    public boolean isTodoNotComplete(int index){
        WebElement todo = driver.findElements(todoItems).get(index);
        return !todo.getAttribute("class").contains("completed");
    }

    public void clickCheckbox(int index){
       driver.findElements(checkboxes).get(index).click();
    }

    public void editTodo(int index, String newTodo) throws InterruptedException {
        WebElement todo = driver.findElements(todoItems).get(index);
        doubleClick(todo);
        WebElement editBox = driver.findElement(editInputLocator);
        clearEditInput(editBox);
        editBox.sendKeys(newTodo, Keys.ENTER);
    }

    public void doubleClick(WebElement todo){
        new Actions(driver)
                .doubleClick(todo)
                .perform();
    }
    public void clearEditInput(WebElement editBox) throws InterruptedException {
        String text = editBox.getAttribute("value");
        for (int i = 0; i < Objects.requireNonNull(text).length(); i++){
           editBox.sendKeys(Keys.BACK_SPACE);
        }
    }
}
