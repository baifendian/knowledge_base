package com.bfd.parse.reprocess;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bfd.crawler.utils.JsonUtils;
import com.bfd.parse.ParseResult;
import com.bfd.parse.ParserFace;
import com.bfd.parse.facade.parseunit.ParseUnit;
import com.bfd.parse.util.ahackthonUtil;

public class ahackthonRunoobListRe implements ReProcessor {
	private static final Log LOG = LogFactory.getLog(ahackthonRunoobListRe.class);
	
	public ReProcessResult process(ParseUnit unit, ParseResult result, ParserFace arg2) {
		Map<String, Object> processdata = new HashMap<String, Object>();
		Map<String, Object> resultData = result.getParsedata().getData();
		//items里增加iid与url
		ahackthonUtil.getIid(resultData);
		LOG.info("url:" + unit.getUrl() + "after:" + JsonUtils.toJSONString(resultData));
		return new ReProcessResult(SUCCESS, processdata);
	}
}
