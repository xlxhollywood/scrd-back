package org.example.scrd.controller;

import lombok.RequiredArgsConstructor;
import org.example.scrd.dto.CrewDto;
import org.example.scrd.service.CrewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scrd/api")
@RequiredArgsConstructor
public class CrewController {
    private final CrewService crewService;

    @PostMapping("/crew/create")
    public ResponseEntity<Void> createCrew(@RequestBody CrewDto dto, @AuthenticationPrincipal Long userId){
        crewService.createCrew(userId, dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/crew/join/{crewId}")
    public ResponseEntity<Void> joinCrew(@AuthenticationPrincipal Long userId, @PathVariable Long crewId){
        crewService.joinCrew(crewId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/crew")
    public ResponseEntity<List<CrewDto>> getCrews() {
        List<CrewDto> crews = crewService.getAllCrews();
        return ResponseEntity.ok(crews);
    }
}
