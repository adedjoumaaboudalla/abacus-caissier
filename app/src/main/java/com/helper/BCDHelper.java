package com.helper;


import android.text.format.Time;

import java.io.UnsupportedEncodingException;


public class BCDHelper { 

    /**

    * @Title: StrToBCD

    * @Description: TODO(用BCD码压缩数字字符串)

    *

    * @param str

    * @return

    * @return:byte[]

    *

    * @version: v1.0.0

    * @author: WANJJ

    * @date: 2011-11-17

    * 

    * Modification History: 

    * Date  Author  Version  Description

    * ---------------------------------------------------------*

    * 2011-11-17  wanjj v1.0.0   修改原因

    */

    

    public static byte[] StrToBCD(String str) {

 

        return StrToBCD(str, str.length());

    }

 

    public static byte[] StrToBCD(String str, int numlen) {

        if (numlen % 2 != 0)

            numlen++;

 

        while (str.length() < numlen) {

            str = "0" + str;

        }

 

        byte[] bStr = new byte[numlen / 2];

        char[] cs = str.toCharArray();

        int i = 0;

        int iNum = 0;

        for (i = 0; i < numlen; i += 2) {

 

            int iTemp = 0;

            if (cs[i] >= '0' && cs[i] <= '9') {

                iTemp = (cs[i] - '0') << 4;

            } else {

                // 判断是否为a~f

                if (cs[i] >= 'a' && cs[i] <= 'f') {

                    cs[i] -= 32;

                }

                iTemp = (cs[i] - '0' - 7) << 4;

            }

            // 处理低位

            if (cs[i + 1] >= '0' && cs[i + 1] <= '9') {

                iTemp += cs[i + 1] - '0';

            } else {

                // 判断是否为a~f

                if (cs[i + 1] >= 'a' && cs[i + 1] <= 'f') {

                    cs[i + 1] -= 32;

                }

                iTemp += cs[i + 1] - '0' - 7;

            }

            bStr[iNum] = (byte) iTemp;

            iNum++;

        }

        return bStr;

 

    }

 

    /**

    * @Title: bcdToInt

    * @Description: TODO(BCD转int)

    *

    * @param bcdNum

    * @param offset

    * @param numlen

    * @return

    * @return:int

    *

    * @version: v1.0.0

    * @author: WANJJ

    * @date: 2011-11-17

    * 

    * Modification History: 

    * Date  Author  Version  Description

    * ---------------------------------------------------------*

    * 2011-11-17  wanjj v1.0.0   修改原因

    */

    

    public static int bcdToInt(byte[] bcdNum, int offset, int numlen) {

        return Integer.parseInt(bcdToString(bcdNum, offset, numlen));

    }

 

    /**

    * @Title: bcdToString

    * @Description: TODO(BCD转字符串)

    *

    * @param bcdNum

    * @param offset

    * @param numlen

    * @return

    * @return:String

    *

    * @version: v1.0.0

    * @author: WANJJ

    * @date: 2011-11-17

    * 

    * Modification History: 

    * Date  Author  Version  Description

    * ---------------------------------------------------------*

    * 2011-11-17  wanjj v1.0.0   修改原因

    */

    

    public static String bcdToString(byte[] bcdNum, int offset, int numlen) {

        int len = numlen / 2;

        if (numlen % 2 != 0)

            len++;

 

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < len; i++) {

            sb.append(Integer.toHexString((bcdNum[i + offset] & 0xf0) >> 4));

            sb.append(Integer.toHexString(bcdNum[i + offset] & 0xf));

        }

        return sb.toString();

    }

 

    /**

    * @Title: Bcd3ToDateTime

    * @Description: TODO(BCD码转日期)

    *

    * @param data

    * @param offset

    * @return

    * @return:Time

    *

    * @version: v1.0.0

    * @author: WANJJ

    * @date: 2011-11-17

    * 

    * Modification History: 

    * Date  Author  Version  Description

    * ---------------------------------------------------------*

    * 2011-11-17  wanjj v1.0.0   修改原因

    */

    

    public static Time Bcd3ToDateTime(byte[] data, int offset) {

        int year = 0, month = 0, monthDay = 0;

        year = Integer.parseInt("20" + bcdToString(data, offset, 2));

        month = bcdToInt(data, offset + 1, 2);

        monthDay = bcdToInt(data, offset + 2, 2);

        Time time = new Time("GMT+8");

        time.set(monthDay, month, year);

        return time;

    }

 

    public static Time Bcd5ToDateTime(byte[] data, int offset) {

        int year = 0, month = 0, monthDay = 0, hour = 0, minute = 0, second = 0;

 

        year = Integer.parseInt("20" + bcdToString(data, offset, 2));

        month = bcdToInt(data, offset + 1, 2);

        monthDay = bcdToInt(data, offset + 2, 2);

        hour = bcdToInt(data, offset + 3, 2);

        minute = bcdToInt(data, offset + 4, 2);

 

        Time time = new Time("GMT+8");

        time.set(second, minute, hour, monthDay, month, year);

        return time;

    } 

    /**

    * @Title: Bcd6ToDateTime

    * @Description: TODO(BCD码转时间格式)

    *

    * @param data

    * @param offset

    * @return

    * @return:Time

    *

    * @version: v1.0.0

    * @author: WANJJ

    * @date: 2011-11-17

    * 

    * Modification History: 

    * Date  Author  Version  Description

    * ---------------------------------------------------------*

    * 2011-11-17  wanjj v1.0.0   修改原因

    */

    

    public static Time Bcd6ToDateTime(byte[] data, int offset) {

        int year = 0, month = 0, monthDay = 0, hour = 0, minute = 0, second = 0;

 

        year = Integer.parseInt("20" + bcdToString(data, offset, 2));

        month = bcdToInt(data, offset + 1, 2);

        monthDay = bcdToInt(data, offset + 2, 2);

        hour = bcdToInt(data, offset + 3, 2);

        minute = bcdToInt(data, offset + 4, 2);

        second = bcdToInt(data, offset + 5, 2);

 

        Time time = new Time("GMT+8");

        time.set(second, minute, hour, monthDay, month, year);

        return time;

    }

 

    public static Time Bcd7ToDateTime(byte[] data, int offset) {

        int year = 0, month = 0, monthDay = 0, hour = 0, minute = 0, second = 0;

 

        year = bcdToInt(data, offset, 4);

        month = bcdToInt(data, offset + 2, 2);

        monthDay = bcdToInt(data, offset + 3, 2);

        hour = bcdToInt(data, offset + 4, 2);

        minute = bcdToInt(data, offset + 5, 2);

        second = bcdToInt(data, offset + 6, 2);

 

        Time time = new Time("GMT+8");

        time.set(second, minute, hour, monthDay, month, year);

        return time;

    }

 

    /**

    * @Title: DateTimeToBcd3

    * @Description: TODO(日期转BCD码)

    *

    * @param dt

    * @return

    * @throws UnsupportedEncodingException

    * @return:byte[]

    *

    * @version: v1.0.0

    * @author: WANJJ

    * @date: 2011-11-17

    * 

    * Modification History: 

    * Date  Author  Version  Description

    * ---------------------------------------------------------*

    * 2011-11-17  wanjj v1.0.0   修改原因

    */

    

    public static byte[] DateTimeToBcd3(Time dt)

            throws UnsupportedEncodingException {

 

        StringBuilder sb = new StringBuilder();

        sb.append(FStrLen(String.valueOf((dt.year - 2000)), 2));

        sb.append(FStrLen(String.valueOf((dt.month)), 2));

        sb.append(FStrLen(String.valueOf((dt.monthDay)), 2));

 

        return StrToBCD(sb.toString());

    }

 

    public static byte[] DateTimeToBcd5(Time dt)

            throws UnsupportedEncodingException {

 

        StringBuilder sb = new StringBuilder();

 

        sb.append(FStrLen(String.valueOf((dt.year - 2000)), 2));

        sb.append(FStrLen(String.valueOf(dt.month), 2));

        sb.append(FStrLen(String.valueOf(dt.monthDay), 2));

        sb.append(FStrLen(String.valueOf(dt.hour), 2));

        sb.append(FStrLen(String.valueOf(dt.minute), 2));

 

        return StrToBCD(sb.toString());

    }

 

    public static byte[] DateTimeToBcd6(Time dt)

            throws UnsupportedEncodingException {

        StringBuilder sb = new StringBuilder();

 

        sb.append(FStrLen(String.valueOf((dt.year - 2000)), 2));

        sb.append(FStrLen(String.valueOf(dt.month), 2));

        sb.append(FStrLen(String.valueOf(dt.monthDay), 2));

        sb.append(FStrLen(String.valueOf(dt.hour), 2));

        sb.append(FStrLen(String.valueOf(dt.minute), 2));

        sb.append(FStrLen(String.valueOf(dt.second), 2));

 

        return StrToBCD(sb.toString());

    }

 

    public static byte[] DateTimeToBcd7(Time dt)

            throws UnsupportedEncodingException {

        StringBuilder sb = new StringBuilder();

        sb.append(FStrLen(String.valueOf(dt.year), 4));

        sb.append(FStrLen(String.valueOf(dt.month), 2));

        sb.append(FStrLen(String.valueOf(dt.monthDay), 2));

        sb.append(FStrLen(String.valueOf(dt.hour), 2));

        sb.append(FStrLen(String.valueOf(dt.minute), 2));

        sb.append(FStrLen(String.valueOf(dt.second), 2));

        return StrToBCD(sb.toString());

    }

 

    public static String FStrLen(String str, int len) {

        if (str.length() < len) {

            str = "0" + str;

        }

        return str;

    }

 

    /**

     * @Title: GetNowTime

     * @Description: TODO(获取当前GMT+8时间)

     * 

     * @return

     * @return:Time

     * 

     * @version: v1.0.0

     * @author: WANJJ

     * @date: 2011-11-9

     * 

     *        Modification History: Date Author Version Description

     *        ---------------------------------------------------------*

     *        2011-11-9 wanjj v1.0.0 修改原因

     */

    public static Time GetNowTime() {

 

        Time t = GetNewTime();

        t.setToNow();

 

        return t;

    }

 

    public static Time GetNewTime() {

        return new Time("GMT+8");

    }

 

    /**

     * @Title: TimeDiff

     * @Description: TODO(计算两个时间相差毫秒数)

     * 

     * @param tm1

     * @param tm2

     * @return

     * @return:long

     * 

     * @version: v1.0.0

     * @author: WANJJ

     * @date: 2011-11-11

     * 

     *        Modification History: Date Author Version Description

     *        ---------------------------------------------------------*

     *        2011-11-11 wanjj v1.0.0 修改原因

     */

 

    public static long TimeDiff(Time tm1, Time tm2) {

        return (tm1.toMillis(true) - tm2.toMillis(true));

    }

}

