package com.example.auth.controller;

import com.example.auth.dto.SearchUser;
import com.example.auth.dto.request.ReqBirthDate;
import com.example.auth.dto.response.ResCommon;
import com.example.auth.dto.response.ResUser;
import com.example.auth.entity.User;
import com.example.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    UserService userService;
    @Autowired
    UserController(UserService userService) {
        this.userService=userService;
    }

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
    public ResponseEntity<?> getSEarchUser(@RequestParam(value = "name", required = false) String name, @RequestParam(value = "username", required = false) String username, @RequestParam(value = "page", required = false, defaultValue = "0") Integer page, @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        SearchUser searchParams = SearchUser.builder().name(name).username(username).page(page).size(size).build();
        Page<ResUser> contactResponses = userService.searchUser(searchParams);
        return ResponseEntity.ok(contactResponses);
    }

    @PostMapping(value = "/custom-validation")
    public ResponseEntity<?> postTestCustomValidation(@Valid @RequestBody ReqBirthDate request) {

        return ResponseEntity.ok(request);
    }

    @PostMapping(value = "/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> postUploadFile(@RequestParam(name = "file") MultipartFile file) throws IOException {
//        String extension = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        Path filepath = Paths.get("/" + file.getOriginalFilename());
        // save file
        File output = filepath.toFile();
        FileOutputStream fop = new FileOutputStream(output);
        fop.write(file.getBytes());
        fop.flush();
        fop.close();
        log.info(file.getOriginalFilename());
        log.info(filepath.toString());
        List<String> list = new ArrayList<>();
        list.add("test");
        ResCommon response = ResCommon.builder().code(0).message("file uploaded").data(list).build();
        return ResponseEntity.ok(response);

//        return file.isEmpty() ? new ResponseEntity<String>(HttpStatus.NOT_FOUND) : new ResponseEntity<String>(HttpStatus.OK).bo;
    }

    @GetMapping(value = "/download-report")
    public ResponseEntity<?> getDownloadReport() {

        JasperReport jasperReport;

        try {

            jasperReport = (JasperReport) JRLoader.loadObject(ResourceUtils.getFile("example-report.jasper"));
        } catch (FileNotFoundException | JRException e) {
            try {
//                file template yang akan di compile dari classpath
                File file = ResourceUtils.getFile("classpath:jasper/example-report.jrxml");
                jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
//                dikompile tapi pada directory project atau jar nya
                JRSaver.saveObject(jasperReport, "example-report.jasper");
            } catch (FileNotFoundException | JRException ex) {
                throw new RuntimeException(e);
            }
        }

//        isi data ke file jasper
        List<User> items = userService.getUsers();
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(items);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("title", "Item Report");
        JasperPrint jasperPrint;
        byte[] reportContent;

        try {
            jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            reportContent = JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (JRException e) {
            throw new RuntimeException(e);
        }
        ByteArrayResource resource = new ByteArrayResource(reportContent);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).contentLength(resource.contentLength()).header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("item-report.pdf").build().toString()).body(resource);
    }
}
