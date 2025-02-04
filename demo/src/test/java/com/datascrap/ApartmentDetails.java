package com.datascrap;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class ApartmentDetails {

@Test
 public static void main(String[] args) throws InterruptedException {

    try{
      
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");  
        WebDriver driver = new ChromeDriver(options);

       
        driver.get("https://www.google.com/maps");
        Thread.sleep(3000);

        
        WebElement searchBox = driver.findElement(By.name("q"));
        searchBox.sendKeys("Apartments near me");
        searchBox.sendKeys(Keys.RETURN);
        Thread.sleep(5000);

        
        String Apartmentname = "//div[@class='qBF1Pd fontHeadlineSmall ']";

       
        String listingsContainerXpath = "//div[@role='feed']";

       
        WebElement scrollableDiv = driver.findElement(By.xpath(listingsContainerXpath));

        Set<String> apartmentSet = new HashSet<>();
        int scrollCount = 0;

        WebDriverWait wait = new WebDriverWait(driver, 10);

        while (scrollCount < 15) {
           
            List<WebElement> apartments = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(Apartmentname)));

           
            for (WebElement apt : apartments) {
                apartmentSet.add(apt.getText());
            }

           
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollBy(0, 700);", scrollableDiv);
            Thread.sleep(5000); 

            scrollCount++;
        }

        System.out.println("Total Apartments Collected: " + apartmentSet.size());
        
        
        for (String name : apartmentSet) {
            System.out.println(name);
        }

        // driver.quit();

    } catch (Exception e) {
        e.printStackTrace();
    }
 }
}


