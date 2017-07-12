package com.example.sandeepsharma.diginehru.Frameworks;

/**
 * Created by sandeepsharma on 30/06/17.
 */

public class Response {
    private String reponseText;
    private String errorText;
    private int reponseCode;

    public Response() {

    }

    public String getReponseText() {
        return reponseText;
    }

    public void setReponseText(String reponseText) {
        this.reponseText = reponseText;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public int getReponseCode() {
        return reponseCode;
    }

    public void setReponseCode(int reponseCode) {
        this.reponseCode = reponseCode;
    }


}
