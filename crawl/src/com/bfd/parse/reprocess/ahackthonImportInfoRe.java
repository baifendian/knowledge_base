package com.bfd.parse.reprocess;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bfd.crawler.utils.DateUtil;
import com.bfd.crawler.utils.JsonUtils;
import com.bfd.parse.ParseResult;
import com.bfd.parse.ParserFace;
import com.bfd.parse.facade.parseunit.ParseUnit;

public class ahackthonImportInfoRe  implements ReProcessor {
	private static final Log LOG = LogFactory.getLog(ahackthonImportInfoRe.class);

	private Pattern postTimePartten = Pattern.compile("([0-9]*/[0-9]*/[0-9]*)");
	private Pattern catPartten = Pattern.compile("标签[:：]([\\S\\s]*)");
	@SuppressWarnings("unchecked")
	@Override
	public ReProcessResult process(ParseUnit unit, ParseResult result, ParserFace arg2) {
		Map<String, Object> processdata = new HashMap<String, Object>();
		Map<String, Object> resultData = result.getParsedata().getData();
		Map<String, Object> taskData = result.getTaskdata();
		
		try {
			String data = unit.getPageData();
			resultData.put("html", data);
			//从列表页入口获取topic
			if(taskData.containsKey("attr")){
				Map<String, Object> attrMap = (Map<String, Object>) taskData.get("attr");
				String topic = (String) attrMap.get("keyword");
				resultData.put("topic", topic);
			}
			if(resultData.containsKey("post_time")){
				String pTime = (String) resultData.get("post_time");
				Matcher catMatch = catPartten.matcher(pTime);
				if(catMatch.find()){
					String cat = catMatch.group(1);
					resultData.put("cat", cat.trim());
				}
				Matcher pTimeMatch = postTimePartten.matcher(pTime);
				if(pTimeMatch.find()){
					String postTime = pTimeMatch.group(1).replace("/", "-");
					long postTimeLong = DateUtil.getTimeMillis(postTime + " 00:00:00");
					resultData.put("post_time", postTimeLong);
				}
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
