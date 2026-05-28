import org.openqa.selenium.*;

public class TodoMVCHomePage {
    private WebDriver driver;

    public TodoMVCHomePage(WebDriver driver) {
        this.driver = driver;
    }

    public void gotoHomePage() {
        driver.get("https://todomvc.com/");
    }

    public void selectFramework(String framework) {
        WebElement frameworkLink = driver.findElement(By.partialLinkText(framework));
        frameworkLink.click();

    }
}
