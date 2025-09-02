package org.example.scrd.service;

import lombok.RequiredArgsConstructor;
import org.example.scrd.domain.Crew;
import org.example.scrd.domain.Theme;
import org.example.scrd.domain.User;
import org.example.scrd.dto.CrewDto;
import org.example.scrd.dto.ThemeDto;
import org.example.scrd.exception.NotFoundException;
import org.example.scrd.repo.CrewRepository;
import org.example.scrd.repo.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CrewService {
    private final CrewRepository crewRepository;
    private final UserRepository userRepository;

    public void createCrew(Long userId, CrewDto dto) { // crew 만들기
        crewRepository.save(Crew.from(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("유저가 존재하지 않습니다.")), dto, Crew.Role.LEADER));
    }

    public void joinCrew(Long crewId, Long userId) { // crew 신청하기
        CrewDto crewDto = CrewDto.from(crewRepository.findById(crewId).orElseThrow(() -> new NotFoundException("해당 크루는 존재하지 않습니다.")));
        crewRepository.save(Crew.from(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("유저가 존재하지 않습니다.")), crewDto, Crew.Role.MEMBER));
    }

    public List<CrewDto> getAllCrews() {
        List<Crew> crews = crewRepository.findAll();
        return crews
                .stream()
                .map(CrewDto::from)
                .collect(Collectors.toList());
    }
}
