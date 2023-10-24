package com.example.auth.controller;

import com.example.auth.dto.SearchUser;
import com.example.auth.dto.request.ReqBirthDate;
import com.example.auth.dto.response.ResUser;
import com.example.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping(value = "/detail")
    public ResponseEntity<?> getCurrentUserDetail() {
        ResUser user = userService.getUserDetail();
        System.out.println(org.hibernate.Version.getVersionString());
        return ResponseEntity.ok(user);
    }
    @GetMapping(value = "/check-user")
    public ResponseEntity<?> getCheckUser() {
        ResUser user = userService.getByRole();
        return ResponseEntity.ok(user);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<?> getSEarchUser(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "username", required = false) String username, 
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size
            ) {
        SearchUser searchParams = SearchUser.builder().name(name).username(username).page(page).size(size).build();
        Page<ResUser> contactResponses = userService.searchUser(searchParams);
        return ResponseEntity.ok(contactResponses);
    }

    @PostMapping(value = "/custom-validation" )
    public ResponseEntity<?> postTestCustomValidation(@Valid @RequestBody ReqBirthDate request) {

        return ResponseEntity.ok(request);
    }

    @PostMapping(value = "/upload-file", consumes =   MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> postUploadFile(@RequestParam(name = "file", required = true) MultipartFile file) throws IOException {
//        String extension = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        Path filepath = Paths.get( "/" + file.getOriginalFilename()  );
        // save file
        File output = filepath.toFile();
        FileOutputStream fop = new FileOutputStream(output);
        fop.write(file.getBytes());
        fop.flush();
        fop.close();
        log.info(file.getOriginalFilename());
        log.info(filepath.toString());
        return file.isEmpty() ?
                new ResponseEntity<String>(HttpStatus.NOT_FOUND) : new ResponseEntity<String>(HttpStatus.OK);
    }

    @GetMapping(value = "/download-file" )
    public ResponseEntity<?> getDownloadFile() throws IOException {
        String filePath = "D:\\reviewcv\\pom.xml";
        java.io.File file = new java.io.File(filePath);
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String mimeType = fileNameMap.getContentTypeFor(filePath);
        log.info(mimeType);
        Path resultFile = Paths.get(filePath);
        byte[] data = Files.readAllBytes(resultFile);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(mimeType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "File : " + file.getName())
                .body(data);
    }
}
