package calories.counting_calories.controller;

import calories.counting_calories.model.Nutrition;
import calories.counting_calories.model.User;
import calories.counting_calories.repository.NutritionRepository;
import calories.counting_calories.repository.UserRepository;
import calories.counting_calories.service.NutritionService;
import calories.counting_calories.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class    NutritionController {
    private NutritionRepository nutritionRepository;
    private final NutritionService nutritionService;
    private final UserRepository userRepository;
    private final UserService userService;

    public NutritionController(
            NutritionService nutritionService,
            UserRepository userRepository,
            UserService userService) {
        this.nutritionService = nutritionService;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/weight/loss")
    public String showLossCalories(@RequestParam Long userId, Model model) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        // Получаем дневную норму
        Nutrition dailyNorm = nutritionService.weightLoss(user);

        // Создаем пустые значения для первого отображения
        Nutrition consumedNutrition = new Nutrition(0, 0.0, 0.0, 0.0);
        Nutrition remainingNutrition = dailyNorm; // изначально остается вся норма

        model.addAttribute("user", user);
        model.addAttribute("dailyNorm", dailyNorm);
        model.addAttribute("consumedNutrition", consumedNutrition);
        model.addAttribute("remainingNutrition", remainingNutrition);

        return "result-loss";
    }

    @PostMapping("/weight/loss")
    public String countingCalories(
            @RequestParam Long userId,
            @RequestParam double fat,
            @RequestParam double protein,
            @RequestParam double carb,
            Model model
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        // Это то, что пользователь уже съел
        Nutrition consumedNutrition = new Nutrition(0, fat, protein, carb);

        // Получаем остаток от дневной нормы
        Nutrition remainingNutrition = nutritionService.weightLossCount(userId, consumedNutrition);

        // Получаем дневную норму для отображения
        Nutrition dailyNorm = nutritionService.weightLoss(user);

        model.addAttribute("user", user);
        model.addAttribute("consumedNutrition", consumedNutrition); // съеденное
        model.addAttribute("remainingNutrition", remainingNutrition); // остаток
        model.addAttribute("dailyNorm", dailyNorm); // дневная норма

        return "result-loss";
    }


    @GetMapping("/weight/gain")
    public String showGainCalories(@RequestParam Long userId, Model model) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        Nutrition nutrition = nutritionService.weightGain(user);

        model.addAttribute("user",user);
        model.addAttribute("nutrition",nutrition);

        return "result-loss";
    }

}
