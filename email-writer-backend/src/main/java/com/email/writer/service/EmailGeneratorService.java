package com.email.writer.service;

import com.email.writer.model.EmailRequest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EmailGeneratorService {

    private final OllamaChatModel chatModel;

    public EmailGeneratorService(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String generateEmailReply(EmailRequest emailRequest) {
        try {
            String promptText = buildPrompt(emailRequest);
            System.out.println("promptText-----> " + promptText);
            Prompt prompt = new Prompt(promptText);

            ChatResponse response = chatModel.call(prompt);
            //return response.getResult().getOutput().getText();
            return removeThinkSection(response.getResult().getOutput().getText());
        } catch (Exception e) {
            return ("Failed to generate email reply "+ e);
        }
    }


    /*
    // For streaming responses
    public Flux<String> generateEmailReplyStream(EmailRequest emailRequest) {
        String promptText = buildPrompt(emailRequest);
        Prompt prompt = new Prompt(promptText);

        return chatModel.stream(prompt)
                .map(chatResponse -> chatResponse.getResult().getOutput().getText());
    }
     */

    private static String getPromptTemplate() {
        String strEmailtemplate = "You are a professional email assistant. Generate a concise, formal reply to the user's email below. Follow these rules:\n" +
                "1. Acknowledge the sender’s key request.\n" +
                "2. Address all explicit questions or concerns.\n" +
                "3. Use a polite tone with proper formatting (greeting, body, closing).\n" +
                "4. Avoid slang, emojis, or markdown.\n" +
                "5. Do not generate any subject line\n" +
                " Example of a good reply:\n" +
                "Dear [Name], \n " +
                " Thank you for your email. [Acknowledge request]. We will [action] and notify you once completed.\n" +
                " Best regards, \n " +
                "[Your Name]\n";
        return strEmailtemplate;
    }

        private String buildPrompt(EmailRequest emailRequest){

        StringBuilder prompt = new StringBuilder();
        //prompt.append("Generate a email reply for the following email content. Never include a subject line. ");
        //prompt.append("Always start mail with greeting and add one blank line after greeting. ");
            String strTemplate = getPromptTemplate();
            prompt.append(strTemplate);
            prompt.append("Now reply to the below email\n");
            if(emailRequest.getTone() != null && !emailRequest.getTone().isEmpty()){
            System.out.println("tone-----> " + emailRequest.getTone());
            prompt.append("Use a ").append(emailRequest.getTone()).append(" tone.");
        }
        prompt.append("\nOriginal email: \n").append(emailRequest.getEmailContent());
        return prompt.toString();
    }

    public static String removeThinkSection(String input) {
        // Regex pattern to match <think>...</think> (including multiline content)
        Pattern pattern = Pattern.compile("<think>(.*?)</think>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(input);

        // Replace the matched section with an empty string
        String result = matcher.replaceAll("").trim();

        return result;
    }

}