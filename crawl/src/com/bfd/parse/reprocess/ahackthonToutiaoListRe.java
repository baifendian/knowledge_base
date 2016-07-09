package com.bfd.parse.reprocess;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bfd.crawler.utils.JsonUtils;
import com.bfd.parse.ParseResult;
import com.bfd.parse.ParserFace;
import com.bfd.parse.facade.parseunit.ParseUnit;
import com.bfd.parse.util.ahackthonUtil;

public class ahackthonToutiaoListRe implements ReProcessor {
	private static final Log LOG = LogFactory.getLog(ahackthonToutiaoListRe.class);
	
	public ReProcessResult process(ParseUnit unit, ParseResult result, ParserFace arg2) {
		Map<String, Object> processdata = new HashMap<String, Object>();
		Map<String, Object> resultData = result.getParsedata().getData();
		try {
			//items里增加iid与url
			ahackthonUtil.getIid(resultData);
			if(resultData.containsKey("items")){
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> items = (List<Map<String, Object>>) resultData.get("items");
				for(Map<String, Object> item : items){
					item.put("abs", "摘要：" + item.get("title"));
				}
				resultData.put("items", items);
			}
			LOG.info("url:" + unit.getUrl() + "after:" + JsonUtils.toJSONString(resultData));
			return new ReProcessResult(SUCCESS, processdata);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("url:" + unit.getUrl() + " reprocess error!");
			return new ReProcessResult(FAILED, processdata); 
		}
	}
}
