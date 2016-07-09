package com.bfd.parse.reprocess;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bfd.crawler.utils.JsonUtils;
import com.bfd.parse.ParseResult;
import com.bfd.parse.ParserFace;
import com.bfd.parse.facade.parseunit.ParseUnit;

public class ahackthonRunoobInfoRe implements ReProcessor{
	private static final Log LOG = LogFactory.getLog(ahackthonRunoobInfoRe.class);

	private Pattern titleHtmlPartten = Pattern.compile("<div class=\"article-heading\">[\\s\\S]*?</div>");
	private Pattern contentHtmlPartten = Pattern.compile("<div class=\"article-body\">[\\s\\S]*?</div>");
	private String HTML_INDEX = "<html><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, "
			+ "initial-scale=1.0, user-scalable=0, minimum-scale=1.0, maximum-scale=1.0\"><title>";
	@SuppressWarnings("unchecked")
	@Override
	public ReProcessResult process(ParseUnit unit, ParseResult result, ParserFace arg2) {
		Map<String, Object> processdata = new HashMap<String, Object>();
		Map<String, Object> resultData = result.getParsedata().getData();
		Map<String, Object> taskData = result.getTaskdata();
		try {
			if(taskData.containsKey("attr")){
				Map<String, Object> attrMap = (Map<String, Object>) taskData.get("attr");
				String topic = (String) attrMap.get("keyword");
				resultData.put("topic", topic);
			}
			String data = unit.getPageData();
			Matcher titleHtmlMatch = titleHtmlPartten.matcher(data);
			Matcher contentHtmlMatch = contentHtmlPartten.matcher(data);
			if(titleHtmlMatch.find() && contentHtmlMatch.find()){
				String htmlData = HTML_INDEX + resultData.get("title") + "</title></head><body>"
						+ titleHtmlMatch.group(0) + contentHtmlMatch.group(0) + "</body></html>";
				resultData.put("html", htmlData);
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
