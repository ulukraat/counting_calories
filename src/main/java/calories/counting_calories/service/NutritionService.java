package calories.counting_calories.service;

import calories.counting_calories.model.DailyNutritionRemaining;
import calories.counting_calories.model.Nutrition;
import calories.counting_calories.model.User;
import calories.counting_calories.repository.DailyNutritionRemainingRepository;
import calories.counting_calories.repository.NutritionRepository;
import calories.counting_calories.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class NutritionService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final NutritionRepository nutritionRepository;
    @Autowired
    private DailyNutritionRemainingRepository dailyRemainingRepository;

    public NutritionService(NutritionRepository nutritionRepository, UserRepository userRepository) {
        this.nutritionRepository = nutritionRepository;
        this.userRepository = userRepository;
    }


    public Nutrition weightLoss(User user) {
        double weight = user.getWeight();
        double fat = weight * 0.5;
        double protein = weight * 1.5;
        double carb = weight * 3;
        double currentCalories = ((carb * 4) + (protein * 4) + (fat * 9));
        int calories = ((int) currentCalories);
        Nutrition nutrition = new Nutrition(calories,fat,protein,carb);
        nutrition.setUser(user);
        return nutritionRepository.save(nutrition);
    }

    public Nutrition weightGain(User user) {
        double weight = user.getWeight();
        double fat  = weight * 0.5;
        double protein = weight * 2;
        double carb = weight * 5;
        double currentCalories = ((carb * 4) + (protein * 4)) + (fat * 9);
        int calories = ((int) currentCalories);
        Nutrition nutrition = new Nutrition(calories,fat,protein,carb);
        nutrition.setUser(user);
        return nutritionRepository.save(nutrition);
    }

    // Метод сервиса
    public Nutrition weightLossCount(Long userId, Nutrition consumedNutrition) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        LocalDate today = LocalDate.now();

        // Ищем остаток на сегодня
        Optional<DailyNutritionRemaining> remainingOpt =
                dailyRemainingRepository.findByUserIdAndDate(userId, today);

        DailyNutritionRemaining dailyRemaining;

        if (remainingOpt.isEmpty()) {
            // Если остатка нет, создаем новый на основе дневной нормы
            Nutrition dailyNorm = weightLoss(user);
            dailyRemaining = new DailyNutritionRemaining();
            dailyRemaining.setUser(user);
            dailyRemaining.setDate(today);
            dailyRemaining.setRemainingCalories(dailyNorm.getCalories());
            dailyRemaining.setRemainingFat(dailyNorm.getFat());
            dailyRemaining.setRemainingProtein(dailyNorm.getProtein());
            dailyRemaining.setRemainingCarb(dailyNorm.getCarb());
        } else {
            dailyRemaining = remainingOpt.get();
        }

        // Вычисляем новый остаток
        double newRemainingFat = Math.max(0, dailyRemaining.getRemainingFat() - consumedNutrition.getFat());
        double newRemainingProtein = Math.max(0, dailyRemaining.getRemainingProtein() - consumedNutrition.getProtein());
        double newRemainingCarb = Math.max(0, dailyRemaining.getRemainingCarb() - consumedNutrition.getCarb());

        double newRemainingCalories = (newRemainingFat * 9) + (newRemainingProtein * 4) + (newRemainingCarb * 4);

        // Обновляем остаток
        dailyRemaining.setRemainingFat(newRemainingFat);
        dailyRemaining.setRemainingProtein(newRemainingProtein);
        dailyRemaining.setRemainingCarb(newRemainingCarb);
        dailyRemaining.setRemainingCalories((int) newRemainingCalories);

        // Сохраняем обновленный остаток
        dailyRemainingRepository.save(dailyRemaining);

        // Создаем результат для возврата
        Nutrition resultNutrition = new Nutrition((int) newRemainingCalories, newRemainingFat, newRemainingProtein, newRemainingCarb);
        resultNutrition.setUser(user);

        return nutritionRepository.save(resultNutrition);
    }
}
