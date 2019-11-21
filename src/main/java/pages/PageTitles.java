package pages;

public enum PageTitles {

    MORNINGSTAR("-morning star");


    String title;

    PageTitles(String title){this.title=title;}

    public String title(){
        return title;
    }


}
