package test.runner;

import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;
import test.TestBase;

import java.io.IOException;
import java.sql.SQLException;

import java.util.Map;
import java.util.HashMap;
import java.util.*;


public class OneTest extends TestBase {

    public static void main (String args[])throws IOException, SQLException,ClassNotFoundException{
        XmlSuite suite = new XmlSuite();
        suite.setName("One Test");
        Map<String, String> suiteParameters = new HashMap<>();
        suiteParameters.put("environment", "QA");
        suite.setParameters(suiteParameters);
        String run = "";

        XmlTest test = new XmlTest(suite);
        test.setName("Test");
        List<XmlClass> classes = new ArrayList<XmlClass>();
        List<String> classpath = new ArrayList<String>();


        classpath.add("test.");





        for(int i = 0; i<classpath.size(); i++){
            classes.add(new XmlClass(classpath.get(i)));
            if (!classpath.get(i).toUpperCase().contains("PROD")) {
                            }
        }


    }


}
