package hexfive.ismedi.openApi;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/drug-info")
public class DrugInfoController {
    private final DrugInfoService drugInfoService;

    @RequestMapping("/init")
    public void fetchAll() throws Exception {
        drugInfoService.fetchAll();
    }

    @RequestMapping("/{pageNo}")
    public void fetchPage(@PathVariable int pageNo) throws Exception {
        drugInfoService.fetchPage(pageNo);
    }
}
