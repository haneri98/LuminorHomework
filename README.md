### Luminor Application Homework  
By Hans Erik

Base URL of API: http://localhost:8080/api/

Endpoints:
* GET http://localhost:8080/api/payments
* POST http://localhost:8080/api/payments
* POST http://localhost:8080/api/payment-files

Swagger is implemented at: http://localhost:8080/api/swagger-ui/  
However, I recommend something like Postman for hand testing uploading CSV files or setting headers.

The program only accepts valid IBANs, so one can use something like http://randomiban.com/ for testing.

This Spring application uses Java 11.