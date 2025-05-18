package calories.counting_calories.controller;

import calories.counting_calories.model.Nutrition;
import calories.counting_calories.model.User;
import calories.counting_calories.repository.UserRepository;
import calories.counting_calories.service.NutritionService;
import calories.counting_calories.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class    NutritionController {
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

    @GetMapping("/result")
    public String showCalories(@RequestParam Long userId, Model model) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        Nutrition nutrition = nutritionService.weightLoss(user);

        model.addAttribute("user",user);
        model.addAttribute("nutrition",nutrition);

        return "result";

    }
}
