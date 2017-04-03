# Reactive-Client
The goald of this library is to ease the use of Spring latest Reactive library Spring-Webflux that can be sometime a bit verbose.


## Comparison
#### Using Reactive-client:

```java
public inerface AccountClient {
	@GetMapping(value = "/accounts/{id}", consumes = APPLICATION_JSON_VALUE)
	Mono<Account> getAccount(@PathVariable("id") Integer id);
}
...
AccountClient accountClient = return ReactiveClientBuilder
	.create(HelloClient.class, "http://example.com");
```

#### Using Spring-webflux:
This is the code you have to write in order to achieve the same result as in the previus example.

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
Static headers can be add to the request using Spring mappring annotations : 
* @RequestMapping
* @GetMapping
* @PostMapping
* @PutMapping
* @DeleteMapping
* @PatchMapping
```java
@RequestMapping(headers = {"x-api-token=static-token"})
```

Dinamic headers can be added to the request using a parameter on the method

```java
@GetMapping("/accounts/{id}")
Mono<Account> getAccount(@PathVariable("id") Integer id, @RequestHeader("x-api-token") String token);
```

### Request parameters
Configure request parameters on the request is achieve with the Spring annotation @RequestParam
```java
@GetMapping(value = "/accounts")
Flux<Account> getAccounts(@RequestParam("limit") Integer limit);
```
You don't need to specify anything in the uri. When the annotation is process the parameter will automaticly added to the request path, this will the the URI generate : ```/accounts?limit={limit}```

### Request body
There is two way to configure the request body of the request.

With the Spring annotation @RequestBody :
```java
@PostMapping(value = "/accounts")
Flux<Account> createAccounts(@RequestBody Account newAccount);
```
Without annotation. Be careful, you can only have one parameter without annation on each method.
```java
@PostMapping(value = "/accounts")
Flux<Account> createAccounts(Account newAccount);
```

### Request interceptor
You can configure request interceptors on every Client. These interceptors will be call on every request created by the client.

```java
public class TokenRequestInterceptor implements Consumer<ReactiveRequest> {
	@Override public void accept(ReactiveRequest request) {
    	request.addHeader("x-token", "encoded-token");
    }
}
...
AccountClient accountClient = return ReactiveClientBuilder
	.builder()
    .requestInterceptor(new TokenRequestInterceptor())
	.build(HelloClient.class, "http://example.com");
```