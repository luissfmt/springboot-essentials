package academy.devdojo.springboot2.service;

import academy.devdojo.springboot2.domain.Anime;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AnimeService {
//  private final AnimeRepository animeRepository;
  private List<Anime> animes = new ArrayList<>(Arrays.asList(new Anime(1L, "DBZ"), new Anime(2L, "Berserk")));
  public List<Anime> listAll() {
    return animes;
  }

  public Anime findById(Long id) {
    return animes.stream()
            .filter(anime -> anime.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Anime not found"));
  }

  public void delete(Long id) {
    animes.remove(findById(id));
  }
}
