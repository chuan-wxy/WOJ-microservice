package com.chuan.wojjudgeservice.codesandbox;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * 沙箱通用模板
 */
public class CommonCodeSandboxTemplate
{
    /**
     * 把用户的代码保存为文件
     *
     * @param code           用户代码
     * @param parentPath 全局代码路径
     * @param fileName       文件名
     * @return
     */
    public File saveCodeToFile(String code, String parentPath, String fileName)
    {
        // 判断全局代码目录是否存在，没有则新建
        if (!FileUtil.exist(parentPath))
        {
            FileUtil.mkdir(parentPath);
        }

        // 把用户的代码隔离存放
        String userCodePath = parentPath + File.separator + fileName;
        File userCodeFile = FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);

        return userCodeFile;
    }

    /**
     * 删除文件
     *
     * @param userCodeFile
     * @return
     */
    public boolean deleteFile(File userCodeFile)
    {
        if (userCodeFile.getParentFile() != null)
        {
            String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();
            boolean del = FileUtil.del(userCodeParentPath);
            System.out.println("删除" + (del ? "成功" : "失败"));
            return del;
        }
        return true;
    }

}
