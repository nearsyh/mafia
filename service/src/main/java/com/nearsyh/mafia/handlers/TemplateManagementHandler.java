package com.nearsyh.mafia.handlers;

import com.nearsyh.mafia.protos.Template;
import com.nearsyh.mafia.service.TemplateService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class TemplateManagementHandler {

    private final TemplateService templateService;

    public TemplateManagementHandler(TemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping("/games/templates")
    public Mono<Template> getTemplate(
        @RequestParam("playersNumber") int playersNumber,
        @RequestParam("name") String name) {
        return templateService.getTemplate(playersNumber, name);
    }

    @PostMapping("/games/templates")
    public Mono<Template> upsertTemplate(
        @RequestParam("playersNumber") int playersNumber,
        @RequestParam("name") String name,
        @RequestBody Map<String, Integer> charactersCount) {
        return templateService.upsertTemplate(playersNumber, name, charactersCount);
    }

    @GetMapping("/games/templates:list")
    public Mono<List<String>> listTemplates(
        @RequestParam("playersNumber") int playersNumber) {
        return templateService.listTemplates(playersNumber);
    }

    @PostMapping("/games/templates:delete")
    public Mono<Template> deleteTemplate(
        @RequestParam("playersNumber") int playersNumber,
        @RequestParam("name") String name) {
        return templateService.deleteTemplate(playersNumber, name);
    }

}
