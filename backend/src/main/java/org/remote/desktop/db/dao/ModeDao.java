package org.remote.desktop.db.dao;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.repository.ModeRepository;
import org.remote.desktop.mapper.ModeMapper;
import org.remote.desktop.model.vto.ModeVto;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
