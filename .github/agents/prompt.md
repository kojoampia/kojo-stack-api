Project Prompt: Kojo.Stack (Enterprise DevOps Portfolio)
Role & Objective
Act as a Senior Fullstack Architect. Your task is to scaffold a robust, enterprise-grade web application named "Kojo.Stack" using api.java from the project folder.
Concept: This is not a standard portfolio website. It is a "Consultant Portfolio designed to emulate an Enterprise DevOps Command Center." It must look and feel like a monitoring tool (e.g., Grafana, Datadog) where the "systems" being monitored are the candidate's professional history, skills, and projects.
Technical Stack Requirements
Backend (The Engine)
Framework: Spring Boot 4 (Java 21 LTS).
Database: MongoDB (using Spring Data MongoDB).
Messaging: Apache Kafka (using Spring Kafka).
Real-time: Spring WebSocket (STOMP protocol) for pushing updates to the frontend.
Security: Spring Security (JWT based).
Build Tool: Maven.
Data Source: MongoDB collection experiences.
Backend Logic: The REST controller accepts the payload -> Publishes a ContractInitializedEvent to a Kafka Topic (engagement-events).
Kafka Consumer: Consumes the event -> Saves a new "Pending" project to MongoDB -> Triggers a WebSocket message to the /topic/deployments channel.
Frontend: The "Projects" view listens to this channel and auto-updates the UI with the new "Pending" system.
Data Model Specifications
Experience Document:
{
  "company": "String",
  "role": "String",
  "period": "String",
  "status": "
        ACTIVE, 
        COMPLETED, 
        ON_HOLD, 
        CANCELLED, 
        PLANNED, 
        ARCHIVED, 
        ONGOING, 
        EXPIRED, 
        TERMINATED, 
        SUSPENDED,
        PENDING,
        REJECTED,
        APPROVED,
        IN_PROGRESS,
        FINISHED,
        CANCELED,
        WITHDRAWN,
        DEPRECATED,
        RESOLVED,
        CLOSED,
        RETIRED,
        OPEN",
  "techStack": ["String"],
  "metrics": [{"label": "String", "value": "String"}]
}


Project Document:
{
  "name": "String",
  "client": "String",
  "type": "Microservices | DevOps | Migration | ETL",
  "status": "Live | Pending | Maintenance | Consulting",
  "description": "String"
}


Execution Steps
Generate the Spring Boot project structure with necessary dependencies (Web, Data MongoDB, Kafka, WebSocket, Lombok).
Create the MongoDB repository interfaces and Data Models.
Implement the Kafka Producer/Consumer configuration for the "Engagement" flow.
Implement the WebSocket Service in Angular using RxJS/Signals to listen for updates.
