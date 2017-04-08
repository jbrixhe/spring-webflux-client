[![Build Status](https://travis-ci.org/jgsqware/projectAlpha.svg?branch=master)](https://travis-ci.org/jbrixhe/projectAlpha)

# Reactive-Client
The goal of this library is to ease the use of latest Spring Reactive library [Spring-Webflux](http://docs.spring.io/spring-framework/docs/5.0.0.M1/spring-framework-reference/html/web-reactive.html) that can be a bit verbose sometimes.


## Comparison
#### Using Reactive-client:

```java
public interface AccountClient {
	@GetMapping(value = "/accounts/{id}", consumes = APPLICATION_JSON_VALUE)
	Mono<Account> getAccount(@PathVariable("id") Integer id);
}
...
AccountClient accountClient = return ClientBuilder
	.defaults(HelloClient.class, "http://example.com");
```

#### Using Spring-webflux:
This is the code you have to write in order to achieve the same result as in the previous example.

```java
public class AccountClient {
	public Mono<Account> getAccount(Integer id) {
	    WebClient client = WebClient.create("http://example.com");
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
public class TokenRequestInterceptor implements RequestInterceptor {
    @Override public void accept(ReactiveRequest request) {
    	request.addHeader("x-token", "encoded-token");
    }
}
...
AccountClient accountClient = return ClientBuilder
    					.builder()
    					.requestInterceptor(new TokenRequestInterceptor())
					    .build(HelloClient.class, "http://example.com");
```
