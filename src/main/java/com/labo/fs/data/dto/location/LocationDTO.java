package com.labo.fs.data.dto.location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.labo.fs.data.entity.Location;
import com.labo.fs.data.enums.LocationStatus;
import com.labo.fs.data.enums.LocationType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LocationDTO {
    private UUID id;
    private LocationType type;
    private String name;
    private LocationStatus status;
    private List<LocationDTO> children = new ArrayList<>();

    public LocationDTO(UUID id, LocationType type, String name, LocationStatus status) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.status = status;
    }

    public LocationDTO(Location entity) {
        this.id = entity.getId();
        this.type = entity.getType();
        this.name = entity.getName();
        this.status = entity.getStatus();
    }
}
