package com.rocket.apirequest.section01;

import com.rocket.apirequest.section01.dto.RequestDTO;
import com.rocket.apirequest.section01.dto.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/*
* RestTemplate
*
* Spring에서 지원하는 객체로 간편하게 Rest 방식 API를 호출할 수 있는 Spring 내장 클래스
* RestAPI 서비스를 요청후 응답 받을 수 있게 설계되어 있음.
*
* 특징
* - 간단하고 직관적인 사용법
* - 동기식 처리로 이해하기 쉽다.
* */

@Service
@Slf4j

public class RestTemplateService {

    private final RestTemplate restTemplate;

    // 요청보낼 URL
    private final String FAST_API_SERVER_URL = "http://localhost:8000/translate";

    public RestTemplateService(RestTemplate restTemplate) {
        this.restTemplate = new RestTemplate();
    }

    public ResponseDTO translateText(RequestDTO requestDTO) {

        // 1. HttpHeader
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 2. requestBody
        // header + body
        HttpEntity<RequestDTO> entity = new HttpEntity<>(requestDTO, headers);

        // 3. HTTP 메서드
        // 4. 응답을 받을 타입

        try {
            ResponseEntity<ResponseDTO> response = restTemplate.exchange(
                    FAST_API_SERVER_URL,    // 요청 url
                    HttpMethod.POST,        // HTTP 요청 메서드
                    entity,                 // 요청 entity(헤더 + 본문)
                    ResponseDTO.class       // 응답 본문을 반환할 타입 (JSON -> ResponseDTO)
            );

            log.info("=== 번역 서비스 응답 데이터 ===");
            log.info("번역 결과 : {}", response.getBody());

            return response.getBody();
        } catch (RestClientException e) {
            throw new RuntimeException(e);
        }
    }

    public String uploadFile(MultipartFile file) throws IOException {
        // 파일 내용을 ByteArray로 읽기
        byte[] fileContent = file.getBytes();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Multipart 요청 생성
        HttpEntity<ByteArrayResource> requestEntity = new HttpEntity<>(
                new ByteArrayResource(fileContent) {
                    @Override
                    public String getFilename() {
                        return file.getOriginalFilename(); // 원본 파일 이름 설정
                    }
                }, headers);

        // API 호출
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:8000/uploadfile", HttpMethod.POST, requestEntity, String.class);

        // 결과 반환
        return response.getBody();
    }
}
