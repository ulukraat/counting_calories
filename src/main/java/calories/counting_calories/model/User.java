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

    @OneToMany
    private List<Nutrition> nutritionList;

    public User(String username, double weight) {
        this.username = username;
        this.weight = weight;
    }
    public User() {}
}
