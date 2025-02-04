package com.datascrap;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import io.github.bonigarcia.wdm.WebDriverManager;

public class exampleDataScrap {

    @Test
    public static void main(String[] args) throws InterruptedException {

        try {
            // Setup ChromeDriver
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            WebDriver driver = new ChromeDriver(options);
            WebDriverWait wait = new WebDriverWait(driver, 10);

            // Open Google Maps
            driver.get("https://www.google.com/maps");
            Thread.sleep(3000);

            // Search for "Apartments near me"
            WebElement searchBox = driver.findElement(By.name("q"));
            searchBox.sendKeys("Apartments near me");
            searchBox.sendKeys(Keys.RETURN);
            Thread.sleep(5000);

            //  Corrected XPaths
            String apartmentXpath = "//div[@class=\"Nv2PK THOPZb CpccDe \"]/a"; // Clickable apartment links
            String listingsContainerXpath = "//div[@role='feed']"; // Scrollable listing container
            String addressXpath = "//div[contains(@class, 'fontBodyMedium') and not(contains(text(), 'Closed'))]"; // Address
            String contactXpath = "//button[contains(@aria-label, 'Call')]"; // Contact Button

            // Locate the scrollable container
            WebElement scrollableDiv = driver.findElement(By.xpath(listingsContainerXpath));

            // Store apartment data
            Set<String> apartmentSet = new HashSet<>();

            // Excel setup
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Apartments");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Apartment Name");
            headerRow.createCell(1).setCellValue("Address");
            headerRow.createCell(2).setCellValue("Contact Number");

            int rowNum = 1;
            int scrollCount = 0;

            while (scrollCount < 10) { // Adjust scroll limit as needed
                // Wait until apartment listings load
                List<WebElement> apartments = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(apartmentXpath)));

                for (WebElement apt : apartments) {
                    String aptName = apt.getAttribute("aria-label");
                    if (!apartmentSet.contains(aptName)) { // Avoid duplicates
                        apartmentSet.add(aptName);

                        //  Close popups before clicking
                        try {
                            WebElement closeButton = driver.findElement(By.xpath("//button[@aria-label='Close']"));
                            closeButton.click();
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            System.out.println("No popup found.");
                        }

                        //  Scroll into view & click apartment
                        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", apt);
                        Thread.sleep(1000);
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", apt);
                        Thread.sleep(4000); // Wait for details to load

                        //  Extract Address
                        String aptAddress = "N/A";
                        try {
                            WebElement addressElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(addressXpath)));
                            aptAddress = addressElement.getText();
                        } catch (Exception e) {
                            System.out.println("No address found for: " + aptName);
                        }

                        //  Extract Contact Details
                        String aptContact = "N/A";
                        try {
                            WebElement contactElement = driver.findElement(By.xpath(contactXpath));
                            aptContact = contactElement.getAttribute("aria-label").replace("Call ", "");
                        } catch (Exception e) {
                            System.out.println("No contact found for: " + aptName);
                        }

                        //  Store data in Excel
                        Row row = sheet.createRow(rowNum++);
                        row.createCell(0).setCellValue(aptName);
                        row.createCell(1).setCellValue(aptAddress);
                        row.createCell(2).setCellValue(aptContact);

                        //  Go back to results list
                        driver.navigate().back();
                        Thread.sleep(3000);
                    }
                }

                //  Scroll inside the listings panel
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollBy(0, 500);", scrollableDiv);
                Thread.sleep(3000);

                scrollCount++;
            }

            // Save data to Excel
            try (FileOutputStream fileOut = new FileOutputStream("Apartments.xlsx")) {
                workbook.write(fileOut);
                System.out.println("Excel file saved successfully!");
            } catch (IOException e) {
                e.printStackTrace();
            }

            workbook.close();
            driver.quit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
