package com.eof.libs.base.utils;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

/**
 * 字符串工具类
 * 
 */
public final class TextUtil {

	/**
	 * 把windows的文本转化为linux的文本格式，主要是换行符的处理; windows的换行符\r\n,而linux的换行符为\n
	 * 
	 * @param text
	 * @return
	 */
	public static String replaceRt(String text) {
		if (text != null) {
			StringBuilder sb = new StringBuilder();
			int len = text.length();
			int i = 0;
			int step = 1;
			while (i < len) {
				char c = text.charAt(i);
				if (c == '\r') {
					if (i < len - 1) {
						if (text.charAt(i + 1) == '\n') {
							step = 2;
						}
					}
					sb.append('\n');
				} else if (c == '\n') {
					if (i < len - 1) {
						if (text.charAt(i + 1) == '\r') {
							step = 2;
						}
					}
					sb.append('\n');
				} else if (c == (char) 0x2029 || c == (char) 0x0c) {
					sb.append('\n');
				} else {
					sb.append(c);
				}

				i += step;
				step = 1;
			}
			return sb.toString();
		}
		return text;
	}

	public static String replaceRt2Space(String text) {
		if (text != null) {
			StringBuilder sb = new StringBuilder();
			int len = text.length();
			int i = 0;
			int step = 1;
			while (i < len) {
				char c = text.charAt(i);
				if (c == '\r') {
					if (i < len - 1) {
						if (text.charAt(i + 1) == '\n') {
							step = 2;
						}
					}
					sb.append(' ');
				} else if (c == '\n') {
					if (i < len - 1) {
						if (text.charAt(i + 1) == '\r') {
							step = 2;
						}
					}
					sb.append(' ');
				} else if (c == (char) 0x2029 || c == (char) 0x0c) {
					sb.append(' ');
				} else {
					sb.append(c);
				}

				i += step;
				step = 1;
			}
			return sb.toString();
		}
		return text;
	}

	/**
	 * 判断字符串是否为null或者为空串(先trim)
	 * 
	 * @param s
	 *            输入串
	 * @return 是否为null或者为空串
	 */
	public static boolean isNullOrEmptyWithTrim(String s) {
		return (s == null || "".equals(s.trim()));
	}
	
	/**
	 * 判断字符串是否为null或者为空串（不trim）
	 * 
	 * @param s
	 *            输入串
	 * @return 是否为null或者为空串
	 */
	public static boolean isNullOrEmptyWithoutTrim(String s) {
		return (s == null || "".equals(s));
	}

	/**
	 * 判断字符串是否为空，如果为空则返回空串
	 * 
	 * @param s
	 *            字符串
	 * @return
	 */
	public static String emptyIfNull(String s) {
		return s == null ? "" : s;
	}

	

	/**
	 * 判断字符两个字符串是否相等,是null或者“”则认为相等
	 * 
	 * @param s1,s2
	 *            字符串
	 * @return
	 */
	public static boolean equalIgnoreNullAndEnmpty(String s1,String s2) {
		if(isNullOrEmptyWithoutTrim(s1)){
			return isNullOrEmptyWithoutTrim(s2);
		}else{
			return s1.equals(s2);
		}
	}

	
	/**
	 * HTML字符转移
	 * 
	 * @param s
	 * @return
	 */
	public static String htmlStrConvert(String s) {
		if (s == null)
			return null;
		int size = s.length();
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < size; i++) {
			char c = s.charAt(i);
			switch (c) {
			case '\"':
				sb.append("&quot;");
				break;
			case '&':
				sb.append("&amp;");
				break;
			case '<':
				sb.append("&lt;");
				break;
			case '>':
				sb.append("&gt;");
				break;
			case ' ':
				sb.append("&nbsp;");
				break;
			default:
				sb.append(c);
				break;

			}
		}
		return sb.toString();
	}

	/**
	 * 字符串高亮处理
	 * 
	 * @param s
	 *            要高亮处理的字符
	 * @param startPos
	 *            开始位置
	 * @param len
	 *            长度
	 * @param prefix
	 *            高亮前缀，HTML代码
	 * @param subfix
	 *            高亮后缀，HTML代码
	 * @return 加入前缀和后缀的字符串
	 */
	public static CharSequence highLightString(CharSequence s, int startPos, int len,
			int color) {
//		Log.i("MATCH", "startPos:" + startPos);
//		Log.i("MATCH", "Len:" + len);
		SpannableStringBuilder buf = new SpannableStringBuilder(s);
		ForegroundColorSpan span = new ForegroundColorSpan(color);
		if (startPos + len <= s.length()) {
			buf.setSpan(span, startPos, startPos + len,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return buf;
	}

	public static boolean isNumeric(String str) {
		for (int i = str.length(); --i >= 0;) {
			int chr = str.charAt(i);
			if (chr < 48 || chr > 57)
				return false;
		}
		return true;
	}
	

	/**
	 * 获取以 startWith 开头，以 endWith 结尾的字符串，不包含开头而且不包含结尾
	 * 例如 content 为 asdurl:http://www.baidu.com;132123，
	 * startWith 为 url:，endWith 为 ;，
	 * 则返回 http://www.baidu.com
	 */
	public static String getSpecifiedString(String content, String startWith, String endWith) {
		if(content == null) {
			return null;
		}
		if(startWith == null) {
			startWith = "";
		}
		if(endWith == null) {
			endWith = "";
		}
		if(startWith != null && endWith != null && (startWith.equals(endWith))) {
			return content;
		}
		int startIndex = 0;
		int endIndex = content.length();
		int index = content.indexOf(startWith);
		if(index > -1 && startWith != null) {
			startIndex = index + startWith.length();
		} else {
			return null;
		}
		if(startIndex != 0) {
			content = content.substring(startIndex);
		}
		index = content.indexOf(endWith);
		if(index > -1 && endWith != null) {
			endIndex = index;
		}
		return content.substring(0, endIndex);
	}

}
