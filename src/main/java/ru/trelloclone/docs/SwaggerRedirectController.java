package ru.trelloclone.docs;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Springdoc's own {@code springdoc.swagger-ui.path} redirect breaks once the configured
 * page path is nested under a prefix like {@code /api} (it strips only the last path
 * segment when computing the redirect to its static {@code /swagger-ui/index.html}
 * assets), so {@code /api/docs} is served here directly instead.
 */
@Controller
@SecurityRequirements
public class SwaggerRedirectController {

    @GetMapping("/api/docs")
    public String redirectToSwaggerUi() {
        return "redirect:/swagger-ui/index.html";
    }
}
