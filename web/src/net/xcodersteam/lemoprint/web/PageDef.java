package net.xcodersteam.lemoprint.web;

/** Class to define all non-special pages bindings
 * Created by semoro on 23.04.15.
 */
public class PageDef {
    public static void addStaticPages(){
        DynamicPage.initialize();

       // DynamicPage.addDynamicPage("^/[\\w.]+\\.jhtml$");
        DynamicPage.addDynamicPage("^/$");
        DynamicPage.addDynamicPage("^/index.html$");

        StaticPage.addStaticPage("^/[\\w.]*$");
    }
}
