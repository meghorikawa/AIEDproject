package org.aiedwise2024.aiedproject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson; //google's JSON converter

//imports for OkHttp
import okhttp3.*;


// import the loggers for easy debugging
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
//@ServletSecurity(value = @HttpConstraint(rolesAllowed = {"USER_ACTIVATED", "TEACHER_ACTIVATED", "ADMIN"}))

public class GrammarFormQuestionGenerationServlet extends HttpServlet {

    public static final String GROQ_SERVICE_PATH = "https://api.groq.com/openai/v1/chat/completions";
    public static final String URL_PATH = "/question/generation";
    //Get API Key that was set as environmental variable
    String groqAPIkey = System.getenv("GROQ_API_KEY");

    //instance of OkHttpClient
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();

    /**Set the parameters here */
    // grammar construct and num of questions
    public static String PARAM_CONSTRUCT = "grammar_construct";
    public static String PARAM_NUM_OF_QUESTIONS = "num_ques";
    public static String PARAM_CEFR_LVL = "cefr_lvl"; //ideally pulled from construct description

    //logger instance
    private static final Logger logger = LoggerFactory.getLogger(GrammarFormQuestionGenerationServlet.class);


    /**Override doGet method to send the prompt to GPT API for generation
     * req is the HTTP request sent by the user
     * resp is the HTTP response sent back to the user and receive response back
     * groq requires a post request...
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //parse request to recevieve parameters to include in prompt
        String par_construct = req.getParameter(PARAM_CONSTRUCT);
        int par_num = Integer.parseInt(req.getParameter(PARAM_NUM_OF_QUESTIONS));
        String par_level = req.getParameter(PARAM_CEFR_LVL);

        logger.info("Received request: grammar_construct = "+ par_construct + ", num_ques= " + par_num + " Cefr_Level"  + par_level);

        //handle cases for empty parameters or negative numbers
        if (par_construct == null || par_num <= 0 ) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            //error message must be returned as json format
            resp.getWriter().write("{\"error\": \"Missing parameters: Grammar Form and number of questions required.\"}");
            //implement logger
            logger.error("Missing parameters: Grammar Form and number of questions required.");
            return;
        }
        //also handle case for too many questions being generated at once?? limit to 20 questions?
        if (par_num > 20) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("{\"error\": \"Too Many Questions: You can only generate 20 or less questions at a time.\"}");
            ///  implement logger here to.
            logger.error("Too many questions at a time.");
            return;
        }

        try {
            //add the prompt builder here which will assemble the prompt
            String prompt = constructPrompt(par_construct, par_level , par_num);

            //create list of messages to hold conversation messages i.e. system message, user message etc.
            List<LMmessage> messages = new ArrayList<LMmessage>();
            //System level instruction, role and content
            LMmessage systemMsg = new LMmessage(ROLE_SYSTEM, "You are an EFL teacher who teaches English to non-native school students aged 10-18 ");
            //User message - user role and prompt
            LMmessage userMsg = new LMmessage(ROLE_USER, prompt);

            messages.add(systemMsg);
            messages.add(userMsg);
            //New request body to assemble the request
            RequestBodyData body = new RequestBodyData(messages);

            //convert to JSON
            String requestBodyJson = new Gson().toJson(body);

            //log body format for debugging
            logger.debug("Generated JSON request: {}", requestBodyJson);

            //finally send prompt too Groq
            String questions = sendRequestReturnRawResponse(resp, groqAPIkey,requestBodyJson,3,logger);

            //check response came back properly
            if (questions == null || questions.isEmpty()) {
                logger.error("No response from Groq API or response is empty.");
                resp.getWriter().write("{\"error\": \"No response from Groq API.\"}");
            }else {
            resp.getWriter().write(questions);
            }

        } catch (Exception e) {
           logger.error("Error generating questions via Groq API", e);

           resp.getWriter().write("{\"error\": \"Internal server error\"}");
        }

    }

    //

    //method for constructing prompt
    private String constructPrompt(String construct, String level, int n){
        return "You are an EFL teacher who teaches English to non-native school students " +
                "aged 10-18. Generate " + n + " grammar questions in a fill-in-the-blank format " +
                "with a CEFR level of " + level + " on the topic of " + construct +
                ". Ensure that the questions provide students with opportunities to practice the topic " +
                "from different perspectives and align with their level of knowledge. " +
                "Your response should be in the following JSON format: { \"topic\": \"TOPIC_HERE\", " +
                "\"questions\": [{\"question\": \"QUESTION_HERE\", \"answer\": \"ANSWER_HERE\"}] }";
    }

    // method for sending request to Groq and returning raw response
    private String sendRequestReturnRawResponse(HttpServletResponse resp, String APIkey, String requestBodyJson, int numFallback, Logger logger) throws IOException {

        if (numFallback == 0) {
            logger.error("Fallbacks have been exhausted");
            return "{\"error\": \"Fallbacks have been exhausted - failed to fetch response from Groq API\"}";
        }
        //convert to string
        String jsonRequestBody = gson.toJson(requestBodyJson);

        //create OkHttp request body
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonRequestBody);
        Request request = new Request.Builder()
                .url(GROQ_SERVICE_PATH)
                .header("Authorization","Bearer " + APIkey)
                .post(requestBody)
                .build();


        logger.info("Sending request to Groq API. Remaining fallbacks: " + numFallback);
        logger.debug("Request body: " + requestBodyJson);

        //Send response
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to fetch data from Groq API");
                return sendRequestReturnRawResponse(resp, groqAPIkey, requestBodyJson, numFallback -1, logger);
            }
            String model_response =response.body().string();
            //log response from LLM
            logger.info("Groq API Resp: " + model_response);
            return model_response;
        }


    }


}


