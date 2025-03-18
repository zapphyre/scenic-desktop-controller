package org.remote.desktop.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Setting {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String instanceName;

    private Boolean allowNetworkDiscovery;

    private String baseSceneName;

    private Boolean disconnectLocalOnRemoteConnection;

    private String ipAddress;

    private Integer port;
}
