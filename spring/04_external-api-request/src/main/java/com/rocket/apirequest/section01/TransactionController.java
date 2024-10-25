package com.rocket.apirequest.section01;

import com.rocket.apirequest.section01.dto.RequestDTO;
import com.rocket.apirequest.section01.dto.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/*
* Spring에서 외부 API 요청 및 처리
*
* 대표적인 라이브러리
* - HTTPClient
* - RestTemplate
* - WebClient
* - OpenFeign
*
* 주의할점
* - request와 response가 외부서버와 맞게 설정되어있는지 확인!
* */

@RestController
@RequestMapping("/translate")
@Slf4j
public class TransactionController {

    private final RestTemplateService restTemplateService;
    private final WebClientService webClientService;

    // 생성자 주입
    public TransactionController(RestTemplateService restTemplateService, WebClientService webClientService) {
        this.restTemplateService = restTemplateService;
        this.webClientService = webClientService;
    }

    @GetMapping("/test")
    public String test() {

        log.info("/test 로 get 요청 들어옴...");

        return "test 성공";
    }

    @PostMapping("/resttemplate")
    public ResponseDTO translateByRestTemplate(@RequestBody RequestDTO requestDTO) {

        log.info("번역[RestTemplate] Controller 요청 들어옴...");
        log.info("text: {}, lang: {}",requestDTO.getText(), requestDTO.getLang());

        ResponseDTO result = restTemplateService.translateText(requestDTO);

        return result;
    }

    @PostMapping("/webclient")
    public ResponseDTO translateByWebClient(@RequestBody RequestDTO requestDTO) {

        log.info("번역[webclient] Controller 요청 들어옴...");
        log.info("text: {}, lang: {}",requestDTO.getText(), requestDTO.getLang());

        ResponseDTO result = webClientService.translateText(requestDTO);

        return result;
    }

    @PostMapping("/uploadfile")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file){
        try {
            String result = restTemplateService.uploadFile(file);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("파일 업로드 실패: " + e.getMessage());
        }
    }
}
