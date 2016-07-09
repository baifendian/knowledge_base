package com.bfd.parse.util;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bfd.crawler.utils.DataUtil;
import com.bfd.crawler.utils.JsonUtils;

public class ahackthonUtil {

	private static final Log LOG = LogFactory.getLog(ahackthonUtil.class);
	public static boolean getIid(Map<String, Object> resultData){
		if(resultData.containsKey("items")){
			String link = "";
			try {
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> items = (List<Map<String, Object>>) resultData.get("items");
				for(Map<String, Object> item : items){
					@SuppressWarnings("unchecked")
					Map<String, Object> linkMap = (Map<String, Object>) item.get("link");
					link = (String) linkMap.get("link");
					item.put("iid", DataUtil.calcMD5(link));
					item.put("infoUrl", link);
				}
				resultData.put("items", items);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				LOG.error("getIid error: " + JsonUtils.toJSONString(resultData));
				return false;
			}
		}
		else
			return false;
	}
}
