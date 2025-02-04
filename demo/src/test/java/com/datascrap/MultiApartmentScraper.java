package com.datascrap;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class MultiApartmentScraper {

    public static void main(String[] args) throws InterruptedException {
        try {
            // Setup ChromeDriver
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            WebDriver driver = new ChromeDriver(options);
            WebDriverWait wait = new WebDriverWait(driver, 10);

            // Create an Excel workbook and sheet
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Apartments Data");

            // Set column headers
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Apartment Name");
            headerRow.createCell(1).setCellValue("Address");
            headerRow.createCell(2).setCellValue("Contact");

            // Open Google Maps
            driver.get("https://www.google.com/maps");
            Thread.sleep(3000);

            // Search for "Apartments near me"
            WebElement searchBox = driver.findElement(By.name("q"));
            searchBox.sendKeys("Apartments near me");
            searchBox.sendKeys(Keys.RETURN);
            Thread.sleep(5000);

            // Track apartments to avoid duplicates
            Set<String> processedApartments = new HashSet<>();
            int totalApartments = 0;
            int rowIndex = 1; // Starting row index for data insertion

            // Loop through continuous scrolling
            while (true) {
                // Find all apartments in the list
                String apartmentXpath = "//div[@class='Nv2PK THOPZb CpccDe ']/a[1]";
                List<WebElement> apartments = wait
                        .until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(apartmentXpath)));

                // Loop through each apartment
                for (int i = 0; i < apartments.size(); i++) {
                    try {
                        WebElement apt = apartments.get(i);

                        // Get apartment name and ensure it's unique
                        String apartmentName = apt.getAttribute("aria-label");
                        if (processedApartments.contains(apartmentName)) {
                            continue; // Skip if already processed
                        }
                        processedApartments.add(apartmentName);

                        // Scroll into view & click apartment
                        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});",
                                apt);
                        Thread.sleep(1000);
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", apt);
                        Thread.sleep(4000); // Wait for details to load

                        // Extract Address using XPath
                        String address = "N/A";
                        try {
                            WebElement addressElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                                    By.xpath("//div[@class='Io6YTe fontBodyMedium kR99db fdkmkc '][1]")));
                            address = addressElement.getText();
                        } catch (Exception e) {
                            System.out.println("No address found for: " + apartmentName);
                        }

                        // **Try to Extract Contact Using Multiple XPaths** (Fallback logic)
                        String contact = "N/A";
                        try {
                            // Try second XPath (for the second position of the contact)
                            contact = getContactFromXpath(driver,
                                    "//*[@id=\"QA0Szd\"]/div/div/div[1]/div[3]/div/div[1]/div/div/div[2]/div[7]/div[4]/button/div/div[2]/div[1]");

                            // If no contact found, try third XPath (for the third position of the contact)
                            if (contact.equals("N/A")) {
                                contact = getContactFromXpath(driver,
                                        "//*[@id=\"QA0Szd\"]/div/div/div[1]/div[3]/div/div[1]/div/div/div[2]/div[7]/div[5]/button/div/div[2]/div[1]");
                            }

                            // If still no contact, try fourth XPath (for the fourth position of the
                            // contact)

                            if (contact.equals("N/A")) {
                                contact = getContactFromXpath(driver,
                                        "//*[@id=\"QA0Szd\"]/div/div/div[1]/div[3]/div/div[1]/div/div/div[2]/div[7]/div[6]/button/div/div[2]/div[1]");
                            }

                            // If still no contact, try fifth XPath (for the fifth position of the contact)

                            if (contact.equals("N/A")) {
                                contact = getContactFromXpath(driver,
                                        "//*[@id=\"QA0Szd\"]/div/div/div[1]/div[3]/div/div[1]/div/div/div[2]/div[7]/div[7]/button/div/div[2]/div[1]");
                            }

                            // Check if the contact is a valid phone number
                            if (contact.equals("N/A") || !isValidPhoneNumber(contact)) {
                                System.out.println(
                                        "❌ No valid contact found or found invalid contact (likely working hours or location) for: "
                                                + apartmentName);
                                contact = "N/A"; // Set contact to "N/A" if it's not valid
                            } else {
                                System.out.println(" Contact Found: " + contact);
                            }

                        } catch (Exception e) {
                            System.out.println("Unable to extract contact for: " + apartmentName);
                        }

                        // Write data into the Excel sheet
                        Row row = sheet.createRow(rowIndex++);
                        row.createCell(0).setCellValue(apartmentName);
                        row.createCell(1).setCellValue(address);
                        row.createCell(2).setCellValue(contact);

                        // Increment total apartments count
                        totalApartments++;

                    } catch (Exception e) {
                        System.out.println("Error processing apartment: " + (i + 1));
                        e.printStackTrace();
                    }
                }

                // Scroll down to load more apartments (scroll to bottom)
                ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
                Thread.sleep(3000); // Wait for new apartments to load

                // Check if new apartments have been added by comparing the size
                List<WebElement> newApartments = driver.findElements(By.xpath(apartmentXpath));
                if (newApartments.size() == apartments.size()) {
                    System.out.println("No new apartments found. Exiting...");
                    break; // Exit the loop if no new apartments are loaded
                }
            }

            // Write the Excel file to disk

            String filepath = "C:\\Users\\CTV_QCPC\\Desktop\\demo maven\\DataScrapping\\demo\\ApartmentDetails.xlsx"+Math.random();
            FileOutputStream fileOut = new FileOutputStream(filepath);

            workbook.write(fileOut);
            fileOut.close();
            System.out.println("Total apartments found: " + totalApartments);
            driver.quit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // **Helper Function to Extract Contact From Given XPath**
    public static String getContactFromXpath(WebDriver driver, String xpath) {
        try {
            WebElement contactElement = driver.findElement(By.xpath(xpath));
            return contactElement.getText().trim();
        } catch (Exception e) {
            return "N/A"; // Return "N/A" if no contact is found for the given XPath
        }
    }

    // **Updated Helper Function to Check If Extracted Text is a Valid Phone
    // Number**
    public static boolean isValidPhoneNumber(String text) {
        // Check if the text contains alphabets (exclude such cases)
        if (text.matches(".*[A-Za-z]+.*")) {
            return false; // Exclude text with alphabets (likely geo-location or working hours)
        }

        // Valid phone number patterns allow digits, +, spaces, or hyphens
        String phoneRegex = "^(\\+\\d{1,3}\\s?)?\\d{3,5}(\\s?[-\\s]?\\d{3,}){1,3}$";

        // Return true if the text matches a valid phone number format
        return Pattern.matches(phoneRegex, text);
    }
}



// package com.datascrap;

// import org.openqa.selenium.*;
// import org.openqa.selenium.chrome.ChromeDriver;
// import org.openqa.selenium.chrome.ChromeOptions;
// import org.openqa.selenium.support.ui.ExpectedConditions;
// import org.openqa.selenium.support.ui.WebDriverWait;
// import io.github.bonigarcia.wdm.WebDriverManager;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Set;

// public class MultiApartmentScraper {

// public static void main(String[] args) throws InterruptedException {

// try {
// // Setup ChromeDriver
// WebDriverManager.chromedriver().setup();
// ChromeOptions options = new ChromeOptions();
// options.addArguments("--start-maximized");
// WebDriver driver = new ChromeDriver(options);
// WebDriverWait wait = new WebDriverWait(driver, 10);

// // Open Google Maps
// driver.get("https://www.google.com/maps");
// Thread.sleep(3000);

// // Search for "Apartments near me"
// WebElement searchBox = driver.findElement(By.name("q"));
// searchBox.sendKeys("Apartments near me");
// searchBox.sendKeys(Keys.RETURN);
// Thread.sleep(5000);

// // Track apartments to avoid duplicates
// Set<String> processedApartments = new HashSet<>();
// int totalApartments = 0;

// // Loop through continuous scrolling
// while (true) {
// // Find all apartments in the list
// String apartmentXpath = "//div[@class='Nv2PK THOPZb CpccDe ']/a[1]";
// List<WebElement> apartments =
// wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(apartmentXpath)));

// // Loop through each apartment
// for (int i = 0; i < apartments.size(); i++) {
// try {
// WebElement apt = apartments.get(i);

// // Get apartment name and ensure it's unique
// String apartmentName = apt.getAttribute("aria-label");
// if (processedApartments.contains(apartmentName)) {
// continue; // Skip if already processed
// }
// processedApartments.add(apartmentName);

// // Scroll into view & click apartment
// ((JavascriptExecutor)
// driver).executeScript("arguments[0].scrollIntoView({block: 'center'});",
// apt);
// Thread.sleep(1000);
// ((JavascriptExecutor) driver).executeScript("arguments[0].click();", apt);
// Thread.sleep(4000); // Wait for details to load

// // Extract Address using XPath
// String address = "N/A";
// try {
// WebElement addressElement =
// wait.until(ExpectedConditions.presenceOfElementLocated(
// By.xpath("//div[@class='Io6YTe fontBodyMedium kR99db fdkmkc '][1]")));
// address = addressElement.getText();
// } catch (Exception e) {
// System.out.println("No address found for: " + apartmentName);
// }

// // **Scroll Inside the Popup Dynamically**
// try {
// WebElement popupContainer =
// driver.findElement(By.xpath("//*[@id=\"QA0Szd\"]/div/div/div[1]/div[3]/div/div[1]/div/div/div[2]"));
// for (int scrolls = 0; scrolls < 2; scrolls++) { // Scroll multiple times
// ((JavascriptExecutor) driver).executeScript("arguments[0].scrollBy(0, 300);",
// popupContainer);
// Thread.sleep(3000); // Wait for elements to load

// // **Try to Extract Contact After Each Scroll**
// try {
// WebElement contactElement =
// driver.findElement(By.xpath("//*[@id=\"QA0Szd\"]/div/div/div[1]/div[3]/div/div[1]/div/div/div[2]/div[7]/div[5]/button/div/div[2]/div[1]"));
// String contact = contactElement.getText();

// System.out.println(" Contact Found: " + contact);
// break; // Exit the loop if contact is found
// } catch (Exception e) {
// System.out.println("Contact not found yet, scrolling again...");
// }
// }
// } catch (Exception e) {
// System.out.println("Popup container not found or unable to scroll.");
// }

// // Print extracted details
// System.out.println("----------------------------------------");
// System.out.println("Apartment Name: " + apartmentName);
// System.out.println("Address: " + address);
// System.out.println("----------------------------------------");
// totalApartments++;

// } catch (Exception e) {
// System.out.println("Error processing apartment: " + (i + 1));
// e.printStackTrace();
// }
// }

// // Scroll down to load more apartments (scroll to bottom)
// ((JavascriptExecutor) driver).executeScript("window.scrollTo(0,
// document.body.scrollHeight)");
// Thread.sleep(3000); // Wait for new apartments to load

// // Check if new apartments have been added by comparing the size
// List<WebElement> newApartments =
// driver.findElements(By.xpath(apartmentXpath));
// if (newApartments.size() == apartments.size()) {
// System.out.println("No new apartments found. Exiting...");
// break; // Exit the loop if no new apartments are loaded
// }
// }

// // Print the total number of apartments found at the end
// System.out.println("Total apartments found: " + totalApartments);

// // Close browser
// driver.quit();

// } catch (Exception e) {
// e.printStackTrace();
// }
// }
// }

// package com.datascrap;

// import org.openqa.selenium.By;
// import org.openqa.selenium.JavascriptExecutor;
// import org.openqa.selenium.Keys;
// import org.openqa.selenium.WebDriver;
// import org.openqa.selenium.WebElement;
// import org.openqa.selenium.chrome.ChromeDriver;
// import org.openqa.selenium.chrome.ChromeOptions;
// import org.openqa.selenium.support.ui.ExpectedConditions;
// import org.openqa.selenium.support.ui.WebDriverWait;

// import io.github.bonigarcia.wdm.WebDriverManager;

// public class simpledatascrap {

// public static void main(String[] args) throws InterruptedException {

// try {
// // Setup ChromeDriver
// WebDriverManager.chromedriver().setup();
// ChromeOptions options = new ChromeOptions();
// options.addArguments("--start-maximized");
// WebDriver driver = new ChromeDriver(options);
// WebDriverWait wait = new WebDriverWait(driver, 10);

// // Open Google Maps
// driver.get("https://www.google.com/maps");
// Thread.sleep(3000);

// // Search for "Apartments near me"
// WebElement searchBox = driver.findElement(By.name("q"));
// searchBox.sendKeys("Apartments near me");
// searchBox.sendKeys(Keys.RETURN);
// Thread.sleep(5000);

// // ✅ XPath for First Apartment Listing
// String firstApartmentXpath ="//div[@class='Nv2PK THOPZb CpccDe ']/a[1]";

// // Wait until the first apartment appears
// WebElement firstApartment =
// wait.until(ExpectedConditions.elementToBeClickable(By.xpath(firstApartmentXpath)));

// // ✅ Click on the first apartment
// ((JavascriptExecutor) driver).executeScript("arguments[0].click();",
// firstApartment);
// Thread.sleep(4000); // Wait for details to load

// // ✅ Extract Name, Address, and Contact
// String apartmentName = firstApartment.getAttribute("aria-label");
// String address = "N/A";
// String contact = "N/A";

// try {
// WebElement addressElement =
// wait.until(ExpectedConditions.presenceOfElementLocated(
// By.xpath("//div[@class=\"Io6YTe fontBodyMedium kR99db fdkmkc \"][1]")));
// address = addressElement.getText();
// } catch (Exception e) {
// System.out.println("No address found.");
// }

// try {
// WebElement contactElement =
// driver.findElement(By.xpath("//*[@id=\"QA0Szd\"]/div/div/div[1]/div[3]/div/div[1]/div/div/div[2]/div[7]/div[5]/button/div/div[1]/span"));
// contact = contactElement.getAttribute("aria-hidden").replace("Call ", "");
// } catch (Exception e) {
// System.out.println("No contact found.");
// }

// // ✅ Print extracted details
// System.out.println("Apartment Name: " + apartmentName);
// System.out.println("Address: " + address);
// System.out.println("Contact: " + contact);

// // Close browser
// // driver.quit();

// } catch (Exception e) {
// e.printStackTrace();
// }
// }

// }
