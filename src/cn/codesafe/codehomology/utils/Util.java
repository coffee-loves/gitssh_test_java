package cn.codesafe.codehomology.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;

import de.innosystec.unrar.Archive;
import de.innosystec.unrar.rarfile.FileHeader;
import sun.nio.ch.FileChannelImpl;

public class Util {
	private static final String HEXES = "0123456789abcdef";
	public static String getFileExtension(String fileName) {
        String ret = null;
        final int pos = fileName.lastIndexOf(".");
        if (pos >= 0) {
            ret = fileName.substring(pos + 1, fileName.length()).toLowerCase();
        }
        return ret;
    }
	
	public static void removedic(String dic){
		DeleteFolder(dic);
	}
	
	public static int getLCString(char[] str1, char[] str2) {
		int len1, len2;
		len1 = str1.length;
		len2 = str2.length;
		int maxLen = len1 > len2 ? len1 : len2;

		int[] max = new int[maxLen];// 保存最长子串长度的数组
		int[] maxIndex = new int[maxLen];// 保存最长子串长度最大索引的数组
		int[] c = new int[maxLen];

		int i, j;
		for (i = 0; i < len2; i++) {
			for (j = len1 - 1; j >= 0; j--) {
				if (str2[i] == str1[j]) {
					if ((i == 0) || (j == 0))
						c[j] = 1;
					else
						c[j] = c[j - 1] + 1;//此时C[j-1]还是上次循环中的值，因为还没被重新赋值
				} else {
					c[j] = 0;
				}

				// 如果是大于那暂时只有一个是最长的,而且要把后面的清0;
				if (c[j] > max[0]) {
					max[0] = c[j];
					maxIndex[0] = j;

					for (int k = 1; k < maxLen; k++) {
						max[k] = 0;
						maxIndex[k] = 0;
					}
				}
				// 有多个是相同长度的子串
				else if (c[j] == max[0]) {
					for (int k = 1; k < maxLen; k++) {
						if (max[k] == 0) {
							max[k] = c[j];
							maxIndex[k] = j;
							break; // 在后面加一个就要退出循环了
						}
					}
				}
			}
		}
        //打印最长子字符串
		int length = 0;
		for (j = 0; j < maxLen; j++) {
			if (max[j] > length) {
				length = max[j];
			}
		}
		return length;
	}
    /**
     *  根据路径删除指定的目录或文件，无论存在与否
     *@param sPath  要删除的目录或文件
     *@return 删除成功返回 true，否则返回 false。
     */
    private static boolean DeleteFolder(String sPath) {
        File file = new File(sPath);
        // 判断目录或文件是否存在
        if (!file.exists()) {  // 不存在返回 false
            return false;
        } else {
            // 判断是否为文件
            if (file.isFile()) {  // 为文件时调用删除文件方法
                return deleteFile(sPath);
            } else {  // 为目录时调用删除目录方法
                return deleteDirectory(sPath);
            }
        }
    }
    /**
     * 删除单个文件
     * @param   sPath    被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    private static boolean deleteFile(String sPath) {
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            return true;
        }
        return false;
    }
    
    /**
     * 删除目录（文件夹）以及目录下的文件
     * @param   sPath 被删除目录的文件路径
     * @return  目录删除成功返回true，否则返回false
     */
    private static boolean deleteDirectory(String sPath) {
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        //删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            //删除子文件
            if (files[i].isFile()) {
                if(!deleteFile(files[i].getAbsolutePath())){
                	System.out.println(files[i].getAbsolutePath());
                	return false;
                }
            } //删除子目录
            else {
                if(!deleteDirectory(files[i].getAbsolutePath())){
                	System.out.println(files[i].getAbsolutePath());
                	return false;
                }
            }
        }
        //删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }
	
	public static String getFileRealName(String fileName) {
        String ret = null;
        final int pos = fileName.lastIndexOf(".");
        if (pos >= 0) {
            ret = fileName.substring(0,pos);
        }
        return ret;
    }
	
	public static String getMD5Checksum(File file) throws IOException, NoSuchAlgorithmException {
        byte[] b = getChecksum("MD5", file);
        return getHex(b);
    }
	
	public static String getHex(byte[] raw) {
        if (raw == null) {
            return null;
        }
        final StringBuilder hex = new StringBuilder(2 * raw.length);
        for (final byte b : raw) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt(b & 0x0F));
        }
        return hex.toString();
    }
	
	public static byte[] getChecksum(String algorithm, File file) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            FileChannel ch = fis.getChannel();
            long remainingToRead = file.length();
            long start = 0;
            while (remainingToRead > 0) {
                long amountToRead;
                if (remainingToRead > Integer.MAX_VALUE) {
                    remainingToRead -= Integer.MAX_VALUE;
                    amountToRead = Integer.MAX_VALUE;
                } else {
                    amountToRead = remainingToRead;
                    remainingToRead = 0;
                }
                MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, start, amountToRead);
                digest.update(byteBuffer);
                start += amountToRead;
                // lijiamig:bug fix, unmap  
                Method m = FileChannelImpl.class.getDeclaredMethod("unmap", MappedByteBuffer.class);  
                m.setAccessible(true);  
                m.invoke(FileChannelImpl.class, byteBuffer);
            }
            ch.close();
        } catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ex) {
                	ex.printStackTrace();
                }
            }
        }
        return digest.digest();
    }
	
	public static void copyFile(String from,String to) throws IOException {
		FileOutputStream fos = null;
		InputStream is = null;
		try {
			fos = new FileOutputStream(to);
			is = new FileInputStream(from);
	        final byte[] buff = new byte[4096];
	        int bread = -1;
	        while ((bread = is.read(buff)) >= 0) {
	            fos.write(buff, 0, bread);
	        }
	        fos.flush();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			throw e;
		} finally{
			if (fos != null) {
                try {
                    fos.close();
                } catch (Throwable e) {
                	e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (Throwable e) {
                	e.printStackTrace();
                }
            }
		}
        
	}
	
	public static void extract(File archive, File destination) throws Exception {

        if (archive == null || destination == null) {
            return;
        }

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(archive);
        } catch (FileNotFoundException ex) {
            throw new Exception("Archive file was not found.", ex);
        }
        String archiveExt = Util.getFileExtension(archive.getName()).toLowerCase();
        System.out.println(archiveExt);
        try {
            if ("zip".equals(archiveExt)) {
                extractArchive(new ZipArchiveInputStream(new BufferedInputStream(fis)), destination);
            }
            if ("rar".equals(archiveExt)) {
            	unrar(archive, destination);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
            	ex.printStackTrace();
            }
        }
	}
	
	public static void makefile(File f){
		if(!f.getParentFile().exists()){
			makedir(f.getParentFile());
		}
	}
	public static void makedir(File f){
		if(!f.getParentFile().exists()){
			makedir(f.getParentFile());
		}
		f.mkdir();
	}
	
	private static String chs = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static String getNgramId(){
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		StringBuilder sb = new StringBuilder();
		for(int i = 0;i < uuid.length() / 2;i ++){
			sb.append(chs.charAt(Integer.parseInt(uuid.substring(2 * i, 2 * i + 2),16) % chs.length()));
		}
		return sb.toString();
	}
	
	public static void unrar(File sourceRar, File destDir) throws Exception {
		Archive archive = null;
		FileOutputStream fos = null;
		System.out.println("Starting...");
		try {
			archive = new Archive(sourceRar);
			FileHeader fh = archive.nextFileHeader();
			int count = 0;
			File destFileName = null;
			while (fh != null) {
				System.out.println((++count) + ") " + fh.getFileNameString());
				String compressFileName = fh.getFileNameString().trim();
				destFileName = new File(destDir.getAbsolutePath() + "/"	+ compressFileName);
				if (fh.isDirectory()) {
					if (!destFileName.exists()) {
						destFileName.mkdirs();
					}
					fh = archive.nextFileHeader();
					continue;
				} 
				if (!destFileName.getParentFile().exists()) {
					destFileName.getParentFile().mkdirs();
				}
				fos = new FileOutputStream(destFileName);
				archive.extractFile(fh, fos);
				fos.close();
				fos = null;
				fh = archive.nextFileHeader();
			}

			archive.close();
			archive = null;
			System.out.println("Finished !");
		} catch (Exception e) {
			throw e;
		} finally {
			if (fos != null) {
				try {
					fos.close();
					fos = null;
				} catch (Exception e) {
					//ignore
				}
			}
			if (archive != null) {
				try {
					archive.close();
					archive = null;
				} catch (Exception e) {
					//ignore
				}
			}
		}
	}
	
	private static void extractArchive(ArchiveInputStream input, File destination) throws Exception {
        ArchiveEntry entry;
        try {
            while ((entry = input.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    File d = new File(destination, entry.getName());
                    if (!d.exists()) {
                        if (!d.mkdirs()) {
                            String msg = String.format("Unable to create directory '%s'.", d.getAbsolutePath());
                            throw new Exception(msg);
                        }
					}
				} else {
					File file = new File(destination, entry.getName());
					BufferedOutputStream bos = null;
					FileOutputStream fos = null;
					try {
						File parent = file.getParentFile();
						if (!parent.isDirectory()) {
							if (!parent.mkdirs()) {
								String msg = String.format("Unable to build directory '%s'.", parent.getAbsolutePath());
								throw new Exception(msg);
							}
						}
						fos = new FileOutputStream(file);
						bos = new BufferedOutputStream(fos, 4096);
						int count;
						byte[] data = new byte[4096];
						while ((count = input.read(data, 0, 4096)) != -1) {
							bos.write(data, 0, count);
						}
						bos.flush();
					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {
						if (bos != null) {
							try {
								bos.close();
							} catch (IOException ex) {
								ex.printStackTrace();
							}
						}
						if (fos != null) {
							try {
								fos.close();
							} catch (IOException ex) {
								ex.printStackTrace();
							}
						}
					}

				}
			}
            input.close();
        } catch (Exception ex) {
        	ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ex) {
                	ex.printStackTrace();
                }
            }
        }
    }
}
