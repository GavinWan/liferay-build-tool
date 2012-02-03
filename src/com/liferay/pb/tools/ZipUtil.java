
package com.liferay.pb.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

public class ZipUtil {

	public static void unzip(String des, File zip, String skipFolder)
		throws Exception {

		byte[] buf = new byte[BUF_SIZE];
		ZipInputStream zipinputstream = null;
		ZipEntry zipentry;
		zipinputstream = new ZipInputStream(new FileInputStream(zip));
		while ((zipentry = zipinputstream.getNextEntry()) != null) {
			String zipPath =
				zipentry.getName().replaceFirst(
					StringUtils.stripFilenameExtension(zip.getName()), "");

			if (skipFolder != null) {
				zipPath = zipPath.replaceFirst(skipFolder, "");
			}

			if (zipentry.isDirectory()) {
				File zipFolder = new File(des + zipPath);
				if (!zipFolder.exists()) {
					zipFolder.mkdirs();
					
					if(log.isDebugEnabled()){
						log.debug("unpack " + zipFolder.getAbsolutePath());
					}
				}
			}
			else {
				File file = new File(des + zipPath);
				if (!file.exists()) {
					File pathDir = file.getParentFile();
					pathDir.mkdirs();
					file.createNewFile();
					
					if(log.isDebugEnabled()){
						log.info("unpack " + file.getAbsolutePath()
							+ file.getName());
					}
				}
				FileOutputStream fos = new FileOutputStream(file);
				int n;
				while ((n = zipinputstream.read(buf, 0, BUF_SIZE)) != -1) {
					fos.write(buf, 0, n);
				}
				fos.close();
			}
		}
	}

	public static void unzip(String des, File zip)
		throws Exception {

		unzip(des, zip, null);
	}

	private static int BUF_SIZE = 1024;

	private static Log log = LogFactory.getLog(ZipUtil.class);
}
