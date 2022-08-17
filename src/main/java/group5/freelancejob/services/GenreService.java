package group5.freelancejob.services;

import group5.freelancejob.daos.Genre;
import group5.freelancejob.mapper.GenreMapper;
import group5.freelancejob.models.GenreDto;
import group5.freelancejob.repositories.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class GenreService {
    private final GenreRepository _genreRepository;
    private final GenreMapper _genreMapper;

    public GenreService(GenreRepository genreRepository, GenreMapper genreMapper) {
        _genreRepository = genreRepository;
        _genreMapper = genreMapper;
    }

    public Collection<GenreDto> getAllGenre() {
        List<Genre> genreList = _genreRepository.findAll();
        return genreList.stream().map(_genreMapper::convertToGenreDto).sorted(Comparator.comparing(GenreDto::getId)).toList();
    }
}
