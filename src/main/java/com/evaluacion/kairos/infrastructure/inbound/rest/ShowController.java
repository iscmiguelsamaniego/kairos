package com.evaluacion.kairos.infrastructure.inbound.rest;
import com.evaluacion.kairos.domain.Show;
import com.evaluacion.kairos.ports.in.ShowServicePort;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/shows")
public class ShowController {

    private final ShowServicePort showServicePort;

    public ShowController(ShowServicePort showServicePort) {
        this.showServicePort = showServicePort;
    }

    @GetMapping("/search")
    public ResponseEntity<List<Show>> searchShows(@RequestParam("q") String query) {
        List<Show> shows = showServicePort.searchShows(query);
        return ResponseEntity.ok(shows);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Show> getShowById(@PathVariable("id") Long id) {
        Show show = showServicePort.getShowById(id);
        return ResponseEntity.ok(show);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<String> addComment(
            @PathVariable("id") Long showId,
            @Valid @RequestBody CommentRequest request) {

        showServicePort.addComment(showId, request.getComment(), request.getRating());

        return ResponseEntity.ok("Comentario agregado exitosamente");    }
}