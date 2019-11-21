package util;

public enum Result {

    PASS("-Passed"),
    FAIL("-Failed"),
    LOADED("-Loaded"),

    COMPLETED("-Loaded"),
    NOTSET("-Nonset"),
    ELEMENTNOTFOUND("-Element NOT Found"),
    CLICKED("-Clicked"),
    NOTCLICKED("-Not Clicked"),
    ENTEREDTEXT("-Entered text"),
    CLEARED("-Cleared"),
    NOTLOADED("-Not loaded"),
    STARTED("-Started"),
    TRYAGAIN("-Try again"),
    WARNING("-Warning"),
    SET("-Set"),
    ELEMENTFOUND("-Element Found"),
    FAILURE("-failure");



    public String activity_done;

    public String activity_done(){ return this.activity_done;}

    Result(String activity_done){ this.activity_done=activity_done;}
}
