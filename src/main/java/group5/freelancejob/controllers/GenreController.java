package group5.freelancejob.controllers;

import group5.freelancejob.services.GenreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/genre")
public class GenreController {
    private final GenreService _genreService;

    public GenreController(GenreService genreService) {
        _genreService = genreService;
    }

    @GetMapping()
    public ResponseEntity<?> getAllGenre(){
        return ResponseEntity.ok(_genreService.getAllGenre());
    }
}
