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
        return ResponseEntity.ok(showServicePort.searchShows(query));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Show> getShowById(@PathVariable("id") Long id) {
        Show show = showServicePort.getShowById(id);
        return ResponseEntity.ok(show);
    }

}