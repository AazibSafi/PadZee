/*
*	Developer: Aazib Safi Patoli
*	January 2017
*/

package com.bit14.padzee;

/**
 * Created by aazib.patoli on 17-Jan-17.
 * Add more script statement by just dappending them to script String in Function
 * And call that function in WebView_Activity.java
 */

public class JS_Injection {

    private String JS_Begin = "javascript:(function(){";
    private String JS_End = "})()";

    private String script;
    private Helper helper;

    public JS_Injection(){
        script = "";
    }

    public void setHelper(Helper helper) {
        this.helper = helper;
    }

    public String getScriptToLoad() {
        return JS_Begin + script + JS_End;
    }

    public boolean isEmpty(){
        return script.equals("");
    }

    public void clear(){
        script = "";
    }

    public void putSharedText(String sharedContent){
        script += "$('textarea"+helper.APP_SUPPORT_REFERENCE+"').val('" + sharedContent + "');";
    }

    public void hideDownloadAppButton(){
        script += "$('"+helper.APP_SUPPORT_DOWNLOAD_BUTTON+"').hide();";
    }

    public void getTextfromWeb(){
        script += "$('textarea"+helper.APP_SUPPORT_REFERENCE+"').val();";
    }

    public void getPadName(){
        script += "$('#txtUrl').val()";
    }
}
