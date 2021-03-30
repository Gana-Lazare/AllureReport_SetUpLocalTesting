package Base;


import com.browserstack.local.Local;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.LogStatus;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Step;
import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;
import org.testng.annotations.Optional;
import reporting.ExtentManager;
import reporting.ExtentTestManager;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Base  {
public static WebDriver driver;
    //ExtentReport
    public static ExtentReports extent;
    @BeforeSuite
    public void extentSetup(ITestContext context) {
        ExtentManager.setOutputDirectory(context);
        extent = ExtentManager.getInstance();
    }
    @BeforeMethod
    public void startExtent(Method method) {
        String className = method.getDeclaringClass().getSimpleName();
        String methodName = method.getName().toLowerCase();
        ExtentTestManager.startTest(method.getName());
        ExtentTestManager.getTest().assignCategory(className);
    }
    @BeforeClass
    public void startExtentbdd(Method method) {
        String className = method.getDeclaringClass().getSimpleName();
        String methodName = method.getName().toLowerCase();
        ExtentTestManager.startTest(method.getName());
        ExtentTestManager.getTest().assignCategory(className);
    }


    @Parameters({"url","useCloud","cloud" ,"browserName","browserVersion","os" , "os_version" ,
            "envUsername" , "envAccessKey"})
    @BeforeMethod
    public void setup(@Optional("https://www.google.com/") String url,@Optional ("false")boolean useCloud,String cloud ,String browserName,
            String browserVersion,String os, String os_version,
            String envUsername, String envAccessKey){
        if (useCloud==false){
        try {
            driver = getLocalDriver(browserName);
        }
        catch (WebDriverException e){
            e.getStackTrace();
        }}
        else if (useCloud==true){
            try {
                driver = getCloudDriver(cloud, browserName, browserVersion, os, os_version, envUsername, envAccessKey);
            }
            catch (Exception e){}
        }
       driver.manage().deleteAllCookies();
        driver.manage().window().maximize();
        driver.get(url);


    }
public WebDriver getLocalDriver(String browser){
        switch (browser){
            case "chrome":{
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver();
            break;}
            case "firfox":{
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                break;}
        }
        return driver;
}
public WebDriver getCloudDriver(String cloud , String browserName,String browserVersion,String os , String os_version ,
                                String envUsername , String envAccessKey) throws Exception {
    DesiredCapabilities cap = new DesiredCapabilities();
        cap.setCapability("browserName",browserName);
        cap.setCapability("browserVersion",browserVersion);
        cap.setCapability("os",os);
        cap.setCapability("os_version",os_version);
    cap.setCapability("browserstack.local", "true");
    cap.setCapability("acceptSslCerts", "true");
    Local bsLocal = new Local();
    HashMap<String, String> bsLocalArgs = new HashMap<String, String>();
    bsLocalArgs.put("key", envAccessKey);
    bsLocal.start(bsLocalArgs);
    bsLocalArgs.put("forcelocal", "true");
        if (cloud.equalsIgnoreCase("browserStack")){
            driver= new RemoteWebDriver(new URL("http://" + envUsername + ":" + envAccessKey +
                    "@hub-cloud.browserstack.com/wd/hub"),cap);
        }
        return  driver;
}

    public void captureScreenshot(WebDriver driver,String screenName) throws IOException {
        DateFormat df = new SimpleDateFormat("(yyMMddHHmmssZ)");
        Date date = new Date();
        df.format(date);
        TakesScreenshot screenshot = (TakesScreenshot)driver;
        File src = screenshot.getScreenshotAs(OutputType.FILE);
        File dest = new File(System.getProperty("user.dir")+"\\ScreenShot\\"+screenName+df.format(date)+".png");
        FileHandler.copy(src,dest);
    }
@Step("test allure report Links Broken")
    public void allPageLinksStatus(WebDriver driver) throws IOException {
        List<WebElement> elements = driver.findElements(By.tagName("a"));
        elements.addAll(driver.findElements(By.tagName("img")));
        Iterator<WebElement> it = elements.iterator();
        List<WebElement> activeLinks = new ArrayList<>();

        while(it.hasNext()){
            WebElement element=it.next();
            if (element.getAttribute("href")!=null) {
                activeLinks.add(element);
            }
            else
            System.out.println(element.getText());
        }
        Iterator<WebElement> it2 = activeLinks.iterator();
        while (it2.hasNext()){
            WebElement element=it2.next();
            HttpURLConnection httpURLConnection = (HttpURLConnection)new URL(element.getAttribute("href")).openConnection();
            httpURLConnection.connect();
            String status = httpURLConnection.getResponseMessage();
            httpURLConnection.disconnect();
            System.out.println(element.getText() + "  status code is :" + status);
        }


    }
/*@AfterMethod()
public void closeDriver(){
        driver.close();
}*/
    @AfterMethod
    public void afterEachTest(ITestResult result) throws IOException {
        try {
            ExtentTestManager.getTest().getTest().setStartedTime(getTime(result.getStartMillis()));
            ExtentTestManager.getTest().getTest().setEndedTime(getTime(result.getEndMillis()));

            for (String group : result.getMethod().getGroups()) {
                ExtentTestManager.getTest().assignCategory(group);
            }

            if (result.getStatus() == 1) {
                ExtentTestManager.getTest().log(LogStatus.PASS, "Test Passed");
            } else if (result.getStatus() == 2) {
                ExtentTestManager.getTest().log(LogStatus.FAIL, getStackTrace(result.getThrowable()));
            } else if (result.getStatus() == 3) {
                ExtentTestManager.getTest().log(LogStatus.SKIP, "Test Skipped");
            }
            ExtentTestManager.endTest();
            extent.flush();
            if (result.getStatus() == ITestResult.FAILURE) {
                captureScreenshot(driver, result.getName());
            }

        } catch (NullPointerException e) {
            e.getStackTrace();
            e.getMessage();
        }
        driver.close();
    }
    protected String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }

    @AfterSuite
    public void generateReport() {
        extent.close();
    }


    private Date getTime(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.getTime();
    }
}
