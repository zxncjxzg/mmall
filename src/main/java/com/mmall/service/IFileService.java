package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by 11790 on 2018/11/20.
 */
public interface IFileService {
    String upload(MultipartFile file, String path);
}
