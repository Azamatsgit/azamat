package base;

import com.relevantcodes.extentreports.ExtentTest;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import pages.PageTitles;
import report.Reporting;
import util.Perform;
import util.Result;


import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;
import static util.Result.*;
public class PageBase extends Reporting{
    WebDriver driver;
    ExtentTest logger;

    public PageBase (WebDriver driver , ExtentTest logger){
        this.driver = driver;
        this.logger=logger;
        PageFactory.initElements(driver, this);
    }

    public void perform(String ElementName, List<WebElement> Element, Perform Action, String SetData) throws IOException {
        switch (Action) {
            case SelectFromRadioOptions:
                if (findElements (ElementName, Element)) {
                    int size = Element.size ( );
                    for (int i = 0; i < size; i++) {
                        try{
                            if (Element.get (i).getText ( ).trim ( ).equalsIgnoreCase (SetData)){
                                highlighter (Element.get (i));
                                //((JavascriptExecutor) driver).executeScript("arguments[0].click();", Element.get (i));
                                Element.get (i).click ();
                                log (logger,driver,ElementName, Element.get (i).getText (), CLICKED);
                                pageLoader ( );
                                return;
                            }
                        }catch(Exception e){ }
                        try{
                            if (Element.get (i).getAttribute ("value").trim ( ).replace (" ", "").equalsIgnoreCase (SetData.replace (" ", ""))) {
                                highlighter (Element.get (i));
                                //((JavascriptExecutor) driver).executeScript("arguments[0].click();", Element.get (i));
                                Element.get (i).click ( );
                                log (logger,driver,ElementName, Element.get (i).getAttribute ("value"), CLICKED);
                                pageLoader ( );
                                return;
                            }
                        }catch(Exception e){}
                        try{
                            if (Element.get (i).getAttribute ("caption").trim ( ).replace (" ", "").equalsIgnoreCase (SetData.replace (" ", ""))) {
                                highlighter (Element.get (i));
                                //((JavascriptExecutor) driver).executeScript("arguments[0].click();", Element.get (i));
                                Element.get (i).click ( );
                                log (logger,driver,ElementName, Element.get (i).getAttribute ("caption"), CLICKED);
                                pageLoader ( );
                                return;
                            }
                        }catch(Exception e){}
                    }
                    log (logger,driver,ElementName,SetData, Result.NOTCLICKED);
                }
                break;
            case SelectFromRadioOptionsRandomnly:
                if (findElements (ElementName, Element)) {
                    int size = Element.size ( );
                    Random rand = new Random();
                    int i=rand.nextInt(size);
                    try{
                        highlighter (Element.get (i));
                        //((JavascriptExecutor) driver).executeScript("arguments[0].click();", Element.get (i));
                        Element.get (i).click ();
                        log (logger,driver,ElementName, Element.get (i).getText (), CLICKED);
                        pageLoader ( );
                        return;
                    }catch(Exception e){ }
                    log (logger,driver,ElementName,SetData, Result.NOTCLICKED);
                }
                break;
            case SelectFromTableResult:
                if (findElements (ElementName, Element)) {
                    int size = Element.size ( );
                    for (int i = 0; i < size; i++) {
                        if (Element.get (i).getText ().equalsIgnoreCase (SetData)) {
                            highlighter (Element.get (i));
                            String text=Element.get (i).getText ();
                            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", Element.get (i));
                            //Element.get (i).click ( );
                            log (logger,driver,ElementName,text, CLICKED);
                            pageLoader();
                            return;
                        }
                    }
                    log (logger,driver,ElementName,SetData,Result.NOTCLICKED);
                }
                break;
            case SelectRandomFromTableResult:
                if (findElements (ElementName, Element)) {
                    int size = Element.size ( );
                    Random rand = new Random();
                    int i=rand.nextInt(size);
                    try{
                        highlighter (Element.get (i));
                        String text=Element.get (i).getText ();
//                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", Element.get (i));
                        Element.get (i).click ( );
                        log (logger,driver,ElementName, text, CLICKED);
                        pageLoader();
                        return;
                    }catch(Exception E){
                        log (logger,driver,ElementName,"NO DATA FOUND",Result.NOTCLICKED);
                    }
                }
                break;
        }
    }

    public void perform(String ElementName, WebElement Element, Perform Action, String SetData) throws IOException {
        switch (Action) {

            case Click:
                if (findElement (ElementName, Element)) {
                    //((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", Element);
                    //((JavascriptExecutor) driver).executeScript("arguments[0].click();", Element);
                    try{
                        Element.click ( );
                    } catch (TimeoutException toe){
                        log(logger,driver,"Page Load Issue",Result.FAIL);
                    } catch (ElementNotVisibleException e){
                        pageLoader();
                        Element.click();
                    } catch (Exception e){
                        pageLoader();
                        Element.click();
                    }

                    log (logger,driver,ElementName, CLICKED);
                    pageLoader();
                    break;
                }
                log (logger,driver,ElementName,Result.NOTCLICKED);
                break;
            case SendKeys:
                if (findElement (ElementName, Element)) {
                    //((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", Element);
                    //((JavascriptExecutor) driver).executeScript("arguments[0].value='"+SetData+"';", Element);
                    Element.sendKeys (SetData);
                    log (logger,driver,ElementName, SetData, ENTEREDTEXT);
                }
                break;
            case Clear:
                if (findElement (ElementName, Element)) {
                    //((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", Element);
                    Element.clear ( );
                    log (logger,driver,ElementName, CLEARED);
                }
                break;
            case ElementShouldNotBeFound:
                driver.manage ().timeouts ().implicitlyWait (5000, TimeUnit.MILLISECONDS);
                if (findElement (ElementName, Element)){
                    log (logger,driver,ElementName, FAIL);
                }

                else
                    log (logger,driver,ElementName, PASS);
                driver.manage ().timeouts ().implicitlyWait (implicitwait,TimeUnit.SECONDS);
                break;
            case ElementShouldBeFound:
                if (findElement (ElementName, Element))
                    log (logger,driver,ElementName, PASS);
                else{
                    log (logger,driver,ElementName, FAIL);
                }
                break;

            case SelectFromDropdownOptionsByText:
                if (findElement (ElementName, Element)) {
                    Select select = new Select (Element);
                    select.selectByVisibleText (SetData);
                    log (logger,driver,ElementName, SetData, Result.SET);
                    pageLoader();
                    break;
                }
                log (logger,driver,ElementName, SetData, Result.NOTSET);
                break;

            case SelectFromDropdownRandomnly:
                if (findElement (ElementName, Element)) {
                    Select select = new Select (Element);
                    Random rand = new Random();
                    int i=rand.nextInt(select.getOptions ().size ());
                    try{
                        select.getOptions ().get (i).click ();
                        log (logger,driver,ElementName, SetData, Result.SET);
                        pageLoader();

                    }catch (Exception e){}
                    break;
                }
                log (logger,driver,ElementName, SetData, Result.NOTSET);
                break;

            case SelectFromDropdownOptionsByValue:
                if (findElement (ElementName, Element)) {
                    Select select = new Select (Element);
                    select.selectByValue (SetData);
                    log (logger,driver,ElementName, SetData, Result.SET);
                    pageLoader();
                    break;
                }
                log (logger,driver,ElementName, SetData, Result.NOTSET);
                break;
            case VerifyNotEqual:
                if(findElement (ElementName,Element)){
                    if(SetData.equalsIgnoreCase ("")){
                        if(!Element.getText ().equalsIgnoreCase (SetData))
                            log (logger,driver,ElementName,Element.getText (),Result.PASS);
                        else{
                            log (logger,driver,ElementName,Element.getText (),Result.FAIL);
                        }
                    }else if(!Element.getText ().toLowerCase ().contains (SetData.toLowerCase ()))
                        log (logger,driver,ElementName,Element.getText (),Result.PASS);
                    else{
                        log (logger,driver,ElementName,Element.getText (),Result.FAIL);
                    }
                }
                break;
            case VerifyDropdownSelectedNotBlank:
                if(findElement (ElementName,Element)){
                    try{
                        Select select=new Select (Element);
                        if(!select.getFirstSelectedOption ().getText ().trim ().equalsIgnoreCase ("")){
                            log(logger,driver,ElementName,select.getFirstSelectedOption ().getText (),Result.PASS);
                        }else log(logger,driver,ElementName,select.getFirstSelectedOption ().getText (),Result.WARNING);
                    }catch (Exception e){
                        log (logger,driver,ElementName,Element.getText (),Result.WARNING);
                    }
                }
                break;
            case VerifyEqual:
                if(findElement (ElementName,Element)){
                    if(SetData.equalsIgnoreCase ("")){
                        if(Element.getAttribute ("value").equalsIgnoreCase (SetData))
                            log (logger,driver,ElementName,Element.getAttribute ("value"),Result.PASS);
                        else{
                            log (logger,driver,ElementName,Element.getAttribute ("value"),Result.FAIL);
                        }
                    }else if(Element.getAttribute ("value").toLowerCase ().contains (SetData.toLowerCase ()))
                        log (logger,driver,ElementName,Element.getAttribute ("value"),Result.PASS);
                    else{
                        log (logger,driver,ElementName,Element.getAttribute ("value"),Result.FAIL);
                    }
                }
                break;
            case PrintElementVisibleText:
                if(findElement (ElementName,Element)){
                    //((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", Element);
                    log (logger,driver,ElementName,Element.getText (),Result.PASS);
                }
                else{
                    log (logger,driver,ElementName,"",Result.FAIL);
                }

                break;
            case TabSelected:
                if(findElement (ElementName,Element)){
                    try{
                        if(Element.findElement (By.xpath ("..")).getAttribute ("class").equalsIgnoreCase ("active")){
                            log (logger,driver,ElementName,Result.PASS);
                            return;
                        }
                    }catch (Exception E){ }
                    try{
                        if(Element.getAttribute ("class").toLowerCase().contains ("active") && !Element.getAttribute ("class").toLowerCase().contains ("inactive")){
                            log (logger,driver,ElementName,Result.PASS);
                            return;
                        }
                    }catch (Exception E){ }
                    try{
                        if(Element.getAttribute ("class").contains ("selected")){
                            log (logger,driver,ElementName,Result.PASS);
                            return;
                        }
                    }catch (Exception E){ }
                    try{
                        if(Element.getAttribute ("src").contains ("_on")){
                            log (logger,driver,ElementName,Result.PASS);
                            return;
                        }
                    }catch (Exception E){}
                    log (logger,driver,ElementName,Result.FAIL);
                }else {
                    log(logger,driver,ElementName,Result.FAIL);
                }
        }
    }

    public String perform(String ElementName, Perform Action) throws IOException {
        switch (Action) {
            case GetPageTitle:
                boolean flaged=false;
                while (!flaged) try {
                    driver.getTitle ();
                    flaged = true;
                    log (logger,driver,ElementName, Result.ELEMENTFOUND);
                    return driver.getTitle ( );
                } catch (Exception E) {
                    log (logger,driver,ElementName, Result.ELEMENTNOTFOUND);
                }
                break;
            case PopupHandle:
                try{
                    String popupMessage;
                    popupMessage=driver.switchTo ().alert ().getText ();
                    driver.switchTo ().alert ().accept ();
                    pageLoader();
                    log (logger,driver,ElementName,Result.ELEMENTFOUND);
                    return popupMessage;
                }catch(Exception E){
                    log (logger,driver,ElementName,Result.ELEMENTNOTFOUND);
                }
                break;
            case SwitchWindow:
                try{
                    driver.switchTo ().window (ElementName);
                    log (logger,driver,ElementName,Result.PASS);
                }catch (Exception E){
                    log(logger,driver,ElementName,Result.FAIL);
                }
        }
        return "";
    }

    public boolean findElement(String Name, WebElement Element) throws IOException {
        try {
            Element.isDisplayed();
            highlighter (Element);
            log(logger,driver,Name, ELEMENTFOUND);
            return true;
        } catch (Exception NotFound) {
            log(logger,driver,Name, ELEMENTNOTFOUND);
            return false;
        }


        /*
        try{
            ((JavascriptExecutor) driver).executeScript ("return typeof(arguments[0]) != 'undefined' && arguments[0] != null;",
                    Element);
            highlighter (Element);
            log(Name, ELEMENTFOUND);
            return true;
            }catch (Exception NotFound){
            log(Name, ELEMENTNOTFOUND);
            return false;
            }
            */
    }

    public boolean findElements(String Name, List<WebElement> Element) throws IOException {
        try {
            Element.size ();
            Element.get (0).isDisplayed();
            log(logger,driver,Name, ELEMENTFOUND);
            return true;
        } catch (Exception NotFound) {
            log(logger,driver,Name, ELEMENTNOTFOUND);
            return false;
        }

        /*
            try{
            ((JavascriptExecutor) driver).executeScript ("return typeof(arguments[0]) != 'undefined' && arguments[0] != null;",
                    Element.get (0));
            highlighter (Element.get (0));
            log(Name, ELEMENTFOUND);
            return true;
        }catch (Exception NotFound){
            log(Name, ELEMENTNOTFOUND);
            return false;
        }
        */
    }

    public void pageLoader() throws IOException {
        driver.switchTo ().defaultContent ();
        int count=0;
        driver.manage ().timeouts ().implicitlyWait (100,TimeUnit.MILLISECONDS);
        try{
            while(driver.findElement (By.xpath ("//p[@class='preloader']")).isEnabled () && count++<450){
                highlighter (driver.findElement (By.xpath ("//p[@class='preloader']")));
                //System.out.println ("Pre Loader2" );
                sleep (200);
            }
            count=0;
        }catch(Exception e){ }

        try{
            for(int i=0;i<driver.findElements (By.xpath ("//div[@id='ajax-loader']")).size ();i++) {
                while (driver.findElements (By.xpath ("//div[@id='ajax-loader']")).get (i).getLocation ( ).x > 0 && count++ < 450) {
                    highlighter (driver.findElements (By.xpath ("//div[@id='ajax-loader']")).get (i));
                    //System.out.println ("Ajax Loader" );
                    sleep (200);
                }
                count=0;
            }
        }catch(Exception e1){ }

        try{
            for(int i=0;i<driver.findElements (By.xpath ("//div[contains(@class,'loading')]")).size ();i++){
                while(driver.findElements (By.xpath ("//div[contains(@class,'loading')]")).get (i).getLocation ().x>0&& count++<450){
                    highlighter (driver.findElements (By.xpath ("//div[contains(@class,'loading')]")).get (i));
                    //System.out.println ("Loading" );
                    sleep (200);
                }
                count=0;
            }

        }catch (Exception e2){ }

        try{
            while(driver.findElement (By.xpath ("//div[contains(@class,'loading')]")).getLocation ().x>0&& count++<450){
                highlighter (driver.findElement (By.xpath ("//div[contains(@class,'loading')]")));
                //System.out.println ("Loading2" );
                sleep (200);
            }
            count=0;
        }catch (Exception e4){ }

        try {
            while (driver.findElement (By.xpath ("//div[@id='warning-area' and contains(@style,'opacity')]")).isEnabled ()&& count++<450){
                highlighter (driver.findElement (By.xpath ("//div[@id='warning-area' and contains(@style,'opacity')]")));
                //System.out.println ("Warning Area" );
                sleep (200);
            }
            count=0;
        }catch(Exception e3){ }


        try{
            while (driver.findElement (By.xpath ("//iframe[not(contains(@style,''))]")).getLocation ().x>0&& count++<450) {
                highlighter (driver.findElement (By.xpath ("//iframe[not(contains(@style,''))]")));
                //System.out.println ("Frame Loading");
                sleep (200);
            }
            count=0;
        }catch (Exception e7){}

        try{driver.switchTo ().frame (0);
            while (driver.findElement (By.xpath ("//div[contains(@class,'loading') and contains(@style,'hidden')]")).isEnabled ()&& count++<450) {
                highlighter (driver.findElement (By.xpath ("//div[contains(@class,'loading') and contains(@style,'hidden')]")));
                //System.out.println ("Loading Wrapper");
                sleep (200);
            }
            count=0;
            driver.switchTo ().defaultContent ();
        }catch (Exception e10){driver.switchTo ().defaultContent ();}

        try{driver.switchTo ().frame (0);
            while (driver.findElements (By.xpath ("//div[contains(@class,'spinnerAreaWrapper')]")).get (1).getLocation ().x>0&& count++<450) {
                highlighter (driver.findElements (By.xpath ("//div[contains(@class,'spinnerAreaWrapper')]")).get (1));
                //System.out.println ("Spinner Area");
                sleep (200);
            }
            count=0;
            driver.switchTo ().defaultContent ();
        }catch (Exception e8){driver.switchTo ().defaultContent ();}

        try{driver.switchTo ().frame (0);
            while (driver.findElement (By.xpath ("//img[contains(@class,'spinner')]")).getLocation ().x>0&& count++<450) {
                highlighter (driver.findElement (By.xpath ("//img[contains(@class,'spinner')]")));
                //System.out.println ("Spinner Image");
                sleep (200);
            }
            count=0;
            driver.switchTo ().defaultContent ();
        }catch (Exception e9){driver.switchTo ().defaultContent ();}

        try{driver.switchTo ().frame (0);
            while (driver.findElement (By.xpath ("//img[contains(@id,'Spinner') and contains(@style,'block')]")).getLocation ().x>0&& count++<450) {
                highlighter (driver.findElement (By.xpath ("//img[contains(@id,'Spinner') and contains(@style,'block')]")));
                //System.out.println ("Search Spinner");
                sleep (200);
            }
            count=0;
            driver.switchTo ().defaultContent ();
        }catch (Exception e9){driver.switchTo ().defaultContent ();}

        try{driver.switchTo ().frame (0);
            while (driver.findElement (By.xpath ("//img[contains(@src,'loader.gif')]")).getLocation ().x>0&& count++<450) {
                highlighter (driver.findElement (By.xpath ("//img[contains(@src,'loader.gif')]")));
                //System.out.println ("Loader.gif");
                sleep (200);
            }
            count=0;
            driver.switchTo ().defaultContent ();
        }catch (Exception e9){
            driver.switchTo ().defaultContent ();
        }

        try{
            while (driver.findElement (By.xpath ("//div[contains(@class,'eviti-waiting-bar')]")).isDisplayed ()&& count++<450) {
                sleep (200);
            }
            count=0;
        }catch (Exception e5){}

        /*
        try{
            while (driver.findElement (By.xpath ("//*[contains(*,'Updating Clinical Information')]")).isDisplayed ()) {
                System.out.println ("Clinical Information");
                sleep (200);
            }
        }catch (Exception e5){}
        */

        try{driver.switchTo ().frame (0);
            while(driver.findElement (By.xpath ("//span[contains(text(),'Loading')]")).isEnabled ()&& count++<450){
                highlighter (driver.findElement (By.xpath ("//span[contains(text(),'Loading')]")));
//                System.out.println ("Progress Bar" );
                sleep (200);
            }
            count=0;
            driver.switchTo ().defaultContent ();
        }catch(Exception e){driver.switchTo ().defaultContent (); }


        try{driver.switchTo ().frame (0);
            while(driver.findElement (By.xpath ("//div[@class='progress-bar progress-bar-striped active']")).isEnabled ()&& count++<450){
                highlighter (driver.findElement (By.xpath ("//div[@class='progress-bar progress-bar-striped active']")));
//                System.out.println ("Progress Bar" );
                sleep (200);
            }
            count=0;
            driver.switchTo ().defaultContent ();
        }catch(Exception e){driver.switchTo ().defaultContent (); }

        try{
            while(driver.findElement (By.xpath ("//img[contains(@src,'SearchIcon') and contains(@style,'none')]")).isEnabled ()&& count++<450){
//                highlighter (driver.findElement (By.xpath ("//img[contains(@src,'SearchIcon') and contains(@style,'none')]")));
                sleep (200);
            }
            count=0;
        }catch(Exception e){}

//        try{
//            while(driver.findElement (By.xpath ("//div[contains(@class,'largeSpinner')]")).getLocation ().x>0&& count++<450){
//                highlighter (driver.findElement (By.xpath ("//div[contains(@class,'largeSpinner')]")));
//                System.out.println ("I am here" );
//                sleep (200);
//            }
//            count=0;
//        }catch(Exception e){}

        try{
            if (driver.findElement (By.xpath ("//*[contains(text(),'An unexpected')]")).isDisplayed ()) {
                highlighter (driver.findElement (By.xpath ("//*[contains(text(),'An unexpected')]")));
                log (logger,driver,driver.findElement (By.xpath ("//*[contains(text(),'An unexpected')]")).getText (),Result.FAIL);
                return;
            }
        }catch (Exception e5){}

        try{
            if (driver.findElement (By.xpath ("//*[contains(text(),'Server Error')]")).isDisplayed () && !this.getClass ().getName ().contains ("Prompt")) {
//                highlighter (driver.findElement (By.xpath ("//*[contains(text(),'Server Error')]")));
                log (logger,driver,driver.findElement (By.xpath ("//*[contains(text(),'Server Error')]")).getText (),Result.FAIL);
                return;
            }
        }catch (Exception e5){}

        try{driver.switchTo ().frame (0);
            if (driver.findElement (By.xpath ("//*[contains(text(),'An unexpected')]")).isDisplayed ()) {
                highlighter (driver.findElement (By.xpath ("//*[contains(text(),'An unexpected')]")));
                log (logger,driver,driver.findElement (By.xpath ("//*[contains(text(),'An unexpected')]")).getText (),Result.FAIL);
                return;
            }
            driver.switchTo ().defaultContent ();
        }catch (Exception e5){driver.switchTo ().defaultContent ();}

        try{driver.switchTo ().frame (0);
            if (driver.findElement (By.xpath ("//*[contains(text(),'The Case is missing')]")).isDisplayed ()) {
                highlighter (driver.findElement (By.xpath ("//*[contains(text(),'The Case is missing')]")));
                log (logger,driver,driver.findElement (By.xpath ("//*[contains(text(),'The Case is missing')]")).getText (),Result.FAIL);
                return;
            }
            driver.switchTo ().defaultContent ();
        }catch (Exception e5){driver.switchTo ().defaultContent ();}

        try{
            if (driver.findElement (By.xpath ("//*[contains(text(),'Please try again later')]")).isDisplayed ()) {
                highlighter (driver.findElement (By.xpath ("//*[contains(text(),'Please try again later')]")));
                log (logger,driver,driver.findElement (By.xpath ("//*[contains(text(),'Please try again later')]")).getText (),Result.FAIL);
                return;
            }
        }catch (Exception e5){}

        try{
            if (driver.findElement (By.xpath ("//div[contains(text(),'Error')]")).isDisplayed ()) {
                highlighter (driver.findElement (By.xpath ("//div[contains(text(),'Error')]")));
                log (logger,driver,driver.findElement (By.xpath ("//div[contains(text(),'Error')]")).getText (),Result.FAIL);
                return;
            }
        }catch (Exception e5){}

        try{
            if (driver.findElement (By.xpath ("//*[contains(text(),'Our apologies')]")).isDisplayed ()) {
                highlighter (driver.findElement (By.xpath ("//*[contains(text(),'Our apologies')]")));
                log (logger,driver,driver.findElement (By.xpath ("//*[contains(text(),'Our apologies')]")).getText (),Result.FAIL);
                return;
            }
        }catch (Exception e5){}


        try{
            if (driver.findElement (By.xpath ("//li[contains(text(),'duplicate for the following drug')]")).isDisplayed ()) {
                highlighter (driver.findElement (By.xpath ("//li[contains(text(),'duplicate for the following drug')]")));
                log (logger,driver,driver.findElement (By.xpath ("//li[contains(text(),'duplicate for the following drug')]")).getText (),Result.WARNING);
                return;
            }
        }catch (Exception e5){}/////////////////////////////////////duplicate message ONC-P/////////////////////////////////////////////


        try{
            if (driver.findElement (By.xpath ("//*[contains(text(),'Requests to the server have been blocked by an extension.')]")).isEnabled()) {
                highlighter (driver.findElement (By.xpath ("//*[contains(text(),'Requests to the server have been blocked by an extension.')]")));
                log (logger,driver,"Requests to the server have been blocked by an extension.",Result.FAIL);
                return;
            }
        }catch (Exception e5){}
        try{driver.switchTo ().frame (0);
            if (driver.findElement (By.xpath ("//*[contains(text(),'Requests to the server have been blocked by an extension.')]")).isEnabled()) {
                highlighter (driver.findElement (By.xpath ("//*[contains(text(),'Requests to the server have been blocked by an extension.')]")));
                log (logger,driver,"Requests to the server have been blocked by an extension.",Result.FAIL);
                return;
            }
            driver.switchTo ().defaultContent ();
        }catch (Exception e5){ driver.switchTo ().defaultContent ();}

//        Actions action = new Actions(driver);
//        action.sendKeys(Keys.PAGE_UP).build().perform();
        driver.manage().timeouts().implicitlyWait(implicitwait, TimeUnit.SECONDS);
    }

    public void popupHandle() throws IOException {
        perform ("Popup",Perform.PopupHandle);}

    private void highlighter(WebElement element){
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].setAttribute('style', 'border: 2px solid green;');", element);
    }

    public void verifyPage(PageTitles pagetitle) throws IOException {
        String ExpectedPageTitle=pagetitle.title().replace (" ","");
        String ActualPageTitle=perform("Page Title",Perform.GetPageTitle).replace (" ","");
        if (ActualPageTitle.contains (ExpectedPageTitle)) {
            log(logger,driver,ActualPageTitle + " Page", LOADED);
        } else {
            log(logger,driver,ActualPageTitle + " Page", NOTLOADED);
        }
    }

}
