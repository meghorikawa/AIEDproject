package org.aiedwise2024.aiedproject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.HttpConstraint;
import jakarta.servlet.annotation.ServletSecurity;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson; //google's JSON converter

import static org.aiedwise2024.aiedproject.LMmessage.ROLE_SYSTEM;
import static org.aiedwise2024.aiedproject.LMmessage.ROLE_USER;

/**
 * A servlet that handles question generation for students who
 * wish for more practice on a specific grammatic form by prompting ChatGPT
 * */

@WebServlet(
        name = "grammar form question generation servlet",
        description = "For generating questions via an LLM for students to practice grammatic form",
        urlPatterns = GrammarFormQuestionGenerationServlet.URL_PATH
)
/*Set who can access - both students and teachers should be able to access this feature*/
@ServletSecurity(value = @HttpConstraint(rolesAllowed = {"USER_ACTIVATED", "TEACHER_ACTIVATED", "ADMIN"}))

public class GrammarFormQuestionGenerationServlet extends HttpServlet {

    public static final String GROQ_SERVICE_PATH = "https://api.groq.com/openai/v1/chat/completions";
    public static final String URL_PATH = "/ChatGPT/questions/generation";

    /**Set the parameters here */
    // grammar construct and num of questions
    public static String PARAM_CONSTRUCT = "grammar_construct";
    public static String PARAM_NUM_OF_QUESTIONS = "num_ques";
    public static String PARAM_CEFR_LVL = "cefr_lvl"; //ideally pulled from construct description


    /**Override doGet method to send the prompt to GPT API for generation
     * req is the HTTP request sent by the user
     * resp is the HTTP response sent back to the user and receive response back
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //parse request to recevieve parameters to include in prompt
        String par_construct = req.getParameter(PARAM_CONSTRUCT);
        int par_num = Integer.parseInt(req.getParameter(PARAM_NUM_OF_QUESTIONS));
        String par_level = req.getParameter(PARAM_CEFR_LVL);

        //handle cases for empty parameters or negative numbers
        if (par_construct == null || par_num <= 0 ) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            //error message must be returned as json format
            resp.getWriter().write("{\"error\": \"Missing parameters: Grammar Form and number of questions required.\"}");
        }

        //also handle case for too many questions being generated at once?? up to 20 questions?
        if (par_num > 20) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("{\"error\": \"Too Many Questions: You can only generate 20 or less questions at a time.\"}");
        }


        try {
            //add the prompt builder here which will assemble the prompt
            String prompt = constructPrompt(par_construct, par_level , par_num);

            //Get API Key that was set as environmental variable
            String groqAPIkey = System.getenv("GROQ_API_KEY");

            //New request body to assemble the request
            RequestBody body = new RequestBody();

            //create list of messages to hold conversation messages i.e. system message, user message etc.
            List<LMmessage> messages = new ArrayList<LMmessage>();

            //System level instruction, role and content
            LMmessage systemMsg = new LMmessage(ROLE_SYSTEM, "You are an EFL teacher who teaches English to non-native school students aged 10-18 ");

            //User message - user role and prompt
            LMmessage userMsg = new LMmessage(ROLE_USER, prompt);

            messages.add(systemMsg);
            messages.add(userMsg);
            body.setMessages(messages);

            //convert to JSON
            String requestBodyJson = new Gson().toJson(body);

            //finally send prompt too Groq
            String questions = sendRequestReturnRawResponse(requestBodyJson);

            //recieve generation and parse and send to front end
            resp.setContentType ("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(questions);
        } catch (Exception e) {
           e.printStackTrace();
        }

    }

    //method for constructing prompt
    private String constructPrompt(String construct, String level, int n){
        return "You are an EFL teacher who teaches English to non-native school students " +
                "aged 10-18. Generate " + n +"grammar questions in a " +
                "fill-in-the-blank format with a CEFR level of " + level + "on the topic " +
                "of " + construct + ".Ensure that the questions provide students with opportunities " +
                "to practice the topic from different perspectives and align with their level of knowledge. " +
                "Your resonse should be in the following JSON format: { \"topic\" : \"TOPIC_HERE\", \"questions\" : [\"question\": \"QUESTION_HERE\", \"answer\" : \" ANSWER_HERE\"] }";
    }

    // method for sending request to Groq and returning raw response
    private String sendRequestReturnRawResponse(String requestBodyJson) throws IOException {
        URL url = new URL(GROQ_SERVICE_PATH);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST"); // post request
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] bytes = requestBodyJson.getBytes("UTF-8"); //convert to bytes to send over HTTP
            os.write(bytes, 0, bytes.length); // this sends the byte array to the API
        }

        int responseCode = connection.getResponseCode();
        Scanner scanner = new Scanner(connection.getInputStream(), "UTF-8");
        String response = scanner.useDelimiter("\\A").next(); // convert byte stream into string
        scanner.close();

        // anything besides 200 means an error occured
        if (responseCode != 200) {
            return "{\"error\": \"Failed to fetch data from Groq API\"}";
        }

        return response;

    }


}


