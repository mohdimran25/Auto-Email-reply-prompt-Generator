package com.email.writer.app;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/email")
@AllArgsConstructor
public class EmailGeneratorController {
	
	public final EmailGeneratorService emailGeneratorService;
	
	@PostMapping("/generate")
	public ResponseEntity<String> generateEmail(@RequestBody EmailRequest emailrequest) {
		
		String response = emailGeneratorService.generateEmailReply(emailrequest);
		
		return ResponseEntity.ok(response);
	}
}
