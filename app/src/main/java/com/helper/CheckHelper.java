package com.helper;

import java.util.ArrayList;


/**

 * 用户可以直接调用类里面的两个静态方法

 * 

 * @author WANJJ

 * 

 */

public class CheckHelper {

 

    /**

     * 得出CRC计算结果

     * 

     * @param buf

     *            要计算CRC的字符串

     * @return 整数

     */

    public static int getCRC(String buf) {

        int crc = 0xFFFF; // initial value

        int polynomial = 0x1021; // 0001 0000 0010 0001 (0, 5, 12)

 

        for (int j = 0; j < buf.length(); j++) {

            char b = buf.charAt(j);

            for (int i = 0; i < 8; i++) {

                boolean bit = ((b >> (7 - i) & 1) == 1);

                boolean c15 = ((crc >> 15 & 1) == 1);

                crc <<= 1;

                if (c15 ^ bit)

                    crc ^= polynomial;

            }

        }

 

        crc &= 0xffff;

        return crc;

    }

 

    /**

     * 得出CRC计算结果

     * 

     * @param buf

     *            要计算CRC的字符串

     * @return 字符串,2个字节

     */

    public static String getCRCString(String buf) {

        int crc = 0xFFFF; // initial value

        int polynomial = 0x1021; // 0001 0000 0010 0001 (0, 5, 12)

 

        for (int j = 0; j < buf.length(); j++) {

            char b = buf.charAt(j);

            for (int i = 0; i < 8; i++) {

                boolean bit = ((b >> (7 - i) & 1) == 1);

                boolean c15 = ((crc >> 15 & 1) == 1);

                crc <<= 1;

                if (c15 ^ bit)

                    crc ^= polynomial;

            }

        }

 

        crc &= 0xffff;

        String str = "" + (char) (crc / 256) + (char) (crc % 256);

        return str;

    }

 

    /**

     * 得出异或计算结果

     * 

     * @param data

     *            要计算异或的数据

     * @param offset

     *            偏移位

     * @param length

     *            长度

     * @return byte

     */

    public static byte CheckXOR(byte[] data, int offset, int length) {

        byte xda = 0;

        for (int i = 0; i < length; i++) {

            xda = (byte) ((data[i + offset] ^ xda) & 0xFF);

        }

        return xda;

    }

 

    /**

     * 校验并转义(异或效验)

     * 

     * @param data

     *            要计算异或的数据

     * @param offset

     *            偏移位

     * @param length

     *            长度

     * @return byte

     */

    public static void XORAndEscape(ArrayList<Byte> data, int offset, int length) {

        byte xda = 0;

        for (int i = offset; i < offset + length; i++) {

            xda = (byte) (data.get(i) ^ xda);

            if (data.get(i) == 0x7e)// 转义 向后插入一个0x02

            {

                data.set(i, (byte) 0x7d);

                data.add(i + 1, (byte) 0x02);

                length++;

                i++;

            } else if (data.get(i) == 0x7d)// 转义 向后插入一个0x01

            {

                data.add(i + 1, (byte) 0x01);

                length++;

                i++;

            }

        }

        data.add(offset + length, xda);

        if (xda == 0x7e)// 转义 向后插入一个0x02

            data.add(offset + length + 1, (byte) 0x02);

        else if (xda == 0x7d)// 转义 向后插入一个0x01

            data.add(offset + length + 1, (byte) 0x01);

    }

}
