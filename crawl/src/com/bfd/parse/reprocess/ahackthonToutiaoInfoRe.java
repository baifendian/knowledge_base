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

public class ahackthonToutiaoInfoRe  implements ReProcessor{
	private static final Log LOG = LogFactory.getLog(ahackthonToutiaoInfoRe.class);

	//标题、文章内容过滤
	private Pattern htmlPartten = Pattern.compile("(<div id=\"pagelet-article\">[\\s\\S]*</div>)\\s*?<div id=\"pagelet-relatednews");
	//过滤
	private Pattern errorPartten = Pattern.compile("(<div class=\"article-actions\"[\\s\\S]*</div>)\\s*?</div>\\s*?</body>");
	private String HTML_INDEX = "<html><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, "
			+ "initial-scale=1.0, user-scalable=0, minimum-scale=1.0, maximum-scale=1.0\"><title>";
	@SuppressWarnings("unchecked")
	@Override
	public ReProcessResult process(ParseUnit unit, ParseResult result, ParserFace arg2) {
		Map<String, Object> processdata = new HashMap<String, Object>();
		Map<String, Object> resultData = result.getParsedata().getData();
		Map<String, Object> taskData = result.getTaskdata();
		try {
			//从列表页入口获取topic
			if(taskData.containsKey("attr")){
				Map<String, Object> attrMap = (Map<String, Object>) taskData.get("attr");
				String topic = (String) attrMap.get("keyword");
				resultData.put("topic", topic);
			}
			//获取源码
			String data = unit.getPageData();
			System.out.println("data: " + data);
			Matcher htmlMatch = htmlPartten.matcher(data);
			if(htmlMatch.find()){
				String htmlData = HTML_INDEX + resultData.get("title") + "</title></head><body>"
						+ htmlMatch.group(1) + "</body></html>";
				Matcher errorMatch = errorPartten.matcher(htmlData);
				if(errorMatch.find()){
					htmlData = htmlData.replace(errorMatch.group(1), "").replace("\\", "");
				}
				resultData.put("html", htmlData);
			}
			
			if(resultData.containsKey("post_time")){
				String postTimeStr = (String) resultData.get("post_time");
				long postTimeLong = DateUtil.getTimeMillis(postTimeStr + ":00");
				resultData.put("post_time", postTimeLong);
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
