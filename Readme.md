# CloudBees Train Ticket Booking API

## Overview

The **CloudBees Train Ticket Booking API** is a Spring Boot-based application that provides RESTful APIs to manage train ticket bookings, users, and ticket information. This application demonstrates the use of various Spring features such as MVC architecture, global exception handling, unit testing, and design patterns like Service and Repository.

## Architecture Overview

The application follows a layered architecture consisting of the following layers:
1. **Controller Layer**: Handles HTTP requests and responses.
2. **Service Layer**: Contains business logic and data transformation.
3. **Repository Layer**: Handles data persistence using Spring Data JPA.
4. **Model Layer**: Represents the data structure used across the application.
5. **DTO (Data Transfer Object)**: Transforms entity data for controller responses.

## Design Patterns Used

### 1. Service Pattern
The service pattern is implemented to separate business logic from the controller logic.

### 2. Repository Pattern
The repository pattern is used to abstract the data layer and provide CRUD operations without exposing the details of data management.

### 3. DTO Pattern
The DTO pattern is implemented using the `TicketDTO` class to transform entity data for external representation. It is used to prevent exposing internal data models and to provide a simplified structure for API responses.

### 4. Singleton Pattern
The `@Service` and `@Repository` components are singletons by nature, ensuring that only one instance of each is created throughout the application’s lifecycle.

## Global Exception Handling

The application implements global exception handling using `@ControllerAdvice` and custom exceptions:

1. **Custom Exceptions**:
   - `UserNotFoundException`: Thrown when a user is not found in the database.
   - `TicketNotFoundException`: Thrown when a ticket is not found for a given user.

2. **Global Exception Handler**:
   The `GlobalExceptionHandler` class handles these exceptions and returns meaningful error messages with appropriate HTTP status codes such as 404 (Not Found) and 400 (Bad Request).

## Unit Testing

The application uses JUnit and Mockito for unit testing. Both the controller and service layers have dedicated test cases to validate their functionality.

### Controller Unit Tests
The `BookingControllerTest` class uses `MockMvc` to mock HTTP requests and responses for the controller methods. It verifies status codes, response content, and service method invocations.

### Service Unit Tests
The `BookingServiceImplTest` class uses Mockito to mock dependencies like `UserRepository` and `TicketRepository`. It tests the business logic by simulating various scenarios, such as successful operations and exception cases.


## The following drive link contains screenshots of the API responses