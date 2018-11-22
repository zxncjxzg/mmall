package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by 11790 on 2018/11/20.
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService {
    private Logger logger= LoggerFactory.getLogger(FileServiceImpl.class);

    /**
     * 上传文件
     * @param file 文件对象
     * @param path 文件路径
     * @return
     */
    public String upload(MultipartFile file,String path){
        String fileName=file.getOriginalFilename();
        //获取文件的扩展名
        String fileExtensionName=fileName.substring(fileName.lastIndexOf(".")+1);
        //为上传的文件重新设置不会重复的文件名
        String uploadFileName= UUID.randomUUID().toString()+"."+fileExtensionName;
        //{}为占位符
        logger.info("开始上传文件，上传文件的文件名：{}，上传的路径：{}，新文件名：{}",fileName,path,uploadFileName);
        //File类主要用于文件和目录的创建、文件的查找和文件的删除
        File fileDir=new File(path);//创建文件夹
        if(!fileDir.exists()){//如果文件夹不存在
            fileDir.setWritable(true);
            fileDir.mkdirs();//注意与mkdir方法的区别，mkdirs方法可同时创建多层级目录
        }
        File targetFile=new File(path,uploadFileName);//创建文件
        try {
            //todo transferTo方法的作用
            file.transferTo(targetFile);//文件已经上传成功了
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));//已经上传到ftp服务器上
            targetFile.delete();
        } catch (IOException e) {
            logger.error("上传文件异常",e);
            return null;
        }
        return targetFile.getName();
    }
}
