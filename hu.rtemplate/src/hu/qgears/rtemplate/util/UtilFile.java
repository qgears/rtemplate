package hu.qgears.rtemplate.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.jar.Manifest;

public class UtilFile {

	/**
	 * Create the parent directories of the file. Then create the file and fill
	 * its content from the given URL.
	 * 
	 * @param trg
	 * @param url
	 * @throws IOException
	 */
	public static void copyFileFromUrl(File trg, URL url) throws IOException {
		trg.getParentFile().mkdirs();
		InputStream is = url.openStream();
		try {
			FileOutputStream fos = new FileOutputStream(trg);
			try {
				BufferedInputStream bis = new BufferedInputStream(is, 16384);
				BufferedOutputStream bos = new BufferedOutputStream(fos, 16384);
				int c;
				while ((c = bis.read()) >= 0) {
					bos.write(c);
				}
				bos.close();
			} finally {
				fos.close();
			}
		} finally {
			is.close();
		}
	}
	
	public static void saveAsFile(File targetFile, String s) throws IOException
	{
		FileOutputStream fos=new FileOutputStream(targetFile);
		try
		{
			OutputStreamWriter osw=new OutputStreamWriter(fos, "UTF-8");
			osw.write(s);
			osw.close();
		}finally
		{
			fos.close();
		}
	}

	public static void saveAsFile(File targetFile, byte[] s) throws IOException
	{
		FileOutputStream fos=new FileOutputStream(targetFile);
		try
		{
			fos.write(s);
		}finally
		{
			fos.close();
		}
	}

	public static void deleteRecursive(File dir) {
		if(dir.exists())
		{
			if(dir.isDirectory())
			{
				for(File f:dir.listFiles())
				{
					deleteRecursive(f);
				}
			}
			dir.delete();
		}
	}
	
	public static String getMd5(byte[] bytes) throws NoSuchAlgorithmException
	{
		MessageDigest m=MessageDigest.getInstance("MD5");
	    m.update(bytes);
	    return ""+new BigInteger(1,m.digest()).toString(16);
	}

	public static byte[] loadFile(URL resource) throws IOException {
		InputStream is=resource.openStream();
		try
		{
			ByteArrayOutputStream bos=new ByteArrayOutputStream();
			byte[] buffer=new byte[1024];
			int n;
			while((n=is.read(buffer))>0)
			{
				bos.write(buffer, 0, n);
			}
			return bos.toByteArray();
		}finally
		{
			is.close();
		}
	}
	public static byte[] loadFile(InputStream is) throws IOException {
		try
		{
			ByteArrayOutputStream bos=new ByteArrayOutputStream();
			byte[] buffer=new byte[1024];
			int n;
			while((n=is.read(buffer))>0)
			{
				bos.write(buffer, 0, n);
			}
			return bos.toByteArray();
		}finally
		{
			is.close();
		}
	}
	public static String loadAsString(InputStream is, String  encoding) throws IOException {
		InputStreamReader reader=new InputStreamReader(is, encoding);
		char[] chars=new char[1024];
		StringBuilder ret=new StringBuilder();
		int count;
		while((count=reader.read(chars))>0)
		{
			ret.append(chars, 0, count);
		}
		return ret.toString();
	}
	public static String loadAsString(File f) throws IOException {
		FileInputStream fis=new FileInputStream(f);
		try
		{
		InputStreamReader reader=new InputStreamReader(fis, "UTF-8");
		char[] chars=new char[1024];
		StringBuilder ret=new StringBuilder();
		int count;
		while((count=reader.read(chars))>0)
		{
			ret.append(chars, 0, count);
		}
		return ret.toString();
		}finally
		{
			fis.close();
		}
	}
	public static String loadAsString(URL url, String charset) throws IOException {
		InputStream is=url.openStream();
		try
		{
			return loadAsString(is, charset);
		}finally
		{
			is.close();
		}
	}

	public static byte[] decodeHexString(String content) {
		int l=content.length()/2;
		byte[] bs=new byte[l];
		for(int i=0;i<l;++i)
		{
			byte b=(byte)Integer.parseInt(
					content.substring(i*2,i*2+2), 16);
			bs[i]=b;
		}
		return bs;
	}
	
	public static Manifest loadManifest(byte[] bytes) throws IOException
	{
		ByteArrayInputStream fis=new ByteArrayInputStream(bytes);
		try
		{
			Manifest mf=new Manifest(fis);
			return mf;
		}finally
		{
			fis.close();
		}

	}
	public static byte[] saveManifest(Manifest mf) throws IOException
	{
		ByteArrayOutputStream fos=new ByteArrayOutputStream();
		try
		{
			mf.write(fos);
		}finally
		{
			fos.close();
		}
		return fos.toByteArray();
	}

}
