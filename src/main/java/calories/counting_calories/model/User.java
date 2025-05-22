package calories.counting_calories.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private double weight;
    @Column(nullable = false)
    private int password;


    public User(String username, double weight, int password) {
        this.username = username;
        this.weight = weight;
        this.password = password;
    }
    public User() {}
}
