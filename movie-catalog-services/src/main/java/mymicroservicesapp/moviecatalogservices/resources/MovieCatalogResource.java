package mymicroservicesapp.moviecatalogservices.resources;


import com.netflix.discovery.DiscoveryClient;
import mymicroservicesapp.moviecatalogservices.model.CatalogItem;
import mymicroservicesapp.moviecatalogservices.model.Movie;
import mymicroservicesapp.moviecatalogservices.model.Rating;
import mymicroservicesapp.moviecatalogservices.model.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {


    @Autowired
    private RestTemplate restTemplate;


    @Autowired
    private WebClient.Builder webCilentBuilder;
    

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {

        List<Rating> ratings=restTemplate.getForObject("http://rating-data/ratingsdata/user/"+userId,
                UserRating.class).getRatings();

        WebClient.Builder builder = WebClient.builder();

//        List<Rating> ratings= Arrays.asList(
//                new Rating("1234", 4),
//                new Rating("5678", 3)
//        );

        return ratings.stream()
                .map(rating -> {
//                    Movie movie = restTemplate.getForObject("http://localhost:8082/movies/" + rating.getMovieId(), Movie.class);

                    Movie movie=webCilentBuilder.build()
                            .get()
                            .uri("http://movie-info/movies/" + rating.getMovieId())
                            .retrieve()
                            .bodyToMono(Movie.class)
                            .block();

                    return new CatalogItem(movie.getName(), movie.getDescription(), rating.getRating());
                })
                .collect(Collectors.toList());

    }

}
