package com.chuan.wojwebservice.service.impl;

import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojcommon.utils.ResultUtils;
import com.chuan.wojmodel.pojo.vo.JudgeCaseFileVO;
import com.chuan.wojwebservice.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.IgnoreForbiddenApisErrors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chuan_wxy
 *
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {
    @Value("${path.code.judgecase-path}")
    String judgeCasePath;


    @Override
    public BaseResponse<List<JudgeCaseFileVO>> uploadJudgeCase(MultipartFile file, String pid) {
        String originFileName = file.getOriginalFilename();
        // 使用 File.separator 保证 Win/Linux 通用
        File dir = new File(judgeCasePath + File.separator + pid);

        // 如果目录不存在，必须先创建
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) return ResultUtils.error("文件夹创建失败");
        }

        try {
            File destFile = new File(dir, originFileName);
            file.transferTo(destFile);

            log.info("文件已保存至: " + destFile.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error("保存失败: " + e.getMessage());
        }

        List<JudgeCaseFileVO> fileList = getFileListByPid(pid);

        return ResultUtils.success(fileList);
    }

    @Override
    public BaseResponse<List<JudgeCaseFileVO>> deleteJudgeCase(String fileName, String pid) {
        File targetFile = new File(judgeCasePath + File.separator + pid + File.separator + fileName);

        if (targetFile.exists() && targetFile.isFile()) {
            boolean deleted = targetFile.delete();
            if (!deleted) {
                return ResultUtils.error("文件删除失败，请检查系统权限");
            }
        } else {
            return ResultUtils.error("文件不存在，删除失败");
        }

        List<JudgeCaseFileVO> currentList = getFileListByPid(pid);
        return ResultUtils.success(currentList);
    }


    private List<JudgeCaseFileVO> getFileListByPid(String pid) {
        List<JudgeCaseFileVO> fileList = new ArrayList<>();
        File dir = new File(judgeCasePath + File.separator + pid);

        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    // 健壮性检查：防止获取隐藏文件或子目录
                    if (f.isFile()) {
                        String name = f.getName();
                        String suffix = name.contains(".") ? name.substring(name.lastIndexOf(".") + 1) : "";
                        fileList.add(new JudgeCaseFileVO(name, f.length(), suffix));
                    }
                }
            }
        }
        return fileList;
    }
}
