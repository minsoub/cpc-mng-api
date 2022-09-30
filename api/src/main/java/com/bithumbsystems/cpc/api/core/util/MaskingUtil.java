package com.bithumbsystems.cpc.api.core.util;

import org.springframework.util.StringUtils;

/**
 * 마스킹 유틸 클래스
 */
public class MaskingUtil {
  /**
   * 이메일 마스킹
   * 이메일 주소는 아이디의 앞 3자리 및 @ 이후를 제외한 문자, 숫자 표시 제한(abc****@abc.com, abc@abc.com)
   * @param email
   * @return
   */
  public static String getEmailMask(String email) {

    if (StringUtils.hasLength(email)) {

      return email.replaceAll("(?<=.{3}).(?=.*@)", "*");
    } else {
      return email;
    }
  }

  /**
   * Phone 마스킹
   * 전화번호 국번 외 앞 2자리 및 뒤 2자리를 제외한 숫자 표시 제한(ex : 02-12**-**78, 031-12**-**78)
   * 휴대폰 번호 앞 5자리 및 뒤 2자리를 제외한 숫자 표시 제한(ex : 010-12**-**34, 010-12*-**34)
   *
   * @param phoneNum
   * @return
   */
  public static String getPhoneMask(String phoneNum) {
    if (StringUtils.hasLength(phoneNum)) {
      phoneNum = phoneNum.substring(0,3) + "****"
          + phoneNum.substring(phoneNum.length()-4, phoneNum.length());

      return phoneNum;
    } else {
      return phoneNum;
    }
  }

  /**
   * 이름 마스킹
   * 성을 제외한 이름의 첫 번째 자리 표시 제한(ex : 홍*동, 남궁*분, 고*)
   * @param name
   * @return
   */
  public static String getNameMask(String name) {

    if (StringUtils.hasLength(name)) {
      String middleMask = "";
      // 이름이 외자 또는 4자 이상인 경우 분기
      if(name.length() > 2){
        middleMask = name.substring(1, name.length()-1);
      } else {
        middleMask = name.substring(1, name.length()); // 외자
      }
      // 마스킹 변수 선언(*)
      String masking = "";
      // 가운데 글자 마스킹 하기위한 증감값
      for(int i = 0; i < middleMask.length(); i++){
        masking += "*";
      }
      // 이름이 외자 또는 4자 이상인 경우 분기
      if(name.length() > 2){
        name = name.substring(0,1)
            + middleMask.replace(middleMask, masking)
            + name.substring(name.length()-1, name.length());
      } else { // 외자인경우
        name = name.substring(0,1)
            + middleMask.replace(middleMask, masking);
      }
      return name;
    }else {
      return name;
    }
  }
}