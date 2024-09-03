package com.example.catalogservice;

import com.example.catalogservice.vo.ResponseCatalog;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/catalog-service") // api gateway에서 호출할 때 사용할 이름
public class CatalogController {
    private final CatalogService catalogService;
    private final Environment evn;

    @GetMapping("/heath_check")
    public String status() {
        return String.format("It's Working in Catalog Service on PORT %s"
                , evn.getProperty("local.server.port"));
    }

    @RequestMapping("/catalogs")
    public ResponseEntity<List<ResponseCatalog>> getAllCatalogs() {
        Iterable<CatalogEntity> catalogList = catalogService.getAllCatalogs();

        List<ResponseCatalog> result = new ArrayList<>();
        catalogList.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseCatalog.class));
        });

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
