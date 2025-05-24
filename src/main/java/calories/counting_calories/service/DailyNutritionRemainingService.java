package calories.counting_calories.service;

import calories.counting_calories.model.DailyNutritionRemaining;
import calories.counting_calories.repository.DailyNutritionRemainingRepository;
import org.springframework.stereotype.Service;

@Service
public class DailyNutritionRemainingService {
    private final DailyNutritionRemainingRepository dailyNutritionRemainingRepository;

    public DailyNutritionRemainingService(DailyNutritionRemainingRepository dailyNutritionRemainingRepository){
        this.dailyNutritionRemainingRepository = dailyNutritionRemainingRepository;
    }
}
