// package com.datascrap;

// import org.apache.poi.ss.usermodel.Row;
// import org.apache.poi.xssf.usermodel.XSSFSheet;
// import org.apache.poi.xssf.usermodel.XSSFWorkbook;
// import org.openqa.selenium.*;
// import org.openqa.selenium.NoSuchElementException;
// import org.openqa.selenium.chrome.ChromeDriver;
// import org.openqa.selenium.chrome.ChromeOptions;
// import org.openqa.selenium.support.ui.ExpectedConditions;
// import org.openqa.selenium.support.ui.WebDriverWait;
// import io.github.bonigarcia.wdm.WebDriverManager;

// import java.io.FileOutputStream;
// import java.text.SimpleDateFormat;
// import java.util.*;
// import java.util.regex.Pattern;

// public class OptimizedApartmentScraper {

//     public static void main(String[] args) throws InterruptedException {
//         try {
//             // Setup ChromeDriver
//             WebDriverManager.chromedriver().setup();
//             ChromeOptions options = new ChromeOptions();
//             options.addArguments("--start-maximized");
//             WebDriver driver = new ChromeDriver(options);
//             WebDriverWait wait = new WebDriverWait(driver, 10);

//             // Create Excel Workbook
//             XSSFWorkbook workbooks = new XSSFWorkbook();
//             String[] locations = {"Apartments near me", "Apartments in Hulimav", "Luxury apartments"};

//             for (String location : locations) {
//                 XSSFSheet sheet = workbooks.createSheet(location.replace(" ", "_"));
//                 List<String[]> apartmentData = new ArrayList<>();
//                 apartmentData.add(new String[]{"Apartment Name", "Address", "Contact"});

//                 // Open Google Maps
//                 driver.get("https://www.google.com/maps");
//                 Thread.sleep(3000);

//                 // Search for the current location keyword
//                 WebElement searchBox = driver.findElement(By.name("q"));
//                 searchBox.clear();
//                 searchBox.sendKeys(location, Keys.RETURN);
//                 Thread.sleep(5000);

//                 Set<String> processedApartments = new HashSet<>();
//                 JavascriptExecutor js = (JavascriptExecutor) driver;

//                 // Infinite loop to keep scrolling and loading apartments
//                 while (true) {
//                     String apartmentXpath = "//div[@class='Nv2PK THOPZb CpccDe ']/a[1]";
//                     List<WebElement> apartments = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(apartmentXpath)));

//                     for (WebElement apt : apartments) {
//                         try {
//                             String apartmentName = apt.getAttribute("aria-label");
//                             if (!processedApartments.add(apartmentName)) continue;

//                             js.executeScript("arguments[0].scrollIntoView({block: 'center'});", apt);
//                             Thread.sleep(1000);
//                             apt.click();
//                             Thread.sleep(4000);

//                             String address = "N/A";
//                             try {
//                                 WebElement addressElement = wait.until(ExpectedConditions.presenceOfElementLocated(
//                                         By.xpath("//div[@class='Io6YTe fontBodyMedium kR99db fdkmkc '][1]")));
//                                 address = addressElement.getText().trim();
//                                 if (address.isEmpty()) address = "N/A";
//                             } catch (NoSuchElementException | TimeoutException e) {
//                                 System.out.println("[INFO] Address not found for: " + apartmentName);
//                             }

//                             String contact = "N/A";
//                             try {
//                                 List<WebElement> contactElements = driver.findElements(By.xpath(
//                                         "//*[@id=\"QA0Szd\"]/div/div/div[1]/div[3]/div/div[1]/div/div/div[2]/div[7]/div/button/div/div[2]/div[1]"));
//                                 for (WebElement contactElement : contactElements) {
//                                     String extractedContact = contactElement.getText().trim();
//                                     if (isValidPhoneNumber(extractedContact)) {
//                                         contact = extractedContact;
//                                         break;
//                                     }
//                                 }
//                             } catch (Exception e) {
//                                 System.out.println("Unable to extract contact for: " + apartmentName);
//                             }

//                             apartmentData.add(new String[]{apartmentName, address, contact});
//                         } catch (Exception e) {
//                             e.printStackTrace();
//                         }
//                     }

//                     // Scroll to load more apartments until all are fetched
//                     long lastHeight = (long) js.executeScript("return document.body.scrollHeight");
//                     while (true) {
//                         js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
//                         Thread.sleep(2000);
//                         long newHeight = (long) js.executeScript("return document.body.scrollHeight");
//                         if (newHeight == lastHeight) {
//                             System.out.println("No new apartments found. Moving to next search...");
//                             break;
//                         }
//                         lastHeight = newHeight;
//                     }

//                     // Break condition to exit while loop (can be customized as per your requirement)
//                     // Example: After a certain number of apartments, exit the loop
//                     if (processedApartments.size() >= 200) { // Example limit
//                         break;
//                     }
//                 }

//                 // Write apartment data to Excel sheet after data collection
//                 for (int i = 0; i < apartmentData.size(); i++) {
//                     Row row = sheet.createRow(i);
//                     row.createCell(0).setCellValue(apartmentData.get(i)[0]);
//                     row.createCell(1).setCellValue(apartmentData.get(i)[1]);
//                     row.createCell(2).setCellValue(apartmentData.get(i)[2]);
//                 }
//             }

//             // Save the Excel file to disk
//             String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//             String filepath = "C:\\Users\\CTV_QCPC\\Desktop\\demo maven\\DataScrapping\\demo\\ApartmentDetails_" + timestamp + ".xlsx";
//             FileOutputStream fileOut = new FileOutputStream(filepath);
//             workbooks.write(fileOut);
//             fileOut.close();

//             System.out.println("All searches completed. Data saved successfully.");
//             driver.quit();

//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }

//     public static boolean isValidPhoneNumber(String text) {
//         if (text.matches(".*[A-Za-z]+.*")) return false;
//         String phoneRegex = "^(\\+\\d{1,3}\\s?)?\\d{3,5}(\\s?[-\\s]?\\d{3,}){1,3}$";
//         return Pattern.matches(phoneRegex, text);
//     }
// }
///////////////////////////////////////Limited search run///////////////////////////////////////////////////////////////////////////////////////////////////////

// package com.datascrap;

// import org.apache.poi.ss.usermodel.Row;
// import org.apache.poi.xssf.usermodel.XSSFSheet;
// import org.apache.poi.xssf.usermodel.XSSFWorkbook;
// import org.openqa.selenium.*;
// import org.openqa.selenium.NoSuchElementException;
// import org.openqa.selenium.chrome.ChromeDriver;
// import org.openqa.selenium.chrome.ChromeOptions;
// import org.openqa.selenium.support.ui.ExpectedConditions;
// import org.openqa.selenium.support.ui.WebDriverWait;
// import io.github.bonigarcia.wdm.WebDriverManager;

// import java.io.FileOutputStream;
// import java.text.SimpleDateFormat;
// import java.util.*;
// import java.util.regex.Pattern;

// public class OptimizedApartmentScraper {

//     public static void main(String[] args) throws InterruptedException {
//         try {
//             // Setup ChromeDriver
//             WebDriverManager.chromedriver().setup();
//             ChromeOptions options = new ChromeOptions();
//             options.addArguments("--start-maximized");
//             WebDriver driver = new ChromeDriver(options);
//             WebDriverWait wait = new WebDriverWait(driver, 10);

//             // Create Excel Workbook
//             XSSFWorkbook workbooks = new XSSFWorkbook();
//             String[] locations = {"Apartments near me", "Apartments in Hulimav", "Luxury apartments"};

//             for (String location : locations) {
//                 XSSFSheet sheet = workbooks.createSheet(location.replace(" ", "_"));
//                 List<String[]> apartmentData = new ArrayList<>();
//                 apartmentData.add(new String[]{"Apartment Name", "Address", "Contact"});

//                 // Open Google Maps
//                 driver.get("https://www.google.com/maps");
//                 Thread.sleep(3000);

//                 // Search for the current location keyword
//                 WebElement searchBox = driver.findElement(By.name("q"));
//                 searchBox.clear();
//                 searchBox.sendKeys(location, Keys.RETURN);
//                 Thread.sleep(5000);

//                 Set<String> processedApartments = new HashSet<>();
//                 JavascriptExecutor js = (JavascriptExecutor) driver;
//                 int apartmentCount = 0;

//                 // Scroll and load apartments up to 15 per search query
//                 while (apartmentCount < 15) {
//                     String apartmentXpath = "//div[@class='Nv2PK THOPZb CpccDe ']/a[1]";
//                     List<WebElement> apartments = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(apartmentXpath)));
                    
//                     for (WebElement apt : apartments) {
//                         if (apartmentCount >= 15) break;
//                         try {
//                             String apartmentName = apt.getAttribute("aria-label");
//                             if (!processedApartments.add(apartmentName)) continue;

//                             js.executeScript("arguments[0].scrollIntoView({block: 'center'});", apt);
//                             Thread.sleep(1000);
//                             apt.click();
//                             Thread.sleep(4000);

//                             String address = "N/A";
//                             try {
//                                 WebElement addressElement = wait.until(ExpectedConditions.presenceOfElementLocated(
//                                         By.xpath("//div[@class='Io6YTe fontBodyMedium kR99db fdkmkc '][1]")));
//                                 address = addressElement.getText().trim();
//                                 if (address.isEmpty()) address = "N/A";
//                             } catch (NoSuchElementException | TimeoutException e) {
//                                 System.out.println("[INFO] Address not found for: " + apartmentName);
//                             }

//                             String contact = "N/A";
//                             try {
//                                 List<WebElement> contactElements = driver.findElements(By.xpath(
//                                         "//*[@id=\"QA0Szd\"]/div/div/div[1]/div[3]/div/div[1]/div/div/div[2]/div[7]/div/button/div/div[2]/div[1]"));
//                                 for (WebElement contactElement : contactElements) {
//                                     String extractedContact = contactElement.getText().trim();
//                                     if (isValidPhoneNumber(extractedContact)) {
//                                         contact = extractedContact;
//                                         break;
//                                     }
//                                 }
//                             } catch (Exception e) {
//                                 System.out.println("Unable to extract contact for: " + apartmentName);
//                             }

//                             apartmentData.add(new String[]{apartmentName, address, contact});
//                             apartmentCount++;
//                         } catch (Exception e) {
//                             e.printStackTrace();
//                         }
//                     }
                    
//                     // Scroll to load more apartments if less than 15 are found
//                     if (apartmentCount < 15) {
//                         js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
//                         Thread.sleep(2000);
//                     }
//                 }
                
//                 // Write apartment data to Excel sheet after data collection
//                 for (int i = 0; i < apartmentData.size(); i++) {
//                     Row row = sheet.createRow(i);
//                     row.createCell(0).setCellValue(apartmentData.get(i)[0]);
//                     row.createCell(1).setCellValue(apartmentData.get(i)[1]);
//                     row.createCell(2).setCellValue(apartmentData.get(i)[2]);
//                 }
//             }

//             // Save the Excel file to disk
//             String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//             String filepath = "C:\\Users\\CTV_QCPC\\Desktop\\demo maven\\DataScrapping\\demo\\ApartmentDetails_" + timestamp + ".xlsx";
//             FileOutputStream fileOut = new FileOutputStream(filepath);
//             workbooks.write(fileOut);
//             fileOut.close();

//             System.out.println("All searches completed. Data saved successfully.");
//             driver.quit();

//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }

//     public static boolean isValidPhoneNumber(String text) {
//         if (text.matches(".*[A-Za-z]+.*")) return false;
//         String phoneRegex = "^(\\+\\d{1,3}\\s?)?\\d{3,5}(\\s?[-\\s]?\\d{3,}){1,3}$";
//         return Pattern.matches(phoneRegex, text);
//     }
// }

//////////////////////////////////Read from file/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// package com.datascrap;

// import org.apache.poi.ss.usermodel.*;
// import org.apache.poi.xssf.usermodel.XSSFSheet;
// import org.apache.poi.xssf.usermodel.XSSFWorkbook;
// import org.openqa.selenium.*;
// import org.openqa.selenium.NoSuchElementException;
// import org.openqa.selenium.chrome.ChromeDriver;
// import org.openqa.selenium.chrome.ChromeOptions;
// import org.openqa.selenium.support.ui.ExpectedConditions;
// import org.openqa.selenium.support.ui.WebDriverWait;
// import io.github.bonigarcia.wdm.WebDriverManager;

// import java.io.File;
// import java.io.FileInputStream;
// import java.io.FileOutputStream;
// import java.text.SimpleDateFormat;
// import java.util.*;
// import java.util.regex.Pattern;

// public class OptimizedApartmentScraper {

//     public static void main(String[] args) throws InterruptedException {
//         try {
//             // Setup ChromeDriver
//             WebDriverManager.chromedriver().setup();
//             ChromeOptions options = new ChromeOptions();
//             options.addArguments("--start-maximized");
//             WebDriver driver = new ChromeDriver(options);
//             WebDriverWait wait = new WebDriverWait(driver, 10);

//             // Load search queries from Excel file
//             FileInputStream file = new FileInputStream(new File("C:\\Users\\CTV_QCPC\\Desktop\\demo maven\\DataScrapping\\DataScrappingValues.xlsx"));
//             XSSFWorkbook workbook = new XSSFWorkbook(file);
//             XSSFSheet querySheet = workbook.getSheetAt(0);

//             // Create a new Excel Workbook for storing results
//             XSSFWorkbook workbooks = new XSSFWorkbook();
            
//             for (Row row : querySheet) {
//                 Cell cell = row.getCell(0);
//                 if (cell == null) continue;
//                 String location = cell.getStringCellValue().trim();
//                 if (location.isEmpty()) continue;
                
//                 XSSFSheet sheet = workbooks.createSheet(location.replace(" ", "_"));
//                 List<String[]> apartmentData = new ArrayList<>();
//                 apartmentData.add(new String[]{"Apartment Name", "Address", "Contact"});

//                 // Open Google Maps
//                 driver.get("https://www.google.com/maps");
//                 Thread.sleep(5000);

//                 // Search for the current location keyword
//                 WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@id='searchboxinput']")));
//                 searchBox.clear();
//                 searchBox.sendKeys(location);
//                 searchBox.sendKeys(Keys.RETURN);
//                 Thread.sleep(5000);

//                 Set<String> processedApartments = new HashSet<>();
//                 JavascriptExecutor js = (JavascriptExecutor) driver;
//                 int apartmentCount = 0;

//                 // Scroll and load apartments up to 15 per search query
//                 while (apartmentCount < 15) {
//                     String apartmentXpath = "//div[@class='Nv2PK THOPZb CpccDe ']/a[1]";
//                     List<WebElement> apartments = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(apartmentXpath)));
                    
//                     for (WebElement apt : apartments) {
//                         if (apartmentCount >= 15) break;
//                         try {
//                             String apartmentName = apt.getAttribute("aria-label");
//                             if (!processedApartments.add(apartmentName)) continue;

//                             js.executeScript("arguments[0].scrollIntoView({block: 'center'});", apt);
//                             Thread.sleep(2000);
//                             apt.click();
//                             Thread.sleep(5000);

//                             String address = "N/A";
//                             try {
//                                 WebElement addressElement = wait.until(ExpectedConditions.presenceOfElementLocated(
//                                         By.xpath("//div[@class='Io6YTe fontBodyMedium kR99db fdkmkc '][1]")));
//                                 address = addressElement.getText().trim();
//                                 if (address.isEmpty()) address = "N/A";
//                             } catch (NoSuchElementException | TimeoutException e) {
//                                 System.out.println("[INFO] Address not found for: " + apartmentName);
//                             }

//                             String contact = "N/A";
//                             try {
//                                 List<WebElement> contactElements = driver.findElements(By.xpath(
//                                         "//*[@id=\"QA0Szd\"]/div/div/div[1]/div[3]/div/div[1]/div/div/div[2]/div[7]/div/button/div/div[2]/div[1]"));
//                                 for (WebElement contactElement : contactElements) {
//                                     String extractedContact = contactElement.getText().trim();
//                                     if (isValidPhoneNumber(extractedContact)) {
//                                         contact = extractedContact;
//                                         break;
//                                     }
//                                 }
//                             } catch (Exception e) {
//                                 System.out.println("Unable to extract contact for: " + apartmentName);
//                             }

//                             apartmentData.add(new String[]{apartmentName, address, contact});
//                             apartmentCount++;
//                         } catch (Exception e) {
//                             e.printStackTrace();
//                         }
//                     }
                    
//                     // Scroll to load more apartments if less than 15 are found
//                     if (apartmentCount < 15) {
//                         js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
//                         Thread.sleep(2000);
//                     }
//                 }
                
//                 // Write apartment data to Excel sheet after data collection
//                 for (int i = 0; i < apartmentData.size(); i++) {
//                     Row newRow = sheet.createRow(i);
//                     newRow.createCell(0).setCellValue(apartmentData.get(i)[0]);
//                     newRow.createCell(1).setCellValue(apartmentData.get(i)[1]);
//                     newRow.createCell(2).setCellValue(apartmentData.get(i)[2]);
//                 }
//             }
//             file.close();

//             // Save the Excel file to disk
//             String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//             String filepath = "C:\\Users\\CTV_QCPC\\Desktop\\demo maven\\DataScrapping\\demo\\ApartmentDetails_" + timestamp + ".xlsx";
//             FileOutputStream fileOut = new FileOutputStream(filepath);
//             workbooks.write(fileOut);
//             fileOut.close();

//             System.out.println("All searches completed. Data saved successfully.");
//             driver.quit();

//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }

//     public static boolean isValidPhoneNumber(String text) {
//         if (text.matches(".*[A-Za-z]+.*")) return false;
//         String phoneRegex = "^(\\+\\d{1,3}\\s?)?\\d{3,5}(\\s?[-\\s]?\\d{3,}){1,3}$";
//         return Pattern.matches(phoneRegex, text);
//     }
// }


////////////////////////////////////////////////From multiple column search////////////////////////////////////////////////////////////////////////////
/// 



// package com.datascrap;

// import org.apache.poi.ss.usermodel.*;
// import org.apache.poi.xssf.usermodel.XSSFSheet;
// import org.apache.poi.xssf.usermodel.XSSFWorkbook;
// import org.openqa.selenium.*;
// import org.openqa.selenium.NoSuchElementException;
// import org.openqa.selenium.chrome.ChromeDriver;
// import org.openqa.selenium.chrome.ChromeOptions;
// import org.openqa.selenium.support.ui.ExpectedConditions;
// import org.openqa.selenium.support.ui.WebDriverWait;
// import io.github.bonigarcia.wdm.WebDriverManager;

// import java.io.File;
// import java.io.FileInputStream;
// import java.io.FileOutputStream;
// import java.text.SimpleDateFormat;
// import java.util.*;
// import java.util.regex.Pattern;

// public class OptimizedApartmentScraper {

//     public static void main(String[] args) throws InterruptedException {
//         try {
//             // Setup ChromeDriver
//             WebDriverManager.chromedriver().setup();
//             ChromeOptions options = new ChromeOptions();
//             options.addArguments("--start-maximized");
//             WebDriver driver = new ChromeDriver(options);
//             WebDriverWait wait = new WebDriverWait(driver, 10);

//             // Load search queries from Excel file
//             FileInputStream file = new FileInputStream(new File("C:\\Users\\CTV_QCPC\\Desktop\\demo maven\\DataScrapping\\DataScrappingValues.xlsx"));
//             XSSFWorkbook workbook = new XSSFWorkbook(file);
//             XSSFSheet querySheet = workbook.getSheetAt(0);

//             // Create a new Excel Workbook for storing results
//             XSSFWorkbook workbooks = new XSSFWorkbook();
            
//             int totalColumns = querySheet.getRow(0).getPhysicalNumberOfCells();
//             for (int col = 0; col < totalColumns; col++) {
//                 int nullCount = 0;
//                 for (Row row : querySheet) {
//                     Cell cell = row.getCell(col);
//                     if (cell == null || cell.getStringCellValue().trim().isEmpty()) {
//                         nullCount++;
//                         if (nullCount >= 2) break;
//                         continue;
//                     }
//                     nullCount = 0;
//                     String location = cell.getStringCellValue().trim();
                    
//                     XSSFSheet sheet = workbooks.createSheet(location.replace(" ", "_"));
//                     List<String[]> apartmentData = new ArrayList<>();
//                     apartmentData.add(new String[]{"Apartment Name", "Address", "Contact"});

//                     // Open Google Maps
//                     driver.get("https://www.google.com/maps");
//                     Thread.sleep(5000);

//                     // Search for the current location keyword
//                     WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@id='searchboxinput']")));
//                     searchBox.clear();
//                     searchBox.sendKeys(location);
//                     searchBox.sendKeys(Keys.RETURN);
//                     Thread.sleep(5000);

//                     Set<String> processedApartments = new HashSet<>();
//                     JavascriptExecutor js = (JavascriptExecutor) driver;
//                     int apartmentCount = 0;

//                     // Scroll and load apartments up to 15 per search query
//                     while (apartmentCount < 15) {
//                         String apartmentXpath = "//div[@class='Nv2PK THOPZb CpccDe ']/a[1]";
//                         List<WebElement> apartments = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(apartmentXpath)));
                        
//                         for (WebElement apt : apartments) {
//                             if (apartmentCount >= 15) break;
//                             try {
//                                 String apartmentName = apt.getAttribute("aria-label");
//                                 if (!processedApartments.add(apartmentName)) continue;

//                                 js.executeScript("arguments[0].scrollIntoView({block: 'center'});", apt);
//                                 Thread.sleep(2000);
//                                 apt.click();
//                                 Thread.sleep(5000);

//                                 String address = "N/A";
//                                 try {
//                                     WebElement addressElement = wait.until(ExpectedConditions.presenceOfElementLocated(
//                                             By.xpath("//div[@class='Io6YTe fontBodyMedium kR99db fdkmkc '][1]")));
//                                     address = addressElement.getText().trim();
//                                     if (address.isEmpty()) address = "N/A";
//                                 } catch (NoSuchElementException | TimeoutException e) {
//                                     System.out.println("[INFO] Address not found for: " + apartmentName);
//                                 }

//                                 String contact = "N/A";
//                                 try {
//                                     List<WebElement> contactElements = driver.findElements(By.xpath(
//                                             "//*[@id=\"QA0Szd\"]/div/div/div[1]/div[3]/div/div[1]/div/div/div[2]/div[7]/div/button/div/div[2]/div[1]"));
//                                     for (WebElement contactElement : contactElements) {
//                                         String extractedContact = contactElement.getText().trim();
//                                         if (isValidPhoneNumber(extractedContact)) {
//                                             contact = extractedContact;
//                                             break;
//                                         }
//                                     }
//                                 } catch (Exception e) {
//                                     System.out.println("Unable to extract contact for: " + apartmentName);
//                                 }

//                                 apartmentData.add(new String[]{apartmentName, address, contact});
//                                 apartmentCount++;
//                             } catch (Exception e) {
//                                 e.printStackTrace();
//                             }
//                         }
                        
//                         // Scroll to load more apartments if less than 15 are found
//                         if (apartmentCount < 15) {
//                             js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
//                             Thread.sleep(2000);
//                         }
//                     }
                    
//                     // Write apartment data to Excel sheet after data collection
//                     for (int i = 0; i < apartmentData.size(); i++) {
//                         Row newRow = sheet.createRow(i);
//                         newRow.createCell(0).setCellValue(apartmentData.get(i)[0]);
//                         newRow.createCell(1).setCellValue(apartmentData.get(i)[1]);
//                         newRow.createCell(2).setCellValue(apartmentData.get(i)[2]);
//                     }
//                 }
//             }
//             file.close();

//             // Save the Excel file to disk
//             String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//             String filepath = "C:\\Users\\CTV_QCPC\\Desktop\\demo maven\\DataScrapping\\demo\\ApartmentDetails_" + timestamp + ".xlsx";
//             FileOutputStream fileOut = new FileOutputStream(filepath);
//             workbooks.write(fileOut);
//             fileOut.close();

//             System.out.println("All searches completed. Data saved successfully.");
//             driver.quit();

//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }

//     public static boolean isValidPhoneNumber(String text) {
//         if (text.matches(".*[A-Za-z]+.*")) return false;
//         String phoneRegex = "^(\\+\\d{1,3}\\s?)?\\d{3,5}(\\s?[-\\s]?\\d{3,}){1,3}$";
//         return Pattern.matches(phoneRegex, text);
//     }
// }


    ////////////////////////////////////////////////Multiple sheet cration////////////////////////////////////////////////////////////////////////////////////////////
    // package com.datascrap;

    // import org.apache.poi.ss.usermodel.*;
    // import org.apache.poi.xssf.usermodel.XSSFSheet;
    // import org.apache.poi.xssf.usermodel.XSSFWorkbook;
    // import org.openqa.selenium.*;
    // import org.openqa.selenium.NoSuchElementException;
    // import org.openqa.selenium.chrome.ChromeDriver;
    // import org.openqa.selenium.chrome.ChromeOptions;
    // import org.openqa.selenium.support.ui.ExpectedConditions;
    // import org.openqa.selenium.support.ui.WebDriverWait;
    // import io.github.bonigarcia.wdm.WebDriverManager;
    
    // import java.io.File;
    // import java.io.FileInputStream;
    // import java.io.FileOutputStream;
    // import java.text.SimpleDateFormat;
    // import java.util.*;
    // import java.util.regex.Pattern;
    
    // public class OptimizedApartmentScraper {
    
    //     public static void main(String[] args) throws InterruptedException {
    //         try {
    //             // Setup ChromeDriver
    //             WebDriverManager.chromedriver().setup();
    //             ChromeOptions options = new ChromeOptions();
    //             options.addArguments("--start-maximized");
    //             WebDriver driver = new ChromeDriver(options);
    //             WebDriverWait wait = new WebDriverWait(driver, 10);
    
    //             // Load search queries from Excel file
    //             FileInputStream file = new FileInputStream(new File("C:\\Users\\CTV_QCPC\\Desktop\\demo maven\\DataScrapping\\DataScrappingValues.xlsx"));
    //             XSSFWorkbook workbook = new XSSFWorkbook(file);
    //             XSSFSheet querySheet = workbook.getSheetAt(0);
    
    //             // Create a new Excel Workbook for storing results
    //             XSSFWorkbook workbooks = new XSSFWorkbook();
                
    //             int totalColumns = querySheet.getRow(0).getPhysicalNumberOfCells();
    //             for (int col = 0; col < totalColumns; col++) {
    //                 String sheetName = querySheet.getRow(0).getCell(col).getStringCellValue().trim(); // First row as sheet name
    //                 if (sheetName.isEmpty()) continue;
    //                 XSSFSheet sheet = workbooks.createSheet(sheetName.replace(" ", "_"));
    //                 List<String[]> apartmentData = new ArrayList<>();
    //                 apartmentData.add(new String[]{"Apartment Name", "Address", "Contact"});
    
    //                 int nullCount = 0;
    //                 for (int rowIdx = 1; rowIdx < querySheet.getPhysicalNumberOfRows(); rowIdx++) { // Start from second row
    //                     Row row = querySheet.getRow(rowIdx);
    //                     if (row == null) continue;
    //                     Cell cell = row.getCell(col);
    //                     if (cell == null || cell.getStringCellValue().trim().isEmpty()) {
    //                         nullCount++;
    //                         if (nullCount >= 2) break;
    //                         continue;
    //                     }
    //                     nullCount = 0;
    //                     String location = cell.getStringCellValue().trim();
                        
    //                     // Open Google Maps
    //                     driver.get("https://www.google.com/maps");
    //                     Thread.sleep(3000);
    
    //                     // Search for the current location keyword
    //                     WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@id='searchboxinput']")));
    //                     searchBox.clear();
    //                     searchBox.sendKeys(location);
    //                     searchBox.sendKeys(Keys.RETURN);
    //                     Thread.sleep(3000);
    
    //                     Set<String> processedApartments = new HashSet<>();
    //                     JavascriptExecutor js = (JavascriptExecutor) driver;
    //                     int apartmentCount = 0;
    
    //                     // Scroll and load apartments up to 15 per search query
    //                     while (apartmentCount < 15) {
    //                         String apartmentXpath = "//div[@class='Nv2PK THOPZb CpccDe ']/a[1]";
    //                         List<WebElement> apartments = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(apartmentXpath)));
                            
    //                         for (WebElement apt : apartments) {
    //                             if (apartmentCount >= 15) break;
    //                             try {
    //                                 String apartmentName = apt.getAttribute("aria-label");
    //                                 if (!processedApartments.add(apartmentName)) continue;
    
    //                                 js.executeScript("arguments[0].scrollIntoView({block: 'center'});", apt);
    //                                 Thread.sleep(2000);
    //                                 apt.click();
    //                                 Thread.sleep(3000);
    
    //                                 String address = "N/A";
    //                                 try {
    //                                     WebElement addressElement = wait.until(ExpectedConditions.presenceOfElementLocated(
    //                                             By.xpath("//div[@class='Io6YTe fontBodyMedium kR99db fdkmkc '][1]")));
    //                                     address = addressElement.getText().trim();
    //                                     if (address.isEmpty()) address = "N/A";
    //                                 } catch (NoSuchElementException | TimeoutException e) {
    //                                     System.out.println("[INFO] Address not found for: " + apartmentName);
    //                                 }
    
    //                                 String contact = "N/A";
    //                                 try {
    //                                     List<WebElement> contactElements = driver.findElements(By.xpath(
    //                                             "//*[@id=\"QA0Szd\"]/div/div/div[1]/div[3]/div/div[1]/div/div/div[2]/div[7]/div/button/div/div[2]/div[1]"));
    //                                     for (WebElement contactElement : contactElements) {
    //                                         String extractedContact = contactElement.getText().trim();
    //                                         if (isValidPhoneNumber(extractedContact)) {
    //                                             contact = extractedContact;
    //                                             break;
    //                                         }
    //                                     }
    //                                 } catch (Exception e) {
    //                                     System.out.println("Unable to extract contact for: " + apartmentName);
    //                                 }
    
    //                                 apartmentData.add(new String[]{apartmentName, address, contact});
    //                                 apartmentCount++;
    //                             } catch (Exception e) {
    //                                 e.printStackTrace();
    //                             }
    //                         }
                            
    //                         // Scroll to load more apartments if less than 15 are found
    //                         if (apartmentCount < 15) {
    //                             js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
    //                             Thread.sleep(2000);
    //                         }
    //                     }
                        
    //                     // Write apartment data to Excel sheet after data collection
    //                     for (int i = 0; i < apartmentData.size(); i++) {
    //                         Row newRow = sheet.createRow(i);
    //                         newRow.createCell(0).setCellValue(apartmentData.get(i)[0]);
    //                         newRow.createCell(1).setCellValue(apartmentData.get(i)[1]);
    //                         newRow.createCell(2).setCellValue(apartmentData.get(i)[2]);
    //                     }
    //                 }
    //             }
    //             file.close();
    
    //             // Save the Excel file to disk
    //             String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    //             String filepath = "C:\\Users\\CTV_QCPC\\Desktop\\demo maven\\DataScrapping\\demo\\ApartmentDetails_" + timestamp + ".xlsx";
    //             FileOutputStream fileOut = new FileOutputStream(filepath);
    //             workbooks.write(fileOut);
    //             fileOut.close();
    
    //             System.out.println("All searches completed. Data saved successfully.");
    //             driver.quit();
    
    //         } catch (Exception e) {
    //             e.printStackTrace();
    //         }
    //     }
    
    //     public static boolean isValidPhoneNumber(String text) {
    //         if (text.matches(".*[A-Za-z]+.*")) return false;
    //         String phoneRegex = "^(\\+\\d{1,3}\\s?)?\\d{3,5}(\\s?[-\\s]?\\d{3,}){1,3}$";
    //         return Pattern.matches(phoneRegex, text);
    //     }
    // }



    ///////////////////////////////////////////Complete Script////////////////////////////////////////////////////////////////////////
//     package com.datascrap;

// import org.apache.poi.ss.usermodel.*;
// import org.apache.poi.xssf.usermodel.XSSFSheet;
// import org.apache.poi.xssf.usermodel.XSSFWorkbook;
// import org.openqa.selenium.*;
// import org.openqa.selenium.NoSuchElementException;
// import org.openqa.selenium.chrome.ChromeDriver;
// import org.openqa.selenium.chrome.ChromeOptions;
// import org.openqa.selenium.support.ui.ExpectedConditions;
// import org.openqa.selenium.support.ui.WebDriverWait;
// import io.github.bonigarcia.wdm.WebDriverManager;

// import java.io.File;
// import java.io.FileInputStream;
// import java.io.FileOutputStream;
// import java.text.SimpleDateFormat;
// import java.util.*;
// import java.util.regex.Pattern;

// public class OptimizedApartmentScraper {

//     public static void main(String[] args) throws InterruptedException {
//         try {
//             // Setup ChromeDriver
//             WebDriverManager.chromedriver().setup();
//             ChromeOptions options = new ChromeOptions();
//             options.addArguments("--start-maximized");
//             WebDriver driver = new ChromeDriver(options);
//             WebDriverWait wait = new WebDriverWait(driver, 10);

//             // Load search queries from Excel file
//             FileInputStream file = new FileInputStream(new File("C:\\Users\\CTV_QCPC\\Desktop\\demo maven\\DataScrapping\\DataScrappingValues.xlsx"));
//             XSSFWorkbook workbook = new XSSFWorkbook(file);
//             XSSFSheet querySheet = workbook.getSheetAt(0);

//             // Create a new Excel Workbook for storing results
//             XSSFWorkbook workbooks = new XSSFWorkbook();
            
//             int totalColumns = querySheet.getRow(0).getPhysicalNumberOfCells();
//             for (int col = 0; col < totalColumns; col++) {
//                 String sheetName = querySheet.getRow(0).getCell(col).getStringCellValue().trim(); // First row as sheet name
//                 if (sheetName.isEmpty()) continue;
//                 XSSFSheet sheet = workbooks.createSheet(sheetName.replace(" ", "_"));
//                 List<String[]> apartmentData = new ArrayList<>();
//                 apartmentData.add(new String[]{"Apartment Name", "Address", "Contact"});

//                 int nullCount = 0;
//                 for (int rowIdx = 1; rowIdx < querySheet.getPhysicalNumberOfRows(); rowIdx++) { // Start from second row
//                     Row row = querySheet.getRow(rowIdx);
//                     if (row == null) continue;
//                     Cell cell = row.getCell(col);
//                     if (cell == null || cell.getStringCellValue().trim().isEmpty()) {
//                         nullCount++;
//                         if (nullCount >= 2) break;
//                         continue;
//                     }
//                     nullCount = 0;
//                     String location = cell.getStringCellValue().trim();
                    
//                     // Open Google Maps
//                     driver.get("https://www.google.com/maps");
//                     Thread.sleep(5000);

//                     // Search for the current location keyword
//                     WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@id='searchboxinput']")));
//                     searchBox.clear();
//                     searchBox.sendKeys(location);
//                     searchBox.sendKeys(Keys.RETURN);
//                     Thread.sleep(5000);

//                     Set<String> processedApartments = new HashSet<>();
//                     JavascriptExecutor js = (JavascriptExecutor) driver;
                    
//                     // Loop until no more new apartments are found
//                     while (true) {
//                         String apartmentXpath = "//div[@class='Nv2PK THOPZb CpccDe ']/a[1]";
//                         List<WebElement> apartments = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(apartmentXpath)));
//                         int initialSize = processedApartments.size();
                        
//                         for (WebElement apt : apartments) {
//                             try {
//                                 String apartmentName = apt.getAttribute("aria-label");
//                                 if (!processedApartments.add(apartmentName)) continue;

//                                 js.executeScript("arguments[0].scrollIntoView({block: 'center'});", apt);
//                                 Thread.sleep(2000);
//                                 apt.click();
//                                 Thread.sleep(5000);

//                                 String address = "N/A";
//                                 try {
//                                     WebElement addressElement = wait.until(ExpectedConditions.presenceOfElementLocated(
//                                             By.xpath("//div[@class='Io6YTe fontBodyMedium kR99db fdkmkc '][1]")));
//                                     address = addressElement.getText().trim();
//                                     if (address.isEmpty()) address = "N/A";
//                                 } catch (NoSuchElementException | TimeoutException e) {
//                                     System.out.println("[INFO] Address not found for: " + apartmentName);
//                                 }

//                                 String contact = "N/A";
//                                 try {
//                                     List<WebElement> contactElements = driver.findElements(By.xpath(
//                                             "//*[@id=\"QA0Szd\"]/div/div/div[1]/div[3]/div/div[1]/div/div/div[2]/div[7]/div/button/div/div[2]/div[1]"));
//                                     for (WebElement contactElement : contactElements) {
//                                         String extractedContact = contactElement.getText().trim();
//                                         if (isValidPhoneNumber(extractedContact)) {
//                                             contact = extractedContact;
//                                             break;
//                                         }
//                                     }
//                                 } catch (Exception e) {
//                                     System.out.println("Unable to extract contact for: " + apartmentName);
//                                 }

//                                 apartmentData.add(new String[]{apartmentName, address, contact});
//                             } catch (Exception e) {
//                                 e.printStackTrace();
//                             }
//                         }
                        
//                         // Scroll to load more apartments
//                         js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
//                         Thread.sleep(3000);
                        
//                         // If no new apartments were added, break loop
//                         if (processedApartments.size() == initialSize) break;
//                     }
                    
//                     // Write apartment data to Excel sheet after data collection
//                     for (int i = 0; i < apartmentData.size(); i++) {
//                         Row newRow = sheet.createRow(i);
//                         newRow.createCell(0).setCellValue(apartmentData.get(i)[0]);
//                         newRow.createCell(1).setCellValue(apartmentData.get(i)[1]);
//                         newRow.createCell(2).setCellValue(apartmentData.get(i)[2]);
//                     }
//                 }
//             }
//             file.close();

//             // Save the Excel file to disk
//             String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//             String filepath = "C:\\Users\\CTV_QCPC\\Desktop\\demo maven\\DataScrapping\\demo\\Apartment_Details_" + timestamp + ".xlsx";
//             FileOutputStream fileOut = new FileOutputStream(filepath);
//             workbooks.write(fileOut);
//             fileOut.close();

//             System.out.println("All searches completed. Data saved successfully.");
//             driver.quit();

//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }

//     public static boolean isValidPhoneNumber(String text) {
//         if (text.matches(".*[A-Za-z]+.*")) return false;
//         String phoneRegex = "^(\\+\\d{1,3}\\s?)?\\d{3,5}(\\s?[-\\s]?\\d{3,}){1,3}$";
//         return Pattern.matches(phoneRegex, text);
//     }
// }

//////////////////////////////////////Multiple Workbook With Infinte Scrolling//////////////////////////////////
package com.datascrap;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class OptimizedApartmentScraper {

    public static void main(String[] args) throws InterruptedException {
        try {
            // Setup ChromeDriver
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            WebDriver driver = new ChromeDriver(options);
            WebDriverWait wait = new WebDriverWait(driver, 10);

            // Load search queries from Excel file
            FileInputStream file = new FileInputStream(new File("C:\\Users\\CTV_QCPC\\Desktop\\demo maven\\DataScrapping\\DataScrappingValues.xlsx"));
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet querySheet = workbook.getSheetAt(0);

            int totalColumns = querySheet.getRow(0).getPhysicalNumberOfCells();
            for (int col = 0; col < totalColumns; col++) {
                String workbookName = querySheet.getRow(0).getCell(col).getStringCellValue().trim(); // First row as workbook name
                if (workbookName.isEmpty()) continue;
                XSSFWorkbook workbooks = new XSSFWorkbook();

                int nullCount = 0;
                for (int rowIdx = 1; rowIdx < querySheet.getPhysicalNumberOfRows(); rowIdx++) { // Start from second row
                    Row row = querySheet.getRow(rowIdx);
                    if (row == null) continue;
                    Cell cell = row.getCell(col);
                    if (cell == null || cell.getStringCellValue().trim().isEmpty()) {
                        nullCount++;
                        if (nullCount >= 2) break;
                        continue;
                    }
                    nullCount = 0;
                    String sheetName = cell.getStringCellValue().trim();
                    XSSFSheet sheet = workbooks.createSheet(sheetName.replace(" ", "_"));
                    List<String[]> apartmentData = new ArrayList<>();
                    apartmentData.add(new String[]{"Apartment Name", "Address", "Contact"});
                    
                    // Open Google Maps
                    driver.get("https://www.google.com/maps");
                    Thread.sleep(5000);

                    // Search for the current location keyword
                    WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@id='searchboxinput']")));
                    searchBox.clear();
                    searchBox.sendKeys(sheetName);
                    searchBox.sendKeys(Keys.RETURN);
                    Thread.sleep(2000);

                    Set<String> processedApartments = new HashSet<>();
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    
                    // Loop until no more new apartments are found
                    while (true) {
                        String apartmentXpath = "//div[@class='Nv2PK THOPZb CpccDe ']/a[1]";
                        List<WebElement> apartments = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(apartmentXpath)));
                        int initialSize = processedApartments.size();
                        
                        for (WebElement apt : apartments) {
                            try {
                                String apartmentName = apt.getAttribute("aria-label");
                                if (!processedApartments.add(apartmentName)) continue;

                                js.executeScript("arguments[0].scrollIntoView({block: 'center'});", apt);
                                Thread.sleep(2000);
                                apt.click();
                                Thread.sleep(2000);

                                String address = "N/A";
                                try {
                                    WebElement addressElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                                            By.xpath("//div[@class='Io6YTe fontBodyMedium kR99db fdkmkc '][1]")));
                                    address = addressElement.getText().trim();
                                    if (address.isEmpty()) address = "N/A";
                                } catch (NoSuchElementException | TimeoutException e) {
                                    System.out.println("[INFO] Address not found for: " + apartmentName);
                                }

                                String contact = "N/A";
                                try {
                                    List<WebElement> contactElements = driver.findElements(By.xpath(
                                            "//*[@id=\"QA0Szd\"]/div/div/div[1]/div[3]/div/div[1]/div/div/div[2]/div[7]/div/button/div/div[2]/div[1]"));
                                    for (WebElement contactElement : contactElements) {
                                        String extractedContact = contactElement.getText().trim();
                                        if (isValidPhoneNumber(extractedContact)) {
                                            contact = extractedContact;
                                            break;
                                        }
                                    }
                                } catch (Exception e) {
                                    System.out.println("Unable to extract contact for: " + apartmentName);
                                }

                                apartmentData.add(new String[]{apartmentName, address, contact});
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        
                        // Scroll to load more apartments
                        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
                        Thread.sleep(3000);
                        
                        // If no new apartments were added, break loop
                        if (processedApartments.size() == initialSize) break;
                    }
                    
                    // Write apartment data to Excel sheet after data collection
                    for (int i = 0; i < apartmentData.size(); i++) {
                        Row newRow = sheet.createRow(i);
                        newRow.createCell(0).setCellValue(apartmentData.get(i)[0]);
                        newRow.createCell(1).setCellValue(apartmentData.get(i)[1]);
                        newRow.createCell(2).setCellValue(apartmentData.get(i)[2]);
                    }
                }
                
                // Save each workbook separately
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String filepath = "C:\\Users\\CTV_QCPC\\Desktop\\demo maven\\DataScrapping\\demo\\" + workbookName + "_" + timestamp + ".xlsx";
                FileOutputStream fileOut = new FileOutputStream(filepath);
                workbooks.write(fileOut);
                fileOut.close();
            }
            file.close();

            System.out.println("All searches completed. Data saved successfully.");
            driver.quit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isValidPhoneNumber(String text) {
        if (text.matches(".*[A-Za-z]+.*")) return false;
        String phoneRegex = "^(\\+\\d{1,3}\\s?)?\\d{3,5}(\\s?[-\\s]?\\d{3,}){1,3}$";
        return Pattern.matches(phoneRegex, text);
    }
}


    


