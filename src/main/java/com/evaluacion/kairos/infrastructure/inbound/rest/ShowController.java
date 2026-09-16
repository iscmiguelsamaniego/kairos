package com.evaluacion.kairos.infrastructure.inbound.rest;
import com.evaluacion.kairos.domain.Show;
import com.evaluacion.kairos.ports.in.ShowServicePort;
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

    @PostMapping("/{id}/comments")
    public ResponseEntity<String> addComment(
            @PathVariable("id") Long showId,
            @jakarta.validation.Valid @RequestBody CommentRequest request) {

        return ResponseEntity.ok("Comentario agregado exitosamente");
    }
}