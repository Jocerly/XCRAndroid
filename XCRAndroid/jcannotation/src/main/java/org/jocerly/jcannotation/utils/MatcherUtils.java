package org.jocerly.jcannotation.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则验证类
 * 
 * @author Administrator
 * 
 */
public class MatcherUtils {

	/**
	 * 检查日期格式
	 * 
	 * @param date
	 * @return
	 */
	public static boolean checkDate(String date) {
		String eL = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))"
				+ "[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?"
				+ "((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|"
				+ "(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))"
				+ "[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|"
				+ "([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|"
				+ "([1][0-9])|([2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";
		Pattern p = Pattern.compile(eL);
		Matcher m = p.matcher(date);
		boolean b = m.matches();
		return b;
	}

	/**
	 * 检查整数
	 * 
	 * @param num
	 * @param type
	 *            "0+":非负整数 "+":正整数 "-0":非正整数 "-":负整数 "":整数
	 * @return
	 */
	public static boolean checkNumber(String num, String type) {
		String eL = "";
		if (type.equals("0+"))
			eL = "^\\d+$";// 非负整数
		else if (type.equals("+"))
			eL = "^\\d*[1-9]\\d*$";// 正整数
		else if (type.equals("-0"))
			eL = "^((-\\d+)|(0+))$";// 非正整数
		else if (type.equals("-"))
			eL = "^-\\d*[1-9]\\d*$";// 负整数
		else
			eL = "^-?\\d+$";// 整数
		Pattern p = Pattern.compile(eL);
		Matcher m = p.matcher(num);
		boolean b = m.matches();
		return b;
	}

	/**
	 * 检查数目的限制
	 * 
	 * @return
	 */
	public static boolean checkNumberSize(String num, double min, double max) {
		double n = Double.parseDouble(num);
		if ((n > min || n == min) && (n < max || n == max)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 验证固定电话号码、手机号
	 * 
	 * @param phone 电话号码，格式：国家（地区）电话代码 + 区号（城市代码） + 电话号码，如：+8602085588447
	 * 				移动、联通、电信运营商的号码段
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkPhoneNum(String phone) {
		String regex = "(^(\\d{3,4})?\\d{7,8})$|((\\+\\d+)?1[3458]\\d{9})$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(phone);
		return m.find();
	}
	
	/**
	 * 验证手机号
	 * 
	 * @param phone  移动、联通、电信运营商的号码段
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkMobilePhoneNum(String phone) {
		String regex = "^[1][34578]\\d{9}$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(phone);
		return m.matches();
	}

	/**
	 * 判断是否含有特殊字符
	 * 
	 * @param str
	 * @return
	 */
	public static boolean checkSpecialChar(String str) {
		String regEx = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.find();
	}

	/**
	 * 检查浮点数
	 * 
	 * @param num
	 * @param type
	 *            "0+":非负浮点数 "+":正浮点数 "-0":非正浮点数 "-":负浮点数 "":浮点数
	 * @return
	 */
	public static boolean checkFloat(String num, String type) {
		String eL = "";
		if (type.equals("0+"))
			eL = "^\\d+(\\.\\d+)?$";// 非负浮点数
		else if (type.equals("+"))
			eL = "^((\\d+\\.\\d*[1-9]\\d*)|(\\d*[1-9]\\d*\\.\\d+)|(\\d*[1-9]\\d*))$";// 正浮点数
		else if (type.equals("-0"))
			eL = "^((-\\d+(\\.\\d+)?)|(0+(\\.0+)?))$";// 非正浮点数
		else if (type.equals("-"))
			eL = "^(-((\\d+\\.\\d*[1-9]\\d*)|(\\d*[1-9]\\d*\\.\\d+)|(\\d*[1-9]\\d*)))$";// 负浮点数
		else
			eL = "^(-?\\d+)(\\.\\d+)?$";// 浮点数
		Pattern p = Pattern.compile(eL);
		Matcher m = p.matcher(num);
		boolean b = m.matches();
		return b;
	}

	/**
	 * 检查用户名，账号必须为5-16位字母、数字或下划线
	 * @param str
	 * @return
	 */
	public static boolean checkAccount(String str) {
		if (StringUtils.isEmpty(str)) {
			return false;
		}
		return str.matches("^[a-zA-Z0-9_]{5,16}$");
	}
	
	/**
	 * 检查用户名是纯数字
	 * @param str
	 * @return true是，false否
	 */
	public static boolean checkAccountAllNum(String str) {
		if (StringUtils.isEmpty(str)) {
			return false;
		}
		Pattern p = Pattern.compile("[0-9]+");
		Matcher m = p.matcher(str);
		boolean b = m.matches();
		return b;
	}
	
	/**
	 * 判断密码
	 * @param str
	 * @return
	 */
	public static boolean chechPwd(String str) {
		if (StringUtils.isEmpty(str)) {
			return false;
		}
		return str.matches("^[a-zA-Z0-9]{6,16}$");
	}
	
	/**
	 * 检查是否有中文
	 * @param str
	 * @return
	 */
	public static boolean checkChinese(String str) {
		if (StringUtils.isEmpty(str)) {
			return false;
		}
		return str.matches("^[\u4E00-\u9FA5]+$");
	}
	
	/**
	 * 检查银行卡名称2-8个汉字
	 * @param str
	 * @return
	 */
	public static boolean checkBankName(String str) {
		if (StringUtils.isEmpty(str)) {
			return false;
		}
		return str.matches("^[\u4E00-\u9FA5]{2,8}$");
	}
	
	private static String ALLNUMBER = "^\\d{6,50}";
	// 身份证号码
	private static String IDCARD = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])(\\d{3}(\\d|X|x))$";

	/**
	 * 判断是否为全数字
	 * 
	 * @param matchStr
	 * @return
	 */
	public static boolean isAllNumber(String matchStr) {
		return matchStr == null ? false : matchStr.matches(ALLNUMBER);
	}

	/**
	 * 判断身份证号码是否合法
	 * 
	 * @param matcherStr
	 * @return
	 */
	public static boolean isIdCard(String matcherStr) {
		return matcherStr == null ? false : matcherStr.matches(IDCARD);
	}
	
	public static int getNumWithStr(String paramString)
	  {
	    int i = 0;
	    for (int j = 0; ; j++)
	    {
	      if (j >= paramString.length())
	        return i;
	      int k = paramString.charAt(j);
	      if (((k >= 48) && (k <= 57)) || ((k >= 97) && (k <= 122)) || ((k >= 65) && (k <= 90)) || (k == 40) || (k == 41) || (k == 43) || (k == 45) || (k == 61) || (k == 126) || (k == 33) || (k == 64) || (k == 35) || (k == 36) || (k == 37) || (k == 94) || (k == 38) || (k == 42) || (k == 60) || (k == 62) || (k == 63) || (k == 32) || (k == 124) || (k == 91) || (k == 93) || (k == 123) || (k == 125) || (k == 58) || (k == 63) || (k == 44) || (k == 46) || (k == 47) || (k == 59) || (k == 39) || (k == 92) || (k == 34))
	        i++;
	    }
	  }

	/**
	 * 是否包含数字
	 * @return
	 */
	public static boolean hasNUm(String matcherStr) {
		String regex = "[0-9]+?";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(matcherStr);
		return m.find();
	}
}
