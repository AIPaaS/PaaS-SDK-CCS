package com.ai.paas.ipaas.ccs.addons.properties;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import com.ai.paas.ipaas.PaaSConstant;
import com.ai.paas.ipaas.ccs.zookeeper.ZKClient;
import com.ai.paas.ipaas.util.StringUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CSSPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
	protected static final Logger log = LoggerFactory.getLogger(CSSPropertyPlaceholderConfigurer.class);

	private final String WRITE_TO_LOCAL = "local";
	private final String LOAD_TO_SPRING = "spring";
	private final String PROPS_FORMAT_JSON = "{json}";
	private String zkClientName = null;
	private String[] locations;

	private ZKClient zkClient = null;

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		// 去获取zkClient Bean
		zkClient = (ZKClient) beanFactory.getBean(getZkClientName());
		// 然后加载locations的配置
		final Properties props = new Properties();
		props.putAll(processLocations());
		// 声明好，是否需要写到本地
		// 同时加载到Spring 环境中
		setProperties(props);
		super.postProcessBeanFactory(beanFactory);
	}

	public String getZkClientName() {
		return zkClientName;
	}

	public void setZkClientName(String zkClientName) {
		this.zkClientName = zkClientName;
	}

	private Properties processLocations() {
		boolean writeToLocal = false;
		boolean loadInSpring = false;
		Properties props = new Properties();
		for (String filename : locations) {
			// trim
			filename = filename.trim();
			String[] splits = filename.split("=");
			if (null == splits || splits.length < 2) {
				// 不符合的就不加载
				continue;
			}
			if (splits.length >= 3) {
				if (splits[2].equalsIgnoreCase(WRITE_TO_LOCAL)) {
					writeToLocal = true;
				}
				if (splits[2].equalsIgnoreCase(LOAD_TO_SPRING)) {
					loadInSpring = true;
				}
			}
			if (splits.length >= 4) {
				if (splits[2].equalsIgnoreCase(WRITE_TO_LOCAL) || splits[3].equalsIgnoreCase(WRITE_TO_LOCAL)) {
					writeToLocal = true;
				}
				if (splits[2].equalsIgnoreCase(LOAD_TO_SPRING) || splits[3].equalsIgnoreCase(LOAD_TO_SPRING)) {
					loadInSpring = true;
				}
			}
			// judge it is a json format
			String zkPath = StringUtil.isBlank(zkClient.getAppName()) ? splits[0]
					: zkClient.getAppName() + "." + splits[0];
			boolean isJson = zkPath.endsWith(PROPS_FORMAT_JSON);
			if (isJson) {
				zkPath = zkPath.substring(0, zkPath.indexOf(PROPS_FORMAT_JSON));
			}
			if (writeToLocal) {
				String realFileName = getFileName(splits[1]);
				writeToLocal(getKeyZKPath(zkPath), realFileName, isJson);
			}
			if (loadInSpring) {
				props.putAll(loadProperties(getKeyZKPath(zkPath), isJson));
			}
		}
		return props;
	}

	private Properties loadProperties(String zkPath, boolean isJson) {
		Properties props = new Properties();
		if (null == zkClient) {
			throw new RuntimeException("Can not init the zk client!");
		}
		// 开始读取
		try {
			String data = zkClient.getNodeData(zkPath);
			if (!StringUtil.isBlank(data)) {
				if (isJson) {
					// convert to json array
					Gson gson = new Gson();
					JsonObject json = gson.fromJson(data, JsonObject.class);
					if (null != json) {
						Set<Entry<String, JsonElement>> attrs = json.entrySet();
						Iterator<Entry<String, JsonElement>> iter = attrs.iterator();
						Entry<String, JsonElement> entry = null;
						while (iter.hasNext()) {
							entry = iter.next();
							props.put(entry.getKey(), entry.getValue().getAsString());
						}
					}
				} else {
					props.load(new StringReader(data));
				}
			}
		} catch (Exception e) {
			log.error("", e);
			throw new RuntimeException(e);
		}
		return props;
	}

	/**
	 * 注入zkclient
	 * 
	 * @throws IOException
	 */
	public void setLocations(List<String> fileNames) throws IOException {
		locations = fileNames.toArray(new String[fileNames.size()]);
	}

	/**
	 * get file name from resource
	 *
	 * @param fileName
	 *
	 * @return
	 */
	private String getFileName(String fileName) {

		if (fileName != null) {
			int index = fileName.indexOf(':');
			if (index < 0) {
				return fileName;
			} else {

				fileName = fileName.substring(index + 1);

				index = fileName.lastIndexOf('/');
				if (index < 0) {
					return fileName;
				} else {
					return fileName.substring(index + 1);
				}

			}
		}
		return null;
	}

	/**
	 * 将ZK的key的内容写到本地文件，先不实现自动更新
	 * 
	 * @param key
	 * @param filename
	 */
	private void writeToLocal(String key, String filename, boolean isJson) {
		// 初始化
		if (null == zkClient) {
			throw new RuntimeException("Can not init the zk client!");
		}
		File localFile = null;
		// 开始读取
		try {
			String data = zkClient.getNodeData(key);
			// 写到本地文件,都是类路径文件,不考虑其他地址
			String basePath = Thread.currentThread().getContextClassLoader().getResource("/").getPath();
			localFile = new File(basePath + filename);
			if (!StringUtil.isBlank(data)) {
				if (isJson) {
					// convert to json array
					Gson gson = new Gson();
					JsonObject json = gson.fromJson(data, JsonObject.class);
					if (null != json) {
						Set<Entry<String, JsonElement>> attrs = json.entrySet();
						Iterator<Entry<String, JsonElement>> iter = attrs.iterator();
						List<String> lines = new ArrayList<>();
						Entry<String, JsonElement> entry = null;
						while (iter.hasNext()) {
							entry = iter.next();
							lines.add(entry.getKey() + "=" + entry.getValue().getAsString());
						}
						FileUtils.writeLines(localFile, PaaSConstant.CHARSET_UTF8, lines);
						lines.clear();
					}
				} else {
					// 写到本地文件,都是类路径文件,不考虑其他地址
					FileUtils.writeStringToFile(localFile, data, PaaSConstant.CHARSET_UTF8);
				}
			}
		} catch (Exception e) {
			log.error("", e);
			throw new RuntimeException(e);
		} finally {
			if (null != localFile) {
				localFile = null;
			}
		}
	}

	private String getKeyZKPath(String key) {
		String zkPath = key;
		if (StringUtil.isBlank(key))
			return zkPath;
		zkPath = zkPath.replaceAll("\\.", "/");
		zkPath = "/" + zkPath;
		return zkPath;
	}
}
