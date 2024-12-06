package com.labo.fs.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.labo.fs.data.entity.Organisation;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;
import java.util.Date;

@Entity
@Data
@Table(name = "application_user")
public class User extends AbstractEntity {

    private String username;
    private String name;
    @JsonIgnore
    private String hashedPassword;
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.LAZY)
    private Set<Role> roles;
    @Lob
    @Column(length = 1000000)
    private byte[] profilePicture;

    @OneToMany(fetch = FetchType.LAZY)
    private Set<Organisation> organisations;

    private boolean active = true;
    private Date created = new Date();
    private Date updated = new Date();
    private String createdBy;
    private String updatedBy;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getHashedPassword() {
        return hashedPassword;
    }
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
    public Set<Role> getRoles() {
        return roles;
    }
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
    public byte[] getProfilePicture() {
        return profilePicture;
    }
    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }

}
