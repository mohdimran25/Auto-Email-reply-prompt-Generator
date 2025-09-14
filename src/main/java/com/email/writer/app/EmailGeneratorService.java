package com.email.writer.app;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EmailGeneratorService {
	
	private final WebClient webclient;
	
	public EmailGeneratorService(WebClient.Builder webclientBuilder) {
		this.webclient = webclientBuilder.build();
	}
	
	
	
	@Value("${gemini.api.url}")
	private String geminiApiUrl;
	@Value("${gemini.api.key}")
	private String geminiApiKey;
	
	public String generateEmailReply(EmailRequest emailRequest) {
		
		// build a prompt
		String prompt = buildPrompt(emailRequest);
		// craft a request
		Map<String, Object> requestBody = Map.of(
				"contents", new Object[] {
					Map.of("parts", new Object[] {
							Map.of("text", prompt)
					})	
				}
		);
		// make a request and generate a response
		
		String response = webclient.post()
				.uri(geminiApiUrl)
				.header("Content-Type", "application/json")
				.header("X-goog-api-key", geminiApiKey)
				.bodyValue(requestBody)
				.retrieve()
				.bodyToMono(String.class)
				.block();
		
		// extract the response and return the response
		
		return extractResponse(response);
	}
	
	private String extractResponse(String response) {
		
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode root = objectMapper.readTree(response);
			
			return root.path("candidates")
					.get(0)
					.path("content")
					.path("parts")
					.get(0)
					.path("text")
					.asText();
			
		} catch (Exception e) {
			return "Error during extracting response: " + e.getMessage();
		}
		
	}

	public String buildPrompt(EmailRequest emailRequest) {
		StringBuilder prompt = new StringBuilder();
		
		prompt.append("Generate a professional email reply for the following email content. Avoid generating a subject line. ");
		
		if(emailRequest.getTone() != null && !emailRequest.getTone().isEmpty()) {
			prompt.append("Use a ").append(emailRequest.getTone()).append(" tone. ");
		}
		prompt.append("\n Original Mail : \n").append(emailRequest.getEmailContent());
		
		return prompt.toString();
	}
}
