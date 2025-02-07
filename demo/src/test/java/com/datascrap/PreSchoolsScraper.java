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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PreSchoolsScraper {

    public static void main(String[] args) {
        try {
            // Setup ChromeDriver
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            WebDriver driver = new ChromeDriver(options);
            WebDriverWait wait = new WebDriverWait(driver, 10);

            // Create an Excel workbook and sheet
            XSSFWorkbook workbooks = new XSSFWorkbook();
            XSSFSheet sheet = workbooks.createSheet("PreSchools Data");

            // Set column headers
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("PreSchool Name");
            headerRow.createCell(1).setCellValue("Address");
            headerRow.createCell(2).setCellValue("Contact");

            // Open Google Maps
            driver.get("https://www.google.com/maps");
            Thread.sleep(3000);

            // Search for "PreSchools near me"
            WebElement searchBox = driver.findElement(By.name("q"));
            searchBox.sendKeys("PreSchools near me");
            searchBox.sendKeys(Keys.RETURN);
            Thread.sleep(5000);

            // Track preschools to avoid duplicates
            Set<String> processedPreschools = new HashSet<>();
            int totalPreschools = 0;
            int rowIndex = 1;

            // XPath for preschools inside the results list
            String preschoolXpath = "//div[@class='Nv2PK tH5CWc THOPZb ']/a[1]";
            String scrollContainerXpath = "//div[contains(@aria-label, 'Results for')]/following-sibling::div";

            // Infinite scrolling until no new results are found
            int previousCount = 0;
            int sameCountScrolls = 0;
            int maxNoChangeScrolls = 10; // Increased from 3 to 10 for better coverage

            while (sameCountScrolls < maxNoChangeScrolls) {
                // Find all preschools in the list
                List<WebElement> preschools = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(preschoolXpath)));

                for (int i = previousCount; i < preschools.size(); i++) {
                    try {
                        WebElement preschool = preschools.get(i);
                        String preschoolName = preschool.getAttribute("aria-label");

                        if (processedPreschools.contains(preschoolName)) {
                            continue;
                        }
                        processedPreschools.add(preschoolName);

                        // Scroll into view & click preschool
                        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", preschool);
                        Thread.sleep(3000);
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", preschool);
                        Thread.sleep(6000);

                        // Extract Address
                        String address = getElementText(wait, "//div[@class='Io6YTe fontBodyMedium kR99db fdkmkc '][1]");

                        // Extract Contact using loop
                        String contact = getValidContact(driver, wait);

                        // Write data into Excel
                        Row row = sheet.createRow(rowIndex++);
                        row.createCell(0).setCellValue(preschoolName);
                        row.createCell(1).setCellValue(address);
                        row.createCell(2).setCellValue(contact);

                        totalPreschools++;

                    } catch (Exception e) {
                        System.out.println("Error processing preschool: " + (i + 1));
                        e.printStackTrace();
                    }
                }

                // Scroll inside the list container aggressively
                WebElement scrollContainer = driver.findElement(By.xpath(scrollContainerXpath));
                for (int j = 0; j < 5; j++) {  // Scroll multiple times before checking
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollTop += 1000;", scrollContainer);
                    Thread.sleep(2000);
                }

                // Check if new preschools have been added
                List<WebElement> newPreschools = driver.findElements(By.xpath(preschoolXpath));
                if (newPreschools.size() == previousCount) {
                    sameCountScrolls++;
                } else {
                    sameCountScrolls = 0;
                }
                previousCount = newPreschools.size();
            }

            System.out.println("No new preschools found. Exiting...");

            // Write Excel file to disk
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String filepath = "C:\\Users\\CTV_QCPC\\Desktop\\demo maven\\DataScrapping\\demo\\PreSchoolsDetails_" + timestamp + ".xlsx";
            FileOutputStream fileOut = new FileOutputStream(filepath);
            workbooks.write(fileOut);
            fileOut.close();

            System.out.println("Total PreSchools found: " + totalPreschools);
            driver.quit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // **Optimized Helper Function to Extract Contact Using a Loop**
    public static String getValidContact(WebDriver driver, WebDriverWait wait) {
        // Primary contact XPath
        String primaryXpath = "//div[@class='OMl5r hH0dDd jBYmhd']";
        String primaryContact = getElementText(wait, primaryXpath);
        if (!primaryContact.equals("N/A") && isValidPhoneNumber(primaryContact)) {
            return primaryContact;
        }

        // Loop through dynamically generated XPaths
        for (int i = 5; i <= 7; i++) {
            String xpath = "//*[@id='QA0Szd']/div/div/div[1]/div[3]/div/div[1]/div/div/div[2]/div[7]/div[" + i + "]/button/div/div[2]/div[1]";
            String contact = getElementText(wait, xpath);

            if (!contact.equals("N/A") && isValidPhoneNumber(contact)) {
                return contact;
            }
        }

        return "N/A";  // Return "N/A" if no valid contact is found
    }

    // **Helper Function to Get Text from XPath with Explicit Wait**
    public static String getElementText(WebDriverWait wait, String xpath) {
        try {
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
            return element.getText().trim();
        } catch (Exception e) {
            return "N/A";
        }
    }

    // **Helper Function to Validate Phone Numbers**
    public static boolean isValidPhoneNumber(String text) {
        return text.matches("^(\\+\\d{1,3}\\s?)?\\d{3,5}(\\s?[-\\s]?\\d{3,}){1,3}$");
    }
}




// package com.datascrap;

// import org.apache.poi.xssf.usermodel.XSSFSheet;
// import org.apache.poi.xssf.usermodel.XSSFWorkbook;
// import org.apache.poi.ss.usermodel.Row;
// import org.openqa.selenium.*;
// import org.openqa.selenium.chrome.ChromeDriver;
// import org.openqa.selenium.chrome.ChromeOptions;
// import org.openqa.selenium.support.ui.ExpectedConditions;
// import org.openqa.selenium.support.ui.WebDriverWait;
// import io.github.bonigarcia.wdm.WebDriverManager;

// import java.io.FileOutputStream;
// import java.text.SimpleDateFormat;
// import java.util.Date;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Set;
// import java.util.regex.Pattern;

// public class PreSchoolsScraper {

//     public static void main(String[] args) {
//         try {
//             // Setup ChromeDriver
//             WebDriverManager.chromedriver().setup();
//             ChromeOptions options = new ChromeOptions();
//             options.addArguments("--start-maximized");
//             WebDriver driver = new ChromeDriver(options);
//             WebDriverWait wait = new WebDriverWait(driver, 10);

//             // Create an Excel workbook and sheet
//             XSSFWorkbook workbooks = new XSSFWorkbook();
//             XSSFSheet sheet = workbooks.createSheet("PreSchools Data");

//             // Set column headers
//             Row headerRow = sheet.createRow(0);
//             headerRow.createCell(0).setCellValue("PreSchool Name");
//             headerRow.createCell(1).setCellValue("Address");
//             headerRow.createCell(2).setCellValue("Contact");

//             // Open Google Maps
//             driver.get("https://www.google.com/maps");
//             Thread.sleep(3000);

//             // Search for "preschools near me"
//             WebElement searchBox = driver.findElement(By.name("q"));
//             searchBox.sendKeys("PreSchools near me");
//             searchBox.sendKeys(Keys.RETURN);
//             Thread.sleep(5000);

//             // Track preschools to avoid duplicates
//             Set<String> processedPreschools = new HashSet<>();
//             int totalPreschools = 0;
//             int rowIndex = 1; // Starting row index for data insertion

//             // Loop through continuous scrolling
//             while (true) {
//                 // Find all preschools in the list
//                 String preschoolXpath = "//div[@class='Nv2PK tH5CWc THOPZb ']/a[1]";
//                 List<WebElement> preschools = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(preschoolXpath)));

//                 // Loop through each preschool
//                 for (int i = 0; i < preschools.size(); i++) {
//                     try {
//                         WebElement preschool = preschools.get(i);

//                         // Get preschool name and ensure it's unique
//                         String preschoolName = preschool.getAttribute("aria-label");
//                         if (processedPreschools.contains(preschoolName)) {
//                             continue; // Skip if already processed
//                         }
//                         processedPreschools.add(preschoolName);

//                         // Scroll into view & click preschool
//                         ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", preschool);
//                         Thread.sleep(3000);
//                         ((JavascriptExecutor) driver).executeScript("arguments[0].click();", preschool);
//                         Thread.sleep(3000); // Wait for details to load

//                         // Extract Address
//                         String address = getElementText(wait, "//div[@class='Io6YTe fontBodyMedium kR99db fdkmkc '][1]");

//                         // Extract Contact using multiple XPaths
//                         String contact = getValidContact(driver, wait);

//                         // Write data into the Excel sheet
//                         Row row = sheet.createRow(rowIndex++);
//                         row.createCell(0).setCellValue(preschoolName);
//                         row.createCell(1).setCellValue(address);
//                         row.createCell(2).setCellValue(contact);

//                         totalPreschools++;

//                     } catch (Exception e) {
//                         System.out.println("Error processing preschool: " + (i + 1));
//                         e.printStackTrace();
//                     }
//                 }

//                 // Scroll down to load more preschools
//                 ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
//                 Thread.sleep(3000);

//                 // Check if new preschools have been added
//                 List<WebElement> newPreschools = driver.findElements(By.xpath(preschoolXpath));
//                 if (newPreschools.size() == preschools.size()) {
//                     System.out.println("No new preschools found. Exiting...");
//                     break;
//                 }
//             }

//             // Write the Excel file to disk
//             String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//             String filepath = "C:\\Users\\CTV_QCPC\\Desktop\\demo maven\\DataScrapping\\demo\\PreSchoolsDetails_" + timestamp + ".xlsx";
//             FileOutputStream fileOut = new FileOutputStream(filepath);
//             workbooks.write(fileOut);
//             fileOut.close();

//             System.out.println("Total PreSchools found: " + totalPreschools);
//             // driver.quit();

//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }

//     // **Helper Function to Extract Contact from Multiple XPaths**
//     public static String getValidContact(WebDriver driver, WebDriverWait wait) {
//         String[] contactXpaths = {
//             "//div[@class='OMl5r hH0dDd jBYmhd']", 
//             "//*[@id='QA0Szd']/div/div/div[1]/div[3]/div/div[1]/div/div/div[2]/div[7]/div[5]/button/div/div[2]/div[1]",
//             "//*[@id='QA0Szd']/div/div/div[1]/div[3]/div/div[1]/div/div/div[2]/div[7]/div[6]/button/div/div[2]/div[1]",
//             "//*[@id='QA0Szd']/div/div/div[1]/div[3]/div/div[1]/div/div/div[2]/div[7]/div[7]/button/div/div[2]/div[1]"
//         };

//         for (String xpath : contactXpaths) {
//             String contact = getElementText(wait, xpath);
//             if (!contact.equals("N/A") && isValidPhoneNumber(contact)) {
//                 return contact; // Return first valid contact
//             }
//         }
//         return "N/A"; // Return "N/A" if no valid contact is found
//     }

//     // **Helper Function to Get Text from Given XPath with Explicit Wait**
//     public static String getElementText(WebDriverWait wait, String xpath) {
//         try {
//             WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
//             return element.getText().trim();
//         } catch (Exception e) {
//             return "N/A"; // Return "N/A" if element not found
//         }
//     }

//     // **Helper Function to Validate Phone Number Format**
//     public static boolean isValidPhoneNumber(String text) {
//         if (text.matches(".*[A-Za-z]+.*")) {
//             return false; // Exclude text with alphabets (likely geo-location or working hours)
//         }
//         String phoneRegex = "^(\\+\\d{1,3}\\s?)?\\d{3,5}(\\s?[-\\s]?\\d{3,}){1,3}$";
//         return Pattern.matches(phoneRegex, text);
//     }
// }



// package com.datascrap;

// import org.apache.poi.xssf.usermodel.XSSFSheet;
// import org.apache.poi.xssf.usermodel.XSSFWorkbook;
// import org.apache.poi.ss.usermodel.Row;
// import org.openqa.selenium.*;
// import org.openqa.selenium.chrome.ChromeDriver;
// import org.openqa.selenium.chrome.ChromeOptions;
// import org.openqa.selenium.support.ui.ExpectedConditions;
// import org.openqa.selenium.support.ui.WebDriverWait;
// import io.github.bonigarcia.wdm.WebDriverManager;

// import java.io.FileOutputStream;
// import java.text.SimpleDateFormat;
// import java.util.Date;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Set;
// import java.util.regex.Pattern;

// public class PreSchoolsScraper {

//     public static void main(String[] args) throws InterruptedException {
//         try {
//             // Setup ChromeDriver
//             WebDriverManager.chromedriver().setup();
//             ChromeOptions options = new ChromeOptions();
//             options.addArguments("--start-maximized");
//             WebDriver driver = new ChromeDriver(options);
//             WebDriverWait wait = new WebDriverWait(driver, 10);

//             // Create an Excel workbook and sheet
//             XSSFWorkbook workbooks = new XSSFWorkbook();
//             XSSFSheet sheet = workbooks.createSheet("PreSchools Data");

//             // Set column headers
//             Row headerRow = sheet.createRow(0);
//             headerRow.createCell(0).setCellValue("PreSchools Name");
//             headerRow.createCell(1).setCellValue("Address");
//             headerRow.createCell(2).setCellValue("Contact");

//             // Open Google Maps
//             driver.get("https://www.google.com/maps");
//             Thread.sleep(3000);

//             // Search for "preschools near me"
//             WebElement searchBox = driver.findElement(By.name("q"));
//             searchBox.sendKeys("PreSchools near me");
//             searchBox.sendKeys(Keys.RETURN);
//             Thread.sleep(5000);

//             // Track preschools to avoid duplicates
//             Set<String> processedpreschools = new HashSet<>();
//             int totalpreschools = 0;
//             int rowIndex = 1; // Starting row index for data insertion

//             // Loop through continuous scrolling
//             while (true) {
//                 // Find all preschools in the list
//                 String preschoolXpath = "//div[@class=\"Nv2PK tH5CWc THOPZb \"]/a[1] ";
//                 List<WebElement> preschool = wait
//                         .until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(preschoolXpath)));

//                 // Loop through each preschools
//                 for (int i = 0; i < preschool.size(); i++) {
//                     try {
//                         WebElement apt = preschool.get(i);

//                         // Get preschools name and ensure it's unique
//                         String preschoolName = apt.getAttribute("aria-label");
//                         if (processedpreschools.contains(preschoolName)) {
//                             continue; // Skip if already processed
//                         }
//                         processedpreschools.add(preschoolName);

//                         // Scroll into view & click preschools
//                         ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});",
//                                 apt);
//                         Thread.sleep(3000);
//                         ((JavascriptExecutor) driver).executeScript("arguments[0].click();", apt);
//                         Thread.sleep(6000); // Wait for details to load

//                         // Extract Address using XPath
//                         String address = "N/A";
//                         try {
//                             WebElement addressElement = wait.until(ExpectedConditions.presenceOfElementLocated(
//                                     By.xpath("//div[@class=\"Io6YTe fontBodyMedium kR99db fdkmkc \"][1]")));
//                             address = addressElement.getText();
//                         } catch (Exception e) {
//                             System.out.println("No address found for: " + preschoolName);
//                         }

//                         // **Try to Extract Contact Using Multiple XPaths** (Fallback logic)
//                         String contact = "N/A";
//                         try {
//                             // Try second XPath (for the second position of the contact)
//                             contact = getContactFromXpath(driver,
//                                    "//div[@class='OMl5r hH0dDd jBYmhd']");

//                             // If no contact found, try third XPath (for the third position of the contact)
//                             if (contact.equals("N/A")) {
//                                 contact = getContactFromXpath(driver,
//                                         "//*[@id=\"QA0Szd\"]/div/div/div[1]/div[3]/div/div[1]/div/div/div[2]/div[7]/div[5]/button/div/div[2]/div[1]");
//                             }

//                             // If still no contact, try fourth XPath (for the fourth position of the contact)
//                             if (contact.equals("N/A")) {
//                                 contact = getContactFromXpath(driver,
//                                         "//*[@id=\"QA0Szd\"]/div/div/div[1]/div[3]/div/div[1]/div/div/div[2]/div[7]/div[6]/button/div/div[2]/div[1]");
//                             }

//                             // If still no contact, try fifth XPath (for the fifth position of the contact)
//                             if (contact.equals("N/A")) {
//                                 contact = getContactFromXpath(driver,
//                                         "//*[@id=\"QA0Szd\"]/div/div/div[1]/div[3]/div/div[1]/div/div/div[2]/div[7]/div[7]/button/div/div[2]/div[1]");
//                             }

//                             // Check if the contact is a valid phone number
//                             if (contact.equals("N/A") || !isValidPhoneNumber(contact)) {
//                                 System.out.println(
//                                         " No valid contact found or found invalid contact (likely working hours or location) for: "
//                                                 + preschoolName);
//                                 contact = "N/A"; // Set contact to "N/A" if it's not valid
//                             } else {
//                                 System.out.println(" Contact Found: " + contact);
//                             }

//                         } catch (Exception e) {
//                             System.out.println("Unable to extract contact for: " + preschoolName);
//                         }

//                         // Write data into the Excel sheet
//                         Row row = sheet.createRow(rowIndex++);
//                         row.createCell(0).setCellValue(preschoolName);
//                         row.createCell(1).setCellValue(address);
//                         row.createCell(2).setCellValue(contact);

//                         // Increment total apartments count
//                         totalpreschools++;

//                     } catch (Exception e) {
//                         System.out.println("Error processing apartment: " + (i + 1));
//                         e.printStackTrace();
//                     }
//                 }

//                 // Scroll down to load more preschools (scroll to bottom)
//                 ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
//                 Thread.sleep(6000); // Wait for new preschools to load

//                 // Check if new preschools have been added by comparing the size
//                 List<WebElement> newpreschools = driver.findElements(By.xpath(preschoolXpath));
//                 if (newpreschools.size() == preschool.size()) {
//                     System.out.println("No new preschools found. Exiting...");
//                     break; // Exit the loop if no new preschools are loaded
//                 }
//             }

//             // Write the Excel file to disk

//             String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//             String filepath = "C:\\Users\\CTV_QCPC\\Desktop\\demo maven\\DataScrapping\\demo\\PreSchoolsDetails_ " +timestamp + ".xlsx";
//             FileOutputStream fileOut = new FileOutputStream(filepath);

//             workbooks.write(fileOut);
//             fileOut.close();
//             System.out.println("Total PreSchools found: " + totalpreschools);
//             driver.quit();

//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }

//     // **Helper Function to Extract Contact From Given XPath**
//     public static String getContactFromXpath(WebDriver driver, String xpath) {
//         try {
//             WebElement contactElement = driver.findElement(By.xpath(xpath));
//             return contactElement.getText().trim();
//         } catch (Exception e) {
//             return "N/A"; // Return "N/A" if no contact is found for the given XPath
//         }
//     }

//     // Updated Helper Function to Check If Extracted Text is a Valid Phone
//     // Number
//     public static boolean isValidPhoneNumber(String text) {

//         // Check if the text contains alphabets (exclude such cases)
//         if (text.matches(".*[A-Za-z]+.*")) {
//             return false; // Exclude text with alphabets (likely geo-location or working hours)
//         }

//         // Valid phone number patterns allow digits, +, spaces, or hyphens
//         String phoneRegex = "^(\\+\\d{1,3}\\s?)?\\d{3,5}(\\s?[-\\s]?\\d{3,}){1,3}$";

//         // Return true if the text matches a valid phone number format
//         return Pattern.matches(phoneRegex, text);
//     }
// }