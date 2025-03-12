package org.aiedwise2024.aiedproject;

import java.util.List;

/**This class handles the parameters to be sent to the model for prompting
 * we are using groq's API call format here with documentation found here
 * https://console.groq.com/docs/api-reference#chat
 */


public class RequestBodyData {
    String model = "llama-3.3-70b-versatile";
    double temp = 0.0; //higher temperature introduces more randomness ares usese 0.0
    String responseFormat = "{ \"type\": \"json_object\" }";

    List<LMmessage> messages; // list of messages to include to model

    //constructor
    public RequestBodyData(List<LMmessage> messages) {
        this.messages = messages;
    }

    //getter and setter methods
    public void setModel(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public double getTemp() {
        return temp;
    }

    public void setResponseFormat(String responseFormat) {
        this.responseFormat = responseFormat;
    }

    public String getResponseFormat() {
        return responseFormat;
    }

    public List<LMmessage> getMessages() {
        return messages;
    }

    public void setMessages(List<LMmessage> messages) {this.messages = messages;}
}