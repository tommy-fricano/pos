package com.pos.pos.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pos.pos.models.Basket;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class HttpClientService {

    private final CloseableHttpClient httpClient;

    ObjectMapper objectMapper = new ObjectMapper();

    @Value("${discountservice.url}")
    private String discountServiceUrl;

    public Basket sendRequestToDiscountService(Basket basket)  {
        HttpPost request = new HttpPost(discountServiceUrl);
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Accept", "*");
        try{
            request.setEntity(new StringEntity(objectMapper.writeValueAsString(basket)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity http = response.getEntity();
            if(http != null) {
                try (InputStream inputStream = http.getContent()) {
                    return objectMapper.readValue(inputStream, Basket.class);
                }
                catch (IOException e) {
                    throw new RuntimeException("Error reading object", e);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new Basket();
    }

}
