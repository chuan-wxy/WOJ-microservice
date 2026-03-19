package com.chuan.wojwebservice.controller;

import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojcommon.exception.StatusSystemErrorException;
import com.chuan.wojcommon.utils.ResultUtils;
import com.chuan.wojmodel.pojo.vo.JudgeCaseFileVO;
import com.chuan.wojwebservice.service.FileService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: chuan-wxy
 * @Date: 2024/8/18 9:02
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    FileService fileService;

    @PostMapping("/admin/upload-judgecase")
    public BaseResponse<List<JudgeCaseFileVO>> uploadJudgeCase(@RequestParam("file") MultipartFile file, @RequestParam("pid") String pid) {
        if (file.isEmpty()) {
            return ResultUtils.error("上传文件为空");
        }
        return fileService.uploadJudgeCase(file, pid);
    }

    @PostMapping("/admin/delete-judgecase")
    public BaseResponse<List<JudgeCaseFileVO>> deleteJudgeCase(@NotBlank @RequestParam("fileName") String fileName,@NotBlank @RequestParam("problemId")  String pid) {
        return fileService.deleteJudgeCase(fileName, pid);
    }
}
