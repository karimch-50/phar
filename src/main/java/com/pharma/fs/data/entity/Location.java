
package com.pharma.fs.data.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pharma.fs.data.enums.LocationStatus;
import com.pharma.fs.data.enums.LocationType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Location extends AbstractEntity {
     @Column(unique = true)
     private String name;
     private String address;

     @Enumerated(EnumType.STRING)
     private LocationStatus status;

     @Enumerated(EnumType.STRING)
     private LocationType type;

     private Integer capacity;

     @ManyToOne
     private Organisation organisation;

     @ManyToOne
     private Location parent;

  /*   @OneToOne
     private Equipment equipment;*/

   //   @OneToMany
   //   private Set<Mouvement> mouvement = new HashSet<>();

     private boolean active = true;
     private Date created = new Date();
     private Date updated = new Date();
     private String createdBy;
     private String updatedBy;
}
