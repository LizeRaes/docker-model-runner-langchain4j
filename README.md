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
