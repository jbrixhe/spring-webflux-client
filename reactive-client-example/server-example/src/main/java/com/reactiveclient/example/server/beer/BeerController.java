package com.reactiveclient.example.server.beer;

import com.reactiveclient.example.server.DuplicateResourceException;
import com.reactiveclient.example.server.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/beers")
public class BeerController {

    private BeerService beerService;

    @Autowired
    public BeerController(BeerService beerService) {
        this.beerService = beerService;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{code}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<Beer> getBeer(@PathVariable("code") String code) {
        return beerService.getBeer(code);
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_EVENT_STREAM_VALUE})
    public Flux<Beer> getMessages() {
        return beerService.getBeers();
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<Beer> addBeer(@RequestBody Mono<Beer> newBeer) {
        return beerService.addBeer(newBeer);
    }

    @PutMapping(path = "/{code}", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<Beer> updateMessage(@PathVariable("code") String code, @Valid Mono<Beer> newMessage) {
        return beerService.updateBeer(code, newMessage);
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public Mono<String> handleResourceNotFoundException(ResourceNotFoundException e) {
        return Mono.just(e.getMessage());
    }

    @ResponseStatus(value = HttpStatus.CONFLICT)
    @ExceptionHandler
    public Mono<String> handleDuplicateResourceException(DuplicateResourceException e) {
        return Mono.just(e.getMessage());
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public Mono<List<ObjectError>> handleException(WebExchangeBindException e) {
        return Mono.just(e.getAllErrors());
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public Mono<String> handleException(Exception e) {
        return Mono.just(e.getMessage());
    }
}
