package ch.erni.ai.demo.rag.rest;

import ch.erni.ai.demo.rag.model.ModelData;
import ch.erni.ai.demo.rag.service.LmStudioModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lmstudio")
@RequiredArgsConstructor
public class LmStudioController {

    private final LmStudioModelService lmStudioModelService;

    @GetMapping("/models")
    public List<ModelData> getModels() {
        return lmStudioModelService.getModels();
    }
}
