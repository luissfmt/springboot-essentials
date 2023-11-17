package academy.devdojo.springboot2.integration;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.domain.DevDojoUser;
import academy.devdojo.springboot2.repository.AnimeRepository;
import academy.devdojo.springboot2.repository.DevDojoUserRepository;
import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.util.AnimeCreator;
import academy.devdojo.springboot2.util.AnimePostRequestBodyCreator;
import academy.devdojo.springboot2.wrapper.PageableResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
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
  @Qualifier(value = "testRestTemplateRoleUser")
  private TestRestTemplate testRestTemplateRoleUser;
  @Autowired
  @Qualifier(value = "testRestTemplateRoleAdmin")
  private TestRestTemplate testRestTemplateRoleAdmin;
  @Autowired
  private AnimeRepository animeRepository;
  @Autowired
  private DevDojoUserRepository devDojoUserRepository;
  private static final DevDojoUser USER = DevDojoUser.builder()
            .name("Luis")
            .password("{bcrypt}$2a$10$yQ26BMzmaaZviN0tdPZ6Xu/2RfdLMwDdPC7Kb/5hUw8FVNrtKhENy")
            .username("luissfmt")
            .authorities("ROLE_USER")
            .build();

  private static final DevDojoUser ADMIN = DevDojoUser.builder()
          .name("DevDojo Academy")
          .password("{bcrypt}$2a$10$yQ26BMzmaaZviN0tdPZ6Xu/2RfdLMwDdPC7Kb/5hUw8FVNrtKhENy")
          .username("devdojo")
          .authorities("ROLE_USER,ROLE_ADMIN")
          .build();

  @TestConfiguration
  @Lazy
  static class Config {
    @Bean(name = "testRestTemplateRoleUser")
    public TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}") int port) {
      RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
              .rootUri("http://localhost:" + port)
              .basicAuthentication("luissfmt", "academy");

      return new TestRestTemplate(restTemplateBuilder);
    }

    @Bean(name = "testRestTemplateRoleAdmin")
    public TestRestTemplate testRestTemplateRoleAdminCreator(@Value("${local.server.port}") int port) {
      RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
              .rootUri("http://localhost:" + port)
              .basicAuthentication("devdojo", "academy");

      return new TestRestTemplate(restTemplateBuilder);
    }
  }

  @Test
  @DisplayName("List returns list of anime inside page object when successful")
  void list_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
    Anime savedAnime = animeRepository.save(animeToBeSaved);

    devDojoUserRepository.save(USER);

    String expectedName = savedAnime.getName();

    PageableResponse<Anime> animePage = testRestTemplateRoleUser.exchange(
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

    devDojoUserRepository.save(USER);

    String expectedName = savedAnime.getName();

    List<Anime> animes = testRestTemplateRoleUser.exchange(
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

    devDojoUserRepository.save(USER);

    Long expectedId = savedAnime.getId();

    Anime anime = testRestTemplateRoleUser.getForObject("/animes/{id}", Anime.class, expectedId);

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

    devDojoUserRepository.save(USER);

    String expectedName = savedAnime.getName();
    String url = String.format("/animes/find?name=%s", expectedName);

    List<Anime> animes = testRestTemplateRoleUser.exchange(
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
    devDojoUserRepository.save(USER);
    
    List<Anime> animes = testRestTemplateRoleUser.exchange(
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

    devDojoUserRepository.save(USER);

    ResponseEntity<Anime> animeResponseEntity = testRestTemplateRoleUser.postForEntity(
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

    devDojoUserRepository.save(USER);

    savedAnimeToBeUpdated.setName("Hajime no Ippo 2");

    ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleUser.exchange(
            "/animes",
            HttpMethod.PUT,
            new HttpEntity<>(savedAnimeToBeUpdated),
            Void.class
    );

    Assertions.assertThat(animeResponseEntity).isNotNull();
    Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  @DisplayName("Delete returns 403 when user is not admin")
  void delete_Returns403_WhenUserIsNotAdmin() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
    Anime savedAnimeToBeDeleted = animeRepository.save(animeToBeSaved);

    devDojoUserRepository.save(USER);

    ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleUser.exchange(
            "/animes/admin/{id}",
            HttpMethod.DELETE,
            null,
            Void.class,
            savedAnimeToBeDeleted.getId()
    );

    Assertions.assertThat(animeResponseEntity).isNotNull();
    Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  @DisplayName("Delete removes anime when successful")
  void delete_RemovesAnime_WhenSuccessful() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
    Anime savedAnimeToBeDeleted = animeRepository.save(animeToBeSaved);

    devDojoUserRepository.save(ADMIN);

    ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleAdmin.exchange(
            "/animes/admin/{id}",
            HttpMethod.DELETE,
            null,
            Void.class,
            savedAnimeToBeDeleted.getId()
    );

    Assertions.assertThat(animeResponseEntity).isNotNull();
    Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }
}
