package academy.devdojo.springboot2.integration;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.repository.AnimeRepository;
import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.util.AnimeCreator;
import academy.devdojo.springboot2.util.AnimePostRequestBodyCreator;
import academy.devdojo.springboot2.wrapper.PageableResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AnimeControlllerIT {
  @Autowired
  private TestRestTemplate testRestTemplate;
  @LocalServerPort
  private int port;
  @Autowired
  private AnimeRepository animeRepository;

  @Test
  @DisplayName("List returns list of anime inside page object when successful")
  void list_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
    Anime savedAnime = animeRepository.save(animeToBeSaved);

    String expectedName = savedAnime.getName();

    PageableResponse<Anime> animePage = testRestTemplate.exchange(
            "/animes",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<PageableResponse<Anime>>() {}
    ).getBody();

    Assertions.assertThat(animePage).isNotNull();
    Assertions.assertThat(animePage.toList())
            .isNotEmpty()
            .hasSize(1);
    Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);
  }

  @Test
  @DisplayName("List all returns list of anime inside when successful")
  void listAll_ReturnsListOfAnimes_WhenSuccessful() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
    Anime savedAnime = animeRepository.save(animeToBeSaved);

    String expectedName = savedAnime.getName();

    List<Anime> animes = testRestTemplate.exchange(
            "/animes/all",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Anime>>() {}
    ).getBody();

    Assertions.assertThat(animes)
            .isNotNull()
            .isNotEmpty()
            .hasSize(1);
    Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
  }

  @Test
  @DisplayName("Find by id returns anime when successful")
  void findById_ReturnsAnime_WhenSuccessful() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
    Anime savedAnime = animeRepository.save(animeToBeSaved);

    Long expectedId = savedAnime.getId();

    Anime anime = testRestTemplate.getForObject("/animes/{id}", Anime.class, expectedId);

    Assertions.assertThat(anime).isNotNull();
    Assertions.assertThat(anime.getId())
            .isNotNull()
            .isEqualTo(expectedId);
  }

  @Test
  @DisplayName("Find by name returns a list of anime when successful")
  void findByName_ReturnsListOfAnime_WhenSuccessful() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
    Anime savedAnime = animeRepository.save(animeToBeSaved);

    String expectedName = savedAnime.getName();
    String url = String.format("/animes/find?name=%s", expectedName);

    List<Anime> animes = testRestTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Anime>>() {}
    ).getBody();

    Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
  }

  @Test
  @DisplayName("Find by name returns an empty list of anime when anime is not found")
  void findByName_ReturnsAnEmptyListOfAnime_WhenAnimeIsNotFound() {
    List<Anime> animes = testRestTemplate.exchange(
            "/animes/find?name=inexistent",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Anime>>() {}
    ).getBody();

    Assertions.assertThat(animes)
            .isNotNull()
            .isEmpty();
  }

  @Test
  @DisplayName("Save returns anime when successful")
  void save_ReturnsAnime_WhenSuccessful() {
    AnimePostRequestBody animeToBeSaved = AnimePostRequestBodyCreator.createAnimePostRequestBody();

    ResponseEntity<Anime> animeResponseEntity = testRestTemplate.postForEntity(
            "/animes",
            animeToBeSaved,
            Anime.class
    );

    Assertions.assertThat(animeResponseEntity).isNotNull();
    Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertThat(animeResponseEntity.getBody()).isNotNull();
    Assertions.assertThat(animeResponseEntity.getBody().getId()).isNotNull();
  }

  @Test
  @DisplayName("Replace updates anime when successful")
  void replace_UpdatesAnime_WhenSuccessful() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
    Anime savedAnimeToBeUpdated = animeRepository.save(animeToBeSaved);

    savedAnimeToBeUpdated.setName("Hajime no Ippo 2");

    ResponseEntity<Void> animeResponseEntity = testRestTemplate.exchange(
            "/animes",
            HttpMethod.PUT,
            new HttpEntity<>(savedAnimeToBeUpdated),
            Void.class
    );

    Assertions.assertThat(animeResponseEntity).isNotNull();
    Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  @DisplayName("Delete removes anime when successful")
  void delete_RemovesAnime_WhenSuccessful() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
    Anime savedAnimeToBeDeleted = animeRepository.save(animeToBeSaved);

    ResponseEntity<Void> animeResponseEntity = testRestTemplate.exchange(
            "/animes/{id}",
            HttpMethod.DELETE,
            null,
            Void.class,
            savedAnimeToBeDeleted.getId()
    );

    Assertions.assertThat(animeResponseEntity).isNotNull();
    Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }
}
