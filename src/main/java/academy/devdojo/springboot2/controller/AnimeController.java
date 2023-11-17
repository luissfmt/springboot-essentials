package academy.devdojo.springboot2.controller;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.requests.AnimePutRequestBody;
import academy.devdojo.springboot2.service.AnimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("animes")
@Log4j2
@RequiredArgsConstructor
public class AnimeController {
    private final AnimeService animeService;

    @GetMapping
    @Operation(
            summary = "List all animes paginated",
            description = "The default size is 20, use the parameter size to change the default value"
    )
    public ResponseEntity<Page<Anime>> list(@ParameterObject Pageable pageable) {
        return new ResponseEntity<>(animeService.listAll(pageable), HttpStatus.OK);
    }

    @Operation(
            summary = "List all animes non paginated",
            description = "Lists all animes from database"
    )
    @GetMapping(path = "/all")
    public ResponseEntity<List<Anime>> listAll() {
        return new ResponseEntity<>(animeService.listAllNonPageable(), HttpStatus.OK);
    }

    @Operation(
            summary = "Find an anime by id",
            description = "Get an anime from database by id or throw bad request code"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "When anime does not exist in the database", content = @Content),
    })
    @GetMapping(path = "/{id}")
    public ResponseEntity<Anime> findById(@PathVariable Long id) {
        return new ResponseEntity<>(animeService.findByIdOrThrowBadRequestException(id), HttpStatus.OK);
    }

    @Operation(
            summary = "Find animes by name",
            description = "Get a list of animes from database by name"
    )
    @GetMapping(path = "/find")
    public ResponseEntity<List<Anime>> findByName(@RequestParam String name) {
        return new ResponseEntity<>(animeService.findByName(name), HttpStatus.OK);
    }

    @Operation(
            summary = "Create a new anime",
            description = "Saves a new anime in database"
    )
    @PostMapping
    public ResponseEntity<Anime> save(@RequestBody @Valid AnimePostRequestBody anime) {
      return new ResponseEntity<>(animeService.save(anime), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Updates an existing anime",
            description = "Replaces an anime data from database"
    )
    @PutMapping
    public ResponseEntity<Void> replace(@RequestBody AnimePutRequestBody anime) {
        animeService.replace(anime);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Delete anime by id",
            description = "Delete an anime from database"
    )
    @DeleteMapping(path = "/admin/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "When anime does not exist in the database"),
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        animeService.delete(id);

      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}