package com.bithumbsystems.cpc.api.core.util;

import com.bithumbsystems.cpc.api.core.exception.InvalidParameterException;
import com.bithumbsystems.cpc.api.core.model.enums.ErrorCode;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.util.StringUtils;

public class ValidationUtils {

  public static void assertEmailFormat(String value) throws InvalidParameterException {
    String regexp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    Pattern pattern = Pattern.compile(regexp);
    Matcher matcher = pattern.matcher(value);
    if (!matcher.matches()) {
      throw new InvalidParameterException(ErrorCode.INVALID_EMAIL_FORMAT);
    }
  }

  public static void assertNameFormat(String value) throws InvalidParameterException {
    String regexp = "^[a-zA-Z0-9가-힣. ]*$";
    Pattern pattern = Pattern.compile(regexp);
    Matcher matcher = pattern.matcher(value);
    if (!matcher.matches()) {
      throw new InvalidParameterException(ErrorCode.INVALID_NAME_FORMAT);
    }
  }

  public static void assertCellPhoneFormat(String value) throws InvalidParameterException {
    String regexp = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$";
    Pattern pattern = Pattern.compile(regexp);
    Matcher matcher = pattern.matcher(value);
    if (!matcher.matches()) {
      throw new InvalidParameterException(ErrorCode.INVALID_PHONE_FORMAT);
    }
  }

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
