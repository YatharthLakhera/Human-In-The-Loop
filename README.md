# Human-in-the-Loop Approval System

A robust, event-driven orchestration system designed to manage human-in-the-loop approvals in agent-based workflows.

## Problem Statement

Modern agent systems often require human approvals or feedback before executing critical actions such as purchases, deployments, or contract signings. This system provides a flexible, configurable solution for managing these approval workflows asynchronously across multiple channels.

## Key Features

- **Multi-channel Approval System**: Supports various communication channels (Email, Slack, etc.)
- **Event-Driven Architecture**: Asynchronous processing of approvals and responses
- **State Management**: Tracks approval status and workflow state
- **Dynamic UI**: Configurable approval interfaces
- **Retry & Timeout Handling**: Built-in mechanisms for handling delays and failures

## Architecture

```mermaid
graph TD
    A[Agent System] -->|Request Approval| B[Approval Service]
    B -->|Create Workflow| C[(Database)]
    B -->|Send Approval| D[Notification Service]
    D -->|Email| E[Email Channel]
    D -->|Slack| F[Slack Channel]
    E -->|User Action| G[Callback Endpoint]
    F -->|User Action| G
    G -->|Update Status| B
    B -->|Approval/Rejection| A
```

### Components

- Approval Service: Core service managing approval workflows  
- Notification Service: Handles multi-channel notifications  
- Database: Stores workflow state and configurations  
- API Gateway: Entry point for all external communications  
- Frontend: Dynamic UI Template management(In-progress) 

## Getting Started

### Prerequisites

- Java 17+
- MySQL 8.0+
- Maven 3.8+
- AWS Account (for SQS)

### Environment Setup

```
AWS_ACCESS_KEY=
AWS_ACCOUNT_ID=
AWS_REGION=
AWS_SECRET_KEY=
AWS_SQS_REGION=
DB_PASSWORD=
EMAIL_ADDRESS_FROM=
MAILGUN_API_KEY=
MAILGUN_DOMAIN=
```

CLI Arguments -> `--spring.profiles.active=local`
DB Schema Creation -> `source db_schema.sql` [SQL File](https://github.com/YatharthLakhera/Human-In-The-Loop/blob/main/src/main/resources/db_schema.sql)

## API Documentation

Access the Swagger UI at:  
`http://localhost:8080/swagger-ui.html`

Basic UI For Workflow templates configurations - `http://localhost:8080`

<img width="1432" height="699" alt="Screenshot 2025-10-16 at 2 16 52 PM" src="https://github.com/user-attachments/assets/b55c70a2-f493-4b7e-87b2-e63fc37faa35" />
<img width="1423" height="526" alt="Screenshot 2025-10-16 at 3 12 30 PM" src="https://github.com/user-attachments/assets/7dc6902f-5cab-405a-9bcd-df9a512b188c" />
<img width="1414" height="466" alt="Screenshot 2025-10-16 at 3 12 12 PM" src="https://github.com/user-attachments/assets/cb9d7bc8-ba14-45c0-9f24-4aeedfac968a" />
