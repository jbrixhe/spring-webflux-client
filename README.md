[![Build Status](https://travis-ci.org/jgsqware/projectAlpha.svg?branch=master)](https://travis-ci.org/jbrixhe/projectAlpha)

# Reactive-Client
The goal of this library is to ease the use of latest Spring Reactive library [Spring-Webflux](http://docs.spring.io/spring/docs/5.0.0.RC1/spring-framework-reference/web.html#web-reactive) that can be a bit verbose sometimes.


## Comparison
#### Using Reactive-client:

```java
public interface AccountClient {
    @GetMapping(value = "/accounts/{id}", consumes = APPLICATION_JSON_VALUE)
    Mono<Account> getAccount(@PathVariable("id") Integer id);
}
...
AccountClient accountClient = return ClientBuilder
	.defaults(AccountClient.class, "http://example.com");
```

#### Using Spring-webflux:
This is the code you have to write in order to achieve the same result as in the previous example.

```java
public class AccountClient {
    private WebClient client = WebClient.create("http://example.com");
    
    public Mono<Account> getAccount(Integer id) {
        return client.get()
            .url("/accounts/{id}", id)
            .accept(APPLICATION_JSON)
            .exchange(request)
            .then(response -> response.bodyToMono(Account.class));
    }
}
```

## Features
### Request headers
Static headers can be added to the request using Spring mapping annotations: 
* @RequestMapping
* @GetMapping
* @PostMapping
* @PutMapping
* @DeleteMapping
* @PatchMapping

```java
@RequestMapping(headers = {"x-api-token=static-token"})
```

Dynamic headers can be added to the request using a parameter on the method

```java
@GetMapping("/accounts/{id}")
Mono<Account> getAccount(@PathVariable("id") Integer id, @RequestHeader("x-api-token") String token);
```

### Request parameters
Spring annotation @RequestParam configure the request parameters on the request
```java
@GetMapping(value = "/accounts")
Flux<Account> getAccounts(@RequestParam("limit") Integer limit);
```

You don't need to specify anything in the uri. When the annotation is processed, request parameters will be automatically added to the request path, this will generate this URI: ```/accounts?limit={limit}```

### Request body
Request body configuration:

1. With the Spring annotation @RequestBody :
```java
@PostMapping(value = "/accounts")
Flux<Account> createAccounts(@RequestBody Account newAccount);
```

2. Without annotation. Be careful, you can only have one parameter without annation on each method.
```java
@PostMapping(value = "/accounts")
Flux<Account> createAccounts(Account newAccount);
```

### Request interceptor
You can configure request interceptors on every Client. These interceptors will be called on every request created by the client.

```java
public class ContentTypeRequestInterceptor implements RequestInterceptor {
    @Override public ClientRequest accept(ClientRequest clientRequest) {
        return ClientRequest.from(clientRequest)
                            .headers(headers -> headers
                                .set(HttpHeaders.CONTENT_TYPE, "application/json"))
                            .build();
    }
}
...
AccountClient accountClient = return ClientBuilder
    .builder()
    .requestInterceptor(new ContentTypeRequestInterceptor())
    .build(HelloClient.class, "http://example.com");
```

### Codecs
There is 3 kinds of codecs you can configure within the ClientBuilder: 
* HttpMessageWriter 
* HttpMessageReader 
* HttpErrorReader

HttpMessageWriter and HttpMessageReader standard codecs used within the Spring-webflux.  
 

1. HttpErrorReader 

 These readers are used when the response status code is 4xx or 5xx so you can deserialize custom error payload.

 By default there is 2 HttpErrorReader that will process the response and wrap the body as String. 
* Statuses 5xx response will be wrap in a HttpServerException
* Statuses 4xx response will be wrap in a HttpServerException


2. Override default codecs.

During the building phase of your client you can configure some of the default codec decoders used by the client. 
```java
ClientBuilder.builder()
    .defaultCodecs(defaultCodecsConfigurerConsumer -> {
        defaultCodecsConfigurerConsumer.jackson2Encoder(new CustomJackson2JsonEncoder());
        defaultCodecsConfigurerConsumer.jackson2Decoder(new CustomJackson2JsonDecoder());
        defaultCodecsConfigurerConsumer.serverSentEventDecoder(new CustomDecoder());
        defaultCodecsConfigurerConsumer.clientErrorDecoder(new CustomClientErrorDecoder());
        defaultCodecsConfigurerConsumer.serverErrorDecoder(new CustomServerErrorDecoder);
    })
    .build(HelloClient.class, "http://example.com");
```

3. Configure custom codecs.

 During the building phase of your client you can configure add custom codecs to fit your specifics needs.

3.1 HttpMessageWriter, HttpMessageReader and HttpErrorReader  
```java
ClientBuilder.builder()
    .customCodecs(customCodecs -> {
        customCodecs.reader(new CustomHttpMessageReader());
        customCodecs.writer(new CustomHttpMessageWriter());
        customCodecs.errorReader(new CustomHttpErrorReader());
    })
    .build(HelloClient.class, "http://example.com");
```

3.2 Encoder, Decoder and ErrorDecoder 
 
 ```java
 ClientBuilder.builder()
     .customCodecs(customCodecs -> {
         customCodecs.decoder(new CustomDecoder());
         customCodecs.encoder(new CustomEncoder());
         customCodecs.errorDecoder(new CustomErrorDecoder());
     })
     .build(HelloClient.class, "http://example.com");
 ```
