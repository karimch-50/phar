package com.labo.fs.data.dto.organisation;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class OrganisationInfoMapper {
    public List<OrganisationInfoDTO> toOrganisationInfoDTO(Object[] array,
                List<OrganisationInfoDTO> dtoList) {
        for (int i = 0; i < array.length; i += 8) {
            OrganisationInfoDTO dto = new OrganisationInfoDTO();
            dto.setId((UUID) array[i]);
            dto.setName((String) array[i + 1]);
            dto.setVille((String) array[i + 2]);
            dto.setAddress((String) array[i + 3]);
            dto.setLongitude((double) array[i + 4]);
            dto.setLatitude((double) array[i + 5]);
            dto.setActive((boolean) array[i + 6]);
            dto.setCreated((Date) array[i + 7]);

            dtoList.add(dto);
        }
        return dtoList;
    }
    
}

