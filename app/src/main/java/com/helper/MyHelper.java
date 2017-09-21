package com.helper;

import android.util.Log;

import com.bw.spdev.Ped;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class MyHelper 
{
	public static boolean  _Debug = false;
	public static int getStringLen(byte []mem1,int len)
	{
		int idx=0;
		while(true)
		{
			if(idx==len)
				break;
			if(mem1[idx]!=0)
			{
				idx++;
				continue;
			}
			break;
		}
		return idx;
	}
	public static int memcmp(byte []mem1,byte []mem2,int len)
	{
		int i=0;
		int tmp1,tmp2;
		for(;i<len;i++)
		{
			tmp1 = mem1[i];
			tmp1 = (tmp1>=0?tmp1:tmp1+256);
			
			tmp2 = mem2[i];
			tmp2 = (tmp2>=0?tmp2:tmp2+256);
			
			if((tmp1-tmp2)!=0)
				return (tmp1-tmp2);
			
		}
		return 0;
	}
	
	
	public static int memcmp(byte []mem1,int ofs1,byte []mem2,int ofs2,int len)
	{
		int i=0;
		int tmp1,tmp2;
		for(;i<len;i++)
		{
			tmp1 = mem1[i+ofs1];
			tmp1 = (tmp1>=0?tmp1:tmp1+256);
			
			tmp2 = mem2[i+ofs2];
			tmp2 = (tmp2>=0?tmp2:tmp2+256);
			
			if((tmp1-tmp2)!=0)
				return (tmp1-tmp2);
			
		}
		return 0;
	}
	
	
	public static void memcpy(byte []mem1,byte []mem2,int len)
	{
		System.arraycopy(mem2, 0, mem1, 0, len);
	}
	
	public static void memcpy(byte []mem1,int ofs1,byte []mem2,int ofs2,int len)
	{
		System.arraycopy(mem2, ofs2, mem1, ofs1, len);
	}
	
	public static void memset(byte []mem1,byte value,int len)
	{
		int i=0;
		for(i=0;i<len;i++)
		{
			mem1[i] = value;
		}
	}
	
	public static void memset(byte []mem1,int ofs,byte value,int len)
	{
		int i=0;
		for(i=0;i<len;i++)
		{
			mem1[i+ofs] = value;
		}
	}
	
	public static void LongToStrByLen(long src,byte [] data,int len)
	{
		Arrays.fill(data, 0, len-1, (byte)0);
		data[len-1] = (byte)src;
		data[len-2] = (byte)((src>>8)&0x000000ff);
		data[len-3] = (byte)((src>>16)&0x000000ff);
		data[len-4] = (byte)((src>>24)&0x000000ff);
	}
	public static int  StrLen(byte [] data)
	{
		int i=0;
    	while(true&&i<data.length)
    	{
    		if(data[i]==0)
    			break;
    		i++;
    	}
    	return i;
	}
	
	public static long StrToLong(byte []str)
	{
	    long tmp;
	    tmp = (str[0]>=0?str[0]:str[0]+256);
	    tmp <<= 24;
	    tmp += (str[1]>=0?str[1]:str[1]+256);
	    tmp <<= 16;
	    tmp += (str[2]>=0?str[2]:str[2]+256);
	    tmp <<= 8;
	    tmp += (str[3]>=0?str[3]:str[3]+256);
	    return tmp;
	}
	
	public static long StrToLong(byte []str,int ofst)
	{
		long tmp;
	    tmp = (str[0+ofst]>=0?str[0]:str[0]+256);
	    tmp <<= 24;
	    tmp += (str[1+ofst]>=0?str[1]:str[1]+256);
	    tmp <<= 16;
	    tmp += (str[2+ofst]>=0?str[2]:str[2]+256);
	    tmp <<= 8;
	    tmp += (str[3+ofst]>=0?str[3]:str[3]+256);
	    return tmp;
	}
	
	
	public static void LongToStr( long ldat, byte []str)
	{
	    str[0] = (byte)(ldat >> 24);
	    str[1] = (byte)(ldat >> 16);
	    str[2] = (byte)(ldat >> 8);
	    str[3] = (byte)(ldat);
	}
	
	//输入的是不带小数点的金额字符串
	public static String PackAmtStr(String samt)
	{
		long iamt = Long.parseLong(samt);
		return String.format("%d.%02d", iamt/100, iamt % 100);
	}
	
	//输入的是不带小数点的金额十进制数
	public static String PackAmtStr(long iamt)
	{
		return String.format("%d.%02d", iamt/100, iamt % 100);
	}
	
	public static  byte[] LongToStr( long ldat)
	{
		byte str[] = new byte[4];
	    str[0] = (byte)(ldat >> 24);
	    str[1] = (byte)(ldat >> 16);
	    str[2] = (byte)(ldat >> 8);
	    str[3] = (byte)(ldat);
	    return str;
	
	}
	
	
	public static long StrToLongByLen(byte []str, int ucLen) {
	    long tmp = 0;
	    byte i = 0;
		 byte twobuff[] = new byte[20];
		 byte aucStr[]= new byte[20];

		 Arrays.fill(aucStr, (byte)0);
		Arrays.fill(twobuff, (byte)0);
		
		vOneTwo(str, ucLen, twobuff);
		for (i = 0; i< 20; i++) 
		{
			if (twobuff[i] != 0x30) 
			{
				break;
			}
		}
		
		System.arraycopy(twobuff,i,aucStr,0,12-i);
		String s=null;
		try {
			s = new String(aucStr,0,12-i,"iso-8859-1");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(s==null) s = "0";
		if(s==""||s.length()==0||s==null)
			s="0";
		Log.e(null,"s="+s);
		tmp = Long.parseLong(s);
		
		return tmp;
	}
	
	public static void vOneTwo(byte []One, int len,byte []Two)
	{
	    int  i;
	    byte  TAB[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	    int tmp=0;
	    for (i = 0; i < len; i++) 
	    {
	    	tmp = One[i];
	    	tmp = tmp>=0?tmp:tmp+256;
	        Two[i * 2] = TAB[tmp >> 4];
	        Two[i * 2 + 1] = TAB[tmp & 0x0f];
	    }
	}
	
	public static void vTwoOne(byte []in,  short in_len, byte []out)
	{
	     int tmp;
	     short i;

	     for (i = 0; i < in_len; i += 2) {
	          tmp = in[i];
	          if (tmp > '9')
	                tmp = Character.toUpperCase(tmp) - ('A' - 0x0A);
	          else
	                tmp &= 0x0f;
	          tmp <<= 4;
	          out[i / 2] = (byte) tmp;

	          tmp = in[i + 1];
	          if (tmp > '9')
	        	  tmp = Character.toUpperCase(tmp) - ('A' - 0x0A);
	          else
	                tmp &= 0x0f;
	          out[i / 2] += tmp;
	     }
	}
	
	
	public static void sleep_ms(int ms)
	{
    	try 
		{
	       Thread.currentThread();
	       Thread.sleep(ms);
	    } 
		catch (InterruptedException e)
		{
          e.printStackTrace();
		}  
	}
	

	public static byte ConvBcdAmount(byte []bcdAmt, byte[]amount_ptr)
	{
	    byte  buffer[] = new byte[16];
	    byte amtLen;
	    int i;
	    byte tmp[] = new byte[16];
	
	    Arrays.fill(buffer, (byte)0);
	    vOneTwo(bcdAmt, 6, buffer);
	
	    buffer[12] = 0x00;
	    for (i = 0; i < 12; i++) 
	    {
	        if (buffer[i] != '0') 
	        {
	        	System.arraycopy(buffer, i, tmp, 0, 12-i);
	            amtLen = LongToDec(tmp,12-i);	            
	            System.arraycopy(tmp, 0, amount_ptr, 0, amtLen);
	            return amtLen;
	        }
	    }
	    amount_ptr[0]='0';amount_ptr[1]='.';
	    amount_ptr[2]='0';amount_ptr[3]='0';
	    
	    return 4;
	}
	
	
	static byte LongToDec(byte[] szAmount,int len)
	{
	   
	    byte  tmp[] = new byte[16];
	    byte len1;
	
	    tmp[0]='0';tmp[1]='.';tmp[2]='0';tmp[3]='0';
	    
	    tmp[4] = 0;
	
	    len1 = (byte) len;
	    if (len1 == 0) 
	    {
	        memcpy(szAmount, tmp, 4);
	        szAmount[4] = 0;
	        return (4);
	    }
	    if (len1 == 1) 
	    {
	        tmp[3] = szAmount[0];
	        memcpy(szAmount, tmp, 4);
	        szAmount[4] = 0;
	        return (4);
	    }
	    if (len1 == 2) {
	        tmp[2] = szAmount[0];
	        tmp[3] = szAmount[1];
	        memcpy(szAmount, tmp, 4);
	        szAmount[4] = 0;
	        return (4);
	    }
	    memcpy(tmp, szAmount, len1 - 2);
	    tmp[len1 - 2] = '.';
	    System.arraycopy(szAmount, len1 - 2, tmp, len1 - 1, 2);
	   
	    tmp[len1 + 1] = 0;
	    memcpy(szAmount, tmp, len1 + 1);
	    szAmount[len1 + 1] = 0;
	    return ((byte) (len1 + 1));
	}

	public static int PrivateGetIntRand()
	{
		int rd = 0;
		int tmp=0;
		byte buf[]= new byte [4];
		
		Ped ped = new Ped();
		ped.GetRandom(4, buf);
		tmp = buf[0];
		tmp = tmp<0?tmp+256:tmp;
		rd  = (tmp)<<24;
		tmp = buf[1];
		tmp = tmp<0?tmp+256:tmp;
		rd += (tmp<<16);
		tmp = buf[2];
		tmp = tmp<0?tmp+256:tmp;
		rd += (tmp<<8);
		tmp = buf[3];
		tmp = tmp<0?tmp+256:tmp;
		rd += (tmp);
		if(rd<0)
		{
			return PrivateGetIntRand();
		}
		return rd;
	}
}
