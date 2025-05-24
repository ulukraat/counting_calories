package calories.counting_calories.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class DailyNutritionRemaining {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;

    private LocalDate date;
    private int remainingCalories;
    private double remainingFat;
    private double remainingProtein;
    private double remainingCarb;
}
