package com.lyzr.human_in_the_loop.client.communication;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class MailGunClient {

    @Value("${email.address.from}")
    private String emailFrom;

    private final String apiKey;
    private final String domain;
    private final String baseUrl;
    private final OkHttpClient client;

    public MailGunClient(String apiKey, String domain, String baseUrl) {
        this.apiKey = apiKey;
        this.domain = domain;
        this.baseUrl = baseUrl;
        this.client = new OkHttpClient();
    }

    public void sendEmail(String userName, String email, String subject, String body) throws IOException {
        // Create request body
        RequestBody requestBody = new FormBody.Builder()
                .add("from", emailFrom)
                .add("to", userName + " <" + email + ">")
                .add("subject", subject)
                .add("text", body)
                .build();
        // Build the request with Basic Authentication and URL
        String apiEndpoint = baseUrl + "/v3/" + domain + "/messages";
        Request request = new Request.Builder()
                .url(apiEndpoint)
                .header("Authorization", Credentials.basic("api", apiKey))
                .post(requestBody)
                .build();

        // Execute the request and get the response
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            // Print the response body
            System.out.println(response.body().string());
        }

    }
}
