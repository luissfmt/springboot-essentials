package academy.devdojo.springboot2.repository;

import academy.devdojo.springboot2.domain.Anime;
import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@DisplayName("Tests for Anime Repository")
class AnimeRepositoryTest {
  @Autowired
  private AnimeRepository animeRepository;

  @Test
  @DisplayName("Save persists anime when successful")
  void save_PersistAnime_WhenSuccessful() {
    Anime animeToBeSaved = createAnime();

    Anime savedAnime = this.animeRepository.save(animeToBeSaved);

    Assertions.assertThat(savedAnime).isNotNull();
    Assertions.assertThat(savedAnime.getId()).isNotNull();
    Assertions.assertThat(savedAnime.getName()).isEqualTo(animeToBeSaved.getName());
  }

  @Test
  @DisplayName("Save updates anime when successful")
  void save_UpdatesAnime_WhenSuccessful() {
    Anime animeToBeSaved = createAnime();

    Anime savedAnime = this.animeRepository.save(animeToBeSaved);

    savedAnime.setName("Overlord");

    Anime animeUpdated = this.animeRepository.save(savedAnime);

    Assertions.assertThat(animeUpdated).isNotNull();
    Assertions.assertThat(animeUpdated.getId()).isNotNull();
    Assertions.assertThat(animeUpdated.getName()).isEqualTo(savedAnime.getName());
  }

  @Test
  @DisplayName("Delete removes anime when successful")
  void delete_RemovesAnime_WhenSuccessful() {
    Anime animeToBeSaved = createAnime();

    Anime savedAnime = this.animeRepository.save(animeToBeSaved);

    this.animeRepository.delete(savedAnime);

    Optional<Anime> animeOptional = this.animeRepository.findById(savedAnime.getId());

    Assertions.assertThat(animeOptional).isEmpty();
  }

  @Test
  @DisplayName("Find By Name returns anime when successful")
  void findByName_ReturnsAnime_WhenSuccessful() {
    Anime animeToBeSaved = createAnime();

    Anime savedAnime = this.animeRepository.save(animeToBeSaved);

    List<Anime> animes = this.animeRepository.findByName(savedAnime.getName());

    Assertions.assertThat(animes).isNotEmpty().contains(savedAnime);
  }

  @Test
  @DisplayName("Find By Name returns empty list when no anime is found")
  void findByName_ReturnsEmptyList_WhenNoAnimeIsFound() {
    List<Anime> animes = this.animeRepository.findByName("Wrong Anime");

    Assertions.assertThat(animes).isEmpty();
  }

  @Test
  @DisplayName("Save throws ConstraintViolationException when name is empty")
  void save_ThrowsConstraintViolationException_WhenNameIsEmpty() {
    Anime anime = new Anime();

//    Assertions.assertThatThrownBy(() -> this.animeRepository.save(anime))
//            .isInstanceOf(ConstraintViolationException.class);

    Assertions.assertThatExceptionOfType(ConstraintViolationException.class)
            .isThrownBy(() -> this.animeRepository.save(anime))
            .withMessageContaining("The anime name cannot be blank or null");
  }

  private Anime createAnime() {
    return Anime.builder().name("Hajime no Ippo").build();
  }
}