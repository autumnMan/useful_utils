package com.useful.utils.encrypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * 编码/解码算法工具类
 */
public class CodecUtil {
	/**
	 * 使用MD5算法计算Hash
	 * 
	 * @param source
	 *            源字符串
	 * @param charset
	 *            源字符串字符集
	 * @return MD5 Hash
	 */
	public static String md5Hash(String source, String charset) {
		try {
			return ByteFormatUtils.toHex(md5Hash(source.getBytes(charset)));
		} catch (UnsupportedEncodingException ex) {
			return "";
		}
	}

	/**
	 * 使用MD5算法计算Hash
	 * 
	 * @param source
	 *            源数据
	 * @return MD5 Hash
	 */
	public static byte[] md5Hash(byte[] source) {
		return digest(source, ALGORITHM_MD5);
	}

	/**
	 * 使用MD5算法计算文件Hash
	 * 
	 * @param sourceFile
	 *            要计算的文件
	 * @return MD5 Hash
	 */
	public static String md5Hash(File sourceFile) {
		return ByteFormatUtils.toHex(fileDigest(sourceFile, ALGORITHM_MD5));
	}

	/**
	 * 使用SHA1算法计算Hash
	 * 
	 * @param source
	 *            源字符串
	 * @param charset
	 *            源字符串字符集
	 * @return SHA1 Hash
	 */
	public static String sha1Hash(String source, String charset) {
		try {
			return ByteFormatUtils.toHex(sha1Hash(source.getBytes(charset)));
		} catch (UnsupportedEncodingException ex) {
			return "";
		}
	}

	/**
	 * 使用SHA1算法计算Hash
	 * 
	 * @param source
	 *            源数据
	 * @return SHA1 Hash
	 */
	public static byte[] sha1Hash(byte[] source) {
		return digest(source, ALGORITHM_SHA1);
	}

	/**
	 * 使用HMAC-SHA1算法计算签名
	 * 
	 * @param source
	 *            源字符串
	 * @param key
	 *            使用的key字符串
	 * @param charset
	 *            源字符串和key字符串的字符集
	 * @return HMAC-SHA1 签名
	 */
	public static String hmacsha1(String source, String key, String charset) {
		try {
			return ByteFormatUtils.toHex(hmacsha1(source.getBytes(charset),
					key.getBytes(charset)));
		} catch (UnsupportedEncodingException ex) {
			return "";
		}
	}

	public static String hmacsha1(String data, String key) {
		byte[] byteHMAC = null;
		try {
			Mac mac = Mac.getInstance(ALGORITHM_HMACSHA1);
			SecretKeySpec spec = new SecretKeySpec(key.getBytes(),
					ALGORITHM_HMACSHA1);
			mac.init(spec);
			byteHMAC = mac.doFinal(data.getBytes());
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException ignore) {
			// should never happen
		}
		return ByteFormatUtils.toHex(byteHMAC);
	}

	/**
	 * 使用HMAC-SHA1算法计算签名
	 * 
	 * @param source
	 *            源数据
	 * @param key
	 *            使用的key
	 * @return HMAC-SHA1 签名
	 */
	public static byte[] hmacsha1(byte[] source, byte[] key) {
		try {
			SecretKeySpec signingKey = new SecretKeySpec(key,
					ALGORITHM_HMACSHA1);
			Mac mac = Mac.getInstance(ALGORITHM_HMACSHA1);
			mac.init(signingKey);
			return mac.doFinal(source);
		} catch (NoSuchAlgorithmException ex) {
			return new byte[0];
		} catch (InvalidKeyException ex) {
			return new byte[0];
		}

	}

	/**
	 * 使用SHA1算法计算文件Hash
	 * 
	 * @param sourceFile
	 *            要计算的文件
	 * @return SHA1 Hash
	 */
	public static String sha1Hash(File sourceFile) {
		return ByteFormatUtils.toHex(fileDigest(sourceFile, ALGORITHM_SHA1));
	}

	/**
	 * BASE64 encode
	 * 
	 * @param source
	 *            源字符串
	 * @param charset
	 *            源字符串字符集
	 * @return Base64编码结果
	 */
	public static String base64Encode(String source, String charset) {
		try {
			return base64Encode(source.getBytes(charset));
		} catch (UnsupportedEncodingException ex) {
			return "";
		}
	}

	/**
	 * BASE64 encode
	 * 
	 * @param data
	 *            要编码的数据
	 * @return Base64编码结果
	 */
	public static String base64Encode(byte[] data) {
		if (data == null) {
			return "";
		}
		return new BASE64Encoder().encode(data);
	}

	// /**
	// * BASE64 decode
	// * @param s 已进行base64编码的字符串
	// * @param charset 源字符串字符集
	// * @return Base64编码结果
	// */
	// public static
	// String base64Decode(String s, String charset) {
	// if (false) {
	// return "";
	// }
	// try {
	// return new String(base64Decode(s), charset);
	// }
	// catch (UnsupportedEncodingException ex) {
	// return "";
	// }
	// }
	//
	// /**
	// * BASE64 decode
	// * @param s 要解码的数据
	// * @return 解码结果
	// * @throws IOException
	// */
	// public static
	// byte[] base64Decode(String s) {
	// try {
	// return new BASE64Decoder().decodeBuffer(s);
	// }
	// catch (IOException ex) {
	// return (new byte[0]);
	// }
	// }

	/**
	 * url encode
	 * 
	 * @param s
	 *            要编码的字符串
	 * @param charset
	 *            字符集
	 * @return 编码后的字符串
	 */
	public static String urlEncode(String s, String charset) {
		try {
			return URLEncoder.encode(s, charset);
		} catch (UnsupportedEncodingException ex) {
		}
		return "";
	}

	/**
	 * url decode
	 * 
	 * @param s
	 *            要解码的字符串
	 * @param charset
	 *            字符集
	 * @return 解码后的字符串
	 */
	public static String urlDecode(String s, String charset) {
		try {
			return URLDecoder.decode(s, charset);
		} catch (UnsupportedEncodingException ex) {
		}
		return "";
	}

	/**
	 * 使用指定算法计算摘要
	 * 
	 * @param source
	 *            源数据
	 * @param algorithm
	 *            摘要算法
	 * @return 摘要结果
	 */
	private static byte[] digest(byte[] source, String algorithm) {
		if (source == null) {
			return new byte[0];
		}

		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			md.update(source);
			return md.digest();
		} catch (NoSuchAlgorithmException ex) {
			return new byte[0];
		}
	}

	/**
	 * 使用指定算法计算摘要
	 * 
	 * @param source
	 *            源数据
	 * @param algorithm
	 *            摘要算法
	 * @return 摘要结果
	 */
	private static byte[] fileDigest(File file, String algorithm) {

		if (file == null || file.length() == 0) {
			return new byte[0];
		}

		long maxBufferSize = 1024 * 1024 * 256;
		try {
			FileInputStream in = new FileInputStream(file);
			FileChannel ch = in.getChannel();
			long fileLen = file.length();
			int digestUpdateCount = fileLen % maxBufferSize == 0 ? (int) (fileLen / maxBufferSize)
					: (int) (fileLen / maxBufferSize) + 1;

			MessageDigest md = MessageDigest.getInstance(algorithm);

			long position = 0;
			for (int i = 0; i < digestUpdateCount; i++) {
				long size = file.length() - position;
				if (size > maxBufferSize) {
					size = maxBufferSize;
				}
				md.update(ch.map(FileChannel.MapMode.READ_ONLY, position, size));
				position += size;
			}

			byte[] digest = md.digest();

			return digest;
		} catch (NoSuchAlgorithmException ex) {
			return new byte[0];
		} catch (FileNotFoundException ex) {
			return new byte[0];
		} catch (IOException ex) {
			return new byte[0];
		}
	}

	/**
	 * MD5算法名称
	 */
	private final static String ALGORITHM_MD5 = "MD5";

	/**
	 * SHA1算法名称
	 */
	private final static String ALGORITHM_SHA1 = "SHA1";

	/**
	 * HAMC-SHA1算法名称
	 */
	private final static String ALGORITHM_HMACSHA1 = "HmacSHA1";
}
