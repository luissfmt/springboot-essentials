package academy.devdojo.springboot2.mapper;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.domain.Anime.AnimeBuilder;
import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.requests.AnimePutRequestBody;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-10-31T17:29:32-0300",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.8.1 (Amazon.com Inc.)"
)
@Component
public class AnimeMapperImpl extends AnimeMapper {

    @Override
    public Anime toAnime(AnimePostRequestBody animePostRequestBody) {
        if ( animePostRequestBody == null ) {
            return null;
        }

        AnimeBuilder anime = Anime.builder();

        anime.name( animePostRequestBody.getName() );

        return anime.build();
    }

    @Override
    public Anime toAnime(AnimePutRequestBody animePutRequestBody) {
        if ( animePutRequestBody == null ) {
            return null;
        }

        AnimeBuilder anime = Anime.builder();

        anime.id( animePutRequestBody.getId() );
        anime.name( animePutRequestBody.getName() );

        return anime.build();
    }
}
