package org.remote.desktop.service.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.remote.desktop.db.dao.ModeDao;
import org.remote.desktop.model.vto.ModeVto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModeService {

    private final ModeDao modeDao;

    @Getter @Setter
    private String currentMode = "DESKTOP";

    public List<ModeVto> getAllModes() {
        return modeDao.getAllModes();
    }

}
