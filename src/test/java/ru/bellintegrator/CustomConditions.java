package ru.bellintegrator;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.ExpectedCondition;


public class CustomConditions {
    public static ExpectedCondition<Boolean> jsReturnsThisValue(final String javaScript, String thisValue) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                try {
                    Object value = ((JavascriptExecutor)driver).executeScript(javaScript, new Object[0]);
                    if (value instanceof String) {
                        System.out.println(value);
                        return String.valueOf(value).equalsIgnoreCase(thisValue) ? true : null;
                    } else {
                        return null;
                    }
                } catch (WebDriverException var3) {
                    return null;
                }
            }

            public String toString() {
                return String.format("js %s to be executable", javaScript);
            }
        };
    }
}
