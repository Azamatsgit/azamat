package report;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.SkipException;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;
import util.Result;
import util.Retry;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class Reporting{

    public static String mpath;
    public static String FailedTestPath;
    public static long implicitwait;
    public static String browser;

    public void ReportingParametersSet(String mpath,long implicitwait,String browser,String FailedTestPath){
        this.mpath=mpath;
        this.implicitwait=implicitwait;
        this.browser=browser;
        this.FailedTestPath=FailedTestPath;
    }

    public void Testing(ExtentTest logger, WebDriver driver, String ElementDetails, Result results) throws IOException {
        switch (results){
            case ELEMENTNOTFOUND:
                logger.log (LogStatus.UNKNOWN, results.name (), ElementDetails+results.activity_done());
                break;
            case ELEMENTFOUND:
                logger.log (LogStatus.INFO, results.name (),ElementDetails+results.activity_done ());
                break;
            case ENTEREDTEXT:
                logger.log (LogStatus.INFO,results.name (),ElementDetails+results.activity_done ());
                break;
            case CLICKED:
                logger.log (LogStatus.INFO,results.name (),ElementDetails+results.activity_done ());
                break;
            case NOTCLICKED:
                logger.log (LogStatus.UNKNOWN,results.name (),ElementDetails+results.activity_done ());
                break;
            case STARTED:
                logger.log (LogStatus.INFO,results.name (),ElementDetails+results.activity_done ());
                break;
            case SET:
                logger.log (LogStatus.INFO,results.name (),ElementDetails+results.activity_done ());
                break;
            case NOTSET:
                logger.log (LogStatus.UNKNOWN,results.name (),ElementDetails+results.activity_done ());
                break;
            case COMPLETED:
                logger.log (LogStatus.INFO,results.name (),ElementDetails+results.activity_done ());
                break;
            case CLEARED:
                logger.log (LogStatus.INFO,results.name (),ElementDetails+results.activity_done ());
                break;
            case LOADED:
                logger.log (LogStatus.INFO,results.name (),ElementDetails+results.activity_done ());
                break;
            case PASS:
                logger.log (LogStatus.PASS,results.name (),ElementDetails+results.activity_done ());
                break;
            case TRYAGAIN:
                logger.log (LogStatus.INFO,results.name (),ElementDetails+results.activity_done ());
                break;
            case FAIL:
                logger.log (LogStatus.FAIL, results.name ( )+ ElementDetails + results.activity_done ( ),
                        logger.addScreenCapture (takeScreenshot(driver,logger)));
                logger.setDescription (ElementDetails+results.activity_done());
                driver.close ();

            case NOTLOADED:
                logger.log (LogStatus.FAIL, results.name ( )+ ElementDetails + results.activity_done ( ),
                        logger.addScreenCapture (takeScreenshot(driver,logger)));
                logger.setDescription (ElementDetails+results.activity_done());
                driver.close ();

            case WARNING:
                logger.log (LogStatus.WARNING, results.name ( )+ ElementDetails + results.activity_done ( ),
                        logger.addScreenCapture (takeScreenshot(driver,logger)));
                break;
            case FAILURE:
                logger.log (LogStatus.FAIL,results.name ()+ElementDetails+results.activity_done (),
                        logger.addScreenCapture (takeScreenshot(driver,logger)));

                logger.setDescription ("Script Failure");
//                throw new SkipException("Skip Test");
        }
    }

    public void log(ExtentTest logger,WebDriver driver,String ElementName, Result results) throws IOException {
        //System.out.println(">> "+results+":"+ElementName+" "+results.activity_done());
        Testing (logger,driver," "+ElementName+" ",results);
    }

    public void log(ExtentTest logger,WebDriver driver,String ElementName, String DataSet, Result results) throws IOException {
        //System.out.println(">> "+results+":"+DataSet+":"+ElementName+" "+results.activity_done());
        Testing (logger,driver,DataSet+": "+ElementName+" ",results);
    }

    private String takeScreenshot(WebDriver driver,ExtentTest logger) throws IOException {
        try{
            Screenshot fpScreenshot = new AShot ().shootingStrategy(ShootingStrategies.viewportPasting(1000)).takeScreenshot(driver);
            TakesScreenshot screenshot= (TakesScreenshot)driver;
            String screenshotPath=logger.getTest ().getName ()+"_"+dateTodaywithoutslash ()+"-"+timeToday ()+".PNG";
            ImageIO.write(fpScreenshot.getImage(),"PNG",new File("\\\\sdcpifil010\\Releases\\Automation - QTP\\UCDSmoke\\Selenium Smoke Test\\Results"+"/"+screenshotPath));
            return "\\\\sdcpifil010\\Releases\\Automation - QTP\\UCDSmoke\\Selenium Smoke Test\\Results"+"/"+screenshotPath;
        }catch (Exception E){return "No Screenshot";}
    }

    public String dateTodaywithoutslash(){
        return dateToday().replace ("/","").replace ("-","");
    }

    public String dateBehindFromTodaywithoutslash(int days){
        return dateBehindFromToday(days).replace ("/","").replace ("-","");
    }

    public String dateBehindFromToday(int days){
        SimpleDateFormat sdf=new SimpleDateFormat ("MM/dd/yyyy");
        Calendar c=Calendar.getInstance ();
        c.add (Calendar.DATE,-days);
        Date dt=c.getTime ();
        if(browser.equalsIgnoreCase ("chrome")||browser.equalsIgnoreCase ("ie")){
            return sdf.format (dt);
        }else {
            SimpleDateFormat sdf2=new SimpleDateFormat ("yyyy-MM-dd");
            return sdf2.format(dt);
        }
    }

    public String timeToday(){
        SimpleDateFormat sdf=new SimpleDateFormat ("HH-mm-ss");
        Date Today=Calendar.getInstance ().getTime ();
        return sdf.format (Today);
    }

    public String dateAheadFromTodaywithoutslash(int days){
        return dateAheadFromToday(days).replace ("/","").replace ("-","");
    }

    public String dateAheadFromToday(int days){
        SimpleDateFormat sdf=new SimpleDateFormat ("MM/dd/yyyy");
        Calendar c=Calendar.getInstance ();
        c.add (Calendar.DATE,days);
        Date dt=c.getTime ();
        if(browser.equalsIgnoreCase ("chrome")||browser.equalsIgnoreCase ("ie")){
            return sdf.format (dt);
        }else {
            SimpleDateFormat sdf2=new SimpleDateFormat ("yyyy-MM-dd");
            return sdf2.format(dt);
        }
    }

    public String dateToday(){
        SimpleDateFormat sdf=new SimpleDateFormat ("MM/dd/yyyy");
        Date Today=Calendar.getInstance ().getTime ();
        if(browser.equalsIgnoreCase ("chrome")||browser.equalsIgnoreCase ("ie")){
            return sdf.format (Today);
        }else {
            SimpleDateFormat sdf2=new SimpleDateFormat ("yyyy-MM-dd");
            return sdf2.format(Today);
        }
    }

    public String dateTodayForReport(){
        SimpleDateFormat sdf=new SimpleDateFormat ("yyyy-MM-dd");
        Date Today=Calendar.getInstance ().getTime ();
        return sdf.format(Today).replace ("-","");
    }
}