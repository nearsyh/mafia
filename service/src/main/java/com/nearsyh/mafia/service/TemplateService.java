package com.nearsyh.mafia.service;

import com.nearsyh.mafia.protos.Template;
import java.util.List;
import java.util.Map;
import reactor.core.publisher.Mono;

public interface TemplateService {

    Mono<Template> getTemplate(int playersNumber, String name);

    Mono<Template> upsertTemplate(int playersNumber, String name, Map<String, Integer> counts);

    Mono<List<String>> listTemplates(int playersNumber);

    Mono<Template> deleteTemplate(int playersNumber, String name);

}
