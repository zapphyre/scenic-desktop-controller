package org.remote.desktop.service.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.remote.desktop.model.EMode;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ModeService {

    @Getter @Setter
    private EMode mode = EMode.DESKTOP;

}
