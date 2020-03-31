package ru.bellintegrator;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class Tests extends WebDriverSettings{
    @Test
    public void testFirst(){
        int resultsNeeded = 10;
        Map<String, String> resultsList = new Hashtable<>();
        driver.navigate().to("https://www.sberbank-ast.ru");
        PageObjectSberAST sberAstPage = new PageObjectSberAST(driver, timeOut);
        sberAstPage.setPrimaryResultsNeeded(120);
        sberAstPage.setMinRubles(4000000);
        sberAstPage.goToSearchPage();
        sberAstPage.find("Страхование");
        do {
            sberAstPage.collectRequiredResults();
            resultsList.putAll(sberAstPage.getCheckedResults());
            sberAstPage.commitCheck();
            sberAstPage.goToNextResultsPage();
        } while (!sberAstPage.isEnoughResults());
        System.out.println(resultsList.size());
        Assert.assertTrue("Количество результатов не удовлетворяет требованиям",
                resultsList.size() >= resultsNeeded);
        resultsList.forEach((num, link) -> System.out.println("Номер закупки :" + num +
                " ссылка на закупку :" + link));
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
