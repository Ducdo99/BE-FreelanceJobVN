package group5.freelancejob.utils;

import org.springframework.util.StringUtils;

import java.text.DecimalFormat;

public class Constant {
    /**
     * CONSTANT STRING
     */
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String FLOAT_PATTERN = "#.#";
    public static final String NUMBER_ZERO = "0";
    public static final String SYMBOL_SLASH = "/";

    public static final String MINIMUM_DEPOSIT_TO_OFFER_MESSAGE = "Freelancer cần ít nhất 500.000₫ trong tài khoản để có thể chào giá";
    public static final int MINIMUM_DEPOSIT = 500000;

    public static final String ROLE_RECRUITER = "hasAuthority('recruiter')";
    public static final String ROLE_FREELANCER = "hasAuthority('freelancer')";
    public static final String ROLE_ADMIN = "hasAuthority('admin')";

    public static final String PAYMENT_TYPE_TOPUP = "Nạp tiền";
    public static final String PAYMENT_TYPE_WITHDRAW = "Rút tiền";
    public static final String PAYMENT_TYPE_TRANSFER = "Chuyển tiền";
    public static final String PAYMENT_TYPE_PAYMENT = "Thanh toán";


    /**
     * Calculate Rating for Freelancer and Recruiter.
     * <p>
     * Rating of Freelancer and Recruiter when creating account is 0/0.
     * When ever a Job has been clicked [Finish] from both sides, Rating feature is activated.
     * Freelancer/Recruiter rates from 1 to 5 in Integer.
     * rating = rate number / number of rated people.
     */
    public static String calculateRating(String rating) {
        if (rating == null || StringUtils.hasLength(rating)) {
            return "";
        }
        DecimalFormat df = new DecimalFormat(Constant.FLOAT_PATTERN);
        String[] arrOfInpStr = rating.split(Constant.SYMBOL_SLASH);
        String finalResult;

        if (arrOfInpStr[0].equals(Constant.NUMBER_ZERO) && arrOfInpStr[1].equals(Constant.NUMBER_ZERO)) {
            finalResult = Constant.NUMBER_ZERO;
        } else {
            double result = Integer.parseInt(arrOfInpStr[0]) / Integer.parseInt(arrOfInpStr[1]);
            finalResult = df.format(result);
        }
        return finalResult;
    }
}