package AppiumUITheAppTests;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Interaction;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.List;

public class AppiumTheAppTests {
    private static final String APP = "https://github.com/appium-pro/TheApp/releases/download/v1.12.0/TheApp.apk";
    private static final String SERVER = "http://127.0.0.1:4723/";
    private AndroidDriver driver;

    @BeforeEach
    void setup() throws MalformedURLException {
        DesiredCapabilities ds = new DesiredCapabilities();
        ds.setCapability("platformName", "Android");
        ds.setCapability("platformVersion", "15");
        ds.setCapability("deviceName", "emulator-5554");
        ds.setCapability("app", APP);
        ds.setCapability("automationName", "UiAutomator2");

        driver = new AndroidDriver(new URL(SERVER), ds);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @DisplayName("multiple check with wait test")
    @Tags({@Tag("UI"), @Tag("appium"), @Tag("smoke")})
    @Test
    void checkFieldTest(){
        String expectedName = "Geolocation Demo";

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement GeolocationDemo = wait.until(ExpectedConditions.visibilityOfElementLocated
                (AppiumBy.androidUIAutomator("new UiSelector().text(\"Geolocation Demo\")")));

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(GeolocationDemo.isDisplayed()).isTrue();
        softly.assertThat(GeolocationDemo.isSelected()).isFalse();
        softly.assertThat(GeolocationDemo.isEnabled()).isTrue();
        softly.assertThat(GeolocationDemo.getText()).isEqualTo(expectedName);
        softly.assertAll();
    }

    @DisplayName("successful login test and logout")
    @Tags({@Tag("UI"), @Tag("appium"), @Tag("extended")})
    @Test
    void loginScreenTest(){
        String login = "alice";

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement LoginScreen = wait.until(ExpectedConditions.visibilityOfElementLocated
                (AppiumBy.accessibilityId("Login Screen")));
        LoginScreen.click();

        WebElement userNameField = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.accessibilityId("username")));
        userNameField.sendKeys(login);
        driver.findElement(AppiumBy.accessibilityId("password")).sendKeys("mypassword");
        driver.findElement(AppiumBy.accessibilityId("loginBtn")).click();
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement loginMessage = wait.until(ExpectedConditions.visibilityOfElementLocated
                (AppiumBy.xpath("//android.widget.TextView[contains(@text, 'You are logged in')]")));

        Assertions.assertTrue(loginMessage.getText().contains(login));

        driver.findElement(AppiumBy.accessibilityId("Logout")).click();
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement LoginTitleText = wait.until(ExpectedConditions.visibilityOfElementLocated
                (AppiumBy.androidUIAutomator("new UiSelector().text(\"Login\").instance(0)")));

        Assertions.assertTrue(LoginTitleText.isDisplayed());
    }

    @DisplayName("login with invalid password")
    @Tags({@Tag("UI"), @Tag("appium"), @Tag("extended"), @Tag("negative")})
    @Test
    void loginScreenNegativeTest(){
        String login = "alice";
        String expectedErrorMessage = "Invalid login credentials, please try again";

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement LoginScreen = wait.until(ExpectedConditions.visibilityOfElementLocated
                (AppiumBy.accessibilityId("Login Screen")));
        LoginScreen.click();

        WebElement userNameField = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.accessibilityId("username")));
        userNameField.sendKeys(login);
        driver.findElement(AppiumBy.accessibilityId("password")).sendKeys("password");
        driver.findElement(AppiumBy.accessibilityId("loginBtn")).click();
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated
                (AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"android:id/message\")")));

        Assertions.assertTrue(errorMessage.getText().contains(expectedErrorMessage));

        driver.findElement(AppiumBy.androidUIAutomator
                ("new UiSelector().resourceId(\"android:id/button1\")")).click();
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement LoginTitleText = wait.until(ExpectedConditions.visibilityOfElementLocated
                (AppiumBy.androidUIAutomator("new UiSelector().text(\"Login\").instance(0)")));

        Assertions.assertTrue(LoginTitleText.isDisplayed());
    }

    @DisplayName("swipe and find element")
    @Tags({@Tag("UI"), @Tag("appium"), @Tag("smoke")})
    @Test
    void swipeTest() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement listDemo = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.accessibilityId("List Demo")));
        listDemo.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.accessibilityId("AWS")));

        Dimension size = driver.manage().window().getSize();
        int screenWidth = size.getWidth();
        int screenHeight = size.getHeight();
        System.out.printf("Width: %s, Height: %s", screenWidth, screenHeight);

        int startX = screenWidth / 2;
        int startY = (int) (screenHeight * 0.8);
        int endY = (int) (screenHeight * 0.2);

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Interaction moveToStart = finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY);
        Interaction pressDown = finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg());
        Interaction moveToEnd = finger.createPointerMove(Duration.ofMillis(1000), PointerInput.Origin.viewport(), startX, endY);
        Interaction pressUp = finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg());

        Sequence swipe = new Sequence(finger, 0);
        swipe.addAction(moveToStart);
        swipe.addAction(pressDown);
        swipe.addAction(moveToEnd);
        swipe.addAction(pressUp);

        driver.perform(List.of(swipe));
        driver.perform(List.of(swipe));

        WebElement Stratus = driver.findElement(AppiumBy.accessibilityId("Stratus"));
        Assertions.assertTrue(Stratus.isDisplayed());
    }

    @DisplayName("echo screen simple test")
    @Tags({@Tag("UI"), @Tag("appium"), @Tag("smoke")})
    @Test
    void echoScreenTest() {
        String textForTest = "New text";

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement echoScreen = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.accessibilityId("Echo Box")));
        echoScreen.click();
        WebElement messageInput = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.accessibilityId("messageInput")));
        messageInput.sendKeys(textForTest);
        driver.findElement(AppiumBy.accessibilityId("messageSaveBtn")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.androidUIAutomator("new UiSelector().text(\"Here's what you said before:\")")));
        WebElement savedMessage = driver.findElement(AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"savedMessage\")"));

        Assertions.assertEquals(textForTest, savedMessage.getText(), "Values must be equal");
    }

    @DisplayName("photo demo test")
    @Tags({@Tag("UI"), @Tag("appium"), @Tag("smoke")})
    @Disabled //picture's locator is connected to its order while the set of pictures is random, so you can't find connection between picture founded by locator and text
    @Test
    void photoDemoTest(){
        String expectedMessage = "This is a picture of: A long thin cloud below mountain level";

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement PhotoDemo = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.accessibilityId("Photo Demo")));
        PhotoDemo.click();
        WebElement firstPicture = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.androidUIAutomator
                ("new UiSelector().className(\"android.widget.ImageView\").instance(0)")));
        firstPicture.click();
        WebElement SelectedPhotoPopUp = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.androidUIAutomator
                ("new UiSelector().className(\"android.widget.FrameLayout\").instance(1)")));
        WebElement textInPopUp = driver.findElement(AppiumBy.id("android:id/message"));

        Assertions.assertEquals(expectedMessage, textInPopUp.getText());

        driver.findElement(AppiumBy.id("android:id/button1")).click();
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        Assertions.assertFalse(SelectedPhotoPopUp.isDisplayed());
    }

    @DisplayName("picker test")
    @Tags({@Tag("UI"), @Tag("appium"), @Tag("smoke")})
    @Disabled // you can't check text because year generates randomly and messages are different
    @Test
    void pickerDemoTest(){
        String expectedSubtitleText = "On this day (6/9) in 1815...";
        String expectedMessageText = "End of the Congress of Vienna: The new European political situation is set";

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement pickerDemo = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.accessibilityId("Picker Demo")));
        pickerDemo.click();
        WebElement monthPicker = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.accessibilityId("monthPicker")));
        monthPicker.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.id("com.appiumpro.the_app:id/select_dialog_listview")));
        driver.findElement(AppiumBy.androidUIAutomator("new UiSelector().text(\"June\")")).click();
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement dayPicker = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.accessibilityId("dayPicker")));
        dayPicker.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.className("android.widget.LinearLayout")));
        driver.findElement(AppiumBy.androidUIAutomator("new UiSelector().text(\"9\")")).click();
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        driver.findElement(AppiumBy.accessibilityId("learnMore")).click();
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.accessibilityId
                ("new UiSelector().className(\"android.widget.FrameLayout\").instance(1)")));
        WebElement subtitleText = driver.findElement(AppiumBy.id("android:id/alertTitle"));
        WebElement messageText = driver.findElement(AppiumBy.id("android:id/message"));

        Assertions.assertEquals(expectedSubtitleText, subtitleText.getText());
        Assertions.assertEquals(expectedMessageText, messageText.getText());
    }
}
