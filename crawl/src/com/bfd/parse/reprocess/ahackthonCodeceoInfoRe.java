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

public class ahackthonCodeceoInfoRe implements ReProcessor{
	private static final Log LOG = LogFactory.getLog(ahackthonCodeceoInfoRe.class);

	private Pattern htmlPartten = Pattern.compile("(<div class=\"artical\">[\\s\\S]*</div>)\\s*<a name=\"comments\">\\s*</a>");
	private Pattern postTimePartten = Pattern.compile("([0-9]*-[0-9]*-[0-9]*)");
	//相关资料、推荐流量、分享过滤
	private Pattern errorPartten3 = Pattern.compile("<!-- 相关资料开始 -->[\\s\\S]*<!-- 相关资料结束 -->");
	private Pattern errorPartten4 = Pattern.compile("<div class=\"bdsharebuttonbox share\".*?>[\\s\\S]*?</div>");
	private Pattern errorPartten5 = Pattern.compile("<div class=\"article-tags\">[\\s\\S]*?</div>");
	private String HTML_INDEX = "<html><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, "
			+ "initial-scale=1.0, user-scalable=0, minimum-scale=1.0, maximum-scale=1.0\"><title>";
	@Override
	public ReProcessResult process(ParseUnit unit, ParseResult result, ParserFace arg2) {
		Map<String, Object> processdata = new HashMap<String, Object>();
		Map<String, Object> resultData = result.getParsedata().getData();
		Map<String, Object> taskData = result.getTaskdata();
		try {
			if(taskData.containsKey("attr")){
				@SuppressWarnings("unchecked")
				Map<String, Object> attrMap = (Map<String, Object>) taskData.get("attr");
				String topic = (String) attrMap.get("keyword");
				resultData.put("topic", topic);
			}
			String data = unit.getPageData();
			Matcher errorMatcher3 = errorPartten3.matcher(data);
			Matcher errorMatcher4 = errorPartten4.matcher(data);
			Matcher errorMatcher5 = errorPartten5.matcher(data);
			if(errorMatcher3.find()){
				String error = errorMatcher3.group(0);
				data = data.replace(error, "");
			}
			while(errorMatcher4.find()){
				String error = errorMatcher4.group(0);
				data = data.replace(error, "");
			}
			if(errorMatcher5.find()){
				String error = errorMatcher5.group(0);
				data = data.replace(error, "");
			}
			Matcher htmlMatcher = htmlPartten.matcher(data);
			if(htmlMatcher.find()){
				String htmlData = HTML_INDEX + resultData.get("title") + "</title></head><body>"
						+ htmlMatcher.group(1) + "</body></html>";
			    resultData.put("html", htmlData);
			}
			
			if(resultData.containsKey("post_time")){
				String postTimeStr = (String) resultData.get("post_time");
				Matcher pTimeMatch = postTimePartten.matcher(postTimeStr);
				//获取ptime
				if(pTimeMatch.find()){
					String postTime = pTimeMatch.group(1);
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
