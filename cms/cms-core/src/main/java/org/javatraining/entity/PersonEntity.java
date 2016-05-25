package org.javatraining.entity;


import org.javatraining.entity.enums.PersonRole;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "person", schema = "")
public class PersonEntity implements Serializable, GenericEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    private Long id;

    @NotNull
    @Basic
    @Column(name = "name", nullable = false, insertable = true, updatable = true, length = 255)
    private String name;

    @Basic
    @Column(name = "second_name", nullable = true, insertable = true, updatable = true, length = 255)
    private String secondName;

    @NotNull
    @Basic
    @Column(name = "last_name", nullable = false, insertable = true, updatable = true, length = 255)
    private String lastName;

    @NotNull
    @Basic
    @Column(name = "email", nullable = false, insertable = true, updatable = true, length = 255)
    private String email;

    @Basic
    @Column(name = "phone", nullable = true, insertable = true, updatable = true, length = 11)
    private String phone;

    @Basic
    @Column(name = "avatar", nullable = true, insertable = true, updatable = true, length = 255)
    private String avatar;

    @Basic
    @Column(name = "graduation", nullable = true, insertable = true, updatable = true, length = 255)
    private String graduation;

    @Basic
    @Column(name = "experience", nullable = true, insertable = true, updatable = true, length = 255)
    private String experience;

    @Enumerated(EnumType.STRING)
    @Column(name = "personRole", nullable = false, insertable = true, updatable = true, length = 255)
    private PersonRole personRole;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "persons")
    private Set<ForumMessageEntity> forumMassages;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "persons")
    private Set<MarkEntity> marks;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "person")
    private Set<CoursePersonStatusEntity> coursePersonEntities;

    public PersonEntity() {
    }

    public PersonEntity(String name, String secondName, String lastName, String email, String phone, PersonRole personRole) {
        this.name = name;
        this.secondName = secondName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.personRole = personRole;
    }

    public PersonEntity(String name, String lastName, String email, PersonRole personRole) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.personRole = personRole;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setForumMassages(Set<ForumMessageEntity> forumMassages) {
        this.forumMassages = forumMassages;
    }

    public Set<ForumMessageEntity> getForumMassages() {
        return forumMassages;
    }

    public PersonRole getPersonRole() {
        return personRole;
    }

    public void setPersonRole(PersonRole personRole) {
        this.personRole = personRole;
    }

    public Set<MarkEntity> getMarks() {
        return marks;
    }

    public void setMarks(Set<MarkEntity> marks) {
        this.marks = marks;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGraduation() {
        return graduation;
    }

    public void setGraduation(String graduation) {
        this.graduation = graduation;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public Set<CoursePersonStatusEntity> getCoursePersonEntities() {
        return coursePersonEntities;
    }

    public void setCoursePersonEntities(Set<CoursePersonStatusEntity> coursePersonEntities) {
        this.coursePersonEntities = coursePersonEntities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonEntity)) return false;

        PersonEntity that = (PersonEntity) o;

        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (phone != null ? !phone.equals(that.phone) : that.phone != null) return false;
        if (personRole != that.personRole) return false;
        if (secondName != null ? !secondName.equals(that.secondName) : that.secondName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (secondName != null ? secondName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (personRole != null ? personRole.hashCode() : 0);
        return result;
    }
}
