package com.helper;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


public class FileService
{
	private RandomAccessFile fd;
	

	
	
	public void open(String filename,String mode) 
	{
		try {
			fd = new  RandomAccessFile(filename, mode);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void close() 
	{
		try {
			fd.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void seek(long offset) 
	{
		try {
			fd.seek(offset);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public byte[] read(int size) 
    {
		
    	byte dataget[] = new byte[size+1];
    	try {
			fd.read(dataget,0,size);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return dataget;
    }
	
	public void write(byte data[],int size) 
    {
    	try {
			fd.write(data,0,size);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	public long  size()
	{
		try {
			return fd.length();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public boolean delete(String path)
	{
		File file = new File(path);
		return deleteFoder(file);
	}
	
	private  boolean deleteFoder(File file) 
	{
        if (file.exists()) { // 判断文件是否存在
               if (file.isFile()) { // 判断是否是文件
                       file.delete(); // delete()方法 你应该知道 是删除的意思;
                } else if (file.isDirectory()) { // 否则如果它是一个目录
                       File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                        if (files != null) {
                                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                                       deleteFoder(files[i]); // 把每个文件 用这个方法进行迭代
                               }
                        }
                }
                boolean isSuccess = file.delete();
                if (!isSuccess) {
                        return false;
                }
        }
        return true;
	}
	
	
}
