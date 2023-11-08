package academy.devdojo.springboot2.client;

import academy.devdojo.springboot2.domain.Anime;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Log4j2
public class SpringClient {
  public static void main(String[] args) {
//    ResponseEntity<Anime> entity = new RestTemplate().getForEntity("http://localhost:8080/animes/1", Anime.class);
    Anime entity = new RestTemplate().getForObject("http://localhost:8080/animes/1", Anime.class);

    log.info(entity);

    ResponseEntity<List<Anime>> animes = new RestTemplate().exchange(
            "http://localhost:8080/animes/all",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<>() {}
    );

    log.info(animes);

//    Anime kingdom = Anime.builder().name("Kingdom").build();
//    Anime kingdomSaved = new RestTemplate().postForObject("http://localhost:8080/animes", kingdom, Anime.class);
//    log.info("saved anime {}", kingdomSaved);

    Anime samuraiChamploo = Anime.builder().name("Samurai Champloo").build();
    ResponseEntity<Anime> samuraiChamplooSaved = new RestTemplate().exchange(
            "http://localhost:8080/animes",
            HttpMethod.POST,
            new HttpEntity<>(samuraiChamploo, createJsonHeader()),
            Anime.class
    );

    log.info("saved anime {}", samuraiChamplooSaved);

    Anime animeToBeUpdated = samuraiChamplooSaved.getBody();
    animeToBeUpdated.setName("Samurai Champloo 2");

    ResponseEntity<Void> samuraiChamplooUpdated = new RestTemplate().exchange(
            "http://localhost:8080/animes",
            HttpMethod.PUT,
            new HttpEntity<>(animeToBeUpdated, createJsonHeader()),
            Void.class
    );

    log.info(samuraiChamplooUpdated);

    ResponseEntity<Void> samuraiChamplooDeleted = new RestTemplate().exchange(
            "http://localhost:8080/animes/{id}",
            HttpMethod.DELETE,
            null,
            Void.class,
            animeToBeUpdated.getId()
    );

    log.info(samuraiChamplooDeleted);
  }

  private static HttpHeaders createJsonHeader() {
    HttpHeaders httpHeaders = new HttpHeaders();

    httpHeaders.setContentType(MediaType.APPLICATION_JSON);

    return httpHeaders;
  }
}
