package com.nnu.chatrobot.controller;

import com.nnu.chatrobot.common.OssTemplate;
import com.nnu.chatrobot.common.R;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/test/oss")
public class OssTemplateTest {

    private final OssTemplate ossTemplate;

    @PostMapping("/upload")
    public R<String> upload(@RequestParam("file") MultipartFile file, @RequestParam("username")String username) throws Exception {
        return ossTemplate.upload(file,username);
    }

    @PostMapping("/batch")
    public List<String> upload(MultipartFile[] files) throws Exception {
        return ossTemplate.upload(files);
    }

    @GetMapping("/delete")
    public void delete(@RequestParam(name = "url") String url) throws Exception {
        ossTemplate.delete(url);
    }

    @PostMapping("/deletes")
    public void deletes(@RequestBody List<String> urlList) throws Exception {
        ossTemplate.delete(urlList);
    }

}