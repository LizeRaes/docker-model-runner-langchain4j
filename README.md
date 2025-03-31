# Minimal LangChain4j Chat Server

A bare-bones, framework-free chat application built with vanilla Java and LangChain4j. Perfect as a starting point for experimenting with LLM applications.

## Features

- **Zero Framework**: Uses only Java's built-in HTTP server
- **LangChain4j Integration**: Simple OpenAI chat model integration
- **Session Management**: Maintains separate chat histories for different users
- **Memory Management**: Keeps last 15 messages per session
- **Pure Frontend**: Basic HTML + vanilla JavaScript, no frameworks
- **Minimal Dependencies**: Just LangChain4j and Jackson for JSON handling
- **Build Flexibility**: Choose between Maven or Gradle

## Prerequisites

- Java 17 or later
- Maven (or Gradle if you prefer)
- OpenAI API key

## Quick Start

First, set your OpenAI API key:
```bash
export OPENAI_API_KEY=your-api-key-here
```

Then build and run using either Maven or Gradle:

### Maven (Default)
```bash
mvn exec:java
```

### Gradle (Alternative)
```bash
gradle run
```

Finally, open `http://localhost:8080` in your browser

## Structure

```
.
├── LICENSE                # MIT License
├── README.md             # This file
├── build.gradle          # Gradle build (optional)
├── pom.xml               # Maven build (default)
└── src/
    └── main/
        ├── java/
        │   └── engineering/
        │       └── epic/
        │           └── ChatServer.java  # All server code
        └── resources/
            └── static/
                └── index.html          # Frontend code
```

## Extending

This project is intentionally minimal to serve as a base for experimentation. Some ideas to extend it:

- Add streaming responses
- Implement different chat memory strategies
- Add authentication
- Use different LLM providers
- Add prompt templates
- Implement chat history persistence
- Add system prompts configuration

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.