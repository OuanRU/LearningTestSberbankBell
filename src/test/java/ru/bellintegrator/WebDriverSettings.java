package ru.bellintegrator;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.concurrent.TimeUnit;

public class WebDriverSettings {
    WebDriver driver;
    int timeOut = 10;

    @Before
    public void setupBellTest(){
        System.out.println("Before");
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Ouan\\ChromeDriver\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize(); // окно на весь экран драйвером
        driver.manage().timeouts().pageLoadTimeout(timeOut, TimeUnit.SECONDS); // ждет загрузки страницы
//        chromeDriver.manage().timeouts().setScriptTimeout(30, TimeUnit.SECONDS); // ждет загрузки скрипта
//        chromeDriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);  // ждет конкретных элементов
    }

    @After
    public void closeBellTest() {
        System.out.println("After");
        driver.quit();
    }
}
