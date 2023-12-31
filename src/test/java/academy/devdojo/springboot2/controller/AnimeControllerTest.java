package academy.devdojo.springboot2.controller;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.requests.AnimePutRequestBody;
import academy.devdojo.springboot2.service.AnimeService;
import academy.devdojo.springboot2.util.AnimeCreator;
import academy.devdojo.springboot2.util.AnimePostRequestBodyCreator;
import academy.devdojo.springboot2.util.AnimePutRequestBodyCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

@ExtendWith(SpringExtension.class)
class AnimeControllerTest {
  @InjectMocks
  private AnimeController animeController;
  @Mock
  private AnimeService animeServiceMock;

  @BeforeEach
  void setUp() {
    PageImpl<Anime> animePage = new PageImpl<>(List.of(AnimeCreator.createValidAnime()));
    List<Anime> animeList = List.of(AnimeCreator.createValidAnime());
    Anime validAnime = AnimeCreator.createValidAnime();

    BDDMockito.when(animeServiceMock.listAll(ArgumentMatchers.any()))
            .thenReturn(animePage);

    BDDMockito.when(animeServiceMock.listAllNonPageable())
            .thenReturn(animeList);

    BDDMockito.when(animeServiceMock.findByIdOrThrowBadRequestException(ArgumentMatchers.anyLong()))
            .thenReturn(validAnime);

    BDDMockito.when(animeServiceMock.findByName(ArgumentMatchers.anyString()))
            .thenReturn(animeList);

    BDDMockito.when(animeServiceMock.save(ArgumentMatchers.any(AnimePostRequestBody.class)))
            .thenReturn(validAnime);

    BDDMockito.doNothing().when(animeServiceMock).replace(ArgumentMatchers.any(AnimePutRequestBody.class));

    BDDMockito.doNothing().when(animeServiceMock).delete(ArgumentMatchers.anyLong());
  }
  
  @Test
  @DisplayName("List returns list of anime inside page object when successful")
  void list_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
    String expectedName = AnimeCreator.createValidAnime().getName();

    Page<Anime> animePage = animeController.list(null).getBody();

    Assertions.assertThat(animePage).isNotNull();
    Assertions.assertThat(animePage.toList())
            .isNotEmpty()
            .hasSize(1);
    Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);
  }

  @Test
  @DisplayName("List all returns list of anime inside when successful")
  void listAll_ReturnsListOfAnimes_WhenSuccessful() {
    String expectedName = AnimeCreator.createValidAnime().getName();

    List<Anime> animes = animeController.listAll().getBody();

    Assertions.assertThat(animes)
            .isNotNull()
            .isNotEmpty()
            .hasSize(1);
    Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
  }

  @Test
  @DisplayName("Find by id returns anime when successful")
  void findById_ReturnsAnime_WhenSuccessful() {
    Long expectedId = AnimeCreator.createValidAnime().getId();

    Anime anime = animeController.findById(1L).getBody();

    Assertions.assertThat(anime).isNotNull();
    Assertions.assertThat(anime.getId())
            .isNotNull()
            .isEqualTo(expectedId);
  }

  @Test
  @DisplayName("Find by name returns a list of anime when successful")
  void findByName_ReturnsListOfAnime_WhenSuccessful() {
    String expectedName = AnimeCreator.createValidAnime().getName();

    List<Anime> animes = animeController.findByName("anime").getBody();

    Assertions.assertThat(animes)
            .isNotNull()
            .isNotEmpty()
            .hasSize(1);

    Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
  }

  @Test
  @DisplayName("Find by name returns an empty list of anime when anime is not found")
  void findByName_ReturnsAnEmptyListOfAnime_WhenAnimeIsNotFound() {
    BDDMockito.when(animeServiceMock.findByName(ArgumentMatchers.anyString()))
            .thenReturn(Collections.emptyList());

    List<Anime> animes = animeController.findByName("anime").getBody();

    Assertions.assertThat(animes)
            .isNotNull()
            .isEmpty();
  }

  @Test
  @DisplayName("Save returns anime when successful")
  void save_ReturnsAnime_WhenSuccessful() {
    AnimePostRequestBody animeToBeSaved = AnimePostRequestBodyCreator.createAnimePostRequestBody();
    Anime expectedAnime = AnimeCreator.createValidAnime();

    Anime savedAnime = animeController.save(animeToBeSaved).getBody();

    Assertions.assertThat(savedAnime)
            .isNotNull()
            .isEqualTo(expectedAnime);
  }

  @Test
  @DisplayName("Replace updates anime when successful")
  void replace_UpdatesAnime_WhenSuccessful() {
    AnimePutRequestBody animeUpdate = AnimePutRequestBodyCreator.createAnimePutRequestBody();
    ResponseEntity<Void> entity = animeController.replace(animeUpdate);

    Assertions.assertThatCode(() -> animeController.replace(animeUpdate)).doesNotThrowAnyException();
    Assertions.assertThat(entity).isNotNull();
    Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  @DisplayName("Delete removes anime when successful")
  void delete_RemovesAnime_WhenSuccessful() {
    ResponseEntity<Void> entity = animeController.delete(1L);

    Assertions.assertThatCode(() -> animeController.delete(1L)).doesNotThrowAnyException();
    Assertions.assertThat(entity).isNotNull();
    Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }
}