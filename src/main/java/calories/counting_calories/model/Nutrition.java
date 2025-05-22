package calories.counting_calories.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.PrintWriter;

@Data
@Entity
@Table(name = "nutritional_value")
public class Nutrition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private int calories;
    @Column(nullable = false)
    private double fat;
    @Column(nullable = false)
    private double protein;
    @Column(nullable = false)
    private double carb;

    public Nutrition(int calories,double fat,double protein,double carb) {
        this.calories = calories;
        this.fat = fat;
        this.protein = protein;
        this.carb = carb;
    }
    public Nutrition() {}

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;


}
