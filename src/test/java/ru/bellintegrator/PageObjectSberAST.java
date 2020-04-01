package ru.bellintegrator;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PageObjectSberAST {
    private WebDriver driver;
    private WebDriverWait wait;
    private List<WebElement> requiredResults;
    private final int timeOut;
    private int primaryResultsNeeded;
    private int minRubles;
    private int resultsChecked = 0;
    private final String contractType = "Госзакупки по 44-ФЗ";
    private final String resultsFullLocator = "//div[@content = 'node:hits']";
    private final String resultsMoneyLocator = "descendant::span[@format = 'money']";
    private final String resultsNumberLocator = "descendant::span[@content = 'leaf:purchCodeTerm']";
    private final String resultsLinkLocator = "descendant::input[@content = 'leaf:objectHrefTerm']";
    private List<String> resultsNumbersStr = new ArrayList<>();
    private List<WebElement> resultsTest;

    @FindBy(how = How.CSS, using = ".SearchIco")
    private WebElement searchPageButton;

    @FindBy(how = How.ID, using = "searchInput")
    private WebElement searchField;

    @FindBy(how = How.XPATH, using = "//*[@onchange = 'changePageSize(this);']")
    private WebElement pageSize;

    @FindBy(how = How.XPATH, using = "//option[text()='100']")
    private WebElement pageSizeOption100;

    @FindAll(@FindBy(how = How.XPATH, using = resultsFullLocator))
    private List<WebElement> results;

    @FindAll(@FindBy(how = How.XPATH, using = resultsNumberLocator))
    private List<WebElement> resultsNumber;

    @FindBy(how = How.XPATH, using = "//span[text() = '>']")
    private WebElement nextPageButton;

    PageObjectSberAST(WebDriver driver, int timeOut) {
        this.timeOut = timeOut;
        wait = new WebDriverWait(driver, timeOut);
        PageFactory.initElements(driver, this);
    }

    public void setMinRubles(int minRubles) {
        this.minRubles = minRubles;
    }

    public void setPrimaryResultsNeeded(int primaryResultsNeeded) {
        this.primaryResultsNeeded = primaryResultsNeeded;
    }

    //переход из корневой страницы на страницу поиска
    public void goToSearchPage() {
        wait.until(ExpectedConditions.elementToBeClickable(searchPageButton));
        searchPageButton.click();
    }

    //поиск запроса с отображением по 100 результатов на страницу
        public void find(String findRequest){
            wait.until(ExpectedConditions.elementToBeClickable(pageSize));
            pageSize.click();
            pageSizeOption100.click();
            searchField.sendKeys(findRequest + Keys.ENTER);
        }

    //метод, используемый для дебага! Не использовать в конечном тесте
    public void testMethod() {
        System.out.println(requiredResults.get(0).findElement(By.xpath(resultsMoneyLocator)).getText());
        System.out.println(requiredResults.get(1).findElement(By.xpath(resultsMoneyLocator)).getText());
        System.out.println(requiredResults.get(2).findElement(By.xpath(resultsMoneyLocator)).getText());
    }

    //сбор полученных результатов в List, с учетом фильтра по типу контракта
    public void collectRequiredResults() {
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(resultsFullLocator),90));
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(resultsFullLocator)));
        requiredResults = results.stream()
                .filter(result -> result.getText().contains(contractType))
                .collect(Collectors.toList());
    }

    //перевод полученной строки цены в int значение
    private Integer rublesToInt(String str) {
        str = str.replaceAll(" ", "");
        str = str.substring(0, str.indexOf("."));
        return Integer.valueOf(str);
    }

    //получение Map с номером закупки и ссылкой на неё
    public Map<String,String> getCheckedResults() {
        return requiredResults.stream()
                //ограничение на максимальное количество проверяемых контрактов
                .limit(primaryResultsNeeded - resultsChecked)
                //проверка на соответствие минимальной цене контракта в рублях
                .filter(result -> rublesToInt(result.findElement(By.xpath(resultsMoneyLocator))
                        .getText()) > minRubles)
                .collect(Collectors.toMap(element -> element.findElement(By.xpath(resultsNumberLocator)).getText(),
                        result -> result.findElement(By.xpath(resultsLinkLocator)).getAttribute("value")));
    }

    //увеличение каунтера проверенных результатов после завершения действий с ними
    public void commitCheck() {
        resultsChecked += requiredResults.size();
    }

    //переход на следующую страницу с результатами поиска
    public void goToNextResultsPage(){
        nextPageButton.click();
        //асинхронные вызовы, проще сделать так
        sleep(timeOut);
    }

    //проверка на необходимость дальнейшего поиска результатов
    public boolean isEnoughResults() {
        return resultsChecked >= primaryResultsNeeded;
    }

    private void sleep(int seconds){
        try {
            Thread.sleep(seconds*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
