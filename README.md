# Cloud Gateway

## Overview

This project configures a **Spring Cloud Gateway** that routes and secures microservices. The setup includes **Spring Cloud Admin** for centralized monitoring and management, and a **Profile Microservice**. Two endpoints of the Profile Microservice are publicly accessible, and performance can be benchmarked using the provided script.

## Microservices

Start the microservices locally (optimized for Linux environments):

1. **Spring Cloud Admin**: [http://localhost:1111](http://localhost:1111)
   - Provides centralized monitoring and management of services.
   - Change log levels dynamically using the admin interface.
   
2. **Spring Cloud Gateway**: [http://localhost:8080](http://localhost:8080)
   - Protects the **Profile Microservice**.
   - Configures routing and security for microservices.
   
3. **Profile Microservice**: [http://localhost:2050](http://localhost:2050)
   - Provides user profile management.
   - Two endpoints are publicly accessible, with the rest secured by the gateway.

## Benchmarking

To benchmark the public endpoints of the Profile Microservice, run the provided script:

```bash
./benchmark.sh
```

## Usage

- **Spring Cloud Gateway** protects the Profile Microservice, allowing only authorized access to its secured endpoints.
- **Spring Cloud Admin** allows you to dynamically adjust log levels of services, helping with debugging and performance tuning.

