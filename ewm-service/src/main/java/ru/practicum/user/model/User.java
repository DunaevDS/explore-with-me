package ru.practicum.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String name;
    @ToString.Exclude
    @ManyToMany
    @JoinTable(name = "user_subscribers",
               joinColumns = @JoinColumn(name = "subscriber_id"),
               inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> subscribers;
    @ToString.Exclude
    @ManyToMany(mappedBy = "subscribers")
    private List<User> subs; // подписчики подписчика
}
