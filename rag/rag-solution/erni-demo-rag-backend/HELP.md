# INSTALLATION GUIDE:

## Prerequisites
* Docker
* Java IDE

## Please install LM Studio (https://lmstudio.ai/)
Then download the following models:
* text-embedding-nomic-embed-text-v2
* llama-3.2-3b-instruct

Then find the following file:
rag/rag-solution/docker-compose.yaml
and run
\
```docker compose up -d```

## POSSIBLE ISSUES AND FIXES:
the following issue:
***
"type": "dev.langchain4j.exception.InvalidRequestException",
"message": "400 Bad Request: \"{\"error\":\"Trying to keep the first 4506 tokens when context the overflows. However, the model is loaded with context length of only 4096 tokens, which is not enough. Try to load the model with a larger context length, or provide a shorter input\"}\""
***
can be resolved by going into lmstudio -> my models -> llms -> under actions click on settings icon ->set "context lenght" to about 8000
close lm studio and open again (refresh settings)