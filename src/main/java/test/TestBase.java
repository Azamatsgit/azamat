
        package test;

        import com.relevantcodes.extentreports.ExtentReports;
        import com.relevantcodes.extentreports.ExtentTest;
        import com.relevantcodes.extentreports.LogStatus;
        import org.apache.commons.exec.OS;
        import org.apache.commons.io.FileUtils;
        import org.apache.commons.io.filefilter.AgeFileFilter;
        import org.apache.commons.lang3.StringUtils;
        import org.apache.commons.lang3.time.DateUtils;
        import org.openqa.selenium.*;
        import org.openqa.selenium.chrome.ChromeDriver;
        import org.openqa.selenium.chrome.ChromeOptions;
        import org.openqa.selenium.firefox.FirefoxDriver;
        import org.openqa.selenium.ie.InternetExplorerDriver;
        import org.openqa.selenium.ie.InternetExplorerOptions;
        import org.testng.ITestContext;
        import org.testng.ITestResult;
        import org.testng.annotations.*;
        import org.w3c.dom.Document;
        import org.w3c.dom.Node;
        import org.xml.sax.SAXException;
        import report.Reporting;
        import util.Application;
        import util.Result;

        import javax.mail.*;
        import javax.mail.internet.InternetAddress;
        import javax.mail.internet.MimeBodyPart;
        import javax.mail.internet.MimeMessage;
        import javax.mail.internet.MimeMultipart;
//        import javax.servlet.annotation.WebListener;
        import javax.xml.parsers.DocumentBuilder;
        import javax.xml.parsers.DocumentBuilderFactory;
        import javax.xml.parsers.ParserConfigurationException;
        import javax.xml.transform.Transformer;
        import javax.xml.transform.TransformerException;
        import javax.xml.transform.TransformerFactory;
        import javax.xml.transform.dom.DOMSource;
        import javax.xml.transform.stream.StreamResult;
        import java.io.File;
        import java.io.FileInputStream;
        import java.io.IOException;
        import java.net.InetAddress;
        import java.nio.file.Files;
        import java.nio.file.Path;
        import java.nio.file.Paths;
        import java.util.*;
        import java.util.concurrent.TimeUnit;
        import java.util.zip.ZipEntry;
        import java.util.zip.ZipOutputStream;

        import static util.Result.*;

        public class TestBase extends Reporting {
    public WebDriver driver;
    public static long implicitwait;
    public static Properties Prop;
    public static ExtentReports extent;
    public ExtentTest logger;
    public static String envi="";
    public static String mpath;
    public static String FailedTestPath;
    public static String browser;
    public static String submpath;
    public static int tests;
    public static String[] Body;
    public static String suitename;
    public static String email;
    public int count;

    @BeforeSuite
    public void reportsetup(ITestContext iTestContext) throws IOException, ParserConfigurationException, SAXException, TransformerException {
        tests=0;
        Body=new String[256];
        suitename=iTestContext.getSuite ().getName ();
        if(envi.equalsIgnoreCase ("")){
            envi=iTestContext.getSuite ().getParameter ("environment");
        }
        ReportStart ( );
        ReportingParametersSet (mpath, implicitwait, browser,FailedTestPath);
    }

    @BeforeClass
    public void setup() throws IOException {
        logger = extent.startTest (StringUtils.substringAfterLast (this.getClass ( ).getName ( ), ".").replace ("_"," "));
        count=0;
    }

    @BeforeMethod
    public void setgo() throws IOException {
        loadProperty ( );
        Start ();
    }

    @AfterMethod
    public void afterMethod(ITestResult result) throws IOException {
        if(!result.isSuccess () && count<2){
            logger.getTest ().getLogList ().clear ();
            logger.setDescription ("");
            logger.getTest ().setStatus (LogStatus.INFO);
            count++;

        }

        try{
            driver.close ( );
        }catch (Exception E){}
        if (result.getStatus ( ) == ITestResult.FAILURE) {
            if(!result.getThrowable ().toString ().contains ("NoSuchSessionException")){
                log (logger,driver, String.valueOf (result.getThrowable ( )),FAILURE);

            }
//        } else if(result.getStatus ( ) == ITestResult.SKIP){
//            log (logger,driver, String.valueOf (result.getThrowable ( )),FAILURE);
        }
    }

    @AfterClass
    public void teardown() {
        extent.flush ( );
        extent.endTest (logger);
        try{
            driver.close ( );
        }catch (Exception E){}
        if(logger.getRunStatus ( ).toString ( ).toUpperCase ( ).equalsIgnoreCase ("PASS")){
            Body[tests++]="Pass-"+StringUtils.substringAfterLast (this.getClass ( ).getName ( ), ".").replace ("_"," ");
        }
        else if(logger.getRunStatus ( ).toString ( ).toUpperCase ( ).equalsIgnoreCase ("Warning")){
            Body[tests++]="Pass-"+StringUtils.substringAfterLast (this.getClass ( ).getName ( ), ".").replace ("_"," ");
        }
        else {
            Body[tests++]="Fail-"+StringUtils.substringAfterLast (this.getClass ( ).getName ( ), ".").replace ("_"," ")+" Reason: "+logger.getDescription ();
        }
    }

    @AfterSuite
    public void end() throws IOException, MessagingException {
        ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "chrome.bat");
        File dir = new File(System.getProperty ("user.dir"));
        pb.directory(dir);
        Process p = pb.start();
        extent.close ( );
        updateBody(Body);
//        pack (mpath, submpath + ".zip");
        InetAddress local=InetAddress.getLocalHost ();
        String hostname = local.getHostName();
        SendMail (suitename+" in "+envi+" on "+hostname+" by "+System.getProperty ("user.name")+"-"+browser);
        deleteOldFiles ();
    }

    public void ReportStart() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "chrome.bat");
        File dir = new File(System.getProperty ("user.dir"));
        pb.directory(dir);
        Process p = pb.start();
        loadProperty ( );
        createOutputDirectory ( );
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(System.getProperty ("user.dir") + "\\" + "extent-config.xml");
        Node reportheadline = document.getElementsByTagName("reportHeadline").item (0);
        reportheadline.setTextContent (suitename+" in "+envi);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(new File(System.getProperty ("user.dir") + "\\" + "extent-config.xml"));
        transformer.transform(domSource, streamResult);

        extent = new ExtentReports (mpath + "/" + "ExtentReport.html", true);
        extent
                .addSystemInfo ("User Name", System.getProperty ("user.name"))
                .addSystemInfo ("Environment", envi)
                .addSystemInfo ("Report Name", suitename)
                .addSystemInfo ("Browser",browser);
        extent.loadConfig (new File (System.getProperty ("user.dir") + "\\" + "extent-config.xml"));
    }

    public void createOutputDirectory() {
        submpath=dateTodayForReport ( ) + " " + timeToday ( );
        FailedTestPath=getproperty ("FailedTestPath")+getproperty ("testname") + "/" + submpath;
        mpath = getproperty ("reportpath") + getproperty ("testname") + "/" + submpath;
        browser = getproperty ("browser");
        implicitwait = Long.parseLong (getproperty ("implicitwait"));
        email=getproperty ("email");
        new File (mpath).mkdirs ( );
        new File (FailedTestPath).mkdir ();
    }

    public static String getProjectPath() {
        return System.getProperty ("user.dir");
    }

    private String getDriverKey() {
        return "webdriver." + getBrowserKey ( ) + ".driver";
    }

    private String getBrowserDriverPath() {
        return getProjectPath() + "\\src\\main\\resources\\browserDrivers\\" + getBrowserDriverName()+"_" + getOS() + getOSArchitecture();
        //return getProjectPath ( ) + "\\src\\main\\resources\\browserDrivers\\" + getBrowserDriverName ( ) + "_" + getOS ( ) + "32.exe";
    }

    private String getOS() {
        if (OS.isFamilyWindows ( ))
            return "WIN";
        else if (OS.isFamilyMac ( ))
            return "MAC";
        else {
            return "";
        }
    }

    private String getOSArchitecture() {
        if (getOS ( ).equalsIgnoreCase ("mac"))
            return "";
        else if (getOS ( ).equalsIgnoreCase ("win")) {
            if (System.getProperty ("os.arch").contains ("86"))
                return "32.exe";
            else if (System.getProperty ("os.arch").contains ("64")) {
                if (getBrowserKey ( ).equalsIgnoreCase ("chrome")) {
                    return "32.exe";
                } else return "64.exe";
            } else return "";
        } else
            return "";
    }

    private String getBrowserKey() {
        if (browser.equalsIgnoreCase ("ff"))
            return "gecko";
        else if (browser.equalsIgnoreCase ("chrome"))
            return "chrome";
        else if (browser.equalsIgnoreCase ("ie"))
            return "ie";
        else return "";
    }

    private String getBrowserDriverName() {
        if (browser.equalsIgnoreCase ("ff"))
            return "geckodriver";
        else if (browser.equalsIgnoreCase ("chrome"))
            return "chromedriver";
        else if (browser.equalsIgnoreCase ("ie"))
            return "IEDriverServer";
        else return "";
    }

    public void loadProperty() throws IOException {
        String rootPath = getProjectPath ( ) + "\\properties\\";
        String ConfigPath = rootPath + "Config.Properties";
        FileInputStream f = new FileInputStream (ConfigPath);
        Prop = new Properties ( );
        Prop.load (f);
    }

    public String getproperty(String WhatProperty) {
        return Prop.getProperty (WhatProperty);
    }

    public void launchApp(Application App) {
        String URL = getproperty (App + envi);
        driver.get (URL);
    }

    public void navigateToApp(Application App) {
        String URL = getproperty (App + envi);
        driver.navigate ().to (URL);
    }

    public void launchApp(Application App,String environment) {
        String URL = getproperty (App + environment);
        driver.get (URL);
    }

    public void launchURL(String URL) {
        driver.get (URL);
    }

    private void Start() throws IOException {
        File DriverFile = new File (getBrowserDriverPath ( ));
        try {
            System.setProperty (getDriverKey ( ), DriverFile.getAbsolutePath ( ));
            log (logger, driver, "Browser Driver Set", PASS);
        } catch (Exception NotLoaded) {
            log (logger, driver, "Browser Driver Not Set", ELEMENTNOTFOUND);
        }
        initializeDriver ();
        driver.manage ( ).window ( ).fullscreen ( );
        driver.manage ( ).timeouts ( ).implicitlyWait (implicitwait, TimeUnit.SECONDS);
        driver.manage ( ).timeouts ( ).pageLoadTimeout (Long.parseLong (getproperty ("pageloadtime")), TimeUnit.SECONDS);
        driver.manage ( ).deleteAllCookies ( );
    }

    private InternetExplorerOptions setCapabilityIE() {
        InternetExplorerOptions options = new InternetExplorerOptions ( );
        options.setCapability (InternetExplorerDriver.NATIVE_EVENTS, false);
        options.setCapability (InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
        options.setCapability (InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
        options.setCapability (InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, true);
        //options.setCapability(InternetExplorerDriver.SILENT, true);
        return options;
    }

    private ChromeOptions setCapabilityChrome() {
        ChromeOptions options = new ChromeOptions ();
//        options.setBinary("C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe");
        options.addArguments("disable-popup-blocking");
        options.addArguments("--no-sandbox");
//        options.addArguments ("incognito");
//        String userProfile= "C:\\Users\\sbiswas\\AppData\\Local\\Google\\Chrome\\User Data\\Default";
//        options.addArguments("user-data-dir="+userProfile);
//        options.addArguments("--headless");
        options.setUnhandledPromptBehaviour (UnexpectedAlertBehaviour.ACCEPT);
        options.addArguments ("--explicitly-allowed-ports=1,7,9,11,13,15,17,19,20,21,22,23,25,37,42,43,53,77,79,87,95," +
                "101,102,103,104,109,110,111,113,115,117,119,123,135,139,143,179,389,465,512,513,514,515,526,530,531," +
                "532,540,556,563,587,601,636,993,995,2049,3659,4045,6000,6665,6666,6667,6668,6669");
        // these are err_unsafe_port list from https://www.itread01.com/articles/1496921898.html

        return options;
    }

    private void initializeDriver() throws IOException {
        try {
            if (browser.equalsIgnoreCase ("ff")) {
                driver = new FirefoxDriver ( );
                log (logger, driver, browser + " Browser", Result.LOADED);
            } else if (browser.equalsIgnoreCase ("chrome")) {
                driver = new ChromeDriver (setCapabilityChrome());
                log (logger, driver, browser + " Browser", Result.LOADED);
            } else if (browser.equalsIgnoreCase ("ie")) {
                driver = new InternetExplorerDriver (setCapabilityIE ( ));
                log (logger, driver, browser + " Browser", Result.LOADED);
            } else log (logger, driver, browser + " Browser", Result.NOTLOADED);
        } catch (Exception NotFound) {
            log (logger, driver, browser + " Browser- Did Not Open", Result.TRYAGAIN);
            initializeDriver ();
        }
    }

    public static void pack(String sourceDirPath, String zipFilePath) throws IOException {
        Path p = Files.createFile (Paths.get (zipFilePath));
        try (ZipOutputStream zs = new ZipOutputStream (Files.newOutputStream (p))) {
            Path pp = Paths.get (sourceDirPath);
            Files.walk (pp)
                    .filter (path -> !Files.isDirectory (path))
                    .forEach (path -> {
                        ZipEntry zipEntry = new ZipEntry (pp.relativize (path).toString ( ));
                        try {
                            zs.putNextEntry (zipEntry);
                            Files.copy (path, zs);
                            zs.closeEntry ( );
                        } catch (IOException e) {
                            System.err.println (e);
                        }
                    });
        }
    }

    public void SendMail(String subject) throws IOException, MessagingException {
        try {
            String smtpHostServer = "sdcpixch006.deerfield.aim.local";
            Properties proper = System.getProperties ();
            proper.setProperty ("mail.smtp.host", smtpHostServer);
            //proper.put ("mail.smtp.port","25");
            Session session = Session.getInstance (proper);
            MimeMessage msg = new MimeMessage (session);
            //set message headers
            msg.addHeader ("Content-type", "text/plain; charset=UTF-8");
            msg.addHeader ("format", "flowed");
            msg.addHeader ("Content-Transfer-Encoding", "8bit");
            msg.setFrom (new InternetAddress ("DoNotReply-azamat.uulu@morningstar.com"));
            msg.setReplyTo (InternetAddress.parse ("no_reply@morningstar.com",false));
            msg.setSubject (subject, "UTF-8");
            String bodytext="Please find the "+suitename+" Automation Summary "+"in "+envi+" Environment as mentioned below:\n\n";
            for(int j=0;j<tests;j++)
                bodytext=bodytext+Body[j]+"\n";
            bodytext=bodytext+"\nPlease find the attached report";
            msg.setText (bodytext, "UTF-8");
            msg.setSentDate (new Date ( ));
            BodyPart messageBodyPart = new MimeBodyPart ( );
            messageBodyPart.setContent (bodytext, "text/plain; charset=UTF-8");
            Multipart multipart = new MimeMultipart ( );
            multipart.addBodyPart (messageBodyPart);
//            attachPart.attachFile (submpath +".zip");
            File folder = new File(mpath);
            File[] listOfFiles = folder.listFiles();
            for (int i = 0; i < listOfFiles.length; i++) {
                MimeBodyPart attachPart = new MimeBodyPart();
                attachPart.attachFile(mpath+"\\"+listOfFiles[i].getName());
                multipart.addBodyPart(attachPart);
            }
            msg.setContent (multipart);
            msg.setRecipients (Message.RecipientType.TO, InternetAddress.parse (email,false));
            //System.out.println ("Message is ready");
            Transport.send (msg);

            //System.out.println ("Email Sent Successfully!!");
        } catch (Exception e) {
            e.printStackTrace ( );
        }
    }

    public void updateBody(String[] tBody){
        String[] tempBody=new String [tests+5];
        int j=0;
        tempBody[j++]="TOTAL TESTS: "+tests;
        tempBody[j++]="";
        int passrow=j++;
        for(int i=0;i<tests;i++){
            if(Body[i].contains ("Fail-")){
                tempBody[j++]=Body[i];
                System.out.println (Body[i]);
            }
        }
        if(j>passrow+1){
            tempBody[passrow]="FAILED TESTS: "+String.valueOf (j-passrow-1);
        }else {
            tempBody[passrow]="FAILED TESTS: "+String.valueOf (0);
        }
        tempBody[j++]="";
        int temp=j++;
        for(int i=0;i<tests;i++){
            if(Body[i].contains ("Pass-")){
                tempBody[j++]=Body[i];
                System.out.println (Body[i]);
            }
        }
        if(j-temp-1>0){
            tempBody[temp]="PASSED TESTS: "+String.valueOf (j-temp-1);
        }else{
            tempBody[temp]="PASSED TESTS: "+String.valueOf (0);
        }
        for(int i=0;i<j;i++){
            Body[i]=tempBody[i];
        }
        tests=j;
    }

    public void deleteOldFiles() {
        Date oldestAllowedFileDate = DateUtils.addDays(new Date(), -30); //minus days from current date
        File targetDir = new File("\\\\sdcpifil010\\Releases\\Automation - QTP\\UCDSmoke\\Selenium Smoke Test\\Results");
        Iterator<File> filesToDelete = FileUtils.iterateFiles(targetDir, new AgeFileFilter (oldestAllowedFileDate), null);
        while (filesToDelete.hasNext()) {
            FileUtils.deleteQuietly(filesToDelete.next());
        }
    }

}
