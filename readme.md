This project demo how to decode encoded request parameter and request body with spring mvc.
====
 
 
 ```
 ./gradlew bootRun
 ```
 
 and go localhost:8080.
 
 You can send base64 encoded request parameter or request body, and the application will respond the decoded data. 
 
 
 * The request parameter is decoded by `RequestDecryptFilter` and `Base64RequestWrapper`
 * The request body is decoded by `JsonDecryptBase64MessageConverter`