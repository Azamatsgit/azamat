package util;

import com.google.gson.internal.$Gson$Preconditions;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class Retry implements IRetryAnalyzer {

private int count = 0;
        private static int maxTry = 2;

        @Override
            public boolean retry(ITestResult iTestResult){
            if(!iTestResult.isSuccess() && count < maxTry){
                count++;
                return true;
            }
            return false;

        }



        }
