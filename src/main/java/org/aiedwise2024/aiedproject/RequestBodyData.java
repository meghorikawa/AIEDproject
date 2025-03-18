package org.aiedwise2024.aiedproject;

import java.util.List;
import java.util.Map;

/**This class handles the parameters to be sent to the model for prompting
 * we are using groq's API call format here with documentation found here
 * https://console.groq.com/docs/api-reference#chat
 *
 * response format must be sent as:
 * "response_format": {"type": "json_object"}
 *
 */

public class RequestBodyData {
    String model = "deepseek-r1-distill-llama-70b";
    double temperature = 0.0; //higher temperature introduces more randomness ares uses 0.0

    //response_format
    Map<String, String> response_format = Map.of("type", "json_object");

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
        this.temperature = temp;
    }

    public double getTemp() {
        return temperature;
    }

    public Map<String,String> getResponseFormat() {
        return response_format;
    }

    public List<LMmessage> getMessages() {
        return messages;
    }

    public void setMessages(List<LMmessage> messages) {this.messages = messages;}
}