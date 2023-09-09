package br.com.medeirosecia.analyzemail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.com.medeirosecia.analyzemail.domain.service.gmail.AnalyzeGmailInbox;

@SpringBootApplication
public class AnalyzemailApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnalyzemailApplication.class, args);
		new AnalyzeGmailInbox();

	}

}
