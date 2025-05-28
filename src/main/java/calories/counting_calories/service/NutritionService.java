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
        // Рассчитываем новые значения на основе текущего веса
        double weight = user.getWeight();
        double fat = weight * 0.5;
        double protein = weight * 1.5;
        double carb = weight * 3;
        double currentCalories = ((carb * 4) + (protein * 4) + (fat * 9));
        int calories = (int) currentCalories;

        // Получаем все записи пользователя
        List<Nutrition> existingNutritions = nutritionRepository.findAllByUserId(user.getId());

        if (!existingNutritions.isEmpty()) {
            // Удаляем все старые записи
            nutritionRepository.deleteAll(existingNutritions);
        }

        // Создаем новую единственную запись
        Nutrition nutrition = new Nutrition(calories, fat, protein, carb);
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


    public Nutrition weightLossCount(Long userId, Nutrition consumedNutrition) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        LocalDate today = LocalDate.now();

        Optional<DailyNutritionRemaining> remainingOpt =
                dailyRemainingRepository.findByUserIdAndDate(userId, today);

        DailyNutritionRemaining dailyRemaining;

        if (remainingOpt.isEmpty()) {
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

        // Вычисляем новые остатки
        double newRemainingFat = Math.max(0, dailyRemaining.getRemainingFat() - consumedNutrition.getFat());
        double newRemainingProtein = Math.max(0, dailyRemaining.getRemainingProtein() - consumedNutrition.getProtein());
        double newRemainingCarb = Math.max(0, dailyRemaining.getRemainingCarb() - consumedNutrition.getCarb());
        double newRemainingCalories = (newRemainingFat * 9) + (newRemainingProtein * 4) + (newRemainingCarb * 4);

        // Обновляем остатки в базе
        dailyRemaining.setRemainingFat(newRemainingFat);
        dailyRemaining.setRemainingProtein(newRemainingProtein);
        dailyRemaining.setRemainingCarb(newRemainingCarb);
        dailyRemaining.setRemainingCalories((int) newRemainingCalories);
        dailyRemainingRepository.save(dailyRemaining);

        // Возвращаем объект Nutrition с остатками (НЕ сохраняем в базу)
        Nutrition resultNutrition = new Nutrition((int) newRemainingCalories, newRemainingFat, newRemainingProtein, newRemainingCarb);
        resultNutrition.setUser(user);

        return resultNutrition;
    }
}
