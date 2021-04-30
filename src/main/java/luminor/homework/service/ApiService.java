package luminor.homework.service;

import luminor.homework.HomeworkApplication;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

@Service
public class ApiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeworkApplication.class);
    private final String API_BASE_URL = "http://ip-api.com/json/";

    public String getRequestCountry(HttpServletRequest request) {
        if (request == null || request.getHeader("X-Forwarded-For") == null) {
            return "Unknown";
        }
        String url = API_BASE_URL + request.getHeader("X-Forwarded-For");
        try {
            InputStream input = new URL(url).openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String jsonString = readAll(reader);
            JSONObject json = new JSONObject(jsonString);
            if (json.getString("status").equals("success")
                    && json.getString("country") != null) {
                return json.getString("country");
            }
        } catch (IOException e) {
            LOGGER.error("Error parsing request country: " + e.getMessage());
        }
        return "Unknown";
    }

    private String readAll(Reader reader) throws IOException {
        StringBuilder builder = new StringBuilder();
        int cp;
        while ((cp = reader.read()) != -1) {
            builder.append((char) cp);
        }
        return builder.toString();
    }
}
