# EDD Java and RAG Demo
## Objective
This is a demo project that accompanies my EDD hands-on
session and shows the incomplete solution.
You can find the hands-on exercise solutions under [codesnippets.md](./erni-demo-rag-backend/snippets/code_snippets.md)
This session shows how to connect an LLM with a vectorstore
to answer questions with your own knowledge

## Prerequisites
* Java 21 
* Git
* Node JS >18 installed, NPM installed
* Optional: Docker Desktop if you want to try it as a local setup (with pgvector and ollama)
* Optional: Ollama (a bit more performant than running it in a docker container)

## Setup
* extract the cv_data.zip from [Sharepoint folder](https://erniegh-my.sharepoint.com/:f:/g/personal/david_beisert_betterask_erni/Es-_6g4ai89Cj5LcHQT7T2kBiqaq9MD-5ApdtmRSW6PR2g?e=9j3nXx) 
  * to ``erni-demo-rag-backend/src/main/resources``
* copy the application.properties from from [Sharepoint folder](https://erniegh-my.sharepoint.com/:f:/g/personal/david_beisert_betterask_erni/Es-_6g4ai89Cj5LcHQT7T2kBiqaq9MD-5ApdtmRSW6PR2g?e=9j3nXx) 
  * also to ``erni-demo-rag-backend/src/main/resources``
* If you want to import data into your own vector store namespace
  * Change: application.properties, ``erni.vector-store.pinecone.namespace=bedv`` to your user name so you do not overwrite my data

## Setup with Codespace
Codespaces is an online IDE that starts a VS Code instance right in the browser with the project. It can be accessed when you are logged into github
and then can be opened from the repository
* Goto the github repo -> Code -> Codespaces -> Open main
* Copy the application.properties and cv_files.zip by uploading it to erni-rag-backend/src/main/resources folder
* Open terminal
* in the terminal execute ``cd erni-demo-rag-backend/src/main/resources``
* run in erni-rag-backend/src/main/resources the command ``jar -xvf cv_data.zip .``
* In erni-rag-backend/ run ``mvn clean install spring-boot:run``
* Accept the port forwarding, open in browser, copy the url
* Make the port of the backend public so the frontend can access it. (Ports tab in vs code)
* Run the UI frontend like in Run frontend
* Accept that the port is opened, open the url
* edit the environment.json, add the url of the backend there

## Run backend 
* Go to backend: ``cd erni-demo-rag-backend``
* In the terminal execute:  ``./mvnw clean install spring-boot:run``
  * or just run the Main class ``ErniDemoRagApplication`` from the IDE run menu
* You can access the swagger ui on: http://localhost:8080/swagger

## Run frontend
* go to ui:  In the terminal: ``cd erni-demo-rag-ui``
* in the terminal execute ``npm install``
* in the terminal execute ``npm start``
* You can access the UI on: http://localhost:4200/chat
