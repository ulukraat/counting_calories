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

        if (user.getNutritionList().isEmpty()) {
            throw new RuntimeException("Данные по питанию отсутствуют!");
        }

        return user.getNutritionList(); // ⬅️ Возвращаем весь список!
    }


    public Nutrition weightLoss(User user) {
        double weight = user.getWeight();
        double fat = weight * 0.5;
        double protein = weight * 1.5;
        double carb = weight * 3;
        double doubleCalories = ((carb * 4) + (protein * 4) + (fat * 9));
        int calories = ((int) doubleCalories);
        Nutrition nutrition = new Nutrition(calories,fat,protein,carb);
        return nutritionRepository.save(nutrition);
    }
    public Nutrition remainingNutrition(Long userId, double eatenProteins, double eatenFats, double eatenCarbs) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден!"));

        List<Nutrition> nutritionList = user.getNutritionList();

        if (nutritionList.isEmpty()) {
            throw new RuntimeException("Данных о питании нет!");
        }

        Nutrition latestNutrition = nutritionList.get(nutritionList.size() - 1); // Берём последнюю запись

        double remainingProteins = Math.max(latestNutrition.getProtein() - eatenProteins, 0);
        double remainingFats = Math.max(latestNutrition.getFat() - eatenFats, 0);
        double remainingCarbs = Math.max(latestNutrition.getCarb() - eatenCarbs, 0);

        return new Nutrition(latestNutrition.getCalories(), remainingProteins, remainingFats, remainingCarbs);
    }


}
