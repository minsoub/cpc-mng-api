package com.bithumbsystems.cpc.api.core.util;

import com.bithumbsystems.cpc.api.core.exception.InvalidParameterException;
import com.bithumbsystems.cpc.api.core.model.enums.ErrorCode;
import java.util.Arrays;
import org.springframework.util.StringUtils;

public class ValidationUtils {

  public static void assertAllowFileExt(String fileName, String[] allowExt) {
    String ext = StringUtils.getFilenameExtension(fileName).toUpperCase();

    if(Arrays.asList(allowExt).contains(ext) == false){
      throw new InvalidParameterException(ErrorCode.NOT_ALLOWED_FILE_EXT);
    }
  }

  public static void assertAllowFileSize(Long fileSize, Long maxFileSize) {
    if(fileSize > maxFileSize) {
      throw new InvalidParameterException(ErrorCode.NOT_ALLOWED_FILE_SIZE);
    }
  }
}
