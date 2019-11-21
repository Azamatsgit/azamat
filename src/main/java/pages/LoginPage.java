package pages;

import base.PageBase;
import com.relevantcodes.extentreports.ExtentTest;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.io.IOException;

public class LoginPage extends PageBase {

   @FindBy (xpath = "")


    public WebDriver driver;
    public ExtentTest logger;

    private PageTitles pageTitle = PageTitles.MORNINGSTAR;



    public LoginPage(WebDriver driver, ExtentTest logger) throws IOException {
        super(driver, logger);
        this.driver=driver;
        this.logger=logger;
        PageFactory.initElements(driver,this);
        verifyPage(pageTitle);

    }
}



