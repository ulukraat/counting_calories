package calories.counting_calories.repository;

import calories.counting_calories.model.Nutrition;
import calories.counting_calories.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NutritionRepository extends JpaRepository<Nutrition, Long> {
    List<Nutrition> findByUser(User user);
}
