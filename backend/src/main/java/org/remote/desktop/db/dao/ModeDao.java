package org.remote.desktop.db.dao;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.entity.Mode;
import org.remote.desktop.db.repository.ModeRepository;
import org.remote.desktop.mapper.ModeMapper;
import org.remote.desktop.model.vto.ModeVto;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ModeDao {

    private final ModeRepository modeRepository;
    private final ModeMapper mapper;

    public List<ModeVto> getAllModes() {
        return modeRepository.findAll().stream()
                .map(mapper::map)
                .toList();
    }

    public List<String> getModeVerbs(String mode) {
        return Optional.ofNullable(mode)
                .map(modeRepository::findByAdapterMode)
                .map(Mode::getKeyEvtTypes)
                .orElseGet(Collections::emptyList);
    }

    public List<String> getModeNouns(String mode) {
        return modeRepository.findByAdapterMode(mode)
                .getNouns();
    }
}
