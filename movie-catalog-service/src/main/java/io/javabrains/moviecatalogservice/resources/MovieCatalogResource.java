package io.javabrains.moviecatalogservice.resources;


import io.javabrains.moviecatalogservice.models.CatalogItem;
import io.javabrains.moviecatalogservice.models.Movie;
import io.javabrains.moviecatalogservice.models.Rating;
import io.javabrains.moviecatalogservice.models.UserRating;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/catalog")
public class MovieCatalogResource {

    private final RestTemplate restTemplate;
    private final WebClient.Builder webClientBuilder;

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {

//        List<Rating> ratingList = Arrays.asList(
//                new Rating("id1", 7),
//                new Rating("id2", 1)
//        );


        UserRating userRating = restTemplate.getForObject("http://ratings-data-service/ratingsdata/user/" + userId, UserRating.class);

        return userRating.getRatings().stream()
                .map(rating -> {
                    ResponseEntity<Movie> forEntity = restTemplate.getForEntity("http://move-info-service/movies/" + rating.getMovieId(), Movie.class);
                    Movie movie = forEntity.getBody();

//                    Movie movie = webClientBuilder.build()
//                            .get()
//                            .uri("http://localhost:8082/movies/" + rating.getMovieId())
//                            .retrieve()
//                            .bodyToMono(Movie.class)
//                            .block();

                    return new CatalogItem(movie.getName(), "Test Desc", rating.getRating());
                })
                .collect(Collectors.toList());
    }
}
