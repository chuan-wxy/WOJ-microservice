package com.chuan.wojwebservice.service;

import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojmodel.pojo.vo.JudgeCaseFileVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author chuan_wxy
 *
 */
public interface FileService {
    BaseResponse<List<JudgeCaseFileVO>> uploadJudgeCase(MultipartFile file, String pid);

    BaseResponse<List<JudgeCaseFileVO>> deleteJudgeCase(String fileName, String pid);
}
