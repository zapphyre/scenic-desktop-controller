//package org.remote.desktop.controller.impl;
//
//import lombok.RequiredArgsConstructor;
//import org.remote.desktop.component.WinderHostRepository;
//import org.remote.desktop.model.vto.EventVto;
//import org.remote.desktop.model.vto.SceneVto;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.winder.common.model.EWinderOp;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("${api.prefix}/winder")
//@RequiredArgsConstructor
//public class WinderCtrl {
//
//    public final WinderHostRepository  winderHostRepository;
//
//    @GetMapping("ops")
//    public EWinderOp[] getAllOperations() {
//        return EWinderOp.values();
//    }
//
//    @GetMapping("scene")
//    public SceneVto winderSceneId() {
//        return winderHostRepository.getWinderScenery();
//    }
//
//    @GetMapping("events")
//    public Map<EWinderOp, EventVto> winderOperationEvents() {
//        return winderHostRepository.getOpEventMap();
//    }
//
//}
