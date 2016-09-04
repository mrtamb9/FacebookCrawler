package processor;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneNumber {
	
	public static ArrayList<String> getListPhoneNumbers(String input)
	{
		ArrayList<String> listPhoneNumbers = new ArrayList<String>();
		Pattern pattern = Pattern.compile("0\\d[\\s\\d-.]{5,}\\d");
		Matcher matcher = pattern.matcher(input);
		if (matcher.find()) {
		    do {
		    	String candidate = matcher.group();
		    	candidate = candidate.replace(".", "");
		    	candidate = candidate.replace("-", "");
		    	candidate = candidate.replace(" ", "");
		    	if(candidate.length()>=9 && candidate.length()<=11)
		    	{
		    		listPhoneNumbers.add(candidate);
		    	}
		    } while (matcher.find());
		}
		return listPhoneNumbers;
	}
	
	public static void main(String [] args)
	{
		String input = " cho em 1 cái màu đen dài 80-130 cm nhé. Địa chỉ : thôn PHÚ LƯƠNG - xã NAM ĐỒNG - tp. HẢI DƯƠNG - tỉnh HẢI DƯƠNG. Bưu điện tỉnh HẢI DƯƠNG. Số điện thoại: 0989468107. Họ và tên: LÊ HOÀNG HIỆP.";
		ArrayList<String> listPhoneNumbers = PhoneNumber.getListPhoneNumbers(input);
		System.out.println(listPhoneNumbers);
	}
}
