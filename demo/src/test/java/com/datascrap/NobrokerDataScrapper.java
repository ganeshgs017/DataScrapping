package com.datascrap;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class NobrokerDataScrapper {

    @Test
    public void nobrokerwebscrap(){

        try{



        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.nobroker.in/bangalore/rent?utm_source=google&utm_medium=cpc&utm_campaign=PMax_Commercial_Rent_Bangalore_Office&gad_source=1&gclid=Cj0KCQiAkoe9BhDYARIsAH85cDOTO8xAMViamFkS1LymgCVHSFHEwGX1YgbcWtRbCebtkzBXgERG83UaAlgTEALw_wcB");
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);


        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        Assert.assertTrue(alert.getText().contains("Allow"));
        alert.accept();

    }

    catch(Exception e){

        e.printStackTrace();


    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    }

}
