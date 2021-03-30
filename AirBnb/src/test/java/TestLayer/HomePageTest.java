package TestLayer;

import Base.Base;
import PageLayer.HomePageCrm;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.openqa.selenium.By;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import reporting.AllureTestListner;

import java.io.IOException;

@Listeners({AllureTestListner.class})
public class HomePageTest extends Base {
    HomePageCrm homePageCrm = new HomePageCrm();
    @BeforeMethod
    public void init(){
        homePageCrm = PageFactory.initElements(driver,HomePageCrm.class);
    }

    @Test(priority = 1,timeOut = 10000,description = "test allure report")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test Allure report")
    @Story(("Story Name"))
    public void test() throws InterruptedException, IOException {
        Thread.sleep(3000);
      allPageLinksStatus(driver);
    }


    @Severity(SeverityLevel.BLOCKER)
    @Description("log in with valid credentials")
    @Story("login to login Page")
    @Test(priority = 2)
    public void login(){
        String text = driver.findElement(By.xpath("//*[@class='h3 mb-0 text-gray-800']")).getText();
        Assert.assertEquals(text,"not fun ");

    }

}
