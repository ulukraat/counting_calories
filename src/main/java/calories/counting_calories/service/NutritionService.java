package calories.counting_calories.service;

import calories.counting_calories.model.Nutrition;
import calories.counting_calories.model.User;
import calories.counting_calories.repository.NutritionRepository;
import calories.counting_calories.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NutritionService {
    private final UserRepository userRepository;
    NutritionRepository nutritionRepository;

    public NutritionService(NutritionRepository nutritionRepository, UserRepository userRepository) {
        this.nutritionRepository = nutritionRepository;
        this.userRepository = userRepository;
    }
        public List<Nutrition> showNutrition(Long userId) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден!"));
            List<Nutrition> nutritionList = nutritionRepository.findByUser(user);

            if (nutritionList.isEmpty()) {
                throw new RuntimeException("Данные по питанию отсутствуют!");
            }

            return nutritionList; // ⬅️ Возвращаем весь список!
        }


    public Nutrition weightLoss(User user) {
        double weight = user.getWeight();
        double fat = weight * 0.5;
        double protein = weight * 1.5;
        double carb = weight * 3;
        double doubleCalories = ((carb * 4) + (protein * 4) + (fat * 9));
        int calories = ((int) doubleCalories);
        Nutrition nutrition = new Nutrition(calories,fat,protein,carb);
        nutrition.setUser(user);
        return nutritionRepository.save(nutrition);
    }

    public Nutrition weightGain(User user) {
        double weight = user.getWeight();
        double fat  = weight * 0.5;
        double protein = weight * 2;
        double carb = weight * 5;
        double doubleCalories = ((carb * 4) + (protein * 4)) + (fat * 9);
        int calories = ((int) doubleCalories);
        Nutrition nutrition = new Nutrition(calories,fat,protein,carb);
        nutrition.setUser(user);
        return nutritionRepository.save(nutrition);
    }

}
