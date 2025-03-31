package engineering.epic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ChatLanguageModel chatModel = OpenAiChatModel.builder()
            .apiKey("not needed")
            .baseUrl("http://localhost:12434/engines/llama.cpp/v1")
            .modelName("ai/gemma3")
            .build();
    private static final Map<String, MessageWindowChatMemory> chatMemories = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        // Handle root path - serve index.html
        server.createContext("/", exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                String path = exchange.getRequestURI().getPath();
                if (path.equals("/")) {
                    path = "/index.html";
                }
                
                URL resource = ChatServer.class.getResource("/static" + path);
                if (resource != null) {
                    try (InputStream is = resource.openStream()) {
                        byte[] content = is.readAllBytes();
                        String contentType = path.endsWith(".html") ? "text/html" : 
                                          path.endsWith(".css") ? "text/css" : 
                                          path.endsWith(".js") ? "application/javascript" : 
                                          "application/octet-stream";
                        
                        exchange.getResponseHeaders().set("Content-Type", contentType);
                        exchange.sendResponseHeaders(200, content.length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(content);
                        }
                    }
                } else {
                    String response = "404 Not Found";
                    exchange.sendResponseHeaders(404, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                    }
                }
            } else {
                exchange.sendResponseHeaders(405, 0);
            }
            exchange.close();
        });

        // Handle chat endpoint
        server.createContext("/chat", exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    ChatRequest request = objectMapper.readValue(requestBody, ChatRequest.class);
                    
                    // Get or create chat memory for this session
                    MessageWindowChatMemory chatMemory = chatMemories.computeIfAbsent(
                        request.sessionId(),
                        k -> MessageWindowChatMemory.withMaxMessages(15)
                    );
                    
                    // Add user message to memory
                    chatMemory.add(UserMessage.from(request.message()));
                    
                    // Generate response with memory context
                    AiMessage aiMessage = chatModel.generate(chatMemory.messages()).content();
                    
                    // Add assistant response to memory
                    chatMemory.add(aiMessage);
                    
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = exchange.getResponseBody()) {
                        String jsonResponse = objectMapper.writeValueAsString(new ChatResponse(aiMessage.text()));
                        os.write(jsonResponse.getBytes(StandardCharsets.UTF_8));
                    }
                } catch (Exception e) {
                    String error = objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(500, 0);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(error.getBytes(StandardCharsets.UTF_8));
                    }
                }
            } else {
                exchange.sendResponseHeaders(405, 0);
            }
            exchange.close();
        });

        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port 8080");
        System.out.println("Press Ctrl+C to stop the server");
    }

    record ChatRequest(String message, String sessionId) {}
    record ChatResponse(String response) {}
    record ErrorResponse(String error) {}
} 