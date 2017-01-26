package com.reactiveclient.test;

import com.reactiveclient.ReactiveClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ReactiveClient(url = "http://localhost:8080")
public interface HelloClient extends HelloRessource {

}
