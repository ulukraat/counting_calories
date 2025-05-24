package calories.counting_calories.repository;

import calories.counting_calories.model.DailyNutritionRemaining;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyNutritionRemainingRepository extends JpaRepository<DailyNutritionRemaining, Long> {
    Optional<DailyNutritionRemaining> findByUserIdAndDate(Long userId, LocalDate date);
}
