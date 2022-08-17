package group5.freelancejob.mapper;

import group5.freelancejob.daos.Genre;
import group5.freelancejob.models.GenreDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class GenreMapper {
    public abstract GenreDto convertToGenreDto(Genre genre);
}
