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
    private final double PROTEINTCOUNTFORLOSS = 1.5;
    private final double FATCOUNTFORLOSS = 0.5;
    private final double CARBCOUNTFORLOSS = 3;
    private final double PROTEININTTOCALORIES = 4;
    private final double FATINTTOCALORIES = 9;
    private final double CARBINTTOCALORIES = 4;
    private final double PROTEINCOUNTFORGAIN = 2;
    private final double FATCOUNTFORGAIN = 0.75;
    private final double CARBCOUNTFORGAIN = 5;

    public NutritionService(NutritionRepository nutritionRepository, UserRepository userRepository) {
        this.nutritionRepository = nutritionRepository;
        this.userRepository = userRepository;
    }


    public Nutrition weightLoss(User user) {
        double weight = user.getWeight();
        double fat = weight * FATCOUNTFORLOSS;
        double protein = weight * PROTEINTCOUNTFORLOSS;
        double carb = weight * CARBCOUNTFORLOSS;
        double currentCalories = ((carb * CARBINTTOCALORIES) + (protein * PROTEININTTOCALORIES) + (fat * FATINTTOCALORIES));
        int calories = (int) currentCalories;

        List<Nutrition> existingNutritions = nutritionRepository.findAllByUserId(user.getId());

        if (!existingNutritions.isEmpty()) {
            nutritionRepository.deleteAll(existingNutritions);
        }

        Nutrition nutrition = new Nutrition(calories, fat, protein, carb);
        nutrition.setUser(user);

        return nutritionRepository.save(nutrition);
    }

    public Nutrition weightGain(User user) {
        double weight = user.getWeight();
        double fat  = weight * FATCOUNTFORGAIN;
        double protein = weight * PROTEINCOUNTFORGAIN;
        double carb = weight * CARBCOUNTFORGAIN;
        double currentCalories = ((carb * CARBINTTOCALORIES) + (protein * PROTEININTTOCALORIES)) + (fat * FATINTTOCALORIES);
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

        double newRemainingFat = Math.max(0, dailyRemaining.getRemainingFat() - consumedNutrition.getFat());
        double newRemainingProtein = Math.max(0, dailyRemaining.getRemainingProtein() - consumedNutrition.getProtein());
        double newRemainingCarb = Math.max(0, dailyRemaining.getRemainingCarb() - consumedNutrition.getCarb());
        double newRemainingCalories = (newRemainingFat * 9) + (newRemainingProtein * 4) + (newRemainingCarb * 4);

        dailyRemaining.setRemainingFat(newRemainingFat);
        dailyRemaining.setRemainingProtein(newRemainingProtein);
        dailyRemaining.setRemainingCarb(newRemainingCarb);
        dailyRemaining.setRemainingCalories((int) newRemainingCalories);
        dailyRemainingRepository.save(dailyRemaining);


        Nutrition resultNutrition = new Nutrition((int) newRemainingCalories, newRemainingFat, newRemainingProtein, newRemainingCarb);
        resultNutrition.setUser(user);

        return resultNutrition;
    }
}
