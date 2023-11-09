package academy.devdojo.springboot2.service;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.exception.BadRequestException;
import academy.devdojo.springboot2.repository.AnimeRepository;
import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.requests.AnimePutRequestBody;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
class AnimeServiceTest {
  @InjectMocks
  private AnimeService animeService;
  @Mock
  private AnimeRepository animeRepositoryMock;

  @BeforeEach
  void setUp() {
    PageImpl<Anime> animePage = new PageImpl<>(List.of(AnimeCreator.createValidAnime()));
    List<Anime> animeList = List.of(AnimeCreator.createValidAnime());
    Anime validAnime = AnimeCreator.createValidAnime();

    BDDMockito.when(animeRepositoryMock.findAll(ArgumentMatchers.any(PageRequest.class)))
            .thenReturn(animePage);

    BDDMockito.when(animeRepositoryMock.findAll())
            .thenReturn(List.of(AnimeCreator.createValidAnime()));

    BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyLong()))
            .thenReturn(Optional.of(validAnime));

    BDDMockito.when(animeRepositoryMock.findByName(ArgumentMatchers.anyString()))
            .thenReturn(animeList);

    BDDMockito.when(animeRepositoryMock.save(ArgumentMatchers.any(Anime.class)))
            .thenReturn(validAnime);

    BDDMockito.doNothing().when(animeRepositoryMock).delete(ArgumentMatchers.any(Anime.class));
  }

  @Test
  @DisplayName("List returns list of anime inside page object when successful")
  void listAll_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
    String expectedName = AnimeCreator.createValidAnime().getName();

    Page<Anime> animePage = animeService.listAll(PageRequest.of(1, 1));

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

    List<Anime> animes = animeService.listAllNonPageable();

    Assertions.assertThat(animes)
            .isNotNull()
            .isNotEmpty()
            .hasSize(1);
    Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
  }

  @Test
  @DisplayName("Find by id or throw bad request exception returns anime when successful")
  void findByIdOrThrowBadRequestException_ReturnsAnime_WhenSuccessful() {
    Long expectedId = AnimeCreator.createValidAnime().getId();

    Anime anime = animeService.findByIdOrThrowBadRequestException(1L);

    Assertions.assertThat(anime).isNotNull();
    Assertions.assertThat(anime.getId())
            .isNotNull()
            .isEqualTo(expectedId);
  }

  @Test
  @DisplayName("Find by id or throw bad request exception throws bad request exception when anime is not found")
  void findByIdOrThrowBadRequestException_ThrowBadRequestException_WhenAnimeIsNotFound() {
    BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyLong()))
            .thenReturn(Optional.empty());

    Long expectedId = AnimeCreator.createValidAnime().getId();

    Assertions.assertThatExceptionOfType(BadRequestException.class)
            .isThrownBy(() -> animeService.findByIdOrThrowBadRequestException(1L))
            .withMessage("Anime not found");
  }

  @Test
  @DisplayName("Find by name returns a list of anime when successful")
  void findByName_ReturnsListOfAnime_WhenSuccessful() {
    String expectedName = AnimeCreator.createValidAnime().getName();

    List<Anime> animes = animeService.findByName("anime");

    Assertions.assertThat(animes)
            .isNotNull()
            .isNotEmpty()
            .hasSize(1);

    Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
  }

  @Test
  @DisplayName("Find by name returns an empty list of anime when anime is not found")
  void findByName_ReturnsAnEmptyListOfAnime_WhenAnimeIsNotFound() {
    BDDMockito.when(animeRepositoryMock.findByName(ArgumentMatchers.anyString()))
            .thenReturn(Collections.emptyList());

    List<Anime> animes = animeService.findByName("anime");

    Assertions.assertThat(animes)
            .isNotNull()
            .isEmpty();
  }

  @Test
  @DisplayName("Save returns anime when successful")
  void save_ReturnsAnime_WhenSuccessful() {
    AnimePostRequestBody animeToBeSaved = AnimePostRequestBodyCreator.createAnimePostRequestBody();
    Anime expectedAnime = AnimeCreator.createValidAnime();

    Anime savedAnime = animeService.save(animeToBeSaved);

    Assertions.assertThat(savedAnime)
            .isNotNull()
            .isEqualTo(expectedAnime);
  }

  @Test
  @DisplayName("Replace updates anime when successful")
  void replace_UpdatesAnime_WhenSuccessful() {
    AnimePutRequestBody animeUpdate = AnimePutRequestBodyCreator.createAnimePutRequestBody();

    Assertions.assertThatCode(() -> animeService.replace(animeUpdate)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("Delete removes anime when successful")
  void delete_RemovesAnime_WhenSuccessful() {
    Assertions.assertThatCode(() -> animeService.delete(1L)).doesNotThrowAnyException();
  }
}